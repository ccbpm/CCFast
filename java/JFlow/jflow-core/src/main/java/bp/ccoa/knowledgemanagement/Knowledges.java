package bp.ccoa.knowledgemanagement;

import bp.da.*;
import bp.web.*;
import bp.en.*;
import bp.port.*;
import bp.sys.*;
import bp.difference.*;
import java.util.*;

/**
 知识管理 s
*/
public class Knowledges extends EntitiesNoName
{

		///#region 查询.
	/**
	 所有的知识管理

	 @return
	*/
	public final String Default_Init() throws Exception {

		//初始化实体.
		Knowledges ens = new Knowledges();

		QueryObject qo = new QueryObject(ens);
		//  qo.addLeftBracket();
		//qo.AddWhere(KnowledgeAttr.KnowledgeSta, 0);

		//qo.addOr();
		//qo.AddWhere(KnowledgeAttr.KnowledgeSta, 1);

		//qo.addLeftBracket();
		//qo.AddWhere(KnowledgeAttr.KnowledgeSta, 1);
		//qo.addAnd();
		//qo.AddWhere(KnowledgeAttr.Emps, " like ", "%," + WebUser.getNo() + ",%");
		//qo.addRightBracket();

		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			qo.AddWhere(KnowledgeAttr.OrgNo, " = ", WebUser.getOrgNo());
		}
		qo.DoQuery();

		return ens.ToJson("dt");
	}

	public final String Default_Init11() throws Exception {

		//初始化实体.
		Knowledges ens = new Knowledges();

		QueryObject qo = new QueryObject(ens);
		//  qo.addLeftBracket();
		qo.AddWhere(KnowledgeAttr.KnowledgeSta, 0);

		//   qo.addOr();

		//qo.addLeftBracket();
		//qo.AddWhere(KnowledgeAttr.KnowledgeSta, 1);
		//qo.addAnd();
		//qo.AddWhere(KnowledgeAttr.Emps, " like ", "%," + WebUser.getNo() + ",%");
		//qo.addRightBracket();

		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.SAAS)
		{
			qo.addAnd();
			qo.AddWhere(KnowledgeAttr.OrgNo, " = ", WebUser.getOrgNo());
		}
		qo.DoQuery();

		return ens.ToJson("dt");
	}
	/**
	 关注的知识点

	 @return
	*/
	public final String Default_KMDtlFoucs() throws Exception {
		KMDtls dtls = new KMDtls();
		dtls.Retrieve(KMDtlAttr.Foucs, " LIKE ", "%" + WebUser.getNo() + "%");
		return dtls.ToJson("dt");
	}

		///#endregion 重写.


		///#region 重写.
	/**
	 知识管理
	*/
	public Knowledges()
	{
	}
	/**
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Knowledge();
	}

		///#endregion 重写.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/**
	 转化成 java list,C#不能调用.

	 @return List
	*/
	public final java.util.List<Knowledge> ToJavaList()
	{
		return (java.util.List<Knowledge>)(Object)this;
	}
	/**
	 转化成list

	 @return List
	*/
	public final ArrayList<Knowledge> Tolist()
	{
		ArrayList<Knowledge> list = new ArrayList<Knowledge>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Knowledge)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.


	public final String Selecter_DeptEmps(String deptNo) throws Exception {
		DataSet ds = new DataSet();

		Depts depts = new Depts();
		QueryObject qo = new QueryObject(depts);
		qo.AddWhere(DeptAttr.ParentNo, " = ", deptNo);
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			qo.addAnd();
			qo.AddWhere(DeptAttr.OrgNo, WebUser.getOrgNo());

		}
		qo.addOrderBy(DeptAttr.Idx);
		qo.DoQuery();

		//获取这个部门下的人员
		Emps emps = new Emps();
		emps.Retrieve(EmpAttr.FK_Dept, deptNo, null);

		ds.Tables.add(depts.ToDataTableField("Depts"));
		ds.Tables.add(emps.ToDataTableField("Emps"));

		return bp.tools.Json.ToJson(ds);

	}
	public final String SelectEmpByKey(String searchKey)
	{
		String dbStr = SystemConfig.getAppCenterDBVarStr();
		String sql = " SELECT A.No,A.Name,B.NameOfPath,B.Name AS DeptName From Port_Emp A ,Port_Dept B WHERE A.FK_Dept=B.No AND (A.No like";
		switch (SystemConfig.getAppCenterDBType())
		{
			case MySQL:
			case PostgreSQL:
			case HGDB:
			case GBASE8CByMySQL:
			case GBASE8A:
				sql += " CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "No,'%') OR A.Name like CONCAT('%'," + SystemConfig.getAppCenterDBVarStr() + "Name,'%'))";
				break;
			case MSSQL:
				sql += " '%'+" + SystemConfig.getAppCenterDBVarStr() + "No+'%' OR A.Name like '%'+" + SystemConfig.getAppCenterDBVarStr() + "Name+'%')";
				break;
			case Oracle:
			case DM:
			case KingBaseR3:
			case KingBaseR6:
			case GBASE8CByOracle:
				sql += " '%'||" + SystemConfig.getAppCenterDBVarStr() + "No||'%' OR A.Name like '%'||" + SystemConfig.getAppCenterDBVarStr() + "Name||'%')";
				break;
			default:
				throw new RuntimeException("err@数据据" + SystemConfig.getAppCenterDBType() + "还未解析");

		}
		if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
		{
			sql += " AND A.OrgNo='" + WebUser.getOrgNo() + "' AND B.OrgNo='" + WebUser.getOrgNo() + "'";
		}
		Paras paras = new Paras();
		paras.SQL = sql;
		paras.Add("No", searchKey, false);
		paras.Add("Name", searchKey, false);
		DataTable dt = DBAccess.RunSQLReturnTable(paras);
		if (SystemConfig.getAppCenterDBFieldCaseModel() != FieldCaseModel.None)
		{
			dt.Columns.get(0).ColumnName = "No";
			dt.Columns.get(1).ColumnName = "Name";
			dt.Columns.get(2).ColumnName = "NameOfPath";
			dt.Columns.get(3).ColumnName = "DeptName";
		}
		return bp.tools.Json.ToJson(dt);
	}
}
