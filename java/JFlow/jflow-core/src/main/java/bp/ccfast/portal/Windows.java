package bp.ccfast.portal;

import bp.en.*;
import java.util.*;

/** 
 信息块s
*/
public class Windows extends EntitiesMyPK
{

		///#region 构造
	/** 
	 信息块s
	*/
	public Windows()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Window();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Window> ToJavaList()
	{
		return (java.util.List<Window>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Window> Tolist()
	{
		ArrayList<Window> list = new ArrayList<Window>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Window)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

	@Override
	public int RetrieveAll() throws Exception {
		//查询全部模板.
		WindowTemplates templates = new WindowTemplates();
		templates.RetrieveAll();

		Windows windows = new Windows();
		windows.Retrieve(WindowAttr.EmpNo, bp.web.WebUser.getNo(), "Idx");

		//检查是否有新增的？
		for (WindowTemplate en : templates.ToJavaList())
		{
			if (en.getItIsEnable() == false)
			{
				continue; //如果是不启用的.
			}

			//从实例里面获取window..
			bp.en.Entity tempVar = windows.Filter(WindowAttr.WindowTemplateNo, en.getNo());
			Window window = tempVar instanceof Window ? (Window)tempVar : null;
			if (window == null)
			{
				/*不存在，就Insrt.*/
				window = new Window();
				window.setMyPK(en.getNo() + "_" + bp.web.WebUser.getNo());
				window.setEmpNo(bp.web.WebUser.getNo());
				window.setWindowTemplateNo(en.getNo());
				window.setItIsEnable(true);
				window.Insert();
				continue;
			}

			//如果 个人设置 他是启用的.
			if (window.getItIsEnable() == true)
			{
				en.setIdx(window.getIdx()); //就给他顺序号.
			}
			else
			{
				en.setItIsEnable(false);
			}
		}


		//把模板放入到这里.
		this.AddEntities(templates);
		return templates.size();
	}
}
