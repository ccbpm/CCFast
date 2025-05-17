package bp.ccoa.worklog;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;

/** 
 日志审核
*/
public class WorkChecker extends EntityMyPK
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(WorkCheckerAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(WorkCheckerAttr.OrgNo, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(WorkCheckerAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(WorkCheckerAttr.Rec, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(WorkCheckerAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(WorkCheckerAttr.RDT, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(WorkCheckerAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(WorkCheckerAttr.RecName, value);
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
	 日志审核
	*/
	public WorkChecker()
	{
	}
	public WorkChecker(String mypk) throws Exception
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

		Map map = new Map("OA_WorkChecker", "日志审核");

		map.AddMyPK(true);

		map.AddTBString(WorkCheckerAttr.RefPK, null, "RefPK", false, false, 0, 100, 10);
		map.AddTBString(WorkCheckerAttr.Doc, null, "Doc", false, false, 0, 999, 10);

		map.AddTBInt(WorkCheckerAttr.Cent, 0, "评分", false, false);

		map.AddTBString(WorkCheckerAttr.OrgNo, null, "OrgNo", false, false, 0, 100, 10);
		map.AddTBString(WorkCheckerAttr.Rec, null, "记录人", false, false, 0, 100, 10, true);
		map.AddTBString(WorkCheckerAttr.RecName, null, "记录人", false, false, 0, 100, 10, true);

		map.AddTBDateTime(WorkCheckerAttr.RDT, null, "记录时间", false, false);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(DBAccess.GenerGUID(0, null, null));
		this.setRec(WebUser.getNo());
		this.setRecName(WebUser.getName());
		this.setOrgNo(WebUser.getOrgNo());

		this.setRDT(DataType.getCurrentDateTime());

		return super.beforeInsert();
	}


}
