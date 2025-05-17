package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 实体其他业务流程
*/
public class MethodFlowEtcs extends EntitiesNoName
{
	/** 
	 实体其他业务流程
	*/
	public MethodFlowEtcs()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodFlowEtc();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodFlowEtc> ToJavaList()
	{
		return (java.util.List<MethodFlowEtc>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodFlowEtc> Tolist()
	{
		ArrayList<MethodFlowEtc> list = new ArrayList<MethodFlowEtc>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodFlowEtc)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
