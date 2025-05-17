package bp.wf.data;


import bp.en.*;
import java.util.*;

/** 
 月份s
*/
public class GenerWorkFlowViewNYs extends EntitiesNoName
{

		///#region 构造
	/** 
	 月份s
	*/
	public GenerWorkFlowViewNYs()throws Exception
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GenerWorkFlowViewNY();
	}

		///#endregion

	@Override
	public int RetrieveAll() throws Exception 
	{
		String sql = "SELECT DISTINCT FK_NY, FK_NY FROM WF_GenerWorkFlow";

		return super.RetrieveAll();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GenerWorkFlowViewNY> ToJavaList()throws Exception
	{
		return (java.util.List<GenerWorkFlowViewNY>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GenerWorkFlowViewNY> Tolist()throws Exception
	{
		ArrayList<GenerWorkFlowViewNY> list = new ArrayList<GenerWorkFlowViewNY>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GenerWorkFlowViewNY)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}