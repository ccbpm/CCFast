package bp.wf.template;

import bp.en.*; import bp.en.Map;
import bp.sys.*;


/** 
 抄送
*/
public class FlowTab extends EntityMyPK
{

		///#region 属性
	public final String getName()  {
		return this.GetValStrByKey(FlowTabAttr.Name);
	}
	public final void setName(String value){
		this.SetValByKey(FlowTabAttr.Name, value);
	}
	public final String getFlowNo()  {
		return this.GetValStrByKey(FlowTabAttr.FK_Flow);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(FlowTabAttr.FK_Flow, value);
	}
	public final boolean getItIsEnable()  {
		return this.GetValBooleanByKey(FlowTabAttr.IsEnable);
	}
	public final void setItIsEnable(boolean value){
		this.SetValByKey(FlowTabAttr.IsEnable, value);
	}
	public final String getOrgNo()  {
		return this.GetValStrByKey(FlowTabAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(FlowTabAttr.OrgNo, value);
	}
	public final String getTip()  {
		return this.GetValStrByKey(FlowTabAttr.Tip);
	}
	public final void setTip(String value){
		this.SetValByKey(FlowTabAttr.Tip, value);
	}
	public final String getUrlExt()  {
		return this.GetValStrByKey(FlowTabAttr.UrlExt);
	}
	public final void setUrlExt(String value){
		this.SetValByKey(FlowTabAttr.UrlExt, value);
	}
	public final String getMark()  {
		return this.GetValStrByKey(FlowTabAttr.Mark);
	}
	public final void setMark(String value){
		this.SetValByKey(FlowTabAttr.Mark, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 抄送设置
	*/
	public FlowTab()
	{
	}
	/** 
	 抄送设置
	 
	 @param mypk
	*/
	public FlowTab(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
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

		Map map = new Map("WF_FlowTab", "流程功能");

		map.AddMyPK(true);
		map.AddTBString(FlowTabAttr.Name, null, "标签", true, true, 0, 100, 10, false);
		map.AddTBString(FlowTabAttr.FK_Flow, null, "流程编号", false, false, 0, 4, 10);
		map.AddTBString(FlowTabAttr.Mark, null, "标记", false, false, 0, 50, 10);
		map.AddTBString(FlowTabAttr.Tip, null, "Tip", false, false, 0, 200, 10);

		map.AddTBInt(FlowTabAttr.IsEnable, 1, "IsEnable", true, false);
		map.AddTBString(FlowTabAttr.UrlExt, null, "url链接", false, false, 0, 300, 10);
		map.AddTBString(FlowTabAttr.Icon, null, "Icon", false, false, 0, 50, 10);
		map.AddTBString(FlowTabAttr.OrgNo, null, "OrgNo", false, false, 0, 50, 10);
		map.AddTBInt(FlowTabAttr.Idx, 0, "Idx", true, true);


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
		}
		else
		{
			this.setOrgNo(bp.web.WebUser.getOrgNo());
		}
		return super.beforeInsert();
	}
}
