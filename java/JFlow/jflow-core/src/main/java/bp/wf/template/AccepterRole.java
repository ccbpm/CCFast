package bp.wf.template;

import bp.en.*; import bp.en.Map;


/** 
 这里存放每个接受人规则的信息.	 
*/
public class AccepterRole extends EntityOID
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsUpdate = true;
		return uac;
	}
	/** 
	 节点编号
	*/
	public final String getFKNode()  {
		return this.GetValStringByKey(AccepterRoleAttr.FK_Node);
	}
	public final void setFKNode(String value) throws Exception
	{
		SetValByKey(AccepterRoleAttr.FK_Node, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 接受人规则
	*/
	public AccepterRole()
	{
	}
	/** 
	 接受人规则
	 
	 @param oid 接受人规则ID	
	*/
	public AccepterRole(int oid) throws Exception
	{
		this.setOID(oid);
		this.Retrieve();
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

		Map map = new Map("WF_AccepterRole", "接受人规则");

		map.AddTBIntPKOID();

		map.AddTBString(AccepterRoleAttr.Name, null, null, true, false, 0, 200, 10, true);
		map.AddTBString(AccepterRoleAttr.FK_Node, null, "节点", false, true, 0, 100, 10);
		map.AddTBInt(AccepterRoleAttr.FK_Mode, 0, "模式类型", false, true);

		map.AddTBString(AccepterRoleAttr.Tag0, null, "Tag0", false, true, 0, 999, 10);
		map.AddTBString(AccepterRoleAttr.Tag1, null, "Tag1", false, true, 0, 999, 10);
		map.AddTBString(AccepterRoleAttr.Tag2, null, "Tag2", false, true, 0, 999, 10);
		map.AddTBString(AccepterRoleAttr.Tag3, null, "Tag3", false, true, 0, 999, 10);
		map.AddTBString(AccepterRoleAttr.Tag4, null, "Tag4", false, true, 0, 999, 10);
		map.AddTBString(AccepterRoleAttr.Tag5, null, "Tag5", false, true, 0, 999, 10);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

}
