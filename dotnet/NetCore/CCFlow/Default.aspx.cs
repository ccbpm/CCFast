using System;
using System.Data;
using System.Configuration;
using System.Collections;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Web.UI.HtmlControls;
using System.Collections.Concurrent;
using System.IO;
using Newtonsoft.Json;
using BP.En;
using BP.Port;
using BP.DA;
using BP.Sys;
using BP.WF;

namespace CCFlow
{
    public partial class Default : System.Web.UI.Page
    {
        /// <summary>
        /// 启动固定资产维修申请流程
        /// </summary>
        /// <param name="no"></param>
        /// <param name="name"></param>
        public void Shenqing(string no, string name)
        {
            //流程相关操作。 
            Int64 workid = BP.WF.Dev2Interface.Node_CreateBlankWork("001");

            //保存数据到内置表单.
            Hashtable ht = new Hashtable();
            ht.Add("Tel", "11111");
            ht.Add("Addr", "11111");

            //发送.
            BP.WF.Dev2Interface.Node_SaveWork("001", 101, workid, ht);

            BP.WF.Dev2Interface.Node_SendWork("001", workid, 103, "zhanasan,lisi");
            BP.WF.Dev2Interface.Node_SendWork("001", workid, 0, "zhanasan,lisi");

        }
        public static string GenerAllInfoByWorkID(Int64 workID)
        {

            //组织结构
            BP.WF.Dev2Interface.Port_Login("zhoupeng");
            BP.WF.Dev2Interface.Port_LoginBySID("ssxxxx-s--sdsd-sd-sds-s-ddsss");

            BP.WF.Dev2Interface.Port_SigOut();  //退出方法.

            //执发送. 一般发送.
            /// BP.WF.Dev2Interface.Node_SendWork("001", workid, ht);


            //   BP.WF.Dev2Interface.Port_GenerSID("ZHOUPENG");
            //定义容器.
            DataSet ds = new DataSet();

            //工作流引擎表.
            GenerWorkFlow gwf = new GenerWorkFlow(workID);
            ds.Tables.Add(gwf.ToDataTableField("WF_GenerWorkFlow"));

            //工作流人员表.
            GenerWorkerLists gwls = new GenerWorkerLists();
            gwls.Retrieve(GenerWorkerListAttr.WorkID, workID, GenerWorkerListAttr.RDT);
            ds.Tables.Add(gwf.ToDataTableField("WF_GenerWorkerList"));

            //轨迹表
            string sql = "SELECT * FROM ND" + int.Parse(gwf.FK_Flow) + "Track WHERE WorkID=" + workID;
            DataTable dt = DBAccess.RunSQLReturnTable(sql);
            ds.Tables.Add(dt);

            //节点绑定的表单.
            BP.WF.Template.FrmNodes fns = new BP.WF.Template.FrmNodes();
            fns.Retrieve(BP.WF.Template.FrmNodeAttr.FK_Node, gwf.FK_Node);

            foreach (BP.WF.Template.FrmNode item in fns)
            {
                //    MapData md = new MapData(item.FK_Frm);
                GEEntity ge = new GEEntity(item.FK_Frm, workID);
                ds.Tables.Add(ge.ToDataTableField(item.FK_Frm));
            }
            return BP.Tools.Json.ToJson(ds);
        }
        protected void Page_Load(object sender, EventArgs e)
        {

            if (BP.Sys.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
            {
                string url1 = "/App/GotoUrl.htm?OrgNo=f5de&UserNo=18660153394&Token=81b01dc0-5223-4f05-bfda-b35bd2f78e98";
                this.Response.Redirect(url1);
            }
            else
            {
                string url12 = "/WF/Portal/Login.htm";
                this.Response.Redirect(url12);
            }
            return;


            //让管理员登录.
            BP.WF.Dev2Interface.Port_Login("zhangsan");

            //创建workid.
            Int64 workid = BP.WF.Dev2Interface.Node_CreateBlankWork("001");

            //执行发送.
            BP.WF.Dev2Interface.Node_SendWork("001", workid);

            //发送到指定的节点.
            BP.WF.Dev2Interface.Node_SendWork("001", workid, null, 105, null);

            //发送到指定的节点，指定的人员。
            BP.WF.Dev2Interface.Node_SendWork("001", workid, null, 105, "lisi,wangwu");

            //让李四登录.
            BP.WF.Dev2Interface.Port_Login("lisi");

            //退回接口.
            BP.WF.Dev2Interface.Node_ReturnWork(workid, 102, "lisi", "请假天数太久，项目太忙，不允许。", false);

            //执行流程结束..
            BP.WF.Dev2Interface.Flow_DoFlowOver(workid, "我要结束流程");

            //执行删除流程.
            BP.WF.Dev2Interface.Flow_DoDeleteFlowByReal(workid);



            // BP.WF.Dev2Interface.Node_SendWork("001", 100, ht, 0, null);

            // BP.DA.DataType.AppDate
            //Hashtable ht = new Hashtable();
            // ht.Add("TianShu", 100);
            // BP.WF.Dev2Interface.Node_SendWork("001", 100, ht, 0, null);

            // BP.WF.Dev2Interface.Node_SendWork("001", 4444, 104, "zhangsan,lisi");

            // BP.WF.Dev2Interface.Node_SendWork("001", 4444, 0, "zhangsan,lisi");

            // BP.WF.Dev2Interface.WriteTrack();

            // BP.Sys.MapData md = new MapData();
            // md.DoCopy();

            if (BP.Sys.SystemConfig.CCBPMRunModel == CCBPMRunModel.SAAS)
            {
                string url1 = "/App/GotoUrl.htm?OrgNo=f5de&UserNo=18660153394&Token=81b01dc0-5223-4f05-bfda-b35bd2f78e98";
                this.Response.Redirect(url1);
            }
            else
            {
                string url12 = "/WF/Portal/Login.htm?OrgNo=f5de&UserNo=18660153394&Token=81b01dc0-5223-4f05-bfda-b35bd2f78e98";
                this.Response.Redirect(url12);
            }
            return;

            // 手工的启动一个流程.
            // 创建workid.
            Int64 workid1 = BP.WF.Dev2Interface.Node_CreateBlankWork("023");

            // 预制开始节点数据.
            Hashtable ht = new Hashtable();
            ht.Add("DTFrom", "2021-09-01");
            ht.Add("DTTo", "2021-10-01");

            BP.WF.Dev2Interface.Node_SaveWork("001", 101, workid1, ht);

            //打开表单.
            string url = "/WF/MyFlow.htm?WorkID=" + workid + "&FK_Flow=001";
        }

        public void TestCash()
        {
            /* BP.WF.Node nd = new Node(101);
             string html = "";
             foreach (Attr item in nd.EnMap.Attrs)
             {
                 html += "@" + item.Key + " - " + nd.GetValStrByKey(item.Key);
             }
             this.Response.Write(html);
             nd.Name = "abc123";
             nd.Update();

             html = "<hr>";
             foreach (Attr item in nd.EnMap.Attrs)
             {
                 html += "@" + item.Key + " - " + nd.GetValStrByKey(item.Key);
             }
             this.Response.Write(html);*/
        }
    }
}