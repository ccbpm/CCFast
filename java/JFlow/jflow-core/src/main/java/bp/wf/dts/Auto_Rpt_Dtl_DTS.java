package bp.wf.dts;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.tools.DateUtils;
import bp.wf.data.*;
import bp.*;
import bp.wf.*;
import java.time.*;
import java.util.Date;

/** 
 Method 的摘要说明
*/
public class Auto_Rpt_Dtl_DTS extends Method
{
	/** 
	 不带有参数的方法
	*/
	public Auto_Rpt_Dtl_DTS()
	{
		this.Title = "自动报表的发送";
		this.Help = "自动报表配置到WF_AutoRpt, 与WF_AutoRptDtl中，读取之后进行发送消息或者数据.";
		this.GroupName = "流程自动执行定时任务";

	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {
		//获得定时任务信息.
		AutoRpts rpts = new AutoRpts();
		rpts.RetrieveAllFromDBSource();

		int nowInt = Integer.parseInt( DateUtils.format(new Date(),"HHmm"));
		//比如: 2009
		String strHours = DataType.getCurrentDateTime();

		String msg = "";

		for (AutoRpt rpt : rpts.ToJavaList())
		{

				///#region 判断是否到了发起时间.
			if (rpt.getDots().contains(strHours) == true)
			{
				continue;
			}

			//StartDT 格式:  20:02,18:02
			String[] strs = rpt.getStartDT().split("[,]", -1);
			boolean isHave = false;
			for (String str : strs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				String mystr = str.replace(":", "");
				int mynum = Integer.parseInt(mystr);

				if (nowInt >= mynum)
				{
					isHave = true;
					break;
				}
			}
			if (isHave == false)
			{
				continue;
			}

				///#endregion 判断是否到了发起时间.


				///#region 组织内容.
			//组织内容.
			String title = rpt.getName();

			//获得消息.
			AutoRptDtls dtls = new AutoRptDtls();
			dtls.Retrieve(AutoRptDtlAttr.AutoRptNo, rpt.getNo(), null);

			//找到可以发送的人员.

				///#endregion 组织内容.


				///#region 求出可以发起的人员,并执行发送.
			String empOfSQLs = bp.wf.Glo.DealExp(rpt.getToEmpOfSQLs(), null);
			DataTable dtEmp = DBAccess.RunSQLReturnTable(empOfSQLs);
			for (DataRow dr : dtEmp.Rows)
			{
				//执行登录.
				String empNo = dr.getValue("No").toString();
				Dev2Interface.Port_Login(empNo);

				//求出内容.
				String docs = "";
				for (AutoRptDtl dtl : dtls.ToJavaList())
				{
					String sql = dtl.getSQLExp();
					Glo.DealExp(sql, null);

					String val = DBAccess.RunSQLReturnStringIsNull(sql, "无");
					docs += "\t\n" + dtl.getName() + " (" + val + "): " + dtl.getBeiZhu();

					String url = dtl.getUrlExp();
					Glo.DealExp(url, null);
					docs += " <a href='" + url + "'> 打开连接</a>";
				}

				String agentId = bp.difference.SystemConfig.getWX_AgentID() != null ? bp.difference.SystemConfig.getWX_AgentID() : null;
				if (agentId != null)
				{
					String accessToken = bp.gpm.weixin.WeiXinEntity.getAccessToken(); //获取 AccessToken

					bp.port.Emp emp = new bp.port.Emp(empNo);
					bp.gpm.weixin.MsgText msgText = new bp.gpm.weixin.MsgText();
					msgText.setContent(docs);
					msgText.setAccessToken(accessToken);
					msgText.setAgentid(bp.difference.SystemConfig.getWX_AgentID());
					msgText.setTouser(emp.getNo());
					msgText.setSafe("0");

					//执行发送 
					bp.gpm.weixin.Glo.PostMsgOfText(msgText);
				}
			}

				///#endregion 求出可以发起的人员.并执行发送

			//更新时间点.
			if (rpt.getDots().length() > 3999)
			{
				rpt.setDots(rpt.getDots().substring(200));
			}
			rpt.setDots(rpt.getDots() + "," + strHours + ",");
			rpt.Update();
		}

		return "执行成功.";

	}
}
