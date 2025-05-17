package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.difference.*;

/** 
 角色
*/
public class Station extends EntityNoName
{

		///#region 属性
	public final String getFKStationType()  {
		return this.GetValStrByKey(StationAttr.FK_StationType);
	}
	public final void setFKStationType(String value){
		this.SetValByKey(StationAttr.FK_StationType, value);
	}
	/** 
	 组织编码
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(StationAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(StationAttr.OrgNo, value);
	}
	public final int getIdx()  {
		return this.GetValIntByKey(StationAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(StationAttr.Idx, value);
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
	 角色
	*/
	public Station()
	{
	}
	/** 
	 角色
	 
	 @param _No
	*/
	public Station(String _No) throws Exception {
		super(_No);
	}
	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_Station", "角色");
	   // map.setCodeStruct("3");
	  //  map.setIsAutoGenerNo(true);

		map.AddTBStringPK(StationAttr.No, null, "编号", true, true, 1, 50, 200);
		map.AddTBString(StationAttr.Name, null, "名称", true, false, 0, 100, 200);
		map.AddDDLEntities(StationAttr.FK_StationType, null, "类型", new StationTypes(), true);
		map.AddTBString(StationAttr.OrgNo, null, "隶属组织", false, false, 0, 50, 250);


			///#region 根据组织结构类型不同.
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			map.AddHidden(StationAttr.OrgNo, "=", "@WebUser.OrgNo"); //加隐藏条件.
		}

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			if (SystemConfig.getGroupStationModel() == 0)
			{
				map.AddHidden(StationAttr.OrgNo, "=", "@WebUser.OrgNo"); //每个组织都有自己的岗责体系的时候. 加隐藏条件.
			}
			//每个部门都有自己的角色体系.
			if (SystemConfig.getGroupStationModel() == 2)
			{
				map.AddTBString(StationAttr.FK_Dept, null, "部门编号", true, false, 0, 100, 200);
				map.AddHidden(StationAttr.FK_Dept, "=", "@WebUser.FK_Dept"); //每个组织都有自己的岗责体系的时候. 加隐藏条件.
			}
		}

		map.AddTBInt(StationAttr.Idx, 0, "顺序号", true, false);

			///#endregion 根据组织结构类型不同.

		map.AddSearchAttr(StationAttr.FK_StationType);

		//角色下的用户.
	   // map.AddDtl(new DeptEmpStations(), DeptEmpStationAttr.FK_Station, null, DtlEditerModel.DtlSearch, null);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()))
		{
			this.setNo(DBAccess.GenerGUID());
		}

		return super.beforeInsert();
	}

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getName()) == true)
		{
			throw new RuntimeException("请输入名称");
		}
		if (DataType.IsNullOrEmpty(this.getFKStationType()) == true)
		{
			throw new RuntimeException("请选择类型");
		}

		if (DataType.IsNullOrEmpty(this.getOrgNo()) && SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(bp.web.WebUser.getOrgNo());
		}

		//每个部门都有自己的角色体系.
		if (SystemConfig.getGroupStationModel() == 2)
		{
			this.SetValByKey(StationAttr.FK_Dept, bp.web.WebUser.getDeptNo());
		}


		bp.sys.base.Glo.WriteUserLog("新建/修改角色:" + this.ToJson(), "组织数据操作");

		return super.beforeUpdateInsertAction();
	}
	@Override
	protected boolean beforeDelete() throws Exception
	{
		bp.sys.base.Glo.WriteUserLog("删除角色:" + this.ToJson(), "组织数据操作");
		return super.beforeDelete();
	}

}
