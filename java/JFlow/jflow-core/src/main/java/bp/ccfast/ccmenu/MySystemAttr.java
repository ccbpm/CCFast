package bp.ccfast.ccmenu;

import bp.sys.*;
import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.ccbill.template.*;
import bp.wf.*;
import bp.wf.template.*;
import bp.*;
import bp.ccfast.*;
import java.util.*;
import java.io.*;

/** 
 系统
*/
public class MySystemAttr extends EntityNoNameAttr
{
	/** 
	 顺序
	*/
	public static final String Idx = "Idx";
	/** 
	 应用类型
	*/
	public static final String MySystemModel = "MySystemModel";
	/** 
	 UrlExt
	*/
	public static final String UrlExt = "UrlExt";
	/** 
	 SubUrl
	*/
	public static final String OrgNo = "OrgNo";
	/** 
	 是否启用.
	*/
	public static final String IsEnable = "IsEnable";
	/** 
	 关联菜单编号
	*/
	public static final String RefMenuNo = "RefMenuNo";
	public static final String Icon = "Icon";

	//系统类型
	public static final String SystemType = "SystemType";
	//尾部内容
	public static final String Docs = "Docs";
	//待办
	public static final String Todolist_EmpWorks = "Todolist_EmpWorks";
	//草稿
	public static final String Todolist_Draft = "Todolist_Draft";
	//已完成
	public static final String Todolist_Complete = "Todolist_Complete";
	//退回
	public static final String Todolist_ReturnNum = "Todolist_ReturnNum";
	//抄送
	public static final String Todolist_CCWorks = "Todolist_CCWorks";
	//逾期
	public static final String Todolist_OverWorkNum = "Todolist_OverWorkNum";
	//未阅
	public static final String Todolist_UnRead = "Todolist_UnRead";
	//挂起
	public static final String Todolist_HungupNum = "Todolist_HungupNum";
	public static final String FK_Stations = "FK_Stations";
}
