package bp.ccbill.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 实体工具栏按钮
*/
public class ToolbarBtns extends EntitiesMyPK
{
	/** 
	 实体工具栏按钮
	*/
	public ToolbarBtns()
	{
	}
	/** 
	 实体工具栏按钮
	 
	 @param nodeid 方法IDID
	*/
	public ToolbarBtns(int nodeid) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(ToolbarBtnAttr.BtnID, nodeid);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new ToolbarBtn();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<ToolbarBtn> ToJavaList()
	{
		return (java.util.List<ToolbarBtn>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<ToolbarBtn> Tolist()
	{
		ArrayList<ToolbarBtn> list = new ArrayList<ToolbarBtn>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((ToolbarBtn)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
