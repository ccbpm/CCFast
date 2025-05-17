package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 经典表单属性s
*/
public class MapFrmFools extends EntitiesNoName
{

		///#region 构造
	/** 
	 经典表单属性s
	*/
	public MapFrmFools()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapFrmFool();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapFrmFool> ToJavaList()
	{
		return (java.util.List<MapFrmFool>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapFrmFool> Tolist()
	{
		ArrayList<MapFrmFool> list = new ArrayList<MapFrmFool>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapFrmFool)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
