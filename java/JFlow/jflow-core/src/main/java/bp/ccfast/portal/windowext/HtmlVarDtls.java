package bp.ccfast.portal.windowext;

import bp.en.*;
import java.util.*;

/** 
 变量信息s
*/
public class HtmlVarDtls extends EntitiesMyPK
{

		///#region 构造
	/** 
	 变量信息s
	*/
	public HtmlVarDtls()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new HtmlVarDtl();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<HtmlVarDtl> ToJavaList()
	{
		return (java.util.List<HtmlVarDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<HtmlVarDtl> Tolist()
	{
		ArrayList<HtmlVarDtl> list = new ArrayList<HtmlVarDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((HtmlVarDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
