package bp.wf;

/** 
 位置类型
*/
public enum NodePosType
{
	/** 
	 开始
	*/
	Start,
	/** 
	 中间
	*/
	Mid,
	/** 
	 结束
	*/
	End;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static NodePosType forValue(int value)
	{
		return values()[value];
	}
}
