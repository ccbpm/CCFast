package bp.ccoa.knowledgemanagement;

import bp.da.*;
import bp.web.*;
import bp.en.*;
import bp.en.Map;
import bp.sys.*;

/** 
 知识树
*/
public class KMTree extends EntityTree
{

		///#region 基本属性
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStrByKey(KMTreeAttr.OrgNo);
	}
	public final void setOrgNo(String value)  {
		this.SetValByKey(KMTreeAttr.OrgNo, value);
	}
	public final String getRec()  {
		return this.GetValStrByKey(KMTreeAttr.Rec);
	}
	public final void setRec(String value)  {
		this.SetValByKey(KMTreeAttr.Rec, value);
	}
	public final String getRecName()  {
		return this.GetValStrByKey(KMTreeAttr.RecName);
	}
	public final void setRecName(String value)  {
		this.SetValByKey(KMTreeAttr.RecName, value);
	}
	public final String getRDT()  {
		return this.GetValStrByKey(KMTreeAttr.RDT);
	}
	public final void setRDT(String value)  {
		this.SetValByKey(KMTreeAttr.RDT, value);
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
	 知识树
	*/
	public KMTree()
	{
	}
	public KMTree(String mypk) throws Exception
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

		Map map = new Map("OA_KMTree", "知识树");

		map.AddTBStringPK(KMTreeAttr.No, null, "编号", false, false, 0, 50, 10);
		map.AddTBString(KMTreeAttr.Name, null, "名称", false, false, 0, 500, 10);
		map.AddTBString(KMTreeAttr.ParentNo, null, "父节点编号", false, false, 0, 50, 10);


		map.AddTBString(KMTreeAttr.KnowledgeNo, null, "知识编号", false, false, 0, 50, 10);
		map.AddTBInt(KMTreeAttr.FileType, 1, "文件类型", false, false);
		map.AddTBInt(KMTreeAttr.Idx, 0, "Idx", false, false);


		map.AddTBString(KMTreeAttr.OrgNo, null, "组织编号", false, false, 0, 100, 10);
		map.AddTBString(KMTreeAttr.Rec, null, "记录人", false, false, 0, 100, 10);
		map.AddTBString(KMTreeAttr.RecName, null, "记录人名称", false, false, 0, 100, 10, true);
		map.AddTBDateTime(KMTreeAttr.RDT, null, "记录时间", false, false);
		map.AddTBInt(KMTreeAttr.IsDel, 0, "IsDel", false, false);

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

		return super.beforeInsert();
	}
	@Override
	protected boolean beforeUpdate() throws Exception
	{
		return super.beforeUpdate();
	}

		///#endregion 执行方法.
}
