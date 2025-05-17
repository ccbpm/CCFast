package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 评论(抄送)组件s
*/
public class MapAttrFlowBBSs extends EntitiesMyPK
{

		///#region 构造
	/** 
	 评论(抄送)组件s
	*/
	public MapAttrFlowBBSs()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapAttrFlowBBS();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapAttrFlowBBS> ToJavaList()
	{
		return (java.util.List<MapAttrFlowBBS>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapAttrFlowBBS> Tolist()
	{
		ArrayList<MapAttrFlowBBS> list = new ArrayList<MapAttrFlowBBS>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapAttrFlowBBS)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
