package bp.wf.template;

import bp.en.*;
import bp.en.Map;

/** 
 任务
*/
public class SubSystem extends EntityNoName
{

		///#region 属性
	/** 
	 参数
	*/
	public final String getApiParas()  {
		return this.GetValStringByKey(SubSystemAttr.ApiParas);
	}
	/** 
	 发起人
	*/
	public final int getCallMaxNum()  {
		return this.GetValIntByKey(SubSystemAttr.CallMaxNum);
	}
	/** 
	 到达的人员
	*/
	public final String getRequestMethod()  {
		return this.GetValStringByKey(SubSystemAttr.RequestMethod);
	}
	public final String getCallBack()  {
		return this.GetValStringByKey(SubSystemAttr.CallBack);
	}
	public final String getTokenPublie()  {
		return this.GetValStringByKey(SubSystemAttr.TokenPublie);
	}
	public final String getTokenPiv()  {
		return this.GetValStringByKey(SubSystemAttr.TokenPiv);
	}
	public final String getWebHost()  {
		return this.GetValStringByKey(SubSystemAttr.WebHost);
	}
	public final boolean getItIsJson() throws Exception {
		String str = this.GetValStringByKey(SubSystemAttr.ParaDTModel);
		if (str.equals("1") == true)
		{
			return true;
		}
		return false;
	}

		///#endregion


		///#region 构造函数
	/** 
	 SubSystem
	*/
	public SubSystem()
	{
	}
	public SubSystem(String no) throws Exception
	{
		this.setNo(no);
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
		Map map = new Map("WF_FlowSort", "子系统");

		map.AddTBStringPK("No", null, "编号", true, false, 0, 5, 10);
		map.AddTBString("Name", null, "名称", true, false, 0, 200, 10);

		map.AddTBString("WebHost", null, "系统根路径", true, false, 0, 200, 30, true);
		map.AddTBString("TokenPiv", null, "系统私钥", true, false, 0, 200, 30, true);
		map.AddTBString("TokenPublie", null, "系统公钥", true, false, 0, 200, 30, true);
		map.AddTBString("CallBack", null, "系统回调审批态的url全路径", true, false, 0, 200, 30, true);
		map.AddDDLStringEnum("RequestMethod", "POST", "请求模式", "@POST=POST@Get=Get", true, "", false);
		map.AddDDLStringEnum("ParaDTModel", "1", "数据格式", "@0=From格式@1=JSON格式", true, "", false);

		map.AddTBInt("CallMaxNum", 5, "最大回调次数", true, false);
		map.AddTBStringDoc("ApiParas", null, "参数格式", true, false, true, 10);
		map.AddTBStringDoc("ApiNote", null, "备注", true, false, true, 10);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion
}
