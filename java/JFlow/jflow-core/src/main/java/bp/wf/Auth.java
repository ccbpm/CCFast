package bp.wf;

import bp.en.*; import bp.en.Map;

/** 
 授权
*/
public class Auth extends EntityMyPK
{

		///#region 基本属性
	/** 
	 流程编号
	*/
	public final String getFlowNo() {
		return this.GetValStringByKey(AuthAttr.FlowNo);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(AuthAttr.FlowNo, value);
	}
	/** 
	 取回日期
	*/
	public final String getTakeBackDT()  {
		return this.GetValStringByKey(AuthAttr.TakeBackDT);
	}
	public final void setTakeBackDT(String value){
		this.SetValByKey(AuthAttr.TakeBackDT, value);
	}
	public final String getAuther()  {
		return this.GetValStringByKey(AuthAttr.Auther);
	}
	public final void setAuther(String value){
		this.SetValByKey(AuthAttr.Auther, value);
	}
	public final String getAutherToEmpNo()  {
		return this.GetValStringByKey(AuthAttr.AutherToEmpNo);
	}
	public final void setAutherToEmpNo(String value){
		this.SetValByKey(AuthAttr.AutherToEmpNo, value);
	}
	public final String getAutherToEmpName()  {
		return this.GetValStringByKey(AuthAttr.AutherToEmpName);
	}
	public final void setAutherToEmpName(String value){
		this.SetValByKey(AuthAttr.AutherToEmpName, value);
	}
	/** 
	 授权类型
	*/
	public final AuthorWay getAuthType() {
		return AuthorWay.forValue(this.GetValIntByKey(AuthAttr.AuthType));
	}
	public final void setAuthType(AuthorWay value){
		this.SetValByKey(AuthAttr.AuthType, value.getValue());
	}

		///#endregion


		///#region 构造方法
	/** 
	 授权
	*/
	public Auth()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Auth", "授权");
		map.AddMyPK(true);

		map.AddTBString(AuthAttr.Auther, null, "授权人", true, false, 0, 100, 10);
		map.AddTBString(AuthAttr.AutherName, null, "授权人", true, false, 0, 100, 10);
		map.AddDDLSysEnum(AuthAttr.AuthType, 0, "授权方式", true, false, AuthAttr.AuthType, "@0=全部流程@1=指定流程@2=指定流程+部门");

		map.AddTBString(AuthAttr.AutherToEmpNo, null, "授权给谁?", true, false, 0, 100, 10);
		map.AddTBString(AuthAttr.AutherToEmpName, null, "授权给谁?", true, false, 0, 100, 10);

		map.AddTBString(AuthAttr.FlowNo, null, "流程编号", true, false, 0, 100, 10);
		map.AddTBString(AuthAttr.FlowName, null, "流程名称", true, false, 0, 100, 10);
		map.AddTBString(AuthAttr.FlowNoT, null, "流程名称", true, false, 0, 100, 10);

		map.AddTBDate(AuthAttr.TakeBackDT, null, "取回日期", true, false);
		map.AddTBDate(AuthAttr.RDT, null, "记录日期", true, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
