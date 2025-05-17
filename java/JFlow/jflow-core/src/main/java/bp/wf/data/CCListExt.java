package bp.wf.data;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.*;
import bp.en.Map;
import bp.sys.DTSearchWay;
import bp.wf.*;

/** 
 抄送
*/
public class CCListExt extends EntityMyPK
{

		///#region 属性
	/** 
	 状态
	*/
	public final CCSta getHisSta()throws Exception
	{
		return CCSta.forValue(this.GetValIntByKey(CCListAttr.Sta));
	}
	public final void setHisSta(CCSta value) throws Exception {
		if (value == CCSta.Read)
		{
			this.setReadDT(DataType.getCurrentDataTime());
		}
		this.SetValByKey(CCListAttr.Sta, value.getValue());
	}
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{

		UAC uac = new UAC();
		if (!bp.web.WebUser.getNo().equals("admin"))
		{
			uac.IsView = false;
			return uac;
		}
		uac.IsDelete = false;
		uac.IsInsert = false;
		uac.IsUpdate = true;
		return uac;
	}
	/** 
	 域
	*/
	public final String getDomainExt()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.DomainExt);
	}
	public final void setDomainExt(String value){
		this.SetValByKey(CCListAttr.DomainExt, value);
	}
	/** 
	 抄送给
	*/
	public final String getCCTo()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.CCTo);
	}
	public final void setCCTo(String value){
		this.SetValByKey(CCListAttr.CCTo, value);
	}
	public final String getOrgNo()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(CCListAttr.OrgNo, value);
	}
	/** 
	 抄送给Name
	*/
	public final String getCCToName()throws Exception
	{
		String s = this.GetValStringByKey(CCListAttr.CCToName);
		if (DataType.IsNullOrEmpty(s))
		{
			s = this.getCCTo();
		}
		return s;
	}
	public final void setCCToName(String value){
		this.SetValByKey(CCListAttr.CCToName, value);
	}

	/** 
	 父流程工作ID
	*/
	public final long getPWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(CCListAttr.PWorkID);
	}
	public final void setPWorkID(long value){
		this.SetValByKey(CCListAttr.PWorkID, value);
	}
	/** 
	 父流程编号
	*/
	public final String getPFlowNo()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.PFlowNo);
	}
	public final void setPFlowNo(String value){
		this.SetValByKey(CCListAttr.PFlowNo, value);
	}

	public final String getFlowName()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.FlowName);
	}
	public final void setFlowName(String value){
		this.SetValByKey(CCListAttr.FlowName, value);
	}
	public final String getNodeName()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.NodeName);
	}
	public final void setNodeName(String value){
		this.SetValByKey(CCListAttr.NodeName, value);
	}
	/** 
	 抄送标题
	*/
	public final String getTitle()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.Title);
	}
	public final void setTitle(String value){
		this.SetValByKey(CCListAttr.Title, value);
	}
	/** 
	 抄送内容
	*/
	public final String getDoc()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.Doc);
	}
	public final void setDoc(String value){
		this.SetValByKey(CCListAttr.Doc, value);
	}
	public final String getDocHtml()  throws Exception
	{
		return this.GetValHtmlStringByKey(CCListAttr.Doc);
	}
	/** 
	 抄送对象
	*/
	public final String getFlowNo()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.FlowNo);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(CCListAttr.FlowNo, value);
	}
	public final String getRecEmpNo()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.RecEmpNo);
	}
	public final void setRecEmpNo(String value){
		this.SetValByKey(CCListAttr.RecEmpNo, value);
	}
	/** 
	 读取日期
	*/
	public final String getReadDT()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.ReadDT);
	}
	public final void setReadDT(String value){
		this.SetValByKey(CCListAttr.ReadDT, value);
	}
	/** 
	 写入日期
	*/
	public final String getRDT()  throws Exception
	{
		return this.GetValStringByKey(CCListAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(CCListAttr.RDT, value);
	}
	/** 
	 是否加入待办列表
	*/
	public final boolean getInEmpWorks()  throws Exception
	{
		return this.GetValBooleanByKey(CCListAttr.InEmpWorks);
	}
	public final void setInEmpWorks(boolean value){
		this.SetValByKey(CCListAttr.InEmpWorks, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 CCListExt
	*/
	public CCListExt()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public bp.en.Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_CCList", "抄送");

		map.AddMyPK(); //组合主键 WorkID+"_"+FK_Node+"_"+FK_Emp
		map.AddTBInt(CCListAttr.WorkID, 0, "工作ID", false, true);
		map.AddTBInt(CCListAttr.NodeIDCC, 0, "节点", false, false);
		map.AddTBInt(CCListAttr.NodeIDWork, 0, "节点", false, false);
		map.AddTBInt(CCListAttr.FID, 0, "FID", false, false);
		map.AddTBString(CCListAttr.FlowNo, null, "流程编号", false, false, 0, 5, 10, true);

		map.AddTBString(CCListAttr.Title, null, "标题", true, true, 0, 500, 10, true);
		map.AddDDLSysEnum(CCListAttr.Sta, 0, "状态", true, false, "CCSta", "@0=未读@1=已读@2=已回复@3=删除");

		map.AddTBString(CCListAttr.FlowName, null, "流程", true, true, 0, 200, 10, true);
		map.AddTBString(CCListAttr.NodeName, null, "节点", true, true, 0, 500, 10, true);
		map.AddTBString(CCListAttr.RecEmpNo, null, "抄送人", false, false, 0, 50, 10, false);
		map.AddTBString(CCListAttr.RecEmpName, null, "抄送人", true, false, 0, 50, 10, true);
		map.AddTBDateTime(CCListAttr.RDT, null, "抄送日期", true, true);

		map.AddTBString(CCListAttr.CCTo, null, "抄送给", false, false, 0, 50, 10, true);
		map.AddTBString(CCListAttr.CCToName, null, "抄送给(人员名称)", false, false, 0, 50, 10, true);

		map.AddTBString(CCListAttr.OrgNo, null, "组织", false, false, 0, 50, 10, true);
		map.AddTBDateTime(CCListAttr.CDT, null, "打开时间", true, true);
		map.AddTBDateTime(CCListAttr.ReadDT, null, "阅读时间", true, true);

		//add by zhoupeng
		map.AddTBString(CCListAttr.DomainExt, null, "DomainExt", false, true, 0, 50, 10, true);
		map.AddTBString(CCListAttr.OrgNo, null, "OrgNo", false, true, 0, 50, 10, true);

         //#region 查询条件.
		map.DTSearchLabel = "抄送日期";
		map.DTSearchKey = CCListAttr.RDT;
		map.DTSearchWay = bp.sys.DTSearchWay.ByDate;

		map.AddSearchAttr(CCListAttr.Sta); //按状态.

		//增加隐藏条件.
		if (bp.difference.SystemConfig.getCCBPMRunModel() == bp.sys.CCBPMRunModel.Single
				|| bp.difference.SystemConfig.getCCBPMRunModel() == bp.sys.CCBPMRunModel.GroupInc)
		{
			map.AddHidden(CCListAttr.CCTo, "=", "@WebUser.No");
		}
		else
		{
			map.AddHidden(CCListAttr.OrgNo, "=", "@WebUser.OrgNo");
			map.AddHidden(CCListAttr.CCTo, "=", "@WebUser.No");
		}
        //#endregion 查询条件.


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}