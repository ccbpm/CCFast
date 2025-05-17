package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.wf.template.*;
import bp.ccfast.ccmenu.*;
import bp.*;
import bp.wf.*;

/** 
 页面功能实体
*/
public class WF_GPM extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_GPM()
	{

	}

	/** 
	 清除缓存
	 
	 @return 
	*/
	public final String PowerCenter_DoClearCache()
	{
		String ctrlGroup = this.GetRequestVal("CtrlGroup");

		String sql = "";
		if (ctrlGroup.equals("Menu") == true)
		{
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				sql = "DELETE FROM Sys_UserRegedit WHERE OrgNo='" + WebUser.getOrgNo() + "' AND CfgKey='" + ctrlGroup + "' ";
			}
			else
			{
				sql = "DELETE FROM Sys_UserRegedit WHERE  CfgKey='" + ctrlGroup + "' ";
			}
			DBAccess.RunSQL(sql);
		}
		return "清除成功.";
	}
	/** 
	 模块移动.
	 
	 @return 
	*/
	public final String Module_Move()
	{
		String sortNo = this.GetRequestVal("RootNo");
		String[] EnNos = this.GetRequestVal("EnNos").split("[,]", -1);
		for (int i = 0; i < EnNos.length; i++)
		{
			String enNo = EnNos[i];
			String sql = "UPDATE GPM_Module SET Idx=" + i + ", SystemNo='" + sortNo + "' WHERE No='" + enNo + "'";
			DBAccess.RunSQL(sql);
		}
		return "模块顺序移动成功..";
	}
	public final String System_Move()
	{
		String[] EnNos = this.GetRequestVal("EnNos").split("[,]", -1);
		for (int i = 0; i < EnNos.length; i++)
		{
			String enNo = EnNos[i];
			String sql = "UPDATE GPM_System SET Idx=" + i + " WHERE No='" + enNo + "'";
			DBAccess.RunSQL(sql);
		}
		return "系统顺序移动成功..";
	}
	/** 
	 菜单移动
	 
	 @return 
	*/
	public final String Menu_Move()
	{
		String sortNo = this.GetRequestVal("RootNo");
		String[] EnNos = this.GetRequestVal("EnNos").split("[,]", -1);
		for (int i = 0; i < EnNos.length; i++)
		{
			String enNo = EnNos[i];

			String sql = "UPDATE GPM_Menu SET Idx=" + i + ", ModuleNo='" + sortNo + "' WHERE No='" + enNo + "'";
			DBAccess.RunSQL(sql);
		}
		return "菜单顺序移动成功..";
	}


	public final String Home_Init()
	{
		String str = bp.difference.SystemConfig.getPathOfData() + "XML/BarTemp.xml";
		DataSet ds = new DataSet();
		ds.readXml(str);

		DataTable dt = ds.Tables.get(0);
		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 创建系统
	 
	 @return 
	*/
	public final String NewSystem_Save() throws Exception {
		String rootNo = "0";
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			rootNo = WebUser.getOrgNo();
		}

		//创建根目录。
		SysFormTree frmTree = new SysFormTree();
		if (rootNo.equals("0") == true)
		{
			int i = frmTree.Retrieve(SysFormTreeAttr.ParentNo, rootNo);
		}
		else
		{
			frmTree.Retrieve(SysFormTreeAttr.No, rootNo);
		}

		//类别.
		FlowSort fs = new FlowSort();
		if (rootNo.equals("0") == true)
		{
			fs.Retrieve(SysFormTreeAttr.ParentNo, rootNo);
		}
		else
		{
			fs.Retrieve(SysFormTreeAttr.No, rootNo);
		}

		//系统名称
		String name = this.GetRequestVal("TB_Name");

		//创建系统.
		MySystem system = new MySystem();
		system.setName(name);
		system.setIcon("icon-briefcase");
		system.setOrgNo(WebUser.getOrgNo());
		system.Insert();

		bp.en.EntityTree tempVar = frmTree.DoCreateSubNode(null);
		SysFormTree frmTee = tempVar instanceof SysFormTree ? (SysFormTree)tempVar : null;
		frmTee.setName(name);
		// en.ICON = system.Icon;
		frmTee.setOrgNo(WebUser.getOrgNo());
		frmTree.setIdx(100);
		frmTee.Update();
		DBAccess.RunSQL("UPDATE Sys_FormTree SET No='" + system.getNo() + "' WHERE No='" + frmTee.getNo() + "'");

		bp.en.EntityTree tempVar2 = fs.DoCreateSubNode(null);
		FlowSort myen = tempVar2 instanceof FlowSort ? (FlowSort)tempVar2 : null;
		myen.setName(name);
		myen.setOrgNo(WebUser.getOrgNo());
		myen.setIdx(100);
		myen.Update();
		DBAccess.RunSQL("UPDATE WF_FlowSort SET No='" + system.getNo() + "' WHERE No='" + myen.getNo() + "'");

		//创建模块.
		String modelNo = null;
		for (int i = 0; i <= 5; i++)
		{
			name = this.GetRequestVal("TB_" + i);
			if (DataType.IsNullOrEmpty(name) == true)
			{
				continue;
			}

			Module en = new Module();
			en.setSystemNo(system.getNo());
			en.setName(name);
			en.setIcon("icon-folder");
			en.Insert();
			if (modelNo == null)
			{
				modelNo = en.getNo();
			}
		}

		return system.getNo();
	}
}
