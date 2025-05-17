package bp.en;


/** 
 工作模式
*/
public enum Dot2DotModel
{
	/** 
	 默认模式
	*/
	Default,
	/** 
	 树模式
	*/
	TreeDept,
	/** 
	 树叶子模式
	*/
	TreeDeptEmp;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static Dot2DotModel forValue(int value) 
	{
		return values()[value];
	}
}