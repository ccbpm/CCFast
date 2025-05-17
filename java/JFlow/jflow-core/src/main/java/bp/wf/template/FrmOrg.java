package bp.wf.template;

import bp.en.*; import bp.en.Map;


/** 
 表单对应组织
*/
public class FrmOrg extends EntityMyPK
{

		///#region 基本属性
	/** 
	表单
	*/
	public final String getFrmID()  {
		return this.GetValStringByKey(FrmOrgAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmOrgAttr.FrmID, value);
	}
	/** 
	 组织
	*/
	public final String getOrgNo()  {
		return this.GetValStringByKey(FrmOrgAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(FrmOrgAttr.OrgNo, value);
	}
	public final String getOrgNoT()  {
		return this.GetValRefTextByKey(FrmOrgAttr.OrgNo);
	}

		///#endregion


		///#region 构造方法
	/** 
	 表单对应组织
	*/
	public FrmOrg()
	{
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

		Map map = new Map("WF_FrmOrg", "表单对应组织");
		map.IndexField = FrmOrgAttr.FrmID;

		map.AddMyPK(false);

		map.AddTBStringPK(FrmOrgAttr.FrmID,null,"表单",true,true,1,100,100);
		map.AddTBString(FlowOrgAttr.OrgNo, null, "到组织", true, true, 1, 150, 150);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
