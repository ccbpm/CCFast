package bp.ccbill.template;

import bp.da.*;
import bp.web.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.ccbill.*;
import java.util.*;

/** 
 方法单据
*/
public class MethodBills extends EntitiesNoName
{
	/** 
	 方法单据
	*/
	public MethodBills()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MethodBill();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MethodBill> ToJavaList()
	{
		return (java.util.List<MethodBill>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MethodBill> Tolist()
	{
		ArrayList<MethodBill> list = new ArrayList<MethodBill>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MethodBill)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
