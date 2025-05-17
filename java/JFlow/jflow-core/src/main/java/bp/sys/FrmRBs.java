package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 单选框s
*/
public class FrmRBs extends EntitiesMyPK
{

		///#region 构造
	/** 
	 单选框s
	*/
	public FrmRBs()
	{
	}
	/** 
	 单选框s
	 
	 @param frmID s
	*/
	public FrmRBs(String frmID) throws Exception {
		this.Retrieve(MapAttrAttr.FK_MapData, frmID);
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmRB();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmRB> ToJavaList()
	{
		return (java.util.List<FrmRB>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmRB> Tolist()
	{
		ArrayList<FrmRB> list = new ArrayList<FrmRB>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmRB)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
