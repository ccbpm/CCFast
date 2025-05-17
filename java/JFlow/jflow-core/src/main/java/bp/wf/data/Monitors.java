package bp.wf.data;
import bp.en.*;
import java.util.*;

/** 
 流程监控s
*/
public class Monitors extends Entities
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Monitor();
	}
	/** 
	 流程监控集合
	*/
	public Monitors()throws Exception
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Monitor> ToJavaList()throws Exception
	{
		return (java.util.List<Monitor>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Monitor> Tolist()throws Exception
	{
		ArrayList<Monitor> list = new ArrayList<Monitor>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Monitor)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}