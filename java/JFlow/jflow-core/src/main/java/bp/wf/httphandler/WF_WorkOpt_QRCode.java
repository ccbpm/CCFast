package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.tools.QrCodeUtil;
import bp.web.*;
import bp.port.*;
import bp.wf.Glo;
import bp.wf.template.*;
import bp.wf.*;
import java.time.*;

/** 
 二维码
*/
public class WF_WorkOpt_QRCode extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_WorkOpt_QRCode()
	{

	}

	/** 
	 执行登录
	 
	 @return 
	*/
	public final String Login_Submit() throws Exception {
		NodeExt ne = new NodeExt(this.getNodeID());
		int val = ne.GetValIntByKey(BtnAttr.QRCodeRole);
		if (val == 0)
		{
			return "err@流程表单扫描已经关闭不允许扫描.";
		}

		//如果是：预览无需要权限， 没有这样的情况.
		if (val == 1)
		{
			if (WebUser.getNo() == null)
			{
				Dev2Interface.Port_Login("Guest");
			}

			return "../../MyView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}


		//如果是：预览需要权限， 检查是否可以查看该流程？.
		if (val == 2)
		{
			//使用内部用户登录.
			Emp emp = new Emp();
			emp.setNo(this.getNo());
			if (emp.RetrieveFromDBSources() == 0)
			{
				return "err@用户名或者密码错误.";
			}

			if (emp.CheckPass(this.GetRequestVal("Pass")) == false)
			{
				return "err@用户名或者密码错误.";
			}

			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
			{
				Dev2Interface.Port_Login(emp.getNo());
			}
			else
			{
				Dev2Interface.Port_Login(emp.getNo(), emp.getOrgNo());
			}

			return "../../MyFlowView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}

		//如果是：外部用户？.
		if (val == 3)
		{
			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
			if (gwf.getNodeID() != this.getNodeID())
			{
				return "err@二维码过期";
			}

			//使用内部用户登录.
			bp.wf.port.User user = new bp.wf.port.User();
			user.setNo(this.getNo());
			if (user.RetrieveFromDBSources() == 0)
			{
				return "err@用户名账号错误.";
			}

			if (user.CheckPass(this.GetRequestVal("Pass")) == false)
			{
				return "err@用户名账号错误.";
			}

			//执行登录.
			Dev2InterfaceGuest.Port_Login(user.getNo(), user.getName());

			HuiQian_AddGuest(this.getWorkID(), this.getNodeID());

			return "../../MyFlow.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}

		return "err@系统错误.";
	}

	/** 
	 生成二维码
	 
	 @return 
	*/
	public final String GenerCode_Init()
	{
		String url = "";


		if (this.getWorkID() == 0) //开始节点的时候.
		{
			url = bp.difference.SystemConfig.getHostURL() + "/WF/WorkOpt/QRCode/ScanGuide.htm?WorkID=" + this.getWorkID() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo();
		}
		else
		{
			url = bp.difference.SystemConfig.getHostURL() + "/WF/WorkOpt/QRCode/ScanGuide.htm?WorkID=" + this.getWorkID() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo();
		}
		String tempPath = bp.difference.SystemConfig.getPathOfTemp();

		String fileName =  this.getFK_Flow() + ".png";
		if(this.getWorkID()!=0)
			fileName = this.getWorkID()+".png";

		QrCodeUtil.createQrCode(url, tempPath, fileName, "png");
		//返回url.
		return url;
	}
	public final String ScanGuide_Init() throws Exception {
		NodeExt ne = new NodeExt(this.getNodeID());
		int val = ne.GetValIntByKey(BtnAttr.QRCodeRole);

		if (val == 0)
		{
			return "err@流程表单扫描已经关闭不允许扫描.";
		}

		// 如果不需要权限 就可以查看表单.
		if (val == 1)
		{
			if (WebUser.getNo() == null)
			{
				Dev2Interface.Port_Login("Guest");
			}

			return "/CCMobile/MyView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}

		// 如果需要权限才能查看表单.
		if (val == 2)
		{
			//判断是否登录?
			if (WebUser.getNo() == null)
			{
				return "Login.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID() + "&QRCodeRole=2";
			}

			return "/CCMobile/MyView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}

		//外部账户协作模式处理工作.
		if (val == 3)
		{
			if (GuestUser.getNo() == null)
			{
				return "Login.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID() + "&QRCodeRole=2";
			}

			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
			if (gwf.getNodeID() != this.getNodeID())
			{
				return "err@二维码过期";
			}

			HuiQian_AddGuest(this.getWorkID(), this.getNodeID());


			return "/CCMobile/MyFlowView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}

		return "err@没有判断的模式.";

	}

	/**
	 * vue3 二维码扫描接口
	 */
	public final String GenerCode_Init_Vue3()
	{
		String url = bp.difference.SystemConfig.getHostURLVue3() + "/#/CCMobile/ScanGuide?WorkID=" + this.getWorkID() + "&FK_Node=" + this.getNodeID() + "&FK_Flow=" + this.getFlowNo();
		String tempPath = bp.difference.SystemConfig.getPathOfTemp();

		String fileName =  this.getFK_Flow() + ".png";
		if(this.getWorkID()!=0)
			fileName = this.getWorkID()+".png";

		QrCodeUtil.createQrCode(url, tempPath, fileName, "png");
		//返回url.
		return url;
	}
	public final String ScanGuide_Init_Vue3() throws Exception {
		if(this.getNodeID() == 0){
			return ScanGuide_Init_Dict();
		}
		NodeExt ne = new NodeExt(this.getNodeID());
		int val = ne.GetValIntByKey(BtnAttr.QRCodeRole);

		if (val == 0)
		{
			return "err@流程表单扫描已经关闭不允许扫描.";
		}

		// 如果不需要权限 就可以查看表单.
		if (val == 1)
		{
			if (DataType.IsNullOrEmpty(WebUser.getNo()) == true)
			{
				Dev2Interface.Port_Login("Guest");
			}
			String token = bp.wf.Dev2Interface.Port_GenerToken();

			return "url@/CCMobile/MyView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID() + "&Token=" + token;
		}

		// 如果需要权限才能查看表单.
		if (val == 2)
		{
			//判断是否登录?
			if (DataType.IsNullOrEmpty(WebUser.getNo()) == true)
			{
				return "url@Login.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID() + "&QRCodeRole=2";
			}

			return "url@/CCMobile/MyView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}

		//外部账户协作模式处理工作.
		if (val == 3)
		{
			if (DataType.IsNullOrEmpty(GuestUser.getNo()) == true)
			{
				return "url@Login.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID() + "&QRCodeRole=2";
			}

			GenerWorkFlow gwf = new GenerWorkFlow(this.getWorkID());
			if (gwf.getNodeID() != this.getNodeID())
			{
				return "err@二维码过期";
			}

			HuiQian_AddGuest(this.getWorkID(), this.getNodeID());


			return "url@/CCMobile/MyFlowView.htm?WorkID=" + this.getWorkID() + "&FK_Flow=" + this.getFlowNo() + "&FK_Node=" + this.getNodeID();
		}

		return "err@没有判断的模式.";

	}

	/**
	 * 实体的二维码扫描初始化
	 * @return
	 * @throws Exception
	 */
	public final String ScanGuide_Init_Dict() throws Exception {
		if (DataType.IsNullOrEmpty(WebUser.getNo()) == true)
		{
			Dev2Interface.Port_Login("Guest");
		}
		String token = bp.wf.Dev2Interface.Port_GenerToken();

		return "url@/WF/CCBill/MyDictFrameWork.htm?MethodNo=" + this.GetRequestVal("MethodNo") + "&WorkID=" + this.getWorkID() + "&FrmID=" + this.getFrmID() +"&MenuNo="+this.GetRequestVal("MenuNo")+ "&Token=" + token;
	}

	private void HuiQian_AddGuest(long workid, int fk_node) throws Exception {
		//判断是否存在该节点的待办
		GenerWorkerList gwl = new GenerWorkerList();
		gwl.setNodeID(fk_node);
		gwl.setEmpNo(GuestUser.getNo());
		gwl.setWorkID(workid);
		int num = gwl.RetrieveFromDBSources();
		//还没有待办，增加会签人信息
		if (num == 0)
		{
			Node nd = new Node(fk_node);
			GenerWorkerList gwlZCR = null;
			//获取会签组长的信息
			GenerWorkFlow gwf = new GenerWorkFlow(workid);
			if (DataType.IsNullOrEmpty(gwf.getHuiQianZhuChiRen()) == true)
			{
				gwlZCR = new GenerWorkerList();
				num = gwlZCR.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, fk_node, GenerWorkerListAttr.IsPass, 0);
			}
			else
			{
				gwlZCR = new GenerWorkerList();
				num = gwlZCR.Retrieve(GenerWorkerListAttr.WorkID, workid, GenerWorkerListAttr.FK_Node, fk_node, GenerWorkerListAttr.FK_Emp, gwf.getHuiQianZhuChiRen());
			}
			if (num == 0)
			{
				throw new RuntimeException("err@发生不可预测的问题,组长协作模式下找不到组长信息");
			}
			gwf.setHuiQianZhuChiRen(gwlZCR.getEmpNo());
			gwf.setHuiQianZhuChiRenName(gwlZCR.getEmpName());
			gwlZCR.SetPara("HuiQianType", "");
			gwlZCR.setEmpNo(GuestUser.getNo());
			gwlZCR.setEmpName(GuestUser.getName());
			gwlZCR.setPassInt(0); //设置不可以用.
			gwlZCR.setDeptNo("");
			gwlZCR.setDeptName(""); //部门名称.
			gwlZCR.setItIsRead(false);
			gwlZCR.setGuestNo(GuestUser.getNo());
			gwlZCR.setGuestName(GuestUser.getName());
			gwlZCR.SetPara("HuiQianZhuChiRen", gwlZCR.getEmpNo());


				///#region 计算会签时间.
			if (nd.getHisCHWay() == CHWay.None)
			{
				gwlZCR.setSDT("无");
			}
			else
			{
				//给会签人设置应该完成日期. 考虑到了节假日.                
				java.util.Date dtOfShould = Glo.AddDayHoursSpan(new java.util.Date(), nd.getTimeLimit(), nd.getTimeLimitHH(), nd.getTimeLimitMM(), nd.getTWay());
				//应完成日期.
				gwlZCR.setSDT(DataType.getDateByFormart(dtOfShould,DataType.getSysDateTimeFormat()) + ":ss");
			}

			//求警告日期.
			java.util.Date dtOfWarning = new java.util.Date();
			//计算警告日期。
			// 增加小时数. 考虑到了节假日.
			if (nd.getWarningDay() != 0)
			{
				dtOfWarning = Glo.AddDayHoursSpan(new java.util.Date(), (int)nd.getWarningDay(), 0, 0, nd.getTWay());
			}

			gwlZCR.setDTOfWarning(DataType.getDateByFormart(dtOfWarning,DataType.getSysDateTimeFormat()));

				///#endregion 计算会签时间.

			gwlZCR.setSender(gwlZCR.getEmpNo() + "," + gwlZCR.getEmpName()); //发送人为当前人.
			gwlZCR.setItIsHuiQian(true);
			gwlZCR.Insert(); //插入作为待办.

			//修改GenerWorkFlow的信息
			//gwf.TodoEmps += GuestUser.getNo() + "," + GuestUser.getName() +";";
			gwf.setHuiQianTaskSta(HuiQianTaskSta.HuiQianing);
			gwf.Update();
			//给组长发送消息
			Dev2Interface.Port_SendMsg(gwlZCR.getEmpNo(), "bpm会签工作参与", "HuiQian" + gwf.getWorkID() + "_" + gwf.getNodeID() + "_" + GuestUser.getNo(), GuestUser.getName() +"参与了您对工作的｛" + gwf.getTitle() + "｝邀请,请您及时关注工作进度.", "HuiQian", gwf.getFlowNo(), gwf.getNodeID(), gwf.getWorkID(), gwf.getFID());

			//执行会签,写入日志.
			Dev2Interface.WriteTrack(gwf.getFlowNo(), gwf.getNodeID(), gwf.getNodeName(), gwf.getWorkID(), gwf.getFID(), GuestUser.getNo() + "," + GuestUser.getName(), ActionType.HuiQian, "执行会签", null, null, null, GuestUser.getNo(), GuestUser.getName(), gwlZCR.getEmpNo(), gwlZCR.getEmpName());
			return;
		}


	}

}
