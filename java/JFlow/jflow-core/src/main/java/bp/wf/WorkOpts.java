package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 退回轨迹s 
*/
public class WorkOpts extends Entities
{

		///#region 构造
	/** 
	 退回轨迹s
	*/
	public WorkOpts()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new WorkOpt();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<WorkOpt> ToJavaList()
	{
		return (java.util.List<WorkOpt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<WorkOpt> Tolist()
	{
		ArrayList<WorkOpt> list = new ArrayList<WorkOpt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((WorkOpt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
