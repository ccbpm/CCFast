package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 框架信息块s
*/
public class iFrames extends EntitiesNoName
{

		///#region 构造
	/** 
	 框架信息块s
	*/
	public iFrames()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new iFrame();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<iFrame> ToJavaList()
	{
		return (java.util.List<iFrame>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<iFrame> Tolist()
	{
		ArrayList<iFrame> list = new ArrayList<iFrame>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((iFrame)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
