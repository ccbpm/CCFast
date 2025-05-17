package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.DirectoryPageBase;
import bp.en.Map;
import bp.sys.*;
import bp.tools.FileAccess;
import bp.web.*;
import bp.port.*;
import bp.en.*;
import bp.wf.rpt.*;
import bp.difference.*;
import bp.wf.*;
import java.util.*;
import java.io.*;
import java.math.*;

/** 
 页面功能实体
*/
public class WF_RptDfine extends bp.difference.handler.DirectoryPageBase
{

	///#region 属性.
	/** 
	 查询类型
	*/
	public final String getSearchType() throws Exception {
		String val = this.GetRequestVal("SearchType");

		if (val == null || val.equals(""))
		{
			val = this.GetRequestVal("GroupType");
		}
		return val;
	}



	/** 
	 分析类型
	*/
	public final String getGroupType() {
		return this.GetRequestVal("GroupType");
	}

	public final boolean isContainsNDYF() {
		return this.GetRequestValBoolen("IsContainsNDYF");
	}

	/** 
	 部门编号
	*/
	public final String getFK_Dept()  {
		String str = this.GetRequestVal("FK_Dept");
		if (str == null || str.equals("") || str.equals("null"))
		{
			return null;
		}
		return str;
	}
	public final void setFK_Dept(String value)throws Exception
	{String val = value;
		if (val.equals("all"))
		{
			return;
		}

		if (this.getFK_Dept() == null)
		{
			this.setFK_Dept(value);
			return;
		}
	}


		///#endregion 属性.

	/** 
	 构造函数
	*/
	public WF_RptDfine() throws Exception {
	}

	/** 
	 流程列表
	 
	 @return 
	*/
	public final String Flowlist_Init() throws Exception {
		DataSet ds = new DataSet();
		String sql = "SELECT No,Name,ParentNo FROM WF_FlowSort ORDER BY No, Idx";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Sort";
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).setColumnName("No");
			dt.Columns.get(1).setColumnName("Name");
			dt.Columns.get(2).setColumnName("ParentNo");
		}
		ds.Tables.add(dt);

		sql = "SELECT No,Name,FK_FlowSort FROM WF_Flow WHERE IsCanStart=1 ORDER BY FK_FlowSort, Idx";
		dt = DBAccess.RunSQLReturnTable(sql);
		dt.TableName = "Flows";

		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).setColumnName("No");
			dt.Columns.get(1).setColumnName("Name");
			dt.Columns.get(2).setColumnName("FK_FlowSort");
		}
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 新的数据源
	 
	 @return 
	*/
	public final String Flowlist_Init2022() throws Exception {
		String sql = "SELECT A.FK_Flow AS FlowNo, A.FlowName, B.Name AS SortName,COUNT(*) as Num FROM WF_GenerWorkFlow A, WF_FlowSort B";
		sql += " WHERE A.WFState>1 AND (A.Emps LIKE '%" + WebUser.getNo() + "%'  OR A.Starter='" + WebUser.getNo() + "') AND A.FK_FlowSort=B.No ";
		sql += " GROUP BY A.FK_Flow, A.FlowName, B.Name, B.Idx ORDER BY B.Idx ";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);

	}


		///#region 功能列表
	/** 
	 功能列表
	 
	 @return 
	*/
	public final String Default_Init() throws Exception {
		Hashtable ht = new Hashtable();
		ht.put("My", "我发起的流程");
		ht.put("MyJoin", "我审批的流程");

		RptDfine rd = new RptDfine(this.getFK_Flow());
		Paras ps = new Paras();


			///#region 增加本部门发起流程的查询.
		if (rd.getMyDeptRole() == 0)
		{
			/*如果仅仅部门领导可以查看: 检查当前人是否是部门领导人.*/
			if (DBAccess.IsExitsTableCol("Port_Dept", "Leader") == true)
			{
				ps.SQL = "SELECT Leader FROM Port_Dept WHERE No=" + SystemConfig.getAppCenterDBVarStr() + "No";
				ps.Add("No", WebUser.getDeptNo(), false);
				//String sql = "SELECT Leader FROM Port_Dept WHERE No='" + bp.web.WebUser.getDeptNo() + "'";
				String strs = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (strs != null && strs.contains(WebUser.getNo()) == true)
				{
					ht.put("MyDept", "我本部门发起的流程");
				}
			}
		}

		if (rd.getMyDeptRole() == 1)
		{
			/*如果部门下所有的人都可以查看: */
			ht.put("MyDept", "我本部门发起的流程");
		}

		if (rd.getMyDeptRole() == 2)
		{
			/*如果部门下所有的人都可以查看: */
			ht.put("MyDept", "我本部门发起的流程");
		}

			///#endregion 增加本部门发起流程的查询.

		Flow fl = new Flow(this.getFK_Flow());
		ht.put("FlowName", fl.getName());

		String advEmps = String.valueOf(SystemConfig.getAppSettings().get("AdvEmps"));
		if (advEmps != null && advEmps.contains(WebUser.getNo()) == true)
		{
			ht.put("Adminer", "高级查询");
		}
		else
		{
			String data = fl.GetParaString("AdvSearchRight");
			data = "," + data + ",";
			if (data.contains(WebUser.getNo() + ",") == true)
			{
				ht.put("Adminer", "高级查询");
			}
		}

		return bp.tools.Json.ToJsonEntitiesNoNameMode(ht);
	}

		///#endregion


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod() throws Exception {
		switch (this.getDoType())
		{
			case "DtlFieldUp": //字段上移
				return "执行成功.";
			default:
				break;
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到. @RowURL:" + ContextHolderUtils.getRequest().getRequestURI());
	}

		///#endregion 执行父类的重写方法.


		///#region MyStartFlow.htm 我发起的流程.

	/** 
	 获取单流程查询条件
	 
	 @return 
	*/
	public final String FlowSearch_InitToolBar() throws Exception {
		if (DataType.IsNullOrEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		DataSet ds = new DataSet();

		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();


			///#region 用户查询条件信息 不存在注册
		UserRegedit ur = new UserRegedit();
		ur.setAutoMyPK(false);
		String mypk = WebUser.getNo() + rptNo + "_SearchAttrs";
		ur.SetValByKey("MyPK", mypk);
		String selectFields = "," + GERptAttr.Title + "," + GERptAttr.FlowStarter + "," + GERptAttr.FlowStartRDT + "," + GERptAttr.FK_Dept + "," + GERptAttr.WFState + ",";
		if (ur.RetrieveFromDBSources() == 0)
		{
			//  ur.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs";
			ur.SetValByKey("MyPK", mypk);
			ur.SetValByKey("FK_Emp", WebUser.getNo());
			ur.SetValByKey("CfgKey", rptNo + "_SearchAttrs");
			ur.SetPara("SelectFields", selectFields);
			ur.Insert();
		}
		if (DataType.IsNullOrEmpty(ur.GetParaString("SelectFields")) == true)
		{
			ur.SetPara("SelectFields", selectFields);
			ur.Update();
		}

			///#endregion 用户查询条件

		//报表信息
		MapData md = new MapData();
		md.setNo(rptNo);
		if (md.RetrieveFromDBSources() == 0)
		{
			/*如果没有找到，就让其重置一下.*/
			RptDfine rd = new RptDfine(this.getFK_Flow());
			rd.DoReset(this.getSearchType());
			md.RetrieveFromDBSources();
		}


			///#region  关键字 时间查询条件
		md.SetPara("DTSearchWay", md.getDTSearchWay().getValue());
		md.SetPara("DTSearchKey", md.getDTSearchKey());
		md.SetPara("IsSearchKey", md.getItIsSearchKey());
		md.SetPara("StringSearchKeys", md.GetParaString("StringSearchKeys"));
		md.SetPara("SearchKey", ur.getSearchKey());

		if (md.getDTSearchWay ()!= DTSearchWay.None)
		{
			MapAttr mapAttr = new MapAttr(rptNo, md.getDTSearchKey());
			md.SetPara("DTSearchLabel", mapAttr.getName() );

			if (md.getDTSearchWay ()== DTSearchWay.ByDate)
			{
				md.SetPara("DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
			else
			{
				md.SetPara("DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
		}

			///#endregion 关键字时间查询条件

		ds.Tables.add(md.ToDataTableField("Sys_MapData"));

		//判断是否含有导出至模板的模板文件，如果有，则显示导出至模板按钮RptExportToTmp
//		if ((new File(tmpDir)).isDirectory())
//		{
//			if (Directory.GetFiles(tmpDir, "*.xls*").length() > 0)
//			{
//				md.SetPara("RptExportToTmp", "1");
//			}
//		}
		String tmpDir = SystemConfig.getPathOfDataUser() + "@TempleteExpEns" + rptNo;
		File file = new File(tmpDir);
		if (file.exists() == true)
		{
			File[] fileList = file.listFiles();
			for (File f:fileList){
				if (f.isFile() && (f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx"))){
					md.SetPara("RptExportToTmp", "1");
				}
			}
		}

			///#region 外键枚举的查询条件
		MapAttrs attrs = new MapAttrs(rptNo);
		DataTable dt = new DataTable();
		dt.Columns.Add("Field");
		dt.Columns.Add("Name");
		dt.Columns.Add("Width");
		dt.Columns.Add("UIContralType");
		dt.TableName = "Attrs";

		//String[] ctrls = md.RptSearchKeys.split("[*]", -1);
		String[] ctrls = md.getRptSearchKeys().split("\\*");

		MapAttr attr;
		for (String ctrl : ctrls)
		{
			//增加判断，如果URL中有传参，则不进行此SearchAttr的过滤条件显示context.Request.QueryString[ctrl]
			if (DataType.IsNullOrEmpty(ctrl) || !DataType.IsNullOrEmpty(this.GetRequestVal(ctrl)))
			{
				continue;
			}

			Object tempVar = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ctrl);
			attr = tempVar instanceof MapAttr ? (MapAttr)tempVar : null;
			if (attr == null)
			{
				continue;
			}
			DataRow dr = dt.NewRow();
			//如果外键是FK_NY的时候解析成时间类型
			if (attr.getKeyOfEn().equals("FK_NY") == true)
			{

				dr.setValue("Field", attr.getKeyOfEn());
				dr.setValue("Name", attr.getHisAttr().getDesc());
				dr.setValue("Width", attr.getUIWidth()); //下拉框显示的宽度.
				dr.setValue("UIContralType", "");
				dt.Rows.add(dr);
				continue;
			}
			dr.setValue("Field", attr.getKeyOfEn());
			dr.setValue("Name", attr.getHisAttr().getDesc());
			dr.setValue("Width", attr.getUIWidth()); //下拉框显示的宽度.
			dr.setValue("UIContralType", attr.getHisAttr().getUIContralType());
			dt.Rows.add(dr);

			Attr ar = attr.getHisAttr();
			//判读该字段是否是枚举
			if (ar.getItIsEnum() == true)
			{
				SysEnums ses = new SysEnums(attr.getUIBindKey());
				DataTable dtEnum = ses.ToDataTableField("dt");
				dtEnum.TableName = attr.getKeyOfEn();
				ds.Tables.add(dtEnum);
				continue;
			}
			//判断是否是外键
			if (ar.getItIsFK() == true)
			{
				Entities ensFK = ar.getHisFKEns();
				ensFK.RetrieveAll();

				DataTable dtEn = ensFK.ToDataTableField("dt");
				dtEn.TableName = ar.getKey();
				ds.Tables.add(dtEn);
				continue;
			}

			if (DataType.IsNullOrEmpty(ar.getUIBindKey()) == false && ds.contains(ar.getKey()) == false)
			{
				//@lizhen.
				EntitiesNoName ens = attr.getHisEntitiesNoName();
				ds.Tables.add(ens.ToDataTableField(ar.getKey()));
			}
		}

		ds.Tables.add(dt);


			///#endregion 外键枚举的查询条件

		return bp.tools.Json.ToJson(ds);

	}
	/** 
	 单流程查询显示的列
	 
	 @return 
	*/
	public final String FlowSearch_MapAttrs() throws Exception {

		DataSet ds = new DataSet();
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();


		//查询出单流程的所有字段
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);



		//默认显示的系统字段 标题、创建人、创建时间、部门、状态.
		MapAttrs mattrsOfSystem = new MapAttrs();
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.Title));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FlowStarter));
		//  mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FlowStartRDT));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FK_Dept));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.WFState));
		mattrsOfSystem.AddEntity(attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, GERptAttr.FlowEmps));

		ds.Tables.add(mattrsOfSystem.ToDataTableField("Sys_MapAttrOfSystem"));

		attrs.RemoveEn(rptNo + "_OID");
		attrs.RemoveEn(rptNo + "_Title");
		attrs.RemoveEn(rptNo + "_FlowStarter");
		attrs.RemoveEn(rptNo + "_FK_Dept");
		attrs.RemoveEn(rptNo + "_WFState");
		attrs.RemoveEn(rptNo + "_WFSta");
		attrs.RemoveEn(rptNo + "_FlowEmps");

		ds.Tables.add(attrs.ToDataTableField("Sys_MapAttr"));




		//系统字段字符串
		/* String sysFields = BP.WF.Glo.FlowFields;
		 DataTable dt = new DataTable();
		 dt.Columns.Add("Field");
		 dt.TableName = "Sys_Fields";
		 DataRow dr = dt.NewRow();
		 dr["Field"] = sysFields;
		 dt.Rows.add(dr);
		 ds.Tables.add(dt);*/

		return bp.tools.Json.ToJson(ds);
	}

	/** 
	 单流程查询数据集合
	 
	 @return 
	*/
	public final String FlowSearch_Data() throws Exception {
		//表单编号
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();

		//当前用户查询信息表
		UserRegedit ur = new UserRegedit(WebUser.getNo(), rptNo + "_SearchAttrs");

		//表单属性
		MapData mapData = new MapData(rptNo);

		//流程表单对应的所有字段
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);

		//流程表单对应的流程数据
		GEEntitys ens = new GEEntitys(rptNo);
		QueryObject qo = new QueryObject(ens);

		switch (this.getSearchType())
		{
			case "My": //我发起的.
				qo.AddWhere(GERptAttr.FlowStarter, WebUser.getNo());
				break;
			case "MyDept": //我部门发起的.
				if (mapData.GetParaBoolen("IsSearchNextLeavel") == false)
				{
					//只查本部门及兼职部门
					qo.AddWhereInSQL(GERptAttr.FK_Dept, "SELECT FK_Dept From Port_DeptEmp Where FK_Emp='" + WebUser.getNo() + "'");
				}
				else
				{
					//查本部门及子级
					String sql = "SELECT FK_Dept From Port_DeptEmp Where FK_Emp='" + WebUser.getNo() + "'";
					sql += " UNION ";
					sql += "SELECT No AS FK_Dept From Port_Dept Where ParentNo IN(SELECT FK_Dept From Port_DeptEmp Where FK_Emp='" + WebUser.getNo() + "')";
					qo.AddWhereInSQL(GERptAttr.FK_Dept, sql);

				}
				break;
			case "MyJoin": //我参与的.
				qo.AddWhere(GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
				break;
			case "Adminer":
				break;
			default:
				return "err@" + this.getSearchType() + "标记错误.";
		}
		//需要增加状态的查询
		qo.addAnd();
		qo.AddWhere(GERptAttr.WFState, ">", 1);
			///#region 关键字查询
		String searchKey = ""; //关键字查询
		if (mapData.getItIsSearchKey())
		{
			searchKey = ur.getSearchKey();
		}

		if (mapData.getItIsSearchKey() && DataType.IsNullOrEmpty(searchKey) == false && searchKey.length() >= 1)
		{
			int i = 0;

			for (MapAttr myattr : attrs.ToJavaList())
			{
				Attr attr = myattr.getHisAttr();
				switch (attr.getMyFieldType())
				{
					case Enum:
					case FK:
					case PKFK:
						continue;
					default:
						break;
				}

				if (attr.getMyDataType() != DataType.AppString)
				{
					continue;
				}

				if (attr.getMyFieldType() == FieldType.RefText)
				{
					continue;
				}

				if (attr.getKey().equals("FK_Dept"))
				{
					continue;
				}

				i++;

				if (i == 1)
				{
					qo.addLeftBracket();
					if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
					{
						qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
					}
					else
					{
						qo.AddWhere(attr.getKey(), " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
					}
					continue;
				}

				qo.addOr();

				if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
				{
					qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
				}
				else
				{
					qo.AddWhere(attr.getKey(), " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
				}
			}

			qo.getMyParas().Add("SKey", searchKey, false);
			qo.addRightBracket();
		}
		else if (DataType.IsNullOrEmpty(mapData.GetParaString("StringSearchKeys")) == false)
		{
			String field = ""; //字段名
			String fieldValue = ""; //字段值
			int idx = 0;

			//获取查询的字段
			String[] searchFields = mapData.GetParaString("StringSearchKeys").split("[*]", -1);
			for (String str : searchFields)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				//字段名
				String[] items = str.split("[,]", -1);
				if (items.length == 2 && DataType.IsNullOrEmpty(items[0]) == true)
				{
					continue;
				}
				field = items[0];
				//字段名对应的字段值
				fieldValue = ur.GetParaString(field);
				if (DataType.IsNullOrEmpty(fieldValue) == true)
				{
					continue;
				}
				idx++;
				if (idx == 1)
				{
					/* 第一次进来。 */
					qo.addLeftBracket();
					if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
					{
						qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
					}
					else
					{
						qo.AddWhere(field, " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
					}
					qo.getMyParas().Add(field, fieldValue, false);
					continue;
				}
				qo.addAnd();

				if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
				{
					qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
				}
				else
				{
					qo.AddWhere(field, " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
				}
				qo.getMyParas().Add(field, fieldValue, false);


			}
			if (idx != 0)
			{
				qo.addRightBracket();
			}
		}


			///#endregion 关键字查询


			///#region Url传参条件
		String val = "";
		ArrayList<String> keys = new ArrayList<String>();
		for (MapAttr attr : attrs.ToJavaList())
		{
			if (DataType.IsNullOrEmpty(this.GetRequestVal(attr.getKeyOfEn())))
			{
				continue;
			}
			qo.addAnd();
			qo.addLeftBracket();
			val = this.GetRequestVal(attr.getKeyOfEn());

			switch (attr.getMyDataType())
			{
				case DataType.AppBoolean:
					qo.AddWhere(attr.getKeyOfEn(), Boolean.valueOf(val));
					break;
				case DataType.AppDate:
				case DataType.AppDateTime:
				case DataType.AppString:
					qo.AddWhere(attr.getKeyOfEn(), val);
					break;
				case DataType.AppDouble:
				case DataType.AppFloat:
				case DataType.AppMoney:
					qo.AddWhere(attr.getKeyOfEn(), Double.parseDouble(val));
					break;
				case DataType.AppInt:
					qo.AddWhere(attr.getKeyOfEn(), Integer.parseInt(val));
					break;
				default:
					break;
			}

			qo.addRightBracket();

			if (keys.contains(attr.getKeyOfEn()) == false)
			{
				keys.add(attr.getKeyOfEn());
			}
		}

			///#endregion


			///#region 过滤条件
		HashMap<String, String> kvs = ur.GetVals();
		for (MapAttr attr1 : attrs.ToJavaList())
		{
			Attr attr = attr1.getHisAttr();
			//此处做判断，如果在URL中已经传了参数，则不算SearchAttrs中的设置
			if (keys.contains(attr.getKey()))
			{
				continue;
			}

			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			if (attr.getKey().equals("FK_NY"))
			{
				String fieldValue = ur.GetParaString(attr.getKey());
				if (DataType.IsNullOrEmpty(fieldValue) == true)
				{
					continue;
				}
				qo.addAnd();
				qo.AddWhere(attr.getKey(), " = ", fieldValue);
				continue;
			}

			String selectVal = "";
			String cid = "";

			switch (attr.getUIContralType())
			{

				case DDL:
				case RadioBtn:
					cid = "DDL_" + attr.getKey();

					if (kvs.containsKey(cid) == false || DataType.IsNullOrEmpty(kvs.get(cid)))
					{
						continue;
					}

					selectVal = kvs.get(cid);

					if (selectVal.equals("all") || selectVal.equals("-1"))
					{
						continue;
					}

					qo.addAnd();

					qo.addLeftBracket();


					String deptName = bp.sys.base.Glo.DealClassEntityName("bp.port.Depts");

					if (attr.getUIBindKey().equals(deptName) == true) //判断特殊情况。
					{
						qo.AddWhere(attr.getKey(), " LIKE ", selectVal + "%");
					}
					else
					{
						qo.AddWhere(attr.getKey(), selectVal);
					}

					qo.addRightBracket();
					break;

				default:
					break;
			}
		}

			///#endregion


			///#region 日期处理
		if (mapData.getDTSearchWay() != DTSearchWay.None)
		{
			String dtKey = mapData.getDTSearchKey();
			String dtFrom = ur.GetValStringByKey(UserRegeditAttr.DTFrom).trim();
			String dtTo = ur.GetValStringByKey(UserRegeditAttr.DTTo).trim();

			if (DataType.IsNullOrEmpty(dtFrom) == true)
			{
				if (mapData.getDTSearchWay() == DTSearchWay.ByDate)
				{
					dtFrom = "1900-01-01";
				}
				else
				{
					dtFrom = "1900-01-01 00:00";
				}
			}

			if (DataType.IsNullOrEmpty(dtTo) == true)
			{
				if (mapData.getDTSearchWay() == DTSearchWay.ByDate)
				{
					dtTo = "2999-01-01";
				}
				else
				{
					dtTo = "2999-12-31 23:59";
				}
			}

			if (mapData.getDTSearchWay() == DTSearchWay.ByDate)
			{

				qo.addAnd();
				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + "'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + "'");
				qo.addRightBracket();
			}

			if (mapData.getDTSearchWay() == DTSearchWay.ByDateTime)
			{

				qo.addAnd();
				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + " 00:00'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + " 23:59'");
				qo.addRightBracket();
			}
		}

			///#endregion 日期处理

		qo.AddWhere(" AND  WFState > 1 ");
		qo.AddWhere(" AND FID = 0 ");
		if (DataType.IsNullOrEmpty(ur.getOrderBy()) == false)
		{
			if (ur.getOrderWay().toUpperCase().equals("DESC") == true)
			{
				qo.addOrderByDesc(ur.getOrderBy());
			}
			else
			{
				qo.addOrderBy(ur.getOrderBy());
			}
		}
		ur.SetPara("Count", qo.GetCount());
		ur.Update();
		qo.DoQuery("OID", this.getPageSize(), this.getPageIdx());

		return bp.tools.Json.ToJson(ens.ToDataTableField("FlowSearch_Data"));

	}


	/** 
	 导出
	 
	 @return 
	*/
	public final String FlowSearch_Exp() throws Exception {
		String vals = this.GetRequestVal("vals");
		String searchKey = GetRequestVal("key");
		String dtFrom = GetRequestVal("dtFrom");
		String dtTo = GetRequestVal("dtTo");
		String mvals = GetRequestVal("mvals");


		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();
		UserRegedit ur = new UserRegedit();
		//   ur.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs";
		ur.SetValByKey("MyPK", WebUser.getNo() + rptNo + "_SearchAttrs");

		ur.RetrieveFromDBSources();
		ur.SetValByKey("SearchKey", searchKey);
		ur.SetValByKey("DTFrom_Data", dtFrom);
		ur.SetValByKey("DTTo_Data", dtTo);

		ur.SetValByKey("Vals", vals);
		ur.SetValByKey("MVals", mvals);
		ur.Update();


		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);

		DataSet ds = new DataSet();
		MapData md = new MapData(rptNo);
		//MapAttrs attrs = new MapAttrs(rptNo);
		GEEntitys ges = new GEEntitys(rptNo);
		QueryObject qo = new QueryObject(ges);

		String title = "数据导出";
		switch (this.getSearchType())
		{
			case "My": //我发起的.
				title = "我发起的流程";
				qo.AddWhere(GERptAttr.FlowStarter, WebUser.getNo());
				break;
			case "MyDept": //我部门发起的.
				title = "我部门发起的流程";
				qo.AddWhere(GERptAttr.FK_Dept, WebUser.getDeptNo());
				break;
			case "MyJoin": //我参与的.
				title = "我参与的流程";
				qo.AddWhere(GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
				break;
			case "Adminer":
				break;
			default:
				return "err@" + this.getSearchType() + "标记错误.";
		}


		qo = InitQueryObject(qo, md, ges.getNewEntity().getEnMap().getAttrs(), attrs, ur);
		qo.AddWhere(" AND  WFState > 1 "); //排除空白，草稿数据.
		qo.addOrderByDesc("OID");
		Attrs attrsa = new Attrs();
		for (MapAttr attr : attrs.ToJavaList())
		{
			attrsa.add(attr.getHisAttr());
		}

		String filePath = bp.tools.ExportExcelUtil.ExportDGToExcel(qo.DoQueryToTable(), ges.getNewEntity(), title, attrsa);


		return filePath;
	}
	/** 
	 流程分組分析 1.获取查询条件 2.获取分组的枚举或者外键值 3.获取分析的信息列表进行求和、求平均
	 
	 @return 
	*/
	public final String FlowGroup_Init() throws Exception {
		if (DataType.IsNullOrEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		String fcid = "";
		DataSet ds = new DataSet();
		HashMap<String, String> vals = null;
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getGroupType();

		//报表信息，包含是否显示关键字查询RptIsSearchKey，过滤条件枚举/下拉字段RptSearchKeys，时间段查询方式RptDTSearchWay，时间字段RptDTSearchKey
		MapData md = new MapData();
		md.setNo(rptNo);
		if (md.RetrieveFromDBSources() == 0)
		{
			/*如果没有找到，就让其重置一下.*/
			bp.wf.rpt.RptDfine rd = new RptDfine(this.getFK_Flow());

			if (this.getGroupType().equals("My"))
			{
				rd.DoReset(this.getGroupType());
			}

			if (this.getGroupType().equals("MyJoin"))
			{
				rd.DoReset(this.getGroupType());
			}

			if (this.getGroupType().equals("MyDept"))
			{
				rd.DoReset(this.getGroupType());
			}

			if (this.getGroupType().equals("Adminer"))
			{
				rd.DoReset(this.getGroupType());
			}

			md.RetrieveFromDBSources();
		}

		MapAttr ar = null;

		//查询条件的信息表
		String cfgfix = "_SearchAttrs";
		UserRegedit ur = new UserRegedit();
		ur.setAutoMyPK(false);
		ur.setMyPK(WebUser.getNo() + rptNo + cfgfix);

		if (ur.RetrieveFromDBSources() == 0)
		{
			ur.setMyPK(WebUser.getNo() + rptNo + cfgfix);
			ur.setFK_Emp(WebUser.getNo());
			ur.setCfgKey(rptNo + cfgfix);

			ur.Insert();
		}

		//分组条件存储的信息表
		cfgfix = "_GroupAttrs";
		UserRegedit groupUr = new UserRegedit();
		groupUr.setAutoMyPK(false);
		groupUr.setMyPK(WebUser.getNo() + rptNo + cfgfix);

		if (groupUr.RetrieveFromDBSources() == 0)
		{
			groupUr.setMyPK(WebUser.getNo() + rptNo + cfgfix);
			groupUr.setFK_Emp(WebUser.getNo());
			groupUr.setCfgKey(rptNo + cfgfix);

			groupUr.Insert();
		}



		vals = ur.GetVals();
		md.SetPara("RptDTSearchWay", md.getDTSearchWay().getValue());
		md.SetPara("RptDTSearchKey", md.getDTSearchKey());
		md.SetPara("IsSearchKey", md.getItIsSearchKey());
		md.SetPara("T_SearchKey", ur.getSearchKey());

		if (md.getDTSearchWay() != DTSearchWay.None)
		{
			ar = new MapAttr(rptNo, md.getDTSearchKey());
			md.SetPara("T_DateLabel", ar.getName());

			if (md.getDTSearchWay() == DTSearchWay.ByDate)
			{
				md.SetPara("T_DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("T_DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
			else
			{
				md.SetPara("T_DTFrom", ur.GetValStringByKey(UserRegeditAttr.DTFrom));
				md.SetPara("T_DTTo", ur.GetValStringByKey(UserRegeditAttr.DTTo));
			}
		}

		//判断是否含有导出至模板的模板文件，如果有，则显示导出至模板按钮RptExportToTmp
		String tmpDir = SystemConfig.getPathOfDataUser() + "TempleteExpEns/" + rptNo;
		if ((new File(tmpDir)).isDirectory())
		{

			List resultList = new ArrayList();
			FileAccess.findFiles(tmpDir, "*.xls*", resultList);
			if (resultList.size() > 0)
			{
				md.SetPara("T_RptExportToTmp", "1");
			}
		}


		/////显示的内容
		DataRow row = null;
		DataTable dt = new DataTable("Group_MapAttr");
		dt.Columns.Add("Field", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Checked", String.class);


		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);

		for (MapAttr attr : attrs.ToJavaList())
		{
			if (attr.getUIContralType()== UIContralType.DDL)
			{
				DataRow dr = dt.NewRow();
				dr.setValue("Field", attr.getKeyOfEn());
				dr.setValue("Name", attr.getHisAttr().getDesc());

				// 根据状态 设置信息.
				if (groupUr.getVals().indexOf(attr.getKeyOfEn()) != -1)
				{
					dr.setValue("Checked", "true");
				}

				if (groupUr.getVals().indexOf(attr.getKeyOfEn()) != -1)
				{
					dr.setValue("Checked", "true");
				}

				dt.Rows.add(dr);
			}
		}
		ds.Tables.add(dt);

		///


		/////分析的内容
		dt = new DataTable("Analysis_MapAttr");
		dt.Columns.Add("Field", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Checked", String.class);

		//如果不存在分析项手动添加一个分析项
		DataRow dtr = dt.NewRow();
		dtr.setValue("Field", "Group_Number");
		dtr.setValue("Name", "数量");
		dtr.setValue("Checked", "true");
		dt.Rows.add(dtr);

		DataTable ddlDt = new DataTable();
		ddlDt.TableName = "Group_Number";
		ddlDt.Columns.Add("No");
		ddlDt.Columns.Add("Name");
		ddlDt.Columns.Add("Selected");
		DataRow ddlDr = ddlDt.NewRow();
		ddlDr.setValue("No", "SUM");
		ddlDr.setValue("Name", "求和");
		ddlDr.setValue("Selected", "true");
		ddlDt.Rows.add(ddlDr);
		ds.Tables.add(ddlDt);


		for (MapAttr attr : attrs.ToJavaList())
		{
			if (attr.getItIsPK() || attr.getItIsNum() == false)
			{
				continue;
			}
			if (attr.getUIContralType()== UIContralType.TB == false)
			{
				continue;
			}
			if (attr.getUIVisible()== false)
			{
				continue;
			}
			if (attr.getHisAttr().getMyFieldType() == FieldType.FK)
			{
				continue;
			}
			if (attr.getHisAttr().getMyFieldType() == FieldType.Enum)
			{
				continue;
			}
			if (attr.getKeyOfEn().equals("OID") || attr.getKeyOfEn().equals("WorkID") || attr.getKeyOfEn().equals("MID"))
			{
				continue;
			}

			dtr = dt.NewRow();
			dtr.setValue("Field", attr.getKeyOfEn());
			dtr.setValue("Name", attr.getHisAttr().getDesc());


			// 根据状态 设置信息.
			if (groupUr.getVals().indexOf(attr.getKeyOfEn()) != -1)
			{
				dtr.setValue("Checked", "true");
			}
			dt.Rows.add(dtr);

			ddlDt = new DataTable();
			ddlDt.Columns.Add("No");
			ddlDt.Columns.Add("Name");
			ddlDt.Columns.Add("Selected");
			ddlDt.TableName = attr.getKeyOfEn();

			ddlDr = ddlDt.NewRow();
			ddlDr.setValue("No", "SUM");
			ddlDr.setValue("Name", "求和");
			if (groupUr.getVals().indexOf("@" + attr.getKeyOfEn() + "=SUM") != -1)
			{
				ddlDr.setValue("Selected", "true");
			}
			ddlDt.Rows.add(ddlDr);

			ddlDr = ddlDt.NewRow();
			ddlDr.setValue("No", "AVG");
			ddlDr.setValue("Name", "求平均");
			if (groupUr.getVals().indexOf("@" + attr.getKeyOfEn() + "=AVG") != -1)
			{
				ddlDr.setValue("Selected", "true");
			}
			ddlDt.Rows.add(ddlDr);

			if (this.isContainsNDYF())
			{
				ddlDr = ddlDt.NewRow();
				ddlDr.setValue("No", "AMOUNT");
				ddlDr.setValue("Name", "求累计");
				if (groupUr.getVals().indexOf("@" + attr.getKeyOfEn() + "=AMOUNT") != -1)
				{
					ddlDr.setValue("Selected", "true");
				}
				ddlDt.Rows.add(ddlDr);
			}

			ds.Tables.add(ddlDt);

		}
		ds.Tables.add(dt);

		///


		/////增加枚举/外键字段信息
		attrs = new MapAttrs(rptNo);
		dt = new DataTable("FilterCtrls");
		dt.Columns.Add("Field", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Type", String.class);
		dt.Columns.Add("DataType", Integer.class);
		dt.Columns.Add("DefaultValue", String.class);
		dt.Columns.Add("ValueField", String.class);
		dt.Columns.Add("TextField", String.class);
		dt.Columns.Add("ParentField", String.class);
		dt.Columns.Add("W", String.class);
		String[] ctrls = md.getRptSearchKeys().split("[*]", -1);
		DataTable dtNoName = null;

		for (String ctrl : ctrls)
		{
			//增加判断，如果URL中有传参，则不进行此SearchAttr的过滤条件显示context.Request.QueryString[ctrl]
			if (DataType.IsNullOrEmpty(ctrl) || !DataType.IsNullOrEmpty(this.GetRequestVal(ctrl)))
			{
				continue;
			}

			Object tempVar = attrs.GetEntityByKey(MapAttrAttr.KeyOfEn, ctrl);
			ar = tempVar instanceof MapAttr ? (MapAttr)tempVar : null;
			if (ar == null)
			{
				continue;
			}

			row = dt.NewRow();
			row.setValue("Field", ctrl);
			row.setValue("Name", ar.getName());
			row.setValue("DataType", ar.getMyDataType());
			row.setValue("W", ar.getUIWidth()); //宽度.

			switch (ar.getUIContralType())
			{
				case DDL:
					row.setValue("Type", "combo");
					fcid = "DDL_" + ar.getKeyOfEn();
					if (vals.containsKey(fcid))
					{
						if (vals.get(fcid).equals("mvals"))
						{
							AtPara ap = new AtPara(ur.getMVals());
							row.setValue("DefaultValue", ap.GetValStrByKey(ar.getKeyOfEn()));
						}
						else
						{
							row.setValue("DefaultValue", vals.get(fcid));
						}
					}

					switch (ar.getLGType())
					{

						case FK:
							Entities ens = ar.getHisAttr().getHisFKEns();
							ens.RetrieveAll();
							EntitiesTree treeEns = ens instanceof EntitiesTree ? (EntitiesTree)ens : null;

							if (treeEns != null)
							{
								row.setValue("Type", "combotree");
								dtNoName = ens.ToDataTableField();
								dtNoName.TableName = ar.getKeyOfEn();
								ds.Tables.add(dtNoName);

								row.setValue("ValueField", "No");
								row.setValue("TextField", "Name");
								row.setValue("ParentField", "ParentNo");
							}
							else
							{
								EntitiesTree treeSimpEns = ens instanceof EntitiesTree ? (EntitiesTree)ens : null;

								if (treeSimpEns != null)
								{
									row.setValue("Type", "combotree");
									dtNoName = ens.ToDataTableField();
									dtNoName.TableName = ar.getKeyOfEn();
									ds.Tables.add(dtNoName);

									row.setValue("ValueField", "No");
									row.setValue("TextField", "Name");
									row.setValue("ParentField", "ParentNo");
								}
								else
								{
									dtNoName = GetNoNameDataTable(ar.getKeyOfEn());
									dtNoName.Rows.AddDatas("all", "全部");

									for (Entity en : ens)
									{
										dtNoName.Rows.AddDatas(en.GetValStringByKey(ar.getHisAttr().getUIRefKeyValue()), en.GetValStringByKey(ar.getHisAttr().getUIRefKeyText()));
									}

									ds.Tables.add(dtNoName);

									row.setValue("ValueField", "No");
									row.setValue("TextField", "Name");
								}
							}
							break;

						case Enum:
							dtNoName = GetNoNameDataTable(ar.getKeyOfEn());
							dtNoName.Rows.AddDatas("all", "全部");

							SysEnums enums = new SysEnums(ar.getUIBindKey());

							for (SysEnum en : enums.ToJavaList())
							{
								dtNoName.Rows.AddDatas(String.valueOf(en.getIntKey()), en.getLab());
							}

							ds.Tables.add(dtNoName);

							row.setValue("ValueField", "No");
							row.setValue("TextField", "Name");
							break;
						default:
							break;
					}
					break;

				default:
					break;
			}

			dt.Rows.add(row);
		}

		ds.Tables.add(dt);
		ds.Tables.add(md.ToDataTableField("Sys_MapData"));

		///

		return bp.tools.Json.ToJson(ds);
	}

	public final String FlowGropu_Done() throws Exception {

		if (!this.getGroupType().equals("My") && this.getGroupType().equals("MyJoin") && this.getGroupType().equals("MyDept") && this.getGroupType().equals("Adminer"))
		{
			return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red>" + this.getGroupType() + "标记错误.</font></b>";
		}
		DataSet ds = new DataSet();
		ds = FlowGroupDoneSet();
		if (ds == null)
		{
			return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red> 您没有选择显示的内容</font></b>";
		}

		return bp.tools.Json.ToJson(ds);
	}
	/** 
	 
	 
	 
	 @return 
	*/
	public final DataSet FlowGroupDoneSet() throws Exception {
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getGroupType();
		DataSet ds = new DataSet();
		MapData md = new MapData(rptNo);
		MapAttrs attrs = new MapAttrs(rptNo);
		GEEntitys ges = new GEEntitys(rptNo);
		GEEntity en = new GEEntity(rptNo);
		Map map = en.getEnMapInTime();



		UserRegedit groupUr = new UserRegedit(WebUser.getNo(), rptNo + "_GroupAttrs");
		//分组的参数
		String groupVals = groupUr.getVals();
		//查询条件
		//分组
		String groupKey = "";
		Attrs AttrsOfNum = new Attrs(); //列
		String Condition = ""; //处理特殊字段的条件问题。

		//根据注册表信息获取里面的分组信息
		String StateNumKey = groupVals.substring(groupVals.indexOf("@StateNumKey") + 1);
		String[] statNumKeys = StateNumKey.split("[@]", -1);
		for (String ct : statNumKeys)
		{
			if (ct.split("[=]", -1).length != 2)
			{
				continue;
			}
			String[] paras = ct.split("[=]", -1);

			//判断paras[0]的类型
			int dataType = 2;
			if (paras[0].equals("Group_Number"))
			{
				AttrsOfNum.add(new Attr("Group_Number", "Group_Number", 1, DataType.AppInt, false, "数量"));
			}
			else
			{
				Attr attr = GetAttrByKey(attrs, paras[0]);
				AttrsOfNum.add(attr);
				dataType = attr.getMyDataType();
			}

			if (paras[0].equals("Group_Number"))
			{
				groupKey += " count(*) \"" + paras[0] + "\",";
			}
			else
			{
				switch (paras[1])
				{
					case "SUM":
						if (dataType == 2)
						{
							groupKey += " SUM(" + paras[0] + ") \"" + paras[0] + "\",";
						}
						else
						{
							groupKey += " round ( SUM(" + paras[0] + "), 4) \"" + paras[0] + "\",";
						}
						break;
					case "AVG":
						groupKey += " round (AVG(" + paras[0] + "), 4)  \"" + paras[0] + "\",";
						break;
					case "AMOUNT":
						if (dataType == 2)
						{
							groupKey += " SUM(" + paras[0] + ") \"" + paras[0] + "\",";
						}
						else
						{
							groupKey += " round ( SUM(" + paras[0] + "), 4) \"" + paras[0] + "\",";
						}
						break;
					default:
						throw new RuntimeException("没有判断的情况.");
				}

			}

		}
		boolean isHaveLJ = false; // 是否有累计字段。
		if (StateNumKey.indexOf("AMOUNT@") != -1)
		{
			isHaveLJ = true;
		}

		if (groupKey.equals(""))
		{
			return null;
		}

		/* 如果包含累计数据，那它一定需要一个月份字段。业务逻辑错误。*/
		groupKey = groupKey.substring(0, groupKey.length() - 1);
		Paras ps = new Paras();
		// 生成 sql.
		String selectSQL = "SELECT ";
		String groupBy = " GROUP BY ";
		Attrs AttrsOfGroup = new Attrs();

		String SelectedGroupKey = groupVals.substring(0, groupVals.indexOf("@StateNumKey")); // 为保存操作状态的需要。
		if (!DataType.IsNullOrEmpty(SelectedGroupKey))
		{
			boolean isSelected = false;
			String[] SelectedGroupKeys = SelectedGroupKey.split("[@]", -1);
			for (String key : SelectedGroupKeys)
			{
				if (key.toString().contains("=") == true)
				{
					continue;
				}
				selectSQL += key + " \"" + key + "\",";
				groupBy += key + ",";
				// 加入组里面。
				AttrsOfGroup.Add(GetAttrByKey(attrs, key), false, false);

			}
		}

		String groupList = this.GetRequestVal("GroupList");
		if (!DataType.IsNullOrEmpty(SelectedGroupKey))
		{
			/* 如果是年月 分组， 并且如果内部有 累计属性，就强制选择。*/
			if (groupList.indexOf("FK_NY") != -1 && isHaveLJ)
			{
				selectSQL += "FK_NY,";
				groupBy += "FK_NY,";
				SelectedGroupKey += "@FK_NY";
				// 加入组里面。
				AttrsOfGroup.Add(GetAttrByKey(attrs, "FK_NY"), false, false);
			}
		}

		groupBy = groupBy.substring(0, groupBy.length() - 1);

		if (groupBy.equals(" GROUP BY"))
		{
			return null;
		}



		String orderByReq = this.GetRequestVal("OrderBy");

		String orderby = "";

		if (orderByReq != null && (selectSQL.contains(orderByReq) || groupKey.contains(orderByReq)))
		{
			orderby = " ORDER BY " + orderByReq;
			String orderWay = this.GetRequestVal("OrderWay");
			if (!DataType.IsNullOrEmpty(orderWay) && !orderWay.equals("Up"))
			{
				orderby += " DESC ";
			}
		}

		//查询语句
		QueryObject qo = new QueryObject(ges);

		switch (this.getGroupType())
		{
			case "My": //我发起的.
				qo.AddWhere(GERptAttr.FlowStarter, WebUser.getNo());
				break;
			case "MyDept": //我部门发起的.
				qo.AddWhere(GERptAttr.FK_Dept, WebUser.getDeptNo());
				break;
			case "MyJoin": //我参与的.
				qo.AddWhere(GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");
				break;
			case "Adminer":
				break;
			default:
				return null;
		}

		//查询注册信息表
		UserRegedit ur = new UserRegedit();
		ur.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
		ur.RetrieveFromDBSources();
		qo = InitQueryObject(qo, md, ges.getNewEntity().getEnMap().getAttrs(), attrs, ur);
		qo.AddWhere(" AND  WFState > 1 "); //排除空白，草稿数据.

		DataTable dt2 = qo.DoGroupQueryToTable(selectSQL + groupKey, groupBy, orderby);

		DataTable dt1 = dt2.clone();

		dt1.Columns.Add("IDX", Integer.class);


			///#region 对他进行分页面

		int myIdx = 0;
		for (DataRow dr : dt2.Rows)
		{
			myIdx++;
			DataRow mydr = dt1.NewRow();
			mydr.setValue("IDX", myIdx);
			for (DataColumn dc : dt2.Columns)
			{
				mydr.setValue(dc.ColumnName, dr.getValue(dc.ColumnName));
			}
			dt1.Rows.add(mydr);
		}

			///#endregion


			///#region 处理 Int 类型的分组列。
		DataTable dt = dt1.clone();
		dt.TableName = "GroupSearch";
		dt.Rows.clear();
		for (Attr attr : AttrsOfGroup)
		{
			dt.Columns.get(attr.getKey()).DataType = String.class;
		}
		for (DataRow dr : dt1.Rows)
		{
			dt.Rows.add(dr);
		}

			///#endregion

		// 处理这个物理表 , 如果有累计字段, 就扩展它的列。
		if (isHaveLJ)
		{
			// 首先扩充列.
			for (Attr attr : AttrsOfNum)
			{
				if (StateNumKey.indexOf(attr.getKey() + "=AMOUNT") == -1)
				{
					continue;
				}

				switch (attr.getMyDataType())
				{
					case DataType.AppInt:
						dt.Columns.Add(attr.getKey() + "Amount", Integer.class);
						break;
					default:
						dt.Columns.Add(attr.getKey() + "Amount", BigDecimal.class);
						break;
				}
			}

			String sql = "";
			String whereOFLJ = "";
			AtPara ap = new AtPara(ur.getVals());
			/** #region 获得查询数据.
			*/
			for (String str : ap.getHisHT().keySet())
			{
				Object val = ap.GetValStrByKey(str);
				if (val.equals("all"))
				{
					continue;
				}
				if (!str.equals("FK_NY"))
				{
					whereOFLJ += " " + str + " =" + SystemConfig.getAppCenterDBVarStr() + str + "   AND ";
				}

			}

			// 添加累计汇总数据.
			for (DataRow dr : dt.Rows)
			{
				for (Attr attr : AttrsOfNum)
				{
					if (StateNumKey.indexOf(attr.getKey() + "=AMOUNT") == -1)
					{
						continue;
					}

					//形成查询sql.
					if (whereOFLJ.length() > 0)
					{
						sql = "SELECT SUM(" + attr.getKey() + ") FROM " + ges.getNewEntity().getEnMap().getPhysicsTable() + whereOFLJ + " AND ";
					}
					else
					{
						sql = "SELECT SUM(" + attr.getKey() + ") FROM " + ges.getNewEntity().getEnMap().getPhysicsTable() + " WHERE ";
					}

					for (Attr attr1 : AttrsOfGroup)
					{
						switch (attr1.getKey())
						{
							case "FK_NY":
								sql += " FK_NY <= '" + dr.getValue("FK_NY") + "' AND FK_ND='" + dr.getValue("FK_NY").toString().substring(0, 4) + "' AND ";
								break;
							case "FK_Dept":
								sql += attr1.getKey() + "='" + dr.getValue(attr1.getKey()) + "' AND ";
								break;
							case "FK_SJ":
							case "FK_XJ":
								sql += attr1.getKey() + " LIKE '" + dr.getValue(attr1.getKey()) + "%' AND ";
								break;
							default:
								sql += attr1.getKey() + "='" + dr.getValue(attr1.getKey()) + "' AND ";
								break;
						}
					}

					sql = sql.substring(0, sql.length() - "AND ".length());
					if (attr.getMyDataType() == DataType.AppInt)
					{
						dr.setValue(attr.getKey() + "Amount", DBAccess.RunSQLReturnValInt(sql, 0));
					}
					else
					{
						dr.setValue(attr.getKey() + "Amount", DBAccess.RunSQLReturnValDecimal(sql, BigDecimal.valueOf(0), 2));
					}
				}
			}
		}

		// 为表扩充外键
		for (Attr attr : AttrsOfGroup)
		{
			dt.Columns.Add(attr.getKey() + "T", String.class);
		}
		for (Attr attr : AttrsOfGroup)
		{
			if (attr.getUIBindKey().indexOf(".") == -1)
			{
				/* 说明它是枚举类型 */
				SysEnums ses = new SysEnums(attr.getUIBindKey());
				for (DataRow dr : dt.Rows)
				{
					int val = 0;
					try
					{
						val = Integer.parseInt(dr.getValue(attr.getKey()).toString());
					}
					catch (java.lang.Exception e)
					{
						dr.setValue(attr.getKey() + "T", " ");
						continue;
					}

					for (SysEnum se : ses.ToJavaList())
					{
						if (se.getIntKey() == val)
						{
							dr.setValue(attr.getKey() + "T", se.getLab());
						}
					}
				}
				continue;
			}
			for (DataRow dr : dt.Rows)
			{
				Entity myen = attr.getHisFKEn();
				String val = dr.getValue(attr.getKey()).toString();
				myen.SetValByKey(attr.getUIRefKeyValue(), val);
				try
				{
					myen.Retrieve();
					dr.setValue(attr.getKey() + "T", myen.GetValStrByKey(attr.getUIRefKeyText()));
				}
				catch (java.lang.Exception e2)
				{
					if (val == null || val.length() <= 1)
					{
						dr.setValue(attr.getKey() + "T", val);
					}
					else if (val.substring(0, 2).equals("63"))
					{
						try
						{
							Dept Dept = new Dept(val);
							dr.setValue(attr.getKey() + "T", Dept.getName());
						}
						catch (java.lang.Exception e3)
						{
							dr.setValue(attr.getKey() + "T", val);
						}
					}
					else
					{
						dr.setValue(attr.getKey() + "T", val);
					}
				}
			}
		}
		ds.Tables.add(dt);
		ds.Tables.add(AttrsOfNum.ToMapAttrs().ToDataTableField("AttrsOfNum"));
		ds.Tables.add(AttrsOfGroup.ToMapAttrs().ToDataTableField("AttrsOfGroup"));


		return ds;
	}

	/** 
	 执行导出
	 
	 @return 
	*/
	public final String FlowGroup_Exp() throws Exception {
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getGroupType();
		String desc = "";

		if (this.getGroupType().equals("My"))
		{
			desc = "我发起的流程";
		}
		else if (this.getGroupType().equals("MyJoin"))
		{
			desc = "我审批的流程";
		}
		else if (this.getGroupType().equals("MyDept"))
		{
			desc = "本部门发起的流程";
		}
		else if (this.getGroupType().equals("Adminer"))
		{
			desc = "高级查询";
		}
		else
		{
			return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red>" + this.getGroupType() + "标记错误.</font></b>";
		}

		DataSet ds = new DataSet();
		ds = FlowGroupDoneSet();
		if (ds == null)
		{
			return "info@<img src='../Img/Pub/warning.gif' /><b><font color=red> 您没有选择显示的内容</font></b>";
		}

		//获取注册信息表
		UserRegedit ur = new UserRegedit(WebUser.getNo(), rptNo + "_GroupAttrs");

		String filePath = bp.tools.ExportExcelUtil.ExportDGToExcel(ds, desc, ur.getVals());

		return filePath;
	}

	/** 
	 初始化数据
	 
	 @return 
	*/
	public final String FlowContrastDtl_Init() throws Exception {
		if (DataType.IsNullOrEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		String pageSize = GetRequestVal("pageSize");
		String fcid = "";
		DataSet ds = new DataSet();
		HashMap<String, String> vals = null;
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();

		GEEntitys ges = new GEEntitys(rptNo);

		//属性集合.
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);
		DataRow row = null;
		DataTable dt = new DataTable("Sys_MapAttr");
		dt.Columns.Add("No", String.class);
		dt.Columns.Add("Name", String.class);
		dt.Columns.Add("Width", Integer.class);
		dt.Columns.Add("UIContralType", Integer.class);
		for (MapAttr attr : attrs.ToJavaList())
		{
			row = dt.NewRow();
			row.setValue("No", attr.getKeyOfEn());
			row.setValue("Name", attr.getName());
			row.setValue("Width", attr.getUIWidthInt());
			row.setValue("UIContralType", attr.getUIContralType().getValue());

			if (attr.getHisAttr().getItIsFKorEnum())
			{
				row.setValue("No", attr.getKeyOfEn() + "Text");
			}

			dt.Rows.add(row);
		}

		ds.Tables.add(dt);

		//查询结果
		QueryObject qo = new QueryObject(ges);
		Enumeration enu=getRequest().getParameterNames();
		boolean isExist = false;
		String newFK_Dept=this.getFK_Dept();
		while(enu.hasMoreElements()){
			isExist = false;
			String key=(String)enu.nextElement();
			if (key.equals("FK_Flow")|| key.equals("SearchType"))
			{
				continue;
			}

			if (key.equals("OID") || key.equals("MyPK"))
			{
				continue;
			}

			if (key.equals("FK_Dept"))
			{
				newFK_Dept = getRequest().getParameter(key);
				continue;
			}
			for (MapAttr attr : attrs.ToJavaList())
			{
				if (attr.getKeyOfEn().toUpperCase().equals(key.toUpperCase()))
				{
					isExist = true;
					break;
				}
			}

			if (isExist == false)
			{
				continue;
			}

			if (getRequest().getParameter(key).equals("mvals"))
			{
				//如果用户多项选择了，就要找到它的选择项目.

				UserRegedit sUr = new UserRegedit();
				sUr.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
				sUr.RetrieveFromDBSources();

				// 如果是多选值
				String cfgVal = sUr.getMVals();
				AtPara ap = new AtPara(cfgVal);
				String instr = ap.GetValStrByKey(key);
				String val = "";
				if (instr == null || instr.equals(""))
				{
					if (key.equals("FK_Dept") || key.equals("FK_Unit"))
					{
						if (key.equals("FK_Dept"))
						{
							val = WebUser.getDeptNo();
						}
					}
					else
					{
						continue;
					}
				}
				else
				{
					instr = instr.replace("..", ".");
					instr = instr.replace(".", "','");
					instr = instr.substring(2);
					instr = instr.substring(0, instr.length() - 2);
					qo.AddWhereIn(key, instr);
				}
			}
			else
			{
				qo.AddWhere(key, getRequest().getParameter(key));
			}
			qo.addAnd();
		}

		if (this.getFK_Dept() != null && (this.GetRequestVal("FK_Emp") == null || this.GetRequestVal("FK_Emp").equals("all")))
		{
			if (this.getFK_Dept().length() == 2)
			{
				qo.AddWhere("FK_Dept", " = ", "all");
				qo.addAnd();
			}
			else
			{
				if (this.getFK_Dept().length() == 8)
				{
					qo.AddWhere("FK_Dept", " = ", this.getFK_Dept());
				}
				else
				{
					qo.AddWhere("FK_Dept", " like ", this.getFK_Dept() + "%");
				}

				qo.addAnd();
			}
		}

		qo.AddHD();

		dt = qo.DoQueryToTable();
		dt.TableName = "Group_Dtls";
		ds.Tables.add(dt);

		return bp.tools.Json.ToJson(ds);
	}


	/** 
	 执行导出
	 
	 @return 
	*/
	public final String FlowGroupDtl_Exp() throws Exception {
		if (DataType.IsNullOrEmpty(this.getFK_Flow()))
		{
			return "err@参数FK_Flow不能为空";
		}

		String pageSize = GetRequestVal("pageSize");
		String fcid = "";
		HashMap<String, String> vals = null;
		String rptNo = "ND" + Integer.parseInt(this.getFK_Flow()) + "Rpt" + this.getSearchType();

		GEEntitys ges = new GEEntitys(rptNo);

		//属性集合.
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, rptNo, MapAttrAttr.Idx);


		//查询结果
		QueryObject qo = new QueryObject(ges);

		Enumeration enu=getRequest().getParameterNames();
		boolean isExist = false;
		String newFK_Dept=this.getFK_Dept();
		while(enu.hasMoreElements()){
			isExist = false;
			String key=(String)enu.nextElement();
			if (key.equals("FK_Flow")|| key.equals("SearchType"))
			{
				continue;
			}

			if (key.equals("OID") || key.equals("MyPK"))
			{
				continue;
			}

			if (key.equals("FK_Dept"))
			{
				newFK_Dept = getRequest().getParameter(key);
				continue;
			}
			for (MapAttr attr : attrs.ToJavaList())
			{
				if (attr.getKeyOfEn().toUpperCase().equals(key.toUpperCase()))
				{
					isExist = true;
					break;
				}
			}

			if (isExist == false)
			{
				continue;
			}

			if (getRequest().getParameter(key).equals("mvals"))
			{
				//如果用户多项选择了，就要找到它的选择项目.

				UserRegedit sUr = new UserRegedit();
				sUr.setMyPK(WebUser.getNo() + rptNo + "_SearchAttrs");
				sUr.RetrieveFromDBSources();

				// 如果是多选值
				String cfgVal = sUr.getMVals();
				AtPara ap = new AtPara(cfgVal);
				String instr = ap.GetValStrByKey(key);
				String val = "";
				if (instr == null || instr.equals(""))
				{
					if (key.equals("FK_Dept") || key.equals("FK_Unit"))
					{
						if (key.equals("FK_Dept"))
						{
							val = WebUser.getDeptNo();
						}
					}
					else
					{
						continue;
					}
				}
				else
				{
					instr = instr.replace("..", ".");
					instr = instr.replace(".", "','");
					instr = instr.substring(2);
					instr = instr.substring(0, instr.length() - 2);
					qo.AddWhereIn(key, instr);
				}
			}
			else
			{
				qo.AddWhere(key, getRequest().getParameter(key));
			}
			qo.addAnd();
		}

		if (this.getFK_Dept() != null && (this.GetRequestVal("FK_Emp") == null || this.GetRequestVal("FK_Emp").equals("all")))
		{
			if (this.getFK_Dept().length() == 2)
			{
				qo.AddWhere("FK_Dept", " = ", "all");
				qo.addAnd();
			}
			else
			{
				if (this.getFK_Dept().length() == 8)
				{
					qo.AddWhere("FK_Dept", " = ", this.getFK_Dept());
				}
				else
				{
					qo.AddWhere("FK_Dept", " like ", this.getFK_Dept() + "%");
				}

				qo.addAnd();
			}
		}

		qo.AddHD();

		DataTable dt = qo.DoQueryToTable();
		Attrs newAttrs = new Attrs();
		for (MapAttr attr : attrs.ToJavaList())
		{
			if (attr.getKeyOfEn().toUpperCase().equals("OID"))
			{
				continue;
			}

			newAttrs.Add(attr.getHisAttr());
		}

		bp.tools.ExportExcelUtil.ExportDGToExcel(dt, ges.getNewEntity(), rptNo, newAttrs);
		return "";
	}

	/** 
	 通过一个key 得到它的属性值。
	 
	 param key key
	 @return attr
	*/
	public final Attr GetAttrByKey(MapAttrs mapAttrs, String key) throws Exception {
		for (MapAttr attr : mapAttrs.ToJavaList())
		{
			if (attr.getKeyOfEn().toUpperCase().equals(key.toUpperCase()))
			{
				return attr.getHisAttr();
			}
		}
		return null;

	}

	/** 
	 初始化QueryObject
	 
	 param qo
	 @return 
	*/
	public final QueryObject InitQueryObject(QueryObject qo, MapData md, Attrs attrs, MapAttrs rptAttrs, UserRegedit ur) throws Exception {
		HashMap<String, String> kvs = null;
		ArrayList<String> keys = new ArrayList<String>();
		String cfg = "_SearchAttrs";
		String searchKey = "";
		String val = null;

		kvs = ur.GetVals();

		if (!this.getSearchType().equals("Adminer"))
		{
			qo.addAnd();
		}


			///#region 关键字查询
		if (md.getItIsSearchKey())
		{
			searchKey = ur.getSearchKey();
		}


		boolean isFirst = true;
		if (md.getItIsSearchKey() && DataType.IsNullOrEmpty(searchKey) == false && searchKey.length() >= 1)
		{
			int i = 0;

			for (Attr attr : attrs)
			{
				switch (attr.getMyFieldType())
				{
					case Enum:
					case FK:
					case PKFK:
						continue;
					default:
						break;
				}

				if (attr.getMyDataType() != DataType.AppString)
				{
					continue;
				}

				if (attr.getMyFieldType() == FieldType.RefText)
				{
					continue;
				}

				if (attr.getKey().equals("FK_Dept"))
				{
					continue;
				}

				i++;

				if (i == 1)
				{
					isFirst = false;
					qo.addLeftBracket();
					if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
					{
						qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
					}
					else
					{
						qo.AddWhere(attr.getKey(), " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
					}
					continue;
				}

				qo.addOr();

				if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
				{
					qo.AddWhere(attr.getKey(), " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "SKey,'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + "SKey+'%'"));
				}
				else
				{
					qo.AddWhere(attr.getKey(), " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + "SKey||'%'");
				}
			}

			qo.getMyParas().Add("SKey", searchKey, false);
			qo.addRightBracket();
		}
		else if (DataType.IsNullOrEmpty(md.GetParaString("StringSearchKeys")) == false)
		{
			String field = ""; //字段名
			String fieldValue = ""; //字段值
			int idx = 0;

			//获取查询的字段
			String[] searchFields = md.GetParaString("StringSearchKeys").split("[*]", -1);
			for (String str : searchFields)
			{
				if (DataType.IsNullOrEmpty(str) == true)
				{
					continue;
				}

				//字段名
				String[] items = str.split("[,]", -1);
				if (items.length == 2 && DataType.IsNullOrEmpty(items[0]) == true)
				{
					continue;
				}
				field = items[0];
				//字段名对应的字段值
				fieldValue = ur.GetParaString(field);
				if (DataType.IsNullOrEmpty(fieldValue) == true)
				{
					continue;
				}
				idx++;
				if (idx == 1)
				{
					isFirst = false;
					/* 第一次进来。 */
					qo.addLeftBracket();
					if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
					{
						qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? (" CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : (" '%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
					}
					else
					{
						qo.AddWhere(field, " LIKE ", " '%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
					}
					qo.getMyParas().Add(field, fieldValue, false);
					continue;
				}
				qo.addAnd();

				if (SystemConfig.getAppCenterDBVarStr().equals("@") || SystemConfig.getAppCenterDBType( ) == DBType.MySQL || SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
				{
					qo.AddWhere(field, " LIKE ", SystemConfig.getAppCenterDBType( ) == DBType.MySQL ? ("CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + field + ",'%')") : ("'%'+" + SystemConfig.getAppCenterDBVarStr() + field + "+'%'"));
				}
				else
				{
					qo.AddWhere(field, " LIKE ", "'%'||" + SystemConfig.getAppCenterDBVarStr() + field + "||'%'");
				}
				qo.getMyParas().Add(field, fieldValue, false);


			}
			if (idx != 0)
			{
				qo.addRightBracket();
			}
		}
		else
		{
			qo.AddHD();
			isFirst = false;
		}

			///#endregion


			///#region Url传参条件
		for (Attr attr : attrs)
		{
			if (DataType.IsNullOrEmpty(this.GetRequestVal(attr.getKey())))
			{
				continue;
			}
			if (isFirst == false)
			{
				qo.addAnd();
			}
			if (isFirst == true)
			{
				isFirst = false;
			}
			qo.addLeftBracket();

			val = this.GetRequestVal(attr.getKey());

			switch (attr.getMyDataType())
			{
				case DataType.AppBoolean:
					qo.AddWhere(attr.getKey(), Boolean.valueOf(val));
					break;
				case DataType.AppDate:
				case DataType.AppDateTime:
				case DataType.AppString:
					qo.AddWhere(attr.getKey(), val);
					break;
				case DataType.AppDouble:
				case DataType.AppFloat:
				case DataType.AppMoney:
					qo.AddWhere(attr.getKey(), Double.parseDouble(val));
					break;
				case DataType.AppInt:
					qo.AddWhere(attr.getKey(), Integer.parseInt(val));
					break;
				default:
					break;
			}

			qo.addRightBracket();

			if (keys.contains(attr.getKey()) == false)
			{
				keys.add(attr.getKey());
			}
		}

			///#endregion


			///#region 过滤条件
		for (MapAttr attr1 : rptAttrs.ToJavaList())
		{
			Attr attr = attr1.getHisAttr();
			//此处做判断，如果在URL中已经传了参数，则不算SearchAttrs中的设置
			if (keys.contains(attr.getKey()))
			{
				continue;
			}

			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			String selectVal = "";
			String cid = "";

			switch (attr.getUIContralType())
			{
				//case UIContralType.TB:
				//    switch (attr.getMyDataType())
				//    {
				//        case DataType.AppDate:
				//        case DataType.AppDateTime:
				//            if (attr.getMyDataType() == DataType.AppDate)
				//                cid = "D_" + attr.getKey();
				//            else
				//                cid = "DT_" + attr.getKey();

				//            if (kvs.containsKey(cid) == false || string.IsNullOrWhiteSpace(kvs[cid]))
				//                continue;

				//            selectVal = kvs[cid];

				//            qo.addAnd();
				//            qo.addLeftBracket();
				//            qo.AddWhere(attr.getKey(), selectVal);
				//            qo.addRightBracket();
				//            break;
				//        default:
				//            cid = "TB_" + attr.getKey();

				//            if (kvs.containsKey(cid) == false || string.IsNullOrWhiteSpace(kvs[cid]))
				//                continue;

				//            selectVal = kvs[cid];

				//            qo.addAnd();
				//            qo.addLeftBracket();
				//            qo.AddWhere(attr.getKey(), " LIKE ", "%" + selectVal + "%");
				//            qo.addRightBracket();
				//            break;
				//    }
				//    break;
				case DDL:
				case RadioBtn:
					cid = "DDL_" + attr.getKey();

					if (kvs.containsKey(cid) == false || DataType.IsNullOrEmpty(kvs.get(cid)))
					{
						continue;
					}

					selectVal = kvs.get(cid);

					if (selectVal.equals("all") || selectVal.equals("-1"))
					{
						continue;
					}

					if (selectVal.equals("mvals"))
					{
						/* 如果是多选值 */
						AtPara ap = new AtPara(ur.getMVals());
						String instr = ap.GetValStrByKey(attr.getKey());

						if (DataType.IsNullOrEmpty(instr))
						{
							if (attr.getKey().equals("FK_Dept") || attr.getKey().equals("FK_Unit"))
							{
								if (attr.getKey().equals("FK_Dept"))
								{
									selectVal = WebUser.getDeptNo();
								}
							}
							else
							{
								continue;
							}
						}
						else
						{
							instr = instr.replace("..", ".");
							instr = instr.replace(".", "','");
							instr = instr.substring(2);
							instr = instr.substring(0, instr.length() - 2);

							if (isFirst == false)
							{
								qo.addAnd();
							}
							if (isFirst == true)
							{
								isFirst = false;
							}

							qo.addLeftBracket();
							qo.AddWhereIn(attr.getKey(), "(" + instr + ")");
							qo.addRightBracket();
							continue;
						}
					}

					if (isFirst == false)
					{
						qo.addAnd();
					}
					if (isFirst == true)
					{
						isFirst = false;
					}

					qo.addLeftBracket();


					String deptName = bp.sys.base.Glo.DealClassEntityName("bp.port.Depts");

					if (attr.getUIBindKey().equals(deptName) == true) //判断特殊情况。
					{
						qo.AddWhere(attr.getKey(), " LIKE ", selectVal + "%");
					}
					else
					{
						qo.AddWhere(attr.getKey(), selectVal);
					}

					qo.addRightBracket();
					break;
				//case UIContralType.CheckBok:
				//    cid = "CB_" + attr.getKey();

				//    if (kvs.containsKey(cid) == false || string.IsNullOrWhiteSpace(kvs[cid]))
				//        continue;

				//    selectVal = kvs[cid];

				//    qo.addAnd();
				//    qo.addLeftBracket();
				//    qo.AddWhere(attr.getKey(), int.Parse(selectVal));
				//    qo.addRightBracket();
				//    break;
				default:
					break;
			}
		}

			///#endregion


			///#region 日期处理
		if (md.getDTSearchWay ()!= DTSearchWay.None)
		{
			String dtKey = md.getDTSearchKey();
			String dtFrom = ur.GetValStringByKey(UserRegeditAttr.DTFrom).trim();
			String dtTo = ur.GetValStringByKey(UserRegeditAttr.DTTo).trim();

			if (DataType.IsNullOrEmpty(dtFrom) == true)
			{
				if (md.getDTSearchWay ()== DTSearchWay.ByDate)
				{
					dtFrom = "1900-01-01";
				}
				else
				{
					dtFrom = "1900-01-01 00:00";
				}
			}

			if (DataType.IsNullOrEmpty(dtTo) == true)
			{
				if (md.getDTSearchWay ()== DTSearchWay.ByDate)
				{
					dtTo = "2999-01-01";
				}
				else
				{
					dtTo = "2999-12-31 23:59";
				}
			}

			if (md.getDTSearchWay ()== DTSearchWay.ByDate)
			{
				if (isFirst == false)
				{
					qo.addAnd();
				}
				if (isFirst == true)
				{
					isFirst = false;
				}

				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + "'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + "'");
				qo.addRightBracket();
			}

			if (md.getDTSearchWay ()== DTSearchWay.ByDateTime)
			{
				if (isFirst == false)
				{
					qo.addAnd();
				}
				if (isFirst == true)
				{
					isFirst = false;
				}

				qo.addLeftBracket();
				qo.setSQL(dtKey + " >= '" + dtFrom + " 00:00'");
				qo.addAnd();
				qo.setSQL(dtKey + " <= '" + dtTo + " 23:59'");
				qo.addRightBracket();
			}
		}

			///#endregion

		return qo;
	}

	private DataTable GetNoNameDataTable(String tableName)
	{
		DataTable dt = new DataTable(tableName);
		dt.Columns.Add("No", String.class);
		dt.Columns.Add("Name", String.class);

		return dt;
	}

		///#endregion MyStartFlow.htm 我发起的流程

	public final String MyDeptFlow_Init() throws Exception {
		String fk_mapdata = "ND" + Integer.parseInt(this.getFK_Flow()) + "RptMyDept";

		DataSet ds = new DataSet();

		//字段描述.
		MapAttrs attrs = new MapAttrs(fk_mapdata);
		DataTable dtAttrs = attrs.ToDataTableField("Sys_MapAttr");
		ds.Tables.add(dtAttrs);

		//数据.
		GEEntitys ges = new GEEntitys(fk_mapdata);

		//设置查询条件.
		QueryObject qo = new QueryObject(ges);
		qo.AddWhere(GERptAttr.FlowStarter, WebUser.getNo());

		//查询.
		// qo.DoQuery(BP.WF.GERptAttr.OID, 15, this.PageIdx);

		if (SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
		{
			DataTable dt = qo.DoQueryToTable();
			dt.TableName = "dt";
			ds.Tables.add(dt);
		}
		else
		{
			qo.DoQuery();
			ds.Tables.add(ges.ToDataTableField("dt"));
		}

		return bp.tools.Json.ToJson(ds);
	}

	public final String MyJoinFlow_Init() throws Exception {
		String fk_mapdata = "ND" + Integer.parseInt(this.getFK_Flow()) + "RptMyJoin";

		DataSet ds = new DataSet();

		//字段描述.
		MapAttrs attrs = new MapAttrs(fk_mapdata);
		DataTable dtAttrs = attrs.ToDataTableField("Sys_MapAttr");
		ds.Tables.add(dtAttrs);

		//数据.
		GEEntitys ges = new GEEntitys(fk_mapdata);

		//设置查询条件.
		QueryObject qo = new QueryObject(ges);
		qo.AddWhere(GERptAttr.FlowEmps, " LIKE ", "%" + WebUser.getNo() + "%");

		if (SystemConfig.getAppCenterDBType( ) == DBType.MSSQL)
		{
			DataTable dt = qo.DoQueryToTable();
			dt.TableName = "dt";
			ds.Tables.add(dt);
		}
		else
		{
			qo.DoQuery();
			ds.Tables.add(ges.ToDataTableField("dt"));
		}
		return bp.tools.Json.ToJson(ds);
	}
}