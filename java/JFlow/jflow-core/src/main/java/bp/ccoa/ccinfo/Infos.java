package bp.ccoa.ccinfo;
import bp.en.*;
import java.util.*;

/** 
 信息 s
*/
public class Infos extends EntitiesNoName
{

		///#region 构造函数.
	/** 
	 信息
	*/
	public Infos()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Info();
	}

		///#endregion 构造函数.



		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Info> ToJavaList()
	{
		return (java.util.List<Info>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Info> Tolist()
	{
		ArrayList<Info> list = new ArrayList<Info>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Info)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
