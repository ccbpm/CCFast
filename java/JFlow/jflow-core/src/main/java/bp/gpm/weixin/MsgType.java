package bp.gpm.weixin;

import bp.da.*;
import bp.gpm.weixin.msg.*;
import bp.*;
import java.util.*;

/** 
 消息类型
*/
public enum MsgType
{
	text(0),
	image(1),
	voice(2),
	video(3),
	file(4),
	news(5),
	mapnews(6);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, MsgType> mappings;
	private static java.util.HashMap<Integer, MsgType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (MsgType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, MsgType>();
				}
			}
		}
		return mappings;
	}

	private MsgType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static MsgType forValue(int value)
	{
		return getMappings().get(value);
	}
}
