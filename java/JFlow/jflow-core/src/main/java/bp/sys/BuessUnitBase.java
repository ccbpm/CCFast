package bp.sys;

import bp.en.Entity;

/**
 业务单元基类
 1. 重写该类为业务单元子类.
 2. 每个业务单元子类可以在流程事件节点时间设置.
 3. 被继承的子类的必须在BP.*.DLL 里面,才能确保设置时候被映射到.
 4. 子类在DoIt方法中根据WorkID 的书写业务逻辑.
*/
public abstract class BuessUnitBase
{
	/** 
	 主键
	*/
	public long WorkID = 0;
	/// <summary>
	/// 从表编号
	/// </summary>
	public String DtlNo = "";
	/// <summary>
	/// 事件ID
	/// </summary>
	public String EventID = "";
	/// <summary>
	/// 实体参数
	/// </summary>
	public Entity HisEn = null;
	/** 
	 标题
	*/
	public abstract String getTitle();
	/** 
	 执行的方法
	*/
	public String DoIt() throws Exception {
		return null;
	}
}
