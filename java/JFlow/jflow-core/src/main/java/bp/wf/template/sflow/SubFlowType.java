package bp.wf.template.sflow;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 子流程类型
*/
public enum SubFlowType
{
	/** 
	 手动的子流程
	*/
	HandSubFlow(0),
	/** 
	 自动触发的子流程
	*/
	AutoSubFlow(1),
	/** 
	 延续子流程
	*/
	YanXuFlow(2);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, SubFlowType> mappings;
	private static java.util.HashMap<Integer, SubFlowType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (SubFlowType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, SubFlowType>();
				}
			}
		}
		return mappings;
	}

	private SubFlowType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static SubFlowType forValue(int value)
	{
		return getMappings().get(value);
	}
}
