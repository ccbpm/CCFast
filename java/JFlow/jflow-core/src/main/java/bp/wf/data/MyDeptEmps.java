package bp.wf.data;

import bp.en.*;
import java.util.*;

/** 
 报表集合
*/
public class MyDeptEmps extends bp.en.EntitiesNoName
{
	/** 
	 报表集合
	*/
	public MyDeptEmps()
	{
	}

	@Override
	public Entity getNewEntity()
	{
		return new MyDeptEmp();
	}
	@Override
	public int RetrieveAll() throws Exception 
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(MyDeptEmpAttr.FK_Dept, bp.web.WebUser.getDeptNo());
		return qo.DoQuery();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MyDeptEmp> ToJavaList()
	{
		return (java.util.List<MyDeptEmp>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MyDeptEmp> Tolist()
	{
		ArrayList<MyDeptEmp> list = new ArrayList<>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MyDeptEmp)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}