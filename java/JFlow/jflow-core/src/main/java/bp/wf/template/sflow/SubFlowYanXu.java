package bp.wf.template.sflow;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 延续子流程.
*/
public class SubFlowYanXu extends EntityMyPK
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 主流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(SubFlowAutoAttr.FK_Flow);
	}
	public final void setFlowNo(String value) throws Exception
	{
		SetValByKey(SubFlowAutoAttr.FK_Flow, value);
	}
	/** 
	 流程编号
	*/
	public final String getSubFlowNo()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.SubFlowNo);
	}
	public final void setSubFlowNo(String value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.SubFlowNo, value);
	}
	/** 
	 流程名称
	*/
	public final String getSubFlowName()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.SubFlowName);
	}
	/** 
	 条件表达式.
	*/
	public final String getCondExp()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.CondExp);
	}
	public final void setCondExp(String value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.CondExp, value);
	}
	/** 
	 表达式类型
	*/
	public final ConnDataFrom getExpType() {
		return ConnDataFrom.forValue(this.GetValIntByKey(SubFlowYanXuAttr.ExpType));
	}
	public final void setExpType(ConnDataFrom value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.ExpType, value.getValue());
	}
	public final String getNodeID()  {
		return this.GetValStringByKey(SubFlowYanXuAttr.FK_Node);
	}
	public final void setNodeID(String value) throws Exception
	{
		SetValByKey(SubFlowYanXuAttr.FK_Node, value);
	}
	/** 
	 运行类型
	*/
	public final SubFlowModel getHisSubFlowModel() {
		return SubFlowModel.forValue(this.GetValIntByKey(SubFlowAutoAttr.SubFlowModel));
	}
	/** 
	 延续到的节点@lizhen.
	*/
	public final String getYanXuToNode() throws Exception {
		String str = this.GetValStringByKey(SubFlowAutoAttr.YanXuToNode);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			str = this.getSubFlowNo() + "01";
		}
		return str;
	}

		///#endregion


		///#region 构造函数
	/** 
	 延续子流程
	*/
	public SubFlowYanXu()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_NodeSubFlow", "延续子流程");

		map.AddMyPK(true);

		map.AddTBString(SubFlowAttr.FK_Flow, null, "主流程编号", true, true, 0, 5, 100);

		map.AddTBInt(SubFlowYanXuAttr.FK_Node, 0, "节点", false, true);
		map.AddDDLSysEnum(SubFlowYanXuAttr.SubFlowType, 2, "子流程类型", true, false, SubFlowYanXuAttr.SubFlowType, "@0=手动启动子流程@1=触发启动子流程@2=延续子流程");

		map.AddDDLSysEnum(SubFlowYanXuAttr.SubFlowModel, 0, "子流程模式", true, true, SubFlowYanXuAttr.SubFlowModel, "@0=下级子流程@1=同级子流程");

		map.AddDDLSysEnum(SubFlowYanXuAttr.SubFlowSta, 1, "状态", true, true, SubFlowYanXuAttr.SubFlowSta, "@0=禁用@1=启用@2=只读");

		map.AddTBString(SubFlowYanXuAttr.SubFlowNo, null, "子流程编号", true, true, 0, 10, 150, false);
		map.AddTBString(SubFlowYanXuAttr.SubFlowName, null, "子流程名称", true, true, 0, 200, 150, false);


		map.AddDDLSysEnum(SubFlowYanXuAttr.ExpType, 3, "表达式类型", true, true, SubFlowYanXuAttr.ExpType, "@3=按照SQL计算@4=按照参数计算");

		map.AddTBString(SubFlowYanXuAttr.CondExp, null, "条件表达式", true, false, 0, 500, 150, true);

		map.AddTBString(SubFlowYanXuAttr.YanXuToNode, null, "延续到的节点", true, false, 0, 100, 150);
		String msg = "允许延续到多个节点上,多个节点用逗号分开 ";
		msg += "\t\n 比如: 903,904,906";
		msg += "\t\n 如果为空则标识延续到第一个节点上去.";
		map.SetHelperAlert(SubFlowYanXuAttr.YanXuToNode, msg);


		// map.AddDDLSQL(SubFlowYanXuAttr.YanXuToNode, null, "延续到的节点",
		//   "SELECT NodeID AS No, Name FROM WF_Node WHERE RunModel IN(0,2,3) AND FK_Flow='@SubFlowNo'", true);

		//@du.
		map.AddDDLSysEnum(SubFlowYanXuAttr.YBFlowReturnRole, 0, "退回方式", true, true, SubFlowYanXuAttr.YBFlowReturnRole, "@0=不能退回@1=退回到父流程的开始节点@2=退回到父流程的任何节点@3=退回父流程的启动节点@4=可退回到指定的节点");

		// map.AddTBString(SubFlowYanXuAttr.ReturnToNode, null, "要退回的节点", true, false, 0, 200, 150, true);
		map.AddDDLSQL(SubFlowYanXuAttr.ReturnToNode, "0", "要退回的节点", "SELECT NodeID AS No, Name FROM WF_Node WHERE FK_Flow IN (SELECT FK_Flow FROM WF_Node WHERE NodeID=@FK_Node) ", true);

		map.AddTBInt(SubFlowYanXuAttr.Idx, 0, "顺序", true, false);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 设置主键
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setMyPK(this.getNodeID() + "_" + this.getSubFlowNo() + "_2");
		return super.beforeInsert();
	}


		///#region 移动.
	/** 
	 上移
	 
	 @return 
	*/
	public final String DoUp() throws Exception
	{
		this.DoOrderUp(SubFlowYanXuAttr.FK_Node, this.getNodeID(), SubFlowYanXuAttr.SubFlowType, "2", SubFlowYanXuAttr.Idx);
		return "执行成功";
	}
	/** 
	 下移
	 
	 @return 
	*/
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(SubFlowYanXuAttr.FK_Node, this.getNodeID(), SubFlowYanXuAttr.SubFlowType, "2", SubFlowYanXuAttr.Idx);
		return "执行成功";
	}

		///#endregion 移动.

}
