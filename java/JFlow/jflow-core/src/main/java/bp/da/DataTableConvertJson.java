package bp.da;

import bp.*;
import bp.en.*; import bp.en.Map;
import bp.da.*;

public class DataTableConvertJson
{


		///#region dataTable转换成Json格式
	/**   
	 dataTable转换成Json格式  
	   
	 param dt
	 @return   
	*/
	public static String DataTable2Json(DataTable dt)
	{
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("[");
		for (int i = 0; i < dt.Rows.size(); i++)
		{
			jsonBuilder.append("{");
			for (int j = 0; j < dt.Columns.size(); j++)
			{
				jsonBuilder.append("\"");
				jsonBuilder.append(dt.Columns.get(j).ColumnName);
				jsonBuilder.append("\":\"");
				jsonBuilder.append(dt.getValue(i, j).toString());
				jsonBuilder.append("\",");
			}
			jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
			jsonBuilder.append("},");
		}
		if (jsonBuilder.length() > 1)
		{
			jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
		}
		jsonBuilder.append("]");

		return jsonBuilder.toString();
	}
	/** 
	  替换特殊字符，避免json解析时报错 秦-2015-5-20
	 
	 param getText 需要替换的原始字符串
	 @return 
	*/
	public static String GetFilteredStrForJSON(String getText, boolean checkId)
	{
		if (checkId)
		{
			getText = getText.replace("\n", "");
		}
		else
		{
			getText = getText.replace("\n", "ccflow_lover");
		}

		getText = getText.replace("\n", "");
		getText = getText.replace("\r", "");
		getText = getText.replace("{", "｛");
		getText = getText.replace("}", "｝");
		getText = getText.replace("[", "【");
		getText = getText.replace("]", "】");
		getText = getText.replace("\"", "”");
		getText = getText.replace("\'", "‘");
		getText = getText.replace("<", "《");
		getText = getText.replace(">", "》");
		getText = getText.replace("(", "（");
		getText = getText.replace(")", "）");
		return getText;
	}
	/** 
	   dataTable转换成Json格式  秦2014年06月23日 17:11
	 
	 param dt 表
	 param totalRows 总行数
	 @return 
	*/
	public static String DataTable2Json(DataTable dt, int totalRows)
	{
		StringBuilder jsonBuilder = new StringBuilder();

		jsonBuilder.append("{\"rows\":[");
		for (int i = 0; i < dt.Rows.size(); i++)
		{
			jsonBuilder.append("{");
			for (int j = 0; j < dt.Columns.size(); j++)
			{
				jsonBuilder.append("\""+dt.Columns.get(j).ColumnName.toUpperCase()+"\"");
				jsonBuilder.append(":");
				jsonBuilder.append("\"" + GetFilteredStrForJSON(dt.getValue(i, j).toString(), true) + "\"");
				jsonBuilder.append(",");
			}
			jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
			jsonBuilder.append("},");
		}
		//不存在数据时
		if (jsonBuilder.length() > 7)
		{
			jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
		}
		//jsonBuilder.Remove(jsonBuilder.length() - 1, 1);
		if (totalRows == 0)
		{
			jsonBuilder.append("],\"total\":0");
		}
		else
		{
			jsonBuilder.append("],\"total\":" + totalRows);
		}
		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}


		///#endregion dataTable转换成Json格式


		///#region DataSet转换成Json格式
	/**   
	 DataSet转换成Json格式  
	   
	 param ds DataSet
	 @return   
	*/
	public static String Dataset2Json(DataSet ds)
	{
		StringBuilder json = new StringBuilder();
		json.append("[");

		for (DataTable dt : ds.Tables)
		{
			json.append("{\"");
			json.append(dt.TableName);
			json.append("\":");
			json.append(DataTable2Json(dt));
			json.append("},");
		}

		if (json.length() > 1)
		{
			json.deleteCharAt(json.length() - 1);
		}

		json.append("]");

		return json.toString();
	}

		///#endregion

	/** 
	 Msdn
	 
	 param jsonName
	 param dt
	 @return 
	*/
	public static String DataTableToJson(String jsonName, DataTable dt)
	{
		StringBuilder Json = new StringBuilder();
		Json.append("{\"" + jsonName + "\":[");
		if (dt.Rows.size() > 0)
		{
			for (int i = 0; i < dt.Rows.size(); i++)
			{
				Json.append("{");
				for (int j = 0; j < dt.Columns.size(); j++)
				{
					Json.append("\"" + dt.Columns.get(j).ColumnName.toString() + "\":\"" + dt.getValue(i, j).toString() + "\"");
					if (j < dt.Columns.size() - 1)
					{
						Json.append(",");
					}
				}
				Json.append("}");
				if (i < dt.Rows.size() - 1)
				{
					Json.append(",");
				}
			}
		}
		Json.append("]}");
		return Json.toString();
	}
	/** 
	 根据DataTable生成Json树结构
	 
	 param tabel 数据源
	 param idCol ID列
	 param txtCol Text列
	 param rela 关系字段
	 param pId 父ID
	@return easyui tree json格式
	*/
	public static String TransDataTable2TreeJson(DataTable tabel, String idCol, String txtCol, String rela, Object pId)
	{
		treeResult = new StringBuilder();
		treesb = new StringBuilder();
		return GetTreeJsonByTable(tabel, idCol, txtCol, rela, pId);
	}

	/** 
	 根据DataTable生成Json树结构
	 
	 param tabel 数据源
	 param idCol ID列
	 param txtCol Text列
	 param rela 关系字段
	 param pId 父ID
	@return easyui tree json格式
	*/
	private static StringBuilder treeResult = new StringBuilder();
	private static StringBuilder treesb = new StringBuilder();
	private static String GetTreeJsonByTable(DataTable tabel, String idCol, String txtCol, String rela, Object pId)
	{
		String treeJson = "";
		String treeState = "close";
		treeResult.append(treesb.toString());

		treesb.setLength(0);
		if (treeResult.length() == 0)
		{
			treeState = "open";
		}
		if (tabel.Rows.size() > 0)
		{
			treesb.append("[");
			String filer = "";
			if (pId.toString().equals(""))
			{
				filer = String.format("%1$s is null", rela);
			}
			else
			{
				filer = String.format("%1$s='%2$s'", rela, pId);
			}
			DataRow[] rows = tabel.Select(filer);
			if (rows.length > 0)
			{
				for (DataRow row : rows)
				{
					treesb.append("{\"id\":\"" + row.getValue(idCol) + "\",\"text\":\"" + row.getValue(txtCol) + "\",\"state\":\"" + treeState + "\"");


					if (tabel.Select(String.format("%1$s='%2$s'", rela, row.getValue(idCol))).length > 0)
					{
						treesb.append(",\"children\":");
						GetTreeJsonByTable(tabel, idCol, txtCol, rela, row.getValue(idCol));
						treeResult.append(treesb.toString());
						treesb.setLength(0);
					}
					treeResult.append(treesb.toString());
					treesb.setLength(0);
					treesb.append("},");
				}
				treesb = treesb.deleteCharAt(treesb.length() - 1);
			}
			treesb.append("]");
			treeResult.append(treesb.toString());
			treeJson = treeResult.toString();
			treesb.setLength(0);
		}
		return treeJson;
	}
}