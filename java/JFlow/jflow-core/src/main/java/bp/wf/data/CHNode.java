package bp.wf.data;

import bp.en.EntityMyPK;
import bp.en.UAC;
import bp.en.Map;
import java.util.*;

/**
 节点时限
 */
public class CHNode extends EntityMyPK
{
	///#region 基本属性
	/**
	 工作ID
	 */
	public final long getWorkID() {
		return this.GetValInt64ByKey(CHAttr.WorkID);
	}
	public final void setWorkID(long value)  {
		this.SetValByKey(CHAttr.WorkID, value);
	}


	/**
	 节点ID
	 */
	public final int getNodeID() {
		return this.GetValIntByKey(CHAttr.FK_Node);
	}
	public final void setNodeID(int value)  {
		this.SetValByKey(CHAttr.FK_Node, value);
	}
	/**
	 节点名称
	 */
	public final String getNodeName()  {
		return this.GetValStrByKey(CHNodeAttr.NodeName);
	}
	public final void setNodeName(String value)  {
		this.SetValByKey(CHNodeAttr.NodeName, value);
	}

	/**
	 操作人员
	 */
	public final String getEmpNo() {
		return this.GetValStringByKey(CHAttr.FK_Emp);
	}
	public final void setEmpNo(String value)  {
		this.SetValByKey(CHAttr.FK_Emp, value);
	}
	/**
	 人员
	 */
	public final String getEmpT() {
		return this.GetValStringByKey(CHAttr.FK_EmpT);
	}
	public final void setEmpT(String value)  {
		this.SetValByKey(CHAttr.FK_EmpT, value);
	}

	/**
	 计划开始时间
	 */
	public final String getStartDT() {
		return this.GetValStringByKey(CHNodeAttr.StartDT);
	}
	public final void setStartDT(String value)  {
		this.SetValByKey(CHNodeAttr.StartDT, value);
	}
	/**
	 计划完成时间
	 */
	public final String getEndDT() {
		return this.GetValStringByKey(CHNodeAttr.EndDT);
	}
	public final void setEndDT(String value)  {
		this.SetValByKey(CHNodeAttr.EndDT, value);
	}

	/**
	 工天
	 */
	public final int getGT() {
		return this.GetValIntByKey(CHNodeAttr.GT);
	}
	public final void setGT(int value)  {
		this.SetValByKey(CHNodeAttr.GT, value);
	}
	/**
	 阶段占比
	 */
	public final float getScale() {
		return this.GetValFloatByKey(CHNodeAttr.Scale);
	}
	public final void setScale(float value)  {
		this.SetValByKey(CHNodeAttr.Scale, value);
	}

	/**
	 总体进度
	 */
	public final float getTotalScale() {
		return this.GetValFloatByKey(CHNodeAttr.TotalScale);
	}
	public final void setTotalScale(float value)  {
		this.SetValByKey(CHNodeAttr.TotalScale, value);
	}
	/**
	 产值
	 */
	public final float getChanZhi() {
		return this.GetValFloatByKey(CHNodeAttr.ChanZhi);
	}
	public final void setChanZhi(float value)  {
		this.SetValByKey(CHNodeAttr.ChanZhi, value);
	}

	/**
	 UI界面上的访问控制
	 */
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsDelete = false;
		uac.IsInsert = false;
		uac.IsUpdate = false;
		uac.IsView = true;
		return uac;
	}
	/**
	 节点时限
	 */
	public CHNode()
	{
	}
	/**


	 @param pk
	 */
	public CHNode(String pk) throws Exception {
		super(pk);
	}

	/**
	 * EnMap
	 */
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_CHNode", "节点时限");

		map.AddTBInt(CHNodeAttr.WorkID, 0, "WorkID", true, true);
		map.AddTBInt(CHNodeAttr.FK_Node, 0, "节点", true, true);
		map.AddTBString(CHNodeAttr.NodeName, null, "节点名称", true, true, 0, 50, 5);

		map.AddTBString(CHAttr.FK_Emp, null, "处理人", true, true, 0, 100, 3);
		map.AddTBString(CHAttr.FK_EmpT, null, "处理人名称", true, true, 0, 200, 5);

		map.AddTBString(CHNodeAttr.StartDT, null, "计划开始时间", true, true, 0, 50, 5);
		map.AddTBString(CHNodeAttr.EndDT, null, "计划结束时间", true, true, 0, 50, 5);
		map.AddTBInt(CHNodeAttr.GT, 0, "工天", true, true);
		map.AddTBFloat(CHNodeAttr.Scale, 0, "阶段占比", true, true);
		map.AddTBFloat(CHNodeAttr.TotalScale, 0, "总进度", true, true);
		map.AddTBFloat(CHNodeAttr.ChanZhi, 0, "产值", true, true);

		map.AddTBAtParas(500);

		map.AddTBStringPK(CHNodeAttr.MyPK, null, "MyPK", false, false, 0, 50, 5);


		this.set_enMap(map);
		return this.get_enMap();
	}
	///#endregion
	@Override
	protected boolean beforeUpdateInsertAction() throws Exception {
		this.setMyPK(this.getWorkID() + "_" + this.getNodeID());
		return super.beforeUpdateInsertAction();
	}

}
