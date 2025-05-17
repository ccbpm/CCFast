package bp.ccbill;

import bp.en.*;
import java.util.*;

/** 
 数据源实体s
*/
public class DBLists extends EntitiesNoName
{

		///#region 构造
	/** 
	 数据源实体s
	*/
	public DBLists()
	{
	}

	@Override
	public Entity getNewEntity() {
		return new DBList();
	}

	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<DBList> ToJavaList()
	{
		return (java.util.List<DBList>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<DBList> Tolist()
	{
		ArrayList<DBList> list = new ArrayList<DBList>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((DBList)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
