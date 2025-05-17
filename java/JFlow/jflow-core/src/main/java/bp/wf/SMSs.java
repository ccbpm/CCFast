package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 消息s
*/
public class SMSs extends Entities
{
	/** 
	 获得实体
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SMS();
	}
	public SMSs()
	{
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SMS> ToJavaList()
	{
		return (java.util.List<SMS>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SMS> Tolist()
	{
		ArrayList<SMS> list = new ArrayList<SMS>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SMS)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
