package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 框架s
*/
public class MapFrames extends EntitiesMyPK
{

		///#region 构造
	/** 
	 框架s
	*/
	public MapFrames()
	{
	}
	/** 
	 框架s
	 
	 @param fk_mapdata s
	*/
	public MapFrames(String fk_mapdata) throws Exception {
		this.Retrieve(MapFrameAttr.FK_MapData, fk_mapdata);
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapFrame();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapFrame> ToJavaList()
	{
		return (java.util.List<MapFrame>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapFrame> Tolist()
	{
		ArrayList<MapFrame> list = new ArrayList<MapFrame>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapFrame)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
