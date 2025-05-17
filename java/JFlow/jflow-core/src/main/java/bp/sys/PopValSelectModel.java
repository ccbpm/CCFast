package bp.sys;


/** 
 选择模式
*/
public enum PopValSelectModel
{
	/** 
	 单选
	*/
	One,
	/** 
	 多选
	*/
	More;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static PopValSelectModel forValue(int value)
	{
		return values()[value];
	}
}
