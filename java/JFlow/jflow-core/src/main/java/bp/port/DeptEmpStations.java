package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import java.util.*;

/** 
 部门角色人员对应 
*/
public class DeptEmpStations extends EntitiesMyPK
{

		///#region 构造
	/** 
	 工作部门角色人员对应
	*/
	public DeptEmpStations()
	{
	}

		///#endregion


		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new DeptEmpStation();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<DeptEmpStation> ToJavaList()
	{
		return (java.util.List<DeptEmpStation>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<DeptEmpStation> Tolist()
	{
		ArrayList<DeptEmpStation> list = new ArrayList<DeptEmpStation>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((DeptEmpStation)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.


		///#region 删除方法
	public final String DelteNotInEmp()
	{
		String sql = "DELETE FROM Port_DeptEmpStation WHERE FK_Emp NOT IN (SELECT No FROM Port_Emp)";
		DBAccess.RunSQL(sql);
		return "删除成功";
	}

		///#endregion
}
