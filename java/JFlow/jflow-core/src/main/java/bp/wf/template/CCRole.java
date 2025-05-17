package bp.wf.template;

import bp.en.*; import bp.en.Map;
import bp.wf.*;

/**
 抄送
*/
public class CCRole extends EntityMyPK
{

		///#region 属性
	/**
	 获得抄送人

	 @param rpt
	 @return
	*/

	/**
	节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(NodeAttr.NodeID);
	}
	public final void setNodeID(int value){
		this.SetValByKey(NodeAttr.NodeID, value);
	}
	/**
	 执行类型
	*/
	public final CCRoleExcType getCCRoleExcType() {
		return CCRoleExcType.forValue(this.GetValIntByKey(CCRoleAttr.CCRoleExcType));
	}
	public final void setCCRoleExcType(CCRoleExcType value){
		this.SetValByKey(CCRoleAttr.CCRoleExcType, value);
	}
	public final CCStaWay getCCStaWay() {
		return CCStaWay.forValue(this.GetValIntByKey(CCRoleAttr.CCStaWay));
	}

	/**
	 多个元素的分割.
	*/
	public final String getEnIDs() throws Exception {
		String str = this.GetValStringByKey(CCRoleAttr.EnIDs);
		str = str.replace(",","','");
		str = "'" + str + "'";
		str = str.replace("''","'");
		str = str.replace("''", "'");
		return str;
	}

	public final String getEnDeptIDs() throws Exception {
		String str = this.GetValStringByKey(CCRoleAttr.Tag2);
		str = str.replace(",","','");
		str = "'" + str + "'";
		str = str.replace("''","'");
		str = str.replace("''", "'");
		return str;
	}
	/**
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();

		if (bp.web.WebUser.getIsAdmin() == false)
		{
			uac.IsView = false;
			return uac;
		}
		uac.IsDelete = false;
		uac.IsInsert = false;
		uac.IsUpdate = true;
		return uac;
	}

		///#endregion


		///#region 构造函数
	/**
	 抄送设置
	*/
	public CCRole()
	{
	}
	/**
	 抄送设置

	 @param nodeid
	*/
	public CCRole(int nodeid) throws Exception {
		this.setNodeID(nodeid);
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

		Map map = new Map("WF_CCRole", "抄送规则");

		map.AddMyPK(true);
		map.AddTBInt(CCRoleAttr.NodeID, 0, "节点", false, true);
		map.AddTBString(CCRoleAttr.FlowNo, null, "流程编号", false, false, 0, 10, 50, true);
		// 执行类型.
		String val = "@0=按表单字段计算@1=按人员计算@2=按角色计算@3=按部门计算@4=按SQL计算@5=按接受人规则计算";
		map.AddDDLSysEnum(CCRoleAttr.CCRoleExcType, 0, "执行类型", true, true, CCRoleAttr.CCRoleExcType, val);
		map.AddTBInt(CCRoleAttr.CCStaWay, 0, "CCStaWay", false, true);

		map.AddTBStringDoc(CCRoleAttr.EnIDs, null, "执行内容1", true, false, true, 10);
		map.AddTBStringDoc(CCRoleAttr.Tag2, null, "执行内容2", true, false, true, 10);

		map.AddTBInt(CCRoleAttr.Idx, 0, "Idx", false, true);


		map.AddTBString("EnIDsT", null, "执行内容1", false, false, 0, 10, 500);
		map.AddTBString("Tag2T", null, "执行内容1", false, false, 0, 10, 500);

		map.AddTBAtParas(300);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
