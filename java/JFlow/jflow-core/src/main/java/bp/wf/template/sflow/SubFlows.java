package bp.wf.template.sflow;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 子流程集合
*/
public class SubFlows extends EntitiesMyPK
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SubFlow();
	}

		///#endregion


		///#region 构造方法
	/** 
	 子流程集合
	*/
	public SubFlows()
	{
	}
	 /** 
	 子流程集合.
	 
	 @param fk_node 节点
	 */
	public SubFlows(int fk_node) throws Exception {
		this.Retrieve(SubFlowYanXuAttr.FK_Node, fk_node, null);
	}

	/** 
	 根据主流程编号获取该流程启动的子流程数据
	 
	 @param fk_flow
	*/
	public SubFlows(String fk_flow) throws Exception {
		this.Retrieve(SubFlowYanXuAttr.FK_Flow, fk_flow, null);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SubFlow> ToJavaList()
	{
		return (java.util.List<SubFlow>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SubFlow> Tolist()
	{
		ArrayList<SubFlow> list = new ArrayList<SubFlow>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SubFlow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
