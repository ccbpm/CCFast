package bp.ccbill;

import bp.en.*;
import java.util.*;

/** 
 实体表单s
*/
public class FrmDicts extends EntitiesNoName
{

		///#region 构造
	/** 
	 实体表单s
	*/
	public FrmDicts()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmDict();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmDict> ToJavaList()
	{
		return (java.util.List<FrmDict>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmDict> Tolist()
	{
		ArrayList<FrmDict> list = new ArrayList<FrmDict>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmDict)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
