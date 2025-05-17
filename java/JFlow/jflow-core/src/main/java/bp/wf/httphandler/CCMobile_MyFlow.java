package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.wf.template.*;
import bp.difference.*;
import bp.wf.template.sflow.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/**
 页面功能实体
*/
public class CCMobile_MyFlow extends bp.difference.handler.DirectoryPageBase
{
	/**
	 构造函数
	*/
	public CCMobile_MyFlow() throws Exception {
		WebUser.setSheBei("Mobile");
	}
	/**
	 获得工作节点

	 @return
	*/
	public final String GenerWorkNode() throws Exception {

		WF_MyFlow en = new WF_MyFlow();
		return en.GenerWorkNode();
	}
	/**
	 绑定多表单中获取节点表单的数据

	 @return
	*/
	public final String GetNoteValue() throws Exception {
		int fk_node = this.getNodeID();
		if (fk_node == 0)
		{
			fk_node = Integer.parseInt(this.getFlowNo() + "01");
		}
		Node nd = new Node(fk_node);

			///#region  获取节点表单的数据
		Work wk = nd.getHisWork();
		wk.setOID(this.getWorkID());
		wk.RetrieveFromDBSources();
		wk.ResetDefaultVal(null, null, 0);
		if (SystemConfig.isBSsystem() == true)
		{
			// 处理传递过来的参数。
			Enumeration enu = ContextHolderUtils.getRequest().getParameterNames();
			while (enu.hasMoreElements()) {
				String k = (String) enu.nextElement();
				wk.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
			}

			// 处理传递过来的frm参数。
			for (String k : ContextHolderUtils.getRequest().getParameterMap().keySet())
			{
				if (DataType.IsNullOrEmpty(k) == true)
				{
					continue;
				}

				wk.SetValByKey(k, ContextHolderUtils.getRequest().getParameter(k));
			}
		}

			///#endregion 获取节点表单的数据
		//节点表单字段
		MapData md = new MapData(nd.getNodeFrmID());
		MapAttrs mattrs = md.getMapAttrs();
		DataTable dt = new DataTable();
		dt.TableName = "Node_Note";
		dt.Columns.Add("KeyOfEn", String.class);
		dt.Columns.Add("NoteVal", String.class);
		String nodeNote = nd.GetParaString("NodeNote");

		for (MapAttr attr : mattrs.ToJavaList())
		{
			if (nodeNote.contains("," + attr.getKeyOfEn() + ",") == false)
			{
				continue;
			}
			String text = "";
			switch (attr.getLGType())
			{
				case Normal: // 输出普通类型字段.
					if (attr.getMyDataType() == 1 && attr.getUIContralType().getValue() == DataType.AppString)
					{

						if (mattrs.contains(attr.getKeyOfEn() + "Text") == true)
						{
							text = wk.GetValRefTextByKey(attr.getKeyOfEn());
						}
						if (DataType.IsNullOrEmpty(text))
						{
							if (mattrs.contains(attr.getKeyOfEn() + "T") == true)
							{
								text = wk.GetValStrByKey(attr.getKeyOfEn() + "T");
							}
						}
					}
					else
					{
						text = wk.GetValStrByKey(attr.getKeyOfEn());
						if (attr.getTextModel() == 3)
						{
							text = text.replace("white-space: nowrap;", "");
						}
					}

					break;
				case Enum:
				case FK:
					text = wk.GetValRefTextByKey(attr.getKeyOfEn());
					break;
				default:
					break;
			}
			DataRow dr = dt.NewRow();
			dr.setValue("KeyOfEn", attr.getKeyOfEn());
			dr.setValue("NoteVal", text);
			dt.Rows.add(dr);

		}

		return bp.tools.Json.ToJson(dt);
	}
	/**
	 获得toolbar

	 @return
	*/
	public final String InitToolBar() throws Exception {
		DataSet ds = new DataSet();

		//节点信息
		Node nd = new Node(this.getNodeID());
		ds.Tables.add(nd.ToDataTableField("WF_Node"));

		//流程信息
		Flow flow = new Flow(this.getFlowNo());
		ds.Tables.add(flow.ToDataTableField("WF_Flow"));

		//操作按钮信息
		BtnLab btnLab = new BtnLab(this.getNodeID());
		ds.Tables.add(btnLab.ToDataTableField("WF_BtnLab"));


			///#region  加载自定义的button.
		NodeToolbars bars = new NodeToolbars();
		bars.Retrieve(NodeToolbarAttr.FK_Node, this.getNodeID(), NodeToolbarAttr.IsMyFlow, 1, NodeToolbarAttr.Idx);
		ds.Tables.add(bars.ToDataTableField("WF_NodeToolbar"));

			///#endregion //加载自定义的button.


			///#region 处理是否是加签，或者是否是会签模式.
		boolean isAskForOrHuiQian = false;
		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		ds.Tables.add(gwf.ToDataTableField("WF_GenerWorkFlow"));

		if (String.valueOf(this.getNodeID()).endsWith("01") == false)
		{
			if (gwf.getWFState() == WFState.Askfor)
			{
				isAskForOrHuiQian = true;
			}

			/*判断是否是加签状态，如果是，就判断是否是主持人，如果不是主持人，就让其 isAskFor=true ,屏蔽退回等按钮.*/
			/**说明：针对于组长模式的会签，协作模式的会签加签人仍可以加签*/
			if (gwf.getHuiQianTaskSta() == HuiQianTaskSta.HuiQianing)
			{
				//初次打开会签节点时
				if (DataType.IsNullOrEmpty(gwf.getHuiQianZhuChiRen()) == true)
				{
					if (gwf.getTodoEmps().contains(WebUser.getNo() + ",") == false)
					{
						isAskForOrHuiQian = true;
					}
				}

				//执行会签后的状态
				if (btnLab.getHuiQianRole() == HuiQianRole.TeamupGroupLeader && btnLab.getHuiQianLeaderRole() == HuiQianLeaderRole.OnlyOne)
				{
					if (!Objects.equals(gwf.getHuiQianZhuChiRen(), WebUser.getNo()) && gwf.GetParaString("AddLeader").contains(WebUser.getNo() + ",") == false)
					{
						isAskForOrHuiQian = true;
					}
				}
				else
				{
					if (gwf.getHuiQianZhuChiRen().contains(WebUser.getNo() + ",") == false && gwf.GetParaString("AddLeader").contains(WebUser.getNo() + ",") == false)
					{
						isAskForOrHuiQian = true;
					}
				}
			}
			DataTable dt = new DataTable();
			dt.TableName = "HuiQian";
			dt.Columns.Add("isAskForOrHuiQian", Integer.class);
			DataRow dr = dt.NewRow();
			if (isAskForOrHuiQian == true)
			{
				dr.setValue("isAskForOrHuiQian", 1);
			}
			else
			{
				dr.setValue("isAskForOrHuiQian", 0);
			}
			dt.Rows.add(dr);

			ds.Tables.add(dt);
		}

			///#endregion 处理是否是加签，或者是否是会签模式，.


			///#region 按钮旁的下拉框
		if (nd.getCondModel() != DirCondModel.ByLineCond)
		{
			if (nd.getItIsStartNode() == true || gwf.getTodoEmps().contains(WebUser.getNo() + ",") == true)
			{
				/*如果当前不是主持人,如果不是主持人，就不让他显示下拉框了.*/

				/*如果当前节点，是可以显示下拉框的.*/
				Nodes nds = nd.getHisToNodes();

				DataTable dtToNDs = new DataTable();
				dtToNDs.TableName = "ToNodes";
				dtToNDs.Columns.Add("No", String.class); //节点ID.
				dtToNDs.Columns.Add("Name", String.class); //到达的节点名称.
				dtToNDs.Columns.Add("IsSelectEmps", String.class); //是否弹出选择人的对话框？
				dtToNDs.Columns.Add("IsSelected", String.class); //是否选择？


					///#region 增加到达延续子流程节点。
				if (nd.getSubFlowYanXuNum() >= 0)
				{
					SubFlowYanXus ygflows = new SubFlowYanXus(this.getNodeID());
					for (SubFlowYanXu item : ygflows.ToJavaList())
					{
						DataRow dr = dtToNDs.NewRow();
						dr.setValue("No", item.getSubFlowNo() + "01");
						dr.setValue("Name", "启动:" + item.getSubFlowName());
						dr.setValue("IsSelectEmps", "1");
						dr.setValue("IsSelected", "0");
						dtToNDs.Rows.add(dr);
					}
				}

					///#endregion 增加到达延续子流程节点。


					///#region 到达其他节点.
				//上一次选择的节点.
				int defalutSelectedNodeID = 0;
				if (nds.size() > 1)
				{
					String mysql = "";
					// 找出来上次发送选择的节点.
					if (SystemConfig.getAppCenterDBType() == DBType.MSSQL)
					{
						mysql = "SELECT  top 1 NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND ActionType=1 ORDER BY WorkID DESC";
					}
					else if (SystemConfig.getAppCenterDBType() == DBType.DM || SystemConfig.getAppCenterDBType() == DBType.Oracle || SystemConfig.getAppCenterDBType() == DBType.KingBaseR3 || SystemConfig.getAppCenterDBType() == DBType.KingBaseR6 || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByOracle)
					{
						mysql = "SELECT * FROM ( SELECT  NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND ActionType=1 ORDER BY WorkID DESC ) WHERE ROWNUM =1";
					}
					else if (SystemConfig.getAppCenterDBType() == DBType.MySQL || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8CByMySQL  || bp.difference.SystemConfig.getAppCenterDBType() == DBType.GBASE8A)
					{
						mysql = "SELECT  NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND ActionType=1 ORDER BY WorkID  DESC limit 1,1";
					}
					else if (SystemConfig.getAppCenterDBType() == DBType.PostgreSQL || SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.UX)
					{
						mysql = "SELECT  NDTo FROM ND" + Integer.parseInt(nd.getFlowNo()) + "Track A WHERE A.NDFrom=" + this.getNodeID() + " AND ActionType=1 ORDER BY WorkID  DESC limit 1";
					}

					//获得上一次发送到的节点.
					defalutSelectedNodeID = DBAccess.RunSQLReturnValInt(mysql, 0);
				}


					///#region 为天业集团做一个特殊的判断.
				if (Objects.equals(SystemConfig.getCustomerNo(), "TianYe") && nd.getName().contains("董事长") == true)
				{
					/*如果是董事长节点, 如果是下一个节点默认的是备案. */
					for (Node item : nds.ToJavaList())
					{
						if (item.getName().contains("备案") == true && item.getName().contains("待") == false)
						{
							defalutSelectedNodeID = item.getNodeID();
							break;
						}
					}
				}

					///#endregion 为天业集团做一个特殊的判断.


				for (Node item : nds.ToJavaList())
				{
					DataRow dr = dtToNDs.NewRow();
					dr.setValue("No", item.getNodeID());
					dr.setValue("Name", item.getName());
					//if (item.hissel

					if (item.getHisDeliveryWay() == DeliveryWay.BySelected)
					{
						dr.setValue("IsSelectEmps", "1");
					}
					else
					{
						dr.setValue("IsSelectEmps", "0"); //是不是，可以选择接受人.
					}

					//设置默认选择的节点.
					if (defalutSelectedNodeID == item.getNodeID())
					{
						dr.setValue("IsSelected", "1");
					}
					else
					{
						dr.setValue("IsSelected", "0");
					}

					dtToNDs.Rows.add(dr);
				}

					///#endregion 到达其他节点。


				//增加一个下拉框, 对方判断是否有这个数据.
				ds.Tables.add(dtToNDs);
			}
		}

			///#endregion 按钮旁的下拉框

		return bp.tools.Json.ToJson(ds);
	}
	public final String MyFlow_Init() throws Exception {
		WF_MyFlow en = new WF_MyFlow();
		return en.MyFlow_Init();
	}
	public final String MyFlow_StopFlow()
	{
		WF_MyFlow en = new WF_MyFlow();
		return en.MyFlow_StopFlow();
	}
	public final String Save() throws Exception {
		WF_MyFlow en = new WF_MyFlow();
		return en.Save();
	}
	public final String Send() throws Exception {
		WF_MyFlow en = new WF_MyFlow();
		return en.Send();
	}
	public final String StartGuide_Init()
	{
		WF_MyFlow en = new WF_MyFlow();
		return en.StartGuide_Init();
	}
	public final String FrmGener_Init() throws Exception {
		WF_CCForm ccfrm = new WF_CCForm();
		return ccfrm.FrmGener_Init();
	}
	public final String FrmGener_Save() throws Exception {
		WF_CCForm ccfrm = new WF_CCForm();
		String str = ccfrm.FrmGener_Save();

		Flow fl = new Flow(this.getFlowNo());
		Node nd = new Node(this.getNodeID());
		Work wk = nd.getHisWork();
		if (this.getWorkID() != 0)
		{
			wk.setOID(this.getWorkID());
			wk.RetrieveFromDBSources();
		}
		wk.ResetDefaultVal(null, null, 0);
		String title = WorkFlowBuessRole.GenerTitle(fl, wk);
		//修改RPT表的标题
		wk.SetValByKey(GERptAttr.Title, title);
		wk.Update();

		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(this.getWorkID());
		int i = gwf.RetrieveFromDBSources();
		gwf.setTitle(title); //标题.
		gwf.setNodeName(nd.getFWCNodeName()); //节点意见名称
		gwf.Update();


		// 这里保存的时候，需要保存到草稿,没有看到PC端对应的方法。
		String nodeIDStr = String.valueOf(this.getNodeID());
		if (nodeIDStr.endsWith("01") == true)
		{
			if (fl.getDraftRole() == DraftRole.SaveToDraftList)
			{
				Dev2Interface.Node_SetDraft(this.getWorkID());
			}

			if (fl.getDraftRole() == DraftRole.SaveToTodolist)
			{
				Dev2Interface.Node_SetDraft2Todolist(this.getWorkID());
			}
		}
		return str;
	}

	public final String MyFlowGener_Delete() throws Exception {
		Dev2Interface.Flow_DoDeleteFlowByWriteLog(this.getFlowNo(), this.getWorkID(), WebUser.getName() + "用户删除", true);
		return "删除成功...";
	}

	public final String AttachmentUpload_Down() throws Exception {
		WF_CCForm ccform = new WF_CCForm();
		return ccform.AttachmentUpload_Down();
	}
	/**
	 查询
	 @return
	*/
	public final String RetrieveFieldGroup() throws Exception {
		String FrmID = this.GetRequestVal("FrmID");
		GroupFields gfs = new GroupFields();
		QueryObject qo = new QueryObject(gfs);
		qo.AddWhere(GroupFieldAttr.FrmID, FrmID);
		int num = qo.DoQuery();

		if (num == 0)
		{
			GroupField gf = new GroupField();
			gf.setFrmID(FrmID);
			MapData md = new MapData();
			md.setNo(FrmID);
			if (md.RetrieveFromDBSources() == 0)
			{
				gf.setLab("基础信息");
			}
			else
			{
				gf.setLab(md.getName());
			}
			gf.setIdx(0);
			gf.Insert();
			gfs.AddEntity(gf);
		}
		return gfs.ToJson("dt");
	}
}
