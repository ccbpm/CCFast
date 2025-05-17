package bp.port.dingtalk.DingModel;

/**
 创建部门后消息
*/
public class CreateDepartMent_PostVal
{
	/** 
	 返回码
	*/
	private String errcode;
	public final String getErrcode()
	{
		return errcode;
	}
	public final void setErrcode(String value)
	{
		errcode = value;
	}
	/** 
	 对返回码的文本描述内容
	*/
	private String errmsg;
	public final String getErrmsg()
	{
		return errmsg;
	}
	public final void setErrmsg(String value)
	{
		errmsg = value;
	}
	/** 
	 创建的部门id
	*/
	private String id;
	public final String getId()
	{
		return id;
	}
	public final void setId(String value)
	{
		id = value;
	}
}
