package bp.ccoa;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 任务
*/
public class Task extends EntityMyPK
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(TaskAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(TaskAttr.OrgNo, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(TaskAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(TaskAttr.Rec, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(TaskAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(TaskAttr.RecName, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(TaskAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(TaskAttr.RDT, value);
	}
	/** 
	 日期
	*/
	public final String getRiQi()  {
		return this.GetValStrByKey(TaskAttr.RiQi);
	}
	public final void setRiQi(String value)  {
		this.SetValByKey(TaskAttr.RiQi, value);
	}
	/** 
	 年月
	*/
	public final String getDTFrom()  {
		return this.GetValStrByKey(TaskAttr.DTFrom);
	}
	public final void setDTFrom(String value)  {
		this.SetValByKey(TaskAttr.DTFrom, value);
	}
	public final String getDTTo()  {
		return this.GetValStrByKey(TaskAttr.DTTo);
	}
	public final void setDTTo(String value)  {
		this.SetValByKey(TaskAttr.DTTo, value);
	}
	/** 
	 项目数
	*/
	public final int getRefEmpsNo() {
		return this.GetValIntByKey(TaskAttr.RefEmpsNo);
	}
	public final void setRefEmpsNo(int value)  {
		this.SetValByKey(TaskAttr.RefEmpsNo, value);
	}
	/** 
	 第几周？
	*/
	public final int getRefEmpsName() {
		return this.GetValIntByKey(TaskAttr.RefEmpsName);
	}
	public final void setRefEmpsName(int value)  {
		this.SetValByKey(TaskAttr.RefEmpsName, value);
	}
	/** 
	 负责人
	*/
	public final float getManager() {
		return this.GetValFloatByKey(TaskAttr.Manager);
	}
	public final void setManager(float value)  {
		this.SetValByKey(TaskAttr.Manager, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getIsAdmin())
		{
			uac.IsUpdate = true;
			return uac;
		}
		return super.getHisUAC();
	}
	/** 
	 任务
	*/
	public Task()
	{
	}
	public Task(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("OA_Task", "任务");

		map.AddMyPK(true);

		map.AddTBString(TaskAttr.Title, null, "标题", false, false, 0, 500, 10);
		map.AddTBString(TaskAttr.Docs, null, "内容", false, false, 0, 4000, 10);

		map.AddTBString(TaskAttr.ParentNo, null, "父节点ID", false, false, 0, 50, 10);
		map.AddTBInt(TaskAttr.IsSubTask, 0, "是否是子任务", true, false);


		map.AddDDLSysEnum(TaskAttr.TaskPRI, 0, "优先级", true, false, "TaskPRI", "@0=高@1=中@2=低");
		map.AddDDLSysEnum(TaskAttr.TaskSta, 0, "状态", true, false, "TaskSta", "@0=未完成@1=已完成");

		map.AddTBDateTime(TaskAttr.DTFrom, null, "日期从", false, false);
		map.AddTBDateTime(TaskAttr.DTTo, null, "到", false, false);

		map.AddTBString(TaskAttr.ManagerEmpNo, null, "负责人", false, false, 0, 30, 10);
		map.AddTBString(TaskAttr.ManagerEmpName, null, "负责人名称", false, false, 0, 40, 10);

		map.AddTBString(TaskAttr.RefEmpsNo, null, "参与人编号", false, false, 0, 3000, 10);
		map.AddTBString(TaskAttr.RefEmpsName, null, "参与人名称", false, false, 0, 3000, 10);

		map.AddTBString(TaskAttr.RefLabelNo, null, "标签标号", false, false, 0, 3000, 10);
		map.AddTBString(TaskAttr.RefLabelName, null, "标签名称", false, false, 0, 3000, 10);

		map.AddTBString(TaskAttr.OrgNo, null, "组织编号", false, false, 0, 100, 10);
		map.AddTBString(TaskAttr.Rec, null, "记录人", false, false, 0, 100, 10);
		map.AddTBString(TaskAttr.RecName, null, "记录人名称", false, false, 0, 100, 10, true);
		map.AddTBDateTime(TaskAttr.RDT, null, "记录时间", false, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(DBAccess.GenerGUID(0, null, null));
		this.setRec(WebUser.getNo());
		this.setRecName(WebUser.getName());
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(WebUser.getOrgNo());
		}

		this.SetValByKey("RDT", DataType.getCurrentDateTime());

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		////计算条数.
		//this.RefEmpsNo = DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS N FROM OA_TaskDtl WHERE RefPK='" + this.MyPK + "'");

		////计算合计工作小时..
		//this.Manager = DBAccess.RunSQLReturnValInt("SELECT SUM(Hour) + Sum(Minute)/60.00 AS N FROM OA_TaskDtl WHERE RefPK='" + this.MyPK + "'");

		return super.beforeUpdate();
	}

		///#endregion 执行方法.
}
