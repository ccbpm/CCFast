package bp.ccoa.ccbbs;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 信息
*/
public class BBSExt extends EntityNoName
{

		///#region 基本属性
	public final String getDocs()  {
		return this.GetValStrByKey(BBSAttr.Docs);
	}
	public final void setDocs(String value)  {
		this.SetValByKey(BBSAttr.Docs, value);
	}


		///#endregion


		///#region 构造方法
	/** 
	 权限控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsUpdate = true;
		uac.IsInsert = false;
		return uac;
	}
	/** 
	 信息
	*/
	public BBSExt()
	{
	}
	public BBSExt(String no) throws Exception {
		this.SetValByKey(BBSAttr.No, no);
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

		Map map = new Map("OA_BBS", "信息");

		map.AddTBStringPK(BBSAttr.No, null, "编号", false, true, 1, 59, 59);
		map.AddTBString(BBSAttr.Name, null, "标题", true, false, 0, 100, 10, true);

		map.AddTBStringDoc(BBSAttr.Docs, "Docs", null, "内容", true, false, 0, 4000, 20, true, true);


		map.AddDDLSysEnum(BBSAttr.BBSPRI, 0, "重要性", true, true, "BBSPRI", "@0=普通@1=紧急@2=火急");

		map.AddDDLEntities(BBSAttr.BBSType, null, "类型", new BBSTypes(), true);


		map.AddTBString(BBSAttr.Rec, null, "记录人", false, false, 0, 100, 10);
		map.AddTBString(BBSAttr.RecName, null, "记录人", false, true, 0, 100, 10, false);
		map.AddTBString(BBSAttr.RecDeptNo, null, "记录人部门", false, false, 0, 100, 10, false);


		map.AddTBString(BBSAttr.RelerName, null, "发布人", true, false, 0, 100, 10, false);
		map.AddTBString(BBSAttr.RelDeptName, null, "发布单位", true, false, 0, 100, 10, false);

		map.AddTBDateTime(BBSAttr.RDT, null, "发布日期", false, false);
		map.AddTBString(BBSAttr.NianYue, null, "隶属年月", false, false, 0, 10, 10);

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			map.AddTBString(BBSAttr.OrgNo, null, "组织", true, true, 0, 100, 10);
		}



		//增加附件.
		map.AddMyFileS();

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{

		this.SetValByKey(BBSAttr.No, DBAccess.GenerGUID(0, null, null));

		this.SetValByKey(BBSAttr.Rec, WebUser.getNo());
		this.SetValByKey(BBSAttr.RecName, WebUser.getName());
		this.SetValByKey(BBSAttr.RecDeptNo, WebUser.getDeptNo());

		this.SetValByKey(BBSAttr.RDT, DataType.getCurrentDateTime()); //记录日期.
		this.SetValByKey(BBSAttr.NianYue, DataType.getCurrentYearMonth()); //隶属年月.

		this.SetValByKey(BBSAttr.RelerName, WebUser.getName());
		this.SetValByKey(BBSAttr.RelDeptName, WebUser.getDeptName());

		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.SetValByKey(BBSAttr.OrgNo, WebUser.getOrgNo());
		}

		return super.beforeInsert();
	}

		///#endregion 执行方法.
}
