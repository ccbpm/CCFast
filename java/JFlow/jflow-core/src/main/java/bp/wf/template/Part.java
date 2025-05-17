package bp.wf.template;

import bp.da.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.tools.*;

/** 
 配件.	 
*/
public class Part extends EntityMyPK
{

		///#region 基本属性
	/** 
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.IsUpdate = true;
		return uac;
	}
	/** 
	 配件的事务编号
	*/
	public final String getFlowNo()  {
		return this.GetValStringByKey(PartAttr.FlowNo);
	}
	public final void setFlowNo(String value) throws Exception
	{
		SetValByKey(PartAttr.FlowNo, value);
	}
	/** 
	 类型
	*/
	public final String getPartType()  {
		return this.GetValStringByKey(PartAttr.PartType);
	}
	public final void setPartType(String value) throws Exception
	{
		SetValByKey(PartAttr.PartType, value);
	}
	/** 
	 节点ID
	*/
	public final int getNodeID()  {
		return this.GetValIntByKey(PartAttr.NodeID);
	}
	public final void setNodeID(int value) throws Exception
	{
		SetValByKey(PartAttr.NodeID, value);
	}
	/** 
	 字段存储0
	*/
	public final String getTag0()  {
		return this.GetValStringByKey(PartAttr.Tag0);
	}
	public final void setTag0(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag0, value);
	}
	/** 
	 字段存储1
	*/
	public final String getTag1()  {
		return this.GetValStringByKey(PartAttr.Tag1);
	}
	public final void setTag1(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag1, value);
	}
	/** 
	 字段存储2
	*/
	public final String getTag2()  {
		return this.GetValStringByKey(PartAttr.Tag2);
	}
	public final void setTag2(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag2, value);
	}
	/** 
	 字段存储3
	*/
	public final String getTag3()  {
		return this.GetValStringByKey(PartAttr.Tag3);
	}
	public final void setTag3(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag3, value);
	}
	/** 
	 字段存储4
	*/
	public final String getTag4()  {
		return this.GetValStringByKey(PartAttr.Tag4);
	}
	public final void setTag4(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag4, value);
	}
	/** 
	 字段存储5
	*/
	public final String getTag5()  {
		return this.GetValStringByKey(PartAttr.Tag5);
	}
	public final void setTag5(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag5, value);
	}
	/** 
	 字段存储6
	*/
	public final String getTag6()  {
		return this.GetValStringByKey(PartAttr.Tag6);
	}
	public final void setTag6(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag6, value);
	}
	/** 
	 字段存储7
	*/
	public final String getTag7()  {
		return this.GetValStringByKey(PartAttr.Tag7);
	}
	public final void setTag7(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag7, value);
	}
	/** 
	 字段存储8
	*/
	public final String getTag8()  {
		return this.GetValStringByKey(PartAttr.Tag8);
	}
	public final void setTag8(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag8, value);
	}
	/** 
	 字段存储9
	*/
	public final String getTag9()  {
		return this.GetValStringByKey(PartAttr.Tag9);
	}
	public final void setTag9(String value) throws Exception
	{
		SetValByKey(PartAttr.Tag9, value);
	}

		///#endregion


		///#region 构造函数
	/** 
	 配件
	*/
	public Part()
	{
	}
	/** 
	 配件
	 
	 @param mypk 配件ID	
	*/
	public Part(String mypk) throws Exception
	{
		this.setMyPK(mypk);
		this.Retrieve();
	}
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		Map map = new Map("WF_Part", "配件");

		map.AddMyPK(true);

		map.AddTBString(PartAttr.FlowNo, null, "流程编号", false, true, 0, 5, 10);
		map.AddTBInt(PartAttr.NodeID, 0, "节点ID", false, false);
		map.AddTBString(PartAttr.PartType, null, "类型", false, true, 0, 100, 10);

		map.AddTBString(PartAttr.Tag0, null, "Tag0", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag1, null, "Tag1", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag2, null, "Tag2", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag3, null, "Tag3", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag4, null, "Tag4", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag5, null, "Tag5", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag6, null, "Tag6", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag7, null, "Tag7", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag8, null, "Tag8", false, true, 0, 200, 10);
		map.AddTBString(PartAttr.Tag9, null, "Tag9", false, true, 0, 200, 10);

		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion

	/** 
	 执行测试.
	 
	 @param paras
	 @return 
	*/
	public final String DoTestARWebApi(String paras) throws Exception {
		if (paras.contains("@WorkID") == false || paras.contains("@OID") == false)
		{
			return "err@参数模式是表单全量模式，您没有传入workid参数.";
		}

		//获得参数.
		AtPara ap = new AtPara(paras);
		int workID = 0;
		if (ap.getHisHT().containsKey("OID") == true)
		{
			workID = ap.GetValIntByKey("OID", 0);
		}
		else
		{
			workID = ap.GetValIntByKey("WorkID", 0);
		}

		String url = this.getTag0(); //url.
		String urlUodel = this.getTag1(); //模式. Post,Get
		String paraMode = this.getTag2(); //参数模式. 0=自定义模式， 1=全量模式.
		String pdocs = this.getTag3(); //参数内容.  对自定义模式有效.

		//处理url里的参数.
		for (String item : ap.getHisHT().keySet())
		{
			url = url.replace("@" + item, ap.GetValStrByKey(item));
		}

		//全量参数模式. 
		if (paraMode.equals("1") == true)
		{
			GEEntity geEntity = new GEEntity("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt", workID);
			pdocs = geEntity.ToJson(false);
		}
		else
		{
			pdocs = pdocs.replace("`", "\"");
			//自定义参数模式.
			pdocs = bp.wf.Glo.DealExp(pdocs, null);
			for (String item : ap.getHisHT().keySet())
			{
				pdocs = pdocs.replace("@" + item, ap.GetValStrByKey(item));
			}

			if (pdocs.contains("@") == true)
			{
				return "err@TestAPI参数不完整:" + pdocs;
			}
		}

		//判断提交模式.
		if (urlUodel.toLowerCase().equals("get") == true)
		{
			return DataType.ReadURLContext(url, 9000); //返回字符串.
		}

		try
		{
			String doc = PubGlo.HttpPostConnect(url, pdocs, "POST", false);
			return doc;
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage() + " - " + url;
		}
	}
	public final String ARWebApi(String paras) throws Exception {
		if (paras.contains("@WorkID") == false || paras.contains("@OID") == false)
		{
			return "err@参数模式是表单全量模式，您没有传入workid参数.";
		}

		//获得参数.
		AtPara ap = new AtPara(paras);
		int workID = 0;
		if (ap.getHisHT().containsKey("OID") == true)
		{
			workID = ap.GetValIntByKey("OID", 0);
		}
		else
		{
			workID = ap.GetValIntByKey("WorkID", 0);
		}

		GEEntity geEntity = new GEEntity("ND" + Integer.parseInt(this.getFlowNo()) + "Rpt", workID);

		String url = this.getTag0(); //url.
		url = bp.wf.Glo.DealExp(url, geEntity);

		String urlUodel = this.getTag1(); //模式. Post,Get
		String paraMode = this.getTag2(); //参数模式. 0=自定义模式， 1=全量模式.
		String pdocs = this.getTag3(); //参数内容.  对自定义模式有效.

		//全量参数模式. 
		if (paraMode.equals("1") == true)
		{
			pdocs = geEntity.ToJson(false);
		}
		else
		{
			pdocs = pdocs.replace("~", "\"");
			pdocs = bp.wf.Glo.DealExp(pdocs, geEntity);
			if (pdocs.contains("@") == true)
			{
				return "err@参数不完整:" + pdocs;
			}
			pdocs = pdocs.replace("'", "\"");
		}

		//判断提交模式.
		if (urlUodel.toLowerCase().equals("get") == true)
		{
			return DataType.ReadURLContext(url, 9000); //返回字符串.
		}

		boolean isJson = false;
		if (this.getTag4().trim().equals("1") == true)
		{
			isJson = true;
		}

		String doc = PubGlo.HttpPostConnect(url, pdocs,"POST", isJson);
		return doc;
	}
}
