package bp.en;


import bp.da.AtPara;
import bp.da.DataType;
import bp.difference.SystemConfig;
import bp.sys.SFDBSrc;
import bp.web.WebUser;

import java.util.Hashtable;

/**
 可以被重写的类
*/
public class OverrideFile
{

	public static String Data_WebApi(Hashtable ht, String requestMethod, String sfDBSrcNo, String apiUrl, String paramData, String remark) throws Exception {
		//返回值
		//用户输入的webAPI地址
		if (apiUrl.contains("@WebApiHost")) //可以替换配置文件中配置的webapi地址
		{
			apiUrl = apiUrl.replace("@WebApiHost", SystemConfig.getWebApiHost());
		}

		SFDBSrc mysrc = new SFDBSrc(sfDBSrcNo);


		///#region  GET 解析路径变量 /{xxx}/{yyy} ? xxx=xxx
		if (requestMethod.toUpperCase().equals("GET") == true)
		{
			if (apiUrl.contains("{") == true)
			{
				if (ht != null)
				{
					for (Object key : ht.keySet())
					{
						apiUrl = apiUrl.replace("{" + key + "}", ht.get(key).toString());
					}
				}
				apiUrl = mysrc.getConnString() + apiUrl;
			}
			else
			{
				if (ht != null)
				{
					for (Object key : ht.keySet())
					{
						apiUrl = apiUrl.replace("@" + key,  ht.get(key).toString());
					}
				}
				apiUrl = mysrc.getConnString() + apiUrl;
			}
			return bp.tools.HttpClientUtil.doGet(apiUrl);
		}

		///#endregion

		if (apiUrl.startsWith("http") == false)
		{
			apiUrl = mysrc.getConnString() + apiUrl;
		}
		if (DataType.IsNullOrEmpty(paramData))
			paramData = "";
		paramData = paramData.replace("~~", "\"");
		paramData = paramData.replace("~", "\"");
		if (DataType.IsNullOrEmpty(paramData) == false)
		{
			paramData = paramData.replace("@WebUser.No", WebUser.getNo());
			paramData = paramData.replace("@WebUser.Name", WebUser.getName());
			paramData = paramData.replace("@WebUser.DeptNo", WebUser.getDeptNo());
			paramData = paramData.replace("@WebUser.DeptName", WebUser.getDeptName());
			paramData = paramData.replace("@WebUser.OrgNo", WebUser.getOrgNo());
			if (ht != null)
			{
				for (Object key : ht.keySet())
				{
					paramData = paramData.replace("@" + key,  ht.get(key).toString());
				}
			}
			//没有替换的@值，并且配置了别名
			if (paramData.contains("@")==true && DataType.IsNullOrEmpty(remark)==false)
			{
				AtPara atpara = new AtPara(remark);
				for(String key : atpara.getHisHT().keySet())
				{
					String alias = atpara.GetValStrByKey(key);
					String[] strs = alias.split(",");
					for(String str: strs)
					{
						if (DataType.IsNullOrEmpty(str) == true)
							continue;
						paramData = paramData.replace("@" + key, ht.get(key).toString());
					}
				}
			}
		}
		return bp.tools.HttpClientUtil.doPostJson(apiUrl, paramData);
	}	///可以重写的表单事件.
	/**
	 执行的事件

	 param frmID
	 param en
	*/
	public static String FrmEvent_LoadBefore(String frmID, Entity en)
	{
		return null;
	}
	/**
	 装载填充的事件.

	 param frmID
	 param en
	*/
	public static String FrmEvent_FrmLoadAfter(String frmID, Entity en)
	{
		return null;
	}
	/**
	 保存前事件

	 param frmID
	 param en
	*/
	public static String FrmEvent_SaveBefore(String frmID, Entity en)
	{
		return null;
	}
	/**
	 保存后事件

	 param frmID
	 param en
	*/
	public static String FrmEvent_SaveAfter(String frmID, Entity en)
	{
		return null;
	}

	public static String FrmBill_CheckStart(String frmID, Entity en)
	{
		return null;
	}
	/**
	 单据实体审核结束以后

	 @param frmID 单据ID
	 @param en 实体
	 */
	public static String FrmBill_CheckOver(String frmID, Entity en)
	{
		return null;
	}
	/// <summary>
	/// 单据重新审核(回滚)
	/// </summary>
	/// <param name="frmID"></param>
	/// <param name="en"></param>
	/// <returns></returns>
	public static String FrmBill_Reback(String frmID, Entity en)
	{
		return null;
	}
	/**
	 单据实体撤销审核

	 @param frmID
	 @param en
	 */
	public static String FrmBill_UnSend(String frmID, Entity en)
	{
		return null;
	}
	public static String FrmBill_OverBefore(String frmID, Entity en)
	{
		return null;
	}
	public static String FrmBill_OverAfter(String frmID, Entity en)
	{
		return null;
	}

		/// 可以重写的表单事件.
}
