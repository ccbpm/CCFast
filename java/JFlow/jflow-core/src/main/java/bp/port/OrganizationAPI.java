package bp.port;

import bp.da.*;
import bp.*;
import bp.difference.SystemConfig;
import bp.sys.CCBPMRunModel;
import bp.tools.AesEncodeUtil;
import bp.tools.BCryptUtils;
import bp.tools.MD5Utill;

/** 
 组织接口API
*/
public class OrganizationAPI
{

		///#region 关于组织结构的接口.
	/** 
	 集团模式下同步组织以及管理员信息.
	 
	 @param orgNo 组织编号
	 @param name 组织名称
	 @param adminer 管理员账号
	 @param adminerName 管理员名字
	 @param keyVals 比如：@Leaer=zhangsan@Tel=12233333@Idx=1
	 @return return 1 增加成功，其他的增加失败.
	*/
	public static String Port_Org_Save(String orgNo, String name, String adminer, String adminerName, String keyVals) throws Exception {
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护组织信息";
		}

		int msg = 0;
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			AtPara ap = new AtPara(keyVals);
			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
			org.setNo(orgNo);
			org.setName(name);
			org.setAdminer(adminer);
			org.setAdminerName(adminerName);
			for (String key : ap.getHisHT().keySet())
			{
				if (DataType.IsNullOrEmpty(key) == true)
				{
					continue;
				}
				org.SetValByKey(key, ap.GetValStrByKey(key));
			}
			msg = org.Insert();

			bp.wf.port.admin2group.OrgAdminer oa = new bp.wf.port.admin2group.OrgAdminer();
			oa.setMyPK(orgNo + "_" + adminer);
			oa.setOrgNo(orgNo);
			oa.setEmpNo(adminer);
			oa.setEmpName(adminerName);
			msg = oa.Insert();
		}
		return String.valueOf(msg);
	}
	/** 
	 保存用户数据, 如果有此数据则修改，无此数据则增加.
	  注意:
	  1. 该方法允许多次调用.
	  2  比如一个人一个部门的时候，就调用一次即可,传入部门编号, 与该部门下的岗位编号集合001,003,004.
	  3  比如一个人n个部门的时候，就调用n次,传入部门编号, 与该部门下的岗位编号集合001,002,003.
	 @param orgNo 组织编号
	 @param userNo 用户编号,如果是saas版本就是orgNo_userID
	 @param userName 用户名称
	 @param deptNo 部门编号
	 @param kvs 属性值，比如: @Name=张三@Tel=18778882345@Pass=123, 如果是saas模式：就必须有@UserID=xxxx 
	 @param stats 角色编号：比如:001,002,003,
	 @return 执行信息.
	*/
	public static String Port_Emp_Save(String orgNo, String userNo, String userName, String deptNo, String kvs, String stats) throws Exception {
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护人员信息";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			if (kvs == null || kvs.indexOf("@UserID=") == -1)
			{
				return "err@saas模式下，需要在kvs参数里，增加@UserID=xxxx 属性.";
			}
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(orgNo) == true)
			{
				return "err@组织编号不能为空.";
			}

			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
			org.setNo(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织编号错误:" + orgNo;
			}
		}
		else
		{
			orgNo = "";
		}

		if (DataType.IsNullOrEmpty(userNo) || DataType.IsNullOrEmpty(userName) || DataType.IsNullOrEmpty(deptNo) == true)
		{
			throw new RuntimeException("err@用户编号，名称，部门不能为空.");
		}

		bp.port.Dept dept = new bp.port.Dept();
		dept.setNo(deptNo);
		if (dept.RetrieveFromDBSources() == 0)
		{
			throw new RuntimeException("err@部门编号错误:" + deptNo);
		}

		try
		{
			//增加人员信息.
			bp.port.Emp emp = new bp.port.Emp();
			emp.setNo(userNo);
			if (emp.RetrieveFromDBSources() == 0)
			{
				emp.setName(userName);
				emp.setDeptNo(deptNo);
				emp.setOrgNo(orgNo);
				emp.Insert();
			}

			AtPara ap = new AtPara(kvs);
			for (String key : ap.getHisHT().keySet())
			{
				if (DataType.IsNullOrEmpty(key) == true)
				{
					continue;
				}
				if ("Pass".equals(key)){
					continue;
				}
				emp.SetValByKey(key, ap.GetValStrByKey(key));
			}
			emp.setDeptNo(deptNo);
			emp.setName(userName);
			emp.setOrgNo(orgNo);
			emp.Update();

			/// #region 处理用户密码
			String pass = ap.GetValStrByKey("Pass");
			if (!DataType.IsNullOrEmpty(pass)){
				String type = SystemConfig.getIsPasswordEncryptionType();
				String encodPass = pass;
				//BCrypt加密类型
				if(type.equals("2")){
					encodPass = BCryptUtils.hashpw(pass);
				}
				//MD5加密类型
				if(type.equals("1")){
					encodPass = MD5Utill.MD5Encode(pass, "UTF8");
				}
				emp.setPass(encodPass);
			}
			/// #endregion 处理用户密码

			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
			{
				DBAccess.RunSQL("DELETE FROM Port_DeptEmp WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+emp.getDeptNo()+"'");
				DBAccess.RunSQL("DELETE FROM Port_DeptEmpStation WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+emp.getDeptNo()+"'");

			}
			else
			{
				DBAccess.RunSQL("DELETE FROM Port_DeptEmp WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+emp.getDeptNo()+"' AND OrgNo='" + orgNo + "'");
				DBAccess.RunSQL("DELETE FROM Port_DeptEmpStation WHERE FK_Emp='" + userNo + "' AND FK_Dept='"+emp.getDeptNo()+"' AND OrgNo='" + orgNo + "'");
			}

			//插入部门.
			bp.port.DeptEmp de = new bp.port.DeptEmp();
			de.setDeptNo(deptNo);
			de.setEmpNo(userNo);
			de.setOrgNo(orgNo);
			de.setDeptName(dept.getName());
			de.setMyPK(de.getDeptNo() + "_" + userNo);

			//更新角色.
			if (stats == null)
			{
				stats = "";
			}
			String[] strs = stats.split("[,]", -1);
			String staNames = "";
			for (int i = 0; i < strs.length; i++)
			{
				String str = strs[i];
				if (DataType.IsNullOrEmpty(str))
				{
					continue;
				}

				if (bp.difference.SystemConfig.getCCBPMRunModel()== CCBPMRunModel.SAAS)
					str= orgNo + "_" + str;


				Station st = new Station();
				st.setNo(str);
				if (st.RetrieveFromDBSources() == 0)
					throw new RuntimeException("err@角色编号错误." + str+"，请检查角色是否同步？");

				staNames += st.getName() +",";

				//插入部门.
				DeptEmpStation des = new DeptEmpStation();
				des.setDeptNo(deptNo);
				des.setEmpNo(userNo);
				des.setStationNo(str);
				des.setOrgNo(orgNo);
				des.setMyPK(de.getDeptNo() + "_" + des.getEmpNo() + "_" + des.getStationNo());
				des.DirectInsert();
			}

			de.setStationNo(stats);
			de.setStationNoT(staNames);
			de.DirectInsert();

			String sql = "UPDATE Port_Emp SET OrgNo='" + orgNo + "' WHERE No='" + emp.getNo() + "'";

			DBAccess.RunSQL(sql);

			return "人员信息保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 保存角色
	 
	 @param userNo
	 @return reutrn 1=成功,  其他的标识异常.
	*/
	public static String Port_Emp_Delete(String orgNo, String userNo) throws Exception
	{
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能删除人员信息";
		}
		try
		{
			//增加人员信息.
			bp.port.Emp emp = new bp.port.Emp();
			emp.setNo(userNo);
			emp.setOrgNo(orgNo);
			if (emp.RetrieveFromDBSources() == 0)
			{
				return "err@该用户【" + userNo + "】不存在.";
			}

			//删除角色.
			DBAccess.RunSQL("delete from port_deptemp where fk_emp='" + userNo + "' AND OrgNo='" + orgNo + "'");
			DBAccess.RunSQL("delete from port_deptempStation where fk_emp='" + userNo + "' AND OrgNo='" + orgNo + "'");
			emp.Delete();
			return "人员信息删除成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 保存部门, 如果有此数据则修改，无此数据则增加.
	 
	 @param orgNo 组织编号
	 @param no 部门编号
	 @param name 名称
	 @param parntNo 父节点编号
	 @param keyVals 比如：@Leaer=zhangsan@Tel=12233333@Idx=1
	 @return return 1 增加成功，其他的增加失败.
	*/
	public static String Port_Dept_Save(String orgNo, String no, String name, String parntNo, String keyVals) throws Exception
	{
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护部门信息";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(orgNo) == true)
			{
				return "err@组织编号不能为空.";
			}

			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
			org.setNo(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织编号错误:" + orgNo;
			}
		}

		try
		{
			AtPara ap = new AtPara(keyVals);
			//增加部门.
			bp.port.Dept dept = new bp.port.Dept();
			dept.setNo(no);
			if (dept.RetrieveFromDBSources() == 0)
			{
				dept.setName(name);
				dept.setParentNo(parntNo);
				dept.setOrgNo(orgNo);

				for (String key : ap.getHisHT().keySet())
				{
					if (DataType.IsNullOrEmpty(key) == true)
					{
						continue;
					}
					dept.SetValByKey(key, ap.GetValStrByKey(key));
				}
				dept.Insert();
			}
			else
			{
				dept.setName(name);
				dept.setParentNo(parntNo);
				dept.setOrgNo(orgNo);

				for (String key : ap.getHisHT().keySet())
				{
					if (DataType.IsNullOrEmpty(key) == true)
					{
						continue;
					}
					dept.SetValByKey(key, ap.GetValStrByKey(key));
				}

				dept.Update();
			}

			DBAccess.RunSQL("UPDATE Port_Dept SET OrgNo='" + orgNo + "' WHERE No='" + dept.getNo() + "'");

			return "部门信息保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/**
	 保存部门, 如果有此数据则修改，无此数据则增加.

	 @param orgNo 组织编号
	 @param no 部门编号
	 @param name 名称
	 @param parntNo 父节点编号
	 @param keyVals 比如：@Leaer=zhangsan@Tel=12233333@Idx=1
	 @return return 1 增加成功，其他的增加失败.
	 */
	public static String Port_Dept_ST_Save(String orgNo, String no, String name, String parntNo, String keyVals,String stations) throws Exception
	{
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护部门信息";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(orgNo) == true)
			{
				return "err@组织编号不能为空.";
			}

			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
			org.setNo(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织编号错误:" + orgNo;
			}
		}

		try
		{
			AtPara ap = new AtPara(keyVals);
			//增加部门.
			bp.port.Dept dept = new bp.port.Dept();
			dept.setNo(no);
			if (dept.RetrieveFromDBSources() == 0)
			{
				dept.setName(name);
				dept.setParentNo(parntNo);
				dept.setOrgNo(orgNo);

				for (String key : ap.getHisHT().keySet())
				{
					if (DataType.IsNullOrEmpty(key) == true)
					{
						continue;
					}
					dept.SetValByKey(key, ap.GetValStrByKey(key));
				}
				dept.Insert();
			}
			else
			{
				dept.setName(name);
				dept.setParentNo(parntNo);
				dept.setOrgNo(orgNo);

				for (String key : ap.getHisHT().keySet())
				{
					if (DataType.IsNullOrEmpty(key) == true)
					{
						continue;
					}
					dept.SetValByKey(key, ap.GetValStrByKey(key));
				}

				dept.Update();
			}
			//如果岗位传值不为空，则插入部门岗位表
			if(stations.length() > 0 ){
				String[] stationsArry = stations.split(",");
				for(String stas : stationsArry){
					bp.port.DeptStation deptStation = new bp.port.DeptStation();
					deptStation.setMyPK(orgNo+"_"+no + "_" + stas);

					if (deptStation.RetrieveFromDBSources() == 0)
					{
						deptStation.setDeptNo(no);
						deptStation.setStationNo(stas);
						deptStation.setOrgNo(orgNo);
						deptStation.Insert();
					}else{
						deptStation.setDeptNo(no);
						deptStation.setStationNo(stas);
						deptStation.setOrgNo(orgNo);
						deptStation.DirectUpdate();
					}
				}
			}

			DBAccess.RunSQL("UPDATE Port_Dept SET OrgNo='" + orgNo + "' WHERE No='" + dept.getNo() + "'");

			return "部门信息保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 删除部门.
	 
	 @param no 删除指定的部门编号
	 @return 
	*/

	public static String Port_Dept_Delete(String no) throws Exception
	{
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能删除部门信息";
		}

		try
		{
			//删除部门.
			bp.port.Dept dept = new bp.port.Dept(no);
			dept.Delete();

			return "删除成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 保存角色, 如果有此数据则修改，无此数据则增加.
	 
	 @param orgNo 组织编号
	 @param no 编号
	 @param name 名称
	 @return return 1 增加成功，其他的增加失败.
	*/
	public static String Port_Station_Save(String orgNo, String no, String name,String stationTypeNo, String keyVals) throws Exception
	{
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护岗位信息";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			//组织结构部分.
			if (DataType.IsNullOrEmpty(orgNo) == true)
			{
				return "err@组织编号不能为空.";
			}

			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
			org.setNo(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织编号错误:" + orgNo;
			}

			//如果是个SAAS版本.
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				no = orgNo + "_" + no;
				stationTypeNo = orgNo + "_" + stationTypeNo;
			}
		}

		try
		{
			AtPara ap = new AtPara(keyVals);

			//增加角色.
			bp.port.Station en = new bp.port.Station();
			en.setNo(no);

			if (en.RetrieveFromDBSources() == 0)
			{
				en.setName(name);
				en.setOrgNo(orgNo);
				en.setFKStationType(stationTypeNo);
				en.Insert();
			//	return "[" + name + "]保存成功";
			}

			for (String item : ap.getHisHT().keySet())
			{
				if (DataType.IsNullOrEmpty(item) == true)
					continue;
				en.SetValByKey(item, ap.GetValStrByKey(item));
			}
			en.setName(name);
			en.setOrgNo(orgNo);
			en.setFKStationType(stationTypeNo);
			en.Update();

		//	DBAccess.RunSQL("UPDATE Port_Station SET OrgNo='" + orgNo + "' WHERE No='" + no + "'");
			return "[" + name + "]保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	/** 
	 删除部门.
	 
	 @param no 删除指定的部门编号
	 @return 
	*/
	public static String Port_Station_Delete(String no,String orgNo) throws Exception
	{
		try
		{
			if (bp.web.WebUser.getIsAdmin() == false)
			{
				return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能删除岗位信息";
			}

			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				no = orgNo + "_" + no; //@gaoxi
			//删除部门.
			bp.port.Station dept = new bp.port.Station(no);
			dept.Delete();

			return "删除成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	public static String Port_Team_Delete(String no) throws Exception
	{
		try
		{
			if (bp.web.WebUser.getIsAdmin() == false)
			{
				return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能删除岗位信息";
			}
			//删除部门.
			bp.port.Team dept = new bp.port.Team(no);
			dept.Delete();

			return "删除成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	public static String Port_Team_Save(String orgNo, String typeno, String no, String name, String keyVals) throws Exception {
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护岗位信息";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(orgNo) == true)
			{
				return "err@组织编号不能为空.";
			}

			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
			org.setNo(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织编号错误:" + orgNo;
			}
		}

		try
		{
			AtPara ap = new AtPara(keyVals);

			//增加部门.
			bp.port.Team en = new bp.port.Team();
			en.setNo(no);
			if (en.RetrieveFromDBSources() == 0)
			{
				en.setName(name);
				en.SetValByKey(TeamAttr.FK_TeamType,typeno);
				en.SetValByKey("OrgNo", orgNo);
				en.Insert();
			}
			for (String item : ap.getHisHT().keySet())
			{
				if (DataType.IsNullOrEmpty(item) == true)
				{
					continue;
				}
				en.SetValByKey(item, ap.GetValStrByKey(item));
			}
			en.setName(name);
			en.SetValByKey(TeamAttr.FK_TeamType,typeno);
			en.SetValByKey("OrgNo", orgNo);
			en.Update();

			DBAccess.RunSQL("UPDATE Port_Team SET OrgNo='" + orgNo + "' WHERE No='" + no + "'");
			return "[" + name + "]保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public static String Port_TeamType_Delete(String no) throws Exception
	{
		try
		{
			if (bp.web.WebUser.getIsAdmin() == false)
			{
				return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能删除岗位信息";
			}
			//删除部门.
			bp.port.TeamType dept = new bp.port.TeamType(no);
			dept.Delete();

			return "删除成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	public static String Port_TeamType_Save(String orgNo, String no, String name, String keyVals) throws Exception {
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护岗位信息";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(orgNo) == true)
			{
				return "err@组织编号不能为空.";
			}

			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织编号错误:" + orgNo;
			}
		}

		try
		{
			AtPara ap = new AtPara(keyVals);

			//增加部门.
			bp.port.TeamType en = new bp.port.TeamType();
			en.setNo(no);
			if (en.RetrieveFromDBSources() == 0)
			{
				en.setName(name);
				en.SetValByKey("OrgNo", orgNo);
				en.Insert();
			}
			for (String item : ap.getHisHT().keySet())
			{
				if (DataType.IsNullOrEmpty(item) == true)
				{
					continue;
				}
				en.SetValByKey(item, ap.GetValStrByKey(item));
			}
			en.setName(name);
			en.SetValByKey("OrgNo", orgNo);
			en.Update();

			DBAccess.RunSQL("UPDATE Port_TeamType SET OrgNo='" + orgNo + "' WHERE No='" + no + "'");
			return "[" + name + "]保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

		///#endregion 关于组织的接口.


		///#region 岗位类型
	public static String Port_StationType_Delete(String no,String orgNo) throws Exception
	{
		try
		{
			if (bp.web.WebUser.getIsAdmin() == false)
			{
				return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能删除岗位信息";
			}

			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				no = orgNo + "_" + no;

			//删除部门.
			bp.port.StationType dept = new bp.port.StationType(no);
			dept.Delete();

			return "删除成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	public static String Port_StationType_Save(String orgNo, String no, String name, String keyVals) throws Exception {
		if (bp.web.WebUser.getIsAdmin() == false)
		{
			return "err@[" + bp.web.WebUser.getName() + "]不是管理员不能维护岗位信息";
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			if (DataType.IsNullOrEmpty(orgNo) == true)
			{
				return "err@组织编号不能为空.";
			}

			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org();
			org.setNo(orgNo);
			if (org.RetrieveFromDBSources() == 0)
			{
				return "err@组织编号错误:" + orgNo;
			}
			//如果是个SAAS版本.
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
				no = orgNo + "_" + no;
		}

		try
		{
			AtPara ap = new AtPara(keyVals);

			//增加部门.
			bp.port.StationType en = new bp.port.StationType();
			en.setNo(no);
			if (en.RetrieveFromDBSources() == 0)
			{
				en.setName(name);
				en.SetValByKey("OrgNo", orgNo);
				en.Insert();
			}
			for (String item : ap.getHisHT().keySet())
			{
				if (DataType.IsNullOrEmpty(item) == true)
				{
					continue;
				}
				en.SetValByKey(item, ap.GetValStrByKey(item));
			}
			en.setName(name);
			en.SetValByKey("OrgNo", orgNo);
			en.Update();

			DBAccess.RunSQL("UPDATE Port_StationType SET OrgNo='" + orgNo + "' WHERE No='" + no + "'");
			return "[" + name + "]保存成功";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}

		///#endregion
}
