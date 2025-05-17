package bp.gpm.weixin;

import bp.da.*;
import bp.tools.*;
import bp.*;
import java.util.*;

/** 
 部门下的人员
*/
public class UserList
{

		///#region 属性.
	/** 
	 返回码
	*/
	private int errcode;
	public final int getErrcode()
	{
		return errcode;
	}
	public final void setErrcode(int value)
	{
		errcode = value;
	}
	/** 
	 对返回码的文本描述内容
	*/
	private String errmsg;
	public final String getErrmsg()
	{
		return errmsg;
	}
	public final void setErrmsg(String value)
	{
		errmsg = value;
	}
	/** 
	 成员列表
	*/
	private ArrayList<UserEntity> userlist;
	public final ArrayList<UserEntity> getUserlist()
	{
		return userlist;
	}
	public final void setUserlist(ArrayList<UserEntity> value)
	{
		userlist = value;
	}

		///#endregion 属性.

	/** 
	 初始化部门信息
	*/
	public UserList()
	{
	}
	/** 
	 部门ID
	 
	 @param deptID
	*/
	public UserList(String deptID) throws Exception {
		//获取token.
		String access_token = bp.gpm.weixin.WeiXinEntity.getAccessToken();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=" + access_token + "&department_id=" + deptID + "&status=0";

		//获得信息.
		String str = DataType.ReadURLContext(url, 9999);
		UserList userList = (UserList) FormatToJson.<UserList>ParseFromJson(str);

		//赋值.
		this.setErrcode(userList.getErrcode());
		this.setErrmsg(userList.getErrmsg());
		this.setUserlist(userList.getUserlist());
	}
}
