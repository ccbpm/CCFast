package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点集合
*/
public class TemplateNodes extends EntitiesOID
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new TemplateNode();
	}

		///#endregion


		///#region 构造方法
	/** 
	 节点集合
	*/
	public TemplateNodes()
	{
	}

	public TemplateNodes(String fk_flow) throws Exception {
		this.Retrieve(NodeAttr.FK_Flow, fk_flow, NodeAttr.Step);
	}
		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<TemplateNode> ToJavaList()
	{
		return (java.util.List<TemplateNode>)(Object)this;
	}
	/** 
	 转化成list 为了翻译成java的需要
	 
	 @return List
	*/
	public final ArrayList<TemplateNode> Tolist()
	{
		ArrayList<TemplateNode> list = new ArrayList<TemplateNode>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((TemplateNode)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
