package bp.sys;

/** 
 文件展现方式
*/
public enum FileShowWay
{
	/** 
	 表格
	*/
	Table,
	/** 
	 图片
	*/
	Pict,
	/** 
	 自由模式
	*/
	Free;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FileShowWay forValue(int value)
	{
		return values()[value];
	}
}
