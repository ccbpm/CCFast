package bp.wf.httphandler;

import bp.da.DataColumn;
import bp.da.DataRow;
import bp.da.DataTable;
import bp.sys.*;
import bp.*;
import bp.sys.CCFormAPI;
import bp.sys.frmui.MapAttrString;
import bp.wf.*;

public class WF_Admin_FoolFormDesigner_Batch extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 批量修改
	*/
	public WF_Admin_FoolFormDesigner_Batch()
	{
	}
	/** 
	 批量修改字段Init.
	 
	 @return 
	*/
	public final String KeyOfEn_Init() throws Exception {
		MapAttrs attrs = new MapAttrs();
		attrs.Retrieve("FK_MapData", this.getFrmID(), "Idx");

		DataTable dt = new DataTable();
		dt.Columns.add(new DataColumn("Name")); //字段名
		dt.Columns.Add(new DataColumn("DBType")); //数据类型
		dt.Columns.Add(new DataColumn("GroupID")); //隶属分组.
		dt.Columns.Add(new DataColumn("KeyOfEn")); //字段ID
		dt.Columns.Add(new DataColumn("JianPin")); //简拼
		dt.Columns.Add(new DataColumn("QuanPin")); //全拼.
		dt.Columns.Add(new DataColumn("Etc")); //其他.

		for (MapAttr attr : attrs.ToJavaList())
		{
			//生成字段名称.
			DataRow dr = dt.NewRow();
			dr.setValue("Name", attr.getName());
			dr.setValue("KeyOfEn", attr.getKeyOfEn());
			dr.setValue("JianPin", CCFormAPI.ParseStringToPinyinField(attr.getName(), false)); //简拼;
			dr.setValue("QuanPin", CCFormAPI.ParseStringToPinyinField(attr.getName(), true)); //全拼;
			dr.setValue("GroupID", attr.getGroupID());

			switch (attr.getMyDataType())
			{
				case bp.da.DataType.AppString:
					dr.setValue("DBType", "String");
					break;
				case bp.da.DataType.AppBoolean:
					dr.setValue("DBType", "AppBoolean");
					break;
				case bp.da.DataType.AppFloat:
					dr.setValue("DBType", "AppFloat");
					break;
				case bp.da.DataType.AppInt:
					dr.setValue("DBType", "Int");
					break;
				case bp.da.DataType.AppMoney:
					dr.setValue("DBType", "Money");
					break;
				case bp.da.DataType.AppDate:
					dr.setValue("DBType", "Date");
					break;
				case bp.da.DataType.AppDateTime:
					dr.setValue("DBType", "DateTime");
					break;
				default:
					break;
			}
			dt.Rows.add(dr);
		}
		return bp.tools.Json.ToJson(dt);
	}
	public final String KeyOfEn_Save() throws Exception {
		String newName = this.getKeyOfEn();
		bp.sys.frmui.MapAttrString en = new MapAttrString(this.getMyPK());
		return en.DoRenameField(newName);
	}
}
