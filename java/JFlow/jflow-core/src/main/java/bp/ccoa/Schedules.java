package bp.ccoa;

import bp.en.*;
import java.util.*;

/** 
 日程 s
*/
public class Schedules extends EntitiesMyPK
{
	/** 
	 查询事件到.
	 
	 @param dtFrom
	 @param dtTo
	 @return 
	*/
	public final String DTFromTo(String dtFrom, String dtTo) throws Exception {
		this.RetrieveAll();
		return this.ToJson("dt");
	}
	/** 
	 日程
	*/
	public Schedules()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Schedule();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Schedule> ToJavaList()
	{
		return (java.util.List<Schedule>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Schedule> Tolist()
	{
		ArrayList<Schedule> list = new ArrayList<Schedule>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Schedule)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
