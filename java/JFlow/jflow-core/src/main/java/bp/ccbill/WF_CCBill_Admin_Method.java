package bp.ccbill;

import bp.ccbill.template.Method;
import bp.da.*;
import bp.sys.*;
import bp.en.*;
import bp.wf.*;
import bp.wf.httphandler.*;
import bp.ccbill.template.*;

/** 
 页面功能实体
*/
public class WF_CCBill_Admin_Method extends bp.difference.handler.DirectoryPageBase
{

		///#region 属性.
	/** 
	 模块编号
	*/
	public final String getModuleNo()
	{
		String str = this.GetRequestVal("ModuleNo");
		return str;
	}
	/** 
	 菜单ID.
	*/
	public final String getMenuNo()
	{
		String str = this.GetRequestVal("MenuNo");
		return str;
	}

	public final String getGroupID()
	{
		String str = this.GetRequestVal("GroupID");
		return str;
	}
	public final String getName()
	{
		String str = this.GetRequestVal("Name");
		return str;
	}

		///#endregion 属性.


	public final String Bill_Save() throws Exception {
		String fromFrmID = this.GetRequestVal("DictFrmID");
		String toFrmID = this.GetRequestVal("BillFrmID");


		//这里仅仅复制主表的字段.
		MapAttrs attrsFrom = new MapAttrs();
		attrsFrom.Retrieve(MapAttrAttr.FK_MapData, fromFrmID, null);
		for (MapAttr attr : attrsFrom.ToJavaList())
		{
			if (attr.IsExit(MapAttrAttr.FK_MapData, toFrmID, MapAttrAttr.KeyOfEn, attr.getKeyOfEn()) == true)
			{
				continue;
			}

			attr.setFrmID( toFrmID);
			attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
			attr.Insert();
		}
		return "复制成功.";

		////如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
		//BP.WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp handlerFrm = new WF.HttpHandler.WF_Admin_FoolFormDesigner_ImpExp();
		////   handler.AddPara
		//handlerFrm.Imp_CopyFrm(toFrmID, fromFrmID);

		//return "复制成功.";
	}

	/** 
	 构造函数
	*/
	public WF_CCBill_Admin_Method()
	{

	}
	/** 
	 其他业务流程
	 
	 @return 
	*/
	public final String FlowEtc_Save() throws Exception {
		//当前表单的信息
		MapData mapData = new MapData(this.getFrmID());


			///#region 第1步: 创建一个流程.
		//首先创建流程. 参数都通过 httrp传入了。
		WF_Admin_CCBPMDesigner_FlowDevModel handler = new WF_Admin_CCBPMDesigner_FlowDevModel();
		String flowNo = handler.FlowDevModel_Save();

		//执行更新. 设置为不能独立启动.
		Flow fl = new Flow(flowNo);
		fl.setItIsCanStart(false);
		fl.Update();

		//更新开始节点.
		Node nd = new Node(Integer.parseInt(flowNo + "01"));
		nd.setName(this.getName());
		if (mapData.getHisFrmType() == FrmType.Develop)
		{
			nd.setFormType(NodeFormType.Develop);
			MapData map = new MapData(nd.getNodeFrmID());
			map.setHisFrmType(FrmType.Develop);
			map.Update();
		}

		nd.Update();


			///#endregion 创建一个流程.


			///#region 第2步 把表单导入到流程上去.
		//如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
		WF_Admin_FoolFormDesigner_ImpExp handlerFrm = new WF_Admin_FoolFormDesigner_ImpExp();
		//   handler.AddPara
		handlerFrm.Imp_CopyFrm("ND" + Integer.parseInt(flowNo + "01"), this.getFrmID());

		//把Title,BillNo隐藏
		String newFrmID = "ND" + Integer.parseInt(flowNo + "01");
		MapAttr mapAttrT = new MapAttr(newFrmID, "Title");

		MapAttr mapAttrB = new MapAttr(newFrmID, "BillNo");

		//增加DictNo,DictName
		MapAttr mapAttr = new MapAttr();
		mapAttr.setFrmID(newFrmID);
		mapAttr.setEditType(EditType.UnDel);
		mapAttr.setKeyOfEn("DictNo");
		mapAttr.setName(mapAttrB.getName()); // "流程标题";
		mapAttr.setMyDataType(DataType.AppString);
		mapAttr.setUIContralType(UIContralType.TB);
		mapAttr.setLGType(FieldTypeS.Normal);
		mapAttr.setUIVisible(true);
		mapAttr.setUIIsEnable(false);
		mapAttr.setUIIsLine(mapAttrB.getUIIsLine());
		mapAttr.setUIWidth(251);
		mapAttr.setMinLen(0);
		mapAttr.setMaxLen(200);
		mapAttr.setIdx(-100);
		mapAttr.Insert();

		mapAttr = new MapAttr();
		mapAttr.setFrmID(newFrmID);
		mapAttr.setEditType(EditType.UnDel);
		mapAttr.setKeyOfEn("DictName");
		mapAttr.setName(mapAttrT.getName()); // "流程标题";
		mapAttr.setMyDataType(DataType.AppString);
		mapAttr.setUIContralType(UIContralType.TB);
		mapAttr.setLGType(FieldTypeS.Normal);
		mapAttr.setUIVisible(true);
		mapAttr.setUIIsEnable(false);
		mapAttr.setUIIsLine(mapAttrT.getUIIsLine());
		mapAttr.setUIWidth(251);
		mapAttr.setMinLen(0);
		mapAttr.setMaxLen(200);
		mapAttr.setIdx(-100);
		mapAttr.Insert();

		mapAttrT.setUIVisible(false);
		mapAttrT.setName("标题");
		mapAttrT.Update();

		mapAttrB.setUIVisible(false);
		mapAttrB.setName("单据编号");
		mapAttrB.Update();

        //#endregion 把表单导入到流程上去.
		//创建方法.
		Method en = new Method();
		en.setFrmID(this.getFrmID());
		en.setNo(en.getFrmID() + "_" + flowNo);
		en.setName(this.getName());
		en.setGroupID(this.getGroupID()); //分组的编号.
		en.setFlowNo(flowNo);
		en.setIcon("icon-paper-plane");
		en.setRefMethodType(RefMethodType.LinkeWinOpen); // = 1;
		en.setMethodModel(MethodModelClass.FlowEtc); //类型.
		en.setMark("Search"); //发起流程.
		en.setTag1(flowNo); //标记为空.
		en.setMethodID(flowNo); // 就是流程编号.
		en.setFlowNo(flowNo);
		en.SetPara("EnName", "TS.CCBill.MethodFlowEtc"); //新增.
		en.Insert();

	 //   //创建查询菜单.放入到与该实体平行的位置.
	 //   BP.CCFast.CCMenu.Menu menu = new BP.CCFast.CCMenu.Menu();
	 //   menu.ModuleNo = this.ModuleNo; //隶属与实体一个模块.
	 //   menu.setName(this.Name;
	 //   menu.setIdx(0;
	 ////   menu.MenuModel = "FlowEtc"; //
	 //   menu.MenuModel = MethodModelClass.FlowEtc; //其他类型的业务流程..

	 //   menu.Mark = "Search"; //流程查询.
	 //   menu.setTag1(flowNo; //流程编号.
	 //   menu.setNo(this.getFrmID() + "_" + flowNo;
	 //   menu.Icon = "icon-paper-plane";
	 //   menu.Insert();

		//返回方法编号。
		return en.getNo();


		//// 第4步: 创建实体分组的方法.
		//CrateFlowMenu_4_GroupMethod(MethodModelClass.FlowEtc, flowNo);

		////创建流程目录与流程菜单.
		//CrateFlow_5_Module(MethodModelClass.FlowEtc, flowNo);

		//return this.getFrmID() + "_" + flowNo; //返回的方法ID;
	}
	/** 
	 创建基础信息变更流程
	 
	 @return 
	*/
	public final String FlowBaseData_Save() throws Exception {


			///#region 第1步: 创建一个流程.
		//首先创建流程. 参数都通过 httrp传入了。
		WF_Admin_CCBPMDesigner_FlowDevModel handler = new WF_Admin_CCBPMDesigner_FlowDevModel();
		String flowNo = handler.FlowDevModel_Save();

		//执行更新. 设置为不能独立启动.
		Flow fl = new bp.wf.Flow(flowNo);
		fl.setItIsCanStart(false);
		fl.Update();

		//更新开始节点.
		Node nd = new Node(Integer.parseInt(flowNo + "01"));
		nd.setName(this.getName());

		nd.Update();


			///#endregion 创建一个流程.


			///#region 第2步 把表单导入到流程上去.
		//如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
		WF_Admin_FoolFormDesigner_ImpExp handlerFrm = new WF_Admin_FoolFormDesigner_ImpExp();

		handlerFrm.Imp_CopyFrm("ND" + Integer.parseInt(flowNo + "01"), this.getFrmID());



			///#endregion 把表单导入到流程上去.


			///#region 第3步： 处理流程的业务表单 - 字段增加一个影子字段.
		//处理字段数据.增加一个列.
		String frmID = "ND" + Integer.parseInt(fl.getNo() + "01");
		MapData md = new MapData(frmID);
		if (md.getTableCol() != 0)
		{
			md.setTableCol( 0); //设置为4列.
			md.Update();
		}

		//查询出来数据.
		MapAttrs mattrs = new MapAttrs(md.getNo());
		GroupFields gfs = new GroupFields(md.getNo());

		//遍历分组.
		for (GroupField gs : gfs.ToJavaList())
		{
			//遍历字段.
			int idx = 0;
			for (MapAttr mapAttr : mattrs.ToJavaList())
			{
				if (gs.getOID() != mapAttr.getGroupID())
				{
					continue;
				}

				//是否包含，系统字段？
				if (bp.wf.Glo.getFlowFields().contains("," + mapAttr.getKeyOfEn() + ",") == true)
				{
					continue;
				}

				//其他类型的控件，就排除.
				if (mapAttr.getUIContralType().getValue() >= 5)
				{
					continue;
				}

				if (mapAttr.getUIVisible() == false)
				{
					continue;
				}

				idx++;
				idx++;
				mapAttr.setIdx(idx);
				mapAttr.Update();
				//  DBAccess.RunSQL("UP")

				//复制一个影子字段.
				mapAttr.setKeyOfEn("bak" + mapAttr.getKeyOfEn());
				mapAttr.setName("(原)" + mapAttr.getName());

				mapAttr.setMyPK(mapAttr.getFrmID() + "_" + mapAttr.getKeyOfEn());
				mapAttr.setUIIsEnable(false);
				mapAttr.setIdx(idx - 1);
				mapAttr.DirectInsert();
			}
		}

			///#endregion 处理流程的业务表单 - 字段增加一个影子字段..

		//创建方法.
		Method en = new Method();
		en.setFrmID(this.getFrmID());
		en.setNo(en.getFrmID() + "_" + flowNo);
		en.setName(this.getName());
		en.setGroupID(this.getGroupID()); //分组的编号.
		en.setFlowNo(flowNo);
		en.setIcon("icon-paper-plane");
		en.setRefMethodType(RefMethodType.LinkeWinOpen); // = 1;
		en.setMethodModel(MethodModelClass.FlowBaseData); //类型.
		en.setMark("Search"); //发起流程.
		en.setTag1(flowNo); //标记为空.
		en.setMethodID(flowNo); // 就是流程编号.

		en.setFlowNo(flowNo);
		en.SetPara("EnName", "TS.CCBill.MethodFlowBaseData");
		en.Insert();

		////创建查询菜单.放入到与该实体平行的位置.
		//BP.CCFast.CCMenu.Menu menu = new BP.CCFast.CCMenu.Menu();
		//menu.ModuleNo = this.ModuleNo; //隶属与实体一个模块.
		//menu.setName(this.Name;
		//menu.setIdx(0;
		//menu.MenuModel = "FlowBaseData"; //修改基础数据流程.
		//menu.Mark = "Search"; //流程查询.
		//menu.setTag1(flowNo; //流程编号.
		//menu.setNo(this.getFrmID() + "_" + flowNo;
		//menu.Icon = "icon-paper-plane";
		//menu.Insert();


		//返回方法编号。
		return en.getNo();

		// 第4步: 创建实体分组的方法.
		//   CrateFlowMenu_4_GroupMethod(MethodModelClass.FlowBaseData, flowNo);
	}


	/** 
	 创建方法分组.
	 
	 @param menuModel
	 @param flowNo
	*/
	private void CrateFlowMenu_4_GroupMethod(String menuModel, String flowNo) throws Exception {

			///#region 第4步: 创建实体分组/方法.

		GroupMethod gm = new GroupMethod();
		gm.setName(this.getName());
		gm.setFrmID(this.getFrmID());
		gm.setMethodType(menuModel); //类型.
		gm.setMethodID(flowNo);
		gm.Insert();


		//创建 - 方法
		Method en = new Method();
		en.setFrmID(this.getFrmID());
		en.setNo(en.getFrmID() + "_" + flowNo);
		en.setName(this.getName());
		en.setGroupID(gm.getNo()); //分组的编号.
		en.setFlowNo(flowNo);
		en.setIcon("icon-paper-plane");

		en.setRefMethodType(RefMethodType.LinkeWinOpen); // = 1;
		en.setMethodModel(menuModel); //类型.
		en.setMark("StartFlow"); //发起流程.
		en.setTag1(flowNo); //标记为空.
		en.setMethodID(flowNo); // 就是流程编号.
		en.setFlowNo(flowNo);
		en.Insert();

		// 增加内置流程方法:发起查询.
		en.setName("流程查询");
		en.setIcon("icon-grid");

		en.setMethodModel(menuModel); //类型.
		en.setMark("Search"); //流程查询.
		en.setTag1(flowNo); //标记为空.
		en.setMethodID(flowNo); // 就是流程编号.
		en.setFlowNo(flowNo);
		en.setNo(DBAccess.GenerGUID(0, null, null));
		en.Insert();


		//// 增加内置流程方法:流程分析.
		//en.setName("流程分析";
		//en.Icon = "icon-chart";
		//en.MethodModel = menuModel; //类型.
		//en.Mark = "Group"; //流程分析.
		//en.setTag1(flowNo; //标记为空.
		//en.MethodID = flowNo; // 就是流程编号.
		//en.setFlowNo(flowNo);
		//en.setNo(DBAccess.GenerGUID();
		//en.Insert();

			///#endregion 第4步 创建方法.
	}
	/** 
	 创建菜单分组
	 
	 @param menuModel
	 @param flowNo
	*/
	private void CrateFlow_5_Module(String menuModel, String flowNo) throws Exception {

			///#region 第5步: 创建菜单目录与菜单-分组
		//创建该模块下的 菜单:分组.
		bp.ccfast.ccmenu.Module mmodule = new bp.ccfast.ccmenu.Module();
		mmodule.setName(this.getName());
		mmodule.setSystemNo(this.GetRequestVal("SortNo")); // md.getFormTreeNo(); //设置类别.
		mmodule.setIdx(100);
		mmodule.Insert();

		//创建菜单.
		bp.ccfast.ccmenu.Menu menu = new bp.ccfast.ccmenu.Menu();

		//流程查询.
		menu = new bp.ccfast.ccmenu.Menu();
		menu.setModuleNo(mmodule.getNo());
		menu.setName("发起流程");
		menu.setIdx(0);

		menu.setMenuModel(menuModel); //模式.
		menu.setMark("StartFlow"); //发起流程.
		menu.setTag1(flowNo); //流程编号.
		menu.setUrlExt("../MyFlow.htm?FK_Flow=" + flowNo);
		menu.setNo(this.getFrmID() + "_" + flowNo);
		menu.setIcon("icon-paper-plane");
		menu.Insert();

		//待办.
		menu = new bp.ccfast.ccmenu.Menu();
		menu.setModuleNo(mmodule.getNo());
		menu.setName("待办");

		menu.setMenuModel(menuModel);
		menu.setMark("Todolist");
		menu.setTag1(flowNo);
		menu.setUrlExt("../Todolist.htm?FK_Flow=" + flowNo);
		menu.setIcon("icon-bell");
		menu.setIdx(1);
		menu.Insert();

		//未完成.
		menu = new bp.ccfast.ccmenu.Menu();
		menu.setMenuModel(menuModel); //模式.
		menu.setModuleNo(mmodule.getNo());
		menu.setName("未完成(在途)");
		menu.setMark("Runing"); //未完成.
		menu.setTag1(flowNo); //流程编号.
		menu.setIdx(2);
		menu.setUrlExt("../Runing.htm?FK_Flow=" + flowNo);
		menu.setIcon("icon-clock");
		menu.Insert();

		//流程查询.
		menu = new bp.ccfast.ccmenu.Menu();
		menu.setModuleNo(mmodule.getNo());
		menu.setName("流程查询");

		menu.setMenuModel(menuModel); //模式.
		menu.setMark("FlowSearch"); //流程查询.
		menu.setTag1(flowNo); //流程编号.
		menu.setUrlExt("/App/OneFlow/RptSearch.htm?FK_Flow=" + flowNo);
		menu.setIdx(3);
		menu.setIcon("icon-magnifier");
		menu.Insert();

		//流程查询.
		menu = new bp.ccfast.ccmenu.Menu();
		menu.setMenuModel(menuModel); //模式.
		menu.setModuleNo(mmodule.getNo());
		menu.setName("流程分析");

		menu.setMenuModel(menuModel); //模式.
		menu.setMark("FlowGroup"); //流程查询.
		menu.setTag1(flowNo); //流程编号.
		menu.setIdx(4);
		menu.setUrlExt("/App/OneFlow/RptGroup.htm?FK_Flow=" + flowNo);
		menu.setIcon("icon-chart");
		menu.Insert();

			///#endregion 第5步 创建目录.
	}

}
