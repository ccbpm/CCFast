package bp.wf.data;
import bp.en.*;
import java.util.*;

/** 
 我授权的流程s
*/
public class MyAuthtos extends Entities
{

		///#region 方法.
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MyAuthto();
	}
	/** 
	 我授权的流程集合
	*/
	public MyAuthtos()throws Exception
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MyAuthto> ToJavaList()throws Exception
	{
		return (java.util.List<MyAuthto>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MyAuthto> Tolist()throws Exception
	{
		ArrayList<MyAuthto> list = new ArrayList<MyAuthto>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MyAuthto)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}