package bp.wf.data;

import bp.en.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/** 
 流程
*/
public class FlowSimples extends EntitiesNoName
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()  {
		return new FlowSimple();
	}
	/** 
	 流程
	*/
	public FlowSimples()  {
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FlowSimple> ToJavaList() {
		return (java.util.List<FlowSimple>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FlowSimple> Tolist()  {
		ArrayList<FlowSimple> list = new ArrayList<FlowSimple>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FlowSimple)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}