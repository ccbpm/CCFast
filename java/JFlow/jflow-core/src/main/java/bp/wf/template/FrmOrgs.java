package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 表单对应组织
*/
public class FrmOrgs extends EntitiesMyPK
{
	/** 
	 表单对应组织
	*/
	public FrmOrgs()
	{
	}
	/** 
	 表单对应组织
	 @param orgNo orgNo
	*/
	public FrmOrgs(String orgNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(FrmOrgAttr.OrgNo, orgNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmOrg();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmOrg> ToJavaList()
	{
		return (java.util.List<FrmOrg>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmOrg> Tolist()
	{
		ArrayList<FrmOrg> list = new ArrayList<FrmOrg>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmOrg)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
