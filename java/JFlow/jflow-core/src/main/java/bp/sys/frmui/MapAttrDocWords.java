package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 公文字号组件s
*/
public class MapAttrDocWords extends EntitiesMyPK
{

		///#region 构造
	/** 
	 公文字号组件s
	*/
	public MapAttrDocWords()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapAttrDocWord();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapAttrDocWord> ToJavaList()
	{
		return (java.util.List<MapAttrDocWord>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapAttrDocWord> Tolist()
	{
		ArrayList<MapAttrDocWord> list = new ArrayList<MapAttrDocWord>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapAttrDocWord)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
