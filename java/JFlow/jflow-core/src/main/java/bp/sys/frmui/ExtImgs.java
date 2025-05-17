package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 装饰图片s
*/
public class ExtImgs extends EntitiesMyPK
{

		///#region 构造
	/** 
	 装饰图片s
	*/
	public ExtImgs()
	{
	}
	/** 
	 装饰图片s
	 
	 @param fk_mapdata s
	*/
	public ExtImgs(String fk_mapdata) throws Exception {
		if (bp.difference.SystemConfig.isDebug())
		{
			this.Retrieve(MapAttrAttr.FK_MapData, fk_mapdata);
		}
		else
		{
			this.RetrieveFromCache(MapAttrAttr.FK_MapData, (Object)fk_mapdata);
		}
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmImg();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ExtImg> ToJavaList()
	{
		return (java.util.List<ExtImg>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ExtImg> Tolist()
	{
		ArrayList<ExtImg> list = new ArrayList<ExtImg>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ExtImg)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
