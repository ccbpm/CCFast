package bp.wf;


/**
 投递方式
*/
public enum DeliveryWay
{
	/**
	 按角色(以部门为纬度)
	*/
	ByStation(0),
	/**
	 本部门内的人员
	*/
	FindSpecDeptEmpsInStationlist(19),
	/**
	 按部门
	*/
	ByDept(1),
	/**
	 按SQL
	*/
	BySQL(2),
	/**
	 按本节点绑定的人员
	*/
	ByBindEmp(3),
	/**
	 由上一步发送人选择
	*/
	BySelected(4),
	/**
	 所有人员都可以发起
	*/
	BySelected_1(41),
	/**
	 固定范围的选择
	*/
	BySelected_2(60),
	/**
	 按表单选择人员
	*/
	ByPreviousNodeFormEmpsField(5),
	/**
	 从表里的人员编号字段
	 */
	ByPreviousNodeFormEmpsFrmDtl(501),
	/**
	 与上一节点的人员相同
	*/
	ByPreviousNodeEmp(6),
	/**
	 与开始节点的人员相同
	*/
	ByStarter(7),
	/**
	 与指定节点的人员相同
	*/
	BySpecNodeEmp(8),
	/**
	 按角色与部门交集计算
	*/
	ByDeptAndStation(9),
	/**
	 按角色计算(以部门集合为纬度)
	*/
	//ByStationAndEmpDept = 10,
	/**
	 按指定节点的人员或者指定字段作为人员的角色计算
	*/
	BySpecNodeEmpStation(11),
	/**
	 按SQL确定子线程接受人与数据源.
	*/
	BySQLAsSubThreadEmpsAndData(12),
	/**
	 按明细表确定子线程接受人.
	*/
	ByDtlAsSubThreadEmps(13),
	/**
	 仅按角色计算
	*/
	ByStationOnly(14),
	/**
	 FEE计算.
	*/
	ByFEE(15),
	/**
	 按绑定部门计算,该部门一人处理标识该工作结束(子线程).
	*/
	BySetDeptAsSubthread(16),
	/**
	 按SQL模版计算
	*/
	BySQLTemplate(17),
	/**
	 从人员到人员
	*/
	ByFromEmpToEmp(18),
	/// <summary>
	/// 部门路由
	/// </summary>
	ByFromDeptToEmp(1801),
	/// <summary>
	/// 字段路由.
	/// </summary>
	ByFromAttrToEmp (1802),
	/**
	 按照角色计算-范围内的
	*/
	ByStationForPrj(20),
	/**
	 按照选择模式计算.
	*/
	BySelectedForPrj(21),
	/**
	 按照设置的组织计算
	*/
	BySelectedOrgs(22),
	/**
	 按照部门领导计算
	*/
	ByDeptLeader(23),
	/**
	 按照部门分管领导计算
	*/
	ByDeptShipLeader(28),
	/**
	 找自己的直属领导.
	*/
	ByEmpLeader(50),
	/**
	 按照用户组计算(本组织范围内)
	*/
	ByTeamOrgOnly(24),
	/**
	 按照用户组计算(全集团)
	*/
	ByTeamOnly(25),
	/**
	 按照用户组计算(本部门范围内)
	*/
	ByTeamDeptOnly(26),
	/**
	 按照绑定角色的用户组人员
	*/
	ByBindTeamEmp(27),
	/**
	 按照组织模式人员选择器
	*/
	BySelectedEmpsOrgModel(43),
	/**
	 按照自定义Url模式的人员选择器
	*/
	BySelfUrl(44),
	/**
	 按照设置的WebAPI接口获取的数据计算
	*/
	ByAPIUrl(45),
	/**
	 发送人的上级部门的负责人
	 就是找上级领导主管.
	*/
	BySenderParentDeptLeader(46),
	/**
	 发送人上级部门指定的角色
	*/
	BySenderParentDeptStations(47),
	/**
	 外部用户
	*/
	ByGuest(51),
	/**
	 字段是权限组.
	 */
	ByPreviousNodeFormEmpsTeam (502),
	/**
	 按照部门计算
	*/
	ByPreviousNodeFormDepts(503),
	/**
	 按照角色计算
	*/
	ByPreviousNodeFormStationsAI(53),
	/**
	 智能计算
	*/
	ByPreviousNodeFormStationsOnly(54),
	/**
	 选择其他组织的联络员
	*/
	BySelectEmpByOfficer(55),
	/**
	 绑定字典表
	*/
	BySFTable(52),
	/**
	 按指定的部门集合与设置的岗位交集
	*/
	ByStationSpecDepts(56),
	/**
	 按指定的角色集合与设置的部门交集
	*/
	ByStationSpecStas(57),

	//分管领导.
	ByDeptSpecer(58),
	/**
	 连续多级主管 - 指定角色模式 模式
	 **/
	ByMLeader0(701),
	/**
	 * 连续多级主管 - 指定指定通讯录等级 模式
	 */
	ByMLeader1(702),
	/**
	 按照ccflow的BPM模式处理
	*/
	ByCCFlowBPM(100),
	/**
	 * 预置:自由选择.
	 */
	PreplaceWokerFree(710),
	/**
	 * 预置:固定模式
	 */
	PreplaceWokerFix(711);

	public static final int SIZE = java.lang.Integer.SIZE;

	private int intValue;
	private static java.util.HashMap<Integer, DeliveryWay> mappings;
	private static java.util.HashMap<Integer, DeliveryWay> getMappings()
	{
		if (mappings == null)
		{
			synchronized (DeliveryWay.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, DeliveryWay>();
				}
			}
		}
		return mappings;
	}

	private DeliveryWay(int value)
	{
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static DeliveryWay forValue(int value)
	{
		return getMappings().get(value);
	}
}
