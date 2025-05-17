package bp.wf.template;


/** 
 方案类型
*/
public enum FrmSln
{
	/** 
	 默认方案
	*/
	Default,
	/** 
	 只读方案
	*/
	Readonly,
	/** 
	 自定义方案
	*/
	Self;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FrmSln forValue(int value)
	{
		return values()[value];
	}
}
