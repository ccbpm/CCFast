package bp.ccoa.worklog;

import bp.web.*;
import bp.en.*;
import bp.port.*;
import java.util.*;

/** 
 日志共享 s
*/
public class WorkShares extends EntitiesMyPK
{
	public final String DoAddEmp(String empNo)
	{
		return "err@执行失败.";
	}

	public final String ShareTo_Init() throws Exception {
		this.Retrieve(WorkShareAttr.EmpNo, WebUser.getNo(), null);

		if (this.size() == 0)
		{
			Emps emps = new Emps();
			emps.Retrieve(EmpAttr.FK_Dept, WebUser.getDeptNo(), null);

			for (Emp emp : emps.ToJavaList())
			{
				WorkShare en = new WorkShare();
				en.setShareToEmpNo(emp.getNo());
				en.setShareToEmpName(emp.getName());
				en.setShareState(1);
				en.Insert();
			}

			this.Retrieve(WorkShareAttr.EmpNo, WebUser.getNo(), null);
		}

		return this.ToJson("dt");
	}
	/** 
	 日志共享
	*/
	public WorkShares()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new WorkShare();
	}

		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<WorkShare> ToJavaList()
	{
		return (java.util.List<WorkShare>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<WorkShare> Tolist()
	{
		ArrayList<WorkShare> list = new ArrayList<WorkShare>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((WorkShare)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
