package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 权限模型
*/
public class PowerModels extends EntitiesMyPK
{
	/** 
	 权限模型
	*/
	public PowerModels()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new PowerModel();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<PowerModel> ToJavaList()
	{
		return (java.util.List<PowerModel>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<PowerModel> Tolist()
	{
		ArrayList<PowerModel> list = new ArrayList<PowerModel>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((PowerModel)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
