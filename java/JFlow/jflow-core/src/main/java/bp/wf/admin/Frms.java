package bp.wf.admin;

import bp.en.*;
import java.util.*;

/** 
 流程集合
*/
public class Frms extends EntitiesNoName
{


		///#region 构造方法
	/** 
	 工作流程
	*/
	public Frms()
	{
	}

		///#endregion


		///#region 得到实体
	/**
	 * 得到它的 Entity
	 */
	public Entity getNewEntity()
	{
		return new Frm();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Frm> ToJavaList()
	{
		return (java.util.List<Frm>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Frm> Tolist()
	{
		ArrayList<Frm> list = new ArrayList<Frm>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Frm)this.get(i));
		}
		return list;
	}
		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
