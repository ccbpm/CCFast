package bp.wf.template.sflow;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 手工启动子流程.
*/
public class SubFlowHandGuide extends EntityMyPK
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
		return this.GetValStringByKey(SubFlowHandAttr.SubFlowNo);
	}
	public final void setSubFlowNo(String value) throws Exception
	{
		SetValByKey(SubFlowHandAttr.SubFlowNo, value);
	}
	/** 
	 流程名称
	*/
	public final String getSubFlowName()  {
		return this.GetValStringByKey(SubFlowHandAttr.SubFlowName);
	}
	public final String getFKNode()  {
		return this.GetValStringByKey(SubFlowHandAttr.FK_Node);
	}
	public final void setFKNode(String value) throws Exception
	{
		SetValByKey(SubFlowHandAttr.FK_Node, value);
	}

	public final String getSubFlowGuideEnNoFiled()  {
		return this.GetValStringByKey(SubFlowHandGuideAttr.SubFlowGuideEnNoFiled);
	}

	public final String getSubFlowGuideEnNameFiled()  {
		return this.GetValStringByKey(SubFlowHandGuideAttr.SubFlowGuideEnNameFiled);
	}

	public final boolean getItIsTreeConstruct()  {
		return this.GetValBooleanByKey(SubFlowHandGuideAttr.IsTreeConstruct);
	}
	public final String getParentNo()  {
		return this.GetValStringByKey(SubFlowHandGuideAttr.ParentNo);
	}

	public final boolean getSubFlowHidTodolist()  {
		return this.GetValBooleanByKey(SubFlowHandGuideAttr.SubFlowHidTodolist);
	}


		///#endregion


		///#region 构造函数
	/** 
	 手工启动子流程
	*/
	public SubFlowHandGuide()
	{
	}
	public SubFlowHandGuide(String mypk) throws Exception
	{
		this.setMyPK(mypk);
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

		Map map = new Map("WF_NodeSubFlow", "启动子流程前置导航");

		map.AddMyPK(true);

		map.AddTBString(SubFlowAttr.FK_Flow, null, "主流程编号", true, true, 0, 5, 100);
		map.AddTBInt(SubFlowHandAttr.FK_Node, 0, "节点", false, true);

		map.AddTBString(SubFlowYanXuAttr.SubFlowNo, null, "子流程编号", false, true, 0, 10, 150, false);
		map.AddTBString(SubFlowYanXuAttr.SubFlowName, null, "子流程名称", false, true, 0, 200, 150, false);

		map.AddBoolean(SubFlowHandGuideAttr.IsSubFlowGuide, false, "是否启用子流程批量发起前置导航", true, true, true);
		map.AddTBString(SubFlowHandGuideAttr.SubFlowGuideSQL, null, "子流程前置导航配置SQL", true, false, 0, 200, 150, true);
		String msg = "发起前置导航的实体列表SQL, 必须包含No,Name两个列,与流程发起前置导航相同.";
		msg += "\t\n比如：SELECT No,Name FROM Port_Emp ";
		msg += "\t\nSQL配置支持ccbpm表达式.";
		map.SetHelperAlert(SubFlowHandGuideAttr.SubFlowGuideSQL, msg);


		map.AddTBString(SubFlowHandGuideAttr.SubFlowGuideGroup, null, "分组的SQL", true, false, 0, 200, 150, true);

		map.AddTBString(SubFlowHandGuideAttr.SubFlowGuideEnNoFiled, null, "实体No字段", true, false, 0, 40, 150);
		map.AddTBString(SubFlowHandGuideAttr.SubFlowGuideEnNameFiled, null, "实体Name字段", true, false, 0, 40, 150);

		//@0=单条手工启动, 1=按照简单数据源批量启动. @2=分组数据源批量启动. @3=树形结构批量启动.
		map.AddTBInt(SubFlowHandAttr.SubFlowStartModel, 0, "启动模式", false, false);

		//@0=表格模式, 1=列表模式.
		map.AddTBInt(SubFlowHandAttr.SubFlowShowModel, 0, "展现模式", false, false);
		//  map.Add(SubFlowHandAttr.IsTreeConstruct, 0, "是否是树结构", false, true);

		//批量发送后，是否隐藏父流程的待办. 
		map.AddBoolean(SubFlowAttr.SubFlowHidTodolist, false, "发送后是否隐藏父流程待办",false,false);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeUpdateInsertAction() throws Exception
	{
		//if (this.IsTreeConstruct== true
		//    && DataType.IsNullOrEmpty(this.ParentNo) == true)
		//{
		//    throw new Exception("请配置父节点的编号");
		//}
		return super.beforeUpdateInsertAction();
	}


}
