package bp.wf.admin;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.web.*;

/** 
 独立组织
*/
public class Org extends EntityNoName
{

		///#region 属性
	/** 
	 父级组织编号
	*/
	public final String getParentNo()  {
		return this.GetValStrByKey(OrgAttr.ParentNo);
	}
	public final void setParentNo(String value){
		this.SetValByKey(OrgAttr.ParentNo, value);
	}
	/** 
	 父级组织名称
	*/
	public final String getParentName()  {
		return this.GetValStrByKey(OrgAttr.ParentName);
	}
	public final void setParentName(String value){
		this.SetValByKey(OrgAttr.ParentName, value);
	}
	/** 
	 父节点编号
	*/
	public final String getAdminer()  {
		return this.GetValStrByKey(OrgAttr.Adminer);
	}
	public final void setAdminer(String value){
		this.SetValByKey(OrgAttr.Adminer, value);
	}
	/** 
	 管理员名称
	*/
	public final String getAdminerName()  {
		return this.GetValStrByKey(OrgAttr.AdminerName);
	}
	public final void setAdminerName(String value){
		this.SetValByKey(OrgAttr.AdminerName, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 独立组织
	*/
	public Org()
	{
	}
	/** 
	 独立组织
	 
	 @param no 编号
	*/
	public Org(String no) throws Exception
	{
		super(no);
	}

		///#endregion


		///#region 重写方法
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	/** 
	 Map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_Org", "系统信息");

		map.AddTBStringPK(OrgAttr.No, null, "编号", true, true, 1, 50, 40);
		map.AddTBString(OrgAttr.Name, null, "单位", true, false, 0, 60, 200);

		map.AddTBInt("FlowNums", 0, "流程模板数", true, true);
		map.AddTBInt("FrmNums", 0, "表单模板数", true, true);
		map.AddTBInt("Users", 0, "用户数", true, true);
		map.AddTBInt("Depts", 0, "部门数", true, true);
		map.AddTBInt("GWFS", 0, "运行中流程", true, true);
		map.AddTBInt("GWFSOver", 0, "完成流程", true, true);

		map.AddTBString(OrgAttr.Adminer, null, "管理员(创始人)", true, true, 0, 60, 200);
		map.AddTBString(OrgAttr.AdminerName, null, "管理员名称", true, true, 0, 60, 200);


			///#region 低代码.
		RefMethod rm = new RefMethod();
		rm.GroupName = "低代码";
		rm.Title = "菜单体系";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".LowCodeList";
		rm.Icon = "icon-grid";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "低代码";
		rm.Title = "新建系统";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".LowCodeNew";
		rm.Icon = "icon-folder";
		map.AddRefMethod(rm);

			///#endregion 流程管理.


			///#region 流程管理.
		rm = new RefMethod();
		rm.GroupName = "流程";
		rm.Title = "流程模板";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FlowTemplate";
		rm.Icon = "icon-grid";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "流程";
		rm.Title = "模板目录";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FlowSorts";
		rm.Icon = "icon-folder";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "流程";
		rm.Title = "流程实例";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FlowGenerWorkFlowView";
		rm.Icon = "icon-layers";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "流程";
		rm.Title = "流程分析";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FlowRptWhite";
		rm.Icon = "icon-chart";
		map.AddRefMethod(rm);

			///#endregion 流程管理.


			///#region 表单管理.
		rm = new RefMethod();
		rm.GroupName = "表单";
		rm.Title = "表单模板";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FrmTemplate";
		rm.Icon = "icon-grid";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "表单";
		rm.Title = "目录";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FrmSort";
		rm.Icon = "icon-chart";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.GroupName = "表单";
		rm.Title = "外键";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FrmFK";
		rm.Icon = "icon-chart";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "表单";
		rm.Title = "数据源";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".FrmDBSrc";
		rm.Icon = "icon-chart";
		map.AddRefMethod(rm);

			///#endregion 表单管理.


			///#region 组织管理.
		rm = new RefMethod();
		rm.GroupName = "组织";
		rm.Title = "组织结构";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".OrgUrl";
		rm.Icon = "icon-grid";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "组织";
		rm.Title = "角色";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".OrgStation";
		rm.Icon = "icon-chart";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "组织";
		rm.Title = "角色类型";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".OrgStationType";
		rm.Icon = "icon-chart";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.GroupName = "组织";
		rm.Title = "部门";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ClassMethodName = this.toString() + ".OrgDept";
		rm.Icon = "icon-chart";
		map.AddRefMethod(rm);

			///#endregion 组织管理.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 低代码.
	public final String LowCodeList()
	{
		return "/WF/GPM/SystemList.htm";
	}
	public final String LowCodeNew()
	{
		return "/WF/GPM/NewSystem.htm";
	}

		///#endregion 低代码.


		///#region 组织.
	public final String OrgUrl()
	{
		return "/GPM/Organization.htm";
	}
	public final String OrgStation()
	{
		return "/WF/Comm/Search.htm?EnsName=bp.port.Stations";
	}
	public final String OrgStationType()
	{
		return "/WF/Comm/Ens.htm?EnsName=bp.port.StationTypes";
	}
	public final String OrgDept()
	{
		return "/WF/Comm/Search.htm?EnsName=bp.port.Depts";
	}

		///#endregion 组织.


		///#region 表单.
	public final String FrmFK()
	{
		return "/WF/Comm/Search.htm?EnsName=BP.Sys.SFTables";
	}
	public final String FrmEnum()
	{
		return "/WF/Comm/Search.htm?EnsName=BP.Sys.EnumMains";
	}
	public final String FrmDBSrc()
	{
		return "/WF/Comm/Search.htm?EnsName=BP.Sys.SFDBSrcs";
	}

	public final String FrmTemplate()
	{
		return "/WF/Comm/Search.htm?EnsName=BP.WF.Template.Frms";
	}
	public final String FrmSort()
	{
		return "/WF/Comm/Ens.htm?EnsName=BP.WF.Template.FrmSorts";
	}

		///#endregion 表单.


		///#region 流程模板.
	public final String FlowTemplate()
	{
		return "/WF/Comm/Search.htm?EnsName=BP.WF.Admin.Flows";
	}
	public final String FlowSorts()
	{
		return "/WF/Comm/Ens.htm?EnsName=BP.WF.Template.FlowSorts";
	}
	public final String FlowGenerWorkFlowView()
	{
		return "/WF/Comm/Search.htm?EnsName=BP.WF.Data.GenerWorkFlowViews";
	}

	public final String FlowRptWhite()
	{
		return "/WF/Comm/Group.htm?EnsName=BP.WF.Data.GenerWorkFlowViews";
	}

		///#endregion 流程模板

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{

		this.SetValByKey("FlowNums", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM WF_Flow WHERE OrgNo='" + WebUser.getOrgNo() + "'"));
		this.SetValByKey("FrmNums", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM Sys_MapData WHERE OrgNo='" + WebUser.getOrgNo() + "'"));

		this.SetValByKey("Users", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM Port_Emp WHERE OrgNo='" + WebUser.getOrgNo() + "'"));
		this.SetValByKey("Depts", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM Port_Dept WHERE OrgNo='" + WebUser.getOrgNo() + "'"));
		this.SetValByKey("GWFS", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM WF_GenerWorkFlow WHERE OrgNo='" + WebUser.getOrgNo() + "' AND WFState!=3"));
		this.SetValByKey("GWFSOver", DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS a FROM WF_GenerWorkFlow WHERE OrgNo='" + WebUser.getOrgNo() + "' AND WFState=3"));

		return super.beforeUpdateInsertAction();
	}
}
