package bp.wf.httphandler;

import bp.cloud.Org;
import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.port.Dept;
import bp.port.DeptAttr;
import bp.port.Depts;
import bp.tools.FileAccess;
import bp.web.*;
import bp.sys.*;
import bp.en.*;
import bp.difference.*;
import bp.port.*;
import bp.wf.port.admin2group.OrgAdminer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** 
 页面功能实体
*/
public class GPMPage extends bp.difference.handler.DirectoryPageBase
{

		///#region 构造函数
	/** 
	 构造函数
	*/
	public GPMPage()
	{
	}

		///#endregion 构造函数


		///#region 签名.
	/** 
	 图片签名初始化
	 
	 @return 
	*/
	public final String Siganture_Init() throws Exception {
		if (WebUser.getNoOfRel() == null)
		{
			return "err@登录信息丢失";
		}
		Hashtable ht = new Hashtable();
		ht.put("No", WebUser.getNo());
		ht.put("Name", WebUser.getName());
		ht.put("FK_Dept", WebUser.getDeptNo());
		ht.put("FK_DeptName", WebUser.getDeptName());
		return bp.tools.Json.ToJson(ht);
	}

	/** 
	 签名保存
	 
	 @return 
	*/
	public final String Siganture_Save() throws Exception {
		try {
			HttpServletRequest request = getRequest();
			String contentType = request.getContentType();
			if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
				String tempFilePath = SystemConfig.getPathOfWebApp() + "DataUser/Siganture/" + this.getFK_Emp()
						+ ".jpg";
				File tempFile = new File(tempFilePath);
				if (tempFile.exists()) {
					tempFile.delete();
				}
				MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);

				MultipartFile item = mrequest.getFile("file");

				// 获取文件名
				String fileName = item.getOriginalFilename();
				String fileExt = ",bpm,jpg,jpeg,png,gif,";
				// 扩展名
				String exts = FileAccess.getExtensionName(fileName).toLowerCase().replace(".", "");
				if (fileExt.indexOf(exts + ",") == -1) {
					return "err@上传的文件必须是以图片格式:" + fileExt + "类型, 现在类型是:" + exts;
				}

				MultipartFile multipartFile = mrequest.getFile("File_Upload");
				try {
					multipartFile.transferTo(tempFile);
				} catch (Exception e) {

				}

			}

		} catch (RuntimeException ex) {
			return "err@" + ex.getMessage();
		}

		return "上传成功！";
	}


	/** 
	 初始化组织结构部门表维护.
	 
	 @return 
	*/
	public final String Organization_Init() throws Exception {
		Depts depts = new Depts();
		String parentNo = this.GetRequestVal("ParentNo");
		if (DataType.IsNullOrEmpty(parentNo) == true)
		{
			parentNo = "0";
		}

		if (DataType.IsNullOrEmpty(parentNo) == true)
		{
			if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
			{
				parentNo = WebUser.getOrgNo();
			}
			else
			{
				parentNo = "0";
			}
		}

		QueryObject qo = new QueryObject(depts);
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			if (parentNo.equals("0") == true)
			{
				qo.AddWhere(DeptAttr.ParentNo, parentNo);
				qo.addOr();
				qo.AddWhereInSQL(DeptAttr.ParentNo, "SELECT No From Port_Dept Where ParentNo='0'");
			}
			else
			{
				qo.AddWhere(DeptAttr.ParentNo, parentNo);
			}
		}
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc || SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			qo.AddWhere(DeptAttr.No, WebUser.getOrgNo());
		}
		qo.addOrderBy(DeptAttr.Idx);
		qo.DoQuery();

		return depts.ToJson("dt");

	}

	public final String Organization_GetDeptsByParentNo() throws Exception {

		Depts depts = new Depts();
		QueryObject qo = new QueryObject(depts);
		String parentNo = GetRequestVal("ParentNo");
		qo.AddWhere(DeptAttr.ParentNo, parentNo);
		qo.addOrderBy(DeptAttr.Idx);
		qo.DoQuery();
		return depts.ToJson("dt");
	}

	/** 
	 获取本部门及人员信息
	 
	 @return 
	*/
	public final String DeptEmp_Init() throws Exception {

		Depts depts = new Depts();
		Emps emps = new Emps();
		String parentNo = this.GetRequestVal("ParentNo");
		QueryObject qo = new QueryObject(depts);
		if (DataType.IsNullOrEmpty(parentNo) == false)
		{
			if (parentNo.equals("0") == true)
			{
				emps.RetrieveIn(EmpAttr.FK_Dept, "SELECT No From Port_Dept Where ParentNo='0'", null);
				qo.AddWhere(DeptAttr.ParentNo, parentNo);
				qo.addOr();
				qo.AddWhereInSQL(DeptAttr.ParentNo, "SELECT No From Port_Dept Where ParentNo='0'");

			}
			else
			{
				emps.Retrieve(EmpAttr.FK_Dept, parentNo, null);
				qo.AddWhere(DeptAttr.ParentNo, parentNo);
				qo.addOr();
				qo.AddWhere(DeptAttr.No, parentNo);
			}


		}
		qo.addOrderBy(DeptAttr.Idx);
		qo.DoQuery();
		DataSet ds = new DataSet();
		ds.Tables.add(depts.ToDataTableField("Depts"));
		ds.Tables.add(emps.ToDataTableField("Emps"));
		return bp.tools.Json.ToJson(ds);

	}

	///// <summary>
	///// 获取该部门的所有人员
	///// </summary>
	///// <returns></returns>        
	//public String LoadDatagridDeptEmp_Init()
	//{
	//    String deptNo = this.GetRequestVal("deptNo");
	//    if (string.IsNullOrEmpty(deptNo))
	//    {
	//        return "{ total: 0, rows: [] }";
	//    }
	//    String orderBy = this.GetRequestVal("orderBy");


	//    String searchText = this.GetRequestVal("searchText");
	//    if (!DataType.IsNullOrEmpty(searchText))
	//    {
	//        searchText.Trim();
	//    }
	//    String addQue = "";
	//    if (!string.IsNullOrEmpty(searchText))
	//    {
	//        addQue = "  AND (pe.No like '%" + searchText + "%' or pe.Name like '%" + searchText + "%') ";
	//    }

	//    String pageNumber = this.GetRequestVal("pageNumber");
	//    int iPageNumber = string.IsNullOrEmpty(pageNumber) ? 1 : Convert.ToInt32(pageNumber);
	//    //每页多少行
	//    String pageSize = this.GetRequestVal("pageSize");
	//    int iPageSize = string.IsNullOrEmpty(pageSize) ? 9999 : Convert.ToInt32(pageSize);

	//    String sql = "(select pe.*,pd.name FK_DutyText from Port_Emp pe left join port_duty pd on pd.no = pe.fk_duty where pe.no in (select fk_emp from Port_DeptEmp where fk_dept='" + deptNo + "') "
	//        + addQue + " ) dbSo ";


	//    return DBPaging(sql, iPageNumber, iPageSize, "No", orderBy);

	//}

	///// <summary>
	///// 以下算法只包含 oracle mysql sqlserver 三种类型的数据库 qin
	///// </summary>
	///// <param name="dataSource">表名</param>
	///// <param name="pageNumber">当前页</param>
	///// <param name="pageSize">当前页数据条数</param>
	///// <param name="key">计算总行数需要</param>
	///// <param name="orderKey">排序字段</param>
	///// <returns></returns>
	//public String DBPaging(String dataSource, int pageNumber, int pageSize, String key, String orderKey)
	//{
	//    String sql = "";
	//    String orderByStr = "";

	//    if (!string.IsNullOrEmpty(orderKey))
	//        orderByStr = " ORDER BY " + orderKey;

	//    switch (DBAccess.getAppCenterDBType())
	//    {
	//        case DBType.Oracle:
	//        case DBType.KingBaseR3:
	//        case DBType.KingBaseR6:
	//            int beginIndex = (pageNumber - 1) * pageSize + 1;
	//            int endIndex = pageNumber * pageSize;

	//            sql = "SELECT * FROM ( SELECT A.*, ROWNUM RN " +
	//                "FROM (SELECT * FROM  " + dataSource + orderByStr + ") A WHERE ROWNUM <= " + endIndex + " ) WHERE RN >=" + beginIndex;
	//            break;
	//        case DBType.MSSQL:
	//            sql = "SELECT TOP " + pageSize + " * FROM " + dataSource + " WHERE " + key + " NOT IN  ("
	//            + "SELECT TOP (" + pageSize + "*(" + pageNumber + "-1)) " + key + " FROM " + dataSource + " )" + orderByStr;
	//            break;
	//        case DBType.MySQL:
	//            pageNumber -= 1;
	//            sql = "select * from  " + dataSource + orderByStr + " limit " + pageNumber + "," + pageSize;
	//            break;
	//        default:
	//            throw new Exception("暂不支持您的数据库类型.");
	//    }

	//    DataTable DTable = DBAccess.RunSQLReturnTable(sql);

	//    int totalCount = DBAccess.RunSQLReturnCOUNT("select " + key + " from " + dataSource);

	//    return DataTableConvertJson.DataTable2Json(DTable, totalCount);
	//}

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
		dirs.TableName = "Dirs"; //获得目录.

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
		menus.TableName = "Menus"; //获得菜单.
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			menus.Columns.get(0).ColumnName = "No";
			menus.Columns.get(1).ColumnName = "Name";
			menus.Columns.get(2).ColumnName = "FK_Menu";
			menus.Columns.get(3).ColumnName = "ParentNo";
			menus.Columns.get(4).ColumnName = "UrlExt";
			menus.Columns.get(5).ColumnName = "Icon";
			menus.Columns.get(6).ColumnName = "Idx";
		}
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
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		ps.SQL = "SELECT No FROM GPM_Menu WHERE MenuType=" + dbstr + "MenuType AND FK_App=" + dbstr + "FK_App";
		ps.Add("MenuType", 2);
		ps.Add("FK_App", appNo, false);

		String ParentNo = DBAccess.RunSQLReturnString(ps);

		if ((ParentNo == null || ParentNo.isEmpty()))
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
		dirs.TableName = "Dirs"; //获得目录.

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
		menus.TableName= "Menus"; //获得菜单.

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
		String cloum = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			cloum = "e.userid AS UserID,";
		}
		String sql = "SELECT e.no AS \"No\"," + cloum + "e.name AS \"Name\",d.No AS FK_Dept,d.Name AS deptName,e.Email AS Email,e.Tel AS Tel from Port_Dept d,Port_Emp e " + "where d.No=e.FK_Dept AND (e.No LIKE '%" + searchKey + "%' or e.NAME LIKE '%" + searchKey + "%' or d.Name LIKE '%" + searchKey + "%' or e.Tel LIKE '%" + searchKey + "%')";
		if (DataType.IsNullOrEmpty(WebUser.getOrgNo()) == false)
		{
			sql += " AND e.OrgNo='" + WebUser.getOrgNo() + "'";
		}
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

		///#endregion
    //单组织版组织结构导入
	public final String Template_Save() throws Exception {
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			throw new RuntimeException("err@仅仅导入单组织版.");
		}

			///#region 获得数据源.
		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		if (contentType == null || contentType.indexOf("multipart/form-data") == -1) {
			throw new RuntimeException("ContentType不符合要求[ContentType=" + contentType + "]");
		}

		MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
		MultipartFile requestFile = mrequest.getFile("file");
		if (requestFile == null) {
			throw new RuntimeException("没有获取到文件");
		}
		String ext = ".xls";
		String fileNmae = requestFile.getOriginalFilename();
		if(fileNmae.endsWith(".xlsx"))
			ext = ".xlsx";
		//设置文件名
		String fileNewName = DataType.getCurrentDateByFormart("yyyyMMddHHmmss") + ext;
		String filePath =  SystemConfig.getPathOfTemp() +  fileNewName;
		File tempFile = new File(filePath);
		CommonFileUtils.upload(request, "file", tempFile);

		//region 获得数据源.
		List sheetNameList = Arrays.asList(DBLoad.GenerTableNames(filePath));
		if (sheetNameList.size() < 3
				|| sheetNameList.contains("部门") == false
				|| sheetNameList.contains("岗位") == false
				|| sheetNameList.contains("人员") == false)
		{
			throw new RuntimeException("excel不符合要求，需要包含Sheet(部门、岗位、人员)");
		}

		//获得部门数据.
		DataTable dtDept = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("部门"));
		for (int i = 0; i < dtDept.Columns.size(); i++)
		{
			String name = dtDept.Columns.get(i).ColumnName;
			name = name.replace(" ", "");
			name = name.replace("*", "");
			dtDept.Columns.get(i).ColumnName = name;
		}

		//获得角色数据.
		DataTable dtStation = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("岗位"));
		for (int i = 0; i < dtStation.Columns.size(); i++)
		{
			String name = dtStation.Columns.get(i).ColumnName;
			name = name.replace(" ", "");
			name = name.replace("*", "");
			dtStation.Columns.get(i).ColumnName = name;
		}

		//获得人员数据.
		DataTable dtEmp = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("人员"));
		for (int i = 0; i < dtEmp.Columns.size(); i++)
		{
			String name = dtEmp.Columns.get(i).ColumnName;
			name = name.replace(" ", "");
			name = name.replace("*", "");
			dtEmp.Columns.get(i).ColumnName = name;
		}



			///#endregion 获得数据源.


			///#region 检查是否有根目录为 0 的数据?
		//检查数据的完整性.
		//1.检查是否有根目录为0的数据?
		int num = 0;
		boolean isHave = false;
		for (DataRow dr : dtDept.Rows)
		{
			String str1 = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
			if (DataType.IsNullOrEmpty(str1) == true)
			{
				continue;
			}

			num++;
			String str = dr.getValue(1) instanceof String ? (String)dr.getValue(1) : null;
			if (str == null || str.equals(""))
			{
				return "err@导入出现数据错误:" + str1 + "的.上级部门名称-不能用空行的数据， 第[" + num + "]行数据.";
			}

			if (str.equals("0") == true || str.equals("root") == true)
			{
				isHave = true;
				break;
			}
		}
		if (isHave == false)
		{
			return "err@导入数据没有找到部门根目录节点.";
		}

			///#endregion 检查是否有根目录为0的数据


			///#region 检查部门名称是否重复?
		String deptStrs = "";
		for (DataRow dr : dtDept.Rows)
		{
			String deptName = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
			if (DataType.IsNullOrEmpty(deptName) == true)
			{
				continue;
			}

			if (deptStrs.contains("," + deptName + ",") == true)
			{
				return "err@部门名称:" + deptName + "重复.";
			}

			//加起来..
			deptStrs += "," + deptName + ",";
		}

			///#endregion 检查部门名称是否重复?


			///#region 检查人员帐号是否重复?
		String emps = "";
		for (DataRow dr : dtEmp.Rows)
		{
			String empNo = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			if (emps.contains("," + empNo + ",") == true)
			{
				return "err@人员帐号:" + empNo + "重复.";
			}

			//加起来..
			emps += "," + empNo + ",";
		}

			///#endregion 检查人员帐号是否重复?


			///#region 检查角色名称是否重复?
		String staStrs = "";
		for (DataRow dr : dtStation.Rows)
		{
			String staName = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
			if (DataType.IsNullOrEmpty(staName) == true)
			{
				continue;
			}

			if (staStrs.contains("," + staName + ",") == true)
			{
				return "err@角色名称:" + staName + "重复.";
			}

			//加起来..
			staStrs += "," + staName + ",";
		}

			///#endregion 检查角色名称是否重复?


			///#region 检查人员的部门名称是否存在于部门数据里?
		int idx = 0;
		for (DataRow dr : dtEmp.Rows)
		{
			String emp = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
			if (DataType.IsNullOrEmpty(emp) == true)
			{
				continue;
			}

			idx++;
			//去的部门编号.
			String strs = dr.getValue("部门名称").toString();
			if (DataType.IsNullOrEmpty(strs) == true)
			{
				return "err@第[" + idx + "]行,人员[" + emp + "]部门不能为空:" + strs + ".";
			}

			String[] mystrs = strs.split("[,]", -1);
			for (String str : mystrs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				if (str.equals("0") || str.equals("root") == true)
				{
					continue;
				}

				//先看看数据是否有?
				Dept dept = new Dept();
				if (dept.Retrieve("Name", str) == 1)
				{
					continue;
				}

				//从xls里面判断.
				isHave = false;
				for (DataRow drDept : dtDept.Rows)
				{
					if (str.equals(drDept.getValue(0).toString()) == true)
					{
						isHave = true;
						break;
					}
				}
				if (isHave == false)
				{
					return "err@第[" + idx + "]行,人员[" + emp + "]部门名[" + str + "]，不存在模版里。";
				}
			}
		}

			///#endregion 检查人员的部门名称是否存在于部门数据里


			///#region 检查人员的角色名称是否存在于角色数据里?
		idx = 0;
		for (DataRow dr : dtEmp.Rows)
		{
			String emp = dr.getValue(0) instanceof String ? (String)dr.getValue(0) : null;
			if (DataType.IsNullOrEmpty(emp) == true)
			{
				continue;
			}

			idx++;

			//角色名称..
			String strs = dr.getValue("角色名称") instanceof String ? (String)dr.getValue("角色名称") : null;
			if (DataType.IsNullOrEmpty(strs) == true)
			{
				continue;
			}

			//判断角色.
			String[] mystrs = strs.split("[,]", -1);
			for (String str : mystrs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				//先看看数据是否有?
				Station stationEn = new Station();
				if (stationEn.Retrieve("Name", str) == 1)
				{
					continue;
				}

				//从 xls 判断.
				isHave = false;
				for (DataRow drSta : dtStation.Rows)
				{
					if (str.equals(drSta.getValue(0).toString()) == true)
					{
						isHave = true;
						break;
					}
				}
				if (isHave == false)
				{
					return "err@第[" + idx + "]行,人员[" + emp + "]角色名称[" + str + "]，不存在模版里。";
				}
			}
		}

			///#endregion 检查人员的部门名称是否存在于部门数据里


			///#region 检查部门负责人是否存在于人员列表里?
		String empStrs = ",";
		for (DataRow item : dtEmp.Rows)
		{
			empStrs += item.getValue(0).toString() + ",";
		}
		idx = 0;
		for (DataRow dr : dtDept.Rows)
		{
			String empNo = dr.getValue(2) instanceof String ? (String)dr.getValue(2) : null;
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			idx++;
			if (empStrs.contains("," + empNo + ",") == false)
			{
				return "err@部门负责人[" + empNo + "]不存在与人员表里，第[" + idx + "]行.";
			}
		}

			///#endregion 检查部门负责人是否存在于人员列表里


			///#region 检查直属领导帐号是否存在于人员列表里?
		idx = 0;
		for (DataRow dr : dtEmp.Rows)
		{
			String empNo = dr.getValue(6) instanceof String ? (String)dr.getValue(6) : null;
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			idx++;
			if (empStrs.contains("," + empNo + ",") == false)
			{
				return "err@部门负责人[" + empNo + "]不存在与人员表里，第[" + idx + "]行.";
			}
		}

			///#endregion 检查部门负责人是否存在于人员列表里


			///#region 插入数据到 Port_StationType.
		idx = -1;
		for (DataRow dr : dtStation.Rows)
		{
			idx++;
			String str = dr.getValue(1).toString();

			//判断是否是空.
			if (DataType.IsNullOrEmpty(str) == true)
			{
				continue;
			}

			if (str.equals("角色类型") == true)
			{
				continue;
			}

			str = str.trim();

			//看看数据库是否存在.
			StationType st = new StationType();
			if (st.IsExit("Name", str) == false)
			{
				st.setName(str);
				st.setOrgNo(WebUser.getOrgNo());
				st.setNo(DBAccess.GenerGUID(0, null, null));
				st.Insert();
			}
		}

			///#endregion 插入数据到 Port_StationType.


			///#region 插入数据到 Port_Station.
		idx = -1;
		for (DataRow dr : dtStation.Rows)
		{
			idx++;
			String str = dr.getValue(0).toString();

			//判断是否是空.
			if (DataType.IsNullOrEmpty(str) == true)
			{
				continue;
			}

			if (str.equals("角色名称") == true)
			{
				continue;
			}


			//获得类型的外键的编号.
			String stationTypeName = dr.getValue(1).toString().trim();
			StationType st = new StationType();
			if (st.Retrieve("Name", stationTypeName) == 0)
			{
				return "err@系统出现错误,没有找到角色类型[" + stationTypeName + "]的数据.";
			}

			//看看数据库是否存在.
			Station sta = new Station();
			sta.setName(str);
			sta.setIdx(idx);

			//不存在就插入.
			if (sta.IsExit("Name", str) == false)
			{
				sta.setOrgNo(WebUser.getOrgNo());
				sta.setFKStationType(st.getNo());
				sta.setNo(DBAccess.GenerGUID(0, null, null));
				sta.Insert();
			}
			else
			{
				//存在就更新.
				sta.setFKStationType(st.getNo());
				sta.Update();
			}
		}

			///#endregion 插入数据到 Port_Station.


			///#region 插入数据到 Port_Dept.
		idx = -1;
		for (DataRow dr : dtDept.Rows)
		{
			//获得部门名称.
			String deptName = dr.getValue(0).toString();
			if (deptName.equals("部门名称") == true)
			{
				continue;
			}

			String parentDeptName = dr.getValue(1) instanceof String ? (String)dr.getValue(1) : null;
			String leader = dr.getValue(2) instanceof String ? (String)dr.getValue(2) : null;

			//说明是根目录.
			if (parentDeptName.equals("0") == true || parentDeptName.equals("root") == true)
			{
				Dept root = new Dept();
				root.setNo(WebUser.getDeptNo());
				if (root.RetrieveFromDBSources() == 0)
				{
					return "err@没有找到根目录节点，请联系管理员。";
				}

				root.setName(deptName);
				root.Update();
				continue;
			}


			//先求出来父节点.
			Dept parentDept = new Dept();
			int i = parentDept.Retrieve("Name", parentDeptName);
			if (i == 0)
			{
				return "err@没有找到当前部门[" + deptName + "]的上一级部门[" + parentDeptName + "]";
			}

			Dept myDept = new Dept();

			//如果数据库存在.
			i = parentDept.Retrieve("Name", deptName);
			if (i >= 1)
			{
				continue;
			}

			//插入部门.
			myDept.setName(deptName);
			//   myDept.setOrgNo(bp.web.WebUser.getOrgNo();
			myDept.setNo(DBAccess.GenerGUID(0, null, null));
			myDept.setParentNo(parentDept.getNo());
			myDept.setLeader(leader); //领导.
			myDept.setIdx(idx);
			myDept.Insert();
		}

			///#endregion 插入数据到 Port_Dept.


			///#region 插入到 Port_Emp.
		idx = 0;
		for (DataRow dr : dtEmp.Rows)
		{
			String empNo = dr.getValue("人员帐号").toString();
			String empName = dr.getValue("人员姓名").toString();
			String deptNames = dr.getValue("部门名称").toString();
			String deptPaths = dr.getValue("部门路径").toString();

			String stationNames = dr.getValue("角色名称").toString();
			String tel = dr.getValue("电话").toString();
			String email = dr.getValue("邮箱").toString();
			String leader = dr.getValue("直属领导").toString(); //部门领导.

			Emp emp = new Emp();
			int i = emp.Retrieve("No", empNo);
			if (i >= 1)
			{
				emp.setTel(tel);
				emp.setName(empName);
				emp.Update();
				continue;
			}

			//找到人员的部门.
			String[] myDeptStrs = deptNames.split("[,]", -1);
			Dept dept = new Dept();
			for (String deptName : myDeptStrs)
			{
				if (DataType.IsNullOrEmpty(deptName) == true)
				{
					continue;
				}

				i = dept.Retrieve("Name", deptName);
				if (i <= 0)
				{
					return "err@部门名称不存在." + deptName;
				}

				DeptEmp de = new DeptEmp();
				de.setDeptNo(dept.getNo());
				de.setEmpNo(empNo);
				de.setOrgNo(WebUser.getOrgNo());
				de.setMyPK(de.getDeptNo() + "_" + de.getEmpNo());
				de.Delete();
				de.Insert();
			}

			//插入角色.
			String[] staNames = stationNames.split("[,]", -1);
			Station sta = new Station();
			for (String staName : staNames)
			{
				if (DataType.IsNullOrEmpty(staName) == true)
				{
					continue;
				}

				i = sta.Retrieve("Name", staName);
				if (i == 0)
				{
					return "err@角色名称不存在." + staName;
				}

				DeptEmpStation des = new DeptEmpStation();
				des.setDeptNo(dept.getNo());
				des.setEmpNo(empNo);
				des.setStationNo(sta.getNo());
				//   des.setOrgNo(WebUser.getOrgNo();
				des.setMyPK(des.getDeptNo() + "_" + des.getEmpNo() + "_" + des.getStationNo());
				des.Delete();
				des.Insert();
			}

			//插入到数据库.
			emp.setNo(empNo);
			//   emp.setUserID(empNo;
			emp.setName(empName);
			emp.setDeptNo(dept.getNo());
			// emp.getOrgNo() = WebUser.getOrgNo();
			emp.setTel(tel);
			//emp.Email = email;
			//emp.Leader = leader;
			//emp.setIdx(idx;

			emp.Insert();
		}

			///#endregion 插入到 Port_Emp.


		//删除临时文件
		//  System.IO.File.Delete(filePath);

		return "执行完成.";
	}
	public final String Template_SaveBySaaS() throws Exception {

		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS) {
			return "err@必须是SaaS模式才能使用此接口导入组织结构.";
		}

		if(WebUser.getIsAdmin() == false){
			return "err@必须是管理员账号才可以导入组织.";
		}

		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		if (contentType == null || contentType.indexOf("multipart/form-data") == -1) {
			throw new RuntimeException("ContentType不符合要求[ContentType=" + contentType + "]");
		}

		MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
		MultipartFile requestFile = mrequest.getFile("file");
		if (requestFile == null) {
			throw new RuntimeException("没有获取到文件");
		}
		String ext = ".xls";
		String fileNmae = requestFile.getOriginalFilename();
		if(fileNmae.endsWith(".xlsx"))
			ext = ".xlsx";
		//设置文件名
		String fileNewName = DataType.getCurrentDateByFormart("yyyyMMddHHmmss") + ext;
		String filePath =  SystemConfig.getPathOfTemp() +  fileNewName;
		File tempFile = new File(filePath);
		CommonFileUtils.upload(request, "file", tempFile);

		//region 获得数据源.
		List sheetNameList = Arrays.asList(DBLoad.GenerTableNames(filePath));
		if (sheetNameList.size() < 3
				|| sheetNameList.contains("部门") == false
				|| sheetNameList.contains("岗位") == false
				|| sheetNameList.contains("人员") == false)
		{
			throw new RuntimeException("excel不符合要求，需要包含Sheet(部门、岗位、人员)");
		}

		//获得部门数据.
		DataTable dtDept = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("部门"));
		for (int i = 0; i < dtDept.Columns.size(); i++)
		{
			String name = dtDept.Columns.get(i).ColumnName;
			name = name.replace(" ", "");
			name = name.replace("*", "");
			dtDept.Columns.get(i).ColumnName = name;
		}

		//获得岗位数据.
		DataTable dtStation = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("岗位"));
		for (int i = 0; i < dtStation.Columns.size(); i++)
		{
			String name = dtStation.Columns.get(i).ColumnName;
			name = name.replace(" ", "");
			name = name.replace("*", "");
			dtStation.Columns.get(i).ColumnName = name;
		}

		//获得人员数据.
		DataTable dtEmp = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("人员"));
		for (int i = 0; i < dtEmp.Columns.size(); i++)
		{
			String name = dtEmp.Columns.get(i).ColumnName;
			name = name.replace(" ", "");
			name = name.replace("*", "");
			dtEmp.Columns.get(i).ColumnName = name;
		}

		//endregion 获得数据源.

		//region 检查是否有根目录为 0 且 组织为100 的数据?
		//检查数据的完整性.
		//1.检查是否有根目录为0的数据?
		int num = 0;
		boolean isHave = false;
		for (DataRow dr : dtDept.Rows)
		{
			String str1 = dr.getValue(dtDept.Columns.get(0)) instanceof String ? (String)dr.getValue(dtDept.Columns.get(0)) : null;
			if (DataType.IsNullOrEmpty(str1) == true)
			{
				continue;
			}

			num++;
			String str = dr.getValue(dtDept.Columns.get(1)) instanceof String ? (String)dr.getValue(dtDept.Columns.get(1)) : null;
			if (DataType.IsNullOrEmpty(str) == true)
			{
				return "err@导入出现数据错误:" + str1 + "的." + dtDept.Columns.get(1) + "-不能有空数据， 第[" + num + "]行数据.";
			}

			if (str.equals("0") == true || str.equals("root") == true)
			{
				isHave = true;
				break;
			}

			try {
				bp.wf.port.Dept rootDept = new bp.wf.port.Dept();
				int i = rootDept.Retrieve("ParentNo", "0", "OrgNo", "100");
				if (i > 0)
				{
					isHave = true;
					break;
				}
			}catch (Exception e){
				return "err@没有找到部门根目录节点.ParentNo=0,OrgNo=100";
			}
		}
		if (isHave == false)
		{
			return "err@没有找到部门根目录节点.";
		}

		//endregion 检查是否有根目录为0的数据

		//region 检查部门名称是否重复?
		String deptStrs = "";
		for (DataRow dr : dtDept.Rows)
		{
			String deptName = dr.getValue(dtDept.Columns.get(0)) instanceof String ? (String)dr.getValue(dtDept.Columns.get(0)) : null;
			if (DataType.IsNullOrEmpty(deptName) == true)
			{
				continue;
			}

			if (deptStrs.contains("," + deptName + ",") == true)
			{
				return "err@部门名称:" + deptName + "重复.";
			}

			//加起来..
			deptStrs += "," + deptName + ",";
		}

		//endregion 检查部门名称是否重复?

		//region 检查人员帐号是否重复?
		String emps = "";
		for (DataRow dr : dtEmp.Rows)
		{
			String empNo = dr.getValue(dtEmp.Columns.get(0)) instanceof String ? (String)dr.getValue(dtEmp.Columns.get(0)) : null;
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			if (emps.contains("," + empNo + ",") == true)
			{
				return "err@人员帐号:" + empNo + "重复.";
			}

			//加起来..
			emps += "," + empNo + ",";
		}

		//endregion 检查人员帐号是否重复?

		//region 检查岗位名称是否重复?
		String staStrs = "";
		for (DataRow dr : dtStation.Rows)
		{
			String staName = dr.getValue(dtStation.Columns.get(0)) instanceof String ? (String)dr.getValue(dtStation.Columns.get(0)) : null;
			if (DataType.IsNullOrEmpty(staName) == true)
			{
				continue;
			}

			if (staStrs.contains("," + staName + ",") == true)
			{
				return "err@岗位名称:" + staName + "重复.";
			}

			//加起来..
			staStrs += "," + staName + ",";
		}

		//endregion 检查岗位名称是否重复?

		//region 检查人员的部门名称是否存在于部门数据里?
		int idx = 0;
		for (DataRow dr : dtEmp.Rows)
		{
			String emp = dr.getValue(dtEmp.Columns.get(0)) instanceof String ? (String)dr.getValue(dtEmp.Columns.get(0)) : null;
			if (DataType.IsNullOrEmpty(emp) == true)
			{
				continue;
			}

			idx++;
			//去的部门编号.
			String strs = dr.getValue("部门名称") instanceof String ? (String)dr.getValue("部门名称") : null;
			if (DataType.IsNullOrEmpty(strs) == true)
			{
				return "err@第[" + idx + "]行,人员[" + emp + "]部门不能为空:" + strs + ".";
			}

			String[] mystrs = strs.split("[,]", -1);
			for (String str : mystrs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				if (str.equals("0") || str.equals("root") == true)
				{
					continue;
				}

				//先看看数据是否有?
				bp.wf.port.Dept dept = new bp.wf.port.Dept();
				if (dept.Retrieve("Name", str) == 1)
				{
					continue;
				}

				//从xls里面判断.
				isHave = false;
				for (DataRow drDept : dtDept.Rows)
				{
					if (str.equals(drDept.getValue(dtDept.Columns.get(0)).toString()) == true)
					{
						isHave = true;
						break;
					}
				}
				if (isHave == false)
				{
					return "err@第[" + idx + "]行,人员[" + emp + "]部门名[" + str + "]，不存在模版里。";
				}
			}
		}

		//endregion 检查人员的部门名称是否存在于部门数据里


		//region 检查人员的岗位名称是否存在于岗位数据里?
		idx = 0;
		for (DataRow dr : dtEmp.Rows)
		{
			String emp = dr.getValue(dtEmp.Columns.get(0)) instanceof String ? (String)dr.getValue(dtEmp.Columns.get(0)) : null;
			if (DataType.IsNullOrEmpty(emp) == true)
			{
				continue;
			}

			idx++;

			//岗位名称..
			String strs = dr.getValue("岗位名称") instanceof String ? (String)dr.getValue("岗位名称") : null;
			if (DataType.IsNullOrEmpty(strs) == true)
			{
				continue;
			}

			//判断岗位.
			String[] mystrs = strs.split("[,]", -1);
			for (String str : mystrs)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				//先看看数据是否有?
				Station stationEn = new Station();
				if (stationEn.Retrieve("Name", str) == 1)
				{
					continue;
				}

				//从 xls 判断.
				isHave = false;
				for (DataRow drSta : dtStation.Rows)
				{
					if (str.equals(drSta.getValue(dtStation.Columns.get(0)).toString()) == true)
					{
						isHave = true;
						break;
					}
				}
				if (isHave == false)
				{
					return "err@第[" + idx + "]行,人员[" + emp + "]岗位名称[" + str + "]，不存在模版里。";
				}
			}
		}

		//endregion 检查人员的部门名称是否存在于部门数据里


		//region 检查部门负责人是否存在于人员列表里?
		String empStrs = ",";
		for (DataRow item : dtEmp.Rows)
		{
			empStrs += item.getValue(dtEmp.Columns.get(0)).toString() + ",";
		}
		idx = 0;
		for (DataRow dr : dtDept.Rows)
		{
			String empNo = dr.getValue(dtDept.Columns.get(2)) instanceof String ? (String)dr.getValue(dtDept.Columns.get(2)) : null;
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			idx++;
			if (empStrs.contains("," + empNo + ",") == false)
			{
				return "err@部门主管（部门负责人）[" + empNo + "]不存在与人员表里，第[" + idx + "]行.";
			}
		}

		//endregion 检查部门负责人是否存在于人员列表里


		//region 检查直属领导帐号是否存在于人员列表里?
		idx = 0;
		for (DataRow dr : dtEmp.Rows)
		{
			String empNo = dr.getValue(dtEmp.Columns.get(6)) instanceof String ? (String)dr.getValue(dtEmp.Columns.get(6)) : null;
			if (DataType.IsNullOrEmpty(empNo) == true)
			{
				continue;
			}

			idx++;
			if (empStrs.contains("," + empNo + ",") == false)
			{
				return "err@部门负责人[" + empNo + "]不存在与人员表里，第[" + idx + "]行.";
			}
		}

		//endregion 检查部门负责人是否存在于人员列表里

		//2级管理员的组织编号
		String orgNo = WebUser.getOrgNo();
		//region 插入数据到 Port_StationType.
		idx = -1;
		for (DataRow dr : dtStation.Rows)
		{
			idx++;
			String str = dr.getValue(dtStation.Columns.get(1)) instanceof String ? (String)dr.getValue(dtStation.Columns.get(1)) : null;

			//判断是否是空.
			if (DataType.IsNullOrEmpty(str) == true)
			{
				continue;
			}

			if (str.equals("岗位类型") == true)
			{
				continue;
			}

			str = str.trim();

			//看看数据库是否存在.
			StationType st = new StationType();
			if (st.IsExit("Name", str, "OrgNo", orgNo) == false)
			{
				st.setName(str);
				st.setOrgNo(orgNo);
				st.setNo(DBAccess.GenerGUID(0, null, null));
				st.Insert();
			}
		}

		//endregion 插入数据到 Port_StationType.

		//region 插入数据到 Port_Station.
		idx = -1;
		for (DataRow dr : dtStation.Rows)
		{
			idx++;
			String str = dr.getValue(dtStation.Columns.get(0).toString()) instanceof String ? (String)dr.getValue(dtStation.Columns.get(0).toString()) : null;

			//判断是否是空.
			if (DataType.IsNullOrEmpty(str) == true)
			{
				continue;
			}

			if (str.equals("岗位名称") == true)
			{
				continue;
			}

			//获得类型的外键的编号.
			String stationTypeName = dr.getValue(dtStation.Columns.get(1).toString()).toString().trim();
			StationType st = new StationType();
			if (st.Retrieve("Name", stationTypeName, "OrgNo", orgNo) == 0)
			{
				return "err@系统出现错误,没有找到岗位类型[" + stationTypeName + "]的数据.";
			}

			//看看数据库是否存在.
			Station sta = new Station();
			sta.setName(str);
			sta.setOrgNo(orgNo);
			sta.setIdx(idx);

			//不存在就插入.
			if (sta.IsExit("Name", str, "OrgNo", orgNo) == false)
			{
				sta.setOrgNo(orgNo);
				sta.setFKStationType(st.getNo());
				sta.setNo(DBAccess.GenerGUID(0, null, null));
				sta.Insert();
			}
			else
			{
				//存在就更新.
				sta.setFKStationType(st.getNo());
				sta.Update();
			}
		}

		//endregion 插入数据到 Port_Station.

		//region 插入数据到 Port_Dept.
		idx = -1;
		for (DataRow dr : dtDept.Rows)
		{
			//获得部门名称.
			String deptName = dr.getValue(dtDept.Columns.get(0)) instanceof String ? (String)dr.getValue(dtDept.Columns.get(0)) : null;
			if (deptName.equals("部门名称") == true)
			{
				continue;
			}

			String parentDeptName = dr.getValue(dtDept.Columns.get(1)) instanceof String ? (String)dr.getValue(dtDept.Columns.get(1)) : null;
			String leader = dr.getValue(dtDept.Columns.get(2)) instanceof String ? (String)dr.getValue(dtDept.Columns.get(2)) : null;

			//说明是根目录.
			if (parentDeptName.equals("0") == true || parentDeptName.equals("root") == true)
			{
				bp.wf.port.Dept root = new bp.wf.port.Dept();
				root.setNo(orgNo);
				root.setParentNo("100");
				root.setOrgNo(orgNo);
				if (root.RetrieveFromDBSources() == 0)
				{
					root.Insert();
					continue;
				}
				root.setName(deptName);
				root.Update();
				continue;
			}

			//先求出来父节点.
			bp.wf.port.Dept parentDept = new bp.wf.port.Dept();
			int i = parentDept.Retrieve("Name", parentDeptName, "OrgNo", orgNo);
			if (i == 0)
			{
				return "err@没有找到当前部门[" + deptName + "]的上一级部门[" + parentDeptName + "]";
			}

			bp.wf.port.Dept myDept = new bp.wf.port.Dept();

			//如果数据库存在.
			i = parentDept.Retrieve("Name", deptName, "OrgNo", orgNo);
			if (i >= 1)
			{
				//插入部门.
				myDept.setName(deptName);
				myDept.setOrgNo(orgNo);
				myDept.setNo(DBAccess.GenerGUID(0, null, null));
				myDept.setParentNo(parentDept.getNo());
				myDept.setLeader(leader); //领导.
				myDept.setIdx(idx);
				myDept.Update();
				continue;
			}

			//插入部门.
			myDept.setName(deptName);
			myDept.setOrgNo(orgNo);
			myDept.setNo(DBAccess.GenerGUID(0, null, null));
			myDept.setParentNo(parentDept.getNo());
			myDept.setLeader(leader); //领导.
			myDept.setIdx(idx);
			myDept.Insert();
		}

		//endregion 插入数据到 Port_Dept.

		///#region 插入到 Port_Emp.
		try {
			for (DataRow dr : dtEmp.Rows)
			{
				//登录帐号
				String empNo = dr.getValue(dtEmp.Columns.get(0)).toString();
				//名称
				String empName = dr.getValue(dtEmp.Columns.get(1)).toString();
				//部门名称
				String deptNames = dr.getValue(dtEmp.Columns.get(2)).toString();
				//岗位名称
				String stationNames = dr.getValue(dtEmp.Columns.get(3)).toString();
				//电话
				String tel = dr.getValue(dtEmp.Columns.get(4)).toString();
				//邮件
				String email = dr.getValue(dtEmp.Columns.get(5)).toString();
				//直属主管
				String leader = dr.getValue(dtEmp.Columns.get(6)).toString(); //部门领导.

				Emp emp = new Emp();
				int i = emp.Retrieve("No", orgNo + "_" + empNo, "OrgNo", orgNo);
				if (i >= 1)
				{
					emp.setTel (tel);
					emp.setEmail(email);
					emp.setName (empName);
					emp.Update();
					continue;
				}

				//找到人员的部门.
				String[] myDeptStrs = deptNames.split("[,]", -1);
				bp.wf.port.Dept dept = new bp.wf.port.Dept();
				for (String deptName : myDeptStrs)
				{
					if (DataType.IsNullOrEmpty(deptName) == true)
					{
						continue;
					}

					i = dept.Retrieve("Name", deptName, "OrgNo", orgNo);
					if (i <= 0)
					{
						return "err@部门名称不存在." + deptName;
					}

					DeptEmp de = new DeptEmp();
					de.setDeptNo(dept.getNo());
					de.setDeptName(dept.getName());
					de.setEmpNo(orgNo + "_" + empNo);
					de.setOrgNo(orgNo);
					de.setMyPK(de.getDeptNo() + "_" + de.getEmpNo());
					de.Delete();
					de.Insert();
				}

				//插入岗位.
				String[] staNames = stationNames.split("[,]", -1);
				Station sta = new Station();
				for (String staName : staNames)
				{
					if (DataType.IsNullOrEmpty(staName) == true)
					{
						continue;
					}

					i = sta.Retrieve("Name", staName, "OrgNo", orgNo);
					if (i == 0)
					{
						return "err@岗位名称不存在." + staName;
					}

					DeptEmpStation des = new DeptEmpStation();
					des.setDeptNo(dept.getNo());
					des.setEmpNo(orgNo + "_" + empNo);
					des.setStationNo(sta.getNo());
					des.setOrgNo(orgNo);
					des.setMyPK(des.getDeptNo() + "_" + des.getEmpNo() + "_" + des.getStationNo());
					des.Delete();
					des.Insert();
				}

				//插入到数据库.
				emp.setNo(orgNo + "_" + empNo);
				if(emp.RetrieveFromDBSources() == 0){
					emp.setUserID (empNo);
					emp.setName (empName);
					emp.setDeptNo(dept.getNo());
					emp.setOrgNo(orgNo);
					emp.setTel (tel);
					emp.setEmail(email);
					emp.SetValByKey("Leader", leader);
					emp.setNo(orgNo + "_" + empNo);
					emp.DirectInsert();

				}else {
					emp.setUserID (empNo);
					emp.setName (empName);
					emp.setDeptNo(dept.getNo());
					emp.setOrgNo(orgNo);
					emp.setTel (tel);
					emp.setEmail(email);
					emp.SetValByKey("Leader", leader);
					emp.setNo(orgNo + "_" + empNo);
					emp.DirectUpdate();
				}

				emp.setPass(SystemConfig.getUserDefaultPass());
				if(SystemConfig.getIsEnablePasswordEncryption())
				{
					String pass = bp.tools.MD5Utill.MD5Encode(SystemConfig.getUserDefaultPass(),"UTF8");
					emp.setPass(pass);
				}

			}
		}catch (Exception e){
			String sql = "DELETE FROM Port_Dept WHERE OrgNo='" + orgNo + "'; ";
			sql += "DELETE FROM Port_DeptEmp WHERE OrgNo='" + orgNo + "'; ";
			sql += "DELETE FROM Port_Emp WHERE OrgNo='" + orgNo + "'; ";
			sql += "DELETE FROM Port_Stationtype WHERE OrgNo='" + orgNo + "'; ";
			sql += "DELETE FROM Port_Station WHERE OrgNo='" + orgNo + "'; ";
			DBAccess.RunSQLs(sql);
			return "info@导入失败:" + e.getMessage();
		}

		///#endregion 插入到 Port_Emp.

		//删除临时文件
		tempFile.delete();
		//  System.IO.File.Delete(filePath);

		return "执行完成.";
	}

	public final String EmpDepts_Init() throws Exception {
		String empNo = this.getEmpNo();
		if (DataType.IsNullOrEmpty(empNo) == true)
		{
			return "err@参数FK_Emp不能为空";
		}
		//if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		//    empNo = bp.web.WebUser.getOrgNo() + "_" + empNo;

		Emp emp = new Emp();
		emp.setNo(empNo);
		emp.Retrieve();

		DataSet ds = new DataSet();
		String dbstr = SystemConfig.getAppCenterDBVarStr();

		String sql = "";

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			//获取当前人员所在的部门及兼职部门
			sql = "SELECT B.No AS FK_Dept,B.Name AS  FK_DeptText,A.MyPK  From Port_DeptEmp A,Port_Dept B WHERE A.FK_Dept=B.No AND A.FK_Emp='" + empNo + "'";
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				sql += " AND B.OrgNo='" + emp.getOrgNo() + "'";
			}
		}
		else
		{
			//获取当前人员所在的部门及兼职部门
			sql = "SELECT B.No AS FK_Dept,B.Name AS  FK_DeptText,A.MyPK  From Port_DeptEmp A,Port_Dept B WHERE A.FK_Dept=B.No AND A.FK_Emp='" + empNo + "'";
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				sql += " AND B.OrgNo='" + emp.getOrgNo() + "'";
			}
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "FK_Dept";
			dt.Columns.get(1).ColumnName = "FK_DeptText";
			dt.Columns.get(2).ColumnName = "MyPK";
		}

		if (dt.Rows.size() == 0)
		{
			DeptEmp deptEmp = new DeptEmp();
			deptEmp.setDeptNo(emp.getDeptNo());
			deptEmp.setEmpNo(emp.getNo());
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				deptEmp.setMyPK(emp.getDeptNo() + "_" + emp.getUserID());
			}
			else
			{
				deptEmp.setMyPK(emp.getDeptNo() + "_" + emp.getNo());
			}

			deptEmp.setOrgNo(emp.getOrgNo());
			deptEmp.Insert();
			DataRow dr = dt.NewRow();
			dr.setValue(0, emp.getDeptNo());
			dr.setValue(1, emp.getDeptText());
			dr.setValue(2, deptEmp.getMyPK());
			dt.Rows.add(dr);
		}
		dt.TableName = "Port_DeptEmp";
		ds.Tables.add(dt);
		//获取岗位
		sql = "SELECT B.No AS FK_Station,B.Name AS FK_StationText ,A.FK_Dept AS FK_Dept From Port_DeptEmpStation A,Port_Station B WHERE A.FK_Station=B.No AND A.FK_Emp='" + empNo + "'";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			sql += " AND B.OrgNo='" + WebUser.getOrgNo() + "'";
		}

		dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "FK_Station";
			dt.Columns.get(1).ColumnName = "FK_StationText";
			dt.Columns.get(2).ColumnName = "FK_Dept";
		}

		dt.TableName = "Port_DeptEmpStation";
		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

	/**
	取消人员部门岗位管理关系
	@return
	*/
	public final String DeptEmpStation_Dele()
	{
		String sql = "delete from Port_DeptEmpStation where FK_Emp='" + this.GetRequestVal("FK_Emp") + "' and FK_Dept='" + this.GetRequestVal("FK_Dept") + "'";
		DBAccess.RunSQL(sql);
		return "执行成功 ";
	}
	/** 
	 绑定人员
	 
	 @return 
	*/
	public final String BindEmp() throws Exception {
		String deptNo = this.GetRequestVal("DeptNo");

		Emps emps = new Emps();
		emps.Retrieve("FK_Dept", deptNo, "Idx");
		DataTable dt = emps.ToDataTableField("dt");
		dt.Columns.Add("State");

		String sql = "SELECT No,Name,Tel,Email FROM Port_Emp A, Port_DeptEmp B WHERE A.No=B.FK_Emp AND B.FK_Dept='" + deptNo + "' AND A.FK_Dept!='" + deptNo + "'";
		DataTable mydt = DBAccess.RunSQLReturnTable(sql);
		for (DataRow mydr : mydt.Rows)
		{
			DataRow dr = dt.NewRow();
			dr.setValue("No", mydr.getValue(0));
			dr.setValue("Name", mydr.getValue(1));
			dr.setValue("Tel", mydr.getValue(2));
			dr.setValue("Email", mydr.getValue(3));
			dr.setValue("State", 1); //兼职人员.
			//加入里面.
			dt.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(dt);
	}
	//集团版组织结构导入，先清除再插入
	public final String Template_SaveGroupIncByClear() throws Exception {

		//获取数据
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.GroupInc) {
			return "err@必须是集团模式才能使用此接口导入组织结构.";
		}

		if(WebUser.getIsAdmin() == false){
			return "err@必须是管理员账号才可以导入组织.";
		}

		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		if (contentType == null || contentType.indexOf("multipart/form-data") == -1) {
			throw new RuntimeException("ContentType不符合要求[ContentType=" + contentType + "]");
		}

		MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
		MultipartFile requestFile = mrequest.getFile("file");
		if (requestFile == null) {
			return "err@没有获取到文件.";
		}
		String ext = ".xls";
		String fileNmae = requestFile.getOriginalFilename();
		if(fileNmae.endsWith(".xlsx"))
			ext = ".xlsx";
		//设置文件名
		String fileNewName = DataType.getCurrentDateByFormart("yyyyMMddHHmmss") + ext;
		String filePath =  SystemConfig.getPathOfTemp() +  fileNewName;
		File tempFile = new File(filePath);
		CommonFileUtils.upload(request, "file", tempFile);
		//region 获得数据源.
		List sheetNameList = Arrays.asList(DBLoad.GenerTableNames(filePath));
		if (sheetNameList.size() < 5
				|| sheetNameList.contains("组织") == false
				|| sheetNameList.contains("部门") == false
				|| sheetNameList.contains("岗位") == false
				|| sheetNameList.contains("人员") == false
				|| sheetNameList.contains("岗位类型") == false)
		{
			return "err@excel不符合要求，需要包含Sheet(组织、部门、人员、岗位、岗位类型).";
		}
		//获得组织数据.
		DataTable dtOrg = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("组织"));
		//获得部门数据.
		DataTable dtDept = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("部门"));
		//获得人员数据.
		DataTable dtEmp = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("人员"));
		//获得岗位数据.
		DataTable dtStation = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("岗位"));
		//获得岗位类型数据.
		DataTable dtStationType = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("岗位类型"));
		if (dtOrg==null || dtDept==null || dtEmp==null || dtStation==null || dtStationType==null
				|| dtOrg.Rows.size() <= 0 || dtDept.Rows.size()<=0 || dtEmp.Rows.size()<=0
				|| dtStation.Rows.size()<=0 || dtStationType.Rows.size()<=0) {
			return "err@组织、部门、人员、岗位、岗位类型必须都要有数据";
		}

		//检测第一个组织id是否=100
//		DataRow dOrg=dtOrg.Rows.get(0);
//		if(!dOrg.getValue(0).toString().equals("100")){
//			return "err@组织中第一行的组织ID必须是100";
//		}
		/*************部门数据校验*************************/
		//检测第一个部门父id是否=0
		DataRow dDept=dtDept.Rows.get(0);
		if(!dDept.getValue(2).toString().equals("0")){
			Log.DebugWriteInfo("集团组织数据导入错误 : err@部门中第一行的父部门ID必须是0" );
			return "err@部门中第一行的父部门ID必须是0";
		}
		//检测部门id不重复
		//检测父id是否存在
		StringBuilder estr=new StringBuilder("err@");
		AtomicInteger num= new AtomicInteger(2);
       dtDept.Rows.stream().forEach(i->{
            String deptID=i.getValue(0).toString();
			String parentID=i.getValue(2).toString();
			if(deptID.equals("") &&parentID.equals("")){
				return;
			}
			if(deptID.equals(parentID)){
				estr.append("部门数据错误：第").append(num.intValue()).append("行 部门ID和父ID不能相等;");
			}
		   List<DataRow> drc= dtDept.Rows.stream().filter(j->
			    j.getValue(0).toString().equals(deptID)	||   j.getValue(0).toString().equals(parentID)
		   ).collect(Collectors.toList());
			if(drc.size()!=2 && !parentID.equals("0")){
				estr.append("部门数据错误：第").append(num.intValue()).append("行 部门ID不唯一或父ID不存在、不唯一;");
			}
		   num.getAndIncrement();
	   });
      if(!estr.toString().equals("err@")){
		  Log.DebugWriteInfo("集团组织数据导入错误 : " + estr.toString());
		  return  estr.toString();
	  }


		/*************人员数据校验*************************/
		//检测第一个人员是否是admin
		DataRow dEmp=dtEmp.Rows.get(0);
		if(!dEmp.getValue(0).toString().equals("admin")){
			Log.DebugWriteInfo("集团组织数据导入错误 : err@人员中第一行的账号必须是admin" );
			return "err@人员中第一行的账号必须是admin";
		}
		//检测人员id不重复
		//检测部门id是否存在
		//检测岗位是否存在
		num.set(2);
		dtEmp.Rows.stream().forEach(i->{
			String empID=i.getValue(0).toString();
			String deptID=i.getValue(2).toString();
			String stationName=i.getValue(3).toString();
			String orgNo=i.getValue(7).toString();
			if(empID.equals("") &&deptID.equals("")){
				return;
			}
			if(empID.equals("admin")){
				num.getAndIncrement();
				return;
			}
			List<DataRow> drc= dtEmp.Rows.stream().filter(j->
					j.getValue(0).toString().equals(empID)
			).collect(Collectors.toList());
			if(drc.size()>1 ){
				estr.append("人员数据错误：第").append(num.intValue()).append("行,"+empID+" 人员ID不唯一;");
			}
			drc= dtDept.Rows.stream().filter(j->
					(","+deptID+",").contains(","+j.getValue(0).toString()+",")
			).collect(Collectors.toList());
			if(drc==null || drc.size()==0 || drc.size()<deptID.split(",").length){
				estr.append("人员数据错误：第").append(num.intValue()).append("行, "+deptID+"部门ID不存在;");
			}
			//岗位可以空
            if(stationName==null ||stationName.equals("")){
				num.getAndIncrement();
				return;
			}
			StringBuilder stations=new StringBuilder(",");
			for(String str:stationName.split(";")){
				for(String astr:str.split(",")){
					stations.append(astr).append(",");
				}
			}
			drc= dtStation.Rows.stream().filter(j->
							stations.toString().contains(j.getValue(0).toString())
			).collect(Collectors.toList());
			if(drc==null || drc.size()==0){
				estr.append("人员数据错误：第").append(num.intValue()).append("行,"+stations.toString()+" 岗位不存在;");
			}
			num.getAndIncrement();
		});

		if(!estr.toString().equals("err@")){
			Log.DebugWriteInfo("集团组织数据导入错误 : " + estr.toString());
			return  estr.toString();
		}
		/*************岗位数据校验*************************/
		num.set(2);
		StringBuilder ss=new StringBuilder(",");
		dtStationType.Rows.stream().forEach(i->{
			String type=i.getValue(0).toString();
			if(type.equals("") ){
				return;
			}
			ss.append(type).append(",");
		});
		//检测岗位类型是否存在
		dtStation.Rows.stream().forEach(i->{
			String type=i.getValue(1).toString();
			String name=i.getValue(0).toString();
			if(type.equals("") && name.equals("")){
				return;
			}
			if(!ss.toString().contains(type)){
				estr.append("岗位数据错误：第").append(num.intValue()).append("行 岗位类型不存在;");
			}
			num.getAndIncrement();
		});
		if(!estr.toString().equals("err@")){
			Log.DebugWriteInfo("集团组织数据导入错误 : " + estr.toString());
			return  estr.toString();
		}

		//数据操作
		try {
			//数据操作顺序
//			1.组织：			port_org  port_orgadmin
//			2.岗位：			port_station  port_stationtype
//			3.部门：			port_dept
//			4.人员：			port_emp
//			5.人员  部门：	port_deptemp port_deptempstation
//			业务：
//			  1）人员数据中：  多个部门用英文逗号分隔（第一个部门为主部门）,
//			  2）人员数据中：  部门下的多个岗位用英文逗号分隔，不同部门的岗位用英文分号分隔
//	          根据以上2点，数据插入到以下2个表中
//	          port_deptemp        （人员和部门是一对多的情况，部门下的岗位用逗号分隔）
//	          port_deptempstation  （部门和岗位是一对多的情况）

			/*************组织数据处理*************************/
			//删除数据
			delGroupIncData(new String[]{"port_org","port_orgadminer"});
			for (DataRow dr : dtOrg.Rows)
			{
				try {
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					Org org = new Org();
					org.setNo(dr.getValue(0).toString());
					org.setName(dr.getValue(1).toString());
					org.setAdminer(dr.getValue(2).toString());
					org.setAdminerName(dr.getValue(3).toString());
					org.Insert();
					String Admine = dr.getValue(2).toString();
					String AdmineName = dr.getValue(3).toString();
					String[] arr = Admine.split(",");
					for (int i = 0; i < arr.length; i++) {
						OrgAdminer oad = new OrgAdminer();
						oad.setMyPK(org.getNo() + "_" + arr[i]);
						oad.setOrgNo(org.getNo());
						oad.setEmpNo(arr[i]);
						oad.setEmpName(AdmineName.split(",")[i]);
						oad.Insert();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}


          /*************岗位数据处理*************************/
			delGroupIncData(new String[]{"port_station","port_stationtype"});
			for (int i=0;i<dtStationType.Rows.size();i++)
			{
				try {
					DataRow dr = dtStationType.Rows.get(i);
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					StationType st = new StationType();
					st.setNo(String.valueOf(i));
					st.setOrgNo(dr.getValue(1).toString());
					st.setName(dr.getValue(0).toString());
					st.Insert();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			for (int i=0;i<dtStation.Rows.size();i++)
			{
				try {
					DataRow dr = dtStation.Rows.get(i);
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					Station st = new Station();
					st.setNo(String.valueOf(i));
					st.setOrgNo(dr.getValue(2).toString());
					st.setName(dr.getValue(0).toString());
					List<Integer> sIndex =
							IntStream.range(0, dtStationType.Rows.size())
									.filter(item ->
											dtStationType.Rows.get(item).getValue(0).toString().equals(dr.getValue(1).toString())
													&& dtStationType.Rows.get(item).getValue(1).toString().equals(dr.getValue(2).toString())
									)
									.boxed()
									.collect(Collectors.toList());
					st.setFKStationType(sIndex != null ? sIndex.get(0).toString() : "");
					st.Insert();
					String webUserOrgNo = WebUser.getOrgNo();
					if (!webUserOrgNo.equals(dr.getValue(2).toString())) {
						Paras ps = new Paras();
						ps.SQL = "update port_station set OrgNo=" + ps.getDBStr() + "v1 where No=" + ps.getDBStr() + "v2";
						ps.Add("v1", dr.getValue(2).toString());
						ps.Add("v2", String.valueOf(i));
						DBAccess.RunSQL(ps);
					}
				}catch(Exception es){
					es.printStackTrace();
				}
			}

			/*************部门数据处理*************************/
			delGroupIncData(new String[]{"port_dept"});
			for (int i=0;i<dtDept.Rows.size();i++)
			{
				try{
					DataRow dr=dtDept.Rows.get(i);
					if(dr==null || dr.getValue(0).toString().equals("")){
						continue;
					}
					Dept st=new Dept();
					st.setNo(dr.getValue(0).toString());
					st.setOrgNo(dr.getValue(3).toString());
					st.setName(dr.getValue(1).toString());
					st.setParentNo(dr.getValue(2).toString());
					st.setLeader(dr.getValue(4).toString());
					st.Insert();
				}catch (Exception e){
					e.printStackTrace();;
				}
			}

            /*************人员数据处理*************************/
			delGroupIncData(new String[]{"port_emp","port_deptemp","port_deptempstation"});
			//人员表数据插入
			for (DataRow dr : dtEmp.Rows)
			{
				try {
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					String[] depts = dr.getValue(2).toString().split(",");
					String[] stations = dr.getValue(3).toString().split(";");
					Emp em = new Emp();
					em.setNo(dr.getValue(0).toString());
					em.setOrgNo(dr.getValue(7).toString());
					em.setName(dr.getValue(1).toString());
					em.setEmail(dr.getValue(5).toString());
					em.setTel(dr.getValue(4).toString());
					//首部门
					em.setDeptNo(depts[0]);
					em.SetValByKey("Leader", dr.getValue(6).toString());
					em.Insert();
					//部门人员表数据插入
					for (int i = 0; i < depts.length; i++) {
						String str = depts[i];
						if (str == null || str.equals("")) {
							continue;
						}
						DeptEmp demp = new DeptEmp();
						demp.setMyPK(str + "_" + em.getNo());
						demp.SetValByKey("FK_Dept", str);
						demp.SetValByKey("FK_Emp", em.getNo());
						//获取部门对应的组织和名称
						DataRow drow = dtDept.Rows.stream().filter(j -> j.getValue(0).toString().equals(str))
								.collect(Collectors.toList()).get(0);
						demp.setOrgNo(drow.getValue(3).toString());
						demp.setDeptName(drow.getValue(1).toString());
						demp.Delete();
						if(stations.length > i){
							demp.setStationNoT(stations[i]);

							//获取岗位ids
							String sno = "";
							for (int j = 0; j < dtStation.Rows.size(); j++) {
								DataRow dt = dtStation.Rows.get(j);
								String name = dt.getValue(0).toString();
								String orgno=dt.getValue(2).toString();
								if (("," + stations[i] + ",").contains("," + name + ",")
										&&drow.getValue(3).toString().equals(orgno)) {
									sno += j + ",";
								}
							}
							demp.setStationNo(sno.equals("") ? sno : sno.substring(0, sno.length() - 1));

							//部门人员岗位表数据插入
							for (int g = 0; g < sno.split(",").length; g++) {
								String sindex = sno.split(",")[g];
								if (sindex == null || sindex.equals("")) {
									continue;
								}
								DeptEmpStation demps = new DeptEmpStation();
								demps.setMyPK(str + "_" + em.getNo() + "_" + sindex);
								demps.setDeptNo(str);
								demps.setOrgNo(demp.getOrgNo());
								demps.setStationNo(sindex);
								demps.setEmpNo(em.getNo());
								demps.Insert();
							}
						}

						demp.Insert();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return "info@导入失败:" + e.getMessage();
		}
		//删除临时文件
		tempFile.delete();
		return "执行完成";
	}
	private void delGroupIncData(String[]tablesName){
		StringBuilder sql=new StringBuilder("");
		for(String str:tablesName){
			sql.append("DELETE FROM ").append(str).append(";");
		}
		DBAccess.RunSQLs(sql.toString());

	}
	//集团版组织结构导入，更新或追加
	public final String Template_SaveGroupIncByAppend() throws Exception {
//获取数据
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.GroupInc) {
			return "err@必须是集团模式才能使用此接口导入组织结构.";
		}

		if(WebUser.getIsAdmin() == false){
			return "err@必须是管理员账号才可以导入组织.";
		}

		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		if (contentType == null || contentType.indexOf("multipart/form-data") == -1) {
			throw new RuntimeException("ContentType不符合要求[ContentType=" + contentType + "]");
		}

		MultipartHttpServletRequest mrequest = CommonFileUtils.getMultipartHttpServletRequest(request);
		MultipartFile requestFile = mrequest.getFile("file");
		if (requestFile == null) {
			return "err@没有获取到文件.";
		}
		String ext = ".xls";
		String fileNmae = requestFile.getOriginalFilename();
		if(fileNmae.endsWith(".xlsx"))
			ext = ".xlsx";
		//设置文件名
		String fileNewName = DataType.getCurrentDateByFormart("yyyyMMddHHmmss") + ext;
		String filePath =  SystemConfig.getPathOfTemp() +  fileNewName;
		File tempFile = new File(filePath);
		CommonFileUtils.upload(request, "file", tempFile);
		//region 获得数据源.
		List sheetNameList = Arrays.asList(DBLoad.GenerTableNames(filePath));
		if (sheetNameList.size() < 5
				|| sheetNameList.contains("组织") == false
				|| sheetNameList.contains("部门") == false
				|| sheetNameList.contains("岗位") == false
				|| sheetNameList.contains("人员") == false
				|| sheetNameList.contains("岗位类型") == false)
		{
			return "err@excel不符合要求，需要包含Sheet(组织、部门、人员、岗位、岗位类型).";
		}
		//获得组织数据.
		DataTable dtOrg = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("组织"));
		//获得部门数据.
		DataTable dtDept = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("部门"));
		//获得人员数据.
		DataTable dtEmp = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("人员"));
		//获得岗位数据.
		DataTable dtStation = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("岗位"));
		//获得岗位类型数据.
		DataTable dtStationType = DBLoad.ReadExcelFileToDataTable(filePath, sheetNameList.indexOf("岗位类型"));
		//追加时组织、岗位、岗位类型可以为空
		if ( dtDept==null && dtEmp==null && dtStation==null || (dtDept.Rows.size()<=0 && dtEmp.Rows.size()<=0 && dtStation.Rows.size()<=0)) {
			return "err@部门、人员、岗位必须都要有一个sheet有追加数据";
		}

		//数据操作
		try {
			//数据操作顺序
//			1.组织：			port_org  port_orgadmin
//			2.岗位：			port_station  port_stationtype
//			3.部门：			port_dept
//			4.人员：			port_emp
//			5.人员  部门：	port_deptemp port_deptempstation
//			业务：
//			  1）人员数据中：  多个部门用英文逗号分隔（第一个部门为主部门）,
//			  2）人员数据中：  部门下的多个岗位用英文逗号分隔，不同部门的岗位用英文分号分隔
//	          根据以上2点，数据插入到以下2个表中
//	          port_deptemp        （人员和部门是一对多的情况，部门下的岗位用逗号分隔）
//	          port_deptempstation  （部门和岗位是一对多的情况）

			/*************组织数据处理*************************/
			for (DataRow dr : dtOrg.Rows)
			{
				try {
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					Org org = new Org();
					org.setNo(dr.getValue(0).toString());
					if(org.RetrieveFromDBSources()> 0 ){
						org.setName(dr.getValue(1).toString());
						org.setAdminer(dr.getValue(2).toString());
						org.setAdminerName(dr.getValue(3).toString());
						org.Update();
					}else {
						org.setName(dr.getValue(1).toString());
						org.setAdminer(dr.getValue(2).toString());
						org.setAdminerName(dr.getValue(3).toString());
						org.Insert();
					}
					String Admine = dr.getValue(2).toString();
					String AdmineName = dr.getValue(3).toString();
					String[] arr = Admine.split(",");
					for (int i = 0; i < arr.length; i++) {
						OrgAdminer oad = new OrgAdminer();
						oad.setMyPK(org.getNo() + "_" + arr[i]);
						if(oad.RetrieveFromDBSources()> 0 ){
							oad.setOrgNo(org.getNo());
							oad.setEmpNo(arr[i]);
							oad.setEmpName(AdmineName.split(",")[i]);
							oad.Update();
						}else {
							oad.setOrgNo(org.getNo());
							oad.setEmpNo(arr[i]);
							oad.setEmpName(AdmineName.split(",")[i]);
							oad.Insert();
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			/*************岗位数据处理*************************/
			for (int i=0;i<dtStationType.Rows.size();i++)
			{
				try {
					DataRow dr = dtStationType.Rows.get(i);
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					String stationTypeName = dr.getValue(0).toString();
					String orgNo = dr.getValue(1).toString();

					StationType st = new StationType();
					String sql ="select count(*)  from port_stationtype where name = '"+stationTypeName+"' and orgno ='"+orgNo+"'";
					int count=DBAccess.RunSQLReturnValInt(sql);
					if(count >0 ){
						st.setOrgNo(orgNo);
						st.setName(stationTypeName);
						st.Update();
					}else {
						st.setNo(DBAccess.GenerGUID());
						st.setOrgNo(orgNo);
						st.setName(stationTypeName);
						st.Insert();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			for (int i=0;i<dtStation.Rows.size();i++)
			{
				try {
					DataRow dr = dtStation.Rows.get(i);
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					String stationName = dr.getValue(0).toString();
					String stationTypeName = dr.getValue(1).toString();
					String orgNo = dr.getValue(2).toString();
					Station st = new Station();

					String sql ="select no  from port_stationtype where name = '"+stationTypeName+"' and orgno ='"+orgNo+"'";
					String fkStationType = DBAccess.RunSQLReturnString(sql);

					String sqlStation ="select count(*)  from port_station where name = '"+stationName+"' and orgno ='"+orgNo+"'";
					int count=DBAccess.RunSQLReturnValInt(sqlStation);
					if(count >0 ){
						st.setOrgNo(orgNo);
						st.setName(stationName);
						st.setFKStationType(fkStationType);
						st.Update();
					}else {
						st.setNo(DBAccess.GenerGUID());
						st.setOrgNo(orgNo);
						st.setName(stationName);
						st.setFKStationType(fkStationType);
						st.Insert();
					}
				}catch(Exception es){
					es.printStackTrace();
				}
			}

			/*************部门数据处理*************************/
			for (int i=0;i<dtDept.Rows.size();i++)
			{
				try{
					DataRow dr=dtDept.Rows.get(i);
					if(dr==null || dr.getValue(0).toString().equals("")){
						continue;
					}
					Dept dept=new Dept();
					dept.setNo(dr.getValue(0).toString());
					if(dept.RetrieveFromDBSources()> 0 ){
						dept.setOrgNo(dr.getValue(3).toString());
						dept.setName(dr.getValue(1).toString());
						dept.setParentNo(dr.getValue(2).toString());
						dept.setLeader(dr.getValue(4).toString());
						dept.Update();
					}else{
						dept.setOrgNo(dr.getValue(3).toString());
						dept.setName(dr.getValue(1).toString());
						dept.setParentNo(dr.getValue(2).toString());
						dept.setLeader(dr.getValue(4).toString());
						dept.Insert();
					}

				}catch (Exception e){
					e.printStackTrace();;
				}
			}

			/*************人员数据处理*************************/
			//人员表数据插入
			for (DataRow dr : dtEmp.Rows)
			{
				try {
					if (dr == null || dr.getValue(0).toString().equals("")) {
						continue;
					}
					String[] depts = dr.getValue(2).toString().split(",");
					String[] stations = new String[]{};
					if(!DataType.IsNullOrEmpty(dr.getValue(3).toString().trim())){
						stations= dr.getValue(3).toString().split(";");
					}

					String OrgNo = dr.getValue(7).toString();
					Emp em = new Emp();
					em.setNo(dr.getValue(0).toString());
					if(em.RetrieveFromDBSources()> 0 ){
						em.setOrgNo(OrgNo);
						em.setName(dr.getValue(1).toString());
						em.setEmail(dr.getValue(5).toString());
						em.setTel(dr.getValue(4).toString());
						//首部门
						em.setDeptNo(depts[0]);
						em.SetValByKey("Leader", dr.getValue(6).toString());
						em.Update();
					}else{
						em.setOrgNo(OrgNo);
						em.setName(dr.getValue(1).toString());
						em.setEmail(dr.getValue(5).toString());
						em.setTel(dr.getValue(4).toString());
						//首部门
						em.setDeptNo(depts[0]);
						em.SetValByKey("Leader", dr.getValue(6).toString());
						em.Insert();
					}

					DBAccess.RunSQL("DELETE FROM Port_DeptEmp WHERE FK_Emp='" + dr.getValue(0).toString() + "' AND FK_Dept='"+depts[0]+"' AND OrgNo='" + OrgNo + "'");
					DBAccess.RunSQL("DELETE FROM Port_DeptEmpStation WHERE FK_Emp='" + dr.getValue(0).toString() + "' AND FK_Dept='"+depts[0]+"' AND OrgNo='" + OrgNo + "'");

					//部门人员表数据插入
					for (int i = 0; i < depts.length; i++) {
						String str = depts[i];
						if (str == null || str.equals("")) {
							continue;
						}
						DeptEmp demp = new DeptEmp();
						demp.setMyPK(str + "_" + em.getNo());

						demp.SetValByKey("FK_Dept", str);
						demp.SetValByKey("FK_Emp", em.getNo());
						//获取部门对应的组织和名称
						String sqlDept ="select name  from port_dept where no = '"+str+"' and orgno ='"+OrgNo+"'";
						String deptName = DBAccess.RunSQLReturnString(sqlDept);

						demp.setOrgNo(OrgNo);
						demp.setDeptName(deptName);

						String sno="";
						String snoT="";
						for (int j = 0; j < stations.length; j++) {
							String sql ="select no  from port_station where name = '"+stations[j]+"' and orgno ='"+OrgNo+"'";
							String stationNo = DBAccess.RunSQLReturnString(sql);
							sno += stationNo + ",";
							snoT += stations[j] + ",";
						}
						demp.setStationNoT(snoT.equals("") ? snoT : snoT.substring(0, snoT.length() - 1));
						demp.setStationNo(sno.equals("") ? sno : sno.substring(0, sno.length() - 1));
						demp.Insert();
						//部门人员岗位表数据插入
						if(!DataType.IsNullOrEmpty(sno)){
							for (int g = 0; g < sno.split(",").length; g++) {
								String sindex = sno.split(",")[g];
								if (sindex == null || sindex.equals("")) {
									continue;
								}
								DeptEmpStation demps = new DeptEmpStation();
								demps.setMyPK(str + "_" + em.getNo() + "_" + sindex);
								demps.setDeptNo(str);
								demps.setOrgNo(demp.getOrgNo());
								demps.setStationNo(sindex);
								demps.setEmpNo(em.getNo());
								demps.Insert();
							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return "info@导入失败:" + e.getMessage();
		}
		//删除临时文件
		tempFile.delete();
		return "执行完成";
	}
}
