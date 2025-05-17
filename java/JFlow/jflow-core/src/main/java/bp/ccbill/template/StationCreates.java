package bp.ccbill.template;

import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 单据可创建的工作角色
*/
public class StationCreates extends EntitiesMM
{

		///#region 构造函数.
	/** 
	 单据可创建的工作角色
	*/
	public StationCreates()
	{
	}
	/** 
	 单据可创建的工作角色
	 
	 @param nodeID 单据ID
	*/
	public StationCreates(int nodeID) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(StationCreateAttr.FrmID, nodeID);
		qo.DoQuery();
	}
	/** 
	 单据可创建的工作角色
	 
	 @param StationNo StationNo 
	*/
	public StationCreates(String StationNo) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(StationCreateAttr.FK_Station, StationNo);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new StationCreate();
	}

		///#endregion 构造函数.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<StationCreate> ToJavaList()
	{
		return (java.util.List<StationCreate>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<StationCreate> Tolist()
	{
		ArrayList<StationCreate> list = new ArrayList<StationCreate>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((StationCreate)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
