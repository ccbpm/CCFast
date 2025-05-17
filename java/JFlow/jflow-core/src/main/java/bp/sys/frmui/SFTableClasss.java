package bp.sys.frmui;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.sys.*;
import java.util.*;

/** 
 用户自定义表s
*/
public class SFTableClasss extends EntitiesNoName
{

		///#region 构造
	/** 
	 用户自定义表s
	*/
	public SFTableClasss()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SFTableClass();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SFTableClass> ToJavaList()
	{
		return (java.util.List<SFTableClass>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SFTableClass> Tolist()
	{
		ArrayList<SFTableClass> list = new ArrayList<SFTableClass>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SFTableClass)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
