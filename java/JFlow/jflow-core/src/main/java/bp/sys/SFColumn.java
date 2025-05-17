package bp.sys;

import bp.en.*; import bp.en.Map;

/** 
 查询列s
*/
public class SFColumn extends EntityMyPK
{


		///#region 构造方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.Readonly();
		return uac;
	}
	/** 
	 用户自定义表
	*/
	public SFColumn()
	{
	}
	public SFColumn(String no) throws Exception  {
		this.setMyPK(no);
		this.Retrieve();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("Sys_SFColumn", "查询列");

		map.AddMyPK();
		map.AddTBString("RefPKVal", null, "实体主键", false, false, 1, 200, 20);
		map.AddTBString("AttrKey", null, "列英文名", true, true, 1, 200, 100);
		map.AddTBString("AttrName", null, "列中文名", true, false, 0, 200, 100);
		map.AddTBString("DataType", null, "数据类型", true, false, 0, 200, 100);

	 // map.AddDDLStringEnum("DataType", "String", "数据类型", "@String=String@Int=Int@Float=Float", true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
