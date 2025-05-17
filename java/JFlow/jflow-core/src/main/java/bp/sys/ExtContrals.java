package bp.sys;
import bp.en.*;
import java.util.*;

/** 
 扩展控件s
*/
public class ExtContrals extends EntitiesMyPK
{

		///#region 构造
	/** 
	 扩展控件s
	*/
	public ExtContrals()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ExtContral();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ExtContral> ToJavaList()
	{
		return (java.util.List<ExtContral>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ExtContral> Tolist()
	{
		ArrayList<ExtContral> list = new ArrayList<ExtContral>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ExtContral)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
