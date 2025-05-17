package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.wf.Glo;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/**
 页面功能实体
*/
public class WF_Admin_AttrNode_FrmSln extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public WF_Admin_AttrNode_FrmSln()
	{

	}

	/**
	 嵌入式表单-设置该流程的所有节点都是用该方案。

	 @return
	 */
	public final String SelfForm_SetAllNodeFrmUseThisSln() throws Exception {
		String nodeID = GetRequestVal("FK_Node");
		Node currNode = new Node(this.getNodeID());
		BtnLab btn  = new BtnLab(this.getNodeID());
		String flowNo = currNode.getFlowNo();
		Nodes nds = new Nodes();
		nds.Retrieve("FK_Flow", flowNo, null);

		for (int i = 0; i < nds.size(); i++)
		{

			Node jsNode = nds.get(i) instanceof Node ? (Node)nds.get(i) : null;
			if (jsNode.getNodeID() == currNode.getNodeID())
			{
				continue;
			}
			BtnLab jsonBtn  = new BtnLab(jsNode.getNodeID());

			//修改按钮 保存，轨迹，回滚
			DBAccess.RunSQL("UPDATE WF_Node SET SaveEnable="+btn.getSaveEnable()+" WHERE NodeID ='"+jsNode.getNodeID()+"'");
			DBAccess.RunSQL("UPDATE WF_Node SET TrackEnable="+btn.getTrackEnable()+" WHERE NodeID ='"+jsNode.getNodeID()+"'");
			DBAccess.RunSQL("UPDATE WF_Node SET RollbackEnable="+btn.getRollbackEnable()+" WHERE NodeID ='"+jsNode.getNodeID()+"'");

			//修改表单
			jsNode.setFormType(currNode.getFormType());
			jsNode.setFormUrl( currNode.getFormUrl());
			jsNode.SetValByKey(NodeAttr.FormUrlMobile, currNode.GetValStringByKey(NodeAttr.FormUrlMobile));
			jsNode.SetPara("SelfFormEnRoot", currNode.GetParaInt("SelfFormEnRoot"));
			jsNode.Update();
		}

		return "执行成功.";
	}

	/**
	 设置该流程的所有节点都是用该方案。

	 @return
	*/
	public final String RefOneFrmTree_SetAllNodeFrmUseThisSln() throws Exception {

		Node currNode = new Node(this.getNodeID());
		String flowNo = currNode.getFlowNo();
		Nodes nds = new Nodes();
		nds.Retrieve("FK_Flow", flowNo, null);

		//求出来组件s.
		MapAttrs attrOfCommpents = new MapAttrs();
		QueryObject qo = new QueryObject(attrOfCommpents);
		qo.AddWhere(MapAttrAttr.FK_MapData, currNode.getNodeFrmID());
		qo.addAnd();
		qo.AddWhere(MapAttrAttr.UIContralType, ">=", 6);
		qo.DoQuery();

		for (int i = 0; i < nds.size(); i++)
		{

			Node jsNode = nds.get(i) instanceof Node ? (Node)nds.get(i) : null;
			if (jsNode.getNodeID() == currNode.getNodeID())
			{
				continue;
			}

			//修改表单属性
			jsNode.setFormType(currNode.getFormType());

			jsNode.setNodeFrmID( currNode.getNodeFrmID());
			jsNode.Update();

			//节点表单属性
			//先删除掉已有的，避免换绑时出现垃圾数据.
			FrmNodes ens = new FrmNodes();
			ens.Retrieve("FK_Node", jsNode.getNodeID(), null);

			//是不是该frmNode已经存在？
			boolean isHave = false;
			for (int idx = 0; idx < ens.size(); idx++)
			{
				FrmNode en = ens.get(idx) instanceof FrmNode ? (FrmNode)ens.get(idx) : null;
				if (!Objects.equals(en.getFrmID(), currNode.getNodeFrmID()))
				{
					FrmNode Frm = new FrmNode(en.getMyPK());
					Frm.Delete();
					continue;
				}
				isHave = true;
			}
			if (isHave == true)
			{
				continue; //已经存在就不处理.
			}

			FrmNode frmNode = new FrmNode();
			frmNode.setMyPK(jsNode.getNodeFrmID() + "_" + jsNode.getNodeID() + "_" + jsNode.getFlowNo());
			frmNode.setNodeID(jsNode.getNodeID());
			frmNode.setFlowNo(jsNode.getFlowNo());
			frmNode.setFrmID(jsNode.getNodeFrmID());

			//判断是否为开始节点
			String nodeID1 = String.valueOf(jsNode.getNodeID());
			if (Objects.equals(nodeID1.substring(nodeID1.length() - 2), "01"))
			{
				frmNode.setFrmSln(FrmSln.Default); //默认方案
			}
			else
			{
				frmNode.setFrmSln(FrmSln.Readonly); //只读方案
			}
			frmNode.Insert();

			//设置组件都是可用的.
			FrmField ff = new FrmField();
			for (MapAttr attr : attrOfCommpents.ToJavaList())
			{
				ff.setUIVisible(true);
				ff.setKeyOfEn(attr.getKeyOfEn());
				ff.setFlowNo(currNode.getFlowNo());
				ff.setNodeID(jsNode.getNodeID());
				ff.setFrmID(jsNode.getNodeFrmID()); //表单ID.
				ff.setMyPK(ff.getFrmID() + "_" + ff.getNodeID() + "_" + ff.getKeyOfEn());
				if (ff.IsExits() == false)
				{
					ff.Insert();
				}
			}
		}

		//更新模式.
		Flow flow = new Flow(currNode.getFlowNo());
		flow.setFrmUrl(currNode.getNodeFrmID());
		flow.setFlowDevModel(FlowDevModel.RefOneFrmTree);
		flow.Update();

		return "执行成功,建议您关闭浏览器重新打卡.";
	}
	/**
	 获得下拉框的值.

	 @return
	*/
	public final String BatchEditSln_InitDDLData() throws Exception {
		DataSet ds = new DataSet();

		SysEnums ses = new SysEnums("FrmSln");
		ds.Tables.add(ses.ToDataTableField("FrmSln"));

		SysEnums se1s = new SysEnums("FWCSta");
		ds.Tables.add(se1s.ToDataTableField("FWCSta"));

		//签字类型.
		SysEnums myses = new SysEnums("SigantureEnabel");
		ds.Tables.add(myses.ToDataTableField("SigantureEnabel"));

		String sql = Glo.getSQLOfCheckField().replace("@FK_Frm", this.getFrmID());
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
		}

		dt.TableName = "CheckFields";
		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

	/**
	 返回

	 @return
	*/
	public final String RefOneFrmTreeFrms_Init()
	{
		String sql = "";
		String key = GetRequestVal("KeyWord"); //查询的关键字
		//单机模式下
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql += "SELECT  b.NAME AS SortName, a.no AS \"No\", A.name AS \"Name\",";
			sql += "A.PTable,";
			sql += "A.OrgNo ";
			sql += "FROM ";
			sql += "Sys_MapData A, ";
			sql += "Sys_FormTree B ";
			sql += " WHERE ";
			sql += " A.FK_FormTree = B.NO ";
			//sql += " AND B.setOrgNo('" + WebUser.getOrgNo() + "'";
			if (DataType.IsNullOrEmpty(key) == false)
			{
				sql += " AND A.Name like '%" + key + "%'";
			}
			sql += "ORDER BY B.IDX,A.IDX";

		}

		// 云服务器环境下
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			sql += "SELECT  b.NAME AS SortName, a.no AS \"No\", A.name AS \"Name\", ";
			sql += "A.PTable, ";
			sql += "A.OrgNo ";
			sql += "FROM ";
			sql += "Sys_MapData A, ";
			sql += "Sys_FormTree B ";
			sql += " WHERE ";
			sql += " A.FK_FormTree = B.NO ";
			sql += " AND B.OrgNo = '" + WebUser.getOrgNo() + "' ";
			if (DataType.IsNullOrEmpty(key) == false)
			{
				sql += " AND A.Name like '%" + key + "%'";
			}
			sql += "ORDER BY B.IDX,A.IDX";
		}

		//集团模式下
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			sql += " SELECT  b.NAME AS SortName, a.no AS \"No\", A.name AS \"Name\",";
			sql += "A.PTable,";
			sql += "A.OrgNo, '" + WebUser.getOrgName() + "' as OrgName ";
			sql += "FROM ";
			sql += "Sys_MapData A, ";
			sql += "Sys_FormTree B, ";
			sql += "Port_Org C ";
			sql += " WHERE ";
			sql += " A.FK_FormTree = B.NO ";
			sql += " AND B.OrgNo = '" + WebUser.getOrgNo() + "' ";
			sql += " AND C.No =B.OrgNo ";
			if (DataType.IsNullOrEmpty(key) == false)
			{
				sql += " AND A.Name like '%" + key + "%'";
			}

			sql += " UNION  ";

			sql += " SELECT  '- 共享 -' AS SortName, a.no AS \"No\", A.name AS \"Name\", ";
			sql += " A.PTable, A.OrgNo, '其他组织' as OrgName ";
			sql += " FROM ";
			sql += " Sys_MapData A,  WF_FrmOrg B, Port_Org C ";
			sql += " WHERE ";
			sql += "  A.setNo(B.FrmID  AND B.OrgNo=C.No ";
			sql += "  AND B.OrgNo = '" + WebUser.getOrgNo() + "' ";
			if (DataType.IsNullOrEmpty(key) == false)
			{
				sql += " AND A.Name like '%" + key + "%'";
			}
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);


			///#warning 需要判断不同的数据库类型
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
		{
			dt.Columns.get("SORTNAME").ColumnName = "SortName";
			dt.Columns.get("NO").ColumnName = "No";
			dt.Columns.get("NAME").ColumnName = "Name";
			dt.Columns.get("PTABLE").ColumnName = "PTable";
			dt.Columns.get("ORGNO").ColumnName = "OrgNo";
		}
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
		{
			dt.Columns.get("sortname").ColumnName = "SortName";
			dt.Columns.get("no").ColumnName = "No";
			dt.Columns.get("name").ColumnName = "Name";
			dt.Columns.get("ptable").ColumnName = "PTable";
			dt.Columns.get("orgno").ColumnName = "OrgNo";
		}

		return bp.tools.Json.ToJson(dt);
	}

}
