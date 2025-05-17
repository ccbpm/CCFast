package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 信息块s
*/
public class HtmlVars extends EntitiesNoName
{

		///#region 构造
	/** 
	 信息块s
	*/
	public HtmlVars()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new HtmlVar();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<HtmlVar> ToJavaList()
	{
		return (java.util.List<HtmlVar>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<HtmlVar> Tolist()
	{
		ArrayList<HtmlVar> list = new ArrayList<HtmlVar>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((HtmlVar)this.get(i));

		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
