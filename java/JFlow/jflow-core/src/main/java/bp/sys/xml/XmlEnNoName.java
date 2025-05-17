package bp.sys.xml;


public abstract class XmlEnNoName extends XmlEn
{
	public final String getNo() {
		return this.GetValStringByKey("No");
	}
	public final String getName() {
		return this.GetValStringByKey("Name");
	}
	public XmlEnNoName()
	{
	}
	public XmlEnNoName(String no) throws Exception  {
	   int i = this.RetrieveByPK("No", no);
	   if (i == 0)
	   {
		   throw new RuntimeException("@没有查询到 No =" + no + " XML数据.");
	   }
	}
}
