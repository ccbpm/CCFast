package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 表单属性s
*/
public class MapDataURLs extends EntitiesNoName
{

		///#region 构造
	/** 
	 表单属性s
	*/
	public MapDataURLs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapDataURL();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapDataURL> ToJavaList()
	{
		return (java.util.List<MapDataURL>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapDataURL> Tolist()
	{
		ArrayList<MapDataURL> list = new ArrayList<MapDataURL>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapDataURL)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
