package bp.ccfast.portal.windowext;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccfast.*;
import bp.ccfast.portal.*;
import java.util.*;

/** 
 表格s
*/
public class Tables extends EntitiesNoName
{

		///#region 构造
	/** 
	 表格s
	*/
	public Tables()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Table();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Table> ToJavaList()
	{
		return (java.util.List<Table>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Table> Tolist()
	{
		ArrayList<Table> list = new ArrayList<Table>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Table)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
