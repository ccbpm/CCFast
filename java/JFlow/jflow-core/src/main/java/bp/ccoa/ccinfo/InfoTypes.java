package bp.ccoa.ccinfo;

import bp.en.*;
import java.util.*;

/** 
 类型 s
*/
public class InfoTypes extends EntitiesNoName
{
	/** 
	 类型
	*/
	public InfoTypes()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new InfoType();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<InfoType> ToJavaList()
	{
		return (java.util.List<InfoType>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<InfoType> Tolist()
	{
		ArrayList<InfoType> list = new ArrayList<InfoType>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((InfoType)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
