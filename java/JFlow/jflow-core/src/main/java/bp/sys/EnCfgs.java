package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 实体集合
*/
public class EnCfgs extends EntitiesNoName
{

		///#region 构造
	/** 
	 配置信息
	*/
	public EnCfgs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new EnCfg();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<EnCfg> ToJavaList()
	{
		return (java.util.List<EnCfg>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<EnCfg> Tolist()
	{
		ArrayList<EnCfg> list = new ArrayList<EnCfg>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((EnCfg)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
