package bp.wf.template.frm;

import bp.*;
import bp.wf.*;
import bp.wf.template.*;

/** 
 二维码生成方式
*/
public enum QRModel
{
	/** 
	 不生成
	*/
	None,
	/** 
	 生成
	*/
	Gener;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static QRModel forValue(int value)
	{
		return values()[value];
	}
}
