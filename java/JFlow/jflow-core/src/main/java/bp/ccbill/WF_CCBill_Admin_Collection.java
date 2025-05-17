package bp.ccbill;

import bp.ccbill.template.Method;
import bp.da.*;
import bp.sys.*;
import bp.en.*;
import bp.wf.*;
import bp.wf.Glo;
import bp.wf.httphandler.*;
import bp.ccbill.template.*;

/**
 实体集合的处理
*/
public class WF_CCBill_Admin_Collection extends bp.difference.handler.DirectoryPageBase
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

		///#endregion 属性.

	/**
	 构造函数
	*/
	public WF_CCBill_Admin_Collection()
	{

	}
	/**
	 新建实体流程

	 @return
	*/
	public final String FlowNewEntity_Save() throws Exception {
		//当前表单的信息
		MapData mapData = new MapData(this.getFrmID());

			///#region 第1步: 创建一个流程.
		//首先创建流程. 参数都通过 httrp传入了。
		WF_Admin_CCBPMDesigner_FlowDevModel handler = new WF_Admin_CCBPMDesigner_FlowDevModel();
		String flowNo = handler.FlowDevModel_Save();

		//执行更新. 设置为不能独立启动.
		Flow fl = new Flow(flowNo);
		fl.setItIsCanStart(false);
		fl.setTitleRole("@WebUser.No 在@RDT 发起【@DictName】");
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
		String ndFrmID = "ND" + Integer.parseInt(flowNo + "01");
		handlerFrm.Imp_CopyFrm(ndFrmID, this.getFrmID());

		MapAttr attr = new MapAttr(ndFrmID + "_Title");
		attr.setUIVisible(false);
		attr.setName("流程标题");
		attr.Update();

		//生成名称字段.
		attr.setKeyOfEn("DictName");
		attr.setName("名称");
		attr.setUIVisible(true);
		attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
		attr.DirectInsert();



			///#endregion 把表单导入到流程上去.


		//创建查询菜单.放入到与该实体平行的位置.
		bp.ccfast.ccmenu.Menu menu = new bp.ccfast.ccmenu.Menu();
		menu.setModuleNo(this.getModuleNo()); //隶属与实体一个模块.
		menu.setName(this.getName());
		menu.setIdx(0);
		menu.setMenuModel(MethodModelClass.FlowNewEntity); //新建实体流程..

		//menu.MenuModel = menuModel "FlowEtc"; //其他类型的业务流程.
		menu.setMark("Search"); //流程查询.
		menu.setTag1(flowNo); //流程编号.
		menu.setNo(this.getFrmID() + "_" + flowNo);
		menu.setIcon("icon-paper-plane");
		menu.Insert();


		//处理启动此流程后与实体的关系设计.
		MethodFlowNewEntity en = new MethodFlowNewEntity(menu.getNo());
		en.setFlowNo(flowNo);
		en.setFrmID(this.getFrmID());
		en.setDTSWhenFlowOver(true); // 是否在流程结束后同步?
		en.setDTSDataWay(1); // 同步所有相同的字段.
		en.setUrlExt("/WF/CCBill/Opt/StartFlowByNewEntity.htm?FlowNo=" + en.getFlowNo() + "&FrmID=" + this.getFrmID() + "&MenuNo=" + menu.getNo());
		en.Update();

		//增加一个集合链接.
		Collection enColl = new Collection();
		enColl.setFrmID(this.getFrmID());
		enColl.setMethodID(MethodModelClass.FlowNewEntity);
		enColl.setMark(MethodModelClass.FlowNewEntity);
		enColl.setName(this.getName());

		enColl.setFlowNo(flowNo);
		enColl.setTag1(flowNo);

		enColl.setMethodModel(MethodModelClass.FlowNewEntity); //方法模式.
		//   enColl.UrlExt = "../CCBill/Opt/StartFlowByNewEntity.htm?FlowNo=" + en.FlowNo + "&FrmID=" + this.getFrmID() + "&MenuNo=" + menu.No;
		enColl.setIcon("icon-drop");
		enColl.setIdx(100);
		enColl.SetPara("EnName", "TS.CCBill.CollectionFlowNewEntity");
		enColl.setNo(enColl.getFrmID() + "_" + enColl.getFlowNo());
		enColl.Insert();

		return menu.getNo(); //返回的方法ID;
	}
	/**
	 单据批量发起流程

	 @return
	*/
	public final String Bill_Save() throws Exception {
		String fromFrmID = this.GetRequestVal("DictFrmID");
		String toFrmID = this.GetRequestVal("BillFrmID");

		//创建从表
		MapDtl mapDtl = new MapDtl();
		mapDtl.setFrmID(toFrmID);
		mapDtl.setNo(toFrmID + "Dtl1");
		mapDtl.setNodeID(0);
		mapDtl.setName("从表");
		mapDtl.setPTable(mapDtl.getNo());
		mapDtl.setH(300);
		mapDtl.Insert();
		mapDtl.IntMapAttrs();

		//这里仅仅复制主表的字段.
		MapAttrs attrsFrom = new MapAttrs();
		attrsFrom.Retrieve(MapAttrAttr.FK_MapData, fromFrmID, null);
		for (MapAttr attr : attrsFrom.ToJavaList())
		{
			if (attr.IsExit(MapAttrAttr.FK_MapData, mapDtl.getNo(), MapAttrAttr.KeyOfEn, attr.getKeyOfEn()) == true)
			{
				continue;
			}

			if (attr.IsExit(MapAttrAttr.FK_MapData, mapDtl.getNo(), MapAttrAttr.KeyOfEn, attr.getKeyOfEn()) == true)
			{
				continue;
			}

			attr.setFrmID( mapDtl.getNo());
			attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
			attr.Insert();
		}
		//增加一个关联的实体字段的OID
		MapAttr mapAttr = new MapAttr();
		mapAttr.setFrmID(mapDtl.getNo());
		mapAttr.setEditType(EditType.Readonly);
		mapAttr.setKeyOfEn("DictOID");
		mapAttr.setName("关联实体的OID");
		mapAttr.setMyDataType(DataType.AppInt);
		mapAttr.setUIContralType(UIContralType.TB);
		mapAttr.setLGType(FieldTypeS.Normal);
		mapAttr.setUIVisible(false);
		mapAttr.setUIIsEnable(false);
		mapAttr.setDefVal("0");
		mapAttr.Insert();
		return "复制成功.";


	}
	/**
	 实体批量发起流程

	 @return
	*/
	public final String FlowEntityBatchStart_Save() throws Exception {


			///#region 第1步: 创建一个流程.
		//首先创建流程. 参数都通过 httrp传入了。
		WF_Admin_CCBPMDesigner_FlowDevModel handler = new WF_Admin_CCBPMDesigner_FlowDevModel();
		String flowNo = handler.FlowDevModel_Save();

		//执行更新. 设置为不能独立启动.
		Flow fl = new bp.wf.Flow(flowNo);
		fl.setItIsCanStart(false);
		fl.Update();

			///#endregion 创建一个流程.


			///#region 第2步 把表单导入到流程的从表中去.
		//如果是发起流程的方法，就要表单的字段复制到，流程的表单上去.
		String frmID = "ND" + Integer.parseInt(fl.getNo()) + "01";
		MapDtl mapDtl = new MapDtl();
		mapDtl.setFrmID(frmID);
		mapDtl.setNo(frmID + "Dtl1");
		mapDtl.setNodeID(0);
		mapDtl.setName("从表");
		mapDtl.setPTable(mapDtl.getNo());
		mapDtl.setH(300);
		mapDtl.Insert();
		mapDtl.IntMapAttrs();

		//这里仅仅复制主表的字段.
		MapAttrs attrsFrom = new MapAttrs();
		attrsFrom.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), null);
		for (MapAttr attr : attrsFrom.ToJavaList())
		{
			//是否包含，系统字段？
			if (bp.wf.Glo.getFlowFields().contains("," + attr.getKeyOfEn() + ",") == true)
			{
				continue;
			}

			if (attr.IsExit(MapAttrAttr.FK_MapData, mapDtl.getNo(), MapAttrAttr.KeyOfEn, attr.getKeyOfEn()) == true)
			{
				continue;
			}

			attr.setFrmID( mapDtl.getNo());
			attr.setMyPK(attr.getFrmID() + "_" + attr.getKeyOfEn());
			attr.Insert();
		}
		//增加一个关联的实体字段的OID
		MapAttr mapAttr = new MapAttr();
		mapAttr.setFrmID(mapDtl.getNo());
		mapAttr.setEditType(EditType.Readonly);
		mapAttr.setKeyOfEn("DictOID");
		mapAttr.setName("关联实体的OID");
		mapAttr.setMyDataType(DataType.AppInt);
		mapAttr.setUIContralType(UIContralType.TB);
		mapAttr.setLGType(FieldTypeS.Normal);
		mapAttr.setUIVisible(false);
		mapAttr.setUIIsEnable(false);
		mapAttr.setDefVal("0");
		mapAttr.Insert();
		//更新开始节点.
		Node nd = new Node(Integer.parseInt(flowNo + "01"));
		nd.setName(this.getName());
		nd.Update();

			///#endregion 把表单导入到流程上去.


		//创建方法.
		Method en = new Method();
		en.setFrmID(this.getFrmID());
		en.setNo(en.getFrmID() + "_" + flowNo);
		en.setName(this.getName());
		en.setGroupID(this.getGroupID()); //分组的编号.
		en.setFlowNo(flowNo);
		en.setIcon("icon-paper-plane");
		en.setRefMethodType(RefMethodType.LinkeWinOpen); // = 1;
		en.setMethodModel(MethodModelClass.FlowEntityBatchStart); //类型.
		en.setMark("Search"); //发起流程.
		en.setTag1(flowNo); //标记为空.
		en.SetValByKey("IsCanBatch", 1);
		en.setMethodID(flowNo); // 就是流程编号.

		en.setFlowNo(flowNo);
		en.Insert();


		//增加一个集合链接.
		Collection enColl = new Collection();
		enColl.setFrmID(this.getFrmID());
		enColl.setMethodID(MethodModelClass.FlowEntityBatchStart);
		enColl.setMark(MethodModelClass.FlowEntityBatchStart);
		enColl.setName(this.getName());

		enColl.setFlowNo(flowNo);
		enColl.setTag1(flowNo);

		enColl.setMethodModel(MethodModelClass.FlowEntityBatchStart); //方法模式.
		enColl.setIcon("icon-drop");
		enColl.Insert();
		//返回方法编号。
		return en.getNo();
	}

}
