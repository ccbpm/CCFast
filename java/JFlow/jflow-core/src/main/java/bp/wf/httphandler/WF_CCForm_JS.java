package bp.wf.httphandler;

import bp.difference.handler.DirectoryPageBase;
import bp.sys.*;
import bp.*;
import bp.wf.*;

/** 
 页面功能实体
*/
public class WF_CCForm_JS extends DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public WF_CCForm_JS()
	{
	}

	/** 
	 批注
	 
	 @return 
	*/
	public final String FrmDBRemark_GenerReamrkFields() throws Exception {
		FrmDBRemarks ens = new FrmDBRemarks();
		ens.Retrieve(FrmDBRemarkAttr.FrmID, this.getFrmID(), FrmDBRemarkAttr.RefPKVal, this.getPKVal(),"RDT");
		return ens.ToJson("dt");
	}
	/** 
	 获得版本管理中-变化的字段
	 
	 @return 
	*/
	public final String FrmDBVer_GenerChangeFields() throws Exception {
		//FrmDBVers ens = new FrmDBVers();
		//ens.Retrieve();

		FrmDBRemarks ens = new FrmDBRemarks();
		ens.Retrieve(FrmDBRemarkAttr.FrmID, this.getFrmID(), FrmDBRemarkAttr.RefPKVal, this.getRefPKVal(), null);
		return ens.ToJson("dt");
	}

}
