package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 打印组件s
*/
public class MapAttrPrints extends EntitiesMyPK
{

		///#region 构造
	/** 
	 打印组件s
	*/
	public MapAttrPrints()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapAttrPrint();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapAttrPrint> ToJavaList()
	{
		return (java.util.List<MapAttrPrint>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapAttrPrint> Tolist()
	{
		ArrayList<MapAttrPrint> list = new ArrayList<MapAttrPrint>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapAttrPrint)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
