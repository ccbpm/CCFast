package bp.nbport;

import bp.da.DBAccess;
import bp.da.DataType;
import bp.en.*;
import bp.port.*;
import bp.sys.CCBPMRunModel;
import bp.web.WebUser;

/** 
 部门
*/
public class DeptForJC extends EntityTree
{

		///#region 属性
	public final String getOrgNo()  {
		return this.GetValStrByKey(DeptAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(DeptAttr.OrgNo, value);
	}
	/**
	 父节点的ID
	*/
	public final String getParentNo()  {
		return this.GetValStrByKey(DeptAttr.ParentNo);
	}
	public final void setParentNo(String value){
		this.SetValByKey(DeptAttr.ParentNo, value);
	}
	public final String getNameOfPath()  {
		return this.GetValStrByKey(DeptAttr.NameOfPath);
	}
	public final void setNameOfPath(String value){
		this.SetValByKey(DeptAttr.NameOfPath, value);
	}
	public final String getLeader()  {
		return this.GetValStrByKey(DeptAttr.Leader);
	}
	public final void setLeader(String value){
		this.SetValByKey(DeptAttr.Leader, value);
	}

		///#endregion


		///#region 构造函数
	/**
	 部门
	*/
	public DeptForJC()
	{
	}
	/**
	 部门

	 @param no 编号
	*/
	public DeptForJC(String no) throws Exception  {
		super(no);
	}

		///#endregion


		///#region 重写方法
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
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Port_Dept", "部门");
		map.ItIsEnableVer = false;

		map.AddTBStringPK(DeptAttr.No, null, "编号", true, false, 1, 50, 20);
		map.AddTBString(DeptAttr.Name, null, "名称", true, false, 0, 100, 30);
		map.AddTBString(DeptAttr.NameOfPath, null, "部门路径", true, false, 0, 100, 30);

		map.AddTBString(DeptAttr.ParentNo, null, "父节点编号", true, true, 0, 100, 30);
		map.AddTBString(DeptAttr.OrgNo, null, "OrgNo", true, true, 0, 50, 30);
		map.AddDDLEntities(DeptAttr.Leader, null, "部门领导", new Emps(), true);
		map.AddTBInt(DeptAttr.Idx, 0, "序号", false, true);

		RefMethod rm = new RefMethod();
		//rm.Title = "历史变更";
		//rm.ClassMethodName = this.ToString() + ".History";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//map.AddRefMethod(rm);


			///#region 增加点对多属性
		rm.Title = "重置该部门以下的部门路径";
		rm.ClassMethodName = this.toString() + ".DoResetPathName";
		rm.refMethodType = RefMethodType.Func;

		String msg = "当该部门名称变化后,该部门与该部门的子部门名称路径(Port_Dept.NameOfPath)将发生变化.";
		msg += "\t\n 该部门与该部门的子部门的人员路径也要发生变化Port_Emp列DeptDesc.StaDesc.";
		msg += "\t\n 您确定要执行吗?";
		rm.Warning = msg;

		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "增加同级部门";
		//rm.ClassMethodName = this.ToString() + ".DoSameLevelDept";
		//rm.getHisAttrs().AddTBString("No", null, "同级部门编号", true, false, 0, 100, 100);
		//rm.getHisAttrs().AddTBString("Name", null, "部门名称", true, false, 0, 100, 100);
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "增加下级部门";
		//rm.ClassMethodName = this.ToString() + ".DoSubDept";
		//rm.getHisAttrs().AddTBString("No", null, "同级部门编号", true, false, 0, 100, 100);
		//rm.getHisAttrs().AddTBString("Name", null, "部门名称", true, false, 0, 100, 100);
		//map.AddRefMethod(rm);


		//节点绑定人员. 使用树杆与叶子的模式绑定.
		String rootNo = "0";
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single && (DataType.IsNullOrEmpty(WebUser.getNo()) == true || WebUser.getIsAdmin()  == false))
		{
			rootNo = "@WebUser.FK_Dept";
		}
		else
		{
			rootNo = "@WebUser.OrgNo";
		}
		map.getAttrsOfOneVSM().AddBranchesAndLeaf(new DeptEmps(), new Emps(), DeptEmpAttr.FK_Dept, DeptEmpAttr.FK_Emp, "对应人员", EmpAttr.FK_Dept, EmpAttr.Name, EmpAttr.No, rootNo);



			///#endregion

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		bp.sys.base.Glo.WriteUserLog("新建/修改部门:" + this.ToJson(), "组织数据操作");
		return super.beforeUpdateInsertAction();
	}

	@Override
	protected boolean beforeDelete() throws Exception
	{
		this.CheckIsCanDelete();

		bp.sys.base.Glo.WriteUserLog("删除部门:" + this.ToJson(), "组织数据操作");
		return super.beforeDelete();
	}

	public final boolean CheckIsCanDelete() throws Exception {
		String err = "";
		String sql = "select count(*) FROM Port_Emp WHERE FK_Dept='" + this.getNo() + "'";
		int num = DBAccess.RunSQLReturnValInt(sql);
		if (num != 0)
		{
			err += "err@该部门下有" + num + "个人员数据，您不能删除.";
		}

		sql = "select count(*) FROM Port_DeptEmp WHERE FK_Dept='" + this.getNo() + "'";
		num = DBAccess.RunSQLReturnValInt(sql);
		if (num != 0)
		{
			err += "err@该部门在人员部门信息表里有" + num + "笔数据,您不能删除.";
		}

		sql = "select count(*) FROM Port_DeptEmpStation WHERE FK_Dept='" + this.getNo() + "'";
		num = DBAccess.RunSQLReturnValInt(sql);
		if (num != 0)
		{
			err += "err@该部门在人员部门角色表里有" + num + "笔数据,您不能删除.";
		}

		//检查是否有子级部门.
		sql = "select count(*) FROM Port_Dept WHERE ParentNo='" + this.getNo() + "'";
		if (num != 0)
		{
			err += "err@该部门有" + num + "个子部门,您不能删除.";
		}

		//是不是组织？.
		sql = "select count(*) FROM Port_Org WHERE OrgNo='" + this.getNo() + "'";
		if (num != 0)
		{
			err += "err@该部门是一个组织,您不能删除.";
		}

		if (DataType.IsNullOrEmpty(err) == false)
		{
			throw new RuntimeException(err);
		}
		return true;
	}


	/** 
	 执行排序
	 
	 @param deptIDs
	 @return 
	*/
	public final String DoOrder(String deptIDs)
	{
		String[] ids = deptIDs.split("[,]", -1);

		for (int i = 0; i < ids.length; i++)
		{
			String id = ids[i];
			if (DataType.IsNullOrEmpty(id) == true)
				continue;
			DBAccess.RunSQL("UPDATE Port_Dept SET Idx=" + i + " WHERE No='" + id + "'");
		}
		return "排序成功.";
	}

	public final String History() {
		return "EnVerDtl.htm?EnName=" + this.toString() + "&PK=" + this.getNo();
	}


		///#region 重写查询. 2015.09.31 为适应ws的查询.
	/** 
	 查询
	 
	 @return 
	*/
	@Override
	public int Retrieve() throws Exception {
		return super.Retrieve();
	}
	/** 
	 查询.
	 
	 @return 
	*/
	@Override
	public int RetrieveFromDBSources() throws Exception {
		return super.RetrieveFromDBSources();
	}

		///#endregion

}
