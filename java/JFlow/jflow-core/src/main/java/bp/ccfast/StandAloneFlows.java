package bp.ccfast;
import bp.en.*;
import java.util.*;

/** 
 独立运行流程设置 s
*/
public class StandAloneFlows extends EntitiesNoName
{
	/** 
	 独立运行流程设置
	*/
	public StandAloneFlows()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new StandAloneFlow();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<StandAloneFlow> ToJavaList()
	{
		return (java.util.List<StandAloneFlow>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<StandAloneFlow> Tolist()
	{
		ArrayList<StandAloneFlow> list = new ArrayList<StandAloneFlow>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((StandAloneFlow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
