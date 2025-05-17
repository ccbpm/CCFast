package bp.wf.data;

import bp.en.*;

/** 
 流程
*/
public class FlowSimple extends EntityNoName
{

		///#region 属性
	/** 
	 发起时间点
	*/
	public final String getStartDT()  {
		return this.GetValStringByKey(FlowSimpleAttr.StartDT);
	}
	public final void setStartDT(String value)  throws Exception
	 {
		this.SetValByKey(FlowSimpleAttr.StartDT, value);
	}
	/** 
	 执行的时间点.
	*/
	public final String getDots()  {
		return this.GetValStringByKey(FlowSimpleAttr.Dots);
	}
	public final void setDots(String value)  throws Exception
	 {
		this.SetValByKey(FlowSimpleAttr.Dots, value);
	}
	/** 
	 执行时间
	*/
	public final String getDTOfExe()  {
		return this.GetValStringByKey(FlowSimpleAttr.DTOfExe);
	}
	public final void setDTOfExe(String value)  throws Exception
	 {
		this.SetValByKey(FlowSimpleAttr.DTOfExe, value);
	}
	/** 
	 到达的人员
	*/
	public final String getToEmps()  {
		return this.GetValStringByKey(FlowSimpleAttr.ToEmps);
	}
	public final void setToEmps(String value)  throws Exception
	 {
		this.SetValByKey(FlowSimpleAttr.ToEmps, value);
	}
	public final String getToEmpOfSQLs()  {
		return this.GetValStringByKey(FlowSimpleAttr.ToEmpOfSQLs);
	}
	public final void setToEmpOfSQLs(String value)  throws Exception
	 {
		this.SetValByKey(FlowSimpleAttr.ToEmpOfSQLs, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 FlowSimple
	*/
	public FlowSimple()  {

	}
	/** 
	 重写基类方法
	*/
	@Override
	public bp.en.Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Flow", "流程");
		map.setEnType(EnType.View);

		map.AddTBStringPK(FlowSimpleAttr.No, null, "编号", true, true, 3, 3, 3);
		map.AddTBString(FlowSimpleAttr.Name, null, "名称", true, false, 0, 200, 200, true);
		map.AddTBString("OrgNo", null, "OrgNo", false, false, 0, 200, 200);

		map.AddHidden("OrgNo", " = ", "@WebUser.OrgNo");

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}