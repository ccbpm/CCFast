package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 收文字号s
*/
public class MapAttrDocWordReceives extends EntitiesMyPK
{

		///#region 构造
	/** 
	 收文字号s
	*/
	public MapAttrDocWordReceives()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapAttrDocWordReceive();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapAttrDocWordReceive> ToJavaList()
	{
		return (java.util.List<MapAttrDocWordReceive>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapAttrDocWordReceive> Tolist()
	{
		ArrayList<MapAttrDocWordReceive> list = new ArrayList<MapAttrDocWordReceive>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapAttrDocWordReceive)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
