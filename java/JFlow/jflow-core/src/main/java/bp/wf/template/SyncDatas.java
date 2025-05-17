package bp.wf.template;

import bp.en.*;
import java.util.*;

/**
 流程数据同步
*/
public class SyncDatas extends EntitiesMyPK
{

		///#region 方法
	/**
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SyncData();
	}

	public SyncDatas(){

	}
	/**
	 流程数据同步
	*/
	public SyncDatas(String _No) throws Exception {
		this.Retrieve(SyncDataAttr.FlowNo,_No);
	}
		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/**
	 转化成 java list,C#不能调用.

	 @return List
	*/
	public final java.util.List<SyncData> ToJavaList()
	{
		return (java.util.List<SyncData>)(Object)this;
	}
	/**
	 转化成list

	 @return List
	*/
	public final ArrayList<SyncData> Tolist()
	{
		ArrayList<SyncData> list = new ArrayList<SyncData>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SyncData)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
