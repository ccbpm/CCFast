package bp.sys;


/** 
 附件在扩展控件里的显示方式
*/
public enum AthShowModel
{
	/** 
	 简单的
	*/
	Simple,
	/** 
	 只有文件名称
	*/
	FileNameOnly;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AthShowModel forValue(int value)
	{
		return values()[value];
	}
}
