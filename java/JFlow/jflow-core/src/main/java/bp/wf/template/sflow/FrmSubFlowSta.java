package bp.wf.template.sflow;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 父子流程控件状态
*/
public enum FrmSubFlowSta
{
	/** 
	 不可用
	*/
	Disable,
	/** 
	 可用
	*/
	Enable,
	/** 
	 只读
	*/
	Readonly;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FrmSubFlowSta forValue(int value)
	{
		return values()[value];
	}
}
