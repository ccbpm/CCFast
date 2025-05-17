package bp.wf;

import bp.da.*;
import bp.en.*;
import java.util.*;

/** 
 流程实例s
*/
public class GenerWorkFlows extends Entities
{
	/** 
	 根据工作流程,工作人员 ID 查询出来他当前的能做的工作.
	 
	 @param flowNo 流程编号
	 @param empId 工作人员ID
	 @return 
	*/
	public static DataTable QuByFlowAndEmp(String flowNo, int empId)
	{
		String sql = "SELECT a.WorkID FROM WF_GenerWorkFlow a, WF_GenerWorkerlist b WHERE a.WorkID=b.WorkID   AND b.FK_Node=a.FK_Node  AND b.FK_Emp='" + String.valueOf(empId) + "' AND a.FK_Flow='" + flowNo + "'";
		return DBAccess.RunSQLReturnTable(sql);
	}

	/** 
	 根据流程编号，标题模糊查询
	 
	 @param flowNo
	 @param likeKey
	 @return 
	*/
	public final String QueryByLike(String flowNo, String likeKey) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere("FK_Flow", flowNo);
		if (DataType.IsNullOrEmpty(likeKey) == false)
		{
			qo.addAnd();
			if (Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "@") || Objects.equals(bp.difference.SystemConfig.getAppCenterDBVarStr(), "?"))
			{
				qo.AddWhere("Title", " LIKE ", bp.difference.SystemConfig.getAppCenterDBType() == DBType.MySQL ? (" CONCAT('%'," + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title" + ",'%')") : (" '%'+" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title" + "+'%'"));
			}
			else
			{
				qo.AddWhere("Title", " LIKE ", " '%'||" + bp.difference.SystemConfig.getAppCenterDBVarStr() + "Title" + "||'%'");
			}
			qo.getMyParas().Add("Title", likeKey, false);
		}

		qo.addOrderBy("WorkID");
		qo.DoQuery();
		return bp.tools.Json.ToJson(this.ToDataTableField("WF_GenerWorkFlow"));
	}


		///#region 方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GenerWorkFlow();
	}
	/** 
	 流程实例集合
	*/
	public GenerWorkFlows()
	{
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GenerWorkFlow> ToJavaList()
	{
		return (java.util.List<GenerWorkFlow>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GenerWorkFlow> Tolist()
	{
		ArrayList<GenerWorkFlow> list = new ArrayList<GenerWorkFlow>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GenerWorkFlow)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
