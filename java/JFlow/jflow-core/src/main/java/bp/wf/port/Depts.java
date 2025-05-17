package bp.wf.port;

import bp.en.*;
import bp.sys.*;
import java.util.*;

/** 
部门集合(即将弃用)
*/
public class Depts extends EntitiesNoName
{
	/** 
	 查询全部。
	 
	 @return 
	*/
	@Override
	public int RetrieveAll() throws Exception {
		if (bp.web.WebUser.getNo().equals("admin") == true)
		{
			return super.RetrieveAll();
		}

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			QueryObject qo = new QueryObject(this);
			qo.AddWhere(DeptAttr.No, " = ", bp.web.WebUser.getDeptNo());
			qo.addOr();
			qo.AddWhere(DeptAttr.ParentNo, " = ", bp.web.WebUser.getDeptNo());
			return qo.DoQuery();
		}

		return this.Retrieve("OrgNo", bp.web.WebUser.getOrgNo(), null);
	}
	/** 
	 得到一个新实体
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Dept();
	}
	/** 
	 create ens
	*/
	public Depts()
	{
	}


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
