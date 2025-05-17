package bp.wf.httphandler;

import bp.da.*;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;

/**
 页面功能实体
*/
public class WF_Admin_Cond2020 extends bp.difference.handler.DirectoryPageBase
{

	/**
	 构造函数
	*/
	public WF_Admin_Cond2020()
	{
	}
	/**
	 初始化列表

	 @return
	*/
	public final String List_Init()
	{
		// BP.WF.Template.CondAttr.ToNodeID
		// Conds condes = new Conds();
		// condes.RetrieveAll();
		return "";
	}
	public final String List_Move()
	{
		String ptable = this.GetRequestVal("PTable");
		if (DataType.IsNullOrEmpty(ptable) == true)
		{
			ptable = "WF_Cond";
		}
		String pkField = this.GetRequestVal("PKField");
		if (DataType.IsNullOrEmpty(pkField) == true)
		{
			pkField = "MyPK";
		}
		String[] ens = this.GetRequestVal("MyPKs").split("[,]", -1);
		for (int i = 0; i < ens.length; i++)
		{
			String enNo = ens[i];
			String sql = "UPDATE "+ ptable + " SET Idx=" + i + " WHERE "+ pkField + "='" + enNo + "'";
			DBAccess.RunSQL(sql);
		}
		//return "err@xxxxxxxx";
		return "顺序移动成功..";
	}
	public final String List_DoCheck() throws Exception {
		int toNodeID = 0;
		String mystr = this.GetRequestVal("ToNodeID");
		if (DataType.IsNullOrEmpty(mystr) == false)
		{
			toNodeID = Integer.parseInt(mystr);
		}

		int condType = this.GetRequestValInt("CondType");
		return List_DoCheckExt(condType, this.getNodeID(), toNodeID);
	}
	/**
	 校验是否正确

	 @return
	*/
	public static String List_DoCheckExt(int condType, int nodeID, int toNodeID) throws Exception {
		if (toNodeID == 0)
		{
			toNodeID = nodeID;
		}

		//集合.
		Conds conds = new Conds();
		conds.Retrieve(CondAttr.FK_Node, nodeID, CondAttr.ToNodeID, toNodeID, CondAttr.CondType, condType, CondAttr.Idx);

		if (conds.size() == 0)
		{
			return " 没有条件. ";
		}

		if (conds.size() == 1)
		{
			for (Cond item : conds.ToJavaList())
			{
				if (item.getHisDataFrom() == ConnDataFrom.CondOperator)
				{
					return "info@请继续增加条件";
				}
				else
				{
					return "条件成立.";
				}
			}
		}
		String str = "";
		//遍历方向条件.
		for (Cond item : conds.ToJavaList())
		{
			if (item.getHisDataFrom() == ConnDataFrom.CondOperator)
			{
				str += " " + item.getOperatorValue();
			}
			else
			{
				str += " 1=1 "; // + item.AttrKey + item.FK_Operator + item.OperatorValue;
			}
		}

		String sql = "";
		switch (bp.difference.SystemConfig.getAppCenterDBType())
		{
			case MSSQL:
				sql = " SELECT TOP 1 No FROM Port_Emp WHERE " + str;
				break;
			case PostgreSQL:
			case MySQL:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql = " SELECT No FROM Port_Emp WHERE " + str + "   limit 1 ";
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql = " SELECT No FROM Port_Emp WHERE (" + str + ") AND  rownum <=1 ";
				break;
			default:
				return "err@没有做的数据库类型判断.";
		}

		try
		{
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			return "格式正确:" + str;
		}
		catch (RuntimeException ex)
		{
			return "不符合规范:" + str;
		}
	}
	/**
	 初始化岗位

	 @return
	*/

	public final String SelectStation_StationTypes()
	{
		String sql = "select No,Name FROM port_StationType WHERE No in (SELECT Fk_StationType from Port_Station WHERE OrgNo ='" + this.GetRequestVal("OrgNo") + "')";

		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
		}
		return bp.tools.Json.ToJson(dt);
	}
}
