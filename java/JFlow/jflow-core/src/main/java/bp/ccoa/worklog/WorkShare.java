package bp.ccoa.worklog;

import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.sys.*;

/** 
 日志共享
*/
public class WorkShare extends EntityMyPK
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(WorkShareAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(WorkShareAttr.OrgNo, value);
	}
	public final String getEmpNo()  {
		return this.GetValStrByKey(WorkShareAttr.EmpNo);
	}
	public final void setEmpNo(String value)  {
		this.SetValByKey(WorkShareAttr.EmpNo, value);
	}
	public final String getEmpName()  {
		return this.GetValStrByKey(WorkShareAttr.EmpName);
	}
	public final void setEmpName(String value)  {
		this.SetValByKey(WorkShareAttr.EmpName, value);
	}
	public final String getShareToEmpNo()  {
		return this.GetValStrByKey(WorkShareAttr.ShareToEmpNo);
	}
	public final void setShareToEmpNo(String value)  {
		this.SetValByKey(WorkShareAttr.ShareToEmpNo, value);
	}
	public final String getShareToEmpName()  {
		return this.GetValStrByKey(WorkShareAttr.ShareToEmpName);
	}
	public final void setShareToEmpName(String value)  {
		this.SetValByKey(WorkShareAttr.ShareToEmpName, value);
	}
	public final int getShareState() {
		return this.GetValIntByKey(WorkShareAttr.ShareState);
	}
	public final void setShareState(int value)  {
		this.SetValByKey(WorkShareAttr.ShareState, value);
	}

		///#endregion


		///#region 构造方法
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getIsAdmin())
		{
			uac.IsUpdate = true;
			return uac;
		}
		return super.getHisUAC();
	}
	/** 
	 日志共享
	*/
	public WorkShare()
	{
	}
	public WorkShare(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("OA_WorkShare", "日志共享");

		map.AddMyPK(true);

		map.AddTBString(WorkShareAttr.EmpNo, null, "记录人", false, false, 0, 100, 10, true);
		map.AddTBString(WorkShareAttr.EmpName, null, "记录人名称", false, false, 0, 100, 10, true);

		map.AddTBString(WorkShareAttr.ShareToEmpNo, null, "记录人名称", false, false, 0, 100, 10, true);
		map.AddTBString(WorkShareAttr.ShareToEmpName, null, "记录人名称", false, false, 0, 100, 10, true);

		map.AddTBInt(WorkShareAttr.ShareState, 0, "状态0=关闭,1=分享", false, false);
		map.AddTBString(WorkShareAttr.OrgNo, null, "组织编号", false, false, 0, 50, 10);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{

		this.setOrgNo(WebUser.getOrgNo());

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			this.setMyPK(this.getEmpNo() + "_" + this.getShareToEmpNo());
		}
		else
		{
			this.setMyPK(this.getOrgNo() + "_" + this.getEmpNo() + "_" + this.getShareToEmpNo());
		}

		this.setEmpNo(WebUser.getNo());
		this.setEmpName(WebUser.getName());


		Emp emp = new Emp();
		emp.setNo(this.getShareToEmpNo());
		if (emp.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("err@错误:人员编号不正确." + emp.getNo());
		}

		this.setShareToEmpName(emp.getName());

		this.setShareState(1);

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		return super.beforeUpdate();
	}

		///#endregion 执行方法.
}
