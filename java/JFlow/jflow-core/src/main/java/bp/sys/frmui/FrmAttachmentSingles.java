package bp.sys.frmui;

import bp.en.*; import bp.en.Map;
import java.util.*;

/** 
 字段单附件s
*/
public class FrmAttachmentSingles extends EntitiesMyPK
{

		///#region 构造
	/** 
	 字段单附件s
	*/
	public FrmAttachmentSingles()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmAttachmentSingle();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmAttachmentSingle> ToJavaList()
	{
		return (java.util.List<FrmAttachmentSingle>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmAttachmentSingle> Tolist()
	{
		ArrayList<FrmAttachmentSingle> list = new ArrayList<FrmAttachmentSingle>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmAttachmentSingle)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
