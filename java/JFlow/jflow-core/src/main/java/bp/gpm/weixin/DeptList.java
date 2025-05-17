package bp.gpm.weixin;

import bp.da.*;
import bp.*;
import java.util.*;

/** 
 部门列表
*/
public class DeptList
{

		///#region 部门属性.
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
	 部门列表数据
	*/
	private ArrayList<DeptEntity> department;
	public final ArrayList<DeptEntity> getDepartment()
	{
		return department;
	}
	public final void setDepartment(ArrayList<DeptEntity> value)
	{
		department = value;
	}

		///#endregion 部门属性.

	public DeptList()
	{
	}
	/** 
	 查询所有的部门
	 
	 @return 
	*/
	public final int RetrieveAll() throws Exception {
		String access_token = bp.gpm.weixin.WeiXinEntity.getAccessToken();
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=" + access_token;

		//读取数据.
		String str = DataType.ReadURLContext(url, 9999);
		DeptList departMentList = (DeptList) bp.tools.FormatToJson.<DeptList>ParseFromJson(str);

		if (departMentList.getErrcode() != 0)
		{
			throw new RuntimeException("err@获得部门信息错误:code" + departMentList.getErrcode() + ",Msg=" + departMentList.getErrmsg());
		}


		this.setErrcode(departMentList.getErrcode());
		this.setErrmsg(departMentList.getErrmsg());
		this.setDepartment(departMentList.getDepartment());

		return this.getDepartment().size();
	}
}
