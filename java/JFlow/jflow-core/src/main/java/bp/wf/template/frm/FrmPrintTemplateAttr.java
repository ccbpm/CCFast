package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;
import java.io.*;

/** 
 打印模板属性
*/
public class FrmPrintTemplateAttr extends bp.en.EntityMyPKAttr
{
	/** 
	 路径
	*/
	public static final String Name = "Name";
	/** 
	 路径
	*/
	public static final String TempFilePath = "TempFilePath";
	/** 
	 NodeID
	*/
	public static final String NodeID = "NodeID";
	/** 
	 流程编号
	*/
	public static final String FlowNo = "FlowNo";
	/** 
	 字段名称
	*/
	public static final String KeyOfEn = "KeyOfEn";
	/** 
	 为生成单据使用
	*/
	public static final String Idx = "Idx";
	/** 
	 单据类型
	*/
	public static final String TemplateFileModel = "TemplateFileModel";
	/** 
	 是否生成PDF
	*/
	public static final String PrintFileType = "PrintFileType";
	/** 
	 二维码生成方式
	*/
	public static final String QRModel = "QRModel";
	/** 
	 文件打开方式
	*/
	public static final String PrintOpenModel = "PrintOpenModel";
	/** 
	 表单的ID
	*/
	public static final String FrmID = "FrmID";
}
