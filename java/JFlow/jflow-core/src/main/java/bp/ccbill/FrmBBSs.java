package bp.ccbill;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import java.util.*;

/** 
 评论组件集合s
*/
public class FrmBBSs extends EntitiesNoName
{

		///#region 构造方法.
	/** 
	 评论组件集合
	*/
	public FrmBBSs()
	{
	}
	@Override
	public Entity getNewEntity()
	{
		return new FrmBBS();
	}

		///#endregion 构造方法.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmBBS> ToJavaList()
	{
		return (java.util.List<FrmBBS>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmBBS> Tolist()
	{
		ArrayList<FrmBBS> list = new ArrayList<FrmBBS>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmBBS)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
