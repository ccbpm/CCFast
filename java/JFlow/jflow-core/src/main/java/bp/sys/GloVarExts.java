package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 全局变量s
*/
public class GloVarExts extends EntitiesNoName
{


		///#region 构造
	/** 
	 全局变量s
	*/
	public GloVarExts()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GloVarExt();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GloVarExt> ToJavaList()
	{
		return (java.util.List<GloVarExt>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GloVarExt> Tolist()
	{
		ArrayList<GloVarExt> list = new ArrayList<GloVarExt>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GloVarExt)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
