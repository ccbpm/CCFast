package bp.wf.httphandler;

import bp.web.*;
import bp.en.*;
import bp.wf.data.*;
import bp.difference.*;
import bp.wf.*;
import java.util.*;

/** 
 页面功能实体
*/
public class WF_AppGuest extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_AppGuest()
	{
	}
	/** 
	 初始化Home
	 
	 @return 
	*/
	public final String Home_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("UserNo", WebUser.getNo());
		ht.put("UserName", WebUser.getName());

		//系统名称.
		ht.put("SysName", SystemConfig.getSysName());
		ht.put("CustomerName", SystemConfig.getCustomerName());

		ht.put("Todolist_EmpWorks", Dev2Interface.getTodolistEmpWorks());
		ht.put("Todolist_Runing", Dev2Interface.getTodolistRuning());
		ht.put("Todolist_Sharing", Dev2Interface.getTodolistSharing());
		ht.put("Todolist_CCWorks", Dev2Interface.getTodolistCCWorks());
		ht.put("Todolist_Apply", Dev2Interface.getTodolistApply()); //申请下来的任务个数.
		ht.put("Todolist_Draft", Dev2Interface.getTodolistDraft()); //草稿数量.
		ht.put("Todolist_Complete", Dev2Interface.getTodolistComplete()); //完成数量.
		ht.put("UserDeptName", WebUser.getDeptName());

		//我发起
		MyStartFlows myStartFlows = new MyStartFlows();
		QueryObject obj = new QueryObject(myStartFlows);
		obj.AddWhere(MyStartFlowAttr.Starter, WebUser.getNo());
		obj.addAnd();
		//运行中\已完成\挂起\退回\转发\加签\批处理\
		obj.addLeftBracket();
		obj.AddWhere("WFState=2 or WFState=3 or WFState=4 or WFState=5 or WFState=6 or WFState=8 or WFState=10");
		obj.addRightBracket();
		obj.DoQuery();
		ht.put("Todolist_MyStartFlow", myStartFlows.size());

		//我参与
		MyJoinFlows myFlows = new MyJoinFlows();
		obj = new QueryObject(myFlows);
		obj.AddWhere("Emps like '%" + WebUser.getNo() + "%'");
		obj.DoQuery();
		ht.put("Todolist_MyFlow", myFlows.size());

		return bp.tools.Json.ToJsonEntityModel(ht);
	}
}
