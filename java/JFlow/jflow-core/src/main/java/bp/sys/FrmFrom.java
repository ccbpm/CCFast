package bp.sys;

public enum FrmFrom
{
	Flow,
	Node,
	Dtl;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FrmFrom forValue(int value)
	{
		return values()[value];
	}
}
