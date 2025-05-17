package bp.sys;
import bp.en.*;
import java.util.*;

/** 
 部门角色对应 
*/
public class EnVerDtls extends EntitiesMyPK
{

		///#region 构造

	public EnVerDtls()
	{
	}

		///#endregion


		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new EnVerDtl();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<EnVerDtl> ToJavaList()
	{
		return (java.util.List<EnVerDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<EnVerDtl> Tolist()
	{
		ArrayList<EnVerDtl> list = new ArrayList<EnVerDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((EnVerDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
