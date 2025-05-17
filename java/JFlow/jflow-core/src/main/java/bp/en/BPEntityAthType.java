package bp.en;

/** 
 实体附件类型
*/
public enum BPEntityAthType
{
	/** 
	 无
	*/
	None,
	/** 
	 单附件
	*/
	Single,
	/** 
	 多附件
	*/
	Multi;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static BPEntityAthType forValue(int value) 
	{
		return values()[value];
	}
}