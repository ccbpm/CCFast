package bp.wf.httphandler;


/** 
 页面功能实体
*/
public class TA_App extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public TA_App()
	{
	}


		///#region  界面 .
	public final String AccepterRole_Init()
	{
		return "方法未完成";
	}
	public final String Student_JiaoNaXueFei()
	{
		String no = this.GetRequestVal("No");
		String name = this.GetRequestVal("Name");
		String note = this.GetRequestVal("Note");
		float jine = this.GetRequestValFloat("JinE");


		return "学费缴纳成功[" + no + "][" + name + "][" + note + "][" + jine + "]";

	}

		///#endregion 界面方法.

}
