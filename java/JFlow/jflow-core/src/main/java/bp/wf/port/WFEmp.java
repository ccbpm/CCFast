package bp.wf.port;

import bp.en.*;
import bp.en.Map;
import bp.port.*;
import bp.sys.CCBPMRunModel;
import java.util.*;

/** 
 操作员
*/
public class WFEmp extends EntityNoName
{

		///#region 基本属性
	/** 
	 编号
	*/

	public final String getNo()  {
		return this.GetValStringByKey(EntityNoNameAttr.No);
	}
	public final void setNo(String value){
		this.SetValByKey(EmpAttr.No, value);
	}
	/** 
	 用户ID:SAAS模式下UserID是可以重复的.
	*/
	public final String getUserID() throws Exception {
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			return this.GetValStringByKey(WFEmpAttr.UserID);
		}

		return this.GetValStringByKey(WFEmpAttr.No);
	}
	public final void setUserID(String value)  {
		this.SetValByKey(WFEmpAttr.UserID, value);

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			this.SetValByKey(WFEmpAttr.No, bp.web.WebUser.getOrgNo() + "_" + value);
		}
		else
		{
			this.SetValByKey(WFEmpAttr.No, value);
		}
	}

	/** 
	 Token
	*/
	public final String getToken()  {
		return this.GetValStringByKey(WFEmpAttr.Token);
	}
	public final void setToken(String value){
		this.SetValByKey(WFEmpAttr.Token, value);
	}
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStringByKey(WFEmpAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(WFEmpAttr.OrgNo, value);
	}
	/** 
	 用户状态
	*/
	public final int getUseSta()  {
		return this.GetValIntByKey(WFEmpAttr.UseSta);
	}
	public final void setUseSta(int value) throws Exception
	{
		SetValByKey(WFEmpAttr.UseSta, value);
	}
	/** 
	 部门编号
	*/
	public final String getDeptNo()  {
		return this.GetValStringByKey(WFEmpAttr.FK_Dept);
	}
	public final void setDeptNo(String value) throws Exception
	{
		SetValByKey(WFEmpAttr.FK_Dept, value);
	}

	/** 
	 电话
	*/
	public final String getTel()  {
		return this.GetValStringByKey(WFEmpAttr.Tel);
	}
	public final void setTel(String value) throws Exception
	{
		SetValByKey(WFEmpAttr.Tel, value);
	}

   /* public String Email
    {
        get
        {
            return this.GetValStringByKey(WFEmpAttr.Email);
        }
        set
        {
            SetValByKey(WFEmpAttr.Email, value);
        }
    }*/

	/** 
	 发起流程.
	*/
	public final String getStartFlows()  {
		return this.GetValStrByKey(WFEmpAttr.StartFlows);
	}
	public final void setStartFlows(String value) throws Exception {
		SetValByKey(WFEmpAttr.StartFlows, value);
	}
	/** 
	 图片签名密码
	*/
	public final String getSPass() {
		return this.GetValStringByKey(WFEmpAttr.SPass);
	}
	public final void setSPass(String value) throws Exception
	{
		SetValByKey(WFEmpAttr.SPass, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 操作员
	*/
	public WFEmp()
	{
	}
	/** 
	 操作员
	 
	 @param userID
	*/
	public WFEmp(String userID) throws Exception {
		if (userID == null || userID.length() == 0)
		{
			throw new RuntimeException("@要查询的操作员编号为空。");
		}

		userID = userID.trim();
		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			if (userID.equals("admin") == true)
			{
				this.SetValByKey("No", userID);
			}
			else
			{
				this.SetValByKey("No", bp.web.WebUser.getOrgNo() + "_" + userID);
			}
		}
		else
		{
			this.SetValByKey("No", userID);
		}

		if (this.RetrieveFromDBSources() == 0)
		{
			Emp emp = new Emp(userID);
			this.setRow(emp.getRow());
			this.SetValByKey("AtPara","@Online=1");
			this.Insert();
		}
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

		Map map = new Map("WF_Emp", "操作员");

		map.AddTBStringPK(WFEmpAttr.No, null, "No", true, true, 1, 50, 36);
		map.AddTBString(WFEmpAttr.Name, null, "Name", true, false, 0, 50, 20);

		map.AddTBInt(WFEmpAttr.UseSta, 1, "用户状态0禁用,1正常.", true, true);

		map.AddTBString(WFEmpAttr.Tel, null, "Tel", true, true, 0, 50, 20);
		map.AddTBString(WFEmpAttr.FK_Dept, null, "FK_Dept", true, true, 0, 100, 36);
		//map.AddTBString(WFEmpAttr.Email, null, "Email", true, true, 0, 50, 20);

		map.AddTBString(WFEmpAttr.Stas, null, "角色s", true, true, 0, 3000, 20);
		map.AddTBString(WFEmpAttr.Depts, null, "部门s", true, true, 0, 100, 36);

		map.AddTBString(WFEmpAttr.Msg, null, "消息", true, true, 0, 4000, 20);

		//如果是集团模式或者是SAAS模式.
		if (bp.difference.SystemConfig.getCCBPMRunModel() !=  CCBPMRunModel.Single)
		{
			map.AddTBString(WFEmpAttr.UserID, null, "用户ID", true, false, 0, 50, 30);
		}

		//隶属组织.
		map.AddTBString(WFEmpAttr.OrgNo, null, "OrgNo", true, true, 0, 100, 20);

		map.AddTBString(WFEmpAttr.SPass, null, "图片签名密码", true, true, 0, 200, 20);

		map.AddTBString(WFEmpAttr.Author, null, "授权人", true, true, 0, 50, 20);
		map.AddTBString(WFEmpAttr.AuthorDate, null, "授权日期", true, true, 0, 20, 20);
		map.AddTBString(WFEmpAttr.Token, null, "Token", true, true, 0, 50, 20);
		map.AddTBAtParas(3500); //增加字段.

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 方法
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		String msg = "";
		//if (this.Email.length() == 0)
		//{
		//    if (this.HisAlertWay == AlertWay.SMSAndEmail || this.HisAlertWay == AlertWay.Email)
		//        msg += "错误：您设置了用e-mail接收信息，但是您没有设置e-mail。";
		//}

		//if (this.Tel.length() == 0)
		//{
		//    if (this.HisAlertWay == AlertWay.SMSAndEmail || this.HisAlertWay == AlertWay.SMS)
		//        msg += "错误：您设置了用短信接收信息，但是您没有设置手机号。";
		//}

		//EmpStations ess = new EmpStations();
		//ess.Retrieve(EmpStationAttr.FK_Emp, this.getNo());
		//String sts = "";
		//foreach (EmpStation es in ess)
		//{
		//    sts += es.FK_StationT + " ";
		//}
		//this.Stas = sts;

		if (!Objects.equals(msg, ""))
		{
			throw new RuntimeException(msg);
		}

		return super.beforeUpdate();
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setUseSta(1);
		return super.beforeInsert();
	}

		///#endregion

	public final void DoUp() throws Exception {
		this.DoOrderUp("FK_Dept", this.getDeptNo(), "Idx");
		return;
	}
	public final void DoDown() throws Exception
	{
		this.DoOrderDown("FK_Dept", this.getDeptNo(), "Idx");
		return;
	}
}
