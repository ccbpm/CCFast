package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.difference.*;

/** 
  角色类型
*/
public class StationType extends EntityNoName
{

		///#region 属性
	/** 
	 组织编码
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(StationAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(StationAttr.OrgNo, value);
	}
	public final String getFKStationType()  {
		return this.GetValStrByKey(StationAttr.FK_StationType);
	}
	public final void setFKStationType(String value){
		this.SetValByKey(StationAttr.FK_StationType, value);
	}

	public final String getFKStationTypeText()  {
		return this.GetValRefTextByKey(StationAttr.FK_StationType);
	}


		///#endregion


		///#region 实现基本的方方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}

		///#endregion


		///#region 构造方法
	/** 
	 角色类型
	*/
	public StationType()
	{
	}
	/** 
	 角色类型
	 
	 @param _No
	*/
	public StationType(String _No) throws Exception {
		super(_No);
	}

		///#endregion

	/**
	 * 角色类型Map
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Port_StationType", "角色类型");
		map.setCodeStruct("2");

		map.AddTBStringPK(StationTypeAttr.No, null, "编号", true, true, 1, 40, 40);
		map.AddTBString(StationTypeAttr.Name, null, "名称", true, false, 1, 50, 20);
		map.AddTBInt(StationTypeAttr.Idx, 0, "顺序", true, false);

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			map.AddTBString(StationAttr.OrgNo, null, "隶属组织", false, false, 0, 50, 250);
			map.AddHidden(StationAttr.OrgNo, "=", "@WebUser.OrgNo"); //加隐藏条件.
		}

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			map.AddTBString(StationAttr.OrgNo, null, "隶属组织", true, true, 0, 50, 250);

			if (SystemConfig.getGroupStationModel() == 0)
			{
				map.AddHidden(StationAttr.OrgNo, "=", "@WebUser.OrgNo"); //每个组织都有自己的岗责体系的时候. 加隐藏条件.
			}
			if (SystemConfig.getGroupStationModel() == 2)
			{
				map.AddTBString(StationAttr.FK_Dept, null, "隶属部门", false, false, 0, 50, 250);
				map.AddHidden(StationAttr.FK_Dept, "=", "@WebUser.FK_Dept");

			}
		}

		this.set_enMap(map);
		return this.get_enMap();
	}
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getName()) == true)
		{
			throw new RuntimeException("请输入名称");
		}

		if (DataType.IsNullOrEmpty(this.getOrgNo()) == true && SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(bp.web.WebUser.getOrgNo());
		}

		if (SystemConfig.getGroupStationModel() == 2)
		{
			this.SetValByKey(StationAttr.FK_Dept, bp.web.WebUser.getDeptNo());
		}

		return super.beforeUpdateInsertAction();
	}

}
