package bp.sys.frmui;
import bp.en.*; import bp.en.Map;
import java.util.*;

/** 
 地图s
*/
public class ExtMaps extends EntitiesMyPK
{

		///#region 构造
	/** 
	 地图s
	*/
	public ExtMaps()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ExtMap();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ExtMap> ToJavaList()
	{
		return (java.util.List<ExtMap>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ExtMap> Tolist()
	{
		ArrayList<ExtMap> list = new ArrayList<ExtMap>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ExtMap)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
