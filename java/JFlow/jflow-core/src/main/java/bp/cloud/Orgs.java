package bp.cloud;
import bp.en.*;
import java.util.*;

/** 
 组织s
*/
// </summary>
public class Orgs extends EntitiesNoName
{

		///#region 构造方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Org();
	}
	/** 
	 组织s
	*/
	public Orgs()
	{
	}

		///#endregion 构造方法


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Org> ToJavaList()
	{
		return (java.util.List<Org>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Org> Tolist()
	{
		ArrayList<Org> list = new ArrayList<Org>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Org)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
