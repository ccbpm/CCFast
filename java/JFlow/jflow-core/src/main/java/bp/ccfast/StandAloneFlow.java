package bp.ccfast;

import bp.web.*;
import bp.en.*;
import bp.en.Map;

/** 
 独立运行流程设置
*/
public class StandAloneFlow extends EntityNoName
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(StandAloneFlowAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(StandAloneFlowAttr.OrgNo, value);
	}
	/** 
	 记录人
	*/
	public final String getRec()  {
		return this.GetValStrByKey(StandAloneFlowAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(StandAloneFlowAttr.Rec, value);
	}
	/** 
	 记录日期
	*/
	public final String getRDT()  {
		return this.GetValStrByKey(StandAloneFlowAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(StandAloneFlowAttr.RDT, value);
	}
	/** 
	 年月
	*/
	public final String getNianYue()  {
		return this.GetValStrByKey(StandAloneFlowAttr.NianYue);
	}
	public final void setNianYue(String value)  {
		this.SetValByKey(StandAloneFlowAttr.NianYue, value);
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
	 独立运行流程设置
	*/
	public StandAloneFlow()
	{
	}
	public StandAloneFlow(String mypk) throws Exception
	{
		this.setNo(mypk);
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Flow", "独立运行流程设置");

		map.AddTBStringPK(StandAloneFlowAttr.No, null, "流程编号", true, true, 0, 100, 10);
		map.AddTBString(StandAloneFlowAttr.Name, null, "流程名称", true, false, 0, 300, 10);

		//map.AddBoolean(StandAloneFlowAttr.IsStar, false, "是否标星", false, false);
		//map.AddTBString(StandAloneFlowAttr.Is, null, "流程名称", true, false, 0, 300, 10);
		//map.AddTBStringDoc(StandAloneFlowAttr.Docs, null, "内容", true, false);
		//map.AddTBString(StandAloneFlowAttr.OrgNo, null, "OrgNo", false, false, 0, 100, 10);
		//map.AddTBString(StandAloneFlowAttr.Rec, null, "记录人", false, false, 0, 100, 10, true);
		//map.AddTBDateTime(StandAloneFlowAttr.RDT, null, "记录时间", false, false);
		//map.AddTBString(StandAloneFlowAttr.NianYue, null, "NianYue", false, false, 0, 10, 10);
		//map.AddTBInt(StandAloneFlowAttr.IsStar, 0, "是否标星", false, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{
		throw new RuntimeException("err@");
		//return super.beforeInsert();
	}

		///#endregion 执行方法.
}
