package bp.wf.data;

import bp.en.*;

import java.util.*;

/** 
 逾期流程s
*/
public class Delays extends EntitiesMyPK
{

		///#region 构造方法属性
	/** 
	 逾期流程s
	*/
	public Delays()throws Exception
	{
	}

		///#endregion


		///#region 属性
	/** 
	 逾期流程
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Delay();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Delay> ToJavaList()
	{
		return (java.util.List<Delay>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Delay> Tolist()
	{
		ArrayList<Delay> list = new ArrayList<Delay>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Delay)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}