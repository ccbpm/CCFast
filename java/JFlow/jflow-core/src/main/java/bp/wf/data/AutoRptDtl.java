package bp.wf.data;
import bp.en.*;
import bp.en.Map;
import bp.wf.*;
/** 
 自动报表-数据项
*/
public class AutoRptDtl extends EntityOIDName
{

		///#region 属性
	/** 
	 对应的任务.
	*/
	public final String getAutoRptNo()  throws Exception
	{
		return this.GetValStringByKey(AutoRptDtlAttr.AutoRptNo);
	}
	public final void setAutoRptNo(String value){
		this.SetValByKey(AutoRptDtlAttr.AutoRptNo, value);
	}
	/** 
	 到达的人员
	*/
	public final String getSQLExp() throws Exception {
		String str = this.GetValStringByKey(AutoRptDtlAttr.SQLExp);
		return Glo.DealExp(str, null);
	}
	public final void setSQLExp(String value){
		this.SetValByKey(AutoRptDtlAttr.SQLExp, value);
	}
	public final String getUrlExp() throws Exception {
		String str = this.GetValStringByKey(AutoRptDtlAttr.UrlExp);
		return Glo.DealExp(str, null);
	}
	public final void setUrlExp(String value){
		this.SetValByKey(AutoRptDtlAttr.UrlExp, value);
	}

	/** 
	 发起时间（可以为空）
	*/
	public final String getBeiZhu()
	{
		return this.GetValStringByKey(AutoRptDtlAttr.BeiZhu);
	}
	public final void setBeiZhu(String value)
	{
		this.SetValByKey(AutoRptDtlAttr.BeiZhu, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 AutoRptDtl
	*/
	public AutoRptDtl()
	{
	}
	/** 
	 重写基类方法
	*/
	@Override
	public bp.en.Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_AutoRptDtl", "自动报表-数据项");
		map.setCodeStruct("3");


			//主键.
		map.AddTBIntPKOID();
		map.AddTBString(AutoRptDtlAttr.Name, null, "标题", true, false, 0, 300, 150);

		map.AddTBString(AutoRptDtlAttr.SQLExp, null, "SQL表达式(返回一个数字)", true, false, 0, 300, 300);
			//map.AddTBStringDoc(AutoRptDtlAttr.SQLExp, null, "SQL表达式(返回一个数字)", true, false, true);
		String msg = "返回的一行一列:";
		msg += "\t\n比如:SELECT COUNT(*) FROM WF_EmpWorks WHERE FK_Emp ='@WebUser.No'";
		msg += "\t\n支持ccbpm表达式。";
		map.SetHelperAlert(AutoRptDtlAttr.SQLExp, msg);

		map.AddTBString(AutoRptDtlAttr.UrlExp, null, "Url表达式", true, false, 0, 300, 300);
		  msg = "具有绝对路径的Url表达式:";
		msg += "\t\n比如:http:/128.1.1.1:9090/myurl.htm?UserNo=@WebUser.No";
		msg += "\t\n支持ccbpm表达式。";
		map.SetHelperAlert(AutoRptDtlAttr.UrlExp, msg);

		map.AddTBString(AutoRptDtlAttr.BeiZhu, null, "备注", true, false, 0, 500, 150);

		map.AddTBString(AutoRptDtlAttr.AutoRptNo, null, "任务ID", false, false, 0, 20, 10);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}