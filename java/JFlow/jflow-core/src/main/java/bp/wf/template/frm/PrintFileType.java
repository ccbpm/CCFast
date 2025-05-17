package bp.wf.template.frm;

import bp.*;
import bp.wf.*;
import bp.wf.template.*;

/** 
 生成的类型
*/
public enum PrintFileType
{
	/** 
	 Word
	*/
	Word(0),
	PDF(1),
	Excel(2),
	Html(3),
	RuiLang(5);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, PrintFileType> mappings;
	private static java.util.HashMap<Integer, PrintFileType> getMappings()
	{
		if (mappings == null)
		{
			synchronized (PrintFileType.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, PrintFileType>();
				}
			}
		}
		return mappings;
	}

	private PrintFileType(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static PrintFileType forValue(int value)
	{
		return getMappings().get(value);
	}
}
