package bp.wf.data;

import bp.en.*;
import bp.wf.*;
import java.util.*;

/** 
 抄送
*/
public class CCListExts extends EntitiesMyPK
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CCListExt();
	}
	/** 
	 抄送
	*/
	public CCListExts()throws Exception
	{
	}


	/** 
	 查询出来所有的抄送信息
	 
	 param fk_node
	 param workid
	 param fid
	*/
	public CCListExts(int fk_node, long workid, long fid) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(CCListAttr.NodeIDWork, fk_node);
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
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CCListExt> ToJavaList()throws Exception
	{
		return (java.util.List<CCListExt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CCListExt> Tolist()throws Exception
	{
		ArrayList<CCListExt> list = new ArrayList<CCListExt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CCListExt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}