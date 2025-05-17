package bp.pub;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.web.*;
import bp.sys.*;
import bp.difference.*;
import bp.*;

/** 
 柱状图显示类型
*/
public enum ColumnChartShowType
{
	/** 
	 不显示
	*/
	None,
	/** 
	 横向
	*/
	HengXiang,
	/** 
	 竖向
	*/
	ShuXiang,
	/** 
	 横向叠加
	*/
	HengXiangAdd,
	/** 
	 竖向叠加
	*/
	ShuXiangAdd;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ColumnChartShowType forValue(int value)
	{
		return values()[value];
	}
}
