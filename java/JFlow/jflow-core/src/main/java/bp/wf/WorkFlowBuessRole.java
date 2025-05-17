package bp.wf;

import bp.da.*;
import bp.tools.DateUtils;
import bp.tools.StringHelper;
import bp.tools.StringUtils;
import bp.wf.template.*;
import bp.sys.*;
import bp.en.*;
import bp.port.*;
import bp.web.*;
import bp.*;
import java.util.*;
import java.time.*;
import java.util.Map;

/** 
 工作流程业务规则
*/
public class WorkFlowBuessRole
{

	/**
	 * 处理流程标题生成规则[@AtPara.参数Key]
	 * 按照参数字段替换标题
	 * 如: @AtPara.xxTitle会获取GenerWorkFlow的AtPara字段@xxTitle=XXX的标题
	 * tip:如果没有获取到标题,就使用系统默认标题
	 * @param titleRole
	 * @param workID
	 * @return
	 */
	public static String processAtPara(String titleRole, long workID){
		// 求出GenerWorkFlow
		GenerWorkFlow gwf = new GenerWorkFlow();
		int i = 0;
		try {
			gwf.setWorkID(workID);
			i = gwf.RetrieveFromDBSources();
		}catch (Exception ex){
			Log.DebugWriteError(ex);
		}
		// 如果没有数据就不执行替换
		if (i > 0) {
			// 标题生成规则必须为 @AtPara.参数Key
			String key = titleRole.split("@AtPara.")[1];
			if(!DataType.IsNullOrEmpty(key)){
				AtPara atPara = gwf.getatPara();
				// 判断键是否存在
				if(!atPara.getHisHT().containsKey(key)){
					Log.DebugWriteError("err@您设置的标题生成规则=[" + titleRole + "]，在AtPara没有找到键=[" + key + "].");
					return "";
				}
				// 获取AtPara中的键值
				String val = atPara.GetValStrByKey(key);
				if(DataType.IsNullOrEmpty(val)){
					Log.DebugWriteError("err@AtPara键=[" + key + "],值=[" + val + "],值为空,当前WorkID[" + workID + "]");
					return "";
				}
				return val;
			}
		}
		return "";
	}

	///#region 生成标题的方法.
	/** 
	 生成标题
	 
	 @param wk 工作
	 @param emp 人员
	 @param rdt 日期
	 @return 生成string.
	*/
	public static String GenerTitle(Flow fl, Work wk, Emp emp, String rdt) throws Exception {
		String titleRole = fl.getTitleRole();
		// 判斷是否存在@AtPara需要替換
		if (titleRole.contains("@AtPara"))
		{
			titleRole = processAtPara(titleRole, wk.getOID());
		}
		if (DataType.IsNullOrEmpty(titleRole))
		{
			// 为了保持与ccflow4.5的兼容,从开始节点属性里获取.
			Attr myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			if (myattr == null)
			{
				myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			}

			if (myattr != null)
			{
				titleRole = myattr.getDefaultVal().toString();
			}

			if (DataType.IsNullOrEmpty(titleRole) || titleRole.contains("@") == false)
			{
				titleRole = "@WebUser.FK_DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
			}
		}

		titleRole = titleRole.replace("@WebUser.No", emp.getUserID());
		titleRole = titleRole.replace("@WebUser.Name", emp.getName());
		titleRole = titleRole.replace("@WebUser.FK_DeptNameOfFull", WebUser.getDeptNameOfFull());
		titleRole = titleRole.replace("@WebUser.FK_DeptName", emp.getDeptText());
		titleRole = titleRole.replace("@WebUser.FK_Dept", emp.getDeptNo());
		titleRole = titleRole.replace("@WebUser.DeptNo", emp.getDeptNo());
		titleRole = titleRole.replace("@RDT", rdt);
		if (titleRole.contains("@") == true)
		{
			Attrs attrs = wk.getEnMap().getAttrs();

			// 优先考虑外键的替换。
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}
				if (attr.getItIsRefAttr()  == false)
				{
					continue;
				}
				titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
			}

			//在考虑其它的字段替换.
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}
				if (attr.getItIsRefAttr()  == true)
				{
					continue;
				}
				if (attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.DDL && attr.getMyFieldType() == FieldType.Normal)
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey() + "T"));
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}
				else
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}

			}
		}
		titleRole = titleRole.replace('~', '-');
		titleRole = titleRole.replace("'", "”");

		if (titleRole.contains("@"))
		{
			/*如果没有替换干净，就考虑是用户字段拼写错误*/
			throw new RuntimeException("@请检查是否是字段拼写错误，标题中有变量没有被替换下来. @" + titleRole);
		}

		if (titleRole.contains("@"))
		{
			titleRole = GenerTitleExt(fl, wk.getNodeID(), wk.getOID(), titleRole);
		}

		wk.SetValByKey("Title", titleRole);
		return titleRole;
	}
	/** 
	 生成标题
	 
	 @param wk
	 @return 
	*/
	public static String GenerTitle(Flow fl, Work wk) throws Exception {
		String titleRole = fl.getTitleRole();
		// 判斷是否存在@AtPara需要替換
		if (titleRole.contains("@AtPara"))
		{
			titleRole = processAtPara(titleRole, wk.getOID());
		}
		if (DataType.IsNullOrEmpty(titleRole))
		{
			// 为了保持与ccflow4.5的兼容,从开始节点属性里获取.
			Attr myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			if (myattr == null)
			{
				myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			}

			if (myattr != null)
			{
				titleRole = myattr.getDefaultVal().toString();
			}

			if (DataType.IsNullOrEmpty(titleRole) || titleRole.contains("@") == false)
			{
				titleRole = "@WebUser.FK_DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
			}
		}

		if (Objects.equals(titleRole, "@OutPara") || DataType.IsNullOrEmpty(titleRole) == true)
		{
			titleRole = "@WebUser.FK_DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
		}

		titleRole = titleRole.replace("@WebUser.No", WebUser.getNo());
		titleRole = titleRole.replace("@WebUser.Name", WebUser.getName());
		titleRole = titleRole.replace("@WebUser.FK_DeptNameOfFull", WebUser.getDeptNameOfFull());

		titleRole = titleRole.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
		titleRole = titleRole.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		titleRole = titleRole.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		titleRole = titleRole.replace("@RDT", DataType.getCurrentDateTime());
		titleRole = titleRole.replace("@FlowName", fl.getName());


		if (titleRole.contains("@"))
		{
			Attrs attrs = wk.getEnMap().getAttrs();

			// 优先考虑外键的替换 , 因为外键文本的字段的长度相对较长。
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}
				if (attr.getItIsRefAttr()  == false)
				{
					continue;
				}
				titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
			}

			//在考虑其它的字段替换.
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}

				if (attr.getItIsRefAttr()  == true)
				{
					continue;
				}
				if (attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.DDL && attr.getMyFieldType() == FieldType.Normal)
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey() + "T"));
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}
				//复选框
				if (attr.getMyFieldType() == FieldType.Enum && titleRole.contains("Checkboxs"))
				{
					String content = wk.GetValStrByKey(attr.getKey());
					if (DataType.IsNullOrEmpty(attr.getUIBindKey()) == true)
					{
						titleRole = titleRole.replace("@" + attr.getKey() + "Checkboxs", content);
						continue;
					}
					SysEnums enums = new SysEnums(attr.getUIBindKey());
					String str = "";
					for (SysEnum en : enums.ToJavaList())
					{
						if ((content + ",").contains(en.getIntKey() + ",") == true)
						{
							str += en.getLab() + ",";
						}
					}
					titleRole = titleRole.replace("@" + attr.getKey() + "Checkboxs", str);

				}
				if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum)
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey() + "Text"));
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}
				else
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}
			}
		}
		titleRole = titleRole.replace('~', '-');
		titleRole = titleRole.replace("'", "”");

		if (titleRole.contains("@"))
		{
			titleRole = GenerTitleExt(fl, wk.getNodeID(), wk.getOID(), titleRole);
		}

		// 为当前的工作设置title.
		wk.SetValByKey("Title", titleRole);

		return titleRole;
	}
	/** 
	 生成标题
	 
	 @param fl
	 @param wk
	 @return 
	*/
	public static String GenerTitle(Flow fl, GERpt wk) throws Exception {
		String titleRole = fl.getTitleRole();
		// 判斷是否存在@AtPara需要替換
		if (titleRole.contains("@AtPara"))
		{
			titleRole = processAtPara(titleRole, wk.getOID());
		}
		if (DataType.IsNullOrEmpty(titleRole))
		{
			// 为了保持与ccflow4.5的兼容,从开始节点属性里获取.
			Attr myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			if (myattr == null)
			{
				myattr = wk.getEnMap().getAttrs().GetAttrByKey("Title");
			}

			if (myattr != null)
			{
				titleRole = myattr.getDefaultVal().toString();
			}

			if (DataType.IsNullOrEmpty(titleRole) || titleRole.contains("@") == false)
			{
				titleRole = "@WebUser.FK_DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
			}
		}

		if (Objects.equals(titleRole, "@OutPara") || DataType.IsNullOrEmpty(titleRole) == true)
		{
			titleRole = "@WebUser.FK_DeptName-@WebUser.No,@WebUser.Name在@RDT发起.";
		}


		titleRole = titleRole.replace("@WebUser.No", wk.getFlowStarter());
		titleRole = titleRole.replace("@WebUser.Name", WebUser.getName());
		titleRole = titleRole.replace("@WebUser.FK_DeptNameOfFull", WebUser.getDeptNameOfFull());
		titleRole = titleRole.replace("@WebUser.FK_DeptName", WebUser.getDeptName());
		titleRole = titleRole.replace("@WebUser.FK_Dept", WebUser.getDeptNo());
		titleRole = titleRole.replace("@WebUser.DeptNo", WebUser.getDeptNo());
		titleRole = titleRole.replace("@RDT", wk.getFlowStartRDT());
		if (titleRole.contains("@"))
		{
			Attrs attrs = wk.getEnMap().getAttrs();

			// 优先考虑外键的替换,因为外键文本的字段的长度相对较长。
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}
				if (attr.getItIsRefAttr()  == false)
				{
					continue;
				}
				titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
			}

			//在考虑其它的字段替换.
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}

				if (attr.getItIsRefAttr()  == true)
				{
					continue;
				}
				if (attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.DDL && attr.getMyFieldType() == FieldType.Normal)
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey() + "T"));
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}
				if (attr.getMyFieldType() == FieldType.Enum || attr.getMyFieldType() == FieldType.PKEnum)
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey() + "Text"));
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}
				else
				{
					titleRole = titleRole.replace("@" + attr.getKey(), wk.GetValStrByKey(attr.getKey()));
				}


			}
		}
		titleRole = titleRole.replace('~', '-');
		titleRole = titleRole.replace("'", "”");

		if (titleRole.contains("@"))
		{
			titleRole = GenerTitleExt(fl, Integer.parseInt(fl.getNo() + "01"), wk.getOID(), titleRole);
		}

		// 为当前的工作设置title.
		wk.SetValByKey("Title", titleRole);
		return titleRole;
	}
	/** 
	 如果从节点表单上没有替换下来，就考虑独立表单的替换.
	 
	 @param fl 流程
	 @param workid 工作ID
	 @return 返回生成的标题
	*/
	private static String GenerTitleExt(Flow fl, int nodeId, long workid, String titleRole) throws Exception {
		FrmNodes nds = new FrmNodes(fl.getNo(), nodeId);
		for (FrmNode item : nds.ToJavaList())
		{
			GEEntity en = null;
			try
			{
				en = new GEEntity(item.getFrmID());
				en.setPKVal(workid);
				if (en.RetrieveFromDBSources() == 0)
				{
					continue;
				}
			}
			catch (Exception ex)
			{
				continue;
			}

			Attrs attrs = en.getEnMap().getAttrs();
			// 优先考虑外键的替换,因为外键文本的字段的长度相对较长。
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}
				if (attr.getItIsRefAttr()  == false)
				{
					continue;
				}
				titleRole = titleRole.replace("@" + attr.getKey(), en.GetValStrByKey(attr.getKey()));
			}

			//在考虑其它的字段替换.
			for (Attr attr : attrs)
			{
				if (titleRole.contains("@") == false)
				{
					break;
				}

				if (attr.getItIsRefAttr()  == true)
				{
					continue;
				}
				if (attr.getMyDataType() == DataType.AppString && attr.getUIContralType() == UIContralType.DDL && attr.getMyFieldType() == FieldType.Normal)
				{
					titleRole = titleRole.replace("@" + attr.getKey(), en.GetValStrByKey(attr.getKey() + "T"));
					titleRole = titleRole.replace("@" + attr.getKey(), en.GetValStrByKey(attr.getKey()));
				}
				else
				{
					titleRole = titleRole.replace("@" + attr.getKey(), en.GetValStrByKey(attr.getKey()));
				}


			}

			//如果全部已经替换完成.
			if (titleRole.contains("@") == false)
			{
				return titleRole;
			}
		}
		return titleRole;
	}

		///#endregion 生成标题的方法.


		///#region 产生单据编号
	/** 
	 产生单据编号
	 
	 @param billNo
	 @param workid
	 @param en
	 @param flowPTable
	 @return 
	*/
	public static String GenerBillNo(String billNo, long workid, Entity en, String flowPTable) throws Exception {
		if (DataType.IsNullOrEmpty(billNo))
		{
			return "";
		}

		if (billNo.contains("@"))
		{
			billNo = bp.wf.Glo.DealExp(billNo, en, null);
		}

		/*如果，Bill 有规则 */
		billNo = billNo.replace("{YYYY}", DateUtils.format(new Date(), "yyyy"));
		billNo = billNo.replace("{yyyy}", DateUtils.format(new Date(), "yyyy"));

		billNo = billNo.replace("{yy}", DateUtils.format(new Date(), "yy"));
		billNo = billNo.replace("{YY}", DateUtils.format(new Date(), "yy"));

		billNo = billNo.replace("{MM}", DateUtils.format(new Date(), "MM"));
		billNo = billNo.replace("{mm}", DateUtils.format(new Date(), "mm"));

		billNo = billNo.replace("{DD}",DateUtils.format(new Date(), "dd"));
		billNo = billNo.replace("{dd}",DateUtils.format(new Date(), "dd"));

		billNo = billNo.replace("{HH}", DateUtils.format(new Date(), "HH"));
		billNo = billNo.replace("{hh}", DateUtils.format(new Date(), "HH"));

		billNo = billNo.replace("{LSH}", (new Long(workid)).toString());
		billNo = billNo.replace("{WorkID}", (new Long(workid)).toString());
		billNo = billNo.replace("{OID}", (new Long(workid)).toString());

		if (billNo.contains("@WebUser.DeptZi"))
		{
			String val = DBAccess.RunSQLReturnStringIsNull("SELECT Zi FROM Port_Dept WHERE No='" + WebUser.getDeptNo()+ "'", "");
			billNo = billNo.replace("@WebUser.DeptZi", val.toString());
		}

		if (billNo.contains("{ParentBillNo}"))
		{
			String pWorkID = DBAccess.RunSQLReturnStringIsNull("SELECT PWorkID FROM " + flowPTable + " WHERE   WFState >1 AND  OID=" + workid, "0");
			String parentBillNo = DBAccess.RunSQLReturnStringIsNull("SELECT BillNo FROM WF_GenerWorkFlow WHERE WorkID=" + pWorkID, "");
			billNo = billNo.replace("{ParentBillNo}", parentBillNo);

			String sql = "";
			int num = 0;
			for (int i = 2; i < 9; i++)
			{
				if (billNo.contains("{LSH" + i + "}") == false)
				{
					continue;
				}

				sql = "SELECT COUNT(OID) FROM " + flowPTable + " WHERE PWorkID =" + pWorkID + " AND WFState >1 ";
				num = DBAccess.RunSQLReturnValInt(sql, 0);
				billNo = billNo + bp.tools.StringHelper.padLeft(String.valueOf(num), i, '0');
				billNo = billNo.replace("{LSH" + i + "}", "");
				break;
			}
		}
		else
		{
			String sql = "";
			int num = 0;
			String supposeBillNo = billNo; //假设单据号，长度与真实单据号一致
			ArrayList<java.util.Map.Entry<Integer, Integer>> loc = new ArrayList<java.util.Map.Entry<Integer, Integer>>();   //流水号位置，流水号位数
			String lsh; //流水号设置码
			int lshIdx = -1; //流水号设置码所在位置
			java.util.Map<Integer, Integer>  map = new HashMap<Integer, Integer>();
			for (int i = 2; i < 9; i++)
			{
				lsh = "{LSH" + i + "}";

				if (!supposeBillNo.contains(lsh))
				{
					continue;
				}

				while (supposeBillNo.contains(lsh))
				{
					//查找流水号所在位置
					lshIdx = supposeBillNo.indexOf(lsh);
					//将找到的流水号码替换成假设的流水号
					supposeBillNo = (lshIdx == 0 ? "" : supposeBillNo.substring(0, lshIdx)) + bp.tools.StringHelper.padLeft("", i, '_') + (lshIdx + 6 < supposeBillNo.length() ? supposeBillNo.substring(lshIdx + 6) : "");
					//保存当前流程号所处位置，及流程号长度，以便之后使用替换成正确的流水号
					map.put(lshIdx, i);
				}
			}

			Iterator<java.util.Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, Integer> entry = iterator.next();
				loc.add(entry);
			}


			//数据库中查找符合的单据号集合,NOTE:此处需要注意，在LIKE中带有左广方括号时，要使用一对广播号将其转义
			sql = "SELECT BillNo FROM " + flowPTable + " WHERE BillNo LIKE '" + supposeBillNo.replace("[", "[[]") + "'" + (flowPTable.toLowerCase().equals("wf_generworkflow") ? (" AND WorkID <> " + workid) : (" AND OID <> " + workid)) + " ORDER BY BillNo DESC";

			String maxBillNo = DBAccess.RunSQLReturnString(sql);
			int ilsh = 0;

			if (DataType.IsNullOrEmpty(maxBillNo))
			{
				//没有数据，则所有流水号都从1开始
				for (java.util.Map.Entry<Integer, Integer> kv : loc)
				{
					supposeBillNo = (kv.getKey() == 0 ? "" : supposeBillNo.substring(0, kv.getKey())) + bp.tools.StringHelper.padLeft("1", kv.getValue(), '0') + (kv.getKey() + kv.getValue() < supposeBillNo.length() ? supposeBillNo.substring(kv.getKey() + kv.getValue()) : "");
				}
			}
			else
			{
				//有数据，则从右向左开始判断流水号，当右侧的流水号达到最大值，则左侧的流水号自动加1
				HashMap<Integer, Integer> mlsh = new HashMap<Integer, Integer>();
				int plus1idx = -1;

				for (int i = loc.size() - 1; i >= 0; i--)
				{
					//获取单据号中当前位的流水码数
					ilsh = Integer.parseInt(bp.tools.StringHelper.substring(maxBillNo, loc.get(i).getKey(), loc.get(i).getValue()));

					if (plus1idx >= 0)
					{
						//如果当前码位被置为+1，则+1，同时将标识置为-1
						ilsh++;
						plus1idx = -1;
					}
					else
					{
						mlsh.put(loc.get(i).getKey(), i == loc.size() - 1 ? ilsh + 1 : ilsh);
						continue;
					}

					if (ilsh >= Integer.parseInt(bp.tools.StringHelper.padLeft("", loc.get(i).getValue(), '9')))
					{
						//右侧已经达到最大值
						if (i > 0)
						{
							//记录前位的码
							mlsh.put(loc.get(i).getKey(), 1);
						}
						else
						{
							supposeBillNo = "单据号超出范围";
							break;
						}

						//则将前一个流水码位，标记为+1
						plus1idx = i - 1;
					}
					else
					{
						mlsh.put(loc.get(i).getKey(), ilsh + 1);
					}
				}

				if (supposeBillNo.equals("单据号超出范围"))
				{
					return supposeBillNo;
				}

				//拼接单据号
				for (java.util.Map.Entry<Integer, Integer> kv : loc)
				{
					supposeBillNo = (kv.getKey() == 0 ? "" : supposeBillNo.substring(0, kv.getKey())) +
							StringHelper.padLeft(mlsh.get(kv.getKey()).toString(), kv.getValue(), '0') +
							(kv.getKey() + kv.getValue() < supposeBillNo.length() ? supposeBillNo.substring(kv.getKey() + kv.getValue()) : "");
				}
			}

			billNo = supposeBillNo;
		}

		return billNo;
	}
	/**
	 找到下一个节点的接受人员
	 
	 @param fl 流程
	 @param currNode 当前节点
	 @param toNode 到达节点
	 @return 下一步工作人员No,Name格式的返回.
	*/
	public static DataTable RequetNextNodeWorkers(Flow fl, Node currNode, Node toNode, Entity enParas, long workid) throws Exception {
		if (toNode.getItIsGuestNode())
		{
			/*到达的节点是客户参与的节点. add by zhoupeng 2016.5.11*/
			DataTable mydt = new DataTable();
			mydt.Columns.Add("No", String.class);
			mydt.Columns.Add("Name", String.class);

			DataRow dr = mydt.NewRow();
			dr.setValue("No", "Guest");
			dr.setValue("Name", "外部用户");
			mydt.Rows.add(dr);
			return mydt;
		}

		DataTable dt = new DataTable();
		dt.Columns.Add("No", String.class);
		String sql;
		String FK_Emp;

		//变量.
		String dbStr = bp.difference.SystemConfig.getAppCenterDBVarStr();

		Paras ps = new Paras();
		// 按上一节点发送人处理。
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByPreviousNodeEmp)
		{
			DataRow dr = dt.NewRow();
			dr.setValue(0, WebUser.getNo());
			dt.Rows.add(dr);
			return dt;
		}


			///#region 首先判断是否配置了获取下一步接受人员的sql.
		if (toNode.getHisDeliveryWay() == DeliveryWay.BySQL || toNode.getHisDeliveryWay() == DeliveryWay.BySQLTemplate || toNode.getHisDeliveryWay() == DeliveryWay.BySQLAsSubThreadEmpsAndData)
		{
			if (toNode.getHisDeliveryWay() == DeliveryWay.BySQLTemplate)
			{
				SQLTemplate st = new SQLTemplate(toNode.getDeliveryParas());
				sql = st.getDocs();
			}
			else
			{
				if (toNode.getDeliveryParas().length() < 4)
				{
					throw new RuntimeException("@您设置的当前节点按照SQL，决定下一步的接受人员，但是你没有设置SQL.");
				}
				sql = toNode.getDeliveryParas();
			}

			//特殊的变量.
			sql = sql.replace("@FK_Node", String.valueOf(toNode.getNodeID()));
			sql = sql.replace("@NodeID", String.valueOf(toNode.getNodeID()));
			sql = sql.replace("@WebUser.DeptNo", WebUser.getDeptNo());

			sql = bp.wf.Glo.DealExp(sql, enParas, null);


			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0 && toNode.getHisWhenNoWorker() == false)
			{
				throw new RuntimeException("@没有找到可接受的工作人员。@技术信息：执行的SQL没有发现人员:" + sql);
			}
			return dt;
		}

			///#endregion 首先判断是否配置了获取下一步接受人员的sql.


			///#region 按绑定部门计算,该部门一人处理标识该工作结束(子线程)..
		if (toNode.getHisDeliveryWay() == DeliveryWay.BySetDeptAsSubthread)
		{
			if (toNode.getItIsSubThread() == false)
			{
				throw new RuntimeException("@您设置的节点接收人方式为：按绑定部门计算,该部门一人处理标识该工作结束(子线程)，但是当前节点非子线程节点。");
			}

			sql = "SELECT " + bp.sys.base.Glo.getUserNo() + ", Name,FK_Dept AS GroupMark FROM Port_Emp WHERE FK_Dept IN (SELECT FK_Dept FROM WF_NodeDept WHERE FK_Node=" + toNode.getNodeID() + ")";
			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0 && toNode.getHisWhenNoWorker() == false)
			{
				throw new RuntimeException("@没有找到可接受的工作人员,接受人方式为, ‘按绑定部门计算,该部门一人处理标识该工作结束(子线程)’ @技术信息：执行的SQL没有发现人员:" + sql);
			}
			return dt;
		}

			///#endregion 按绑定部门计算,该部门一人处理标识该工作结束(子线程)..


			///#region 按照明细表,作为子线程的接收人.
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByDtlAsSubThreadEmps)
		{
			if (toNode.getItIsSubThread() == false)
			{
				throw new RuntimeException("@您设置的节点接收人方式为：以分流点表单的明细表数据源确定子线程的接收人，但是当前节点非子线程节点。");
			}

			currNode.WorkID = workid; //为获取表单ID ( NodeFrmID )提供参数.

			MapDtls dtls = new MapDtls(currNode.getNodeFrmID());
			String msg = null;
			for (MapDtl dtl : dtls.ToJavaList())
			{
				try
				{
					String empFild = toNode.getDeliveryParas();
					if (DataType.IsNullOrEmpty(empFild))
					{
						empFild = " UserNo ";
					}

					ps = new Paras();
					ps.SQL = "SELECT " + empFild + ", * FROM " + dtl.getPTable() + " WHERE RefPK=" + dbStr + "OID ORDER BY OID";
					ps.Add("OID", workid);
					dt = DBAccess.RunSQLReturnTable(ps);
					if (dt.Rows.size() == 0 && toNode.getHisWhenNoWorker() == false)
					{
						throw new RuntimeException("@流程设计错误，到达的节点（" + toNode.getName() + "）在指定的节点中没有数据，无法找到子线程的工作人员。");
					}
					return dt;
				}
				catch (RuntimeException ex)
				{
					msg += ex.getMessage();
					//if (dtls.size()== 1)
					//    throw new Exception("@估计是流程设计错误,没有在分流节点的明细表中设置");
				}
			}
			throw new RuntimeException("@没有找到分流节点的明细表作为子线程的发起的数据源，流程设计错误，请确认分流节点表单中的明细表是否有UserNo约定的系统字段。" + msg);
		}

			///#endregion 按照明细表,作为子线程的接收人.


			///#region 按节点绑定的人员处理.
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByBindEmp)
		{
			ps = new Paras();
			ps.Add("FK_Node", toNode.getNodeID());
			ps.SQL = "SELECT FK_Emp FROM WF_NodeEmp WHERE FK_Node=" + dbStr + "FK_Node ORDER BY FK_Emp";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				throw new RuntimeException("@流程设计错误:下一个节点(" + toNode.getName() + ")没有绑定工作人员 . ");
			}
			return dt;
		}

			///#endregion 按节点绑定的人员处理.


			///#region 按照选择的人员处理。
		if (toNode.getHisDeliveryWay() == DeliveryWay.BySelected || toNode.getHisDeliveryWay() == DeliveryWay.ByFEE)
		{
			ps = new Paras();
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("WorkID", workid);
			ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				/*从上次发送设置的地方查询. */
				SelectAccpers sas = new SelectAccpers();
				int i = sas.QueryAccepterPriSetting(toNode.getNodeID());
				if (i == 0)
				{
					if (toNode.getHisDeliveryWay() == DeliveryWay.BySelected)
					{
						if (currNode.getCondModel() != DirCondModel.ByPopSelect)
						{
							// 2020.08.17 这里注释掉了， 有可能是到达的节点是，按照弹出窗体计算的. 
							// 不做强制修改.
							//currNode.getCondModel() = DirCondModel.SendButtonSileSelect;
							//currNode.Update();
							//throw new Exception("@下一个节点的接收人规则是按照上一步发送人员选择器选择的，但是在当前节点您没有启接收人选择器，系统已经自动做了设置，请关闭当前窗口重新打开重试。");
						}

						throw new RuntimeException("@请选择下一步骤工作(" + toNode.getName() + ")接受人员。");
					}
					else
					{
						throw new RuntimeException("@流程设计错误，请重写FEE，然后为节点(" + toNode.getNo() + "," + 	toNode.getName() + ",接收人规则:"+ toNode.getHisDeliveryWay().toString() + "," + toNode.getHisDeliveryWayText() + ")设置接受人员，详细请参考cc流程设计手册。");
					}
				}

				//插入里面.
				for (SelectAccper item : sas.ToJavaList())
				{
					DataRow dr = dt.NewRow();
					dr.setValue(0, item.getEmpName());
					dt.Rows.add(dr);
				}
				return dt;
			}
			return dt;
		}

			///#endregion 按照选择的人员处理。


			///#region 按照指定节点的处理人计算。
		if (toNode.getHisDeliveryWay() == DeliveryWay.BySpecNodeEmp || toNode.getHisDeliveryWay() == DeliveryWay.ByStarter)
		{
			/* 按指定节点角色上的人员计算 */
			String strs = toNode.getDeliveryParas();
			if (toNode.getHisDeliveryWay() == DeliveryWay.ByStarter)
			{
				/*找开始节点的处理人员. */
				strs = Integer.parseInt(fl.getNo()) + "01";
				ps = new Paras();
				ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node AND IsPass=1 AND IsEnable=1 ORDER BY CDT ";
				ps.Add("FK_Node", Integer.parseInt(strs));
				ps.Add("OID", workid);
				dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() == 1)
				{
					return dt;
				}
				else
				{
					/* 有可能当前节点就是第一个节点，那个时间还没有初始化数据，就返回当前人. */
					DataRow dr = dt.NewRow();
					dr.setValue(0, WebUser.getNo());
					dt.Rows.add(dr);
					return dt;
				}
			}

			// 首先从本流程里去找。
			strs = strs.replace(";", ",");
			String[] ndStrs = strs.split("[,]", -1);
			for (String nd : ndStrs)
			{
				if (DataType.IsNullOrEmpty(nd))
				{
					continue;
				}

				if (DataType.IsNumStr(nd) == false)
				{
					throw new RuntimeException("流程设计错误:您设置的节点(" + toNode.getName() + ")的接收方式为按指定的节点角色投递，但是您没有在访问规则设置中设置节点编号。");
				}

				ps = new Paras();
				ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node AND IsPass=1 AND IsEnable=1 ";
				ps.Add("FK_Node", Integer.parseInt(nd));
				if (currNode.getItIsSubThread() == true)
				{
					ps.Add("OID", workid);
				}
				else
				{
					ps.Add("OID", workid);
				}

				DataTable dt_ND = DBAccess.RunSQLReturnTable(ps);
				//添加到结果表
				if (dt_ND.Rows.size() != 0)
				{
					for (DataRow row : dt_ND.Rows)
					{
						DataRow dr = dt.NewRow();
						dr.setValue(0, row.get(0).toString());
						dt.Rows.add(dr);
					}
					//此节点已找到数据则不向下找，继续下个节点
					continue;
				}

				//就要到轨迹表里查,因为有可能是跳过的节点.
				ps = new Paras();
				ps.SQL = "SELECT " + TrackAttr.EmpFrom + " FROM ND" + Integer.parseInt(fl.getNo()) + "Track WHERE (ActionType=" + dbStr + "ActionType1 OR ActionType=" + dbStr + "ActionType2 OR ActionType=" + dbStr + "ActionType3 OR ActionType=" + dbStr + "ActionType4 OR ActionType=" + dbStr + "ActionType5) AND NDFrom=" + dbStr + "NDFrom AND WorkID=" + dbStr + "WorkID";
				ps.Add("ActionType1", ActionType.Skip.getValue());
				ps.Add("ActionType2", ActionType.Forward.getValue());
				ps.Add("ActionType3", ActionType.ForwardFL.getValue());
				ps.Add("ActionType4", ActionType.ForwardHL.getValue());
				ps.Add("ActionType5", ActionType.Start.getValue());

				ps.Add("NDFrom", Integer.parseInt(nd));
				ps.Add("WorkID", workid);
				dt_ND = DBAccess.RunSQLReturnTable(ps);
				if (dt_ND.Rows.size() != 0)
				{
					for (DataRow row : dt_ND.Rows)
					{
						DataRow dr = dt.NewRow();
						dr.setValue(0, row.get(0).toString());
						dt.Rows.add(dr);
					}
				}
			}

			//本流程里没有有可能该节点是配置的父流程节点,也就是说子流程的一个节点与父流程指定的节点的工作人员一致.
			GenerWorkFlow gwf = new GenerWorkFlow(workid);
			if (gwf.getPWorkID() != 0)
			{
				for (String pnodeiD : ndStrs)
				{
					if (DataType.IsNullOrEmpty(pnodeiD))
					{
						continue;
					}

					Node nd = new Node(Integer.parseInt(pnodeiD));
					if (!Objects.equals(nd.getFlowNo(), gwf.getPFlowNo()))
					{
						continue; // 如果不是父流程的节点，就不执行.
					}

					ps = new Paras();
					ps.SQL = "SELECT FK_Emp FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node AND IsPass=1 AND IsEnable=1 ";
					ps.Add("FK_Node", nd.getNodeID());
					if (currNode.getItIsSubThread() == true)
					{
						ps.Add("OID", gwf.getPFID());
					}
					else
					{
						ps.Add("OID", gwf.getPWorkID());
					}

					DataTable dt_PWork = DBAccess.RunSQLReturnTable(ps);
					if (dt_PWork.Rows.size() != 0)
					{
						for (DataRow row : dt_PWork.Rows)
						{
							DataRow dr = dt.NewRow();
							dr.setValue(0, row.get(0).toString());
							dt.Rows.add(dr);
						}
						//此节点已找到数据则不向下找，继续下个节点
						continue;
					}

					//就要到轨迹表里查,因为有可能是跳过的节点.
					ps = new Paras();
					ps.SQL = "SELECT " + TrackAttr.EmpFrom + " FROM ND" + Integer.parseInt(fl.getNo()) + "Track WHERE (ActionType=" + dbStr + "ActionType1 OR ActionType=" + dbStr + "ActionType2 OR ActionType=" + dbStr + "ActionType3 OR ActionType=" + dbStr + "ActionType4 OR ActionType=" + dbStr + "ActionType5) AND NDFrom=" + dbStr + "NDFrom AND WorkID=" + dbStr + "WorkID";
					ps.Add("ActionType1", ActionType.Start.getValue());
					ps.Add("ActionType2", ActionType.Forward.getValue());
					ps.Add("ActionType3", ActionType.ForwardFL.getValue());
					ps.Add("ActionType4", ActionType.ForwardHL.getValue());
					ps.Add("ActionType5", ActionType.Skip.getValue());
					ps.Add("NDFrom", nd.getNodeID());

					if (currNode.getItIsSubThread() == true)
					{
						ps.Add("OID", gwf.getPFID());
					}
					else
					{
						ps.Add("OID", gwf.getPWorkID());
					}

					dt_PWork = DBAccess.RunSQLReturnTable(ps);
					if (dt_PWork.Rows.size() != 0)
					{
						for (DataRow row : dt_PWork.Rows)
						{
							DataRow dr = dt.NewRow();
							dr.setValue(0, row.get(0).toString());
							dt.Rows.add(dr);
						}
					}
				}
			}
			//返回指定节点的处理人
			if (dt.Rows.size() != 0)
			{
				return dt;
			}

			throw new RuntimeException("@流程设计错误，到达的节点（" + toNode.getName() + "）在指定的节点(" + strs + ")中没有数据，无法找到工作的人员。 @技术信息如下: 投递方式:BySpecNodeEmp sql=" + ps.getSQLNoPara());
		}

			///#endregion 按照节点绑定的人员处理。


			///#region 按照上一个节点表单指定字段的人员处理。
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormEmpsField)
		{
			// 检查接受人员规则,是否符合设计要求.
			String specEmpFields = toNode.getDeliveryParas();
			if (DataType.IsNullOrEmpty(specEmpFields))
			{
				specEmpFields = "SysSendEmps";
			}

			if (enParas.getEnMap().getAttrs().contains(specEmpFields) == false)
			{
				throw new RuntimeException("@您设置的接受人规则是按照表单指定的字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");
			}

			//获取接受人并格式化接受人, 
			String emps = enParas.GetValStringByKey(specEmpFields);
			emps = emps.replace(" ", "");
			if (emps.contains(",") && emps.contains(";"))
			{
				/*如果包含,; 例如 zhangsan,张三;lisi,李四;*/
				String[] myemps1 = emps.split("[;]", -1);
				for (String str : myemps1)
				{
					if (DataType.IsNullOrEmpty(str))
					{
						continue;
					}

					String[] ss = str.split("[,]", -1);
					DataRow dr = dt.NewRow();
					dr.setValue(0, ss[0]);
					dt.Rows.add(dr);
				}
				if (dt.Rows.size() == 0)
				{
					throw new RuntimeException("@输入的接受人员信息错误;[" + emps + "]。");
				}
				else
				{
					return dt;
				}
			}

			emps = emps.replace(";", ",");
			emps = emps.replace("；", ",");
			emps = emps.replace("，", ",");
			emps = emps.replace("、", ",");
			emps = emps.replace("@", ",");

			if (DataType.IsNullOrEmpty(emps))
			{
				throw new RuntimeException("@没有在字段[" + enParas.getEnMap().getAttrs().GetAttrByKey(specEmpFields).getDesc() + "]中指定接受人，工作无法向下发送。");
			}

			// 把它加入接受人员列表中.
			String[] myemps = emps.split("[,]", -1);
			for (String s : myemps)
			{
				if (DataType.IsNullOrEmpty(s))
				{
					continue;
				}
				DataRow dr = dt.NewRow();
				dr.setValue(0, s);
				dt.Rows.add(dr);
			}
			return dt;
		}

			///#endregion 按照上一个节点表单指定字段的人员处理。



			///#region 按照上一个节点表单指定字段的【角色】处理。
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormStationsAI || toNode.getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormStationsOnly)
		{
			// 检查接受人员规则,是否符合设计要求.
			String specEmpFields = toNode.getDeliveryParas();
			if (DataType.IsNullOrEmpty(specEmpFields))
			{
				specEmpFields = "SysSendEmps";
			}

			if (enParas.getEnMap().getAttrs().contains(specEmpFields) == false)
			{
				throw new RuntimeException("@您设置的接受人规则是按照表单指定的角色字段，决定下一步的接受人员，该字段{" + specEmpFields + "}已经删除或者丢失。");
			}

			String stas = "";
			//获取接受人并格式化接受人, 
			String emps = enParas.GetValStringByKey(specEmpFields);
			emps = emps.replace(" ", "");
			if (emps.contains(",") && emps.contains(";"))
			{
				/*如果包含,; 例如 xx,角色1;222,角色2;*/
				String[] myemps1 = emps.split("[;]", -1);
				for (String str : myemps1)
				{
					if (DataType.IsNullOrEmpty(str))
					{
						continue;
					}

					String[] ss = str.split("[,]", -1);
					stas += "," + ss[0];
				}
				if (DataType.IsNullOrEmpty(stas))
				{
					throw new RuntimeException("@输入的接受人员的角色信息错误;[" + emps + "]。");
				}
			}
			else
			{
				emps = emps.replace(";", ",");
				emps = emps.replace("；", ",");
				emps = emps.replace("，", ",");
				emps = emps.replace("、", ",");
				emps = emps.replace("@", ",");

				if (DataType.IsNullOrEmpty(emps))
				{
					throw new RuntimeException("@没有在字段[" + enParas.getEnMap().getAttrs().GetAttrByKey(specEmpFields).getDesc() + "]中指定接受人的角色，工作无法向下发送。");
				}

				// 把它加入接受人员列表中.
				String[] myemps = emps.split("[,]", -1);
				for (String s : myemps)
				{
					if (DataType.IsNullOrEmpty(s))
					{
						continue;
					}
					stas += "," + s;
				}
			}
			//根据角色:集合获取信息.
			stas = stas.substring(1);

			// 仅按角色计算. 以下都有要重写.
			if (toNode.getHisDeliveryWay() == DeliveryWay.ByPreviousNodeFormStationsOnly)
			{
				dt = WorkFlowBuessRole.FindWorker_GetEmpsByStations(stas);
				if (dt.Rows.size() == 0 && toNode.getHisWhenNoWorker() == false)
				{
					throw new RuntimeException("err@按照字段角色(仅按角色计算)找接受人错误,当前部门下没有您选择的角色人员.");
				}
				return dt;
			}


				///#region 按角色智能计算, 集合模式.
			if (toNode.getDeliveryStationReqEmpsWay() == 0)
			{
				String deptNo = WebUser.getDeptNo();
				dt = FindWorker_GetEmpsByDeptAI(stas, deptNo);
				if (dt.Rows.size() == 0 && toNode.getHisWhenNoWorker() == false)
				{
					throw new RuntimeException("err@按照字段角色(智能)找接受人错误,当前部门与父级部门下没有您选择的角色人员.");
				}
				return dt;
			}

				///#endregion 按角色智能计算, 要判断切片模式,还是集合模式.


				///#region 按角色智能计算, 切片模式. 需要对每个角色都要找到接受人，然后把这些接受人累加起来.
			if (toNode.getDeliveryStationReqEmpsWay() == 1 || toNode.getDeliveryStationReqEmpsWay() == 2)
			{
				String deptNo = WebUser.getDeptNo();
				String[] temps = stas.split("[,]", -1);
				for (String str : temps)
				{
					//求一个角色下的人员.
					DataTable mydt = FindWorker_GetEmpsByDeptAI(str, deptNo);

					//如果是严谨模式.
					if (toNode.getDeliveryStationReqEmpsWay() == 1 && mydt.Rows.size() == 0)
					{
						Station st = new Station(str);
						throw new RuntimeException("@角色[" + st.getName() + "]下，没有找到人不能发送下去，请检查组织结构是否完整。");
					}

					//累加起来.
					for (DataRow dr : mydt.Rows)
					{
						DataRow mydr = dt.NewRow();
						mydr.setValue(0, dr.getValue(0).toString());
						dt.Rows.add(mydr);
					}
				}

				if (dt.Rows.size() == 0 && toNode.getHisWhenNoWorker() == false)
				{
					throw new RuntimeException("err@按照字段角色(智能,切片)找接受人错误,当前部门与父级部门下没有您选择的角色人员.");
				}

				return dt;
			}

				///#endregion 按角色智能计算, 切片模式.

			throw new RuntimeException("err@没有判断的模式....");
		}

			///#endregion 按照上一个节点表单指定字段的人员处理 【角色】。



			///#region 按部门与角色的交集计算.
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByDeptAndStation)
		{
			sql = "SELECT pdes.fk_emp AS No"
					+ " FROM   Port_DeptEmpStation pdes"
					+ "        INNER JOIN WF_NodeDept wnd"
					+ "             ON  wnd.fk_dept = pdes.fk_dept"
					+ "             AND wnd.fk_node = " + toNode.getNodeID()
					+ "        INNER JOIN WF_NodeStation wns"
					+ "             ON  wns.FK_Station = pdes.fk_station"
					+ "             AND wnd.fk_node =" + toNode.getNodeID()
					+ " ORDER BY"
					+ "        pdes.fk_emp";
			dt = DBAccess.RunSQLReturnTable(sql);


			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			else
			{
				throw new RuntimeException("@节点访问规则(" + toNode.getHisDeliveryWay().toString() + ")错误:节点(" + toNode.getNodeID() + "," + toNode.getName() + "), 按照角色与部门的交集确定接受人的范围错误，没有找到人员:SQL=" + sql);
			}
		}

			///#endregion 按部门与角色的交集计算.


			///#region 判断节点部门里面是否设置了部门，如果设置了就按照它的部门处理。
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByDept)
		{
			ps = new Paras();
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("WorkID", workid);
			ps.SQL = "SELECT FK_Emp FROM WF_SelectAccper WHERE FK_Node=" + dbStr + "FK_Node AND WorkID=" + dbStr + "WorkID AND AccType=0 ORDER BY IDX";
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
		}

			///#endregion 判断节点部门里面是否设置了部门，如果设置了，就按照它的部门处理。


			///#region 仅按用户组计算
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByTeamOnly)
		{
			sql = "SELECT DISTINCT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B WHERE A.FK_Team=B.FK_Team AND B.FK_Node=" + toNode.getNodeID();
			dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			else
			{
				throw new RuntimeException("@节点访问规则错误:节点(" + toNode.getNodeID() + "," + toNode.getName() + "), 仅按用户组计算，没有找到人员:SQL=" + sql);
			}
		}

			///#endregion


			///#region 本集团组织
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByTeamOrgOnly)
		{
			sql = "SELECT DISTINCT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B, Port_Emp C WHERE A.FK_Emp=C." + bp.sys.base.Glo.getUserNoWhitOutAS() + " AND A.FK_Team=B.FK_Team AND B.FK_Node=" + dbStr + "FK_Node AND C.OrgNo=" + dbStr + "OrgNo  ORDER BY A.FK_Emp";
			ps = new Paras();
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("OrgNo", WebUser.getOrgNo(), false);

			ps.SQL =sql;
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			else
			{
				throw new RuntimeException("@节点访问规则错误:节点(" + toNode.getNodeID() + "," + toNode.getName() + "), 按用户组智能计算，没有找到人员:SQL=" + ps.getSQLNoPara());
			}
		}

			///#endregion


			///#region 本部门
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByTeamDeptOnly)
		{
			sql = "SELECT DISTINCT A.FK_Emp FROM Port_TeamEmp A, WF_NodeTeam B, Port_Emp C WHERE A.FK_Emp=C." + bp.sys.base.Glo.getUserNoWhitOutAS() + " AND A.FK_Team=B.FK_Team AND B.FK_Node=" + dbStr + "FK_Node AND C.FK_Dept=" + dbStr + "FK_Dept  ORDER BY A.FK_Emp";
			ps = new Paras();
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("FK_Dept", WebUser.getDeptNo(), false);

			ps.SQL = sql;
			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			else
			{
				throw new RuntimeException("@节点访问规则错误:节点(" + toNode.getNodeID() + "," + toNode.getName() + "), 按用户组智能计算，没有找到人员:SQL=" + ps.getSQLNoPara());
			}
		}

			///#endregion


			///#region 仅按角色计算
		if (toNode.getHisDeliveryWay() == DeliveryWay.ByStationOnly)
		{
			ps = new Paras();
			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND A.OrgNo=" + dbStr + "OrgNo AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Emp";
				ps.Add("OrgNo", WebUser.getOrgNo(), false);
				ps.Add("FK_Node", toNode.getNodeID());
				ps.SQL=sql;
			}
			else
			{
				sql = "SELECT A.FK_Emp FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node ORDER BY A.FK_Emp";
				ps.Add("FK_Node", toNode.getNodeID());
				ps.SQL=sql;
			}

			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			else
			{
				throw new RuntimeException("@节点访问规则错误:节点(" + toNode.getNodeID() + "," + toNode.getName() + "), 仅按角色计算，没有找到人员。");
			}
		}

			///#endregion

	   /* #region 按角色计算(以部门集合为纬度).
	    if (toNode.getHisDeliveryWay() == DeliveryWay.ByStationAndEmpDept)
	    {
	        *//* 考虑当前操作人员的部门, 如果本部门没有这个角色就不向上寻找. *//*

			if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
			{
				dt = DBAccess.RunSQLReturnTable("SELECT UserID as No, Name FROM Port_Emp WHERE UserID='" + WebUser.getNo() + "' AND OrgNo='" + WebUser.getOrgNo() + "'");
			}
			else
			{
				ps = new Paras();
				sql = "SELECT No,Name FROM Port_Emp WHERE No=" + dbStr + "FK_Emp ";
				ps.Add("FK_Emp", WebUser.getNo(), false);
				dt = DBAccess.RunSQLReturnTable(ps);
			}



			if (dt.Rows.size() > 0)
			{
				return dt;
			}
			else
			{
				throw new RuntimeException("@节点访问规则(" + toNode.getHisDeliveryWay().toString() + ")错误:节点(" + toNode.getNodeID() + "," + toNode.getName() + "), 按角色计算(以部门集合为纬度)。技术信息,执行的SQL=" + ps.getSQLNoPara());
			}
	}

			///#endregion*/

		String empNo = WebUser.getNo();
		String empDept = WebUser.getDeptNo();


			///#region 按指定的节点的人员角色，做为下一步骤的流程接受人。
//C# TO JAVA CONVERTER TASK: The following line could not be converted:
		if (toNode.getHisDeliveryWay() == DeliveryWay.BySpecNodeEmpStation)
		{
			/* 按指定的节点的人员角色 */
			String para = toNode.getDeliveryParas();
			para = para.replace("@", "");

			if (DataType.IsNumStr(para) == true)
			{
				ps = new Paras();
				ps.SQL = "SELECT FK_Emp,FK_Dept FROM WF_GenerWorkerlist WHERE WorkID=" + dbStr + "OID AND FK_Node=" + dbStr + "FK_Node ";
				ps.Add("OID", workid);
				ps.Add("FK_Node", Integer.parseInt(para));

				dt = DBAccess.RunSQLReturnTable(ps);
				if (dt.Rows.size() != 1)
				{
					throw new RuntimeException("@流程设计错误，到达的节点（" + toNode.getName() + "）在指定的节点中没有数据，无法找到工作的人员。");
				}

				empNo = dt.Rows.get(0).getValue(0).toString();
				empDept = dt.Rows.get(0).getValue(1).toString();
			}
			else
			{
				if (enParas.getRow().containsKey(para) == false)
				{
					throw new RuntimeException("@在找人接收人的时候错误@字段{" + para + "}不包含在rpt里，流程设计错误。");
				}

				empNo = enParas.GetValStrByKey(para);
				if (DataType.IsNullOrEmpty(empNo))
				{
					throw new RuntimeException("@字段{" + para + "}不能为空，没有取出来处理人员。");
				}

				Emp emp = new Emp(empNo);
				empDept = emp.getDeptNo();
			}
		}

			///#endregion 按指定的节点人员，做为下一步骤的流程接受人。


			///#region 最后判断 - 按照角色AI来执行。
		if (currNode.getItIsStartNode() == false)
		{
			ps = new Paras();
			dt = DBAccess.RunSQLReturnTable(ps);
			// 如果能够找到.
			if (dt.Rows.size() >= 1)
			{
				if (dt.Rows.size() == 1)
				{
					/*如果人员只有一个的情况，说明他可能要 */
				}
				return dt;
			}
		}

		/* 如果执行节点 与 接受节点角色集合一致 */
		if (currNode.getGroupStaNDs() == toNode.getGroupStaNDs())
		{
			/* 说明，就把当前人员做为下一个节点处理人。*/
			DataRow dr = dt.NewRow();
			dr.setValue(0, WebUser.getNo());
			dt.Rows.add(dr);
			return dt;
		}

		/* 如果执行节点 与 接受节点角色集合不一致 */
		if (currNode.getGroupStaNDs() != toNode.getGroupStaNDs())
		{
			/* 没有查询到的情况下, 先按照本部门计算。*/


			sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B  WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept=" + dbStr + "FK_Dept";
			ps = new Paras();
			ps.SQL=sql;
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("FK_Dept", empDept);


			dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				NodeStations nextStations = toNode.getNodeStations();
				if (nextStations.size() == 0)
				{
					throw new RuntimeException("@节点没有角色:" + toNode.getNodeID() + "  " + toNode.getName());
				}
			}
			else
			{
				boolean isInit = false;
				for (DataRow dr : dt.Rows)
				{
					if (Objects.equals(dr.getValue(0).toString(), WebUser.getNo()))
					{
						/* 如果角色分组不一样，并且结果集合里还有当前的人员，就说明了出现了当前操作员，拥有本节点上的角色也拥有下一个节点的工作角色
						 导致：节点的分组不同，传递到同一个人身上。 */
						isInit = true;
					}
				}


///#warning edit by zhoupeng, 用来确定不同角色集合的传递包含同一个人的处理方式。

				//  if (isInit == false || isInit == true)
				return dt;
			}
		}
		/*这里去掉了向下级别寻找的算法. */

		/* 没有查询到的情况下, 按照最大匹配数 提高一个级别计算，递归算法未完成。
		 * 因为:以上已经做的角色的判断，就没有必要在判断其它类型的节点处理了。
		 * */
		Object tempVar = empDept;
		String nowDeptID = tempVar instanceof String ? (String)tempVar : null;
		while (true)
		{
			Dept myDept = new Dept(nowDeptID);
			nowDeptID = myDept.getParentNo();
			if (Objects.equals(nowDeptID, "-1") || Objects.equals(nowDeptID.toString(), "0"))
			{
				break; //一直找到了最高级仍然没有发现，就跳出来循环从当前操作员人部门向下找。
			}

			//检查指定的部门下面是否有该人员.
			DataTable mydtTemp = RequetNextNodeWorkers_DiGui(nowDeptID, empNo, toNode);
			if (mydtTemp == null)
			{
				/*如果父亲级没有，就找父级的平级. */
				Depts myDepts = new Depts();
				myDepts.Retrieve(DeptAttr.ParentNo, myDept.getParentNo(), null);
				for (Dept item : myDepts.ToJavaList())
				{
					if (Objects.equals(item.getNo(), nowDeptID))
					{
						continue;
					}
					mydtTemp = RequetNextNodeWorkers_DiGui(item.getNo(), empNo, toNode);
					if (mydtTemp == null)
					{
						continue;
					}
					else
					{
						return mydtTemp;
					}
				}

				continue; //如果平级也没有，就continue.
			}
			else
			{
				return mydtTemp;
			}
		}

		/* 如果向上找没有找到，就考虑从本级部门上向下找. */
		Object tempVar2 = empDept;
		nowDeptID = tempVar2 instanceof String ? (String)tempVar2 : null;
		Depts subDepts = new Depts(nowDeptID);

		//递归出来子部门下有该角色的人员.
		DataTable mydt123 = RequetNextNodeWorkers_DiGui_ByDepts(subDepts, empNo, toNode);
		if (mydt123 == null)
		{
			throw new RuntimeException("@按角色计算没有找到(" + toNode.getName() + ")接受人.");
		}
		return mydt123;

			///#endregion  按照角色来执行。
}
	/**
	 按照部门编号，与角色集合智能计算接受人.

	 @param stas 角色编号
	 @param deptNo 部门编号
	 @return
	*/
//C# TO JAVA CONVERTER TASK: Local functions are not converted by C# to Java Converter:
	public static DataTable FindWorker_GetEmpsByDeptAI(String stas, String deptNo) throws Exception {
			DataTable dt = WorkFlowBuessRole.FindWorker_GetEmpsByStationsAndDepts(stas, deptNo);
			if (dt.Rows.size() == 0)
			{
				//本部门的父级.
				Dept deptMy = new Dept(deptNo);
				dt = WorkFlowBuessRole.FindWorker_GetEmpsByStationsAndDepts(stas, deptMy.getParentNo());

				//本级部门的祖父级,不在向上判断了.
				if (dt.Rows.size() == 0 && deptMy.getParentNo().equals("0") == false)
				{
					Dept deptParent = new Dept(deptMy.getParentNo());
					dt = WorkFlowBuessRole.FindWorker_GetEmpsByStationsAndDepts(stas, deptParent.getParentNo());
				}

				//扫描评级部门.
				if (dt.Rows.size() == 0)
				{
					String deptNos = "";
					Depts depts = new Depts();
					depts.Retrieve(DeptAttr.ParentNo, deptMy.getParentNo());
					for (Dept mydept : depts.ToJavaList())
						deptNos += "," + mydept.getNo();

					dt = WorkFlowBuessRole.FindWorker_GetEmpsByStationsAndDepts(stas, deptNos);
				}
				return dt;
			}
			return dt;
		}

	public static DataTable FindWorker_GetEmpsByStations(String stas)
		{
			String sqlEnd = "";
			if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
				sqlEnd = " AND OrgNo='" + bp.web.WebUser.getOrgNo() + "'";

			//处理合法的 in 字段.
			if (stas.contains("'") == false)
			{
				String[] temps = stas.split("[,]",-1);
				String mystrs = "";
				for (String temp : temps)
					mystrs += ",'" + temp + "'";

				mystrs = mystrs.substring(1);
				stas = mystrs;
			}

			String sql = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station IN (" + stas + ") " + sqlEnd;
			return DBAccess.RunSQLReturnTable(sql);
		}
	/**
	 获取部门与角色的交集.

	 @param stas 角色集合s
	 @param depts 部门集合s
	 @return
	*/
	public static DataTable FindWorker_GetEmpsByStationsAndDepts(String stas, String depts)
		{
			String sqlEnd = "";
			if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
				sqlEnd = " AND OrgNo='" + bp.web.WebUser.getOrgNo() + "'";

			//是单个的.
			if (stas.contains(",") == false && depts.contains(",") == false)
			{
				String sql1 = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station='" + stas + "' AND FK_Dept='" + depts + "' "; // + sqlEnd;
				return DBAccess.RunSQLReturnTable(sql1);
			}

			//处理合法的 in 字段.
			if (stas.contains("'") == false)
			{
				String[] temps = stas.split("[,]",-1);
				String mystrs = "";
				for (String temp : temps)
					mystrs += ",'" + temp + "'";

				mystrs = mystrs.substring(1);
				stas = mystrs;
			}

			//处理合法的in 字段.
			if (depts.contains("'") == false)
			{
				String[] temps = depts.split("[,]",-1);
				String mystrs = "";
				for (String temp : temps)
					mystrs += ",'" + temp + "'";

				mystrs = mystrs.substring(1);
				depts = mystrs;
			}

			String sql = "SELECT FK_Emp FROM Port_DeptEmpStation WHERE FK_Station IN(" + stas + ") AND FK_Dept IN (" + depts + ") "; // + sqlEnd;
			return DBAccess.RunSQLReturnTable(sql);
		}
	/**
	 递归出来子部门下有该角色的人员

	 @param subDepts
	 @param empNo
	 @return
	*/
	private static DataTable RequetNextNodeWorkers_DiGui_ByDepts(bp.port.Depts subDepts, String empNo, Node toNode) throws Exception {
			for (bp.port.Dept item : subDepts.ToJavaList())
			{
				DataTable dt = RequetNextNodeWorkers_DiGui(item.getNo(), empNo, toNode);
				if (dt != null)
					return dt;

				Depts MySubDepts = new Depts();
				MySubDepts.Retrieve(DeptAttr.ParentNo, item.getNo());

				dt = RequetNextNodeWorkers_DiGui_ByDepts(MySubDepts, empNo, toNode);
				if (dt != null)
					return dt;
			}
			return null;
		}
	/**
	 根据部门获取下一步的操作员

	 @param deptNo
	 @return
	*/
	private static DataTable RequetNextNodeWorkers_DiGui(String deptNo, String empNo, Node toNode) throws Exception {
			String sql;
			String dbStr = bp.difference.SystemConfig.getAppCenterDBVarStr();

			sql = "SELECT FK_Emp as No FROM Port_DeptEmpStation A, WF_NodeStation B WHERE A.FK_Station=B.FK_Station AND B.FK_Node=" + dbStr + "FK_Node AND A.FK_Dept=" + dbStr + "FK_Dept AND A.FK_Emp!=" + dbStr + "FK_Emp";
			Paras ps = new Paras();
			ps.SQL = sql;
			ps.Add("FK_Node", toNode.getNodeID());
			ps.Add("FK_Dept", deptNo);
			if(bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS && StringUtils.startsWith(empNo,WebUser.getOrgNo()) == false)
				ps.Add("FK_Emp", WebUser.getOrgNo()+"_"+empNo);
			else
				ps.Add("FK_Emp", empNo);

			DataTable dt = DBAccess.RunSQLReturnTable(ps);
			if (dt.Rows.size() == 0)
			{
				NodeStations nextStations = toNode.getNodeStations();
				if (nextStations.size()== 0)
					throw new Exception("@节点没有角色:" + toNode.getNodeID() + "  " + toNode.getName());

				sql = "SELECT " + bp.sys.base.Glo.getUserNo() + " FROM Port_Emp WHERE No IN ";
				sql += "(SELECT  FK_Emp  FROM Port_DeptEmpStation  WHERE FK_Station IN (SELECT FK_Station FROM WF_NodeStation WHERE FK_Node=" + dbStr + "FK_Node ) )";
				sql += " AND " + bp.sys.base.Glo.getUserNoWhitOutAS() + " IN ";

				if (deptNo == "1")
				{
					sql += "(SELECT " + bp.sys.base.Glo.getUserNoWhitOutAS() + " as FK_Emp FROM Port_Emp WHERE " + bp.sys.base.Glo.getUserNoWhitOutAS() + "!=" + dbStr + "FK_Emp ) ";
				}
				else
				{
					bp.port.Dept deptP = new bp.port.Dept(deptNo);
					sql += "(SELECT " + bp.sys.base.Glo.getUserNoWhitOutAS() + " as FK_Emp FROM Port_Emp WHERE " + bp.sys.base.Glo.getUserNoWhitOutAS() + "!=" + dbStr + "FK_Emp AND FK_Dept = '" + deptP.getParentNo() + "')";
				}

				ps = new Paras();
				ps.SQL = sql;
				ps.Add("FK_Node", toNode.getNodeID());
				ps.Add("FK_Emp", empNo);
				dt = DBAccess.RunSQLReturnTable(ps);

				if (dt.Rows.size() == 0)
					return null;
				return dt;
			}
			else
			{
				return dt;
			}
		}

		///#endregion 找到下一个节点的接受人员

}
