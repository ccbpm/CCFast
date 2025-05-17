package bp.ccbill;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.wf.*;
import bp.wf.template.*;
import bp.sys.*;
import bp.ccbill.template.*;
import bp.*;
import java.util.*;

/** 
 单据属性s
*/
public class FrmBills extends EntitiesNoName
{

		///#region 构造
	/** 
	 单据属性s
	*/
	public FrmBills()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmBill();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmBill> ToJavaList()
	{
		return (java.util.List<FrmBill>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmBill> Tolist()
	{
		ArrayList<FrmBill> list = new ArrayList<FrmBill>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmBill)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
