package bp.wf.template.sflow;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 子流程模式
*/
public enum SubFlowModel
{
	/** 
	 下级
	*/
	SubLevel,
	/** 
	 同级
	*/
	SameLevel;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static SubFlowModel forValue(int value)
	{
		return values()[value];
	}
}
