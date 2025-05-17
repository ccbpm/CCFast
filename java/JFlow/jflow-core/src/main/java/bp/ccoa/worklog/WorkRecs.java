package bp.ccoa.worklog;

import bp.da.*;
import bp.web.*;
import bp.en.*;
import java.util.*;

/** 
 工作日志 s
*/
public class WorkRecs extends EntitiesMyPK
{

		///#region 主页数据. Default.htm
	/** 
	 最近的数据
	 
	 @return 
	*/
	public final String Default_Init() throws Exception {
		//查询最近的日志数据.
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(WorkRecAttr.Rec, WebUser.getNo());
		qo.Top = 20;
		qo.addOrderByDesc(WorkRecAttr.RDT);
		qo.DoQuery();

		return this.ToJson("dt");

	}
	/** 
	 默认页面的本周按时完成率.
	 
	 @return 
	*/
	public final String Default_ChartInit()
	{
		Hashtable ht = new Hashtable();

		//本周完成的数量.
		String sql = "SELECT COUNT(*) FROM OA_WorkRecDtl WHERE Rec='" + WebUser.getNo() + "' AND WeekNum='" + DataType.getCurrentWeek() + "'";
		ht.put("weekNum", DBAccess.RunSQLReturnValInt(sql));

		//本月的数量.
		  sql= "SELECT COUNT(*) FROM OA_WorkRecDtl WHERE Rec='" + WebUser.getNo() + "' AND NianYue='" + DataType.getCurrentYearMonth() + "'";
		ht.put("monthNum", DBAccess.RunSQLReturnValInt(sql));

		//上一个月的的数量.
		sql = "SELECT COUNT(*) FROM OA_WorkRecDtl WHERE Rec='" + WebUser.getNo() + "' AND NianYue='" + DataType.getCurrentNYOfPrevious() + "'";
		ht.put("monthOfPrvNum", DBAccess.RunSQLReturnValInt(sql));

		//本月的数量.
		int monthOfPrvNum = DBAccess.RunSQLReturnValInt(sql, 0);

		//本年的数量.
		sql = "SELECT COUNT(*) FROM OA_WorkRecDtl WHERE Rec='" + WebUser.getNo() + "' AND NianYue='" + DataType.getCurrentYear() + "'";
		ht.put("yearNum", DBAccess.RunSQLReturnValInt(sql));

		//转成Json.
		return bp.tools.Json.ToJson(ht);
	}
	/** 
	 月份
	 
	 @return 
	*/
	public final String Default_Months()
	{
		String sql = "SELECT COUNT(*) AS Num, NianYue FROM OA_WorkRecDtl WHERE Rec='" + WebUser.getNo() + "' AND NianDu='" + DataType.getCurrentYear() + "'";
		sql += " GROUP BY NianYue ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}
	public final String Default_Prjs()
	{
		String sql = "SELECT Count(*) AS Num, PrjName FROM OA_WorkRecDtl WHERE Rec='" + WebUser.getNo() + "' AND NianDu='" + DataType.getCurrentYear() + "'";
		sql += " GROUP BY PrjName ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

		///#endregion 主页数据.


		///#region 分析页面. FenXi.htm
	/** 
	 工作周平均工作时长.
	 
	 @return 
	*/
	public final String FenXi_WeekAvg(String empNo)
	{
		String sql = "SELECT AVG(HeiJiHour ) as Num, WeekNum as Item  FROM OA_WorkRecDtl WHERE   Rec='" + empNo + "' AND NianDu='" + DataType.getCurrentYear() + "'  GROUP BY WeekNum";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 工作月平均工作时长
	 
	 @param empNo
	 @return 
	*/
	public final String FenXi_MonthAvg(String empNo)
	{
		String sql = "SELECT AVG(HeiJiHour ) as Num, NianYue as Item  FROM OA_WorkRecDtl WHERE   Rec='" + empNo + "' AND NianDu='" + DataType.getCurrentYear() + "'  GROUP BY NianYue";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

		///#endregion 分析页面.


		///#region 分析页面. FenXi.htm 项目分析。
	public final String FenXi_PrjWeek(String empNo)
	{
		String sql = "SELECT SUM(HeiJiHour) as Num, PrjName, WeekNum as Item  FROM OA_WorkRecDtl WHERE   Rec='" + empNo + "' AND NianDu='" + DataType.getCurrentYear() + "'  GROUP BY PrjName,WeekNum";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}
	public final String FenXi_PrjMonth(String empNo)
	{
		String sql = "SELECT SUM(HeiJiHour) as Num, PrjName, NianYue as Item  FROM OA_WorkRecDtl WHERE   Rec='" + empNo + "' AND NianDu='" + DataType.getCurrentYear() + "'  GROUP BY PrjName,NianYue";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}
	public final String FenXi_Prj(String empNo)
	{
		String sql = "SELECT SUM(HeiJiHour) as Num, PrjName  FROM OA_WorkRecDtl WHERE   Rec='" + empNo + "' AND NianDu='" + DataType.getCurrentYear() + "'  GROUP BY PrjName";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}

		///#endregion 分析页面. FenXi.htm
	public final String CheckRZ_Init()
	{
		String sql = "SELECT A.* FROM OA_WorkRec A,OA_WorkShare B WHERE A.Rec=B.EmpNo AND B.ShareToEmpNo='" + WebUser.getNo() + "' AND B.ShareState=1 ORDER BY A.RDT ";
		DataTable dt = DBAccess.RunSQLReturnTable(sql);
		return bp.tools.Json.ToJson(dt);
	}
	/** 
	 xxxx
	 
	 @return 
	*/
	public final String FenXi_Init() throws Exception {
		// HtmlVar信息块 ， 本周完成数， 本月完成数，
		bp.ccfast.portal.windowext.HtmlVar html = new bp.ccfast.portal.windowext.HtmlVar();
		html.GetValDocHtml();
		html.Insert();

		return "";

	}

	/** 
	 工作日志
	*/
	public WorkRecs()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new WorkRec();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<WorkRec> ToJavaList()
	{
		return (java.util.List<WorkRec>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<WorkRec> Tolist()
	{
		ArrayList<WorkRec> list = new ArrayList<WorkRec>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((WorkRec)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
