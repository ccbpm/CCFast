package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.web.*;
import bp.sys.*;
import bp.*;
import java.util.*;

/** 
部门s
*/
public class Depts extends EntitiesTree
{

		///#region 初始化实体.
	/** 
	 得到一个新实体
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Dept();
	}
	/** 
	 部门集合
	*/
	public Depts()
	{
	}
	/** 
	 部门集合
	 
	 @param parentNo 父部门No
	*/
	public Depts(String parentNo) throws Exception {
		this.Retrieve(DeptAttr.ParentNo, parentNo);
	}


		///#endregion 初始化实体.


		///#region 重写查询,add by zhoupeng 2015.09.30 为了适应能够从webservice数据源查询数据.
	@Override
	public int RetrieveAll() throws Exception {

		if (WebUser.getNo().equals("admin") == true)
		{
			QueryObject qo = new QueryObject(this);
			qo.addOrderBy(DeptAttr.Idx);
			return qo.DoQuery();
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			QueryObject qo = new QueryObject(this);
			qo.AddWhere(DeptAttr.No, " = ", WebUser.getDeptNo());
			qo.addOr();
			qo.AddWhere(DeptAttr.ParentNo, " = ", WebUser.getDeptNo());
			qo.addOrderBy(DeptAttr.Idx);
			return qo.DoQuery();
		}

		return this.Retrieve("OrgNo", WebUser.getOrgNo(), DeptAttr.Idx);
	}
	/** 
	 重写重数据源查询全部适应从WS取数据需要
	 
	 @return 
	*/
	@Override
	public int RetrieveAllFromDBSource() throws Exception {

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAllFromDBSource();
		}

		//按照orgNo查询.
		return this.Retrieve("OrgNo", WebUser.getOrgNo());
	}

		///#endregion 重写查询.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Dept> ToJavaList()
	{
		return (java.util.List<Dept>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Dept> Tolist()
	{
		ArrayList<Dept> list = new ArrayList<Dept>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Dept)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
