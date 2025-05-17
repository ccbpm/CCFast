package bp.sys;


/** 
 图片应用类型
*/
public enum ImgAppType
{
	/** 
	 图片
	*/
	Img,
	/** 
	 图片公章
	*/
	Seal,
	/** 
	 北京安证通公章CA
	*/
	SealESA,
	/** 
	 二维码
	*/
	QRCode;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ImgAppType forValue(int value)
	{
		return values()[value];
	}
}
