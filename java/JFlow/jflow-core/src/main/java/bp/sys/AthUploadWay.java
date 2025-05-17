package bp.sys;
/** 
 附件上传方式
*/
public enum AthUploadWay
{
	/** 
	 继承模式
	*/
	Inherit,
	/** 
	 协作模式
	*/
	Interwork;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static AthUploadWay forValue(int value)
	{
		return values()[value];
	}
}
