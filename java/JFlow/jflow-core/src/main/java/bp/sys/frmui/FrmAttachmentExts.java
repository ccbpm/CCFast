package bp.sys.frmui;

import bp.en.*; import bp.en.Map;
import bp.sys.*;
import java.util.*;

/** 
 附件s
*/
public class FrmAttachmentExts extends EntitiesMyPK
{

		///#region 构造
	/** 
	 附件s
	*/
	public FrmAttachmentExts()
	{
	}
	/** 
	 附件s
	 
	 @param fk_mapdata s
	*/
	public FrmAttachmentExts(String fk_mapdata) throws Exception {
		this.Retrieve(FrmAttachmentAttr.FK_MapData, fk_mapdata, FrmAttachmentAttr.FK_Node, 0);
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmAttachmentExt();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmAttachmentExt> ToJavaList()
	{
		return (java.util.List<FrmAttachmentExt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmAttachmentExt> Tolist()
	{
		ArrayList<FrmAttachmentExt> list = new ArrayList<FrmAttachmentExt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmAttachmentExt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
