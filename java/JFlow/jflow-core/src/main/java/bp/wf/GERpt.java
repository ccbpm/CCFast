package bp.wf;

import bp.da.DataRow;
import bp.da.DataType;
import bp.en.*;
import bp.en.Map;
import bp.sys.*;
import java.util.*;

/**
 报表
*/
public class GERpt extends bp.en.EntityOID
{
	/**
	 流程时间跨度
	*/
	public final float getFlowDaySpan() {
		return this.GetValFloatByKey(GERptAttr.FlowDaySpan);
	}
	public final void setFlowDaySpan(float value)  {
		this.SetValByKey(GERptAttr.FlowDaySpan, value);
	}
	/**
	 主流程ID
	*/
	public final long getFID() {
		return this.GetValInt64ByKey(GERptAttr.FID);
	}
	public final void setFID(long value)  {
		this.SetValByKey(GERptAttr.FID, value);
	}
	public final String getGUID() {
		return this.GetValStringByKey(GERptAttr.GUID);
	}
	public final void setGUID(String value)  {
		this.SetValByKey(GERptAttr.GUID, value);
	}
	/**
	 流程参与人员
	*/
	public final String getFlowEmps() {
		return this.GetValStringByKey(GERptAttr.FlowEmps);
	}
	public final void setFlowEmps(String value)  {
		this.SetValByKey(GERptAttr.FlowEmps, value);
	}

	/**
	 客户编号
	*/
	public final String getGuestNo() {
		return this.GetValStringByKey(GERptAttr.GuestNo);
	}
	public final void setGuestNo(String value)  {
		this.SetValByKey(GERptAttr.GuestNo, value);
	}
	/**
	 客户名称
	*/
	public final String getGuestName() {
		return this.GetValStringByKey(GERptAttr.GuestName);
	}
	public final void setGuestName(String value)  {
		this.SetValByKey(GERptAttr.GuestName, value);
	}
	public final String getBillNo()
	{
		try
		{
			return this.GetValStringByKey(GERptAttr.BillNo);
		}
		catch (Exception ex)
		{
			return "";
		}
	}
	public final void setBillNo(String value)  {
		this.SetValByKey(GERptAttr.BillNo, value);
	}
	/**
	 流程发起人
	*/
	public final String getFlowStarter() {
		return this.GetValStringByKey(GERptAttr.FlowStarter);
	}
	public final void setFlowStarter(String value)  {
		this.SetValByKey(GERptAttr.FlowStarter, value);
	}

	/**
	 流程发起时间
	*/
	public final String getFlowStartRDT() {
		return this.GetValStringByKey(GERptAttr.FlowStartRDT);
	}
	public final void setFlowStartRDT(String value)  {
		this.SetValByKey(GERptAttr.FlowStartRDT, value);
	}
	/**
	 流程结束者
	*/
	public final String getFlowEnder() {
		return this.GetValStringByKey(GERptAttr.FlowEnder);
	}
	public final void setFlowEnder(String value)  {
		this.SetValByKey(GERptAttr.FlowEnder, value);
	}
	/**
	 流程最后处理时间
	*/
	public final String getFlowEnderRDT() {
		return this.GetValStringByKey(GERptAttr.FlowEnderRDT);
	}
	public final void setFlowEnderRDT(String value)  {
		this.SetValByKey(GERptAttr.FlowEnderRDT, value);
	}
	public final String getFlowEndNodeText() throws Exception {
		Node nd = new Node(this.getFlowEndNode());
		return nd.getName();
	}
	public final int getFlowEndNode() {
		return this.GetValIntByKey(GERptAttr.FlowEndNode);
	}
	public final void setFlowEndNode(int value)  {
		this.SetValByKey(GERptAttr.FlowEndNode, value);
	}
	/**
	 流程标题
	*/
	public final String getTitle() {
		return this.GetValStringByKey(GERptAttr.Title);
	}
	public final void setTitle(String value)  {
		this.SetValByKey(GERptAttr.Title, value);
	}
	/**
	 隶属年月
	*/
	public final String getNY() {
		return this.GetValStringByKey(GERptAttr.FK_NY);
	}
	public final void setNY(String value)  {
		this.SetValByKey(GERptAttr.FK_NY, value);
	}
	/**
	 发起人部门
	*/
	public final String getDeptNo() {
		return this.GetValStringByKey(GERptAttr.FK_Dept);
	}
	public final void setDeptNo(String value)  {
		this.SetValByKey(GERptAttr.FK_Dept, value);
	}
	public final String getDeptName() {
		return this.GetValStringByKey(GERptAttr.FK_DeptName);
	}
	public final void setDeptName(String value)  {
		this.SetValByKey(GERptAttr.FK_DeptName, value);
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
		{
			case CompleteEnd:
			case CompleteByNode:
				SetValByKey(bp.wf.GenerWorkFlowAttr.WFSta, WFSta.Complete.getValue());
				break;
			case Delete:
			case Blank:
				SetValByKey(bp.wf.GenerWorkFlowAttr.WFSta, WFSta.Etc.getValue());
				break;
			default:
				SetValByKey(bp.wf.GenerWorkFlowAttr.WFSta, WFSta.Runing.getValue());
				break;
		}

	}
	/**
	 父流程WorkID
	*/
	public final long getPWorkID() {
		return this.GetValInt64ByKey(GERptAttr.PWorkID);
	}
	public final void setPWorkID(long value)  {
		this.SetValByKey(GERptAttr.PWorkID, value);
	}
	/**
	 发出的节点
	*/
	public final int getPNodeID() {
		return this.GetValIntByKey(GERptAttr.PNodeID);
	}
	public final void setPNodeID(int value)  {
		this.SetValByKey(GERptAttr.PNodeID, value);
	}
	/**
	 父流程流程编号
	*/
	public final String getPFlowNo() {
		return this.GetValStringByKey(GERptAttr.PFlowNo);
	}
	public final void setPFlowNo(String value)  {
		this.SetValByKey(GERptAttr.PFlowNo, value);
	}
	public final String getPEmp() {
		return this.GetValStringByKey(GERptAttr.PEmp);
	}
	public final void setPEmp(String value)  {
		this.SetValByKey(GERptAttr.PEmp, value);
	}
	/**
	 项目编号
	*/
	public final String getPrjNo() {
		return this.GetValStringByKey(GERptAttr.PrjNo);
	}
	public final void setPrjNo(String value)  {
		this.SetValByKey(GERptAttr.PrjNo, value);
	}
	public final String getPrjName() {
		return this.GetValStringByKey(GERptAttr.PrjName);
	}
	public final void setPrjName(String value)  {
		this.SetValByKey(GERptAttr.PrjName, value);
	}
	public final String getAtPara() {
		return this.GetValStringByKey(GERptAttr.AtPara);
	}
	public final void setAtPara(String value)  {
		this.SetValByKey(GERptAttr.AtPara, value);
	}
		///#endregion attrs


		///#region 重写。

	@Override
	public void Copy(DataRow dr) throws Exception {
		for (Attr attr : this.getEnMap().getAttrs())
		{
			if (Objects.equals(attr.getKey(), WorkAttr.Rec) || Objects.equals(attr.getKey(), WorkAttr.FID) || Objects.equals(attr.getKey(), WorkAttr.OID) || Objects.equals(attr.getKey(), WorkAttr.Emps) || Objects.equals(attr.getKey(), GERptAttr.AtPara) || Objects.equals(attr.getKey(), GERptAttr.BillNo) || Objects.equals(attr.getKey(), GERptAttr.FID) || Objects.equals(attr.getKey(), GERptAttr.FK_Dept) || Objects.equals(attr.getKey(), GERptAttr.FK_NY) || Objects.equals(attr.getKey(), GERptAttr.FlowDaySpan) || Objects.equals(attr.getKey(), GERptAttr.FlowEmps) || Objects.equals(attr.getKey(), GERptAttr.FlowEnder) || Objects.equals(attr.getKey(), GERptAttr.FlowEnderRDT) || Objects.equals(attr.getKey(), GERptAttr.FlowEndNode) || Objects.equals(attr.getKey(), GERptAttr.FlowStarter) || Objects.equals(attr.getKey(), GERptAttr.GuestName) || Objects.equals(attr.getKey(), GERptAttr.GuestNo) || Objects.equals(attr.getKey(), GERptAttr.GUID) || Objects.equals(attr.getKey(), GERptAttr.PEmp) || Objects.equals(attr.getKey(), GERptAttr.PFlowNo) || Objects.equals(attr.getKey(), GERptAttr.PNodeID) || Objects.equals(attr.getKey(), GERptAttr.PWorkID) || Objects.equals(attr.getKey(), GERptAttr.Title) || Objects.equals(attr.getKey(), GERptAttr.PrjNo) || Objects.equals(attr.getKey(), GERptAttr.PrjName) || attr.getKey().equals("No") || attr.getKey().equals("Name"))
			{
				continue;
			}


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
			if (Objects.equals(attr.getKey(), WorkAttr.Rec) || Objects.equals(attr.getKey(), WorkAttr.FID) || Objects.equals(attr.getKey(), WorkAttr.OID) || Objects.equals(attr.getKey(), WorkAttr.Emps) || Objects.equals(attr.getKey(), GERptAttr.AtPara) || Objects.equals(attr.getKey(), GERptAttr.BillNo) || Objects.equals(attr.getKey(), GERptAttr.FID) || Objects.equals(attr.getKey(), GERptAttr.FK_Dept) || Objects.equals(attr.getKey(), GERptAttr.FK_NY) || Objects.equals(attr.getKey(), GERptAttr.FlowDaySpan) || Objects.equals(attr.getKey(), GERptAttr.FlowEmps) || Objects.equals(attr.getKey(), GERptAttr.FlowEnder) || Objects.equals(attr.getKey(), GERptAttr.FlowEnderRDT) || Objects.equals(attr.getKey(), GERptAttr.FlowEndNode) || Objects.equals(attr.getKey(), GERptAttr.FlowStarter) || Objects.equals(attr.getKey(), GERptAttr.GuestName) || Objects.equals(attr.getKey(), GERptAttr.GuestNo) || Objects.equals(attr.getKey(), GERptAttr.GUID) || Objects.equals(attr.getKey(), GERptAttr.PEmp) || Objects.equals(attr.getKey(), GERptAttr.PFlowNo) || Objects.equals(attr.getKey(), GERptAttr.PNodeID) || Objects.equals(attr.getKey(), GERptAttr.PWorkID) || Objects.equals(attr.getKey(), GERptAttr.Title) || Objects.equals(attr.getKey(), GERptAttr.PrjNo) || Objects.equals(attr.getKey(), GERptAttr.PrjName) || Objects.equals(attr.getKey(), "No") || Objects.equals(attr.getKey(), "Name"))
			{
				continue;
			}
			if(attr.getUIContralType() == UIContralType.SignCheck)
			{
				String checkVal = this.GetValStringByKey(attr.getKey());
				String copyVal = fromEn.GetValStringByKey(attr.getKey());
				if (DataType.IsNullOrEmpty(copyVal) || checkVal.contains(copyVal))
					continue;
				this.SetValByKey(attr.getKey(), checkVal + copyVal);
			}
			else
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
	public String toString()
	{
		return getRptName();
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
	public Map getEnMap() {
		if (this.getRptName() == null)
		{
			bp.port.Emp emp = new bp.port.Emp();
			return emp.getEnMap();
		}

		if (this.get_enMap() == null)
		{
			try{
				this.set_enMap(MapData.GenerHisMap(this.getRptName()));
			}catch(Exception e){

			}

		}

		return this.get_enMap();
	}
	/**
	 报表

	 @param rptName
	*/
	public GERpt(String rptName) throws Exception {
		this.setRptName(rptName);
	}
	public GERpt()
	{

	}
	/**
	 报表

	 @param rptName
	 @param oid
	*/
	public GERpt(String rptName, long oid) throws Exception {
		this.setRptName(rptName);
		this.setOID((int)oid);
		this.Retrieve();
	}

		///#endregion attrs
}
