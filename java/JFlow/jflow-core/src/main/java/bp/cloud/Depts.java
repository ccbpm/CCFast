package bp.cloud;

import bp.en.*;
import bp.web.*;
import java.util.*;

/** 
部门集合
*/
public class Depts extends EntitiesTree
{
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
	 @param parentNo 父部门No
	*/
	public Depts(String parentNo) throws Exception {
		this.Retrieve(DeptAttr.ParentNo, parentNo, null);
	}
	/** 
	 部门集合
	*/
	public Depts()
	{
	}
	@Override
	public int RetrieveAll() throws Exception {
		return this.Retrieve(EmpAttr.OrgNo, WebUser.getOrgNo(), null);
	}

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
}
