package bp.flowable.httphandler;

import bp.web.*;
import bp.en.*;
import bp.wf.data.*;
import bp.difference.*;
import bp.wf.*;
import bp.wf.httphandler.WF_Admin_CCBPMDesigner2018;
import bp.wf.template.Direction;
import bp.wf.template.DirectionAttr;
import bp.wf.template.Directions;
import bp.wf.template.TemplateGlo;

import java.net.URLDecoder;
import java.util.*;

/** 
 页面功能实体
*/
public class flowable_AdminFlow extends bp.difference.handler.DirectoryPageBase
{
	/** 
	 构造函数
	*/
	public flowable_AdminFlow()
	{
	}

	public final String CreateFlowableTemplate() throws Exception {

		//String flowNo=this.getRefNo();
		//获得模板编号.
		String flowNo=this.GetRequestVal("No");

		//执行创建 flowable 的流程模板. 标记为 flowNo

		// 创建开始节点为  101

		//创建第2个节点 102.

		//创建连接线.

		return "创建成功";

	}
	public final String CreateNode() throws Exception {

		int x= this.GetRequestValInt("X");
		int y= this.GetRequestValInt("Y");
		String flowNo=this.GetRequestVal("No");

		Node node = TemplateGlo.NewNode( flowNo, x, y, "", 0);
		return  String.valueOf( node.getNodeID());
	}
	public final String DeleteNode() throws Exception {
		try
		{
			Node node = new Node();
			node.setNodeID(this.getNodeID());
			if (node.RetrieveFromDBSources() == 0)
			{
				return "err@删除失败,没有删除到数据，估计该节点已经别删除了.";
			}

			if (node.getItIsStartNode())
				return "err@开始节点不允许被删除。";

			node.Delete();
			return "删除成功.";
		}
		catch (RuntimeException ex)
		{
			return "err@" + ex.getMessage();
		}
	}
	public final String ChangeNodeName() throws Exception {
		String FK_Node = this.GetValFromFrmByKey("NodeID");
		String NodeName = URLDecoder.decode(this.GetValFromFrmByKey("NodeName"),"UTF-8");

		Node node = new Node();
		node.setNodeID(Integer.parseInt(FK_Node));
		int iResult = node.RetrieveFromDBSources();
		if (iResult > 0)
		{
			node.setName(NodeName);
			node.Update();
			return "@修改成功.";
		}

		return "err@修改节点失败，请确认该节点是否存在？";
	}
	public final String ChangeFlowName() throws Exception {
		String flowNo=this.GetRequestVal("No");
		Flow en=new Flow(flowNo);
		en.Update();
		return "修改成功.";
	}

	public final String SaveFlow() throws Exception {
		String flowNo=this.GetRequestVal("No");
		Flow en=new Flow(flowNo);
		en.Update();
		return "修改成功.";
	}
	/**
	 删除连接线
	 @return
	 */
	public final String Direction_Delete()
	{
		try
		{
			String pk = this.getFlowNo() + "_" + this.getNodeID() + "_" + this.GetValFromFrmByKey("ToNode");
			Direction dir = new Direction();
			dir.setMyPK(pk);
			dir.Delete();
			return "@删除成功！";
		}
		catch (Exception ex)
		{
			return "@err:" + ex.getMessage();
		}
	}
	public final String Direction_Init()
	{
		try
		{
			String pk = this.getFlowNo() + "_" + this.getNodeID() + "_" + this.GetValFromFrmByKey("ToNode");

			Direction dir = new Direction();
			dir.setMyPK(pk);

			if (dir.RetrieveFromDBSources() > 0)
			{
				return dir.getDes();
			}

			return "";
		}
		catch (Exception ex)
		{
			return "@err:" + ex.getMessage();
		}
	}
	/**
	 @return
	 */
	public final String Direction_Save()
	{
		try
		{
			String pk = this.getFlowNo() + "_" + this.getNodeID() + "_" + this.GetValFromFrmByKey("ToNode");

			Direction dir = new Direction();
			dir.setMyPK(pk);

			if (dir.RetrieveFromDBSources() > 0)
			{
				dir.setDes(this.GetValFromFrmByKey("Des"));
				dir.DirectUpdate();
			}
			return "@保存成功！";
		}
		catch (Exception ex)
		{
			return "@err:" + ex.getMessage();
		}
	}
}
