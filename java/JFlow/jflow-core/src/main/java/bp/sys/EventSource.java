package bp.sys;
/** 
 事件类型
*/
public enum EventSource
{
	/** 
	 表单
	*/
	Frm,
	/** 
	 流程
	*/
	Flow,
	/** 
	 节点
	*/
	Node;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EventSource forValue(int value)
	{
		return values()[value];
	}
}
