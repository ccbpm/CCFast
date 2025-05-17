package bp.sys;

import bp.en.*;
import bp.en.Map;

/** 
  级别
*/
public class UserLogLevel extends EntityNoName
{

		///#region 构造方法
	/** 
	 级别
	*/
	public UserLogLevel()
	{
	}
	/** 
	 表单目录
	 
	 @param _No
	*/
	public UserLogLevel(String _No) throws Exception {
		super(_No);
	}
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.Readonly();

		return uac;
	}

		///#endregion

	/** 
	 级别
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_UserLogLevel", "级别");

		map.AddTBStringPK("No", null, "编号", true, true, 1, 100, 300);
		map.AddTBString("Name", null, "名称", true, false, 0, 100, 600);

		this.set_enMap(map);
		return this.get_enMap();
	}
}
