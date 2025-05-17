package bp.en;

import java.io.Serializable;
import java.util.ArrayList;

/**
 查询属性s
*/
public class SearchFKEnums extends ArrayList<SearchFKEnum> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public SearchFKEnums()
	{
	}

	public final void Add(Attr attr, boolean isShowSelectedAll, String relationalDtlKey)
	{
		Add(attr, isShowSelectedAll, relationalDtlKey, 120);
	}

	public final void Add(Attr attr, boolean isShowSelectedAll, String relationalDtlKey, int width)
	{
		SearchFKEnum en = new SearchFKEnum();
		en.HisAttr = attr;
		en.IsShowAll = isShowSelectedAll;
		en.RelationalDtlKey = relationalDtlKey;
		en.Key = attr.getKey();
		en.Width = width; //宽度.
		this.Add(en);
	}
	public final void Add(SearchFKEnum attr)
	{
		this.add(attr);
	}
}
