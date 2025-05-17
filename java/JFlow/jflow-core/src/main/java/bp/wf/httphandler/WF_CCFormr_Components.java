package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 页面功能实体
*/
public class WF_CCFormr_Components extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_CCFormr_Components()
	{

	}
	/** 
	 视图组件
	 
	 @return 
	*/
	public final String DataView_Init() throws Exception {
		bp.sys.frmui.MapAttrDataView en = new bp.sys.frmui.MapAttrDataView(this.getMyPK());

		String sql = en.GetValStringByKey(MapAttrAttr.DefaultVal);

		sql = bp.wf.Glo.DealExp(sql, null, null);

		if (this.getWorkID() != 0)
		{
			sql = sql.replace("@WorkID", String.valueOf(this.getWorkID()));
			sql = sql.replace("@OID", String.valueOf(this.getWorkID()));
		}

		if (this.getOID() != 0)
		{
			sql = sql.replace("@WorkID", String.valueOf(this.getOID()));
			sql = sql.replace("@OID", String.valueOf(this.getOID()));
		}

		if (this.getFID() != 0)
		{
			sql = sql.replace("@FID", String.valueOf(this.getFID()));
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

		///#region  公文文号 .
	/** 
	 初始化字号编辑器
	 
	 @return 当前的字号信息.
	*/
	public final String DocWord_Init() throws Exception {
		//创建实体.
		GEEntity en = new GEEntity(this.getFrmID(), this.getOID());

		//查询字段.
		String ptable = en.getEnMap().getPhysicsTable(); //获得存储表.

		//必须有4个列，分别是 DocWordKey字的外键,DocWordName字的名称,DocWordYear年度,DocWordLSH流水号,DocWord字号
		String sql = "SELECT DocWordKey,DocWordName,DocWordYear,DocWordLSH,DocWord FROM " + ptable + " WHERE OID=" + this.getOID();
		DataTable dt = new DataTable();
		try
		{
			dt = DBAccess.RunSQLReturnTable(sql);
		}
		catch (RuntimeException ex)
		{
			String repairSQL = ""; //修复表结构的sql.

			//如果没有此列就检查增加此列.
			if (DBAccess.IsExitsTableCol(ptable, "DocWordKey") == false)
			{
				repairSQL += "@ALTER TABLE " + ptable + " ADD DocWordKey varchar(100) ";
			}
			if (DBAccess.IsExitsTableCol(ptable, "DocWordName") == false)
			{
				repairSQL += "@ALTER TABLE " + ptable + " ADD  DocWordName varchar(100) ";
			}
			if (DBAccess.IsExitsTableCol(ptable, "DocWordYear") == false)
			{
				repairSQL += "@ALTER TABLE " + ptable + " ADD DocWordYear nvarchar(100) ";
			}
			if (DBAccess.IsExitsTableCol(ptable, "DocWordLSH") == false)
			{
				repairSQL += "@ALTER TABLE " + ptable + " ADD DocWordLSH nvarchar(100) ";
			}
			if (DBAccess.IsExitsTableCol(ptable, "DocWord") == false)
			{
				repairSQL += "@ALTER TABLE " + ptable + " ADD DocWord nvarchar(100) ";
			}

			if (DataType.IsNullOrEmpty(repairSQL) == false)
			{
				DBAccess.RunSQLs(repairSQL);
			}

			dt = DBAccess.RunSQLReturnTable(sql);
		}

		//处理大小写.
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "DocWordKey";
			dt.Columns.get(1).ColumnName = "DocWordName";
			dt.Columns.get(2).ColumnName = "DocWordYear";
			dt.Columns.get(3).ColumnName = "DocWordLSH";
			dt.Columns.get(4).ColumnName = "DocWord";
		}

		// 判断流水号是否未空.
		String key = dt.Rows.get(0).getValue("DocWordKey").toString();
		String name = dt.Rows.get(0).getValue("DocWordName").toString();
		String year = dt.Rows.get(0).getValue("DocWordYear").toString();
		String lsh = dt.Rows.get(0).getValue("DocWordLSH").toString();
		String word = dt.Rows.get(0).getValue("DocWord").toString();

		//如果year是空的就去当前年度.
		if (DataType.IsNullOrEmpty(year) == true)
		{
			year = DataType.getCurrentYear();
		}

		//流水号为空，就取当前年度的最大流水号.
		if (DataType.IsNullOrEmpty(lsh) == true)
		{
			//生成一个新的流水号.
			sql = "SELECT MAX(DocWordLSH) AS No FROM " + ptable + " WHERE DocWordKey='" + key + "' AND DocWordYear='" + year + "' AND OID!=" + this.getOID();
			lsh = DBAccess.RunSQLReturnStringIsNull(sql, "");
			if (DataType.IsNullOrEmpty(lsh) == true)
			{
				lsh = "";
			}

			dt.Rows.get(0).setValue("DocWordYear",year);
			dt.Rows.get(0).setValue("DocWordLSH",lsh);
		}

		//初始化数据.
		sql = "UPDATE " + ptable + " SET DocWordLSH='" + lsh + "', DocWordYear='" + year + "' WHERE OID=" + this.getOID();
		DBAccess.RunSQL(sql);
		//为了计算机中心做个性化处理为一下两个表名跟新orgno和orgname
		if (Objects.equals(ptable, "gov_receivefile"))
		{
			String sql1 = "UPDATE gov_receivefile set OrgNo='" + WebUser.getOrgNo() + "',OrgName='" + WebUser.getOrgName() + "' where oid=" + this.getOID();
			DBAccess.RunSQL(sql1);
		}
		if (Objects.equals(ptable, "gov_sendfilecopy"))
		{
			String sql1 = "UPDATE gov_sendfilecopy set OrgNo='" + WebUser.getOrgNo() + "',OrgName='" + WebUser.getOrgName() + "' where oid=" + this.getOID();
			DBAccess.RunSQL(sql1);
		}


		//转成Json，返回出去.
		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 重新生成字号
	 
	 @return 
	*/
	public final String DocWord_ReGenerDocWord() throws Exception {
		//创建实体.
		GEEntity en = new GEEntity(this.getFrmID(), this.getOID());

		//查询字段.
		String ptable = en.getEnMap().getPhysicsTable(); //获得存储表.

		String word = this.GetRequestVal("DDL_WordKey"); //字号
		String ny = this.GetRequestVal("DDL_Year"); //年月.

		//判断这个字号和年月是否已经存在这个表中，存在不要生成
		String sql = "SELECT DocWordLSH  FROM " + ptable + " WHERE DocWordKey='" + word + "' AND DocWordYear='" + ny + "' AND OID=" + this.getOID();
		String lsh = DBAccess.RunSQLReturnStringIsNull(sql,"");
		if (DataType.IsNullOrEmpty(lsh) == false)
		{
			return lsh;
		}

		//生成一个新的流水号.
		sql = "SELECT max(cast(DocWordLSH as UNSIGNED INTEGER)) AS No FROM " + ptable + " WHERE DocWordKey='" + word + "' AND DocWordYear='" + ny + "'";
		lsh = DBAccess.RunSQLReturnStringIsNull(sql, "");
		if (DataType.IsNullOrEmpty(lsh) == true)
		{
			return lsh = "1";
		}

	  int a = Integer.parseInt(lsh) + 1;
	   String str = String.valueOf(a);
		return str;
	}

	/** 
	 保存重新生成的字号和保存字号
	 
	 @return 
	*/
	public final String DocWord_Save() throws Exception {


		//创建实体.
		GEEntity en = new GEEntity(this.getFrmID(), this.getOID());

		//查询字段.
		String ptable = en.getEnMap().getPhysicsTable(); //获得存储表.


		String wordkey = this.GetRequestVal("DDL_WordKey"); //字号
		String wordname = this.GetRequestVal("DocWordName"); //DocWordName
		String ny = this.GetRequestVal("DDL_Year"); //年份.
		String lsh = this.GetRequestVal("TB_LSH"); //流水号.

		//检查一下这个流水号是否存在？
		String sql = "SELECT DocWordLSH  FROM " + ptable + " WHERE DocWordLSH=" + lsh + " AND DocWordKey='" + wordkey + "' AND DocWordYear='" + ny + "' AND OID!=" + this.getOID();
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (dt.Rows.size() != 0)
		{
			return "err@该文号[" + lsh + "]已经存在.";
		}

		String docword = wordname + "〔" + ny + "〕" + lsh + "号";


		//生成一个新的流水号.
		sql = "update " + ptable + " set DocWordKey='" + wordkey + "',DocWordName='" + wordname + "' ,DocWordYear='" + ny + "',DocWordLSH='" + lsh + "',DocWord='" + docword + "' WHERE OID=" + this.getOID();
		DBAccess.RunSQL(sql);
		return docword;
	}
	/** 
	 选择一个空闲的编号
	 
	 @return 
	*/
	public final String DocWord_GenerBlankNum() throws Exception {
		//创建实体.
		GEEntity en = new GEEntity(this.getFrmID(), this.getOID());

		//查询字段.
		String ptable = en.getEnMap().getPhysicsTable(); //获得存储表.

		String wordkey = this.GetRequestVal("DDL_WordKey"); //字号
		String wordname = this.GetRequestVal("DocWordName"); //DocWordName
		String ny = this.GetRequestVal("DDL_Year"); //年份.
		String lsh = this.GetRequestVal("TB_LSH"); //流水号.

		//生成一个新的流水号.
		String sql = "SELECT MAX(DocWordLSH) AS No FROM " + ptable + " WHERE DocWordKey='" + wordkey + "' AND DocWordYear='" + ny + "' AND OID!=" + this.getOID();
		lsh = DBAccess.RunSQLReturnStringIsNull(sql, "");
		if (DataType.IsNullOrEmpty(lsh) == true)
		{
			return "0";
		}
		String sqlmax = "SELECT MAX(DocWordLSH) AS No FROM " + ptable;
		String maxlsh = DBAccess.RunSQLReturnStringIsNull(sqlmax, "");
		//查询出来所有的流水号.
		DataTable dt = DBAccess.RunSQLReturnTable("select *  from Frm_ZhouPengDeKaiFaZheBiaoShan ORDER BY DocWordLSH");

		String num = "";

		for (int i = 1; i < Integer.parseInt(maxlsh); i++)
		{
			boolean isHave = false;

			for (DataRow dr : dt.Rows)
			{

				int lshNum = Integer.parseInt(dr.getValue(9).toString());
				if (lshNum == i)
				{
					isHave = true;
					break;
				}
				if (isHave == true || lshNum <= i || num.contains(String.valueOf(i)) == true)
				{
					continue;
				}

				//请严格按照000格式不够位数补0不然会影响其他地方 不允许使用000编号
				num += (new Integer(i)).toString(Integer.parseInt("000")) + ",";
			}
		}

		return num;
	}

		///#endregion 公文文号.

}
