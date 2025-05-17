package bp.port;

import bp.da.*;
import bp.difference.*;
import bp.en.*; import bp.en.Map;
import bp.en.Map;
import bp.sys.*;
import bp.web.*;

/** 
 用户组
*/
public class Team extends EntityNoName
{

		///#region 构造方法
	/** 
	 类型
	*/
	public final String getFKTeamType()  {
		return this.GetValStringByKey(TeamAttr.FK_TeamType);
	}
	/** 
	 用户组
	*/
	public Team()
	{
	}
	/** 
	 用户组
	 
	 @param no
	*/
	public Team(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
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

		Map map = new Map("Port_Team", "标签");
		map.setEnType(EnType.Sys);
		map.setItIsAutoGenerNo(true);

		map.AddTBStringPK(TeamAttr.No, null, "编号", true, true, 3, 3, 3);
		map.AddTBString(TeamAttr.Name, null, "名称", true, false, 0, 300, 20);
		map.AddDDLEntities(TeamAttr.FK_TeamType, null, "类型", new TeamTypes(), true);
		map.AddTBInt(TeamAttr.Idx, 0, "顺序", true, false);

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			map.AddTBString(StationAttr.OrgNo, null, "隶属组织", true, true, 0, 50, 250);
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
				map.AddTBString(StationAttr.FK_Dept, null, "隶属部门", true, true, 0, 50, 250);
				map.AddHidden(StationAttr.FK_Dept, "=", "@WebUser.FK_Dept");
			}
		}

		map.AddSearchAttr(TeamAttr.FK_TeamType);
		map.getAttrsOfOneVSM().Add(new TeamEmps(), new Emps(), TeamEmpAttr.FK_Team, TeamEmpAttr.FK_Emp, EmpAttr.Name, EmpAttr.No, "人员");


		////节点绑定人员. 使用树杆与叶子的模式绑定.
		//map.getAttrsOfOneVSM().AddBranchesAndLeaf(new bp.port.TeamEmps(), new bp.port.Emps(),
		//   TeamEmpAttr.FK_Team,
		//   TeamEmpAttr.FK_Emp, "人员(树)", EmpAttr.FK_Dept, EmpAttr.Name, EmpAttr.No, "@WebUser.FK_Dept");

		//map.getAttrsOfOneVSM().Add(new TeamEmps(), new Emps(),
		// TeamEmpAttr.FK_Team, TeamEmpAttr.FK_Emp, EmpAttr.Name, EmpAttr.No, "人员(简单)");

		//map.getAttrsOfOneVSM().Add(new TeamStations(), new Stations(),
		//    TeamEmpAttr.FK_Team, TeamStationAttr.FK_Station, EmpAttr.Name, EmpAttr.No, "角色(简单)");


		//map.getAttrsOfOneVSM().AddTeamListModel(new TeamStations(), new bp.port.Stations(),
		//  TeamStationAttr.FK_Team,
		//  TeamStationAttr.FK_Station, "角色(平铺)", StationAttr.FK_StationType);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(this.GetValStringByKey("OrgNo")) == true)
			{
				this.SetValByKey("OrgNo", WebUser.getOrgNo());
			}
		}
		if (DataType.IsNullOrEmpty(this.GetValStringByKey("No")) == true)
		{
			this.SetValByKey("No", DBAccess.GenerGUID());
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

		if (DataType.IsNullOrEmpty(this.getFKTeamType()) == true)
		{
			throw new RuntimeException("请选择类型");
		}

		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(this.GetValStringByKey("OrgNo")) == true)
			{
				this.SetValByKey("OrgNo", WebUser.getOrgNo());
			}
		}

		return super.beforeUpdateInsertAction();
	}
}
