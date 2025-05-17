package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 系统定位组件s
*/
public class MapAttrFixeds extends EntitiesMyPK
{

		///#region 构造
	/** 
	 系统定位组件s
	*/
	public MapAttrFixeds()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapAttrFixed();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapAttrFixed> ToJavaList()
	{
		return (java.util.List<MapAttrFixed>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapAttrFixed> Tolist()
	{
		ArrayList<MapAttrFixed> list = new ArrayList<MapAttrFixed>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapAttrFixed)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
