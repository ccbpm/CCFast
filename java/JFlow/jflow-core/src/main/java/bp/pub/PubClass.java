package bp.pub;
import bp.difference.handler.CommonUtils;
import bp.en.*;
import bp.da.*;
import bp.difference.ContextHolderUtils;
import bp.difference.SystemConfig;
import bp.sys.*;
import bp.tools.StringHelper;
import bp.web.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLDecoder;

/**
 PageBase 的摘要说明。
 */
public class PubClass
{

	public static String ToHtmlColor(String colorName) throws Exception {
		try {
			if (colorName.startsWith("#")) {
				colorName = colorName.replace("#", "");
				// update by dgq 六位颜色不需要转换
				if (colorName.length() == 6 || colorName.length() == 3) {
					return "#" + colorName;
				} else if (colorName.length() == 8) {
					return "#" + colorName.substring(2, 8);
				} else {
					return "#" + colorName;
				}
			} else {
				return colorName;
			}
		} catch (java.lang.Exception e) {
			return "black";
		}
	}



	/**
	 处理字段

	 param fd
	 @return
	 */
	public static String DealToFieldOrTableNames(String fd)
	{
		String ptable = fd;
		String keys = "~!@#$%^&*()+{}|:<>?`=[];,./～！＠＃￥％……＆×（）——＋｛｝｜：“《》？｀－＝［］；＇，．／";
		char[] cc = keys.toCharArray();
		for (char c : cc)
		{
			fd = fd.replace(String.valueOf(c), "");
		}
		if (fd.length() <= 0)
		{
			throw new RuntimeException("存储表PTable为" + ptable + ",不合法");
		}
		String s = fd.substring(0, 1);
		try
		{

			int a = Integer.parseInt(s);
			fd = "F" + fd;
		}
		catch (java.lang.Exception e)
		{
		}
		return fd;
	}
	private static String _KeyFields = null;
	public static String getKeyFields() throws Exception {
		if (_KeyFields == null)
		{
			_KeyFields = bp.da.DataType.ReadTextFile(SystemConfig.getPathOfData() + "/Sys/FieldKeys.txt");
		}
		return _KeyFields;
	}
	public static boolean IsNum(String str)
	{
		boolean strResult;
		String cn_Regex = "^[\\u4e00-\\u9fa5]+$";

		Pattern pattern = Pattern.compile(cn_Regex);
		Matcher matcher = pattern.matcher(str);

		if (matcher.find()) {
			strResult = true;
		} else {
			strResult = false;
		}
		return strResult;
	}

	public static boolean IsCN(String str) {
		boolean strResult;
		String cn_Regex = "^[\\u4e00-\\u9fa5]+$";
		Pattern pattern = Pattern.compile(cn_Regex);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			strResult = true;
		} else {
			strResult = false;
		}
		return strResult;
	}

	public static boolean IsImg(String ext)
	{
		ext = ext.replace(".", "").toLowerCase();
		switch (ext)
		{
			case "gif":
				return true;
			case "jpg":
				return true;
			case "bmp":
				return true;
			case "png":
				return true;
			default:
				return false;
		}
	}
	/**
	 按照比例数小

	 param ObjH 目标高度
	 param factH 实际高度
	 param factW 实际宽度
	 @return 目标宽度
	 */
	public static int GenerImgW_del(int ObjH, int factH, int factW, int isZeroAsWith)
	{
		if (factH == 0 || factW == 0)
		{
			return isZeroAsWith;
		}
		int d = ObjH / (factH * factW);

		try
		{
			return d;
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException(d + ex.getMessage());
		}
	}

	/**
	 按照比例数小

	 param ObjW 目标高度
	 param factH 实际高度
	 param factW 实际宽度
	 @return 目标宽度
	 */
	public static int GenerImgH(int ObjW, int factH, int factW, int isZeroAsWith)
	{
		if (factH == 0 || factW == 0) {
			return isZeroAsWith;
		}

		int d = ObjW / (factW * factH);

		try {
			return d;
		} catch (RuntimeException ex) {
			throw new RuntimeException(d + ex.getMessage());
		}
	}
	/**
	 产生临时文件名称

	 param hz
	 @return
	  * @throws Exception
	 */
	public static String GenerTempFileName(String hz) throws Exception
	{
		return WebUser.getNo() + DataType.getCurrentDateByFormart("MMddhhmmss") + "." + hz;
	}
	public static void DeleteTempFiles()throws Exception {
		File file = new File(SystemConfig.getPathOfTemp());
		File[] strs = file.listFiles();

		for (File s : strs) {
			s.delete();
		}
	}
	/**
	 重新建立索引
	 * @throws Exception
	 */
	public static void ReCreateIndex() throws Exception
	{
		ArrayList als = ClassFactory.GetObjects("bp.en.*");
		String sql = "";
		for (Object obj : als)
		{
			Entity en = (Entity)obj;
			if (en.getEnMap().getEnType() == EnType.View)
			{
				continue;
			}
			sql += "IF EXISTS( SELECT name  FROM  sysobjects WHERE  name='" + en.getEnMap().getPhysicsTable() + "') <BR> DROP TABLE " + en.getEnMap().getPhysicsTable() + "<BR>";
			sql += "CREATE TABLE " + en.getEnMap().getPhysicsTable() + " ( <BR>";
			sql += "";
		}


	}


	/**
	 获取datatable.

	 param uiBindKey
	 @return
	  * @throws Exception
	 */

	public static DataTable GetDataTableByUIBineKey(String uiBindKey) throws Exception
	{
		return GetDataTableByUIBineKey(uiBindKey, null);
	}


	public static DataTable GetDataTableByUIBineKey(String uiBindKey, Hashtable ht) throws Exception
	{
		DataTable dt = new DataTable();
		if (uiBindKey.contains(".")) {
			Entities ens = bp.en.ClassFactory.GetEns(uiBindKey);
			if (ens == null)
				ens = bp.en.ClassFactory.GetEns(uiBindKey);

			if (ens == null)
				ens = bp.en.ClassFactory.GetEns(uiBindKey);
			if (ens == null)
				throw new Exception("类名错误:" + uiBindKey + ",不能转化成ens.");

			ens.RetrieveAllFromDBSource();
			dt = ens.ToDataTableField(uiBindKey);
			return dt;
		}

		// 增加动态SQL查询类型的处理，此种类型没有固定的数据表或视图
		SFTable sf = new SFTable();
		sf.setNo(uiBindKey);
		if (sf.RetrieveFromDBSources() != 0) {
			if (sf.getSrcType() == bp.sys.DictSrcType.Handler || sf.getSrcType() == bp.sys.DictSrcType.JQuery)
				return null;
			dt = sf.GenerHisDataTable(ht);
		}
		if (dt == null)
			dt = new DataTable();

		// 把列名做成标准的.
		for (DataColumn col : dt.Columns) {
			String colName = col.ColumnName.toLowerCase();

			if (colName.equals("no") == true)
				col.ColumnName = "No";

			if (colName.equals("name") == true)
				col.ColumnName = "Name";

			if (colName.equals("parentno") == true)
				col.ColumnName = "ParentNo";

		}
		//  把列名做成标准的.

		dt.setTableName(uiBindKey);
		return dt;
	}



	///系统调度
	public static String GenerDBOfOreacle() throws Exception
	{
		ArrayList<Entity> als = ClassFactory.GetObjects("bp.en.Entity");
		String sql = "";
		for (Entity en : als) {
			sql += "IF EXISTS( SELECT name  FROM  sysobjects WHERE  name='" + en.getEnMap().getPhysicsTable()
					+ "') <BR> DROP TABLE " + en.getEnMap().getPhysicsTable() + "<BR>";
			sql += "CREATE TABLE " + en.getEnMap().getPhysicsTable() + " ( <BR>";
			sql += "";
		}
		return sql;
	}
	public static String DBRpt(DBCheckLevel level) throws Exception
	{
		// 取出全部的实体
		ArrayList<Entity> als = ClassFactory.GetObjects("bp.en.Entity");
		String msg = "";
		for (Object obj : als)
		{
			Entities ens = (Entities)obj;
			try
			{
				msg += DBRpt1(level, ens);
			}
			catch (RuntimeException ex)
			{
				msg += "<hr>" + ens.toString() + "体检失败:" + ex.getMessage();
			}
		}

		MapDatas mds = new MapDatas();
		mds.RetrieveAllFromDBSource();
		for (bp.sys.MapData md : mds.ToJavaList())
		{
			try
			{
				md.getHisGEEn().CheckPhysicsTable();
				PubClass.AddComment(md.getHisGEEn());
			}
			catch (RuntimeException ex)
			{
				msg += "<hr>" + md.getNo() + "体检失败:" + ex.getMessage();
			}
		}

		MapDtls dtls = new MapDtls();
		dtls.RetrieveAllFromDBSource();
		for (MapDtl dtl : dtls.ToJavaList())
		{
			try
			{
				dtl.getHisGEDtl().CheckPhysicsTable();
				PubClass.AddComment(dtl.getHisGEDtl());
			}
			catch (RuntimeException ex)
			{
				msg += "<hr>" + dtl.getNo() + "体检失败:" + ex.getMessage();
			}
		}


		///检查处理必要的基础数据 Pub_Day .
		String sql = "";
		String sqls = "";
		sql = "SELECT count(*) Num FROM Pub_Day";
		try
		{
			if (DBAccess.RunSQLReturnValInt(sql) == 0)
			{
				for (int i = 1; i <= 31; i++)
				{
					String d = StringHelper.padLeft(String.valueOf(i), 2, '0');
					sqls += "@INSERT INTO Pub_Day(No,Name)VALUES('" + d.toString() + "','" + d.toString() + "')";
				}
			}
		}
		catch (java.lang.Exception e)
		{
		}

		sql = "SELECT count(*) Num FROM Pub_YF";
		try
		{
			if (DBAccess.RunSQLReturnValInt(sql) == 0)
			{
				for (int i = 1; i <= 12; i++)
				{
					String d = StringHelper.padLeft(String.valueOf(i), 2, '0');
					sqls += "@INSERT INTO Pub_YF(No,Name)VALUES('" + d.toString() + "','" + d.toString() + "')";
				}
			}
		}
		catch (java.lang.Exception e2)
		{
		}

		sql = "SELECT count(*) Num FROM Pub_ND";
		try
		{
			if (DBAccess.RunSQLReturnValInt(sql) == 0)
			{
				for (int i = 2010; i < 2015; i++)
				{
					String d = String.valueOf(i);
					sqls += "@INSERT INTO Pub_ND(No,Name)VALUES('" + d.toString() + "','" + d.toString() + "')";
				}
			}
		}
		catch (java.lang.Exception e3)
		{

		}
		sql = "SELECT count(*) Num FROM Pub_NY";
		try
		{
			if (DBAccess.RunSQLReturnValInt(sql) == 0)
			{
				for (int i = 2010; i < 2015; i++)
				{

					for (int yf = 1; yf <= 12; yf++)
					{
						String d = String.valueOf(i) + "-" + StringHelper.padLeft(String.valueOf(yf), 2, '0');
						sqls += "@INSERT INTO Pub_NY(No,Name)VALUES('" + d + "','" + d + "')";
					}
				}
			}
		}
		catch (java.lang.Exception e4)
		{
		}

		DBAccess.RunSQLs(sqls);

		/// 检查处理必要的基础数据。
		return msg;
	}
	private static void RepleaceFieldDesc(Entity en) throws Exception
	{
		String tableId = DBAccess.RunSQLReturnVal("select ID from sysobjects WHERE name='" + en.getEnMap().getPhysicsTable() + "' AND xtype='U'").toString();

		if (tableId == null || tableId.equals(""))
		{
			return;
		}

		for (Attr attr : en.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

		}
	}
	/**
	 为表增加注释

	 @return
	  * @throws Exception
	 */
	public static String AddComment() throws Exception
	{
		// 取出全部的实体
		ArrayList<Entities> als = ClassFactory.GetObjects("bp.en.Entities");

		String msg = "";
		Entity en = null;
		Entities ens = null;
		for (Object obj : als)
		{
			ens = obj instanceof Entities ? (Entities) obj : null;
			if (ens == null) {
				continue;
			}
			try
			{
				String className = ens.getClass().getName();
				switch (className.toUpperCase()) {
					case "BP.WF.STARTWORKS":
					case "BP.WF.WORKS":
					case "BP.WF.GESTARTWORKS":
					case "BP.EN.GENONAMES":
					case "BP.EN.GETREES":
					case "BP.WF.GERptS":
					case "BP.WF.GEENTITYS":
					case "BP.WF.GEWORKS":
					case "BP.SYS.TSENTITYNONAMES":
						continue;
					default:
						break;
				}
				en = ens.getNewEntity();
				if (en.getEnMap().getEnType() == EnType.View || en.getEnMap().getEnType() == EnType.ThirdPartApp)
				{
					continue;
				}
			}
			catch (java.lang.Exception e)
			{
				continue;
			}
			if(en.equals("null"))
				continue;
			msg += AddComment(en);
		}
		return msg;
	}
	public static String AddComment(Entity en) throws Exception
	{

		if (en == null)
		{
			return "实体错误 en=null ";
		}
		if (en.getEnMap() == null)
		{
			return "实体错误 en.getEnMap=null ";
		}
		if (en.getEnMap().getPhysicsTable() == null)
		{
			return "实体错误 en.getEnMap.getPhysicsTable=null ";
		}


		if (DBAccess.IsExitsObject(en.getEnMap().getPhysicsTable()) == false)
		{
			return "实体表不存在.";
		}

		try
		{
			switch (en.getEnMap().getEnDBUrl().getDBType())
			{
				case Oracle:
				case DM:
				case KingBaseR3:
				case KingBaseR6:
				case GBASE8CByOracle:
					AddCommentForTable_Ora(en);
					break;
				case MySQL:
				case GBASE8CByMySQL:
				case GBASE8A:
					AddCommentForTable_MySql(en);
					break;
				case PostgreSQL:
				case HGDB:
					AddCommentForTable_PSQL(en);
					break;
				default:
					AddCommentForTable_MS(en);
					break;
			}
			return "";
		}
		catch (RuntimeException ex)
		{
			return "<hr>" + en.toString() + "体检失败:" + ex.getMessage();
		}
	}
	private static void AddCommentForTable_Ora(Entity en) throws Exception
	{


		en.RunSQL("comment on table " + en.getEnMap().getPhysicsTable() + " IS '" + en.getEnDesc() + "'");
		SysEnums ses = new SysEnums();
		for (Attr attr : en.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}

			if (DBAccess.IsExitsTableCol(en.getEnMap().getPhysicsTable(), attr.getField()) == false)
			{
				continue;
			}


			switch (attr.getMyFieldType())
			{
				case PK:
					en.RunSQL("comment on column  " + en.getEnMap().getPhysicsTable() + "." + attr.getField() + " IS '" + attr.getDesc() + "-主键'");
					break;
				case Normal:
					en.RunSQL("comment on column  " + en.getEnMap().getPhysicsTable() + "." + attr.getField() + " IS '" + attr.getDesc() + "'");
					break;
				case Enum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					en.RunSQL("comment on column  " + en.getEnMap().getPhysicsTable() + "." + attr.getField() + " IS '" + attr.getDesc() + ",枚举类型:" + ses.ToDesc() + "'");
					break;
				case PKEnum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					en.RunSQL("comment on column  " + en.getEnMap().getPhysicsTable() + "." + attr.getField() + " IS '" + attr.getDesc() + ", 主键:枚举类型:" + ses.ToDesc() + "'");
					break;
				case FK:
					Entity myen = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					en.RunSQL("comment on column  " + en.getEnMap().getPhysicsTable() + "." + attr.getField() + " IS '" + attr.getDesc() + ", 外键:对应物理表:" + myen.getEnMap().getPhysicsTable() + ",表描述:" + myen.getEnDesc()+"'");
					break;
				case PKFK:
					Entity myen1 = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					en.RunSQL("comment on column  " + en.getEnMap().getPhysicsTable() + "." + attr.getField() + " IS '" + attr.getDesc() + ", 主外键:对应物理表:" + myen1.getEnMap().getPhysicsTable() + ",表描述:" + myen1.getEnDesc() + "'");
					break;
				default:
					break;
			}
		}
	}


	public static void AddCommentForTable_MySql(Entity en) throws Exception {
		String database = SystemConfig.getAppCenterDBDatabase();
		if(DataType.IsNullOrEmpty(en.getEnMap().getPhysicsTable())) return;
		en.RunSQL("alter table " + database + "." + en.getEnMap().getPhysicsTable() + " comment = '" + en.getEnDesc()
				+ "'");
		// 获取当前实体对应表的所有字段结构信息
		DataTable cols = DBAccess.RunSQLReturnTable(
				"select column_name,column_default,is_nullable,character_set_name,column_type from information_schema.columns where table_schema = '"
						+ database + "' and table_name='" + en.getEnMap().getPhysicsTable() + "'");
		SysEnums ses = new SysEnums();
		String sql = "";
		DataRow row = null;

		for (Attr attr : en.getEnMap().getAttrs()) {
			if (attr.getMyFieldType() == FieldType.RefText) {
				continue;
			}

			row = cols.select(String.format("column_name='%1$s'", attr.getField())).get(0);
			sql = String.format("ALTER TABLE %1$s.%2$s CHANGE COLUMN %3$s %3$s %4$s%5$s%6$s%7$s COMMENT ", database,
					en.getEnMap().getPhysicsTable(), attr.getField(),
					row.getValue("column_type").toString().toUpperCase(),
					row.getValue("character_set_name").equals("utf8") ? " CHARACTER SET 'utf8'" : "",
					row.getValue("is_nullable").equals("YES") ? " NULL" : " NOT NULL",
					row.getValue("column_default").equals("NULL") ? " DEFAULT NULL" : (row.getValue("").equals("") ? ""
							: " DEFAULT " + row.getValue((""))));

			switch (attr.getMyFieldType()) {
				case PK:
					en.RunSQL(sql + String.format("'%1$s - 主键'", attr.getDesc()));
					break;
				case Normal:
					en.RunSQL(sql + String.format("'%1$s'", attr.getDesc()));
					break;
				case Enum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					en.RunSQL(sql + String.format("'%1$s,枚举类型:%2$s'", attr.getDesc(), ses.ToDesc()));
					break;
				case PKEnum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					en.RunSQL(sql + String.format("'%1$s,主键:枚举类型:%2$s'", attr.getDesc(), ses.ToDesc()));
					break;
				case FK:
					Entity myen = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					en.RunSQL(sql + String.format("'%1$s,外键:对应物理表:%2$s,表描述:%3$s'", attr.getDesc(),
							myen.getEnMap().getPhysicsTable(), myen.getEnDesc()));
					break;
				case PKFK:
					Entity myen1 = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					en.RunSQL(sql + String.format("'%1$s,主外键:对应物理表:%2$s,表描述:%3$s'", attr.getDesc(),
							myen1.getEnMap().getPhysicsTable(), myen1.getEnDesc()));
					break;
				default:
					break;
			}
		}
	}

	public static void AddCommentForTable_PSQL(Entity en) throws Exception {
		String database = SystemConfig.getAppCenterDBDatabase();
		if(DataType.IsNullOrEmpty(en.getEnMap().getPhysicsTable())) return;
		//给表增加注释
		en.RunSQL("comment on table " + en.getEnMap().getPhysicsTable().toLowerCase() + " is '" + en.getEnDesc()
				+ "'");
		// 获取当前实体对应表的所有字段结构信息
		DataTable cols = DBAccess.RunSQLReturnTable(
				"select column_name,column_default,is_nullable,character_set_name,data_type from information_schema.columns where  table_name='" + en.getEnMap().getPhysicsTable().toLowerCase() + "'");
		SysEnums ses = new SysEnums();
		String sql = "";
		DataRow row = null;

		for (Attr attr : en.getEnMap().getAttrs()) {
			if (attr.getMyFieldType() == FieldType.RefText) {
				continue;
			}

			row = cols.select(String.format("column_name='%1$s'", attr.getField())).get(0);
			sql = String.format("comment on column %1$s.%2$s is ", en.getEnMap().getPhysicsTable().toLowerCase(), attr.getField());

			switch (attr.getMyFieldType()) {
				case PK:
					en.RunSQL(sql + String.format("'%1$s - 主键'", attr.getDesc()));
					break;
				case Normal:
					en.RunSQL(sql + String.format("'%1$s'", attr.getDesc()));
					break;
				case Enum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					en.RunSQL(sql + String.format("'%1$s,枚举类型:%2$s'", attr.getDesc(), ses.ToDesc()));
					break;
				case PKEnum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					en.RunSQL(sql + String.format("'%1$s,主键:枚举类型:%2$s'", attr.getDesc(), ses.ToDesc()));
					break;
				case FK:
					Entity myen = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					en.RunSQL(sql + String.format("'%1$s,外键:对应物理表:%2$s,表描述:%3$s'", attr.getDesc(),
							myen.getEnMap().getPhysicsTable(), myen.getEnDesc()));
					break;
				case PKFK:
					Entity myen1 = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					en.RunSQL(sql + String.format("'%1$s,主外键:对应物理表:%2$s,表描述:%3$s'", attr.getDesc(),
							myen1.getEnMap().getPhysicsTable(), myen1.getEnDesc()));
					break;
				default:
					break;
			}
		}
	}
	private static void AddColNote(Entity en, String table, String col, String note) throws Exception
	{
		try
		{
			String sql = "execute  sp_addextendedproperty 'MS_Description', '" + note + "', 'user', dbo, 'table', '" + table + "', 'column', '" + col + "'";
			en.RunSQL(sql);
		}
		catch (RuntimeException ex)
		{
			String sql = "execute  sp_updateextendedproperty 'MS_Description', '" + note + "', 'user', dbo, 'table', '" + table + "', 'column', '" + col + "'";
			en.RunSQL(sql);
		}

	}
	/**
	 为表增加解释

	 param en
	 * @throws Exception
	 */
	public static void AddCommentForTable_MS(Entity en) throws Exception
	{

		if (en.getEnMap().getEnType() == EnType.View || en.getEnMap().getEnType() == EnType.ThirdPartApp)
			return;

		try
		{
			String sql = "execute  sp_addextendedproperty 'MS_Description', '" + en.getEnDesc() + "', 'user', dbo, 'table', '" + en.getEnMap().getPhysicsTable() + "'";
			en.RunSQL(sql);
		}
		catch (RuntimeException ex)
		{
			String sql = "execute  sp_updateextendedproperty 'MS_Description', '" + en.getEnDesc() + "', 'user', dbo, 'table', '" + en.getEnMap().getPhysicsTable() + "'";
			en.RunSQL(sql);
		}

		SysEnums ses = new SysEnums();
		for (Attr attr : en.getEnMap().getAttrs())
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			if (attr.getKey().equals(attr.getDesc()))
			{
				continue;
			}
			if (attr.getField().equals(attr.getDesc()))
			{
				continue;
			}

			switch (attr.getMyFieldType())
			{
				case PK:
					AddColNote(en, en.getEnMap().getPhysicsTable(), attr.getField(), attr.getDesc() + "(主键)");
					//en.RunSQL("comment on table "+ en.getEnMap().getPhysicsTable()+"."+attr.Field +" IS '"+en.EnDesc+"'");
					break;
				case Normal:
					AddColNote(en, en.getEnMap().getPhysicsTable(), attr.getField(), attr.getDesc());
					//en.RunSQL("comment on table "+ en.getEnMap().getPhysicsTable()+"."+attr.Field +" IS '"+en.EnDesc+"'");
					break;
				case Enum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					//	en.RunSQL("comment on table "+ en.getEnMap().getPhysicsTable()+"."+attr.Field +" IS '"++"'" );
					AddColNote(en, en.getEnMap().getPhysicsTable(), attr.getField(), attr.getDesc() + ",枚举类型:" + ses.ToDesc());
					break;
				case PKEnum:
					ses = new SysEnums(attr.getKey(), attr.UITag);
					AddColNote(en, en.getEnMap().getPhysicsTable(), attr.getField(), attr.getDesc() + ",主键:枚举类型:" + ses.ToDesc());
					//en.RunSQL("comment on table "+ en.getEnMap().getPhysicsTable()+"."+attr.Field +" IS '"+en.EnDesc+", 主键:枚举类型:"+ses.ToDesc()+"'" );
					break;
				case FK:
					Entity myen = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					AddColNote(en, en.getEnMap().getPhysicsTable(), attr.getField(), attr.getDesc() + ", 外键:对应物理表:" + myen.getEnMap().getPhysicsTable() + ",表描述:" + myen.getEnDesc());
					//en.RunSQL("comment on table "+ en.getEnMap().getPhysicsTable()+"."+attr.Field +" IS "+  );
					break;
				case PKFK:
					Entity myen1 = attr.getHisFKEn(); // ClassFactory.GetEns(attr.getUIBindKey()).getGetNewEntity();
					AddColNote(en, en.getEnMap().getPhysicsTable(), attr.getField(), attr.getDesc() + ", 主外键:对应物理表:" + myen1.getEnMap().getPhysicsTable() + ",表描述:" + myen1.getEnDesc());
					//en.RunSQL("comment on table "+ en.getEnMap().getPhysicsTable()+"."+attr.Field +" IS '"+  );
					break;
				default:
					break;
			}
		}
	}
	/**
	 产程系统报表，如果出现问题，就写入日志里面。

	 @return
	  * @throws Exception
	 */
	public static String DBRpt1(DBCheckLevel level, Entities ens) throws Exception
	{
		Entity en = ens.getNewEntity();
		if (en.getEnMap().getEnDBUrl().getDBUrlType() != DBUrlType.AppCenterDSN)
		{
			return null;
		}

		if (en.getEnMap().getEnType() == EnType.ThirdPartApp)
		{
			return null;
		}

		if (en.getEnMap().getEnType() == EnType.View)
		{
			return null;
		}

		if (en.getEnMap().getEnType() == EnType.Ext)
		{
			return null;
		}

		// 检测物理表的字段。
		en.CheckPhysicsTable();

		PubClass.AddComment(en);

		return "";
	}




	/**
	 转换

	 param ht
	 @return
	 */
	public static DataTable HashtableToDataTable(Hashtable ht)throws Exception
	{
		DataTable dt = new DataTable();
		dt.setTableName("Hashtable");
		for (Object key : ht.keySet())
		{
			if(key == null)
				continue;
			dt.Columns.Add(key.toString(), String.class);
		}

		DataRow dr = dt.NewRow();
		for (Object key : ht.keySet())
		{
			if(key == null)
				continue;
			dr.setValue(key.toString(), ht.get(key) instanceof String ? (String)ht.get(key) : null);
		}
		dt.Rows.add(dr);
		return dt;
	}


	public static Hashtable GetMainTableHT() throws UnsupportedEncodingException
	{
		Hashtable htMain = new Hashtable();
		for (String key : CommonUtils.getRequest().getParameterMap().keySet())
		{
			if (key == null)
			{
				continue;
			}

			String myKey = key;
			String val = ContextHolderUtils.getRequest().getParameter(key);
			myKey = myKey.replace("TB_", "");
			myKey = myKey.replace("DDL_", "");
			myKey = myKey.replace("CB_", "");
			myKey = myKey.replace("RB_", "");
			if(DataType.IsNullOrEmpty(val)==false)
				val = URLDecoder.decode(val, "UTF-8");

			if (htMain.containsKey(myKey) == true)
			{
				htMain.put(myKey, val);
			}
			else
			{
				htMain.put(myKey, val);
			}
		}
		return htMain;
	}
	public static bp.en.Entity CopyFromRequest(bp.en.Entity en) throws Exception
	{
		ArrayList<String> requestKeys = new ArrayList<String>();
		HttpServletRequest request = ContextHolderUtils.getRequest();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			// 判断是否有内容，hasNext()
			String key = (String) enu.nextElement();
			requestKeys.add(key);
		}

		// 给每个属性值.
		Attrs attrs = en.getEnMap().getAttrs();
		for (Attr item : attrs.ToJavaList()) {
			String relKey = null;
			switch (item.getUIContralType()) {
				case TB:
					relKey = "TB_" + item.getKey(); // 不要改成小写，否则表单数据无法保存
					break;
				case CheckBok:
					relKey = "CB_" + item.getKey(); // 不要改成小写，否则表单数据无法保存
					break;
				case DDL:
					relKey = "DDL_" + item.getKey(); // 不要改成小写，否则表单数据无法保存
					break;
				case RadioBtn:
					relKey = "RB_" + item.getKey(); // 不要改成小写，否则表单数据无法保存
					break;
				default:
					break;
			}

			if (requestKeys.contains(relKey)) {

				int index = requestKeys.indexOf(relKey);
				// 说明已经找到了这个字段信息。
				String myK = requestKeys.get(index);

				if (myK == null || myK.equals("")) {
					continue;
				}

				if (item.getUIContralType() == UIContralType.CheckBok) {
					String val = request.getParameter(myK);
					if (val.equals("on") || val.equals("1")) {
						en.SetValByKey(item.getKey(), 1);
					} else {
						en.SetValByKey(item.getKey(), 0);
					}
				} else {
					String value = request.getParameter(myK).trim();
					en.SetValByKey(item.getKey(), value);
				}
				continue;
			} else {
				if (null != relKey && relKey.contains("CB_")) {
					en.SetValByKey(item.getKey(), 0);
				}
			}
		}
		return en;
	}

	public static bp.en.Entity CopyFromRequestByPost(bp.en.Entity en) throws Exception
	{
		String allKeys = ";";
		// 获取传递来的所有的checkbox ids 用于设置该属性为false.
		String checkBoxIDs = ContextHolderUtils.getRequest().getParameter("CheckBoxIDs");
		if (checkBoxIDs != null) {
			String[] strs = checkBoxIDs.split(",");
			for (String str : strs) {
				if (str == null || "".equals(str))
					continue;

				if (str.contains("CBPara")) {
					/* 如果是参数字段. */
					en.getRow().SetValByKey(str.replace("CBPara_", ""), 0);
				} else {
					// 设置该属性为false.
					en.getRow().SetValByKey(str.replace("CB_", ""), 0);
				}

			}
		}

		for (Iterator iter = ContextHolderUtils.getRequest().getParameterMap().keySet().iterator(); iter.hasNext();) {
			String myK = (String) iter.next();
			allKeys += myK + ";";
		}

		// 给每个属性值.
		Attrs attrs = en.getEnMap().getAttrs();
		for (Attr item : attrs.ToJavaList()) {
			String relKey = null;
			switch (item.getUIContralType()) {
				case TB:
					relKey = "TB_" + item.getKey();
					break;
				case CheckBok:
					relKey = "CB_" + item.getKey();
					break;
				case DDL:
					relKey = "DDL_" + item.getKey();
					break;
				case RadioBtn:
					relKey = "RB_" + item.getKey();
					break;
				default:
					break;
			}

			if (relKey == null) {
				continue;
			}

			if (allKeys.contains(relKey + ";")) {
				// 说明已经找到了这个字段信息。
				for (Iterator iter = ContextHolderUtils.getRequest().getParameterMap().keySet().iterator(); iter.hasNext();) {
					String myK = (String) iter.next();
					if (myK == null || "".equals(myK)) {
						continue;
					}
					String val = ContextHolderUtils.getRequest().getParameter(myK);
					if (myK.endsWith(relKey)) {
						if (item.getUIContralType() == UIContralType.CheckBok) {
							if (myK.indexOf("CB_") == 0 || myK.indexOf("CBPara_") == 0) {
								en.SetValByKey(item.getKey(), 1);
							}
						} else {
							en.SetValByKey(item.getKey(), val);
						}
					}
				}
				continue;
			}
		}
		return en;
	}

	/**
	 明细表传参保存

	 param en
	 param pk
	 param map
	 @return
	  * @throws Exception
	 */
	public static Entity CopyDtlFromRequests(Entity en, String pk, bp.en.Map map) throws Exception
	{
		String allKeys = ";";
		if (pk == null || pk.equals(""))
		{
			pk = "";
		}
		else
		{
			pk = "_" + pk;
		}

		for (Iterator iter = ContextHolderUtils.getRequest().getParameterMap().keySet().iterator(); iter.hasNext();) {
			String myK = (String) iter.next();
			allKeys += myK + ";";
		}



		Attrs attrs = map.getAttrs();
		for (Attr attr : attrs)
		{
			String relKey = null;
			switch (attr.getUIContralType())
			{
				case TB:
					relKey = "TB_" + attr.getKey() + pk;
					break;
				case CheckBok:
					relKey = "CB_" + attr.getKey() + pk;
					break;
				case DDL:
					relKey = "DDL_" + attr.getKey() + pk;
					break;
				case RadioBtn:
					relKey = "RB_" + attr.getKey() + pk;
					break;
				case MapPin:
					relKey = "TB_" + attr.getKey() + pk;
					break;
				default:
					break;
			}

			if (relKey == null)
			{
				continue;
			}

			if (allKeys.contains(relKey + ";"))
			{
				/*说明已经找到了这个字段信息。*/
				for (Iterator iter = ContextHolderUtils.getRequest().getParameterMap().keySet().iterator(); iter.hasNext();)
				{
					String myK = (String) iter.next();
					if (myK.endsWith(relKey))
					{
						if (attr.getUIContralType() == UIContralType.CheckBok)
						{
							String val = ContextHolderUtils.getRequest().getParameter(myK);
							if (val.equals("on") || val.equals("1") || val.contains(",on"))
							{
								en.SetValByKey(attr.getKey(), 1);
							}
							else
							{
								en.SetValByKey(attr.getKey(), 0);
							}
						}
						else
						{
							en.SetValByKey(attr.getKey(), ContextHolderUtils.getRequest().getParameter(myK));
						}
					}
				}
				continue;
			}
		}
		return en;
	}


	public static void DownloadFile(String filepath, String tempName) throws Exception {

		// 设置文件MIME类型
		HttpServletResponse response = ContextHolderUtils.getResponse();
		HttpServletRequest request = ContextHolderUtils.getRequest();

		tempName = toUtf8String(request, tempName);

		File myfile = new File(filepath);
		if (myfile.exists() == false)
			throw new Exception("err@文件名不存在:" + filepath);

		response.reset();
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + tempName);
		response.setHeader("Connection", "close");
		// 读取目标文件，通过response将目标文件写到客户端
		// 读取文件
		InputStream in = new FileInputStream(myfile);
		OutputStream out = response.getOutputStream();
		// 写文件
		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}
		in.close();
		out.close();
	}

	// 不同浏览器编码问题 add by qin 15.10.20
	public static String toUtf8String(HttpServletRequest request, String s) {
		String agent = request.getHeader("User-Agent");
		try {
			boolean isFireFox = (agent != null && agent.toLowerCase().indexOf("firefox") != -1);
			if (isFireFox) {
				s = new String(s.getBytes("UTF-8"), "ISO8859-1");
			} else {
				s = toUtf8String(s);
				if ((agent != null && agent.indexOf("MSIE") != -1)) {
					// see http://support.microsoft.com/default.aspx?kbid=816868
					if (s.length() > 150) {
						// 根据request的locale 得出可能的编码
						s = new String(s.getBytes("UTF-8"), "ISO8859-1");
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String toUtf8String(String s) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					// exceptionUtil.error("将文件名中的汉字转为UTF8编码的串时错误，输入的字符串为：" +
					// s);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}


}
