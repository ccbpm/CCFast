package bp.wf.template.frm;

import bp.*;
import bp.wf.*;
import bp.wf.template.*;

/** 
 模版类型
*/
public enum TemplateFileModel
{
	/** 
	 旧版本的rtf模版格式
	*/
	RTF,
	/** 
	 Word模版格式
	*/
	VSTOForWord,
	/** 
	 Excel模版格式
	*/
	VSTOForExcel,
	/** 
	 WPS模板格式(只适应于专业版)
	*/
	WPS;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static TemplateFileModel forValue(int value)
	{
		return values()[value];
	}
}
