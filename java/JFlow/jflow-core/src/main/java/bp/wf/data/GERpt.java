package bp.wf.data;

import bp.da.DataRow;
import bp.en.*;
import bp.sys.*;
import bp.wf.*;

/** 
 报表
*/
public class GERpt extends bp.en.EntityOID
{

	public final void setOID(long value){
		this.SetValByKey(GERptAttr.OID, value);
	}
	/** 
	 流程时间跨度
	*/
	public final float getFlowDaySpan()  {
		return this.GetValFloatByKey(GERptAttr.FlowDaySpan);
	}
	public final void setFlowDaySpan(float value){
		this.SetValByKey(GERptAttr.FlowDaySpan, value);
	}
	/** 
	 主流程ID
	*/
	public final long getFID()  {
		return this.GetValInt64ByKey(GERptAttr.FID);
	}
	public final void setFID(long value){
		this.SetValByKey(GERptAttr.FID, value);
	}
	public final String getGUID()  {
		return this.GetValStringByKey(GERptAttr.GUID);
	}
	public final void setGUID(String value){
		this.SetValByKey(GERptAttr.GUID, value);
	}
	/** 
	 流程参与人员
	*/
	public final String getFlowEmps()  {
		return this.GetValStringByKey(GERptAttr.FlowEmps);
	}
	public final void setFlowEmps(String value){
		this.SetValByKey(GERptAttr.FlowEmps, value);
	}
	/** 
	 流程备注
	*/
	public final String getFlowNote()  {
		return this.GetValStringByKey(GERptAttr.FlowNote);
	}
	public final void setFlowNote(String value){
		this.SetValByKey(GERptAttr.FlowNote, value);
	}
	/** 
	 客户编号
	*/
	public final String getGuestNo()  {
		return this.GetValStringByKey(GERptAttr.GuestNo);
	}
	public final void setGuestNo(String value){
		this.SetValByKey(GERptAttr.GuestNo, value);
	}
	/**
	 隶属年月
	 */
	public final String getFK_NY()  {
		return this.GetValStringByKey(GERptAttr.FK_NY);
	}

	/** 
	 客户名称
	*/
	public final String getGuestName()  {
		return this.GetValStringByKey(GERptAttr.GuestName);
	}
	public final void setGuestName(String value){
		this.SetValByKey(GERptAttr.GuestName, value);
	}
	public final String getBillNo()
	{
		try
		{
			return this.GetValStringByKey(GERptAttr.BillNo);
		}
		catch (RuntimeException ex)
		{
			return "";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public final void setBillNo(String value){
		this.SetValByKey(GERptAttr.BillNo, value);
	}
	/** 
	 流程发起人
	*/
	public final String getFlowStarter()  {
		return this.GetValStringByKey(GERptAttr.FlowStarter);
	}
	public final void setFlowStarter(String value){
		this.SetValByKey(GERptAttr.FlowStarter, value);
	}
	/** 
	 流程发起时间
	*/
	public final String getFlowStartRDT()  {
		return this.GetValStringByKey(GERptAttr.FlowStartRDT);
	}
	public final void setFlowStartRDT(String value){
		this.SetValByKey(GERptAttr.FlowStartRDT, value);
	}
	/** 
	 流程结束者
	*/
	public final String getFlowEnder()  {
		return this.GetValStringByKey(GERptAttr.FlowEnder);
	}
	public final void setFlowEnder(String value){
		this.SetValByKey(GERptAttr.FlowEnder, value);
	}
	/** 
	 流程最后处理时间
	*/
	public final String getFlowEnderRDT()  {
		return this.GetValStringByKey(GERptAttr.FlowEnderRDT);
	}
	public final void setFlowEnderRDT(String value){
		this.SetValByKey(GERptAttr.FlowEnderRDT, value);
	}
	public final String getFlowEndNodeText() throws Exception {
		Node nd = new Node(this.getFlowEndNode());
		return nd.getName();
	}
	public final int getFlowEndNode()  {
		return this.GetValIntByKey(GERptAttr.FlowEndNode);
	}
	public final void setFlowEndNode(int value){
		this.SetValByKey(GERptAttr.FlowEndNode, value);
	}
	/** 
	 流程标题
	*/
	public final String getTitle()  {
		return this.GetValStringByKey(GERptAttr.Title);
	}
	public final void setTitle(String value){
		this.SetValByKey(GERptAttr.Title, value);
	}
	/** 
	 隶属年月
	*/
	public final String getFK_Ny()  {
		return this.GetValStringByKey(GERptAttr.FK_NY);
	}
	public final void setFK_Ny(String value){
		this.SetValByKey(GERptAttr.FK_NY, value);
	}
	/** 
	 发起人部门
	*/
	public final String getFK_Dept()  {
		return this.GetValStringByKey(GERptAttr.FK_Dept);
	}
	public final void setFK_Dept(String value){
		this.SetValByKey(GERptAttr.FK_Dept, value);
	}
	/** 
	 流程状态
	*/
	public final WFState getWFState() {
		return WFState.forValue(this.GetValIntByKey(GERptAttr.WFState));
	}
	public final void setWFState(WFState value) throws Exception {
			//设置他的值.
		this.SetValByKey(GERptAttr.WFState, value.getValue());

			// 设置 WFSta 的值.
		switch (value)
	{case CompleteEnd:
				SetValByKey(GenerWorkFlowAttr.WFSta, WFSta.Complete.getValue());
				break;
			case Delete:
			case Blank:
				SetValByKey(GenerWorkFlowAttr.WFSta, WFSta.Etc.getValue());
				break;
			default:
				SetValByKey(GenerWorkFlowAttr.WFSta, WFSta.Runing.getValue());
				break;
		}

	}
	/** 
	 父流程WorkID
	*/
	public final long getPWorkID()  {
		return this.GetValInt64ByKey(GERptAttr.PWorkID);
	}
	public final void setPWorkID(long value){
		this.SetValByKey(GERptAttr.PWorkID, value);
	}
	/** 
	 发出的节点
	*/
	public final int getPNodeID()  {
		return this.GetValIntByKey(GERptAttr.PNodeID);
	}
	public final void setPNodeID(int value){
		this.SetValByKey(GERptAttr.PNodeID, value);
	}
	/** 
	 父流程流程编号
	*/
	public final String getPFlowNo()  {
		return this.GetValStringByKey(GERptAttr.PFlowNo);
	}
	public final void setPFlowNo(String value){
		this.SetValByKey(GERptAttr.PFlowNo, value);
	}
	public final String getPEmp()  {
		return this.GetValStringByKey(GERptAttr.PEmp);
	}
	public final void setPEmp(String value){
		this.SetValByKey(GERptAttr.PEmp, value);
	}
	/** 
	 项目编号
	*/
	public final String getPrjNo()  {
		return this.GetValStringByKey(GERptAttr.PrjNo);
	}
	public final void setPrjNo(String value){
		this.SetValByKey(GERptAttr.PrjNo, value);
	}
	public final String getPrjName()  {
		return this.GetValStringByKey(GERptAttr.PrjName);
	}
	public final void setPrjName(String value){
		this.SetValByKey(GERptAttr.PrjName, value);
	}

		///#endregion attrs


		///#region 重写。

	@Override
	public void Copy(DataRow dr) throws Exception {
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (attr.getKey().equals(WorkAttr.Rec) || attr.getKey().equals(WorkAttr.FID) || attr.getKey().equals(WorkAttr.OID) || attr.getKey().equals(WorkAttr.Emps) || attr.getKey().equals(GERptAttr.AtPara) || attr.getKey().equals(GERptAttr.BillNo) || attr.getKey().equals(GERptAttr.FID) || attr.getKey().equals(GERptAttr.FK_Dept) || attr.getKey().equals(GERptAttr.FK_NY) || attr.getKey().equals(GERptAttr.FlowDaySpan) || attr.getKey().equals(GERptAttr.FlowEmps) || attr.getKey().equals(GERptAttr.FlowEnder) || attr.getKey().equals(GERptAttr.FlowEnderRDT) || attr.getKey().equals(GERptAttr.FlowEndNode) || attr.getKey().equals(GERptAttr.FlowNote) || attr.getKey().equals(GERptAttr.FlowStarter) || attr.getKey().equals(GERptAttr.GuestName) || attr.getKey().equals(GERptAttr.GuestNo) || attr.getKey().equals(GERptAttr.GUID) || attr.getKey().equals(GERptAttr.PEmp) || attr.getKey().equals(GERptAttr.PFlowNo) || attr.getKey().equals(GERptAttr.PNodeID) || attr.getKey().equals(GERptAttr.PWorkID) || attr.getKey().equals(GERptAttr.Title) || attr.getKey().equals(GERptAttr.PrjNo) || attr.getKey().equals(GERptAttr.PrjName) || attr.getKey().equals("No") || attr.getKey().equals("Name"))
				continue;

			try
			{
				this.SetValByKey(attr.getKey(), dr.getValue(attr.getKey()));
			}
			catch (java.lang.Exception e)
			{
			}
		}
	}
	@Override
	public void Copy(Entity fromEn) throws Exception {
		if (fromEn == null)
		{
			return;
		}

		Attrs attrs = fromEn.getEnMap().getAttrs();
		for (Attr attr : attrs)
		{
			if (attr.getKey().equals(WorkAttr.Rec) || attr.getKey().equals(WorkAttr.FID) || attr.getKey().equals(WorkAttr.OID) || attr.getKey().equals(WorkAttr.Emps) || attr.getKey().equals(GERptAttr.AtPara) || attr.getKey().equals(GERptAttr.BillNo) || attr.getKey().equals(GERptAttr.FID) || attr.getKey().equals(GERptAttr.FK_Dept) || attr.getKey().equals(GERptAttr.FK_NY) || attr.getKey().equals(GERptAttr.FlowDaySpan) || attr.getKey().equals(GERptAttr.FlowEmps) || attr.getKey().equals(GERptAttr.FlowEnder) || attr.getKey().equals(GERptAttr.FlowEnderRDT) || attr.getKey().equals(GERptAttr.FlowEndNode) || attr.getKey().equals(GERptAttr.FlowNote) || attr.getKey().equals(GERptAttr.FlowStarter) || attr.getKey().equals(GERptAttr.GuestName) || attr.getKey().equals(GERptAttr.GuestNo) || attr.getKey().equals(GERptAttr.GUID) || attr.getKey().equals(GERptAttr.PEmp) || attr.getKey().equals(GERptAttr.PFlowNo) || attr.getKey().equals(GERptAttr.PNodeID) || attr.getKey().equals(GERptAttr.PWorkID) || attr.getKey().equals(GERptAttr.Title) || attr.getKey().equals(GERptAttr.PrjNo) || attr.getKey().equals(GERptAttr.PrjName) || attr.getKey().equals("No") || attr.getKey().equals("Name"))
			{
				continue;
			}


			this.SetValByKey(attr.getKey(), fromEn.GetValByKey(attr.getKey()));
		}
	}

		///#endregion


		///#region 属性.
	private String _RptName = null;
	public final String getRptName()
	{
		return _RptName;
	}
	public final void setRptName(String value) throws Exception {
		this._RptName = value;

		this._SQLCache = null;
		this.set_enMap(null);
		this.setRow(null);
	}
	@Override
	public String toString()  {
		return _RptName;
	}
	@Override
	public String getPK()
	{
		return "OID";
	}
	@Override
	public String getPKField()
	{
		return "OID";
	}
	/** 
	 Map
	*/
	@Override
	public bp.en.Map getEnMap() {
		if (this.getRptName() == null)
		{
			bp.port.Emp emp = new bp.port.Emp();
			return emp.getEnMap();
		}

		if (this.get_enMap() == null)
		{
			try {
				this.set_enMap(MapData.GenerHisMap(this.getRptName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this.get_enMap();
	}
	/** 
	 报表
	 
	 param rptName
	*/
	public GERpt(String rptName) throws Exception {
		this.setRptName(rptName);
	}
	public GERpt()
	{

	}
	/** 
	 报表
	 
	 param rptName
	 param oid
	*/
	public GERpt(String rptName, long oid) throws Exception {
		this.setRptName(rptName);
		this.setOID((int)oid);
		this.Retrieve();
	}

}