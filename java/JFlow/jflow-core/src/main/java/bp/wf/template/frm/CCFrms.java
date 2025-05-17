package bp.wf.template.frm;

import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 表单s
*/
public class CCFrms extends EntitiesNoName
{
	/** 
	 CCFrm
	*/
	public CCFrms()
	{
	}
	/** 
	 CCFrm
	 
	 @param fk_flow
	*/
	public CCFrms(String fk_flow) throws Exception {
		this.Retrieve(CCFrmAttr.FK_Flow, fk_flow, null);
	}
	public CCFrms(int fk_node) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhereInSQL(CCFrmAttr.No, "SELECT FK_CCFrm FROM WF_CCFrmNode WHERE FK_Node=" + fk_node);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new CCFrm();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<CCFrm> ToJavaList()
	{
		return (java.util.List<CCFrm>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<CCFrm> Tolist()
	{
		ArrayList<CCFrm> list = new ArrayList<CCFrm>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((CCFrm)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
