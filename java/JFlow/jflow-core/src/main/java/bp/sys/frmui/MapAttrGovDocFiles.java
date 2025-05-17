package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 公文正文组件s
*/
public class MapAttrGovDocFiles extends EntitiesMyPK
{

		///#region 构造
	/** 
	 公文正文组件s
	*/
	public MapAttrGovDocFiles()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapAttrGovDocFile();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapAttrGovDocFile> ToJavaList()
	{
		return (java.util.List<MapAttrGovDocFile>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapAttrGovDocFile> Tolist()
	{
		ArrayList<MapAttrGovDocFile> list = new ArrayList<MapAttrGovDocFile>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapAttrGovDocFile)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
