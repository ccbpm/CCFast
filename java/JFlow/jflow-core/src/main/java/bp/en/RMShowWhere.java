package bp.en;

import bp.da.*;
import bp.*;
import java.util.*;
import java.io.*;

/** 
 显示位置
*/
public enum RMShowWhere
{
	/** 
	 实例左侧
	*/
	EnLeft,
	/** 
	 实例工具栏
	*/
	EnToolbar,
	/** 
	 查询工具栏
	*/
	SearchToolbar;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static RMShowWhere forValue(int value)
	{
		return values()[value];
	}
}
