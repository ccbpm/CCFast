package bp.cloud;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.web.*;
import bp.port.*;

import java.util.*;

/** 
 部门
*/
public class Dept extends EntityTree
{

		///#region 属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(DeptAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(DeptAttr.OrgNo, value);
	}
	/** 
	 领导
	*/
	public final String getLeader()  {
		return this.GetValStrByKey(DeptAttr.Leader);
	}
	public final void setLeader(String value)  {
		this.SetValByKey(DeptAttr.Leader, value);
	}
	/** 
	 部门ID
	*/
	public final String getRefID()  {
		return this.GetValStrByKey(DeptAttr.RefID);
	}
	public final void setRefID(String value)  {
		this.SetValByKey(DeptAttr.RefID, value);
	}
	public final String getRefParentID()  {
		return this.GetValStrByKey(DeptAttr.RefParentID);
	}
	public final void setRefParentID(String value)  {
		this.SetValByKey(DeptAttr.RefParentID, value);
	}
	/** 
	 adminer
	*/
	public final String getAdminer()  {
		return this.GetValStrByKey(DeptAttr.Adminer);
	}
	public final void setAdminer(String value)  {
		this.SetValByKey(DeptAttr.Adminer, value);
	}
	/** 
	 全名
	*/
	public final String getNameOfPath()  {
		return this.GetValStrByKey(DeptAttr.NameOfPath);
	}
	public final void setNameOfPath(String value)  {
		this.SetValByKey(DeptAttr.NameOfPath, value);
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

		///#endregion


		///#region 构造函数
	/** 
	 部门
	*/
	public Dept()
	{
	}
	/** 
	 部门
	 
	 @param no 编号
	*/
	public Dept(String no) throws Exception  {
		super(no);
	}

		///#endregion


		///#region 重写方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsInsert = false;
		return uac;
	}
	/**
	 * Map
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
			return this.get_enMap();

		Map map = new Map("Port_Dept", "部门");

		map.AddTBStringPK(DeptAttr.No, null, "编号", true, true, 1, 50, 20);
		map.AddTBString(DeptAttr.Name, null, "部门名称", true, false, 0, 100, 30, false);

		map.AddTBString(DeptAttr.NameOfPath, null, "部门全称", true, true, 0, 100, 30, true);


		map.AddTBString(DeptAttr.ParentNo, null, "父节点编号", true, true, 0, 100, 30);
		map.AddTBString(DeptAttr.OrgNo, null, "组织编码", true, true, 0, 100, 30);

		map.AddTBString(DeptAttr.Leader, null, "部门负责人", true, false, 0, 100, 30);

		map.AddTBString(DeptAttr.Adminer, null, "管理员帐号", false, false, 0, 100, 30);
		map.AddTBInt(DeptAttr.Idx, 0, "Idx", false, false); //顺序号.

		//微信，丁丁的第三方的ID.
		map.AddTBString(DeptAttr.RefID, null, "RefID", false, true, 0, 100, 30);
		map.AddTBString(DeptAttr.RefParentID, null, "RefParentID", false, true, 0, 100, 30);

		////节点绑定人员. 使用树杆与叶子的模式绑定.
		//map.getAttrsOfOneVSM().AddBranchesAndLeaf(new DeptEmps(), new bp.port.Emps(),
		//   DeptEmpAttr.FK_Dept,
		//   DeptEmpAttr.FK_Emp, "对应人员", EmpAttr.FK_Dept, EmpAttr.Name,
		//   EmpAttr.No, "@WebUser.FK_Dept");

		RefMethod rm = new RefMethod();
		rm.Title = "重置部门全称";
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

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeDelete() throws Exception
	{
		DeptEmps ens = new DeptEmps();
		ens.Retrieve(DeptEmpAttr.FK_Dept, this.getNo(), null);
		if (!ens.isEmpty())
			throw new RuntimeException("err@删除部门错误，该部门下有人员。");

		DeptEmpStations ensD = new DeptEmpStations();
		ensD.Retrieve(DeptEmpAttr.FK_Dept, this.getNo(), null);
		if (!ensD.isEmpty())
			throw new RuntimeException("err@删除部门错误，该部门角色下有人员。");

		return super.beforeDelete();
	}

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
			this.setNo(DBAccess.GenerGUID(5, "Port_Dept", "No"));

		if (DataType.IsNullOrEmpty(this.getOrgNo()) == true)
			this.setOrgNo(WebUser.getOrgNo());

		this.setAdminer(WebUser.getNo());
		return super.beforeInsert();
	}
	/** 
	 创建下级节点.
	 
	 @return 
	*/
	public final String DoMyCreateSubNode() throws Exception {
		Entity en = this.DoCreateSubNode(null);
		return en.ToJson(true);
	}

	/** 
	 创建同级节点.
	 
	 @return 
	*/
	public final String DoMyCreateSameLevelNode() throws Exception {
		Entity en = this.DoCreateSameLevelNode(null);
		return en.ToJson(true);
	}

	public final String DoSameLevelDept(String no, String name) throws Exception {
		Dept en = new Dept();
		en.setNo(no);
		if (en.RetrieveFromDBSources() == 1)
		{
			return "err@编号已经存在";
		}

		en.setName(name);
		en.setParentNo(this.getParentNo());
		en.Insert();

		return "增加成功..";
	}
	public final String DoSubDept(String no, String name) throws Exception {
		Dept en = new Dept();
		en.setNo(no);
		if (en.RetrieveFromDBSources() == 1)
		{
			return "err@编号已经存在";
		}

		en.setName(name);
		en.setParentNo(this.getNo());
		en.Insert();

		return "增加成功..";
	}
	/** 
	 重置部门
	 
	 @return 
	*/
	public final String DoResetPathName() throws Exception {
		this.GenerNameOfPath();
		return "重置成功.";
	}

	/** 
	 生成部门全名称.
	*/
	public final void GenerNameOfPath() throws Exception {
		String name = this.getName();

		//根目录不再处理.
		if (this.getItIsRoot() == true || this.getNo().equals("100") == true)
		{
			this.setNameOfPath(name);
			this.DirectUpdate();
			this.GenerChildNameOfPath(this.getNo());
			return;
		}

		Dept dept = new Dept();
		dept.setNo(this.getParentNo());
		if (dept.RetrieveFromDBSources() == 0)
		{
			return;
		}

		while (true)
		{
			if (dept.getItIsRoot() || Objects.equals(dept.getNo(), "100"))
			{
				break;
			}

			name = dept.getName() + "\\" + name;
			dept = new Dept(dept.getParentNo());
		}
		//根目录
		name = dept.getName() + "\\" + name;
		this.setNameOfPath(name);
		this.DirectUpdate();

		this.GenerChildNameOfPath(this.getNo());
	}
	/** 
	 处理子部门全名称
	 @param deptNo
	*/
	public final void GenerChildNameOfPath(String deptNo) throws Exception {
		Depts depts = new Depts(deptNo);

		// bp.port.Depts depts = new bp.port.Depts(deptNo);
		if (depts != null && depts.size() > 0)
		{
			for (Dept dept : depts.ToJavaList())
			{
				dept.GenerNameOfPath();
				GenerChildNameOfPath(dept.getNo());

				////更新人员路径信息.
				//bp.port.Emps emps = new Emps();
				//emps.Retrieve(EmpAttr.FK_Dept, this.getNo());
				//foreach (bp.port.Emp emp in emps)
				//    emp.Update();
			}
		}
	}
}
