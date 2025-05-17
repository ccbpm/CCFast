package bp.wf.template;

import bp.da.*;
import bp.en.*;
import bp.sys.*;
import bp.difference.*;
import bp.web.*;
import java.util.*;

/** 
 流程类别
*/
public class FlowSorts extends EntitiesTree
{
	/** 
	 流程类别s
	*/
	public FlowSorts()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FlowSort();
	}
	/** 
	 初始化数据.
	*/
	private void InitData() throws Exception {
		FlowSort fs = new FlowSort();
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			fs.setName("流程树");
			fs.setNo(WebUser.getOrgNo());
			fs.setParentNo("100");
			fs.SetValByKey("OrgNo", WebUser.getOrgNo());
			fs.Save();

			fs = new FlowSort();
			fs.setName("公文类");
			fs.setNo(DBAccess.GenerGUID(0, null, null));
			fs.setParentNo(WebUser.getOrgNo());
			fs.SetValByKey("OrgNo", WebUser.getOrgNo());
			fs.Insert();

			fs = new FlowSort();
			fs.setName("办公类");
			fs.setNo(DBAccess.GenerGUID(0, null, null));
			fs.setParentNo(WebUser.getOrgNo());
			fs.SetValByKey("OrgNo", WebUser.getOrgNo());
			fs.Insert();
			return;
		}

		fs = new FlowSort();
		fs.setName("流程树");
		fs.setNo("100");
		fs.setParentNo("0");
		fs.SetValByKey("OrgNo", WebUser.getOrgNo());
		fs.Insert();

		fs = new FlowSort();
		fs.setName("办公类");
		fs.setNo("01");
		fs.SetValByKey("Idx", 1);
		fs.setParentNo("100");
		fs.SetValByKey("OrgNo", WebUser.getOrgNo());
		fs.Insert();

		fs = new FlowSort();
		fs.setName("公文类");
		fs.setNo("02");
		fs.SetValByKey("Idx", 2);
		fs.setParentNo("100");
		fs.SetValByKey("OrgNo", WebUser.getOrgNo());
		fs.Insert();
	}
	/** 
	 查询全部.
	 
	 @return 
	*/
	@Override
	public int RetrieveAll() throws Exception {
		if (bp.web.WebUser.getNo().equals("admin") == true)
		    return this.RetrieveAll(FlowSortAttr.Idx);

		if (WebUser.getIsAdmin() == false)
		{
			throw new RuntimeException("err@您不是管理员.");
		}

		//if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		//{
		//    OrgAdminer oa = new OrgAdminer();
		//    oa.setMyPK(WebUser.getOrgNo() + "_" + WebUser.getUserID();
		// //   oa.ret
		//}


		int num = 0;
		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			num = this.Retrieve(FlowSortAttr.Idx);
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.GroupInc)
		{
			num = this.Retrieve(FlowSortAttr.OrgNo, WebUser.getOrgNo(), FlowSortAttr.Idx);
		}

		if (bp.wf.Glo.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			num = this.Retrieve(FlowSortAttr.OrgNo, WebUser.getOrgNo(), FlowSortAttr.Idx);
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
	public final java.util.List<FlowSort> ToJavaList()
	{
		return (java.util.List<FlowSort>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FlowSort> Tolist()
	{
		ArrayList<FlowSort> list = new ArrayList<FlowSort>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FlowSort)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
