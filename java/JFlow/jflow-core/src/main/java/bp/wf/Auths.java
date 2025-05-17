package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 授权
*/
public class Auths extends EntitiesMyPK
{
	/** 
	 授权
	*/
	public Auths()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Auth();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Auth> ToJavaList()
	{
		return (java.util.List<Auth>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Auth> Tolist()
	{
		ArrayList<Auth> list = new ArrayList<Auth>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Auth)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
