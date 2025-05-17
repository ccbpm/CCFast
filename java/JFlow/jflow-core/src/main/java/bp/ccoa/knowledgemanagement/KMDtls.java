package bp.ccoa.knowledgemanagement;

import bp.web.*;
import bp.en.*;
import bp.sys.*;
import java.util.*;

/** 
 知识点 s
*/
public class KMDtls extends EntitiesNoName
{

		///#region 查询.
	/** 
	 所有的知识点
	 
	 @return 
	*/
	public final String KMDtl_AllKMDtls_del() throws Exception {
		QueryObject qo = new QueryObject(this);

		qo.addLeftBracket();
		qo.AddWhere(KMDtlAttr.Rec, WebUser.getNo());
		qo.addOr();
		qo.AddWhere(KMDtlAttr.RefTreeNo, " like ", "%," + WebUser.getNo() + ",%");
		qo.addRightBracket();

		if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
		{
			qo.addAnd();
			qo.AddWhere(KMDtlAttr.OrgNo, " = ", WebUser.getOrgNo());
		}
		qo.DoQuery();
		return this.ToJson("dt");
	}

		///#endregion 重写.


		///#region 重写.
	/** 
	 知识点
	*/
	public KMDtls()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new KMDtl();
	}

		///#endregion 重写.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<KMDtl> ToJavaList()
	{
		return (java.util.List<KMDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<KMDtl> Tolist()
	{
		ArrayList<KMDtl> list = new ArrayList<KMDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((KMDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
