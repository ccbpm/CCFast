package bp.ccoa.ccbbs;

import bp.en.*;
import java.util.*;

/** 
 信息 s
*/
public class BBSs extends EntitiesNoName
{

		///#region 构造函数.
	/** 
	 信息
	*/
	public BBSs()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new BBS();
	}

		///#endregion 构造函数.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<BBS> ToJavaList()
	{
		return (java.util.List<BBS>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<BBS> Tolist()
	{
		ArrayList<BBS> list = new ArrayList<BBS>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((BBS)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
