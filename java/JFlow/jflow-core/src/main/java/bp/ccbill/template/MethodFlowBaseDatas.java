package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 基础数据变更
*/
public class MethodFlowBaseDatas extends EntitiesNoName
{
	/** 
	 基础数据变更
	*/
	public MethodFlowBaseDatas()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodFlowBaseData();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodFlowBaseData> ToJavaList()
	{
		return (java.util.List<MethodFlowBaseData>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodFlowBaseData> Tolist()
	{
		ArrayList<MethodFlowBaseData> list = new ArrayList<MethodFlowBaseData>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodFlowBaseData)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
