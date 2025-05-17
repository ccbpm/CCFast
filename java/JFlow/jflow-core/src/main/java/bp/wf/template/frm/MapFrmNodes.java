package bp.wf.template.frm;

import bp.en.*;
import java.util.*;

/** 
 自由表单属性s
*/
public class MapFrmNodes extends EntitiesNoName
{

		///#region 构造
	/** 
	 自由表单属性s
	*/
	public MapFrmNodes()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapFrmNode();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapFrmNode> ToJavaList()
	{
		return (java.util.List<MapFrmNode>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapFrmNode> Tolist()
	{
		ArrayList<MapFrmNode> list = new ArrayList<MapFrmNode>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapFrmNode)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
