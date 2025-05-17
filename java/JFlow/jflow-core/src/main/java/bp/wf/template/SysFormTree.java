package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.difference.*;
import bp.wf.port.admin2group.*;


/** 
  独立表单树
*/
public class SysFormTree extends EntityTree
{

		///#region 属性.
	/** 
	 是否是目录
	*/
	public final boolean getItIsDir()  {
		return this.GetValBooleanByKey(SysFormTreeAttr.IsDir);
	}
	public final void setItIsDir(boolean value){
		this.SetValByKey(SysFormTreeAttr.IsDir, value);
	}
	/** 
	 序号
	*/
	public final int getIdx()  {
		return this.GetValIntByKey(SysFormTreeAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(SysFormTreeAttr.Idx, value);
	}
	/** 
	 父节点编号
	*/
	public final String getParentNo()  {
		return this.GetValStringByKey(SysFormTreeAttr.ParentNo);
	}
	public final void setParentNo(String value){
		this.SetValByKey(SysFormTreeAttr.ParentNo, value);
	}
	/** 
	 组织编号
	*/
	public final String getOrgNo()  {
		return this.GetValStringByKey(SysFormTreeAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(SysFormTreeAttr.OrgNo, value);
	}

		///#endregion 属性.


		///#region 构造方法
	/** 
	 独立表单树
	*/
	public SysFormTree()
	{
	}
	/** 
	 独立表单树
	 
	 @param _No
	*/
	public SysFormTree(String _No) throws Exception {
		super(_No);
	}

		///#endregion


		///#region 系统方法.
	/** 
	 独立表单树Map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("Sys_FormTree", "表单树");
		//map.setCodeStruct( "2";

		map.setDepositaryOfEntity(Depositary.None);
		map.setDepositaryOfMap(Depositary.Application);

		map.AddTBStringPK(SysFormTreeAttr.No, null, "编号", true, true, 1, 50, 40);
		map.AddTBString(SysFormTreeAttr.Name, null, "名称", true, false, 0, 100, 30);
		map.AddTBString(SysFormTreeAttr.ParentNo, null, "父节点编号", false, false, 0, 100, 40);
		map.AddTBInt(SysFormTreeAttr.Idx, 0, "Idx", false, false);
		map.AddTBString(SysFormTreeAttr.OrgNo, null, "组织编号", false, false, 0, 50, 30);

		this.set_enMap(map);
		return this.get_enMap();
	}
	public final String DoCreateSameLevelFormNodeMy(String name) throws Exception {
		EntityTree en = this.DoCreateSameLevelNode(name);
		en.setName(name);
		en.Update();
		return en.getNo();
	}
	/** 
	 创建下级目录.
	 
	 @param name
	 @return 
	*/
	public final String DoCreateSubFormNodeMy(String name) throws Exception {
		EntityTree en = this.DoCreateSubNode(name);
		en.setName(name);
		en.Update();
		return en.getNo();
	}

		///#endregion 系统方法.

	/** 
	 组织编号
	 
	 @return 
	*/
	@Override
	protected boolean beforeInsert() throws Exception
	{
		if (DataType.IsNullOrEmpty(this.getOrgNo()) == true && bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			this.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
		}

		if (DataType.IsNullOrEmpty(this.getNo()) == true)
		{
			this.setNo(String.valueOf(DBAccess.GenerOID(this.toString())));
		}
		return super.beforeInsert();
	}

	@Override
	protected boolean beforeDelete() throws Exception
	{
		String sql = "SELECT COUNT(*) as Num FROM Sys_MapData WHERE FK_FormTree='" + this.getNo() + "'";
		int num = DBAccess.RunSQLReturnValInt(sql);
		if (num != 0)
		{
			throw new RuntimeException("err@您不能删除该目录，下面有表单。");
		}


		if (!DataType.IsNullOrEmpty(this.getNo()))
		{
			DeleteChild(this.getNo());
		}

		return super.beforeDelete();
	}
	/** 
	 删除子项
	 
	 @param parentNo
	*/
	private void DeleteChild(String parentNo) throws Exception {
		SysFormTrees formTrees = new SysFormTrees();
		formTrees.RetrieveByAttr(SysFormTreeAttr.ParentNo, parentNo);
		for (SysFormTree item : formTrees.ToJavaList())
		{
			MapData md = new MapData();
			md.setFormTreeNo(item.getNo());
			md.Delete();
			DeleteChild(item.getNo());
		}
	}
	public final String DoCreateSameLevelNodeIt(String name) throws Exception {
		SysFormTree en = new SysFormTree();
		en.Copy(this);
		en.setNo(DBAccess.GenerGUID(10, null, null));
		en.setName(name);
		en.Insert();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc && SystemConfig.getGroupStationModel() == 2)
		{
			//如果当前人员不是部门主要管理员
			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org(bp.web.WebUser.getOrgNo());
			if (bp.web.WebUser.getNo().equals(org.getAdminer()) == false)
			{
				OAFrmTree oatree = new OAFrmTree();
				oatree.setEmpNo(bp.web.WebUser.getNo());
				oatree.setOrgNo(bp.web.WebUser.getOrgNo());
				oatree.SetValByKey("RefOrgAdminer", oatree.getOrgNo() + "_" + oatree.getEmpNo());
				oatree.SetValByKey("FrmTreeNo", en.getNo());
				oatree.Insert();
			}
		}
		return en.getNo();
	}
	public final String DoCreateSubNodeIt(String name) throws Exception {
		SysFormTree en = new SysFormTree();
		en.Copy(this);
		en.setNo(DBAccess.GenerGUID(10, null, null));
		en.setParentNo(this.getNo());
		en.setName(name);
		en.Insert();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc && SystemConfig.getGroupStationModel() == 2)
		{
			//如果当前人员不是部门主要管理员
			bp.wf.port.admingroup.Org org = new bp.wf.port.admingroup.Org(bp.web.WebUser.getOrgNo());
			if (bp.web.WebUser.getNo().equals(org.getAdminer()) == false)
			{
				OAFrmTree oatree = new OAFrmTree();
				oatree.setEmpNo(bp.web.WebUser.getNo());
				oatree.setOrgNo(bp.web.WebUser.getOrgNo());
				oatree.SetValByKey("RefOrgAdminer", oatree.getOrgNo() + "_" + oatree.getEmpNo());
				oatree.SetValByKey("FrmTreeNo", en.getNo());
				oatree.Insert();
			}
		}
		return en.getNo();
	}
	public final String DoUp() throws Exception {
		this.DoOrderUp(SysFormTreeAttr.ParentNo, this.getParentNo(), SysFormTreeAttr.Idx);
		return "执行成功";
	}
	public final String DoDown() throws Exception
	{
		this.DoOrderDown(SysFormTreeAttr.ParentNo, this.getParentNo(), SysFormTreeAttr.Idx);
		return "执行成功";
	}
}
