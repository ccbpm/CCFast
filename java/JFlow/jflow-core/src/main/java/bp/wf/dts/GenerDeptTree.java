package bp.wf.dts;

import bp.da.*;
import bp.difference.StringHelper;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;

/** 
 Method 的摘要说明
*/
public class GenerDeptTree extends Method
{
	/** 
	 不带有参数的方法
	*/
	public GenerDeptTree()
	{
		this.Title = "为部门Port_Dept表生成 TreeNo 字段,跟节点为01。";
		this.Help = "该字段仅仅为了用于LIKE查询，不能作为关联主键，因为该字段是变化的，随着部门的增加而变化.";
		this.Help += "执行此功能要求. 1. Port_Dept, 必须有 TreeNo 字段。 2. Port_Dept 必须有DeptTreeNo 字段. 3. Port_DeptEmp 必须有 DeptTreeNo 字段. 4. Port_DeptEmpStation 必须有 DeptTreeNo 字段.";
		//  this.HisAttrs.AddTBString("Path", "C:/ccflow.Template", "生成的路径", true, false, 1, 1900, 200);
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {
		if (DBAccess.IsExitsTableCol("Port_Dept", "TreeNo") == false)
		{
			return "err@ Port_Dept 没有找到 TreeNo 的列.";
		}

		Dept dept = new Dept();
		int i = dept.Retrieve(DeptAttr.ParentNo, "0");
		if (i == 0)
		{
			return "err@没有找到ParentNo=0的根节点.";
		}

		//更新跟节点的TreeNo. 
		String sql = "UPDATE Port_Dept SET TreeNo='01' WHERE No='" + dept.getNo() + "'";
		DBAccess.RunSQL(sql);

		Depts depts = new Depts();
		depts.Retrieve(DeptAttr.ParentNo, dept.getNo(), null);

		int idx = 0;
		for (Dept item : depts.ToJavaList())
		{
			idx++;

			String subNo = StringHelper.padLeft(String.valueOf(idx), 2, '0');
			sql = "UPDATE Port_Dept SET TreeNo='01" + subNo + "' WHERE No='" + item.getNo() + "'";
			DBAccess.RunSQL(sql);

			sql = "UPDATE Port_DeptEmp SET DeptTreeNo='01" + subNo + "' WHERE FK_Dept='" + item.getNo() + "'";
			DBAccess.RunSQL(sql);
			sql = "UPDATE Port_DeptEmpStation SET DeptTreeNo='01" + subNo + "' WHERE FK_Dept='" + item.getNo() + "'";
			DBAccess.RunSQL(sql);

			SetDeptTreeNo(item, "01" + subNo);
		}

		return "执行成功.";
	}

	public final void SetDeptTreeNo(Dept dept, String pTreeNo) throws Exception {
		Depts depts = new Depts();
		depts.Retrieve(DeptAttr.ParentNo, dept.getNo(), null);

		int idx = 0;
		for (Dept item : depts.ToJavaList())
		{
			idx++;
			String subNo = StringHelper.padLeft(String.valueOf(idx), 2, '0');
			String sql = "UPDATE Port_Dept SET TreeNo='" + pTreeNo + "" + subNo + "' WHERE No='" + item.getNo() + "'";
			DBAccess.RunSQL(sql);

			//更新其他的表字段.
			sql = "UPDATE Port_DeptEmp SET DeptTreeNo='" + pTreeNo + "' WHERE FK_Dept='" + item.getNo() + "'";
			DBAccess.RunSQL(sql);
			sql = "UPDATE Port_DeptEmpStation SET DeptTreeNo='" + pTreeNo + "' WHERE FK_Dept='" + item.getNo() + "'";
			DBAccess.RunSQL(sql);

			//递归调用.
			SetDeptTreeNo(item, pTreeNo + subNo);
		}
	}
}
