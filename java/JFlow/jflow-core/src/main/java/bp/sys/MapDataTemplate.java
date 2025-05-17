package bp.sys;

import bp.en.*;
import bp.en.Map;


/** 
 映射基础
*/
public class MapDataTemplate extends EntityNoName
{

		///#region 构造方法
	/** 
	 映射基础
	*/
	public MapDataTemplate()
	{
	}
	/** 
	 映射基础
	 
	 @param no 映射编号
	*/
	public MapDataTemplate(String no) throws Exception
	{
		super(no);
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_MapData", "表单模版表");
		map.setCodeStruct("4");


			///#region 基础信息.
		map.AddTBStringPK(MapDataTemplateAttr.No, null, "编号", true, false, 1, 150, 100);
		map.AddTBString(MapDataTemplateAttr.Name, null, "描述", true, false, 0, 200, 20);
		map.AddTBString(MapDataTemplateAttr.PTable, null, "物理表", true, false, 0, 100, 20);
		map.AddTBInt("IsTemplate", 0, "是否是表单模版", true, false);

			///#endregion 基础信息.


			///#region 设计者信息.
		//增加参数字段.
		map.AddTBAtParas(4000);

			///#endregion

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion 构造方法

}
