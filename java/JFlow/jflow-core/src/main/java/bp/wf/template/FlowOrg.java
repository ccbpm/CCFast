package bp.wf.template;

import bp.en.*; import bp.en.Map;

/** 
 流程对应组织
*/
public class FlowOrg extends EntityMyPK
{

		///#region 基本属性
	/** 
	流程
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(FlowOrgAttr.FlowNo);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(FlowOrgAttr.FlowNo, value);
	}
	/** 
	 组织
	*/
	public final String getOrgNo()  {
		return this.GetValStringByKey(FlowOrgAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(FlowOrgAttr.OrgNo, value);
	}
	public final String getOrgNoT()  {
		return this.GetValRefTextByKey(FlowOrgAttr.OrgNo);
	}

		///#endregion


		///#region 构造方法
	/** 
	 流程对应组织
	*/
	public FlowOrg()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()  {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_FlowOrg", "流程对应组织");
		map.IndexField = FlowOrgAttr.FlowNo;

		map.AddMyPK(false);

		map.AddTBString(FlowOrgAttr.FlowNo,null,"流程",true,true,1,100,100);
		map.AddTBString(FlowOrgAttr.OrgNo, null, "到组织", true, true, 1, 150, 150);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
