package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 用户注册表s
*/
public class UserRegedits extends EntitiesMyPK
{

		///#region 构造
	public UserRegedits()
	{
	}
	/** 
	 
	 
	 @param emp
	*/
	public UserRegedits(String emp) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(UserRegeditAttr.FK_Emp, emp);
		qo.DoQuery();
	}

		///#endregion


		///#region 重写
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new UserRegedit();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成IList, c#代码调用会出错误。
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<UserRegedit> ToJavaList()
	{
		return (java.util.List<UserRegedit>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<UserRegedit> Tolist()
	{
		ArrayList<UserRegedit> list = new ArrayList<UserRegedit>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((UserRegedit)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
