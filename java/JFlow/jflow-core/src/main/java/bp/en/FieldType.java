package bp.en;

/**
 字段类型
*/
public enum FieldType
{
	/**
	 正常的
	*/
	Normal,
	/**
	 主键
	*/
	PK,
	/**
	 外键
	*/
	FK,
	/**
	 枚举
	*/
	Enum,
	/**
	 既是主键又是外键
	*/
	PKFK,
	/**
	 既是主键又是枚举
	*/
	PKEnum,
	/**
	 关连的文本.
	*/
	RefText,
	/**
	 虚拟的
	*/
	NormalVirtual,
	/**
	 多值的
	*/
	MultiValues,
	// 关联表格附件
	RefAthTable;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FieldType forValue(int value)
	{
		return values()[value];
	}
}
