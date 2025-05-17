package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 方向与工作角色对应
*/
public class DirectionStations extends EntitiesMM
{
	/** 
	 方向与工作角色对应
	*/
	public DirectionStations()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new DirectionStation();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<DirectionStation> ToJavaList()
	{
		return (java.util.List<DirectionStation>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<DirectionStation> Tolist()
	{
		ArrayList<DirectionStation> list = new ArrayList<DirectionStation>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((DirectionStation)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
