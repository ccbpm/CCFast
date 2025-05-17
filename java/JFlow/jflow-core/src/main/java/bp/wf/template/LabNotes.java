package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 标签集合
*/
public class LabNotes extends Entities
{

		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new LabNote();
	}

		///#endregion


		///#region 构造方法
	/** 
	 标签集合
	*/
	public LabNotes()
	{
	}
	/** 
	 标签集合.
	 @param fk_flow
	*/
	public LabNotes(String fk_flow) throws Exception {
		this.Retrieve(NodeAttr.FK_Flow, fk_flow, null);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<LabNote> ToJavaList()
	{
		return (java.util.List<LabNote>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<LabNote> Tolist()
	{
		ArrayList<LabNote> list = new ArrayList<LabNote>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((LabNote)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
