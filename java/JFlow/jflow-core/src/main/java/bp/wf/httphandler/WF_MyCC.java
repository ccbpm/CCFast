package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.wf.Glo;
import bp.wf.template.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;

import java.net.URLDecoder;
import java.util.*;
import java.time.*;

/**
 抄送处理类
*/
public class WF_MyCC extends bp.difference.handler.DirectoryPageBase
{
	/**
	 抄送处理类
	*/
	public WF_MyCC()
	{

	}


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


	//杨玉慧
	public final String getDoType1()
	{
		return ContextHolderUtils.getRequest().getParameter("DoType1");
	}

		///#endregion

	public final String Focus() throws Exception {
		Dev2Interface.Flow_Focus(this.getWorkID());
		return "设置成功.";
	}
	/**
	 加载前置导航数据

	 @return
	*/
	public final String StartGuide_Init()
	{
		String josnData = "";
		//流程编号
		String fk_flow = this.GetRequestVal("FK_Flow");
		//查询的关键字
		String skey = this.GetRequestVal("Keys");
		try
		{
			//获取流程实例
			Flow fl = new Flow(fk_flow);
			//获取设置的前置导航的sql
			String sql = fl.getStartGuidePara2();
			//判断是否有查询条件
			if (!DataType.IsNullOrEmpty(skey))
			{
				sql = fl.getStartGuidePara1();
				sql = sql.replace("@Key", skey);
			}
			sql = sql.replace("~", "'");
			//替换约定参数
			sql = sql.replace("@WebUser.No", WebUser.getNo());
			sql = sql.replace("@WebUser.Name", WebUser.getName());
			sql = sql.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
			sql = sql.replace("@WebUser.DeptNo",WebUser.getDeptNo());
			sql = sql.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
			sql = sql.replace("@WebUser.OrgNo", WebUser.getOrgNo());

			if (sql.contains("@") == true)
			{
				for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
				{
					sql = sql.replace("@" + key, this.GetRequestVal(key));
				}

				for (String key : ContextHolderUtils.getRequest().getParameterMap().keySet())
				{
					sql = sql.replace("@" + key, this.GetRequestVal(key));
				}
			}

			//获取数据
			DataTable dt = DBAccess.RunSQLReturnTable(sql);

			//判断前置导航的类型
			switch (fl.getStartGuideWay())
			{
				case BySQLOne:
				case BySystemUrlOneEntity:
					josnData = bp.tools.Json.ToJson(dt);
					break;
				case BySQLMulti:
					josnData = bp.tools.Json.ToJson(dt);
					break;
				default:
					break;
			}
			return josnData;
		}
		catch (Exception ex)
		{
			return "err@:" + ex.getMessage().toString();
		}
	}
	/**
	 设置确认的状态

	 @return
	*/
	public final String MyCC_Make_CheckOver()
	{
		return "执行成功.";
	}
	/**
	 初始化(处理分发)

	 @return
	*/
	public final String MyCC_Init() throws Exception {
		//手动启动子流程的标志 0父子流程 1 同级子流程.
		String isStartSameLevelFlow = this.GetRequestVal("IsStartSameLevelFlow");

		GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
		int ccNodeID = this.getNodeID();
		// 打开抄送给我的数据，都把它设置读取状态.
		CCLists ccs = new CCLists();
		ccs.Retrieve(CCListAttr.WorkID, this.getWorkID(), CCListAttr.CCTo, WebUser.getNo(), null);
		for (CCList item : ccs.ToJavaList())
		{
			if (item.GetValIntByKey(CCListAttr.Sta)==-1 || item.getHisSta()  == CCSta.UnRead)
			{
				Dev2Interface.Node_CC_SetRead(item.getMyPK());
			}
		}

		//节点为自动抄送，通常一个节点一个人只有一条抄送数据
		if (ccs.size() == 1)
		{
			Node nd = new Node(this.getNodeID());
			if (nd.getHisNodeType() == NodeType.CCNode)
			{
				this.setNodeID(Integer.parseInt(ccs.get(0).GetValStringByKey("NodeIDWork")));
				_currNode = new Node(this.getNodeID());
			}
		}

		//如果当前节点类型是抄送节点类型，表单类型就根据发送过来的节点类型计算
		NodeFormType formType = this.getCurrND().getHisFormType();
		int nodeIDWork  = this.GetRequestValInt("NodeIDWork");
		if(this.getCurrND().getHisNodeType() == NodeType.CCNode){
			if(nodeIDWork!=0){
				Node node = new Node(nodeIDWork);
				formType = node.getHisFormType();
			}
		}

		//当前工作.
		Work currWK = this.getCurrND().getHisWork();


		///#region 处理表单类型.
		if (formType == NodeFormType.SheetTree || formType == NodeFormType.SheetAutoTree || this.getCurrFlow().getFlowDevModel() == FlowDevModel.FrmTree)
		{

			if (this.getCurrND().getItIsStartNode())
			{
				/*如果是开始节点, 先检查是否启用了流程限制。*/
				if (CCFlowAPI.CheckIsCanStartFlow_InitStartFlow(this.getCurrFlow()) == false)
				{
					/* 如果启用了限制就把信息提示出来. */
					String msg = bp.wf.Glo.DealExp(this.getCurrFlow().getStartLimitAlert(), currWK, null);
					return "err@" + msg;
				}
			}


			///#region 开始组合url.
			String toUrl = "";

			if (this.getItIsMobile() == true)
			{
				if (gwf.getParasFrms().equals("") == false)
				{
					toUrl = "MyCCGener.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID() + "&Frms=" + gwf.getParasFrms()+"&CCNodeID="+ccNodeID;
				}
				else
				{
					toUrl = "MyCCGener.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID()+"&CCNodeID="+ccNodeID;
				}
			}
			else
			{
				if (gwf.getParasFrms().equals("") == false)
				{
					toUrl = "MyCCTree.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID() + "&Frms=" + gwf.getParasFrms()+"&CCNodeID="+ccNodeID;
				}
				else
				{
					toUrl = "MyCCTree.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&UserNo=" + WebUser.getNo() + "&FID=" + this.getFID() + "&Token=" + WebUser.getToken() + "&PFlowNo=" + gwf.getPFlowNo() + "&PNodeID=" + gwf.getPNodeID() + "&PWorkID=" + gwf.getPWorkID()+"&CCNodeID="+ccNodeID;
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


			//SDK表单上服务器地址,应用到使用ccflow的时候使用的是sdk表单,该表单会存储在其他的服务器上,珠海驰骋提出.
			toUrl = toUrl.replace("@SDKFromServHost", SystemConfig.GetValByKey("SDKFromServHost",""));

			//增加fk_node
			if (toUrl.contains("&FK_Node=") == false)
			{
				toUrl += "&FK_Node=" + this.getCurrND().getNodeID();
			}

			//如果是开始节点.
			if (getCurrND().getItIsStartNode() == true)
			{
				if (toUrl.contains("PrjNo") == true && toUrl.contains("PrjName") == true)
				{
					String sql = "UPDATE " + currWK.getEnMap().getPhysicsTable() + " SET PrjNo='" + this.GetRequestVal("PrjNo") + "', PrjName='" + this.GetRequestVal("PrjName") + "' WHERE OID=" + this.getWorkID();
					DBAccess.RunSQL(sql);
				}
			}
			return "url@" + toUrl+"&CCNodeID="+ccNodeID;
		}

		if (formType == NodeFormType.SDKForm || this.getCurrFlow().getFlowDevModel() == FlowDevModel.SDKFrmWorkID || this.getCurrFlow().getFlowDevModel() == FlowDevModel.SDKFrmSelfPK)
		{
			if (this.getWorkID() == 0)
			{
				currWK = this.getCurrFlow().NewWork();
				this.setWorkID(currWK.getOID());
			}

			String url = getCurrND().getFormUrl();
			if (DataType.IsNullOrEmpty(url))
			{
				return "err@设置读取状流程设计错误态错误,没有设置表单url.";
			}

			//处理连接.
			url = this.MyCC_Init_DealUrl(getCurrND(), currWK);

			//sdk表单就让其跳转.
			return "url@" + url+"&CCNodeID="+ccNodeID;
		}

		///#endregion 处理表单类型.

		//求出当前节点frm的类型.
		//NodeFormType frmtype = this.getCurrND().getHisFormType();
		if (formType != NodeFormType.RefOneFrmTree)
		{
			getCurrND().WorkID=this.getWorkID(); //为获取表单ID ( NodeFrmID )提供参数.

			if (!this.getCurrND().getNodeFrmID().contains(String.valueOf(this.getCurrND().getNodeID())))
			{
				/*如果当前节点引用的其他节点的表单.*/
				String nodeFrmID = getCurrND().getNodeFrmID();
				String refNodeID = nodeFrmID.replace("ND", "");
				Node nd = new Node(Integer.parseInt(refNodeID));

				//表单类型.
				formType = nd.getHisFormType();
			}
		}


		///#region 内置表单类型的判断.
		/*如果是傻瓜表单，就转到傻瓜表单的解析执行器上，为软通动力改造。*/
		if (this.getWorkID() == 0)
		{
			currWK = this.getCurrFlow().NewWork();
			this.setWorkID(currWK.getOID());
		}

		if (formType == NodeFormType.FoolTruck)
		{
			/*如果是傻瓜表单，就转到傻瓜表单的解析执行器上，为软通动力改造。*/
			if (this.getWorkID() == 0)
			{
				currWK = this.getCurrFlow().NewWork();
				this.setWorkID(currWK.getOID());
			}

			//String url = "MyCCFoolTruck.htm";
			String url = "MyCCGener.htm";

			//处理连接.
			url = this.MyCC_Init_DealUrl(getCurrND(), currWK, url);
			return "url@" + url+"&CCNodeID="+ccNodeID;
		}

		if (formType == NodeFormType.WebOffice)
		{
			/*如果是公文表单，就转到公文表单的解析执行器上，为软通动力改造。*/
			if (this.getWorkID() == 0)
			{
				currWK = this.getCurrFlow().NewWork();
				this.setWorkID(currWK.getOID());
			}

			//String url = "MyCCFoolTruck.htm";
			String url = "MyCCWebOffice.htm";

			//处理连接.
			url = this.MyCC_Init_DealUrl(getCurrND(), currWK, url);
			return "url@" + url+"&CCNodeID="+ccNodeID;
		}

		if (formType == NodeFormType.FoolForm && this.getItIsMobile() == false)
		{
			/*如果是傻瓜表单，就转到傻瓜表单的解析执行器上。*/
			if (this.getWorkID() == 0)
			{
				currWK = this.getCurrFlow().NewWork();
				this.setWorkID(currWK.getOID());
			}

			String url = "MyCCGener.htm";
			if (this.getItIsMobile())
			{
				url = "MyCCGener.htm";
			}

			//处理连接.
			url = this.MyCC_Init_DealUrl(getCurrND(), currWK, url);

			url = url.replace("DoType=MyCC_Init&", "");
			url = url.replace("&DoWhat=StartClassic", "");
			return "url@" + url+"&CCNodeID="+ccNodeID;
		}

		//自定义表单
		if ((formType == NodeFormType.SelfForm || this.getCurrFlow().getFlowDevModel() == FlowDevModel.SelfFrm) && this.getItIsMobile() == false)
		{
			if (this.getWorkID() == 0)
			{
				currWK = this.getCurrFlow().NewWork();
				this.setWorkID(currWK.getOID());
			}

			String url = "MyCCSelfForm.htm";
			//处理连接.
			url = this.MyCC_Init_DealUrl(getCurrND(), currWK, url);

			url = url.replace("DoType=MyCC_Init&", "");
			url = url.replace("&DoWhat=StartClassic", "");
			return "url@" + url+"&CCNodeID="+ccNodeID;
		}
		if (this.getCurrND().getHisFormType() == NodeFormType.EntityTS) {
			String formUrl = this.getCurrND().getFormUrl();
			if(DataType.IsNullOrEmpty(formUrl)==true)
				return "err@请联系管理,配置节点["+this.getCurrND().getName()+"]的高代码TS";
			String url = "MyFlowEntityTS.htm?EnName="+formUrl+"&OID="+this.getWorkID()+"&PKVal="+this.getWorkID()+"&PageType=MyCC";
			if (this.getWorkID() == 0)
			{
				currWK = this.getCurrFlow().NewWork();
				this.setWorkID(currWK.getOID());
			}
			// 处理连接.
			url = this.MyCC_Init_DealUrl(getCurrND(),currWK, url);
			url = url.replace("DoType=MyCC_Init&", "");
			url = url.replace("&DoWhat=StartClassic", "");
			return "url@" + url;
		}


		///#endregion 内置表单类型的判断.

		String myurl = "MyCCGener.htm";

		//处理连接.
		myurl = this.MyCC_Init_DealUrl(getCurrND(), currWK, myurl);
		myurl = myurl.replace("DoType=MyCC_Init&", "");
		myurl = myurl.replace("&DoWhat=StartClassic", "");

		return "url@" + myurl+"&CCNodeID="+ccNodeID;
	}

	private String MyCC_Init_DealUrl(bp.wf.Node currND, Work currWK) throws Exception {
		return MyCC_Init_DealUrl(currND, currWK, null);
	}

	private String MyCC_Init_DealUrl(Node currND, Work currWK, String url) throws Exception {
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
		//url = url.replace("@SDKFromServHost", SystemConfig.getAppSettings().get("SDKFromServHost"));

		if (urlExt.contains("&NodeID") == false)
		{
			urlExt += "&NodeID=" + currND.getNodeID();
		}

		if (!urlExt.contains("FK_Node"))
		{
			urlExt += "&FK_Node=" + currND.getNodeID();
		}

		if (!urlExt.contains("&FID") && currWK != null)
		{
			urlExt += "&FID=" + this.getFID();
		}

		if (urlExt.contains("&UserNo") == false)
		{
			urlExt += "&UserNo=" + URLDecoder.decode(WebUser.getNo());
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
			if (DataType.IsNullOrEmpty(str) == true)
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
	/**
	 工具栏

	 @return
	*/
	public final String InitToolBar() throws Exception {
		DataTable dt = new DataTable("ToolBar");
		dt.Columns.Add("No");
		dt.Columns.Add("Name");
		dt.Columns.Add("Oper");

		BtnLab btnLab = new BtnLab(this.getNodeID());
		String tKey = LocalDateTime.now().toString();
		String toolbar = "";
		boolean isMobile = this.GetRequestValBoolen("IsMobile");
		try
		{
			CCList list = new CCList();
			boolean isCheckOver = list.IsExit(CCListAttr.WorkID, this.getWorkID(), CCListAttr.CCTo, WebUser.getNo(), CCListAttr.Sta, CCSta.CheckOver.getValue());
			DataRow dr = dt.NewRow();
			//if (isMobile == false)
			//{
			//    if (isCheckOver == true)
			//    {
			//        dr["No"] = "Close";
			//        dr["Name"] = "关闭";
			//        dr["Oper"] = "CloseWindow();";
			//    }
			//    else
			//    {
			//        dr["No"] = "ReadAndClose";
			//        dr["Name"] = "阅件完毕";
			//        dr["Oper"] = "ReadAndClose();";
			//    }

			//    dt.Rows.add(dr);

			//}

			if (btnLab.GetValBooleanByKey(BtnAttr.ShowParentFormEnableMyCC) && this.getPWorkID() != 0)
			{
				/*如果要查看父流程.*/
				dr = dt.NewRow();
				dr.setValue("No", "ParentForm");
				dr.setValue("Name", btnLab.getShowParentFormLab());
				dr.setValue("Oper", "");

				dt.Rows.add(dr);
			}
			if (btnLab.GetValBooleanByKey(BtnAttr.TrackEnableMyCC))
			{
				dr = dt.NewRow();
				dr.setValue("No", "Track");
				dr.setValue("Name", btnLab.getTrackLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}




				///#region 加载流程抄送 - 按钮


			/* 打包下载zip */
			if (btnLab.getPrintZipMyCC() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_zip");
				dr.setValue("Name", btnLab.getPrintZipLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			/* 打包下载html */
			if (btnLab.getPrintHtmlMyCC() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_html");
				dr.setValue("Name", btnLab.getPrintHtmlLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}

			/* 打包下载pdf */
			if (btnLab.getPrintPDFMyCC() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "PackUp_pdf");
				dr.setValue("Name", btnLab.getPrintPDFLab());
				dr.setValue("Oper", "");
				dt.Rows.add(dr);
			}
			if (btnLab.getFrmDBVerMyCC() == true)
			{
				dr = dt.NewRow();
				dr.setValue("No", "FrmDBVer");
				dr.setValue("Name", btnLab.getFrmDBVerLab());
				dr.setValue("Oper", "FrmDBVer_Init()");
				dt.Rows.add(dr);
			}
			//数据批阅
			if (btnLab.getFrmDBRemarkEnable() != 0)
			{
				dr = dt.NewRow();
				dr.setValue("No", "FrmDBRemark");
				dr.setValue("Name", btnLab.getFrmDBRemarkLab());
				dr.setValue("Oper", "FrmDBRemark(" + btnLab.getFrmDBRemarkEnable() + ")");
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

				///#endregion
			dr = dt.NewRow();
			dr.setValue("No", "FlowBBS");
			dr.setValue("Name", btnLab.getFlowBBSLab());
			dr.setValue("Oper","");
			dt.Rows.add(dr);

				///#region  加载自定义的button.
			NodeToolbars bars = new NodeToolbars();
			bars.Retrieve(NodeToolbarAttr.FK_Node, this.getNodeID(), NodeToolbarAttr.IsMyCC, 1, NodeToolbarAttr.Idx);
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



	public final String MyCCSelfForm_Init() throws Exception {
		return this.GenerWorkNode();
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
					String tempVar2 = frmNode.getFrmEnableExp();
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
					mysql = mysql.replace("@WebUser.DeptNo", WebUser.getDeptNo());

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
					continue;

				if (Objects.equals(md.getFormTreeNo(), ""))
					md.setFormTreeNo(treeNo);

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
				Entity fn = frmNodes.GetEntityByKey(FrmNodeAttr.FK_Frm, md.getNo());
				if (fn != null)
				{
					String str = fn.GetValStrByKey(FrmNodeAttr.FrmNameShow);
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
			EntityTree item = (EntityTree)en;
			String parentNo=item.GetValStringByKey("ParentNo");
			String name=item.GetValStringByKey("Name");
			String no=item.GetValStringByKey("No");

			if (!Objects.equals( parentNo, parentEn.getNo()))
			{
				continue;
			}

			if (checkIds.contains("," + no + ","))
			{
				appendMenuSb.append("{\"id\":\"" + no + "\",\"text\":\"" + name+"\",\"checked\":true");
			}
			else
			{
				appendMenuSb.append("{\"id\":\"" + no + "\",\"text\":\"" + name +"\",\"checked\":false");
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
		WF_MyView myView = new WF_MyView();
		return myView.GenerWorkNode();
	}


}
