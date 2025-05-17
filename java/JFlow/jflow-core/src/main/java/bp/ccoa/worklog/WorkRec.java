package bp.ccoa.worklog;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 工作日志
*/
public class WorkRec extends EntityMyPK
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
	public final String getNianDu()  {
		return this.GetValStrByKey(WorkRecAttr.NianDu);
	}
	public final void setNianDu(String value)  {
		this.SetValByKey(WorkRecAttr.NianDu, value);
	}
	/** 
	 项目数
	*/
	public final int getNumOfItem() {
		return this.GetValIntByKey(WorkRecAttr.NumOfItem);
	}
	public final void setNumOfItem(int value)  {
		this.SetValByKey(WorkRecAttr.NumOfItem, value);
	}
	/** 
	 第几周？
	*/
	public final int getWeekNum() {
		return this.GetValIntByKey(WorkRecAttr.WeekNum);
	}
	public final void setWeekNum(int value)  {
		this.SetValByKey(WorkRecAttr.WeekNum, value);
	}
	/** 
	 合计小时
	*/
	public final float getHeiJiHour() {
		return this.GetValFloatByKey(WorkRecAttr.HeiJiHour);
	}
	public final void setHeiJiHour(float value)  {
		this.SetValByKey(WorkRecAttr.HeiJiHour, value);
	}
	/** 
	 日志类型
	*/
	public final int getWorkRecModel() {
		return this.GetValIntByKey(WorkRecAttr.WorkRecModel);
	}
	public final void setWorkRecModel(int value)  {
		this.SetValByKey(WorkRecAttr.WorkRecModel, value);
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
	 工作日志
	*/
	public WorkRec()
	{
	}
	public WorkRec(String mypk) throws Exception
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

		Map map = new Map("OA_WorkRec", "工作汇报");

		map.AddMyPK(true);

		map.AddDDLSysEnum(WorkRecAttr.WorkRecModel, 0, "模式", true, false, "WorkRecModel", "@0=日志@1=周报@2=月报");

		map.AddTBString(WorkRecAttr.Doc1, null, "本日内容", true, false, 0, 999, 10, true);
		map.AddTBString(WorkRecAttr.Doc2, null, "明日内容", true, false, 0, 999, 10, true);
		map.AddTBString(WorkRecAttr.Doc3, null, "遇到的问题", true, false, 0, 999, 10, true);

		map.AddTBFloat(WorkRecDtlAttr.HeiJiHour, 0, "合计小时", false, false);

		//map.AddTBInt(WorkRecAttr.Hour, 0, "小时", false, false);
		//map.AddTBInt(WorkRecAttr.Minute, 0, "分钟", false, false);

		map.AddTBInt(WorkRecAttr.NumOfItem, 0, "项目数", false, false);


		map.AddTBString(WorkRecAttr.OrgNo, null, "组织编号", false, false, 0, 100, 10);
		map.AddTBString(WorkRecAttr.Rec, null, "记录人", false, false, 0, 100, 10, true);
		map.AddTBString(WorkRecAttr.RecName, null, "记录人名称", false, false, 0, 100, 10, true);
		map.AddTBDateTime(WorkRecAttr.RDT, null, "记录时间", false, false);

		map.AddTBDate(WorkRecAttr.RiQi, null, "隶属日期", false, false);
		map.AddTBString(WorkRecAttr.NianYue, null, "年月", false, false, 0, 10, 10);
		map.AddTBString(WorkRecAttr.NianDu, null, "年度", false, false, 0, 10, 10);

		//RefMethod rm = new RefMethod();
		//rm.Title = "方法参数"; // "设计表单";
		//rm.ClassMethodName = this.ToString() + ".DoParas";
		//rm.Visable = true;
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Target = "_blank";
		//rm.GroupName = "开发接口";
		//map.AddRefMethod(rm);

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
		//if (this.WorkRecModel == 2 || this.WorkRecModel == 2)
		//{
		int i = this.Retrieve(WorkRecAttr.WorkRecModel, 2, WorkRecAttr.RiQi, this.getRiQi());
		if (i != 0)
		{
			throw new RuntimeException("err@日期为[" + this.getRiQi() + "]的日志已经存在");
		}
		//   }

		this.setMyPK(DBAccess.GenerGUID(0, null, null));
		this.setRec(WebUser.getNo());
		this.setRecName(WebUser.getName());
		this.setOrgNo(WebUser.getOrgNo());

		this.setRDT(DataType.getCurrentDateTime());
		this.setRiQi(DataType.getCurrentDate());

		this.setNianYue(DataType.getCurrentYearMonth()); //隶属年月.
		this.setNianDu(DataType.getCurrentYear()); //年度.

		this.setWeekNum(DataType.getCurrentWeek()); //第几周？

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(WebUser.getOrgNo());
		}


		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		//计算条数.
		this.setNumOfItem(DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS N FROM OA_WorkRecDtl WHERE RefPK='" + this.getMyPK() + "'"));

		//计算合计工作小时..
		this.setHeiJiHour(DBAccess.RunSQLReturnValInt("SELECT SUM(Hour) + Sum(Minute)/60.00 AS N FROM OA_WorkRecDtl WHERE RefPK='" + this.getMyPK() + "'"));

		return super.beforeUpdate();
	}

		///#endregion 执行方法.
}
