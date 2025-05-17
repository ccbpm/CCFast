package bp.wf;

import bp.sys.*;
import bp.en.*;
import bp.wf.template.*;
import bp.web.*;

import java.util.*;

/** 
 流程集合
*/
public class Flows extends EntitiesNoName
{


		///#region 查询
	/** 
	 查出来全部的自动流程
	*/
	public final void RetrieveIsAutoWorkFlow() throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FlowAttr.FlowType, 1);
		qo.addOrderBy(FlowAttr.No);
		qo.DoQuery();
	}
	/**
	 * 查询出来全部的在生存期间内的流程
	 *
	 * @param flowSort           流程类别
	 * @return
	 */
	public final int Retrieve(String flowSort) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FlowAttr.FK_FlowSort, flowSort);
		qo.addOrderBy(FlowAttr.No);
		qo.DoQuery();
		return 0;
	}
	@Override
	public int RetrieveAll() throws Exception {
		if (bp.wf.Glo.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			return this.Retrieve(FlowSortAttr.OrgNo, WebUser.getOrgNo(), FlowSortAttr.Idx);
		}
		int i = super.RetrieveAll(FlowSortAttr.Idx);
		return i;
	}

		///#endregion


		///#region 构造方法
	/** 
	 工作流程
	*/
	public Flows()
	{
	}
	/** 
	 工作流程
	 
	 @param fk_sort
	*/
	public Flows(String fk_sort) throws Exception {
		this.Retrieve(FlowAttr.FK_FlowSort, fk_sort, null);
	}

		///#endregion


		///#region 得到实体
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity(){
		return new Flow();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Flow> ToJavaList()
	{
		return (java.util.List<Flow>)(Object)this;
	}
	/** 
	 转化成 list
	 
	 @return List
	*/
	public final ArrayList<Flow> Tolist()
	{
		ArrayList<Flow> list = new ArrayList<Flow>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Flow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
