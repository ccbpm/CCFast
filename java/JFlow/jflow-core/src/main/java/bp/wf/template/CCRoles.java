package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 抄送s
*/
public class CCRoles extends EntitiesMyPK
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CCRole();
	}
	/** 
	 抄送
	*/
	public CCRoles()
	{
	}
	public CCRoles(int nodeID) throws Exception {
		this.Retrieve(NodeAttr.NodeID, nodeID, NodeAttr.Step);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CCRole> ToJavaList()
	{
		return (java.util.List<CCRole>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CCRole> Tolist()
	{
		ArrayList<CCRole> list = new ArrayList<CCRole>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CCRole)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
