package bp.wf.dts;

import bp.da.DataSet;
import bp.da.DataType;
import bp.da.XmlWriteMode;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.sys.CCFormAPI;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;
import java.io.*;
import java.time.*;

/** 
 Method 的摘要说明
*/
public class GenerTemplate extends Method
{
	/** 
	 不带有参数的方法
	*/
	public GenerTemplate()
	{
		this.Title = "生成流程模板与表单模板";
		this.Help = "把系统中的流程与表单转化成模板放在指定的目录下。";
		this.getHisAttrs().AddTBString("Path", "C:/ccflow.Template", "生成的路径", true, false, 1, 1900, 200);
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
		return true;
	}
	/** 
	 执行
	 
	 @return 返回执行结果
	*/
	@Override
	public Object Do() throws Exception {
		String path = this.GetValStrByKey("Path") + "_" + DataType.getCurrentDataTime();
		if ((new File(path)).isDirectory())
		{
			return "系统正在执行中，请稍后。";
		}

		(new File(path)).mkdirs();
		(new File(path + "/Flow.流程模板")).mkdirs();
		(new File(path + "/Frm.表单模板")).mkdirs();

		Flows fls = new Flows();
		fls.RetrieveAll();
		FlowSorts sorts = new FlowSorts();
		sorts.RetrieveAll();

		// 生成流程模板。
		for (FlowSort sort : sorts.ToJavaList())
		{
			String pathDir = path + "/Flow.流程模板/" + sort.getNo() + "." + sort.getName();
			(new File(pathDir)).mkdirs();
			for (Flow fl : fls.ToJavaList())
			{
				fl.DoExpFlowXmlTemplete(pathDir);
			}
		}

		// 生成表单模板。
		for (FlowSort sort : sorts.ToJavaList())
		{
			String pathDir = path + "/Frm.表单模板/" + sort.getNo() + "." + sort.getName();
			(new File(pathDir)).mkdirs();
			for (Flow fl : fls.ToJavaList())
			{
				String pathFlowDir = pathDir + "/" + fl.getNo() + "." + fl.getName();
				(new File(pathFlowDir)).mkdirs();
				Nodes nds = new Nodes(fl.getNo());
				for (Node nd : nds.ToJavaList())
				{
					MapData md = new MapData("ND" + nd.getNodeID());
					DataSet ds = CCFormAPI.GenerHisDataSet(md.getNo(), null, null);
					ds.WriteXml(pathFlowDir + "/" + nd.getNodeID() + "." + nd.getName() + ".Frm.xml", XmlWriteMode.WriteSchema, ds);
				}
			}
		}

		// 独立表单模板.
		SysFormTrees frmSorts = new SysFormTrees();
		frmSorts.RetrieveAll();
		for (SysFormTree sort : frmSorts.ToJavaList())
		{
			String pathDir = path + "/Frm.表单模板/" + sort.getNo() + "." + sort.getName();
			(new File(pathDir)).mkdirs();

			MapDatas mds = new MapDatas();
			mds.Retrieve(MapDataAttr.FK_FormTree, sort.getNo(), null);
			for (MapData md : mds.ToJavaList())
			{
				DataSet ds = CCFormAPI.GenerHisDataSet(md.getNo(), null, null);
				ds.WriteXml(pathDir + "/" + md.getNo() + "." + md.getName() +".Frm.xml",XmlWriteMode.WriteSchema, ds);
			}
		}
		return "生成成功，请打开" + path + "。<br>如果您想共享出来请压缩后发送到template＠ccbpm.cn";
	}
}
