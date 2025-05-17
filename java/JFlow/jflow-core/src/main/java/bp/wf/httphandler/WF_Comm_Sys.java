package bp.wf.httphandler;

import bp.da.*;
import bp.difference.handler.CommonFileUtils;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.difference.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.time.*;

/** 
 页面功能实体
*/
public class WF_Comm_Sys extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 加密字符串
	 
	 @return 
	*/
	public final String JiaMi_Init() throws Exception {
		String str = "ssss";
		return bp.tools.Cryptos.aesEncrypt(str);
	}
	public final String ImpData_Init()
	{
		return "";
	}
	private String ImpData_DoneMyPK(Entities ens, DataTable dt) throws Exception {
		//错误信息
		String errInfo = "";
		EntityMyPK en = (EntityMyPK)ens.getNewEntity();
		//定义属性.
		Attrs attrs = en.getEnMap().getAttrs();

		int impWay = this.GetRequestValInt("ImpWay");


			///#region 清空方式导入.
		//清空方式导入.
		int count = 0; //导入的行数
		int changeCount = 0; //更新数据的行数
		String successInfo = "";
		if (impWay == 0)
		{
			ens.ClearTable();
			for (DataRow dr : dt.Rows)
			{
				en = (EntityMyPK)ens.getNewEntity();
				//给实体赋值
				errInfo += SetEntityAttrVal("", dr, attrs, en, dt, 0);
				//获取PKVal
				en.setPKVal(  en.InitMyPKVals());
				if (en.RetrieveFromDBSources() == 0)
				{
					en.Insert();
					count++;
					successInfo += "&nbsp;&nbsp;<span>MyPK=" + en.getPKVal() + "的导入成功</span><br/>";
				}

			}
		}
			///#region 更新方式导入
		if (impWay == 1 || impWay == 2)
		{
			for (DataRow dr : dt.Rows)
			{
				en = (EntityMyPK)ens.getNewEntity();
				//给实体赋值
				errInfo += SetEntityAttrVal("", dr, attrs, en, dt, 1);

				//获取PKVal
				en.setPKVal(en.InitMyPKVals());
				if (en.RetrieveFromDBSources() == 0)
				{
					en.Insert();
					count++;
					successInfo += "&nbsp;&nbsp;<span>MyPK=" + en.getPKVal() + "的导入成功</span><br/>";
				}
				else
				{
					changeCount++;
					SetEntityAttrVal("", dr, attrs, en, dt, 1);
					successInfo += "&nbsp;&nbsp;<span>MyPK=" + en.getPKVal() + "的更新成功</span><br/>";
				}
			}
		}

			///#endregion

		return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
	}
	/** 
	 执行导入
	 
	 @return 
	*/
	public final String ImpData_Done() throws Exception {

		HttpServletRequest request = getRequest();
		long filesSize = CommonFileUtils.getFilesSize(request, "File_Upload");
		if (filesSize == 0) {
			return "err@请选择要导入的数据信息。";
		}
		String errInfo = "";

		String ext = ".xls";
		String fileName = CommonFileUtils.getOriginalFilename(request, "File_Upload");
		if (fileName.contains(".xlsx"))
			ext = ".xlsx";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String currDate = sdf.format(new Date());

		String fileNewName = currDate + ext;

		//文件存放路径
		String filePath = SystemConfig.getPathOfTemp() + "/" + fileNewName;
		File tempFile = new File(filePath);
		if (tempFile.exists()) {
			tempFile.delete();
		}
		try {
			// multiFile.transferTo(tempFile);
			CommonFileUtils.upload(request, "File_Upload", tempFile);
		} catch (Exception e) {
			try{
				CommonFileUtils.upload(request, "file", tempFile);
			}catch (Exception e1){
				e1.printStackTrace();
				return "err@执行失败";
			}

		}
		//从excel里面获得数据表.
		DataTable dt = DBLoad.ReadExcelFileToDataTable(filePath, 0);

		//删除临时文件
		(new File(filePath)).delete();

		if (dt.Rows.size() == 0)
		{
			return "err@无导入的数据";
		}

		//获得entity.
		Entities ens = ClassFactory.GetEns(this.getEnsName());
		Entity en = ens.getNewEntity();

		if (en.getPK().equals("MyPK") == true)
		{
			return this.ImpData_DoneMyPK(ens, dt);
		}

		if (en.getItIsNoEntity() == false)
		{
			return "err@必须是EntityNo或者EntityMyPK实体,才能导入.";
		}

		String noColName = ""; //实体列的编号名称.
		String nameColName = ""; //实体列的名字名称.

		Attr attr = en.getEnMap().GetAttrByKey("No");
		noColName = attr.getDesc();
		Map map = en.getEnMap();
		String codeStruct = map.getCodeStruct();
		attr = map.GetAttrByKey("Name");
		nameColName = attr.getDesc();

		//定义属性.
		Attrs attrs = en.getEnMap().getAttrs();

		int impWay = this.GetRequestValInt("ImpWay");


			///#region 清空方式导入.
		//清空方式导入.
		int count = 0; //导入的行数
		int changeCount = 0; //更新的行数
		String successInfo = "";
		if (impWay == 0)
		{
			ens.ClearTable();
			for (DataRow dr : dt.Rows)
			{
				String no = dr.getValue(noColName).toString();
				String name = dr.getValue(nameColName).toString();

				//判断是否是自增序列，序列的格式
				if (!DataType.IsNullOrEmpty(codeStruct))
				{
					no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
				}

				bp.en.Entity tempVar = ens.getNewEntity();
				EntityNoName myen = tempVar instanceof EntityNoName ? (EntityNoName)tempVar : null;
				myen.setNo(no);
				if (myen.getIsExits())
				{
					errInfo += "err@编号[" + no + "][" + name + "]重复.";
					continue;
				}

				myen.setName(name);

				en = ens.getNewEntity();

				//给实体赋值
				errInfo += SetEntityAttrVal(no, dr, attrs, en, dt, 0);
				count++;
				successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
			}
		}


			///#endregion 清空方式导入.


			///#region 更新方式导入
		if (impWay == 1 || impWay == 2)
		{
			for (DataRow dr : dt.Rows)
			{
				String no = dr.getValue(noColName).toString();
				String name = dr.getValue(nameColName).toString();
				//判断是否是自增序列，序列的格式
				if (!DataType.IsNullOrEmpty(codeStruct))
				{
					no = StringHelper.padLeft(no, Integer.parseInt(codeStruct), '0');
				}
				bp.en.Entity tempVar2 = ens.getNewEntity();
				EntityNoName myen = tempVar2 instanceof EntityNoName ? (EntityNoName)tempVar2 : null;
				myen.setNo(no);
				if (myen.getIsExits())
				{
					//给实体赋值
					errInfo += SetEntityAttrVal(no, dr, attrs, myen, dt, 1);
					changeCount++;
					successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的更新成功</span><br/>";
					continue;
				}
				myen.setName(name);

				//给实体赋值
				errInfo += SetEntityAttrVal(no, dr, attrs, en, dt, 0);
				count++;
				successInfo += "&nbsp;&nbsp;<span>" + noColName + "为" + no + "," + nameColName + "为" + name + "的导入成功</span><br/>";
			}
		}

			///#endregion

		return "errInfo=" + errInfo + "@Split" + "count=" + count + "@Split" + "successInfo=" + successInfo + "@Split" + "changeCount=" + changeCount;
	}

	private String SetEntityAttrVal(String no, DataRow dr, Attrs attrs, Entity en, DataTable dt, int saveType) throws Exception {
		String errInfo = "";
		//按照属性赋值.
		for (Attr item : attrs)
		{
			if (Objects.equals(item.getKey(), "No"))
			{
				en.SetValByKey(item.getKey(), no);
				continue;
			}
			if (Objects.equals(item.getKey(), "Name"))
			{
				en.SetValByKey(item.getKey(), dr.getValue(item.getDesc()).toString());
				continue;
			}


			if (dt.Columns.contains(item.getDesc()) == false)
			{
				continue;
			}

			//枚举处理.
			if (item.getMyFieldType() == FieldType.Enum)
			{
				String val = dr.getValue(item.getDesc()).toString();

				SysEnum se = new SysEnum();
				int i = se.Retrieve(SysEnumAttr.EnumKey, item.getUIBindKey(), SysEnumAttr.Lab, val);

				if (i == 0)
				{
					errInfo += "err@枚举[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
					continue;
				}

				en.SetValByKey(item.getKey(), se.getIntKey());
				continue;
			}

			//外键处理.
			if (item.getMyFieldType() == FieldType.FK)
			{
				String val = dr.getValue(item.getDesc()).toString();
				Entity attrEn = item.getHisFKEn();
				int i = attrEn.Retrieve("Name", val);
				if (i == 0)
				{
					errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]不存在.";
					continue;
				}

				if (i != 1)
				{
					errInfo += "err@外键[" + item.getKey() + "][" + item.getDesc() + "]，值[" + val + "]重复..";
					continue;
				}

				//把编号值给他.
				en.SetValByKey(item.getKey(), attrEn.GetValByKey("No"));
				continue;
			}

			//boolen类型的处理..
			if (item.getMyDataType ()== DataType.AppBoolean)
			{
				String val = dr.getValue(item.getDesc()).toString();
				if (Objects.equals(val, "是") || Objects.equals(val, "有"))
				{
					en.SetValByKey(item.getKey(), 1);
				}
				else
				{
					en.SetValByKey(item.getKey(), 0);
				}
				continue;
			}

			String myval = dr.getValue(item.getDesc()).toString();
			en.SetValByKey(item.getKey(), myval);
		}

		try
		{
			if (en.getItIsNoEntity() == true)
			{
				if (saveType == 0)
				{
					en.Insert();
				}
				else
				{
					en.Update();
				}
			}

		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}

		return errInfo;
	}


	/** 
	 构造函数
	*/
	public WF_Comm_Sys()
	{
	}
	/** 
	 函数库
	 
	 @return 
	*/
	public final String SystemClass_FuncLib() throws Exception {
		String expFileName = "all-wcprops,dir-prop-base,entries";
		String expDirName = ".svn";

		String pathDir = SystemConfig.getPathOfData() + "/JSLib/";

		String html = "";
		html += "<fieldset>";
		html += "<legend>" + "系统自定义函数. 位置:" + pathDir + "</legend>";

		// .AddFieldSet();
		File dir = new File(pathDir);
		File[] dirs = new File(pathDir).listFiles();
		for (File mydir : dirs) {
			if (expDirName.contains(mydir.getName())) {
				continue;
			}

			html += "事件名称" + mydir.getName();
			html += "<ul>";
			if (mydir.isDirectory()) {
				File[] fls = mydir.listFiles();
				for (File fl : fls) {
					if (expFileName.contains(fl.getName())) {
						continue;
					}

					html += "<li>" + fl.getName() + "</li>";
				}

			}
			html += "</ul>";
		}
		html += "</fieldset>";

		pathDir = SystemConfig.getPathOfDataUser() + "/JSLib/";
		html += "<fieldset>";
		html += "<legend>" + "用户自定义函数. 位置:" + pathDir + "</legend>";

		dir = new File(pathDir);
		dirs = dir.listFiles();
		for (File mydir : dirs) {
			if (expDirName.contains(mydir.getName())) {
				continue;
			}

			html += "事件名称" + mydir.getName();
			html += "<ul>";
			if (mydir.isDirectory()) {
				File[] fls = mydir.listFiles();
				for (File fl : fls) {
					if (expFileName.contains(fl.getName())) {
						continue;
					}
					html += "<li>" + fl.getName() + "</li>";
				}
			}
			html += "</ul>";
		}
		html += "</fieldset>";
		return html;
	}


		///#region 系统实体属性.
	public final String SystemClass_EnsCheck() throws Exception {
		try
		{
			Entity en = ClassFactory.GetEn(this.getEnName());
			Map map = en.getEnMap();
			en.CheckPhysicsTable();
			String msg = "";
			// String msg = "";
			String table = "";
			String sql = "";
			String sql1 = "";
			String sql2 = "";
			int COUNT1 = 0;
			int COUNT2 = 0;

			DataTable dt = new DataTable();
			Entity refen = null;
			for (Attr attr : map.getAttrs())
			{
				if (attr.getMyFieldType() == FieldType.FK || attr.getMyFieldType() == FieldType.PKFK)
				{
					refen = ClassFactory.GetEns(attr.getUIBindKey()).getNewEntity();
					table = refen.getEnMap().getPhysicsTable();
					sql1 = "SELECT COUNT(*) FROM " + table;

					Attr pkAttr = refen.getEnMap().GetAttrByKey(refen.getPK());
					sql2 = "SELECT COUNT( distinct " + pkAttr.getField() + ") FROM " + table;

					COUNT1 = DBAccess.RunSQLReturnValInt(sql1);
					COUNT2 = DBAccess.RunSQLReturnValInt(sql2);

					if (COUNT1 != COUNT2)
					{
						msg += "<BR>@关联表(" + refen.getEnMap().getEnDesc() + ")主键不唯一，它会造成数据查询不准确或者意向不到的错误：<BR>sql1=" + sql1 + " <BR>sql2=" + sql2;
						msg += "@SQL= SELECT * FROM (  select " + refen.getPK() + ",  COUNT(*) AS NUM  from " + table + " GROUP BY " + refen.getPK() + " ) WHERE NUM!=1";
					}

					sql = "SELECT " + attr.getField() + " FROM " + map.getPhysicsTable() + " WHERE " + attr.getField() + " NOT IN (SELECT " + pkAttr.getField() + " FROM " + table + " )";
					dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() == 0)
					{
						continue;
					}
					else
					{
						msg += "<BR>:有" + dt.Rows.size() + "个错误。" + attr.getDesc() + " sql= " + sql;
					}
				}
				if (attr.getMyFieldType() == FieldType.PKEnum || attr.getMyFieldType() == FieldType.Enum)
				{
					if(SystemConfig.getAppCenterDBType() == DBType.HGDB || SystemConfig.getAppCenterDBType() == DBType.PostgreSQL){
						if(attr.getItIsNum())
							sql = "SELECT " + attr.getField() + " FROM " + map.getPhysicsTable() + " WHERE " + attr.getField() + " NOT IN ( select Intkey FROM " + bp.sys.base.Glo.SysEnum() + " WHERE ENUMKEY='" + attr.getUIBindKey() + "' )";
						else
							sql = "SELECT " + attr.getField() + " FROM " + map.getPhysicsTable() + " WHERE " + attr.getField() + " NOT IN ( select CAST(Intkey AS varchar) FROM " + bp.sys.base.Glo.SysEnum() + " WHERE ENUMKEY='" + attr.getUIBindKey() + "' )";
					}
					else
						sql = "SELECT " + attr.getField() + " FROM " + map.getPhysicsTable() + " WHERE " + attr.getField() + " NOT IN ( select Intkey FROM " + bp.sys.base.Glo.SysEnum() + " WHERE ENUMKEY='" + attr.getUIBindKey() + "' )";
					dt = DBAccess.RunSQLReturnTable(sql);
					if (dt.Rows.size() == 0)
					{
						continue;
					}
					else
					{
						msg += "<BR>:有" + dt.Rows.size() + "个错误。" + attr.getDesc() + " sql= " + sql;
					}
				}
			}

			// 检查pk是否一致。
			if (en.getPKs().length == 1)
			{
				sql1 = "SELECT COUNT(*) FROM " + map.getPhysicsTable();
				COUNT1 = DBAccess.RunSQLReturnValInt(sql1);

				Attr attrMyPK = en.getEnMap().GetAttrByKey(en.getPK());
				sql2 = "SELECT COUNT(DISTINCT " + attrMyPK.getField() + ") FROM " + map.getPhysicsTable();
				COUNT2 = DBAccess.RunSQLReturnValInt(sql2);
				if (COUNT1 != COUNT2)
				{
					msg += "@物理表(" + map.getEnDesc() + ")中主键不唯一;它会造成数据查询不准确或者意向不到的错误：<BR>sql1=" + sql1 + " <BR>sql2=" + sql2;
					msg += "@SQL= SELECT * FROM (  select " + en.getPK() + ",  COUNT(*) AS NUM  from " + map.getPhysicsTable() + " GROUP BY " + en.getPK() + " ) WHERE NUM!=1";
				}
			}

			if (Objects.equals(msg, ""))
			{
				return map.getEnDesc() + ":数据体检成功,完全正确.";
			}

			String info = map.getEnDesc() + ":数据体检信息：体检失败" + msg;
			return info;

		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public final String SystemClass_Fields_UI() throws Exception {
		return SystemClass_Fields_UI_Ext(this.getEnsName());
	}
	public final String SystemClass_Fields_UI_Ext(String ensName) throws Exception {
		Entities ens = ClassFactory.GetEns(ensName);
		Entity en = ens.getNewEntity();

		Map map = en.getEnMap();
		en.CheckPhysicsTable();

		String html = "<table style='width:95%;font-size'>";

		html += "<caption>" + map.getEnDesc() + "," + ensName + "," + map.getPhysicsTable() + "</caption>";

		//html += "<tr>";
		//html += "<th colspan=8> " + map.getEnDesc() + ","+ensName+"," + map.getPhysicsTable() + " </th>";
		//html += "</tr>";

		html += "<tr>";
		html += "<th>#</th>";
		html += "<th>描述</th>";
		html += "<th>字段</th>";
		html += "<th>类型</th>";
		//   html += "<th>关系</th>";
		html += "<th>控件/外观/长度</th>";
		html += "<th>备注</th>";
		//      html += "<th>默认值</th>";
		html += "</tr>";

		int i = 0;
		for (Attr attr : map.getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			i++;
			html += "<tr>";
			html += "<td>" + i + "</td>";
			html += "<td>" + attr.getDesc() + "</td>";
			html += "<td>" + attr.getKey() + "</td>";
			//   html += "<td>" + attr.getField() + "</td>";
			html += "<td>" + attr.getMyDataTypeStr() + "</td>";
			// html += "<td>" + attr.getMyFieldType().ToString() + "</td>";
			String desc = "";
			if (attr.getUIVisible() == true)
			{
				desc += "可见,";
			}
			else
			{
				desc += "不可见,";
			}

			if (attr.getUIIsReadonly() == false)
			{
				desc += "可编辑";
			}
			else
			{
				desc += "不可编辑";
			}

			switch (attr.getMyDataType())
			{
				case DataType.AppBoolean:
					html += "<td>选择框," + desc + "</td>";
					break;
				case DataType.AppDouble:
				case DataType.AppFloat:
				case DataType.AppInt:
					if (attr.getMyFieldType() == FieldType.Enum)
					{
						html += "<td>下拉框," + desc + "</td>";
						break;
					}
					else
					{
						html += "<td>数值文本框," + desc + "</td>";
					}
					break;
				case DataType.AppMoney:
					html += "<td>金额文本框," + desc + "</td>";
					break;
				case DataType.AppDate:
					html += "<td>日期文本框," + desc + "</td>";
					break;
				case DataType.AppDateTime:
					html += "<td>日期时间文本框," + desc + "</td>";
					break;
				default:
					if (attr.getMyFieldType() == FieldType.FK)
					{
						html += "<td>下拉框(" + attr.getMinLength() + "/" + attr.getMaxLength() + ")," + desc + "</td>";
					}
					else
					{
						html += "<td>文本框(" + attr.getMinLength() + "/" + attr.getMaxLength() + ")," + desc + "</td>";
					}
					break;
			}

			String defVal = "";
			if (DataType.IsNullOrEmpty(attr.getDefaultValOfReal()) == false)
			{
				defVal += "默认值:" + attr.getDefaultValOfReal() + "";
			}

			switch (attr.getMyFieldType())
			{
				case Enum:
				case PKEnum:
					try
					{
						SysEnums ses = new SysEnums(attr.getUIBindKey());
						String str = "";
						for (SysEnum se : ses.ToJavaList())
						{
							str += se.getIntKey() + "&nbsp;" + se.getLab() + ",";
						}
						html += "<td>" + str + defVal + "</td>";
					}
					catch (java.lang.Exception e)
					{
						html += "<td>" + defVal + "</td>";

					}
					break;
				case FK:
				case PKFK:
					Entities myens = ClassFactory.GetEns(attr.getUIBindKey());
					html += "<td>表/视图:" + myens.getNewEntity().getEnMap().getPhysicsTable() + " 关联字段:" + attr.getUIRefKeyValue()+ "," + attr.getUIRefKeyText() + defVal + "</td>";
					break;
				default:
					html += "<td>" + defVal + "</td>";
					break;
			}
			html += "</tr>";
		}
		html += "</table>";

		//  html += "<div style='text-align:center;' >(表:数据结构" + map.getEnDesc() + "," + ensName + "," + map.getPhysicsTable() + ")</div>";

		return html;
	}
	public final String SystemClass_Fields() throws Exception {
		return SystemClass_Fields_Ext(this.getEnsName());
	}
	public final String SystemClass_Fields_Ext(String ensName) throws Exception {
		Entities ens = ClassFactory.GetEns(ensName);
		Entity en = ens.getNewEntity();

		Map map = en.getEnMap();
		en.CheckPhysicsTable();

		String html = "";
		html += "<center>" + map.getEnDesc() + "," + map.getPhysicsTable() + "</center>";
		html += "<table>";
		html += "<tr>";
		html += "<th>#</th>";
		html += "<th>描述</th>";
		html += "<th>字段</th>";
		html += "<th>类型</th>";
		html += "<th>关系</th>";
		html += "<th>长度</th>";
		html += "<th>对应</th>";
		html += "<th>默认值</th>";
		html += "</tr>";

		int i = 0;
		for (Attr attr : map.getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			i++;
			html += "<tr>";
			html += "<td>" + i + "</td>";
			html += "<td>" + attr.getDesc() + "</td>";
			//   html += "<td>" + attr.getKey() + "</td>";
			html += "<td>" + attr.getField() + "</td>";
			html += "<td>" + attr.getMyDataTypeStr() + "</td>";
			html += "<td>" + attr.getMyFieldType().toString() + "</td>";

			if (attr.getMyDataType() == DataType.AppBoolean || attr.getMyDataType() == DataType.AppDouble || attr.getMyDataType() == DataType.AppFloat || attr.getMyDataType() == DataType.AppInt || attr.getMyDataType() == DataType.AppMoney)
			{
				html += "<td>无</td>";
			}
			else
			{
				html += "<td>" + attr.getMaxLength() + "</td>";
			}


			switch (attr.getMyFieldType())
			{
				case Enum:
				case PKEnum:
					try
					{
						SysEnums ses = new SysEnums(attr.getUIBindKey());
						String str = "";
						for (SysEnum se : ses.ToJavaList())
						{
							str += se.getIntKey() + "&nbsp;" + se.getLab() + ",";
						}
						html += "<td>" + str + "</td>";
					}
					catch (java.lang.Exception e)
					{
						html += "<td>未使用</td>";

					}
					break;
				case FK:
				case PKFK:
					Entities myens = ClassFactory.GetEns(attr.getUIBindKey());
					html += "<td>表/视图:" + myens.getNewEntity().getEnMap().getPhysicsTable() + " 关联字段:" + attr.getUIRefKeyValue()+ "," + attr.getUIRefKeyText() + "</td>";
					break;
				default:
					html += "<td>无</td>";
					break;
			}

			html += "<td>" + attr.getDefaultVal().toString() + "</td>";
			html += "</tr>";
		}
		html += "</table>";
		return html;
	}

	/** 
	 系统日志
	 
	 @return 
	*/
	public final String SystemLog_Init() throws Exception {
		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");
		dt.Columns.Add("LogType");

		String path = SystemConfig.getPathOfDataUser() + "\\Log\\info";
		String[] strs = bp.tools.BaseFileUtils.getFiles(path);
		for (String str : strs)
		{
			DataRow dr = dt.NewRow();
			dr.setValue(0, str.substring(str.indexOf("info") + 5));
			dr.setValue(1, str.substring(str.indexOf("info") + 5));
			dr.setValue(2, "信息");
			// dr[1] = str;
			dt.Rows.add(dr);
		}

		path = SystemConfig.getPathOfDataUser() + "\\Log\\error";
		File fileE =new File(path);    //如果文件夹不存在则创建
		if  (!fileE .exists()  && !fileE .isDirectory()) {
			fileE .mkdir();
		}
		strs = fileE.list();
		for (String str : strs)
		{
			DataRow dr = dt.NewRow();
			dr.setValue(0,str);
			dr.setValue(1 ,str.substring(0,str.indexOf(".log")));
			dr.setValue(2, "错误");
			dt.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(dt);
	}
	public final String SystemLog_Open() throws Exception {
		String logType = this.GetRequestVal("LogType");
		if (logType.equals("信息") == true)
		{
			logType = "info";
		}
		else
		{
			logType = "error";
		}

		String path = SystemConfig.getPathOfDataUser() + "\\Log\\" + logType + "\\" + this.getRefNo();
		String str = DataType.ReadTextFile2Html(path);
		return str;
	}

	public final String SystemClass_Init() throws Exception {
		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("EnsName");
		dt.Columns.Add("Name");
		dt.Columns.Add("PTable");

		ArrayList al = null;
		al = ClassFactory.GetObjects("BP.En.Entity");
		for (Object obj : al)
		{
			Entity en = null;
			try
			{
				en = obj instanceof Entity ? (Entity)obj : null;
				if (en == null)
				{
					continue;
				}

				String className = en.toString();
				if (DataType.IsNullOrEmpty(className) == true)
				{
					continue;
				}

				switch (className.toUpperCase())
				{
					case "BP.WF.STARTWORK":
					case "BP.WF.WORK":
					case "BP.WF.GESTARTWORK":
					case "BP.EN.GENONAME":
					case "BP.EN.GETREE":
					case "BP.WF.GERpt":
					case "BP.WF.GEENTITY":
					case "BP.WF.GEWORK":
					case "BP.SYS.TSENTITYNONAME":
						continue;
					default:
						break;
				}
				String s = en.getEnDesc();
				if (en == null)
				{
					continue;
				}
			}
			catch (java.lang.Exception e)
			{
				continue;
			}

			if (en.toString() == null || en.toString().equals(""))
			{
				continue;
			}


			DataRow dr = dt.NewRow();

			dr.setValue("No", en.toString());
			try
			{
				dr.setValue("EnsName", en.GetNewEntities().toString());
			}
			catch (java.lang.Exception e2)
			{
				dr.setValue("EnsName", en.toString() + "s");
			}
			dr.setValue("Name", en.getEnMap().getEnDesc());
			dr.setValue("PTable", en.getEnMap().getPhysicsTable());
			dt.Rows.add(dr);
		}

		return bp.tools.Json.ToJson(dt);
	}

		///#endregion



		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
		String sfno = this.GetRequestVal("sfno");
		SFTable sftable = null;
		DataTable dt = null;
		StringBuilder s = null;

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


		///#region 数据源管理
	public final String SFDBSrcNewGuide_GetList() throws Exception {
		//SysEnums enums = new SysEnums(SFDBSrcAttr.DBSrcType);
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();

		return srcs.ToJson("dt");
	}

	public final String SFDBSrcNewGuide_LoadSrc() throws Exception {
		DataSet ds = new DataSet();

		SFDBSrc src = new SFDBSrc();
		if (!(this.GetRequestVal("No") == null || this.GetRequestVal("No").isEmpty()))
		{
			src = new SFDBSrc(getNo());
		}
		ds.Tables.add(src.ToDataTableField("SFDBSrc"));

		SysEnums enums = new SysEnums();
		enums.Retrieve(SysEnumAttr.EnumKey, SFDBSrcAttr.DBSrcType, SysEnumAttr.IntKey);
		ds.Tables.add(enums.ToDataTableField("DBSrcType"));

		return bp.tools.Json.ToJson(ds);
	}

	public final String SFDBSrcNewGuide_DelSrc() throws Exception {
		String no = this.GetRequestVal("No");

		//检验要删除的数据源是否有引用
		SFTables sfs = new SFTables();
		sfs.Retrieve(SFTableAttr.FK_SFDBSrc, no, null);

		if (!sfs.isEmpty())
		{
			//Alert("当前数据源已经使用，不能删除！");
			return "当前数据源已经使用，不能删除！";
			//return;
		}
		SFDBSrc src = new SFDBSrc(no);
		src.Delete();
		return "删除成功..";
	}

	//javaScript 脚本上传
	public final String javaScriptImp_Done()
	{
		File xmlFile = null;
		String fileName = "";
		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		if (contentType != null && contentType.indexOf("multipart/form-data") != -1)
		{
			fileName = CommonFileUtils.getOriginalFilename(request, "file");
			String savePath = SystemConfig.getPathOfDataUser() + "JSLibData" + "/" + fileName;
			xmlFile = new File(savePath);
			if (xmlFile.exists()) {
				xmlFile.delete();
			}
			try {
				CommonFileUtils.upload(request, "file", xmlFile);
			} catch (Exception e) {
				e.printStackTrace();
				return "err@执行失败";
			}
		}
		return "脚本" + fileName + "导入成功";
	}

	public final String RichUploadFile() throws Exception {
		File xmlFile = null;
		String fileName = "";
		String savePath = "";
		HttpServletRequest request = getRequest();
		String contentType = request.getContentType();
		String frmID = this.getFrmID();
		if (DataType.IsNullOrEmpty(frmID) == true)
			frmID = this.getEnName();
		String directory = frmID + "/" + this.getWorkID() + "/";

		if (contentType != null && contentType.indexOf("multipart/form-data") != -1) {
			fileName = DBAccess.GenerGUID(4) + ".jpg";
			savePath = SystemConfig.getPathOfDataUser()  + "UploadFile" + "/" + directory;;
			if (new File(savePath).exists() == false)
				new File(savePath).mkdirs();
			savePath = savePath + "/" + fileName;
			xmlFile = new File(savePath);
			if (xmlFile.exists()) {
				xmlFile.delete();
			}

			try {
				CommonFileUtils.upload(request, "edit", xmlFile);
			} catch (Exception e) {
				try{
					CommonFileUtils.upload(request, "Files", xmlFile);
				}catch(Exception e1){
					return "err@执行失败";
				}
			}
		}
		Hashtable ht = new Hashtable();
		ht.put("code", 0);
		ht.put("msg","success");
		savePath =  "DataUser/" + "UploadFile" + "/" + directory  + fileName;
		ht.put("data", savePath);
		return bp.tools.Json.ToJson(ht);
	}

	/**
	 * 获取已知目录下的文件列表
	 * @return
	 */
	public final String javaScriptFiles() throws Exception {
		String savePath = SystemConfig.getPathOfDataUser() + "JSLibData";

		File di = new File(savePath);
		//找到该目录下的文件
		File[] fileList = di.listFiles();

		if (fileList == null || fileList.length == 0)
		{
			return "";
		}
		DataTable dt = new DataTable();
		dt.Columns.Add("FileName");
		dt.Columns.Add("ChangeTime");
		for (File file : fileList)
		{
			DataRow dr = dt.NewRow();
			dr.setValue("FileName", file.getName());
			dr.setValue("ChangeTime",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(file.lastModified())));

			dt.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(dt);

	}

		///#endregion
}
