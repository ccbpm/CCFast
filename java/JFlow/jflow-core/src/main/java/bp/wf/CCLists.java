package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 抄送
*/
public class CCLists extends EntitiesMyPK
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CCList();
	}
	/** 
	 抄送
	*/
	public CCLists()
	{
	}


	/** 
	 查询出来所有的抄送信息
	 
	 @param NodeID
	 @param workid
	 @param fid
	*/
	public CCLists(int NodeID, long workid, long fid) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(CCListAttr.NodeIDWork, NodeID);
		qo.addAnd();
		if (fid != 0)
		{
			qo.AddWhereIn(CCListAttr.WorkID, "(" + workid + "," + fid + ")");
		}
		else
		{
			qo.AddWhere(CCListAttr.WorkID, workid);
		}
		qo.DoQuery();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CCList> ToJavaList()
	{
		return (java.util.List<CCList>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CCList> Tolist()
	{
		ArrayList<CCList> list = new ArrayList<CCList>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CCList)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
