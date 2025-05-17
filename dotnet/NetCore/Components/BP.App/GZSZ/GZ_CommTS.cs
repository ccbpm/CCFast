using System;
using System.Collections.Generic;
using System.Data;
using System.Text;
using System.Web;
using BP.DA;
using BP.Sys;
using BP.Web;
using BP.Port;
using BP.En;
using BP.WF;
using BP.WF.Template;
using BP.Difference;
using System.IO;
using BP.WF.HttpHandler;
using System.Collections;
using BP.CCFast.Portal.WindowExt;
using iTextSharp.text.pdf;
using System.IO.Compression;
using Microsoft.AspNetCore.Http;
using NPOI.SS.Formula.Functions;
using ICSharpCode.SharpZipLib.GZip;
using ICSharpCode.SharpZipLib.Zip;
using NPOI.HPSF;
using ZipFile = System.IO.Compression.ZipFile;

namespace BP.App.NetCore.GZSZ
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class GZ_CommTS : BP.WF.HttpHandler.DirectoryPageBase
    {
        /// <summary>
        /// 构造函数
        /// </summary>
        public GZ_CommTS()
        {
        }

        /// 根据项目id获取项目信息
        public object GetXmInfo()
        {
            string No = this.GetRequestVal("No");
            string sql = " SELECT No, PrjName Name,XMLX,FuZeRen,FuZeRenT," +
              "case XMLX when 0 then '房建' when 1 then '市政'  when 2 then '燃气' when 4 then '敞口' else '轨道' end xmlxName, " +
             "isnull((select sum(HeTongJinE) from PM_HTIncome where XiangMuBianHao=p.No),0) glje,prjSta" +
             " FROM PM_Prj p where No=" + SystemConfig.AppCenterDBVarStr + "v";
            Paras ps = new Paras();
            ps.SQL = sql;
            ps.Add("v", No);
            DataTable prj = DBAccess.RunSQLReturnTable(ps);
            return BP.Tools.Json.ToJson(prj.Rows[0]);
        }
        /// 根据workid，webno 获取送审图纸从表（ND101TuZhi2）中对应人员的专业信息
        public object GetZhuanYeInfo()
        {
            string WorkID = this.GetRequestVal("WorkID");
            string sql = " SELECT ZhuanYe,ZhuanYeT" +
             " FROM ND101TuZhi2  where RenYuan ='" + WebUser.No + "' and RefPK=" + SystemConfig.AppCenterDBVarStr + "v";
            Paras ps = new Paras();
            ps.SQL = sql;
            ps.Add("v", WorkID);
            DataTable prj = DBAccess.RunSQLReturnTable(ps);
            return BP.Tools.Json.ToJson(prj.Rows[0]);
        }

        /// 项目立项轮休PM_FuWuRY表获取下一个服务人员
        public object GetFuWuRY()
        {
            string pageID = this.GetRequestVal("pageID");
            string tb1 = this.GetRequestVal("tb1");
            string EmpNo = "";
            string EmpNoT = "";
            //非轨道，类型==燃气时，指定服务人员 ：周星红   zhouxh
            if (pageID== "Track1.Track2" && tb1 == "2")
            {
                EmpNo = "zhouxh";
                EmpNoT = "周星红";
            }
            else
            {
                string sql = " SELECT MyPK,EmpNo,EmpNoT,PaiXu,WFStatu FROM PM_FuWuRY ORDER BY PaiXu";
                DataTable prj = DBAccess.RunSQLReturnTable(sql);
                int WFStatu = 0;
                string MyPK = "";
              
                foreach (DataRow item in prj.Rows)
                {
                    int WFS = int.Parse(item["WFStatu"].ToString());
                    string MPK = item["MyPK"].ToString();
                    string ENo = item["EmpNo"].ToString();
                    string ENoT = item["EmpNoT"].ToString();
                    if (MyPK.Equals(""))
                    {
                        MyPK = MPK;
                        EmpNo = ENo;
                        EmpNoT = ENoT;
                    }
                    if (WFS == 1)
                    {
                        WFStatu = WFS;
                    }
                    else if (WFStatu == 1)
                    {
                        MyPK = MPK;
                        EmpNo = ENo;
                        EmpNoT = ENoT;
                        WFStatu = WFS;
                    }
                }
                sql = " update PM_FuWuRY set WFStatu=0";
                DBAccess.RunSQL(sql);
                sql = " update PM_FuWuRY set WFStatu=1 where MyPK='" + MyPK + "'";
                DBAccess.RunSQL(sql);
            }
           
            return EmpNo + "," + EmpNoT;
        }
        /// 创建项目总工作量时获取提前存储的送审工作量
        public object GetSongShens()
        {
            //项目名称
            string XiangMuNo = this.GetRequestVal("XiangMuNo");
            string sql = " SELECT JieDuanNo,worknodeno,sum(BenCiSongShenLiang)BenCiSongShenLiang FROM PM_WorkDtlSS where XiangMuNo=" + SystemConfig.AppCenterDBVarStr + "v group by JieDuanNo,worknodeno";
            Paras ps = new Paras();
            ps.SQL = sql;
            ps.Add("v", XiangMuNo);
            DataTable prj = DBAccess.RunSQLReturnTable(ps);
            return BP.Tools.Json.ToJson(prj.Rows);
        }
        /// 项目是否已经创建过合同
        public object IsCreateHTByXM()
        {
            //项目名称
            string XiangMuNo = this.GetRequestVal("XiangMuNo");
            string sql = " SELECT count(1) FROM PM_HTIncome where XiangMuBianHao=" + SystemConfig.AppCenterDBVarStr + "v ";
            Paras ps = new Paras();
            ps.SQL = sql;
            ps.Add("v", XiangMuNo);
            int prj = DBAccess.RunSQLReturnValInt(ps);
            return BP.Tools.Json.ToJson(prj);
        }
        /// 根据合同编号获取之前所有的开票信息
        public object queryKaiPiaosByHT()
        {
            //项目名称
            string HtBH = this.GetRequestVal("HtBH");
            string sql = " SELECT ZhuHeTongBianHao,HeTongBianHao,HeTongMingChen,KaiPiaoRiQi,KaiPiaoShenQingJinE,ZHTLJYDZJE FROM PM_KaiPiaoSQ where HeTongBianHao=" + SystemConfig.AppCenterDBVarStr + "v  order by KaiPiaoRiQi desc ";
            Paras ps = new Paras();
            ps.SQL = sql;
            ps.Add("v", HtBH);
            DataTable prj = DBAccess.RunSQLReturnTable(ps);
            return BP.Tools.Json.ToJson(prj.Rows);
        }
        /// 根据流程编号和workid 获取所有的流程参与人员的No
        public string GetNOsByFlowWorkID()
        {
            string WorkID = this.GetRequestVal("WorkID");
            string FlowNo = this.GetRequestVal("FlowNo");
            FlowNo = "ND"+FlowNo.Replace("0", "")+"track";
            string sql = " SELECT EmpFrom,EmpTo from " + FlowNo + " where WorkID=" + SystemConfig.AppCenterDBVarStr + "v1";
            Paras ps = new Paras();
            ps.SQL = sql;
            ps.Add("v1", WorkID);
            DataTable dtt = DBAccess.RunSQLReturnTable(ps);
            string rstr="";
            foreach (DataRow dr in dtt.Rows)
            {
                rstr += dr[0].ToString() + ","+ dr[1].ToString()+",";
            }
            return rstr;
        }

        //导入数据
        public object PM_ImportByClear()
        {
            string msg = "";
            try
            {
                string rtype = PM_ImportTypeCheck();
                if ( !rtype.Equals(""))
                {
                    return rtype;
                }
                var files = HttpContextHelper.RequestFiles();
                string fileName = System.IO.Path.GetFileName(files[0].FileName);
                string type = this.GetRequestVal("type");
                string impType = this.GetRequestVal("impType");
                string ext = ".zip";
                //excel
                if (impType.Equals("0") )
                {
                    ext = ".xlsx";
                    //设置文件名
                    string fileNewName = DateTime.Now.ToString("yyyyMMddHHmmssff") + ext;
                    //文件存放路径
                    string filePath = BP.Difference.SystemConfig.PathOfTemp + fileNewName;
                    HttpContextHelper.UploadFile(files[0], filePath);
                    if (type.Equals("Prj"))
                    {
                        msg= Prj_Import(filePath);
                    }
                    else if (type.Equals("DanWei"))
                    {
                        msg = DanWei_Import(filePath);
                    }
                }
                else  //zip文件
                {
                    //文件存放路径
                    string zipFlod = BP.Difference.SystemConfig.PathOfTemp + "GZSZ/" + DateTime.Now.ToString("yyyyMMddHHmmssff") + "/";
                    //zip解压后文件夹
                    String zipFlodExt = SystemConfig.PathOfDataUser + "UploadFile/GZSZ/" + DateTime.Now.ToString("yyyyMMddHHmmssff") + "/";
                    // 如果文件夹不存在，则创建文件夹
                    if (System.IO.Directory.Exists(zipFlodExt) == false)
                      System.IO.Directory.CreateDirectory(zipFlodExt);

                    string fname = DateTime.Now.ToString("yyyyMMddHHmmssff") + ext;
                    string zipFile = zipFlod + fname;
                    HttpContextHelper.UploadFile(files[0], zipFile);
                    //zip解压前 注册代码页提供器实例
                    Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);
                    Uncompress(zipFile, zipFlodExt);
                    if (type.Equals("PrjSSLC"))  //送审
                    {
                        msg = PrjSSLC_Import(zipFlodExt);
                    }
                    else if (type.Equals("ZHTIncome"))//合同
                    {
                        msg = ZHTIncome_Import(zipFlodExt);
                    }
                    else if (type.Equals("KaiPiaoSQ"))//开票申请
                    {
                        msg = KaiPiaoSQ_Import(zipFlodExt);
                    }
                }
            }catch (Exception e)
            {
                return "err@导入失败"+e.Message;
            }
            if (msg.Equals(""))
            {
                return "info@导入成功";
            }
            else
            {
                return msg;
            }
           
        }
        //单位数据导入
        public string DanWei_Import(string filePath)
        {
            DataTable dtRows = BP.DA.DBLoad.ReadExcelFileToDataTable(filePath);
            string cls = "TS.PM.DanWei";
            string msg = "";
            string existNo = ",";
            string[] flmc = new string[] { "建设单位", "设计单位", "勘察单位" };
            foreach (DataRow dr in dtRows.Rows)
            {
                TSEntityNoName en = new TSEntityNoName(cls);
                string Name = dr["客户名称"].ToString();
                string sql = "select no from PM_DanWei where name='" + Name + "'";
                string no = DBAccess.RunSQLReturnString(sql);
                if (no!=null && !no.Equals(""))
                {
                    msg += " err@编号:" + no + "," + Name + ",已经存在.";
                    existNo += Name + ",";
                    continue;
                }
                en.SetValByKey("Name", Name);
                string? DWLX = dr["客户分类名称"] as string; //客户分类名称
                int flindex = queryIndex(DWLX, flmc);
                en.SetValByKey("DWLX", flindex);
                string? SHB = dr["统一社会信用代码/纳税人识别号"] as string; //统一社会信用代码/纳税人识别号
                en.SetValByKey("SHB", SHB);
                string? Addr = dr["地址"] as string; //地址
                en.SetValByKey("Addr", Addr);
                string? Tel = dr["电话"] as string; //电话
                en.SetValByKey("Tel", Tel);
                string? BankName = dr["开户行名称"] as string; //开户行名称
                en.SetValByKey("BankName", BankName);
                string? BankNum = dr["银行账号"] as string; //银行账号
                en.SetValByKey("BankNum", BankNum);
                string? Email = dr["联系邮箱"] as string; //联系邮箱
                en.SetValByKey("Email", Email);
                en.Insert();
            }
            //联系人
            DataTable dt1Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(filePath, 1);
            string cls1 = "TS.PM.Contact";
            foreach (DataRow dr in dt1Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls1);
                string? ssName = dr["客户名称"] as string; //客户名称
                if (existNo.Contains("," + ssName + ","))
                {
                    continue;
                }
                string sql = "select no from PM_DanWei where name='"+ ssName + "'";
                string no = DBAccess.RunSQLReturnString(sql);
                if(no==null || no.Equals(""))
                {
                    msg += "info@联系人对应单位[" + ssName + "]不存在.";
                    continue;
                }
                en.SetValByKey("No", no);
                string? Linker = dr["联系人"] as string; // 联系人
                en.SetValByKey("Linker", Linker);
                string? Tel = dr["联系电话"] as string; // 联系电话
                en.SetValByKey("Tel", Tel);
                en.Insert();
               
            }
            return msg;
        }
        //开票申请导入
        public string KaiPiaoSQ_Import(string zipFlodExt)
        {
            string fullName = Get_ZipExcelFile(zipFlodExt);
            if (fullName == "")
            {
                return "err@文件不符合约定的格式，没有找到根目录下的文件.";
            }
            //主数据处理
            DataTable dt0Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName);
            string cls0 = "TS.PM.KaiPiaoSQ";
            string msg = "";
            string existNo = ",";
            string[] sf = new string[] { "否", "是" };
            string[] fplx = new string[] { "数电票(普通发票)", "数电票(增值税专用发票)", "增值税普通发票", "增值税专用发票" };
            string[] sl = new string[] { "0%", "1%", "3%", "6%" };
            DataTable danweis = DBAccess.RunSQLReturnTable("select * from PM_DanWei");
            foreach (DataRow dr in dt0Rows.Rows)
            {
                TSEntityNoName en = new TSEntityNoName(cls0);
                en.No = dr[0].ToString();
                if (en.IsExits == true)
                {
                    msg += " err@编号:" + en.No + "," + en.Name + ",已经存在.";
                    existNo += en.No + ",";
                    continue;
                }
                en.Name = "";
                string? HeTongBianHao = dr["合同编号"] as string; //合同编号
                if (HeTongBianHao == null || HeTongBianHao.Equals(""))
                {
                    msg += " err@编号:" + en.No + "对应的合同编号不能为空";
                    continue;
                }
                en.SetValByKey("HeTongBianHao", HeTongBianHao);
                string? HeTongMingChen = dr["合同名称"] as string; //合同名称
                en.SetValByKey("HeTongMingChen", HeTongMingChen);
                string? KaiPiaoShenQingRen = dr["开票申请人"] as string; //开票申请人
                en.SetValByKey("KaiPiaoShenQingRen", KaiPiaoShenQingRen);
                string? KaiPiaoShenQingRenBH = dr["登录账号"] as string; //登录账号
                en.SetValByKey("KaiPiaoShenQingRenBH", KaiPiaoShenQingRenBH);

                string? KaiPiaoRiQi = dr["开票申请日期"] as string; //开票申请日期
                en.SetValByKey("KaiPiaoRiQi", KaiPiaoRiQi);

                string? XiangMuMingChen = dr["项目名称"] as string; //项目名称
                en.SetValByKey("XiangMuMingChen", XiangMuMingChen);
                string? ZhuHeTongBianHao = dr["主合同编号"] as string; //主合同编号
                en.SetValByKey("ZhuHeTongBianHao", ZhuHeTongBianHao);
                string? JSDWMC = dr["建设单位名称"] as string; //建设单位名称
                en.SetValByKey("JSDWMC", JSDWMC);
                string? XHTJE = dr["现合同金额"] as string; //现合同金额
                en.SetValByKey("XHTJE",return0ByEmpt(XHTJE));
                string? HeTongQianDingRiQi = dr["合同签订日期"] as string; //合同签订日期
                en.SetValByKey("HeTongQianDingRiQi", HeTongQianDingRiQi);
                string? YuJiHuiKuanRiQi = dr["预计回款日期"] as string; //预计回款日期
                en.SetValByKey("YuJiHuiKuanRiQi", YuJiHuiKuanRiQi);
                string? ZHTLJYKPJE = dr["主合同截止上一次累计已开票金额"] as string; //主合同截止上一次累计已开票金额
                en.SetValByKey("XHTJE", return0ByEmpt(ZHTLJYKPJE));
                string? ZHTLJYDZJE = dr["主合同截止上一次累计已到账金额"] as string; //主合同截止上一次累计已到账金额
                en.SetValByKey("XHTJE", return0ByEmpt(ZHTLJYDZJE));
                string? ZHTKGZJLJYQRGZL = dr["主合同截止上一次累计已确认金额"] as string; //主合同截止上一次累计已确认金额
                en.SetValByKey("XHTJE", return0ByEmpt(ZHTKGZJLJYQRGZL));
                string? SFJSK = dr["是否结算款"] as string; //是否结算款
                int sfjskindex = queryIndex(SFJSK,sf);
                en.SetValByKey("SFJSK", sfjskindex);
                string? JieSuanJinE = dr["结算金额"] as string; //结算金额
                en.SetValByKey("JieSuanJinE", return0ByEmpt(JieSuanJinE));
                string? HeTongShouFeiTiaoKua = dr["合同收费条款"] as string; //合同收费条款
                en.SetValByKey("HeTongShouFeiTiaoKua", HeTongShouFeiTiaoKua);
                string? FaPiaoLeiXing = dr["发票类型"] as string; //发票类型
                int fplxindex = queryIndex(FaPiaoLeiXing,fplx);
                en.SetValByKey("FaPiaoLeiXing", fplxindex);
                string? KaiPiaoShenQingJinE = dr["开票申请金额"] as string; //开票申请金额
                en.SetValByKey("KaiPiaoShenQingJinE", return0ByEmpt(KaiPiaoShenQingJinE));

                string? ShiFouLianHeTi = dr["是否联合体"] as string; //是否联合体
                int sflhtindex = queryIndex(ShiFouLianHeTi, sf);
                en.SetValByKey("ShiFouLianHeTi", sflhtindex);
                string? ShiFouDaiShouDaiFuXi = dr["是否代收代付性质"] as string; //是否代收代付性质
                int sfdsdfindex = queryIndex(ShiFouDaiShouDaiFuXi, sf);
                en.SetValByKey("ShiFouDaiShouDaiFuXi", sfdsdfindex);
                string? BenCiShouRuJinE = dr["归属我方金额"] as string; //归属我方金额
                en.SetValByKey("BenCiShouRuJinE", return0ByEmpt(BenCiShouRuJinE));
                string? BenCiBoFuJinE = dr["归属联合体方金额"] as string; //归属联合体方金额
                en.SetValByKey("BenCiBoFuJinE", return0ByEmpt(BenCiBoFuJinE));
                string? ShanWeiMingChenT = dr["单位名称"] as string; //单位名称
                string ShanWeiMingChen = queryIndexByTable(ShanWeiMingChenT,danweis);
                en.SetValByKey("ShanWeiMingChenT", ShanWeiMingChenT);
                en.SetValByKey("ShanWeiMingChen", ShanWeiMingChen);
                if (ShanWeiMingChen!=null && !ShanWeiMingChen.Equals(""))
                {
                    string sql = "select shb,bankName,bankNum,addr,tel,email   from PM_DanWei where Name='" + ShanWeiMingChenT + "'";
                    DataTable xmTable = DBAccess.RunSQLReturnTable(sql);
                    foreach (DataRow xmdr in xmTable.Rows)
                    {
                        en.SetValByKey("NaShuiRenShiBieHao", xmdr["shb"]); //纳税人识别号
                        en.SetValByKey("KaiHuYinXing", xmdr["bankName"]);//开户银行
                        en.SetValByKey("YinXingZhangHao", xmdr["bankNum"]);//银行帐号
                        en.SetValByKey("KaiPiaoLianXiDiZhi", xmdr["addr"]); //开票联系地址
                        en.SetValByKey("KaiPiaoLianXiDianHua", xmdr["tel"]);//开票联系电话
                        en.SetValByKey("DianZiFaPiaoShouPiao", xmdr["email"]);//电子发票收票邮箱

                    }
                }
                en.Insert();
            }

            //详细发票信息
            DataTable dt1Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 1);
            string cls1 = "TS.PM.FaPiaoTZ";
            foreach (DataRow dr in dt1Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls1);
                string? ssNo = dr["开票编号"] as string; //开票编号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("KpNo", ssNo);
                string? FaPiaoBianHao = dr["发票编号"] as string; // 发票编号
                en.SetValByKey("FaPiaoBianHao", FaPiaoBianHao);
                string? KaiPiaoRiQi = dr["开票日期"] as string; // 开票日期
                en.SetValByKey("KaiPiaoRiQi", KaiPiaoRiQi);
                string? ShuiLu = dr["税率"] as string; // 税率
                int slindex = queryIndex(ShuiLu,sl);
                en.SetValByKey("ShuiLu", slindex);
                string? ShuiE = dr["税额"] as string; // 税额
                en.SetValByKey("ShuiE",return0ByEmpt(ShuiE));
                string? FaPiaoJinE = dr["发票金额"] as string; // 发票金额
                en.SetValByKey("FaPiaoJinE", return0ByEmpt(FaPiaoJinE));
                string? FaPiaoBeiZhu = dr["发票备注"] as string; // 发票备注
                en.SetValByKey("FaPiaoBeiZhu", FaPiaoBeiZhu);
                //附件
                string? xh = dr["序号"] as string; //序号
                string rstr = upload_ath(zipFlodExt, "发票附件", xh + "-");
                if (!rstr.Equals(""))
                {
                    en.SetValByKey("FName", rstr.Split(";")[0]);
                    en.SetValByKey("FPath", rstr.Split(";")[1]);
                }
                en.Insert();
            }

            //结算资料
            DataTable dt2Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 2);
            string cls2 = "TS.PM.JieSuanFJ";
            foreach (DataRow dr in dt2Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls2);
                string? ssNo = dr["开票编号"] as string; //开票编号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("KpNo", ssNo);
                string? xh = dr["序号"] as string; // 序号
                string rstr = upload_ath(zipFlodExt, "结算资料", xh + "-");
                if (!rstr.Equals(""))
                {
                    en.SetValByKey("FName", rstr.Split(";")[0]);
                    en.SetValByKey("FPath", rstr.Split(";")[1]);
                }
                en.Insert();
            }
            return msg;
        }
        //收入合同导入
        public string ZHTIncome_Import(string zipFlodExt)
        {
            string fullName = Get_ZipExcelFile(zipFlodExt);
            if (fullName == "")
            {
                return "err@文件不符合约定的格式，没有找到根目录下的文件.";
            }
            //主数据处理
            DataTable dt0Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName);
            string cls0 = "TS.PM.ZHTIncome";
            string msg = "";
            string existNo = ",";
            string[] sf = new string[] { "否", "是" };
            string[] xmlxs = new string[] { "房建", "市政", "燃气", "轨道", "敞口" };
            string[] status = new string[] { "履行中", "终止" };
            string[] htly = new string[] { "委托", "投标", "其他" };
            string[] pslb = new string[] { "会签", "会议" };
            string[] yhtbgfs = new string[] { "增多", "减少" };
            string[] gzlzxlx = new string[] { "十万以下非轨道", "敞口", "十万以上非轨道" };
            string[] htsflx = new string[] { "定金", "进度款", "结算款" };
            string[] dydjd = new string[] { "中期成果", "最终成果" };
            string[] sl = new string[] { "0%", "1%", "3%", "6%" };
            DataTable danweis = DBAccess.RunSQLReturnTable("select * from PM_DanWei");
            DataTable zbbm = DBAccess.RunSQLReturnTable("SELECT No,Name, ParentNo FROM Port_Dept");
            foreach (DataRow dr in dt0Rows.Rows)
            {
                TSEntityNoName en = new TSEntityNoName(cls0);
                en.No = dr[0].ToString();
                if (en.IsExits == true)
                {
                    msg += " err@编号:" + en.No + "," + en.Name + ",已经存在.";
                    existNo += en.No + ",";
                    continue;
                }
                en.Name = dr[1].ToString();
                string? XiangMuBianHao = dr["项目编号"] as string; //项目编号
                if (XiangMuBianHao == null || XiangMuBianHao.Equals(""))
                {
                    msg += " err@编号:" + en.No + "对应的项目编号不能为空";
                    continue;
                }
                en.SetValByKey("XiangMuBianHao", XiangMuBianHao);
                en.SetValByKey("HeTongMingChen", dr[1].ToString());
                string? XiangMuMingChen = dr["项目名称"] as string; //项目名称
                en.SetValByKey("XiangMuMingChen", XiangMuMingChen);
                string? XMLX = dr["项目类型"] as string; //项目类型
                int xmlxindex = queryIndex(XMLX,xmlxs);
                en.SetValByKey("XiangMuMingChen", xmlxindex);
                string? NiGaoRiQi = dr["拟稿日期"] as string; //拟稿日期
                en.SetValByKey("NiGaoRiQi", NiGaoRiQi);
                string? HeTongLaiYuan = dr["合同来源"] as string; //合同来源
                int htlyindex = queryIndex(HeTongLaiYuan, htly);
                en.SetValByKey("HeTongLaiYuan", htlyindex);
                string? QianDingRiQi = dr["签订日期"] as string; //签订日期
                en.SetValByKey("QianDingRiQi", QianDingRiQi);
                string? JianSheShanWeiT = dr["建设单位"] as string; //建设单位
                string JianSheShanWei = queryIndexByTable(JianSheShanWeiT, danweis);
                en.SetValByKey("JianSheShanWeiT", JianSheShanWeiT);
                en.SetValByKey("JianSheShanWei", JianSheShanWei);
                string? JSSW_Linrek = dr["建设单位联系人"] as string; //建设单位联系人
                en.SetValByKey("JSSW_Linrek", JSSW_Linrek);
                string? JSSW_Tel = dr["建设单位电话"] as string; //建设单位电话
                en.SetValByKey("JSSW_Tel", JSSW_Tel);
                string? JianSheGuanLiShanWeiT = dr["建设管理单位"] as string; //建设管理单位
                string JianSheGuanLiShanWei = queryIndexByTable(JianSheGuanLiShanWeiT, danweis);
                en.SetValByKey("JianSheGuanLiShanWeiT", JianSheGuanLiShanWeiT);
                en.SetValByKey("JianSheGuanLiShanWei", JianSheGuanLiShanWei);
                string? JSGLSW_Linrek = dr["建设管理单位联系人"] as string; //建设管理单位联系人
                en.SetValByKey("JSGLSW_Linrek", JSGLSW_Linrek);
                string? JSGLSW_Tel = dr["建设管理单位电话"] as string; //建设管理单位电话
                en.SetValByKey("JSGLSW_Tel", JSGLSW_Tel);
                string? ShiFouBuChongHeTong = dr["是否补充合同"] as string; //是否补充合同
                int sfbcindex = queryIndex(ShiFouBuChongHeTong, sf);
                en.SetValByKey("ShiFouBuChongHeTong", sfbcindex);
                string? ZhuHeTong = dr["主合同编号"] as string; //主合同编号
                en.SetValByKey("ZhuHeTong", ZhuHeTong);
                string? ZhuHeTongT = dr["主合同名称"] as string; //主合同名称
                en.SetValByKey("ZhuHeTongT", ZhuHeTongT);
                string? ZHTLJYKPJE = dr["主合同累计已开票金额"] as string; //主合同累计已开票金额
                en.SetValByKey("ZHTLJYKPJE", return0ByEmpt(ZHTLJYKPJE));
                string? ZHTLJYDZJE = dr["主合同累计已到帐金额"] as string; //主合同累计已到帐金额
                en.SetValByKey("ZHTLJYDZJE", return0ByEmpt(ZHTLJYDZJE));
                string? ZHTKGZJLJYQRGZL = dr["主合同开工至今累计已确认工作量"] as string; //主合同开工至今累计已确认工作量
                en.SetValByKey("ZHTKGZJLJYQRGZL", return0ByEmpt(ZHTKGZJLJYQRGZL));
                string? PingShenLeiBie = dr["评审类别"] as string; //评审类别
                int pslbindex = queryIndex(PingShenLeiBie, pslb);
                en.SetValByKey("PingShenLeiBie", pslbindex);
                string? HeTongJinE = dr["合同金额"] as string; //合同金额
                en.SetValByKey("HeTongJinE", return0ByEmpt(HeTongJinE));
                string? KCSZF = dr["勘察部分审查费"] as string; //勘察部分审查费
                en.SetValByKey("KCSZF", return0ByEmpt(KCSZF));
                string? SJSZF = dr["设计部分审查费"] as string; //设计部分审查费
                en.SetValByKey("SJSZF", return0ByEmpt(SJSZF));
                string? GongZuoLiangYingXian = dr["工作量影响金额"] as string; //工作量影响金额
                en.SetValByKey("GongZuoLiangYingXian", return0ByEmpt( GongZuoLiangYingXian));
                string? KanChaYingXiangJinE = dr["勘察影响金额"] as string; //勘察影响金额
                en.SetValByKey("KanChaYingXiangJinE", return0ByEmpt(KanChaYingXiangJinE));
                string? SheJiYingXiangJinE = dr["设计影响金额"] as string; //设计影响金额
                en.SetValByKey("SheJiYingXiangJinE", return0ByEmpt(SheJiYingXiangJinE));
                string? GCNRJXMYQGY = dr["工程内容及项目要求概要"] as string; //工程内容及项目要求概要
                en.SetValByKey("GCNRJXMYQGY", GCNRJXMYQGY);
                string? XiuDingBianGengNeiRo = dr["修订变更内容摘要"] as string; //修订变更内容摘要
                en.SetValByKey("XiuDingBianGengNeiRo", XiuDingBianGengNeiRo);
                string? SFYXZHTJE = dr["是否影响主合同金额"] as string; //是否影响主合同金额
                int sfyxindex = queryIndex(SFYXZHTJE, sf);
                en.SetValByKey("SFYXZHTJE", sfyxindex);
                string? XinZengBuChongJingE = dr["新增补充净额"] as string; //新增补充净额
                en.SetValByKey("XinZengBuChongJingE", return0ByEmpt(XinZengBuChongJingE));
                string? XianZhuHeTongJinE = dr["现主合同金额"] as string; //现主合同金额
                en.SetValByKey("XianZhuHeTongJinE", return0ByEmpt(XianZhuHeTongJinE));
                string ? StrZhuBanBuMenT = dr["主办部门"] as string; //主办部门
                string StrZhuBanBuMen = queryIndexByTable(StrZhuBanBuMenT,zbbm);
                en.SetValByKey("StrZhuBanBuMenT", StrZhuBanBuMenT);
                en.SetValByKey("StrZhuBanBuMen", StrZhuBanBuMen);
                string ? KeFenBaoYuSuanJinE = dr["可分包预算金额"] as string; //可分包预算金额
                en.SetValByKey("KeFenBaoYuSuanJinE", return0ByEmpt(KeFenBaoYuSuanJinE));
                string ? ShouRuHeTongLeiXing = dr["工作量子项类型"] as string; //工作量子项类型
                int zlindex = queryIndex(ShouRuHeTongLeiXing, gzlzxlx);
                en.SetValByKey("ShouRuHeTongLeiXing", zlindex);
                string ? YuanHeTongBianGengFa = dr["原合同变更方式"] as string; //原合同变更方式
                int bgindex = queryIndex(YuanHeTongBianGengFa, yhtbgfs);
                en.SetValByKey("YuanHeTongBianGengFa", bgindex);
                string ? HeTongYuanJinE = dr["合同原金额"] as string; //合同原金额
                en.SetValByKey("HeTongYuanJinE", return0ByEmpt(HeTongYuanJinE));
                string ? BianGengJinE = dr["变动金额"] as string; //变动金额
                en.SetValByKey("BianGengJinE", return0ByEmpt(BianGengJinE));
                string ? QueDingJinE = dr["变动后金额"] as string; //变动后金额
                en.SetValByKey("QueDingJinE", return0ByEmpt(QueDingJinE));
                string ? BianGengYuanYin = dr["变更原因"] as string; //变更原因
                en.SetValByKey("BianGengYuanYin", BianGengYuanYin);
                string ? SFXYLYBH = dr["是否需要履约保函"] as string; //是否需要履约保函
                int bhindex = queryIndex(SFXYLYBH, sf);
                en.SetValByKey("SFXYLYBH", bhindex);
                string ? QTFXTK = dr["其他风险条款"] as string; //其他风险条款
                en.SetValByKey("QTFXTK", QTFXTK);
                string ? BeiZhu = dr["备注"] as string; //备注
                en.SetValByKey("BeiZhu", BeiZhu);
                string ? HeTongShouFeiTiaoKua = dr["合同收费条款"] as string; //合同收费条款
                en.SetValByKey("HeTongShouFeiTiaoKua", HeTongShouFeiTiaoKua);
                string ? ShiFouLianHeTi = dr["是否联合体"] as string; //是否联合体
                int sfindex = queryIndex(ShiFouLianHeTi, sf);
                en.SetValByKey("ShiFouLianHeTi", sfindex);
                string ? LianHeTiFangJinE = dr["联合体方金额"] as string; //联合体方金额
                en.SetValByKey("LianHeTiFangJinE",return0ByEmpt(LianHeTiFangJinE));
                en.Insert();
            }

            //合同收费条款
            DataTable dt1Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 1);
            string cls1 = "TS.PM.ShouFeiTK";
            foreach (DataRow dr in dt1Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls1);
                string? ssNo = dr["合同编号"] as string; 
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? HeTongShouFeiLeiXing = dr["类型"] as string; // 类型
                int srindex = queryIndex(HeTongShouFeiLeiXing, htsflx);
                en.SetValByKey("HeTongShouFeiLeiXing", srindex);
                string? DingJinRiQi = dr["定金日期"] as string; // 定金日期
                en.SetValByKey("DingJinRiQi", DingJinRiQi);
                string? JieDuanNeiRong = dr["阶段内容"] as string; // 阶段内容
                en.SetValByKey("JieDuanNeiRong", JieDuanNeiRong);
                string? DuiYingDaJieDuan = dr["对应大阶段"] as string; // 对应大阶段
                int djdindex = queryIndex(DuiYingDaJieDuan, dydjd);
                en.SetValByKey("DuiYingDaJieDuan", djdindex);
                string? JieDuanZhanBi = dr["阶段占比"] as string; //阶段占比
                en.SetValByKey("JieDuanZhanBi", JieDuanZhanBi);
                string? YuQiShouRu = dr["预期收入"] as string; //预期收入
                en.SetValByKey("YuQiShouRu", YuQiShouRu);

                en.Insert();
            }

            //联合体
            DataTable dt2Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 2);
            string cls2 = "TS.PM.LianHeTi";
            foreach (DataRow dr in dt2Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls2);
                string? ssNo = dr["合同编号"] as string; //合同编号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? GongSiMingT = dr["公司名"] as string; // 公司名
                string GongSiMing = queryIndexByTable(GongSiMingT, danweis);
                en.SetValByKey("GongSiMingT", GongSiMingT);
                en.SetValByKey("GongSiMing", GongSiMing);
                string? QianTouFang = dr["牵头方"] as string; // 牵头方
                int qtdindex = queryIndex(QianTouFang, sf);
                en.SetValByKey("QianTouFang", qtdindex);
                string? LianHeTiJinE = dr["联合体金额"] as string; // 联合体金额
                en.SetValByKey("LianHeTiJinE", return0ByEmpt(LianHeTiJinE));
                string? WoSiDaiShouJinE = dr["我司代收金额"] as string; // 我司代收金额
                en.SetValByKey("WoSiDaiShouJinE", WoSiDaiShouJinE);
                string? ShuiLu = dr["税率"] as string; // 税率
                int slindex = queryIndex(ShuiLu, sl);
                en.SetValByKey("ShuiLu", slindex);
                string? BeiZhu = dr["备注"] as string; // 备注
                en.SetValByKey("BeiZhu", BeiZhu);
                string? LianHeTiZhuangTai = dr["状态"] as string; // 状态
                int ztindex = queryIndex(LianHeTiZhuangTai, status);
                en.SetValByKey("LianHeTiZhuangTai", ztindex);

                en.Insert();
            }

            //收入子项
            DataTable dt3Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 3);
            string cls3 = "TS.PM.ShouRuZXCF";
            foreach (DataRow dr in dt3Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls3);
                string? ssNo = dr["合同编号"] as string; //合同编号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? ShouRuZiXiangMu = dr["收入子项目"] as string; // 收入子项目
                string? JinE = dr["金额"] as string; // 金额
                en.SetValByKey("ShouRuZiXiangMu", return0ByEmpt(ShouRuZiXiangMu));
                en.SetValByKey("JinE", JinE);

                en.Insert();
            }
            //扫描件
            DataTable dt4Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName,4);
            string cls4= "TS.PM.SaoMiaoFJ";
            foreach (DataRow dr in dt4Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls4);
                string? ssNo = dr["合同编号"] as string; //合同编号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? xh = dr["序号"] as string; // 序号
                string rstr = upload_ath(zipFlodExt, "上传扫描附件", xh + "-");
                if (!rstr.Equals(""))
                {
                    en.SetValByKey("FName", rstr.Split(";")[0]);
                    en.SetValByKey("FPath", rstr.Split(";")[1]);
                }
                en.Insert();
            }
            //中标附件
            DataTable dt5Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 5);
            string cls5 = "TS.PM.ZhongBiaoFJ";
            foreach (DataRow dr in dt5Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls5);
                string? ssNo = dr["合同编号"] as string; //合同编号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? xh = dr["序号"] as string; // 序号
                string rstr = upload_ath(zipFlodExt, "上传扫描附件", xh + "-");
                if (!rstr.Equals(""))
                {
                    en.SetValByKey("FName", rstr.Split(";")[0]);
                    en.SetValByKey("FPath", rstr.Split(";")[1]);
                }
                en.Insert();
            }

            return msg;
        }
        //项目送审处理
        public string PrjSSLC_Import(string zipFlodExt)
        {
            string fullName= Get_ZipExcelFile(zipFlodExt);
            if (fullName == "")
            {
                return "err@文件不符合约定的格式，没有找到根目录下的文件.";
            }
            //主数据处理
            DataTable dt0Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName);
            string cls0 = "TS.PM.PrjSSLC";
            string msg = "";
            string existNo = ",";
            string[] htlx = new string[] { "市政", "道路", "燃气", "建筑" };
            string[] jsyj = new string[] { "长度", "工程投资额", "面积", "其它" };
            string[] qtlx = new string[] { "涉及工程质量安全", "涉及消防安全" };
            DataTable zhuanyes = DBAccess.RunSQLReturnTable("select * from pm_ZhuanYe");
            DataTable jieduans = DBAccess.RunSQLReturnTable("select * from PM_WorkJieDuan");
            DataTable gongzuos = DBAccess.RunSQLReturnTable("select * from PM_WorkNode");
            foreach (DataRow dr in dt0Rows.Rows)
            {
                TSEntityNoName en = new TSEntityNoName(cls0);
                en.No = dr[0].ToString();
                if (en.IsExits == true)
                {
                    msg += " err@编号:" + en.No + "," + en.Name + ",已经存在.";
                    existNo += en.No + ",";
                    continue;
                }
                en.Name ="";
                string? PrjNo = dr["项目编号"] as string; //项目编号
                if(PrjNo==null || PrjNo.Equals(""))
                {
                    msg += " err@编号:" + en.No + "对应的项目编号不能为空" ;
                    continue;
                }
                en.SetValByKey("PrjNo", PrjNo);
                string? PrjName = dr["项目名称"] as string; //项目名称
                en.SetValByKey("PrjName", PrjName);
                string? HeTongLeiXing = dr["合同类型"] as string; //合同类型
                int htlxindex = queryIndex(HeTongLeiXing, htlx);
                en.SetValByKey("HeTongLeiXing", htlxindex);
                string? GZLJSYJ = dr["工作量计算依据"] as string; //工作量计算依据
                int jsyjindex = queryIndex(GZLJSYJ, jsyj);
                en.SetValByKey("GZLJSYJ", jsyjindex);
                string? SheJiHeTongJinE = dr["设计合同金额"] as string; //设计合同金额
                en.SetValByKey("SheJiHeTongJinE", SheJiHeTongJinE);
                string? SheJiZhanBi = dr["设计占比"] as string; //设计占比
                en.SetValByKey("SheJiZhanBi", SheJiZhanBi);
                string? SJWCZCDZGCTZEZMJQT = dr["设计完成总量"] as string; //设计完成总量
                en.SetValByKey("SJWCZCDZGCTZEZMJQT", SJWCZCDZGCTZEZMJQT);
                string? KanChaHeTongJinE = dr["勘察合同金额"] as string; //勘察合同金额
                en.SetValByKey("KanChaHeTongJinE", KanChaHeTongJinE);
                string? KanChaZhanBi = dr["勘察占比"] as string; //勘察占比
                en.SetValByKey("KanChaZhanBi", KanChaZhanBi);
                string? KanChaWanChengZongCh = dr["勘察完成总量"] as string; //勘察完成总量
                en.SetValByKey("KanChaWanChengZongCh", KanChaWanChengZongCh);
                string? JiHuaWanChengShiJian = dr["计划完成时间"] as string; //计划完成时间
                en.SetValByKey("JiHuaWanChengShiJian", JiHuaWanChengShiJian);
                string sql = "select SFZXM,ZXMBH,ZXMBHT,LiXiangShiJian,JSDWMC,JSDWMCT,JSDW_Linker,JSDW_Tel" +
                    ",DJGLDW,DJGLDWT,DJGLDW_Linker,DJGLDW_Tel,ZCBDW,ZCBDWT,ZCBDW_Linker,ZCBDW_Tel,SJDWMC,SJDWMCT,SJDW_Linker" +
                    ",SJDW_Tel,KCSWMC,KCSWMCT,KCDW_Linker,KCDW_Tel,GongChengGaiKuang,XMLX,HeTongLeiXing,GZLJSYJ,SheJiHeTongJinE" +
                    ",SheJiZhanBi,SJWCZCDZGCTZEZMJQT,KanChaHeTongJinE,KanChaZhanBi,KanChaWanChengZongCh" +
                    ",FuZeRen,FuZeRenT,XiangMuFuZeRen,XiangMuFuZeRenT   from PM_Prj where No='" + PrjNo + "'";
                DataTable xmTable = DBAccess.RunSQLReturnTable(sql);
                foreach (DataRow xmdr in xmTable.Rows)
                {
                    en.SetValByKey("SFZXM", xmdr["SFZXM"]);
                    en.SetValByKey("ZXMBH", xmdr["ZXMBH"]);
                    en.SetValByKey("ZXMBHT", xmdr["ZXMBHT"]);
                    en.SetValByKey("LiXiangShiJian", xmdr["LiXiangShiJian"]);
                    en.SetValByKey("JSDWMC", xmdr["JSDWMC"]);
                    en.SetValByKey("JSDWMCT", xmdr["JSDWMCT"]);
                    en.SetValByKey("JSDW_Linker", xmdr["JSDW_Linker"]);
                    en.SetValByKey("JSDW_Tel", xmdr["JSDW_Tel"]);
                    en.SetValByKey("DJGLDW", xmdr["DJGLDW"]);
                    en.SetValByKey("DJGLDWT", xmdr["DJGLDWT"]);
                    en.SetValByKey("DJGLDW_Linker", xmdr["DJGLDW_Linker"]);
                    en.SetValByKey("DJGLDW_Tel", xmdr["DJGLDW_Tel"]);
                    en.SetValByKey("ZCBDW", xmdr["ZCBDW"]);
                    en.SetValByKey("ZCBDWT", xmdr["ZCBDWT"]);
                    en.SetValByKey("ZCBDW_Linker", xmdr["ZCBDW_Linker"]);
                    en.SetValByKey("ZCBDW_Tel", xmdr["ZCBDW_Tel"]);
                    en.SetValByKey("SJDWMC", xmdr["SJDWMC"]);
                    en.SetValByKey("SJDWMCT", xmdr["SJDWMCT"]);
                    en.SetValByKey("SJDW_Linker", xmdr["SJDW_Linker"]);
                    en.SetValByKey("SJDW_Tel", xmdr["SJDW_Tel"]);
                    en.SetValByKey("KCSWMC", xmdr["KCSWMC"]);
                    en.SetValByKey("KCSWMCT", xmdr["KCSWMCT"]);
                    en.SetValByKey("KCDW_Linker", xmdr["KCDW_Linker"]);
                    en.SetValByKey("KCDW_Tel", xmdr["KCDW_Tel"]);
                    en.SetValByKey("XMLX", xmdr["XMLX"]);
                    en.SetValByKey("HeTongLeiXing", xmdr["HeTongLeiXing"]);
                    en.SetValByKey("GZLJSYJ", xmdr["GZLJSYJ"]);
                    en.SetValByKey("SheJiHeTongJinE", xmdr["SheJiHeTongJinE"]);
                    en.SetValByKey("SheJiZhanBi", xmdr["SheJiZhanBi"]);
                    en.SetValByKey("SJWCZCDZGCTZEZMJQT", xmdr["SJWCZCDZGCTZEZMJQT"]);
                    en.SetValByKey("KanChaHeTongJinE", xmdr["KanChaHeTongJinE"]);
                    en.SetValByKey("KanChaZhanBi", xmdr["KanChaZhanBi"]);
                    en.SetValByKey("KanChaWanChengZongCh", xmdr["KanChaWanChengZongCh"]);

                    en.SetValByKey("FuZeRen", xmdr["FuZeRen"]);
                    en.SetValByKey("FuZeRenT", xmdr["FuZeRenT"]);
                    en.SetValByKey("XiangMuFuZeRen", xmdr["XiangMuFuZeRen"]);
                    en.SetValByKey("XiangMuFuZeRenT", xmdr["XiangMuFuZeRenT"]);
                }
                    en.Insert();
            }

            //图纸处理
            DataTable dt1Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName,1);
            string cls1 = "TS.PM.TuoZhi";
            foreach (DataRow dr in dt1Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls1);
                string? ssNo = dr["送审流水号"] as string; //送审流水号
                if (existNo.Contains(","+ ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? ZhuanYeT = dr["专业"] as string; // 专业
                string ZhuanYe = queryIndexByTable(ZhuanYeT, zhuanyes);
                en.SetValByKey("ZhuanYeT", ZhuanYeT);
                en.SetValByKey("ZhuanYe", ZhuanYe);

                string? RenYuanT = dr["审查人员"] as string; //审查人员
                string? RenYuan = dr["审查人员账号"] as string; //审查人员账号
                en.SetValByKey("RenYuanT", RenYuanT);
                en.SetValByKey("RenYuan", RenYuan);
                string? TuZhiShuLiang = dr["图纸数量"] as string; //图纸数量
                en.SetValByKey("TuZhiShuLiang", TuZhiShuLiang);
                string? XiShu = dr["系数"] as string; //系数
                en.SetValByKey("XiShu", XiShu);

                //附件
                string? xh = dr["序号"] as string; //序号
                string? TzName = dr["图纸名称"] as string; //图纸名称
                string rstr = upload_ath(zipFlodExt, "图纸", xh+"-1");
                if (!rstr.Equals(""))
                {
                    en.SetValByKey("TzName", rstr.Split(";")[0]);
                    en.SetValByKey("TzPath", rstr.Split(";")[1]);
                }
              
                string? CsName = dr["初审意见附件"] as string; //初审意见附件
                rstr = upload_ath(zipFlodExt, "图纸", xh + "-2");
                if (!rstr.Equals(""))
                {
                    en.SetValByKey("CsName", rstr.Split(";")[0]);
                    en.SetValByKey("CsPath", rstr.Split(";")[1]);
                }
                string? QrName = dr["已确认意见附件"] as string; //已确认意见附件
                rstr = upload_ath(zipFlodExt, "图纸", xh + "-3");
                if (!rstr.Equals(""))
                {
                    en.SetValByKey("QrName", rstr.Split(";")[0]);
                    en.SetValByKey("QrPath", rstr.Split(";")[1]);
                }
                en.Insert();
            }

            //轨道工作内容
            DataTable dt2Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 2);
            string cls2 = "TS.PM.WorkDtlSS";
            foreach (DataRow dr in dt2Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls2);
                string? ssNo = dr["送审流水号"] as string; //送审流水号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? JieDuanNoT = dr["阶段名称"] as string; // 阶段名称
                string JieDuanNo = queryIndexByTable(JieDuanNoT, jieduans);
                en.SetValByKey("JieDuanNoT", JieDuanNoT);
                en.SetValByKey("JieDuanNo", JieDuanNo);
                string? WorkNodeNoT = dr["工作节点"] as string; // 工作节点
                string WorkNodeNo = queryIndexByTable(WorkNodeNoT, gongzuos);
                en.SetValByKey("WorkNodeNoT", WorkNodeNoT);
                en.SetValByKey("WorkNodeNo", WorkNodeNo);
                string? BiLv = dr["进度比率"] as string; // 进度比率
                en.SetValByKey("BiLv", BiLv);
                string? NumOfAll = dr["总量"] as string; // 总量
                en.SetValByKey("NumOfAll", NumOfAll);
                string? BenCiSongShenLiang = dr["本次送审量"] as string; // 本次送审量
                en.SetValByKey("BenCiSongShenLiang", BenCiSongShenLiang);
                en.Insert();
            }

            //强条统计内容
            DataTable dt3Rows = BP.DA.DBLoad.ReadExcelFileToDataTable(fullName, 3);
            string cls3 = "TS.PM.QiangTiaoSS";
            foreach (DataRow dr in dt3Rows.Rows)
            {
                TSEntityMyPK en = new TSEntityMyPK(cls3);
                string? ssNo = dr["送审流水号"] as string; //送审流水号
                if (existNo.Contains("," + ssNo + ","))
                {
                    continue;
                }
                en.SetValByKey("PrjNo", ssNo);
                string? YueFen = dr["月份"] as string; // 月份
                en.SetValByKey("YueFen", YueFen);
                string? SQL_ZhuanYeT = dr["专业"] as string; // 专业
                string SQL_ZhuanYe = queryIndexByTable(SQL_ZhuanYeT, zhuanyes);
                en.SetValByKey("SQL_ZhuanYeT", SQL_ZhuanYeT);
                en.SetValByKey("SQL_ZhuanYe", SQL_ZhuanYe);
                string? GongChengMingChen = dr["工程名称"] as string; // 工程名称
                en.SetValByKey("GongChengMingChen", GongChengMingChen);
                string? QiangTiaoLeiXing = dr["强条类型"] as string; // 强条类型
                int qtlxindex = queryIndex(QiangTiaoLeiXing, qtlx);
                en.SetValByKey("QiangTiaoLeiXing", qtlxindex);
                string? QiangTiaoShuLiang = dr["强条数量"] as string; // 强条数量
                en.SetValByKey("QiangTiaoShuLiang", QiangTiaoShuLiang);
                string? SuoSheJiQiangTiaoYiJ = dr["所涉及强条意见"] as string; // 所涉及强条意见
                en.SetValByKey("SuoSheJiQiangTiaoYiJ", SuoSheJiQiangTiaoYiJ);

                en.Insert();
            }
            return msg;
        }
            
        //项目立项导入
        public string Prj_Import(string filePath)
        {
            DataTable dtRows = BP.DA.DBLoad.ReadExcelFileToDataTable(filePath);
            string cls = "TS.PM.Prj";
            string msg = "";
            string[] xmlxs = new string[] { "房建", "市政", "燃气", "轨道", "敞口" };
            string[] zxmlxs = new string[] { "非轨道", "轨道类", "敞口" };
            string[] status = new string[] { "履行中", "暂停" };
            string[] htdd = new string[] { "市内", "省内", "省外", "国外" };
            DataTable zhuanyes = DBAccess.RunSQLReturnTable("select * from pm_ZhuanYe");
            DataTable danweis = DBAccess.RunSQLReturnTable("select * from PM_DanWei");
            DataTable sheng = DBAccess.RunSQLReturnTable("select * from sys_area where type=1");
            DataTable shi = DBAccess.RunSQLReturnTable("select * from sys_area where type=2");
            foreach (DataRow dr in dtRows.Rows)
            {
                TSEntityNoName en = new TSEntityNoName(cls);
                en.No = dr[0].ToString();
                if (en.IsExits == true)
                {
                    msg += " err@编号:" + en.No + "," + en.Name + ",已经存在.";
                    continue;
                }

                en.Name = dr[1].ToString();
                string? PrjName = dr["项目名称"] as string; //项目名称
                en.SetValByKey("PrjName", PrjName);
                string? LiXiangShiJian = dr["立项时间"] as string; //立项时间
                en.SetValByKey("LiXiangShiJian", LiXiangShiJian);
                string? SFZXM = dr["是否子项目"] as string; //是否子项目
                SFZXM = isNoTrant(SFZXM);
                en.SetValByKey("SFZXM", SFZXM);
                string? XMLX = dr["项目类型"] as string; //项目类型
                int xmlxindex = queryIndex(XMLX, xmlxs);
                en.SetValByKey("XMLX", xmlxindex);
                string? SQL_ZhuanYeT = dr["主专业"] as string; //专业
                string SQL_ZhuanYe = queryIndexByTable(SQL_ZhuanYeT, zhuanyes);
                en.SetValByKey("SQL_ZhuanYe", SQL_ZhuanYe);
                en.SetValByKey("SQL_ZhuanYeT", SQL_ZhuanYeT);
                string? ZXMBH = dr["主项目编号"] as string; //主项目编号
                en.SetValByKey("ZXMBH", ZXMBH);
                string? JSDWMCT = dr["建设单位名称"] as string; //建设单位
                string JSDWMC = queryIndexByTable(JSDWMCT, danweis);
                en.SetValByKey("JSDWMCT", JSDWMCT);
                en.SetValByKey("JSDWMC", JSDWMC);
                string? JSDW_Linker = dr["建设单位联系人"] as string; //联系人
                en.SetValByKey("JSDW_Linker", JSDW_Linker);
                string? JSDW_Tel = dr["建设单位电话"] as string; //电话
                en.SetValByKey("JSDW_Tel", JSDW_Tel);
                string? ShouRuZiXiangMuLeiXi = dr["收入子项目类型"] as string; //收入子项目类型
                int zxmlxindex = queryIndex(ShouRuZiXiangMuLeiXi, zxmlxs);
                en.SetValByKey("ShouRuZiXiangMuLeiXi", ShouRuZiXiangMuLeiXi);
                string? prjSta = dr["状态"] as string; //状态
                 int staindex = queryIndex(prjSta, status);
                en.SetValByKey("prjSta", staindex);
                string? DJGLDWT = dr["代建管理单位名称"] as string; //代建管理单位名称
                string DJGLDW = queryIndexByTable(DJGLDWT, danweis);
                en.SetValByKey("DJGLDWT", DJGLDWT);
                en.SetValByKey("DJGLDW", DJGLDW);
                string? DJGLDW_Linker = dr["代建管理单位联系人"] as string; //联系人
                en.SetValByKey("DJGLDW_Linker", DJGLDW_Linker);
                string? DJGLDW_Tel = dr["代建管理单位电话"] as string; //电话
                en.SetValByKey("DJGLDW_Tel", DJGLDW_Tel);
                string? ZCBDWT = dr["总承包单位名称"] as string; //总承包单位名称
                string ZCBDW = queryIndexByTable(ZCBDWT, danweis);
                en.SetValByKey("ZCBDWT", ZCBDWT);
                en.SetValByKey("ZCBDW", ZCBDW);
                string? ZCBDW_Linker = dr["总承包单位联系人"] as string; //联系人
                en.SetValByKey("ZCBDW_Linker", ZCBDW_Linker);
                string? ZCBDW_Tel = dr["总承包单位电话"] as string; //电话
                en.SetValByKey("ZCBDW_Tel", ZCBDW_Tel);
                string? SJDWMCT = dr["设计单位名称"] as string; //设计单位
                string SJDWMC = queryIndexByTable(SJDWMCT, danweis);
                en.SetValByKey("SJDWMCT", SJDWMCT);
                en.SetValByKey("SJDWMC", SJDWMC);
                string? SJDW_Linker = dr["设计单位联系人"] as string; //联系人
                en.SetValByKey("SJDW_Linker", SJDW_Linker);
                string? SJDW_Tel = dr["设计单位电话"] as string; //电话
                en.SetValByKey("SJDW_Tel", SJDW_Tel);
                string? KCSWMCT = dr["勘察单位名称"] as string; //勘察单位
                string KCSWMC = queryIndexByTable(KCSWMCT, danweis);
                en.SetValByKey("KCSWMCT", KCSWMCT);
                en.SetValByKey("KCSWMC", KCSWMC);
                string? KCDW_Linker = dr["勘察单位联系人"] as string; //联系人
                en.SetValByKey("KCDW_Linker", KCDW_Linker);
                string? KCDW_Tel = dr["勘察单位电话"] as string; //电话
                en.SetValByKey("KCDW_Tel", KCDW_Tel);
                string? HeTongDiDian = dr["合同地点"] as string; //合同地点
                int htddindex = queryIndex(HeTongDiDian, htdd);
                en.SetValByKey("HeTongDiDian", htddindex);
                string? SQL_ShengT = dr["省"] as string; //省
                string SQL_Sheng = queryIndexByTable(SQL_ShengT, sheng);
                en.SetValByKey("SQL_ShengT", SQL_ShengT);
                en.SetValByKey("SQL_Sheng", SQL_Sheng);

                string? SQL_ShiT = dr["市"] as string; //市
                string SQL_Shi = queryIndexByTable(SQL_ShiT, shi);
                en.SetValByKey("SQL_ShiT", SQL_ShiT);
                en.SetValByKey("SQL_Shi", SQL_Shi);

                string? GongChengGaiKuang = dr["工程概况"] as string; //工程概况
                en.SetValByKey("GongChengGaiKuang", GongChengGaiKuang);
                string? FuZeRenT = dr["服务人员"] as string; //服务人员
                string? FuZeRen = dr["服务人员登录账号"] as string; //服务人员
                en.SetValByKey("FuZeRenT", FuZeRenT);
                en.SetValByKey("FuZeRen", FuZeRen);
                string ? XiangMuFuZeRenT = dr["项目负责人"] as string; //项目负责人
                string XiangMuFuZeRen = queryIndexByTable(XiangMuFuZeRenT, danweis);
                en.SetValByKey("XiangMuFuZeRenT", XiangMuFuZeRenT);
                en.SetValByKey("XiangMuFuZeRen", XiangMuFuZeRen);
       

                en.Insert();
            }
            return msg;
        }
        public string isNoTrant(string info)
        {
          if(info==null || info.Equals("") || info.Equals("否"))
            {
                return "0";
            }
            else
            {
                return "1";
            }
        }

        public int queryIndex(string str, string[] arr)
        {
            if(str==null || str.Equals(""))
            {
                return 0;
            }

            for (int i = 0; i < arr.Length; i++)
            {
                if (str.Equals(arr[i]))
                {
                    return i;
                }
            }
             return 0;
        }

        public string queryIndexByTable(string str, DataTable dataTable)
        {
            if (str == null || str.Equals(""))
            {
                return "0";
            }
            foreach (DataRow row in dataTable.Rows)
            {
                string name = row["Name"].ToString();
                string no = row["No"].ToString();
                if (str.Equals(name))
                {
                    return no;
                }
            }
               
            return "0";
        }
        
    
        //导入数据类型检测
     public   string PM_ImportTypeCheck()
        {
            string msg = "";
            IFormFile file = HttpContextHelper.RequestFiles(0);
            if (file == null)
            {
                return "err@没有获取到文件";
            }
            string type = this.GetRequestVal("type");
            string impType = this.GetRequestVal("impType");
            string fileName = System.IO.Path.GetFileName(file.FileName);
            //xls xlsx格式验证
            if (impType.Equals("0"))
            {
                if (!fileName.EndsWith(".xlsx"))
                {
                    return "err@必须是.xlsx文件";
                }
            }
            else  //zip格式验证
            {
                if (!fileName.EndsWith(".zip"))
                {
                    return "err@必须是.zip文件";
                }
            }

            return msg;
        }
        public string Get_ZipExcelFile(string zipFlodExt)
        {
            System.IO.DirectoryInfo folder = new System.IO.DirectoryInfo(zipFlodExt);
            //文件夹存在
            if (folder.Exists)
            {
                System.IO.DirectoryInfo dir = new System.IO.DirectoryInfo(zipFlodExt);
                FileInfo[] fls = dir.GetFiles();
                DirectoryInfo[] dirs = dir.GetDirectories();
                foreach (FileInfo file in fls)
                {
                   if(file.FullName.EndsWith(".xlsx"))
                    {
                        return file.FullName;
                    }
                }
            }
            return "";
        }
        /// <summary>
        /// 获取文件夹中文件
        /// </summary>
        /// <param name="zipFlodExt">文件夹所在目录</param>
        /// <param name="name">文件夹中的文件夹名称</param>
        /// <param name="xh">所需附件序号</param>
        /// <returns>图片名称,图片名称;存储路径,存储路径</returns>
        public string upload_ath(string zipFlodExt, string name, string xh)
        {
            System.IO.DirectoryInfo folder = new System.IO.DirectoryInfo(zipFlodExt);
            //文件夹存在
            if (folder.Exists)
            {
                System.IO.DirectoryInfo dir = new System.IO.DirectoryInfo(zipFlodExt);
                FileInfo[] fls = dir.GetFiles();
                DirectoryInfo[] dirs = dir.GetDirectories();
                foreach (DirectoryInfo d in dirs)
                {
                    if (d.Name.Equals(name))
                    {
                        System.IO.DirectoryInfo dc = new System.IO.DirectoryInfo(d.FullName);
                        FileInfo[] flsc = dc.GetFiles();
                        string rName = "";
                        string rPath = "";
                        foreach (FileInfo fc in flsc)
                        {
                            if (fc.Name.StartsWith(xh))
                            {
                                if (rName.Equals(""))
                                {
                                    rName += fc.Name;
                                    rPath += fc.FullName;
                                }
                                else
                                {
                                    rName += ","+fc.Name;
                                    rPath += "," + fc.FullName;
                                }
                            }
                        }
                        return rName + ";" + rPath;
                    }
                }
            }
            return "";
        }
        
         void Uncompress(string zipFile, string extractPath)
        {
            // 设置打开的ZIP文件的编码为GBK
            using (var zip = ZipFile.Open(zipFile, ZipArchiveMode.Read, Encoding.GetEncoding("GBK")))
            {
                foreach (var entry in zip.Entries)
                {
                    System.Console.WriteLine(entry.FullName);
                }
                zip.ExtractToDirectory(extractPath, true);
            }
        }
        string  return0ByEmpt(string info)
        {
            if(info==null || info.Equals(""))
            {
                return "0";
            }
            else
            {
                return info;
            }
        }

    }
}
