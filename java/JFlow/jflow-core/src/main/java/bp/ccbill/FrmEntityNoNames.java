package bp.ccbill;

import bp.en.EntitiesNoName;
import bp.en.Entity;

import java.util.ArrayList;
import java.util.List;

/** 
 单据属性s
*/
public class FrmEntityNoNames extends EntitiesNoName
{

		///#region 构造
	/**
	 单据属性s
	*/
	public FrmEntityNoNames()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmEntityNoName();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final List<FrmEntityNoName> ToJavaList()
	{
		return (List<FrmEntityNoName>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmEntityNoName> Tolist()
	{
		ArrayList<FrmEntityNoName> list = new ArrayList<FrmEntityNoName>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmEntityNoName)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
