package bp.sys;
import bp.en.*;
import java.util.*;

/** 
 表单模板版本管理s
*/
public class MapDataVers extends EntitiesMyPK
{

		///#region 构造
	/** 
	 模板版本管理s
	*/
	public MapDataVers()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapDataVer();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapDataVer> ToJavaList()
	{
		return (java.util.List<MapDataVer>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapDataVer> Tolist()
	{
		ArrayList<MapDataVer> list = new ArrayList<MapDataVer>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapDataVer)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
