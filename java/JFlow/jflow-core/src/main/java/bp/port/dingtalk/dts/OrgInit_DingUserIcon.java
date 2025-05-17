package bp.port.dingtalk.dts;

import bp.en.*;
import bp.port.dingtalk.*;

/** 
 钉钉人员头像同步
*/
public class OrgInit_DingUserIcon extends Method
{
	/** 
	 钉钉人员头像同步
	*/
	public OrgInit_DingUserIcon()
	{
		this.Title = "钉钉人员头像同步到DataUser/Icon";
		this.Help = "本功能将钉钉企业号中所有人员的头像下载到本地，包括一张大图，一张小图";
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
		if (bp.wf.Glo.isEnableDingDing() == true)
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
	public Object Do()
	{
		DingDing ding = new DingDing();
		String savePath = bp.difference.SystemConfig.getPathOfDataUser() + "UserIcon";
		boolean result = ding.DownLoadUserIcon(savePath);
		if (result == true)
		{
			return "执行成功...";
		}
		else
		{
			return "执行失败...";
		}
	}
}
