package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 实体方法
*/
public class Methods extends EntitiesNoName
{
	/** 
	 实体方法
	*/
	public Methods()
	{
	}
	/** 
	 实体方法
	 
	 @param nodeid 方法IDID
	*/
	public Methods(int nodeid) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(MethodAttr.MethodID, nodeid);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Method();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Method> ToJavaList()
	{
		return (java.util.List<Method>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Method> Tolist()
	{
		ArrayList<Method> list = new ArrayList<Method>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Method)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
