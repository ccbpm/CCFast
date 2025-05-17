package bp.cloud;

import bp.da.*;

/** 
 云的公共类
*/
public class Glo
{
	public static String getSaasHost()
	{
		String str = bp.difference.SystemConfig.getAppSettings().get("SaasHost").toString();
		if (DataType.IsNullOrEmpty(str) == true)
		{
			return "ccbpm.cn";
		}
		return str;
	}
	/** 
	 当前写入的IP
	*/
	public static String getCurrDB()
	{
		String str = bp.difference.SystemConfig.getAppSettings().get("CurrDB").toString();
		return str;
	}
	/** 
	 生成BPM微信
	 
	 @param userID
	 @param userName
	 @param orgNo
	 @param orgName
	*/
	public static void Port_Org_InstallByWeiXin(String userID, String userName, String orgNo, String orgName, String empstrs, String deptstrs) throws Exception {

			///#region 处理管理员与adminer 的数据.
		bp.cloud.Emp emp = new bp.cloud.Emp();
		emp.setNo(userID);

		//如果没有，请插入.
		if (emp.RetrieveFromDBSources() == 0)
		{
			emp.setNo(userID);
			emp.setName(userName);
			emp.setDeptNo(orgNo);
		 //   emp.Pass = DBAccess.GenerGUID();
			emp.Insert();
		}

		bp.cloud.Org org = new Org();
		org.setNo(orgNo);
		if (org.RetrieveFromDBSources() == 0)
		{
			org.setName(orgName);
			org.setAdminer(userID);
			org.setAdminerName(userName);
			org.setWXUseSta(1);
			org.setDTReg(DataType.getCurrentDateTime());
			org.Insert(); //把他设置为超级管理员.
		}
		else
		{
			org.setWXUseSta(1); //把他变成启用状态.
			org.Update();
		}

			///#endregion 处理管理员与adminer 的数据.


			///#region 开始同步人员信息.

			///#endregion 开始同步人员信息.


			///#region 开始同步部门信息.
		Dept deptRoot = new Dept();
		deptRoot.setNo(orgNo);
		if (deptRoot.RetrieveFromDBSources() == 0)
		{
			deptRoot.setName(orgName);
			deptRoot.setParentNo("100");
			deptRoot.setOrgNo(orgNo);
			deptRoot.Insert();
		}
		else
		{
			deptRoot.setName(orgName);
			deptRoot.setParentNo("100");
			deptRoot.setOrgNo(orgNo);
			deptRoot.Update();
		}

			///#endregion 开始同步部门信息.

		//让管理员登录.
		bp.cloud.Dev2Interface.Port_Login(userID, orgNo);
	}
	public static void Port_Org_UnInstallByWeiXin(String userID, String userName, String orgNo, String orgName) throws Exception {
		Org org = new Org(orgNo);
		org.setWXUseSta(0);
		org.setDTEnd(DataType.getCurrentDateTime());
		org.Update();
	}
	/** 
	 当人员信息变化时
	 
	 @param id
	 @param name
	 @param deptNo
	*/
	public static void Port_Change_Emp(String id, String name, String deptNo)
	{
	}
	public static void Port_Change_Dept(String id, String name, String deptNo)
	{
	}
	public static void Port_Change_Org(String id, String name, String deptNo)
	{
	}

	public static void SaveImgByUrl(String corpSquareLogoUrl, String v1, String v2)
	{
		throw new UnsupportedOperationException();
	}
	public static void DTS_CCFlowCloudCenter(String orgNo)
	{
	}
}
