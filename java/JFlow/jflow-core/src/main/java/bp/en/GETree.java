package bp.en;

/** 
 树结构实体
*/
public class GETree extends EntityNoName
{

		///构造
	@Override
	public String toString()  {
		return this.PhysicsTable;
	}
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	public GETree()
	{

	}
	/** 
	 编号
	 
	 param no 编号
	 * @throws Exception 
	*/
	public GETree(String no) throws Exception  {
		super(no);

	}
	public GETree(String sftable, String tableDesc)
	{
		this.PhysicsTable = sftable;
		this.Desc = tableDesc;
	}
	@Override
	public bp.en.Map getEnMap()
	{
		Map map = new Map(this.PhysicsTable, this.Desc);
		map.setItIsAutoGenerNo(true);

		map.AddTBStringPK(GETreeAttr.No, null, "编号", true, true, 1, 30, 3);
		map.AddTBString(GETreeAttr.Name, null, "名称", true, false, 1, 60, 500);
		return map;

	}
	public String PhysicsTable = null;
	public String Desc = null;


		///
}