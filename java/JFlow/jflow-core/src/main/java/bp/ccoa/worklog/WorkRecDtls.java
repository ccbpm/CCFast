package bp.ccoa.worklog;

import bp.en.*;
import java.util.*;

/** 
 工作内容 s
*/
public class WorkRecDtls extends EntitiesMyPK
{
	/** 
	 工作内容
	*/
	public WorkRecDtls()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new WorkRecDtl();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<WorkRecDtl> ToJavaList()
	{
		return (java.util.List<WorkRecDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<WorkRecDtl> Tolist()
	{
		ArrayList<WorkRecDtl> list = new ArrayList<WorkRecDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((WorkRecDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
