package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.difference.*;


/** 
 用户日志
*/
public class UserLog extends EntityMyPK
{
	@Override
	public UAC getHisUAC() {
		UAC uac = new UAC();
		uac.Readonly();
		return uac;
	}

	///#region 基本属性
	public final String getIP()  {
		return this.GetValStringByKey(UserLogAttr.IP);
	}
	public final void setIP(String value){
		this.SetValByKey(UserLogAttr.IP, value);
	}
	/** 
	 日志标记键
	*/
	public final String getLogFlag()  {
		return this.GetValStringByKey(UserLogAttr.LogFlag);
	}
	public final void setLogFlag(String value){
		this.SetValByKey(UserLogAttr.LogFlag, value);
	}
	/** 
	 FK_Emp
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(UserLogAttr.EmpNo);
	}
	public final void setEmpNo(String value){
		this.SetValByKey(UserLogAttr.EmpNo, value);
	}
	public final String getEmpName()  {
		return this.GetValStringByKey(UserLogAttr.EmpName);
	}
	public final void setEmpName(String value){
		this.SetValByKey(UserLogAttr.EmpName, value);
	}
	public final String getRDT()  {
		return this.GetValStringByKey(UserLogAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(UserLogAttr.RDT, value);
	}

	public final String getDocs()  {
		return this.GetValStringByKey(UserLogAttr.Docs);
	}
	public final void setDocs(String value){
		this.SetValByKey(UserLogAttr.Docs, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 用户日志
	*/
	public UserLog()
	{
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
		Map map = new Map("Sys_UserLogT", "用户日志");
		map.AddMyPK();
		map.AddTBString(UserLogAttr.EmpNo, null, "用户账号", true, true, 0, 50, 20);
		map.AddTBString(UserLogAttr.EmpName, null, "用户名", true, true, 0, 30, 20);
		map.AddTBString(UserLogAttr.RDT, null, "记录日期", true, true, 0, 20, 20);
		map.AddTBString(UserLogAttr.IP, null, "IP", true, true, 0, 200, 20);
		map.AddTBStringDoc(UserLogAttr.Docs, null, "说明", true, true, true);

		//map.AddDDLEntities(UserLogAttr.LogFlag, null, "类型", new UserLogTypes(), false);
		//map.AddDDLEntities(UserLogAttr.Level, null, "级别", new UserLogLevels(), false);
		map.AddTBString(UserLogAttr.LogFlag, null, "类型", true, true, 0, 200, 20);
		map.AddTBString(UserLogAttr.LevelText, null, "级别", true, true, 0, 200, 20);


		map.DTSearchKey = UserLogAttr.RDT;
		map.DTSearchWay = DTSearchWay.ByDate;

		//查询条件.
		map.AddSearchAttr(UserLogAttr.LevelText);
		map.AddSearchAttr(UserLogAttr.LogFlag);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception {
		this.setMyPK(DBAccess.GenerGUID());
		this.setRDT(DataType.getCurrentDateTime());
		if (SystemConfig.isBSsystem()) {
			this.setIP(bp.difference.Glo.getIP());
		}
		if (DataType.IsNullOrEmpty(this.getEmpNo())) {
			this.setEmpNo(bp.web.WebUser.getNo());
			this.setEmpName(bp.web.WebUser.getName());
		}
		return super.beforeInsert();
	}


		///#region 重写
	@Override
	public Entities GetNewEntities()
	{
		return new UserLogs();
	}

		///#endregion 重写
}
