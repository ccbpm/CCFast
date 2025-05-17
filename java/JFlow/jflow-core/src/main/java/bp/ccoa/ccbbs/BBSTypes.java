package bp.ccoa.ccbbs;

import bp.en.*;
import java.util.*;

/** 
 类型 s
*/
public class BBSTypes extends EntitiesNoName
{

		///#region 重写.
	/** 
	 类型
	*/
	public BBSTypes()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new BBSType();
	}

		///#endregion 重写.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<BBSType> ToJavaList()
	{
		return (java.util.List<BBSType>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<BBSType> Tolist()
	{
		ArrayList<BBSType> list = new ArrayList<BBSType>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((BBSType)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
