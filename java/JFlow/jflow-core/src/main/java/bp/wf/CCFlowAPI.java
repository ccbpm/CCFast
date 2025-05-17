package bp.wf;

import bp.da.*;
import bp.difference.handler.CommonUtils;
import bp.sys.*;
import bp.tools.JflowThreadPoolTaskExecutor;
import bp.web.*;
import bp.en.*;
import bp.wf.template.*;
import bp.wf.template.sflow.*;
import bp.difference.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.time.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 流程的API.
*/
public class CCFlowAPI
{
	/**
	 产生一个 WorkNode

	 @param fk_flow 流程编号
	 @param nd 节点
	 @param workID 工作ID
	 @param fid FID
	 @param userNo 用户编号
	 @return 返回dataset
	*/

	public static DataSet GenerWorkNode(String fk_flow, Node nd, long workID, long fid, String userNo, long realWorkID, String fromWorkOpt) throws Exception {
		return GenerWorkNode(fk_flow, nd, workID, fid, userNo, realWorkID, fromWorkOpt, false,"H5");
	}

	public static DataSet GenerWorkNode(String fk_flow, Node nd, long workID, long fid, String userNo, long realWorkID) throws Exception {
		return GenerWorkNode(fk_flow, nd, workID, fid, userNo, realWorkID, "0", false,"H5");
	}

	public static DataSet GenerWorkNode(String fk_flow, Node nd, long workID, long fid, String userNo, long realWorkID,boolean isView) throws Exception {
		return GenerWorkNode(fk_flow, nd, workID, fid, userNo, realWorkID, "0", isView,"H5");
	}

	public static DataSet GenerWorkNode(String fk_flow, Node nd, long workID, long fid, String userNo, long realWorkID, String fromWorkOpt, boolean isView,String pageType) throws Exception {
		DBAccess.RunSQL("DELETE FROM Sys_MapExt WHERE DoWay='0' or DoWay='None'");

		try
		{
			nd.WorkID = workID; //为获取表单ID提供参数.

			Work wk = nd.getHisWork();
			wk.setOID(workID);
			int wkCount = wk.RetrieveFromDBSources();

			String frmID = nd.getNodeFrmID();
			//根据表单存储的数据获取获取使用表单的版本号
			int frmVer = 0;
			if (wk.getEnMap().getAttrs().contains("AtPara") == true)
			{
				frmVer = wk.getFrmVer();
				if (frmVer != 0)
				{
					frmID = nd.getNodeFrmID() + "." + frmVer;
					if (nd.getFormType() != NodeFormType.FoolTruck)
					{
						wk = new GEWork(nd.getNodeID(), frmID);
						wk.setOID(workID);
						wkCount = wk.RetrieveFromDBSources();
					}
				}
			}
			MapData md = new MapData(frmID);
			//定义变量，为绑定独立表单设置单据编号.
			String billNo = null; //定义单据编号.
			String billNoField = null; //定义单据编号字段.

			// 第1.2: 调用,处理用户定义的业务逻辑.
			String sendWhen = ExecEvent.DoNode(EventListNode.FrmLoadBefore, nd, wk, null);

			if(nd.getHisNodeType()==NodeType.CCNode){
				frmID="ND"+Integer.parseInt(nd.getFlowNo()+"01");
				Node node=new Node(Integer.parseInt(nd.getFlowNo()+"01"));
				wk = node.getHisWork();
				wk.setOID(workID);
				wkCount = wk.RetrieveFromDBSources();
			}

			//获得表单模版.
			DataSet myds = bp.sys.CCFormAPI.GenerHisDataSet(frmID, nd.getName(), null);

			//现在版本不是主版本的情况
			if (frmID.equals(nd.getNodeFrmID()) == false)
			{
				DataTable mddt = myds.GetTableByName("Sys_MapData");
				mddt.Rows.get(0).setValue("AtPara",mddt.Rows.get(0).getValue("AtPara") + "@MainFrmID=" + nd.getNodeFrmID());

				DataTable athdt = myds.GetTableByName("Sys_FrmAttachment");
				if (frmVer != 0 && athdt.Rows.size() != 0)
				{
					DataTable gfdt = myds.GetTableByName("Sys_GroupField");
					for (DataRow dr : athdt.Rows)
					{
						DataRow[] gfr = gfdt.Select("CtrlID='" + dr.getValue("MyPK") + "'");
						if (gfr.length != 0)
						{
							gfr[0].setValue("CtrlID", nd.getNodeFrmID() + "_" + dr.getValue("NoOfObj"));
						}
						dr.setValue("MyPK", nd.getNodeFrmID() + "_" + dr.getValue("NoOfObj"));

					}

				}
			}


			//更换表单的名字.
			if (DataType.IsNullOrEmpty(nd.getNodeFrmID()) == false && (nd.getItIsNodeFrm()))
			{
				String realName = myds.GetTableByName("Sys_MapData").Rows.get(0).getValue("Name") instanceof String ? (String)myds.GetTableByName("Sys_MapData").Rows.get(0).getValue("Name") : null;
				if (DataType.IsNullOrEmpty(realName) == true)
				{
					myds.GetTableByName("Sys_MapData").Rows.get(0).setValue("Name",nd.getName());
				}
			}


				///#region 处理表单权限控制方案: 如果是绑定单个表单的时候.
			/*处理表单权限控制方案: 如果是绑定单个表单的时候. */

			//这两个变量在累加表单用到.
		   FrmNode frmNode = new FrmNode();
			Flow flow = new Flow(fk_flow);
			myds.Tables.add(flow.ToDataTableField("WF_Flow"));

			if (nd.getHisFormType() == NodeFormType.RefOneFrmTree
					|| nd.getHisFormType() == NodeFormType.FoolTruck
					|| nd.getHisFormType() == NodeFormType.RefNodeFrm
					|| flow.getFlowDevModel() == FlowDevModel.JiJian)
			{
				int count = frmNode.Retrieve(FrmNodeAttr.FK_Frm, nd.getNodeFrmID(), FrmNodeAttr.FK_Node, nd.getNodeID());
				if (count == 0)
				{
					frmNode.setItIsEnableLoadData(true);
				}
				if (count != 0 && frmNode.getFrmSln().getValue() != 0)
				{
					FrmFields fls = new FrmFields(nd.getNodeFrmID(), frmNode.getNodeID());
					for (FrmField item : fls.ToJavaList())
					{
						for (DataRow dr : myds.GetTableByName("Sys_MapAttr").Rows)
						{
							String keyOfEn = dr.getValue("KeyOfEn").toString();
							if (keyOfEn.equals(item.getKeyOfEn()) == false)
							{
								continue;
							}

							if (item.getItIsSigan() == true)
							{
								dr.setValue("UIIsEnable",0);
							}

							if (item.getUIIsEnable() == true)
							{
								dr.setValue("UIIsEnable", 1);
							}
							else
							{
								dr.setValue("UIIsEnable", 0);
							}

							if (item.getItIsNotNull() == true)
							{
								dr.setValue("UIIsInput", 1);
							}
							else
							{
								dr.setValue("UIIsInput", 0);
							}

							if (item.getUIVisible() == true)
							{
								dr.setValue("UIVisible", 1);
							}
							else
							{
								dr.setValue("UIVisible", 0);
							}

							if (item.getItIsSigan() == true)
							{
								dr.setValue("IsSigan", 1);
							}
							else
							{
								dr.setValue("IsSigan", 0);
							}

							dr.setValue("DefVal", item.getDefVal());
						}
					}

					//从表权限的设置
					MapDtls mapdtls = new MapDtls();
					mapdtls.Retrieve(MapDtlAttr.FK_MapData, nd.getNodeFrmID() + "_" + frmNode.getNodeID(), null);
					for (DataRow dr : myds.GetTableByName("Sys_MapDtl").Rows)
					{
						for (MapDtl mapDtl : mapdtls.ToJavaList())
						{
							String no = dr.getValue("No").toString() + "_" + frmNode.getNodeID();
							if (no.equals(mapDtl.getNo()) == true)
							{
								dr.setValue("IsView", mapDtl.getItIsView() == true ? 1 : 0);
								break;
							}

						}
					}
				}
			}
			else
			{
				frmNode.setItIsEnableLoadData(true);
			}
				///#endregion 处理表单权限控制方案: 如果是绑定单个表单的时候.

			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			GenerWorkFlow gwf = new GenerWorkFlow();
			//把流程信息表发送过去.
			ThreadPoolTaskExecutor threadPoolTaskExecutor= JflowThreadPoolTaskExecutor.getInstance();
			Future<Boolean> futureGenerWorkFlow = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					RequestContextHolder.setRequestAttributes(requestAttributes);
					gwf.setWorkID(realWorkID);
					gwf.RetrieveFromDBSources();
					myds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));
					return true;
				}
			});
			Future<Boolean> futureWF_Node = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					RequestContextHolder.setRequestAttributes(requestAttributes);
					//加入WF_Node.
					DataTable WF_Node = nd.ToDataTableField("WF_Node");
					myds.Tables.add(WF_Node);
					return true;
				}
			});
			Future<Boolean> futureFrmNodeComponent = threadPoolTaskExecutor.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					RequestContextHolder.setRequestAttributes(requestAttributes);
					///#region 加入组件的状态信息, 在解析表单的时候使用.
					FrmNodeComponent fnc = new FrmNodeComponent(nd.getNodeID());
					nd.WorkID = workID; //为获取表单ID提供参数.
					//把审核组件信息，放入ds.
					myds.Tables.add(fnc.ToDataTableField("WF_FrmNodeComponent"));
					return true;
				}
			});
			futureGenerWorkFlow.get();
			futureWF_Node.get();
			futureFrmNodeComponent.get();
			boolean isHaveSubFlow = false;
			///#region 没有审核组件分组就增加上审核组件分组.
			if (nd.getNodeFrmID().equals("ND" + nd.getNodeID()) == true || nd.getHisFormType() == NodeFormType.RefNodeFrm || flow.getFlowDevModel() == FlowDevModel.JiJian || (nd.getHisFormType() == NodeFormType.RefOneFrmTree && DataType.IsNullOrEmpty(frmNode.getMyPK()) == false))
			{
				boolean isHaveFWC = false;
				//绑定表单库中的表单
				if ((DataType.IsNullOrEmpty(frmNode.getMyPK()) == false && frmNode.isEnableFWC() != FrmWorkCheckSta.Disable) || ((nd.getNodeFrmID().equals("ND" + nd.getNodeID()) ||  nd.getHisFormType() == NodeFormType.RefNodeFrm || flow.getFlowDevModel() == FlowDevModel.JiJian) && nd.getFrmWorkCheckSta() != FrmWorkCheckSta.Disable))
				{
					isHaveFWC = true;
				}

				if ((nd.getFormType() == NodeFormType.FoolForm ||  nd.getHisFormType() == NodeFormType.RefNodeFrm || frmNode.getHisFrmType() == FrmType.FoolForm) && isHaveFWC == true)
				{
					//判断是否是经典表单，如果是，就要判断该经典表单是否有审核组件groupfield ,没有的话就增加上.
					DataTable gf = myds.GetTableByName("Sys_GroupField");
					boolean isHave = false;
					for (DataRow dr : gf.Rows)
					{
						String cType = dr.getValue("CtrlType") instanceof String ? (String)dr.getValue("CtrlType") : null;
						if (cType == null)
						{
							continue;
						}
						if (cType.equals("FWC") == true)
						{
							isHave = true;
						}
						if (cType.equals("SubFlow") == true)
						{
							isHaveSubFlow = true;
						}
					}

					if (isHave == false)
					{
						DataRow dr = gf.NewRow();

						nd.WorkID = workID; //为获取表单ID提供参数.
						dr.setValue(GroupFieldAttr.OID, 100);
						dr.setValue(GroupFieldAttr.FrmID, nd.getNodeFrmID());
						dr.setValue(GroupFieldAttr.CtrlType, "FWC");
						dr.setValue(GroupFieldAttr.CtrlID, "FWCND" + nd.getNodeID());
						dr.setValue(GroupFieldAttr.Idx, 100);
						dr.setValue(GroupFieldAttr.Lab, "审核信息");
						gf.Rows.add(dr);

						myds.Tables.remove("Sys_GroupField");
						myds.Tables.add(gf);

						//更新,为了让其自动增加审核分组.
						FrmNodeComponent refFnc = new FrmNodeComponent(nd.getNodeID());
						refFnc.Update();
					}
				}
			}

				///#endregion 没有审核组件分组就增加上审核组件分组.


				///#region 增加父子流程组件
			if (nd.getHisFormType() == NodeFormType.RefOneFrmTree && DataType.IsNullOrEmpty(frmNode.getMyPK()) == false && frmNode.getSFSta() != FrmSubFlowSta.Disable)
			{
				DataTable gf = myds.GetTableByName("Sys_GroupField");

				if (isHaveSubFlow == false)
				{
					DataRow dr = gf.NewRow();

					nd.WorkID = workID; //为获取表单ID提供参数.
					dr.setValue(GroupFieldAttr.OID, 120);
					dr.setValue(GroupFieldAttr.FrmID, nd.getNodeFrmID());
					dr.setValue(GroupFieldAttr.CtrlType, "SubFlow");
					dr.setValue(GroupFieldAttr.CtrlID, "SubFlowND" + nd.getNodeID());
					dr.setValue(GroupFieldAttr.Idx, 120);
					dr.setValue(GroupFieldAttr.Lab, "父子流程");
					gf.Rows.add(dr);
					myds.Tables.remove("Sys_GroupField");
					myds.Tables.add(gf);
					//更新,为了让其自动增加审核分组.
					FrmNodeComponent refFnc = new FrmNodeComponent(nd.getNodeID());
					refFnc.Update();
				}
			}
				///#region 处理累加表单增加 groupfields
			if (nd.getFormType() == NodeFormType.FoolTruck && nd.getItIsStartNode() == false && DataType.IsNullOrEmpty(wk.HisPassedFrmIDs) == false)
			{
					///#region 处理字段分组排序. groupfields
				//查询所有的分组, 如果是查看表单的方式，就不应该把当前的表单显示出来.
				String myFrmIDs = "";
				if (fromWorkOpt.equals("1") == true)
				{
					if (gwf.getWFState() == WFState.CompleteEnd)
					{
						myFrmIDs = wk.HisPassedFrmIDs + ",'ND" + nd.getNodeID() + "'";
					}
					else
					{
						myFrmIDs = wk.HisPassedFrmIDs; //流程未完成并且是查看表单的情况.
					}
				}
				else
				{
					myFrmIDs = wk.HisPassedFrmIDs + ",'ND" + nd.getNodeID() + "'";
				}

				GroupFields gfs = new GroupFields();
				gfs.RetrieveIn(GroupFieldAttr.FrmID, "(" + myFrmIDs + ")", null);

				//按照时间的顺序查找出来 ids .
				String sqlOrder = "SELECT OID FROM  Sys_GroupField WHERE   FrmID IN (" + myFrmIDs + ")";
				String orderMyFrmIDs = myFrmIDs.replace("'", "");
				if (SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6  || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
				{
					sqlOrder += " ORDER BY INSTR('" + orderMyFrmIDs + "',FrmID) , Idx";
				}

				if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
				{
					sqlOrder += " ORDER BY CHARINDEX(FrmID, '" + orderMyFrmIDs + "'), Idx";
				}

				if (SystemConfig.getAppCenterDBType() == DBType.MySQL  || SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL || SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
				{
					sqlOrder += " ORDER BY INSTR('" + orderMyFrmIDs + "', FrmID ), Idx";
				}
				if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.UX || SystemConfig.getAppCenterDBType() == DBType.HGDB)
				{
					sqlOrder += " ORDER BY POSITION(FrmID  IN '" + orderMyFrmIDs + "'), Idx";
				}

				if (SystemConfig.getAppCenterDBType() == DBType.DM)
				{
					sqlOrder += " ORDER BY POSITION(FrmID  IN '" + orderMyFrmIDs + "'), Idx";
				}

				DataTable dtOrder = DBAccess.RunSQLReturnTable(sqlOrder);

				//创建容器,把排序的分组放入这个容器.
				GroupFields gfsNew = new GroupFields();

				//遍历查询出来的分组.
				//只能增加一个审核分组
				GroupField FWCG = null;
				for (DataRow dr : dtOrder.Rows)
				{
					String pkOID = dr.getValue(0).toString();
					Object tempVar = gfs.GetEntityByKey(pkOID);
					GroupField mygf = tempVar instanceof GroupField ? (GroupField)tempVar : null;
					if (mygf.getCtrlType().equals("FWC"))
					{
						FWCG = mygf;
						continue;
					}

					gfsNew.AddEntity(mygf); //把分组字段加入里面去.
				}
				if (FWCG != null)
				{
					gfsNew.AddEntity(FWCG);
				}

				DataTable dtGF = gfsNew.ToDataTableField("Sys_GroupField");
				myds.Tables.remove("Sys_GroupField");
				myds.Tables.add(dtGF);

					///#endregion 处理字段分组排序. groupfields


					///#region 处理 mapattrs
				//求当前表单的字段集合.
				MapAttrs mattrs = new MapAttrs();
				QueryObject qo = new QueryObject(mattrs);
				qo.AddWhere(MapAttrAttr.FK_MapData, "ND" + nd.getNodeID());
				qo.addOrderBy(MapAttrAttr.Idx);
				qo.DoQuery();

				//获取走过节点的表单方案
				MapAttrs attrsLeiJia = new MapAttrs();

				//存在表单方案只读
				String sql1 = "Select FK_Frm From WF_FrmNode Where FK_Frm In(" + wk.HisPassedFrmIDs + ") And FrmSln=" + FrmSln.Readonly.getValue() + " And FK_Node=" + nd.getNodeID();
				DataTable dt1 = DBAccess.RunSQLReturnTable(sql1);
				if (dt1.Rows.size() > 0)
				{
					//获取节点
					String nodes = "";
					for (DataRow dr : dt1.Rows)
					{
						nodes += "'" + dr.getValue(0).toString() + "',";
					}

					nodes = nodes.substring(0, nodes.length() - 1);
					qo = new QueryObject(attrsLeiJia);
					qo.AddWhere(MapAttrAttr.FK_MapData, " IN ", "(" + nodes + ")");
					qo.addOrderBy(MapAttrAttr.Idx);
					qo.DoQuery();

					for (MapAttr item : attrsLeiJia.ToJavaList())
					{
						if (item.getKeyOfEn().equals("RDT") || item.getKeyOfEn().equals("Rec"))
						{
							continue;
						}
						item.setUIIsEnable(false); //设置为只读的.
						mattrs.AddEntity(item);
					}
				}
				//存在表单方案默认
				sql1 = "Select FK_Frm From WF_FrmNode Where FK_Frm In(" + wk.HisPassedFrmIDs + ") And FrmSln=" + FrmSln.Default.getValue() + " And FK_Node=" + nd.getNodeID();
				dt1 = DBAccess.RunSQLReturnTable(sql1);
				if (dt1.Rows.size() > 0)
				{
					//获取节点
					String nodes = "";
					for (DataRow dr : dt1.Rows)
					{
						nodes += "'" + dr.getValue(0).toString() + "',";
					}

					nodes = nodes.substring(0, nodes.length() - 1);
					qo = new QueryObject(attrsLeiJia);
					qo.AddWhere(MapAttrAttr.FK_MapData, " IN ", "(" + nodes + ")");
					qo.addOrderBy(MapAttrAttr.Idx);
					qo.DoQuery();

					for (MapAttr item : attrsLeiJia.ToJavaList())
					{
						if (item.getKeyOfEn().equals("RDT") || item.getKeyOfEn().equals("Rec"))
						{
							continue;
						}
						mattrs.AddEntity(item);
					}

				}

				//存在表单方案自定义
				sql1 = "Select FK_Frm From WF_FrmNode Where FK_Frm In(" + wk.HisPassedFrmIDs + ") And FrmSln=" + FrmSln.Self.getValue() + " And FK_Node=" + nd.getNodeID();
				dt1 = DBAccess.RunSQLReturnTable(sql1);

				if (dt1.Rows.size() > 0)
				{
					//获取节点
					String nodes = "";
					for (DataRow dr : dt1.Rows)
					{
						nodes += "'" + dr.getValue(0).toString() + "',";
					}

					nodes = nodes.substring(0, nodes.length() - 1);
					qo = new QueryObject(attrsLeiJia);
					qo.AddWhere(MapAttrAttr.FK_MapData, " IN ", "(" + nodes + ")");
					qo.addOrderBy(MapAttrAttr.Idx);
					qo.DoQuery();

					//获取累加表单的权限
					FrmFields fls = new FrmFields();
					qo = new QueryObject(fls);
					qo.AddWhere(FrmFieldAttr.FrmID, " IN ", "(" + nodes + ")");
					qo.addAnd();
					qo.AddWhere(FrmFieldAttr.EleType, "Field");
					qo.addAnd();
					qo.AddWhere(FrmFieldAttr.FK_Node, nd.getNodeID());
					qo.DoQuery();

					for (MapAttr attr : attrsLeiJia.ToJavaList())
					{
						if (attr.getKeyOfEn().equals("RDT") || attr.getKeyOfEn().equals("Rec"))
						{
							continue;
						}

						FrmField frmField = null;
						for (FrmField item : fls.ToJavaList())
						{
							if (Objects.equals(attr.getKeyOfEn(), item.getKeyOfEn()))
							{
								frmField = item;
								break;
							}
						}
						if (frmField != null)
						{
							if (frmField.getItIsSigan())
							{
								attr.setUIIsEnable(false);
							}

							attr.setUIIsEnable(frmField.getUIIsEnable());
							attr.setUIVisible(frmField.getUIVisible());
							attr.setItIsSigan(frmField.getItIsSigan());
							attr.setDefValReal(frmField.getDefVal());
						}
						mattrs.AddEntity(attr);
					}

				}

				//替换掉现有的.
				myds.Tables.remove("Sys_MapAttr"); //移除.
				myds.Tables.add(mattrs.ToDataTableField("Sys_MapAttr")); //增加.

					///#endregion 处理mapattrs


					///#region 把枚举放入里面去.
				myds.Tables.remove("Sys_Enum");

				myFrmIDs = wk.HisPassedFrmIDs + ",'ND" + nd.getNodeID() + "'";
				SysEnums enums = new SysEnums();
				enums.RetrieveInSQL(SysEnumAttr.EnumKey, "SELECT UIBindKey FROM Sys_MapAttr WHERE FK_MapData in(" + myFrmIDs + ")", SysEnumAttr.IntKey);

				// 加入最新的枚举.
				myds.Tables.add(enums.ToDataTableField("Sys_Enum"));

					///#endregion 把枚举放入里面去.


					///#region  MapExt .
				myds.Tables.remove("Sys_MapExt");

				// 把扩展放入里面去.
				myFrmIDs = wk.HisPassedFrmIDs + ",'ND" + nd.getNodeID() + "'";
				MapExts exts = new MapExts();
				qo = new QueryObject(exts);
				qo.AddWhere(MapExtAttr.FK_MapData, " IN ", "(" + myFrmIDs + ")");
				qo.DoQuery();

				// 加入最新的MapExt.
				myds.Tables.add(exts.ToDataTableField("Sys_MapExt"));

					///#endregion  MapExt .


					///#region  MapDtl .
				myds.Tables.remove("Sys_MapDtl");

				//把从表放里面
				myFrmIDs = wk.HisPassedFrmIDs + ",'ND" + nd.getNodeID() + "'";
				MapDtls dtls = new MapDtls();
				qo = new QueryObject(dtls);
				qo.AddWhere(MapDtlAttr.FK_MapData, " IN ", "(" + myFrmIDs + ")");
				qo.addAnd();
				qo.AddWhere(MapDtlAttr.FK_Node, 0);

				qo.DoQuery();

				// 加入最新的MapDtl.
				myds.Tables.add(dtls.ToDataTableField("Sys_MapDtl"));

					///#endregion  MapDtl .


					///#region  FrmAttachment .
				myds.Tables.remove("Sys_FrmAttachment");

				//把附件放里面
				myFrmIDs = wk.HisPassedFrmIDs + ",'ND" + nd.getNodeID() + "'";
				FrmAttachments frmAtchs = new FrmAttachments();
				qo = new QueryObject(frmAtchs);
				qo.AddWhere(FrmAttachmentAttr.FK_MapData, " IN ", "(" + myFrmIDs + ")");
				qo.addAnd();
				qo.AddWhere(FrmAttachmentAttr.FK_Node, 0);
				qo.DoQuery();

				// 加入最新的Sys_FrmAttachment.
				myds.Tables.add(frmAtchs.ToDataTableField("Sys_FrmAttachment"));

					///#endregion  FrmAttachment .

			}

				///#endregion 增加 groupfields


				///#region 处理节点表单是经典表单的特定用户特定权限
			MapExts nexts = (MapExts)md.getMapExts().GetEntitiesByKey(MapExtAttr.ExtType, "SepcFieldsSepcUsers");
			GroupFields groupFields = md.getGroupFields();
			//加入分组表.
			if (nexts != null && !nexts.isEmpty())
			{
				String oids = ",";
				String sq1l = "";
				String tag1 = "";
				for (MapExt ext : nexts.ToJavaList())
				{
					tag1 = ext.getTag1();
					tag1 = tag1.replace("，", ",");
					if (ext.getTag().equals("Emps"))
					{
						tag1 = "," + tag1 + ",";
					}

					if (ext.getTag().equals("Emps") && tag1.contains("," + WebUser.getNo() + ",") == true)
					{
						oids += ext.getDoc() + ",";
					}
					if (ext.getTag().equals("Stas") == true)
					{
						if (tag1.endsWith(","))
						{
							tag1 = tag1.substring(0, tag1.length() - 1);
						}
						tag1 = tag1.replace(",", "','") + "'";
						sq1l = "SELECT count(*) From Port_DeptEmpStation WHERE FK_Station IN(" + tag1 + ") AND FK_Emp='" + WebUser.getNo() + "'";
						if (DBAccess.RunSQLReturnValInt(sq1l) != 0)
						{
							oids += ext.getDoc() + ",";
						}
					}
					if (ext.getTag().equals("Depts") == true)
					{
						if (tag1.endsWith(","))
						{
							tag1 = tag1.substring(0, tag1.length() - 1);
						}
						tag1 = tag1.replace(",", "','") + "'";
						sq1l = "SELECT count(*) From Port_DeptEmp WHERE FK_Dept IN(" + tag1 + ") AND FK_Emp='" + WebUser.getNo() + "'";
						if (DBAccess.RunSQLReturnValInt(sq1l) != 0)
						{
							oids += ext.getDoc() + ",";
						}
					}
					if (ext.getTag().equals("SQL") == true)
					{
						tag1 = bp.wf.Glo.DealExp(tag1, wk);
						if (DBAccess.RunSQLReturnValInt(tag1) != 0)
						{
							oids += ext.getDoc() + ",";
						}
					}
				}
				DataTable gfdt = myds.GetTableByName("Sys_GroupField");
				for (DataRow dr : gfdt.Rows)
				{
					if (oids.contains(dr.getValue("OID") + ","))
					{
						dr.setValue("ShowType", 2);
					}

				}
			}

				///#endregion 处理节点表单是经典表单的特定用户特定权限




				///#region 流程设置信息.
			if (isView == false)
			{
				Dev2Interface.Node_SetWorkRead(nd.getNodeID(), realWorkID);
				if (nd.getItIsStartNode() == false)
				{
					if (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == false)
					{
						if(Dev2Interface.Flow_IsCanDoCurrentWork(gwf.getWorkID(), WebUser.getNo())==false)
							throw new RuntimeException("err@您[" + WebUser.getNo() + "," + WebUser.getName() + "]不能执行当前工作, 当前工作已经运转到[" + gwf.getNodeName()
									+ "],处理人[" + gwf.getTodoEmps() + "]。");
						gwf.setTodoEmps(gwf.getTodoEmps() + WebUser.getNo() + "," + WebUser.getName() + ";");
						gwf.Update();
					}
				}
			}

				///#endregion 流程设置信息.


				///#region 把主从表数据放入里面.
			//.工作数据放里面去, 放进去前执行一次装载前填充事件.

			//重设默认值.
			if (isView == false && (DataType.IsNullOrEmpty(frmNode.getMyPK()) == true || (DataType.IsNullOrEmpty(frmNode.getMyPK()) == false && frmNode.getFrmSln() != FrmSln.Readonly)))
			{
				wk.ResetDefaultVal(nd.getNodeFrmID(), fk_flow, nd.getNodeID());
			}

			//URL参数替换
			if (SystemConfig.isBSsystem() == true && isView == false)
			{
				// 处理传递过来的参数。
//				for (String k : HttpContextHelper.RequestQueryStringKeys)
					for (String k : CommonUtils.getRequest().getParameterMap().keySet())
				{
					if (DataType.IsNullOrEmpty(k) == true)
					{
						continue;
					}

					wk.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
				}

				for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet())
				{
					if (DataType.IsNullOrEmpty(k) == true)
					{
						continue;
					}

					wk.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
				}

				//更新到数据库里.
				if(wkCount==0)
					wk.DirectInsert();
				else
					wk.DirectUpdate();
			}

			//执行表单事件
			MapExts mes = md.getMapExts();
			MapExt me = null;
			String msg = null;
			if (isView == false)
			{
				//执行装载填充.
				if (DataType.IsNullOrEmpty(pageType) == false && pageType.equals("Vue3"))
				{
					if (frmNode.getItIsEnableLoadData() == true)
					{
						MapExts mapExts = new MapExts();
						for(MapExt mapExt : mes.ToJavaList())
						{
							if (mapExt.getDoWay().equals("0") == false && (mapExt.getExtModel().equals(MapExtXmlList.PageLoadFullMainTable)
									|| mapExt.getExtModel().equals(MapExtXmlList.PageLoadFullDDL) || mapExt.getExtModel().equals(MapExtXmlList.PageLoadFullDtl)))
								mapExts.AddEntity(mapExt);
						}
						if (mapExts.size() != 0)
						{
							//执行通用的装载方法.
							MapAttrs attrs = md.getMapAttrs();
							Entity tempVar3 = bp.wf.CCFormAPI.DealPageLoadFullVue(wk, mapExts, attrs) ;
							wk = tempVar3 instanceof Work ? (Work)tempVar3 : null;
						}
					}

				}else{
					msg = ExecEvent.DoFrm(md, EventListFrm.FrmLoadBefore, wk);
					if (DataType.IsNullOrEmpty(msg) == false)
					{
						throw new RuntimeException("err@错误:" + msg);
					}

					//执行装载填充.
					String mypk = MapExtXmlList.PageLoadFull + "_" +md.getNo();
					if (frmNode.getItIsEnableLoadData() == true && md.getItIsPageLoadFull() == true)
					{
						Object tempVar2 = mes.GetEntityByKey("MyPK", mypk);
						me = tempVar2 instanceof MapExt ? (MapExt)tempVar2 : null;

						//执行通用的装载方法.
						MapAttrs attrs = md.getMapAttrs();
						MapDtls dtls = md.getMapDtls();
						Entity tempVar3 = CCFormAPI.DealPageLoadFull(wk, me, attrs, dtls);
						wk = tempVar3 instanceof Work ? (Work)tempVar3 : null;
					}
				}

			}
			//如果是累加表单，就把整个rpt数据都放入里面去.
			if (nd.getFormType() == NodeFormType.FoolTruck && nd.getItIsStartNode() == false && DataType.IsNullOrEmpty(wk.HisPassedFrmIDs) == false)
			{
				GERpt rpt = null;
				try{
					rpt= new GERpt("ND" + Integer.parseInt(nd.getFlowNo()) + "Rpt", workID);
				}catch (Exception e){
					rpt= new GERpt("ND" + Integer.parseInt(nd.getFlowNo()) + "Rpt", fid);
				}
				wk.CopyByEmpty(rpt);
				DataTable rptdt = wk.ToDataTableField("MainTable");
				myds.Tables.add(rptdt);
			}
			else
			{
				if (wk.getRow().contains("AtPara") == false)
					wk.getRow().SetValByKey("AtPara", DBAccess.RunSQLReturnString("SELECT AtPara From " + wk.getEnMap().getPhysicsTable() + " WHERE OID=" + wk.getOID()));
				if(workID ==realWorkID){
					GERpt rpt = null;
					try{
						rpt= new GERpt("ND" + Integer.parseInt(nd.getFlowNo()) + "Rpt", workID);
					}catch (Exception e){
						rpt= new GERpt("ND" + Integer.parseInt(nd.getFlowNo()) + "Rpt", fid);
					}
					wk.CopyByEmpty(rpt);
				}

				DataTable rptdt = wk.ToDataTableField("MainTable");
				myds.Tables.add(rptdt);
			}
			String sql = "";
			DataTable dt = null;

				///#endregion
				///#region 把外键表加入 DataSet
			DataTable dtMapAttr = myds.GetTableByName("Sys_MapAttr");

			DataTable ddlTable = new DataTable();
			ddlTable.Columns.Add("No");

			for (DataRow dr : dtMapAttr.Rows)
			{
				String lgType = dr.getValue("LGType").toString();
				String uiBindKey = dr.getValue("UIBindKey").toString();

				if (DataType.IsNullOrEmpty(uiBindKey) == true)
				{
					continue; //为空就continue.
				}

				if (lgType.equals("1") == true)
				{
					continue; //枚举值就continue;
				}
				//@liuqiang,
				String uiIsEnable = dr.getValue("UIIsEnable").toString();
				if (uiIsEnable.equals("0") == true && lgType.equals("2") == true)
				{
					continue; //如果是外键，并且是不可以编辑的状态.
				}

				if (uiIsEnable.equals("0") == true && lgType.equals("0") == true)
				{
					continue; //如果是外部数据源，并且是不可以编辑的状态.
				}


				// 检查是否有下拉框自动填充。
				String keyOfEn = dr.getValue("KeyOfEn").toString();
				String fk_mapData = dr.getValue("FK_MapData").toString();


					///#region 处理下拉框数据范围. for 小杨.
				Object tempVar4 = mes.GetEntityByKey(MapExtAttr.ExtType, MapExtXmlList.AutoFullDLL, MapExtAttr.AttrOfOper, keyOfEn);
				me = tempVar4 instanceof MapExt ? (MapExt)tempVar4 : null;
				if (me != null && myds.contains(keyOfEn) == false)
				{
					Object tempVar5 = me.getDoc();
					String fullSQL = tempVar5 instanceof String ? (String)tempVar5 : null;
					if (fullSQL == null)
					{
						throw new RuntimeException("err@字段[" + keyOfEn + "]下拉框AutoFullDLL，没有配置SQL");
					}

					fullSQL = fullSQL.replace("~", "'");
					fullSQL = bp.wf.Glo.DealExp(fullSQL, wk, null);
						dt = DBAccess.RunSQLReturnTable(fullSQL);
					if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
					{
						String columnName = "";
						for (DataColumn col : dt.Columns)
						{
							columnName = col.ColumnName.toUpperCase();
							switch (columnName)
							{
								case "NO":
									col.ColumnName = "No";
									break;
								case "NAME":
									col.ColumnName = "Name";
									break;
							}
						}
					}
					//重构新表
					DataTable dt_FK_Dll = new DataTable();
					dt_FK_Dll.TableName = keyOfEn; //可能存在隐患，如果多个字段，绑定同一个表，就存在这样的问题.
					dt_FK_Dll.Columns.Add("No", String.class);
					dt_FK_Dll.Columns.Add("Name", String.class);

					for (DataRow dllRow : dt.Rows)
					{
						DataRow drDll = dt_FK_Dll.NewRow();
						drDll.setValue("No", dllRow.getValue("No"));
						drDll.setValue("Name", dllRow.getValue("Name"));
						dt_FK_Dll.Rows.add(drDll);
					}
					myds.Tables.add(dt_FK_Dll);
					continue;
				}

					///#endregion 处理下拉框数据范围.

				// 判断是否存在.
				if (myds.contains(uiBindKey) == true)
				{
					continue;
				}

				DataTable mydt = bp.pub.PubClass.GetDataTableByUIBineKey(uiBindKey, null);
				if (mydt == null)
				{
					DataRow ddldr = ddlTable.NewRow();
					ddldr.setValue("No", uiBindKey);
					ddlTable.Rows.add(ddldr);
				}
				else
				{
					myds.Tables.add(mydt);
				}
			}
			ddlTable.TableName = "UIBindKey";
			myds.Tables.add(ddlTable);

				///#endregion End把外键表加入DataSet


				///#region 处理流程-消息提示.
			if (isView == true || fromWorkOpt.equals("1"))
			{
				//如果是查看模式，或者是从WorkOpt打开模式,就不让其显示消息.
			}
			else
			{
				//退回消息提示
				myds.Tables.add(GetFlowAlertMsg(gwf, nd, fk_flow, realWorkID, fid));
				//催办消息提示
				myds.Tables.add(GetFlowPressMsg(gwf));
			}

				///#endregion


				///#region 增加流程节点表单绑定信息.
			myds.Tables.add(frmNode.ToDataTableField("WF_FrmNode"));
			if (nd.getHisFormType() == NodeFormType.RefOneFrmTree)
			{
				/* 独立流程节点表单. */
				nd.WorkID = workID; //为获取表单ID ( NodeFrmID )提供参数.

				//设置单据编号,对于绑定的表单.
				if (nd.getItIsStartNode() == true && DataType.IsNullOrEmpty(frmNode.getBillNoField()) == false)
				{
					DataTable dtMain = myds.GetTableByName("MainTable");
					if (dtMain.Columns.contains(frmNode.getBillNoField()) == true)
					{
						dtMain.Rows.get(0).setValue(frmNode.getBillNoField(),wk.GetValStringByKey("BillNo"));
					}
				}

			}

				///#endregion 增加流程节点表单绑定信息.

			// myds.WriteXml("c:/11.xml");

			return myds;
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(Arrays.toString(ex.getStackTrace()));
			throw new RuntimeException("generoWorkNode@" + ex.getMessage());
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取流程催办消息
	 * @param gwf
	 * @return
	 */
	public static DataTable GetFlowPressMsg(GenerWorkFlow gwf)
	{
		DataTable dtPree = new DataTable();
		dtPree.TableName = "PressMsg";
		dtPree.Columns.Add("Title", String.class);
		dtPree.Columns.Add("Msg", String.class);

		DataRow drMsg = null;
		String sqlPress = "SELECT MobileInfo FROM Sys_SMS WHERE SendTo='" + WebUser.getNo() + "' AND MsgType='DoPress' AND WorkID=" + gwf.getWorkID() + "  AND IsAlert=0 Order By RDT DESC";
		DataTable dtPress = DBAccess.RunSQLReturnTable(sqlPress);
		if (dtPress.Rows.size() > 0)
		{
			drMsg = dtPree.NewRow();
			drMsg.setValue("Title", "催办信息");
			drMsg.setValue("Msg", dtPress.Rows.get(0).getValue(0).toString());
			dtPree.Rows.add(drMsg);
			DBAccess.RunSQL("UPDATE Sys_SMS SET IsAlert=1  WHERE SendTo='" + WebUser.getNo() + "' AND MsgType='DoPress' AND WorkID=" + gwf.getWorkID() + "  AND IsAlert=0");
		}

		return dtPree;
	}

	public static DataTable GetFlowAlertMsg(GenerWorkFlow gwf, Node nd, String fk_flow, long workID, long fid) throws Exception {
		String sql = "";
		DataTable dt = null;
		DataTable dtAlert = new DataTable();
		dtAlert.TableName = "AlertMsg";
		dtAlert.Columns.Add("Title", String.class);
		dtAlert.Columns.Add("Msg", String.class);
		dtAlert.Columns.Add("URL", String.class);
		DataRow drMsg = null;
		int sta = gwf.GetParaInt("HungupSta", 0);
		switch (gwf.getWFState())
		{
			case Runing:
				drMsg = dtAlert.NewRow();
				drMsg.setValue("Title", "挂起信息");
				if (sta == 2 && gwf.getNodeID() == gwf.GetParaInt("HungupNodeID", 0))
				{
					drMsg.setValue("Msg", "您的工作挂起被拒绝，拒绝原因:" + gwf.GetParaString("HungupCheckMsg"));
					dtAlert.Rows.add(drMsg);
				}
				break;
			case Hungup:
				if (sta == -1)
				{
					break;
				}
				drMsg = dtAlert.NewRow();
				drMsg.setValue("Title", "挂起信息");
				if (sta == 0)
				{
					String msg1 = "您的工单在挂起状态，等待审批，挂起原因：" + gwf.GetParaString("HungupNote");
					if (gwf.GetParaInt("HungupWay", 0) == 1)
					{
						msg1 += "指定时间解除:" + gwf.GetParaString("1@HungupRelDate");
					}
					else
					{
						msg1 += "无解除时间.";
					}
					drMsg.setValue("Msg", msg1);

					drMsg.setValue("Msg", msg1); //  "您的工作在挂起状态，等待审批，挂起原因：" + gwf.GetParaString("HungupNote");
				}

				if (sta == 1)
				{
					drMsg.setValue("Msg", "您的工作在挂起获得同意.");
				}

				if (sta == 2)
				{
					drMsg.setValue("Msg", "您的工作在挂起被拒绝，拒绝原因:" + gwf.GetParaString("HungupCheckMsg"));
				}

				dtAlert.Rows.add(drMsg);
				break;
			case AskForReplay: // 返回加签的信息.
				String mysql = "SELECT Msg,EmpFrom,EmpFromT,RDT FROM ND" + Integer.parseInt(fk_flow) + "Track WHERE WorkID=" + workID + " AND " + TrackAttr.ActionType + "=" + ActionType.ForwardAskfor.getValue();
				DataTable mydt = DBAccess.RunSQLReturnTable(mysql);

				for (DataRow dr : mydt.Rows)
				{
					String msgAskFor = dr.getValue(0).toString();
					String worker = dr.getValue(1).toString();
					String workerName = dr.getValue(2).toString();
					String rdt = dr.getValue(3).toString();

					drMsg = dtAlert.NewRow();
					drMsg.setValue("Title", worker + "," + workerName + "回复信息:");
					drMsg.setValue("Msg", DataType.ParseText2Html(msgAskFor) + "<br>" + rdt);
					dtAlert.Rows.add(drMsg);
				}
				break;
			case Askfor: //加签.

				sql = "SELECT * FROM ND" + Integer.parseInt(fk_flow) + "Track WHERE WorkID=" + workID + " AND " + TrackAttr.ActionType + "=" + ActionType.AskforHelp.getValue();
				dt = DBAccess.RunSQLReturnTable(sql);
				for (DataRow dr : dt.Rows)
				{
					String msgAskFor = dr.getValue(TrackAttr.Msg).toString();
					String worker = dr.getValue(TrackAttr.EmpFrom).toString();
					String workerName = dr.getValue(TrackAttr.EmpFromT).toString();
					String rdt = dr.getValue(TrackAttr.RDT).toString();

					drMsg = dtAlert.NewRow();
					drMsg.setValue("Title", worker + "," + workerName + "请求加签:");
					drMsg.setValue("Msg", DataType.ParseText2Html(msgAskFor) + "<br>" + rdt + "<a href='./WorkOpt/AskForRe.htm?FK_Flow=" + fk_flow + "&FK_Node=" + nd.getNodeID() + "&WorkID=" + workID + "&FID=" + fid + "' >回复加签意见</a> --");
					dtAlert.Rows.add(drMsg);

				}
				// isAskFor = true;
				break;
			case ReturnSta:
				/* 如果工作节点退回了*/

				String trackTable = "ND" + Integer.parseInt(nd.getFlowNo()) + "Track";
				sql = "SELECT NDFrom,NDFromT,EmpFrom,EmpFromT,EmpTo,Msg,RDT FROM " + trackTable + " WHERE ActionType IN(2,201) ";

				//ReturnWorks ens = new ReturnWorks();
				if (nd.getHisRunModel() == RunModel.FL || nd.getHisRunModel() == RunModel.FHL)
				{
					sql += " AND (WorkID=" + workID + " OR FID=" + workID + ") AND NDTo=" + nd.getNodeID() + "  ORDER BY RDT ";
					/*QueryObject qo = new QueryObject(ens);
					qo.addLeftBracket();
					qo.AddWhere(ReturnWorkAttr.WorkID, realWorkID);
					qo.addOr();
					qo.AddWhere(ReturnWorkAttr.FID, realWorkID);
					qo.addRightBracket();
					qo.addAnd();
					qo.AddWhere(ReturnWorkAttr.ReturnToNode,nd.getNodeID());
					qo.addOrderBy("RDT");
					qo.DoQuery();*/
				}
				else
				{
					sql += " AND WorkID=" + workID + "  AND NDTo=" + nd.getNodeID() + "  ORDER BY RDT ";
					//ens.Retrieve(ReturnWorkAttr.WorkID, realWorkID, ReturnWorkAttr.ReturnToNode, nd.getNodeID(), "RDT");
				}
				DataTable dtt = DBAccess.RunSQLReturnTable(sql);

				String msgInfo = "";
				for (DataRow dr : dtt.Rows)
				{
					if (dr.getValue(4).toString().contains(WebUser.getNo()) == true)
					{
						msgInfo += "来自节点：" + dr.getValue(1).toString() + "@退回人：" + dr.getValue(3).toString() + "@退回日期：" + dr.getValue(6).toString();
						msgInfo += "@退回原因：" + dr.getValue(5).toString();
						msgInfo += "<hr/>";
					}
				}

				msgInfo = msgInfo.replace("@", "<br>");
				if (DataType.IsNullOrEmpty(msgInfo) == false)
				{
					String str = nd.getReturnAlert();
					if (DataType.IsNullOrEmpty(str)==false && str.equals("null")==false)
					{
						str = str.replace("~", "'");
						str = str.replace("@PWorkID", String.valueOf(workID));
						str = str.replace("@PNodeID",String.valueOf(nd.getNodeID()));
						str = str.replace("@FK_Node", String.valueOf(nd.getNodeID()));

						str = str.replace("@PFlowNo", fk_flow);
						str = str.replace("@FK_Flow", fk_flow);
						str = str.replace("@PWorkID", String.valueOf(workID));

						str = str.replace("@WorkID", String.valueOf(workID));
						str = str.replace("@OID", String.valueOf(workID));

						drMsg = dtAlert.NewRow();
						drMsg.setValue("Title", "退回信息");
						drMsg.setValue("Msg", msgInfo + "\t\n" + str);
						dtAlert.Rows.add(drMsg);
					}
					else
					{
						drMsg = dtAlert.NewRow();
						drMsg.setValue("Title", "退回信息");
						drMsg.setValue("Msg", msgInfo + "\t\n" + str);
						dtAlert.Rows.add(drMsg);
					}
				}
				break;
			case Shift:
				/* 判断移交过来的。 */
				String sqlshift = "SELECT * FROM ND" + Integer.parseInt(fk_flow) + "Track WHERE ACTIONTYPE=3 AND WorkID=" + workID + " AND NDFrom='" + gwf.getNodeID() + "' ORDER BY RDT DESC ";
				DataTable dtshift = DBAccess.RunSQLReturnTable(sqlshift);
				String msg = "";
				if (dtshift.Rows.size() >= 1)
				{
					msg = "";
					drMsg = dtAlert.NewRow();
					drMsg.setValue("Title", "移交信息");
					//  msg = "<h3>移交信息 </h3><hr/>";
					for (DataRow dr : dtshift.Rows)
					{
						if (Objects.equals(WebUser.getNo(), dr.getValue(TrackAttr.EmpTo).toString()))
						{
							String empFromT = dr.getValue(TrackAttr.EmpFromT).toString();
							String empToT = dr.getValue(TrackAttr.EmpToT).toString();
							String msgShift = dr.getValue(TrackAttr.Msg).toString();
							String rdt = dr.getValue(TrackAttr.RDT).toString();
							if (Objects.equals(msgShift, "undefined"))
							{
								msgShift = "无";
							}

							msg += "移交人：" + empFromT + "@接受人：" + empToT + "@移交日期：" + rdt;
							msg += "@移交原因：" + msgShift;
							msg += "<hr/>";
						}
					}

					msg = msg.replace("@", "<br>");

					drMsg.setValue("Msg", msg);
					if (!DataType.IsNullOrEmpty(msg))
					{
						dtAlert.Rows.add(drMsg);
					}
				}
				break;
			default:
				break;
		}
		//获取催办信息
		//PushMsgs pms = new PushMsgs();
		//if (pms.Retrieve(PushMsgAttr.NodeID, gwf.getNodeID(), PushMsgAttr.EventNo, EventListNode.PressAfter, null) > 0)
		//{
		//	String sqlPress = "SELECT MobileInfo FROM Sys_SMS WHERE MsgType='DoPress' AND WorkID=" + gwf.getWorkID() + "  AND IsAlert=0 Order By RDT DESC";
		//	DataTable dtPress = DBAccess.RunSQLReturnTable(sqlPress);
		//	if (dtPress.Rows.size() > 0)
		//	{
		//		drMsg = dtAlert.NewRow();
		//		drMsg.setValue("Title", "催办信息");
		//		drMsg.setValue("Msg", dtPress.Rows.get(0).getValue(0).toString());
		//		dtAlert.Rows.add(drMsg);
		//	}
		//	DBAccess.RunSQL("UPDATE Sys_SMS SET IsAlert=1  WHERE MsgType='DoPress' AND WorkID=" + gwf.getWorkID() + "  AND IsAlert=0");
		//}
		//拒绝挂起
		String sqlRejectHungup = "SELECT MobileInfo FROM Sys_SMS WHERE MsgType='RejectHungup' and WorkID=" + gwf.getWorkID() + "  AND IsAlert=0";
		DataTable dtRejectHungup = DBAccess.RunSQLReturnTable(sqlRejectHungup);
		if (dtRejectHungup.Rows.size() > 0)
		{
			drMsg = dtAlert.NewRow();
			drMsg.setValue("Title", "拒绝挂起信息");
			for (DataRow item : dtRejectHungup.Rows)
			{
				drMsg.setValue("Msg", drMsg.getValue("Msg") + "<hr/> " + item.getValue(0).toString());
			}
			dtAlert.Rows.add(drMsg);
		}
		DBAccess.RunSQL("UPDATE Sys_SMS SET IsAlert=1  WHERE MsgType='RejectHungup' AND WorkID=" + gwf.getWorkID() + "  AND IsAlert=0");

		return dtAlert;
	}

	/**
	 检查流程发起限制

	 @param flow 流程
	 @return
	*/
	public static boolean CheckIsCanStartFlow_InitStartFlow(Flow flow) throws Exception {
		StartLimitRole role = flow.getStartLimitRole();
		if (role == StartLimitRole.None)
			return true;

		String sql = "";
		String ptable = flow.getPTable();
		try
		{
				///#region 按照时间的必须是，在表单加载后判断, 不管用户设置是否正确.
			Date dtNow = new Date();
			if (role == StartLimitRole.Day)
			{
				/* 仅允许一天发起一次 */
				sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE RDT LIKE '" + DataType.getCurrentDate() + "%' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
				if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
				{
					if (DataType.IsNullOrEmpty(flow.getStartLimitPara()))
					{
						return true;
					}

					//判断时间是否在设置的发起范围内. 配置的格式为 @11:00-12:00@15:00-13:45
					String[] strs = flow.getStartLimitPara().split("[@]", -1);
					for (String str : strs)
					{
						if (DataType.IsNullOrEmpty(str))
						{
							continue;
						}
						String[] timeStrs = str.split("[-]", -1);
						String tFrom = DataType.getCurrentDate() + " " + timeStrs[0].trim();
						String tTo = DataType.getCurrentDate() + " " + timeStrs[1].trim();
						if (DataType.ParseSysDateTime2DateTime(tFrom).compareTo(dtNow) <= 0 && DataType.ParseSysDateTime2DateTime(tTo).compareTo(dtNow) >= 0)
						{
							return true;
						}
					}
					return false;
				}
				else
				{
					return false;
				}
			}

			if (role == StartLimitRole.Week)
			{
				/*
				 * 1, 找出周1 与周日分别是第几日.
				 * 2, 按照这个范围去查询,如果查询到结果，就说明已经启动了。
				 */
				sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE RDT >= '" + DataType.WeekOfMonday(dtNow) + "' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
				if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
				{
					if (DataType.IsNullOrEmpty(flow.getStartLimitPara()))
					{
						return true; //如果没有时间的限制.
					}

					//判断时间是否在设置的发起范围内.
					// 配置的格式为 @Sunday,11:00-12:00@Monday,15:00-13:45, 意思是.周日，周一的指定的时间点范围内可以启动流程.

					String[] strs = flow.getStartLimitPara().split("[@]", -1);
					for (String str : strs)
					{
						if (DataType.IsNullOrEmpty(str))
						{
							continue;
						}

						String weekStr = LocalDateTime.now().getDayOfWeek().toString().toLowerCase();
						if (str.toLowerCase().contains(weekStr) == false)
						{
							continue; // 判断是否当前的周.
						}

						String[] timeStrs = str.split("[,]", -1);
						String tFrom = DataType.getCurrentDate() + " " + timeStrs[0].trim();
						String tTo = DataType.getCurrentDate() + " " + timeStrs[1].trim();
						if (DataType.ParseSysDateTime2DateTime(tFrom).compareTo(dtNow) <= 0 && dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0)
						{
							return true;
						}
					}
					return false;
				}
				else
				{
					return false;
				}
			}

			// #warning 没有考虑到周的如何处理.

			if (role == StartLimitRole.Month)
			{
				sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE FK_NY = '" + DataType.getCurrentYearMonth() + "' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
				if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
				{
					if (DataType.IsNullOrEmpty(flow.getStartLimitPara()))
					{
						return true;
					}

					//判断时间是否在设置的发起范围内. 配置格式: @-01 12:00-13:11@-15 12:00-13:11 , 意思是：在每月的1号,15号 12:00-13:11可以启动流程.
					String[] strs = flow.getStartLimitPara().split("[@]", -1);
					for (String str : strs)
					{
						if (DataType.IsNullOrEmpty(str))
						{
							continue;
						}
						String[] timeStrs = str.split("[-]", -1);
						String tFrom =DataType.getCurrentDateByFormart("yyyy-MM-") + " " + timeStrs[0].trim();
						String tTo =DataType.getCurrentDateByFormart("yyyy-MM-") + " " + timeStrs[1].trim();
						if (DataType.ParseSysDateTime2DateTime(tFrom).compareTo(dtNow) <= 0 && dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0)
						{
							return true;
						}
					}
					return false;
				}
				else
				{
					return false;
				}
			}

			if (role == StartLimitRole.JD)
			{
				sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE FK_NY = '" + DataType.getCurrentAPOfJD() + "' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
				if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
				{
					if (DataType.IsNullOrEmpty(flow.getStartLimitPara()))
					{
						return true;
					}

					//判断时间是否在设置的发起范围内.
					String[] strs = flow.getStartLimitPara().split("[@]", -1);
					for (String str : strs)
					{
						if (DataType.IsNullOrEmpty(str))
						{
							continue;
						}
						String[] timeStrs = str.split("[-]", -1);
						String tFrom = DataType.getCurrentDateByFormart("yyyy-") + " " + timeStrs[0].trim();
						String tTo = DataType.getCurrentDateByFormart("yyyy-") + " " + timeStrs[1].trim();
						if (DataType.ParseSysDateTime2DateTime(tFrom).compareTo(dtNow) <= 0 && dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0)
						{
							return true;
						}
					}
					return false;
				}
				else
				{
					return false;
				}
			}

			if (role == StartLimitRole.Year)
			{
				sql = "SELECT COUNT(*) as Num FROM " + ptable + " WHERE FK_NY LIKE '" + DataType.getCurrentYear() + "%' AND WFState NOT IN(0,1) AND FlowStarter='" + WebUser.getNo() + "'";
				if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
				{
					if (DataType.IsNullOrEmpty(flow.getStartLimitPara()))
					{
						return true;
					}

					//判断时间是否在设置的发起范围内.
					String[] strs = flow.getStartLimitPara().split("[@]", -1);
					for (String str : strs)
					{
						if (DataType.IsNullOrEmpty(str))
						{
							continue;
						}
						String[] timeStrs = str.split("[-]", -1);
						String tFrom = DataType.getCurrentDateByFormart("yyyy-") + " " + timeStrs[0].trim();
						String tTo =DataType.getCurrentDateByFormart("yyyy-") + " " + timeStrs[1].trim();
						if (DataType.ParseSysDateTime2DateTime(tFrom).compareTo(dtNow) <= 0 && dtNow.compareTo(DataType.ParseSysDateTime2DateTime(tTo)) >= 0)
						{
							return true;
						}
					}
					return false;
				}
				else
				{
					return false;
				}
			}

				///#endregion 按照时间的必须是，在表单加载后判断, 不管用户设置是否正确.


			//为子流程的时候，该子流程只能被调用一次.
			if (role == StartLimitRole.OnlyOneSubFlow)
			{

				if (SystemConfig.isBSsystem() == true)
				{

					String pflowNo = ContextHolderUtils.getRequest().getParameter("PFlowNo");
					String pworkid = ContextHolderUtils.getRequest().getParameter("PWorkID");

					if (pworkid == null)
					{
						return true;
					}

					//sql = "SELECT Starter, RDT FROM WF_GenerWorkFlow WHERE PWorkID=" + pworkid + " AND FK_Flow='" + flow.getNo() + "'";
					Paras paras = new Paras();
					paras.SQL="SELECT Starter, RDT FROM WF_GenerWorkFlow WHERE PWorkID=" + SystemConfig.getAppCenterDBVarStr() + "PWorkID AND FK_Flow=" +SystemConfig.getAppCenterDBVarStr() + "FK_Flow";
					paras.Add("PWorkID",pworkid);
					paras.Add("FK_Flow",flow.getNo());
					DataTable dt = DBAccess.RunSQLReturnTable(paras);
					if (dt.Rows.size() == 0 || dt.Rows.size() == 1)
					{
						return true;
					}

					//  String title = dt.Rows.get(0).getValue("Title").toString();
					String starter = dt.Rows.get(0).getValue("Starter").toString();
					String rdt = dt.Rows.get(0).getValue("RDT").toString();
					return false;
					//throw new Exception(flow.StartLimitAlert + "@该子流程已经被[" + starter + "], 在[" + rdt + "]发起，系统只允许发起一次。");
				}
			}

			// 配置的sql,执行后,返回结果是 0 .
			if (role == StartLimitRole.ResultIsZero)
			{
				sql = bp.wf.Glo.DealExp(flow.getStartLimitPara(), null);
				if (DBAccess.RunSQLReturnValInt(sql, 0) == 0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}

			// 配置的sql,执行后,返回结果是 <> 0 .
			if (role == StartLimitRole.ResultIsNotZero)
			{
				if (DataType.IsNullOrEmpty(flow.getStartLimitPara()) == true)
				{
					return true;
				}

				sql = bp.wf.Glo.DealExp(flow.getStartLimitPara(), null);
				if (DBAccess.RunSQLReturnValInt(sql, 0) != 0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}

			return true;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("err@发起限制规则[" + role.toString() + "]出现错误:" + ex.getMessage());
		}
	}

}
