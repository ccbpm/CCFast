package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 实体集合
*/
public class GroupEnsTemplates extends EntitiesOID
{

		///#region 构造
	public GroupEnsTemplates()
	{
	}
	/** 
	 
	 
	 @param emp
	*/
	public GroupEnsTemplates(String emp) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(GroupEnsTemplateAttr.Rec, emp);
		qo.addOr();
		qo.AddWhere(GroupEnsTemplateAttr.Rec, "admin");
		qo.DoQuery();

	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GroupEnsTemplate();
	}


		///#endregion


		///#region 查询方法


		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GroupEnsTemplate> ToJavaList()
	{
		return (java.util.List<GroupEnsTemplate>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GroupEnsTemplate> Tolist()
	{
		ArrayList<GroupEnsTemplate> list = new ArrayList<GroupEnsTemplate>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GroupEnsTemplate)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
