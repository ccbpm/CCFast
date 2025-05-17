package bp.ccoa;

import bp.da.*;
import bp.web.*;
import bp.en.*;
import bp.en.Map;

/** 
 日程
*/
public class Schedule extends EntityMyPK
{

		///#region 基本属性
	public final String getNianYue()  {
		return this.GetValStrByKey(ScheduleAttr.NianYue);
	}
	public final void setNianYue(String value)  {
		this.SetValByKey(ScheduleAttr.NianYue, value);
	}
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(ScheduleAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(ScheduleAttr.OrgNo, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(ScheduleAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(ScheduleAttr.Rec, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(ScheduleAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(ScheduleAttr.RDT, value);
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
	 日程
	*/
	public Schedule()
	{
	}
	public Schedule(String mypk) throws Exception {
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

		Map map = new Map("OA_Schedule", "日程");

		map.AddMyPK(true);
		map.AddTBString(ScheduleAttr.Name, null, "标题", true, false, 0, 300, 10, true);

		map.AddTBDate(ScheduleAttr.DTStart, null, "开始时间", true, false);
		map.AddTBDate(ScheduleAttr.DTEnd, null, "结束时间", true, false);

		map.AddTBString(ScheduleAttr.TimeStart, null, "TimeStart", true, false, 0, 10, 10);
		map.AddTBString(ScheduleAttr.TimeEnd, null, "TimeEnd", true, false, 0, 10, 10);

		map.AddTBString(ScheduleAttr.ChiXuTime, null, "持续时间", true, false, 0, 10, 10);


		map.AddTBDateTime(ScheduleAttr.DTAlert, null, "提醒时间", true, false);

		map.AddDDLSysEnum(ScheduleAttr.Repeats, 0, "重复", true, false, "Repeat", "@0=永不@1=每年@2=每月");

		map.AddTBString(ScheduleAttr.Local, null, "位置", true, false, 0, 300, 10, true);
		map.AddTBString(ScheduleAttr.MiaoShu, null, "描述", true, false, 0, 300, 10, true);

		map.AddTBString(ScheduleAttr.NianYue, null, "隶属年月", false, false, 0, 10, 10);

		map.AddTBString(ScheduleAttr.OrgNo, null, "OrgNo", false, false, 0, 100, 10);
		map.AddTBString(ScheduleAttr.Rec, null, "记录人", false, false, 0, 100, 10, true);
		map.AddTBDateTime(ScheduleAttr.RDT, null, "记录时间", false, false);


		//RefMethod rm = new RefMethod();
		//rm.Title = "方法参数"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoParas";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		////rm.GroupName = "开发接口";
		////  map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "方法内容"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoDocs";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		////rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

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
		this.setOrgNo(WebUser.getOrgNo());

		this.setRDT(DataType.getCurrentDateTime());
		this.setNianYue(DataType.getCurrentYearMonth());

		return super.beforeInsert();
	}

		///#endregion 执行方法.
}
