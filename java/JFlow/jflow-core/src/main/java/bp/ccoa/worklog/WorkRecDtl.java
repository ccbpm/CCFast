package bp.ccoa.worklog;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 工作内容
*/
public class WorkRecDtl extends EntityMyPK
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(WorkRecAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(WorkRecAttr.OrgNo, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(WorkRecAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(WorkRecAttr.Rec, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(WorkRecAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(WorkRecAttr.RecName, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(WorkRecAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(WorkRecAttr.RDT, value);
	}
	/** 
	 日期
	*/
	public final String getRiQi()  {
		return this.GetValStrByKey(WorkRecAttr.RiQi);
	}
	public final void setRiQi(String value)  {
		this.SetValByKey(WorkRecAttr.RiQi, value);
	}
	/** 
	 年月
	*/
	public final String getNianYue()  {
		return this.GetValStrByKey(WorkRecAttr.NianYue);
	}
	public final void setNianYue(String value)  {
		this.SetValByKey(WorkRecAttr.NianYue, value);
	}

	public final float getHeiJiHour() {
		return this.GetValFloatByKey(WorkRecDtlAttr.HeiJiHour);
	}
	public final void setHeiJiHour(float value)  {
		this.SetValByKey(WorkRecDtlAttr.HeiJiHour, value);
	}
	public final int getHour() {
		return this.GetValIntByKey(WorkRecDtlAttr.Hour);
	}
	public final void setHour(int value)  {
		this.SetValByKey(WorkRecDtlAttr.Hour, value);
	}
	public final int getMinute() {
		return this.GetValIntByKey(WorkRecDtlAttr.Minute);
	}
	public final void setMinute(int value)  {
		this.SetValByKey(WorkRecDtlAttr.Minute, value);
	}
	public final int getWeekNum() {
		return this.GetValIntByKey(WorkRecDtlAttr.WeekNum);
	}
	public final void setWeekNum(int value)  {
		this.SetValByKey(WorkRecDtlAttr.WeekNum, value);
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
	 工作内容
	*/
	public WorkRecDtl()
	{
	}
	public WorkRecDtl(String mypk) throws Exception
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

		Map map = new Map("OA_WorkRecDtl", "工作内容");


		map.AddTBStringPK("MyPK", null, "编号", false, false, 0, 40, 10);

		map.AddTBString(WorkRecDtlAttr.RefPK, null, "RefPK", false, false, 0, 40, 10);

		map.AddTBString(WorkRecDtlAttr.PrjName, null, "项目名称", false, false, 0, 90, 10);
		map.AddTBString(WorkRecDtlAttr.Doc, null, "内容", false, false, 0, 999, 10);
		map.AddTBString(WorkRecDtlAttr.Result, null, "结果", false, false, 0, 999, 10);

		map.AddTBInt(WorkRecDtlAttr.Hour, 0, "小时", false, false);
		map.AddTBInt(WorkRecDtlAttr.Minute, 0, "分钟", false, false);
		map.AddTBFloat(WorkRecDtlAttr.HeiJiHour, 0, "合计小时", false, false);

		map.AddTBString(WorkRecAttr.OrgNo, null, "组织编号", false, false, 0, 100, 10);
		map.AddTBString(WorkRecAttr.Rec, null, "记录人", false, false, 0, 100, 10, true);
		map.AddTBString(WorkRecAttr.RecName, null, "记录人名称", false, false, 0, 100, 10, true);
		map.AddTBDateTime(WorkRecAttr.RDT, null, "记录时间", false, false);

		map.AddTBDate(WorkRecAttr.RiQi, null, "隶属日期", false, false);

		map.AddTBString(WorkRecAttr.NianYue, null, "年月", false, false, 0, 10, 10);
		map.AddTBString(WorkRecAttr.NianDu, null, "年度", false, false, 0, 10, 10);

		map.AddTBInt(WorkRecDtlAttr.WeekNum, 0, "周次", false, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(DBAccess.GenerGUID(0, null, null));

		this.setRec(WebUser.getNo());
		this.setRecName(WebUser.getName());

		this.setOrgNo(WebUser.getOrgNo());

		this.setRDT(DataType.getCurrentDateTime());
	   // this.RiQi = DataType.getCurrentDate();

		this.setNianYue(DataType.getCurrentYearMonth()); //隶属年月.

		//小时数.
		this.setHeiJiHour(this.getHour() + this.getMinute() / 60);

		//第几周.
		this.setWeekNum(DataType.getCurrentWeek());

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(WebUser.getOrgNo());
		}

		return super.beforeInsert();
	}

		///#endregion 执行方法.
}
