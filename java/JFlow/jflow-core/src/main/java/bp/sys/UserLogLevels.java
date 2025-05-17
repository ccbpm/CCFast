package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 级别
*/
public class UserLogLevels extends EntitiesNoName
{

		///#region 构造.
	/** 
	 级别s
	*/
	public UserLogLevels()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new UserLogLevel();
	}

		///#endregion 构造.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<UserLogLevel> ToJavaList()
	{
		return (java.util.List<UserLogLevel>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<UserLogLevel> Tolist()
	{
		ArrayList<UserLogLevel> list = new ArrayList<UserLogLevel>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((UserLogLevel)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
