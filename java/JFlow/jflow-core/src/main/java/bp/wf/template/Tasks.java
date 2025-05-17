package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 任务
*/
public class Tasks extends EntitiesMyPK
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Task();
	}
	/** 
	 任务
	*/
	public Tasks()
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Task> ToJavaList()
	{
		return (java.util.List<Task>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Task> Tolist()
	{
		ArrayList<Task> list = new ArrayList<Task>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Task)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
