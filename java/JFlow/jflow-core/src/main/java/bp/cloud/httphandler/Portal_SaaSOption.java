package bp.cloud.httphandler;

import bp.cloud.Dev2Interface;
import bp.cloud.Emp;
import bp.cloud.EmpAttr;
import bp.da.*;
import bp.difference.*;
import bp.sys.*;
import bp.tools.BCryptUtils;
import bp.web.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.wf.*;
import bp.wf.template.*;
import bp.*;
import bp.cloud.*;

/** 
 页面功能实体
*/
public class Portal_SaaSOption extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public Portal_SaaSOption()
	{
	}
	/** 
	 注册页面提交
	 
	 @return 
	*/
	public final String RegisterAdminer_Submit() throws Exception {
		String ip = bp.difference.Glo.getIP();

		String sql = "SELECT COUNT(*) FROM Port_Org WHERE RegIP='" + ip + "' AND DTReg LIKE '" + DataType.getCurrentDate() + "%'";
		int num = DBAccess.RunSQLReturnValInt(sql);
		if (num >= 4)
		{
			return "err@系统错误，今日不能连续注册。";
		}

		Org org = new Org();
		String orgNo = this.GetRequestVal("TB_OrgNo"); //管理员名称拼音.
		String orgName = this.GetRequestVal("TB_OrgName"); //管理员名称拼音.

		org.setNo(orgNo);
		if (org.getIsExits() == true)
		{
			return "err@组织账号[" + orgNo + "]已经存在。";
		}

		String tel = this.GetRequestVal("TB_Adminer"); //管理员名称拼音.
		Emp ep = new Emp();
		ep.setNo(orgNo);
		if (ep.getIsExits() == true)
		{
			return "err@组织账号[" + orgNo + "]已经存在。";
		}
		if (ep.IsExit(EmpAttr.UserID, orgNo) == true)
		{
			return "err@组织账号，已经存在.";
		}

		ep.setNo(tel);
		if (ep.getIsExits() == true)
		{
			return "err@该手机号已经注册请登录，或者使用其他手机号注册.";
		}
		if (ep.IsExit("UserID", tel) == true)
		{
			return "err@该手机号已经注册请登录，或者使用其他手机号注册.";
		}

		String adminer = this.GetRequestVal("TB_AdminerName"); //管理员名称中文.

		ep.setUserID(tel);
		ep.setName(adminer);

		try
		{
			//admin登录.
			Dev2Interface.Port_Login("admin", "100");

			//首先创建组织.
			Dev2Interface.Port_CreateOrg(orgNo, orgName);

			org.setNo(orgNo);
			org.RetrieveFromDBSources();
			org.setNameFull(this.GetRequestVal("TB_OrgNameFull"));
			org.setAdminer(tel);
			org.setAdminerName(adminer);

			//避免其他的注册错误.
			WebUser.setOrgNo(org.getNo());
			WebUser.setOrgName(org.getName());

			org.setRegFrom(0); //0=网站.1=企业微信.
			org.setAdminer(tel);
			org.setAdminerName(ep.getName());
			org.setDTReg(DataType.getCurrentDateTime());

			//获取来源.
			String from = this.GetRequestVal("From");
			if (DataType.IsNullOrEmpty(from) == true)
			{
				from = "ccbpm";
			}
			org.setUrlFrom(from);
			org.Update();


			//循环遍历 看邮箱是否唯一用户忘记密码用邮箱找回.
			String email = this.GetRequestVal("TB_Email");
			ep.setEmail(email);
			ep.setName(adminer);


			//处理拼音
			String pinyinQP = DataType.ParseStringToPinyin(ep.getName()).toLowerCase();
			String pinyinJX = DataType.ParseStringToPinyinJianXie(ep.getName()).toLowerCase();
			ep.setPinYin("," + pinyinQP + "," + pinyinJX + ",");

			ep.setOrgName(this.GetRequestVal("TB_OrgName"));
			ep.setDeptNo(org.getNo());
			ep.setOrgNo(org.getNo());
			ep.setNo(ep.getOrgNo() + "_" + tel);
			ep.DirectInsert();

			// 密码加密。
			String pass = this.GetRequestVal("TB_PassWord2");
			//MD5
			if (SystemConfig.getIsPasswordEncryptionType().equals("1")){
				pass = bp.tools.MD5Utill.MD5Encode(pass, "UTF8");
			}if (SystemConfig.getIsPasswordEncryptionType().equals("2")){ //BCrypt加密类型
		 	    pass =  BCryptUtils.hashpw(pass);
		    }
			DBAccess.RunSQL("UPDATE Port_Emp SET Pass='" + pass + "' WHERE No='" + ep.getNo() + "'");

			//初始化数据.
			org.setAdminer(ep.getUserID());
			org.setAdminerName(ep.getName());
			org.Update();

			//增加管理员.
			bp.wf.port.admin2group.Org myorg = new bp.wf.port.admin2group.Org(org.getNo());
			myorg.AddAdminer(tel);

			//让 组织 管理员登录.
			Dev2Interface.Port_Login(ep.getUserID(), org.getNo());

			//生成token.
			String token = bp.wf.Dev2Interface.Port_GenerToken("PC");
			return token;

		}
		catch (RuntimeException ex)
		{
			org.DoDelete();
			bp.wf.Dev2Interface.Port_SigOut();
			return "err@安装期间出现错误:" + ex.getMessage();
		}
		////让其退出登录.
		//bp.web.GuestUser.Exit();
		//BP.WF.Dev2Interface.Port_Login(ep.getNo());
		//String orgno = WebUser.getOrgNo();
	}

}
