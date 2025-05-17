package bp.wf;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import java.util.*;

/** 
 抄送
*/
public class CCList extends EntityMyPK
{

		///#region 属性
	/** 
	 状态
	*/
	public final CCSta getHisSta() {
		return CCSta.forValue(this.GetValIntByKey(CCListAttr.Sta));
	}
	public final void setHisSta(CCSta value) throws Exception {
		//@sly 这里去掉了业务逻辑.
		if (value == CCSta.Read)
		{
			this.setReadDT(DataType.getCurrentDateTime());
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
		if (!Objects.equals(bp.web.WebUser.getNo(), "admin"))
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
	public final String getDomainExt() {
		return this.GetValStringByKey(CCListAttr.DomainExt);
	}
	public final void setDomainExt(String value)  {
		this.SetValByKey(CCListAttr.DomainExt, value);
	}
	/** 
	 抄送给
	*/
	public final String getCCTo() {
		return this.GetValStringByKey(CCListAttr.CCTo);
	}
	public final void setCCTo(String value)  {
		this.SetValByKey(CCListAttr.CCTo, value);
	}
	public final String getOrgNo() {
		return this.GetValStringByKey(CCListAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(CCListAttr.OrgNo, value);
	}

	public final int getToNodeID() {
		return this.GetValIntByKey(CCListAttr.ToNodeID);
	}
	public final void setToNodeID(int value)  {
		this.SetValByKey(CCListAttr.ToNodeID, value);
	}
	public final String getToNodeName() {
		return this.GetValStringByKey(CCListAttr.ToNodeName);
	}
	public final void setToNodeName(String value)  {
		this.SetValByKey(CCListAttr.ToNodeName, value);
	}
	/** 
	 抄送给Name
	*/
	public final String getCCToName() throws Exception {
		String s = this.GetValStringByKey(CCListAttr.CCToName);
		if (DataType.IsNullOrEmpty(s))
		{
			s = this.getCCTo();
		}
		return s;
	}
	public final void setCCToName(String value)  {
		this.SetValByKey(CCListAttr.CCToName, value);
	}
	/** 
	 读取时间
	*/
	public final String getCDT() {
		return this.GetValStringByKey(CCListAttr.CDT);
	}
	public final void setCDT(String value)  {
		this.SetValByKey(CCListAttr.CDT, value);
	}
	/** 
	 抄送人所在的节点编号
	*/
	public final int getNodeIDWork() {
		return this.GetValIntByKey(CCListAttr.NodeIDWork);
	}
	public final void setNodeIDWork(int value)  {
		this.SetValByKey(CCListAttr.NodeIDWork, value);
	}

	public final long getWorkID() {
		return this.GetValInt64ByKey(CCListAttr.WorkID);
	}
	public final void setWorkID(long value)  {
		this.SetValByKey(CCListAttr.WorkID, value);
	}
	public final long getFID() {
		return this.GetValInt64ByKey(CCListAttr.FID);
	}
	public final void setFID(long value)  {
		this.SetValByKey(CCListAttr.FID, value);
	}
	/** 
	 父流程工作ID
	*/
	public final long getPWorkID() {
		return this.GetValInt64ByKey(CCListAttr.PWorkID);
	}
	public final void setPWorkID(long value)  {
		this.SetValByKey(CCListAttr.PWorkID, value);
	}
	/** 
	 父流程编号
	*/
	public final String getPFlowNo() {
		return this.GetValStringByKey(CCListAttr.PFlowNo);
	}
	public final void setPFlowNo(String value)  {
		this.SetValByKey(CCListAttr.PFlowNo, value);
	}
	public final String getFlowName() {
		return this.GetValStringByKey(CCListAttr.FlowName);
	}
	public final void setFlowName(String value)  {
		this.SetValByKey(CCListAttr.FlowName, value);
	}
	public final String getNodeName() {
		return this.GetValStringByKey(CCListAttr.NodeName);
	}
	public final void setNodeName(String value)  {
		this.SetValByKey(CCListAttr.NodeName, value);
	}
	/** 
	 抄送标题
	*/
	public final String getTitle() {
		return this.GetValStringByKey(CCListAttr.Title);
	}
	public final void setTitle(String value)  {
		this.SetValByKey(CCListAttr.Title, value);
	}
	/** 
	 抄送内容
	*/
	public final String getDoc() {
		return this.GetValStringByKey(CCListAttr.Doc);
	}
	public final void setDoc(String value)  {
		this.SetValByKey(CCListAttr.Doc, value);
	}
	public final String getDocHtml() throws Exception {
		return this.GetValHtmlStringByKey(CCListAttr.Doc);
	}
	/** 
	 抄送对象
	*/
	public final String getFlowNo() {
		return this.GetValStringByKey(CCListAttr.FlowNo);
	}
	public final void setFlowNo(String value)  {
		this.SetValByKey(CCListAttr.FlowNo, value);
	}
	public final String getRecEmpNo() {
		return this.GetValStringByKey(CCListAttr.RecEmpNo);
	}
	public final void setRecEmpNo(String value)  {
		this.SetValByKey(CCListAttr.RecEmpNo, value);
	}
	public final String getRecEmpName() {
		return this.GetValStringByKey(CCListAttr.RecEmpName);
	}
	public final void setRecEmpName(String value)  {
		this.SetValByKey(CCListAttr.RecEmpName, value);
	}
	/** 
	 读取日期
	*/
	public final String getReadDT() {
		return this.GetValStringByKey(CCListAttr.ReadDT);
	}
	public final void setReadDT(String value)  {
		this.SetValByKey(CCListAttr.ReadDT, value);
	}
	/** 
	 写入日期
	*/
	public final String getRDT() {
		return this.GetValStringByKey(CCListAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(CCListAttr.RDT, value);
	}
	/** 
	 是否加入待办列表
	*/
	public final boolean getInEmpWorks() {
		return this.GetValBooleanByKey(CCListAttr.InEmpWorks);
	}
	public final void setInEmpWorks(boolean value)  {
		this.SetValByKey(CCListAttr.InEmpWorks, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 CCList
	*/
	public CCList()
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
		Map map = new Map("WF_CCList", "抄送列表");

		map.AddMyPK(true); //组合主键 WorkID+"_"+NodeID+"_"+FK_Emp
		map.AddTBString(CCListAttr.Title, null, "标题", true, true, 0, 500, 10, true);
		//状态  @0=抄送@1=已读@2=已回复@3=已删除
		map.AddTBInt(CCListAttr.Sta, 0, "状态", true, true);
		map.AddTBString(CCListAttr.FlowNo, null, "流程编号", true, true, 0, 5, 10, true);
		map.AddTBString(CCListAttr.FlowName, null, "名称", true, true, 0, 200, 10, true);

		map.AddTBInt(CCListAttr.NodeIDWork, 0, "节点", true, true);
		map.AddTBString(CCListAttr.NodeName, null, "节点名称", true, true, 0, 500, 10, true);

		map.AddTBInt(CCListAttr.WorkID, 0, "工作ID", true, true);
		map.AddTBInt(CCListAttr.FID, 0, "FID", true, true);

		map.AddTBString(CCListAttr.CCTo, null, "抄送给", true, false, 0, 50, 10, true);
		map.AddTBString(CCListAttr.CCToName, null, "抄送给(人员名称)", true, false, 0, 50, 10, true);

		map.AddTBString(CCListAttr.DeptNo, null, "被抄送人部门", true, false, 0, 50, 10, true);
		map.AddTBString(CCListAttr.DeptName, null, "被抄送人部门", true, false, 0, 50, 10, true);

		map.AddTBDateTime(CCListAttr.ReadDT, null, "阅读时间", true, false);

		map.AddTBString(CCListAttr.PFlowNo, null, "父流程编号", true, true, 0, 100, 10, true);
		map.AddTBInt(CCListAttr.PWorkID, 0, "父流程WorkID", true, true);
		map.AddBoolean(CCListAttr.InEmpWorks, false, "是否加入待办列表", true, true);

		map.AddTBString(CCListAttr.RecEmpNo, null, "抄送人员", true, true, 0, 50, 10, true);
		map.AddTBString(CCListAttr.RecEmpName, null, "抄送人员", true, true, 0, 50, 10, true);
		map.AddTBDateTime(CCListAttr.RDT, null, "抄送日期", true, false);

		//add by zhoupeng  
		map.AddTBString(CCListAttr.DomainExt, null, "DomainExt", true, true, 0, 50, 10, true);
		map.AddTBString(CCListAttr.OrgNo, null, "OrgNo", true, true, 0, 50, 10, true);

		map.AddTBInt(CCListAttr.NodeIDCC, 0, "抄送到节点ID", true, true); //工作节点.
		map.AddTBString(CCListAttr.NodeIDCCName, null, "抄送到节点名称", true, true, 0, 50, 10, true);

		this.set_enMap(map);
		return this.get_enMap();
	}
		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (this.getOrgNo() == null)
		{
			this.setOrgNo(bp.web.WebUser.getOrgNo());
		}
		return super.beforeInsert();
	}
}
