package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 流程删除日志s 
*/
public class WorkFlowDeleteLogs extends Entities
{

		///#region 构造
	/** 
	 流程删除日志s
	*/
	public WorkFlowDeleteLogs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new WorkFlowDeleteLog();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<WorkFlowDeleteLog> ToJavaList()
	{
		return (java.util.List<WorkFlowDeleteLog>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<WorkFlowDeleteLog> Tolist()
	{
		ArrayList<WorkFlowDeleteLog> list = new ArrayList<WorkFlowDeleteLog>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((WorkFlowDeleteLog)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
