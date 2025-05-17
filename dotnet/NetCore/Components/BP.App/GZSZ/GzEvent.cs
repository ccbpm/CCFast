using System;
using System.Data;
using BP.CCFast.Portal.WindowExt;
using BP.DA;
using BP.En;
using BP.MES;
using BP.Sys;
using BP.Web;
using BP.WF;
using NPOI.SS.Formula.Functions;
using System.Collections.Generic;
using ThoughtWorks.QRCode.Geom;
using BP.CCOA.KnowledgeManagement;
using Org.BouncyCastle.Utilities;

namespace BP.App.NetCore.GZSZ
{

    public class GzEvent : Sys.Base.EventBase
    {
        public GzEvent()
        {
            Title = "广州市政相关";
        }
        public override void Do()
        {
            GenerWorkFlow gwf = new GenerWorkFlow(WorkID);
            this.SucessInfo = "";
            switch (EventSource)
            {
                case EventListNode.SendSuccess://发送成功后
                    DoEventBaseSendSuccess(gwf);
                    break;
                case EventListNode.UndoneAfter://撤销发送之后
                    break;
                case EventListNode.SendWhen://发送前
                    DoEventBaseSendWhen(gwf);
                    break;
                case EventListNode.ReturnAfter://退回后
                    break;
                case EventListFlow.FlowOverAfter://流程结束后
                    break;
                default:
                    break;
            }
        }
        private void DoEventBaseSendWhen(GenerWorkFlow gwf)
        {
            if (gwf.NodeID == 201)
            {
                GERpt rpt = new GERpt("ND2Rpt");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                double HeTongJinE = rpt.GetValDoubleByKey("HeTongJinE");

                //合同收费条款,阶段占比不能超出100%，预算收入之和等于合同金额
                GEDtls dtls = new GEDtls("ND201HeTongShouFeiTiaoKua");
                dtls.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                double JieDuanZhanBis = 0;
                double YuQiShouRus = 0;
                foreach (GEDtl dtl in dtls)
                {
                    //阶段占比
                    double JieDuanZhanBi = dtl.GetValDoubleByKey("JieDuanZhanBi");
                    JieDuanZhanBis = JieDuanZhanBis + JieDuanZhanBi;
                    //预期收入
                    double YuQiShouRu = dtl.GetValDoubleByKey("YuQiShouRu");
                    YuQiShouRus = YuQiShouRus + YuQiShouRu;

                }
                if (JieDuanZhanBis > 100)
                {
                    this.SucessInfo = "err@收费条款 阶段占比：" + JieDuanZhanBis + "% 不能大于100%";
                    return;
                }
                if (YuQiShouRus != HeTongJinE)
                {
                    this.SucessInfo = "err@收费条款 预算收入之和：" + YuQiShouRus + " 必须等于合同金额："+ HeTongJinE;
                    return;
                }
               
                 //获取联合体从表数据 我司代收金额要小于联合体金额
                 GEDtls dtlLHT = new GEDtls("ND201LianHeTi");
                 dtlLHT.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                 foreach (GEDtl dtl in dtlLHT)
                    {
                        if (dtl.GetValDoubleByKey("WoSiDaiShouJinE") > dtl.GetValDoubleByKey("LianHeTiJinE"))
                        {
                            this.SucessInfo = "err@我司代收金额" + dtl.GetValDoubleByKey("WoSiDaiShouJinE") + "要小于等于联合体金额" + dtl.GetValDoubleByKey("LianHeTiJinE");
                            return;
                        }
                    }


            }
            if (gwf.NodeID == 701)
            {

                //本次收款金额不能大于未收款
                GEDtls dtls = new GEDtls("ND701ShouKuanMingXi");
                dtls.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                foreach (GEDtl dtl in dtls)
                {
                    string FaPiaoHaoMa = dtl.GetValStrByKey("FaPiaoHaoMa");
                    double WeiShouKuan = dtl.GetValDoubleByKey("WeiShouKuan");
                    double BenCiShouKuan = dtl.GetValDoubleByKey("BenCiShouKuan");
                    if(BenCiShouKuan> WeiShouKuan)
                    {
                        //throw new Exception("@发票号码："+ FaPiaoHaoMa+"本次收款金额不能大于未收款");
                        this.SucessInfo = "err@发票号码：" + FaPiaoHaoMa + "本次收款金额不能大于未收款";
                        return;
                    }

                }

                //是否代收代付性质选择是时，属于我方金额+本次拨付金额=收款金额
                GERpt rpt = new GERpt("ND7Rpt");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                Int64 ShiFouDaiShouDaiFuXi = rpt.GetValInt64ByKey("ShiFouDaiShouDaiFuXi");
                double ShouKuanJinE = rpt.GetValDoubleByKey("ShouKuanJinE");
                double BenCiShouRuJinEYuan = rpt.GetValDoubleByKey("BenCiShouRuJinEYuan");
                double BenCiBoFuJinEYuan = rpt.GetValDoubleByKey("BenCiBoFuJinEYuan");
                if (ShiFouDaiShouDaiFuXi == 1 && ShouKuanJinE != (BenCiShouRuJinEYuan + BenCiBoFuJinEYuan))
                {
                    //throw new Exception("@是否代收代付性质选择是时，属于我方金额+本次拨付金额必须等于收款金额");
                    this.SucessInfo = "err@是否代收代付性质选择是时，本次收入金额+本次拨付金额必须等于收款金额";
                    return;
                }

                //是否代收代付性质选择否时，属于我方金额==收款金额   本次拨付金额=0
                if (ShiFouDaiShouDaiFuXi == 0)
                {
                    rpt.SetValByKey("BenCiShouRuJinEYuan", ShouKuanJinE);
                    rpt.SetValByKey("BenCiBoFuJinEYuan", 0);
                    rpt.Update();
                }
                //是否代收代付性质选择是时，联合体中的本次应付金额需要<=属联合体方金额
                if (ShiFouDaiShouDaiFuXi == 1)
                {
                    //联合体从表
                    GEDtls lhDtls = new GEDtls("ND701LianHeTiFangMingXi");
                    lhDtls.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                    foreach (GEDtl dtl in lhDtls)
                    {
                        double LianHeTiJinE = dtl.GetValDoubleByKey("LianHeTiJinE");
                        double BenCiYingFuJinE = dtl.GetValDoubleByKey("BenCiYingFuJinE");
                        if (BenCiYingFuJinE > LianHeTiJinE)
                        {
                            this.SucessInfo = "err@联合体本次应付金额：" + BenCiYingFuJinE + "不能大于联合体方金额:"+ LianHeTiJinE;
                            return;
                        }

                    }
                }

            }
            if(gwf.NodeID == 301)
            {
                //本次开票金额大于现合同金额减去主合同截止上一次累计已开票金额
                //获取开票申请的主表数据
                GERpt rpt = new GERpt("ND3Rpt");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                double XHTJE = rpt.GetValDoubleByKey("XHTJE");
                double ZHTLJYKPJE = rpt.GetValDoubleByKey("ZHTLJYKPJE");
                double KaiPiao = XHTJE - ZHTLJYKPJE;
                //获取开票中合同收费条款
                //ND301HeTongShouFeiTiaoKua
                //获取联合体从表数据 我司代收金额要小于联合体金额
                GEDtls dtlHTSFT = new GEDtls("ND301HeTongShouFeiTiaoKua");
                dtlHTSFT.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                double JinEs = 0;
                foreach (GEDtl dtl in dtlHTSFT)
                {
                    //本次开票金额
                    double JinE = dtl.GetValDoubleByKey("JinE");
                    JinEs = JinEs + JinE;
                }
                if (JinEs > KaiPiao)
                {
                    this.SucessInfo = "err@本次开票金额大于现合同金额减去主合同截止上一次累计已开票金额";
                    return;
                }

                //当为结算款时，本次开票金额小于等于结算金额减去主合同截止上一次累计已开票金额
                Int64 SFJSK = rpt.GetValInt64ByKey("SFJSK");  //是否结算
                double JieSuanJinE = rpt.GetValDoubleByKey("JieSuanJinE");
                double JieSuanKaiPiao = JieSuanJinE - ZHTLJYKPJE;
                if (SFJSK == 1 && JinEs > JieSuanKaiPiao)
                {
                    this.SucessInfo = "err@当为结算款时，本次开票金额小于等于结算金额减去主合同截止上一次累计已开票金额";
                    return;
                }

                //代收为是的时候 归属我方金额和归属联合体方金额这两个字段之和必须开票申请金额
                Int64 ShiFouDaiShouDaiFuXi = rpt.GetValInt64ByKey("ShiFouDaiShouDaiFuXi");
                Int64 ShiFouJieSuanKuan = rpt.GetValInt64ByKey("SFJSK");  //是否结算
                double BenCiShouRuJinE = rpt.GetValDoubleByKey("BenCiShouRuJinE");  //归属我方金额
                double BenCiBoFuJinE = rpt.GetValDoubleByKey("BenCiBoFuJinE");      //归属联合体方金额
                double KaiPiaoShenQingJinE = rpt.GetValDoubleByKey("KaiPiaoShenQingJinE");

                if (ShiFouDaiShouDaiFuXi == 1 && KaiPiaoShenQingJinE != (BenCiBoFuJinE + BenCiShouRuJinE))
                {
                    this.SucessInfo = "err@是否代收代付性质选择是时，归属我方金额+归属联合体方金额必须等于开票申请金额";
                    return;
                }

                //是否代收代付性质选择否时，本次收入金额==开票申请金额   本次拨付金额=0
                if (ShiFouDaiShouDaiFuXi == 0)
                {
                    rpt.SetValByKey("BenCiShouRuJinE", KaiPiaoShenQingJinE);
                    rpt.SetValByKey("BenCiBoFuJinE", 0);
                    rpt.Update();
                }

                //结算选中时，结算金额>=开票金额
                if (ShiFouJieSuanKuan == 1)
                {
                    double JieSuanJinES = rpt.GetValDoubleByKey("JieSuanJinE");
                    if (KaiPiaoShenQingJinE > JieSuanJinES)
                    {
                        this.SucessInfo = "err@是否结算款选择是时，结算金额需要>=开票申请金额";
                        return;
                    }
                }

            }

            if (gwf.NodeID == 304 || gwf.NodeID == 306)
            {
                //是否代收代付性质选择是时，本次收入金额+本次拨付金额=开票申请金额
                GERpt rpt = new GERpt("ND3Rpt");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                double KaiPiaoShenQingJinE = rpt.GetValDoubleByKey("KaiPiaoShenQingJinE");
              
                //本次发票金额之和必须等于开票申请金额
                GEDtls dtls = new GEDtls("ND301XiangXiFaPiaoXinXi");
                dtls.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                double FaPiaoJinEs = 0;
                foreach (GEDtl dtl in dtls)
                {
                    //本次发票金额
                    double FaPiaoJinE = dtl.GetValDoubleByKey("FaPiaoJinE");
                    FaPiaoJinEs += FaPiaoJinE;

                }
                if(KaiPiaoShenQingJinE!= FaPiaoJinEs)
                {
                    this.SucessInfo = "err@发票金额之和必须等于开票申请金额";
                    return;
                }
                

                GEDtls dtlsAll = new GEDtls("ND301XiangXiFaPiaoXinXi");
                dtlsAll.RetrieveAll(); // 根据实际情况传递参数，这里假设它返回全部数据 
                HashSet<string> invoiceNumbers = new HashSet<string>();
                // 遍历第一组数据（全部数据），检查发票编号的唯一性  
                foreach (GEDtl dtl in dtls) 
                {
                    string invoiceNumber = dtl.GetValStringByKey("FaPiaoBianHao");
                    if (!invoiceNumbers.Add(invoiceNumber))
                    {
                        this.SucessInfo = "err@@发票编号"+ invoiceNumber+"重复了！";  
                        return; // 如果已经发现问题，可以提前退出方法  
                    }
                }
               foreach (GEDtl itemB in dtls)  
                {  
                  string invoiceNumberB = itemB.GetValStringByKey("FaPiaoBianHao");  
                  string refPKB = itemB.GetValStringByKey("RefPK");  

                 foreach (GEDtl itemA in dtlsAll)  
                 {
                        string invoiceNumberA = itemA.GetValStringByKey("FaPiaoBianHao");
                        string refPKA = itemA.GetValStringByKey("RefPK");
                        if (invoiceNumberA== invoiceNumberB && refPKA != refPKB)
                    {  
                          this.SucessInfo = "err@发票编号"+ invoiceNumberA+"已存在";  
                          return; // 如果已经发现问题，可以提前退出方法  
                    }  
                  }  
                }  
            }

            if (gwf.NodeID == 1301)
            {
                GERpt rpt = new GERpt("ND1301");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                string XMLX= rpt.GetValStringByKey("XMLX");
                //非轨道
                if (!XMLX.Equals("3"))
                {
                    //已完成的设计总量
                    double YWCSJ = rpt.GetValDoubleByKey("YWCSJ");
                    //已完成勘察总量
                    double YWCKC = rpt.GetValDoubleByKey("YWCKC");
                    //变更后设计总量
                    double BGHSJZL = rpt.GetValDoubleByKey("BGHSJZL");
                    //变更后勘察总量
                    double BGHKCZL = rpt.GetValDoubleByKey("BGHKCZL");
                    //要求变更后的大于等于已完成量
                    if (BGHSJZL < YWCSJ)
                    {
                        this.SucessInfo = "err@变更后设计总量必须大于等于已完成量！";
                        return;
                    }
                    if (BGHKCZL < YWCKC)
                    {
                        this.SucessInfo = "err@变更后勘察总量必须大于等于已完成量！";
                        return;
                    }
                }
                else   //轨道
                {
                    GEDtls dtls = new GEDtls("ND1301Dtl1");
                    dtls.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                    //要求变更后的大于等于已完成量
                    foreach (GEDtl dtl in dtls)
                    {

                        //已完成的设计总量
                        double YiWanChengGeShu = dtl.GetValDoubleByKey("YiWanChengGeShu");
                        //变更后总个数
                        double BianGengHouZongGeShu = dtl.GetValDoubleByKey("BianGengHouZongGeShu");
                        if (BianGengHouZongGeShu < YiWanChengGeShu)
                        {
                            this.SucessInfo = "err@轨道信息 变更后数量："+ BianGengHouZongGeShu+" 不能小于已完成数量："+ YiWanChengGeShu;
                            return;
                        }
                    }
                }
                
            }
            if (gwf.NodeID == 1101)
            {
                GERpt rpt = new GERpt("ND1101");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                string XMLX = rpt.GetValStringByKey("XMLX");
                //轨道
                if (XMLX.Equals("3"))
                {
                    GEDtls dtls = new GEDtls("ND1101Dtl1");
                    dtls.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                    //要求总数量大于等于已完成的个数
                    foreach (GEDtl dtl in dtls)
                    {

                        //已完成的设计总量
                        double YiWanChengGeShu = dtl.GetValDoubleByKey("YiWanChengGeShu");
                        //总数量
                        double NumOfAll = dtl.GetValDoubleByKey("NumOfAll");
                        if (NumOfAll < YiWanChengGeShu)
                        {
                            this.SucessInfo = "err@轨道信息 总数量：" + NumOfAll + " 不能小于已完成数量：" + YiWanChengGeShu;
                            return;
                        }
                    }
                }
            }
            if (gwf.NodeID == 1601)
            {
                //获取结算批价审批表
                GERpt rpt = new GERpt("ND16Rpt");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                // 其中:审图费(元)
                double QiZhongShenTuFei = rpt.GetValDoubleByKey("QiZhongShenTuFei");
                //其它费用(元)
                double QiTaFeiYong = rpt.GetValDoubleByKey("QiTaFeiYong");
                //本次结算增减金额(元)
                double BenCiJieSuanZengJian = rpt.GetValDoubleByKey("BenCiJieSuanZengJian");
                //累加分包金额不能大于收入合同金额
                if (QiTaFeiYong + QiZhongShenTuFei != BenCiJieSuanZengJian)
                {
                    this.SucessInfo = "err@其中:审图费(元)+其它费用(元)不等于本次结算增减金额";
                    return;
                }
            }
            if (gwf.NodeID == 1602)
            {
                //获取结算批价审批表
                GERpt rpt = new GERpt("ND16Rpt");
                rpt.OID = gwf.WorkID;
                int count = rpt.RetrieveFromDBSources();
                double BenCiJieSuanZengJian = rpt.GetValDoubleByKey("BenCiJieSuanZengJian");
                //获取收入子项目从表
                GEDtls SRDtls = new GEDtls("ND1602ShouRuZiXiangMuChaiF");
                SRDtls.Retrieve(GEDtlAttr.RefPK, WorkID, "OID");
                double sum = 0;
                foreach ( GEDtl dtl in SRDtls)
                {
                    double BenCiJieSuanZengJianC = dtl.GetValDoubleByKey("BenCiJieSuanZengJian");
                    sum += BenCiJieSuanZengJianC;
                }
                
                if (sum != BenCiJieSuanZengJian)
                {
                    this.SucessInfo = "err@本次结算增减金额(元)与收入子项目本次结算增减金额不相等";
                    return;
                }
            }
        }
        private void DoEventBaseSendSuccess(GenerWorkFlow gwf)
        {
            if (gwf.NodeID == 104)
            {

                try
                {
                    //获取合同岗人员
                    string sql = "SELECT FK_EMP FROM Port_DeptEmpStation WHERE FK_Station='d0b2af89-1f87-4f4d-9617-7be4fb277065'";
                    string no = DBAccess.RunSQLReturnString(sql);

                    if (no == null || no.Equals(""))
                        return;

                    Port.Emp emp = new Port.Emp();
                    emp.No = no;
                    if (emp.RetrieveFromDBSources() == 0)
                    {
                        return;
                    }
                    //登录
                    Dev2Interface.Port_Login(emp.No);
                    //创建流程
                    long WorkID = Dev2Interface.Node_CreateBlankWork("002", WebUser.UserID);
                    //Dev2Interface.Flow_SaveParas(WorkID, "@WFState=1");
                    GenerWorkFlow gwf2 = new GenerWorkFlow(WorkID);
                    gwf2.WFState = WFState.Draft;
                    gwf2.Update();

                }
                catch (Exception ex)
                {
                    Dev2Interface.Port_SigOut();
                }

            }
        }
    }
}
