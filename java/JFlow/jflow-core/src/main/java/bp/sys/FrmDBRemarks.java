package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 数据批阅s
*/
public class FrmDBRemarks extends EntitiesMyPK
{

		///#region 构造
	public FrmDBRemarks()
	{
	}

		///#endregion


		///#region 重写
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmDBRemark();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmDBRemark> Tolist()
	{
		ArrayList<FrmDBRemark> list = new ArrayList<FrmDBRemark>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmDBRemark)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.


		///#region 为了适应自动翻译成java的需要,把实体转换成IList, c#代码调用会出错误。
	/** 
	 转化成list
	 
	 @return List
	*/
	public final java.util.List<FrmDBRemark> ToJavaList()
	{
		return (java.util.List<FrmDBRemark>)(Object)this;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成IList, c#代码调用会出错误。

}
