package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 独立表单树
*/
public class FlowFormTrees extends EntitiesTree
{
	/** 
	 独立表单树s
	*/
	public FlowFormTrees()
	{
	}
	/** 
	 独立表单树
	*/
	public FlowFormTrees(String flowNo) throws Exception {
	   int i = this.Retrieve(FlowFormTreeAttr.FK_Flow, flowNo, null);
	   if (i == 0)
	   {
		   FlowFormTree tree = new FlowFormTree();
		   tree.setNo("100");
		   tree.setFlowNo(flowNo);
		   tree.setName("根目录");
		  // tree.IsDir = false;
		   tree.setParentNo("0");
		   tree.Insert();

		   //创建一个节点.
		   tree.DoCreateSubNode(null);
	   }
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FlowFormTree();
	}



		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FlowFormTree> ToJavaList()
	{
		return (java.util.List<FlowFormTree>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FlowFormTree> Tolist()
	{
		ArrayList<FlowFormTree> list = new ArrayList<FlowFormTree>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FlowFormTree)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
