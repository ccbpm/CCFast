package bp.wf.template;

import bp.da.*;
import bp.en.*;
import bp.wf.*;
import bp.sys.*;
import java.util.*;

/** 
 抄送s
*/
public class FlowTabs extends EntitiesMyPK
{

		///#region 获得方法.
	public final String Default_Mover(String flowNo, String myks)
	{
		String[] ens = myks.split("[,]", -1);
		for (int i = 0; i < ens.length; i++)
		{
			String enNo = ens[i];
			if (enNo.contains("Default") == true)
			{
				continue;
			}

			String sql = "UPDATE WF_FlowTab SET Idx=" + (i + 1) + " WHERE MyPK='" + enNo + "' AND FK_Flow='" + flowNo + "' ";
			DBAccess.RunSQL(sql);
		}
		return "移动成功..";
	}

	/** 
	 给主页初始化数据.
	 
	 @param flowNo
	 @return 
	*/
	public final String Default_Init_bak(String flowNo) throws Exception {

		String rptNo = "ND" + Integer.parseInt(flowNo) + "Rpt";

		Flow fl = new Flow(flowNo);

		GERpts rpts = new GERpts();

		//    GEEntitys ens = new GEEntitys(rptNo);

		GenerWorkFlows ens = new GenerWorkFlows();
		QueryObject qo = new QueryObject(ens);
		qo.AddWhere(GenerWorkFlowAttr.FK_Flow, flowNo);
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.Emps, " LIKE ", "%" + bp.web.WebUser.getNo() + "%");
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.OrgNo, bp.web.WebUser.getOrgNo());
		qo.addOrderBy("RDT");
		qo.Top = 100;
		qo.DoQuery();
		return ens.ToJson("dt");
	}

	public final String Default_Init(String flowNo) throws Exception {
		//需要判断当前流程是绑定表单库的表单还是节点表单
		if (DataType.IsNullOrEmpty(flowNo) == true)
		{
			return "err@流程编号不能为空";
		}

		DataSet ds = new DataSet();
		String rptNo = "ND" + Integer.parseInt(flowNo) + "Rpt";
		GEEntitys ens = new GEEntitys(rptNo);
		QueryObject qo = new QueryObject(ens);
		qo.AddWhere(GERptAttr.WFState, ">", 1);
		qo.addAnd();
		qo.addLeftBracket();
		qo.AddWhere(GERptAttr.FlowEmps, " LIKE ", "%" + bp.web.WebUser.getNo() + "%");
		qo.addOr();
		qo.AddWhere(GERptAttr.FlowStarter, bp.web.WebUser.getNo());
		qo.addRightBracket();
		qo.addOrderBy("RDT");
		qo.Top = 100;
		qo.DoQuery();
		ds.Tables.add(ens.ToDataTableField("DT"));


		//表单的ID
		String frmID = "ND" + Integer.parseInt(flowNo) + "Rpt";
		Flow flow = new Flow(flowNo);
		if (flow.getFlowDevModel() == FlowDevModel.RefOneFrmTree)
		{
			frmID = flow.getFrmUrl();
		}
		if (flow.getFlowDevModel() == FlowDevModel.Prefessional)
		{
			//获取第一个节点的表单方案
			Node nd = new Node(Integer.parseInt(flowNo) + "01");
			if (nd.getFormType() == NodeFormType.RefOneFrmTree)
			{
				frmID = nd.getNodeFrmID();
			}
		}

		//查询出单流程的所有字段
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.Idx);

		//默认显示的系统字段 标题、创建人、创建时间、部门、状态.
		MapAttrs mattrsOfSystem = new MapAttrs();
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.Title));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FlowStarter));
		// mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, BP.WF.GERptAttr.fk));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.WFState));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FlowEmps));

		ds.Tables.add(mattrsOfSystem.ToDataTableField("Sys_MapAttrOfSystem"));

		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));

		return bp.tools.Json.ToJson(ds);
	}


	/**
	 * 获取通用系统字段和表单字段
	 */
	public final String FlowTab_Search_MapAttrs(String flowNo) throws Exception {
		//需要判断当前流程是绑定表单库的表单还是节点表单
		if (DataType.IsNullOrEmpty(flowNo) == true)
		{
			return "err@流程编号不能为空";
		}
		//表单的ID
		String frmID = "ND" + Integer.parseInt(flowNo) + "Rpt";
		Flow flow = new Flow(flowNo);
		if (flow.getFlowDevModel() == FlowDevModel.RefOneFrmTree)
		{
			frmID = flow.getFrmUrl();
		}
		if (flow.getFlowDevModel() == FlowDevModel.Prefessional)
		{
			//获取第一个节点的表单方案
			Node nd = new Node(Integer.parseInt(flowNo) + "01");
			if (nd.getFormType() == NodeFormType.RefOneFrmTree)
			{
				frmID = nd.getNodeFrmID();
			}
		}

		DataSet ds = new DataSet();
		//查询出单流程的所有字段
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, frmID, MapAttrAttr.Idx);

		//默认显示的系统字段 标题、创建人、创建时间、部门、状态.
		MapAttrs mattrsOfSystem = new MapAttrs();
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.Title));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FlowStarter));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FK_Dept));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.WFState));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FlowEmps));
		ds.Tables.add(mattrsOfSystem.ToDataTableField("Sys_MapAttrOfSystem"));

		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));

		return bp.tools.Json.ToJson(ds);
	}


	public final String Search_SearchData(String flowNo, int pageIdx, int pageSize) throws Exception {
		//表单编号
		String rptNo = "ND" + Integer.parseInt(flowNo) + "Rpt";

		//当前用户查询信息表
		UserRegedit ur = new UserRegedit(bp.web.WebUser.getNo(), rptNo + "_SearchAttrs");

		//表单属性
		MapData mapData = new MapData(rptNo);

		//流程表单对应的所有字段
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);

		//流程表单对应的流程数据
		GEEntitys ens = new GEEntitys(rptNo);
		QueryObject qo = new QueryObject(ens);
		qo.addLeftBracket();
		qo.AddWhere(GERptAttr.FlowStarter, bp.web.WebUser.getNo());
		qo.addOr();
		qo.AddWhere(GERptAttr.FlowEmps, " LIKE ", "'%@" + bp.web.WebUser.getNo() + ",%'");
		qo.addRightBracket();


			///#region 关键字查询
		String searchKey = ""; //关键字查询
		if (mapData.getItIsSearchKey())
		{
			searchKey = ur.getSearchKey();
		}

		if (mapData.getItIsSearchKey() && DataType.IsNullOrEmpty(searchKey) == false && searchKey.length() >= 1)
		{
			int i = 0;

			for (MapAttr myattr : attrs.ToJavaList())
			{
				Attr attr = myattr.getHisAttr();
				switch (attr.getMyFieldType())
				{
					case Enum:
					case FK:
					case PKFK:
						continue;
					default:
						break;
				}

				if (attr.getMyDataType() != DataType.AppString)
				{
					continue;
				}

				if (attr.getMyFieldType() == FieldType.RefText)
				{
					continue;
				}

				if (Objects.equals(attr.getKey(), "FK_Dept"))
				{
					continue;
				}

				i++;

				if (i == 1)
				{
					qo.addLeftBracket();
					if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?"))
					{
						qo.AddWhere(attr.getKey(), " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
					}
					else
					{
						qo.AddWhere(attr.getKey(), " LIKE ", " '%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
					}
					continue;
				}

				qo.addOr();

				if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?"))
				{
					qo.AddWhere(attr.getKey(), " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
				}
				else
				{
					qo.AddWhere(attr.getKey(), " LIKE ", "'%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
				}
			}

			qo.getMyParas().Add("SKey", searchKey, false);
			qo.addRightBracket();
		}
		else if (DataType.IsNullOrEmpty(mapData.GetParaString("StringSearchKeys")) == false)
		{
			String field = ""; //字段名
			String fieldValue = ""; //字段值
			int idx = 0;

			//获取查询的字段
			String[] searchFields = mapData.GetParaString("StringSearchKeys").split("[*]", -1);
			for (String str : searchFields)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				//字段名
				String[] items = str.split("[,]", -1);
				if (items.length == 2 && DataType.IsNullOrEmpty(items[0]) == true)
				{
					continue;
				}
				field = items[0];
				//字段名对应的字段值
				fieldValue = ur.GetParaString(field);
				if (DataType.IsNullOrEmpty(fieldValue) == true)
				{
					continue;
				}
				idx++;
				if (idx == 1)
				{
					/* 第一次进来。 */
					qo.addLeftBracket();
					if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?"))
					{
						qo.AddWhere(field, " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : (" '%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
					}
					else
					{
						qo.AddWhere(field, " LIKE ", " '%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
					}
					qo.getMyParas().Add(field, fieldValue, false);
					continue;
				}
				qo.addAnd();

				if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?"))
				{
					qo.AddWhere(field, " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? ("CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : ("'%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
				}
				else
				{
					qo.AddWhere(field, " LIKE ", "'%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
				}
				qo.getMyParas().Add(field, fieldValue, false);


			}
			if (idx != 0)
			{
				qo.addRightBracket();
			}
		}


			///#endregion 关键字查询




			///#region 外键或者枚举的查询

		//获得关键字.
		AtPara ap = new AtPara(ur.getVals());
		for (String str : ap.getHisHT().keySet())
		{
			String val = ap.GetValStrByKey(str);
			if (val.equals("all"))
			{
				continue;
			}
			qo.addAnd();
			qo.AddWhere(str, ap.GetValStrByKey(str));
		}

			///#endregion 外键或者枚举的查询





			///#region 日期处理
		if (mapData.getDTSearchWay()!= DTSearchWay.None)
		{
			String dtKey = mapData.getDTSearchKey();
			String dtFrom = ur.GetValStringByKey(UserRegeditAttr.DTFrom).trim();
			String dtTo = ur.GetValStringByKey(UserRegeditAttr.DTTo).trim();

			if (DataType.IsNullOrEmpty(dtFrom) == true)
			{
				if (mapData.getDTSearchWay()== DTSearchWay.ByDate)
				{
					dtFrom = "1900-01-01";
				}
				else
				{
					dtFrom = "1900-01-01 00:00";
				}
			}

			if (DataType.IsNullOrEmpty(dtTo) == true)
			{
				if (mapData.getDTSearchWay()== DTSearchWay.ByDate)
				{
					dtTo = "2999-01-01";
				}
				else
				{
					dtTo = "2999-12-31 23:59";
				}
			}

			if (mapData.getDTSearchWay()== DTSearchWay.ByDate)
			{

				qo.addAnd();
				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + "'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + "'");
				qo.addRightBracket();
			}

			if (mapData.getDTSearchWay()== DTSearchWay.ByDateTime)
			{

				qo.addAnd();
				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + " 00:00'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + " 23:59'");
				qo.addRightBracket();
			}
		}

			///#endregion 日期处理

		qo.AddWhere(" AND  WFState > 1 ");
		qo.AddWhere(" AND FID = 0 ");
		if (DataType.IsNullOrEmpty(ur.getOrderBy()) == false)
		{
			if (ur.getOrderWay().toUpperCase().equals("DESC") == true)
			{
				qo.addOrderByDesc(ur.getOrderBy());
			}
			else
			{
				qo.addOrderBy(ur.getOrderBy());
			}
		}
		ur.SetPara("Count", qo.GetCount());
		ur.Update();
		qo.DoQuery("OID", pageSize, pageIdx);

		return bp.tools.Json.ToJson(ens.ToDataTableField("Search_Data"));

	}




		///#endregion



		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FlowTab();
	}
	/** 
	 抄送
	*/
	public FlowTabs()
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FlowTab> ToJavaList()
	{
		return (java.util.List<FlowTab>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FlowTab> Tolist()
	{
		ArrayList<FlowTab> list = new ArrayList<FlowTab>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FlowTab)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
