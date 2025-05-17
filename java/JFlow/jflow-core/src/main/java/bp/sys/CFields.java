package bp.sys;
import bp.en.*;
import java.util.*;

/** 
 列选择s
*/
public class CFields extends EntitiesMyPK
{
	/** 
	 列选择s
	*/
	public CFields()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CField();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CField> ToJavaList()
	{
		return (java.util.List<CField>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CField> Tolist()
	{
		ArrayList<CField> list = new ArrayList<CField>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CField)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
