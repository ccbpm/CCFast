package bp.wf;

import bp.en.*;
import java.util.*;

/** 
 流程集合
*/
public class TemplateFlows extends EntitiesNoName
{


		///#region 构造方法
	/** 
	 工作流程
	*/
	public TemplateFlows()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity(){
		return new TemplateFlow();
	}

		///#endregion 构造方法


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<TemplateFlow> ToJavaList()
	{
		return (java.util.List<TemplateFlow>)(Object)this;
	}
	/** 
	 转化成 list
	 
	 @return List
	*/
	public final ArrayList<TemplateFlow> Tolist()
	{
		ArrayList<TemplateFlow> list = new ArrayList<TemplateFlow>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((TemplateFlow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
