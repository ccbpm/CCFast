package bp.wf.data;

import bp.en.Entities;
import bp.en.Entity;

import java.util.ArrayList;
import java.util.List;

/** 
 流程实例s
*/
public class GenerWorkFlowExts extends Entities
{

		///#region 方法
	/**
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GenerWorkFlowExt();
	}
	/**
	 流程实例集合
	*/
	public GenerWorkFlowExts()throws Exception
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final List<GenerWorkFlowExt> ToJavaList()throws Exception
	{
		return (List<GenerWorkFlowExt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GenerWorkFlowExt> Tolist()throws Exception
	{
		ArrayList<GenerWorkFlowExt> list = new ArrayList<GenerWorkFlowExt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GenerWorkFlowExt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}