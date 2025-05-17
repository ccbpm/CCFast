package bp.wf.dts;

import bp.da.*;
import bp.en.*;
import bp.wf.*;

import java.util.Calendar;
import java.util.Date;

/**
 Method 的摘要说明
 */
public class AutoRun_WhoExeIt extends Method
{
	/**
	 不带有参数的方法
	 */
	public AutoRun_WhoExeIt()throws Exception
	{
		this.Title = "执行节点的自动任务.";
		this.Help = "对于节点属性里配置的自动执行或者机器执行的节点上的任务自动发送下去。";
		this.GroupName = "流程自动执行定时任务";
	}

	/**
	 执行

	 @return 返回执行结果
	 */
	@Override
	public Object Do()throws Exception
	{
		String info = "";
		String sql = "SELECT WorkID,FID,FK_Emp,FK_Node,FK_Flow,AtPara FROM WF_GenerWorkerList WHERE WhoExeIt!=0 AND IsPass=0 AND IsEnable=1 ORDER BY FK_Emp";
		DataTable dt = null;

		dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() == 0)
		{
			return "无任务";
		}


		///#region 自动启动流程 whoExIt.
		for (DataRow dr : dt.Rows)
		{
			long workid = Long.parseLong(dr.getValue(0).toString());
			long fid = Long.parseLong(dr.getValue(1).toString());
			String empNo = dr.getValue(2).toString();
			int nodeID = Integer.parseInt(dr.getValue(3).toString());
			String fk_flow = dr.getValue(4).toString();
			String paras = dr.getValue(5).toString();
			int toNodeID = 0;
			String toEmps = null;
			//判断AtPara中是否存在设置的时间
			if (DataType.IsNullOrEmpty(paras) == false && paras.contains("DelayedData") == true)
			{
				AtPara atPara = new AtPara(paras);
				String delayedData = atPara.GetValStrByKey("DelayedData");
				int day = atPara.GetValIntByKey("Day");
				int hour = atPara.GetValIntByKey("Hour");
				int minute = atPara.GetValIntByKey("Minute");
				toNodeID = atPara.GetValIntByKey("ToNodeID");
				toEmps = atPara.GetValStrByKey("ToEmps");
				Date dtime = DataType.ParseSysDate2DateTime(delayedData);
				Calendar c = Calendar.getInstance();
				c.setTime(dtime);
				c.add(Calendar.DATE, day);
				c.add(Calendar.HOUR, hour);
				c.add(Calendar.MINUTE, minute);
				dtime = c.getTime();
				String newTime = DataType.getDateByFormart(dtime,"yyyy-MM-dd HH:mm");
				String currTime = DataType.getCurrentDateTime();

				if (DataType.GetSpanMinute(currTime,newTime) > 0)
					continue;

			}

			if (bp.web.WebUser.getNo().equals(empNo) == false)
			{
				Dev2Interface.Port_Login(empNo);
			}

			try
			{
				info += "发送成功:" + bp.web.WebUser.getNo() + Dev2Interface.Node_SendWork(fk_flow, workid,toNodeID,toEmps).ToMsgOfText();
			}
			catch (RuntimeException ex)
			{
				info += "err@发送错误:" + ex.getMessage().toString();
			}
		}

		///#endregion 自动启动流程

		if (bp.web.WebUser.getNo().equals("admin") == false)
		{
			Dev2Interface.Port_Login("admin");
		}

		return info;
	}


	///#region 重写。
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
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			return true;
		}
		return false;
	}

	///#endregion 重写。

}