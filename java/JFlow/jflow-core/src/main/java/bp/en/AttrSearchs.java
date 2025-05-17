package bp.en;

import java.io.Serializable;
import java.util.ArrayList;

/**
 查询属性s
*/
public class AttrSearchs extends ArrayList<AttrSearch> implements Serializable
{
	private static final long serialVersionUID = 1L;

	public AttrSearchs()
	{
	}

	public final void Add(Attr attr, boolean isShowSelectedAll, String relationalDtlKey)  {
		Add(attr, isShowSelectedAll, relationalDtlKey, 120);
	}

	public final void Add(Attr attr, boolean isShowSelectedAll, String relationalDtlKey, int width)  {
		AttrSearch en = new AttrSearch();
		en.HisAttr = attr;
		en.IsShowAll = isShowSelectedAll;
		en.RelationalDtlKey = relationalDtlKey;
		en.Key = attr.getKey();
		en.Width = width; //宽度.
		this.add(en);
	}

	public final void Add(AttrSearch attr)
	{
		this.add(attr);
	}
}
