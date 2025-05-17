package bp.sys;

import bp.da.*;
import bp.en.*;
import java.util.*;

/** 
 数据版本s
*/
public class FrmDBVers extends EntitiesMyPK
{
	/** 
	 获得章节表单的版本.
	 
	 @param frmID
	 @param workID
	 @return 
	*/
	public final String ChapterFrmDBVers(String frmID, String workID)
	{
		String sql = " SELECT DISTINCT Ver,RDT,RecName FROM Sys_FrmDBVer WHERE FrmID = '" + frmID + "' AND RefPKVal='" + workID + "' Group BY Ver,RDT,RecName ORDER BY RDT";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		// dt.Columns.Add("MaxVer"); //最大版本.
		if (bp.difference.SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "Ver"; //版本号
			dt.Columns.get(1).ColumnName = "RDT";
			dt.Columns.get(2).ColumnName = "RecName";
		}


		return bp.tools.Json.ToJson(dt);
	}



		///#region 构造
	public FrmDBVers()
	{
	}

		///#endregion


		///#region 重写
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmDBVer();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmDBVer> Tolist()
	{
		ArrayList<FrmDBVer> list = new ArrayList<FrmDBVer>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmDBVer)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.


		///#region 为了适应自动翻译成java的需要,把实体转换成IList, c#代码调用会出错误。
	/** 
	 转化成list
	 
	 @return List
	*/
	public final java.util.List<FrmDBVer> ToJavaList()
	{
		return (java.util.List<FrmDBVer>)(Object)this;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成IList, c#代码调用会出错误。

}
