package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 变量信息s
*/
public class Dtls extends EntitiesMyPK
{

		///#region 构造
	/** 
	 变量信息s
	*/
	public Dtls()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Dtl();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Dtl> ToJavaList()
	{
		return (java.util.List<Dtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Dtl> Tolist()
	{
		ArrayList<Dtl> list = new ArrayList<Dtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Dtl)this.get(i));

		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
