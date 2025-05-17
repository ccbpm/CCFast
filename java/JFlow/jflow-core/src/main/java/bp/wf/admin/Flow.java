package bp.wf.admin;

import bp.en.Map;
import bp.en.*;
import bp.web.*;
import bp.sys.*;
import bp.wf.template.*;

/** 
 流程
*/
public class Flow extends EntityNoName
{

		///#region 构造方法
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (WebUser.getIsAdmin() == false)
		{
			throw new RuntimeException("err@管理员登录用户信息丢失,当前会话[" + WebUser.getNo() + "," + WebUser.getName() + "]");
		}
		uac.IsUpdate = true;
		uac.IsDelete = true;
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 流程
	*/
	public Flow()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
			return this.get_enMap();

		Map map = new Map("WF_Flow", "流程模版");


			///#region 基本属性。
		//处理流程类别.
		String sql = "";
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			map.AddDDLEntities("FK_FlowSort", null, "类别", new bp.wf.template.FlowSorts(), true);
		}
		else
		{
			sql = "SELECT No,Name FROM WF_FlowSort WHERE OrgNo='@WebUser.OrgNo' ORDER BY No,Idx";
			map.AddDDLSQL(FlowAttr.FK_FlowSort, null, "类别", sql, true);
			map.AddTBString(FlowAttr.OrgNo, null, "组织编号", false, false, 0, 50, 10, false);
			map.AddHidden(FlowAttr.OrgNo, " = ", WebUser.getOrgNo());
		}

		map.AddTBStringPK(FlowAttr.No, null, "编号", true, true, 1, 4, 3);
		map.SetHelperUrl(FlowAttr.No, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661868&doc_id=31094"); //使用alert的方式显示帮助信息.
		map.AddTBString(FlowAttr.Name, null, "名称", true, false, 0, 50, 300);

		//add  2013-08-30.
		map.AddTBString(FlowAttr.BillNoFormat, null, "单号格式", true, false, 0, 50, 10, false);
		map.SetHelperUrl(FlowAttr.BillNoFormat, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3953012&doc_id=31094");


		map.AddTBString(FlowAttr.FlowEventEntity, null, "事件实体", true, true, 0, 150, 30);
		map.SetHelperUrl(FlowAttr.FlowEventEntity, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661871&doc_id=31094");

		map.AddTBString(FlowAttr.PTable, null, "存储表", true, false, 0, 30, 10);
		map.SetHelperUrl(FlowAttr.PTable, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=4000827&doc_id=31094");

		// add 2013-02-05.
		map.AddTBString(FlowAttr.TitleRole, null, "标题生成规则", true, false, 0, 150, 10, true);
		map.SetHelperUrl(FlowAttr.TitleRole, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661872&doc_id=31094");

		// map.AddBoolean(FlowAttr.IsCanStart, true, "独立启动？", true, true);

		map.AddDDLSysEnum(FlowAttr.IsCanStart, 1, "发布状态", true, false, "IsCanStart", "@0=不启用@1=独立启动");

		//map.AddBoolean(FlowAttr.IsCanStart, true, "可以独立启动否？(独立启动的流程可以显示在发起流程列表里)", true, true, true);
		//map.SetHelperUrl(FlowAttr.IsCanStart, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661874&doc_id=31094");

		// // 草稿
		map.AddDDLSysEnum(FlowAttr.Draft, 0, "草稿规则", true, true, FlowAttr.Draft, "@0=无(不设草稿)@1=保存到待办@2=保存到草稿箱");
		map.SetHelperUrl(FlowAttr.Draft, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661878&doc_id=31094");

			///#endregion 基本属性。

		map.AddTBString(FlowAttr.OrgNo, null, "组织编号", false, false, 0, 50, 10, false);

		map.AddTBString("Creater", "admin", "创建人", true, false, 0, 150, 10, true);
		map.AddTBDateTime(FlowAttr.CreateDate, null, "创建日期", true, false);


		//查询.
		map.AddSearchAttr(FlowAttr.FK_FlowSort, 130);
		map.AddSearchAttr(FlowAttr.IsCanStart, 130);


			///#region 流程模版管理.
		RefMethod rm = new RefMethod();
		rm.Title = "流程模版";
		rm.Icon = "../../WF/Img/undo.png";
		rm.ClassMethodName = this.toString() + ".DoExps()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-paper-plane";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Icon = "../../WF/Img/Btn/DTS.gif";
		rm.Title = "重生成报表数据"; // "删除数据";
		rm.Warning = "您确定要执行吗? 注意:此方法耗费资源。"; // "您确定要执行删除流程数据吗？";
		rm.ClassMethodName = this.toString() + ".DoReloadRptData";
		rm.GroupName = "流程维护";
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "重生成流程标题";
		rm.Icon = "../../WF/Img/Btn/DTS.gif";
		rm.ClassMethodName = this.toString() + ".DoGenerTitle()";
		//设置相关字段.
		//rm.RefAttrKey = FlowAttr.TitleRole;
		rm.RefAttrLinkLabel = "重新生成流程标题";
		rm.refMethodType = RefMethodType.Func;
		rm.Target = "_blank";
		rm.Warning = "您确定要根据新的规则重新产生标题吗？";
		rm.GroupName = "流程维护";
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "重生成FlowEmps字段";
		rm.Icon = "../../WF/Img/Btn/DTS.gif";
		rm.ClassMethodName = this.toString() + ".DoGenerFlowEmps()";
		rm.RefAttrLinkLabel = "补充修复emps字段，包括wf_generworkflow,NDxxxRpt字段.";
		rm.refMethodType = RefMethodType.Func;
		rm.Target = "_blank";
		rm.Warning = "您确定要重新生成吗？";
		rm.GroupName = "流程维护";
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
		rm.GroupName = "流程维护";
		rm.Title = "删除指定日期范围内的流程";
		rm.Warning = "您确定要删除吗？";
		rm.Icon = "../../WF/Img/Btn/Delete.gif";
		rm.getHisAttrs().AddTBDateTime("DTFrom", null, "时间从", true, false);
		rm.getHisAttrs().AddTBDateTime("DTTo", null, "时间到", true, false);
		rm.getHisAttrs().AddBoolen("thisFlowOnly", true, "仅仅当前流程");
		rm.Icon = "icon-briefcase";
		rm.ClassMethodName = this.toString() + ".DoDelFlows";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Icon = "../../WF/Img/Btn/Delete.gif";
		rm.Title = "按工作ID删除"; // this.ToE("DelFlowData", "删除数据"); // "删除数据";
		rm.GroupName = "流程维护";
		rm.ClassMethodName = this.toString() + ".DoDelDataOne";
		rm.getHisAttrs().AddTBInt("WorkID", 0, "输入工作ID", true, false);
		rm.getHisAttrs().AddTBString("beizhu", null, "删除备注", true, false, 0, 100, 100);
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
		rm.GroupName = "流程维护";
		rm.Title = "强制设置接收人";
		rm.getHisAttrs().AddTBInt("WorkID", 0, "输入工作ID", true, false);
		rm.getHisAttrs().AddTBInt("NodeID", 0, "节点ID", true, false);
		rm.getHisAttrs().AddTBString("Worker", null, "接受人编号", true, false, 0, 100, 100);
		rm.Icon = "icon-briefcase";
		rm.ClassMethodName = this.toString() + ".DoSetTodoEmps";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "按工作ID强制结束"; // this.ToE("DelFlowData", "删除数据"); // "删除数据";
		rm.GroupName = "流程维护";
		rm.ClassMethodName = this.toString() + ".DoStopFlow";
		rm.getHisAttrs().AddTBInt("WorkID", 0, "输入工作ID", true, false);
		rm.Icon = "icon-briefcase";
		rm.getHisAttrs().AddTBString("beizhu", null, "备注", true, false, 0, 100, 100);
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "回滚流程";
		rm.Icon = "../../WF/Img/Btn/Back.png";
		rm.ClassMethodName = this.toString() + ".DoRebackFlowData()";
		// rm.Warning = "您确定要回滚它吗？";
		rm.getHisAttrs().AddTBInt("workid", 0, "请输入要回滚的WorkID", true, false);
		rm.getHisAttrs().AddTBInt("nodeid", 0, "回滚到的节点ID", true, false);
		rm.getHisAttrs().AddTBString("note", null, "回滚原因", true, false, 0, 600, 200);
		rm.Icon = "icon-briefcase";
		rm.GroupName = "流程维护";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Icon = "../../WF/Img/Btn/DTS.gif";
		rm.Title = "删除模板"; // "删除数据";
		rm.ItIsCanBatch= true;
		rm.ClassMethodName = this.toString() + ".DeleteIt";
		rm.GroupName = "流程维护";
		map.AddRefMethod(rm);


			///#endregion 流程运行维护.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
	public final String DeleteIt()
	{
		try
		{
			bp.wf.Flow fl = new bp.wf.Flow(this.getNo());
			fl.DoDelete();
			return "删除成功...";
		}
		catch (Exception ex)
		{
			return "err@" + ex.getMessage();
		}
	}

	public final String DoExps() {
		return "../../Admin/AttrFlow/Exp.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}

}
