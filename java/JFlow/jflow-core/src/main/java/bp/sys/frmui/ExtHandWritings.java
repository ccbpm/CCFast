package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 手写签名版s
*/
public class ExtHandWritings extends EntitiesMyPK
{

		///#region 构造
	/** 
	 手写签名版s
	*/
	public ExtHandWritings()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ExtHandWriting();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ExtHandWriting> ToJavaList()
	{
		return (java.util.List<ExtHandWriting>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ExtHandWriting> Tolist()
	{
		ArrayList<ExtHandWriting> list = new ArrayList<ExtHandWriting>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ExtHandWriting)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
