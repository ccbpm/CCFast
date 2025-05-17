package bp.ccoa.ccinfo;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;


/** 
 信息
*/
public class Info extends EntityNoName
{

		///#region 基本属性
	public final String getDocs()  {
		return this.GetValStrByKey(InfoAttr.Docs);
	}
	public final void setDocs(String value) {
		this.SetValByKey(InfoAttr.Docs, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(InfoAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(InfoAttr.Rec, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(InfoAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(InfoAttr.RecName, value);
	}
	public final String getRecDeptNo()  {
		return this.GetValStrByKey(InfoAttr.RecDeptNo);
	}
	public final void setRecDeptNo(String value)  {
		this.SetValByKey(InfoAttr.RecDeptNo, value);
	}
	public final String getRecDeptName()  {
		return this.GetValStrByKey(InfoAttr.RecDeptName);
	}
	public final void setRecDeptName(String value)  {
		this.SetValByKey(InfoAttr.RecDeptName, value);
	}

	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsUpdate = true;
		uac.IsInsert = true;
		return uac;
	}
	/** 
	 信息
	*/
	public Info()
	{
	}
	public Info(String no) throws Exception {
		this.SetValByKey(InfoAttr.No, no);
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

		Map map = new Map("OA_Info", "信息");

		map.AddTBStringPK(InfoAttr.No, null, "编号", false, true, 1, 59, 59);
		map.AddTBString(InfoAttr.Name, null, "标题", true, false, 0, 300, 10, true);

		map.AddTBStringDoc("Docs", "Docs", null, "内容", true, false, 0, 4000, 20, true, true);

		map.AddDDLSysEnum(InfoAttr.InfoPRI, 0, "重要性", true, true, "InfoPRI", "@0=普通@1=紧急@2=火急");
		map.AddDDLEntities(InfoAttr.InfoType, null, "类型", new InfoTypes(), true);
		map.AddDDLSysEnum(InfoAttr.InfoSta, 0, "状态", true, true, InfoAttr.InfoSta, "@0=发布中@1=禁用");


		map.AddTBString(InfoAttr.Rec, null, "记录人", false, false, 0, 100, 10);
		map.AddTBString(InfoAttr.RecName, null, "记录人", true, true, 0, 100, 10, false);
		map.AddTBString(InfoAttr.RecDeptNo, null, "记录人部门", false, false, 0, 100, 10, false);


		map.AddTBString(InfoAttr.RelerName, null, "发布人", true, false, 0, 100, 10, false);
		map.AddTBString(InfoAttr.RelDeptName, null, "发布单位", true, false, 0, 100, 10, false);

		map.AddTBDateTime(InfoAttr.RDT, null, "发布日期", true, true);
		map.AddTBString(InfoAttr.NianYue, null, "隶属年月", false, false, 0, 10, 10);

		map.AddTBInt(InfoAttr.ReadTimes, 0, "读取次数", true, true);
		map.AddTBStringDoc(InfoAttr.Reader, null, "读取人", false, false, false, 10);

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			map.AddTBString(InfoAttr.OrgNo, null, "组织", true, true, 0, 100, 10);
		}

		//增加附件.
		map.AddMyFile(null, null, null);



			///#region 设置查询条件.
		map.DTSearchKey = InfoAttr.RDT;
		map.DTSearchWay = DTSearchWay.ByDate;
		map.DTSearchLabel = "发布日期";

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			map.AddHidden("OrgNo", "=", WebUser.getOrgNo());
		}

		map.AddSearchAttr(InfoAttr.InfoSta, 130);
		map.AddSearchAttr(InfoAttr.InfoType, 130);

			///#endregion 设置查询条件.


		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	public final String DoRead() throws Exception {
		String reader = this.GetValStrByKey("Reader");
		if (reader.contains(WebUser.getNo() + ",") == false)
		{
			return "";
		}

		reader += "@" + WebUser.getName() + ",";
		this.SetValByKey("Reader", reader);
		int t = this.GetValIntByKey(InfoAttr.ReadTimes);
		this.SetValByKey("ReadTimes", t + 1);
		try
		{
			this.Update();
		}
		catch (RuntimeException ex)
		{
			Log.DebugWriteInfo("读取人数太多." + ex.getMessage());
		}
		return "";
	}
	@Override
	protected boolean beforeInsert() throws Exception
	{

		this.SetValByKey(InfoAttr.No, DBAccess.GenerGUID(0, null, null));

		this.SetValByKey(InfoAttr.Rec, WebUser.getNo());
		this.SetValByKey(InfoAttr.RecName, WebUser.getName());
		this.SetValByKey(InfoAttr.RecDeptNo, WebUser.getDeptNo());

		this.SetValByKey(InfoAttr.RDT, DataType.getCurrentDateTime()); //记录日期.
		this.SetValByKey(InfoAttr.NianYue, DataType.getCurrentYearMonth()); //隶属年月.

		this.SetValByKey(InfoAttr.RelerName, WebUser.getName());
		this.SetValByKey(InfoAttr.RelDeptName, WebUser.getDeptName());

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.SetValByKey(InfoAttr.OrgNo, WebUser.getOrgNo());
		}

		this.setNo(DBAccess.GenerGUID(0, null, null));


		return super.beforeInsert();
	}

		///#endregion 执行方法.


}
