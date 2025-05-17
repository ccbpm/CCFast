package bp.ccoa.knowledgemanagement;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

/** 
 知识点
*/
public class KMDtl extends EntityNoName
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(KMDtlAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(KMDtlAttr.OrgNo, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(KMDtlAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(KMDtlAttr.Rec, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(KMDtlAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(KMDtlAttr.RecName, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(KMDtlAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(KMDtlAttr.RDT, value);
	}
	/** 
	 关注者
	*/
	public final String getRiQi()  {
		return this.GetValStrByKey(KMDtlAttr.RiQi);
	}
	public final void setRiQi(String value)  {
		this.SetValByKey(KMDtlAttr.RiQi, value);
	}
	/** 
	 年月
	*/
	public final String getFoucs()  {
		return this.GetValStrByKey(KMDtlAttr.Foucs);
	}
	public final void setFoucs(String value)  {
		this.SetValByKey(KMDtlAttr.Foucs, value);
	}
	public final String getDTTo()  {
		return this.GetValStrByKey(KMDtlAttr.DTTo);
	}
	public final void setDTTo(String value)  {
		this.SetValByKey(KMDtlAttr.DTTo, value);
	}
	/** 
	 项目数
	*/
	public final String getRefTreeNo()  {
		return this.GetValStrByKey(KMDtlAttr.RefTreeNo);
	}
	public final void setRefTreeNo(String value)  {
		this.SetValByKey(KMDtlAttr.RefTreeNo, value);
	}
	public final String getKnowledgeNo()  {
		return this.GetValStrByKey(KMTreeAttr.KnowledgeNo);
	}
	public final void setKnowledgeNo(String value)  {
		this.SetValByKey(KMTreeAttr.KnowledgeNo, value);
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
		if (WebUser.getIsAdmin())
		{
			uac.IsUpdate = true;
			return uac;
		}
		return super.getHisUAC();
	}
	/** 
	 知识点
	*/
	public KMDtl()
	{
	}
	public KMDtl(String mypk) throws Exception
	{
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("OA_KMDtl", "知识点");

		map.AddTBStringPK(KMDtlAttr.No, null, "编号", false, false, 0, 50, 10);

		map.AddTBString(KMDtlAttr.Name, null, "名称", true, false, 0, 500, 10,true);
		map.AddTBStringDoc(KMDtlAttr.Docs, "Docs", null, "内容", true, false, 0, 4000, 20, true, true);


		map.AddTBString(KMDtlAttr.RefTreeNo, null, "关联树编号", false, false, 0, 50, 10);
		map.AddTBString(KMDtlAttr.KnowledgeNo, null, "知识编号", false, false, 0, 50, 10);
		map.AddTBString(KMDtlAttr.Foucs, null, "关注者(多个人用都好分开)", false, false, 0, 4000, 10);

		// map.AddTBString(KMDtlAttr.Docs, null, "内容", false, false, 0, 4000, 10);
		// map.AddDDLSysEnum(KMDtlAttr.KMDtlPRI, 0, "优先级", true, false, "KMDtlPRI", "@0=高@1=中@2=低");
		//   map.AddDDLSysEnum(KMDtlAttr.KMDtlSta, 0, "状态", true, false, "KMDtlSta", "@0=未完成@1=删除");
		// map.AddTBInt(KMDtlAttr.KMDtlSta, 0, "状态", false, false);

		//
		map.AddTBInt(KMDtlAttr.IsDel, 0, "IsDel", false, false);


		map.AddTBString(KMDtlAttr.OrgNo, null, "组织编号", false, false, 0, 100, 10);
		map.AddTBString(KMDtlAttr.Rec, null, "记录人", false, false, 0, 100, 10);
		map.AddTBString(KMDtlAttr.RecName, null, "记录人名称", false, false, 0, 100, 10, true);
		map.AddTBDateTime(KMDtlAttr.RDT, null, "记录时间", false, false);
		map.AddTBInt(KMTreeAttr.Idx, 0, "Idx", false, false);

		map.AddMyFileS("附件");




		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


		///#region 执行方法.
	@Override
	protected boolean beforeInsert() throws Exception
	{
		this.setNo(DBAccess.GenerGUID(0, null, null));
		this.setRec(WebUser.getNo());
		this.setRecName(WebUser.getName());
		if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(WebUser.getOrgNo());
		}

		//设置日期.
		this.SetValByKey(KMDtlAttr.RDT, DataType.getCurrentDateTime());

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		////计算条数.
		//this.RefTreeNo = DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS N FROM OA_KMDtlDtl WHERE RefPK='" + this.MyPK + "'");

		////计算合计工作小时..
		//this.Manager = DBAccess.RunSQLReturnValInt("SELECT SUM(Hour) + Sum(Minute)/60.00 AS N FROM OA_KMDtlDtl WHERE RefPK='" + this.MyPK + "'");

		return super.beforeUpdate();
	}

		///#endregion 执行方法.
}
