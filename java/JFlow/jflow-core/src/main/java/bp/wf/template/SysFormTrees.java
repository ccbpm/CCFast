package bp.wf.template;

import bp.da.*;
import bp.en.*;
import bp.sys.*;
import bp.difference.*;
import java.util.*;

/** 
 独立表单树
*/
public class SysFormTrees extends EntitiesTree
{
	/** 
	 独立表单树s
	*/
	public SysFormTrees()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SysFormTree();
	}
	/** 
	 初始化数据.
	*/
	private void InitData() throws Exception {
		SysFormTree fs = new SysFormTree();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			fs.setName("流程树");
			fs.setNo(bp.web.WebUser.getOrgNo());
			fs.setParentNo("100");
			fs.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
			fs.Save();

			fs = new SysFormTree();
			fs.setName("公文类");
			fs.setNo(DBAccess.GenerGUID(0, null, null));
			fs.setParentNo(bp.web.WebUser.getOrgNo());
			fs.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
			fs.Insert();

			fs = new SysFormTree();
			fs.setName("办公类");
			fs.setNo(DBAccess.GenerGUID(0, null, null));
			fs.setParentNo(bp.web.WebUser.getOrgNo());
			fs.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
			fs.Insert();
			return;
		}

		fs = new SysFormTree();
		fs.setName("表单树");
		fs.setNo("100");
		fs.setParentNo("0");
		fs.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
		fs.Insert();

		fs = new SysFormTree();
		fs.setName("办公类");
		fs.setNo("01");
		fs.SetValByKey("Idx", 1);
		fs.setParentNo("100");
		fs.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
		fs.Insert();

		fs = new SysFormTree();
		fs.setName("公文类");
		fs.setNo("02");
		fs.SetValByKey("Idx", 2);
		fs.setParentNo("100");
		fs.SetValByKey("OrgNo", bp.web.WebUser.getOrgNo());
		fs.Insert();

	}
	/** 
	 查询全部.
	 
	 @return 
	*/
	@Override
	public int RetrieveAll() throws Exception {
		//if (bp.web.WebUser.getNo().equals("admin") == true)
		//    return this.RetrieveAll(FlowSortAttr.Idx);

		int num = 0;
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			num = this.Retrieve(FlowSortAttr.OrgNo, bp.web.WebUser.getOrgNo(), FlowSortAttr.Idx);
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			num = this.Retrieve(FlowSortAttr.Idx);
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			num = this.Retrieve(FlowSortAttr.OrgNo, bp.web.WebUser.getOrgNo(), FlowSortAttr.Idx);
		}

		if (num == 0)
		{
			InitData();
			return this.RetrieveAll(FlowSortAttr.Idx);
		}

		return num;
	}



		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SysFormTree> ToJavaList()
	{
		return (java.util.List<SysFormTree>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SysFormTree> Tolist()
	{
		ArrayList<SysFormTree> list = new ArrayList<SysFormTree>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SysFormTree)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
