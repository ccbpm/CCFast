package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 序列号s
*/
public class Serials extends Entities
{
	/** 
	 序列号s
	*/
	public Serials()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Serial();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Serial> ToJavaList()
	{
		return (java.util.List<Serial>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Serial> Tolist()
	{
		ArrayList<Serial> list = new ArrayList<Serial>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Serial)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
