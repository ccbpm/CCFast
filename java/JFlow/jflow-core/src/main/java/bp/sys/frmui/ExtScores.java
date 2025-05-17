package bp.sys.frmui;

import bp.en.*; import bp.en.Map;
import java.util.*;

/** 
 评分控件s
*/
public class ExtScores extends EntitiesMyPK
{

		///#region 构造
	/** 
	 评分控件s
	*/
	public ExtScores()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ExtLink();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ExtLink> ToJavaList()
	{
		return (java.util.List<ExtLink>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ExtLink> Tolist()
	{
		ArrayList<ExtLink> list = new ArrayList<ExtLink>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ExtLink)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
