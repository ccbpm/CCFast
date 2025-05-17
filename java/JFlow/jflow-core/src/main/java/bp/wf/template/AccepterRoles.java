package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 接受人规则集合
*/
public class AccepterRoles extends EntitiesOID
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new AccepterRole();
	}

		///#endregion


		///#region 构造方法
	/** 
	 接受人规则集合
	*/
	public AccepterRoles()
	{
	}
	/** 
	 接受人规则集合.
	 @param FK_Node
	*/
	public AccepterRoles(String FK_Node) throws Exception {
		this.Retrieve(AccepterRoleAttr.FK_Node, FK_Node, null);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<AccepterRole> ToJavaList()
	{
		return (java.util.List<AccepterRole>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<AccepterRole> Tolist()
	{
		ArrayList<AccepterRole> list = new ArrayList<AccepterRole>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((AccepterRole)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
