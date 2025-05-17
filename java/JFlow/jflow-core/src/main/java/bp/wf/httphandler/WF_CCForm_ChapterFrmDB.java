package bp.wf.httphandler;

import bp.da.*;
import bp.difference.*;
import bp.difference.handler.DirectoryPageBase;
import bp.sys.*;
import bp.sys.CCFormAPI;
/** 
 页面功能实体
*/
public class WF_CCForm_ChapterFrmDB extends DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_CCForm_ChapterFrmDB() throws Exception {

	}

	public final String ChapterFrmDB_Init() throws Exception {
		DataSet ds = new DataSet();
		GEEntityOID en = new GEEntityOID(this.getFrmID());
		en.setOID(this.getOID());
		if (en.RetrieveFromDBSources() == 0)
		{
			en.InsertAsOID(this.getOID());
		}
		MapData md = new MapData(this.getFrmID());
		ds.Tables.add(md.ToDataTableField("Sys_MapData"));
		//获取分组信息
		GroupFields gfs = new GroupFields();
		gfs.Retrieve(GroupFieldAttr.FrmID, this.getFrmID(), "Idx");

		MapDtls dtls = md.getMapDtls();
		//获取字段信息
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve(MapAttrAttr.FK_MapData, this.getFrmID(), MapAttrAttr.MyDataType, 1, MapAttrAttr.UIVisible, 1, "Idx");

		int mainVer = this.GetRequestValInt("MainVer");
		int compareVer = this.GetRequestValInt("CompareVer");
		if (mainVer == 0 || compareVer == 0)
		{
			return "err@比对数据库的版本不正确:比对版本1=Ver" + mainVer + ".0 比对版本2=Ver" + compareVer + ".0";
		}

		//获取主版本的所有信息
		FrmDBVers mainVers = new FrmDBVers();
		mainVers.Retrieve("FrmID", this.getFrmID(), "RefPKVal", this.getOID(), "Ver", mainVer, null);

		FrmDBVers compareVers = new FrmDBVers();
		compareVers.Retrieve("FrmID", this.getFrmID(), "RefPKVal", this.getOID(), "Ver", compareVer, null);

		MapAttrs newAttrs = new MapAttrs();
		//获取历史版本数据
		String mainStr = "";
		String compareStr = "";
		for (MapAttr attr : attrs.ToJavaList())
		{
			if (attr.getUIVisible() == false)
			{
				continue;
			}
			mainStr = GetBigTextByKeyOfEn(attr.getKeyOfEn(), mainVers);
			compareStr = GetBigTextByKeyOfEn(attr.getKeyOfEn(), compareVers);
			if (mainStr.equals(compareStr) == false)
			{
				attr.SetPara("MainStr", mainStr);
				attr.SetPara("CompareStr", compareStr);
				newAttrs.AddEntity(attr);
				continue;
			}
		}
		ds.Tables.add(newAttrs.ToDataTableField("MapAttrs"));
		ds.Tables.add(mainVers.ToDataTableField("MainVers"));
		ds.Tables.add(compareVers.ToDataTableField("CompareVers"));

		//获取表单的主版本版本号
		String mainVerPK = "";
		for (FrmDBVer ver : mainVers.ToJavaList())
		{
			if (DataType.IsNullOrEmpty(ver.getKeyOfEn()) == true)
			{
				mainVerPK = ver.getMyPK();
				break;
			}
		}
		String compareVerPK = "";
		for (FrmDBVer ver : compareVers.ToJavaList())
		{
			if (DataType.IsNullOrEmpty(ver.getKeyOfEn()) == true)
			{
				compareVerPK = ver.getMyPK();
				break;
			}
		}

		//获取从表的数据
		mainStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", mainVerPK, "FrmDtlDB");
		compareStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", compareVerPK, "FrmDtlDB");
		gfs = GetNewGFS(gfs, mainStr, compareStr, "Dtl", dtls);

		//获取附件的数据
		mainStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", mainVerPK, "FrmAthDB");
		compareStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", compareVerPK, "FrmAthDB");
		gfs = GetNewGFS(gfs, mainStr, compareStr, "Ath");

		GroupFields ngfs = new GroupFields();
		ngfs.AddEntities(gfs);
		//获取ChartFrmLink的对应表单数据
		for (GroupField gf : gfs.ToJavaList())
		{
			if (gf.getCtrlType().equals("ChapterFrmLinkFrm") == true)
			{
				mainStr = "";
				compareStr = "";
				String sql = "SELECT MyPK,Ver FROM Sys_FrmDBVer WHERE FrmID=" + SystemConfig.getAppCenterDBVarStr() + "FrmID AND RefPKVal=" + SystemConfig.getAppCenterDBVarStr() + "RefPKVal AND Ver IN(" + mainVer + "," + compareVer + ")";
				Paras ps = new Paras();
				ps.SQL = sql;
				ps.Add("FrmID", gf.getCtrlID(), false);
				ps.Add("RefPKVal", this.getOID());
				DataTable dt = DBAccess.RunSQLReturnTable(ps);
				for (DataRow dr : dt.Rows)
				{
					int ver = Integer.parseInt(dr.getValue(1).toString());
					if (ver == mainVer)
					{
						mainStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", dr.getValue(0).toString(), "FrmDB");
					}
					if (ver == compareVer)
					{
						compareStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", dr.getValue(0).toString(), "FrmDB");
					}
				}
				if (mainStr.equals(compareStr) == true)
				{
					ngfs.RemoveEn(gf);
				}
			}
			continue;
		}
	   ds.Tables.add(ngfs.ToDataTableField("GroupFields"));
		return bp.tools.Json.ToJson(ds);
	}

	private GroupFields GetNewGFS(GroupFields gfs, String mainStr, String compareStr, String ctrlType) throws Exception {
		return GetNewGFS(gfs, mainStr, compareStr, ctrlType, null);
	}

	private GroupFields GetNewGFS(GroupFields gfs, String mainStr, String compareStr, String ctrlType, MapDtls dtls) throws Exception {
		//主版本和比对版本都不存在数据
		if (DataType.IsNullOrEmpty(mainStr) == true && DataType.IsNullOrEmpty(compareStr) == true)
		{
			GroupFields ngfs = new GroupFields();
			ngfs.AddEntities(gfs);
			for (GroupField gf : gfs.ToJavaList())
			{
				if (gf.getCtrlType().equals(ctrlType) == true)
				{
					ngfs.RemoveEn(gf);
				}
			}
			return ngfs;
		}
		//两个比对版本其中一个为空时
		if ((DataType.IsNullOrEmpty(mainStr) == true && DataType.IsNullOrEmpty(compareStr) == false) || (DataType.IsNullOrEmpty(mainStr) == false && DataType.IsNullOrEmpty(compareStr) == true))
		{
			return gfs;
		}

		//两个版本的数据都不为空时,转成DataSet
		DataSet mds = bp.tools.Json.ToDataSet(mainStr);
		DataSet cds = bp.tools.Json.ToDataSet(compareStr);
		DataTable cdt = null;
		for (DataTable mdt : mds.Tables)
		{
			if (cds.getTables().contains(mdt) == false)
			{
				//比对table中的内容
				boolean flag = CompareDataTable(mdt, cdt);
				if (flag == true)
				{
					if (ctrlType.equals("Dtl") == true)
					{
						for (MapDtl dtl : dtls.ToJavaList())
						{
							if (dtl.getPTable().equals(mdt.TableName) == true)
							{
								gfs = RemoveByCtrlID(gfs, dtl.getNo());
							}
						}
					}
					if (ctrlType.equals("Ath") == true)
					{
						gfs = RemoveByCtrlID(gfs, this.getFrmID() + "_" + mdt.TableName);
					}
				}
			}

		}
		return gfs;
	}
	/** 
	 比对两个DataTable在数据结构相同下数据是否相同
	 
	 @param dtA
	 @param dtB
	 @return 
	*/
	private boolean CompareDataTable(DataTable dtA, DataTable dtB)
	{
		if (dtA.Rows.size() == dtB.Rows.size())
		{
			//比内容
			for (int i = 0; i < dtA.Rows.size(); i++)
			{
				for (int j = 0; j < dtA.Columns.size(); j++)
				{
					String columnName = dtA.Columns.get(j).ColumnName;
					if (columnName.equals("OID") == true || columnName.equals("WorkID") == true || columnName.equals("MyPK") == true || columnName.equals("RDT") == true)
					{
						continue;
					}
					if (dtA.Rows.get(i).getValue(j).equals(dtB.Rows.get(i).getValue(j)) == false)
					{
						return false;
					}
				}
			}
			return true;

		}
		else
		{
			return false;
		}
	}

	private GroupFields RemoveByCtrlID(GroupFields gfs, String ctrlID) throws Exception {
		for (GroupField gf : gfs.ToJavaList())
		{
			if (gf.getCtrlID().equals(ctrlID) == true)
			{
				gfs.RemoveEn(gf);
				return gfs;
			}
		}
		return gfs;
	}
	public final String ChapterFrmDB_DtlInit() throws Exception {
		String dtlNo = GetRequestVal("DtlNo");

		DataSet myds = new DataSet();
		MapDtl dtl = new MapDtl(dtlNo);
		DataTable Sys_MapDtl = dtl.ToDataTableField("Sys_MapDtl");
		myds.Tables.add(Sys_MapDtl);

		//明细表的表单描述
		MapAttrs attrs = dtl.getMapAttrs();
		DataTable Sys_MapAttr = attrs.ToDataTableField("Sys_MapAttr");
		myds.Tables.add(Sys_MapAttr);

		//明细表的配置信息.
		//DataTable Sys_MapExt = dtl.getMapExts().ToDataTableField("Sys_MapExt");
	   // myds.Tables.add(Sys_MapExt);

		//启用附件，增加附件信息
		DataTable Sys_FrmAttachment = dtl.getFrmAttachments().ToDataTableField("Sys_FrmAttachment");
		myds.Tables.add(Sys_FrmAttachment);
		return bp.tools.Json.ToJson(myds);
	}

	public final String ChapterFrmDB_Dtl() throws Exception {
		String mainVerPK = this.GetRequestVal("MainVerPK");
		String compareVerPK = this.GetRequestVal("CompareVerPK");
		if (DataType.IsNullOrEmpty(mainVerPK) == true || DataType.IsNullOrEmpty(compareVerPK) == true)
		{
			return "err@获取存储版本的主键值失败,请联系管理员";
		}
		//获取主版本的附件信息
		String mainStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", mainVerPK, "FrmDtlDB");
		String compareStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", compareVerPK, "FrmDtlDB");
		DataTable dt = new DataTable();
		dt.Columns.Add("MainDtls");
		dt.Columns.Add("CompareDtls");
		DataRow dr = dt.NewRow();
		dr.setValue(0, mainStr);
		dr.setValue(1, compareStr);
		dt.Rows.add(dr);
		return bp.tools.Json.ToJson(dt);
	}

	/** 
	 获取附件的版本比对信息
	 
	 @return 
	*/
	public final String ChapterFrmDB_Ath() throws Exception {
		String mainVerPK = this.GetRequestVal("MainVerPK");
		String compareVerPK = this.GetRequestVal("CompareVerPK");
		if (DataType.IsNullOrEmpty(mainVerPK) == true || DataType.IsNullOrEmpty(compareVerPK) == true)
		{
		   return "err@获取存储版本的主键值失败,请联系管理员";
		}
		//获取主版本的附件信息
		String mainStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", mainVerPK, "FrmAthDB");
		String compareStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", compareVerPK, "FrmAthDB");
		DataTable dt = new DataTable();
		dt.Columns.Add("MainAths");
		dt.Columns.Add("CompareAths");
		DataRow dr = dt.NewRow();
		dr.setValue(0, mainStr);
		dr.setValue(1, compareStr);
		dt.Rows.add(dr);
		return bp.tools.Json.ToJson(dt);
	}

	public final String ChapterFrmDB_FrmGener() throws Exception {
		int mainVer = this.GetRequestValInt("MainVer");
		int compareVer = this.GetRequestValInt("CompareVer");
		if (mainVer == 0 || compareVer == 0)
		{
			return "err@获取存储版本的主键值失败,请联系管理员";
		}
		MapData md = new MapData(this.getFrmID());
		//主表实体.
		GEEntityOID en = new GEEntityOID(this.getFrmID());
		en.setOID(this.getOID());
		if (en.RetrieveFromDBSources() == 0)
		{
			return "表单" + md.getName() +"的OID=" + this.getOID() + "的数据不存在";
		}
		String frmID = md.getNo();
		//根据表单存储的数据获取获取使用表单的版本号
		int frmVer = 0;
		if (en.getEnMap().getAttrs().contains("AtPara") == true)
		{
			frmVer = en.GetParaInt("FrmVer", 0);
			if (frmVer != 0 && frmVer != md.getVer2022())
			{
				frmID = md.getNo() + "." + frmVer;
			}
		}

		DataSet ds = CCFormAPI.GenerHisDataSet(frmID, null, null);
		//现在版本不是主版本的情况
		if (frmID.equals(this.getFrmID()) == false)
		{
			DataTable mddt = ds.GetTableByName("Sys_MapData");
			mddt.Rows.get(0).setValue("AtPara", mddt.Rows.get(0).getValue("AtPara") + "@MainFrmID=" + this.getFrmID());
			//如果是经典表单
			if (md.getHisFrmType() == FrmType.FoolForm)
			{
				DataTable athdt = ds.GetTableByName("Sys_FrmAttachment");
				if (frmVer != 0 && athdt.Rows.size() != 0)
				{
					DataTable gfdt = ds.GetTableByName("Sys_GroupField");
					for (DataRow dr : athdt.Rows)
					{
						DataRow[] gfr = gfdt.Select("CtrlID='" + dr.getValue("MyPK") + "'");
						if (gfr.length != 0)
						{
							gfr[0].setValue("CtrlID", md.getNo() + "_" + dr.getValue("NoOfObj"));
						}
						dr.setValue("MyPK", md.getNo() + "_" + dr.getValue("NoOfObj"));

					}
				}
			}
		}

		//获取主版本的表单信息
		String dbstr = SystemConfig.getAppCenterDBVarStr();
		String sql = "SELECT MyPK,Ver FROM Sys_FrmDBVer WHERE FrmID=" + dbstr + "FrmID AND RefPKVal=" + dbstr + "RefPKVal AND Ver IN(" + mainVer + "," + compareVer + ")";
		Paras ps = new Paras();
		ps.SQL = sql;
		ps.Add("FrmID", this.getFrmID(), false);
		ps.Add("RefPKVal", this.getOID());
		DataTable dt = DBAccess.RunSQLReturnTable(ps);
		String mainStr = "";
		String compareStr = "";
		for (DataRow dr : dt.Rows)
		{
			int ver = Integer.parseInt(dr.getValue(1).toString());
			if (ver == mainVer)
			{
				mainStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", dr.getValue(0).toString(), "FrmDB");
			}
			if (ver == compareVer)
			{
				compareStr = DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", dr.getValue(0).toString(), "FrmDB");
			}
		}
		dt = new DataTable();
		dt.TableName = "MainData";
		dt.Columns.Add("Ver");
		dt.Columns.Add("Data");
		DataRow drr = dt.NewRow();
		drr.setValue(0, mainVer);
		drr.setValue(1, mainStr);
		dt.Rows.add(drr);
		drr = dt.NewRow();
		drr.setValue(0, compareVer);
		drr.setValue(1, compareStr);
		dt.Rows.add(drr);
		ds.Tables.add(dt);
		return bp.tools.Json.ToJson(ds);
	}

	/** 
	 比对富文本的内容
	 
	 @param keyOfEn
	 @param vers
	 @return 
	*/
	private String GetBigTextByKeyOfEn(String keyOfEn, FrmDBVers vers) throws Exception {
		for (FrmDBVer ver : vers.ToJavaList())
		{
			if (DataType.IsNullOrEmpty(ver.getKeyOfEn()) == true)
			{
				continue;
			}
			if (ver.getKeyOfEn().equals(keyOfEn) == true)
			{
				return DBAccess.GetBigTextFromDB("Sys_FrmDBVer", "MyPK", ver.getMyPK(), "FrmDB");
			}
		}
		return "";
	}
}
