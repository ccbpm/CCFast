package bp.ccfast.ccmenu;

import bp.da.*;
import bp.difference.StringHelper;
import bp.web.*;
import bp.wf.httphandler.*;
import bp.*;
import bp.ccfast.*;

/**
 页面功能实体
*/
public class PortPage extends bp.difference.handler.DirectoryPageBase
{

		///#region 构造函数
	/**
	 构造函数
	*/
	public PortPage()
	{
	}

		///#endregion 构造函数


		///#region 组织结构维护.
	/**
	 获取该部门的所有人员

	 @return
	*/
	public final String LoadDatagridDeptEmp_Init()
	{
		String deptNo = this.GetRequestVal("deptNo");
		if (DataType.IsNullOrEmpty(deptNo))
		{
			return "{ total: 0, rows: [] }";
		}
		String orderBy = this.GetRequestVal("orderBy");


		String searchText = this.GetRequestVal("searchText");
		if (!DataType.IsNullOrEmpty(searchText))
		{
			searchText.trim();
		}
		String addQue = "";
		if (!DataType.IsNullOrEmpty(searchText))
		{
			addQue = "  AND (pe.No like '%" + searchText + "%' or pe.Name like '%" + searchText + "%') ";
		}

		String pageNumber = this.GetRequestVal("pageNumber");
		int iPageNumber = DataType.IsNullOrEmpty(pageNumber) ? 1 : Integer.parseInt(pageNumber);
		//每页多少行
		String pageSize = this.GetRequestVal("pageSize");
		int iPageSize = DataType.IsNullOrEmpty(pageSize) ? 9999 : Integer.parseInt(pageSize);

		String sql = "(select pe.*,pd.name FK_DutyText from Port_Emp pe left join port_duty pd on pd.no = pe.fk_duty where pe.no in (select fk_emp from Port_DeptEmp where fk_dept='" + deptNo + "') " + addQue + " ) dbSo ";


		return DBPaging(sql, iPageNumber, iPageSize, "No", orderBy);

	}

	/**
	 以下算法只包含 oracle mysql sqlserver 三种类型的数据库 qin

	 @param dataSource 表名
	 @param pageNumber 当前页
	 @param pageSize 当前页数据条数
	 @param key 计算总行数需要
	 @param orderKey 排序字段
	 @return
	*/
	public final String DBPaging(String dataSource, int pageNumber, int pageSize, String key, String orderKey)
	{
		String sql = "";
		String orderByStr = "";

		if (!DataType.IsNullOrEmpty(orderKey))
		{
			orderByStr = " ORDER BY " + orderKey;
		}

		switch (DBAccess.getAppCenterDBType())
		{
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				int beginIndex = (pageNumber - 1) * pageSize + 1;
				int endIndex = pageNumber * pageSize;

				sql = "SELECT * FROM ( SELECT A.*, ROWNUM RN " + "FROM (SELECT * FROM  " + dataSource + orderByStr + ") A WHERE ROWNUM <= " + endIndex + " ) WHERE RN >=" + beginIndex;
				break;
			case MSSQL:
				sql = "SELECT TOP " + pageSize + " * FROM " + dataSource + " WHERE " + key + " NOT IN  (" + "SELECT TOP (" + pageSize + "*(" + pageNumber + "-1)) " + key + " FROM " + dataSource + " )" + orderByStr;
				break;
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = "select * from  " + dataSource + orderByStr + " limit " + (pageNumber - 1) * pageSize + "," + pageSize;
				break;
			default:
				throw new RuntimeException("暂不支持您的数据库类型.");
		}

		DataTable DTable = DBAccess.RunSQLReturnTable(sql);

		int totalCount = DBAccess.RunSQLReturnCOUNT("select " + key + " from " + dataSource);

		return DataTableConvertJson.DataTable2Json(DTable, totalCount);
	}

		///#endregion


		///#region 获取菜单权限.
	/**
	 获得菜单数据.

	 @return
	*/
	public final String GPM_DB_Menus() throws Exception {
		String appNo = this.GetRequestVal("AppNo");

		String sql1 = "SELECT No,Name,FK_Menu,ParentNo,UrlExt,Icon,Idx ";
		sql1 += " FROM V_GPM_EmpMenu ";
		sql1 += " WHERE FK_Emp = '" + WebUser.getNo() + "' ";
		sql1 += " AND MenuType = '3' ";
		sql1 += " AND FK_App = '" + appNo + "' ";
		sql1 += " UNION "; //加入不需要权限控制的菜单.
		sql1 += "SELECT No,Name, No as FK_Menu,ParentNo,UrlExt,Icon,Idx";
		sql1 += " FROM GPM_Menu ";
		sql1 += " WHERE MenuCtrlWay=1 ";
		sql1 += " AND MenuType = '3' ";
		sql1 += " AND FK_App = '" + appNo + "' ORDER BY Idx ";
		DataTable dirs = DBAccess.RunSQLReturnTable(sql1);
		dirs.setTableName("Dirs"); //获得目录.

		String sql2 = "SELECT No,Name,FK_Menu,ParentNo,UrlExt,Icon,Idx ";
		sql2 += " FROM V_GPM_EmpMenu ";
		sql2 += " WHERE FK_Emp = '" + WebUser.getNo() + "'";
		sql2 += " AND MenuType = '4' ";
		sql2 += " AND FK_App = '" + appNo + "' ";
		sql2 += " UNION "; //加入不需要权限控制的菜单.
		sql2 += "SELECT No,Name, No as FK_Menu,ParentNo,UrlExt,Icon,Idx ";
		sql2 += " FROM GPM_Menu "; //加入不需要权限控制的菜单.
		sql2 += " WHERE MenuCtrlWay=1 ";
		sql2 += " AND MenuType = '4' ";
		sql2 += " AND FK_App = '" + appNo + "' ORDER BY Idx ";

		DataTable menus = DBAccess.RunSQLReturnTable(sql2);
		menus.setTableName("Menus"); //获得菜单.

		//组装数据.
		DataSet ds = new DataSet();
		ds.Tables.add(dirs);
		ds.Tables.add(menus);

		return bp.tools.Json.ToJson(ds);
	}
	/**
	 获得OA菜单数据.

	 @return
	*/
	public final String GPM_OA_Menus() throws Exception {
		String appNo = this.GetRequestVal("AppNo");

		Paras ps = new Paras();
		String dbstr = bp.difference.SystemConfig.getAppCenterDBVarStr();
		ps.SQL = "SELECT No FROM GPM_Menu WHERE MenuType=" + dbstr + "MenuType AND FK_App=" + dbstr + "FK_App";
		ps.Add("MenuType", 2);
		ps.Add("FK_App", appNo, false);

		String ParentNo = DBAccess.RunSQLReturnString(ps);

		if (DataType.IsNullOrEmpty(ParentNo))
		{
			return "[]";
		}

		String sql1 = "SELECT No,Name,FK_Menu,MenuType,ParentNo,Url,UrlExt,Tag1,Tag2,Tag3,WebPath,Icon,Idx ";
		sql1 += " FROM v_gpm_empmenu ";
		sql1 += " WHERE FK_Emp = '" + WebUser.getNo() + "' ";
		sql1 += " AND ParentNo = '" + ParentNo + "' ";
		sql1 += " AND FK_App = '" + appNo + "' ";
		sql1 += " UNION "; //加入不需要权限控制的菜单.
		sql1 += "SELECT No,Name, No as FK_Menu,MenuType,ParentNo,Url,UrlExt,Tag1,Tag2,Tag3,WebPath,Icon,Idx";
		sql1 += " FROM GPM_Menu ";
		sql1 += " WHERE MenuCtrlWay=1 ";
		sql1 += " AND ParentNo = '" + ParentNo + "' ";
		sql1 += " AND FK_App = '" + appNo + "' ORDER BY Idx ";
		DataTable dirs = DBAccess.RunSQLReturnTable(sql1);
		dirs.setTableName("Dirs"); //获得目录.

		String sql2 = "SELECT No,Name,FK_Menu,MenuType,ParentNo,Url,UrlExt,Tag1,Tag2,Tag3,WebPath,Icon,Idx,openway ";
		sql2 += " FROM v_gpm_empmenu ";
		sql2 += " WHERE FK_Emp = '" + WebUser.getNo() + "'";
		sql2 += " AND ParentNo != '" + ParentNo + "'  ";
		sql2 += " AND FK_App = '" + appNo + "' ";
		sql2 += " UNION "; //加入不需要权限控制的菜单.
		sql2 += "SELECT No,Name, No as FK_Menu,MenuType,ParentNo,Url,UrlExt,Tag1,Tag2,Tag3,WebPath,Icon,Idx,openway ";
		sql2 += " FROM GPM_Menu "; //加入不需要权限控制的菜单.
		sql2 += " WHERE MenuCtrlWay=1 ";
		sql2 += " AND ParentNo != '" + ParentNo + "' ";
		sql2 += " AND FK_App = '" + appNo + "' ORDER BY Idx ";

		DataTable menus = DBAccess.RunSQLReturnTable(sql2);
		menus.setTableName("Menus"); //获得菜单.

		//组装数据.
		DataSet ds = new DataSet();
		ds.Tables.add(dirs);
		ds.Tables.add(menus);

		return bp.tools.Json.ToJson(ds);
	}
	/**
	 是否可以执行当前工作

	 @return
	*/
	public final String GPM_IsCanExecuteFunction()
	{
		DataTable dt = GPM_GenerFlagDB(); //获得所有的标记.
		String funcNo = this.GetRequestVal("FuncFlag");
		for (DataRow dr : dt.Rows)
		{
			if (dr.getValue(0).toString().equals(funcNo) == true)
			{
				return "1";
			}
		}
		return "0";
	}
	/**
	 获得所有的权限标记.

	 @return
	*/
	public final DataTable GPM_GenerFlagDB()
	{
		String appNo = this.GetRequestVal("AppNo");
		String sql2 = "SELECT Flag,Idx";
		sql2 += " FROM V_GPM_EmpMenu ";
		sql2 += " WHERE FK_Emp = '" + WebUser.getNo() + "'";
		sql2 += " AND MenuType = '5' ";
		sql2 += " AND FK_App = '" + appNo + "' ";
		sql2 += " UNION "; //加入不需要权限控制的菜单.
		sql2 += "SELECT Flag,Idx ";
		sql2 += " FROM GPM_Menu "; //加入不需要权限控制的菜单.
		sql2 += " WHERE MenuCtrlWay=1 ";
		sql2 += " AND MenuType = '5' ";
		sql2 += " AND FK_App = '" + appNo + "' ORDER BY Idx ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql2);
		return dt;
	}
	/**
	 获得所有权限的标记

	 @return
	*/
	public final String GPM_AutoHidShowPageElement()
	{
		DataTable dt = GPM_GenerFlagDB(); //获得所有的标记.
		return bp.tools.Json.ToJson(dt);
	}
	/**
	 组织结构查询

	 @return
	*/
	public final String GPM_Search()
	{
		String searchKey = this.GetRequestVal("searchKey");
		//var SearchDept = this.GetRequestVal("SearchDept");
		//var SearchEmp = this.GetRequestVal("SearchEmp");
		//var SearchTel = this.GetRequestVal("SearchTel");
		String sql = "SELECT e.No AS No,e.Name AS Name,d.Name AS deptName,e.Email AS Email,e.Tel AS Tel from Port_Dept d,Port_Emp e " + "where d.No=e.FK_Dept AND (e.No LIKE '%" + searchKey + "%' or e.NAME LIKE '%" + searchKey + "%' or d.Name LIKE '%" + searchKey + "%' or e.Tel LIKE '%" + searchKey + "%')";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

		///#endregion

}
