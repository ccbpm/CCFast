package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点集合
*/
public class NodeSheets extends Entities
{

		///#region 构造方法
	/** 
	 节点集合
	*/
	public NodeSheets()
	{
	}

		///#endregion

	@Override
	public Entity getNewEntity()
	{
		return new NodeSheet();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<NodeSheet> ToJavaList()
	{
		return (java.util.List<NodeSheet>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<NodeSheet> Tolist()
	{
		ArrayList<NodeSheet> list = new ArrayList<NodeSheet>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((NodeSheet)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
