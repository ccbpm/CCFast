package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.difference.*;
import bp.wf.port.admin2group.*;
import bp.web.*;


/** 
  流程类别
*/
public class FlowSort extends EntityTree
{

		///#region 属性.
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(FlowSortAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(FlowSortAttr.OrgNo, value);
	}
	public final String getDomainExt()  {
		return this.GetValStrByKey(FlowSortAttr.DomainExt);
	}
	public final void setDomainExt(String value){
		this.SetValByKey(FlowSortAttr.DomainExt, value);
	}

		///#endregion 属性.


		///#region 构造方法
	/** 
	 流程类别
	*/
	public FlowSort()
	{
	}
	/** 
	 流程类别
	 
	 @param _No
	*/
	public FlowSort(String _No) throws Exception {
		super(_No);
	}
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsDelete = false;
		uac.IsInsert = false;
		uac.IsUpdate = true;
		return uac;
	}

		///#endregion

	/** 
	 流程类别Map
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_FlowSort", "流程类别");

		map.AddTBStringPK(FlowSortAttr.No, null, "编号", false, false, 1, 100, 20);
		map.AddTBString(FlowSortAttr.ParentNo, null, "父节点No", false, false, 0, 100, 30);

		map.AddTBString(FlowSortAttr.Name, null, "名称", true, false, 0, 200, 30, true);
		map.AddTBString(FlowSortAttr.ShortName, null, "简称", true, false, 0, 200, 30, true);
		map.AddTBString(FlowSortAttr.OrgNo, "0", "组织编号(0为系统组织)", false, false, 0, 150, 30);
		map.SetHelperAlert(FlowSortAttr.OrgNo, "用于区分不同组织的的流程,比如:一个集团有多个子公司,每个子公司都有自己的业务流程.");

		map.AddTBString(FlowSortAttr.DomainExt, null, "域/系统编号", true, false, 0, 100, 30);
		map.SetHelperAlert(FlowSortAttr.DomainExt, "用于区分不同系统的流程,比如:一个集团有多个子系统每个子系统都有自己的流程,就需要标记那些流程是那个子系统的.");
		map.AddTBInt(FlowSortAttr.Idx, 0, "目录显示顺序(发起列表)", true, false);
		map.AddDDLSysEnum("DirType", 0, "目录类型", true, false, "DirType", "@0=目录@1=系统");
		map.AddDDLSysEnum("MobileColorType", 0, "移动端颜色类型", true, false, "MobileColorType", "@0=蓝色@1=橙色@2=绿色");

		this.set_enMap(map);
		return this.get_enMap();
	}


		///#region 创建节点.
	/** 
	 创建同级目录.
	 
	 @param name
	 @return 
	*/
	public final String DoCreateSameLevelNodeMy(String name) throws Exception {
		//EntityTree en = this.DoCreateSameLevelNode(name);
		FlowSort en = new FlowSort();
		en.Copy(this);
		en.SetValByKey(FlowSortAttr.No, DBAccess.GenerGUID(10, null, null));
		en.SetValByKey(FlowSortAttr.Name, name);
		en.Insert();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc && SystemConfig.getGroupStationModel() == 2)
		{
			//如果当前人员不是部门主要管理员
			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org(WebUser.getOrgNo());
			if (WebUser.getNo().equals(org.getAdminer()) == false)
			{
				OAFlowSort oaSort = new OAFlowSort();
				oaSort.setEmpNo(WebUser.getNo());
				oaSort.setOrgNo(WebUser.getOrgNo());
				oaSort.SetValByKey("RefOrgAdminer", oaSort.getOrgNo() + "_" + oaSort.getEmpNo());
				oaSort.SetValByKey("FlowSortNo", en.getNo());
				oaSort.Insert();
			}
		}
		return en.getNo();
	}
	/** 
	 创建下级目录.
	 
	 @param name
	 @return 
	*/
	public final String DoCreateSubNodeMy(String name) throws Exception {
		//EntityTree en = this.DoCreateSubNode(name);
		FlowSort en = new FlowSort();
		en.Copy(this);
		en.SetValByKey(FlowSortAttr.No, DBAccess.GenerGUID(10, null, null));
		en.SetValByKey(FlowSortAttr.ParentNo, this.getNo());
		en.SetValByKey(FlowSortAttr.Name, name);
		en.Insert();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc && SystemConfig.getGroupStationModel() == 2)
		{
			//如果当前人员不是部门主要管理员
			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org(WebUser.getOrgNo());
			if (WebUser.getNo().equals(org.getAdminer()) == false)
			{
				OAFlowSort oaSort = new OAFlowSort();
				oaSort.setEmpNo(WebUser.getNo());
				oaSort.setOrgNo(WebUser.getOrgNo());
				oaSort.SetValByKey("RefOrgAdminer", oaSort.getOrgNo() + "_" + oaSort.getEmpNo());
				oaSort.SetValByKey("FlowSortNo", en.getNo());
				oaSort.Insert();
			}
		}
		return en.getNo();
	}

		///#endregion 创建节点.


	/** 
	 创建的时候，给他增加一个OrgNo。
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getOrgNo()) == true && bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.SetValByKey("OrgNo", WebUser.getOrgNo());
		}

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		//更新流程引擎控制表.
		String sql = "UPDATE WF_GenerWorkFlow SET DomainExt='" + this.getDomainExt() + "' WHERE FK_FlowSort='" + this.getNo() + "'";
		DBAccess.RunSQL(sql);

		// sql = "UPDATE WF_Flow SET DomainExt='" + this.DomainExt + "' WHERE FK_FlowSort='" + this.getNo() + "'";
		//DBAccess.RunSQL(sql);

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			sql = "UPDATE WF_Emp SET StartFlows='' ";
		}
		else
		{
			sql = "UPDATE WF_Emp SET StartFlows='' ";
		}

		DBAccess.RunSQL(sql);
		return super.beforeUpdate();
	}
	/** 
	 删除之前的逻辑
	 
	 @return 
	*/
	@Override
	protected boolean beforeDelete() throws Exception
	{
		//检查是否有流程？
		Paras ps = new Paras();
		ps.SQL = "SELECT COUNT(*) FROM WF_Flow WHERE FK_FlowSort=" + SystemConfig.getAppCenterDBVarStr() + "fk_flowSort";
		ps.Add("fk_flowSort", this.getNo(), false);
		//String sql = "SELECT COUNT(*) FROM WF_Flow WHERE FK_FlowSort='" + fk_flowSort + "'";
		if (DBAccess.RunSQLReturnValInt(ps) != 0)
		{
			throw new RuntimeException("err@该目录下有流程，您不能删除。");
		}

		//检查是否有子目录？
		ps = new Paras();
		ps.SQL = "SELECT COUNT(*) FROM WF_FlowSort WHERE ParentNo=" + SystemConfig.getAppCenterDBVarStr() + "ParentNo";
		ps.Add("ParentNo", this.getNo(), false);
		//sql = "SELECT COUNT(*) FROM WF_FlowSort WHERE ParentNo='" + fk_flowSort + "'";
		if (DBAccess.RunSQLReturnValInt(ps) != 0)
		{
			throw new RuntimeException("err@该目录下有子目录，您不能删除...");
		}

		return super.beforeDelete();
	}
}
