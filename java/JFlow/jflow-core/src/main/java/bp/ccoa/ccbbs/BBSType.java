package bp.ccoa.ccbbs;

import bp.en.*;
import bp.en.Map;

/** 
 类型
*/
public class BBSType extends EntityNoName
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
	public BBSType()
	{
	}
	public BBSType(String no) throws Exception
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

		Map map = new Map("OA_BBSType", "类型类型");
		map.setCodeStruct("3");
		map.AddTBStringPK(BBSTypeAttr.No, null, "编号", false, true, 3, 3, 3);
		map.AddTBString(BBSTypeAttr.Name, null, "名称", true, false, 0, 100, 10, true);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
