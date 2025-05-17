package bp.wf.dts;

import bp.da.*;
import bp.difference.SystemConfig;
import bp.en.*;
import bp.sys.*;
import bp.web.WebUser;
import bp.wf.template.*;
import bp.wf.*;
import java.io.*;

/**
 Method 的摘要说明
 */
public class LoadTemplete extends Method
{
	/**
	 不带有参数的方法
	 */
	public LoadTemplete() throws Exception {
		this.Title = "装载流程演示模板";
		this.Help = "为了帮助各位爱好者学习与掌握ccflow, 特提供一些流程模板与表单模板以方便学习。";
		this.Help += "@这些模板的位于" + SystemConfig.getPathOfWebApp() + "SDKFlowDemo/FlowDemo/";
		this.GroupName = "流程维护";

	}
	/**
	 设置执行变量

	 @return
	 */
	@Override
	public void Init()  {
	}
	/**
	 当前的操纵员是否可以执行这个方法
	 * @throws Exception
	 */
	@Override
	public boolean getIsCanDo()
	{
		if (WebUser.getNo().equals("admin") == true)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	@Override
	public Object Do() throws Exception
	{
		String msg = "";


		///#region 处理表单.
		// 调度表单文件。
		SysFormTrees fss = new SysFormTrees();
		fss.ClearTable();

		//创建root.
		SysFormTree root = new SysFormTree();
		root.setNo("1");
		root.setName("表单库");
		root.setParentNo("0");
		root.Insert();

		String frmPath = SystemConfig.getPathOfWebApp() + "SDKFlowDemo/FlowDemo/Form/";
		File dirInfo = new File(frmPath);
		File[] dirs = dirInfo.listFiles();
		int i = 0;
		for (File item : dirs)
		{
			if (item.getPath().contains(".svn"))
			{
				continue;
			}

			String[] fls = (new File(item.getPath())).list();
			if (fls.length == 0)
			{
				continue;
			}

			SysFormTree fs = new SysFormTree();
			fs.setNo(item.getName().substring(0, 2));
			fs.setName(item.getName().substring(3));
			fs.setParentNo("1");
			fs.setIdx(i++);
			fs.Insert();

			for (String f : fls)
			{
				File info = new File(f);
				String fileName = info.getName();
				int lastIndx = fileName.lastIndexOf('.');
				String ext = fileName.substring(lastIndx);
				if (!ext.equals(".xml"))
				{
					continue;
				}

				msg += "@开始调度表单模板文件:" + f;
				Log.DefaultLogWriteLineInfo("@开始调度表单模板文件:" + f);

				DataSet ds = new DataSet();
				ds.readXml(f);

				try
				{
					MapData md = MapData.ImpMapData(ds);
					md.setFormTreeNo(fs.getNo());
					md.setAppType("0");
					md.Update();
				}
				catch (RuntimeException ex)
				{
					Log.DefaultLogWriteLineInfo("@装载表单模版文件:" + f + "出现错误," + ex.getMessage() + " <br> " + ex.getStackTrace());

					throw new RuntimeException("@装载模版文件:" + f + "出现错误," + ex.getMessage() + " <br> " + ex.getStackTrace());
				}
			}
		}

		///#endregion 处理表单.


		///#region 处理流程.
		FlowSorts sorts = new FlowSorts();
		sorts.ClearTable();
		dirInfo = new File(SystemConfig.getPathOfWebApp() + "SDKFlowDemo/FlowDemo/Flow/");
		dirs = dirInfo.listFiles();

		FlowSort fsRoot = new FlowSort();
		fsRoot.setNo("99");
		fsRoot.setName("流程树");
		fsRoot.setParentNo("0");
		fsRoot.DirectInsert();

		for (File dir : dirs)
		{
			if (dir.getPath().contains(".svn"))
			{
				continue;
			}

			String[] fls = (new File(dir.getPath())).list();
			if (fls.length == 0)
			{
				continue;
			}

			FlowSort fs = new FlowSort();
			fs.setNo(dir.getName().substring(0, 3));
			fs.setName(dir.getName().substring(3));
			fs.setParentNo(fsRoot.getNo());
			fs.Insert();

			for (String filePath : fls)
			{
				msg += "\t\n@开始调度流程模板文件:" + filePath;
				Log.DefaultLogWriteLineInfo("@开始调度流程模板文件:" + filePath);

				Flow myflow = bp.wf.template.TemplateGlo.LoadFlowTemplate(fs.getNo(), filePath, ImpFlowTempleteModel.AsTempleteFlowNo);
				msg += "\t\n@流程:[" + myflow.getName() + "]装载成功。";

				File info = new File(filePath);
				myflow.setName(info.getName().replace(".xml", ""));
				if (myflow.getName().substring(2, 3).equals("."))
				{
					myflow.setName(myflow.getName().substring(3));
				}
				myflow.DirectUpdate();
			}


			//调度它的下一级目录.
			File dirSubInfo = new File(SystemConfig.getPathOfWebApp() + "SDKFlowDemo/FlowDemo/Flow/" + dir.getName());
			File[] myDirs = dirSubInfo.listFiles();
			for (File mydir : myDirs)
			{
				if (mydir.getPath().contains(".svn"))
				{
					continue;
				}

				String[] myfls = (new File(mydir.getPath())).list();
				if (myfls.length == 0)
				{
					continue;
				}

				// 流程类别.
				Object tempVar = fs.DoCreateSubNode();
				FlowSort subFlowSort = tempVar instanceof FlowSort ? (FlowSort)tempVar : null;
				subFlowSort.setName(mydir.getName().substring(3));
				subFlowSort.Update();

				for (String filePath : myfls)
				{
					msg += "\t\n@开始调度流程模板文件:" + filePath;
					Log.DefaultLogWriteLineInfo("@开始调度流程模板文件:" + filePath);

					Flow myflow = bp.wf.template.TemplateGlo.LoadFlowTemplate(subFlowSort.getNo(), filePath, ImpFlowTempleteModel.AsTempleteFlowNo);
					msg += "\t\n@流程:" + myflow.getName() + "装载成功。";

					File info = new File(filePath);
					myflow.setName(info.getName().replace(".xml", ""));
					if (myflow.getName().substring(2, 3).equals("."))
					{
						myflow.setName(myflow.getName().substring(3));
					}
					myflow.DirectUpdate();
				}
			}
		}

		//执行流程检查.
		Flows flsEns = new Flows();
		flsEns.RetrieveAll();
		for (Flow fl : flsEns.ToJavaList())
		{
			fl.DoCheck();
		}

		///#endregion 处理流程.



		Log.DefaultLogWriteLineInfo(msg);

		//删除多余的空格.
		DeleteBlankGroupField dts = new DeleteBlankGroupField();
		dts.Do();

		return msg;
	}
}