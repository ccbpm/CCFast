package bp.wf.template.frm;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;
import java.io.*;

/** 
 打印模板s
*/
public class FrmPrintTemplates extends EntitiesMyPK
{

		///#region 构造
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmPrintTemplate();
	}
	/** 
	 打印模板
	*/
	public FrmPrintTemplates()
	{
	}
	public FrmPrintTemplates(int nodeID) throws Exception {
		this.Retrieve(FrmPrintTemplateAttr.NodeID, nodeID, null);
	}
	public FrmPrintTemplates(String flowNo) throws Exception {
		this.Retrieve(FrmPrintTemplateAttr.FlowNo, flowNo, null);
	}

		///#endregion



		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmPrintTemplate> ToJavaList()
	{
		return (java.util.List<FrmPrintTemplate>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmPrintTemplate> Tolist()
	{
		ArrayList<FrmPrintTemplate> list = new ArrayList<FrmPrintTemplate>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmPrintTemplate)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
