package bp.gpm.weixin;

import bp.en.*; import bp.en.Map;
import bp.port.*;
import java.util.*;

public class DTSOrganization extends Method
{
	public DTSOrganization()
	{
		this.Title = "同步微信企业号的通讯录 ";
		this.Help = "将微信企业号中的通讯录同步到本地的组织结构";
		this.Help = "同步完成之后，需要配置人员的角色信息和主部门的信息。";

		this.GroupName = "执行";
	}
	@Override
	public void Init()
	{

	}
	@Override
	public boolean getIsCanDo()
	{
		if (bp.web.WebUser.getIsAdmin() == true)
		{
			return true;
		}
		return false;
	}
	@Override
	public Object Do() throws Exception {

			///#region 读取数据.
		//判断是否配置了企业号
		if ((bp.difference.SystemConfig.getWX_CorpID() == null || bp.difference.SystemConfig.getWX_CorpID().isEmpty()))
		{
			return "err@没有配置企业号相关信息";
		}
		//获取部门列表
		DeptList DeptMentList = new DeptList();
		DeptMentList.RetrieveAll();
		if (DeptMentList.getErrcode() == 0)
		{
			return "err@获得数据期间出现错误.";
		}

			///#endregion 读取数据.


			///#region 清楚现有的数据.
		//先删除所有部门
		Depts depts = new Depts();
		depts.ClearTable();

		//删除所有人员
		Emps emps = new Emps();
		emps.ClearTable();

		//删除部门人员表
		DeptEmps deptEmps = new DeptEmps();
		deptEmps.ClearTable();

		//删除部门人员角色表
		DeptEmpStations deptEmpStations = new DeptEmpStations();
		deptEmpStations.ClearTable();

			///#endregion 清楚现有的数据.


			///#region 写入数据.
		DeptEmp deptEmp = new DeptEmp();
		Emp emp = new Emp();
		for (DeptEntity deptMent : DeptMentList.getDepartment())
		{
			//先插入部门表
			Dept dept = new Dept();
			dept.setNo(deptMent.getId());
			dept.setName(deptMent.getName());
			dept.setParentNo(deptMent.getParentid());
			dept.Insert();

			//获取部门下的人员
			UserList users = new UserList(deptMent.getId());
			if (users.getErrcode() == 0)
			{
				continue;
			}

			for (UserEntity userInfo : users.getUserlist())
			{
				//此处不能同步admin帐号的用户
				if (Objects.equals(userInfo.getUserid(), "admin"))
				{
					continue;
				}

				//如果有，放入部门人员表
				if (emps.Retrieve(EmpAttr.No, userInfo.getUserid(), null) > 0)
				{
					//插入部门人员表
					deptEmp = new DeptEmp();
					deptEmp.setMyPK(deptMent.getId() + "_" + userInfo.getUserid());
					deptEmp.setEmpNo(userInfo.getUserid());
					deptEmp.setDeptNo(deptMent.getId());
					deptEmp.Insert();
				}
				//如果没有，默认主部门是当前第一个
				else
				{
					//插入人员表
					emp = new Emp();
					emp.setNo(userInfo.getUserid());
					emp.setName(userInfo.getName());
					emp.setDeptNo(deptMent.getId());
					emp.setEmail(userInfo.getEmail());
					emp.setTel(userInfo.getMobile());
					emp.Insert();

					//插入部门人员表
					deptEmp = new DeptEmp();
					deptEmp.setMyPK(deptMent.getId() + "_" + userInfo.getUserid());
					deptEmp.setEmpNo(userInfo.getUserid());
					deptEmp.setDeptNo(deptMent.getId());
					deptEmp.Insert();

					//没有角色，不同步，手动分配角色吧
					//GPM.DeptEmpStation deptEmpStation = new bp.port.DeptEmpStation();
					//deptEmpStation.setMyPK(deptMent.id + "_" + userInfo.userid + "";
				}
			}
		}

			///#endregion 写入数据.


			///#region 增加 admin.
		//不管以上有无人员，都添加admin帐号的信息
		//插入admin帐号
		emp = new Emp();
		emp.setNo("admin");
		emp.setName("admin");
		emp.setDeptNo("1"); //默认跟部门为1
		emp.setEmail("");
		emp.setTel("");
		emp.Insert();

		//部门人员表加入admin
		deptEmp = new DeptEmp();
		deptEmp.setMyPK("1_admin");
		deptEmp.setEmpNo("admin");
		deptEmp.setDeptNo("1");
		deptEmp.Insert();

			///#endregion 增加admin.

		return "同步完成..";
	}

}
