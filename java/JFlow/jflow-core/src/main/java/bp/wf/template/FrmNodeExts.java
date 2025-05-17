package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点表单s
*/
public class FrmNodeExts extends EntitiesMyPK
{

		///#region 构造方法..
	/** 
	 节点表单
	*/
	public FrmNodeExts()
	{
	}
	/** 
	 节点表单
	 
	 @param NodeID 节点ID
	*/
	public FrmNodeExts(String fk_flow, int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmNodeAttr.FK_Flow, fk_flow);
		qo.addAnd();
		qo.AddWhere(FrmNodeAttr.FK_Node, nodeID);

		qo.addOrderBy(FrmNodeAttr.Idx);
		qo.DoQuery();
	}

		///#endregion 构造方法..


		///#region 公共方法.
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmNodeExt();
	}

		///#endregion 公共方法.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmNodeExt> ToJavaList()
	{
		return (java.util.List<FrmNodeExt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmNodeExt> Tolist()
	{
		ArrayList<FrmNodeExt> list = new ArrayList<FrmNodeExt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmNodeExt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
