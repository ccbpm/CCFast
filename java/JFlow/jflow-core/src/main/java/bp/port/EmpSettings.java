package bp.port;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;

import java.util.*;

/** 
 操作员s
*/
// </summary>
public class EmpSettings extends EntitiesNoName
{

		///#region 构造方法
	/**
	 * 得到它的 Entity
	 */
	public EmpSetting getNewEntity()
	{
		return new EmpSetting();
	}
	/** 
	 操作员s
	*/
	public EmpSettings()
	{
	}

		///#endregion 构造方法


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<EmpSetting> ToJavaList()
	{
		return (java.util.List<EmpSetting>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<EmpSetting> Tolist()
	{
		ArrayList<EmpSetting> list = new ArrayList<EmpSetting>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((EmpSetting)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
