package bp.ccoa.worklog;

import bp.en.*;
import java.util.*;

/** 
 日志审核 s
*/
public class WorkCheckers extends EntitiesMyPK
{
	/** 
	 日志审核
	*/
	public WorkCheckers()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new WorkChecker();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<WorkChecker> ToJavaList()
	{
		return (java.util.List<WorkChecker>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<WorkChecker> Tolist()
	{
		ArrayList<WorkChecker> list = new ArrayList<WorkChecker>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((WorkChecker)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
