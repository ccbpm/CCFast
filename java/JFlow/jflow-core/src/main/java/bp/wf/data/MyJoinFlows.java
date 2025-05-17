package bp.wf.data;

import bp.en.*;
import java.util.*;

/** 
 我参与的流程s
*/
public class MyJoinFlows extends Entities
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MyJoinFlow();
	}
	/** 
	 我参与的流程集合
	*/
	public MyJoinFlows()throws Exception
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MyJoinFlow> ToJavaList()throws Exception
	{
		return (java.util.List<MyJoinFlow>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MyJoinFlow> Tolist()throws Exception
	{
		ArrayList<MyJoinFlow> list = new ArrayList<>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MyJoinFlow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}