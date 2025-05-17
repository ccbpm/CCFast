package bp.wf;


/** 
 导入流程的模式
*/
public enum ImpFlowTempleteModel
{
	/** 
	 按新的流程导入
	*/
	AsNewFlow,
	/** 
	 按模版的流程编号
	*/
	AsTempleteFlowNo,
	/** 
	 覆盖当前的流程
	*/
	OvrewaiteCurrFlowNo,
	/** 
	 按指定的流程编号导入
	*/
	AsSpecFlowNo;

	public static final int SIZE = java.lang.Integer.SIZE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ImpFlowTempleteModel forValue(int value)
	{
		return values()[value];
	}
}
