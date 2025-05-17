package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;
import java.time.*;

/** 
 实体属性s
*/
public class MapAttrSFSQLs extends EntitiesMyPK
{

		///#region 构造
	/** 
	 实体属性s
	*/
	public MapAttrSFSQLs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapAttrSFSQL();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapAttrSFSQL> ToJavaList()
	{
		return (java.util.List<MapAttrSFSQL>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapAttrSFSQL> Tolist()
	{
		ArrayList<MapAttrSFSQL> list = new ArrayList<MapAttrSFSQL>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapAttrSFSQL)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
