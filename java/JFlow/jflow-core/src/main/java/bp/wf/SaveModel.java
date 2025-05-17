package bp.wf;

/** 
 保存模式
*/
public enum SaveModel
{
	/** 
	 仅节点表.
	*/
	NDOnly,
	/** 
	 节点表与Rpt表.
	*/
	NDAndRpt;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SaveModel forValue(int value)
	{
		return values()[value];
	}
}
