package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 流程集合
*/
public class FlowSheets extends EntitiesNoName
{

		///#region 查询
	/** 
	 查询出来全部的在生存期间内的流程
	 
	 @param FlowSort 流程类别
	*/
	public final int Retrieve(String FlowSort) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(bp.wf.template.FlowAttr.FK_FlowSort, FlowSort);
		qo.addOrderBy(bp.wf.template.FlowAttr.No);
		qo.DoQuery();
		return this.size();
	}

		///#endregion


		///#region 构造方法
	/** 
	 工作流程
	*/
	public FlowSheets()
	{
	}
	/** 
	 工作流程
	 
	 @param fk_sort
	*/
	public FlowSheets(String fk_sort) throws Exception {
		this.Retrieve(bp.wf.template.FlowAttr.FK_FlowSort, fk_sort, null);
	}

		///#endregion


		///#region 得到实体
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FlowSheet();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FlowSheet> ToJavaList()
	{
		return (java.util.List<FlowSheet>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FlowSheet> Tolist()
	{
		ArrayList<FlowSheet> list = new ArrayList<FlowSheet>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FlowSheet)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
