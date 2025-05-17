package bp.wf;

/** 
 运行平台
*/
public enum Plant
{
	CCFlow,
	JFlow;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static Plant forValue(int value)
	{
		return values()[value];
	}
}
