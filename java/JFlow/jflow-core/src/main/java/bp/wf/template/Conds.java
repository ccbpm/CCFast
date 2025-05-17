package bp.wf.template;

import bp.da.*;
import bp.sys.*;
import bp.en.*; import bp.en.Map;
import bp.port.*;
import bp.difference.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/**
 条件s
*/
public class Conds extends Entities
{

		///#region 属性
	public final String getConditionDesc()
	{
		return "";
	}
	/**
	 获得Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Cond();
	}
	public final boolean GenerResult(GERpt en, WebUserCopy webUser){

		return GenerResult(en,webUser,null);
	}
	/**
	 执行计算
	 @param en 模式
	 @return
	*/
	public final boolean GenerResult(GERpt en, WebUserCopy webUser,Node nd)
	{

		try
		{
			if (this.size() == 0)
			{
				throw new RuntimeException("err@没有要计算的条件，无法计算.");
			}

			//给条件赋值.
			if (en != null)
			{
				for (Cond cd : this.ToJavaList())
				{
					cd.setWorkID(en.getOID());
					cd.en = en;
					cd.setWebUser(webUser);
					cd.setNode(nd);
				}
			}


				///#region 首先计算简单的.
			//如果只有一个条件,就直接范围该条件的执行结果.
			if (this.size() == 1)
			{
				Cond cond = this.get(0) instanceof Cond ? (Cond)this.get(0) : null;
				return cond.isPassed();
			}

				///#endregion 首先计算简单的.


				///#region 处理混合计算。
			String exp = "";
			for (Cond item : this.ToJavaList())
			{
				if (item.getHisDataFrom() == ConnDataFrom.CondOperator)
				{
					exp += " " + item.getOperatorValue();
					continue;
				}

				if (item.isPassed())
				{
					exp += " 1=1 ";
				}
				else
				{
					exp += " 1=2 ";
				}
			}

			//如果是混合计算.
			String sql = "";
			switch (SystemConfig.getAppCenterDBType())
			{
				case MSSQL:
					sql = " SELECT TOP 1 No FROM WF_Emp WHERE " + exp;
					break;
				case MySQL:
				case PostgreSQL:
				case UX:
				case HGDB:
				case GBASE8CByMySQL:
				case GBASE8A:
					sql = " SELECT No FROM WF_Emp WHERE " + exp + "    limit 1 ";
					break;
				case Oracle:
				case KingBaseR3:
				case KingBaseR6:
				case DM:
				case GBASE8CByOracle:
					sql = " SELECT No FROM WF_Emp WHERE " + exp + "  AND  rownum <=1 ";
					break;
				default:
					throw new RuntimeException("err@没有做的数据库类型判断.");
			}

			DataTable dt = DBAccess.RunSQLReturnTable(sql);
			if (dt.Rows.size() == 0)
			{
				return false;
			}
			return true;

				///#endregion 处理混合计算。
		}
		catch (RuntimeException ex)
		{
			throw new RuntimeException("err@计算条件出现错误:" + this.NodeID + " - " + ex.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 描述
	*/
	public final String getMsgOfDesc()
	{
		String msg = "";
		for (Cond c : this.ToJavaList())
		{
			msg += "@" + c.MsgOfCond;
		}
		return msg;
	}
	public int NodeID = 0;

		///#endregion


		///#region 构造
	/**
	 条件
	*/
	public Conds()
	{
	}
	/**
	 条件

	 @param fk_flow 流程编号
	*/
	public Conds(String fk_flow) throws Exception {
		this.Retrieve(CondAttr.FK_Flow, fk_flow, null);
	}
	/**
	 条件
	 @param ct 类型
	 @param nodeID 节点
	*/
	public Conds(CondType ct, int nodeID, long workid, GERpt enData) throws Exception {
		this.NodeID=nodeID;
		this.Retrieve(CondAttr.FK_Node, nodeID, CondAttr.CondType, ct.getValue(), CondAttr.Idx);
		for (Cond en : this.ToJavaList())
		{
			en.setWorkID(workid);
			en.en = enData;
		}
	}
	/**
	 条件 - 配置信息

	 @param ct
	 @param nodeID
	*/
	public Conds(CondType ct, int nodeID) throws Exception {
		this.Retrieve(CondAttr.FK_Node, nodeID, CondAttr.CondType, ct.getValue(), CondAttr.Idx);
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/**
	 转化成 java list,C#不能调用.

	 @return List
	*/
	public final java.util.List<Cond> ToJavaList()
	{
		return (java.util.List<Cond>)(Object)this;
	}
	/**
	 转化成list

	 @return List
	*/
	public final ArrayList<Cond> Tolist()
	{
		ArrayList<Cond> list = new ArrayList<Cond>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Cond)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
