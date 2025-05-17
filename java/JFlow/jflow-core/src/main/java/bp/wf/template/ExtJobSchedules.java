package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 流程进度图s
*/
public class ExtJobSchedules extends EntitiesMyPK
{

		///#region 构造
	/** 
	 流程进度图s
	*/
	public ExtJobSchedules()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ExtJobSchedule();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ExtJobSchedule> ToJavaList()
	{
		return (java.util.List<ExtJobSchedule>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ExtJobSchedule> Tolist()
	{
		ArrayList<ExtJobSchedule> list = new ArrayList<ExtJobSchedule>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ExtJobSchedule)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
