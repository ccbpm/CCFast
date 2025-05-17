package bp.wf.httphandler;


import bp.da.*;
import bp.en.FieldTypeS;
import bp.en.SqlBuilder;
import bp.en.UIContralType;
import bp.sys.*;
import bp.tools.StringUtils;
import bp.web.WebUser;
import bp.wf.DeliveryWay;
import bp.wf.Flow;
import bp.wf.Node;
import bp.wf.Nodes;
import bp.wf.template.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dm.jdbc.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.apache.xmlbeans.impl.jam.JProperty;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 页面功能实体
 */
public class WF_Admin_AI extends bp.difference.handler.DirectoryPageBase {

    /// <summary>
    /// 构造函数
    /// </summary>
    public WF_Admin_AI()
    {
    }
    public final String getWords() {
        return this.GetRequestVal("Words");
    }

    public final String CopilotCmd() {
        String frmID = this.getFrmID();
        String words = this.getWords();
        String names = this.GetRequestVal("code");
        names = "发起流程|发起:FlowStart,待办:TODO,在途:Running,近期:Recent,单据:BILL";

        //string json = WF_Admin_AI_Client.CopilotCmd(words, names, false, true);
        String json = WF_Admin_AI_Client.CopilotCmd(words, names);
        if (json.contains("err@") == true && json.length() <= 100) {
            return json;
        }

        return json;
    }
    ///#region 代码生成器.

    /**
     * TS代码生成器.
     *
     * @return
     */
    public final String CodeGener_TSSave_test() throws Exception {
        MapData md = new MapData(this.getFrmID());
        DataSet myds = bp.sys.CCFormAPI.GenerHisDataSet(this.getFrmID(), md.getName());
        String json = bp.tools.Json.ToJson(myds); //包含表单模板所有的内容.
        //生成代码.
        return "生成成功.";
    }
    ///#endregion 代码生成器.

    ///#region Ai 工作处理.
    public final String Assistant_Init() {
        return "您可以说：\r\n1.我同意，请提交。 2. 因为什么什么原因，我不同意，请退回。\r\n";
    }

    public final String Assistant_Save() {
        String words = this.GetRequestVal("Words");


        String checkNote = "我同意。";
        String cmd = "Send";


        return "您可以说：\r\n1.我同意，请提交。 2. 因为什么什么原因，我不同意，请退回。\r\n";
    }
    ///#endregion
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#region 从表.

    public final String AiFrm_DtlsGener() throws Exception {
        String frmID = this.getFrmID();
        String words = this.getWords();
        String names = "";

        //string words = this.GetRequestVal("Words");
        //string json = WF_Admin_AI_Client.AIFrm_GenerDetails_ByWord(words, names, false, true);
        String json = WF_Admin_AI_Client.AIFrm_GenerDetails_ByWord(words, names);
        if (json.contains("err@") == true && json.length() <= 100) {
            return json;
        }

        JSONArray jsonArray = JSONArray.fromObject(json);
        if (jsonArray == null) {
            throw new RuntimeException("err@AI没有返回子表单，请尝试更换提示词。");
        }

        DataTable dt = new DataTable();
        dt.TableName = "Dtls";
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("字段");
        dt.Columns.Add("JSON");

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String no = jsonObject.get("no").toString();
            String cnname = jsonObject.get("name").toString();
            String columnsJson = jsonObject.get("columns").toString();
            JSONArray jsonArray2 = JSONArray.fromObject(columnsJson);
            String fields = "";
            for (int j = 0; j < jsonArray2.size(); j++) {
                JSONObject jobj = (JSONObject) jsonArray2.get(j);
                fields += jobj.get("name").toString() + ",";
            }

            //DataTable columnsdt = convert(jsonArray2);

            DataRow dr = dt.NewRow();
            dr.set("No", no);
            dr.set("Name", cnname);
            dr.set("字段", fields);
            dr.set("JSON", columnsJson);
            dt.Rows.add(dr);
        }

        json = bp.tools.Json.ToJson(dt);
        bp.web.WebUser.TempValSaveByKey("FrmDtl" + frmID, json);
        return json;
    }

    /**
     * 选择从表字段
     * <p>
     * 目前仅取一个从表，后续可扩展到多表，ui中的选择方式需要扩展
     *
     * @return
     * @throws Exception
     */
    public final String AiFrm_DtlAttrs() throws Exception {
        String frmID = this.GetRequestVal("FrmID"); //所有的字段.
        String DtlIDs = this.GetRequestVal("DtlID");

        String[] selectedDtlsArray = {};
        if (!StringUtils.isEmpty(DtlIDs)) {
            selectedDtlsArray = DtlIDs.split("[,]", -1);
        }
        if (selectedDtlsArray == null || selectedDtlsArray.length == 0) {
            return "err@ 没有选择任何从表";
        }

        String fromType = "FrmDtl";

        String json = bp.web.WebUser.TempValGetByKey(fromType + this.getFrmID());

        JSONArray jsonArray = null;
        try {
            jsonArray = JSONArray.fromObject(json);
        } catch (RuntimeException ex) {
            throw new RuntimeException("err@json格式有误，请重试." + ex.getMessage());
        }

        //多个从表
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String dtlFrmID = jsonObject.get("No").toString();
            if (!selectedDtlsArray.toString().contains(dtlFrmID)) {
                continue;
            }

            String dtlname = jsonObject.get("Name").toString();
            String dtljson = jsonObject.get("JSON").toString();

            //每一个从表处理
            //DataTable dtAttrs = bp.tools.Json.ToDataTable(dtljson);
            JSONArray dtlArray = JSONArray.fromObject(dtljson);
            DataTable dt = bp.tools.Json.ToDataTable(dtljson);
            //DataTable dt = convert(dtlArray);
            json = bp.tools.Json.ToJson(dt);
            WebUser.TempValSaveByKey("FrmDtlAttrs" + this.getFrmID(), json);
            //WebUser.TempValSaveByKey("FrmDtl" + dtlFrmID, json);
            WebUser.TempValSaveByKey("FrmDtlAttrs_dtlName" + this.getFrmID(), dtlname);
            break; //Only 1

        } //从表end

        return json;
    }

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#region test

    public final String AiFrm_DtlsGener_test() throws Exception {
        String frmID = this.getFrmID();
        String words = this.getWords();

        DataTable dt = new DataTable();
        dt.TableName = "Dtls";
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("字段");
        dt.Columns.Add("JSON");

        for (int i = 0; i < 3; i++) {
            DataRow dr = dt.NewRow();
            dr.set("No", "xx" + i);
            dr.set("Name", "xx" + i);
            dr.set("No", "xx" + i);
            dr.set("字段", "xx" + i);
            dr.set("JSON", "xx" + i);
            dt.Rows.add(dr);
        }

        String json = bp.tools.Json.ToJson(dt);
        WebUser.TempValSaveByKey("FrmDtl" + this.getFrmID(), json);
        return json;
    }

    public final String AiFrm_DtlAttrs_test() throws Exception {
        String json = WebUser.TempValGetByKey("FrmDtl" + this.getFrmID());
        DataTable dt = bp.tools.Json.ToDataTable(json);
        String dtlID = this.GetRequestVal("DtlID");
        for (DataRow dr : dt.Rows) {
            String no = dr.get("No").toString();
            if (no.equals(dtlID) == true) {
                json = dr.get("JSON").toString();
                WebUser.TempValSaveByKey("FrmDtlAttrs" + this.getFrmID(), json);
                return json;

            }
        }
        return "err@系统错误.";
    }

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#endregion

    /**
     * 保存从表
     *
     * @return
     */
    public final String AiFrm_DtlSaveAttrs() throws Exception {
        String frmType = "FrmDtlAttrs";
        String json = WebUser.TempValGetByKey(frmType + this.getFrmID());
        DataTable dt = bp.tools.Json.ToDataTable(json);
        String dtlName = WebUser.TempValGetByKey("FrmDtlAttrs_dtlName" + this.getFrmID());
        String dtlName2 = this.GetRequestVal("DtlName"); //从表名称.
        String dtlID = this.getFrmID() + this.GetRequestVal("DtlID"); //从表ID.

        String attrs = this.GetRequestVal("SelectedAttrs"); //选择的字段.

        //创建从表.
        bp.sys.CCFormAPI.CreateOrSaveDtl(this.getFrmID(), dtlID, dtlName);

        String attrsJson = WebUser.TempValGetByKey(frmType + this.getFrmID());

        //保存从表数据.
        return AiFrm_SaveAttrsExt(dtlID, attrs, attrsJson, frmType);
    }
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#endregion

    public String AiFrm_GenerAttrs() throws Exception {
        String words = this.GetRequestVal("Words");
        DataTable dt = AiFrm_GenerAttrsExt(this.getFrmID(), words);
        dt.TableName = "Attrs";
        String json = bp.tools.Json.ToJson(dt);
        WebUser.TempValSaveByKey("FrmAttr" + this.getFrmID(), json);
        return json;
    }

    /**
     * 批量增加字段
     *
     * @return
     */
    public final String AiFrm_GenerBatchAttrs() throws Exception {
        String words = this.GetRequestVal("Words");
        //@hwz. 修改这里获得数据.
        DataTable dt = AiFrm_GenerAttrsExt(this.getFrmID(), words);
        dt.TableName = "Attrs";
        //DataSet ds = new DataSet();
        //ds.Tables.Add(dt);
        //DataTable dtls = new DataTable();
        String json = bp.tools.Json.ToJson(dt);
        WebUser.TempValSaveByKey("FrmAttrBatch" + this.getFrmID(), json);
        return json;
    }


    /**
     * 获得分析内容字段公式
     *
     * @return
     */
    public final String CheckFrm_GenerFX() throws Exception {
        String frmID = this.getFrmID();
        String words = this.GetRequestVal("Words");
        MapData md = new MapData(this.getFrmID());
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve("FK_MapData", this.getFrmID());
        String vals = "";
        for (MapAttr attr : attrs.ToJavaList()) {
            if (attr.getUIVisible() == false) {
                continue;
            }
            if (attr.getUIContralType().getValue() >= 4) {
                continue;
            }
            if (attr.getMyDataType() == 1 && attr.getLGType().getValue() != 0) {
                vals += "外键字段:" + attr.getName() + "," + attr.getKeyOfEn() + ";";
                continue;
            }
            if (attr.getMyDataType() == 1) {
                vals += "文本字段:" + attr.getName() + "," + attr.getKeyOfEn() + ";";
                continue;
            }
            if (attr.getMyDataType() == 6 || attr.getMyDataType() == 6) {
                vals += "日期字段:" + attr.getName() + "," + attr.getKeyOfEn() + ";";
                continue;
            }
            if (attr.getMyDataType() == 2 && attr.getLGType()  == FieldTypeS.Enum) {
                vals += "枚举字段:" + attr.getName() + "," + attr.getKeyOfEn() + ";";
                continue;
            }
        }
        String info = "表单名称为：" + md.getName() + "有如下字段" + vals + " 请检查表单字段，按照如下要求" + "1. 在数值类型的字段里寻找：一个字段值是通过其他字段值通过表达式计算得来. 比如: 合计=单价*数量" + "2. 在日期字段里寻找：日期字段不能输入历史日期. " + "3. 在日期字段里寻找：一个日字段大于等于另外一个日期字段." + "4. 可以在字段上增加的正则表达式. 比如：手机号，邮件，地址." + "5. 根据枚举库：把文本类型配备枚举，采用模糊匹配的模式." + "6. 根据字典库：把文本类型配备外键或者外部数据源，采用模糊匹配的模式." + "7. 在日期类型与数值类型的字段中寻找，数值=日期1-日期2 的规则. " + "8. 根据表单名称：在枚举库与字典库里寻找那些字段没有被补充上给与提示. ";


        String json = bp.wf.httphandler.WF_Admin_AI_Client.AIFrm_GenerFX(frmID, info);
        //string json = bp.wf.httphandler.WF_Admin_AI_Client.AIFrm_GenerFX(frmID, words, false, true);
        if (bp.da.DataType.IsNullOrEmpty(json) == true) {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }
        if (json.contains("err@") == true) {
            throw new RuntimeException("err@AI返回结果错误:CheckFrm_GenerFX:" + json);
        }

        JSONArray jsonArray = JSONArray.fromObject(json);
        if (jsonArray == null || jsonArray.size() == 0) {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }

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


        for (int i =0;i<jsonArray.size();i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String relType = jsonObject.get("RelType").toString(); //逻辑编号ID. BindFunction
            String fieldDesc = jsonObject.get("RelDesc").toString(); // 逻辑名称.
            String fieldKey = jsonObject.get("FieldKey").toString(); //英文名称.
            String fieldName = jsonObject.get("FieldName").toString(); //中文名称.
            String relFields = jsonObject.get("RelFields").toString(); //中文名称.

            //Object fieldArrayObj = jsonObject["RelFields"]; //关联的字段.
            //string[] fieldArrayStr = "".Split(",");
            //if (fieldArrayObj != null && fieldArrayObj is JArray)
            //{
            //    fieldArrayStr = ((JArray)fieldArrayObj).ToObject<string[]>();
            //}
            String fieldEx = jsonObject.get("Docs").toString(); //表达式.

            DataRow dr = dt.NewRow();

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
            ///#region  AutoFull . 运算表达式..
            if (relType.equals("AutoFull") == true) {
                dr.set("Name", fieldDesc); //"字段公式";
                dr.set("Note", "发现:" + fieldName + "," + fieldKey + "可以使用表达式:" + fieldEx); //"系统发现：{小计=单价等于金额} 的公式？";
                dr.set("AttrOfOper", fieldKey); // "XioJi"; //操作的字段.
                dr.set("Tag", fieldEx); // "@DanJia*@ShuLiang";
                dr.set("Tag1", fieldEx); // "单价*数量";
                dr.set("ExtModel", "AutoFull");
                dr.set("ExtType", "AutoFull");
                //主键格式: 操作字段+标记+表达式
                dr.set("No", dr.get("AttrOfOper") + "_AutoFull_" + dr.get("Tag").toString());
                dt.Rows.add(dr);
                continue;
            }
            ///#endregion 自动填充.

            ///#region  DateFieldInputRole1 . 不能输入历史日期.
            if (relType.equals("DateFieldInputRole1") == true) {
                dr = dt.NewRow();
                dr.set("Name", "不能输入历史日期.");
                dr.set("Note", "系统发现字段[" + fieldName + fieldKey + "],不能输入历史日期.");
                dr.set("AttrOfOper", fieldKey); //操作的字段.
                dr.set("Tag", fieldKey);
                dr.set("Tag1", fieldName);
                dr.set("ExtModel", "DateFieldInputRole");
                dr.set("ExtType", "DateFieldInputRole");
                dr.set("DoWay", "1");
                dr.set("No", DBAccess.GenerGUID());
                dr.set("MyPK", this.getFrmID() + "_" + dr.get("AttrOfOper").toString() + "_DateFieldInputRole");
                dt.Rows.add(dr);
                continue;
            }
            ///#endregion DateFieldInputRole.不能输入历史日期.

            ///#region  DateFieldInputRole2 . 大于等于指定的日期.
            if (relType.equals("DateFieldInputRole2") == true)
            {
                if (DataType.IsNullOrEmpty(relFields) == true)
                {
                    throw new RuntimeException("err@AI在求大于等于指定的日期，没有关联到:relFields");
                }

                if (relFields.contains("[") == true)
                {
                    throw new RuntimeException("err@AI在求大于等于指定的日期，关联的字段，不能是数组:[" + relFields + "],多个使用逗号分开.");
                }

                String attrPK = this.getFrmID() + "_" + relFields;
                MapAttr attr = new MapAttr();
                attr.setMyPK(attrPK);
                if (attr.RetrieveFromDBSources() == 0)
                {
                    continue;
                }

                dr = dt.NewRow();
                dr.set("Name", "大于等于指定的日期.");
                dr.set("Note", "AI发现日期字段[" + fieldName + fieldKey + "],需要增加大于等于日期字段:[" + relFields + "," + attr.getName() + "]的控制");
                dr.set("AttrOfOper", fieldKey); //操作的字段.
                dr.set("Tag", "GTE");
                dr.set("Tag1", relFields);
                dr.set("DoWay", "2");
                dr.set("ExtModel", "DateFieldInputRole");
                dr.set("ExtType", "DateFieldInputRole");
                dr.set("TagT","大于等于"); //大于等于.
                dr.set("Tag1T",attr.getName());
                dr.set("No", DBAccess.GenerGUID());
                dr.set("MyPK", this.getFrmID() + "_" + dr.get("AttrOfOper").toString() + "_DateFieldInputRole");
                dt.Rows.add(dr);
                continue;
            }
            ///#endregion DateFieldInputRole2.大于等于指定的日期.

            ///#region  BindFunction - RegularExpression. 正则表达式..
            if (relType.equals("BindFunction") == true)
            {
                dr = dt.NewRow();
                dr.set("Name", "正则表达式.");
                dr.set("Note", "系统发现字段[" + fieldName + fieldKey + "],需要增加正则表达式.");
                dr.set("AttrOfOper", fieldKey); //操作的字段.
                dr.set("Tag", "blur");
                dr.set("Tag1", "blur失去焦点");
                dr.set("Tag2", "提示信息:必须以数字开头，除数字外，可含有“-” ");
                dr.set("Doc", fieldEx);
                dr.set("ExtModel", "BindFunction");//绑定函数.
                dr.set("ExtType", "RegularExpression");//正则表达式.
                dr.set("No",DBAccess.GenerGUID());
                dr.set("DoWay", "2");
                dr.set("No", DBAccess.GenerGUID());
                dr.set("MyPK", dr.get("AttrOfOper") + "_BindFunction_" + dr.get("Tag").toString());
                dr.set("RefPKVal",this.getFrmID() + "_" + dr.get("AttrOfOper")); //大于等于.
                dr.set("AtPara","@EnName=TS.MapExt.RegularExpression");
                dt.Rows.add(dr);
                continue;
            }
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
            ///#endregion RegularExpression 增加正则表达式.
        }

        json = bp.tools.Json.ToJson(dt);
        //临时保存.
        WebUser.TempValSaveByKey("FX" + this.getFrmID(), json);
        return json;
    }
    /**
     保存选择的内容

     @return
     */
    public final String CheckFrm_GenerSave() throws Exception {
        String selectVals = this.GetRequestVal("Vals") + ",";

        //获得保存的全量集合.
        String json = WebUser.TempValGetByKey("FX" + this.getFrmID());
        DataTable fxDT = bp.tools.Json.ToDataTable(json);

        String frmID = this.getFrmID();
        for (DataRow dr : fxDT.Rows)
        {
            String id = dr.get("No").toString();
            if (selectVals.contains(id + ",") == false)
            {
                continue;
            }

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
            ///#region 设置初始值.
            MapExt ext = new MapExt();
            String mypk = dr.get("MyPK").toString();

            ext.SetValByKey("MyPK", dr.get("MyPK").toString());
            ext.SetValByKey("AttrOfOper", dr.get("AttrOfOper").toString());
            ext.SetValByKey("ExtModel", dr.get("ExtModel").toString());
            ext.SetValByKey("ExtType", dr.get("ExtType").toString());
            ext.SetValByKey("Doc", dr.get("Doc").toString());
            ext.SetValByKey("DoWay", dr.get("DoWay").toString());

            ext.SetValByKey("Tag", dr.get("Tag").toString());
            ext.SetValByKey("Tag1", dr.get("Tag1").toString());
            ext.SetValByKey("Tag2", dr.get("Tag2").toString());
            ext.SetValByKey("Tag3", dr.get("Tag3").toString());
            ext.SetValByKey("Tag4", dr.get("Tag4").toString());

            ext.SetValByKey("FK_MapData", this.getFrmID());
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
            ///#endregion 设置初始值.

            if (DataType.IsNullOrEmpty(mypk) == false)
            {
                ext.SetValByKey("MyPK", mypk);
                if (ext.getIsExits() == true)
                {
                    ext.Delete();
                }
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

    /**
     根据提示信息生成字段.

     @return
     */
    public final DataTable AiFrm_GenerAttrsExt(String frmID, String words) throws Exception {
        // 根据words 根据表单ID. 生成一个 具有 No,Name的 json.
        //假设生成如下字段.
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve("FK_MapData", frmID);

        String names = ",";
        for (MapAttr item : attrs.ToJavaList())
        {
            names += item.getName() + ",";
        }

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
        String json = WF_Admin_AI_Client.AIFrm_GenerAttrs_ByWords(frmID, words, names);
        if (bp.da.DataType.IsNullOrEmpty(json) == true)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }
        if (json.contains("err@") == true)
        {
            throw new RuntimeException("err@AI返回结果错误:" + json);
        }

        JSONArray jsonArray = null;
        try
        {
            jsonArray = JSONArray.fromObject(json);
        }
        catch (RuntimeException ex)
        {
            throw new RuntimeException("err@AI没有返回格式有误，请重试." + ex.getMessage());
        }

        if (jsonArray == null || jsonArray.size() == 0)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }

        return bp.tools.Json.ToDataTable(json);
    }
    public final String AiFrm_SaveAttrs() throws Exception {
        String frmID = this.GetRequestVal("FrmID"); //所有的字段.
        String selectedAttrs = this.GetRequestVal("SelectedAttrs");
        String frmType = this.GetRequestVal("AttrsFromType");
        if (DataType.IsNullOrEmpty(frmType) == true)
        {
            frmType = "FrmAttr";
        }
        String json = WebUser.TempValGetByKey(frmType + frmID);
        return AiFrm_SaveAttrsExt(frmID, selectedAttrs, json, frmType);
    }

    public final String AiFrm_SaveAttrsExt(String frmID, String selectedAttrs, String json, String frmType) throws Exception {
        String[] selectedAttrsArray = selectedAttrs.split("[,]", -1);
        if (DataType.IsNullOrEmpty(frmType) == true)
        {
            frmType = "FrmAttr";
        }

        DataTable dtAttrs = bp.tools.Json.ToDataTable(json);

        MapData md = new MapData(frmID);
        int idx = DBAccess.RunSQLReturnValInt("SELECT MAX(Idx) FROM Sys_MapAttr WHERE FK_MapData='" + frmID + "'");
        int groupID = DBAccess.RunSQLReturnValInt("SELECT  MAX(OID) as Num  FROM Sys_GroupField WHERE FrmID ='" + frmID + "' AND CtrlType=''", 0);
        for (DataRow dr : dtAttrs.Rows)
        {
            String no = dr.get(0).toString();
            if (!selectedAttrsArray.toString().contains(no))
            {
                continue;
            }

            String cnName = dr.get("Name").toString();
            String attrKey = dr.get("KeyOfEn").toString();

            String note = dr.get("Note").toString();
            idx++;
            if (attrKey.equals("name") == true || attrKey.endsWith("_id") == true)
            {
                continue;
            }

            int dataType = Integer.parseInt(dr.get("DataType").toString());
            int minLen = Integer.parseInt(dr.get("MinLen").toString());
            int maxLen = Integer.parseInt(dr.get("MaxLen").toString());
            if (dataType == 1)
            {
                if (maxLen == 0)
                {
                    maxLen = 10;
                }
            }
            else
            {
                maxLen = 20;
            }
            MapAttr attr = new MapAttr();
            String mypk = frmID + "_" + attrKey;
            attr.setMyPK(mypk);
            if (attr.RetrieveFromDBSources() == 1)
            {
                continue;
            }

            //检查attrKey 是否符合字段要求。
            attr.setKeyOfEn(attrKey);
            if (DataType.CheckIsFieldOrTableName(attr.getKeyOfEn()) == false)
            {
                continue;
            }

            attr.SetValByKey("FK_MapData", frmID);
            attr.setName(cnName);
            attr.setMyDataType(dataType);
            attr.setMinLen(minLen);
            attr.setMaxLen(maxLen); //最大长度.
            attr.setIdx(idx);
            attr.setGroupID(groupID);
            //判断是否是boolen?
            if (note.equals("Boolen") == true)
            {
                attr.setUIContralType(UIContralType.CheckBok);
                attr.setMyDataType(bp.da.DataType.AppBoolean);
                attr.Insert();
                continue;
            }
            if (attr.getName().contains("附件") == true)
            {
                attr.setUIContralType(UIContralType.AthShow);
                attr.SetPara("EnName", "TS.FrmUI.FrmAttachmentExt");
                attr.Insert();

                bp.sys.CCFormAPI.CreateOrSaveAthSingle(attr.getFrmID(), attr.getKeyOfEn(), attr.getName(), 0, 0);
                String pkvalAth = attr.getFrmID() + "_" + attr.getKeyOfEn();
                FrmAttachment ath = new FrmAttachment(pkvalAth);
                ath.setUploadType(AttachmentUploadType.Single); //字段附件.
                ath.DirectUpdate();
                DBAccess.RunSQL("DELETE FROM Sys_GroupField WHERE CtrlID='" + pkvalAth + "'");
                continue; //附件.
            }

            ///#region 对文本框的判断.
            if (maxLen > 255)
            {
                attr.setUIContralType(UIContralType.TB);
                attr.setUIIsLine(true);
                attr.setUIHeight(150);
                attr.SetValByKey("TextModel", 2);

                if (md.getTableCol() == 6)
                {
                    attr.SetValByKey("ColSpan", 6);
                }
                else
                {
                    attr.SetValByKey("ColSpan", 4);
                }

                attr.Insert();
                continue; //超大文本.
            }
            if (maxLen <= 255 && maxLen >= 60)
            {
                attr.setUIContralType(UIContralType.TB);
                attr.setUIIsLine(true);
                if (md.getTableCol() == 6)
                {
                    attr.SetValByKey("ColSpan", 5);
                }
                else
                {
                    attr.SetValByKey("ColSpan", 3);
                }
                attr.Insert();
                continue; //一行显示.
            }

            if (note.length() <= 2)
            {
                attr.setUIContralType(UIContralType.TB);
                attr.Insert(); // 普通字段.
                continue;
            }
            ///#endregion 对文本框的判断.

            //note: 执行枚举设置.
            attr.setLGType(FieldTypeS.Enum); //设置枚举类型.
            attr.setUIBindKey(attrKey);
            attr.setUIContralType(UIContralType.RadioBtn);
            attr.SetPara("RBShowModel", 3); //横向展示.
            //判断是否有此枚举值?
            if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single || bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
            {
                bp.sys.SysEnumMain main = new bp.sys.SysEnumMain();
                main.setNo(attr.getKeyOfEn());
                if (main.RetrieveFromDBSources() == 0)
                {
                    main.setNo (attrKey);
                    main.setName(cnName);

                    String mynote = "";
                    String[] eles = note.split("[|]", -1);
                    for (int i = 0; i < eles.length; i++)
                    {
                        mynote += "@" + i + "=" + eles[i];
                    }
                    main.setCfgVal(mynote);
                    main.Insert();
                    main.DoInitDtls(); //执行.
                    if (eles.length > 6)
                    {
                        attr.setUIContralType(UIContralType.DDL);
                    }
                    else
                    {
                        attr.setUIContralType(UIContralType.RadioBtn);
                    }
                }
            }
            if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
            {
                bp.sys.SysEnumMain main = new bp.sys.SysEnumMain();
                main.setNo(WebUser.getOrgNo() + "_" + attr.getKeyOfEn());
                if (main.RetrieveFromDBSources() == 0)
                {
                    main.setNo(attrKey);
                    main.setName(cnName);
                    String mynote = "";
                    String[] eles = note.split("[,]", -1);
                    for (int i = 0; i < eles.length; i++)
                    {
                        mynote += "@" + i + "=" + eles[i];
                    }
                    main.setCfgVal(mynote);
                    main.Insert();
                    main.DoInitDtls(); //执行.
                    if (eles.length > 10)
                    {
                        attr.setUIContralType(UIContralType.DDL);
                    }
                    else
                    {
                        attr.setUIContralType(UIContralType.RadioBtn);
                    }
                }
            }
            attr.Insert(); //执行插入.
        }
        return "保存成功，请刷新表单显示增加的字段.";
    }
    public final String AiFrm_IconGener() throws Exception {
        String frmID = this.getFrmID();
        String words = this.GetRequestVal("Words");

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 1. 生成 words.
        if (StringUtil.isEmpty(words))
        {
            MapAttrs attrs = new MapAttrs();
            attrs.Retrieve("FK_MapData", frmID);

            words = "文本字段:";
            for (MapAttr item : attrs.ToJavaList())
            {
                if (item.getMyDataType() == DataType.AppString)
                {
                    words += item.getName() + "," + item.getKeyOfEn() + ";";
                }
            }
            words += "\t\n:数值字段:";
            for (MapAttr item : attrs.ToJavaList())
            {
                if (item.getUIVisible() == false)
                {
                    continue;
                }
                if (item.getItIsNum() == true)
                {
                    words += item.getName() + "," + item.getKeyOfEn() + ";";
                }
            }
            words += "\t\n:枚举字段:";
            for (MapAttr item : attrs.ToJavaList())
            {
                if (item.getUIVisible() == false)
                {
                    continue;
                }
                if (item.getItIsNum() == true && item.getUIContralType() != UIContralType.TB)
                {
                    words += item.getName() + "," + item.getKeyOfEn() + ";";
                }
            }
            words = "表单有如下字段" + words;
        }
        ///#endregion 生成 words.

        //string json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(words, false, true);
        String json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(words);
        if (bp.da.DataType.IsNullOrEmpty(json) == true)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }
        if (json.contains("err@") == true)
        {
            throw new RuntimeException("err@AI返回结果错误:" + json);
        }

        JSONArray jsonArray = null;
        try
        {
            jsonArray = JSONArray.fromObject(json);
        }
        catch (RuntimeException ex)
        {
            throw new RuntimeException("err@AI没有返回格式有误，请重试." + ex.getMessage() + " JSON:" + json);
        }

        if (jsonArray == null || jsonArray.size() == 0)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }

        DataTable dt = new DataTable();
        dt.Columns.Add("No");
        dt.Columns.Add("KeyOfEn");
        dt.Columns.Add("Name");
        dt.Columns.Add("Icon");

        for (int i =0;i<jsonArray.size();i++)
        {
            JSONObject jsonObject= (JSONObject) jsonArray.get(i);
            String name = jsonObject.get("name").toString();
            String cnname = jsonObject.get("cnname").toString();
            String icon = jsonObject.get("icon").toString();

            DataRow dr = dt.NewRow();
            dr.set("No", name + "$" + icon); //"$icon-drop";
            dr.set("KeyOfEn", name);
            dr.set("Name", cnname);
            dr.set("Icon", icon);
            dt.Rows.add(dr);
        }
        return bp.tools.Json.ToJson(dt);
    }
    public final String AiFrm_IconSave()
    {
        String valsStr = this.GetRequestVal("Vals");
        String[] vals = valsStr.split("[,]", -1);
        for (String str : vals)
        {
            if (DataType.IsNullOrEmpty(str) == true)
            {
                continue;
            }

            String[] strs = str.split(java.util.regex.Pattern.quote("$"), -1);
            String attrKey = strs[0];
            String icon = strs[1];
            DBAccess.RunSQL("UPDATE Sys_MapAttr SET Icon='" + icon + "' WHERE MyPK='" + this.getFrmID() + "_" + attrKey + "'");
        }
        return "保存成功.";
    }

    public final DataTable AiFrm_MatchIcons() throws Exception {
        String frmID = this.getFrmID();
        String words = this.GetRequestVal("Words");

        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve("FK_MapData", frmID);

        String names = ",";
        for (MapAttr item : attrs.ToJavaList())
        {
            names += item.getName() + ",";
        }

        DataTable dt = new DataTable();
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("KeyOfEN");
        dt.Columns.Add("DataType");
        dt.Columns.Add("MinLen");
        dt.Columns.Add("MaxLen");
        dt.Columns.Add("Note");

        //string json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(frmID, words, names, false, true);
        String json = WF_Admin_AI_Client.AIFrm_MatchIcons_ByWords(words);
        if (bp.da.DataType.IsNullOrEmpty(json) == true)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }
        if (json.contains("err@") == true)
        {
            throw new RuntimeException("err@AI返回结果错误:" + json);
        }

        JSONArray jsonArray = null;
        try
        {
            jsonArray = JSONArray.fromObject(json);
        }
        catch (RuntimeException ex)
        {
            throw new RuntimeException("err@AI没有返回格式有误，请重试.");
        }

        if (jsonArray == null || jsonArray.size() == 0)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词.");
        }

        for (int i =0;i<jsonArray.size();i++)
        {
            JSONObject jsonObject= (JSONObject) jsonArray.get(i);
            String name = jsonObject.get("name").toString();
            String cnname = jsonObject.get("cnname").toString();
            Object lengthObj = jsonObject.get("length");
            if (lengthObj == null)
            {
                lengthObj = "0";
            }
            String lengthStr = lengthObj.toString();
            String[] lengthArray = {lengthStr};
            if (lengthStr.indexOf(',') >= 0)
            {
                lengthArray = lengthStr.split("[,]", -1);
            }

            String datatype = (String)jsonObject.get("datatype");
            String enumval = (String)jsonObject.get("enum");
            if (!StringUtil.isEmpty(enumval) && enumval.indexOf(",") > 0)
            {
                datatype = "ENUM";
            }

            String note = "无";
            int maxLen = 100;
            int datatypeInt = DataType.AppString;


            DataRow dr = dt.NewRow();
            String no = name + "*" + cnname + "*" + datatypeInt + "*0*" + maxLen + "*" + note;
            dr.set("No", no);
            dr.set("Name", cnname);
            dr.set("KeyOfEN", name);
            dr.set("DataType", datatypeInt);
            dr.set("MinLen", 0);
            dr.set("MaxLen", maxLen);
            dr.set("Note", note);
            dt.Rows.add(dr);
        }
        return dt;
    }
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#region AI表单. - 上传文件解析表单.

    /**
     上传文件-解析表单

     @return
     */
    public final String AiFrm_File()
    {
        String frmID = this.getFrmID();
        return null;
    }
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#endregion AI表单.

    //C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
    ///#region AI流程. - 流程.
    public final String AiFlow_NodesDeliveryWayGener() throws Exception {
        String flowNo = this.getFlowNo();
        Nodes nds = new Nodes();
        nds.Retrieve("FK_Flow", flowNo, "NodeID");


        DataTable dt = new DataTable();
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("接受人规则");
        dt.Columns.Add("相关属性");


        for (Node item : nds.ToJavaList())
        {
            DataRow dr = dt.NewRow();
            dr.set("No", item.getNodeID());
            dr.set("Name", item.getName());
            dr.set("接受人规则", item.getHisDeliveryWayText());
            dr.set("相关属性", "无");
            if (item.getHisDeliveryWay() == DeliveryWay.ByBindEmp)
            {
                NodeEmps nes = new NodeEmps(item.getNodeID());
                String str = "人员信息:";
                for (NodeEmp nd : nes.ToJavaList())
                {
                    str += nd.getEmpName() + ",";
                }
                dr.set("相关属性", str);
            }

            if (item.getHisDeliveryWay() == DeliveryWay.ByDept)
            {
                NodeDepts nes = new NodeDepts(item.getNodeID());
                String str = "部门信息：";
                for (NodeDept nd : nes.ToJavaList())
                {
                    str += nd.GetValRefTextByKey("FK_Dept") + ",";
                }
                dr.set("相关属性", str);
            }
            if (item.getHisDeliveryWay() == DeliveryWay.ByStation || item.getHisDeliveryWay() == DeliveryWay.ByStationOnly)
            {
                NodeStations nes = new NodeStations(item.getNodeID());
                String str = "岗位信息:";
                for (NodeStation nd : nes.ToJavaList())
                {
                    str += nd.getStationT() + ",";
                }
                dr.set("相关属性", str);
            }
            //dr["AI建议"] = "无";
            //dr["AI接受人规则"] = "无";
            //dr["AI相关属性"] = "无";
            dt.Rows.add(dr);
        }

        String words = "驰骋BPM是一个流程引擎，每个节点有绑定人员，绑定岗位，部门等接受人员权限设置。";
        //   words += " ccbpm 提供了如下接收人规则：  public enum DeliveryWay\r\n    {\r\n        /// <summary>\r\n        /// 按角色(以部门为纬度)\r\n        /// </summary>\r\n        ByStation = 0,\r\n        /// <summary>\r\n        /// 本部门内的人员\r\n        /// </summary>\r\n        FindSpecDeptEmpsInStationlist = 19,\r\n        /// <summary>\r\n        /// 按部门\r\n        /// </summary>\r\n        ByDept = 1,\r\n        /// <summary>\r\n        /// 按SQL\r\n        /// </summary>\r\n        BySQL = 2,\r\n        /// <summary>\r\n        /// 按本节点绑定的人员\r\n        /// </summary>\r\n        ByBindEmp = 3,\r\n        /// <summary>\r\n        /// 由上一步发送人选择\r\n        /// </summary>\r\n        BySelected = 4,\r\n        /// <summary>\r\n        /// 所有人员都可以发起\r\n        /// </summary>\r\n        BySelected_1 = 41,\r\n        /// <summary>\r\n        /// 固定范围的选择\r\n        /// </summary>\r\n        BySelected_2 = 60,\r\n        /// <summary>\r\n        /// 按表单选择人员\r\n        /// </summary>\r\n        ByPreviousNodeFormEmpsField = 5, //字段是主表.\r\n        ByPreviousNodeFormEmpsFrmDtl = 501, //字段是从表.\r\n        ByPreviousNodeFormEmpsTeam = 502, // 字段是权限组.\r\n        ByPreviousNodeFormDepts = 503, //字段是部门编号\r\n        /// <summary>\r\n        /// 按照角色计算\r\n        /// </summary>\r\n        ByPreviousNodeFormStationsAI = 53,\r\n        /// <summary>\r\n        /// 智能计算\r\n        /// </summary>\r\n        ByPreviousNodeFormStationsOnly = 54,\r\n        /// <summary>\r\n        /// 与上一节点的人员相同\r\n        /// </summary>\r\n        ByPreviousNodeEmp = 6,\r\n        /// <summary>\r\n        /// 与开始节点的人员相同\r\n        /// </summary>\r\n        ByStarter = 7,\r\n        /// <summary>\r\n        /// 与指定节点的人员相同\r\n        /// </summary>\r\n        BySpecNodeEmp = 8,\r\n        /// <summary>\r\n        /// 按角色与部门交集计算\r\n        /// </summary>\r\n        ByDeptAndStation = 9,\r\n        /// <summary>\r\n        /// 按角色计算(以部门集合为纬度)\r\n        /// </summary>\r\n        //ByStationAndEmpDept = 10,\r\n        /// <summary>\r\n        /// 按指定节点的人员或者指定字段作为人员的角色计算\r\n        /// </summary>\r\n        BySpecNodeEmpStation = 11,\r\n        /// <summary>\r\n        /// 按SQL确定子线程接受人与数据源.\r\n        /// </summary>\r\n        BySQLAsSubThreadEmpsAndData = 12,\r\n        /// <summary>\r\n        /// 按明细表确定子线程接受人.\r\n        /// </summary>\r\n        ByDtlAsSubThreadEmps = 13,\r\n        /// <summary>\r\n        /// 仅按角色计算\r\n        /// </summary>\r\n        ByStationOnly = 14,\r\n        /// <summary>\r\n        /// FEE计算.\r\n        /// </summary>\r\n        ByFEE = 15,\r\n        /// <summary>\r\n        /// 按绑定部门计算,该部门一人处理标识该工作结束(子线程).\r\n        /// </summary>\r\n        BySetDeptAsSubthread = 16,\r\n        /// <summary>\r\n        /// 按SQL模版计算\r\n        /// </summary>\r\n        BySQLTemplate = 17,\r\n        /// <summary>\r\n        /// 从人员到人员\r\n        /// </summary>\r\n        ByFromEmpToEmp = 18,\r\n        /// <summary>\r\n        /// 按照角色计算-范围内的\r\n        /// </summary>\r\n        ByStationForPrj = 20,\r\n        /// <summary>\r\n        /// 按照选择模式计算.\r\n        /// </summary>\r\n        BySelectedForPrj = 21,\r\n        /// <summary>\r\n        /// 按照设置的组织计算\r\n        /// </summary>\r\n        BySelectedOrgs = 22,\r\n        /// <summary>\r\n        /// 按照部门领导计算\r\n        /// </summary>\r\n        ByDeptLeader = 23,\r\n        /// <summary>\r\n        /// 按照部门分管领导计算\r\n        /// </summary>\r\n        ByDeptShipLeader = 28,\r\n        /// <summary>\r\n        /// 找自己的直属领导.\r\n        /// </summary>\r\n        ByEmpLeader = 50,\r\n        /// <summary>\r\n        /// 按照用户组计算(本组织范围内)\r\n        /// </summary>\r\n        ByTeamOrgOnly = 24,\r\n        /// <summary>\r\n        /// 按照用户组计算(全集团)\r\n        /// </summary>\r\n        ByTeamOnly = 25,\r\n        /// <summary>\r\n        /// 按照用户组计算(本部门范围内)\r\n        /// </summary>\r\n        ByTeamDeptOnly = 26,\r\n        /// <summary>\r\n        /// 按照绑定角色的用户组人员\r\n        /// </summary>\r\n        ByBindTeamEmp = 27,\r\n        /// <summary>\r\n        /// 按照组织模式人员选择器\r\n        /// </summary>\r\n        BySelectedEmpsOrgModel = 43,\r\n        /// <summary>\r\n        /// 按照自定义Url模式的人员选择器\r\n        /// </summary>\r\n        BySelfUrl = 44,\r\n        /// <summary>\r\n        /// 按照设置的WebAPI接口获取的数据计算\r\n        /// </summary>\r\n        ByAPIUrl = 45,\r\n        /// <summary>\r\n        /// 发送人的上级部门的负责人\r\n        /// 就是找上级领导主管.\r\n        /// </summary>\r\n        BySenderParentDeptLeader = 46,\r\n        /// <summary>\r\n        /// 发送人上级部门指定的角色\r\n        /// </summary>\r\n        BySenderParentDeptStations = 47,\r\n        /// <summary>\r\n        /// 外部用户\r\n        /// </summary>\r\n        ByGuest = 51,\r\n\r\n        /// <summary>\r\n        /// 选择其他组织的联络员\r\n        /// </summary>\r\n        BySelectEmpByOfficer = 55,\r\n        /// <summary>\r\n        /// 绑定字典表\r\n        /// </summary>\r\n        BySFTable = 52,\r\n        /// <summary>\r\n        /// 按指定的部门集合与设置的岗位交集\r\n        /// </summary>\r\n        ByStationSpecDepts = 56,\r\n        /// <summary>\r\n        /// 按指定的角色集合与设置的部门交集\r\n        /// </summary>\r\n        ByStationSpecStas = 57,\r\n        /// <summary>\r\n        /// 按照分管领导计算.\r\n        /// </summary>\r\n        ByDeptSpecer = 58,\r\n        /// <summary>\r\n        /// 连续多级主管 - 指定角色模式 模式\r\n        /// </summary>\r\n        ByMLeader0 = 701,\r\n        /// <summary>\r\n        /// 连续多级主管 - 指定指定通讯录等级 模式\r\n        /// </summary>\r\n        ByMLeader1 = 702,\r\n        /// <summary>\r\n        /// 按照ccflow的BPM模式处理\r\n        /// </summary>\r\n        ByCCFlowBPM = 100,\r\n        /// <summary>\r\n        /// 预置:自由选择.\r\n        /// </summary>\r\n        PreplaceWokerFree = 710,\r\n        /// <summary>\r\n        /// 预置:固定模式\r\n        /// </summary>\r\n        PreplaceWokerFix = 711,\r\n    } ";


        //    string words1= "如下是一个流程节点的json表达式，No=是节点ID, Name是节点名称, 接受人规则是接受人规则. ";

        String json = bp.tools.Json.ToJson(dt);
        WebUser.TempValSaveByKey("CheckFlowDeliveryWay" + flowNo, json);
        return bp.tools.Json.ToJson(dt);
    }
    /**
     投递规则保存.

     @return
     */
    public final String AiFlow_NodesDeliveryWaySave() throws Exception {
        String flowNo = this.GetRequestVal("FlowNo");
        DataTable dt = bp.tools.Json.ToDataTable(WebUser.TempValGetByKey("CheckFlowDeliveryWay" + flowNo));

        for (DataRow dr : dt.Rows)
        {

        }
        return "保存成功.";
    }
    public final String AiFlow_SaveFlow() throws Exception {
        String flowWords = this.GetRequestVal("FlowWords");
        String frmWords = this.GetRequestVal("FrmWords");

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 设置流程.
        //DataTable dt = bp.wf.httphandler.WF_Admin_AI_Client.AIFlow_GenerFlow_ByWords(flowWords, "", true, true);
        DataTable dt = bp.wf.httphandler.WF_Admin_AI_Client.AIFlow_GenerFlow_ByWords(flowWords, "");

        String flowName = "我的AI流程";
        if (dt.Rows.size() > 1)
        {
            flowName = dt.Rows.get(0).getValue(4).toString();
        }

        //创建流程.
        String sortNo = this.GetRequestVal("SortNo");
        String flowNo = bp.wf.template.TemplateGlo.NewFlowTemplate(sortNo, flowName, bp.wf.template.DataStoreModel.SpecTable, "", null);

        Flow fl = new Flow(flowNo);
        fl.setFlowDevModel(FlowDevModel.JiJian); //流程开发模式.
        fl.setFrmUrl("ND" + Integer.parseInt(flowNo + "01"));
        fl.Update();
        int nodeIDStart = Integer.parseInt(fl.getNo() + "01");
        int nodeID2 = Integer.parseInt(fl.getNo() + "02");
        Node currND = null;
        for (int i = 0; i < dt.Rows.size(); i++)
        {
            DataRow dr = dt.Rows.get(i);
            String nodeNo = dr.get(0).toString(); //编号序号.
            String name = dr.get(1).toString(); //节点名称.
            String stationName = dr.get(2).toString(); //岗位名称.
            String desc = dr.get(3).toString(); //备注.
            if (i == 0)
            {
                Node nd1 = new Node(nodeIDStart);
                nd1.setName(name);
                nd1.Update();
                continue;
            }
            if (i == 1)
            {
                Node nd2 = new Node(nodeID2);
                nd2.setName(name);
                nd2.Update();
                currND = nd2;
                continue;
            }

            //创建节点.
            Node nd = bp.wf.template.TemplateGlo.NewNode(fl.getNo(), currND.getX() + 100, currND.getY() + 100);
            nd.setName(name);
            nd.DirectUpdate();

            //设置连接线.
            Direction dir = new Direction();
            dir.setMyPK(fl.getNo() + "_" + currND.getNodeID() + "_" + nd.getNodeID());
            dir.setFlowNo(fl.getNo());
            dir.setNode( currND.getNodeID());
            dir.setToNode(nd.getNodeID());
            dir.Insert();
            //设置当前节点.
            currND = nd;
            //如果是最后一个节点.
            if (i == dt.Rows.size() - 1)
            {
                //与开始节点相同.
                nd.setHisDeliveryWay(DeliveryWay.ByStarter);
                nd.setFrmWorkCheckSta(FrmWorkCheckSta.Readonly);
                nd.Update();
            }
        }
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#endregion 设置流程.

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 设置表单.
        //设置表单.
        String frmID = "ND" + nodeIDStart;
        DataTable mydt = this.AiFrm_GenerAttrsExt(frmID, frmWords);
        String attrs = "";
        for (DataRow dr : mydt.Rows)
        {
            attrs += dr.get(0).toString() + ",";
        }

        this.AddPara("SelectedAttrs", attrs); //增加选择的值.
        this.AddPara("FrmID", frmID); //增加选择的值.

        //保存数据到临时变量.
        WebUser.TempValSaveByKey("FrmAttr" + this.getFrmID(), bp.tools.Json.ToJson(mydt));
        this.AiFrm_SaveAttrs(); //保存到表单.
//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#endregion 设置表单.
        return fl.getNo();
    }
    public final String AiFrm_TestDBGener() throws Exception {
        String frmID = this.getFrmID();
        GEEntity en = new GEEntity(frmID);
        en.CheckPhysicsTable();

        String sql = "";
        switch (DBAccess.getAppCenterDBType())
        {
            case Oracle:
            case KingBaseR3:
            case KingBaseR6:
            case DM:
            case GBASE8CByOracle:
                sql = SqlBuilder.GenerCreateTableSQLOfOra(en);
                break;
            case Informix:
                sql = SqlBuilder.GenerCreateTableSQLOfInfoMix(en);
                break;
            case PostgreSQL:
            case UX:
            case HGDB:
                sql = SqlBuilder.GenerCreateTableSQLOfPostgreSQL(en);
                break;
            case MSSQL:
                sql = SqlBuilder.GenerCreateTableSQLOfMS(en);
                break;
            case MySQL:
                sql = SqlBuilder.GenerCreateTableSQLOfMySQL(en);
                break;
            case Access:
                sql = SqlBuilder.GenerCreateTableSQLOf_OLE(en);
                break;
            default:
                throw new RuntimeException("@未判断的数据库类型。");
        }

        int dataTotal = 5;
        //创建表的SQL如下 @hwz
        String words = "创建表的SQL：[" + sql + "]请根据表结构: 生成" + dataTotal + "条插入数据，OID字段以 10开始递增. ";

        //String json = bp.wf.httphandler.WF_Admin_AI_Client.AiFrm_GenerTestDB_ByWords(words, true, true);
        String json = bp.wf.httphandler.WF_Admin_AI_Client.AiFrm_GenerTestDB_ByWords(words);

        if (StringUtils.isEmpty(json))
        {
            json = "";
        }

        if (json.contains("err@") == true && json.length() <= 100)
        {
            return json;
        }

        JSONArray jsonArray = JSONArray.fromObject(json);
        if (jsonArray == null || jsonArray.size() == 0)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词..");
        }

        DataTable dt = new DataTable();
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("TestSQL");

        int i = 1;
        for (int j =0;j<jsonArray.size();j++)
        {
            JSONObject jsonObject = (JSONObject) jsonArray.get(j);
//ORIGINAL LINE: string item = (string?)jsonObject.First;
            String item = (String)jsonObject.get("No"); //逻辑编号ID. BindFunction
            if ((item == null || item.isEmpty()))
            {
                continue;
            }

            DataRow dr = dt.NewRow();
            dr.set("No", String.valueOf(i));
            dr.set("Name", "第" + i + "条");
            dr.set("TestSQL", item); // "INSERT INTO XXXX " + i;
            dt.Rows.add(dr);
            i++;
        }
        String str = bp.tools.Json.ToJson(dt);
        bp.web.WebUser.TempValSaveByKey("TestDB" + this.getFrmID(), str);
        return str;
    }


    public final String AiFrm_TestDBSave() throws Exception {
        String json = bp.web.WebUser.TempValGetByKey("TestDB" + this.getFrmID());
        DataTable dt = bp.tools.Json.ToDataTable(json);
        for (DataRow dr : dt.Rows)
        {
            String sql = dr.get("TestSQL").toString();
            if (!StringUtils.isEmpty(sql))
            {
                String[] wavearray = sql.split("[~]", -1);
                if (wavearray.length > 10)
                {
                    sql = sql.replace("~", "'");
                }
            }
            DBAccess.RunSQL(sql);
        }
        return "保存成功.";
    }

    /**
     保存字段.

     @return
     */
    public final String AiFrm_SaveNodes() throws Exception {
        String frmID = this.GetRequestVal("FlowNo"); //所有的字段.
        String selectedAttrs = this.GetRequestVal("SelectedNodes");
        String[] strs = selectedAttrs.split("[,]", -1);
        for (String str : strs)
        {
            if (bp.da.DataType.IsNullOrEmpty(str) == true)
            {
                continue;
            }
            String[] array = str.split("[*]", -1);
            if (array == null || array.length > 6)
            {
                continue;
            }

            String attrKey = array[0];
            String cnName = array[1];
            int datatype = Integer.parseInt(array[2]);
            int minLen = Integer.parseInt(array[3]);
            int maxLen = Integer.parseInt(array[4]);
            String note = array[5]; //备注.

            MapAttr attr = new MapAttr();
            String mypk = frmID + "_" + attrKey;
            attr.setMyPK(mypk);
            if (attr.RetrieveFromDBSources() == 1)
            {
                continue;
            }

            attr.SetValByKey("FK_MapData", frmID);
            attr.setKeyOfEn(attrKey);
            attr.setName(cnName);
            attr.setMyDataType(datatype);
            attr.setMinLen(minLen);
            attr.setMaxLen(maxLen); //最大长度.
            if (note.length() <= 3)
            {
                attr.setUIContralType(UIContralType.TB);
                attr.Insert();
                continue;
            }

            try
            {
                String x = this.GetRequestVal("X");
                String y = this.GetRequestVal("Y");
                String icon = this.GetRequestVal("icon");
                int nodeModel = this.GetRequestValInt("NodeModel");

                int iX = 20;
                int iY = 20;

                if (bp.da.DataType.IsNullOrEmpty(x) == false)
                {
                    iX = (int)Double.parseDouble(x);
                }

                if (bp.da.DataType.IsNullOrEmpty(y) == false)
                {
                    iY = (int)Double.parseDouble(y);
                }

                Node node = bp.wf.template.TemplateGlo.NewNode(this.getFlowNo(), iX, iY, icon, nodeModel);


                Hashtable ht = new Hashtable();
                ht.put("NodeID", node.getNodeID());
                ht.put("Name", node.getName());
                ht.put("RunModel", node.getHisRunModel());
                ht.put("NodeType", 0); //用户节点。

                return bp.tools.Json.ToJsonEntityModel(ht);
            }
            catch (RuntimeException ex)
            {
                return "err@" + ex.getMessage();
            }


        }
        return "保存成功，请刷新表单显示增加的字段.";
    }

    ///#endregion AI流程.
    ///#region AI 应用.
    /**
     获得菜单.
     @return
     */
    public final String App_Menus() throws Exception {
        String words = this.GetRequestVal("Words");
        //string json = bp.wf.httphandler.WF_Admin_AI_Client.App_ByWords(words, true, true);
        String json = bp.wf.httphandler.WF_Admin_AI_Client.App_ByWords(words);
        if (json.contains("err@") == true && json.length() <= 100)
        {
            return json;
        }

        //JSONObject jsonArray = JSONObject.fromObject(json);
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        if (jsonArray == null)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词..");
        }

        DataTable baseInfo = null; //基础信息表.
        DataTable modules = null; //模块
        DataTable menus = null; //菜单.
        System.out.println(jsonArray.toString());
        for (JsonElement element : jsonArray)
        {
            JsonObject obj = element.getAsJsonObject();
            String name =  obj.get("Name").getAsString();
            if (name.toUpperCase().equals("BASEINFO"))
            {
                String jsonStr = obj.getAsString();
                baseInfo = bp.tools.Json.ToDataTable(jsonStr);
            }

            if (name.toUpperCase().equals("MODULES"))
            {
                String jsonStr = obj.getAsString();
                modules = bp.tools.Json.ToDataTable(jsonStr);
            }

            if (name.toUpperCase().equals("MENUS"))
            {
                String jsonStr = obj.getAsString();
                menus = bp.tools.Json.ToDataTable(jsonStr);
            }
        }
        if (baseInfo == null)
        {
            throw new RuntimeException("err@AI没有返回表单结果，请换一个提示词:" + json);
        }

        DataTable dt = new DataTable();
        dt.Columns.Add("No");
        dt.Columns.Add("Name");
        dt.Columns.Add("Note"); //模块编号.
        dt.Columns.Add("MenuType"); //类型.
        dt.Columns.Add("ModuleNo"); //模块编号.
        dt.Columns.Add("ModuleName"); //模块名称.
        dt.Columns.Add("SystemName"); //模块名称.

        String systemName = baseInfo.Rows.get(0).getValue(1).toString();

        //根目录.
        DataRow dr = dt.NewRow();
        //dr["No"] = "100";
        //dr["Name"] = systemName;
        //dr["ParentNo"] = "0";
        //dt.Rows.Add(dr); //增加根目录.

        for (DataRow dr1Model : modules.Rows)
        {
            String moduleNo = dr1Model.get(0).toString();
            String moduleName = dr1Model.get(1).toString();

            DataRow mydr = dt.NewRow();
            //格式:  No$Name$SystemName
            mydr.set("No", moduleNo + "$" + moduleName + "$" + systemName);
            mydr.set("Name", "(+)" + moduleName);
            mydr.set("Note", "模块");
            dt.Rows.add(mydr);

            for (DataRow drMenu : menus.Rows)
            {
                String myModuleNo = drMenu.get("ModuleNo").toString();
                if (myModuleNo.equals(moduleNo) == false)
                {
                    continue;
                }

                String menuNo = drMenu.get("MenuNo").toString();
                String menuName = drMenu.get("MenuName").toString();
                String menuType = drMenu.get("MenuType").toString();

                DataRow mydr2 = dt.NewRow();
                //格式: No*+Name*Type*SortNo
                mydr2.set("No", menuNo + "*" + menuName + "*" + menuType + "*" + moduleNo);
                mydr2.set("MenuType", menuType); //菜单类型.
                if (Objects.equals(menuType, "Dict"))
                {
                    menuType = "实体";
                }
                if (Objects.equals(menuType, "Bill"))
                {
                    menuType = "单据";
                }
                if (Objects.equals(menuType, "Rpt"))
                {
                    menuType = "报表";
                }
                mydr2.set("Note", "菜单"); //菜单类型.
                mydr2.set("Name", "----" + menuName); //类型名称.
                mydr2.set("ModuleNo", mydr.get("No")); //模块名称.
                mydr2.set("ModuleName", mydr.get("Name"));
                dt.Rows.add(mydr2);
            }
        }
        //临时保存掉.
        json = bp.tools.Json.ToJson(dt);
        WebUser.TempValSaveByKey("App" + WebUser.getToken(), json);
        return json;
    }
//====================================================================================================
//The Free Edition of C# to Java Converter limits conversion output to 100 lines per snippet.

//To purchase the Premium Edition, visit our website:
//https://www.tangiblesoftwaresolutions.com/order/order-csharp-to-java.html
//====================================================================================================

    /**
     菜单保存.

     @return
     @exception Exception
     */
    public final String App_MenuSave() throws Exception {
        String json = WebUser.TempValGetByKey("App" + WebUser.getToken());
        DataTable dt = bp.tools.Json.ToDataTable(json);

//C# TO JAVA CONVERTER TASK: There is no preprocessor in Java:
        ///#region 组织数据.
        DataTable modules = new DataTable(); //模块
        modules.Columns.Add("ModuleNo");
        modules.Columns.Add("ModuleName");

        DataTable menus = new DataTable(); //菜单.
        menus.Columns.Add("MenuNo");
        menus.Columns.Add("MenuName");
        menus.Columns.Add("MenuType");
        menus.Columns.Add("ModuleNo");

        //获得选择的数据.
        String nodes = this.GetRequestVal("Nodes");
        String[] strs = nodes.split("[,]", -1);
        String systemName = "";

        for (String str : strs)
        {
            if (DataType.IsNullOrEmpty(str))
            {
                continue;
            }
            //系统.
            if (str.contains("$") == true)
            {
                String[] vals = str.split(java.util.regex.Pattern.quote("$"), -1);
                DataRow dr = modules.NewRow();
                dr.set("ModuleNo", vals[0]);
                dr.set("ModuleName", vals[1]);
                systemName = vals[2];
                modules.Rows.add(dr);
            }

            //菜单.
            if (str.contains("*") == true)
            {
                String[] vals = str.split(java.util.regex.Pattern.quote("*"), -1);
                DataRow dr = menus.NewRow();
                dr.set("MenuNo", vals[0]);
                dr.set("MenuName", vals[1]);
                dr.set("MenuType", vals[2]);
                dr.set("ModuleNo", vals[3]);
                menus.Rows.add(dr);
            }
        }
        //系统名称.
        String sql = "SELECT count(*) as Num FROM GPM_System WHERE Name='" + systemName + "'";
        if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
        {
            sql = "SELECT count(*) as Num FROM GPM_System WHERE Name='" + systemName + "' AND OrgNo='" + WebUser.getOrgNo() + "'";
        }
        if (DBAccess.RunSQLReturnValInt(sql, 0) >= 1)
        {
            systemName = systemName + bp.da.DataType.getCurrentDateTime();
        }

        int idx = 0;
        sql = "SELECT count(*) as Num FROM GPM_System ";
        if (bp.difference.SystemConfig.getCCBPMRunModel()  == CCBPMRunModel.SAAS)
        {
            sql = "SELECT count(*) as Num FROM GPM_System WHERE  OrgNo='" + WebUser.getOrgNo() + "'";
        }
        idx = DBAccess.RunSQLReturnValInt(sql);
        ///#endregion 组织数据.

        bp.ccfast.ccmenu.MySystem enSystem = new bp.ccfast.ccmenu.MySystem();
        enSystem.setName(systemName);
        enSystem.SetValByKey("Icon", "icon-directions"); //icon.
        enSystem.SetValByKey("Idx", "-" + idx); //放到第一个.
        enSystem.Insert();
        String systemNo = enSystem.getNo(); //系统编号.
        //遍历模块.
        idx = 0;
        for (DataRow dr : modules.Rows)
        {
            //创建模块.
            String sortNo = dr.get(0).toString();
            String sortName = dr.get(1).toString();
            bp.ccfast.ccmenu.Module enModule = new bp.ccfast.ccmenu.Module();
            enModule.setName(sortName);
            enModule.SetValByKey("Icon", "icon-layers");
            enModule.SetValByKey("SystemNo", enSystem.getNo());
            enModule.SetValByKey("Idx", idx);
            idx++;
            enModule.Insert();
            //创建菜单
            for (DataRow drMenu : menus.Rows)
            {
                idx++;

                String moduleNo = drMenu.get("ModuleNo").toString();
                if (sortNo.equals(moduleNo) == false)
                {
                    continue;
                }
                String name = drMenu.get("MenuName").toString(); //菜单名字.
                String no = drMenu.get("MenuNo").toString(); //菜单编号
                String menuType = drMenu.get("MenuType").toString(); //菜单编号

                ///#region 创建单据
                //如果是字典.
                if (menuType.equals("Bill"))
                {
                    //编号.
                    String pinyinJX = bp.da.DataType.ParseStringToPinyinJianXie(name);
                    String dictNo = "Bill_" + pinyinJX;
                    MapData md = new MapData();
                    md.setNo(dictNo);
                    int xh = 1;
                    while (md.RetrieveFromDBSources() == 1)
                    {
                        md.setNo(dictNo + xh);
                        xh++;
                    }
                    if (xh != 1)
                    {
                        dictNo = dictNo + xh;
                    }

                    bp.wf.httphandler.WF_Admin_CCFormDesigner handler = new bp.wf.httphandler.WF_Admin_CCFormDesigner();
                    handler.AddPara("TB_No", dictNo);
                    handler.AddPara("TB_Name", name);
                    handler.AddPara("TB_PTable", dictNo);
                    //   handler.AddPara("FK_FrmSort", enSystem.getNo());
                    handler.AddPara("EntityType", "1"); //创建单据.
                    handler.NewFrmGuide_Create();

                    bp.ccfast.ccmenu.Menu enMenu = new bp.ccfast.ccmenu.Menu();
                    enMenu.SetValByKey("Name", name);
                    enMenu.SetValByKey("UrlExt", dictNo + "?displayMode=table&FrmID=" + dictNo);
                    enMenu.SetValByKey("FrmID", dictNo);
                    enMenu.SetValByKey("ListModel", 0);
                    enMenu.SetValByKey("WorkType", 0);
                    enMenu.SetPara("EnName", "TS.CCBill.FrmBill"); //符合readme.md里面的第3个模式,编辑属性.
                    enMenu.SetPara("EnPKVal", dictNo); //设置主键.

                    enMenu.SetValByKey("MenuModel", menuType);
                    enMenu.SetValByKey("Icon", "icon-user");
                    enMenu.SetValByKey("ModuleNo", enModule.getNo());
                    enMenu.SetValByKey("ModuleNoT", enModule.getName());
                    enMenu.SetValByKey("SystemNo", enSystem.getNo());
                    enMenu.SetValByKey("Icon", "icon-user");
                    enMenu.SetValByKey("Idx", idx);
                    idx++;
                    enMenu.Insert();
                }
                  // #endregion 创建.
                ///#region 创建实体
                //如果是实体
                if (menuType.equals("Dict") == true)
                {
                    //.实体编号
                    String pinyinJX = bp.da.DataType.ParseStringToPinyinJianXie(name);
                    String dictNo = "Dict_" + pinyinJX;
                    MapData md = new MapData();
                    md.setNo(dictNo);
                    int xh = 1;
                    while (true)
                    {
                        if (md.RetrieveFromDBSources() == 0)
                        {
                            break;
                        }
                        md.setNo(dictNo + xh);
                        xh++;
                    }

                    bp.wf.httphandler.WF_Admin_CCFormDesigner handler = new bp.wf.httphandler.WF_Admin_CCFormDesigner();
                    handler.AddPara("TB_No", md.getNo());
                    handler.AddPara("TB_Name", name);
                    handler.AddPara("TB_PTable", md.getNo());
                    //    handler.AddPara("FK_FrmSort", enSystem.getNo());
                    handler.AddPara("EntityType", "2");
                    handler.NewFrmGuide_Create();

                    bp.ccfast.ccmenu.Menu enMenu = new bp.ccfast.ccmenu.Menu();
                    enMenu.SetValByKey("Name", name);
                    enMenu.SetValByKey("UrlExt", md.getNo() + "?displayMode=table&FrmID=" + md.getNo());
                    enMenu.SetValByKey("FrmID", md.getNo());
                    enMenu.SetValByKey("MenuModel", "Dict");
                    enMenu.SetValByKey("ListModel", 0);
                    enMenu.SetValByKey("WorkType", 0);
                    enMenu.SetPara("EnName", "TS.CCBill.FrmDict"); //符合readme.md里面的第3个模式,编辑属性.
                    enMenu.SetPara("EnPKVal", md.getNo()); //设置主键.
                    enMenu.SetValByKey("ListModel", 0);
                    enMenu.SetValByKey("UrlPath", "/src/CCFast/CCBill/SearchDict.vue");
                    enMenu.SetValByKey("MenuModel", "Dict");
                    enMenu.SetValByKey("Icon", "icon-user");
                    enMenu.SetValByKey("ModuleNo", enModule.getNo());
                    enMenu.SetValByKey("ModuleNoT", enModule.getName());
                    enMenu.SetValByKey("SystemNo", enSystem.getNo());
                    enMenu.SetValByKey("Icon", "icon-user");
                    enMenu.SetValByKey("Idx", idx);
                    idx++;
                    enMenu.Insert();
                }
                ///#endregion 创建字典.

                ///#region 创建查询-分析.
                //如果是字典.
                if (menuType.equals("Rpt") == true)
                {
                    bp.ccfast.ccmenu.Menu enMenu = new bp.ccfast.ccmenu.Menu();
                    enMenu.setNo(DBAccess.GenerGUID());
                    enMenu.setName(name);
                    enMenu.SetValByKey("MenuModel", "RptWhite");
                    enMenu.SetValByKey("IsEnable", "1");
                    enMenu.SetValByKey("Icon", "icon-screen-desktop");
                    enMenu.SetPara("EnName", "TS.CCFast.Rpt3D"); //符合readme.md里面的第2个模式.

                    enMenu.SetValByKey("UrlPath", "/src/CCFast/Views/RptWhiteMain.vue");
                    String urlExt = "/RptWhite" + enMenu.getNo() + "?PageID=" + enMenu.getNo();
                    enMenu.SetValByKey("UrlExt", urlExt);

                    enMenu.SetValByKey("ModuleNo", enModule.getNo());
                    enMenu.SetValByKey("ModuleNoT", enModule.getName());
                    enMenu.Insert();
                }
                ///#endregion 创建查询-分析.
            }
        }
        return systemNo;
    }
    ///#endregion AI 应用.

    ///#region AI 大屏.
    /**
     生成windows

     @return
     */
    public final String WhiteRpt_GenerWindows() throws Exception {
        MapData md = new MapData(this.getFrmID());
        //获得字段.
        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve("FK_MapData", md.getNo());

        String words = "实体表:" + md.getPTable() + ",实体名称：" + md.getName() + ",有如下字段:";
        for (MapAttr item : attrs.ToJavaList())
        {
            if (item.getUIVisible() == false)
            {
                continue;
            }
            if (item.getUIContralType() == UIContralType.TB && item.getMyDataType() == DataType.AppString)
            {
                continue;
            }

            if (item.getLGType() == FieldTypeS.Enum)
            {
                SysEnumMain enumMain = new SysEnumMain(item.getUIBindKey());
                words += "枚举字段,字段中文名:" + item.getName() + ",英文名:" + item.getKeyOfEn() + ",枚举项" + enumMain.getCfgVal() + ";";
                continue;
            }

            if (item.getItIsNum() == true)
            {
                words += "数值类型,字段中文名:" + item.getName() + ",英文名:" + item.getKeyOfEn() + ";";
            }
        }

        words += "当前数据库是 " + bp.difference.SystemConfig.getAppCenterDBType().toString() + " 数据库.";
        words += "请根据以上表结构描述，结合常规数据分析的经验，给出饼图，柱状图，折线图，雷达图等分析的查询语句。";


        //@hwz 补充这里代码.
        //String json = bp.wf.httphandler.WF_Admin_AI_Client.AiFrm_GenerWindows_ByWords(words, false, true);
        String json = bp.wf.httphandler.WF_Admin_AI_Client.AiFrm_GenerWindows_ByWords(words);

        if (StringUtils.isEmpty(json))
        {
            json = "";
        }

        DataTable dt = new DataTable();
        if (StringUtils.isEmpty(json) || Objects.equals(json, "[]"))
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
            dt = bp.tools.Json.ToDataTable(json);
        }


        String strs = bp.tools.Json.ToJson(dt);
        WebUser.TempValSaveByKey("Rpt" + this.getPageID(), strs);
        return strs;
    }

    public final String WhiteRpt_SaveWindows() throws Exception {
        String vals = WebUser.TempValGetByKey("Rpt" + this.getPageID());
        DataTable dataTable = bp.tools.Json.ToDataTable(vals);

        MapAttrs attrs = new MapAttrs();
        attrs.Retrieve("FK_MapData", this.getFrmID());

        String ids = this.getVals();
        for (DataRow dr : dataTable.Rows)
        {
            String no = dr.get(0).toString();
            if (ids.contains(no) == false)
            {
                continue;
            }
            String name = dr.get(1).toString();
            String chartType = dr.get(3).toString();
            String SQLExp = dr.get(4).toString();
            String En0Exp = "";
            if (DataType.IsNullOrEmpty(En0Exp) == true)
            {
                for (MapAttr attr : attrs.ToJavaList())
                {
                    if (attr.getLGType() != FieldTypeS.Enum)
                    {
                        continue;
                    }
                    if (SQLExp.contains(attr.getUIBindKey()) == true)
                    {
                        En0Exp = "SELECT IntKey as No, Lab as Name FROM Sys_Enum WHERE EnumKey='" + attr.getUIBindKey() + "'";
                        break;
                    }
                }
            }


            if (chartType.equals("ChartLine") == true)
            {
                bp.ccfast.portal.windowext.ChartLine en = new bp.ccfast.portal.windowext.ChartLine();
                en.setNo(no);
                if (en.RetrieveFromDBSources() == 1)
                {
                    continue;
                }

                en.setName(name);
                en.SetValByKey("IsLine", 1);
                en.SetValByKey("ColSpan", 2);
                en.SetValByKey("Docs", SQLExp);
                en.SetValByKey("C0Ens", En0Exp);

                en.SetValByKey("PageID", this.getPageID());
                en.SetValByKey("WinDocModel", chartType);
                en.SetPara("EnName", "TS.CCFast.Windows.ChartLine");

                en.Save();
            }
            if (chartType.equals("ChartPie") == true)
            {
                bp.ccfast.portal.windowext.ChartPie en = new bp.ccfast.portal.windowext.ChartPie();
                en.setNo(no);
                en.setName(name);
                en.SetValByKey("IsPie", 1);
                en.SetValByKey("ColSpan", 2);
                en.SetValByKey("Docs", SQLExp);
                en.SetValByKey("C0Ens", En0Exp);

                en.SetValByKey("PageID", this.getPageID());
                en.SetValByKey("WinDocModel", chartType);
                en.SetPara("EnName", "TS.CCFast.Windows." + chartType);
                en.Save();
            }

            if (chartType.equals("ChartZZT") == true)
            {
                bp.ccfast.portal.windowext.ChartZZT en = new bp.ccfast.portal.windowext.ChartZZT();
                en.setNo(no);
                en.setName(name);
                en.SetValByKey("IsZZT", 1);
                en.SetValByKey("ColSpan", 2);
                en.SetValByKey("Docs", SQLExp);
                en.SetValByKey("C0Ens", En0Exp);

                en.SetValByKey("PageID", this.getPageID());
                en.SetValByKey("WinDocModel", chartType);
                en.SetPara("EnName", "TS.CCFast.Windows." + chartType);
                en.Save();
            }
            if (chartType.equals("Table") == true)
            {
                bp.ccfast.portal.windowext.Table en = new  bp.ccfast.portal.windowext.Table();
                en.setNo(no);
                en.setName(name);
                en.SetValByKey("ColSpan", 2);
                en.SetValByKey("Docs", SQLExp);
                en.SetValByKey("C0Ens", En0Exp);
                en.SetValByKey("PageID", this.getPageID());
                en.SetValByKey("WinDocModel", chartType);
                en.SetPara("EnName", "TS.CCFast.Windows." + chartType);
                en.Save();
            }
        }

        return "保存成功.";
    }
        ///#endregion AI 大屏.
    public static void CreateFrm(String frmID, String frmName)
    {
        return;
    }

   private DataRow configRows(DataTable dt, String frmID, String name, String cnname, String datatype, Object lenObj) throws Exception {
        int minLen = 0;
        int maxLen = 10;

        int datatypeInt = DataType.AppString;
        switch (datatype.toUpperCase()) {
            case "VARCHAR":
            case "TEXT":
                datatypeInt = DataType.AppString;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "DATE":
                datatypeInt = DataType.AppDate;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "DATETIME":
                datatypeInt = DataType.AppDateTime;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "INT":
                datatypeInt = DataType.AppInt;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "DECIMAL":
            case "FLOAT":
                datatypeInt = DataType.AppFloat;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    String[] array = lenStr.split(",");
                    if (array != null && array.length > 1) {
                        maxLen = Integer.parseInt(array[0]);
                        minLen = Integer.parseInt(array[1]);
                    } else {
                        try {
                            maxLen = Integer.parseInt(lenStr);
                        } catch (Exception e) {
                        }
                    }
                }
                break;
            default:
        }
        DataRow row = saveRow(dt, frmID, name, cnname, datatypeInt, minLen, maxLen);
        return row;
    }

    private DataRow saveRow(DataTable dt, String frmID, String name, String cnname, int datatype, int minLen, int maxLen) throws Exception {
        DataRow dr = dt.NewRow();
        dr.setValue("No", buildAttr (name, cnname, datatype, minLen, maxLen));
//                name + "*" + cnname + "*" + datatype + "*" + minLen + "*" + maxLen);
        dr.setValue("Name", cnname);
        dr.setValue("KeyOfEN", name);
        dr.setValue("DataType", datatype);
        dr.setValue("minLen", minLen);
        dr.setValue("maxLen", maxLen);

        return dr;
    }

    private MapAttr configAttrs(String frmID, String name, String cnname, String datatype, Object lenObj) throws Exception {
        int minLen = 0;
        int maxLen = 10;

        int datatypeInt = DataType.AppString;
        switch (datatype.toUpperCase()) {
            case "VARCHAR":
            case "TEXT":
                datatypeInt = DataType.AppString;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "DATE":
                datatypeInt = DataType.AppDate;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "DATETIME":
                datatypeInt = DataType.AppDateTime;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "INT":
                datatypeInt = DataType.AppInt;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    try {
                        maxLen = Integer.parseInt(lenStr);
                    } catch (Exception e) {
                    }
                }
                break;
            case "DECIMAL":
            case "FLOAT":
                datatypeInt = DataType.AppFloat;
                if (lenObj != null && JSONNull.getInstance() != lenObj) {
                    String lenStr = "" + lenObj;
                    String[] array = lenStr.split(",");
                    if (array != null && array.length > 1) {
                        maxLen = Integer.parseInt(array[0]);
                        minLen = Integer.parseInt(array[1]);
                    } else {
                        try {
                            maxLen = Integer.parseInt(lenStr);
                        } catch (Exception e) {
                        }
                    }
                }
                break;
            default:
        }
        MapAttr attr = saveAttrs(frmID, name, cnname, datatypeInt, minLen, maxLen, false);
        return attr;
    }

    private MapAttr saveAttrs(String frmID, String name, String cnname, int datatype, int minLen, int maxLen, boolean toSave) throws Exception {
        MapAttr attr = new MapAttr();

        String mypk = frmID + "_" + name;
        attr.setMyPK(mypk);
        if (attr.RetrieveFromDBSources() == 1)
            return null;

        attr.SetValByKey("FK_MapData", frmID);
        attr.setKeyOfEn(name);
        attr.setName(cnname);
        attr.setMyDataType(datatype);
//        attr.setMyDataType(DataType.AppString);
        attr.setMinLen(minLen);
        attr.setMaxLen(maxLen); //最大长度.

        if (toSave) {
            attr.Insert();
        }
        return attr;
    }

    private JSONObject findAttr(JSONArray array, String findKey, String findValue) {
        AtomicReference<JSONObject> result = new AtomicReference<>(new JSONObject());
        array.forEach(obj -> {
            if (findValue.equals(((JSONObject) obj).getString(findKey))) {
                result.set((JSONObject) obj);
            }
        });
        return result.get();
    }

    private String buildAttr(String name, String cnname, int datatype, int minLen, int maxLen) {
        String splitChar = "*";
        return name + splitChar + cnname + splitChar+ datatype + splitChar + minLen + splitChar + maxLen;
    }

 /*   /// <summary>
    /// 保存字段.
    /// </summary>
    /// <returns></returns>
    public String AiFrm_SaveAttrs() throws Exception {
        String frmID = this.GetRequestVal("FrmID"); //所有的字段.
//        String attrsJson = this.GetRequestVal("Attrs"); //所有的字段.
        String selectedAttrs = this.GetRequestVal("SelectedAttrs");

        Arrays.asList(selectedAttrs.split(",")).forEach(attr -> {
            String noStr = attr;  // xinming*姓名*varchar(100)*100
            String[] array = noStr.split("\\*");
            if (array != null && array.length >= 5) {
                String name = array[0];
                String cnname = array[1];
                int datatype = Integer.parseInt(array[2]);
                int minLen = Integer.parseInt(array[3]);
                int maxLen = Integer.parseInt(array[4]);

                try {
                    saveAttrs(frmID, name, cnname, datatype, minLen, maxLen, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return "保存成功，请刷新表单显示增加的字段.";
    }*/

    //for debug
    private String readFromJsonFile(String filePath) {
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
            return new String(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray converToArrayFromZhipuAI(String jsonStr) {
        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        if (jsonObj.size() > 0) {
            Log.DebugWriteInfo(String.format("json size: %d", jsonObj.size()));
        }

        JSONArray jsonList = new JSONArray();

        Iterator<Object> it = jsonObj.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            if ("columns".equals(key) || "fields".equals((key))) {
                jsonList = jsonObj.getJSONArray(key);
                if (jsonList != null) {
                    Log.DebugWriteInfo(jsonList.toString());
                }
            } else {
                JSONObject o1 = (JSONObject) jsonObj.get(key); //jsonObj.getJSONObject(key);

                if (o1 != null) {
                    Iterator<Object> it11 = o1.keys();
                    while (o1 != null && it11.hasNext()) {
                        String key11 = (String) it11.next();
                        if ("columns".equals(key11) || "fields".equals((key11))) {
                            jsonList = o1.getJSONArray(key11);
                            if (jsonList != null) {
                                Log.DebugWriteInfo(jsonList.toString());
                            }
                        }
                    }
                }
            }
        }
        if (jsonList != null) {
            Log.DebugWriteInfo(String.format("array size: %d", jsonList.size()));
        }
        return jsonList;
    }
   /* *//**
     将json字段转换为dt

     @param jsonArray
     @return
     *//*
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


    *//**
     将json字段转换为dt

     @param jsonArray
     @return
     *//*
    private DataTable convert(DataTable dt, JSONArray jsonArray)
    {
        for (int i =0;i<jsonArray.size();i++)
        {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String name = jsonObject.get("name").toString();
            String cnname = jsonObject.get("cnname").toString();
            Object lengthObj = jsonObject.get("length");
            if (lengthObj == null)
            {
                lengthObj = "0";
            }
            String lengthStr = lengthObj.toString();
            String[] lengthArray = {lengthStr};
            if (lengthStr.indexOf(',') >= 0)
            {
                lengthArray = lengthStr.split("[,]", -1);
            }

            String datatype = (String)jsonObject.get("datatype");
            String enumval = (String)jsonObject.get("enum");
            if (!StringUtils.isEmpty(enumval) && enumval.indexOf(",") > 0)
            {
                datatype = "ENUM";
            }

            String note = "无";
            int minLen = 0;
            int maxLen = 100;
            int datatypeInt = DataType.AppString;
            switch (datatype.toUpperCase())
            {
                case "VARCHAR":
                case "TEXT":
                case "STRING":
                    datatypeInt = DataType.AppString;
                    if (lengthArray.length == 1)
                    {
                        OutObject<Integer> tempOut_maxLen = new OutObject<Integer>();
                        tangible.TryParseHelper.tryParseInt(lengthArray[0], tempOut_maxLen);
                        maxLen = tempOut_maxLen.outArgValue;
                    }
                    break;
                case "DATE":
                    datatypeInt = DataType.AppDate;
                    maxLen = 20;
                    break;
                case "DATETIME":
                    datatypeInt = DataType.AppDateTime;
                    maxLen = 20;
                    break;
                case "NUMBER":
                case "INT":
                case "INTEGER":
                case "BOOLEN": //boolen.
                    datatypeInt = DataType.AppInt;
                    maxLen = 10;
                    break;
                case "DECIMAL":
                    datatypeInt = DataType.AppMoney;
                    if (lengthArray.length == 2)
                    {
                        //金额类型10,2，表示2为小数
                        tangible.OutObject<Integer> tempOut_maxLen2 = new tangible.OutObject<Integer>();
                        tangible.TryParseHelper.tryParseInt(lengthArray[0], tempOut_maxLen2);
                        maxLen = tempOut_maxLen2.outArgValue;
                        tangible.OutObject<Integer> tempOut_minLen = new tangible.OutObject<Integer>();
                        tangible.TryParseHelper.tryParseInt(lengthArray[1], tempOut_minLen);
                        minLen = tempOut_minLen.outArgValue;
                    }
                    break;
                case "FLOAT":
                    datatypeInt = DataType.AppFloat;
                    maxLen = 10;
                    break;
                case "ENUM": // 枚举.
                    datatypeInt = DataType.AppInt;
                    if (enumval != null)
                    {
                        String str = enumval.toString();
                        String[] array = str.split(java.util.regex.Pattern.quote(","), -1);
                        note = array.Join("|");
                    }
                    break;
                default:
                    break;
            }

            DataRow dr = dt.NewRow();
            //  string no = name + "*" + cnname + "*" + datatypeInt + "*0*" + maxLen + "*" + note;
            dr.set("No", DBAccess.GenerGUID()); // no;
            dr.set("Name", cnname);
            dr.set("KeyOfEn", name);
            dr.set("DataType", datatypeInt);
            dr.set("MinLen", minLen);
            dr.set("MaxLen", maxLen);
            dr.set("Note", note);

            String typeStr = "文本";
            if (datatypeInt == 1)
            {
                typeStr = "文本";
            }
            if (datatypeInt == 2)
            {
                typeStr = "数值int";
            }
            if (datatypeInt == 3)
            {
                typeStr = "数值-float";
            }
            if (datatypeInt == 4)
            {
                typeStr = "布尔值";
            }
            if (datatypeInt == 5)
            {
                typeStr = "数值-double";
            }
            if (datatypeInt == 6)
            {
                typeStr = "日期";
            }
            if (datatypeInt == 7)
            {
                typeStr = "日期时间";
            }
            if (datatypeInt == 8)
            {
                typeStr = "金额";
            }
            if (note.contains("|") == true)
            {
                typeStr = "枚举";
            }

            dr.setValue("数据类型",typeStr);
            dt.Rows.add(dr);
        }

        return dt;
    }*/



}
