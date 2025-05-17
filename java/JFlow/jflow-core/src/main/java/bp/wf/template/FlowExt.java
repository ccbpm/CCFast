package bp.wf.template;

import bp.da.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.tools.DateUtils;
import bp.web.*;
import bp.sys.*;
import bp.wf.GERpt;
import bp.wf.Glo;
import bp.wf.data.*;
import bp.wf.template.frm.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;
import java.util.*;
import java.time.*;

/** 
 流程
*/
public class FlowExt extends EntityNoName
{

		///#region 属性.
	/** 
	 流程类别
	*/
	public final String getFlowSortNo()  {
		return this.GetValStringByKey(FlowAttr.FK_FlowSort);
	}
	public final void setFlowSortNo(String value){
		this.SetValByKey(FlowAttr.FK_FlowSort, value);
	}
	/** 
	 系统类别（第2级流程树节点编号）
	*/
	public final String getSysType()  {
		return this.GetValStringByKey(FlowAttr.SysType);
	}
	public final void setSysType(String value){
		this.SetValByKey(FlowAttr.SysType, value);
	}

	/** 
	 是否可以独立启动
	*/
	public final boolean getItIsCanStart()  {
		return this.GetValBooleanByKey(FlowAttr.IsCanStart);
	}
	public final void setItIsCanStart(boolean value){
		this.SetValByKey(FlowAttr.IsCanStart, value);
	}
	/** 
	 流程事件实体
	*/
	public final String getFlowEventEntity()  {
		return this.GetValStringByKey(FlowAttr.FlowEventEntity);
	}
	public final void setFlowEventEntity(String value){
		this.SetValByKey(FlowAttr.FlowEventEntity, value);
	}
	public final String getPTable()  {
		return this.GetValStringByKey(FlowAttr.PTable);
	}
	public final void setPTable(String value){
		this.SetValByKey(FlowAttr.PTable, value);
	}
	/**
	 流程标记
	*/
	public final String getFlowMark() throws Exception {
		String str = this.GetValStringByKey(FlowAttr.FlowMark);
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return this.getNo();
		}
		return str;
	}
	public final void setFlowMark(String value){
		this.SetValByKey(FlowAttr.FlowMark, value);
	}
	public final boolean getItIsStartInMobile()  {
		return this.GetValBooleanByKey(FlowAttr.IsStartInMobile);
	}
	public final void setItIsStartInMobile(boolean value){
		this.SetValByKey(FlowAttr.IsStartInMobile, value);
	}

	/** 
	 测试人员
	*/
	public final String getTester()  {
		return this.GetValStringByKey(FlowAttr.Tester);
	}
	public final void setTester(String value){
		this.SetValByKey(FlowAttr.Tester, value);
	}

		///#endregion 属性.


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
		//uac.OpenForAdmin();  //zsy修改 2020.5.15
		//if (bp.web.WebUser.getNo().equals("admin")==true || this.DesignerNo == WebUser.getNo())
		uac.IsDelete = false;
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 流程
	*/
	public FlowExt()
	{
	}
	/** 
	 流程
	 
	 @param _No 编号
	*/
	public FlowExt(String _No) throws Exception {
		this.setNo(_No);
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

		Map map = new Map("WF_Flow", "流程模版");


			///#region 基本属性。
		map.AddGroupAttr("基本属性", "");
		map.AddTBStringPK(FlowAttr.No, null, "编号", true, true, 1, 4, 3);
		map.SetHelperUrl(FlowAttr.No, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661868&doc_id=31094"); //使用alert的方式显示帮助信息.
		// map.AddDDLEntities(FlowAttr.FK_FlowSort, null, "类别", new FlowSorts(), true);
		//处理流程类别.
		String sql = "";
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			//map.AddDDLEntities(FlowAttr.FK_FlowSort, null, "类别", new FlowSorts(), true);
			sql = "SELECT No,Name FROM WF_FlowSort WHERE Name!='流程树' ORDER BY NO, IDX";
			map.AddDDLSQL(FlowAttr.FK_FlowSort, null, "类别", sql, true);
		}
		else
		{
			sql = "SELECT No,Name FROM WF_FlowSort WHERE OrgNo='@WebUser.OrgNo' ORDER BY NO, IDX";
			map.AddDDLSQL(FlowAttr.FK_FlowSort, null, "类别", sql, true);
		}

		//map.AddDDLEntities(FlowAttr.FK_FlowSort, "01", "流程类别", new FlowSorts(), false);
		//map.SetHelperUrl(FlowAttr.FK_FlowSort, "http://ccbpm.mydoc.io/?v=5404&t=17024");

		map.AddTBString(FlowAttr.Name, null, "名称", true, false, 0, 50, 10, true);

		// add 2013-02-14 唯一确定此流程的标记
		map.AddTBString(FlowAttr.FlowMark, null, "流程标记", true, false, 0, 150, 10);
		map.AddTBString(FlowAttr.FlowEventEntity, null, "流程事件实体", true, true, 0, 150, 10);
		map.SetHelperUrl(FlowAttr.FlowMark, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661870&doc_id=31094");
		map.SetHelperUrl(FlowAttr.FlowEventEntity, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661871&doc_id=31094");

		// add 2013-02-05.
		map.AddTBString(FlowAttr.TitleRole, null, "标题生成规则", true, false, 0, 150, 10, true);
		map.SetHelperUrl(FlowAttr.TitleRole, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661872&doc_id=31094");

		map.AddTBString(FlowAttr.TitleRoleNodes, null, "生成标题的节点", true, false, 0, 300, 10);
		String msg = "设置帮助";
		//msg += "\r\n 1. 如果为空表示只在开始节点生成标题.";
		//msg += "\r\n 2. * 表示在任意节点可生成标题.";
		//msg += "\r\n 3. 要在指定的节点重新生成标题用逗号分开,比如: 102,105,109";
		map.SetHelperUrl(FlowAttr.TitleRoleNodes, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661873&doc_id=31094");


		map.AddBoolean(FlowAttr.IsCanStart, true, "可以独立启动否？(独立启动的流程可以显示在发起流程列表里)", true, true, true);
		map.SetHelperUrl(FlowAttr.IsCanStart, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661874&doc_id=31094");

		map.AddBoolean(FlowAttr.IsFullSA, false, "是否自动计算未来的处理人？", true, true, true);
		map.SetHelperUrl(FlowAttr.IsFullSA, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661875&doc_id=31094");

		//map.AddDDLSysEnum(FlowAttr.IsAutoSendSubFlowOver, 0, "为子流程时结束规则", true, true,
		// FlowAttr.IsAutoSendSubFlowOver, "@0=不处理@1=让父流程自动运行下一步@2=结束父流程");

		//map.AddBoolean(FlowAttr.GuestFlowRole, false, "是否外部用户参与流程(非组织结构人员参与的流程)", true, true, false);
		map.AddDDLSysEnum(FlowAttr.GuestFlowRole, GuestFlowRole.None.getValue(), "外部用户参与流程规则", true, true, "GuestFlowRole", "@0=不参与@1=开始节点参与@2=中间节点参与");
		map.SetHelperUrl(FlowAttr.GuestFlowRole, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661876&doc_id=31094");

		// map.AddDDLSysEnum(FlowAttr.FlowAppType, 0, "流程应用类型", true, true, "FlowAppType", "@0=业务流程@1=工程类(项目组流程)@2=公文流程(VSTO)");
		//map.SetHelperUrl(FlowAttr.FlowAppType, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661877&doc_id=31094");

		//map.AddDDLSysEnum(FlowAttr.SDTOfFlow, (int)TimelineRole.ByNodeSet, "时效性规则",
		// true, true, FlowAttr.SDTOfFlow, "@0=按节点(由节点属性来定义)@1=按发起人(开始节点SysSDTOfFlow字段计算)");
		//map.SetHelperUrl(FlowAttr.TimelineRole, "http://ccbpm.mydoc.io/?v=5404&t=17036");

		// 草稿
		map.AddDDLSysEnum(FlowAttr.Draft, DraftRole.None.getValue(), "草稿规则", true, true, FlowAttr.Draft, "@0=无(不设草稿)@1=保存到待办@2=保存到草稿箱");
		map.SetHelperUrl(FlowAttr.Draft, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661878&doc_id=31094");

		//为 莲荷科技增加一个系统类型, 用于存储当前所在流程树的第2级流程树编号.
		map.AddTBString(FlowAttr.SysType, null, "系统类型", false, false, 0, 50, 10, false);
		map.AddTBString(FlowAttr.Tester, null, "发起测试人", true, false, 0, 100, 10, true);
		map.SetHelperUrl(FlowAttr.Tester, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661882&doc_id=31094");

		// add 2014-10-19.
		map.AddDDLSysEnum(FlowAttr.ChartType, FlowChartType.Icon.getValue(), "节点图形类型", true, true, "ChartType", "@0=几何图形@1=肖像图片");
		map.SetHelperUrl(FlowAttr.ChartType, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661883&doc_id=31094");
		map.AddTBInt(FlowAttr.Idx, 0, "显示顺序号(在发起列表中)", true, false);
		// map.AddTBString(FlowAttr.HostRun, null, "运行主机(IP+端口)", true, false, 0, 40, 10, true);
		map.AddTBString("Icon", "icon-doc", "图标", true, false, 0, 100, 100, true);
		map.AddTBDateTime(FlowAttr.CreateDate, null, "创建日期", true, true);
		map.AddTBString(FlowAttr.Creater, null, "创建人", true, true, 0, 100, 10, true);

			///#endregion 基本属性。


			///#region 表单数据.
		map.AddGroupAttr("数据&表单", "");
		//批量发起 add 2013-12-27. 
		map.AddBoolean(FlowAttr.IsBatchStart, false, "是否可以批量发起流程？(如果是就要设置发起的需要填写的字段,多个用逗号分开)", false, true, true);
		map.AddTBString(FlowAttr.BatchStartFields, null, "发起字段s", true, false, 0, 100, 10, true);
		map.SetHelperUrl(FlowAttr.IsBatchStart, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3952886&doc_id=31094");

		//移动到这里 by zhoupeng 2016.04.08.
		map.AddBoolean(FlowAttr.IsResetData, false, "是否启用开始节点数据重置按钮？已经取消)", false, true, true);
		map.AddBoolean(FlowAttr.IsLoadPriData, false, "是否自动装载上一笔数据？", true, true, true);
		map.AddBoolean(FlowAttr.IsDBTemplate, true, "是否启用数据模版？", true, true, true);
		map.AddBoolean(FlowAttr.IsStartInMobile, true, "是否可以在手机里启用？(如果发起表单特别复杂就不要在手机里启用了)", true, true, true);
		map.SetHelperAlert(FlowAttr.IsStartInMobile, "用于控制手机端流程发起列表.");

		map.AddBoolean(FlowAttr.IsMD5, false, "是否是数据防止篡改(MD5数据加密防篡改)", true, true, true);
		map.SetHelperUrl(FlowAttr.IsMD5, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3952971&doc_id=31094");

		map.AddBoolean(FlowAttr.IsJM, false, "是否是数据加密流程(把所有字段加密存储)", true, true, true);
		map.SetHelperUrl(FlowAttr.IsJM, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3952997&doc_id=31094");

		map.AddBoolean(FlowAttr.IsEnableDBVer, false, "是否是启用数据版本控制", true, true, true);


		map.AddTBString(FlowAttr.PTable, null, "流程数据存储表", true, false, 0, 100, 10);
		map.SetHelperUrl(FlowAttr.PTable, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=4000827&doc_id=31094");


		//add  2013-08-30.
		map.AddTBString(FlowAttr.BillNoFormat, null, "单据编号格式", true, false, 0, 50, 10, false);
		map.SetHelperUrl(FlowAttr.BillNoFormat, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3953012&doc_id=31094");

		// add 2019-09-25 by zhoupeng
		map.AddTBString(FlowAttr.BuessFields, null, "关键业务字段", true, false, 0, 100, 10, false);
		msg = "用于显示在待办上的业务字段信息.";
		msg += "\t\n 1. 用户在看到待办的时候，就可以看到流程的实例的关键信息。";
		msg += "\t\n 2. 用于待办的列表信息显示.";
		msg += "\t\n 3. 配置格式为. Tel,Addr,Email  这些字段区分大小写并且是节点表单字段.";
		msg += "\t\n 4. 数据存储在WF_GenerWorkFlow.AtPara里面.";
		msg += "\t\n 5. 存储格式为: @BuessFields = 电话^Tel^18992323232;地址^Addr^山东杭州;";
		map.SetHelperAlert(FlowAttr.BuessFields, msg);

		map.AddTBString(FlowAttr.AdvEmps, null, "高级查询人员", true, false, 0, 100, 10, true);
		//msg = "用于指定人员查询流程实例数据集合的设置.";
		//msg += "\t\n 存储格式：人员编号以逗号分割如zhangsan,lisi,";
		map.SetHelperUrl(FlowAttr.AdvEmps, "https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=6513111&doc_id=31094");

			///#endregion 表单数据.


			///#region 轨迹信息
		map.AddGroupAttr("轨迹", "");
		//map.AddBoolean(FlowAttr.IsFrmEnable, false, "是否显示表单", true, true, false);
		map.AddBoolean(FlowAttr.IsTruckEnable, true, "是否显示轨迹图", true, true, false);
		map.AddBoolean(FlowAttr.IsTimeBaseEnable, true, "是否显示时间轴", true, true, false);
		map.AddBoolean(FlowAttr.IsTableEnable, true, "是否显示时间表", true, true, false);
		//map.AddBoolean(FlowAttr.IsOPEnable, false, "是否显示操作", true, true, false);
		map.AddDDLSysEnum(FlowAttr.SubFlowShowType, 0, "子流程轨迹图显示模式", true, true, FlowAttr.SubFlowShowType, "@0=平铺模式显示@1=合并模式显示");
		map.AddDDLSysEnum(FlowAttr.TrackOrderBy, 0, "排序方式", true, true, FlowAttr.TrackOrderBy, "@0=按照时间先后顺序@1=倒序(新发生的在前面)");

			///#endregion 轨迹信息


			///#region 基本功能.
		map.AddGroupMethod("基本功能");
		RefMethod rm = new RefMethod();
		rm = new RefMethod();
		rm.Title = "自动发起";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/AutoStart.png";
		rm.ClassMethodName = this.toString() + ".DoSetStartFlowDataSources()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-plane";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "发起限制规则";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Limit.png";
		rm.ClassMethodName = this.toString() + ".DoLimit()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-ban";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "发起前置导航";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/StartGuide.png";
		rm.ClassMethodName = this.toString() + ".DoStartGuideV2019()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-list";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "流程事件"; // "调用事件接口";
		rm.ClassMethodName = this.toString() + ".DoAction";
		rm.Icon = "../../WF/Img/Event.png";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-energy";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "流程消息"; // "调用事件接口";
		rm.ClassMethodName = this.toString() + ".DoMessage";
		rm.Icon = "../../WF/Img/Message24.png";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-bubbles";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "轨迹查看权限";
		rm.Icon = "../../WF/Img/Setting.png";
		rm.ClassMethodName = this.toString() + ".DoTruckRight()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "修改ICON"; // "调用事件接口";
		//rm.ClassMethodName = this.ToString() + ".DoNodesICON";
		////  rm.Icon = "../../WF/Img/Event.png";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Icon = "icon-heart";
		//map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "版本管理";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Node.png";
		rm.ClassMethodName = this.toString() + ".DoVer()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-doc";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "权限控制";
		// rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Node.png";
		rm.ClassMethodName = this.toString() + ".DoPowerModel()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		// rm.GroupName = "实验中的功能";
		rm.Icon = "icon-settings";
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "批量发起";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/AutoStart.png";
		//rm.ClassMethodName = this.ToString() + ".DoBatchStart()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Icon = "icon-calculator";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "流程模版";
		rm.Icon = "../../WF/Img/undo.png";
		rm.ClassMethodName = this.toString() + ".DoExps()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-paper-plane";
		map.AddRefMethod(rm);

			///#endregion 流程设置.


			///#region 时限规则
		map.AddGroupMethod("时限规则");
		rm = new RefMethod();
		rm.Title = "计划时间计算规则";
		rm.ClassMethodName = this.toString() + ".DoSDTOfFlow";
		//rm.Icon = "../../WF/Img/Event.png";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-clock";
		map.AddRefMethod(rm);

		/*rm = new RefMethod();
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/CH.png";
		rm.ClassMethodName = this.ToString() + ".DoDeadLineRole()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		// rm.GroupName = "实验中的功能";
		rm.Icon = "icon-clock";
		map.AddRefMethod(rm);*/

		rm = new RefMethod();
		rm.Title = "预警、超期消息事件";
		rm.Icon = "../../WF/Admin/CCFormDesigner/Img/OvertimeRole.png";
		rm.ClassMethodName = this.toString() + ".DoOverDeadLineRole()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-energy";
		map.AddRefMethod(rm);

			///#endregion 时限规则


			///#region 开发接口.
		map.AddGroupMethod("开发接口");
		rm = new RefMethod();
		rm.Title = "与业务表数据同步";
		rm.ClassMethodName = this.toString() + ".DoDTSBTable()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-paper-plane";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "URL调用接口";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/URL.png";
		rm.ClassMethodName = this.toString() + ".DoAPI()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-paper-plane";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "SDK开发接口";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/API.png";
		rm.ClassMethodName = this.toString() + ".DoAPICode()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-paper-plane";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "代码事件开发接口";
		rm.ClassMethodName = this.toString() + ".DoAPICodeFEE()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-paper-plane";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "流程属性自定义";
		rm.ClassMethodName = this.toString() + ".DoFlowAttrExt()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-paper-plane";
		map.AddRefMethod(rm);

			///#endregion 开发接口.


			///#region 流程运行维护.
		map.AddGroupMethod("流程维护");
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
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
		rm.Title = "重命名节点表单字段";
		//  rm.Warning = "您确定要处理吗？";
		rm.getHisAttrs().AddTBString("FieldOld", null, "旧字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNew", null, "新字段英文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddTBString("FieldNewName", null, "新字段中文名", true, false, 0, 100, 100);
		rm.getHisAttrs().AddBoolen("thisFlowOnly", true, "仅仅当前流程");
		rm.Icon = "icon-briefcase";
		rm.ClassMethodName = this.toString() + ".DoChangeFieldName";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		rm.Title = "节点表单字段视图";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Field.png";
		rm.ClassMethodName = this.toString() + ".DoFlowFields()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
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
		rm.ClassMethodName = this.toString() + ".DoDelDataOne";
		rm.getHisAttrs().AddTBInt("WorkID", 0, "输入工作ID", true, false);
		rm.getHisAttrs().AddTBString("beizhu", null, "删除备注", true, false, 0, 100, 100);
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		//带有参数的方法.
		rm = new RefMethod();
		rm.Title = "强制设置接收人";
		rm.getHisAttrs().AddTBInt("WorkID", 0, "输入工作ID", true, false);
		rm.getHisAttrs().AddTBInt("NodeID", 0, "节点ID", true, false);
		rm.getHisAttrs().AddTBString("Worker", null, "接受人编号", true, false, 0, 100, 100);
		rm.Icon = "icon-briefcase";
		rm.ClassMethodName = this.toString() + ".DoSetTodoEmps";
		map.AddRefMethod(rm);


		rm = new RefMethod();
		//   rm.Icon = "../../WF/Img/Btn/Delete.gif";
		rm.Title = "按工作ID强制结束"; // this.ToE("DelFlowData", "删除数据"); // "删除数据";
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
		map.AddRefMethod(rm);

			///#endregion 流程运行维护.


			///#region 流程监控.
		map.AddGroupMethod("流程监控");
		rm = new RefMethod();
		rm.Title = "设计报表"; // "报表运行";
		rm.Icon = "../../WF/Img/Btn/Rpt.gif";
		rm.ClassMethodName = this.toString() + ".DoOpenRpt()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "监控面板";
		//rm.Icon = ../../Admin/CCBPMDesigner/Img/Monitor.png";
		//rm.ClassMethodName = this.ToString() + ".DoDataManger_Welcome()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.GroupName = "流程监控";
		//map.AddRefMethod(rm);

		//  rm = new RefMethod();
		//rm.Title = "图形分析";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Group.png";
		//rm.ClassMethodName = this.ToString() + ".DoDataManger_DataCharts()";
		//rm.refMethodType = RefMethodType.LinkeWinOpen;
		//rm.GroupName = "流程监控";
		//rm.Icon = "icon-briefcase";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "综合查询";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Search.png";
		rm.ClassMethodName = this.toString() + ".DoDataManger_Search()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "综合分析";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Group.png";
		rm.ClassMethodName = this.toString() + ".DoDataManger_Group()";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);


		//rm = new RefMethod();
		//rm.Title = "实例增长分析";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Grow.png";
		//rm.ClassMethodName = this.ToString() + ".DoDataManger_InstanceGrowOneFlow()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.GroupName = "流程监控";
		//rm.Visable = false;
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "逾期未完成实例";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Warning.png";
		//rm.ClassMethodName = this.ToString() + ".DoDataManger_InstanceWarning()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.GroupName = "流程监控";
		//map.AddRefMethod(rm);

		//rm = new RefMethod();
		//rm.Title = "逾期已完成实例";
		//rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/overtime.png";
		//rm.ClassMethodName = this.ToString() + ".DoDataManger_InstanceOverTimeOneFlow()";
		//rm.refMethodType = RefMethodType.RightFrameOpen;
		//rm.Visable = false;
		//rm.GroupName = "流程监控";
		//map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "删除日志";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/log.png";
		rm.ClassMethodName = this.toString() + ".DoDataManger_DeleteLog()";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

			///#endregion 流程监控.


			///#region 实验中的功能
		map.AddGroupMethod("实验中的功能");
		rm = new RefMethod();
		rm.Title = "数据源管理(如果新增数据源后需要关闭重新打开)";
		rm.ClassMethodName = this.toString() + ".DoDBSrc";
		rm.Icon = "../../WF/Img/Btn/DTS.gif";
		//设置相关字段.
		rm.RefAttrKey = FlowAttr.DTSDBSrc;
		rm.RefAttrLinkLabel = "数据源管理";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "业务表字段同步配置";
		rm.ClassMethodName = this.toString() + ".DoBTable";
		rm.Icon = Glo.getCCFlowAppPath() + "WF/Img/Btn/DTS.gif";
		//设置相关字段.
		rm.RefAttrKey = FlowAttr.DTSField;
		rm.RefAttrLinkLabel = "业务表字段同步配置";
		rm.refMethodType = RefMethodType.LinkeWinOpen;
		rm.Target = "_blank";
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "一键设置审核组件工作模式";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Node.png";
		rm.refMethodType = RefMethodType.Func;
		rm.Warning = "您确定要设置审核组件模式吗？ \t\n 1, 第2个节点以后的节点表单都指向第2个节点表单.  \t\n  2, 结束节点都设置为只读模式. ";
		rm.ClassMethodName = this.toString() + ".DoSetFWCModel()";
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);

		rm = new RefMethod();
		rm.Title = "一键设置施工-监理模式";
		rm.Icon = "../../WF/Admin/CCBPMDesigner/Img/Node.png";
		rm.refMethodType = RefMethodType.Func;
		rm.ClassMethodName = this.toString() + ".DoSDGaoSu()";
		rm.Icon = "icon-briefcase";
		map.AddRefMethod(rm);


			///#endregion 实验中的功能

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 设置施工监理审核模式.
	 
	 @return 
	*/
	public final String DoSDGaoSu() throws Exception {
		Nodes nds = new Nodes();
		nds.Retrieve("FK_Flow", this.getNo(), null);


		for (Node nd : nds.ToJavaList())
		{
			if (nd.getItIsStartNode() == true)
			{
				continue; //开始节点不处理.
			}

			//如果第2个节点.
			if (String.valueOf(nd.getNodeID()).endsWith("02") == true)
			{
				nd.setHisDeliveryWay((DeliveryWay.BySQL));
				//   nd.getDeliveryParas() = "";
			}
			else
			{
				//接受人模式与指定的节点相同。
				nd.setHisDeliveryWay((DeliveryWay.BySpecNodeEmp));
				nd.setDeliveryParas(String.valueOf(Integer.parseInt(this.getNo() + "02")));
			}

			//设置协作模式.
			nd.setTodolistModel(TodolistModel.Teamup);
			nd.setItIsExpSender(false); //是否排除当前人员.
			nd.Update();
		}

		return "执行成功";
	}


		///#region 流程监控.

	public final String DoDataManger_DataCharts() {
		return "../../Admin/AttrFlow/DataCharts.htm?FK_Flow=" + this.getNo();
	}


	public final String DoDataManger_Search() {
		return "../../Comm/Search.htm?EnsName=BP.WF.Data.GenerWorkFlowViews&FK_Flow=" + this.getNo() + "&WFSta=all";
	}
	public final String DoDataManger_Group() {
		return "../../Comm/Group.htm?EnsName=BP.WF.Data.GenerWorkFlowViews&FK_Flow=" + this.getNo() + "&WFSta=all";
	}


	public final String DoDataManger_DeleteLog() {
		return "../../Comm/Search.htm?EnsName=BP.WF.WorkFlowDeleteLogs&FK_Flow=" + this.getNo() + "&WFSta=all";
	}



		///#endregion 流程监控.


		///#region 开发接口.
	/** 
	 执行删除指定日期范围内的流程
	 
	 @param dtFrom 日期从
	 @param dtTo 日期到
	 @param isDelCurrFlow 仅仅删除当前流程？1=删除当前流程, 0=删除全部流程.
	 @return 
	*/
	public final String DoDelFlows(String dtFrom, String dtTo, boolean isDelCurrFlow) throws Exception {
		//if (bp.web.WebUser.getNo() != "admin")
		if (WebUser.getIsAdmin() == false)
		{
			return "非管理员用户，不能删除。";
		}


		String sql = "";
		if (isDelCurrFlow == true)
		{
			sql = "SELECT WorkID, FK_Flow FROM WF_GenerWorkFlow  WHERE RDT >= '" + dtFrom + "' AND RDT <= '" + dtTo + "' AND FK_Node IN (SELECT NodeID From WF_Node WHERE FK_Flow='" + this.getNo() + "') ";
		}
		else
		{
			sql = "SELECT WorkID, FK_Flow FROM WF_GenerWorkFlow  WHERE RDT >= '" + dtFrom + "' AND RDT <= '" + dtTo + "' ";
		}

		DataTable dt = DBAccess.RunSQLReturnTable(sql);

		String msg = "如下流程ID被删除:";
		for (DataRow dr : dt.Rows)
		{
			long workid = Long.parseLong(dr.getValue("WorkID").toString());
			String fk_flow = dr.getValue("FK_Flow").toString();
			DoDelFlowByWorkID(workid, fk_flow);
			//删除指定日期范围内的流程并删除消息
			String delSms = "DELETE FROM Sys_SMS WHERE WorkID=" + workid;
			DBAccess.RunSQL(delSms);

			msg += " " + workid;
		}

		sql = "SELECT WorkID, FK_Flow FROM WF_GenerWorkerlist  WHERE RDT >= '" + dtFrom + "' AND RDT <= '" + dtTo + "' AND FK_Node IN (SELECT NodeID From WF_Node WHERE FK_Flow='" + this.getNo() + "')";

		dt = DBAccess.RunSQLReturnTable(sql);

		for (DataRow dr : dt.Rows)
		{
			long workid = Long.parseLong(dr.getValue("WorkID").toString());
			String fk_flow = dr.getValue("FK_Flow").toString();
			DoDelFlowByWorkID(workid, fk_flow);
			msg += " " + workid;
		}
		return msg;
	}
	public final void DoDelFlowByWorkID(long workid, String fk_flow) throws Exception {
		if (DataType.IsNullOrEmpty(fk_flow))
		{
			fk_flow = this.getNo();
		}
			///#region 删除独立表单数据
		FrmNodes fns = new FrmNodes();
		fns.Retrieve(FrmNodeAttr.FK_Flow, fk_flow, null);
		String strs = "";
		for (FrmNode frmNode : fns.ToJavaList())
		{
			if (strs.contains("@" + frmNode.getFrmID()) == true)
			{
				continue;
			}
			strs += "@" + frmNode.getFrmID() + "@";
			try
			{
				MapData md = new MapData(frmNode.getFrmID());
				DBAccess.RunSQL("DELETE FROM " + md.getPTable() + " WHERE OID=" + workid);
			}
			catch (java.lang.Exception e)
			{

			}
		}

			///#endregion 删除独立表单数据

		//流程运行信息
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkerlist Where (WorkID=" + workid + " OR FID=" + workid + ")");
		//考核信息
		DBAccess.RunSQL("DELETE FROM WF_CH Where (WorkID=" + workid + " OR FID=" + workid + ")");
		DBAccess.RunSQL("DELETE FROM WF_CHEval Where WorkID=" + workid);

		//接收人信息删除
		DBAccess.RunSQL("DELETE FROM WF_SelectAccper Where WorkID=" + workid);
		//流程信息流转自定义
		DBAccess.RunSQL("DELETE FROM WF_TransferCustom Where WorkID=" + workid);

		//流程remberme的信息
		DBAccess.RunSQL("DELETE FROM WF_RememberMe Where FK_Node IN(SELECT NodeID From WF_Node WHERE FK_Flow='" + fk_flow + "')");
		//删除流程的抄送信息
		DBAccess.RunSQL("DELETE FROM WF_CCList WHERE WorkID=" + workid);

		//删除流程实例的信息
		DBAccess.RunSQL("DELETE FROM WF_GenerWorkFlow Where (WorkID=" + workid + " OR FID=" + workid + ")");

		//删除track信息
		if (DBAccess.IsExitsObject("ND" + Integer.parseInt(fk_flow) + "Track") == true)
		{
			DBAccess.RunSQL("DELETE FROM ND" + Integer.parseInt(fk_flow) + "Track Where (WorkID=" + workid + " OR FID=" + workid + ")");
		}

		//删除表单信息
		if (DataType.IsNullOrEmpty(this.getPTable()) == false && DBAccess.IsExitsObject(this.getPTable()) == true)
		{
			DBAccess.RunSQL("DELETE FROM " + this.getPTable() + " Where OID=" + workid);
		}
		//删除节点信息
		Nodes nds = new Nodes(fk_flow);
		for (Node nd : nds.ToJavaList())
		{
			try
			{
				Work wk = nd.getHisWork();
				DBAccess.RunSQL("DELETE FROM " + wk.getEnMap().getPhysicsTable() + " Where OID=" + workid);
			}
			catch (java.lang.Exception e2)
			{

			}
			MapDtls dtls = new MapDtls("ND" + nd.getNodeID());
			for (MapDtl dtl : dtls.ToJavaList())
			{
				try
				{
					DBAccess.RunSQL("DELETE FROM " + dtl.getPTable() + " WHERE RefPK=" + workid);
				}
				catch (java.lang.Exception e3)
				{

				}

			}
		}

	}
	/** 
	 批量重命名字段.
	 
	 @param fieldOld
	 @param fieldNew
	 @param FieldNewName
	 @return 
	*/
	public final String DoChangeFieldName(String fieldOld, String fieldNew, String FieldNewName, String thisFlowOnly) throws Exception {

		if (Objects.equals(thisFlowOnly, "1"))
		{
			return DoChangeFieldNameOne(this, fieldOld, fieldNew, FieldNewName);
		}

		FlowExts fls = new FlowExts();
		fls.RetrieveAll();

		String resu = "";
		for (FlowExt item : fls.ToJavaList())
		{
			resu += "   ====   " + DoChangeFieldNameOne(item, fieldOld, fieldNew, FieldNewName);

		}
		return resu;
	}
	public final String DoChangeFieldNameOne(FlowExt flow, String fieldOld, String fieldNew, String FieldNewName) throws Exception {
		String result = "开始执行对字段:" + fieldOld + " ，进行重命名。";
		result += "<br> ===============================================================   ";
		Nodes nds = new Nodes(flow.getNo());
		for (Node nd : nds.ToJavaList())
		{
			result += " @ 执行节点:" + nd.getName() + " 结果如下. <br>";
			result += "<br> ------------------------------------------------------------------------ ";
			MapDataExt md = new MapDataExt("ND" + nd.getNodeID());
			result += "\t\n @" + md.DoChangeFieldName(fieldOld, fieldNew, FieldNewName);
		}

		result += "@ 执行Rpt结果如下. <br>";
		MapDataExt rptMD = new MapDataExt("ND" + Integer.parseInt(flow.getNo()) + "Rpt");
		result += "\t\n@ " + rptMD.DoChangeFieldName(fieldOld, fieldNew, FieldNewName);

		result += "@ 执行MyRpt结果如下. <br>";
		rptMD = new MapDataExt("ND" + Integer.parseInt(flow.getNo()) + "MyRpt");

		if (rptMD.Retrieve() > 0)
		{
			result += "\t\n@ " + rptMD.DoChangeFieldName(fieldOld, fieldNew, FieldNewName);
		}

		return result;
	}
	/** 
	 字段视图
	 
	 @return 
	*/
	public final String DoFlowFields() {
		return "../../Admin/AttrFlow/FlowFields.htm?FK_Flow=" + this.getNo();
	}
	/** 
	 与业务表数据同步
	 
	 @return 
	*/
	public final String DoDTSBTable() {
		return "../../Admin/AttrFlow/DTSBTable.htm?FK_Flow=" + this.getNo();
	}
	public final String DoAPI() {
		return "../../Admin/AttrFlow/API.htm?FK_Flow=" + this.getNo();
	}
	public final String DoAPICode() {
		return "../../Admin/AttrFlow/APICode.htm?FK_Flow=" + this.getNo();
	}
	public final String DoAPICodeFEE() {
		return "../../Admin/AttrFlow/APICodeFEE.htm?FK_Flow=" + this.getNo();
	}
	/** 
	 流程属性自定义
	 
	 @return 
	*/
	public final String DoFlowAttrExt() {
		return "../../../DataUser/OverrideFiles/FlowAttrExts.html?FK_Flow=" + this.getNo();
	}
	public final String DoVer() {
		return "../../Admin/AttrFlow/Ver.htm?FK_Flow=" + this.getNo();
	}
	public final String DoPowerModel() {
		return "../../Admin/AttrFlow/PowerModel.htm?FK_Flow=" + this.getNo();
	}

	/** 
	 时限规则
	 
	 @return 
	*/
	public final String DoDeadLineRole() {
		return "../../Admin/AttrFlow/DeadLineRole.htm?FK_Flow=" + this.getNo();
	}

	/** 
	 预警、超期规则
	 
	 @return 
	*/
	public final String DoOverDeadLineRole() {
		return "../../Admin/AttrFlow/PushMessage.htm?FK_Flow=" + this.getNo();
	}



		///#endregion 开发接口


		///#region  基本功能
	/** 
	 事件
	 
	 @return 
	*/
	public final String DoAction() {
		return "../../Admin/AttrFlow/Action.htm?FK_Flow=" + this.getNo() + "&tk=" + (new Random()).nextDouble();
	}
	/** 
	 流程事件
	 
	 @return 
	*/
	public final String DoMessage() {
		return "../../Admin/AttrFlow/PushMessage.htm?FK_Node=0&FK_Flow=" + this.getNo() + "&tk=" + (new Random()).nextDouble();
	}
	/** 
	 计划玩成
	 
	 @return 
	*/
	public final String DoSDTOfFlow() {
		return "../../Admin/AttrFlow/SDTOfFlow/Default.htm?FK_Flow=" + this.getNo() + "&tk=" + (new Random()).nextDouble();
	}
	/** 
	 节点标签
	 
	 @return 
	*/
	public final String DoNodesICON() {
		return "../../Admin/AttrFlow/NodesIcon.htm?FK_Flow=" + this.getNo() + "&tk=" + (new Random()).nextDouble();
	}
	public final String DoDBSrc()
	{
		return "../../Comm/Sys/SFDBSrcNewGuide.htm";
	}
	public final String DoBTable() {
		return "../../Admin/AttrFlow/DTSBTable.htm?s=d34&ShowType=FlowFrms&FK_Node=" + Integer.parseInt(this.getNo()) + "01&FK_Flow=" + this.getNo() + "&ExtType=StartFlow&RefNo=" + DataType.getCurrentDateTime();
	}
	/** 
	 批量发起
	 
	 @return 
	*/
	public final String DoBatchStart() {
		return "../../Admin/AttrFlow/BatchStart.htm?s=d34&ShowType=FlowFrms&FK_Node=" + Integer.parseInt(this.getNo()) + "01&FK_Flow=" + this.getNo() + "&ExtType=StartFlow&RefNo=" + DataType.getCurrentDateTime();
	}
	/** 
	 批量修改节点属性
	 
	 @return 
	*/
	public final String DoNodeAttrs() {
		return "../../Admin/AttrFlow/NodeAttrs.htm?NodeID=0&FK_Flow=" + this.getNo() + "&tk=" + (new Random()).nextDouble();
	}
	public final String DoBindFlowExt() {
		return "../../Admin/Sln/BindFrms.htm?s=d34&ShowType=FlowFrms&FK_Node=0&FK_Flow=" + this.getNo() + "&ExtType=StartFlow";
	}
	/** 
	 轨迹查看权限
	 
	 @return 
	*/
	public final String DoTruckRight() {
		return "../../Admin/AttrFlow/TruckViewPower.htm?FK_Flow=" + this.getNo();
	}
	/** 
	 批量发起字段
	 
	 @return 
	*/
	public final String DoBatchStartFields() {
		return "../../Admin/AttrNode/BatchStartFields.htm?s=d34&FK_Flow=" + this.getNo() + "&ExtType=StartFlow";
	}
	/** 
	 执行流程数据表与业务表数据手工同步
	 
	 @return 
	*/
	public final String DoBTableDTS() throws Exception {
		Flow fl = new Flow(this.getNo());
		return fl.DoBTableDTS();

	}
	/** 
	 恢复已完成的流程数据到指定的节点，如果节点为0就恢复到最后一个完成的节点上去.
	 
	 @param workid 要恢复的workid
	 @param backToNodeID 恢复到的节点编号，如果是0，标示回复到流程最后一个节点上去.
	 @param note
	 @return 
	*/
	public final String DoRebackFlowData(long workid, int backToNodeID, String note) throws Exception {
		if (note.length() <= 2)
		{
			return "请填写恢复已完成的流程原因.";
		}

		Flow fl = new Flow(this.getNo());
		GERpt rpt = new GERpt("ND" + Integer.parseInt(this.getNo()) + "Rpt");
		rpt.setOID(workid);
		int i = rpt.RetrieveFromDBSources();
		if (i == 0)
		{
			throw new RuntimeException("@错误，流程数据丢失。");
		}
		if (backToNodeID == 0)
		{
			backToNodeID = rpt.getFlowEndNode();
		}

		Emp empStarter = new Emp(rpt.getFlowStarter());

		// 最后一个节点.
		Node endN = new Node(backToNodeID);
		GenerWorkFlow gwf = null;
		boolean isHaveGener = false;
		try
		{

				///#region 创建流程引擎主表数据.
			gwf = new GenerWorkFlow();
			gwf.setWorkID(workid);
			if (gwf.RetrieveFromDBSources() == 1)
			{
				isHaveGener = true;
				//判断状态
				if (gwf.getIsOver()==false)
				{
					throw new RuntimeException("@当前工作ID为:" + workid + "的流程没有结束,不能采用此方法恢复。");
				}
			}

			gwf.setFlowNo(this.getNo());
			gwf.setFlowName(this.getName());
			gwf.setWorkID(workid);
			gwf.setPWorkID(rpt.getPWorkID());
			gwf.setPFlowNo(rpt.getPFlowNo());
			gwf.setPNodeID(rpt.getPNodeID());
			gwf.setPEmp(rpt.getPEmp());


			gwf.setNodeID(backToNodeID);
			gwf.setNodeName(endN.getName());

			gwf.setStarter(rpt.getFlowStarter());
			gwf.setStarterName(empStarter.getName());
			gwf.setFlowSortNo(fl.getFlowSortNo());
			gwf.setSysType(fl.getSysType());

			gwf.setTitle(rpt.getTitle());
			gwf.setWFState(WFState.ReturnSta); //设置为退回的状态
			gwf.setTaskSta(TaskSta.None); //*取消共享模式
			gwf.setDeptNo(rpt.getDeptNo());

			Dept dept = new Dept(rpt.getDeptNo());

			gwf.setDeptName(dept.getName());
			gwf.setPRI(1);

			Date date = DateUtils.addDay(new Date(), 3);
			String dttime = DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");

			gwf.setSDTOfNode(dttime);
			gwf.setSDTOfFlow(dttime);
			if (isHaveGener)
			{
				gwf.Update();
			}
			else
			{
				gwf.Insert(); //插入流程引擎数据.
			}


				///#endregion 创建流程引擎主表数据
			String ndTrack = "ND" + Integer.parseInt(this.getNo()) + "Track";
			String actionType = ActionType.Forward.getValue() + "," + ActionType.FlowOver.getValue() + "," + ActionType.ForwardFL.getValue() + "," + ActionType.ForwardHL.getValue() + "," + ActionType.Skip.getValue();
			String sql = "SELECT  * FROM " + ndTrack + " WHERE   ActionType IN (" + actionType + ")  and WorkID=" + workid + " ORDER BY RDT DESC, NDFrom ";
			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("@工作ID为:" + workid + "的数据不存在.");
			}

			String starter = "";
			boolean isMeetSpecNode = false;
			GenerWorkerList currWl = new GenerWorkerList();
			for (DataRow dr : dt.Rows)
			{
				int ndFrom = Integer.parseInt(dr.getValue("NDFrom").toString());
				Node nd = new Node(ndFrom);

				String ndFromT = dr.getValue("NDFromT").toString();
				String EmpFrom = dr.getValue(TrackAttr.EmpFrom).toString();
				String EmpFromT = dr.getValue(TrackAttr.EmpFromT).toString();

				// 增加上 工作人员的信息.
				GenerWorkerList gwl = new GenerWorkerList();
				gwl.setWorkID(workid);
				gwl.setFlowNo(this.getNo());

				gwl.setNodeID(ndFrom);
				gwl.setNodeName(ndFromT);

				if (gwl.getNodeID() == backToNodeID)
				{
					gwl.setItIsPass(false);
					currWl = gwl;
				}
				else
				{
					gwl.setItIsPass(true);
				}

				gwl.setEmpNo(EmpFrom);
				gwl.setEmpName(EmpFromT);
				if (gwl.getIsExits())
				{
					continue; //有可能是反复退回的情况.
				}

				Emp emp = new Emp(gwl.getEmpNo());
				gwl.setDeptNo(emp.getDeptNo());

				gwl.setSDT(dr.getValue("RDT").toString());
				gwl.setDTOfWarning(gwf.getSDTOfNode());

				gwl.setItIsEnable(true);
				gwl.setWhoExeIt(nd.getWhoExeIt());
				try{
					gwl.Insert();
				}catch(Exception e){
					gwl.Update();
				}

			}
			//更新流程表的状态.
			rpt.setFlowEnder(currWl.getEmpNo());
			rpt.setWFState(WFState.ReturnSta); //设置为退回的状态
			rpt.setFlowEndNode(currWl.getNodeID());
			rpt.Update();

			// 向接受人发送一条消息.
			Dev2Interface.Port_SendMsg(currWl.getEmpNo(), "工作恢复:" + gwf.getTitle(), "工作被:" + WebUser.getNo() + " 恢复." + note, "ReBack" + workid, SMSMsgType.SendSuccess, this.getNo(), Integer.parseInt(this.getNo() + "01"), workid, 0);

			//写入该日志.
			WorkNode wn = new WorkNode(workid, currWl.getNodeID());
			wn.AddToTrack(ActionType.RebackOverFlow, currWl.getEmpNo(), currWl.getEmpName(), currWl.getNodeID(), currWl.getNodeName(), note);

			return "@已经还原成功,现在的流程已经复原到(" + currWl.getNodeName() + "). @当前工作处理人为(" + currWl.getEmpNo() + " , " + currWl.getEmpName() + ")  @请通知他处理工作.";
		}
		catch (RuntimeException ex)
		{
			//此表的记录删除已取消
			//gwf.Delete();
			GenerWorkerList wl = new GenerWorkerList();
			wl.Delete(GenerWorkerListAttr.WorkID, workid);

			String sqls = "";
			sqls += "@UPDATE " + fl.getPTable() + " SET WFState=" + WFState.CompleteEnd.getValue() + " WHERE OID=" + workid;
			DBAccess.RunSQLs(sqls);
			return "<font color=red>回滚期间出现错误</font><hr>" + ex.getMessage();
		}
	}

	/** 
	 重新产生标题，根据新的规则.
	*/
	public final String DoGenerFlowEmps() throws Exception {
		if (WebUser.getIsAdmin() == false)
		{
			return "非admin用户不能执行。";
		}

		Flow fl = new Flow(this.getNo());

		GenerWorkFlows gwfs = new GenerWorkFlows();
		gwfs.Retrieve(GenerWorkFlowAttr.FK_Flow, this.getNo(), null);
		for (GenerWorkFlow gwf : gwfs.ToJavaList())
		{
			String emps = "@";
			String sql = "SELECT EmpFrom,EmpFromT FROM ND" + Integer.parseInt(this.getNo()) + "Track  WHERE WorkID=" + gwf.getWorkID();

			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			for (DataRow dr : dt.Rows)
			{
				if (emps.contains("@" + dr.getValue(0).toString() + "@") || emps.contains("@" + dr.getValue(0).toString() + "," + dr.getValue(1).toString() + "@"))
				{
					continue;
				}
				emps += dr.getValue(0).toString() + "," + dr.getValue(1).toString() + "@";
			}

			sql = "UPDATE " + fl.getPTable() + " SET FlowEmps='" + emps + "' WHERE OID=" + gwf.getWorkID();
			DBAccess.RunSQL(sql);

			sql = "UPDATE WF_GenerWorkFlow SET Emps='" + emps + "' WHERE WorkID=" + gwf.getWorkID();
			DBAccess.RunSQL(sql);
		}

		Node nd = fl.getHisStartNode();
		Works wks = nd.getHisWorks();
		wks.RetrieveAllFromDBSource(WorkAttr.Rec);
		String table = nd.getHisWork().getEnMap().getPhysicsTable();
		String tableRpt = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		bp.sys.MapData md = new bp.sys.MapData(tableRpt);
		for (Entity en : wks)
		{
			Work wk=(Work)en;
			GERpt rpt = new GERpt(tableRpt, wk.getOID());
			if (!Objects.equals(rpt.getFlowStarter(), WebUser.getNo()))
			{
				WebUser.Exit();
				try
				{
					Emp emp = new Emp(rpt.getFlowStarter());
					WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				}
				catch (java.lang.Exception e)
				{
					continue;
				}
			}
			String sql = "";
			String title = WorkFlowBuessRole.GenerTitle(fl, wk);
			Paras ps = new Paras();
			ps.Add("Title", title, false);
			ps.Add("OID", wk.getOID());
			ps.SQL = "UPDATE " + table + " SET Title=" + SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);

			ps.SQL = "UPDATE " + md.getPTable() + " SET Title=" + SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);

			ps.SQL = "UPDATE WF_GenerWorkFlow SET Title=" + SystemConfig.getAppCenterDBVarStr() + "Title WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);
		}
		Emp emp1 = new Emp("admin");
		WebUser.SignInOfGener(emp1, "CH", false, false, null, null);
		return "全部生成成功,影响数据(" + wks.size() + ")条";
	}

	/** 
	 重新产生标题，根据新的规则.
	*/
	public final String DoGenerTitle() throws Exception {
		if (WebUser.getIsAdmin() == false)
		{
			return "非管理员用户不能执行。";
		}
		String adminNo = WebUser.getNo();
		Flow fl = new Flow(this.getNo());
		Node nd = fl.getHisStartNode();
		Works wks = nd.getHisWorks();
		wks.RetrieveAllFromDBSource(WorkAttr.Rec);
		String table = nd.getHisWork().getEnMap().getPhysicsTable();
		String tableRpt = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		MapData md = new MapData(tableRpt);
		for (Work wk : wks.ToJavaList())
		{

			if (!Objects.equals(wk.getRec(), WebUser.getNo()))
			{
				WebUser.Exit();
				try
				{
					Emp emp = new Emp(wk.getRec());
					WebUser.SignInOfGener(emp, "CH", false, false, null, null);
				}
				catch (java.lang.Exception e)
				{
					continue;
				}
			}
			String title = WorkFlowBuessRole.GenerTitle(fl, wk);
			Paras ps = new Paras();
			ps.Add("Title", title, false);
			ps.Add("OID", wk.getOID());
			if (DBAccess.IsExitsTableCol(md.getPTable(), "Title") == true)
			{
				ps.SQL = "UPDATE " + md.getPTable() + " SET Title=" + SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + SystemConfig.getAppCenterDBVarStr() + "OID";
				DBAccess.RunSQL(ps);
			}

			if (table.equals(md.getPTable()) == false && DBAccess.IsExitsTableCol(table, "Title") == true)
			{
				ps.SQL = "UPDATE " + table + " SET Title=" + SystemConfig.getAppCenterDBVarStr() + "Title WHERE OID=" + SystemConfig.getAppCenterDBVarStr() + "OID";
				DBAccess.RunSQL(ps);
			}

			ps.SQL = "UPDATE WF_GenerWorkFlow SET Title=" + SystemConfig.getAppCenterDBVarStr() + "Title WHERE WorkID=" + SystemConfig.getAppCenterDBVarStr() + "OID";
			DBAccess.RunSQL(ps);

		}
		Emp emp1 = new Emp(adminNo);
		WebUser.SignInOfGener(emp1, "CH", false, false, null, null);

		return "全部生成成功,影响数据(" + wks.size() + ")条";
	}


	/** 
	 定义报表
	 
	 @return 
	*/
	public final String DoAutoStartIt() throws Exception {
		Flow fl = new Flow();
		fl.setNo(this.getNo());
		fl.RetrieveFromDBSources();
		return fl.DoAutoStartIt();
	}
	/** 
	 强制设置接受人
	 
	 @param workid 工作人员ID
	 @param nodeID 节点ID
	 @param worker 工作人员
	 @return 执行结果.
	*/
	public final String DoSetTodoEmps(int workid, int nodeID, String worker) throws Exception {
		GenerWorkFlow gwf = new GenerWorkFlow();
		gwf.setWorkID(workid);
		if (gwf.RetrieveFromDBSources() == 0)
		{
			return "workid=" + workid + "不正确.";
		}

		Emp emp = new Emp();
		emp.setUserID(worker);
		if (emp.RetrieveFromDBSources() == 0)
		{
			return "人员编号不正确" + worker + ".";
		}

		Node nd = new Node();
		nd.setNodeID(nodeID);
		if (nd.RetrieveFromDBSources() == 0)
		{
			return "err@节点编号[" + nodeID + "]不正确.";
		}

		gwf.setNodeID(nodeID);
		gwf.setNodeName(nd.getName());
		gwf.setTodoEmps(emp.getUserID() + "," + emp.getName() +";");
		gwf.setTodoEmpsNum(1);
		gwf.setHuiQianTaskSta(HuiQianTaskSta.None);
		gwf.Update();

		DBAccess.RunSQL("UPDATE WF_GenerWorkerlist SET IsPass=1 WHERE WorkID=" + workid);

		GenerWorkerList gwl = new GenerWorkerList();
		gwl.setNodeID(nodeID);
		gwl.setWorkID(workid);
		gwl.setEmpNo(emp.getUserID());
		if (gwl.RetrieveFromDBSources() == 0)
		{
			Date date = DateUtils.addDay(new Date(), 3);
			String dttime = DateUtils.format(date, "yyyy-MM-dd");
			gwl.setEmpName(emp.getName());

			if (nd.getHisCHWay() == CHWay.None)
			{
				gwl.setSDT("无");
			}
			else
			{
				gwl.setSDT(dttime);
			}

			gwl.setRDT(DateUtils.format(new Date(),"yyyy-MM-dd"));
			gwl.setItIsRead(false);
			gwl.Insert();
		}
		else
		{
			gwl.setItIsRead(false);
			gwl.setPassInt(0);
			gwl.Update();
		}
		return "执行成功.";
	}
	/** 
	 删除流程
	 
	 @param workid
	 @param note
	 @return 
	*/
	public final String DoDelDataOne(int workid, String note)
	{
		try
		{
			Dev2Interface.Flow_DoDeleteFlowByReal(workid, true);
			return "删除成功 workid=" + workid + "  理由:" + note;
		}
		catch (RuntimeException ex)
		{
			return "删除失败:" + ex.getMessage();
		}
	}
	public final String DoStopFlow(long workid, String note)
	{
		try
		{
			Dev2Interface.Flow_DoFlowOver(workid, note);
			return "流程被强制结束 workid=" + workid + "  理由:" + note;
		}
		catch (RuntimeException ex)
		{
			return "删除失败:" + ex.getMessage();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/** 
	 设置发起数据源
	 
	 @return 
	*/
	public final String DoSetStartFlowDataSources() throws Exception {
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			throw new RuntimeException("传入的流程编号为空，请检查流程");
		}
		String flowID = Integer.parseInt(this.getNo()) + "01";
		//return "../../Admin/AttrFlow/AutoStart.htm?s=d34&FK_Flow=" + this.getNo() + "&ExtType=StartFlow&RefNo=";
		return "../../Admin/AttrFlow/AutoStart/Default.htm?FK_Flow=" + this.getNo();
	}
	/** 
	 执行运行
	 
	 @return 
	*/
	public final String DoRunIt() {
		return "../../Admin/TestFlow.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}
	/** 
	 执行检查
	 
	 @return 
	*/
	public final String DoCheck() {
		return "../../Admin/AttrFlow/CheckFlow.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}
	public final String TestingContainer() {
		return "../../Admin/TestingContainer/TestFlow2020.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}
	public final String DoCheck2018Url() {
		return "../../Admin/Testing/FlowCheckError.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}
	/** 
	 启动限制规则
	 
	 @return 返回URL
	*/
	public final String DoLimit() {
		return "../../Admin/AttrFlow/Limit/Default.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}
	/** 
	 设置发起前置导航
	 
	 @return 
	*/
	public final String DoStartGuide() {
		return "../../Admin/AttrFlow/StartGuide.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}
	/** 
	 设置发起前置导航
	 
	 @return 
	*/
	public final String DoStartGuideV2019() {
		return "../../Admin/AttrFlow/StartGuide/Default.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}

	/** 
	 导入
	 
	 @return 
	*/
	public final String DoImp() {
		return "../../Admin/AttrFlow/Imp.htm?FK_Flow=" + this.getNo() + "&Lang=CH&FK_FlowSort=" + this.getFlowSortNo();
	}

	/** 
	 导出
	 
	 @return 
	*/
	public final String DoExps() {
		return "../../Admin/AttrFlow/Exp.htm?FK_Flow=" + this.getNo() + "&Lang=CH";
	}

	/** 
	 删除数据.
	 
	 @return 
	*/
	public final String DoDelData() throws Exception {
		Flow fl = new Flow();
		fl.setNo(this.getNo());
		fl.RetrieveFromDBSources();
		return fl.DoDelData();
	}
	/** 
	 运行报表
	 
	 @return 
	*/
	public final String DoOpenRpt() throws Exception {
		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			throw new RuntimeException("传入的流程编号为空，请检查流程");
		}
		return "../../Admin/RptDfine/Default.htm?FK_Flow=" + this.getNo() + "&DoType=Edit&FK_MapData=ND" + Integer.parseInt(this.getNo()) + "Rpt";
	}
	/** 
	 更新之后的事情，也要更新缓存。
	*/
	@Override
	protected void afterUpdate() throws Exception
	{
		//写入日志.
		bp.sys.base.Glo.WriteUserLog("更新流程属性：" + this.getName() + " - " + this.getNo(), "通用操作");

		// Flow fl = new Flow();
		// fl.setNo(this.No;
		// fl.RetrieveFromDBSources();
		//fl.Update();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		//检查设计流程权限,集团模式下，不是自己创建的流程，不能设计流程.
		bp.wf.template.TemplateGlo.CheckPower(this.getNo());

		//更新流程版本
		Flow.UpdateVer(this.getNo());


			///#region 同步事件实体.
		try
		{
			String flowMark = this.getFlowMark();
			if (DataType.IsNullOrEmpty(flowMark) == true)
			{
				flowMark = this.getNo();
			}

			this.setFlowEventEntity(Glo.GetFlowEventEntityStringByFlowMark(flowMark));
		}
		catch (java.lang.Exception e)
		{
			this.setFlowEventEntity("");
		}

			///#endregion 同步事件实体.

		//更新缓存数据。
		Flow fl = new Flow(this.getNo());
		fl.RetrieveFromDBSources();


			///#region StartFlows的清缓存
		if (fl.getItIsStartInMobile() != this.getItIsStartInMobile()
				|| fl.getItIsCanStart() != this.getItIsCanStart()
				|| fl.getName().equals(this.getName()) == false
				|| fl.GetValByKey("Icon").equals(this.GetValByKey("Icon")) == false)
		{
			//清空WF_Emp 的StartFlows
			DBAccess.RunSQL("UPDATE  WF_Emp Set StartFlows =''");
		}

			///#endregion StartFlows的清缓存

		fl.Copy(this);

		//2019-09-25 -by zhoupeng 为周大福增加 关键业务字段.
		fl.setBuessFields(this.GetValStrByKey(FlowAttr.BuessFields));
		fl.DirectUpdate();


			///#region 检查数据完整性 - 同步业务表数据。
		// 检查业务是否存在.
		if (fl.getDTSWay() != DataDTSWay.None)
		{
			/*检查业务表填写的是否正确.*/
			String sql = "select count(*) as Num from  " + fl.getDTSBTable() + " where 1=2";
			try
			{
				DBAccess.RunSQLReturnValInt(sql, 0);
			}
			catch (RuntimeException e2)
			{
				throw new RuntimeException("@业务表配置无效，您配置业务数据表[" + fl.getDTSBTable() + "]在数据中不存在，请检查拼写错误如果是跨数据库请加上用户名比如: for sqlserver: HR.dbo.Emps, For oracle: HR.Emps");
			}

			sql = "select " + fl.getDTSBTablePK() + " from " + fl.getDTSBTable() + " where 1=2";
			try
			{
				DBAccess.RunSQLReturnValInt(sql, 0);
			}
			catch (RuntimeException e3)
			{
				throw new RuntimeException("@业务表配置无效，您配置业务数据表[" + fl.getDTSBTablePK() + "]的主键不存在。");
			}


			//检查节点配置是否符合要求.
			if (fl.getDTSTime() == FlowDTSTime.SpecNodeSend)
			{
				// 检查节点ID，是否符合格式.
				String nodes = fl.getDTSSpecNodes();
				nodes = nodes.replace("，", ",");
				this.SetValByKey(FlowAttr.DTSSpecNodes, nodes);

				if (DataType.IsNullOrEmpty(nodes) == true)
				{
					throw new RuntimeException("@业务数据同步数据配置错误，您设置了按照指定的节点配置，但是您没有设置节点,节点的设置格式如下：101,102,103");
				}

				String[] strs = nodes.split("[,]", -1);
				for (String str : strs)
				{
					if (DataType.IsNullOrEmpty(str) == true)
					{
						continue;
					}

					if (DataType.IsNumStr(str) == false)
					{
						throw new RuntimeException("@业务数据同步数据配置错误，您设置了按照指定的节点配置，但是节点格式错误[" + nodes + "]。正确的格式如下：101,102,103");
					}

					Node nd = new Node();
					nd.setNodeID(Integer.parseInt(str));
					if (nd.IsExits() == false)
					{
						throw new RuntimeException("@业务数据同步数据配置错误，您设置的节点格式错误，节点[" + str + "]不是有效的节点。");
					}

					nd.RetrieveFromDBSources();
					if (!Objects.equals(nd.getFlowNo(), this.getNo()))
					{
						throw new RuntimeException("@业务数据同步数据配置错误，您设置的节点[" + str + "]不再本流程内。");
					}
				}
			}

			//检查流程数据存储表是否正确
			if (DataType.IsNullOrEmpty(fl.getPTable()) == false)
			{
				/*检查流程数据存储表填写的是否正确.*/
				sql = "select count(*) as Num from  " + fl.getPTable() + " WHERE 1=2";
				try
				{
					DBAccess.RunSQLReturnValInt(sql, 0);
				}
				catch (RuntimeException e4)
				{
					throw new RuntimeException("@流程数据存储表配置无效，您配置流程数据存储表[" + fl.getPTable() + "]在数据中不存在，请检查拼写错误如果是跨数据库请加上用户名比如: for sqlserver: HR.dbo.Emps, For oracle: HR.Emps");
				}
			}
		}

			///#endregion 检查数据完整性. - 同步业务表数据。

		return super.beforeUpdate();
	}
	@Override
	protected void afterInsertUpdateAction() throws Exception
	{

			///#region 更新PTale 后的业务处理
		// 同步流程数据表.
		String ndxxRpt = "ND" + Integer.parseInt(this.getNo()) + "Rpt";
		Flow fl = new Flow(this.getNo());
		//判断流程是不是有对应的实体绑定的流程,不同步修改低代码导出系统导入时报错
		if (DBAccess.IsExitsObject("Frm_Method") == true)
		{
			String dbstr = SystemConfig.getAppCenterDBVarStr();
			String sql = "UPDATE Frm_Method SET Name=" + dbstr + "Name WHERE FlowNo=" + dbstr + "FlowNo AND MethodID=" + dbstr + "MethodID";
			Paras paras = new Paras();
			paras.SQL = sql;
			paras.Add("Name", this.getName(), false);
			paras.Add("FlowNo", this.getNo(), false);
			paras.Add("MethodID", this.getNo(), false);
			DBAccess.RunSQL(paras);
		}

		MapData md = new MapData(ndxxRpt);
		if (md.getPTable().equals(fl.getPTable()) == false)
		{
			md.setPTable(fl.getPTable());
			md.Update();

			Nodes nds = new Nodes();
			nds.Retrieve(NodeAttr.FK_Flow, this.getNo(), null);
			for (Node nd : nds.ToJavaList())
			{
			   String sql = "";
				if (nd.getItIsSubThread() == true)
				{
					sql = "UPDATE Sys_MapData SET PTable=No WHERE No='ND" + nd.getNodeID() + "'";
				}
				else
				{
					sql = "UPDATE Sys_MapData SET PTable='" + fl.getPTable() + "' WHERE No='ND" + nd.getNodeID() + "'";
				}

				DBAccess.RunSQL(sql);
			}
			fl.CheckRpt(); // 检查业务表.
		}

			///#endregion 更新PTale 后的业务处理



			///#region 为systype设置，当前所在节点的第2级别目录。
		if (DataType.IsNullOrEmpty(fl.getFlowSortNo()) == false){
			FlowSort fs = new FlowSort(fl.getFlowSortNo());
			if (DataType.IsNullOrEmpty(fs.getParentNo()) == true)
			{
				fs.setParentNo("0");
			}

			if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				if (fs.getParentNo().equals(WebUser.getOrgNo()) == true || fs.getParentNo().equals("100") == true)
				{
					this.setSysType(fl.getFlowSortNo());
				}
				else
				{
					FlowSort fsP = new FlowSort(fs.getParentNo());
					if (fs.getParentNo().equals(WebUser.getOrgNo()) == true)
					{
						this.setSysType(fsP.getNo());
					}
					else
					{
						FlowSort fsPP = new FlowSort(fsP.getParentNo());
						this.setSysType(fsPP.getNo());
					}
				}
			}
			else
			{
				if (fs.getParentNo().equals("99") || fs.getParentNo().equals("0"))
				{
					this.setSysType(fl.getFlowSortNo());
				}
				else
				{
					FlowSort fsP = new FlowSort(fs.getParentNo());
					fsP.setNo(fs.getParentNo());

					if (Objects.equals(fsP.getParentNo(), "99") || Objects.equals(fsP.getParentNo(), "0"))
					{
						this.setSysType(fsP.getNo());
					}
					else
					{
						FlowSort fsPP = new FlowSort();
						fsPP.setNo(fsP.getParentNo());
						if (fsPP.RetrieveFromDBSources() == 1)
						{
							this.setSysType(fsPP.getNo());
						}
					}
				}
			}

		}
		///#endregion 为systype设置，当前所在节点的第2级别目录。

		fl = new Flow();
		fl.setNo(this.getNo());
		fl.RetrieveFromDBSources();
		fl.Update();

		super.afterInsertUpdateAction();
	}

		///#endregion


		///#region 实验中的功能.
	/** 
	 删除多余的字段.
	 
	 @return 
	*/
	public final String DoDeleteFields()
	{
		return "尚未完成.";
	}
	/** 
	 删除多余的字段.
	 
	 @return 
	*/
	public final String DoDeleteFieldsIsNull()
	{
		return "尚未完成.";
	}
	/** 
	 一件设置审核模式.
	 
	 @return 
	*/
	public final String DoSetFWCModel() throws Exception {
		GroupField gf = new GroupField();
		Nodes nds = new Nodes(this.getNo());
		for (Node nd : nds.ToJavaList())
		{
			if (nd.getItIsStartNode())
			{
				nd.setNodeFrmID( "ND" + nd.getNodeID());
				nd.setFrmWorkCheckSta (FrmWorkCheckSta.Disable);
				// nd.getHisFormType() = NodeFormType.FoolForm;
				nd.Update();
				continue;
			}

			bp.wf.template.FrmNodeComponent fnd = new FrmNodeComponent(nd.getNodeID());

			//判断是否是最后一个节点.
			if (nd.getItIsEndNode() == true || nd.getHisToNodes().size()== 0)
			{
				nd.setFrmWorkCheckSta( FrmWorkCheckSta.Readonly); //最后一个是只读.
				nd.setNodeFrmID( "ND" + Integer.parseInt(this.getNo()) + "02");
				nd.setHisFormType(NodeFormType.RefNodeFrm); //引用到其他节点表单上.
				nd.Update();

				fnd.SetValByKey(NodeAttr.NodeFrmID, nd.getNodeFrmID());
				fnd.SetValByKey(NodeAttr.FWCSta, nd.getFrmWorkCheckSta().getValue());

				fnd.Update();
				if (gf.IsExit(GroupFieldAttr.FrmID, "ND" + nd.getNodeID(), GroupFieldAttr.CtrlType, GroupCtrlType.FWC) == false)
				{
					gf = new GroupField();
					gf.setFrmID("ND" + nd.getNodeID());
					gf.setCtrlType(GroupCtrlType.FWC);
					gf.setLab("审核组件");
					gf.setIdx(0);
					gf.Insert(); //插入.
				}
				continue;
			}

			nd.setFrmWorkCheckSta(FrmWorkCheckSta.Enable);
			nd.setNodeFrmID( "ND" + Integer.parseInt(this.getNo()) + "02");

			if (String.valueOf(nd.getNodeID()).endsWith("02") == true)
			{
				nd.setHisFormType(NodeFormType.FoolForm);
			}
			else
			{
				nd.setHisFormType(NodeFormType.RefNodeFrm);
			}
			nd.Update();

			if (gf.IsExit(GroupFieldAttr.FrmID, "ND" + nd.getNodeID(), GroupFieldAttr.CtrlType, GroupCtrlType.FWC) == false)
			{
				gf = new GroupField();
				gf.setFrmID ("ND" + nd.getNodeID());
				gf.setCtrlType(GroupCtrlType.FWC);
				gf.setLab("审核组件");
				gf.setIdx(0);
				gf.Insert(); //插入.
			}
			//fnd.setHisFormType(NodeFormType.FoolForm;
			fnd.Update(); //不执行更新，会导致部分字段错误.
		}

		return "设置成功...";
	}

		///#endregion
}
