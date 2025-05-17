package bp.wf.template;

import bp.en.*; import bp.en.Map;

/** 
 表单节点排除规则
*/
public class FrmNodeFieldRemove extends EntityMyPK
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
		return uac;
	}
	/** 
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(FrmNodeFieldRemoveAttr.FlowNo);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(FrmNodeFieldRemoveAttr.FlowNo, value);
	}
	public final String getFrmID()  {
		return this.GetValStringByKey(FrmNodeFieldRemoveAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(FrmNodeFieldRemoveAttr.FrmID, value);
	}
	public final String getFields()  {
		return this.GetValStringByKey(FrmNodeFieldRemoveAttr.Fields);
	}
	public final void setFields(String value){
		this.SetValByKey(FrmNodeFieldRemoveAttr.Fields, value);
	}
	public final int getNodeID()  {
		return this.GetValIntByKey(FrmNodeFieldRemoveAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(FrmNodeFieldRemoveAttr.NodeID, value);
	}

		///#endregion 基本属性


		///#region 构造方法
	/** 
	 表单节点排除规则
	*/
	public FrmNodeFieldRemove()
	{
	}
	/** 
	 表单节点排除规则
	 
	 @param mypk
	*/
	public FrmNodeFieldRemove(String mypk) throws Exception
	{
		super(mypk);
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

		Map map = new Map("WF_FrmNodeFieldRemove", "表单节点排除规则");

		map.AddMyPK(true);
		map.AddTBString(FrmNodeFieldRemoveAttr.FrmID, null, "表单ID", true, true, 1, 200, 200);
		map.AddTBInt(FrmNodeFieldRemoveAttr.NodeID, 0, "节点编号", true, true);
		map.AddTBString(FrmNodeFieldRemoveAttr.FlowNo, null, "流程编号", true, true, 1, 10, 20);

		map.AddTBString(FrmNodeFieldRemoveAttr.Fields, null, "字段", false, false, 0, 50, 10, false);
		map.AddTBString(FrmNodeFieldRemoveAttr.ExpType, null, "表达式类型", false, false, 0, 50, 10, false);
		map.SetHelperAlert(FrmNodeFieldRemoveAttr.ExpType, "类型: Stas,Emps,Depts,SQL");
		map.AddTBString(FrmNodeFieldRemoveAttr.Exp, null, "表达式", false, false, 0, 500, 10, false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}
