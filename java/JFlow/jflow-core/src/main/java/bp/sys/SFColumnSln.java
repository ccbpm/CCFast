package bp.sys;

import bp.en.*;
import bp.en.Map;

/** 
 查询列s
*/
public class SFColumnSln extends EntityMyPK
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
	public SFColumnSln()
	{
	}
	public SFColumnSln(String no) throws Exception  {
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
		Map map = new Map("Sys_SFColumnSln", "查询列转换方案");

		map.AddMyPK();
		//对应的是： MapExt的 MyPK
		map.AddTBString("RefPKVal", null, "实体主键", false, false, 1, 200, 20);
		map.AddTBString("FrmID", null, "表单", false, false, 1, 200, 20);
		map.AddTBString("AttrKey", null, "列英文名", true, true, 1, 200, 100);
		map.AddTBString("AttrName", null, "列中文名", true, true, 0, 200, 100);
		map.AddBoolean("IsEnable", true, "启用?", true, true);
		map.AddTBString("DataType", null, "列中文名", true, true, 0, 200, 100);

		// const sql=""
		map.AddTBString("ToField", null, "转换列名", true, true, 0, 200, 100);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
