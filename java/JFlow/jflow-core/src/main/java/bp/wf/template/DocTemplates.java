package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 公文模板s
*/
public class DocTemplates extends EntitiesNoName
{

		///#region 构造
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new DocTemplate();
	}
	/** 
	 公文模板
	*/
	public DocTemplates()
	{
	}

	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<DocTemplate> ToJavaList()
	{
		return (java.util.List<DocTemplate>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<DocTemplate> Tolist()
	{
		ArrayList<DocTemplate> list = new ArrayList<DocTemplate>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((DocTemplate)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
