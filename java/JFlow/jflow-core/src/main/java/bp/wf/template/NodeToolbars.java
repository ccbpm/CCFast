package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 工具栏集合
*/
public class NodeToolbars extends EntitiesOID
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new NodeToolbar();
	}

		///#endregion


		///#region 构造方法
	/** 
	 工具栏集合
	*/
	public NodeToolbars()
	{
	}
	/** 
	 工具栏集合.
	 
	 @param fk_node
	*/
	public NodeToolbars(String fk_node) throws Exception {
		this.Retrieve(NodeToolbarAttr.FK_Node, fk_node, null);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeToolbar> ToJavaList()
	{
		return (java.util.List<NodeToolbar>)(Object)this;
	}

	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeToolbar> Tolist()
	{
		ArrayList<NodeToolbar> list = new ArrayList<NodeToolbar>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeToolbar)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
