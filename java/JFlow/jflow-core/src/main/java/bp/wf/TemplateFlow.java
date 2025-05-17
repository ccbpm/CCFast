package bp.wf;

import bp.da.*;
import bp.en.Map;
import bp.wf.template.*;
/**
 流程
 记录了流程的信息．
 流程的编号，名称，建立时间．
*/
public class TemplateFlow extends bp.en.EntityNoName
{


		///#region 构造方法
	/**
	 流程
	*/
	 public TemplateFlow(){
		this.setNo("");
	 }
	public TemplateFlow(String _No) throws Exception {
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

		Map map = new Map("WF_Flow", "流程");

		//取消了缓存.
		map.setDepositaryOfEntity(Depositary.Application);
		map.setCodeStruct( "3");

		map.AddTBStringPK(FlowAttr.No, null, "编号", true, true, 1, 4, 3);
		map.AddTBString(FlowAttr.Name, null, "名称", true, false, 0, 200, 10);

		map.AddTBString(FlowAttr.FK_FlowSort, null, "流程类别", true, false, 0, 35, 10);
		// map.AddDDLEntities(FlowAttr.FK_FlowSort, "01", "流程类别", new FlowSorts(), false);
		map.AddTBString(FlowAttr.SysType, null, "系统类别", true, false, 0, 3, 10);

		map.AddTBInt(FlowAttr.FlowRunWay, 0, "运行方式", false, false);
		map.AddTBInt(FlowAttr.WorkModel, 0, "WorkModel", false, false);

		map.AddTBString(FlowAttr.RunObj, null, "运行内容", true, false, 0, 500, 10);
		map.AddTBString(FlowAttr.Note, null, "备注", true, false, 0, 300, 10);
		map.AddTBString(FlowAttr.RunSQL, null, "流程结束执行后执行的SQL", true, false, 0, 500, 10);

		map.AddTBInt(FlowAttr.NumOfBill, 0, "是否有单据", false, false);
		map.AddTBInt(FlowAttr.NumOfDtl, 0, "NumOfDtl", false, false);
		map.AddTBInt(FlowAttr.FlowAppType, 0, "流程类型", false, false);
		map.AddTBInt(FlowAttr.ChartType, 1, "节点图形类型", false, false);

		//map.AddTBInt(FlowAttr.IsFrmEnable, 0, "是否显示表单", true, true);
		map.AddTBInt(FlowAttr.IsTruckEnable, 1, "是否显示轨迹图", true, true);
		map.AddTBInt(FlowAttr.IsTimeBaseEnable, 1, "是否显示时间轴", true, true);
		map.AddTBInt(FlowAttr.IsTableEnable, 1, "是否显示时间表", true, true);
		//map.AddTBInt(FlowAttr.IsOPEnable, 0, "是否显示操作", true, true);
		map.AddTBInt(FlowAttr.TrackOrderBy, 0, "排序方式", true, true);
		map.AddTBInt(FlowAttr.SubFlowShowType, 0, "子流程轨迹图显示模式", true, true);

		// map.AddBoolean(FlowAttr.IsOK, true, "是否启用", true, true);
		map.AddTBInt(FlowAttr.IsCanStart, 1, "可以独立启动否？", true, true);
		map.AddTBInt(FlowAttr.IsStartInMobile, 1, "是否可以在手机里发起？", true, true);

		//(启用后,ccflow就会为已知道的节点填充处理人到WF_SelectAccper)
		map.AddTBInt(FlowAttr.IsFullSA, 0, "是否自动计算未来的处理人？", false, false);
		map.AddTBInt(FlowAttr.IsMD5, 0, "IsMD5", false, false);

		map.AddTBInt(FlowAttr.IsEnableDBVer, 0, "是否是启用数据版本控制", false, false);
		map.AddTBInt(FlowAttr.Idx, 0, "显示顺序号(在发起列表中)", true, false);

		map.AddTBInt(FlowAttr.SDTOfFlowRole, 0, "流程计划完成日期计算规则", true, false);
		map.AddTBString(FlowAttr.SDTOfFlowRoleSQL, null, "流程计划完成日期计算规则SQL", false, false, 0, 200, 10);

		// add 2013-01-01.
		map.AddTBString(FlowAttr.PTable, null, "流程数据存储主表", true, false, 0, 50, 10);

		// add 2019.11.07
		map.AddTBString(FlowAttr.FrmUrl, null, "表单Url", true, false, 0, 150, 10, true);

		// 草稿规则 "@0=无(不设草稿)@1=保存到待办@2=保存到草稿箱"
		map.AddTBInt(FlowAttr.Draft, 0, "草稿规则", true, false);

		// add 2013-02-05.
		map.AddTBString(FlowAttr.TitleRole, null, "标题生成规则", true, false, 0, 90, 10, true);
		map.AddTBString(FlowAttr.TitleRoleNodes, null, "生成标题的节点", true, false, 0, 300, 10, true);
		// add 2013-02-14
		map.AddTBString(FlowAttr.FlowMark, null, "流程标记", true, false, 0, 50, 10);
		map.AddTBString(FlowAttr.FlowEventEntity, null, "FlowEventEntity", true, false, 0, 100, 10, true);
		map.AddTBInt(FlowAttr.GuestFlowRole, 0, "是否是客户参与流程？", true, false);
		map.AddTBString(FlowAttr.BillNoFormat, null, "单据编号格式", true, false, 0, 50, 10, true);

		//部门权限控制类型,此属性在报表中控制的.
		map.AddTBInt(FlowAttr.DRCtrlType, 0, "部门查询权限控制方式", true, false);

		//运行主机. 这个流程运行在那个子系统的主机上.
		map.AddTBInt(FlowAttr.IsToParentNextNode, 0, "子流程运行到该节点时，让父流程自动运行到下一步", false, false);


			///#region 流程启动限制
		map.AddTBInt(FlowAttr.StartLimitRole, 0, "启动限制规则", true, false);
		map.AddTBString(FlowAttr.StartLimitPara, null, "规则内容", true, false, 0, 500, 10, true);
		map.AddTBString(FlowAttr.StartLimitAlert, null, "限制提示", true, false, 0, 500, 10, false);
		map.AddTBInt(FlowAttr.StartLimitWhen, 0, "提示时间", true, false);

			///#endregion 流程启动限制


			///#region 导航方式。
		map.AddTBInt(FlowAttr.StartGuideWay, 0, "前置导航方式", false, false);
		map.AddTBString(FlowAttr.StartGuideLink, null, "右侧的连接", true, false, 0, 200, 10, true);
		map.AddTBString(FlowAttr.StartGuideLab, null, "连接标签", true, false, 0, 200, 10, true);

		map.AddTBString(FlowAttr.StartGuidePara1, null, "参数1", true, false, 0, 500, 10, true);
		map.AddTBString(FlowAttr.StartGuidePara2, null, "参数2", true, false, 0, 500, 10, true);
		map.AddTBString(FlowAttr.StartGuidePara3, null, "参数3", true, false, 0, 500, 10, true);
		map.AddTBInt(FlowAttr.IsResetData, 0, "是否启用数据重置按钮？", true, false);
		//    map.AddTBInt(FlowAttr.IsImpHistory, 0, "是否启用导入历史数据按钮？", true, false);
		map.AddTBInt(FlowAttr.IsLoadPriData, 0, "是否导入上一个数据？", true, false);

			///#endregion 导航方式。

		map.AddTBInt(FlowAttr.IsDBTemplate, 0, "是否启用数据模版？", true, false);

		//批量发起 add 2013-12-27.
		map.AddTBInt(FlowAttr.IsBatchStart, 0, "是否可以批量发起", true, false);
		map.AddTBString(FlowAttr.BatchStartFields, null, "批量发起字段(用逗号分开)", true, false, 0, 200, 10, true);
		//map.AddTBInt(FlowAttr.IsAutoSendSubFlowOver, 0, "(当前节点为子流程时)是否检查所有子流程完成后父流程自动发送", true, true);

		map.AddTBString(FlowAttr.Ver, null, "版本号", true, true, 0, 20, 10);
		map.AddTBString(FlowAttr.OrgNo, null, "OrgNo", true, true, 0, 50, 10);

		map.AddTBDateTime(FlowAttr.CreateDate, null, "创建日期", true, true);
		map.AddTBString(FlowAttr.Creater, null, "创建人", true, true, 0, 100, 10, true);

		// 改造参数类型.
		map.AddTBInt(FlowAttr.DevModelType, 0, "设计模式", false, false);
		map.AddTBString(FlowAttr.DevModelPara, null, "设计模式参数", false, false, 0, 50, 10, false);
		//  map.AddTBString(FlowAttr.DomainExt, null, "DomainExt", true, true, 0, 50, 10);
		//参数.
		map.AddTBAtParas(1000);


			///#region 数据同步方案
		//数据同步方式.
		map.AddTBInt(FlowAttr.DTSWay, DataDTSWay.None.getValue(), "同步方式", true, true);
		//map.AddTBString(FlowAttr.DTSSpecNodes, null, "同步字段", false, false, 0, 4000, 10);

		//map.AddTBString(FlowAttr.DTSDBSrc, null, "数据源", true, false, 0, 200, 100, false);
		//map.AddTBString(FlowAttr.DTSBTable, null, "业务表名", true, false, 0, 200, 100, false);
		//map.AddTBString(FlowAttr.DTSBTablePK, null, "业务表主键", false, false, 0, 32, 10);

		//map.AddTBInt(FlowAttr.DTSTime, FlowDTSTime.AllNodeSend.getValue(), "执行同步时间点", true, true);
		//map.AddTBString(FlowAttr.DTSFields, null, "要同步的字段s,中间用逗号分开.", false, false, 0, 900, 100, false);

		map.AddTBString("Icon", null, "Icon", true, true, 0, 50, 10);
		map.AddTBString("TestSysPara", null, "流程系统参数", false, true, 0, 100, 10);
		map.AddTBString("TestFrmPara", null, "表单填充数据", false, true, 0, 100, 10);
			///#endregion 数据同步方案

		this.set_enMap(map);
		return this.get_enMap();
	}


		///#endregion
}
