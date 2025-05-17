package bp.wf.template.frm;

import bp.*;
import bp.wf.*;
import bp.wf.template.*;

/** 
 生成的文件打开方式
*/
public enum PrintOpenModel
{
	/** 
	 下载保存
	*/
	DownLoad(0),
	/** 
	 在线WebOffice打开
	*/
	WebOffice(1);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, PrintOpenModel> mappings;
	private static java.util.HashMap<Integer, PrintOpenModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (PrintOpenModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, PrintOpenModel>();
				}
			}
		}
		return mappings;
	}

	private PrintOpenModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static PrintOpenModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
