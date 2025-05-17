package bp.wf.template;

import bp.en.*;

/** 
 任务 属性
*/
public class SubSystemAttr extends EntityNoNameAttr
{

		///#region 基本属性
	/**
	 系统根路径
	*/
	public static final String WebHost = "WebHost";
	/**
	 系统私钥
	*/
	public static final String TokenPiv = "TokenPiv";
	/**
	 系统公钥
	*/
	public static final String TokenPublie = "TokenPublie";
	/**
	 系统回调审批态的url全路径
	*/
	public static final String CallBack = "CallBack";
	/**
	 请求模式
	*/
	public static final String RequestMethod = "RequestMethod";
	/** 
	 发起时间
	*/
	public static final String CallMaxNum = "CallMaxNum";
	/** 
	 插入日期
	*/
	public static final String ApiParas = "ApiParas";

	/** 
	 到达节点（可以为0）
	*/
	public static final String ApiNote = "ApiNote";
	public static final String ParaDTModel = "ParaDTModel";

		///#endregion
}
