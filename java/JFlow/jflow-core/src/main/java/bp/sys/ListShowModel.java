package bp.sys;

import bp.*;

/** 
 从表显示模式
*/
public enum ListShowModel
{
	/** 
	 表格模式
	*/
	Table,
	/** 
	 经典表单模式
	*/
	Card;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ListShowModel forValue(int value)
	{
		return values()[value];
	}
}
