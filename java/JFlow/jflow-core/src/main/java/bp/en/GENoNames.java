package bp.en;

import java.util.*;

/** 
 GENoNames
*/
public class GENoNames extends EntitiesNoName
{

	private static final long serialVersionUID = 1L;
	/** 
	 物理表
	*/
	public String SFTable = null;
	public String Desc = null;

	/** 
	 GENoNames
	*/
	public GENoNames()throws Exception
	{
	}
	public GENoNames(String sftable, String tableDesc)
	{
		this.SFTable = sftable;
		this.Desc = tableDesc;
	}
	@Override
	public Entity getNewEntity()
	{
		return new GENoName(this.SFTable, this.Desc);
	}
	@Override
	public int RetrieveAll() throws Exception
	{
		return this.RetrieveAllFromDBSource();
	}



		///为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GENoName> ToJavaList()throws Exception
	{
		return (java.util.List<GENoName>)(Object)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GENoName> Tolist()throws Exception
	{
		ArrayList<GENoName> list = new ArrayList<GENoName>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GENoName)this.get(i));
		}
		return list;
	}

		/// 为了适应自动翻译成java的需要,把实体转换成List.
}