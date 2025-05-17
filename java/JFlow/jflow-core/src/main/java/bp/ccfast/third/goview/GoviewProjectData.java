package bp.ccfast.third.goview;

import bp.da.DBAccess;
import bp.en.EntityMyPK;
import bp.en.Map;
import bp.en.UAC;

/**
 * 简历
 */
public class GoviewProjectData extends EntityMyPK
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 12525225252L;
	

	
	/**
	 * 年月
	 */
	public final String getProjectId() {
		return this.GetValStringByKey(GoviewProjectDataAttr.ProjectId);
	}
	
	public final void setProjectId(String value)  {
		this.SetValByKey(GoviewProjectDataAttr.ProjectId, value);
	}
	
	/**
	 * 创建时间
	 */
	public final String getCreateTime() {
		return this.GetValStringByKey(GoviewProjectDataAttr.CreateTime);
	}
	
	public final void setCreateTime(String value)  {
		this.SetValByKey(GoviewProjectDataAttr.CreateTime, value);
	}
	
	/**
	 * 用户ID
	 */
	public final String getCreateUserId() {
		return this.GetValStringByKey(GoviewProjectDataAttr.CreateUserId);
	}
	
	public final void setCreateUserId(String value)  {
		this.SetValByKey(GoviewProjectDataAttr.CreateUserId, value);
	}
	
	/**
	 * 内容
	 */
	public final String getContent() {
		return this.GetValStringByKey(GoviewProjectDataAttr.Content);
	}
	
	public final void setContent(String value)  {
		this.SetValByKey(GoviewProjectDataAttr.Content, value);
	}
	
	/**
	 实体的权限控制
	 */
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	/**
	 * 构造函数
	 */
	public GoviewProjectData()
	{
	}
	
	public GoviewProjectData(String pkval) throws Exception
	{
		super(pkval);
	}
	
	/**
	 * 重写基类方法
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}
		Map map = new Map("goview_projectdatas");
		map.setEnDesc("项目数据");
		
		map.AddMyPK();
		
		
		map.AddTBString(GoviewProjectDataAttr.ProjectId, null, "项目ID", false, false, 0, 200, 10);
		map.AddTBString(GoviewProjectDataAttr.CreateTime, null, "创建时间", true, false, 0, 200, 50);
		map.AddTBString(GoviewProjectDataAttr.CreateUserId, null, "创建用户", true, false, 0,200, 70);
		map.AddTBStringDoc(GoviewProjectDataAttr.Content, "", "内容", false, false, false);
		
		this.set_enMap(map);
		return this.get_enMap();
	}
	protected boolean beforeInsert() throws Exception {
		this.setMyPK(DBAccess.GenerGUID());
		return super.beforeInsert();
	}
}