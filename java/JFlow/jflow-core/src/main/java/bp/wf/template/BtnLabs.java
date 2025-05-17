package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点按钮权限s
*/
public class BtnLabs extends Entities
{
	/** 
	 节点按钮权限s
	*/
	public BtnLabs()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new BtnLab();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<BtnLab> ToJavaList()
	{
		return (java.util.List<BtnLab>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<BtnLab> Tolist()
	{
		ArrayList<BtnLab> list = new ArrayList<BtnLab>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((BtnLab)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
