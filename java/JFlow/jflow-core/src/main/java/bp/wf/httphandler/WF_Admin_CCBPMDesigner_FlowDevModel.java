package bp.wf.httphandler;

import bp.da.*;
import bp.sys.MapData;
import bp.web.*;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;

/**
 页面功能实体
*/
public class WF_Admin_CCBPMDesigner_FlowDevModel extends bp.difference.handler.DirectoryPageBase
{

		///#region 变量.
	/**
	 类别
	*/
	public final String getSortNo()
	{
		return this.GetRequestVal("SortNo");
	}
	/**
	 流程名称
	*/
	public final String getFlowName()
	{
		return this.GetRequestVal("FlowName");
	}
	public final FlowDevModel getFlowDevModel()
	{
		return FlowDevModel.forValue(this.GetRequestValInt("FlowDevModel"));
	}

		///#endregion 变量.

	/**
	 构造函数
	*/
	public WF_Admin_CCBPMDesigner_FlowDevModel()
	{
	}
	/**
	 获取默认的开发模式.

	 @return
	*/
	public final String Default_Init()
	{
		String sql = "SELECT val FROM Sys_GloVar WHERE No='FlowDevModel_" + WebUser.getNo() + "'";
		int val = DBAccess.RunSQLReturnValInt(sql, 1);
		return String.valueOf(val);
	}

	/**
	 * SaaS模式管理页面给租户创建流程
	 * @return
	 * @throws Exception
	 */
	public final String FlowDevModel_Save_SaaS() throws Exception
	{
		String sortNo = this.getSortNo();
		String flowNo = this.FlowDevModel_Save();
		Flow flow = new Flow(flowNo);
		flow.setOrgNo(this.GetRequestVal("SortNo"));
		flow.Update();
		//检查流程目录是否存在
		FlowSort flowSort = new FlowSort();
		flowSort.setNo(sortNo);
		if (flowSort.RetrieveFromDBSources() == 0)
		{
			String orgNo = flow.getOrgNo();
			bp.cloud.Org org = new bp.cloud.Org();
			org.setNo(this.GetRequestVal(orgNo));
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织不存在，组织编号[" + orgNo + "]";
			}
			flowSort.setName(org.getName());
			flowSort.setOrgNo(orgNo);
			flowSort.Insert();
		}
		return flowNo;
	}
	public final String FlowDevModel_Save() throws Exception {
		String sortNo = GetRequestVal("SortNo");
		String flowName = GetRequestVal("FlowName");
		String url = GetRequestVal("Url");
		String frmURL = GetRequestVal("FrmUrl");
		String frmID = GetRequestVal("FrmID");
		String frmPK = GetRequestVal("FrmPK"); //自定义表单主键.

		if (DataType.IsNullOrEmpty(frmURL) == true)
		{
			frmURL = frmID;
		}
		//执行创建流程模版.
		String flowNo = TemplateGlo.NewFlowTemplate(sortNo, flowName, DataStoreModel.ByCCFlow, null, null);
		Flow fl = new Flow(flowNo);
		fl.setFlowDevModel(this.getFlowDevModel()); //流程开发模式.
		if (this.getFlowDevModel() == FlowDevModel.JiJian)
		{
			frmURL = "ND" + Integer.parseInt(flowNo + "01");
		}

		fl.SetPara("FrmPK", frmPK); //自定义主键模式的表单.
		fl.setFrmUrl(frmURL);
		fl.Update();
		//发起测试人为当前登录人No
		DBAccess.RunSQL("UPDATE WF_Flow SET Tester = '" + WebUser.getNo() + "' WHERE No='" + flowNo + "'");

		//设置极简类型的表单信息.
		if (this.getFlowDevModel() == FlowDevModel.JiJian)
		{
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, fl.getNo(), null);
			for (Node nd : nds.ToJavaList())
			{
				nd.setNodeFrmID( "ND" + Integer.parseInt(fl.getNo()) + "01");
				if (nd.getItIsStartNode() == false)
				{
					nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);

					FrmNode fn = new FrmNode();
					fn.setFrmID(nd.getNodeFrmID());
					fn.setEnableFWC(FrmWorkCheckSta.Enable);
					fn.setNodeID(nd.getNodeID());
					fn.setFlowNo(flowNo);
					fn.setFrmSln(FrmSln.Readonly);
					fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
					//执行保存.
					fn.Save();
				}
				nd.DirectUpdate();
			}
		}

		//设置累加类型的表单信息.
		if (this.getFlowDevModel() == FlowDevModel.FoolTruck)
		{
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, fl.getNo(), null);
			for (Node nd : nds.ToJavaList())
			{
				//表单方案的保存
				FrmNode fn = new FrmNode();
				fn.setFrmID(nd.getNodeFrmID());
				//fn.IsEnableFWC = FrmWorkCheckSta.Enable;
				fn.setNodeID(nd.getNodeID());
				fn.setFlowNo(flowNo);
				fn.setFrmSln(FrmSln.Readonly);
				fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
				//执行保存.
				fn.Save();
				nd.setHisFormType(NodeFormType.FoolTruck);
				nd.DirectUpdate();

			}
		}
		//设置绑定表单库的表单信息.
		if (this.getFlowDevModel() == FlowDevModel.RefOneFrmTree)
		{
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, fl.getNo(), null);
			MapData md = new MapData(fl.getFrmUrl());
			md.RetrieveFromDBSources();
			for (Node nd : nds.ToJavaList())
			{
				nd.setNodeFrmID( fl.getFrmUrl());
				nd.setNodeFrmIDT( md.getName());
				if (nd.getItIsStartNode() == true)
				{
					nd.setFrmWorkCheckSta (FrmWorkCheckSta.Disable);
				}
				else
				{
					nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);
				}
				nd.setHisFormType(NodeFormType.RefOneFrmTree);
				nd.DirectUpdate();

				FrmNode fn = new FrmNode();
				fn.setFrmID(nd.getNodeFrmID());
				if (nd.getItIsStartNode() == true)
				{
					fn.setEnableFWC(FrmWorkCheckSta.Disable);
					fn.setFrmSln(FrmSln.Default);
				}
				else
				{
					fn.setEnableFWC(FrmWorkCheckSta.Enable);
					fn.setFrmSln(FrmSln.Readonly);
				}

				fn.setNodeID(nd.getNodeID());
				fn.setFlowNo(flowNo);
				//fn.setFrmSln(FrmSln.Readonly);
				fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
				//执行保存.
				fn.Save();
			}
		}

		//绑定表单库的表单,现在绑定了一个表单
		if (this.getFlowDevModel() == FlowDevModel.FrmTree)
		{
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, fl.getNo(), null);
			for (Node nd : nds.ToJavaList())
			{
				//nd.setNodeFrmID(fl.getFrmUrl());
				if (nd.getItIsStartNode() == true)
				{
					nd.setFrmWorkCheckSta (FrmWorkCheckSta.Disable);
				}
				else
				{
					nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);
				}
				nd.setHisFormType(NodeFormType.SheetTree);
				nd.DirectUpdate();
				FrmNode fn = new FrmNode();
				String[] frmIDs = fl.getFrmUrl().split("[,]", -1);

				for (String str : frmIDs)
				{
					if (DataType.IsNullOrEmpty(str) == true)
					{
						continue;
					}

					fn.setFrmID(str);
					fn.setFrmNameShow(DBAccess.RunSQLReturnString("SELECT Name FROM Sys_MapData WHERE No='" + str + "'"));
					if (nd.getItIsStartNode() == true)
					{
						fn.setEnableFWC(FrmWorkCheckSta.Disable);
						fn.setFrmSln(FrmSln.Default);
					}
					else
					{
						fn.setEnableFWC(FrmWorkCheckSta.Enable);
						fn.setFrmSln(FrmSln.Readonly);
					}

					fn.setNodeID(nd.getNodeID());
					fn.setFlowNo(flowNo);
					fn.setFrmSln(FrmSln.Readonly);
					fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
					fn.Save(); //执行保存.
				}
			}
		}
		if (this.getFlowDevModel() == FlowDevModel.SDKFrmSelfPK || this.getFlowDevModel() == FlowDevModel.SDKFrmWorkID)
		{
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, fl.getNo(), null);
			for (Node nd : nds.ToJavaList())
			{
				nd.setHisFormType(NodeFormType.SDKForm);
				nd.setFormUrl(fl.getFrmUrl());
				nd.DirectUpdate();
			}
		}
		if (this.getFlowDevModel() == FlowDevModel.SelfFrm)
		{
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, fl.getNo(), null);
			for (Node nd : nds.ToJavaList())
			{
				nd.setHisFormType(NodeFormType.SDKForm);
				nd.setFormUrl(fl.getFrmUrl());
				nd.DirectUpdate();
			}
		}
		if (this.getFlowDevModel() == FlowDevModel.EntityTS)
		{
			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, fl.getNo(), null);
			for (Node nd : nds.ToJavaList())
			{
				nd.setHisFormType(NodeFormType.EntityTS);
				nd.setFormUrl(fl.getFrmUrl());
				if (nd.getItIsStartNode() == true)
				{
					nd.setFrmWorkCheckSta (FrmWorkCheckSta.Disable);
				}
				else
				{
					nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);
				}
				nd.DirectUpdate();

				FrmNode fn = new FrmNode();
				fn.setFrmID(fl.getFrmUrl());
				if (nd.getItIsStartNode() == true)
				{
					fn.setEnableFWC(FrmWorkCheckSta.Disable);
					fn.setFrmSln(FrmSln.Default);
				}
				else
				{
					fn.setEnableFWC(FrmWorkCheckSta.Enable);
					fn.setFrmSln(FrmSln.Readonly);
				}

				fn.setNodeID(nd.getNodeID());
				fn.setFlowNo(flowNo);
				fn.setFrmSln(FrmSln.Readonly);
				fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());
				//执行保存.
				fn.Save();
			}
		}

		/**保存模式.
		*/
		SaveModel(this.getFlowDevModel());

		//返回流程编号
		return flowNo;
	}
	/**
	 保存模式

	 @param val
	*/
	public final void SaveModel(FlowDevModel val)
	{
		String pk = "FlowDevModel_" + WebUser.getNo();

		String sql = "SELECT Val FROM Sys_GloVar WHERE No='" + pk + "'";
		int valInt = DBAccess.RunSQLReturnValInt(sql, 1);
		if (valInt == val.getValue())
		{
			return;
		}

		sql = "UPDATE Sys_GloVar SET Val=" + val.getValue() + " WHERE No='" + pk + "'";
		int myval = DBAccess.RunSQL(sql);
		if (myval == 1)
		{
			return;
		}

		sql = "INSERT INTO Sys_GloVar (No,Name,Val) VALUES('" + pk + "','FlowDevModel','" + val.getValue() + "')";
		DBAccess.RunSQL(sql);
	}
	/**
	 创建流程-早期版本模式

	 @return
	*/
	public final String Default_NewFlowMode_0() throws Exception {
		try
		{
			int runModel = this.GetRequestValInt("RunModel");
			String FlowName = this.GetRequestVal("FlowName");
			String FlowSort = this.GetRequestVal("FlowSort").trim();
			FlowSort = FlowSort.trim();

			//int DataStoreModel = this.GetRequestValInt("DataStoreModel");
			String PTable = this.GetRequestVal("PTable");
			String FlowMark = this.GetRequestVal("FlowMark");
			int FlowFrmModel = this.GetRequestValInt("FlowFrmModel");
			String FrmUrl = this.GetRequestVal("FrmUrl");
			String FlowVersion = this.GetRequestVal("FlowVersion");

			String flowNo = TemplateGlo.NewFlowTemplate(FlowSort, FlowName, bp.wf.template.DataStoreModel.SpecTable, PTable, FlowMark);

			Flow fl = new Flow(flowNo);


			//清空WF_Emp 的StartFlows ,让其重新计算.
			// DBAccess.RunSQL("UPDATE  WF_Emp Set StartFlows =''");
			return flowNo;
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

}
