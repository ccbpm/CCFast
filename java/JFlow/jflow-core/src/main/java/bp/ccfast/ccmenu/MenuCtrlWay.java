package bp.ccfast.ccmenu;

import bp.*;
import bp.ccfast.*;

/** 
 菜单控制类型
*/
public enum MenuCtrlWay
{
	/** 
	 按照设置的控制
	*/
	BySetting,
	/** 
	 任何人
	*/
	Anyone,
	/** 
	 仅仅管理员
	*/
	AdminOnly;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static MenuCtrlWay forValue(int value)
	{
		return values()[value];
	}
}
