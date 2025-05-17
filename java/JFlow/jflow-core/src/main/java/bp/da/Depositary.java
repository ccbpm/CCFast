package bp.da;



/** 
 保管位置
*/
public enum Depositary
{
	 /** 
	 不保管
	 */
	None,
	/** 
	 全体
	*/
	Application;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static Depositary forValue(int value) 
	{
		return values()[value];
	}
}