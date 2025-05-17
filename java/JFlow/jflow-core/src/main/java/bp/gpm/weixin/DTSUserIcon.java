package bp.gpm.weixin;

import bp.da.*;
import bp.port.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import java.util.*;
import java.io.*;

/** 
 微信人员头像同步
*/
public class DTSUserIcon extends Method
{
	/** 
	 微信人员头像同步
	*/
	public DTSUserIcon()
	{
		this.Title = "微信人员头像同步到DataUser/Icon";
		this.Help = "本功能将微信企业号中所有人员的头像下载到本地，包括一张大图，一张小图";
	}
	/** 
	 设置执行变量
	 
	 @return 
	*/
	@Override
	public void Init()
	{
	}
	/** 
	 当前的操纵员是否可以执行这个方法
	*/
	@Override
	public boolean getIsCanDo()
	{
		if (bp.wf.Glo.isEnableWeiXin() == true)
		{
			return true;
		}
		return false;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {
		bp.gpm.weixin.WeiXinEntity weixin = new bp.gpm.weixin.WeiXinEntity();
		String savePath = bp.difference.SystemConfig.getPathOfDataUser() + "UserIcon";

		//检查目录.
		if ((new File(savePath)).isDirectory() == false)
		{
			(new File(savePath)).mkdirs();
		}

		//获得部门信息.
		DeptList deptList = new DeptList();
		deptList.RetrieveAll(); //查询所有的部门信息.

		//遍历部门.
		for (DeptEntity deptMent : deptList.getDepartment())
		{
			//获得部门下的人员信息.
			UserList ul = new UserList(deptMent.getId());
			if (ul.getErrcode() != 0)
			{
				continue;
			}

			//遍历用户信息.
			for (UserEntity userInfo : ul.getUserlist())
			{
				if (userInfo.getAvatar() == null)
				{
					continue;
				}

				//大图标
				String headimgurl = userInfo.getAvatar();
				String UserIcon = savePath + "/" + userInfo.getUserid() + "Biger.png";
				DataType.HttpDownloadFile(headimgurl, UserIcon);

				//小图标
				String iconSize = userInfo.getAvatar().substring(headimgurl.lastIndexOf('/'));
				if (Objects.equals(iconSize, "/"))
				{
					headimgurl = userInfo.getAvatar() + "64";
				}
				else
				{
					headimgurl = userInfo.getAvatar().substring(0, headimgurl.lastIndexOf('/')) + "64";
				}
				UserIcon = savePath + "/" + userInfo.getUserid() + "Smaller.png";
				DataType.HttpDownloadFile(headimgurl, UserIcon);
			}
		}
		return "执行成功.";
	}
}
