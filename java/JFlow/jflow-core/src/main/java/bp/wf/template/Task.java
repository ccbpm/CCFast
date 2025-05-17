package bp.wf.template;

import bp.en.*; import bp.en.Map;
/** 
 任务
*/
public class Task extends EntityMyPK
{

		///#region 属性
	/** 
	 参数
	*/
	public final String getParas()  {
		return this.GetValStringByKey(TaskAttr.Paras);
	}
	public final void setParas(String value){
		this.SetValByKey(TaskAttr.Paras, value);
	}
	/** 
	 发起人
	*/
	public final String getStarter()  {
		return this.GetValStringByKey(TaskAttr.Starter);
	}
	public final void setStarter(String value){
		this.SetValByKey(TaskAttr.Starter, value);
	}
	/** 
	 到达的人员
	*/
	public final String getToEmps()  {
		return this.GetValStringByKey(TaskAttr.ToEmps);
	}
	public final void setToEmps(String value){
		this.SetValByKey(TaskAttr.ToEmps, value);
	}
	/** 
	 到达节点（可以为0）
	*/
	public final int getToNode()  {
		return this.GetValIntByKey(TaskAttr.ToNode);
	}
	public final void setToNode(int value){
		this.SetValByKey(TaskAttr.ToNode, value);
	}
	/** 
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(TaskAttr.FK_Flow);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(TaskAttr.FK_Flow, value);
	}
	/** 
	 发起时间（可以为空）
	*/
	public final String getStartDT()  {
		return this.GetValStringByKey(TaskAttr.StartDT);
	}
	public final void setStartDT(String value){
		this.SetValByKey(TaskAttr.StartDT, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 Task
	*/
	public Task()
	{
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
		Map map = new Map("WF_Task", "任务");


		map.AddMyPK(true); //唯一的主键.
		map.AddTBString(TaskAttr.FK_Flow, null, "流程编号", true, false, 0, 5, 10);
		map.AddTBString(TaskAttr.Starter, null, "发起人", true, false, 0, 200, 10);

		//为上海同事科技增加两个字段. 可以为空.
		map.AddTBInt(TaskAttr.ToNode, 0, "到达的节点", true, false);
		map.AddTBString(TaskAttr.ToEmps, null, "到达人员", true, false, 0, 200, 10);

		map.AddTBString(TaskAttr.Paras, null, "参数", true, false, 0, 4000, 10);

		// TaskSta 0=未发起，1=成功发起，2=发起失败.
		map.AddTBInt(TaskAttr.TaskSta, 0, "任务状态", true, false);

		map.AddTBString(TaskAttr.Msg, null, "消息", true, false, 0, 4000, 10);
		map.AddTBString(TaskAttr.StartDT, null, "发起时间", true, false, 0, 20, 10);
		map.AddTBString(TaskAttr.RDT, null, "插入数据时间", true, false, 0, 20, 10);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
