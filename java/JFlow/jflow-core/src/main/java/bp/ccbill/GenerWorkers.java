package bp.ccbill;

import bp.en.EntitiesMyPK;
import bp.en.Entity;

import java.util.ArrayList;
import java.util.List;

/** 
 按钮s
*/
public class GenerWorkers extends EntitiesMyPK
{

	///#region 构造
	/// <summary>
	/// 单据控制表s
	/// </summary>
	public GenerWorkers()
	{
	}

	/// <summary>
	/// 得到它的 Entity
	/// </summary>
	@Override
	public Entity getNewEntity()
	{
		return new GenerWorker();
	}

	/** 
	 转化成 java list,C#不能调用.
	 @return List
	*/
	public final List<GenerWorker> ToJavaList()
	{
		return (List<GenerWorker>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GenerWorker> Tolist()
	{
		ArrayList<GenerWorker> list = new ArrayList<GenerWorker>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GenerWorker)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
