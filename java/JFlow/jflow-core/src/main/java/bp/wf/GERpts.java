package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 报表s
*/
public class GERpts extends bp.en.EntitiesOID
{
	/** 
	 报表s
	*/
	public GERpts()
	{
	}

	/** 
	 获得一个实例.
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GERpt();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GERpt> ToJavaList()
	{
		return (java.util.List<GERpt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GERpt> Tolist()
	{
		ArrayList<GERpt> list = new ArrayList<GERpt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GERpt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
