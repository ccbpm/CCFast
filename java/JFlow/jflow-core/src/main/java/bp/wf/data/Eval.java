package bp.wf.data;

import bp.en.*;
import bp.en.Map;


/** 
 工作质量评价
*/
public class Eval extends EntityMyPK
{

		///#region 基本属性
	/** 
	 流程标题
	*/
	public final String getTitle()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.Title);
	}
	public final void setTitle(String value){
		this.SetValByKey(EvalAttr.Title, value);
	}
	/** 
	 工作ID
	*/
	public final long getWorkID()  throws Exception
	{
		return this.GetValInt64ByKey(EvalAttr.WorkID);
	}
	public final void setWorkID(long value){
		this.SetValByKey(EvalAttr.WorkID, value);
	}
	/** 
	 节点编号
	*/
	public final int getNodeID()  throws Exception
	{
		return this.GetValIntByKey(EvalAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(EvalAttr.FK_Node, value);
	}
	/** 
	 节点名称
	*/
	public final String getNodeName()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.NodeName);
	}
	public final void setNodeName(String value){
		this.SetValByKey(EvalAttr.NodeName, value);
	}
	/** 
	 被评估人员名称
	*/
	public final String getEvalEmpName()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.EvalEmpName);
	}
	public final void setEvalEmpName(String value){
		this.SetValByKey(EvalAttr.EvalEmpName, value);
	}
	/** 
	 记录日期
	*/
	public final String getRDT()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.RDT);
	}
	public final void setRDT(String value){
		this.SetValByKey(EvalAttr.RDT, value);
	}
	/** 
	 流程隶属部门
	*/
	public final String getDeptNo()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.FK_Dept);
	}
	public final void setDeptNo(String value){
		this.SetValByKey(EvalAttr.FK_Dept, value);
	}
	/** 
	 部门名称
	*/
	public final String getDeptName()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.DeptName);
	}
	public final void setDeptName(String value){
		this.SetValByKey(EvalAttr.DeptName, value);
	}
	/** 
	 隶属年月
	*/
	public final String getNY()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.FK_NY);
	}
	public final void setNY(String value){
		this.SetValByKey(EvalAttr.FK_NY, value);
	}
	/** 
	 流程编号
	*/
	public final String getFlowNo()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.FK_Flow);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(EvalAttr.FK_Flow, value);
	}
	/** 
	 流程名称
	*/
	public final String getFlowName()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.FlowName);
	}
	public final void setFlowName(String value){
		this.SetValByKey(EvalAttr.FlowName, value);
	}
	/** 
	 评价人
	*/
	public final String getRec()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.Rec);
	}
	public final void setRec(String value){
		this.SetValByKey(EvalAttr.Rec, value);
	}
	/** 
	 评价人名称
	*/
	public final String getRecName()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.RecName);
	}
	public final void setRecName(String value){
		this.SetValByKey(EvalAttr.RecName, value);
	}
	/** 
	 评价内容
	*/
	public final String getEvalNote()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.EvalNote);
	}
	public final void setEvalNote(String value){
		this.SetValByKey(EvalAttr.EvalNote, value);
	}
	/** 
	 被考核的人员编号
	*/
	public final String getEvalEmpNo()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.EvalEmpNo);
	}
	public final void setEvalEmpNo(String value){
		this.SetValByKey(EvalAttr.EvalEmpNo, value);
	}
	/** 
	 评价分值
	*/
	public final String getEvalCent()  throws Exception
	{
		return this.GetValStringByKey(EvalAttr.EvalCent);
	}
	public final void setEvalCent(String value){
		this.SetValByKey(EvalAttr.EvalCent, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 工作质量评价
	*/
	public Eval()
	{
	}
	/** 
	 工作质量评价
	 
	 param workid
	 param FK_Node
	*/
	public Eval(int workid, int FK_Node) throws Exception {
		this.setWorkID(workid);
		this.setNodeID(FK_Node);
		this.Retrieve();
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
		Map map = new Map("WF_CHEval", "工作质量评价");


		map.AddMyPK(true);
		map.AddTBString(EvalAttr.Title, null, "标题", false, true, 0, 500, 10);
		map.AddTBString(EvalAttr.FK_Flow, null, "流程编号", false, true, 0, 4, 10);
		map.AddTBString(EvalAttr.FlowName, null, "流程名称", false, true, 0, 100, 10);

		map.AddTBInt(EvalAttr.WorkID, 0, "工作ID", false, true);
		map.AddTBInt(EvalAttr.FK_Node, 0, "评价节点", false, true);
		map.AddTBString(EvalAttr.NodeName, null, "停留节点", false, true, 0, 100, 10);

		map.AddTBString(EvalAttr.Rec, null, "评价人", false, true, 0, 50, 10);
		map.AddTBString(EvalAttr.RecName, null, "评价人名称", false, true, 0, 50, 10);

		map.AddTBDateTime(EvalAttr.RDT, "评价日期", true, true);

		map.AddTBString(EvalAttr.EvalEmpNo, null, "被考核的人员编号", false, true, 0, 50, 10);
		map.AddTBString(EvalAttr.EvalEmpName, null, "被考核的人员名称", false, true, 0, 50, 10);
		map.AddTBString(EvalAttr.EvalCent, null, "评价分值", false, true, 0, 20, 10);
		map.AddTBString(EvalAttr.EvalNote, null, "评价内容", false, true, 0, 20, 10);

		map.AddTBString(EvalAttr.FK_Dept, null, "部门", false, true, 0, 50, 10);
		map.AddTBString(EvalAttr.DeptName, null, "部门名称", false, true, 0, 100, 10);
		map.AddTBString(EvalAttr.FK_NY, null, "年月", false, true, 0, 7, 10);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}