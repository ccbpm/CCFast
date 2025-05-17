package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 流程数据同步
*/
public class SyncDataFields extends EntitiesMyPK
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SyncDataField();
	}
	/** 
	 流程数据同步
	*/
	public SyncDataFields()
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SyncDataField> ToJavaList()
	{
		return (java.util.List<SyncDataField>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SyncDataField> Tolist()
	{
		ArrayList<SyncDataField> list = new ArrayList<SyncDataField>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SyncDataField)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
