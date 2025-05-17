package bp.sys;


/** 
 表单事件列表
*/
public enum FrmEvenList11
{
	/** 
	 创建OID
	*/
	CreateOID,
	/** 
	 装载前
	*/
	FrmLoadBefore,
	/** 
	 装载后
	*/
	FrmLoadAfter,
	/** 
	 保存前
	*/
	SaveBefore,
	/** 
	 保存后
	*/
	SaveAfter;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FrmEvenList11 forValue(int value)
	{
		return values()[value];
	}
}
