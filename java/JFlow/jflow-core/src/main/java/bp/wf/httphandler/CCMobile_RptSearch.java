package bp.wf.httphandler;

import bp.difference.*;
import bp.*;
import bp.wf.*;

/** 
 页面功能实体
*/
public class CCMobile_RptSearch extends bp.difference.handler.DirectoryPageBase
{

		///#region 执行父类的重写方法.
	/** 
	 默认执行的方法
	 
	 @return 
	*/
	@Override
	protected String DoDefaultMethod()
	{
		switch (this.getDoType())
		{
			case "DtlFieldUp": //字段上移
				return "执行成功.";
			default:
				break;
		}

		//找不不到标记就抛出异常.
		throw new RuntimeException("@标记[" + this.getDoType() + "]，没有找到. @RowURL:" + ContextHolderUtils.getRequest().getRequestURI());
	}

		///#endregion 执行父类的重写方法.

	/** 
	 构造函数
	*/
	public CCMobile_RptSearch() throws Exception {
		bp.web.WebUser.setSheBei("Mobile");
	}


		///#region 关键字查询.
	/** 
	 执行查询
	 
	 @return 
	*/
	public final String KeySearch_Query() throws Exception {
		bp.wf.httphandler.WF_RptSearch search = new WF_RptSearch();
		return search.KeySearch_Query();
	}

		///#endregion 关键字查询.

}
