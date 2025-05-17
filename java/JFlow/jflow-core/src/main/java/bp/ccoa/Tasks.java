package bp.ccoa;

import bp.da.*;
import bp.web.*;
import bp.en.*;
import bp.port.*;
import bp.sys.*;
import java.util.*;

/** 
 任务 s
*/
public class Tasks extends EntitiesMyPK
{


		///#region 查询.
	/** 
	 所有的任务
	 
	 @return 
	*/
	public final String Task_AllTasks() throws Exception {
		QueryObject qo = new QueryObject(this);

		qo.addLeftBracket();
		qo.AddWhere(TaskAttr.Rec, WebUser.getNo());
		qo.addOr();
		qo.AddWhere(TaskAttr.RefEmpsNo, " like ", "%," + WebUser.getNo() + ",%");
		qo.addOr();
		qo.AddWhere(TaskAttr.ManagerEmpNo, " like ", "%," + WebUser.getNo() + ",%");
		qo.addRightBracket();
		qo.addAnd();
		qo.AddWhere(TaskAttr.IsSubTask, 0);

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			qo.addAnd();
			qo.AddWhere(TaskAttr.OrgNo, " = ", WebUser.getOrgNo());
		}
		qo.DoQuery();
		return this.ToJson("dt");
	}

	public final String TextBox_EmpPinYin(String key)
	{
		String whereSQL = " AND OrgNo='" + WebUser.getOrgNo() + "'";
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			whereSQL = "";
		}

		String sql = "";
		sql = "SELECT No, Name FROM Port_Emp WHERE (No like '%" + key + "%' or Name like '%" + key + "%' or PinYin like '%" + key + "%' )  " + whereSQL;
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

	public final String Selecter_DeptEmps() throws Exception {
		DataSet ds = new DataSet();

		Depts depts = new Depts();
		depts.RetrieveAll();

		Emps emps = new Emps();
		emps.RetrieveAll();

		ds.Tables.add(depts.ToDataTableField("Depts"));
		ds.Tables.add(emps.ToDataTableField("Emps"));

		return bp.tools.Json.ToJson(ds);

	}

		///#endregion 重写.


		///#region 重写.
	/** 
	 任务
	*/
	public Tasks()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Task();
	}

		///#endregion 重写.



		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Task> ToJavaList()
	{
		return (java.util.List<Task>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Task> Tolist()
	{
		ArrayList<Task> list = new ArrayList<Task>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Task)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
