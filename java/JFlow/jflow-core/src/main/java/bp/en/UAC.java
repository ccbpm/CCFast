package bp.en;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.web.*;

/** 
 访问控制
*/
public class UAC
{
	/** 
	 从权限管理系统里装载数据.
	 * @ 
	*/
	public final void LoadRightFromCCGPM(Entity en) 
	{
		String sql = "SELECT Tag1  FROM V_GPM_EmpMenu WHERE  FK_Emp='" + WebUser.getNo() + "'  AND Url LIKE '%" + en.toString() + "%'  ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		for (DataRow dr : dt.Rows)
		{
			String tag = dr.getValue(0).toString();

			if (tag.contains("Insert") == true)
			{
				this.IsInsert = true;
			}
			if (tag.contains("Update") == true)
			{
				this.IsUpdate = true;
			}
			if (tag.contains("Delete") == true)
			{
				this.IsDelete = true;
			}
		}

	}
	public final void Readonly()
	{
		this.IsUpdate=false;
		this.IsDelete=false;
		this.IsInsert=false;
		this.IsAdjunct = false;
		this.IsView = true;
	}
	/** 
	 全部开放
	*/
	public final void OpenAll()
	{
		this.IsUpdate = true;
		this.IsDelete = true;
		this.IsInsert = true;
		this.IsAdjunct = false;
		this.IsView = true;
	}
	/** 
	 为一个岗位设置全部的权限
	 
	 param fk_station
	 * @ 
	*/
	public final void OpenAllForStation(String fk_station) 
	{
		Paras ps = new Paras();
		ps.Add("FK_Emp", WebUser.getNo());
		ps.Add("st", fk_station);

		boolean bl;

		bl = DBAccess.IsExits("SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Emp=" + SystemConfig.getAppCenterDBVarStr() + "FK_Emp AND FK_Station=" + SystemConfig.getAppCenterDBVarStr() + "st", ps);

		if (bl)
		{
			this.OpenAll();
		}
		else
		{
			this.Readonly();
		}
	}
	/** 
	 仅仅对管理员
	 * @ 
	*/
	public final UAC OpenForSysAdmin() 
	{
//		if (WebUser.getNo().equals("admin") == true)
//		{
//			this.OpenAll();
//		}
		if(bp.web.WebUser.getIsAdmin())
			this.OpenAll();
		return this;
	}
	public final UAC OpenForAppAdmin() 
	{
//		if (WebUser.getNo() != null && WebUser.getNo().contains("admin") == true)
//		{
//			this.OpenAll();
//		}
		if(bp.web.WebUser.getIsAdmin())
			this.OpenAll();
		return this;
	}

	public final UAC OpenForAdmin()  {
		if (WebUser.getNo() != null && WebUser.getIsAdmin()  == true)
		{
			this.OpenAll();
		}
		else
		{
			this.Readonly();
		}
		return this;
	}

		///


		///控制属性
	/** 
	 是否插入
	*/
	public boolean IsInsert = false;
	/** 
	 是否删除
	*/
	public boolean IsDelete = false;
	/** 
	 是否更新
	*/
	public boolean IsUpdate = false;
	/** 
	 是否查看
	*/
	public boolean IsView = true;
	/** 
	 附件
	*/
	public boolean IsAdjunct = false;
	/** 
	 是否可以导出
	 <p>注意：要启用导出权限控制，请使用uac.IsExp = UserRegedit.HaveRoleForExp(this.ToString());</p>
	*/
	public boolean IsExp = false;
	/** 
	 是否可以导入
	 <p>注意：要启用导入权限控制，请使用uac.IsImp = UserRegedit.HaveRoleForImp(this.ToString());</p>
	*/
	public boolean IsImp = false;

		///


		///构造
	/** 
	 用户访问
	*/
	public UAC()
	{

	}

		///
}