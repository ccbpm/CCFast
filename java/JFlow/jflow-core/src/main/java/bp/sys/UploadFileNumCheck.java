package bp.sys;
/** 
 上传校验,方式.
*/
public enum UploadFileNumCheck
{
	/** 
	 不校验
	*/
	None,
	/** 
	 不能为空
	*/
	NotEmpty,
	/** 
	 每个类别不能为空.
	*/
	EverySortNoteEmpty;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static UploadFileNumCheck forValue(int value)
	{
		return values()[value];
	}
}
