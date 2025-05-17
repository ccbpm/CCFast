package bp.ccfast.portal;

import bp.da.*;
import bp.en.*;
import bp.ccfast.portal.windowext.*;
import bp.sys.CCBPMRunModel;

import java.util.*;

/** 
 信息块s
*/
public class WindowTemplates extends EntitiesNoName
{

		///#region 构造
	/** 
	 信息块s
	*/
	public WindowTemplates()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new WindowTemplate();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<WindowTemplate> ToJavaList()
	{
		return (java.util.List<WindowTemplate>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<WindowTemplate> Tolist()
	{
		ArrayList<WindowTemplate> list = new ArrayList<WindowTemplate>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((WindowTemplate)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

	public final void InitDocs() throws Exception {
		//处理内容.
		for (WindowTemplate item : this.ToJavaList())
		{
			//文本的, 不用转化.
			if (item.getWinDocModel().equals(WinDocModel.Html))
			{
				continue;
			}

			//内置的.
			if (item.getWinDocModel().equals(WinDocModel.System))
			{
				String exp = item.getDocs();
				exp = bp.wf.Glo.DealExp(exp, null);
				item.setDocs(exp);
				continue;
			}

			//HtmlVar 变量字段.
			if (item.getWinDocModel().equals(WinDocModel.HtmlVar))
			{
				HtmlVarDtls dtls = new HtmlVarDtls();
				dtls.Retrieve(DtlAttr.RefPK, item.getNo(), null);

				for (HtmlVarDtl dtl : dtls.ToJavaList())
				{
					String sql = dtl.getExp0();
					sql = sql.replace("~", "'");
					sql = bp.wf.Glo.DealExp(sql, null);
					try
					{
						dtl.setExp0(DBAccess.RunSQLReturnStringIsNull(sql, "0"));
					}
					catch (RuntimeException ex)
					{
						dtl.setExp0("err@" + ex.getMessage());
					}
				}
				item.setDocs(dtls.ToJson("dt"));
				continue;
			}

			//tab 标签页.
			if (item.getWinDocModel().equals(WinDocModel.Tab))
			{
				TabDtls dtls = new TabDtls();
				dtls.Retrieve(DtlAttr.RefPK, item.getNo(), null);

				for (TabDtl dtl : dtls.ToJavaList())
				{
					String sql = dtl.getExp0();
					sql = sql.replace("~", "'");
					sql = bp.wf.Glo.DealExp(sql, null);
					try
					{
						DataTable dt = DBAccess.RunSQLReturnTable(sql);
						dtl.setExp0(bp.tools.Json.ToJson(dt)); // DBAccess.RunSQLReturnStringIsNull(sql, "0");
					}
					catch (RuntimeException ex)
					{
						dtl.setExp0("err@" + ex.getMessage());
					}
				}
				item.setDocs(dtls.ToJson("dt"));
				continue;
			}



				///#region 扇形百分比.
			if (item.getWinDocModel().equals(WinDocModel.ChartRate)) //sql列表.
			{
				try
				{
					//分子.
					String sql = item.GetValStringByKey(WindowTemplateAttr.SQLOfFZ);
					sql = sql.replace("~", "'");
					sql = bp.wf.Glo.DealExp(sql, null);
					String val = DBAccess.RunSQLReturnString(sql);
					item.SetValByKey(WindowTemplateAttr.SQLOfFZ, val);

					//分母.
					sql = item.GetValStringByKey(WindowTemplateAttr.SQLOfFM);
					sql = sql.replace("~", "'");
					sql = bp.wf.Glo.DealExp(sql, null);
					val = DBAccess.RunSQLReturnString(sql);
					item.SetValByKey(WindowTemplateAttr.SQLOfFM, val);
				}
				catch (RuntimeException ex)
				{
					item.setWinDocModel(WinDocModel.Html);
					item.setDocs("err@" + ex.getMessage() + " SQL=" + item.getDocs());
				}
			}

				///#endregion 扇形百分比.


			//SQL列表. 
			if (item.getWinDocModel().equals(WinDocModel.Table) || item.getWinDocModel().equals(WinDocModel.ChartLine) || item.getWinDocModel().equals(WinDocModel.ChartChina) || item.getWinDocModel().equals(WinDocModel.ChartRing) || item.getWinDocModel().equals(WinDocModel.ChartPie)) //饼图.
			{
				try
				{
					String sql = item.getDocs();
					sql = sql.replace("~", "'");
					sql = bp.wf.Glo.DealExp(sql, null);
					DataTable dt = DBAccess.RunSQLReturnTable(sql);
					item.setDocs(bp.tools.Json.ToJson(dt));
				}
				catch (RuntimeException ex)
				{
					Log.DebugWriteError(ex.getMessage());
					item.setWinDocModel(WinDocModel.Html);
					item.setDocs("err@" + ex.getMessage() + " SQL=" + item.getDocs());
				}
			}
		}
	}
	@Override
	public int RetrieveAll() throws Exception {
		int i = this.RetrieveAllFromDBSource("Idx");
		if (i >= 1)
		{
			InitHomePageData();
			return i;
		}

		//初始化模板数据.
		InitHomePageData();

		//查询模数据.
		i = this.RetrieveAllFromDBSource("Idx");
		InitDocs();
		return i;
	}
	/** 
	 初始化 Home 数据
	*/
	public final String InitHomePageData() throws Exception {

		WindowTemplate en = new WindowTemplate();


			///#region 关于我们.
		en.setNo("001");
		en.setWinDocModel(WinDocModel.Html);
		en.setPageID("Home");
		en.setName("关于我们");
		String html = "";
		html += "<ul>";
		html += " <li>ccbpm是一个100%的开源软件,包含工作流程引擎、表单引擎、组织结构管理、菜单管理等敏捷开发的基础模块。</li>";
		html += " <li>该开源软件由驰骋公司从2003年开始研发到至今，经过多个版本迭代，并历经数千个项目于用户需求场景完成。</li>";
		html += " <li>设计严谨、考究抽象程度高、覆盖大部分客户应用需求，属于一款不可多得的应用国产的敏捷开发工具。</li>";
		html += " <li>源代码都发布在giee上，采用GPL开源协议进行开源，遵守GPL开源协议使用ccbpm合法有效。</li>";
		html += " <li>驰骋公司对外提供现场培训、技术支持、协助集成、协助项目落地服务，对小微企业，小企业，中等企业，大企业收费8,12,18,23三个等级的付费。</li>";
		html += "</ul>";
		en.setDocs(html);
		en.setMoreLinkModel(1);
		en.setColSpan(2);
		en.setItIsDel(true);
		en.Insert();

			///#endregion 关于我们.


			///#region 登录信息.
		en = new WindowTemplate();
		en.setPageID("Home");
		en.setNo("002");
		en.setName("登录信息");
		en.setWinDocModel(WinDocModel.System); //系统内置的.

		html = "<table>";
		html += "<tr>";
		html += " <td>帐号</td>";
		html += " <td>@WebUser.No</td>";
		html += "</tr>";

		html += "<tr>";
		html += " <td>姓名</td>";
		html += " <td>@WebUser.Name</td>";
		html += "</tr>";

		html += "<tr>";
		html += " <td>部门</td>";
		html += " <td>@WebUser.FK_DeptName</td>";
		html += "</tr>";
		en.setDocs(html);
		en.setColSpan(1);
		en.Insert();

			///#endregion 登录信息.


			///#region 我的待办.
		en = new WindowTemplate();
		en.setPageID("Home");
		en.setNo("003");
		en.setName("我的待办");
		en.setWinDocModel(WinDocModel.ChartLine); //柱状图.

		html = "SELECT NodeName AS '流程名', COUNT(WorkID) as '数量' ";
		html += " FROM WF_GenerWorkerlist WHERE FK_Emp = '@WebUser.No' AND IsPass=0 GROUP BY NodeName ";
		en.setDocs(html);
		en.setMoreLinkModel(1);
		en.setColSpan(4);
		en.Insert();

			///#endregion 我的待办分布.


			///#region 全部流程.
		en = new WindowTemplate();
		en.setPageID("Home");
		en.setNo("004");
		en.setName("全部流程");
		en.setWinDocModel(WinDocModel.ChartLine); //柱状图.

		if (bp.difference.SystemConfig.getCCBPMRunModel() ==   CCBPMRunModel.Single)
		{
			en.setDocs("SELECT FlowName AS '流程名', COUNT(WorkID) AS '数量'  FROM WF_GenerWorkFlow WHERE WFState !=0 GROUP BY FlowName");
		}
		else
		{
			en.setDocs("SELECT FlowName AS '流程名', COUNT(WorkID) AS '数量'  FROM WF_GenerWorkFlow WHERE WFState !=0 AND OrgNo='@WebUser.OrgNo' GROUP BY FlowName");
		}

		en.setMoreLinkModel(1);
		en.setColSpan(2);

		en.Insert();

			///#endregion 我的待办分布.


			///#region 我的未完成.
		en = new WindowTemplate();
		en.setPageID("Home");
		en.setWinDocModel(WinDocModel.ChartLine); //.
		en.setNo("005");
		en.setName("未完成");
		html = "SELECT FlowName AS '流程名', COUNT(WorkID) AS '数量' FROM WF_GenerWorkFlow  WHERE WFState = 2 ";
		html += "and Emps like '%@WebUser.No%' GROUP BY FlowName";
		en.setDocs(html);
		en.setMoreLinkModel(1);
		en.setColSpan(4);

		en.Insert();

			///#endregion 我的未完成.


			///#region 我的发起.
		en = new WindowTemplate();
		en.setPageID("Home");
		en.setNo("006");
		en.setName("我的发起");
		en.setWinDocModel(WinDocModel.ChartPie); //柱状图.

		if (bp.difference.SystemConfig.getCCBPMRunModel() ==  CCBPMRunModel.Single)
		{
			en.setDocs("SELECT FlowName AS '流程名', COUNT(WorkID) AS '数量'  FROM WF_GenerWorkFlow WHERE WFState !=0 AND Starter='@WebUser.No'  GROUP BY FlowName");
		}
		else
		{
			en.setDocs("SELECT FlowName AS '流程名', COUNT(WorkID) AS '数量'  FROM WF_GenerWorkFlow WHERE WFState !=0 AND Starter='@WebUser.No' AND OrgNo='@WebUser.OrgNo' GROUP BY FlowName");
		}

		en.setMoreLinkModel(1);
		en.setColSpan(1);
		en.Insert();

			///#endregion 我的发起.


			///#region 我参与的.
		en = new WindowTemplate();
		en.setPageID("Home");
		en.setNo("007");
		en.setName("我参与的");
		en.setWinDocModel(WinDocModel.ChartChina); //柱状图.

		if (bp.difference.SystemConfig.getCCBPMRunModel() ==   CCBPMRunModel.Single)
		{
			en.setDocs("SELECT FlowName AS '流程名', COUNT(WorkID) AS '数量'  FROM WF_GenerWorkFlow WHERE WFState !=0 AND Emps LIKE  '%@WebUser.No,%'  GROUP BY FlowName");
		}
		else
		{
			en.setDocs("SELECT FlowName AS '流程名', COUNT(WorkID) AS '数量'  FROM WF_GenerWorkFlow WHERE WFState !=0 AND Emps LIKE '%@WebUser.No,%' AND OrgNo='@WebUser.OrgNo' GROUP BY FlowName");
		}

		en.setMoreLinkModel(1);
		en.setColSpan(4);

		en.Insert();

			///#endregion 我的发起.


			///#region 流程实例月份柱状图.
		en = new WindowTemplate();
		en.setPageID("Home");
		en.setNo("008");
		en.setName("月统计发起");
		en.setWinDocModel(WinDocModel.ChartLine);
		html = "SELECT FK_NY  AS '月份', COUNT(WorkID) AS '数量'  FROM WF_GenerWorkFlow WHERE WFState !=0 GROUP BY FK_NY";
		en.setDocs(html);
		en.setMoreLinkModel(1);
		en.setColSpan(4);
		en.Insert();

			///#endregion 流程实例月份柱状图.

		return "执行成功.";


	}
}
