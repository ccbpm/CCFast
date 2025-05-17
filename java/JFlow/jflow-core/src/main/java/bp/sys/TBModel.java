package bp.sys;


/** 
 文本框类型
*/
public enum TBModel
{
	/** 
	 正常的
	*/
	Normal,
	/** 
	 大文本
	*/
	BigDoc,
	/** 
	 富文本
	*/
	RichText,
	/** 
	 超大文本
	*/
	SupperText;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static TBModel forValue(int value)
	{
		return values()[value];
	}
}
