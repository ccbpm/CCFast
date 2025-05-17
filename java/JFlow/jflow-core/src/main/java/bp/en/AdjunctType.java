package bp.en;

/** 
 附件类型
*/
public enum AdjunctType
{
	/** 
	 不需要附件。
	*/
	None,
	/** 
	 图片
	*/
	PhotoOnly,
	/** 
	 word 文档。
	*/
	WordOnly,
	/** 
	 所有的类型
	*/
	ExcelOnly,
	/** 
	 所有的类型。
	*/
	AllType;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AdjunctType forValue(int value) 
	{
		return values()[value];
	}
}