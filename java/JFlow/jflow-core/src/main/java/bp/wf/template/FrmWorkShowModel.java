package bp.wf.template;


/** 
 显示格式
*/
public enum FrmWorkShowModel
{
	/** 
	 表格
	*/
	Table,
	/**
	 自由显示
	 */
	Free,
	/**
	 轨迹时间轴模式
	 */
	TrackTime;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FrmWorkShowModel forValue(int value)
	{
		return values()[value];
	}
}
