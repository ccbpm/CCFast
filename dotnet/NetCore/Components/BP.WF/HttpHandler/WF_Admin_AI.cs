using System;
using System.Collections;
using System.ComponentModel.DataAnnotations;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.Linq;
using System.Security.Cryptography;
using System.ServiceModel.Channels;
using System.Text.Json.Nodes;
using System.Text.RegularExpressions;
using System.Threading;
using System.Web.Http.Results;
using BP.CCFast.Portal;
using BP.DA;
using BP.Difference;
using BP.En;
using BP.Sys;
using BP.Sys.XML;
using BP.TA;
using BP.Tools;
using BP.Web;
using BP.WF.Data;
using BP.WF.Template;
using FluentFTP;
using Google.Protobuf.WellKnownTypes;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Connections;
using Newtonsoft.Json.Linq;
using NPinyin;
using NPOI.HPSF;
using NPOI.OpenXmlFormats.Dml.Diagram;
using NPOI.OpenXmlFormats.Spreadsheet;
using NPOI.SS.Formula.Eval;
using NPOI.SS.Formula.Functions;
using Spire.Doc;
using DataType = BP.DA.DataType;

namespace BP.WF.HttpHandler
{
    /// <summary>
    /// AI表单
    /// </summary>
    public class WF_Admin_AI : BP.WF.HttpHandler.DirectoryPageBase
    {
        public string Words
        {
            get
            {
                return this.GetRequestVal("Words");
            }
        }

        # region N cmd
        public string CopilotCmd()
        {
            string frmID = this.FrmID;
            string words = this.Words;
            string names = this.GetRequestVal("code");
            names = "发起流程|发起:FlowStart,待办:TODO,在途:Running,近期:Recent,单据:BILL";

            //string json = WF_Admin_AI_Client.CopilotCmd(words, names, false, true);
            string json = WF_Admin_AI_Client.CopilotCmd(words, names);
            if (json.Contains("err@") == true && json.Length <= 100)
                return json;

            return json;
        }

        #endregion

        #region 代码生成器.
        /// <summary>
        /// TS代码生成器.
        /// </summary>
        /// <returns></returns>
        public string CodeGener_TSSave_test()
        {
            MapData md = new MapData(this.FrmID);
            DataSet myds = BP.Sys.CCFormAPI.GenerHisDataSet(this.FrmID, md.Name);
            string json = BP.Tools.Json.ToJson(myds); //包含表单模板所有的内容.
            //生成代码.
            return "生成成功.";
        }
        #endregion 代码生成器.


        #region Ai 工作处理.
        public string Assistant_Init()
        {
            return "您可以说：\r\n1.我同意，请提交。 2. 因为什么什么原因，我不同意，请退回。\r\n";
        }
        public string Assistant_Save()
        {
            string words = this.GetRequestVal("Words");



            string checkNote = "我同意。";
            string cmd = "Send";


            return "您可以说：\r\n1.我同意，请提交。 2. 因为什么什么原因，我不同意，请退回。\r\n";
        }
        #endregion

        #region 从表.

        public string AiFrm_DtlsGener()
        {
            string frmID = this.FrmID;
            string words = this.Words;
            string names = "";

            //string words = this.GetRequestVal("Words");
            //string json = WF_Admin_AI_Client.AIFrm_GenerDetails_ByWord(words, names, false, true);
            string json = WF_Admin_AI_Client.AIFrm_GenerDetails_ByWord(words, names);
            if (json.Contains("err@") == true && json.Length <= 100)
                return json;

            JArray jsonArray = JArray.Parse(json);
            if (jsonArray == null)
                throw new Exception("err@AI没有返回子表单，请尝试更换提示词。");

            DataTable dt = new DataTable();
            dt.TableName = "Dtls";
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("字段");
            dt.Columns.Add("JSON");

            foreach (JObject jsonObject in jsonArray)
            {
                String no = jsonObject["no"].ToString();
                String cnname = jsonObject["name"].ToString();
                String columnsJson = jsonObject["columns"].ToString();
                JArray jsonArray2 = JArray.Parse(columnsJson);
                string fields = "";
                foreach (JObject jobj in jsonArray2)
                {
                    fields += jobj["name"].ToString() + ",";
                }

                //DataTable columnsdt = convert(jsonArray2);

                DataRow dr = dt.NewRow();
                dr["No"] = no;
                dr["Name"] = cnname;
                dr["字段"] = fields;
                dr["JSON"] = columnsJson;
                dt.Rows.Add(dr);
            }

            json = BP.Tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("FrmDtl" + frmID, json);
            return json;
        }

        /// <summary>
        /// 将json字段转换为dt
        /// </summary>
        /// <param name="jsonArray"></param>
        /// <returns></returns>
        private DataTable convert(JArray jsonArray)
        {
            DataTable columnsdt = new DataTable();
            columnsdt.Columns.Add("No");
            columnsdt.Columns.Add("Name");
            columnsdt.Columns.Add("KeyOfEn");
            columnsdt.Columns.Add("数据类型");
            columnsdt.Columns.Add("MinLen");
            columnsdt.Columns.Add("MaxLen");
            columnsdt.Columns.Add("Note");
            columnsdt.Columns.Add("DataType");

            return convert(columnsdt, jsonArray);

        }


        /// <summary>
        /// 将json字段转换为dt
        /// </summary>
        /// <param name="jsonArray"></param>
        /// <returns></returns>
        private DataTable convert(DataTable dt, JArray jsonArray)
        {
            foreach (JObject jsonObject in jsonArray)
            {
                string name = jsonObject["name"].ToString();
                string cnname = jsonObject["cnname"].ToString();
                Object lengthObj = jsonObject["length"];
                if (lengthObj == null)
                    lengthObj = "0";
                String lengthStr = lengthObj.ToString();
                String[] lengthArray = { lengthStr };
                if (lengthStr.IndexOf(',') >= 0)
                    lengthArray = lengthStr.Split(',');

                string datatype = (String)jsonObject["datatype"];
                string enumval = (String)jsonObject["enum"];
                if (!String.IsNullOrEmpty(enumval) && enumval.IndexOf(",") > 0)
                {
                    datatype = "ENUM";
                }

                string note = "无";
                int minLen = 0;
                int maxLen = 100;
                int datatypeInt = DA.DataType.AppString;
                switch (datatype.ToUpper())
                {
                    case "VARCHAR":
                    case "TEXT":
                    case "STRING":
                        datatypeInt = DA.DataType.AppString;
                        if (lengthArray.Length == 1)
                        {
                            int.TryParse(lengthArray[0], out maxLen);
                        }
                        break;
                    case "DATE":
                        datatypeInt = DA.DataType.AppDate;
                        maxLen = 20;
                        break;
                    case "DATETIME":
                        datatypeInt = DA.DataType.AppDateTime;
                        maxLen = 20;
                        break;
                    case "NUMBER":
                    case "INT":
                    case "INTEGER":
                    case "BOOLEN": //boolen.
                        datatypeInt = DA.DataType.AppInt;
                        maxLen = 10;
                        break;
                    case "DECIMAL":
                        datatypeInt = DA.DataType.AppMoney;
                        if (lengthArray.Length == 2)
                        {
                            //金额类型10,2，表示2为小数
                            int.TryParse(lengthArray[0], out maxLen);
                            int.TryParse(lengthArray[1], out minLen);
                        }
                        break;
                    case "FLOAT":
                        datatypeInt = DA.DataType.AppFloat;
                        maxLen = 10;
                        break;
                    case "ENUM": // 枚举.
                        datatypeInt = DA.DataType.AppInt;
                        if (enumval != null)
                        {
                            String str = enumval.ToString();
                            string[] array = str.Split(",");
                            note = array.Join("|");
                        }
                        break;
                    default:
                        break;
                }

                DataRow dr = dt.NewRow();
                //  string no = name + "*" + cnname + "*" + datatypeInt + "*0*" + maxLen + "*" + note;
                dr["No"] = DBAccess.GenerGUID(); // no;
                dr["Name"] = cnname;
                dr["KeyOfEn"] = name;
                dr["DataType"] = datatypeInt;
                dr["MinLen"] = minLen;
                dr["MaxLen"] = maxLen;
                dr["Note"] = note;

                string typeStr = "文本";
                if (datatypeInt == 1)
                    typeStr = "文本";
                if (datatypeInt == 2)
                    typeStr = "数值int";
                if (datatypeInt == 3)
                    typeStr = "数值-float";
                if (datatypeInt == 4)
                    typeStr = "布尔值";
                if (datatypeInt == 5)
                    typeStr = "数值-double";
                if (datatypeInt == 6)
                    typeStr = "日期";
                if (datatypeInt == 7)
                    typeStr = "日期时间";
                if (datatypeInt == 8)
                    typeStr = "金额";
                if (note.Contains("|") == true)
                    typeStr = "枚举";

                dr["数据类型"] = typeStr;
                dt.Rows.Add(dr);
            }

            return dt;
        }

        /// <summary>
        /// 选择从表字段
        /// 
        /// 目前仅取一个从表，后续可扩展到多表，ui中的选择方式需要扩展
        /// </summary>
        /// <returns></returns>
        /// <exception cref="Exception"></exception>
        public string AiFrm_DtlAttrs()
        {
            string frmID = this.GetRequestVal("FrmID"); //所有的字段.
            string DtlIDs = this.GetRequestVal("DtlID");

            string[] selectedDtlsArray = { };
            if (!string.IsNullOrEmpty(DtlIDs))
            {
                selectedDtlsArray = DtlIDs.Split(',');
            }
            if (selectedDtlsArray == null || selectedDtlsArray.Length == 0)
                return "err@ 没有选择任何从表";

            string fromType = "FrmDtl";

            string json = WebUser.TempValGetByKey(fromType + this.FrmID);

            JArray jsonArray = null;
            try
            {
                jsonArray = JArray.Parse(json);
            }
            catch (Exception ex)
            {
                throw new Exception("err@json格式有误，请重试." + ex.Message);
            }

            //多个从表
            foreach (JObject jsonObject in jsonArray)
            {
                string dtlFrmID = jsonObject["No"].ToString();
                if (!selectedDtlsArray.Contains(dtlFrmID))
                    continue;

                String dtlname = jsonObject["Name"].ToString();
                String dtljson = jsonObject["JSON"].ToString();

                //每一个从表处理    
                //DataTable dtAttrs = BP.Tools.Json.ToDataTable(dtljson);
                JArray dtlArray = JArray.Parse(dtljson);
                DataTable dt = convert(dtlArray);
                json = BP.Tools.Json.ToJson(dt);
                WebUser.TempValSaveByKey("FrmDtlAttrs" + this.FrmID, json);
                //WebUser.TempValSaveByKey("FrmDtl" + dtlFrmID, json);
                WebUser.TempValSaveByKey("FrmDtlAttrs_dtlName" + this.FrmID, dtlname);
                break; //Only 1

            } //从表end

            return json;
        }

        #region test

        public string AiFrm_DtlsGener_test()
        {
            string frmID = this.FrmID;
            string words = this.Words;

            DataTable dt = new DataTable();
            dt.TableName = "Dtls";
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("字段");
            dt.Columns.Add("JSON");

            for (int i = 0; i < 3; i++)
            {
                DataRow dr = dt.NewRow();
                dr["No"] = "xx" + i;
                dr["Name"] = "xx" + i;
                dr["No"] = "xx" + i;
                dr["字段"] = "xx" + i;
                dr["JSON"] = "xx" + i;
                dt.Rows.Add(dr);
            }

            string json = BP.Tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("FrmDtl" + this.FrmID, json);
            return json;
        }

        public string AiFrm_DtlAttrs_test()
        {
            string json = WebUser.TempValGetByKey("FrmDtl" + this.FrmID);
            DataTable dt = BP.Tools.Json.ToDataTable(json);
            string dtlID = this.GetRequestVal("DtlID");
            foreach (DataRow dr in dt.Rows)
            {
                string no = dr["No"].ToString();
                if (no.Equals(dtlID) == true)
                {
                    json = dr["JSON"].ToString();
                    WebUser.TempValSaveByKey("FrmDtlAttrs" + this.FrmID, json);
                    return json;

                }
            }
            return "err@系统错误.";
        }

        #endregion

        /// <summary>
        /// 保存从表
        /// </summary>
        /// <returns></returns>
        public string AiFrm_DtlSaveAttrs()
        {
            string frmType = "FrmDtlAttrs";
            string json = WebUser.TempValGetByKey(frmType + this.FrmID);
            DataTable dt = BP.Tools.Json.ToDataTable(json);
            string dtlName = WebUser.TempValGetByKey("FrmDtlAttrs_dtlName" + this.FrmID);
            string dtlName2 = this.GetRequestVal("DtlName"); //从表名称.
            string dtlID = this.FrmID + this.GetRequestVal("DtlID"); //从表ID.

            string attrs = this.GetRequestVal("SelectedAttrs"); //选择的字段.

            //创建从表.
            BP.Sys.CCFormAPI.CreateOrSaveDtl(this.FrmID, dtlID, dtlName);

            string attrsJson = WebUser.TempValGetByKey(frmType + this.FrmID);

            //保存从表数据.
            return AiFrm_SaveAttrsExt(dtlID, attrs, attrsJson, frmType);
        }
        #endregion

        #region AI表单. - 输入关键字.
        public string AiFrm_GenerAttrs()
        {
            string words = this.GetRequestVal("Words");
            DataTable dt = AiFrm_GenerAttrsExt(this.FrmID, words);
            dt.TableName = "Attrs";
            //DataSet ds = new DataSet();
            //ds.Tables.Add(dt);
            //DataTable dtls = new DataTable();
            string json = BP.Tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("FrmAttr" + this.FrmID, json);
            return json;
        }
        /// <summary>
        /// 批量增加字段
        /// </summary>
        /// <returns></returns>
        public string AiFrm_GenerBatchAttrs()
        {
            string words = this.GetRequestVal("Words");
            //@hwz. 修改这里获得数据.
            DataTable dt = AiFrm_GenerAttrsExt(this.FrmID, words);
            dt.TableName = "Attrs";
            //DataSet ds = new DataSet();
            //ds.Tables.Add(dt);
            //DataTable dtls = new DataTable();
            string json = BP.Tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("FrmAttrBatch" + this.FrmID, json);
            return json;
        }

        /// <summary>
        /// 获得分析内容字段公式
        /// </summary>
        /// <returns></returns>
        public string CheckFrm_GenerFX()
        {
            string frmID = this.FrmID;
            string words = this.GetRequestVal("Words");
            MapData md = new MapData(this.FrmID);
            MapAttrs attrs = new MapAttrs();
            attrs.Retrieve("FK_MapData", this.FrmID);
            string vals = "";
            foreach (MapAttr attr in attrs)
            {
                if (attr.UIVisible == false) continue;
                if ((int)attr.UIContralType >= 4) continue;
                if (attr.MyDataType == 1 && attr.LGType != 0)
                {
                    vals += "外键字段:" + attr.Name + "," + attr.KeyOfEn + ";";
                    continue;
                }
                if (attr.MyDataType == 1)
                {
                    vals += "文本字段:" + attr.Name + "," + attr.KeyOfEn + ";";
                    continue;
                }
                if (attr.MyDataType == 6 || attr.MyDataType == 6)
                {
                    vals += "日期字段:" + attr.Name + "," + attr.KeyOfEn + ";";
                    continue;
                }
                if (attr.MyDataType == 2 && attr.LGType == FieldTypeS.Enum)
                {
                    vals += "枚举字段:" + attr.Name + "," + attr.KeyOfEn + ";";
                    continue;
                }
            }
            string info = "表单名称为：" + md.Name +
                "有如下字段" + vals +
                " 请检查表单字段，按照如下要求" +
                "1. 在数值类型的字段里寻找：一个字段值是通过其他字段值通过表达式计算得来. 比如: 合计=单价*数量" +
                "2. 在日期字段里寻找：日期字段不能输入历史日期. " +
                "3. 在日期字段里寻找：一个日字段大于等于另外一个日期字段." +
                "4. 可以在字段上增加的正则表达式. 比如：手机号，邮件，地址." +
                "5. 根据枚举库：把文本类型配备枚举，采用模糊匹配的模式." +
                "6. 根据字典库：把文本类型配备外键或者外部数据源，采用模糊匹配的模式." +
                "7. 在日期类型与数值类型的字段中寻找，数值=日期1-日期2 的规则. " +
                "8. 根据表单名称：在枚举库与字典库里寻找那些字段没有被补充上给与提示. ";


            string json = BP.WF.HttpHandler.WF_Admin_AI_Client.AIFrm_GenerFX(frmID, info);
            //string json = BP.WF.HttpHandler.WF_Admin_AI_Client.AIFrm_GenerFX(frmID, words, false, true);
            if (BP.DA.DataType.IsNullOrEmpty(json) == true)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");
            if (json.Contains("err@") == true)
                throw new Exception("err@AI返回结果错误:CheckFrm_GenerFX:" + json);

            JArray jsonArray = JArray.Parse(json);
            if (jsonArray == null || jsonArray.Count == 0)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");

            DataTable dt = new DataTable();
            dt.Columns.Add("No"); //标记
            dt.Columns.Add("Name"); //名称.
            dt.Columns.Add("Note"); //描述.
            dt.Columns.Add("AttrOfOper"); //字段.
            dt.Columns.Add("ExtModel"); //参数2.
            dt.Columns.Add("ExtType"); //参数2.
            dt.Columns.Add("Doc"); //参数2.

            dt.Columns.Add("Tag"); //参数1.
            dt.Columns.Add("Tag1"); //参数1.
            dt.Columns.Add("Tag2"); //参数2.
            dt.Columns.Add("Tag3"); //参数2.
            dt.Columns.Add("Tag4"); //参数2.
            dt.Columns.Add("FK_MapData"); //参数2.
            dt.Columns.Add("DoWay"); //参数2.
            dt.Columns.Add("RefPKVal"); //关联字段.
            dt.Columns.Add("MyPK"); //主键.
            dt.Columns.Add("TagT"); //参数1.
            dt.Columns.Add("Tag1T"); //参数1.
            dt.Columns.Add("AtPara"); //参数


            foreach (JObject jsonObject in jsonArray)
            {
                string relType = jsonObject["RelType"].ToString(); //逻辑编号ID. BindFunction
                string fieldDesc = jsonObject["RelDesc"].ToString();// 逻辑名称.
                string fieldKey = jsonObject["FieldKey"].ToString(); //英文名称.  
                string fieldName = jsonObject["FieldName"].ToString(); //中文名称.
                string relFields = jsonObject["RelFields"].ToString(); //中文名称.

                //Object fieldArrayObj = jsonObject["RelFields"]; //关联的字段.
                //string[] fieldArrayStr = "".Split(",");
                //if (fieldArrayObj != null && fieldArrayObj is JArray)
                //{
                //    fieldArrayStr = ((JArray)fieldArrayObj).ToObject<string[]>();
                //}
                string fieldEx = jsonObject["Docs"].ToString(); //表达式.

                DataRow dr = dt.NewRow();

                #region  AutoFull . 运算表达式..
                if (relType.Equals("AutoFull") == true)
                {
                    dr["Name"] = fieldDesc; //"字段公式";
                    dr["Note"] = "发现:" + fieldName + "," + fieldKey + "可以使用表达式:" + fieldEx; //"系统发现：{小计=单价等于金额} 的公式？";
                    dr["AttrOfOper"] = fieldKey; // "XioJi"; //操作的字段.
                    dr["Tag"] = fieldEx; // "@DanJia*@ShuLiang";
                    dr["Tag1"] = fieldEx; // "单价*数量";
                    dr["ExtModel"] = "AutoFull";
                    dr["ExtType"] = "AutoFull";
                    //主键格式: 操作字段+标记+表达式
                    dr["No"] = dr["AttrOfOper"] + "_AutoFull_" + dr["Tag"].ToString();
                    dt.Rows.Add(dr);
                    continue;
                }
                #endregion 自动填充.

                #region  DateFieldInputRole1 . 不能输入历史日期.
                if (relType.Equals("DateFieldInputRole1") == true)
                {
                    dr = dt.NewRow();
                    dr["Name"] = "不能输入历史日期.";
                    dr["Note"] = "系统发现字段[" + fieldName + fieldKey + "],不能输入历史日期.";
                    dr["AttrOfOper"] = fieldKey; //操作的字段.
                    dr["Tag"] = fieldKey;
                    dr["Tag1"] = fieldName;
                    dr["ExtModel"] = "DateFieldInputRole";
                    dr["ExtType"] = "DateFieldInputRole";
                    dr["DoWay"] = "1";
                    dr["No"] = DBAccess.GenerGUID();
                    dr["MyPK"] = this.FrmID + "_" + dr["AttrOfOper"].ToString() + "_DateFieldInputRole";
                    dt.Rows.Add(dr);
                    continue;
                }
                #endregion DateFieldInputRole.不能输入历史日期.

                #region  DateFieldInputRole2 . 大于等于指定的日期.
                if (relType.Equals("DateFieldInputRole2") == true)
                {
                    if (DataType.IsNullOrEmpty(relFields) == true)
                        throw new Exception("err@AI在求大于等于指定的日期，没有关联到:relFields");

                    if (relFields.Contains("[") == true)
                        throw new Exception("err@AI在求大于等于指定的日期，关联的字段，不能是数组:[" + relFields + "],多个使用逗号分开.");

                    string attrPK = this.FrmID + "_" + relFields;
                    MapAttr attr = new MapAttr();
                    attr.setMyPK(attrPK);
                    if (attr.RetrieveFromDBSources() == 0)
                        continue;

                    dr = dt.NewRow();
                    dr["Name"] = "大于等于指定的日期.";
                    dr["Note"] = "AI发现日期字段[" + fieldName + fieldKey + "],需要增加大于等于日期字段:[" + relFields + "," + attr.Name + "]的控制";
                    dr["AttrOfOper"] = fieldKey; //操作的字段.
                    dr["Tag"] = "GTE";
                    dr["Tag1"] = relFields;
                    dr["DoWay"] = "2";
                    dr["ExtModel"] = "DateFieldInputRole";
                    dr["ExtType"] = "DateFieldInputRole";

                    dr["TagT"] = "大于等于"; //大于等于.
                    dr["Tag1T"] = attr.Name; //

                    dr["No"] = DBAccess.GenerGUID();
                    dr["MyPK"] = this.FrmID + "_" + dr["AttrOfOper"].ToString() + "_DateFieldInputRole";
                    dt.Rows.Add(dr);
                    continue;
                }
                #endregion DateFieldInputRole2.大于等于指定的日期.

                #region  BindFunction - RegularExpression. 正则表达式..
                if (relType.Equals("BindFunction") == true)
                {
                    dr = dt.NewRow();
                    dr["Name"] = "正则表达式.";
                    dr["Note"] = "系统发现字段[" + fieldName + fieldKey + "],需要增加正则表达式.";
                    dr["AttrOfOper"] = fieldKey; //操作的字段.
                    dr["Tag"] = "blur";
                    dr["Tag1"] = "blur失去焦点";
                    dr["Tag2"] = "提示信息:必须以数字开头，除数字外，可含有“-” ";
                    dr["Doc"] = fieldEx; //正则表达式内容.
                    dr["ExtModel"] = "BindFunction"; //绑定函数.
                    dr["ExtType"] = "RegularExpression"; //正则表达式.
                    dr["No"] = DBAccess.GenerGUID(); // dr["AttrOfOper"] + "BindFunction$" + dr["Tag"];
                    dr["MyPK"] = dr["AttrOfOper"].ToString() + "_BindFunction_" + dr["Tag"].ToString();
                    dr["RefPKVal"] = this.FrmID + "_" + dr["AttrOfOper"].ToString();
                    dr["AtPara"] = "@EnName=TS.MapExt.RegularExpression";
                    dt.Rows.Add(dr);
                    continue;
                }
                #endregion RegularExpression 增加正则表达式.
            }

            json = BP.Tools.Json.ToJson(dt);
            //临时保存.
            WebUser.TempValSaveByKey("FX" + this.FrmID, json);
            return json;
        }

        /// <summary>
        /// 保存选择的内容
        /// </summary>
        /// <returns></returns>
        public string CheckFrm_GenerSave()
        {
            string selectVals = this.GetRequestVal("Vals") + ",";

            //获得保存的全量集合.
            string json = WebUser.TempValGetByKey("FX" + this.FrmID);
            DataTable fxDT = BP.Tools.Json.ToDataTable(json);

            string frmID = this.FrmID;
            foreach (DataRow dr in fxDT.Rows)
            {
                string id = dr["No"].ToString();
                if (selectVals.Contains(id + ",") == false)
                    continue;

                #region 设置初始值.
                MapExt ext = new MapExt();
                string mypk = dr["MyPK"].ToString();
                //string name = dr["Name"].ToString();
                //string note = dr["Note"].ToString();
                //string attrOfOper = dr["AttrOfOper"].ToString();
                //string extModel = dr["ExtModel"].ToString();
                //string extType = dr["ExtType"].ToString();
                //string doc = dr["Doc"].ToString();
                //string doWay = dr["DoWay"].ToString();

                //string tag = dr["Tag"].ToString();
                //string tag1 = dr["Tag1"].ToString();
                //string tag2 = dr["Tag2"].ToString();
                //string tag3 = dr["Tag3"].ToString();

                ext.SetValByKey("MyPK", dr["MyPK"].ToString());
                ext.SetValByKey("AttrOfOper", dr["AttrOfOper"].ToString());
                ext.SetValByKey("ExtModel", dr["ExtModel"].ToString());
                ext.SetValByKey("ExtType", dr["ExtType"].ToString());
                ext.SetValByKey("Doc", dr["Doc"].ToString());
                ext.SetValByKey("DoWay", dr["DoWay"].ToString());

                ext.SetValByKey("Tag", dr["Tag"].ToString());
                ext.SetValByKey("Tag1", dr["Tag1"].ToString());
                ext.SetValByKey("Tag2", dr["Tag2"].ToString());
                ext.SetValByKey("Tag3", dr["Tag3"].ToString());
                ext.SetValByKey("Tag4", dr["Tag4"].ToString());

                ext.SetValByKey("FK_MapData", this.FrmID);
                #endregion 设置初始值.

                if (DataType.IsNullOrEmpty(mypk) == false)
                {
                    ext.SetValByKey("MyPK", mypk);
                    if (ext.IsExits == true)
                        ext.Delete();
                    ext.Insert();
                }
                else
                {
                    ext.setMyPK(DBAccess.GenerGUID());
                    ext.Insert();
                }
            }
            return "执行成功.";
        }
        /// <summary>
        /// 根据提示信息生成字段.
        /// </summary>
        /// <returns></returns>
        public DataTable AiFrm_GenerAttrsExt(string frmID, string words)
        {
            // 根据words 根据表单ID. 生成一个 具有 No,Name的 json.
            //假设生成如下字段.
            MapAttrs attrs = new MapAttrs();
            attrs.Retrieve("FK_MapData", frmID);

            string names = ",";
            foreach (MapAttr item in attrs)
                names += item.Name + ",";

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("KeyOfEn");
            dt.Columns.Add("数据类型");
            dt.Columns.Add("MinLen");
            dt.Columns.Add("MaxLen");
            dt.Columns.Add("Note");
            dt.Columns.Add("DataType");

            //string json = WF_Admin_AI_Client.AIFrm_GenerAttrs_ByWords(frmID, words, names, false, true);
            string json = WF_Admin_AI_Client.AIFrm_GenerAttrs_ByWords(frmID, words, names);
            if (BP.DA.DataType.IsNullOrEmpty(json) == true)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");
            if (json.Contains("err@") == true)
                throw new Exception("err@AI返回结果错误:" + json);

            JArray jsonArray = null;
            try
            {
                jsonArray = JArray.Parse(json);
            }
            catch (Exception ex)
            {
                throw new Exception("err@AI没有返回格式有误，请重试." + ex.Message);
            }

            if (jsonArray == null || jsonArray.Count == 0)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");

            return convert(dt, jsonArray);
        }
        public string AiFrm_SaveAttrs()
        {
            string frmID = this.GetRequestVal("FrmID"); //所有的字段.
            string selectedAttrs = this.GetRequestVal("SelectedAttrs");
            string frmType = this.GetRequestVal("AttrsFromType");
            if (DataType.IsNullOrEmpty(frmType) == true)
                frmType = "FrmAttr";
            string json = WebUser.TempValGetByKey(frmType + frmID);
            return AiFrm_SaveAttrsExt(frmID, selectedAttrs, json, frmType);
        }
        public string AiFrm_SaveAttrsExt(string frmID, string selectedAttrs, string json, string frmType)
        {
            string[] selectedAttrsArray = selectedAttrs.Split(',');
            if (DataType.IsNullOrEmpty(frmType) == true)
                frmType = "FrmAttr";

            DataTable dtAttrs = BP.Tools.Json.ToDataTable(json);


            MapData md = new MapData(frmID);
            int idx = DBAccess.RunSQLReturnValInt("SELECT MAX(Idx) FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "'");
            int groupID = DBAccess.RunSQLReturnValInt("SELECT  MAX(OID) as Num  FROM Sys_GroupField WHERE FrmID ='" + frmID + "' AND CtrlType=''", 0);
            foreach (DataRow dr in dtAttrs.Rows)
            {
                string no = dr[0].ToString();
                if (!selectedAttrsArray.Any(s => s.Contains(no)))
                    continue;

                string cnName = dr["Name"].ToString();
                string attrKey = dr["KeyOfEn"].ToString();


                string note = dr["Note"].ToString();
                idx++;
                if (attrKey.Equals("name") == true || attrKey.EndsWith("_id") == true)
                    continue;

                int dataType = int.Parse(dr["DataType"].ToString());
                int minLen = int.Parse(dr["MinLen"].ToString());
                int maxLen = int.Parse(dr["MaxLen"].ToString());
                if (dataType == 1)
                {
                    if (maxLen == 0)
                        maxLen = 10;
                }
                else
                {
                    maxLen = 20;
                }
                MapAttr attr = new MapAttr();
                string mypk = frmID + "_" + attrKey;
                attr.setMyPK(mypk);
                if (attr.RetrieveFromDBSources() == 1)
                    continue;

                //检查attrKey 是否符合字段要求。
                attr.setKeyOfEn(attrKey);
                if (DataType.CheckIsFieldOrTableName(attr.KeyOfEn) == false)
                    continue;

                attr.SetValByKey("FK_MapData", frmID);
                attr.setName(cnName);
                attr.setMyDataType(dataType);
                attr.setMinLen(minLen);
                attr.setMaxLen(maxLen); //最大长度.
                attr.setIdx(idx);
                attr.setGroupID(groupID);
                //判断是否是boolen?
                if (note.Equals("Boolen") == true)
                {
                    attr.UIContralType = UIContralType.CheckBok;
                    attr.MyDataType = BP.DA.DataType.AppBoolean;
                    attr.Insert();
                    continue;
                }
                if (attr.Name.Contains("附件") == true)
                {
                    attr.UIContralType = UIContralType.AthShow;
                    attr.SetPara("EnName", "TS.FrmUI.FrmAttachmentExt");
                    attr.Insert();

                    BP.Sys.CCFormAPI.CreateOrSaveAthSingle(attr.FrmID, attr.KeyOfEn, attr.Name, 0, 0);
                    string pkvalAth = attr.FrmID + "_" + attr.KeyOfEn;
                    FrmAttachment ath = new FrmAttachment(pkvalAth);
                    ath.UploadType = AttachmentUploadType.Single; //字段附件.
                    ath.DirectUpdate();
                    DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE CtrlID='" + pkvalAth + "'");
                    continue; //附件.
                }

                #region 对文本框的判断.
                if (maxLen > 255)
                {
                    attr.UIContralType = UIContralType.TB;
                    attr.setUIIsLine(true);
                    attr.UIHeight = 150;
                    attr.SetValByKey("TextModel", 2);

                    if (md.TableCol == 6)
                        attr.SetValByKey("ColSpan", 6);
                    else
                        attr.SetValByKey("ColSpan", 4);

                    attr.Insert();
                    continue; //超大文本.
                }
                if (maxLen <= 255 && maxLen >= 60)
                {
                    attr.UIContralType = UIContralType.TB;
                    attr.setUIIsLine(true);
                    if (md.TableCol == 6)
                        attr.SetValByKey("ColSpan", 5);
                    else
                        attr.SetValByKey("ColSpan", 3);
                    attr.Insert();
                    continue; //一行显示.
                }

                if (note.Length <= 2)
                {
                    attr.UIContralType = UIContralType.TB;
                    attr.Insert(); // 普通字段.
                    continue;
                }
                #endregion 对文本框的判断.

                //note: 执行枚举设置.
                attr.setLGType(FieldTypeS.Enum); //设置枚举类型.
                attr.setUIBindKey(attrKey);
                attr.UIContralType = UIContralType.RadioBtn;
                attr.SetPara("RBShowModel", 3);//横向展示.

                //判断是否有此枚举值?
                if (SystemConfig.CCBPMRunModel == CCBPMRunModel.Single || SystemConfig.CCBPMRunModel == CCBPMRunModel.GroupInc)
                {
                    BP.Sys.SysEnumMain main = new BP.Sys.SysEnumMain();
                    main.No = attr.KeyOfEn;
                    if (main.RetrieveFromDBSources() == 0)
                    {
                        main.No = attrKey;
                        main.Name = cnName;

                        string mynote = "";
                        string[] eles = note.Split('|');
                        for (int i = 0; i < eles.Length; i++)
                            mynote += "@" + i + "=" + eles[i];
                        main.CfgVal = mynote;
                        main.Insert();
                        main.DoInitDtls(); //执行.
                        if (eles.Length > 6)
                            attr.UIContralType = UIContralType.DDL;
                        else
                            attr.UIContralType = UIContralType.RadioBtn;
                    }
                }
                if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                {
                    BP.Sys.SysEnumMain main = new BP.Sys.SysEnumMain();
                    main.No = WebUser.OrgNo + "_" + attr.KeyOfEn;
                    if (main.RetrieveFromDBSources() == 0)
                    {
                        main.No = attrKey;
                        main.Name = cnName;
                        string mynote = "";
                        string[] eles = note.Split(',');
                        for (int i = 0; i < eles.Length; i++)
                            mynote += "@" + i + "=" + eles[i];
                        main.CfgVal = mynote;
                        main.Insert();
                        main.DoInitDtls(); //执行.
                        if (eles.Length > 10)
                            attr.UIContralType = UIContralType.DDL;
                        else
                            attr.UIContralType = UIContralType.RadioBtn;
                    }
                }



                attr.Insert(); //执行插入.
            }
            return "保存成功，请刷新表单显示增加的字段.";
        }

        public string AiFrm_IconGener()
        {
            string frmID = this.FrmID;
            string words = this.GetRequestVal("Words");

            #region 1. 生成 words.
            if (String.IsNullOrEmpty(words))
            {
                MapAttrs attrs = new MapAttrs();
                attrs.Retrieve("FK_MapData", frmID);

                words = "文本字段:";
                foreach (MapAttr item in attrs)
                {
                    if (item.MyDataType == DataType.AppString)
                        words += item.Name + "," + item.KeyOfEn + ";";
                }
                words += "\t\n:数值字段:";
                foreach (MapAttr item in attrs)
                {
                    if (item.UIVisible == false) continue;
                    if (item.ItIsNum == true)
                        words += item.Name + "," + item.KeyOfEn + ";";
                }
                words += "\t\n:枚举字段:";
                foreach (MapAttr item in attrs)
                {
                    if (item.UIVisible == false) continue;
                    if (item.ItIsNum == true && item.UIContralType != UIContralType.TB)
                        words += item.Name + "," + item.KeyOfEn + ";";
                }
                words = "表单有如下字段" + words;
            }
            #endregion 生成 words.

            //string json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(words, false, true);
            string json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(words);
            if (BP.DA.DataType.IsNullOrEmpty(json) == true)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");
            if (json.Contains("err@") == true)
                throw new Exception("err@AI返回结果错误:" + json);

            JArray jsonArray = null;
            try
            {
                jsonArray = JArray.Parse(json);
            }
            catch (Exception ex)
            {
                throw new Exception("err@AI没有返回格式有误，请重试." + ex.Message + " JSON:" + json);
            }

            if (jsonArray == null || jsonArray.Count == 0)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("KeyOfEn");
            dt.Columns.Add("Name");
            dt.Columns.Add("Icon");

            foreach (JObject jsonObject in jsonArray)
            {
                string name = jsonObject["name"].ToString();
                string cnname = jsonObject["cnname"].ToString();
                string icon
                    = jsonObject["icon"].ToString();

                DataRow dr = dt.NewRow();
                dr["No"] = name + "$" + icon;//"$icon-drop";
                dr["KeyOfEn"] = name;
                dr["Name"] = cnname;
                dr["Icon"] = icon;
                dt.Rows.Add(dr);
            }
            return BP.Tools.Json.ToJson(dt);
        }
        //保存.
        public string AiFrm_IconSave()
        {
            string valsStr = this.GetRequestVal("Vals");
            string[] vals = valsStr.Split(',');
            foreach (string str in vals)
            {
                if (DataType.IsNullOrEmpty(str) == true)
                    continue;

                string[] strs = str.Split("$");
                string attrKey = strs[0];
                string icon = strs[1];
                DBAccess.RunSQL("UPDATE Sys_MapAttr SET Icon='" + icon + "' WHERE MyPK='" + FrmID + "_" + attrKey + "'");
            }
            return "保存成功.";
        }

        public DataTable AiFrm_MatchIcons()
        {
            string frmID = this.FrmID;
            string words = this.GetRequestVal("Words");

            MapAttrs attrs = new MapAttrs();
            attrs.Retrieve("FK_MapData", frmID);

            string names = ",";
            foreach (MapAttr item in attrs)
                names += item.Name + ",";

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("KeyOfEN");
            dt.Columns.Add("DataType");
            dt.Columns.Add("MinLen");
            dt.Columns.Add("MaxLen");
            dt.Columns.Add("Note");

            //string json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(frmID, words, names, false, true);
            string json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(words);
            if (BP.DA.DataType.IsNullOrEmpty(json) == true)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");
            if (json.Contains("err@") == true)
                throw new Exception("err@AI返回结果错误:" + json);

            JArray jsonArray = null;
            try
            {
                jsonArray = JArray.Parse(json);
            }
            catch (Exception ex)
            {
                throw new Exception("err@AI没有返回格式有误，请重试.");
            }

            if (jsonArray == null || jsonArray.Count == 0)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词.");

            foreach (JObject jsonObject in jsonArray)
            {
                string name = jsonObject["name"].ToString();
                string cnname = jsonObject["cnname"].ToString();
                Object lengthObj = jsonObject["length"];
                if (lengthObj == null)
                    lengthObj = "0";
                String lengthStr = lengthObj.ToString();
                String[] lengthArray = { lengthStr };
                if (lengthStr.IndexOf(',') >= 0)
                    lengthArray = lengthStr.Split(',');

                string datatype = (String)jsonObject["datatype"];
                string enumval = (String)jsonObject["enum"];
                if (!String.IsNullOrEmpty(enumval) && enumval.IndexOf(",") > 0)
                {
                    datatype = "ENUM";
                }

                string note = "无";
                int maxLen = 100;
                int datatypeInt = DA.DataType.AppString;


                DataRow dr = dt.NewRow();
                string no = name + "*" + cnname + "*" + datatypeInt + "*0*" + maxLen + "*" + note;
                dr["No"] = no;
                dr["Name"] = cnname;
                dr["KeyOfEN"] = name;
                dr["DataType"] = datatypeInt;
                dr["MinLen"] = 0;
                dr["MaxLen"] = maxLen;
                dr["Note"] = note;
                dt.Rows.Add(dr);
            }
            return dt;
        }

        #endregion AI表单.

        #region AI表单. - 上传文件解析表单.

        /// <summary>
        /// 上传文件-解析表单
        /// </summary>
        /// <returns></returns>
        public string AiFrm_File()
        {
            string frmID = this.FrmID;
            return null;
        }
        #endregion AI表单.

        #region AI流程. - 流程.
        public string AiFlow_NodesDeliveryWayGener()
        {
            string flowNo = this.FlowNo;
            Nodes nds = new Nodes();
            nds.Retrieve("FK_Flow", flowNo, "NodeID");


            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("接受人规则");
            dt.Columns.Add("相关属性");


            foreach (Node item in nds)
            {
                DataRow dr = dt.NewRow();
                dr["No"] = item.NodeID;
                dr["Name"] = item.Name;
                dr["接受人规则"] = item.HisDeliveryWayText;
                dr["相关属性"] = "无";
                if (item.HisDeliveryWay == DeliveryWay.ByBindEmp)
                {
                    NodeEmps nes = new NodeEmps(item.NodeID);
                    string str = "人员信息:";
                    foreach (NodeEmp nd in nes)
                    {
                        str += nd.EmpName + ",";
                    }
                    dr["相关属性"] = str;
                }

                if (item.HisDeliveryWay == DeliveryWay.ByDept)
                {
                    NodeDepts nes = new NodeDepts(item.NodeID);
                    string str = "部门信息：";
                    foreach (NodeDept nd in nes)
                    {
                        str += nd.GetValRefTextByKey("FK_Dept") + ",";
                    }
                    dr["相关属性"] = str;
                }
                if (item.HisDeliveryWay == DeliveryWay.ByStation || item.HisDeliveryWay == DeliveryWay.ByStationOnly)
                {
                    NodeStations nes = new NodeStations(item.NodeID);
                    string str = "岗位信息:";
                    foreach (NodeStation nd in nes)
                        str += nd.StationT + ",";
                    dr["相关属性"] = str;
                }
                //dr["AI建议"] = "无";
                //dr["AI接受人规则"] = "无";
                //dr["AI相关属性"] = "无";
                dt.Rows.Add(dr);
            }

            string words = "驰骋BPM是一个流程引擎，每个节点有绑定人员，绑定岗位，部门等接受人员权限设置。";
            //   words += " ccbpm 提供了如下接收人规则：  public enum DeliveryWay\r\n    {\r\n        /// <summary>\r\n        /// 按角色(以部门为纬度)\r\n        /// </summary>\r\n        ByStation = 0,\r\n        /// <summary>\r\n        /// 本部门内的人员\r\n        /// </summary>\r\n        FindSpecDeptEmpsInStationlist = 19,\r\n        /// <summary>\r\n        /// 按部门\r\n        /// </summary>\r\n        ByDept = 1,\r\n        /// <summary>\r\n        /// 按SQL\r\n        /// </summary>\r\n        BySQL = 2,\r\n        /// <summary>\r\n        /// 按本节点绑定的人员\r\n        /// </summary>\r\n        ByBindEmp = 3,\r\n        /// <summary>\r\n        /// 由上一步发送人选择\r\n        /// </summary>\r\n        BySelected = 4,\r\n        /// <summary>\r\n        /// 所有人员都可以发起\r\n        /// </summary>\r\n        BySelected_1 = 41,\r\n        /// <summary>\r\n        /// 固定范围的选择\r\n        /// </summary>\r\n        BySelected_2 = 60,\r\n        /// <summary>\r\n        /// 按表单选择人员\r\n        /// </summary>\r\n        ByPreviousNodeFormEmpsField = 5, //字段是主表.\r\n        ByPreviousNodeFormEmpsFrmDtl = 501, //字段是从表.\r\n        ByPreviousNodeFormEmpsTeam = 502, // 字段是权限组.\r\n        ByPreviousNodeFormDepts = 503, //字段是部门编号\r\n        /// <summary>\r\n        /// 按照角色计算\r\n        /// </summary>\r\n        ByPreviousNodeFormStationsAI = 53,\r\n        /// <summary>\r\n        /// 智能计算\r\n        /// </summary>\r\n        ByPreviousNodeFormStationsOnly = 54,\r\n        /// <summary>\r\n        /// 与上一节点的人员相同\r\n        /// </summary>\r\n        ByPreviousNodeEmp = 6,\r\n        /// <summary>\r\n        /// 与开始节点的人员相同\r\n        /// </summary>\r\n        ByStarter = 7,\r\n        /// <summary>\r\n        /// 与指定节点的人员相同\r\n        /// </summary>\r\n        BySpecNodeEmp = 8,\r\n        /// <summary>\r\n        /// 按角色与部门交集计算\r\n        /// </summary>\r\n        ByDeptAndStation = 9,\r\n        /// <summary>\r\n        /// 按角色计算(以部门集合为纬度)\r\n        /// </summary>\r\n        //ByStationAndEmpDept = 10,\r\n        /// <summary>\r\n        /// 按指定节点的人员或者指定字段作为人员的角色计算\r\n        /// </summary>\r\n        BySpecNodeEmpStation = 11,\r\n        /// <summary>\r\n        /// 按SQL确定子线程接受人与数据源.\r\n        /// </summary>\r\n        BySQLAsSubThreadEmpsAndData = 12,\r\n        /// <summary>\r\n        /// 按明细表确定子线程接受人.\r\n        /// </summary>\r\n        ByDtlAsSubThreadEmps = 13,\r\n        /// <summary>\r\n        /// 仅按角色计算\r\n        /// </summary>\r\n        ByStationOnly = 14,\r\n        /// <summary>\r\n        /// FEE计算.\r\n        /// </summary>\r\n        ByFEE = 15,\r\n        /// <summary>\r\n        /// 按绑定部门计算,该部门一人处理标识该工作结束(子线程).\r\n        /// </summary>\r\n        BySetDeptAsSubthread = 16,\r\n        /// <summary>\r\n        /// 按SQL模版计算\r\n        /// </summary>\r\n        BySQLTemplate = 17,\r\n        /// <summary>\r\n        /// 从人员到人员\r\n        /// </summary>\r\n        ByFromEmpToEmp = 18,\r\n        /// <summary>\r\n        /// 按照角色计算-范围内的\r\n        /// </summary>\r\n        ByStationForPrj = 20,\r\n        /// <summary>\r\n        /// 按照选择模式计算.\r\n        /// </summary>\r\n        BySelectedForPrj = 21,\r\n        /// <summary>\r\n        /// 按照设置的组织计算\r\n        /// </summary>\r\n        BySelectedOrgs = 22,\r\n        /// <summary>\r\n        /// 按照部门领导计算\r\n        /// </summary>\r\n        ByDeptLeader = 23,\r\n        /// <summary>\r\n        /// 按照部门分管领导计算\r\n        /// </summary>\r\n        ByDeptShipLeader = 28,\r\n        /// <summary>\r\n        /// 找自己的直属领导.\r\n        /// </summary>\r\n        ByEmpLeader = 50,\r\n        /// <summary>\r\n        /// 按照用户组计算(本组织范围内)\r\n        /// </summary>\r\n        ByTeamOrgOnly = 24,\r\n        /// <summary>\r\n        /// 按照用户组计算(全集团)\r\n        /// </summary>\r\n        ByTeamOnly = 25,\r\n        /// <summary>\r\n        /// 按照用户组计算(本部门范围内)\r\n        /// </summary>\r\n        ByTeamDeptOnly = 26,\r\n        /// <summary>\r\n        /// 按照绑定角色的用户组人员\r\n        /// </summary>\r\n        ByBindTeamEmp = 27,\r\n        /// <summary>\r\n        /// 按照组织模式人员选择器\r\n        /// </summary>\r\n        BySelectedEmpsOrgModel = 43,\r\n        /// <summary>\r\n        /// 按照自定义Url模式的人员选择器\r\n        /// </summary>\r\n        BySelfUrl = 44,\r\n        /// <summary>\r\n        /// 按照设置的WebAPI接口获取的数据计算\r\n        /// </summary>\r\n        ByAPIUrl = 45,\r\n        /// <summary>\r\n        /// 发送人的上级部门的负责人\r\n        /// 就是找上级领导主管.\r\n        /// </summary>\r\n        BySenderParentDeptLeader = 46,\r\n        /// <summary>\r\n        /// 发送人上级部门指定的角色\r\n        /// </summary>\r\n        BySenderParentDeptStations = 47,\r\n        /// <summary>\r\n        /// 外部用户\r\n        /// </summary>\r\n        ByGuest = 51,\r\n\r\n        /// <summary>\r\n        /// 选择其他组织的联络员\r\n        /// </summary>\r\n        BySelectEmpByOfficer = 55,\r\n        /// <summary>\r\n        /// 绑定字典表\r\n        /// </summary>\r\n        BySFTable = 52,\r\n        /// <summary>\r\n        /// 按指定的部门集合与设置的岗位交集\r\n        /// </summary>\r\n        ByStationSpecDepts = 56,\r\n        /// <summary>\r\n        /// 按指定的角色集合与设置的部门交集\r\n        /// </summary>\r\n        ByStationSpecStas = 57,\r\n        /// <summary>\r\n        /// 按照分管领导计算.\r\n        /// </summary>\r\n        ByDeptSpecer = 58,\r\n        /// <summary>\r\n        /// 连续多级主管 - 指定角色模式 模式\r\n        /// </summary>\r\n        ByMLeader0 = 701,\r\n        /// <summary>\r\n        /// 连续多级主管 - 指定指定通讯录等级 模式\r\n        /// </summary>\r\n        ByMLeader1 = 702,\r\n        /// <summary>\r\n        /// 按照ccflow的BPM模式处理\r\n        /// </summary>\r\n        ByCCFlowBPM = 100,\r\n        /// <summary>\r\n        /// 预置:自由选择.\r\n        /// </summary>\r\n        PreplaceWokerFree = 710,\r\n        /// <summary>\r\n        /// 预置:固定模式\r\n        /// </summary>\r\n        PreplaceWokerFix = 711,\r\n    } ";


            //    string words1= "如下是一个流程节点的json表达式，No=是节点ID, Name是节点名称, 接受人规则是接受人规则. ";

            string json = BP.Tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("CheckFlowDeliveryWay" + flowNo, json);
            return BP.Tools.Json.ToJson(dt);
        }
        /// <summary>
        /// 投递规则保存.
        /// </summary>
        /// <returns></returns>
        public string AiFlow_NodesDeliveryWaySave()
        {
            string flowNo = this.GetRequestVal("FlowNo");
            DataTable dt = BP.Tools.Json.ToDataTable(WebUser.TempValGetByKey("CheckFlowDeliveryWay" + flowNo));

            foreach (DataRow dr in dt.Rows)
            {

            }
            return "保存成功.";
        }
        public string AiFlow_SaveFlow()
        {
            string flowWords = this.GetRequestVal("FlowWords");
            string frmWords = this.GetRequestVal("FrmWords");

            #region 设置流程.
            //DataTable dt = BP.WF.HttpHandler.WF_Admin_AI_Client.AIFlow_GenerFlow_ByWords(flowWords, "", true, true);
            DataTable dt = BP.WF.HttpHandler.WF_Admin_AI_Client.AIFlow_GenerFlow_ByWords(flowWords, "");

            string flowName = "我的AI流程";
            if (dt.Rows.Count > 1)
                flowName = dt.Rows[0][4].ToString();

            //创建流程.
            string sortNo = this.GetRequestVal("SortNo");
            string flowNo = BP.WF.Template.TemplateGlo.NewFlowTemplate(sortNo, flowName, Template.DataStoreModel.SpecTable, "", null);

            Flow fl = new Flow(flowNo);
            fl.FlowDevModel = FlowDevModel.JiJian; //流程开发模式.
            fl.FrmUrl = "ND" + int.Parse(flowNo + "01");
            fl.Update();
            int nodeIDStart = int.Parse(fl.No + "01");
            int nodeID2 = int.Parse(fl.No + "02");
            Node currND = null;
            for (int i = 0; i < dt.Rows.Count; i++)
            {
                DataRow dr = dt.Rows[i];
                string nodeNo = dr[0].ToString(); //编号序号.
                string name = dr[1].ToString(); //节点名称.
                string stationName = dr[2].ToString(); //岗位名称.
                string desc = dr[3].ToString(); //备注.
                if (i == 0)
                {
                    Node nd1 = new Node(nodeIDStart);
                    nd1.Name = name;
                    nd1.Update();
                    continue;
                }
                if (i == 1)
                {
                    Node nd2 = new Node(nodeID2);
                    nd2.Name = name;
                    nd2.Update();
                    currND = nd2;
                    continue;
                }

                //创建节点.
                Node nd = BP.WF.Template.TemplateGlo.NewNode(fl.No, currND.X + 100, currND.Y + 100);
                nd.Name = name;
                nd.DirectUpdate();

                //设置连接线.
                Direction dir = new Direction();
                dir.setMyPK(fl.No + "_" + currND.NodeID + "_" + nd.NodeID);
                dir.FlowNo = fl.No;
                dir.Node = currND.NodeID;
                dir.ToNode = nd.NodeID;
                dir.Insert();
                //设置当前节点.
                currND = nd;
                //如果是最后一个节点.
                if (i == dt.Rows.Count - 1)
                {
                    //与开始节点相同.
                    nd.HisDeliveryWay = DeliveryWay.ByStarter;
                    nd.FrmWorkCheckSta = FrmWorkCheckSta.Readonly;
                    nd.Update();
                }
            }
            #endregion 设置流程.

            #region 设置表单.
            //设置表单.
            string frmID = "ND" + nodeIDStart;
            DataTable mydt = this.AiFrm_GenerAttrsExt(frmID, frmWords);
            string attrs = "";
            foreach (DataRow dr in mydt.Rows)
            {
                attrs += dr[0].ToString() + ",";
            }

            this.AddPara("SelectedAttrs", attrs); //增加选择的值.
            this.AddPara("FrmID", frmID); //增加选择的值.

            //保存数据到临时变量.
            WebUser.TempValSaveByKey("FrmAttr" + this.FrmID, BP.Tools.Json.ToJson(mydt));
            this.AiFrm_SaveAttrs(); //保存到表单.
            #endregion 设置表单.
            return fl.No;
        }
        public string AiFrm_TestDBGener()
        {
            string frmID = this.FrmID;
            GEEntity en = new GEEntity(frmID);
            en.CheckPhysicsTable();

            string sql = "";
            switch (DBAccess.AppCenterDBType)
            {
                case DBType.Oracle:
                case DBType.KingBaseR3:
                case DBType.KingBaseR6:
                case DBType.DM:
                case DBType.GBASE8CByOracle:
                    sql = SqlBuilder.GenerCreateTableSQLOfOra(en);
                    break;
                case DBType.Informix:
                    sql = SqlBuilder.GenerCreateTableSQLOfInfoMix(en);
                    break;
                case DBType.PostgreSQL:
                case DBType.UX:
                case DBType.HGDB:
                    sql = SqlBuilder.GenerCreateTableSQLOfPostgreSQL(en);
                    break;
                case DBType.MSSQL:
                    sql = SqlBuilder.GenerCreateTableSQLOfMS(en);
                    break;
                case DBType.MySQL:
                    sql = SqlBuilder.GenerCreateTableSQLOfMySQL(en);
                    break;
                case DBType.Access:
                    sql = SqlBuilder.GenerCreateTableSQLOf_OLE(en);
                    break;
                default:
                    throw new Exception("@未判断的数据库类型。");
            }

            int dataTotal = 5;
            //创建表的SQL如下 @hwz
            string words = "创建表的SQL：[" + sql + "]请根据表结构: 生成" + dataTotal + "条插入数据，OID字段以 10开始递增. ";

            //String json = BP.WF.HttpHandler.WF_Admin_AI_Client.AiFrm_GenerTestDB_ByWords(words, true, true);
            String json = BP.WF.HttpHandler.WF_Admin_AI_Client.AiFrm_GenerTestDB_ByWords(words);

            if (String.IsNullOrEmpty(json))
                json = "";

            if (json.Contains("err@") == true && json.Length <= 100)
                return json;

            JArray jsonArray = JArray.Parse(json);
            if (jsonArray == null || jsonArray.Count == 0)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词..");

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("TestSQL");

            int i = 1;
            foreach (JObject jsonObject in jsonArray)
            {
                string item = (string?)jsonObject.First; //逻辑编号ID. BindFunction
                if (String.IsNullOrWhiteSpace(item))
                    continue;

                DataRow dr = dt.NewRow();
                dr["No"] = i.ToString();
                dr["Name"] = "第" + i + "条";
                dr["TestSQL"] = item; // "INSERT INTO XXXX " + i;
                dt.Rows.Add(dr);
                i++;
            }
            string str = BP.Tools.Json.ToJson(dt);
            BP.Web.WebUser.TempValSaveByKey("TestDB" + this.FrmID, str);
            return str;
        }
        public string AiFrm_TestDBSave()
        {
            string json = BP.Web.WebUser.TempValGetByKey("TestDB" + this.FrmID);
            DataTable dt = BP.Tools.Json.ToDataTable(json);
            foreach (DataRow dr in dt.Rows)
            {
                string sql = dr["TestSQL"].ToString();
                if (!string.IsNullOrEmpty(sql))
                {
                    String[] wavearray = sql.Split('~');
                    if (wavearray.Length > 10)
                    {
                        sql = sql.Replace("~", "'");
                    }
                }
                DBAccess.RunSQL(sql);
            }
            return "保存成功.";
        }

        /// <summary>
        /// 保存字段.
        /// </summary>
        /// <returns></returns>
        public string AiFrm_SaveNodes()
        {
            string frmID = this.GetRequestVal("FlowNo"); //所有的字段.
            string selectedAttrs = this.GetRequestVal("SelectedNodes");
            string[] strs = selectedAttrs.Split(',');
            foreach (string str in strs)
            {
                if (BP.DA.DataType.IsNullOrEmpty(str) == true)
                    continue;
                string[] array = str.Split('*');
                if (array == null || array.Length > 6)
                    continue;

                string attrKey = array[0];
                string cnName = array[1];
                int datatype = int.Parse(array[2]);
                int minLen = int.Parse(array[3]);
                int maxLen = int.Parse(array[4]);
                string note = array[5]; //备注.

                MapAttr attr = new MapAttr();
                string mypk = frmID + "_" + attrKey;
                attr.setMyPK(mypk);
                if (attr.RetrieveFromDBSources() == 1)
                    continue;

                attr.SetValByKey("FK_MapData", frmID);
                attr.setKeyOfEn(attrKey);
                attr.setName(cnName);
                attr.setMyDataType(datatype);
                attr.setMinLen(minLen);
                attr.setMaxLen(maxLen); //最大长度.
                if (note.Length <= 3)
                {
                    attr.UIContralType = UIContralType.TB;
                    attr.Insert();
                    continue;
                }

                try
                {
                    string x = this.GetRequestVal("X");
                    string y = this.GetRequestVal("Y");
                    string icon = this.GetRequestVal("icon");
                    int nodeModel = this.GetRequestValInt("NodeModel");

                    int iX = 20;
                    int iY = 20;

                    if (BP.DA.DataType.IsNullOrEmpty(x) == false)
                        iX = (int)double.Parse(x);

                    if (BP.DA.DataType.IsNullOrEmpty(y) == false)
                        iY = (int)double.Parse(y);

                    Node node = BP.WF.Template.TemplateGlo.NewNode(this.FlowNo, iX, iY, icon, nodeModel);


                    Hashtable ht = new Hashtable();
                    ht.Add("NodeID", node.NodeID);
                    ht.Add("Name", node.Name);
                    ht.Add("RunModel", (int)node.HisRunModel);
                    ht.Add("NodeType", 0); //用户节点。

                    return BP.Tools.Json.ToJsonEntityModel(ht);
                }
                catch (Exception ex)
                {
                    return "err@" + ex.Message;
                }


            }
            return "保存成功，请刷新表单显示增加的字段.";
        }
        #endregion AI流程.

        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_Admin_AI()
        {
        }

        #region AI 应用.
        /// <summary>
        /// 获得菜单.
        /// </summary>
        /// <returns></returns>
        public string App_Menus()
        {
            string words = this.GetRequestVal("Words");
            //string json = BP.WF.HttpHandler.WF_Admin_AI_Client.App_ByWords(words, true, true);
            string json = BP.WF.HttpHandler.WF_Admin_AI_Client.App_ByWords(words);
            if (json.Contains("err@") == true && json.Length <= 100)
                return json;

            JObject jsonArray = JObject.Parse(json);
            if (jsonArray == null)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词..");

            DataTable baseInfo = null; //基础信息表.
            DataTable modules = null; //模块
            DataTable menus = null; //菜单.
            Console.WriteLine(jsonArray.ToString());
            foreach (JProperty jsonObject in jsonArray.Properties())
            {
                if ((String)jsonObject.Name.ToUpper() == "BASEINFO")
                {
                    String jsonStr = jsonObject.Value.ToString();
                    baseInfo = BP.Tools.Json.ToDataTable(jsonStr);
                }

                if ((String)jsonObject.Name.ToUpper() == "MODULES")
                {
                    String jsonStr = jsonObject.Value.ToString();
                    modules = BP.Tools.Json.ToDataTable(jsonStr);
                }

                if ((String)jsonObject.Name.ToUpper() == "MENUS")
                {
                    String jsonStr = jsonObject.Value.ToString();
                    menus = BP.Tools.Json.ToDataTable(jsonStr);
                }
            }
            if (baseInfo == null)
                throw new Exception("err@AI没有返回表单结果，请换一个提示词:" + json);

            DataTable dt = new DataTable();
            dt.Columns.Add("No");
            dt.Columns.Add("Name");
            dt.Columns.Add("Note"); //模块编号.
            dt.Columns.Add("MenuType"); //类型.
            dt.Columns.Add("ModuleNo"); //模块编号.
            dt.Columns.Add("ModuleName"); //模块名称.
            dt.Columns.Add("SystemName"); //模块名称.

            string systemName = baseInfo.Rows[0][1].ToString();

            //根目录.
            DataRow dr = dt.NewRow();
            //dr["No"] = "100";
            //dr["Name"] = systemName;
            //dr["ParentNo"] = "0";
            //dt.Rows.Add(dr); //增加根目录.

            foreach (DataRow dr1Model in modules.Rows)
            {
                string moduleNo = dr1Model[0].ToString();
                string moduleName = dr1Model[1].ToString();

                DataRow mydr = dt.NewRow();
                //格式:  No$Name$SystemName
                mydr["No"] = moduleNo + "$" + moduleName + "$" + systemName;
                mydr["Name"] = "(+)" + moduleName;
                mydr["Note"] = "模块";
                dt.Rows.Add(mydr);

                foreach (DataRow drMenu in menus.Rows)
                {
                    string myModuleNo = drMenu["ModuleNo"].ToString();
                    if (myModuleNo.Equals(moduleNo) == false)
                        continue;

                    string menuNo = drMenu["MenuNo"].ToString();
                    string menuName = drMenu["MenuName"].ToString();
                    string menuType = drMenu["MenuType"].ToString();

                    DataRow mydr2 = dt.NewRow();
                    //格式: No*+Name*Type*SortNo 
                    mydr2["No"] = menuNo + "*" + menuName + "*" + menuType + "*" + moduleNo;
                    mydr2["MenuType"] = menuType; //菜单类型.
                    if (menuType == "Dict")
                        menuType = "实体";
                    if (menuType == "Bill")
                        menuType = "单据";
                    if (menuType == "Rpt")
                        menuType = "报表";
                    mydr2["Note"] = "菜单"; //菜单类型.
                    mydr2["Name"] = "----" + menuName; //类型名称.
                    mydr2["ModuleNo"] = mydr["No"]; //模块名称.
                    mydr2["ModuleName"] = mydr["Name"];
                    dt.Rows.Add(mydr2);
                }
            }
            //临时保存掉.
            json = BP.Tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("App" + WebUser.Token, json);
            return json;
        }
        /// <summary>
        /// 菜单保存.
        /// </summary>
        /// <returns></returns>
        /// <exception cref="Exception"></exception>
        public string App_MenuSave()
        {
            string json = WebUser.TempValGetByKey("App" + WebUser.Token);
            DataTable dt = BP.Tools.Json.ToDataTable(json);

            #region 组织数据.
            DataTable modules = new DataTable(); //模块
            modules.Columns.Add("ModuleNo");
            modules.Columns.Add("ModuleName");

            DataTable menus = new DataTable(); //菜单.
            menus.Columns.Add("MenuNo");
            menus.Columns.Add("MenuName");
            menus.Columns.Add("MenuType");
            menus.Columns.Add("ModuleNo");

            //获得选择的数据.
            string nodes = this.GetRequestVal("Nodes");
            string[] strs = nodes.Split(',');
            string systemName = "";

            foreach (string str in strs)
            {
                if (DataType.IsNullOrEmpty(str))
                    continue;
                //系统.
                if (str.Contains("$") == true)
                {
                    string[] vals = str.Split("$");
                    DataRow dr = modules.NewRow();
                    dr["ModuleNo"] = vals[0];
                    dr["ModuleName"] = vals[1];
                    systemName = vals[2];
                    modules.Rows.Add(dr);
                }

                //菜单.
                if (str.Contains("*") == true)
                {
                    string[] vals = str.Split("*");
                    DataRow dr = menus.NewRow();
                    dr["MenuNo"] = vals[0];
                    dr["MenuName"] = vals[1];
                    dr["MenuType"] = vals[2];
                    dr["ModuleNo"] = vals[3];
                    menus.Rows.Add(dr);
                }
            }
            //系统名称.
            string sql = "SELECT count(*) as Num FROM GPM_System WHERE Name='" + systemName + "'";
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                sql = "SELECT count(*) as Num FROM GPM_System WHERE Name='" + systemName + "' AND OrgNo='" + WebUser.OrgNo + "'";
            if (DBAccess.RunSQLReturnValInt(sql, 0) >= 1)
                systemName = systemName + BP.DA.DataType.CurrentDateTime;

            int idx = 0;
            sql = "SELECT count(*) as Num FROM GPM_System ";
            if (SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
                sql = "SELECT count(*) as Num FROM GPM_System WHERE  OrgNo='" + WebUser.OrgNo + "'";
            idx = DBAccess.RunSQLReturnValInt(sql);
            #endregion 组织数据.

            BP.CCFast.CCMenu.MySystem enSystem = new CCFast.CCMenu.MySystem();
            enSystem.setName(systemName);
            enSystem.SetValByKey("Icon", "icon-directions"); //icon.
            enSystem.SetValByKey("Idx", "-" + idx); //放到第一个.
            enSystem.Insert();
            string systemNo = enSystem.No; //系统编号.
            //遍历模块.
            idx = 0;
            foreach (DataRow dr in modules.Rows)
            {
                //创建模块.
                string sortNo = dr[0].ToString();
                string sortName = dr[1].ToString();
                BP.CCFast.CCMenu.Module enModule = new CCFast.CCMenu.Module();
                enModule.setName(sortName);
                enModule.SetValByKey("Icon", "icon-layers");
                enModule.SetValByKey("SystemNo", enSystem.No);
                enModule.SetValByKey("Idx", idx);
                idx++;
                enModule.Insert();
                //创建菜单
                foreach (DataRow drMenu in menus.Rows)
                {
                    idx++;

                    string moduleNo = drMenu["ModuleNo"].ToString();
                    if (sortNo.Equals(moduleNo) == false)
                        continue;
                    string name = drMenu["MenuName"].ToString();//菜单名字.
                    string no = drMenu["MenuNo"].ToString(); //菜单编号
                    string menuType = drMenu["MenuType"].ToString(); //菜单编号

                    #region 创建单据
                    //如果是字典.
                    if (menuType.Equals("Bill"))
                    {
                        //编号.
                        string pinyinJX = BP.DA.DataType.ParseStringToPinyinJianXie(name);
                        string dictNo = "Bill_" + pinyinJX;
                        MapData md = new MapData();
                        md.No = dictNo;
                        int xh = 1;
                        while (md.RetrieveFromDBSources() == 1)
                        {
                            md.No = dictNo + xh;
                            xh++;
                        }
                        if (xh != 1)
                            dictNo = dictNo + xh;

                        BP.WF.HttpHandler.WF_Admin_CCFormDesigner handler = new BP.WF.HttpHandler.WF_Admin_CCFormDesigner();
                        handler.AddPara("TB_No", dictNo);
                        handler.AddPara("TB_Name", name);
                        handler.AddPara("TB_PTable", dictNo);
                        //   handler.AddPara("FK_FrmSort", enSystem.No);
                        handler.AddPara("EntityType", "1"); //创建单据.
                        handler.NewFrmGuide_Create();

                        BP.CCFast.CCMenu.Menu enMenu = new CCFast.CCMenu.Menu();
                        enMenu.SetValByKey("Name", name);
                        enMenu.SetValByKey("UrlExt", dictNo + "?displayMode=table&FrmID=" + dictNo);
                        enMenu.SetValByKey("FrmID", dictNo);
                        enMenu.SetValByKey("ListModel", 0);
                        enMenu.SetValByKey("WorkType", 0);
                        enMenu.SetPara("EnName", "TS.CCBill.FrmBill"); //符合readme.md里面的第3个模式,编辑属性.
                        enMenu.SetPara("EnPKVal", dictNo); //设置主键.

                        enMenu.SetValByKey("MenuModel", menuType);
                        enMenu.SetValByKey("Icon", "icon-user");
                        enMenu.SetValByKey("ModuleNo", enModule.No);
                        enMenu.SetValByKey("ModuleNoT", enModule.Name);
                        enMenu.SetValByKey("SystemNo", enSystem.No);
                        enMenu.SetValByKey("Icon", "icon-user");
                        enMenu.SetValByKey("Idx", idx);
                        idx++;
                        enMenu.Insert();
                    }
                    #endregion 创建.

                    #region 创建实体
                    //如果是实体
                    if (menuType.Equals("Dict") == true)
                    {
                        //.实体编号
                        string pinyinJX = BP.DA.DataType.ParseStringToPinyinJianXie(name);
                        string dictNo = "Dict_" + pinyinJX;
                        MapData md = new MapData();
                        md.No = dictNo;
                        int xh = 1;
                        while (true)
                        {
                            if (md.RetrieveFromDBSources() == 0)
                                break;
                            md.No = dictNo + xh;
                            xh++;
                        }

                        BP.WF.HttpHandler.WF_Admin_CCFormDesigner handler = new BP.WF.HttpHandler.WF_Admin_CCFormDesigner();
                        handler.AddPara("TB_No", md.No);
                        handler.AddPara("TB_Name", name);
                        handler.AddPara("TB_PTable", md.No);
                        //    handler.AddPara("FK_FrmSort", enSystem.No);
                        handler.AddPara("EntityType", "2");
                        handler.NewFrmGuide_Create();

                        BP.CCFast.CCMenu.Menu enMenu = new CCFast.CCMenu.Menu();
                        enMenu.SetValByKey("Name", name);
                        enMenu.SetValByKey("UrlExt", md.No + "?displayMode=table&FrmID=" + md.No);
                        enMenu.SetValByKey("FrmID", md.No);
                        enMenu.SetValByKey("MenuModel", "Dict");
                        enMenu.SetValByKey("ListModel", 0);
                        enMenu.SetValByKey("WorkType", 0);
                        enMenu.SetPara("EnName", "TS.CCBill.FrmDict"); //符合readme.md里面的第3个模式,编辑属性.
                        enMenu.SetPara("EnPKVal", md.No); //设置主键.
                        enMenu.SetValByKey("ListModel", 0);
                        enMenu.SetValByKey("UrlPath", "/src/CCFast/CCBill/SearchDict.vue");
                        enMenu.SetValByKey("MenuModel", "Dict");
                        enMenu.SetValByKey("Icon", "icon-user");
                        enMenu.SetValByKey("ModuleNo", enModule.No);
                        enMenu.SetValByKey("ModuleNoT", enModule.Name);
                        enMenu.SetValByKey("SystemNo", enSystem.No);
                        enMenu.SetValByKey("Icon", "icon-user");
                        enMenu.SetValByKey("Idx", idx);
                        idx++;
                        enMenu.Insert();
                    }
                    #endregion 创建字典.

                    #region 创建查询-分析.
                    //如果是字典.
                    if (menuType.Equals("Rpt") == true)
                    {
                        BP.CCFast.CCMenu.Menu enMenu = new CCFast.CCMenu.Menu();
                        enMenu.No = DBAccess.GenerGUID();
                        enMenu.Name = name;
                        enMenu.SetValByKey("MenuModel", "RptWhite");
                        enMenu.SetValByKey("IsEnable", "1");
                        enMenu.SetValByKey("Icon", "icon-screen-desktop");
                        enMenu.SetPara("EnName", "TS.CCFast.Rpt3D"); //符合readme.md里面的第2个模式.

                        enMenu.SetValByKey("UrlPath", "/src/CCFast/Views/RptWhiteMain.vue");
                        string urlExt = "/RptWhite" + enMenu.No + "?PageID=" + enMenu.No;
                        enMenu.SetValByKey("UrlExt", urlExt);

                        enMenu.SetValByKey("ModuleNo", enModule.No);
                        enMenu.SetValByKey("ModuleNoT", enModule.Name);
                        enMenu.Insert();
                    }
                    #endregion 创建查询-分析.
                }
            }
            return systemNo;
        }
        #endregion AI 应用.


        #region AI 大屏.
        /// <summary>
        /// 生成windows
        /// </summary>
        /// <returns></returns>
        public string WhiteRpt_GenerWindows()
        {
            string words = "";

            MapAttrs attrs = null;
            if (this.FrmID.Contains("TS.") || this.FrmID.Contains("BP."))
            {
                TSEntityOID en = new TSEntityOID(this.FrmID);
                words = "实体表:" + en.EnMap.PhysicsTable + ",实体名称：" + en.EnDesc + ",有如下字段:";
                attrs = en.EnMap.Attrs.ToMapAttrs;
            }
            else
            {
                MapData md = new MapData(this.FrmID);
                //获得字段.
                  attrs = new MapAttrs();
                attrs.Retrieve("FK_MapData", md.No);
                words = "实体表:" + md.PTable + ",实体名称：" + md.Name + ",有如下字段:";
            }

            foreach (MapAttr item in attrs)
            {
                if (item.UIVisible == false)
                    continue;
                if (item.UIContralType == UIContralType.TB && item.MyDataType == DataType.AppString)
                    continue;

                if (item.LGType == FieldTypeS.Enum)
                {
                    SysEnumMain enumMain = new SysEnumMain(item.UIBindKey);
                    words += "枚举字段,字段中文名:" + item.Name + ",英文名:" + item.KeyOfEn + ",枚举项" + enumMain.CfgVal + ";";
                    continue;
                }

                if (item.ItIsNum == true)
                    words += "数值类型,字段中文名:" + item.Name + ",英文名:" + item.KeyOfEn + ";";
            }

            words += "当前数据库是 " + SystemConfig.AppCenterDBType.ToString() + " 数据库.";
            words += "请根据以上表结构描述，结合常规数据分析的经验，给出饼图，柱状图，折线图，雷达图等分析的查询语句。";

            //@hwz 补充这里代码.
            //String json = BP.WF.HttpHandler.WF_Admin_AI_Client.AiFrm_GenerWindows_ByWords(words, false, true);
            String json = BP.WF.HttpHandler.WF_Admin_AI_Client.AiFrm_GenerWindows_ByWords(words);
            if (String.IsNullOrEmpty(json))
                json = "";

            DataTable dt = new DataTable();
            if (string.IsNullOrEmpty(json) || json == "[]")
            {
                //生成的json实例.
                dt = new DataTable();
                dt.Columns.Add("No");
                dt.Columns.Add("Name");
                dt.Columns.Add("ChartName");
                dt.Columns.Add("ChartType");
                dt.Columns.Add("SQLExp");
                dt.Columns.Add("C1Ens");
            }
            else
            {
                dt = BP.Tools.Json.ToDataTable(json);
            }

            /*
            DataRow dr = dt.NewRow();
            dr["No"] = "ChartLine_FK_Dept_Count";
            dr["Name"] = "部门统计";
            dr["ChartName"] = "柱状图";
            dr["ChartType"] = "ChartLine";
            dr["SQLExp"] = "SELECT FK_Dept, COUNT(*) AS NUM FROM Port_Emp Group BY FK_Dept ";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "ChartZZT_FK_Dept_Count";
            dr["Name"] = "部门人员个数统计";
            dr["ChartName"] = "折线图";
            dr["ChartType"] = "ChartZZT";
            dr["SQLExp"] = "SELECT FK_Dept, COUNT(*) AS NUM FROM Port_Emp Group BY FK_Dept ";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "ChartPie_FK_Dept_Count";
            dr["Name"] = "部门人员个数统计";
            dr["ChartName"] = "折线图";
            dr["ChartType"] = "ChartPie";
            dr["SQLExp"] = "SELECT FK_Dept, COUNT(*) AS NUM FROM Port_Emp Group BY FK_Dept ";
            dt.Rows.Add(dr);

            dr = dt.NewRow();
            dr["No"] = "Table_FK_Dept_Count";
            dr["Name"] = "我部门的同事";
            dr["ChartName"] = "表格";
            dr["ChartType"] = "Table";
            dr["SQLExp"] = "SELECT No,Name,Email,Tel FROM Port_Emp  WHERE FK_Dept='@WebUser.No' ";
            dt.Rows.Add(dr);
            */

            string strs = BP.Tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("Rpt" + this.PageID, strs);
            return strs;
        }
        public string WhiteRpt_SaveWindows()
        {
            string vals = WebUser.TempValGetByKey("Rpt" + this.PageID);
            DataTable dataTable = BP.Tools.Json.ToDataTable(vals);

            MapAttrs attrs = new MapAttrs();
            attrs.Retrieve("FK_MapData", this.FrmID);

            string ids = this.Vals;
            foreach (DataRow dr in dataTable.Rows)
            {
                string no = dr[0].ToString();
                if (ids.Contains(no) == false)
                    continue;
                string name = dr[1].ToString();
                string chartType = dr[3].ToString();
                string SQLExp = dr[4].ToString();
                string En0Exp = "";
                if (DataType.IsNullOrEmpty(En0Exp) == true)
                {
                    foreach (MapAttr attr in attrs)
                    {
                        if (attr.LGType != FieldTypeS.Enum)
                            continue;
                        if (SQLExp.Contains(attr.UIBindKey) == true)
                        {
                            En0Exp = "SELECT IntKey as No, Lab as Name FROM Sys_Enum WHERE EnumKey='" + attr.UIBindKey + "'";
                            break;
                        }
                    }
                }


                if (chartType.Equals("ChartLine") == true)
                {
                    BP.CCFast.Portal.WindowExt.ChartLine en = new CCFast.Portal.WindowExt.ChartLine();
                    en.No = no;
                    if (en.RetrieveFromDBSources() == 1)
                        continue;

                    en.Name = name;
                    en.SetValByKey("IsLine", 1);
                    en.SetValByKey("ColSpan", 2);
                    en.SetValByKey("Docs", SQLExp);
                    en.SetValByKey("C0Ens", En0Exp);

                    en.SetValByKey("PageID", this.PageID);
                    en.SetValByKey("WinDocModel", chartType);
                    en.SetPara("EnName", "TS.CCFast.Windows.ChartLine");

                    en.Save();
                }
                if (chartType.Equals("ChartPie") == true)
                {
                    BP.CCFast.Portal.WindowExt.ChartPie en = new CCFast.Portal.WindowExt.ChartPie();
                    en.No = no;
                    en.Name = name;
                    en.SetValByKey("IsPie", 1);
                    en.SetValByKey("ColSpan", 2);
                    en.SetValByKey("Docs", SQLExp);
                    en.SetValByKey("C0Ens", En0Exp);

                    en.SetValByKey("PageID", this.PageID);
                    en.SetValByKey("WinDocModel", chartType);
                    en.SetPara("EnName", "TS.CCFast.Windows." + chartType);
                    en.Save();
                }

                if (chartType.Equals("ChartZZT") == true)
                {
                    BP.CCFast.Portal.WindowExt.ChartZZT en = new CCFast.Portal.WindowExt.ChartZZT();
                    en.No = no;
                    en.Name = name;
                    en.SetValByKey("IsZZT", 1);
                    en.SetValByKey("ColSpan", 2);
                    en.SetValByKey("Docs", SQLExp);
                    en.SetValByKey("C0Ens", En0Exp);

                    en.SetValByKey("PageID", this.PageID);
                    en.SetValByKey("WinDocModel", chartType);
                    en.SetPara("EnName", "TS.CCFast.Windows." + chartType);
                    en.Save();
                }
                if (chartType.Equals("Table") == true)
                {
                    BP.CCFast.Portal.WindowExt.Table en = new CCFast.Portal.WindowExt.Table();
                    en.No = no;
                    en.Name = name;
                    en.SetValByKey("ColSpan", 2);
                    en.SetValByKey("Docs", SQLExp);
                    en.SetValByKey("C0Ens", En0Exp);
                    en.SetValByKey("PageID", this.PageID);
                    en.SetValByKey("WinDocModel", chartType);
                    en.SetPara("EnName", "TS.CCFast.Windows." + chartType);
                    en.Save();
                }
            }

            return "保存成功.";
        }
        #endregion AI 大屏.

        public static void CreateFrm(string frmID, string frmName)
        {
            return;
        }
    }
}
