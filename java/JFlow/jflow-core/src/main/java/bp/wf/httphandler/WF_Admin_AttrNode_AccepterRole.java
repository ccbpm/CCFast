package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.*;
import bp.wf.*;

/** 
 页面功能实体
*/
public class WF_Admin_AttrNode_AccepterRole extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_AttrNode_AccepterRole()
	{
	}


		///#region  界面 .
	/** 
	 清空缓存
	 
	 @return 
	*/
	public final String AccepterRole_ClearStartFlowsCache()
	{
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			DBAccess.RunSQL("UPDATE WF_Emp SET StartFlows=''");
		}
		else
		{
			DBAccess.RunSQL("UPDATE WF_Emp SET StartFlows='' WHERE OrgNo='" + bp.web.WebUser.getOrgNo() + "'");
		}
		return "执行成功 ";
	}
	/** 
	 清楚所有组织的缓存,用于多组织.
	 
	 @return 
	*/
	public final String AccepterRole_ClearAllOrgStartFlowsCache()
	{
		DBAccess.RunSQL("UPDATE WF_Emp SET StartFlows=''");
		return "执行成功 ";
	}

		///#endregion 界面方法.

}
