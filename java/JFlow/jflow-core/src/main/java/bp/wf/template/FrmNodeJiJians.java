package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点表单s
*/
public class FrmNodeJiJians extends EntitiesMyPK
{

		///#region 构造方法..
	/** 
	 节点表单
	*/
	public FrmNodeJiJians()
	{
	}

		///#endregion 构造方法..


		///#region 公共方法.
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmNodeJiJian();
	}

		///#endregion 公共方法.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmNodeJiJian> ToJavaList()
	{
		return (java.util.List<FrmNodeJiJian>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmNodeJiJian> Tolist()
	{
		ArrayList<FrmNodeJiJian> list = new ArrayList<FrmNodeJiJian>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmNodeJiJian)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
