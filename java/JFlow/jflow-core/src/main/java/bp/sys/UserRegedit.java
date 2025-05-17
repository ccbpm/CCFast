package bp.sys;

import bp.da.*;
import bp.en.*;
import bp.en.Map;
import bp.web.*;
import java.util.*;

/** 
 用户注册表
*/
public class UserRegedit extends EntityMyPK
{

		///#region 用户注册表信息键值列表

		///#endregion

	/** 
	 是否使用自动的MyPK,即FK_Emp + CfgKey
	*/
	private boolean AutoMyPK;
	public final boolean getAutoMyPK()
	{
		return AutoMyPK;
	}
	public final void setAutoMyPK(boolean value)
	{
		AutoMyPK = value;
	}


		///#region 基本属性
	/** 
	 是否显示图片
	*/
	public final boolean getItIsPic()  {
		return this.GetValBooleanByKey(UserRegeditAttr.IsPic);
	}
	public final void setItIsPic(boolean value){
		this.SetValByKey(UserRegeditAttr.IsPic, value);
	}
	/** 
	 数值键
	*/
	public final String getNumKey()  {
		return this.GetValStringByKey(UserRegeditAttr.NumKey);
	}
	public final void setNumKey(String value){
		this.SetValByKey(UserRegeditAttr.NumKey, value);
	}
	/** 
	 参数
	*/
	public final String getParas()  {
		return this.GetValStringByKey(UserRegeditAttr.Paras);
	}
	public final void setParas(String value){
		this.SetValByKey(UserRegeditAttr.Paras, value);
	}
	/** 
	 产生的sql
	*/
	public final String getGenerSQL() throws Exception {
		String GenerSQL = this.GetValStringByKey(UserRegeditAttr.GenerSQL);
		GenerSQL = GenerSQL.replace("~", "'");
		return GenerSQL;
	}
	public final void setGenerSQL(String value){
		this.SetValByKey(UserRegeditAttr.GenerSQL, value);
	}
	/** 
	 排序方式
	*/
	public final String getOrderWay()  {
		return this.GetValStringByKey(UserRegeditAttr.OrderWay);
	}

	public final String getOrderBy()  {
		return this.GetValStringByKey(UserRegeditAttr.OrderBy);
	}

	/** 
	 FK_Emp
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(UserRegeditAttr.FK_Emp);
	}

	/** 
	 查询时间从
	*/
	public final String getDTFromData() throws Exception {
		String s = this.GetValStringByKey(UserRegeditAttr.DTFrom);
		if (DataType.IsNullOrEmpty(s) || 1 == 1)
		{
			Date dt = new Date();
			return DataType.dateToStr(DataType.AddDays(dt, -14), "yyyy-MM-dd");
		}
		return s.substring(0, 10);
	}

	/** 
	 到
	*/
	public final String getDTToData() throws Exception {
		String s = this.GetValStringByKey(UserRegeditAttr.DTTo);
		if (DataType.IsNullOrEmpty(s) || 1 == 1)
		{
			return DataType.getCurrentDataTime();
		}
		return s.substring(0, 10);
	}


	/** 
	 查询时间从
	*/
	public final String getDTFrom()  {
		return this.GetValStringByKey(UserRegeditAttr.DTFrom);

	}

	/** 
	 到
	*/
	public final String getDTTo()  {
		return this.GetValStringByKey(UserRegeditAttr.DTTo);

	}
	public final void setDTTo(String value){
		this.SetValByKey(UserRegeditAttr.DTTo, value);
	}
	public final String getDTFromDatatime() throws Exception {
		String s = this.GetValStringByKey(UserRegeditAttr.DTFrom);
		if (DataType.IsNullOrEmpty(s))
		{
			Date dt = new Date();

			return DataType.dateToStr(DataType.AddDays(dt, -14), "yyyy-MM-dd");
		}
		return s;
	}
	public final void setDTFromDatatime(String value){
		this.SetValByKey(UserRegeditAttr.DTFrom, value);
	}
	/** 
	 到
	*/
	public final String getDTToDatatime() throws Exception {
		String s = this.GetValStringByKey(UserRegeditAttr.DTTo);
		if (DataType.IsNullOrEmpty(s))
		{
			Date dt = new Date();
			return DataType.getCurrentDataTime();
		}
		return s;
	}
	public final void setDTToDatatime(String value){
		this.SetValByKey(UserRegeditAttr.DTTo, value);
	}
	/** 
	 CfgKey
	*/
	public final String getCfgKey()  {
		return this.GetValStringByKey(UserRegeditAttr.CfgKey);
	}

	public final String getSearchKey()  {
		return this.GetValStringByKey(UserRegeditAttr.SearchKey);
	}
	public final void setSearchKey(String value){
		this.SetValByKey(UserRegeditAttr.SearchKey, value);
	}
	/** 
	 Vals
	*/
	public final String getVals()  {
		return this.GetValStringByKey(UserRegeditAttr.Vals);
	}
	/**
	 Vals
	 */
	public final String getDTSearchKey()  {
		return this.GetValStringByKey("DTSearchKey");
	}

	public final String getMVals()  {
		return this.GetValStringByKey(UserRegeditAttr.MVals);
	}

	public final String getMyPK()  {
		return this.GetValStringByKey(UserRegeditAttr.MyPK);
	}


		///#endregion


		///#region 构造方法
	/** 
	 用户注册表
	*/
	public UserRegedit()
	{
		setAutoMyPK(true);
	}
	public final void setMyPK(String val){
		this.SetValByKey("MyPK", val);
	}
	public final void setFK_Emp(String val){
		this.SetValByKey("FK_Emp", val);
	}
	public final void setCfgKey(String val){
		this.SetValByKey("CfgKey", val);
	}

	/** 
	 用户注册表
	 
	 @param fk_emp 人员
	 @param cfgkey 配置
	*/
	public UserRegedit(String fk_emp, String cfgkey) throws Exception {
		this();
		this.SetValByKey("MyPK", fk_emp + cfgkey);
		this.SetValByKey("CfgKey", cfgkey);
		this.SetValByKey("FK_Emp", fk_emp);

		int i = this.RetrieveFromDBSources();
		if (i == 0)
		{
			this.SetValByKey("CfgKey", cfgkey);
			this.SetValByKey("FK_Emp", fk_emp);
			//this.CfgKey = cfgkey;
			//this.setEmpNo(fk_emp;
			this.DirectInsert();
			// this.DirectInsert();
		}
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
		Map map = new Map("Sys_UserRegedit", "用户注册表");
		map.setEnType(EnType.Sys);

		map.AddMyPK();
		map.AddTBString(UserRegeditAttr.FK_Emp, null, "用户", false, false, 0, 100, 20);
		map.AddTBString(UserRegeditAttr.CfgKey, null, "键", true, false, 0, 200, 20);
		map.AddTBString(UserRegeditAttr.Vals, null, "值", true, false, 0, 2000, 20);
		map.AddTBString(UserRegeditAttr.GenerSQL, null, "GenerSQL", true, false, 0, 2000, 20);
		map.AddTBString(UserRegeditAttr.Paras, null, "Paras", true, false, 0, 2000, 20);
		map.AddTBString(UserRegeditAttr.NumKey, null, "分析的Key", true, false, 0, 300, 20);
		map.AddTBString(UserRegeditAttr.OrderBy, null, "OrderBy", true, false, 0, 300, 20);
		map.AddTBString(UserRegeditAttr.OrderWay, null, "OrderWay", true, false, 0, 300, 20);
		map.AddTBString(UserRegeditAttr.SearchKey, null, "SearchKey", true, false, 0, 300, 20);
		map.AddTBString(UserRegeditAttr.MVals, null, "MVals", true, false, 0, 2000, 20);
		map.AddBoolean(UserRegeditAttr.IsPic, false, "是否图片", true, false);

		map.AddTBString(UserRegeditAttr.DTFrom, null, "查询时间从", true, false, 0, 20, 20);
		map.AddTBString(UserRegeditAttr.DTTo, null, "到", true, false, 0, 20, 20);
		map.AddTBString("DTSearchKey", null, "日期查询字段", true, false, 0, 20, 20);


		map.AddTBString("OrgNo", null, "OrgNo", true, false, 0, 100, 32);


		//增加属性.
		map.AddTBAtParas(4000);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 重写
	@Override
	public Entities GetNewEntities()
	{
		return new UserRegedits();
	}
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception{
		this.SetValByKey("OrgNo", WebUser.getOrgNo());
		return super.beforeUpdateInsertAction();
	}

		///#endregion 重写

	/** 
	 获取键/值对集合
	 
	 @return 
	*/
	public final HashMap<String, String> GetVals() throws Exception {
		if (DataType.IsNullOrEmpty(this.getVals()))
		{
			return new HashMap<String, String>();
		}

		String[] arr = null;
		String[] strs = this.getVals().split("@");
		int idx = -1;
		HashMap<String, String> kvs = new HashMap<String, String>();

		for (String str : strs)
		{
			idx = str.indexOf('=');

			if (idx == -1)
			{
				continue;
			}

			kvs.put(str.substring(0, idx), idx == str.length() - 1 ? "" : str.substring(idx + 1));
		}

		return kvs;
	}

	/** 
	 获取当前用户是否具有导入数据的权限
	 <p>added by liuxc,2017-04-30</p>
	 注意：此权限数据保存于Sys_Regedit.Paras字段中，为@ImpEmpNos=liyan,liping,ligen格式
	 
	 @param ensName 集合类全名，如bp.port.Emps
	 @return 
	*/
	public static boolean HaveRoleForImp(String ensName) throws Exception {
		//获取可导入权限
		UserRegedit ur = new UserRegedit("admin", ensName + "_SearchAttrs");
		String impEmps = (new AtPara(ur.getParas())).GetValStrByKey("ImpEmpNos");

		if (DataType.IsNullOrEmpty(impEmps))
		{
			return true;
		}
		else
		{
			return WebUser.getNo().equals("admin") == true || ("," + impEmps + ",").indexOf("," + WebUser.getNo() + ",") != -1;
		}
	}

	/** 
	 获取当前用户是否具有导出数据的权限
	 <p>added by liuxc,2017-04-30</p>
	 注意：此权限数据保存于Sys_Regedit.Paras字段中，为@ExpEmpNos=liyan,liping,ligen格式
	 
	 @param ensName 集合类全名，如bp.port.Emps
	 @return 
	*/
	public static boolean HaveRoleForExp(String ensName) throws Exception {
		//获取可导入权限
		UserRegedit ur = new UserRegedit("admin", ensName + "_SearchAttrs");
		String impEmps = (new AtPara(ur.getParas())).GetValStrByKey("ExpEmpNos");
		if (DataType.IsNullOrEmpty(impEmps))
		{
			return true;
		}
		else
		{
			return WebUser.getNo().equals("admin") == true || ("," + impEmps + ",").indexOf("," + WebUser.getNo() + ",") != -1;
		}
	}
}
