package bp.demo;

import bp.da.DBUrl;
import bp.da.DBUrlType;
import bp.da.Depositary;
import bp.en.EnType;
import bp.en.Entities;
import bp.en.EntityNoName;
import bp.en.Map;
import bp.en.UAC;

/**
 * 科目
 */
public class KeMu extends EntityNoName
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 13464374747L;
	
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	
	/**
	 * 构造函数
	 */
	public KeMu()
	{
	}
	
	public KeMu(String no) throws Exception
	{
		super(no);
	}
	
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map();
		
		map.setEnDBUrl(new DBUrl(DBUrlType.AppCenterDSN));
		map.setPhysicsTable("Demo_KeMu");
		map.setDepositaryOfEntity(Depositary.None);
		map.setIsAllowRepeatName(true); // 是否允许名称重复.
		map.setIsCheckNoLength(false);
		map.setEnDesc("科目");
		map.setEnType(EnType.App);
		map.setCodeStruct("3");// 让其编号为3位, 从001 到 999 .
		
		map.AddTBStringPK(KeMuAttr.No, null, "编号", true, true, 3, 3, 3);
		map.AddTBString(KeMuAttr.Name, null, "名称", true, false, 0, 50, 200);
		
		this.set_enMap(map);
		return this.get_enMap();
	}
	
	@Override
	public Entities GetNewEntities()
	{
		return new KeMus();
	}
}