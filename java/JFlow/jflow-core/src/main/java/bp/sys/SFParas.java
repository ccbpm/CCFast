package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 用户自定义表s
*/
public class SFParas extends EntitiesMyPK
{

		///#region 构造
	/** 
	 用户自定义表s
	*/
	public SFParas()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFPara();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFPara> ToJavaList()
	{
		return (java.util.List<SFPara>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFPara> Tolist()
	{
		ArrayList<SFPara> list = new ArrayList<SFPara>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFPara)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
