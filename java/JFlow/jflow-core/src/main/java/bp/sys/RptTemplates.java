package bp.sys;
import bp.en.*;
import java.util.*;

/** 
 实体集合
*/
public class RptTemplates extends Entities
{

		///#region 构造
	public RptTemplates()
	{
	}

	/** 
	 查询
	 
	 @param EnsName
	*/
	public RptTemplates(String EnsName) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(RptTemplateAttr.EnsName, EnsName);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new RptTemplate();
	}


		///#endregion


		///#region 查询方法


		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<RptTemplate> ToJavaList()
	{
		return (java.util.List<RptTemplate>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<RptTemplate> Tolist()
	{
		ArrayList<RptTemplate> list = new ArrayList<RptTemplate>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((RptTemplate)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
