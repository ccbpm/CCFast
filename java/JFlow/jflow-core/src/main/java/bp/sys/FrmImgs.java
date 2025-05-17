package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 图片s
*/
public class FrmImgs extends EntitiesMyPK
{

		///#region 构造
	/** 
	 图片s
	*/
	public FrmImgs()
	{
	}
	/** 
	 图片s
	 
	 @param fk_mapdata s
	*/
	public FrmImgs(String fk_mapdata) throws Exception {

	   this.Retrieve(MapAttrAttr.FK_MapData, fk_mapdata);

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
	public final java.util.List<FrmImg> ToJavaList()
	{
		return (java.util.List<FrmImg>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmImg> Tolist()
	{
		ArrayList<FrmImg> list = new ArrayList<FrmImg>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmImg)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
