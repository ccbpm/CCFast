package bp.wf;
/** 
 打印方式
 @0=不打印@1=打印网页@2=打印RTF模板
*/
public enum PrintDocEnable
{
	/** 
	 不打印
	*/
	None,
	/** 
	 打印网页
	*/
	PrintHtml,
	/** 
	 打印RTF模板
	*/
	PrintRTF,
	/** 
	 打印word
	*/
	PrintWord;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static PrintDocEnable forValue(int value)
	{
		return values()[value];
	}
}
