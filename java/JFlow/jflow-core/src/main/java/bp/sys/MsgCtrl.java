package bp.sys;


/** 
 消息控制方式
*/
public enum MsgCtrl
{
	/** 
	 bufasong 
	*/
	None,
	/** 
	 按照设置计算
	*/
	BySet,
	/** 
	 按照表单的是否发送字段计算，字段:IsSendMsg
	*/
	ByFrmIsSendMsg,
	/** 
	 按照SDK参数计算.
	*/
	BySDK;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static MsgCtrl forValue(int value)
	{
		return values()[value];
	}
}
