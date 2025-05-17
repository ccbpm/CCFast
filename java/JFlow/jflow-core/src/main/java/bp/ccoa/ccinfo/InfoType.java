package bp.ccoa.ccinfo;

import bp.en.*;
import bp.en.Map;

/** 
 类型
*/
public class InfoType extends EntityNoName
{

		///#region 基本属性

		///#endregion


		///#region 构造方法
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsUpdate = true;
		uac.IsInsert = true;
		return uac;
	}
	/** 
	 类型
	*/
	public InfoType()
	{
	}
	public InfoType(String no) throws Exception
	{
		this.setNo(no);
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("OA_InfoType", "类型类型");
		map.setCodeStruct("3");
		map.AddTBStringPK(InfoTypeAttr.No, null, "编号", false, true, 3, 3, 3);
		map.AddTBString(InfoTypeAttr.Name, null, "名称", true, false, 0, 100, 10, true);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
