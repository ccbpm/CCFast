package bp.wf.httphandler;

import bp.*;
import bp.wf.*;

/** 
 页面功能实体
*/
public class WF_GPM_CreateMenu extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_GPM_CreateMenu()
	{

	}
	/** 
	 创建独立流程
	 
	 @return 
	*/
	public final String StandAloneFlow_Save() throws Exception {
		//首先创建流程. 参数都通过 httrp传入了。
		bp.wf.httphandler.WF_Admin_CCBPMDesigner_FlowDevModel handler = new WF_Admin_CCBPMDesigner_FlowDevModel();
		String flowNo = handler.FlowDevModel_Save();
		return flowNo;
	}
	/** 
	 模板复制
	 
	 @return 
	*/
	public final String Menus_DictCopy()
	{
	   // BP.CCBill.FrmDict en = new CCBill.FrmDict();
	   // en.doC
		return "复制成功.";
	}
}
