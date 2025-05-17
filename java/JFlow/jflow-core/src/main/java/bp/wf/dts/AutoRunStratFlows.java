package bp.wf.dts;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;

/** 
 Method 的摘要说明
*/
public class AutoRunStratFlows extends Method
{
	/** 
	 不带有参数的方法
	*/
	public AutoRunStratFlows()
	{
		this.Title = "自动启动流程";
		this.Help = "在流程属性上配置的信息,自动发起流程,按照时间规则....";
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
		Flows fls = new Flows();
		fls.RetrieveAll();

		String msg = "开始执行自动发起流程 :" + DataType.getCurrentDateTimess();

		LocalDateTime dt = LocalDateTime.now();
		int week = dt.getDayOfWeek().getValue();
		week++;


			///#region 自动启动流程
		for (Flow fl : fls.ToJavaList())
		{
			if (fl.getHisFlowRunWay() == FlowRunWay.HandWork)
			{
				continue;
			}

			msg += "<br>扫描：" + fl.getName();


				///#region 高级设置模式，是否达到启动的时间点？
			if (fl.getHisFlowRunWay() == FlowRunWay.SpecEmpAdv || fl.getHisFlowRunWay() == FlowRunWay.SpecEmp)
			{
				String currTime = DataType.getCurrentDateTime(); //2022-09-01 09:10
				String[] strs = null;

				if (fl.getHisFlowRunWay() == FlowRunWay.SpecEmp)
				{
					strs = fl.getRunObj().split("[@]", -1);
				}

				if (fl.getHisFlowRunWay() == FlowRunWay.SpecEmpAdv)
				{
					strs = fl.getStartGuidePara2().split("[@]", -1); //@11:15@12:15
				}

				boolean isHave = false; //是否可以执行.
				for (String s : strs)
				{
					if (DataType.IsNullOrEmpty(s) == true)
					{
						continue;
					}
					// 去除首尾空格
					String str = s.trim();

					//如果有周.
					if (str.contains("Week.") == true)
					{
						if (str.contains("Week.1") == true && dt.getDayOfWeek() != DayOfWeek.MONDAY)
						{
							continue;
						}
						if (str.contains("Week.2") == true && dt.getDayOfWeek() != DayOfWeek.TUESDAY)
						{
							continue;
						}
						if (str.contains("Week.3") == true && dt.getDayOfWeek() != DayOfWeek.WEDNESDAY)
						{
							continue;
						}
						if (str.contains("Week.4") == true && dt.getDayOfWeek() != DayOfWeek.THURSDAY)
						{
							continue;
						}
						if (str.contains("Week.5") == true && dt.getDayOfWeek() != DayOfWeek.FRIDAY)
						{
							continue;
						}
						if (str.contains("Week.6") == true && dt.getDayOfWeek() != DayOfWeek.SATURDAY)
						{
							continue;
						}
						if (str.contains("Week.7") == true && dt.getDayOfWeek() != DayOfWeek.SUNDAY)
						{
							continue;
						}

						str = str.replace("Week.1", "");
						str = str.replace("Week.2", "");
						str = str.replace("Week.3", "");
						str = str.replace("Week.4", "");
						str = str.replace("Week.5", "");
						str = str.replace("Week.6", "");
						str = str.replace("Week.7", "");
						str = str.trim();
					}

					//是否每月的最后一天？
					if (str.contains("LastDayOfMonth") == true)
					{
						//获得当前月份有多少天.
						int days = LocalDate.now().lengthOfMonth();
						if (dt.getDayOfMonth() != days)
						{
							continue;
						}
						str = str.replace("LastDayOfMonth", "").trim();
					}

					//如果自动发起流程过多，会延迟判断时间点，要补偿自动发起
					if (!str.contains(":"))
					{
						continue;
					}

					// 逻辑修正， 不包含: 不执行，不用再判断:
					//是不是到时间了？
					//if (str.contains(":"))
					//{
						int i = str.length();
						String currTime01 = currTime.substring(currTime.length() - i);
						LocalDateTime dt1 = LocalDateTime.parse(str);
						LocalDateTime dt2 = LocalDateTime.parse(currTime01);
						if (dt1.compareTo(dt2) > 0)
						{
							continue;
						}
					//}

					//记录执行过的时间点，如果有该时间点，就不要在执行了。
					// 这里的时间点是有问题的，之前是根据当前时刻计算，其实是不对的。
					// 应该根据设定的时间保存时刻。
					String pkval = "";
					//是按照一天的时间点计算的.
					if (s.contains("LastDayOfMonth")) // 月度任务
					{
						pkval = "AutoFlow_" + fl.getNo() + "_" + dt1.format(DateTimeFormatter.ofPattern("yyyyMM")) + str;
					}
					else if (s.contains("Week.")) //按周计算.
					{
						pkval = "AutoFlow_" + fl.getNo() + "_" + dt1.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + dt.getDayOfWeek() + str;
					}
					else if (str.length() <= 6)
					{
						pkval = "AutoFlow_" + fl.getNo() + "_" + dt1.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + str;
					}
					else
					{
						Log.DebugWriteError("不合法的发起规则: " + s);
					}


					GloVar gv = new GloVar();
					gv.setNo(pkval);
					if (gv.RetrieveFromDBSources() == 0)
					{
						gv.setName(fl.getName() + "自动发起.");
						gv.setGroupKey("AutoStartFlow");
						gv.Insert();
						Log.DebugWriteInfo("任务发起：" + gv.getName() +", No: " + gv.getNo());
						isHave = true; //可以执行.
						break;
					}
				}
				if (isHave == false)
				{
					continue;
				}
			}

				///#endregion 高级设置模式，是否达到启动的时间点？



			// 以此用户进入.
			switch (fl.getHisFlowRunWay())
			{
				case SpecEmp: //指定人员按时运行。
					msg += "<br>触发了:指定人员按时运行.";
					this.SpecEmp(fl);
					continue;
				case SelectSQLModel: //按数据集合驱动的模式执行。
					this.SelectSQLModel(fl);
					continue;
				case WF_TaskTableInsertModel: //按数据集合驱动的模式执行。
					this.WF_TaskTableInsertModel(fl);
					continue;
				case SpecEmpAdv: //指定人员按时运行 高级模式.。
					msg += "<br>触发了:指定人员按时运行 高级模式.";
					msg += this.SpecEmpAdv(fl);
					continue;
				case LetAdminSendSpecEmp: //让admin发送给指定的人员.。
					msg += "<br>触发了:指定人员按时运行 高级模式.";
					msg += this.LetAdminSendSpecEmp(fl);
					continue;
				default:
					break;
			}
		}
		if (!Objects.equals(WebUser.getNo(), "admin"))
		{
			bp.port.Emp empadmin = new bp.port.Emp("admin");
			WebUser.SignInOfGener(empadmin, "CH", false, false, null, null);
		}

			///#endregion 发送消息

		return msg;
	}
	public final void SpecEmp(Flow fl) throws Exception {
		String RunObj = fl.getRunObj();
		String fk_emp = RunObj.substring(0, RunObj.indexOf('@'));

		bp.port.Emp emp = new bp.port.Emp();
		emp.setUserID(fk_emp);
		if (emp.RetrieveFromDBSources() == 0)
		{
			Log.DebugWriteError("启动自动启动流程错误：发起人(" + fk_emp + ")不存在。");
			return;
		}

		try
		{
			//让 userNo 登录.
			Dev2Interface.Port_Login(emp.getUserID());

			//创建空白工作, 发起开始节点.
			long workID = Dev2Interface.Node_CreateBlankWork(fl.getNo());

			//执行发送.
			SendReturnObjs objs = Dev2Interface.Node_SendWork(fl.getNo(), workID);

			//String info_send= BP.WF.Dev2Interface.Node_StartWork(fl.No,);
			Log.DebugWriteInfo("流程:" + fl.getNo() + fl.getName() + "的定时任务\t\n -------------- \t\n" + objs.ToMsgOfText());
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError("流程:" + fl.getNo() + fl.getName() + "自动发起错误:\t\n -------------- \t\n" + ex.getMessage());
		}
	}
	public final String LetAdminSendSpecEmp(Flow fl) throws Exception {
		String empsExp = fl.getStartGuidePara1(); //获得人员信息。
		if (DataType.IsNullOrEmpty(empsExp) == true)
		{
			return "配置的表达式错误:StartGuidePara1，人员信息不能为空。";
		}


			///#region 获得人员集合.
		String[] emps = null;
		if (empsExp.toUpperCase().contains("SELECT") == true)
		{
			String strs = "";
			empsExp = bp.wf.Glo.DealExp(empsExp, null, null);
			DataTable dt = DBAccess.RunSQLReturnTable(empsExp);
			for (DataRow dr : dt.Rows)
			{
				strs += dr.getValue(0).toString() + ";";
			}
			emps = strs.split("[;]", -1);
		}
		else
		{
			empsExp = empsExp.replace("@", ";");
			empsExp = empsExp.replace(",", ";");
			empsExp = empsExp.replace("、", ";");
			empsExp = empsExp.replace("，", ";");
			emps = empsExp.split("[;]", -1);
		}

			///#endregion 获得人员集合.

		//让admin登录发送.
		Dev2Interface.Port_Login("admin");

		String msg = "";
		try
		{

			//创建空白工作, 发起开始节点.
			long workID = Dev2Interface.Node_CreateBlankWork(fl.getNo());

			Dev2Interface.Node_SendWork(fl.getNo(), workID, 0, empsExp);

			String info = "流程:【" + fl.getNo() + fl.getName() + "】的定时任务 \t\n -------------- \t\n 已经启动，待办：" + empsExp + " , " + workID;
			Log.DebugWriteInfo(info);
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteError("流程:" + fl.getNo() + fl.getName() + "自动发起错误:\t\n -------------- \t\n" + ex.getMessage());
		}
		return msg;
	}
	/** 
	 指定人员按时启动高级模式
	 
	 @param fl 流程
	 @return 
	*/
	public final String SpecEmpAdv(Flow fl) throws Exception {
		String empsExp = fl.getStartGuidePara1(); //获得人员信息。
		if (DataType.IsNullOrEmpty(empsExp) == true)
		{
			return "配置的表达式错误:StartGuidePara1，人员信息不能为空。";
		}


			///#region 获得人员集合.
		String[] emps = null;
		if (empsExp.toUpperCase().contains("SELECT") == true)
		{
			String strs = "";
			empsExp = bp.wf.Glo.DealExp(empsExp, null, null);
			DataTable dt = DBAccess.RunSQLReturnTable(empsExp);
			for (DataRow dr : dt.Rows)
			{
				strs += dr.getValue(0).toString() + ";";
			}
			emps = strs.split("[;]", -1);
			Log.DebugWriteInfo("strs:" + strs);
		}
		else
		{
			empsExp = empsExp.replace("@", ";");
			empsExp = empsExp.replace(",", ";");
			empsExp = empsExp.replace("、", ";");
			empsExp = empsExp.replace("，", ";");
			emps = empsExp.split("[;]", -1);
		}

			///#endregion 获得人员集合.

		String msg = "";
		int idx = 0;
		for (String emp : emps)
		{
			if (DataType.IsNullOrEmpty(emp) == true)
			{
				continue;
			}
			try
			{
				//让 emp 登录.
				Dev2Interface.Port_Login(emp);

				//创建空白工作, 发起开始节点.
				long workID = Dev2Interface.Node_CreateBlankWork(fl.getNo());

				//执行发送.
				SendReturnObjs objs = Dev2Interface.Node_SendWork(fl.getNo(), workID);

				String info = "流程:" + fl.getNo() + fl.getName() + "的定时任务\t\n -------------- \t\n 已经启动，待办：" + emp + " , " + workID;
				Log.DebugWriteInfo(info);
				idx++;
				msg += "<br/>第" + idx + "条:" + info;
			}
			catch (Exception ex)
			{
				Log.DebugWriteError("流程:" + fl.getNo() + fl.getName() + "自动发起错误:\t\n -------------- \t\n" + ex.getMessage());
			}
		}
		return msg;
	}
	/** 
	 触发模式
	 
	 @param fl
	*/
	public final void WF_TaskTableInsertModel(Flow fl)
	{
	}

	public final void SelectSQLModel(Flow fl) throws Exception {

			///#region 读取数据.
		MapExt me = new MapExt();
		me.setMyPK(MapExtXmlList.StartFlow + "_ND" + Integer.parseInt(fl.getNo()) + "01");
		int i = me.RetrieveFromDBSources();
		if (i == 0)
		{
			return;
		}

		if (DataType.IsNullOrEmpty(me.getTag()))
		{
			Log.DebugWriteError("没有为流程(" + fl.getName() + ")的开始节点设置发起数据,请参考说明书解决.");
			return;
		}



			///#endregion 读取数据.


			///#region 检查数据源是否正确.
		String errMsg = "";
		// 获取主表数据.
		DataTable dtMain = DBAccess.RunSQLReturnTable(me.getTag());
		if (dtMain.Rows.size() == 0)
		{
			Log.DebugWriteError("流程(" + fl.getName() + ")此时无任务.");
			return;
		}

		if (dtMain.Columns.contains("Starter") == false)
		{
			errMsg += "@配值的主表中没有Starter列.";
		}

		if (dtMain.Columns.contains("MainPK") == false)
		{
			errMsg += "@配值的主表中没有MainPK列.";
		}

		if (errMsg.length() > 2)
		{
			Log.DebugWriteError("流程(" + fl.getName() + ")的开始节点设置发起数据,不完整." + errMsg);

			return;
		}

			///#endregion 检查数据源是否正确.


			///#region 处理流程发起.
		String frmID = "ND" + Integer.parseInt(fl.getNo()) + "01";

		String err = "";
		int idx = 0;
		for (DataRow dr : dtMain.Rows)
		{
			idx++;

			String mainPK = dr.getValue("MainPK").toString();
			String sql = "SELECT OID FROM " + fl.getPTable() + " WHERE MainPK='" + mainPK + "'";
			if (DBAccess.RunSQLReturnTable(sql).Rows.size() != 0)
			{
				continue; //说明已经调度过了
			}

			String starter = dr.getValue("Starter").toString();
			if (!Objects.equals(WebUser.getNo(), starter))
			{
				WebUser.Exit();
				bp.port.Emp emp = new bp.port.Emp();
				emp.setUserID(starter);
				if (emp.RetrieveFromDBSources() == 0)
				{
					Log.DebugWriteInfo("@数据驱动方式发起流程(" + fl.getName() + ")设置的发起人员:" + starter + "不存在。");
					continue;
				}
				WebUser.SignInOfGener(emp, "CH", false, false, null, null);
			}


				///#region  给值.
			//System.Collections.Hashtable ht = new Hashtable();
			//创建workid.
			long workID = Dev2Interface.Node_CreateBlankWork(fl.getNo(), null, null, starter, null);

			//创建工作.
			GEEntity wk = new GEEntity(frmID, workID);
			// Work wk = fl.NewWork();

			//给主表赋值.
			for (DataColumn dc : dtMain.Columns)
			{
				wk.SetValByKey(dc.ColumnName, dr.getValue(dc.ColumnName).toString());
			}

			// 获取从表数据.
			DataSet ds = new DataSet();
			String[] dtlSQLs = me.getTag1().split("[*]", -1);
			for (String sqlDtl : dtlSQLs)
			{
				if (DataType.IsNullOrEmpty(sqlDtl))
				{
					continue;
				}

				//替换变量.
				String mySqlDtl = sqlDtl.replace("@MainPK", mainPK);

				String[] tempStrs = mySqlDtl.split("[=]", -1);
				String dtlName = tempStrs[0];
				DataTable dtlTable = DBAccess.RunSQLReturnTable(mySqlDtl.replace(dtlName + "=", ""));
				dtlTable.TableName = dtlName;
				ds.Tables.add(dtlTable);
			}

				///#endregion  给值.


				///#region 取出约定的到达人员.
			int toNodeID = 0;
			try
			{
				toNodeID = Integer.parseInt(dr.getValue("ToNode").toString());
			}
			catch (java.lang.Exception e)
			{
				/*有可能在4.5以前的版本中没有tonode这个约定.*/
			}
			String toEmps = null;
			try
			{
				toEmps = dr.getValue("ToEmps").toString();
			}
			catch (java.lang.Exception e2)
			{
				/*有可能在4.5以前的版本中没有tonode这个约定.*/
			}

				///#endregion 取出约定的到达人员.

			// 处理发送信息.
			//  Node nd =new Node();
			String msg = "";
			try
			{

				//执行发送到下一个节点.
				SendReturnObjs objs = Dev2Interface.Node_SendWork(fl.getNo(), workID, wk.getRow(), ds, toNodeID, toEmps);

				msg = "@自动发起:" + mainPK + "第" + idx + "条:  - " + objs.getMsgOfText();
				Log.DebugWriteInfo(msg);
			}
			catch (RuntimeException ex)
			{
				Log.DebugWriteError("@" + fl.getName() + ",第" + idx + "条,发起人员:" + WebUser.getNo() + "-" + WebUser.getName() + "发起时出现错误.\r\n" + ex.getMessage());
			}
		}

			///#endregion 处理流程发起.
	}
}
