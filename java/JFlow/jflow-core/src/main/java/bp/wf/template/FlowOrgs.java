package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 流程对应组织
*/
public class FlowOrgs extends EntitiesMyPK
{
	/** 
	 流程对应组织
	*/
	public FlowOrgs()
	{
	}
	/** 
	 流程对应组织
	 
	 @param orgNo orgNo 
	*/
	public FlowOrgs(String orgNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FlowOrgAttr.OrgNo, orgNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FlowOrg();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FlowOrg> ToJavaList()
	{
		return (java.util.List<FlowOrg>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FlowOrg> Tolist()
	{
		ArrayList<FlowOrg> list = new ArrayList<FlowOrg>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FlowOrg)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
