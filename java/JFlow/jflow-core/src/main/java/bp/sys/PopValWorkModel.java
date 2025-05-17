package bp.sys;


/** 
 PopVal - 工作方式
*/
public enum PopValWorkModel
{
	/** 
	 禁用
	*/
	None,
	/** 
	 自定义URL
	*/
	SelfUrl,
	/** 
	 表格模式
	*/
	TableOnly,
	/** 
	 表格分页模式
	*/
	TablePage,
	/** 
	 分组模式
	*/
	Group,
	/** 
	 树展现模式
	*/
	Tree,
	/** 
	 双实体树
	*/
	TreeDouble;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static PopValWorkModel forValue(int value)
	{
		return values()[value];
	}
}
