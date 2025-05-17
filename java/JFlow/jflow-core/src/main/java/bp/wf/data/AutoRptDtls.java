package bp.wf.data;
import bp.en.*;
import java.util.*;

/** 
 自动报表-数据项
*/
public class AutoRptDtls extends EntitiesOIDName
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new AutoRptDtl();
	}
	/** 
	 自动报表
	*/
	public AutoRptDtls()
	{
		super();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<AutoRptDtl> ToJavaList()
	{
		return (java.util.List<AutoRptDtl>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<AutoRptDtl> Tolist()
	{
		ArrayList<AutoRptDtl> list = new ArrayList<AutoRptDtl>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((AutoRptDtl)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}