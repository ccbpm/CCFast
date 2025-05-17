package bp.ccfast.ccmenu;

import bp.en.*;

import java.util.ArrayList;

/** 
 查询条件s
*/
public class SearchAttrs extends EntitiesNoName
{

		///#region 构造
	/** 
	 查询条件s
	*/
	public SearchAttrs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SearchAttr();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SearchAttr> ToJavaList()
	{
		return (java.util.List<SearchAttr>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SearchAttr> Tolist()
	{
		ArrayList<SearchAttr> list = new ArrayList<SearchAttr>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SearchAttr)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
