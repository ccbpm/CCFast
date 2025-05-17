package bp.wf.template;

/**
 流程设计模式-
*/
public enum FlowDevModel
{
	/**
	 专业模式
	*/
	Prefessional(0),
	/**
	 极简模式
	*/
	JiJian(1),
	/**
	 累加模式
	*/
	FoolTruck(2),
	/**
	 绑定单表单
	*/
	RefOneFrmTree(3),
	/**
	 绑定多表单
	*/
	FrmTree(4),
	/**
	 SDK表单-WorkID模式
	*/
	SDKFrmWorkID(5),
	/**
	 SDK表单-自定义主键模式.
	*/
	SDKFrmSelfPK(7),
	/**
	 嵌入式表单
	*/
	SelfFrm(6),
	/**
	 物联网流程
	*/
	InternetOfThings(10),
	/**
	 * 高代码EntityTS
	 */
	EntityTS(11);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, FlowDevModel> mappings;
	private static java.util.HashMap<Integer, FlowDevModel> getMappings()
	{
		if (mappings == null)
		{
			synchronized (FlowDevModel.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, FlowDevModel>();
				}
			}
		}
		return mappings;
	}

	private FlowDevModel(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static FlowDevModel forValue(int value)
	{
		return getMappings().get(value);
	}
}
