package bp.wf.httphandler;

import bp.da.*;
import bp.gpm.weixin.*;
import bp.tools.*;
import bp.wf.*;
import java.util.*;

/** 
 集成企业微信OA类
*/
public class WF_MyOA extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 集成企业微信OA类
	*/
	public WF_MyOA()
	{

	}

	/** 
	 初始化(处理登录用户)
	 
	 @return 
	*/
	public final String MyOA_Init() throws Exception {
		String code = this.GetRequestVal("code");
		//String state = this.GetRequestVal("state"); //执行标记.
		Hashtable myht = new Hashtable();
		if (bp.web.WebUser.getNo() == null)
		{
			/* 如果当前登录人员帐号为 null .*/
			String accessToken = WeiXinEntity.getAccessToken(); //获取 AccessToken
			myht.put("Token", accessToken);
			String userId = getUserId(code, accessToken);
			User user = (User) FormatToJson.<User>ParseFromJson(userId);

			if (user.getErrCode() != 0)
			{
				//throw new Exception("err@当前登录帐号为空，请检查AccessToken是否正确：" + accessToken);
				return "err@当前登录帐号为空，请检查AccessToken是否正确：" + accessToken;
			}
			else
			{
				bp.port.Emps emps = new bp.port.Emps();
				int num = emps.Retrieve(bp.port.EmpAttr.No, user.getUserId(), null);
				if (num <= 0)
				{
					//是否使用微信企业号中的组织结构进行登录
					if (Objects.equals(bp.difference.SystemConfig.getOZType(), "1"))
					{
						String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=" + accessToken + "&userid=" + user.getUserId();
						String str = DataType.ReadURLContext(url, 4000);
						//人员详细信息
						UserEntity userEn = (UserEntity) FormatToJson.<UserEntity>ParseFromJson(str);

						//所属部门
						Object tempVar = userEn.getDepartment();
						ArrayList<String> depts = tempVar instanceof ArrayList ? (ArrayList<String>)tempVar : null;
						for (Object item : depts)
						{
							bp.port.Depts dts = new bp.port.Depts();
							url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=" + accessToken + "&id=" + item;
							str = DataType.ReadURLContext(url, 4000);

							//部门详细信息
							DeptList mydepts = (DeptList) FormatToJson.<DeptList>ParseFromJson(str);
							//如果部门不存在，插入部门信息
							if (dts.Retrieve(bp.port.DeptAttr.No, item, null) <= 0)
							{
								for (DeptEntity deptMent : mydepts.getDepartment())
								{
									dts = new bp.port.Depts();
									if (dts.Retrieve(bp.port.DeptAttr.No, item, null) > 0)
									{
										continue;
									}
									else
									{
										bp.port.Dept dp = new bp.port.Dept();
										dp.setNo(deptMent.getId());
										dp.setName(deptMent.getName());
										dp.setParentNo(deptMent.getParentid());
										dp.Insert();
									}
								}
							}
							else //如果存在，更新部门名称
							{
								for (DeptEntity deptMent : mydepts.getDepartment())
								{
									bp.port.Dept dp = new bp.port.Dept(deptMent.getId());
									dp.setName(deptMent.getName());
									dp.setParentNo(deptMent.getParentid());
									dp.Update();
								}
							}
							//插入人员表
							bp.port.Emp emp = new bp.port.Emp();
							emp.setNo(user.getUserId());
							emp.setDeptNo(item.toString());
							emp.setName(userEn.getName());
							emp.setTel(userEn.getMobile());
							emp.Insert();

							//插入部门表
							bp.port.DeptEmp deptEmp = new bp.port.DeptEmp();
							deptEmp.setMyPK(item + "_" + user.getUserId());
							deptEmp.setDeptNo(item.toString());
							deptEmp.setEmpNo(user.getUserId());
							deptEmp.Insert();
						}
						//执行登录
						Dev2Interface.Port_Login(user.getUserId());
					}
					else
					{
						emps = new bp.port.Emps();
						num = emps.Retrieve(bp.port.EmpAttr.Tel, user.getUserId(), null);
						if (num <= 0)
						{
							//throw new Exception("err@用户名错误，没有找到登录信息：" + accessToken);
							return "err@用户名错误，没有找到登录信息：" + accessToken;
						}
						for (bp.port.Emp emp : emps.ToJavaList())
						{
							Dev2Interface.Port_Login(emp.getNo());
							break;
						}
					}
				}
				else
				{
					Dev2Interface.Port_Login(user.getUserId());
				}
			}
		}

		return Json.ToJson(myht);

		//if (!state.contains(','))
		//    Response.Redirect("../CCMobile/" + state + ".htm");
		//else
		//{
		//    string[] vals = state.Split(',');
		//    if (vals[0].contains("URL_") == true)
		//    {
		//        String url = vals[0].Substring(4);
		//        String dummyData = url.Trim().replace("%", "").replace(",", "").replace(" ", "+");
		//        if (dummyData.length() % 4 > 0)
		//        {
		//            dummyData = dummyData.PadRight(dummyData.length() + 4 - dummyData.length() % 4, '=');
		//        }
		//        //Base64位解码
		//        byte[] bytes = Convert.FromBase64String(dummyData);
		//        url = UTF8Encoding.UTF8.GetString(bytes);
		//        Response.Redirect(url);
		//    }

		//    if (vals[0].contains("MyView") == true)
		//    {
		//        Response.Redirect("../CCMobile/MyView.htm?WorkID=" + vals[1].replace("WorkID_", "") + "&FK_Node=" + vals[2].replace("FK_Node_", "") + "");
		//    }

		//    if (vals[0].contains("FlowNo"))
		//    {
		//        string[] pks = vals[0].Split('_');
		//        Response.Redirect("../CCMobile/MyFlow.htm?FK_Flow=" + pks[1] + "&state=" + vals[1] + "");
		//    }

		//}
	}
	/** 
	 获得用户ID
	 
	 @param code
	 @param accessToken
	 @return 
	*/
	public final String getUserId(String code, String accessToken)
	{
		String url = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=" + accessToken + "&code=" + code;
		return DataType.ReadURLContext(url, 39000);
	}

}
