package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 节点表单组件s
*/
public class FrmNodeComponents extends Entities
{

		///#region 构造
	/** 
	 节点表单组件s
	*/
	public FrmNodeComponents()
	{
	}
	/** 
	 节点表单组件s
	 
	 @param fk_mapdata s
	*/
	public FrmNodeComponents(String fk_mapdata) throws Exception {
			this.RetrieveFromCache("No", (Object)fk_mapdata);
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmNodeComponent();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmNodeComponent> ToJavaList()
	{
		return (java.util.List<FrmNodeComponent>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmNodeComponent> Tolist()
	{
		ArrayList<FrmNodeComponent> list = new ArrayList<FrmNodeComponent>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmNodeComponent)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
