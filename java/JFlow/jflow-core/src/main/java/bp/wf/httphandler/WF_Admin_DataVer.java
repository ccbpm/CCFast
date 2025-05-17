package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.web.*;
import bp.en.*;
import bp.difference.*;
import net.sf.json.JSONObject;

import java.util.*;

/** 
 页面功能实体
*/
public class WF_Admin_DataVer extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_DataVer()
	{

	}

	public final String DataVer_RollBack() throws Exception {
		Entity en = ClassFactory.GetEn(this.getFrmID());
		en.setPKVal(this.getRefPKVal());
		if (en.RetrieveFromDBSources() == 0)
		{
			return "err@数据信息丢失";
		}
		//获取还原的数据
		String mainStr = DBAccess.GetBigTextFromDB("Sys_EnVer", EnVerAttr.MyPK, this.getMyPK(), EnVerAttr.DBJSON);
		if (DataType.IsNullOrEmpty(mainStr) == true)
		{
			return "err@还原的版本数据为空，不执行还原";
		}
		JSONObject json = JSONObject.fromObject(mainStr);
		for (Object str : json.keySet())
		{
			if(str == null)
				continue;
			en.SetValByKey(str.toString(), json.get(str));
		}
		en.Update();
		return "数据还原成功";


	}
	public final String DataVer_Init() throws Exception {
		String mainMyPK = this.getFrmID() + "_" + this.getRefPK() + "_" + GetRequestValInt("MainVer");
		String compareMyPK = this.getFrmID() + "_" + this.getRefPK() + "_" + GetRequestValInt("CompareVer");
		if (DataType.IsNullOrEmpty(mainMyPK) || DataType.IsNullOrEmpty(compareMyPK))
		{
			return "err@比对版本传参有误:MainMyPK=" + mainMyPK + ",compareMyPK=" + compareMyPK;
		}
		//获取版本比对数据
		String mainStr = DBAccess.GetBigTextFromDB("Sys_EnVer", EnVerAttr.MyPK, mainMyPK, EnVerAttr.DBJSON);
		String compareStr = DBAccess.GetBigTextFromDB("Sys_EnVer", EnVerAttr.MyPK, compareMyPK, EnVerAttr.DBJSON);
		if (DataType.IsNullOrEmpty(mainStr))
		{
			return "err@数据版本存储有误,版本[" + GetRequestValInt("MainVer") + "]数据JSON为空";
		}
		if (DataType.IsNullOrEmpty(compareStr))
		{
			return "err@数据版本存储有误,版本[" + GetRequestValInt("CompareVer") + "]数据JSON为空";
		}
		//获取实体
		Entity en = en = ClassFactory.GetEn(this.getFrmID());
		//获取实体字段集合
		Attrs attrs = en.getEnMap().getAttrs();
		DataSet ds = new DataSet();
		MapAttrs mapAttrs = new MapAttrs();
		//获取Map中的分组
		DataTable dtGroups = new DataTable("Sys_GroupField");
		dtGroups.Columns.Add("OID");
		dtGroups.Columns.Add("Lab");
		String groupName = "";
		String groupNames = "";
		for (Attr attr : attrs)
		{
			if (attr.getMyFieldType() == FieldType.RefText)
			{
				continue;
			}
			groupName = attr.GroupName;
			if (groupNames.contains(groupName + ",") == false)
			{
				DataRow dr = dtGroups.NewRow();
				groupNames += groupName + ",";
				dr.setValue("OID", groupName);
				dr.setValue("Lab", groupName);
				dtGroups.Rows.add(dr);
			}

			MapAttr mapAttr = attr.getToMapAttr();
			mapAttr.SetPara("GroupName", attr.GroupName);
			mapAttrs.AddEntity(mapAttr);
		}
		ds.Tables.add(dtGroups);
		DataTable sys_MapAttrs = mapAttrs.ToDataTableField("Sys_MapAttr");
		ds.Tables.add(sys_MapAttrs);


			///#region 把外键与枚举放入里面去.
		//加入枚举的外键.
		String enumKeys = "";
		for (MapAttr mapAttr : mapAttrs.ToJavaList())
		{
			if (mapAttr.getUIVisible() == false)
			{
				continue;
			}
			if (mapAttr.getUIIsEnable() == false)
			{
				continue;
			}
			if (mapAttr.getLGType() == FieldTypeS.Enum)
			{
				enumKeys += "'" + mapAttr.getUIBindKey() + "',";
				continue;
			}

			//外键
			if (mapAttr.getLGType() == FieldTypeS.FK)
			{
				// 判断是否存在.
				if (ds.contains(mapAttr.getUIBindKey()) == true)
				{
					continue;
				}

				DataTable keydt = bp.pub.PubClass.GetDataTableByUIBineKey(mapAttr.getUIBindKey(), null);
				keydt.TableName = mapAttr.getKeyOfEn();

				ds.Tables.add(keydt);
			}
			//外部数据源
			if (mapAttr.getLGType() == FieldTypeS.Normal && mapAttr.getUIContralType() == UIContralType.DDL && DataType.IsNullOrEmpty(mapAttr.getUIBindKey()) == false && mapAttr.getUIBindKey().toUpperCase().contains("SELECT") == true)
			{
				/*是一个sql*/
				String sqlBindKey = mapAttr.getUIBindKey();
				sqlBindKey = bp.wf.Glo.DealExp(sqlBindKey, en, null);

				DataTable keydt = DBAccess.RunSQLReturnTable(sqlBindKey);
				keydt.TableName = mapAttr.getKeyOfEn();
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
				{
					keydt.Columns.get("NO").ColumnName = "No";
					keydt.Columns.get("NAME").ColumnName = "Name";
				}
				if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
				{
					keydt.Columns.get("no").ColumnName = "No";
					keydt.Columns.get("name").ColumnName = "Name";
				}
				ds.Tables.add(keydt);
			}
		}
		if (enumKeys.length() > 2)
		{
			enumKeys = enumKeys.substring(0, enumKeys.length() - 1);
			DataTable dtEnum = new DataTable();
			String sqlEnum = "";
			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				String sqlWhere = " EnumKey IN (" + enumKeys + ") AND OrgNo='" + WebUser.getOrgNo() + "'";

				sqlEnum = "SELECT * FROM " + bp.sys.base.Glo.SysEnum() + " WHERE " + sqlWhere;
				sqlEnum += " UNION ";
				sqlEnum += "SELECT * FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey IN (" + enumKeys + ") AND EnumKey NOT IN (SELECT EnumKey FROM " + bp.sys.base.Glo.SysEnum() + " WHERE " + sqlWhere + ") AND (OrgNo Is Null Or OrgNo='')";
				dtEnum = DBAccess.RunSQLReturnTable(sqlEnum);
			}
			else
			{
				sqlEnum = "SELECT * FROM " + bp.sys.base.Glo.SysEnum() + " WHERE EnumKey IN (" + enumKeys + ")";
				dtEnum = DBAccess.RunSQLReturnTable(sqlEnum);
			}
			dtEnum.TableName = "Sys_Enum";

			if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.UpperCase)
			{
				dtEnum.Columns.get("MYPK").ColumnName = "MyPK";
				dtEnum.Columns.get("LAB").ColumnName = "Lab";
				dtEnum.Columns.get("ENUMKEY").ColumnName = "EnumKey";
				dtEnum.Columns.get("INTKEY").ColumnName = "IntKey";
				dtEnum.Columns.get("LANG").ColumnName = "Lang";
			}

			if (SystemConfig.getAppCenterDBFieldCaseModel() == FieldCaseModel.Lowercase)
			{
				dtEnum.Columns.get("mypk").ColumnName = "MyPK";
				dtEnum.Columns.get("lab").ColumnName = "Lab";
				dtEnum.Columns.get("enumkey").ColumnName = "EnumKey";
				dtEnum.Columns.get("intkey").ColumnName = "IntKey";
				dtEnum.Columns.get("lang").ColumnName = "Lang";
			}

			ds.Tables.add(dtEnum);
		}

			///#endregion 把外键与枚举放入里面去.


		DataTable dt = bp.tools.Json.ToDataTable(mainStr);
		dt.TableName = "MainData";
		ds.Tables.add(dt);
		DataTable dtt = bp.tools.Json.ToDataTable(compareStr);
		dtt.TableName = "CompareData";
		ds.Tables.add(dtt);
		return bp.tools.Json.ToJson(ds);
	}
}
