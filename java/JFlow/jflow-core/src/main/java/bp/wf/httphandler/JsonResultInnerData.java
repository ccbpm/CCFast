package bp.wf.httphandler;


/** 
 调Handler，返回值data的通用对象类
*/
public class JsonResultInnerData
{
	/** 
	 信息
	*/
	private String Msg;
	public final String getMsg()
	{
		return Msg;
	}
	public final void setMsg(String value)
	{
		Msg = value;
	}
	/** 
	 返回数据对象
	*/
	private Object InnerData;
	public final Object getInnerData()
	{
		return InnerData;
	}
	public final void setInnerData(Object value)
	{
		InnerData = value;
	}
}
