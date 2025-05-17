package bp.en;

import java.io.Serializable;
import java.util.ArrayList;


public class AARefs extends ArrayList<Object>  implements Serializable
{

	private static final long serialVersionUID = 1L;

	///构造
	public AARefs()throws Exception
	{
	}

	/**
	 增加一个查询属性

	 param lab 标签
	 param refKey 实体的属性
	 param defaultvalue 默认值
	 * @throws Exception
	*/
	public final void Add(String lab, String key, String refKey, String defaultSymbol, String defaultvalue, int tbWidth) throws Exception
	{
		SearchNormal aos = new SearchNormal(key, lab, refKey, defaultSymbol, defaultvalue, tbWidth, false);
		this.add(aos);
	}
}
