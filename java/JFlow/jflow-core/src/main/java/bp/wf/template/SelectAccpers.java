package bp.wf.template;

import bp.da.*;
import bp.en.*;
import bp.port.*;
import bp.wf.*;
import java.util.*;

/** 
 选择接受人
*/
public class SelectAccpers extends EntitiesMyPK
{
	/** 
	 是否记忆下次选择
	*/
	public final boolean getItIsSetNextTime() throws Exception {
		if (this.size() == 0)
		{
			return false;
		}

		for (SelectAccper item : this.ToJavaList())
		{
			if (item.getItIsRemember() == true)
			{
				return item.getItIsRemember();
			}
		}
		return false;
	}
	/** 
	 查询接收人,如果没有设置就查询历史记录设置的接收人.
	 
	 @param fk_node
	 @param rec
	 @return 
	*/
	public final int QueryAccepter(int fk_node, String rec, long workid) throws Exception {
		//查询出来当前的数据.
		int i = this.Retrieve(SelectAccperAttr.FK_Node, fk_node, SelectAccperAttr.WorkID, workid, null);
		if (i != 0)
		{
			return i; //如果没有就找最大的workid.
		}

		//找出最近的工作ID
		int maxWorkID = DBAccess.RunSQLReturnValInt("SELECT Max(WorkID) FROM WF_SelectAccper WHERE Rec='" + rec + "' AND FK_Node=" + fk_node, 0);
		if (maxWorkID == 0)
		{
			return 0;
		}

		//查询出来该数据.
		this.Retrieve(SelectAccperAttr.FK_Node, fk_node, SelectAccperAttr.WorkID, maxWorkID, null);

		//返回查询结果.
		return this.size();
	}
	/** 
	 查询上次的设置
	 @param fk_node 节点编号
	 @return 
	*/
	public final int QueryAccepterPriSetting(int fk_node) throws Exception {
		//找出最近的工作ID.
		int maxWorkID = DBAccess.RunSQLReturnValInt("SELECT Max(WorkID) FROM WF_SelectAccper WHERE " + SelectAccperAttr.IsRemember + "=1 AND Rec='" + bp.web.WebUser.getNo() + "' AND FK_Node=" + fk_node, 0);
		if (maxWorkID == 0)
		{
			return 0;
		}

		//查询出来该数据.
		this.Retrieve(SelectAccperAttr.FK_Node, fk_node, SelectAccperAttr.WorkID, maxWorkID, null);

		//返回查询结果.
		return this.size();
	}
	/** 
	 他的到人员
	*/
	public final Emps getHisEmps() throws Exception {
		Emps ens = new Emps();
		for (SelectAccper ns : this.ToJavaList())
		{
			ens.AddEntity(new Emp(ns.getEmpNo()));
		}
		return ens;
	}
	/** 
	 他的工作节点
	*/
	public final Nodes getHisNodes() throws Exception {
		Nodes ens = new Nodes();
		for (SelectAccper ns : this.ToJavaList())
		{
			ens.AddEntity(new Node(ns.getNodeID()));
		}
		return ens;
	}
	/** 
	 选择接受人
	*/
	public SelectAccpers()
	{
	}
	/** 
	 查询出来选择的人员
	 @param workid
	*/
	public SelectAccpers(long workid) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(SelectAccperAttr.WorkID, workid);
		qo.addOrderByDesc(SelectAccperAttr.FK_Node,SelectAccperAttr.Idx);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SelectAccper();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SelectAccper> ToJavaList()
	{
		return (java.util.List<SelectAccper>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SelectAccper> Tolist()
	{
		ArrayList<SelectAccper> list = new ArrayList<SelectAccper>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SelectAccper)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
