package bp.gpm.weixin;

import bp.da.*;
import bp.tools.*;
import bp.*;
import java.util.*;

/** 
 部门人员详情
*/
public class UserEntity
{

		///#region 属性.
	/** 
	 成员UserID。对应管理端的帐号
	*/
	private String userid;
	public final String getUserid()
	{
		return userid;
	}
	public final void setUserid(String value)
	{
		userid = value;
	}
	/** 
	 成员名称
	*/
	private String name;
	public final String getName()
	{
		return name;
	}
	public final void setName(String value)
	{
		name = value;
	}
	/** 
	 成员所属部门
	*/
	private Object department;
	public final Object getDepartment()
	{
		return department;
	}
	public final void setDepartment(Object value)
	{
		department = value;
	}
	/** 
	 职位信息
	*/
	private String position;
	public final String getPosition()
	{
		return position;
	}
	public final void setPosition(String value)
	{
		position = value;
	}
	/** 
	 手机号码
	*/
	private String mobile;
	public final String getMobile()
	{
		return mobile;
	}
	public final void setMobile(String value)
	{
		mobile = value;
	}
	/** 
	 性别。0表示未定义，1表示男性，2表示女性
	*/
	private String gender;
	public final String getGender()
	{
		return gender;
	}
	public final void setGender(String value)
	{
		gender = value;
	}
	/** 
	 邮箱
	*/
	private String email;
	public final String getEmail()
	{
		return email;
	}
	public final void setEmail(String value)
	{
		email = value;
	}
	/** 
	 微信号
	*/
	private String weixinid;
	public final String getWeixinid()
	{
		return weixinid;
	}
	public final void setWeixinid(String value)
	{
		weixinid = value;
	}
	/** 
	 头像url。注：如果要获取小图将url最后的"/0"改成"/64"即可
	*/
	private String avatar;
	public final String getAvatar()
	{
		return avatar;
	}
	public final void setAvatar(String value)
	{
		avatar = value;
	}
	/** 
	 关注状态: 1=已关注，2=已冻结，4=未关注 
	*/
	private String status;
	public final String getStatus()
	{
		return status;
	}
	public final void setStatus(String value)
	{
		status = value;
	}
	/** 
	 扩展属性
	*/
	private String extattr;
	public final String getExtattr()
	{
		return extattr;
	}
	public final void setExtattr(String value)
	{
		extattr = value;
	}

		///#endregion 属性.


	/** 
	 获取指定部门下  指定手机号的人员
	 
	 @param deptID 部门编号
	 @return
	*/

	public UserEntity(String deptID) throws Exception {
		this(deptID, null);
	}

	public UserEntity(String deptID, String tel) throws Exception {
		String access_token = bp.gpm.weixin.WeiXinEntity.getAccessToken();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token= " + access_token + "&department_id=" + deptID + "&status=0";

		//读取数据.
		String str = DataType.ReadURLContext(url, 9999);
		//获得用户列表.
		UserList users = (UserList) FormatToJson.<UserList>ParseFromJson(str);

		//从用户列表里找到tel的人员信息，并返回.
		for (UserEntity user : users.getUserlist())
		{
			if (user.getMobile().equals(tel))
			{
				this.setUserid(user.getUserid());
				this.setName(user.getName());
				this.setDepartment(user.getDepartment());
				this.setPosition(user.getPosition());
				this.setMobile(user.getMobile());
				this.setGender(user.getGender());
				this.setEmail(user.getEmail());
				this.setWeixinid(user.getWeixinid());
				this.setAvatar(user.getAvatar());
				this.setStatus(user.getStatus());
				this.setExtattr(user.getExtattr());
			}
		}

		throw new RuntimeException("err@该部门下查无此人.");
	}
}
