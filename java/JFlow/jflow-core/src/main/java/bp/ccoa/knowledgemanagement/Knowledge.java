package bp.ccoa.knowledgemanagement;

import bp.da.*;
import bp.web.*;
import bp.en.*;
import bp.en.Map;
import bp.sys.*;
import bp.difference.*;

/** 
 知识管理
*/
public class Knowledge extends EntityNoName
{

		///#region 基本属性
	public final int getKnowledgeSta() {
		return this.GetValIntByKey(KnowledgeAttr.KnowledgeSta);
	}
	public final void setKnowledgeSta(int value)  {
		this.SetValByKey(KnowledgeAttr.KnowledgeSta, value);
	}
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(KnowledgeAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(KnowledgeAttr.OrgNo, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(KnowledgeAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(KnowledgeAttr.Rec, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(KnowledgeAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(KnowledgeAttr.RecName, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(KnowledgeAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(KnowledgeAttr.RDT, value);
	}

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
	 知识管理
	*/
	public Knowledge()
	{
	}
	public Knowledge(String mypk) throws Exception
	{
		this.setNo(mypk);
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

		Map map = new Map("OA_Knowledge", "知识管理");

		map.AddTBStringPK(KnowledgeAttr.No, null, "编号", false, false, 0, 50, 10);
		map.AddTBString(KnowledgeAttr.Name, null, "名称", true, true, 0, 500, 10);

		map.AddTBString(KnowledgeAttr.ImgUrl, null, "路径", true, true, 0, 500, 10);
		map.AddTBString(KnowledgeAttr.Title, null, "标题", true, true, 0, 4000, 10);
		map.AddTBString(KnowledgeAttr.Docs, null, "描述", true, true, 0, 4000, 10);
		//map.AddTBString(KnowledgeAttr.KnowledgeSta, null, "状态", true, true, 0, 4000, 10);

		map.AddDDLSysEnum(KnowledgeAttr.KnowledgeSta, 0, "状态", true, false, "KnowledgeSta", "@0=公开@1=私有");

		//zhoupeng@周朋;liping@李萍;
		map.AddTBString(KnowledgeAttr.Emps, null, "参与人", false, false, 0, 4000, 10);

		//,zhoupeng,liping,
		map.AddTBString(KnowledgeAttr.Foucs, null, "关注的人(多个人用逗号分开)", false, false, 0, 4000, 10);


		map.AddTBString(KnowledgeAttr.OrgNo, null, "组织编号", false, false, 0, 100, 10);
		map.AddTBString(KnowledgeAttr.Rec, null, "记录人", false, false, 0, 100, 10);
		map.AddTBString(KnowledgeAttr.RecName, null, "记录人名称", false, false, 0, 100, 10, true);
		map.AddTBDateTime(KnowledgeAttr.RDT, null, "记录时间", false, false);
//		map.AddMyFile("上传附件");

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
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.setOrgNo(WebUser.getOrgNo());
		}


			///#region 初始化目录数据
		//初始化目录数据.

		KMTree en = new KMTree();
		en.setNo(this.getNo());
		en.setName("根目录");
		en.setParentNo("0");
		en.setKnowledgeNo(this.getNo());
		en.DirectInsert();

		en = new KMTree();
		en.setName("目录1");
		en.setParentNo(this.getNo());
		en.setKnowledgeNo(this.getNo());
		en.Insert();

		KMDtl dtl = new KMDtl();
		dtl.setName("文件1");
		dtl.setRefTreeNo(en.getNo());
		dtl.setKnowledgeNo(this.getNo());
		dtl.Insert();

		dtl = new KMDtl();
		dtl.setName("文件2");
		dtl.setRefTreeNo(en.getNo());
		dtl.setKnowledgeNo(this.getNo());
		dtl.Insert();


		en = new KMTree();
		en.setName("目录2");
		en.setParentNo(this.getNo());
		en.setKnowledgeNo(this.getNo());
		en.Insert();

		dtl = new KMDtl();
		dtl.setName("文件1");
		dtl.setRefTreeNo(en.getNo());
		dtl.setKnowledgeNo(this.getNo());
		dtl.Insert();

		dtl = new KMDtl();
		dtl.setName("文件2");
		dtl.setRefTreeNo(en.getNo());
		dtl.setKnowledgeNo(this.getNo());
		dtl.Insert();

			///#endregion 初始化目录数据

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		////计算条数.
		//this.RefEmpsNo = DBAccess.RunSQLReturnValInt("SELECT COUNT(*) AS N FROM OA_KnowledgeDtl WHERE RefPK='" + this.MyPK + "'");

		////计算合计工作小时..
		//this.Manager = DBAccess.RunSQLReturnValInt("SELECT SUM(Hour) + Sum(Minute)/60.00 AS N FROM OA_KnowledgeDtl WHERE RefPK='" + this.MyPK + "'");

		return super.beforeUpdate();
	}

		///#endregion 执行方法.
}
