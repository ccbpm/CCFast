package bp.wf.httphandler;

import bp.da.DataRow;
import bp.da.DataTable;
import bp.da.DataType;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 页面功能实体
*/
public class WF_Admin_FoolFormDesigner_SFTable extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_FoolFormDesigner_SFTable()
	{

	}


		///#region xxx 界面 .
	/** 
	  初始化sf0. @于庆海，新方法.
	 
	 @return 
	*/
	public final String SF0_Init() throws Exception {
		String cl = "BP.En.Entities";
		ArrayList al = ClassFactory.GetObjects(cl);

		//定义容器.
		DataTable dt = new DataTable();
		dt.Columns.Add("No");
		dt.Columns.Add("Name");

		SFTables sfs = new SFTables();
		sfs.RetrieveAll();

		for (Object obj : al)
		{
			Entities ens = obj instanceof Entities ? (Entities)obj : null;
			if (ens == null)
			{
				continue;
			}

			try
			{
				Entity en = ens.getNewEntity();
				if (en == null)
				{
					continue;
				}

				if (en.getEnMap().getAttrs().contains("No") == false)
				{
					continue;
				}

				if (sfs.contains(ens.toString()) == true)
				{
					continue;
				}

				DataRow dr = dt.NewRow();
				dr.setValue("No", ens.toString());

				if (en.getItIsTreeEntity())
				{
					dr.setValue("Name", en.getEnMap().getEnDesc() + "(树结构) " + ens.toString());
				}
				else
				{
					dr.setValue("Name", en.getEnMap().getEnDesc() + " " + ens.toString());
				}

				dt.Rows.add(dr);
			}
			catch (java.lang.Exception e)
			{

			}
		}
		return bp.tools.Json.ToJson(dt);
	}
	public final String SF0_Save()
	{
		return "保存成功.";
	}

		///#endregion xxx 界面方法.


		///#region 表或者视图 .
	/** 
	  初始化sf2.
	 
	 @return 
	*/
	public final String SF2_Init() throws Exception {
		SFDBSrcs srcs = new SFDBSrcs();
		srcs.RetrieveAll();
		return srcs.ToJson("dt");
	}

	public final String SF2_GetTVs() throws Exception {
		String src = this.GetRequestVal("src");

		SFDBSrc sr = new SFDBSrc(src);
		DataTable dt = sr.GetTables(false);

		return bp.tools.Json.ToJson(dt);
	}

	public final String SF2_GetCols() throws Exception {
		String src = this.GetRequestVal("src");
		String table = this.GetRequestVal("table");

		if (src == null || src.isEmpty())
		{
			throw new RuntimeException("err@参数不正确");
		}

		if (table == null || table.isEmpty())
		{
			return "[]";
		}

		SFDBSrc sr = new SFDBSrc(src);
		DataTable dt = sr.GetColumns(table);

		for (DataRow r : dt.Rows)
		{
			r.setValue("Name", r.getValue("No") + (r.getValue("Name") == null || r.getValue("Name") == null || DataType.IsNullOrEmpty(r.getValue("Name").toString()) ? "" : String.format("[%1$s]", r.getValue("Name"))));
		}

		return bp.tools.Json.ToJson(dt);
	}

	public final String SF2_Save() throws Exception {
		SFTable sf = new SFTable();
		sf.setNo(this.GetValFromFrmByKey("No"));
		if (sf.IsExits())
		{
			return "err@标记:" + sf.getNo() + "已经存在.";
		}

		sf.setName(this.GetValFromFrmByKey("Name"));
		sf.setSFDBSrcNo(this.GetValFromFrmByKey("FK_DBSrc"));
		sf.setSrcTable(this.GetValFromFrmByKey("SrcTable"));
		sf.setCodeStruct(CodeStruct.forValue(this.GetValIntFromFrmByKey("CodeStruct")));
		sf.setColumnValue(this.GetValFromFrmByKey("ColumnValue"));
		sf.setColumnText(this.GetValFromFrmByKey("ColumnText"));
		if (sf.getCodeStruct() == CodeStruct.Tree)
		{
			sf.setParentValue(this.GetValFromFrmByKey("ParentValue"));
			sf.setDefVal(this.GetValFromFrmByKey("RootValue"));
		}
		sf.setSelectStatement(this.GetValFromFrmByKey("Selectstatement"));
		sf.setSrcType(DictSrcType.TableOrView);
		sf.setFKVal("FK_" + sf.getNo());
		sf.Save();

		return "保存成功！";
	}


		///#endregion xxx 界面方法.

}
