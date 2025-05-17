package bp.sys;

/** 
 编码表类型
*/
public enum CodeStruct
{
	/** 
	 普通的编码表
	*/
	NoName,
	/** 
	 树编码表(No,Name,ParentNo)
	*/
	Tree,
	/** 
	 行政机构编码表
	*/
	GradeNoName;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static CodeStruct forValue(int value)
	{
		return values()[value];
	}
}
