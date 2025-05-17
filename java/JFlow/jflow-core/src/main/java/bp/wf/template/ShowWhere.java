package bp.wf.template;


/** 
 显示位置
*/
public enum ShowWhere
{
	/** 
	 树
	*/
	Tree,
	/** 
	 工具栏
	*/
	Toolbar,
	/** 
	 抄送
	*/
	CC;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ShowWhere forValue(int value)
	{
		return values()[value];
	}
}
