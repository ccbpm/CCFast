package bp.wf.httphandler;

import bp.da.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;

/** 
 页面功能实体
*/
public class WF_Admin_Port extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_Admin_Port()
	{
	}


		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
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


		///#region OrderOfDept 部门顺序调整 .
	/** 
	 
	 
	 @return 
	*/
	public final String OrderOfDept_Init()
	{
		String sql = "SELECT No,Name,ParentNo,Idx FROM Port_Dept";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
			dt.Columns.get(2).ColumnName = "ParentNo";
			dt.Columns.get(3).ColumnName = "Idx";
		}
		return "";
	}

		///#endregion xxx 界面方法.



}
