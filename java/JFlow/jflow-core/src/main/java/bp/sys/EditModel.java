package bp.sys;

/** 
 从表显示方式
*/
public enum EditModel
{
	/** 
	 表格模式
	*/
	TableModel,
	/** 
	 经典表单模式
	*/
	FoolModel,
	/** 
	 自由表单模式
	*/
	FreeModel;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EditModel forValue(int value)
	{
		return values()[value];
	}
}
