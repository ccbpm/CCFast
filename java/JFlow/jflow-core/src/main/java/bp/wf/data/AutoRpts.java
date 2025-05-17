package bp.wf.data;
import bp.en.*;
import java.util.*;

/** 
 自动报表
*/
public class AutoRpts extends EntitiesNoName
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new AutoRpt();
	}
	/** 
	 自动报表
	*/
	public AutoRpts()throws Exception
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<AutoRpt> ToJavaList()throws Exception
	{
		return (java.util.List<AutoRpt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<AutoRpt> Tolist()throws Exception
	{
		ArrayList<AutoRpt> list = new ArrayList<AutoRpt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((AutoRpt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}