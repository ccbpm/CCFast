package bp.ccfast.third.goview;

import bp.en.EntityMyPK;
import bp.en.Map;
import bp.en.UAC;

/** 
  项目
*/
public class GoviewProject extends EntityMyPK
{
	


		///#region 属性

	public final String getProjectname()  {
		return this.GetValStrByKey(GoviewProjectAttr.ProjectName);
	}
	public final void setProjectname(String value){
		this.SetValByKey(GoviewProjectAttr.ProjectName, value);
	}
	public final Integer getState()  {
		return this.GetValIntByKey(GoviewProjectAttr.State);
	}
	public final void setState(Integer value){
		this.SetValByKey(GoviewProjectAttr.State, value);
	}


	public final String getCreatetime()  {
		return this.GetValStrByKey(GoviewProjectAttr.CreateTime);
	}
	public final void setCreatetime(String value){
		this.SetValByKey(GoviewProjectAttr.CreateTime, value);
	}

	public final String getCreateuserid()  {
		return this.GetValStrByKey(GoviewProjectAttr.CreateUserId);
	}
	public final void setCreateuserid(String value){
		this.SetValByKey(GoviewProjectAttr.CreateUserId, value);
	}
	public final Integer getIsdelete()  {
		return this.GetValIntByKey(GoviewProjectAttr.isDelete);
	}
	public final void setIsdelete(Integer value){
		this.SetValByKey(GoviewProjectAttr.isDelete, value);
	}
	public final String getRemarks()  {
		return this.GetValStrByKey(GoviewProjectAttr.Remarks);
	}
	public final void setRemarks(String value){
		this.SetValByKey(GoviewProjectAttr.Remarks, value);
	}
	public final String getIndeximage()  {
		return this.GetValStrByKey(GoviewProjectAttr.indexImage);
	}
	public final void setIndeximage(String value){
		this.SetValByKey(GoviewProjectAttr.indexImage, value);
	}
	public final Integer getIsTemplate()  {
		return this.GetValIntByKey(GoviewProjectAttr.isTemplate);
	}
	public final void setIsTemplate(Integer value){
		this.SetValByKey(GoviewProjectAttr.isTemplate, value);
	}

	
		///#endregion


		///#region 实现基本的方方法
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}

		///#endregion


		///#region 构造方法
	/** 
	 登录记录
	*/
	public GoviewProject()
	{
	}
	/** 
	 登录记录
	 
	 @param pkval
	*/
	public GoviewProject(String pkval) throws Exception {
		super(pkval);
	}

		///#endregion

	/**
	 * 登录记录Map
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("goview_projects", "goview项目信息");
		map.setCodeStruct("2");

		//map.AddTBIntPKOID();
		//map.add
		map.AddMyPK();
		//map.AddTBStringPK(GoviewProjectAttr.OID, null, "OID", true, false, 0, 100, 20);
		map.AddTBString(GoviewProjectAttr.ProjectName, null, "项目名称", true, false, 0, 100, 20);
//		map.AddTBString(GoviewProjectAttr.State, null, "项目状态", true, false, 0, 100, 20);
		map.AddTBInt(GoviewProjectAttr.State, 0, "项目状态", true, false);

		map.AddTBString(GoviewProjectAttr.CreateTime, null, "创建时间", true, false, 0, 100, 20);
		map.AddTBString(GoviewProjectAttr.CreateUserId, null, "创建用户ID", true, false, 0, 100, 20);

//		map.AddTBString(GoviewProjectAttr.isDelete, null, "是否删除", true, false, 0, 100, 20);
		map.AddTBInt(GoviewProjectAttr.isDelete, 0, "是否删除", true, false);
		map.AddTBString(GoviewProjectAttr.Remarks, null, "备注", true, false, 0, 100, 20);

		map.AddTBDateTime(GoviewProjectAttr.indexImage, null, "图片索引", true, false);
		map.AddTBInt(GoviewProjectAttr.isTemplate, 0, "是否模板", true, false);

		this.set_enMap(map);
		return this.get_enMap();
	}
//	protected boolean beforeInsert() throws Exception {
//		this.setMyPK(DBAccess.GenerGUID());
//		return super.beforeInsert();
//	}

}
