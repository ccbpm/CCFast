package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 Word表单属性s
*/
public class MapFrmWords extends EntitiesNoName
{

		///#region 构造
	/** 
	 Word表单属性s
	*/
	public MapFrmWords()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapFrmWord();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapFrmWord> ToJavaList()
	{
		return (java.util.List<MapFrmWord>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapFrmWord> Tolist()
	{
		ArrayList<MapFrmWord> list = new ArrayList<MapFrmWord>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapFrmWord)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
