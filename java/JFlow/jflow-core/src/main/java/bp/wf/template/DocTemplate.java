package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;


/** 
 公文模板
*/
public class DocTemplate extends EntityNoName
{

		///#region  属性
	/** 
	 文件
	*/
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: public byte[] getFileBytes()
	public final byte[] getFileBytes() throws Exception {
		//转化为字节.
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte[] bytes = null;
		byte[] bytes = null;
		bytes = DataType.ConvertFileToByte(this.getFilePath());
		return bytes;
	}
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		return uac;
	}
	/** 
	 编号
	*/
	public final String getNo()  {
		return this.GetValStrByKey(DocTemplateAttr.No);
	}
	public final void setNo(String value){
		this.SetValByKey(DocTemplateAttr.No, value);
	}
	/** 
	 路径
	*/
	public final String getFilePath()  {
		return this.GetValStrByKey(DocTemplateAttr.FilePath);
	}
	public final void setFilePath(String value){
		this.SetValByKey(DocTemplateAttr.FilePath, value);
	}
	/** 
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(DocTemplateAttr.FK_Node);
	}
	public final void setNodeID(int value){
		this.SetValByKey(DocTemplateAttr.FK_Node, value);
	}
	/** 
	 流程编号
	*/
	public final String getFlowNo()  {
		return this.GetValStrByKey(DocTemplateAttr.FK_Flow);
	}
	public final void setFlowNo(String value){
		this.SetValByKey(DocTemplateAttr.FK_Flow, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 公文模板
	*/
	public DocTemplate()
	{
	}
	/** 
	 公文模板
	 
	 @param no
	*/
	public DocTemplate(String no) throws Exception
	{
		super(no);
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_DocTemplate", "公文模板");
		//map.setCodeStruct("6");

		map.AddTBStringPK(DocTemplateAttr.No, null, "No", true, true, 1, 50, 20);
		map.AddTBString(DocTemplateAttr.Name, null, "名称", true, false, 0, 200, 20);
		map.AddTBString(DocTemplateAttr.FilePath, null, "模板路径", true, false, 0, 200, 20);
		map.AddTBInt(DocTemplateAttr.FK_Node, 0, "节点ID", true, false);
		map.AddTBString(DocTemplateAttr.FK_Flow, null, "流程编号", true, false, 0, 4, 20);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
