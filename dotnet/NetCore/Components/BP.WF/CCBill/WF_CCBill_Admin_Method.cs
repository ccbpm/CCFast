using BP.DA;
using BP.Sys;
using BP.En;
using BP.WF;
using BP.WF.HttpHandler;
using BP.CCBill.Template;
using System;
using System.Data;

namespace BP.CCBill
{
    /// <summary>
    /// 页面功能实体
    /// </summary>
    public class WF_CCBill_Admin_Method : DirectoryPageBase
    {
        #region 属性.
        /// <summary>
        /// 模块编号
        /// </summary>
        public string ModuleNo
        {
            get
            {
                string str = this.GetRequestVal("ModuleNo");
                return str;
            }
        }
        /// <summary>
        /// 菜单ID.
        /// </summary>
        public string MenuNo
        {
            get
            {
                string str = this.GetRequestVal("MenuNo");
                return str;
            }
        }

        public string GroupID
        {
            get
            {
                string str = this.GetRequestVal("GroupID");
                return str;
            }
        }

        #endregion 属性.


        public string Bill_Save()
        {
            string fromFrmID = this.GetRequestVal("DictFrmID");
            string toFrmID = this.GetRequestVal("BillFrmID");

            //这里仅仅复制主表的字段.
            MapAttrs attrsFrom = new MapAttrs();
            attrsFrom.Retrieve(MapAttrAttr.FK_MapData, fromFrmID);

            GroupField gf = new GroupField();
            gf.FrmID = toFrmID;
            gf.Lab = "基础信息.";
            gf.Insert();

            foreach (MapAttr attr in attrsFrom)
            {
                if (attr.IsExit(MapAttrAttr.FK_MapData, toFrmID, MapAttrAttr.KeyOfEn, attr.KeyOfEn) == true)
                    continue;

                attr.setFrmID(toFrmID);
                attr.setMyPK(attr.FrmID + "_" + attr.KeyOfEn);
                attr.GroupID = gf.OID;
                attr.Insert();
            }

            FrmBill fd = new FrmBill(toFrmID);
            fd.CheckEnityTypeAttrsFor_Bill();

            ////设置关联字段.
            //MapAttr attrRef = new MapAttr();
            //attrRef.setMyPK(toFrmID + "_RefPK");
            //attrRef.FrmID = toFrmID;
            //attrRef.setName("关联单据字段");
            //attrRef.setKeyOfEn("RefPK");
            //attrRef.setUIVisible(false);
            //attrRef.Insert();

            return "复制成功.";

            ////如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
            //BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp handlerFrm = new WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp();
            ////   handler.AddPara
            //handlerFrm.Imp_CopyFrm(toFrmID, fromFrmID);

            //return "复制成功.";
        }
        /// <summary>
        /// 构造函数
        /// </summary>
        public WF_CCBill_Admin_Method()
        {

        }
        /// <summary>
        /// 获得指定单据的字段属性.
        /// </summary>
        /// <returns></returns>
        public string GPN_Menthd_RefBill_BillAttrs()
        {
            string attrSQL = " SELECT MyPK AS No, Name, GroupID FROM Sys_MapAttr WHERE FK_MapData='" + this.FrmID + "' AND UIContralType <= 4 ";
            attrSQL += " AND KeyOfEn NOT IN('OID','Rec','RDT','FID','Title','BillNo','BillState','FlowStarter','FlowEmps','FlowStartRDT','WFState','Emps') AND UIVisible = 1 ORDER BY GroupID,Idx ";
            DataTable dt = DBAccess.RunSQLReturnTable(attrSQL);
            return BP.Tools.Json.ToJson(dt);
        }
        /// <summary>
        /// 其他业务流程
        /// </summary>
        /// <returns></returns>
        public string FlowEtc_Save()
        {
            //当前表单的信息
            MapData mapData = new MapData(this.FrmID);

            string flowModel= this.GetRequestVal("FlowModel");

            #region 第1步: 创建一个流程.
            //首先创建流程. 参数都通过 httrp传入了。
            BP.WF.HttpHandler.WF_Admin_CCBPMDesigner_FlowDevModel handler = new WF_Admin_CCBPMDesigner_FlowDevModel();
            string flowNo = handler.FlowDevModel_Save();

            //执行更新. 设置为不能独立启动.
            BP.WF.Flow fl = new BP.WF.Flow(flowNo);
            fl.ItIsCanStart = false;
            fl.Update();

            //更新开始节点.
            BP.WF.Node nd = new BP.WF.Node(int.Parse(flowNo + "01"));
            nd.Name = this.Name;
            if (mapData.HisFrmType == FrmType.Develop)
            {
                nd.FormType = NodeFormType.Develop;
                MapData map = new MapData(nd.NodeFrmID);
                map.HisFrmType = FrmType.Develop;
                map.Update();
            }
            nd.Update();

            #endregion 创建一个流程.

            #region 第2步 把表单导入到流程上去.
            //如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
            BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp handlerFrm = new BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp();
            handlerFrm.Imp_CopyFrm("ND" + int.Parse(flowNo + "01"), this.FrmID);

            //把Title,BillNo隐藏
            String newFrmID = "ND" + int.Parse(flowNo + "01");
            MapAttr mapAttrT = new MapAttr(newFrmID, "Title");

            MapAttr mapAttrB = new MapAttr(newFrmID, "BillNo");

            //增加DictNo,DictName
            MapAttr mapAttr = new BP.Sys.MapAttr();
            mapAttr.setFrmID(newFrmID);
            mapAttr.setEditType(EditType.UnDel);
            mapAttr.setKeyOfEn("DictNo");
            mapAttr.setName(mapAttrB.Name); // "流程标题";
            mapAttr.setMyDataType(DataType.AppString);
            mapAttr.setUIContralType(UIContralType.TB);
            mapAttr.setLGType(FieldTypeS.Normal);
            mapAttr.setUIVisible(true);
            mapAttr.setUIIsEnable(false);
            mapAttr.UIIsLine = mapAttrB.UIIsLine;
            mapAttr.UIWidth = 251;
            mapAttr.setMinLen(0);
            mapAttr.setMaxLen(200);
            mapAttr.setIdx(-100);
            mapAttr.Insert();

            mapAttr = new BP.Sys.MapAttr();
            mapAttr.setFrmID(newFrmID);
            mapAttr.setEditType(EditType.UnDel);
            mapAttr.setKeyOfEn("DictName");
            mapAttr.setName(mapAttrT.Name); // "流程标题";
            mapAttr.setMyDataType(DataType.AppString);
            mapAttr.setUIContralType(UIContralType.TB);
            mapAttr.setLGType(FieldTypeS.Normal);
            mapAttr.setUIVisible(true);
            mapAttr.setUIIsEnable(false);
            mapAttr.UIIsLine = mapAttrT.UIIsLine;
            mapAttr.UIWidth = 251;
            mapAttr.setMinLen(0);
            mapAttr.setMaxLen(200);
            mapAttr.setIdx(-100);
            mapAttr.Insert();

            mapAttrT.UIVisible = false;
            mapAttrT.Name = "标题";
            mapAttrT.Update();

            mapAttrB.UIVisible = false;
            mapAttrB.Name = "单据编号";
            mapAttrB.Update();

            #endregion 把表单导入到流程上去.

            #region 增加单次流程.
            if (flowModel.Equals("FlowSingle") == true)
            {
                mapAttr.setFrmID(this.FrmID);
                mapAttr.KeyOfEn = "WFState" + flowNo;
                mapAttr.Name = "状态";
                mapAttr.setUIBindKey("FlowSingleState"); //未审核,审核中,成功完成,中间完成.
                mapAttr.UIContralType= UIContralType.DDL;
                mapAttr.setUIVisible(false);
                mapAttr.setLGType(FieldTypeS.Enum);
                mapAttr.setMyDataType(DataType.AppInt);
                mapAttr.Insert();

                mapAttr.setFrmID(this.FrmID);
                mapAttr.KeyOfEn = "Stater" + flowNo;
                mapAttr.setName("发起人");
                mapAttr.setUIContralType( UIContralType.TB);
                mapAttr.setUIVisible(false);
                mapAttr.setMyDataType(DataType.AppString);
                mapAttr.Insert();

                mapAttr.setFrmID(this.FrmID);
                mapAttr.KeyOfEn = "StaterName" + flowNo;
                mapAttr.setName("发起人名称");
                mapAttr.setUIContralType(UIContralType.TB);
                mapAttr.setUIVisible(false);
                mapAttr.setMyDataType(DataType.AppString);
                mapAttr.Insert();

                mapAttr.setFrmID(this.FrmID);
                mapAttr.KeyOfEn = "RDT" + flowNo;
                mapAttr.setName("发起日期");
                mapAttr.setUIContralType(UIContralType.TB);
                mapAttr.setUIVisible(false);
                mapAttr.setMyDataType(DataType.AppDateTime);
                mapAttr.Insert();

                mapAttr.setFrmID(this.FrmID);
                mapAttr.KeyOfEn = "FlowEndNode" + flowNo;
                mapAttr.setName("停留节点");
                mapAttr.setUIContralType(UIContralType.TB);
                mapAttr.setUIVisible(false);
                mapAttr.setMyDataType(DataType.AppInt);
                mapAttr.Insert();

                mapAttr.setFrmID(this.FrmID);
                mapAttr.KeyOfEn = "FlowEndNodeName" + flowNo;
                mapAttr.setName("节点名称");
                mapAttr.setUIContralType(UIContralType.TB);
                mapAttr.setUIVisible(false);
                mapAttr.setMyDataType(DataType.AppString);
                mapAttr.Insert();
            }
            #endregion 增加单次流程.

            //创建方法.
            BP.CCBill.Template.Method en = new BP.CCBill.Template.Method();
            en.FrmID = this.FrmID;
            en.No = en.FrmID + "_" + flowNo;
            en.Name = this.Name;
            en.GroupID = this.GroupID; //分组的编号.
            en.FlowNo = flowNo;
            en.Icon = "icon-paper-plane";
            en.RefMethodType = RefMethodType.LinkeWinOpen; // = 1;
            en.MethodModel = MethodModelClass.FlowEtc; //类型.
            en.Mark = "Search"; //发起流程.
            en.Tag1 = flowNo; //标记为空.
            en.MethodID = flowNo; // 就是流程编号.
            en.FlowNo = flowNo;
            //设置类型.
                en.SetPara("EnName", "TS.CCBill.Method"+flowModel); //实体类设置不同的属性.
            en.Insert();

            //返回方法编号。
            return en.No;
        }
        /// <summary>
        /// 创建基础信息变更流程
        /// </summary>
        /// <returns></returns>
        public string FlowBaseData_Save()
        {

            #region 第1步: 创建一个流程.
            //首先创建流程. 参数都通过 httrp传入了。
            BP.WF.HttpHandler.WF_Admin_CCBPMDesigner_FlowDevModel handler = new WF_Admin_CCBPMDesigner_FlowDevModel();
            string flowNo = handler.FlowDevModel_Save();

            //执行更新. 设置为不能独立启动.
            BP.WF.Flow fl = new BP.WF.Flow(flowNo);
            fl.ItIsCanStart = false;
            fl.Update();

            //更新开始节点.
            BP.WF.Node nd = new BP.WF.Node(int.Parse(flowNo + "01"));
            nd.Name = this.Name;

            nd.Update();

            #endregion 创建一个流程.

            #region 第2步 把表单导入到流程上去.
            //如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
            BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp handlerFrm = new BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp();

            handlerFrm.Imp_CopyFrm("ND" + int.Parse(flowNo + "01"), this.FrmID);


            #endregion 把表单导入到流程上去.

            #region 第3步： 处理流程的业务表单 - 字段增加一个影子字段.
            //处理字段数据.增加一个列.
            string frmID = "ND" + int.Parse(fl.No + "01");
            MapData md = new MapData(frmID);
            if (md.TableCol != 0)
            {
                md.TableCol = 0; //设置为4列.
                md.Update();
            }

            //查询出来数据.
            MapAttrs mattrs = new MapAttrs(md.No);
            GroupFields gfs = new GroupFields(md.No);

            //遍历分组.
            foreach (GroupField gs in gfs)
            {
                //遍历字段.
                int idx = 0;
                foreach (MapAttr mapAttr in mattrs)
                {
                    if (gs.OID != mapAttr.GroupID)
                        continue;

                    //是否包含，系统字段？
                    if (BP.WF.Glo.FlowFields.Contains("," + mapAttr.KeyOfEn + ",") == true)
                        continue;

                    //其他类型的控件，就排除.
                    if ((int)mapAttr.UIContralType >= 5)
                        continue;

                    if (mapAttr.UIVisible == false)
                        continue;

                    idx++;
                    idx++;
                    mapAttr.setIdx(idx);
                    mapAttr.Update();
                    //  DBAccess.RunSQL("UP")

                    //复制一个影子字段.
                    mapAttr.setKeyOfEn("bak" + mapAttr.KeyOfEn);
                    mapAttr.setName("(原)" + mapAttr.Name);

                    mapAttr.setMyPK(mapAttr.FrmID + "_" + mapAttr.KeyOfEn);
                    mapAttr.setUIIsEnable(false);
                    mapAttr.setIdx(idx - 1);
                    mapAttr.DirectInsert();
                }
            }
            #endregion 处理流程的业务表单 - 字段增加一个影子字段..

            //创建方法.
            BP.CCBill.Template.Method en = new BP.CCBill.Template.Method();
            en.FrmID = this.FrmID;
            en.No = en.FrmID + "_" + flowNo;
            en.Name = this.Name;
            en.GroupID = this.GroupID; //分组的编号.
            en.FlowNo = flowNo;
            en.Icon = "icon-paper-plane";
            en.RefMethodType = RefMethodType.LinkeWinOpen; // = 1;
            en.MethodModel = MethodModelClass.FlowBaseData; //类型.
            en.Mark = "Search"; //发起流程.
            en.Tag1 = flowNo; //标记为空.
            en.MethodID = flowNo; // 就是流程编号.

            en.FlowNo = flowNo;
            en.SetPara("EnName", "TS.CCBill.MethodFlowBaseData");
            en.Insert();

            ////创建查询菜单.放入到与该实体平行的位置.
            //BP.CCFast.CCMenu.Menu menu = new BP.CCFast.CCMenu.Menu();
            //menu.ModuleNo = this.ModuleNo; //隶属与实体一个模块.
            //menu.Name = this.Name;
            //menu.Idx = 0;
            //menu.MenuModel = "FlowBaseData"; //修改基础数据流程.
            //menu.Mark = "Search"; //流程查询.
            //menu.Tag1 = flowNo; //流程编号.
            //menu.No = this.FrmID + "_" + flowNo;
            //menu.Icon = "icon-paper-plane";
            //menu.Insert();


            //返回方法编号。
            return en.No;

            // 第4步: 创建实体分组的方法.
            //   CrateFlowMenu_4_GroupMethod(MethodModelClass.FlowBaseData, flowNo);
        }


        /// <summary>
        /// 创建方法分组.
        /// </summary>
        /// <param name="menuModel"></param>
        /// <param name="flowNo"></param>
        private void CrateFlowMenu_4_GroupMethod(string menuModel, string flowNo)
        {
            #region 第4步: 创建实体分组/方法.

            GroupMethod gm = new GroupMethod();
            gm.Name = this.Name;
            gm.FrmID = this.FrmID;
            gm.MethodType = menuModel; //类型.
            gm.MethodID = flowNo;
            gm.Insert();


            //创建 - 方法
            BP.CCBill.Template.Method en = new BP.CCBill.Template.Method();
            en.FrmID = this.FrmID;
            en.No = en.FrmID + "_" + flowNo;
            en.Name = this.Name;
            en.GroupID = gm.No; //分组的编号.
            en.FlowNo = flowNo;
            en.Icon = "icon-paper-plane";

            en.RefMethodType = RefMethodType.LinkeWinOpen; // = 1;
            en.MethodModel = menuModel; //类型.
            en.Mark = "StartFlow"; //发起流程.
            en.Tag1 = flowNo; //标记为空.
            en.MethodID = flowNo; // 就是流程编号.
            en.FlowNo = flowNo;
            en.Insert();

            // 增加内置流程方法:发起查询.
            en.Name = "流程查询";
            en.Icon = "icon-grid";

            en.MethodModel = menuModel; //类型.
            en.Mark = "Search"; //流程查询.
            en.Tag1 = flowNo; //标记为空.
            en.MethodID = flowNo; // 就是流程编号.
            en.FlowNo = flowNo;
            en.No = DBAccess.GenerGUID();
            en.Insert();


            //// 增加内置流程方法:流程分析.
            //en.Name = "流程分析";
            //en.Icon = "icon-chart";
            //en.MethodModel = menuModel; //类型.
            //en.Mark = "Group"; //流程分析.
            //en.Tag1 = flowNo; //标记为空.
            //en.MethodID = flowNo; // 就是流程编号.
            //en.FlowNo = flowNo;
            //en.No = DBAccess.GenerGUID();
            //en.Insert();
            #endregion 第4步 创建方法.
        }
        /// <summary>
        /// 创建菜单分组
        /// </summary>
        /// <param name="menuModel"></param>
        /// <param name="flowNo"></param>
        private void CrateFlow_5_Module(string menuModel, string flowNo)
        {
            #region 第5步: 创建菜单目录与菜单-分组
            //创建该模块下的 菜单:分组.
            BP.CCFast.CCMenu.Module mmodule = new BP.CCFast.CCMenu.Module();
            mmodule.Name = this.Name;
            mmodule.SystemNo = this.GetRequestVal("SortNo"); // md.FormTreeNo; //设置类别.
            mmodule.Idx = 100;
            mmodule.Insert();

            //创建菜单.
            BP.CCFast.CCMenu.Menu menu = new BP.CCFast.CCMenu.Menu();

            //流程查询.
            menu = new BP.CCFast.CCMenu.Menu();
            menu.ModuleNo = mmodule.No;
            menu.Name = "发起流程";
            menu.Idx = 0;

            menu.MenuModel = menuModel; //模式.
            menu.Mark = "StartFlow"; //发起流程.
            menu.Tag1 = flowNo; //流程编号.
            menu.UrlExt = "../MyFlow.htm?FK_Flow=" + flowNo;
            menu.No = this.FrmID + "_" + flowNo;
            menu.Icon = "icon-paper-plane";
            menu.Insert();

            //待办.
            menu = new BP.CCFast.CCMenu.Menu();
            menu.ModuleNo = mmodule.No;
            menu.Name = "待办";

            menu.MenuModel = menuModel;
            menu.Mark = "Todolist";
            menu.Tag1 = flowNo;
            menu.UrlExt = "../Todolist.htm?FK_Flow=" + flowNo;
            menu.Icon = "icon-bell";
            menu.Idx = 1;
            menu.Insert();

            //未完成.
            menu = new BP.CCFast.CCMenu.Menu();
            menu.MenuModel = menuModel; //模式.
            menu.ModuleNo = mmodule.No;
            menu.Name = "未完成(在途)";
            menu.Mark = "Runing"; //未完成.
            menu.Tag1 = flowNo; //流程编号.
            menu.Idx = 2;
            menu.UrlExt = "../Runing.htm?FK_Flow=" + flowNo;
            menu.Icon = "icon-clock";
            menu.Insert();

            //流程查询.
            menu = new BP.CCFast.CCMenu.Menu();
            menu.ModuleNo = mmodule.No;
            menu.Name = "流程查询";

            menu.MenuModel = menuModel; //模式.
            menu.Mark = "FlowSearch"; //流程查询.
            menu.Tag1 = flowNo; //流程编号.
            menu.UrlExt = "/App/OneFlow/RptSearch.htm?FK_Flow=" + flowNo;
            menu.Idx = 3;
            menu.Icon = "icon-magnifier";
            menu.Insert();

            //流程查询.
            menu = new BP.CCFast.CCMenu.Menu();
            menu.MenuModel = menuModel; //模式.
            menu.ModuleNo = mmodule.No;
            menu.Name = "流程分析";

            menu.MenuModel = menuModel; //模式.
            menu.Mark = "FlowGroup"; //流程查询.
            menu.Tag1 = flowNo; //流程编号.
            menu.Idx = 4;
            menu.UrlExt = "/App/OneFlow/RptGroup.htm?FK_Flow=" + flowNo;
            menu.Icon = "icon-chart";
            menu.Insert();
            #endregion 第5步 创建目录.
        }

    }
}
