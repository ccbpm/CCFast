package bp.wf.httphandler;

import bp.da.*;
import bp.web.*;
import bp.port.*;
import bp.en.*;
import bp.wf.*;
import bp.difference.*;
import net.sf.json.JSONObject;

import java.util.*;
import java.time.*;

/**
 页面功能实体
*/
public class CCMobilePortal_SaaS extends bp.difference.handler.DirectoryPageBase
{

	/**
	 构造函数
	*/
	public CCMobilePortal_SaaS() throws Exception {
		WebUser.setSheBei("Mobile");
	}


		///#region  界面
	public final String Home_EditUserName() throws Exception {
		String name = this.GetRequestVal("Name");
		Emp emp = new Emp(WebUser.getOrgNo() + "_" + WebUser.getNo());

		emp.setName(name);
		emp.Update();

		//更改登录人的名字.
		WebUser.setName(name);
		return "修改成功.";
	}

	/**
	 修改部门名字

	 @return
	*/
	public final String Home_EditDeptName() throws Exception {
		String name = this.GetRequestVal("Name");

		Dept dept = new Dept(WebUser.getDeptNo());
		dept.setName(name);
		dept.Update();

		//更改登录人的名字.
		WebUser.setDeptName(name);
		return "修改成功.";
	}

	public final String Home_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("UserNo", WebUser.getNo());
		ht.put("UserName", WebUser.getName());

		//系统名称.
		ht.put("SysName", SystemConfig.getSysName());
		ht.put("CustomerName", SystemConfig.getCustomerName());

		ht.put("Todolist_EmpWorks", Dev2Interface.getTodolistEmpWorks());
		ht.put("Todolist_Runing", Dev2Interface.getTodolistRuning());
		ht.put("Todolist_Complete", Dev2Interface.getTodolistComplete());
		ht.put("Todolist_CCWorks", Dev2Interface.getTodolistCCWorks());

		ht.put("Todolist_HuiQian", Dev2Interface.getTodolistHuiQian()); //会签数量.
		ht.put("Todolist_Drafts", Dev2Interface.getTodolistDraft()); //会签数量.

		return bp.tools.Json.ToJsonEntityModel(ht);
	}
	/**
	 获取当前用户 待处理，已处理，抄送给的消息

	 @return
	*/
	public final String GetGenerWorks() throws Exception {
		DataSet ds = new DataSet();
		//待办列表
		DataTable dt = Dev2Interface.DB_GenerEmpWorksOfDataTable(WebUser.getNo(), 0);
		dt.TableName = "Todolist";
		ds.Tables.add(dt);
		//获取审批过未完成的
		dt = new DataTable();
		dt = Dev2Interface.DB_GenerRuning(WebUser.getNo());
		dt.TableName = "Running";
		ds.Tables.add(dt);

		//获取完成的
		dt = new DataTable();
		dt = Dev2Interface.DB_GenerRuning(WebUser.getNo());
		dt.TableName = "Complte";
		ds.Tables.add(dt);


		//获取抄送当前登录人的列表
		dt = new DataTable();
		dt = Dev2Interface.DB_CCList();
		dt.TableName = "CC";
		ds.Tables.add(dt);
		//返回json格式
		return bp.tools.Json.ToJson(ds);
	}
	/**
	 获取当前用户发起流程的草稿，处理中，已完成的数据

	 @return
	*/
	public final String GetMyStartGenerWorks() throws Exception {
		DataSet ds = new DataSet();
		//流程处理中的
		DataTable dt = Dev2Interface.DB_GenerRuning(WebUser.getNo(), null, true, this.getDomainExt());
		dt.TableName = "Running";
		ds.Tables.add(dt);

		//流程已完成
		dt = Dev2Interface.DB_FlowComplete(WebUser.getNo(), true);
		dt.TableName = "Complete";
		ds.Tables.add(dt);

		//获取草稿中的流程
		dt = Dev2Interface.DB_GenerDraftDataTable();
		dt.TableName = "Draflist";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}

	/**
	 获取当前用户最近使用的流程表单

	 @return
	*/
	public final String GetUseFlowByUserNo()
	{
		String sql = "";
		int top = GetRequestValInt("Top");
		if (top == 0)
		{
			top = 8;
		}

		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = " SELECT TOP " + top + " FK_Flow,FlowName,F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' GROUP BY FK_Flow,FlowName,ICON ORDER By Max(SendDT) DESC";
				break;
			case MySQL:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = " SELECT DISTINCT FK_Flow,FlowName,F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "'  Order By SendDT  limit  " + top;
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = " SELECT * From (SELECT DISTINCT FK_Flow as \"FK_Flow\",FlowName as \"FlowName\",F.Icon ,max(SendDT) SendDT FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' GROUP BY FK_Flow,FlowName,ICON Order By SendDT) WHERE  rownum <=" + top;
				break;
			default:
				throw new RuntimeException("err@系统暂时还未开发使用" + SystemConfig.getAppCenterDBType() + "数据库");
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);


		return bp.tools.Json.ToJson(dt);
	}
	/**
	 获取用户下的组织

	 @return
	*/
	public final String User_OrgNos()
	{
		String userId = this.GetRequestVal("UserID");
		if (DataType.IsNullOrEmpty(userId) == true)
		{
			return "err@请输入用户的登录账号";
		}
		String sql = " SELECT A.OrgNo as No,B.Name,B.OrgSta From Port_Emp A ,Port_Org B Where  A.OrgNo=B.No  AND UserID=" + SystemConfig.getAppCenterDBVarStr() + "UserID";
		Paras ps = new Paras();
		ps.Add("UserID", userId, false);
		DataTable dt = DBAccess.RunSQLReturnTable(sql, ps);
		if (dt.Rows.size() == 0)
		{
			return "err@账号输入错误或者该用户已经被禁用";
		}
		return bp.tools.Json.ToJson(dt);
	}

	public final String User_ChangeOrg() throws Exception {
		String userId = this.GetRequestVal("UserID");
		if (DataType.IsNullOrEmpty(userId) == true)
		{
			return "err@请输入用户的登录账号";
		}
		if (DataType.IsNullOrEmpty(this.getOrgNo()) == true)
		{
			return "err@组织编号不能为空";
		}
		Dev2Interface.Port_Login(userId, this.getOrgNo());
		return "切换成功";
	}
	public final String Home_Near()
	{
		String sql = "";
		int top = GetRequestValInt("Top");
		if (top == 0)
		{
			top = 8;
		}

		switch (SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = " SELECT TOP " + top + " FK_Flow,FlowName,F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' GROUP BY FK_Flow,FlowName,ICON ORDER By Max(SendDT) DESC";
				break;
			case MySQL:
			case PostgreSQL:
			case UX:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "SELECT DISTINCT *  From(SELECT   FK_Flow,FlowName,F.Icon FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "'  Order By SendDT  limit  " + top * 2 + ")A  LIMIT " + top;
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = " SELECT * From (SELECT DISTINCT FK_Flow as \"FK_Flow\",FlowName as \"FlowName\",F.Icon ,max(SendDT) SendDT FROM WF_GenerWorkFlow G ,WF_Flow F WHERE  F.No=G.FK_Flow AND Starter='" + WebUser.getNo() + "' GROUP BY FK_Flow,FlowName,ICON Order By SendDT) WHERE  rownum <=" + top;
				break;
			default:
				throw new RuntimeException("err@系统暂时还未开发使用" + SystemConfig.getAppCenterDBType() + "数据库");
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);


		return bp.tools.Json.ToJson(dt);
	}
	public final String Student_JiaoNaXueFei()
	{
		String no = this.GetRequestVal("No");
		String name = this.GetRequestVal("Name");
		String note = this.GetRequestVal("Note");
		float jine = this.GetRequestValFloat("JinE");


		return "学费缴纳成功[" + no + "][" + name + "][" + note + "][" + jine + "]";

	}
	/**
	 查询流程的

	 @return
	*/
	public final String Search_Init() throws Exception {
		DataSet ds = new DataSet();
		GenerWorkFlows gwfs = new GenerWorkFlows();
		QueryObject qo = new QueryObject(gwfs);
		//关键字查询
		String keyWord = this.GetRequestVal("SearchKey");

		//流程状态
		String wfstate = this.GetRequestVal("WFState");
		if (DataType.IsNullOrEmpty(wfstate) == true)
		{
			qo.AddWhere(GenerWorkFlowAttr.WFState, "!=", 0);
		}
		else
		{
			qo.AddWhere(GenerWorkFlowAttr.WFState, Integer.parseInt(wfstate));
		}

		//查询我发起的，我参与的
		qo.addAnd();
		qo.addLeftBracket();
		qo.AddWhere(GenerWorkFlowAttr.StarterName, WebUser.getNo());
		qo.addOr();
		qo.AddWhere(GenerWorkFlowAttr.Emps, "like", "'%@" + WebUser.getNo() + "@%'");
		qo.addOr();
		qo.AddWhere(GenerWorkFlowAttr.Emps, "like", "'%@" + WebUser.getNo() + ",%'");
		qo.addRightBracket();

		if (DataType.IsNullOrEmpty(keyWord) == false)
		{
			qo.addAnd();
			//模糊查询流程标题，流程名称，发起人，处理人
			qo.addLeftBracket();
			if (Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?"))
			{
				qo.AddWhere(GenerWorkFlowAttr.Title, " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey, '%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
			}
			else
			{
				qo.AddWhere(GenerWorkFlowAttr.Title, " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey|| '%'");
			}

			qo.addOr();
			if (Objects.equals(SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(SystemConfig.getAppCenterDBVarStr(), "?"))
			{
				qo.AddWhere(GenerWorkFlowAttr.FlowName, " LIKE ", SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey, '%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
			}
			else
			{
				qo.AddWhere(GenerWorkFlowAttr.FlowName, " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey|| '%'");
			}
			//qo.addOr();
			//if (bp.difference.SystemConfig.getAppCenterDBVarStr() == "@" || bp.difference.SystemConfig.getAppCenterDBVarStr() == "?")
			//    qo.AddWhere(GenerWorkFlowAttr.Emps, " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey, '%')") : (" '%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
			//else
			//    qo.AddWhere(GenerWorkFlowAttr.Emps, " LIKE ", " '%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "SKey|| '%'");

			qo.getMyParas().Add("SKey", keyWord, false);
			qo.addRightBracket();
		}
		//时间段查询
		int tspan = this.GetRequestValInt("TSpan");
		if (tspan != 3)
		{
			qo.addAnd();
			qo.AddWhere(GenerWorkFlowAttr.TSpan, tspan);
		}


		//发起时间
		String dtFrom = this.GetRequestVal("DTFrom");
		String dtTo = this.GetRequestVal("DTTo");
		if (DataType.IsNullOrEmpty(dtFrom) == true && DataType.IsNullOrEmpty(dtTo) == false)
		{
			qo.addAnd();
			qo.addLeftBracket();
			qo.setSQL(GenerWorkFlowAttr.SendDT + " <= '" + dtTo + "'");
			qo.addRightBracket();
		}
		else if (DataType.IsNullOrEmpty(dtFrom) == false && DataType.IsNullOrEmpty(dtTo) == true)
		{
			qo.addAnd();
			qo.addLeftBracket();
			qo.setSQL(GenerWorkFlowAttr.SendDT + " >= '" + dtFrom + "'");
			qo.addRightBracket();
		}
		else if (DataType.IsNullOrEmpty(dtFrom) == false && DataType.IsNullOrEmpty(dtTo) == false)
		{
			qo.addAnd();
			qo.addLeftBracket();
			dtTo += " 23:59:59";
			qo.setSQL(GenerWorkFlowAttr.SendDT + " >= '" + dtFrom + "'");
			qo.addAnd();
			qo.setSQL(GenerWorkFlowAttr.SendDT + " <= '" + dtTo + "'");
			qo.addRightBracket();
		}

		//增加当前用户所在的组织
		qo.addAnd();
		qo.AddWhere(GenerWorkFlowAttr.OrgNo, WebUser.getOrgNo());
		qo.DoQuery();
		DataTable dt = gwfs.ToDataTableField("WF_GenerWorkFlows");
		ds.Tables.add(dt);
		dt = new DataTable("Todolist_EmpWorks");
		dt.Columns.Add("EmpWorks");
		DataRow dr = dt.NewRow();
		dr.setValue("EmpWorks", Dev2Interface.getTodolistEmpWorks());
		dt.Rows.add(dr);
		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

	public final String JS_SDK_Signature()
	{
		String htmlPage = this.GetRequestVal("htmlPage");
		Hashtable ht = new Hashtable();

		//生成签名的时间戳
		String timestamp = DataType.getCurrentDateByFormart("yyyyMMDDHHmmss");
		//生成签名的随机串
		String nonceStr = DBAccess.GenerGUID(0, null, null);
		String url1 = htmlPage;
		//获取 AccessToken CorpID
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx5c371647cecd1db2&secret=d9db5f92622436dde57bf254b1664ac3";
	   String res = DataType.ReadURLContext(url, 9999); // new HttpWebResponseUtility().HttpResponseGet(url);

		JSONObject jd = JSONObject.fromObject(res);
		if (res.contains("errcode") == true)
		{
			Object errcode = jd.get("errcode");
			if (errcode != null)
			{
				throw new RuntimeException("err@获取token信息失败");
			}
		}

		String access_token = (String)jd.get("access_token");

		//获取jsapi_ticket
		url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=jsapi";
		res = DataType.ReadURLContext(url, 9999);
		jd = JSONObject.fromObject(res);
		if (res.contains("errcode") == true)
		{
			String errcode1 = jd.get("errcode").toString();
			if (errcode1.equals("0") == false)
			{
				throw new RuntimeException("err@获取jsapi_ticket失败");
			}
		}


		String ticket = (String)jd.get("ticket");
		ht.put("timestamp", timestamp);
		ht.put("nonceStr", nonceStr);
		//企业微信的corpID
		ht.put("AppID", "wx5c371647cecd1db2");

		//生成签名算法
		String str1 = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url1 + "";

		ht.put("signature", bp.wf.difference.Glo.Sha1Signature(str1));
		ht.put("str", str1);

		return bp.tools.Json.ToJson(ht);
	}

		///#endregion 界面方法.

}
