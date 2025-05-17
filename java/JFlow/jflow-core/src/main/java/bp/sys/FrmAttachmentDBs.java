package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 附件数据存储s
*/
public class FrmAttachmentDBs extends EntitiesMyPK
{

		///#region 构造
	/** 
	 附件数据存储s
	*/
	public FrmAttachmentDBs()
	{
	}
	/** 
	 附件数据存储s
	 
	 @param fk_mapdata s
	*/
	public FrmAttachmentDBs(String fk_mapdata, String pkval) throws Exception {
		this.Retrieve(FrmAttachmentDBAttr.FK_MapData, fk_mapdata, FrmAttachmentDBAttr.RefPKVal, pkval);
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmAttachmentDB();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmAttachmentDB> ToJavaList()
	{
		return (java.util.List<FrmAttachmentDB>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmAttachmentDB> Tolist()
	{
		ArrayList<FrmAttachmentDB> list = new ArrayList<FrmAttachmentDB>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmAttachmentDB)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
