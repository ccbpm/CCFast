package bp.pub;

import java.util.ArrayList;

public class Rpt2Attrs extends ArrayList<Rpt2Attr>
{
	private static final long serialVersionUID = 1L;
	public final void Add(Rpt2Attr en)
	{
		this.add(en);
	}
	public final Rpt2Attr GetD2(int idx)
	{
		return (Rpt2Attr)this.get(idx);
	}
}
