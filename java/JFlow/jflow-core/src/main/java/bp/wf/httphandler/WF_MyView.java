package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.tools.Json;
import bp.web.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.wf.template.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;

import java.net.URLEncoder;
import java.util.*;
import java.time.*;

/**
 抄送处理类
*/
public class WF_MyView extends bp.difference.handler.DirectoryPageBase
{

	/**
	 抄送处理类
	*/
	public WF_MyView()
	{

	}


		///#region 表单查看.
	/**
	 表单数据

	 @return
	*/
	public final String MyFrm_Init_Data() throws Exception {
		String trackID = this.GetRequestVal("TrackID");
		if (DataType.IsNullOrEmpty(trackID) == true)
		{
			return "";
		}
		//根据TrackID从Sys_FrmDBVer表中获取历史数据
		FrmDBVer ver = new FrmDBVer();
		int i = ver.Retrieve(FrmDBVerAttr.TrackID,trackID);
		if(i==1){
			String mainData = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", ver.getMyPK(), "FrmDB");
			return mainData;
		}
		return "";
	}


		///#endregion 表单查看.


		///#region  运行变量
	/**
	 从节点.
	*/
	public final String getFromNode()
	{
		return this.GetRequestVal("FromNode");
	}
	/**
	 是否抄送
	*/
	public final boolean getItIsCC()
	{
		String str = this.GetRequestVal("Paras");

		if (DataType.IsNullOrEmpty(str) == false)
		{
			String myps = str;

			if (myps.contains("IsCC=1") == true)
			{
				return true;
			}
		}

		str = this.GetRequestVal("AtPara");
		if (DataType.IsNullOrEmpty(str) == false)
		{
			if (str.contains("IsCC=1") == true)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 轨迹ID
	*/
	public final String getTrackID()
	{
		return this.GetRequestVal("TrackeID");
	}
	/**
	 到达的节点ID
	*/
	public final int getToNode()
	{
		return this.GetRequestValInt("ToNode");
	}
	private int _FK_Node = 0;
	/**
	 当前的 NodeID ,在开始时间,nodeID,是地一个,流程的开始节点ID.
	*/
	public final int getFKNode()
	{
		String fk_nodeReq = this.GetRequestVal("FK_Node"); //this.Request.Form["FK_Node");
		if (DataType.IsNullOrEmpty(fk_nodeReq))
		{
			fk_nodeReq = this.GetRequestVal("NodeID"); // this.Request.Form["NodeID");
		}

		if (DataType.IsNullOrEmpty(fk_nodeReq) == false)
		{
			return Integer.parseInt(fk_nodeReq);
		}

		if (_FK_Node == 0)
		{
			if (this.getWorkID() != 0)
			{
				Paras ps = new Paras();
				ps.SQL = "SELECT FK_Node FROM WF_GenerWorkFlow WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "WorkID";
				ps.Add("WorkID", this.getWorkID());
				_FK_Node = DBAccess.RunSQLReturnValInt(ps, 0);
			}
			else
			{
				_FK_Node = Integer.parseInt(this.getFlowNo() + "01");
			}
		}
		return _FK_Node;
	}

	private String _width = "";
	/**
	 表单宽度
	*/
	public final String getWidth()
	{
		return _width;
	}
	public final void setWidth(String value)
	{
		_width = value;
	}
	private String _height = "";
	/**
	 表单高度
	*/
	public final String getHeight()
	{
		return _height;
	}
	public final void setHeight(String value)
	{
		_height = value;
	}
	public String _btnWord = "";
	public final String getBtnWord()
	{
		return _btnWord;
	}
	public final void setBtnWord(String value)
	{
		_btnWord = value;
	}
	private GenerWorkFlow _HisGenerWorkFlow = null;
	public final GenerWorkFlow getHisGenerWorkFlow() throws Exception {
		if (_HisGenerWorkFlow == null)
		{
			_HisGenerWorkFlow = new GenerWorkFlow(this.getWorkID());
		}
		return _HisGenerWorkFlow;
	}
	private Node _currNode = null;
	public final Node getCurrND() throws Exception {
		if (_currNode == null)
		{
			if (this.getNodeID() == 0)
			{
				_currNode = new Node(this.getHisGenerWorkFlow().getNodeID());
				return _currNode;
			}
			_currNode = new Node(this.getNodeID());
		}
		return _currNode;
	}
	private Flow _currFlow = null;
	public final Flow getCurrFlow() throws Exception {
		if (_currFlow == null)
		{
			_currFlow = new Flow(this.getFlowNo());
		}
		return _currFlow;
	}
	/**
	 定义跟路径
	*/
	public String appPath = "/";




		///#endregion

	public final String InitToolBar() throws Exception {
		DataTable dt = new DataTable("ToolBar");
		dt.Columns.Add("No");
		dt.Columns.Add("Name");
		dt.Columns.Add("Oper");

		BtnLab btnLab = new BtnLab(this.getNodeID());
		String tKey = DBAccess.GenerGUID(); // LocalDateTime.now().toString("MM-dd-hh:mm:ss");
		String toolbar = "";
		try
		{

			DataRow dr = dt.NewRow();
			/*if (this.IsMobile == false)
			{
			    dr["No"] = "Close";
			    dr["Name"] = "关闭";
			    dr["Oper"] = "Close();";
			    dt.Rows.add(dr);
			}*/


			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());

			Node nd = new Node(gwf.getNodeID());


				///#region 根据流程权限控制规则获取可以操作的按钮功能
			String sql = "SELECT A.PowerFlag,A.EmpNo,A.EmpName FROM WF_PowerModel A WHERE PowerCtrlType =1" + " UNION " + "SELECT A.PowerFlag,B." + bp.sys.base.Glo.getUserNo() + ",B.Name FROM WF_PowerModel A, Port_Emp B, Port_Deptempstation C WHERE A.PowerCtrlType = 0 AND B.No=C.FK_Emp AND A.StaNo = C.FK_Station";
			sql = "SELECT PowerFlag From(" + sql + ")D WHERE  D.EmpNo='" + WebUser.getNo() + "'";

			String powers = DBAccess.RunSQLReturnStringIsNull(sql, "");
			switch (gwf.getWFState())
			{
				case Runing: // 运行时
					/*删除流程.*/
					if (powers.contains("FlowDataDelete") == true || (Dev2Interface.Flow_IsCanDeleteFlowInstance(this.getFlowNo(), this.getWorkID(), WebUser.getNo()) == true && btnLab.getDeleteEnable() != 0))
					{
						dr = dt.NewRow();
						dr.setValue("No", "Delete");
						dr.setValue("Name", btnLab.getDeleteLab());
						dr.setValue("Oper", "");
						dt.Rows.add(dr);
					}
					if (btnLab.getShowParentFormEnable() && gwf.getPWorkID() != 0) {
						/* 如果要查看父流程. */
						dr = dt.NewRow();
						dr.setValue("No", "ParentForm");
						dr.setValue("Name", btnLab.getShowParentFormLab());
						dr.setValue("Oper", "");

						dt.Rows.add(dr);
					}

					/***取回审批*/
					//String para = "";
					//sql = "SELECT NodeID FROM WF_Node WHERE CheckNodes LIKE '%" + gwf.NodeID + "%'";
					//int myNode = DBAccess.RunSQLReturnValInt(sql, 0);
					//if (myNode != 0)
					//{
					//    GetTask gt = new GetTask(myNode);
					//    if (gt.Can_I_Do_It())
					//    {
					//        dr = dt.NewRow();
					//        dr["No"] = "TackBack";
					//        dr["Name"] = "取回审批";
					//        dr["Oper"] = "TackBack(" + gwf.NodeID + "," + myNode + ")";
					//        dt.Rows.add(dr);

					//    }
					//}


					/*撤销发送*/
					sql = "SELECT WorkID,FK_Emp,FK_Node,WhoExeIt From WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' AND ((IsPass=0 AND WhoExeIt=1) OR IsPass=1) AND IsEnable=1 AND WorkID=" + this.getWorkID();
					DataTable dtt = DBAccess.RunSQLReturnTable(sql);

					if (btnLab.getCancelRole() != 1 && (dtt.Rows.size() > 0 || powers.contains("FlowDataUnSend") == true)) {
						dr = dt.NewRow();
						dr.setValue("No", "UnSend");
						dr.setValue("Name", "撤销");
						if (dtt.Rows.size() > 0 && dtt.Rows.get(0).getValue(3).toString().equals("1")) {
							dr.setValue("Oper", "UnSend(1)");
						} else {
							dr.setValue("Oper", "UnSend()");
						}
						dt.Rows.add(dr);
					}

					//流程结束
					if (powers.contains("FlowDataOver") == true)
					{
						dr = dt.NewRow();
						dr.setValue("No", "EndFlow");
						dr.setValue("Name", btnLab.getEndFlowLab());
						dr.setValue("Oper", "DoStop('" + btnLab.getEndFlowLab() + "','" + this.getFlowNo() + "','" + this.getWorkID() + "');");
						dt.Rows.add(dr);
					}

					//催办
					if (powers.contains("FlowDataPress") == true || (gwf.getEmps().contains(WebUser.getNo()) == true && btnLab.getPressEnable() == true))
					{
						dr = dt.NewRow();
						dr.setValue("No", "Press");
						dr.setValue("Name", btnLab.GetValStringByKey(BtnAttr.PressLab));
						dr.setValue("Oper", "Press();");
						dt.Rows.add(dr);
					}
					break;
				case CompleteEnd: // 完成.
				case CompleteByNode: // 完成.
				case Delete: // 逻辑删除..
					/*恢复使用流程*/
					if (WebUser.getNo().equals("admin") == true || powers.contains("FlowDataRollback") == true || (gwf.getEmps().contains(WebUser.getNo()) == true && btnLab.GetValBooleanByKey(BtnAttr.RollbackEnable) == true))
					{
						dr = dt.NewRow();
						dr.setValue("No", "Rollback");
						dr.setValue("Name", btnLab.GetValStringByKey(BtnAttr.RollbackLab));
						dr.setValue("Oper", "");
						dt.Rows.add(dr);
					}
					if (btnLab.getShowParentFormEnable() && gwf.getPWorkID() != 0) {
						/* 如果要查看父流程. */
						dr = dt.NewRow();
						dr.setValue("No", "ParentForm");
						dr.setValue("Name", btnLab.getShowParentFormLab());
						dr.setValue("Oper", "");

						dt.Rows.add(dr);
					}

					break;
				default:
					break;
			}

			if (btnLab.GetValBooleanByKey(BtnAttr.ShowParentFormEnableMyView) && this.getPWorkID() != 0)
			{
				/*如果要查看父流程.*/
				dr = dt.NewRow();
				dr.setValue("No", "ParentForm");
				dr.setValue("Name", btnLab.getShowParentFormLab());
				dr.setValue("Oper", "");

				dt.Rows.add(dr);
			}
			if (btnLab.GetValBooleanByKey(BtnAttr.TrackEnableMyView))
			{
				dr = dt.NewRow();
				dr.setValue("No", "Track");
				dr.setValue("Name", btnLab.getTrackLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}



				///#endregion 根据流程权限控制规则获取可以操作的按钮功能


				///#region 加载流程查看器 - 按钮

			/* 判断是否是分合流？ 从而增加子线程按钮.*/
			if (gwf.getWFState() == WFState.Runing)
			{
				if (nd.getItIsFLHL() == true)
				{
					dr = dt.NewRow();
					dr.setValue("No", "Thread");
					dr.setValue("Name", btnLab.getThreadLab());
					dr.setValue("Oper", "");
					dt.Rows.add(dr);
				}
			}

			/* 打包下载zip */
			if (btnLab.getPrintZipMyView() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_zip");
				dr.setValue("Name", btnLab.getPrintZipLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			/* 打包下载html */
			if (btnLab.getPrintHtmlMyView() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_html");
				dr.setValue("Name", btnLab.getPrintHtmlLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			/* 打包下载pdf */
			if (btnLab.getPrintPDFMyView() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_pdf");
				dr.setValue("Name", btnLab.getPrintPDFLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			if (btnLab.getPrintDocEnable() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PrintDoc");
				dr.setValue("Name", btnLab.getPrintDocLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);


			}

			/**数据库版本*/
			if (btnLab.getFrmDBVerMyView() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "FrmDBVer");
				dr.setValue("Name", btnLab.getFrmDBVerLab());
				dr.setValue("Oper", "FrmDBVer_Init()");
				dt.Rows.add(dr);
			}

			//数据批阅
			if (btnLab.GetValIntByKey(BtnAttr.FrmDBRemarkEnableMyView) != 0)
			{
				dr = dt.NewRow();
				dr.setValue("No", "FrmDBRemark");
				dr.setValue("Name", btnLab.getFrmDBRemarkLab());
				dr.setValue("Oper", "FrmDBRemark(" + btnLab.getFrmDBRemarkEnable() + ")");
				dt.Rows.add(dr);
			}
			//评论
			//if (btnLab.getFlowBBSRole() != 0)
			//{
				dr = dt.NewRow();
				dr.setValue("No", "FlowBBS");
				dr.setValue("Name", btnLab.getFlowBBSLab());
				dr.setValue("Oper", btnLab.getFlowBBSRole());
				dt.Rows.add(dr);
			//}
			//启用抄送/分享
			if(nd.getHisFlow().GetParaBoolen("CCORShareEnable",false)){
				dr = dt.NewRow();
				dr.setValue("No", "CCORShare");
				dr.setValue("Name", nd.getHisFlow().GetParaString("CCORShareLab","抄送/分享"));
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			/* 公文标签 */
			if (btnLab.getOfficeBtnEnable() == true && btnLab.getOfficeBtnLocal() == 0)
			{
				dr = dt.NewRow();
				dr.setValue("No", "DocWord");
				dr.setValue("Name", btnLab.getOfficeBtnLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

				///#endregion 加载流程查看器 - 按钮


				///#region  加载自定义的button.
			NodeToolbars bars = new NodeToolbars();
			bars.Retrieve(NodeToolbarAttr.FK_Node, this.getNodeID(), NodeToolbarAttr.IsMyView, 1, NodeToolbarAttr.Idx);
			for (NodeToolbar bar : bars.ToJavaList())
			{
				String pageFrom = this.GetRequestVal("PageFrom");
				if(DataType.IsNullOrEmpty(pageFrom)==false && pageFrom.equals("Vue3")){
					dr = dt.NewRow();
					dr.setValue("No", "NodeToolBar");
					dr.setValue("Name", bar.getTitle());
					dr.setValue("Oper", bar.getUrl());
					dr.setValue("AtPara","@ExcType="+bar.getExcType()+"@Target="+bar.getTarget());
					// 判断按钮图片路径是否有值
					dr.setValue("Icon", bar.getIconPath());
					dt.Rows.add(dr);
					continue;
				}
				if (bar.getExcType() == 1 || (!DataType.IsNullOrEmpty(bar.getTarget()) == false && Objects.equals(bar.getTarget().toLowerCase(), "javascript")))
				{
					dr = dt.NewRow();
					dr.setValue("No", "NodeToolBar");
					dr.setValue("Name", bar.getTitle());
					dr.setValue("Oper", bar.getUrl());
					dt.Rows.add(dr);
				}
				else
				{
					String urlr3 = bar.getUrl() + "&FK_Node=" + this.getNodeID() + "&FID=" + this.getFID() + "&WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&s=" + tKey;
					dr = dt.NewRow();
					dr.setValue("No", "NodeToolBar");
					dr.setValue("Name", bar.getTitle());
					dr.setValue("Oper", "WinOpen('" + urlr3 + "')");
					dt.Rows.add(dr);
				}
			}

				///#endregion //加载自定义的button.

		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(ex);
			toolbar = "err@" + ex.getMessage();
		}
		return bp.tools.Json.ToJson(dt);
	}

	public final String MyFrm_InitToolBar() throws Exception {
		DataTable dt = new DataTable("ToolBar");
		dt.Columns.Add("No");
		dt.Columns.Add("Name");
		dt.Columns.Add("Oper");

		BtnLab btnLab = new BtnLab(this.getNodeID());
		String tKey = DataType.getCurrentDateByFormart("MM-dd-hh:mm:ss");
		String toolbar = "";
		try
		{
			DataRow dr = dt.NewRow();
			dr.setValue("No", "Close");
			dr.setValue("Name", "关闭");
			dr.setValue("Oper", "Close();");
			dt.Rows.add(dr);

			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());


				///#region 根据流程权限控制规则获取可以操作的按钮功能

			dr = dt.NewRow();
			dr.setValue("No", "Track");
			dr.setValue("Name", "轨迹");
			dr.setValue("Oper", "");
			dt.Rows.add(dr);

				///#endregion 根据流程权限控制规则获取可以操作的按钮功能


				///#region 加载流程查看器 - 按钮

			/* 打包下载zip */
			if (btnLab.getPrintZipMyView() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_zip");
				dr.setValue("Name", btnLab.getPrintZipLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			/* 打包下载html */
			if (btnLab.getPrintHtmlMyView() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_html");
				dr.setValue("Name", btnLab.getPrintHtmlLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			/* 打包下载pdf */
			if (btnLab.getPrintPDFMyView() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_pdf");
				dr.setValue("Name", btnLab.getPrintPDFLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}
			/* 公文标签 */
			if (btnLab.getOfficeBtnEnable() == true && btnLab.getOfficeBtnLocal() == 0)
			{
				dr = dt.NewRow();
				dr.setValue("No", "DocWord");
				dr.setValue("Name", btnLab.getOfficeBtnLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

				///#endregion 加载流程查看器 - 按钮
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(ex);
			toolbar = "err@" + ex.getMessage();
		}
		return bp.tools.Json.ToJson(dt);
	}


	/**
	 撤销

	 @return
	*/
	public final String MyView_UnSend() throws Exception {
		boolean isUnDelayedSend = this.GetRequestValBoolen("IsUnDelayedSend");
		//是否撤销延期发送
		if (isUnDelayedSend == true)
		{
			GenerWorkerList gwl = new GenerWorkerList();
			int i = gwl.Retrieve(GenerWorkerListAttr.WorkID, this.getWorkID(), GenerWorkerListAttr.FK_Node, this.getNodeID(), GenerWorkerListAttr.FK_Emp, WebUser.getNo());
			if (i != 0)
			{
				gwl.setWhoExeIt(0);
				gwl.SetPara("Day", 0);
				gwl.SetPara("hour", 0);
				gwl.SetPara("Minute", 0);
				gwl.SetPara("DelayedData", "");
				gwl.SetPara("ToNodeID", 0);
				gwl.SetPara("ToEmps", "");
				gwl.Update();
			}
			return "撤销成功";
		}
		//获取用户当前所在的节点
		String currNode = "";
		switch (DBAccess.getAppCenterDBType())
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				currNode = "SELECT FK_Node FROM (SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC ) WHERE rownum=1";
				break;
			case MySQL:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				currNode = "SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC LIMIT 1";
				break;
			case MSSQL:
				currNode = "SELECT TOP 1 FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC";
				break;
			default:
				currNode = "SELECT  FK_Node FROM WF_GenerWorkerlist WHERE FK_Emp='" + WebUser.getNo() + "' Order by RDT DESC";
				break;
		}
		String unSendToNode = DBAccess.RunSQLReturnString(currNode);
		if (DataType.IsNullOrEmpty(unSendToNode) == true)
		{
			return "err@你没有撤销当前流程的权限";
		}
		try
		{
			String msg = Dev2Interface.Flow_DoUnSend(this.getFlowNo(), this.getWorkID(), Integer.parseInt(unSendToNode), this.getFID());
			//执行撤销催办消息
			DBAccess.RunSQL("UPDATE Sys_SMS SET IsAlert=1  WHERE MsgType='DoPress' AND WorkID=" + this.getWorkID() + "  AND IsAlert=0");
			return msg;
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 是否可以查看工作.

	 @param gwf
	 @return
	*/
	public final boolean IsCanView(GenerWorkFlow gwf) throws Exception {
		//是否可以处理当前工作？
		boolean isCanDoCurrWorker = gwf.getTodoEmps().contains(WebUser.getNo() + "," + WebUser.getName() + ";");
		//   bool isCanDoCurrWorker = gwf.TodoEmps.contains(WebUser.getNo() + ",");

		if (isCanDoCurrWorker)
		{
			return true;
		}

		//如果是发起人.
		if (gwf.getStarter().equals(WebUser.getNo()))
		{
			return true;
		}

		//如果是本部门发起的.
		if (gwf.getDeptNo().equals(WebUser.getDeptNo()))
		{
			return true;
		}

		//是否是工作参与人?
		String emps = gwf.getEmps();
		if (DataType.IsNullOrEmpty(emps) == false)
		{
			emps += "@";
		}
		boolean isWorker = gwf.getEmps().contains("@" + WebUser.getNo() + "," + WebUser.getName());
		if (isWorker == true || emps.contains("@" + WebUser.getNo() + "@") == true)
		{
			return true;
		}

		if (WebUser.getNo().equals("admin") == true)
		{
			return true;
		}

		if (WebUser.getIsAdmin() == true && gwf.getOrgNo().equals(WebUser.getOrgNo()) == true)
		{
			return true;
		}

		//处理流程控制权限.
		TruckViewPower viewEn = new TruckViewPower(gwf.getFlowNo());


			///#region 基本权限控制.
		//如果任何人可见.
		if (viewEn.getPAnyOne() == true)
		{
			return true;
		}

		if (viewEn.getPSpecDept() == true && DataType.IsNullOrEmpty(viewEn.getPSpecDeptExt()) == false)
		{
			viewEn.setPSpecDeptExt(viewEn.getPSpecDeptExt() + ",");
			if (viewEn.getPSpecDeptExt().equals(WebUser.getDeptNo() + ",") == true)
			{
				return true;
			}
		}


			///#endregion 基本权限控制.


			///#region 按照部门控制.
		//本部门可见.
		if (viewEn.getPMyDept() == true)
		{
			if (gwf.getDeptNo().equals(WebUser.getDeptNo()) == true)
			{
				return true;
			}
		}

		//直属上级部门可看(比如:我是).
		if (viewEn.getPPMyDept() == true)
		{
			//上级部门可见.
			Dept dept = new Dept(gwf.getDeptNo());
			if (dept.getParentNo().equals(WebUser.getDeptNo()) == true)
			{
				return true;
			}
		}

		//上级部门可看
		if (viewEn.getPPMyDept() == true)
		{
			//上级部门可见.
			Dept dept = new Dept(gwf.getDeptNo());
			if (dept.getParentNo().equals(WebUser.getDeptNo()) == true)
			{
				return true;
			}
		}

		//同级部门可见.
		if (viewEn.getPSameDept() == true)
		{
			//如果发起人的部门，与当前人员的部门是同一级部门.
			Dept dept = new Dept(gwf.getDeptNo());
			Dept mydept = new Dept(WebUser.getDeptNo());
			if (mydept.getParentNo().equals(dept.getParentNo()) == true)
			{
				return true;
			}
		}

			///#endregion 按照部门控制.
		if (viewEn.getPSpecSta() == true && DataType.IsNullOrEmpty(viewEn.getPSpecStaExt()) == false)
		{
			String sql = "Select FK_Station From Port_DeptEmpStation Where FK_Emp='" + WebUser.getUserID() + "'";
			String stas = DBAccess.RunSQLReturnStringIsNull(sql, "");
			if (DataType.IsNullOrEmpty(stas) == false)
			{
				viewEn.setPSpecStaExt(viewEn.getPSpecStaExt() + ",");
				for (String sta : stas.split("[,]", -1))
				{
					if (viewEn.getPSpecStaExt().equals(sta + ",") == true)
					{
						return true;
					}
				}
			}

		}

			///#region 指定角色可见

			///#endregion 指定角色可见


			///#region 指定人员可见
		if (viewEn.getPSpecEmp() == true && DataType.IsNullOrEmpty(viewEn.getPSpecEmpExt()) == false)
		{
			viewEn.setPSpecEmpExt(viewEn.getPSpecEmpExt() + ",");
			if (viewEn.getPSpecEmpExt().equals(WebUser.getNo() + ",") == true)
			{
				return true;
			}
		}

			///#endregion 指定人员可见

		return false;
	}


	/**
	 初始化(处理分发)

	 @return
	*/
	public final String MyView_Init() throws Exception {
		//获得注册实体.
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(this.getWorkID());
		if(gwf.RetrieveFromDBSources()==0){
			return "err@当前流程已经删除无法查看.";
		}
		Node nd = new Node(gwf.getNodeID());
		/*if (gwf.getWFState() == WFState.Delete)
		{
			return "err@当前流程已经删除无法查看.";
		}*/

		if (gwf.getWFState() == WFState.Blank) {
			if(gwf.getStarter().equals(WebUser.getNo())){
				WF_MyFlow handler = new WF_MyFlow();
				handler.isMyView=true;
				return handler.MyFlow_Init();
			}
			return "err@当前流程还没有启动.";
		}

		/*if (gwf.getWFState() == WFState.Draft)
		{
			return "err@当前流程还是草稿.";
		}*/
//		if (gwf.getWFState() == WFState.Draft)
//		{
//			WF_MyFlow handler = new WF_MyFlow();
//			handler.isMyView = true;
//			return handler.MyFlow_Init();
//		}
		//是否可以处理当前工作？
		boolean isCanDoCurrWorker = false;

		if (gwf.getWFState() == WFState.Runing && nd.getItIsStartNode())
		{
			WF_MyFlow handler = new WF_MyFlow();
			handler.isMyView = true;
			return handler.MyFlow_Init();
		}

		String toDoEmps = ";" + gwf.getTodoEmps();
		boolean isReadonly = this.GetRequestValBoolen("IsReadonly");
		//当前的流程还是运行中的，并且可以执行当前工作,如果是，就直接转到工作处理器.
		if (gwf.getFID() != 0)
		{
			if (nd.getItIsSubThread() == true && toDoEmps.contains(";" + WebUser.getNo() + ",") && isReadonly == false)
			{
				WF_MyFlow handler = new WF_MyFlow();
				handler.isMyView=true;
				return handler.MyFlow_Init();
			}
		}

		if (gwf.getFID() == 0 && gwf.getIsOver()==false && toDoEmps.contains(";" + WebUser.getNo() + ",") && isReadonly == false)
		{
			WF_MyFlow handler = new WF_MyFlow();
			handler.isMyView=true;
			return handler.MyFlow_Init();
		}
		//是否是工作参与人?
		boolean isWorker = gwf.getEmps().contains("@" + WebUser.getNo() + "," + WebUser.getName());
		if(SystemConfig.getMessageIsEnableDingDing()){
			Integer num=bp.da.DBAccess.RunSQLReturnValInt("select count(*) from ND"+Integer.parseInt(gwf.getFlowNo())+"Track  where WorkID="+gwf.getWorkID()+" and (EmpFrom='"+WebUser.getNo()+"' or EmpTo='"+WebUser.getNo()+"')");
			if (num>0)
				isWorker=true;
		}
		if (isWorker == true || WebUser.getNo().equals("admin") == true || WebUser.getIsAdmin() == true)
		{
			//可以查看工作,就执行以后的.
		}
		else
		{
			//判断是否是抄送人员?
			CCList list = new CCList();
			boolean isExit = list.IsExit(CCListAttr.WorkID, this.getWorkID(), CCListAttr.CCTo, WebUser.getNo());
			//如果是抄送人员.
			if (isExit == true)
			{
				//是一个抄送人员.
				WF_MyCC mycc = new WF_MyCC();
				return mycc.MyCC_Init();
			}
		}

//		if (IsCanView(gwf) == false&&isWorker==false)
//		{
//			String msg = "err@您无权查看该工作,";
//			msg += "\t\n如下情况可以查看该工作.";
//			msg += "\t\n1. 该流程发起人, 审批人，抄送人，可以查看.";
//			msg += "\t\n2. 默认与发起人是同一个部门的人可以查看.";
//			msg += "\t\n3. 二级管理员可以查看本组织的工作.";
//			msg += "\t\n4. 超级管理员可以查看.";
//			msg += "\t\n5. 流程属性的权限控制设置权限的人可以查看.";
//			msg += "\t\n6. 如果该流程的数据，任何人都可以查看，请在流程属性里设置权限控制，任何人可见.";
//			return msg;
//		}


			///#region 处理表单类型.
		if (this.getCurrND().getHisFormType() == NodeFormType.SheetTree || this.getCurrND().getHisFormType() == NodeFormType.SheetAutoTree || this.getCurrFlow().getFlowDevModel() == FlowDevModel.FrmTree)
		{


				///#region 开始组合url.
			String toUrl = "";

			if (this.getItIsMobile() == true)
			{
				if (gwf.getParasFrms().equals("") == false)
				{
					toUrl = "MyViewGener.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID() + "&Frms=" + gwf.getParasFrms();
				}
				else
				{
					toUrl = "MyViewGener.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID();
				}
			}
			else
			{
				if (gwf.getParasFrms().equals("") == false)
				{
					toUrl = "MyViewTree.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID() + "&Frms=" + gwf.getParasFrms();
				}
				else
				{
					toUrl = "MyViewTree.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID();
				}
			}

			String[] strs = this.getRequestParas().split("[&]", -1);
			for (String str : strs)
			{
				if (toUrl.contains(str) == true)
				{
					continue;
				}
				if (str.contains("DoType=") == true)
				{
					continue;
				}
				if (str.contains("DoMethod=") == true)
				{
					continue;
				}
				if (str.contains("HttpHandlerName=") == true)
				{
					continue;
				}
				if (str.contains("IsLoadData=") == true)
				{
					continue;
				}
				if (str.contains("IsCheckGuide=") == true)
				{
					continue;
				}

				toUrl += "&" + str;
			}
			for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				if (toUrl.contains(key + "=") == true)
				{
					continue;
				}
				toUrl += "&" + key + "=" + ContextHolderUtils.getRequest().getParameter(key);
			}

				///#endregion 开始组合url.

			//增加fk_node
			if (toUrl.contains("&FK_Node=") == false)
			{
				toUrl += "&FK_Node=" + this.getCurrND().getNodeID();
			}
			return "url@" + toUrl;
		}

		if (this.getCurrND().getHisFormType() == NodeFormType.SDKForm || this.getCurrFlow().getFlowDevModel() == FlowDevModel.SDKFrmSelfPK || this.getCurrFlow().getFlowDevModel() == FlowDevModel.SDKFrmWorkID)
		{
			String url = getCurrND().getFormUrl();
			if (DataType.IsNullOrEmpty(url))
			{
				return "err@设置读取状流程设计错误态错误,没有设置表单url.";
			}

			//处理连接.
			url = this.MyView_Init_DealUrl(getCurrND(), url);

			//sdk表单就让其跳转.
			return "url@" + url;
		}

			///#endregion 处理表单类型.

		//求出当前节点frm的类型.
		NodeFormType frmtype = this.getCurrND().getHisFormType();
		if (frmtype != NodeFormType.RefOneFrmTree)
		{
			getCurrND().WorkID= this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.

			if (this.getCurrND().getNodeFrmID().contains(String.valueOf(this.getCurrND().getNodeID())) == false)
			{
				/*如果当前节点引用的其他节点的表单.*/
				String nodeFrmID = getCurrND().getNodeFrmID();
				String refNodeID = nodeFrmID.replace("ND", "");
				nd = new Node(Integer.parseInt(refNodeID));

				//表单类型.
				frmtype = nd.getHisFormType();
			}
		}


			///#region 内置表单类型的判断.
		/*如果是经典表单，就转到经典表单的解析执行器上，为软通动力改造。*/

		if (frmtype == NodeFormType.FoolTruck)
		{
			String url = "MyViewGener.htm";

			//处理连接.
			url = this.MyView_Init_DealUrl(getCurrND(), url);
			return "url@" + url;
		}

		if (frmtype == NodeFormType.WebOffice)
		{
			String url = "MyViewWebOffice.htm";

			//处理连接.
			url = this.MyView_Init_DealUrl(getCurrND(), url);
			return "url@" + url;
		}

		if (frmtype == NodeFormType.FoolForm && this.getItIsMobile() == false)
		{
			String url = "MyViewGener.htm";
			if (this.getItIsMobile())
			{
				url = "MyViewGener.htm";
			}

			//处理连接.
			url = this.MyView_Init_DealUrl(getCurrND(), url);

			url = url.replace("DoType=MyView_Init&", "");
			url = url.replace("&DoWhat=StartClassic", "");
			return "url@" + url;
		}

		//自定义表单
		if ((frmtype == NodeFormType.SelfForm || this.getCurrFlow().getFlowDevModel() == FlowDevModel.SelfFrm) && this.getItIsMobile() == false)
		{
			String url = "MyViewSelfForm.htm";
			//处理连接.
			url = this.MyView_Init_DealUrl(getCurrND(), url);

			url = url.replace("DoType=MyView_Init&", "");
			url = url.replace("&DoWhat=StartClassic", "");
			return "url@" + url;
		}
		if (this.getCurrND().getHisFormType() == NodeFormType.EntityTS) {
			String formUrl = this.getCurrND().getFormUrl();
			if(DataType.IsNullOrEmpty(formUrl)==true)
				return "err@请联系管理,配置节点["+this.getCurrND().getName()+"]的高代码TS";
			String url = "MyFlowEntityTS.htm?EnName="+formUrl+"&OID="+this.getWorkID()+"&PKVal="+this.getWorkID()+"&PageType=MyView";
			// 处理连接.
			url = this.MyView_Init_DealUrl(getCurrND(), url);
			url = url.replace("DoType=MyView_Init&", "");
			url = url.replace("&DoWhat=StartClassic", "");
			return "url@" + url;
		}

			///#endregion 内置表单类型的判断.

		String myurl = "MyViewGener.htm";
		//MapData md = new MapData(this.currND.getNodeFrmID());
		//if (md.getHisFrmType() == FrmType.ChapterFrm)
		 //   myurl = "MyViewTree.htm?NodeFrmType=11";
		//处理连接.
		myurl = this.MyView_Init_DealUrl(getCurrND(), myurl);
		myurl = myurl.replace("DoType=MyView_Init&", "");
		myurl = myurl.replace("&DoWhat=StartClassic", "");

		return "url@" + myurl;
	}

	private String MyView_Init_DealUrl(bp.wf.Node currND) throws Exception {
		return MyView_Init_DealUrl(currND, null);
	}

	private String MyView_Init_DealUrl(Node currND, String url) throws Exception {
		if (url == null)
		{
			url = currND.getFormUrl();
		}

		String urlExt = this.getRequestParas();
		//防止查询不到.
		urlExt = urlExt.replace("?WorkID=", "&WorkID=");
		if (urlExt.contains("&WorkID") == false)
		{
			urlExt += "&WorkID=" + this.getWorkID();
		}
		else
		{
			urlExt = urlExt.replace("&WorkID=0", "&WorkID=" + this.getWorkID());
			urlExt = urlExt.replace("&WorkID=&", "&WorkID=" + this.getWorkID() + "&");
		}

		//SDK表单上服务器地址,应用到使用ccflow的时候使用的是sdk表单,该表单会存储在其他的服务器上,珠海驰骋提出.
		url = url.replace("@SDKFromServHost", SystemConfig.GetValByKey("SDKFromServHost",""));

		if (urlExt.contains("&NodeID") == false)
		{
			urlExt += "&NodeID=" + currND.getNodeID();
		}

		if (urlExt.contains("FK_Node") == false)
		{
			urlExt += "&FK_Node=" + currND.getNodeID();
		}

		if (urlExt.contains("&FID") == false)
		{
			urlExt += "&FID=" + this.getFID();
		}

		if (urlExt.contains("&UserNo") == false)
		{
			urlExt += "&UserNo=" + URLEncoder.encode(WebUser.getNo());
		}

		if (urlExt.contains("&Token") == false)
		{
			urlExt += "&Token=" + WebUser.getToken();
		}

		if (url.contains("?") == true)
		{
			url += "&" + urlExt;
		}
		else
		{
			url += "?" + urlExt;
		}

		for (String str : ContextHolderUtils.getRequest().getParameterMap().keySet())
		{
			if (DataType.IsNullOrEmpty(str) == true || str.toLowerCase().equals("t") == true)
			{
				continue;
			}
			if (url.contains(str + "=") == true)
			{
				continue;
			}
			url += "&" + str + "=" + this.GetRequestVal(str);
		}

		url = url.replace("?&", "?");
		url = url.replace("&&", "&");
		return url;
	}



		///#region 表单树操作
	/**
	 获取表单树数据

	 @return
	*/
	public final String FlowFormTree_Init() throws Exception {
		FlowFormTrees appFlowFormTree = new FlowFormTrees();

		//add root
		FlowFormTree root = new FlowFormTree();
		root.setNo("1");
		root.setParentNo("0");
		root.setName("目录");
		root.setNodeType("root");
		appFlowFormTree.AddEntity(root);


			///#region 添加表单及文件夹

		//节点表单
		Node nd = new Node(this.getNodeID());

		FrmNodes frmNodes = new FrmNodes();
		frmNodes.Retrieve(FrmNodeAttr.FK_Node, this.getNodeID(), FrmNodeAttr.Idx);

		//文件夹
		//SysFormTrees formTrees = new SysFormTrees();
		//formTrees.RetrieveAll(SysFormTreeAttr.Name);

		//所有表单集合. 为了优化效率,这部分重置了一下.
		MapDatas mds = new MapDatas();
		if (frmNodes.size() <= 3)
		{
			for (FrmNode fn : frmNodes.ToJavaList())
			{
				MapData md = new MapData(fn.getFrmID());
				mds.AddEntity(md);
			}
		}
		else
		{
			mds.RetrieveInSQL("SELECT FK_Frm FROM WF_FrmNode WHERE FK_Node=" + this.getNodeID());
		}


		String frms = ContextHolderUtils.getRequest().getParameter("Frms");
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		if (DataType.IsNullOrEmpty(frms) == true)
		{
			frms = gwf.getParasFrms();
		}
		else
		{
			gwf.setParasFrms(frms);
			gwf.Update();
		}

		for (FrmNode frmNode : frmNodes.ToJavaList())
		{

				///#region 增加判断是否启用规则.
			switch (frmNode.getFrmEnableRole())
			{
				case Allways:
					break;
				case WhenHaveData: //判断是否有数据.
					Object tempVar = mds.GetEntityByKey(frmNode.getFrmID());
					MapData md = tempVar instanceof MapData ? (MapData)tempVar : null;
					if (md == null)
					{
						continue;
					}
					long pk = this.getWorkID();
					switch (frmNode.getWhoIsPK())
					{
						case FID:
							pk = this.getFID();
							break;
						case PWorkID:
							pk = this.getPWorkID();
							break;
						case CWorkID:
							pk = this.getCWorkID();
							break;
						case OID:
						default:
							pk = this.getWorkID();
							break;
					}
					if (DBAccess.RunSQLReturnValInt("SELECT COUNT(*) as Num FROM " + md.getPTable() + " WHERE OID=" + pk) == 0)
					{
						continue;
					}
					break;
				case WhenHaveFrmPara: //判断是否有参数.

					frms = frms.trim();
					frms = frms.replace(" ", "");
					frms = frms.replace(" ", "");

					if (DataType.IsNullOrEmpty(frms) == true)
					{
						continue;
						//return "err@当前表单设置为仅有参数的时候启用,但是没有传递来参数.";
					}

					if (frms.contains(",") == false)
					{
						if (!Objects.equals(frms, frmNode.getFrmID()))
						{
							continue;
						}
					}

					if (frms.contains(",") == true)
					{
						if (frms.contains(frmNode.getFrmID() + ",") == false)
						{
							continue;
						}
					}

					break;
				case ByFrmFields:
					throw new RuntimeException("@这种类型的判断，ByFrmFields 还没有完成。");

				case BySQL: // 按照SQL的方式.
					Object tempVar2 = frmNode.getFrmEnableExp();
					String mysql = tempVar2 instanceof String ? (String)tempVar2 : null;

					if (DataType.IsNullOrEmpty(mysql) == true)
					{
						MapData FrmMd = new MapData(frmNode.getFrmID());
						return "err@表单" + frmNode.getFrmID() + ",[" + FrmMd.getName() +"]在节点[" + frmNode.getNodeID() + "]启用方式按照sql启用但是您没有给他设置sql表达式.";
					}


					mysql = mysql.replace("@OID", String.valueOf(this.getWorkID()));
					mysql = mysql.replace("@WorkID", String.valueOf(this.getWorkID()));

					mysql = mysql.replace("@NodeID", String.valueOf(this.getNodeID()));
					mysql = mysql.replace("@FK_Node", String.valueOf(this.getNodeID()));

					mysql = mysql.replace("@FK_Flow", this.getFlowNo());

					mysql = mysql.replace("@WebUser.No", WebUser.getNo());
					mysql = mysql.replace("@WebUser.Name", WebUser.getName());
					mysql = mysql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());


					//替换特殊字符.
					mysql = mysql.replace("~", "'");

					if (DBAccess.RunSQLReturnValFloat(mysql) <= 0)
					{
						continue;
					}
					break;

				case ByStation:
					Object tempVar3 = frmNode.getFrmEnableExp();
					String exp = tempVar3 instanceof String ? (String)tempVar3 : null;
					String Sql = "SELECT FK_Station FROM Port_DeptEmpStation where FK_Emp='" + WebUser.getUserID() + "'";
					String station = DBAccess.RunSQLReturnString(Sql);
					if (DataType.IsNullOrEmpty(station) == true)
					{
						continue;
					}
					String[] stations = station.split("[;]", -1);
					boolean isExit = false;
					for (String s : stations)
					{
						if (exp.contains(s) == true)
						{
							isExit = true;
							break;
						}
					}
					if (isExit == false)
					{
						continue;
					}
					break;

				case ByDept:
					Object tempVar4 = frmNode.getFrmEnableExp();
					exp = tempVar4 instanceof String ? (String)tempVar4 : null;
					Sql = "SELECT FK_Dept FROM Port_DeptEmp where FK_Emp='" + WebUser.getNo() + "'";
					String dept = DBAccess.RunSQLReturnString(Sql);
					if (DataType.IsNullOrEmpty(dept) == true)
					{
						continue;
					}
					String[] deptStrs = dept.split("[;]", -1);
					isExit = false;
					for (String s : deptStrs)
					{
						if (exp.contains(s) == true)
						{
							isExit = true;
							break;
						}
					}
					if (isExit == false)
					{
						continue;
					}

					break;
				case Disable: // 如果禁用了，就continue出去..
					continue;
				default:
					throw new RuntimeException("@没有判断的规则." + frmNode.getFrmEnableRole());
			}

				///#endregion


				///#region 检查是否有没有目录的表单?
			boolean isHave = false;
			for (MapData md : mds.ToJavaList())
			{
				if (Objects.equals(md.getFormTreeNo(), ""))
				{
					isHave = true;
					break;
				}
			}

			String treeNo = "0";
			if (isHave && mds.size() == 1)
			{
				treeNo = "00";
			}
			else if (isHave == true)
			{
				for (MapData md : mds.ToJavaList())
				{
					if (!Objects.equals(md.getFormTreeNo(), ""))
					{
						treeNo = md.getFormTreeNo();
						break;
					}
				}
			}

				///#endregion 检查是否有没有目录的表单?

			for (MapData md : mds.ToJavaList())
			{
				if (!Objects.equals(frmNode.getFrmID(), md.getNo()))
				{
					continue;
				}

				if (Objects.equals(md.getFormTreeNo(), ""))
				{
					md.setFormTreeNo(treeNo);
				}

				//给他增加目录.
				if (appFlowFormTree.contains("Name", md.getFormTreeText()) == false)
				{
					FlowFormTree nodeFolder = new FlowFormTree();
					nodeFolder.setNo(md.getFormTreeNo());
					nodeFolder.setParentNo("1");
					nodeFolder.setName(md.getFormTreeText());
					nodeFolder.setNodeType("folder");
					appFlowFormTree.AddEntity(nodeFolder);
				}

				//检查必填项.
				boolean IsNotNull = false;
				FrmFields formFields = new FrmFields();
				QueryObject obj = new QueryObject(formFields);
				obj.AddWhere(FrmFieldAttr.FK_Node, this.getNodeID());
				obj.addAnd();
				obj.AddWhere(FrmFieldAttr.FrmID, md.getNo());
				obj.addAnd();
				obj.AddWhere(FrmFieldAttr.IsNotNull, 1);
				obj.DoQuery();
				if (formFields != null && formFields.size() > 0)
				{
					IsNotNull = true;
				}

				FlowFormTree nodeForm = new FlowFormTree();
				nodeForm.setNo(md.getNo());
				nodeForm.setParentNo(md.getFormTreeNo());

				//设置他的表单显示名字. 2019.09.30
				String frmName = md.getName();
				bp.en.Entity tempVar5 = frmNodes.GetEntityByKey(FrmNodeAttr.FK_Frm, md.getNo());
				FrmNode fn = tempVar5 instanceof FrmNode ? (FrmNode)tempVar5 : null;
				if (fn != null)
				{
					String str = fn.getFrmNameShow();
					if (DataType.IsNullOrEmpty(str) == false)
					{
						frmName = str;
					}
				}
				nodeForm.setName(frmName);
				nodeForm.setNodeType(IsNotNull ? "form|1" : "form|0");
				nodeForm.setEdit(String.valueOf(frmNode.getItIsEditInt())); // Convert.ToString(Convert.ToInt32(frmNode.IsEdit));
				nodeForm.setCloseEtcFrm(String.valueOf(frmNode.getItIsCloseEtcFrmInt()));
				appFlowFormTree.AddEntity(nodeForm);
				break;
			}
		}

			///#endregion

		//扩展工具，显示位置为表单树类型.

		//增加到数据结构上去.
		TansEntitiesToGenerTree(appFlowFormTree, root.getNo(), "");


		return appendMenus.toString();
	}
	/**
	 将实体转为树形

	 @param ens
	 @param rootNo
	 @param checkIds
	*/
	private StringBuilder appendMenus = new StringBuilder();
	private StringBuilder appendMenuSb = new StringBuilder();
	public final void TansEntitiesToGenerTree(Entities ens, String rootNo, String checkIds) throws Exception {
		Object tempVar = ens.GetEntityByKey(rootNo);
		EntityTree root = tempVar instanceof EntityTree ? (EntityTree)tempVar : null;
		if (root == null)
		{
			throw new RuntimeException("@没有找到rootNo=" + rootNo + "的entity.");
		}
		appendMenus.append("[{");
		appendMenus.append("\"id\":\"" + rootNo + "\"");
		appendMenus.append(",\"text\":\"" + root.getName() +"\"");

		//attributes
		FlowFormTree formTree = root instanceof FlowFormTree ? (FlowFormTree)root : null;
		if (formTree != null)
		{
			String url = formTree.getUrl() == null ? "" : formTree.getUrl();
			url = url.replace("/", "|");
			appendMenus.append(",\"attributes\":{\"NodeType\":\"" + formTree.getNodeType() + "\",\"IsEdit\":\"" + formTree.isEdit() + "\",\"IsCloseEtcFrm\":\"" + formTree.isCloseEtcFrm() + "\",\"Url\":\"" + url + "\"}");
		}
		appendMenus.append(",iconCls:\"icon-Wave\"");
		// 增加它的子级.
		appendMenus.append(",\"children\":");
		AddChildren(root, ens, checkIds);

		appendMenus.append(appendMenuSb);
		appendMenus.append("}]");
	}

	private void AddChildren(EntityTree parentEn, Entities ens, String checkIds) throws Exception {
		appendMenus.append(appendMenuSb);
		appendMenuSb.setLength(0);

		appendMenuSb.append("[");
		for (Entity en : ens.ToJavaListEn())
		{
			EntityTree item = (EntityTree) en;
			if (!Objects.equals(item.getParentNo(), parentEn.getNo()))
			{
				continue;
			}

			if (checkIds.contains("," + item.getNo() + ","))
			{
				appendMenuSb.append("{\"id\":\"" + item.getNo() + "\",\"text\":\"" + item.getName() +"\",\"checked\":true");
			}
			else
			{
				appendMenuSb.append("{\"id\":\"" + item.getNo() + "\",\"text\":\"" + item.getName() +"\",\"checked\":false");
			}


			//attributes
			FlowFormTree formTree = item instanceof FlowFormTree ? (FlowFormTree)item : null;
			if (formTree != null)
			{
				String url = formTree.getUrl() == null ? "" : formTree.getUrl();
				String ico = "icon-tree_folder";
				if (Objects.equals(SystemConfig.getSysNo(), "YYT"))
				{
					ico = "icon-boat_16";
				}
				url = url.replace("/", "|");
				appendMenuSb.append(",\"attributes\":{\"NodeType\":\"" + formTree.getNodeType() + "\",\"IsEdit\":\"" + formTree.isEdit() + "\",\"IsCloseEtcFrm\":\"" + formTree.isCloseEtcFrm() + "\",\"Url\":\"" + url + "\"}");
				//图标
				if (Objects.equals(formTree.getNodeType(), "form|0"))
				{
					ico = "form0";
					if (Objects.equals(SystemConfig.getSysNo(), "YYT"))
					{
						ico = "icon-Wave";
					}
				}
				if (Objects.equals(formTree.getNodeType(), "form|1"))
				{
					ico = "form1";
					if (Objects.equals(SystemConfig.getSysNo(), "YYT"))
					{
						ico = "icon-Shark_20";
					}
				}
				if (formTree.getNodeType().contains("tools"))
				{
					ico = "icon-4";
					if (Objects.equals(SystemConfig.getSysNo(), "YYT"))
					{
						ico = "icon-Wave";
					}
				}
				appendMenuSb.append(",iconCls:\"");
				appendMenuSb.append(ico);
				appendMenuSb.append("\"");
			}
			// 增加它的子级.
			appendMenuSb.append(",\"children\":");
			AddChildren(item, ens, checkIds);
			appendMenuSb.append("},");
		}
		if (appendMenuSb.length() > 1)
		{
			appendMenuSb = appendMenuSb.deleteCharAt(appendMenuSb.length() - 1);
		}
		appendMenuSb.append("]");
		appendMenus.append(appendMenuSb);
		appendMenuSb.setLength(0);
	}

		///#endregion

	/**
	 产生一个工作节点

	 @return
	*/
	public final String GenerWorkNode() throws Exception {
		try
		{
			DataSet ds = new DataSet();

			long workID = this.getWorkID();
			if (this.getCurrND().getHisFormType() == NodeFormType.RefOneFrmTree)
			{
				MapData md = new MapData(this.getCurrND().getNodeFrmID());
				if (md.getHisFrmType() == FrmType.ChapterFrm)
				{
					String url = "Frm.htm?FK_MapData=" +md.getNo();
					url = MyView_Init_DealUrl(this.getCurrND(), url);
					return "url@" + url;
				}

				//获取绑定的表单
				FrmNode frmnode = new FrmNode(this.getNodeID(), this.getCurrND().getNodeFrmID());
				switch (frmnode.getWhoIsPK())
				{
					case FID:
						workID = this.getFID();
						break;
					case PWorkID:
						workID = this.getPWorkID();
						break;
					case P2WorkID:
						GenerWorkFlow gwff = new GenerWorkFlow(this.getPWorkID());
						workID = gwff.getPWorkID();
						break;
					case P3WorkID:
						String sqlId = "Select PWorkID From WF_GenerWorkFlow Where WorkID=(Select PWorkID From WF_GenerWorkFlow Where WorkID=" + this.getPWorkID() + ")";
						workID = DBAccess.RunSQLReturnValInt(sqlId, 0);
						break;
					case RootFlowWorkID:
						workID = Dev2Interface.GetRootWorkIDBySQL(this.getWorkID(), this.getPWorkID());
						break;
					default:
						break;
				}

			}

			ds = CCFlowAPI.GenerWorkNode(this.getFlowNo(), this.getCurrND(), workID, this.getFID(), WebUser.getNo(), this.getWorkID(), "1", true,"H5");


				///#region 如果是移动应用就考虑多表单的问题.
			if (getCurrND().getHisFormType() == NodeFormType.SheetTree && this.getItIsMobile() == true)
			{
				/*如果是表单树并且是，移动模式.*/


				FrmNodes fns = new FrmNodes();
				QueryObject qo = new QueryObject(fns);

				qo.AddWhere(FrmNodeAttr.FK_Node, getCurrND().getNodeID());
				qo.addAnd();
				qo.AddWhere(FrmNodeAttr.FrmEnableRole, "!=", FrmEnableRole.Disable.getValue());
				qo.addOrderBy("Idx");
				qo.DoQuery();


				//把节点与表单的关联管理放入到系统.
				ds.Tables.add(fns.ToDataTableField("FrmNodes"));
			}

				///#endregion 如果是移动应用就考虑多表单的问题.

			if (WebUser.getSysLang().equals("CH") == true)
			{
				return bp.tools.Json.ToJson(ds);
			}

			///#region 处理多语言.
			//if (WebUser.getSysLang().equals("CH") == false)
			//{
			//    Langues langs = new Langues();
			//    langs.Retrieve(LangueAttr.Model, LangueModel.CCForm,
			//        LangueAttr.Sort, "Fields", LangueAttr.Langue, WebUser.getSysLang()); //查询语言.
			//}
			///#endregion 处理多语言.

			return bp.tools.Json.ToJson(ds);


		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError(ex);
			return "err@" + ex.getMessage();
		}
	}

}
