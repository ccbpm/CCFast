package bp.wf.rpt;

import bp.en.*; import bp.en.Map;
import bp.port.*;


/** 
 RptEmp 的摘要说明。
*/
public class RptEmp extends Entity
{

	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsView = true;
			uac.IsDelete = true;
			uac.IsInsert = true;
			uac.IsUpdate = true;
			uac.IsAdjunct = true;
		}
		return uac;
	}


		///#region 基本属性
	/** 
	 报表ID
	*/
	public final String getFKRpt()  {
		return this.GetValStringByKey(RptEmpAttr.FK_Rpt);
	}
	public final void setFKRpt(String value) throws Exception
	{
		SetValByKey(RptEmpAttr.FK_Rpt, value);
	}
	public final String getEmpName()  {
		return this.GetValRefTextByKey(RptEmpAttr.FK_Emp);
	}
	/** 
	人员
	*/
	public final String getEmpNo()  {
		return this.GetValStringByKey(RptEmpAttr.FK_Emp);
	}
	public final void setEmpNo(String value) throws Exception
	{
		SetValByKey(RptEmpAttr.FK_Emp, value);
	}

		///#endregion


		///#region 扩展属性


		///#endregion


		///#region 构造函数
	/** 
	 报表人员
	*/
	public RptEmp()
	{
	}
	/** 
	 报表人员对应
	 
	 @param _empoid 报表ID
	 @param wsNo 人员编号 	
	*/
	public RptEmp(String _empoid, String wsNo) throws Exception {
		this.setFKRpt(_empoid);
		this.setEmpNo(wsNo);
		if (this.Retrieve() == 0)
		{
			this.Insert();
		}
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()  {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_RptEmp", "报表人员对应信息");
		map.setEnType(EnType.Dot2Dot);

		map.AddTBStringPK(RptEmpAttr.FK_Rpt, null, "报表", false, false, 1, 15, 1);
		map.AddDDLEntitiesPK(RptEmpAttr.FK_Emp, null, "人员", new Emps(), true);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
