package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 框架s
*/
public class MapFrameExts extends EntitiesMyPK
{

		///#region 构造
	/** 
	 框架s
	*/
	public MapFrameExts()
	{
	}
	/** 
	 框架s
	 
	 @param frmID 表单ID
	*/
	public MapFrameExts(String frmID) throws Exception {
		this.Retrieve(MapFrameAttr.FK_MapData, frmID);
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapFrameExt();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapFrameExt> ToJavaList()
	{
		return (java.util.List<MapFrameExt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapFrameExt> Tolist()
	{
		ArrayList<MapFrameExt> list = new ArrayList<MapFrameExt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapFrameExt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
