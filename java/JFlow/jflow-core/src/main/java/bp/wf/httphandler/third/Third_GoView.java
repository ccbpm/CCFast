package bp.wf.httphandler.third;

import bp.ccfast.third.goview.GoviewProject;
import bp.web.WebUser;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 大屏操作
*/
public class Third_GoView extends bp.difference.handler.DirectoryPageBase
{

    //创建大屏
	public final String CreateProject() throws Exception {
		String myPk = this.GetRequestVal("myPk").trim();
		GoviewProject goviewProject = new GoviewProject();
		goviewProject.setProjectname(myPk);
		goviewProject.setMyPK(myPk);
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		goviewProject.setCreatetime(dateString);
		goviewProject.setCreateuserid(WebUser.getNo());
		goviewProject.setState(-1);
		goviewProject.setIsTemplate(-1);
		int b = goviewProject.Insert();
		return "创建成功";
	}
}
