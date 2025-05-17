package bp.ccbill;

import bp.en.EntityMyPK;
import bp.en.Map;
import bp.en.UAC;
import bp.sys.CCBPMRunModel;
import bp.sys.FrmBtnAttr;

/** 
 按钮
*/
public class GenerWorker extends EntityMyPK
{

		///#region 属性
	//单据ID
	public final String getFrmID()  {
		return this.GetValStringByKey(GenerBillAttr.FrmID);
	}
	public final void setFrmID(String value){
		this.SetValByKey(GenerBillAttr.FrmID, value);
	}

	//产生时间
	public final String getRDT()  {
		return this.GetValStringByKey("RDT");
	}
	public final void setRDT(String value){
		this.SetValByKey("RDT", value);
	}

	public final String getSendDT()  {
		return this.GetValStringByKey("SendDT");
	}
	public final void setSendDT(String value){
		this.SetValByKey("SendDT", value);
	}
    //审核意见
	public final String getCheckerNote()  {
		return this.GetValStringByKey("CheckerNote");
	}
	public final void setCheckerNote(String value){
		this.SetValByKey("CheckerNote", value);
	}

	public final String getEmpName()  {
		return this.GetValStringByKey("EmpName");
	}
	public final void setEmpName(String value){
		this.SetValByKey("EmpName", value);
	}

	//单据ID
	public final long getWorkID()  {return this.GetValInt64ByKey("WorkID");}
	public final void setWorkID(long value){
		this.SetValByKey("WorkID", value);
	}

	public final String getEmpNo()  {return this.GetValStrByKey("EmpNo");}
	public final void setEmpNo(String value){SetValByKey("EmpNo", value);}

	public final int getIdx()  {
		return this.GetValIntByKey("Idx");
	}
	public final void setIdx(int value){
		SetValByKey("Idx", value);
	}

	public final int getPassSta()  {
		return this.GetValIntByKey("PassSta");
	}
	public final void setPassSta(int value){
		SetValByKey("PassSta", value);
	}
	///#endregion

	///#region 权限控制.
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			uac.IsDelete = false;
			uac.IsUpdate = true;
			return uac;
		}
		uac.Readonly();
		return uac;
	}
		///#region 构造方法
	/**
	 按钮
	*/
	public GenerWorker()
	{
	}
	/// <summary>
	/// 单据控制表
	/// </summary>
	/// <param name="workID">workID</param>
	public GenerWorker(String mypk) throws Exception {
		this.setMyPK(mypk);
		this.Retrieve();
	}
	/** 
	 EnMap
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Frm_GenerWorker", "工作人员表");

		map.AddMyPK();
		map.AddTBInt("Idx", 0, "节点步骤", true, true);
		map.AddTBInt("WorkID", 0, "WorkID", true, true);
		map.AddTBString("EmpNo", null, "处理人编号", true, false, 0, 100, 10);
		map.AddTBString("EmpName", null, "处理人名称", true, false, 0, 100, 10);

		map.AddTBInt("PassSta", 0, "状态", true, false); //@0=待办@1=未开始@2=已通过@3=退回.
		map.AddTBDateTime("RDT",null, "记录日期", true, true);
		map.AddTBDateTime("SendDT",null, "审核日期", true, true);

		map.AddTBString("DeptNo", null, "部门No", true, false, 0, 100, 10);
		map.AddTBString("DeptName", null, "部门名称", true, false, 0, 100, 10);
		map.AddTBString("FrmID", null, "表单ID", true, false, 0, 100, 10);
		map.AddTBString("CheckerNote", null, "审核意见", true, false, 0, 500, 10);
		//参数.
		map.AddTBString(GenerBillAttr.AtPara, null, "参数", true, false, 0, 2000, 10);
		map.AddTBString("OrgNo", null, "OrgNo", true, false, 0, 500, 10);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	@Override
	protected boolean beforeInsert() throws Exception {
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
		}

		return super.beforeInsert();
	}

}
