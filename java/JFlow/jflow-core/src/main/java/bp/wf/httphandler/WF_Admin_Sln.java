package bp.wf.httphandler;

import bp.da.*;
import bp.sys.*;
import bp.en.*;
import bp.wf.template.*;
import bp.difference.*;
import bp.wf.*;
import java.util.*;

/** 
 页面功能实体
*/
public class WF_Admin_Sln extends bp.difference.handler.DirectoryPageBase
{

	/** 
	 构造函数
	*/
	public WF_Admin_Sln()
	{
	}


		///#region 绑定流程表单
	/** 
	 获取所有节点，复制表单
	 
	 @return 
	*/
	public final String BindForm_GetFlowNodeDropList() throws Exception {
		Nodes nodes = new Nodes();
		nodes.Retrieve(NodeAttr.FK_Flow, this.getFlowNo(), NodeAttr.Step);

		if (nodes.size() == 0)
		{
			return "";
		}

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("<select id = \"copynodesdll\"  multiple = \"multiple\" style = \"border - style:None; width: 100%; Height: 100%; \">");

		for (Node node : nodes.ToJavaList())
		{
			sBuilder.append("<option " + (this.getNodeID() == node.getNodeID() ? "disabled = \"disabled\"" : "") + " value = \"" + node.getNodeID() + "\" >" + "[" + node.getNodeID() + "]" + node.getName() + "</ option >");
		}

		sBuilder.append("</select>");

		return sBuilder.toString();
	}

	/** 
	 复制表单到节点 
	 
	 @return 
	*/
	public final String BindFrmsDtl_DoCopyFrmToNodes() throws Exception {
		String nodeStr = this.GetRequestVal("NodeStr"); //节点string,
		String frmStr = this.GetRequestVal("frmStr"); //表单string,

		String[] nodeList = nodeStr.split("[,]", -1);
		String[] frmList = frmStr.split("[,]", -1);

		for (String node : nodeList)
		{
			if (node == null || node.isEmpty())
			{
				continue;
			}

			int nodeid = Integer.parseInt(node);

			//删除节点绑定的表单
			DBAccess.RunSQL("DELETE FROM WF_FrmNode WHERE FK_Node=" + nodeid);

			for (String frm : frmList)
			{
				if (frm == null || frm.isEmpty())
				{
					continue;
				}

				FrmNode fn = new FrmNode();
				FrmNode frmNode = new FrmNode();

				if (fn.IsExit("mypk", frm + "_" + this.getNodeID() + "_" + this.getFlowNo()))
				{
					frmNode.Copy(fn);
					frmNode.setMyPK(frm + "_" + nodeid + "_" + this.getFlowNo());
					frmNode.setFlowNo(this.getFlowNo());
					frmNode.setNodeID(nodeid);
					frmNode.setFrmID(frm);
				}
				else
				{
					frmNode.setMyPK(frm + "_" + nodeid + "_" + this.getFlowNo());
					frmNode.setFlowNo(this.getFlowNo());
					frmNode.setNodeID(nodeid);
					frmNode.setFrmID(frm);
				}

				frmNode.Insert();
			}
		}

		return "操作成功！";
	}

	/** 
	 保存流程表单
	 
	 @return 
	*/
	public final String BindFrmsDtl_Save() throws Exception {
		try
		{
			String formNos = ContextHolderUtils.getRequest().getParameter("formNos"); // this.context.Request["formNos");

			FrmNodes fns = new FrmNodes(this.getFlowNo(), this.getNodeID());
			//删除已经删除的。
			for (FrmNode fn : fns.ToJavaList())
			{
				if (formNos.contains("," + fn.getFrmID() + ",") == false)
				{
					fn.Delete();
					continue;
				}
			}

			// 增加集合中没有的。
			String[] strs = formNos.split("[,]", -1);
			for (String s : strs)
			{
				if (DataType.IsNullOrEmpty(s))
				{
					continue;
				}
				if (fns.contains(FrmNodeAttr.FK_Frm, s))
				{
					continue;
				}

				FrmNode fn = new FrmNode();
				fn.setFrmID(s);
				fn.setFlowNo(this.getFlowNo());
				fn.setNodeID(this.getNodeID());

				fn.setMyPK(fn.getFrmID() + "_" + fn.getNodeID() + "_" + fn.getFlowNo());

				fn.Save();
			}
			return "保存成功.";
		}
		catch (RuntimeException ex)
		{
			return "err:保存失败." + ex.getMessage();
		}
	}

	/** 
	 获取表单库所有表单
	 
	 @return 
	*/
	public final String BindForm_GenerForms() throws Exception {
		//形成树
		FlowFormTrees appendFormTrees = new FlowFormTrees();
		//节点绑定表单
		FrmNodes frmNodes = new FrmNodes(this.getFlowNo(), this.getNodeID());
		//所有表单类别
		SysFormTrees formTrees = new SysFormTrees();
		formTrees.RetrieveAll(SysFormTreeAttr.Idx);

		//根节点
		FlowFormTree root = new FlowFormTree();
		root.setName("表单库");
		int i = root.Retrieve(FlowFormTreeAttr.ParentNo, "0");
		if (i == 0)
		{
			root.setName("表单库");
			root.setNo("1");
			root.setNodeType("root");
			root.Insert();
		}
		root.setNodeType("root");
		appendFormTrees.AddEntity(root);

		for (SysFormTree formTree : formTrees.ToJavaList())
		{
			//已经添加排除
			if (appendFormTrees.contains("No", formTree.getNo()) == true)
			{
				continue;
			}

			//根节点排除
			if (formTree.getParentNo().equals("0"))
			{
				root.setNo(formTree.getNo());
				continue;
			}

			//文件夹
			FlowFormTree nodeFolder = new FlowFormTree();
			nodeFolder.setNo(formTree.getNo());
			nodeFolder.setParentNo(formTree.getParentNo());
			nodeFolder.setName(formTree.getName());
			nodeFolder.setNodeType("folder");
			if (formTree.getParentNo().equals("0"))
			{
				nodeFolder.setParentNo(root.getNo());
			}
			appendFormTrees.AddEntity(nodeFolder);

			//表单
			MapDatas mapS = new MapDatas();
			mapS.RetrieveByAttr(MapDataAttr.FK_FormTree, formTree.getNo());
			if (mapS != null && !mapS.isEmpty())
			{
				for (MapData map : mapS.ToJavaList())
				{
					FlowFormTree formFolder = new FlowFormTree();
					formFolder.setNo(map.getNo());
					formFolder.setParentNo(map.getFormTreeNo());
					formFolder.setName(map.getName() +"[" + map.getNo() + "]");
					formFolder.setNodeType("form");
					appendFormTrees.AddEntity(formFolder);
				}
			}
		}

		String strCheckedNos = "";
		//设置选中
		for (FrmNode frmNode : frmNodes.ToJavaList())
		{
			strCheckedNos += "," + frmNode.getFrmID() + ",";
		}
		//重置
		appendMenus.setLength(0);
		//生成数据
		TansEntitiesToGenerTree(appendFormTrees, root.getNo(), strCheckedNos);
		return appendMenus.toString();
	}

	/** 
	 将实体转为树形
	 
	 @param ens
	 @param rootNo
	 @param checkIds
	*/
	private StringBuilder appendMenus = new StringBuilder();
	private StringBuilder appendMenuSb = new StringBuilder();
	public final void TansEntitiesToGenerTree(Entities ens, String rootNo, String checkIds) throws Exception {
		Object tempVar = ens.GetEntityByKey(rootNo);
		EntityTree root = tempVar instanceof EntityTree ? (EntityTree)tempVar : null;
		if (root == null)
		{
			throw new RuntimeException("@没有找到rootNo=" + rootNo + "的entity.");
		}
		appendMenus.append("[{");
		appendMenus.append("\"id\":\"" + rootNo + "\"");
		appendMenus.append(",\"text\":\"" + root.getName() +"\"");
		appendMenus.append(",\"state\":\"open\"");

		//attributes
		FlowFormTree formTree = root instanceof FlowFormTree ? (FlowFormTree)root : null;
		if (formTree != null)
		{
			String url = formTree.getUrl() == null ? "" : formTree.getUrl();
			url = url.replace("/", "|");
			appendMenus.append(",\"attributes\":{\"NodeType\":\"" + formTree.getNodeType() + "\",\"IsEdit\":\"" + formTree.isEdit() + "\",\"Url\":\"" + url + "\"}");
		}
		// 增加它的子级.
		appendMenus.append(",\"children\":");
		AddChildren(root, ens, checkIds);
		appendMenus.append(appendMenuSb);
		appendMenus.append("}]");
	}

	public final void AddChildren(EntityTree parentEn, Entities ens, String checkIds) throws Exception {
		appendMenus.append(appendMenuSb);
		appendMenuSb.setLength(0);

		appendMenuSb.append("[");
		for (Entity en : ens)
		{
			EntityTree item = (EntityTree)en;
			if (!Objects.equals(item.getParentNo(), parentEn.getNo()))
			{
				continue;
			}

			if (checkIds.contains("," + item.getNo() + ","))
			{
				appendMenuSb.append("{\"id\":\"" + item.getNo() + "\",\"text\":\"" + item.getName() +"\",\"checked\":true");
			}
			else
			{
				appendMenuSb.append("{\"id\":\"" + item.getNo() + "\",\"text\":\"" + item.getName() +"\",\"checked\":false");
			}


			//attributes
			FlowFormTree formTree = item instanceof FlowFormTree ? (FlowFormTree)item : null;
			if (formTree != null)
			{
				String url = formTree.getUrl() == null ? "" : formTree.getUrl();
				String ico = "icon-tree_folder";
				String treeState = "closed";
				url = url.replace("/", "|");
				appendMenuSb.append(",\"attributes\":{\"NodeType\":\"" + formTree.getNodeType() + "\",\"IsEdit\":\"" + formTree.isEdit() + "\",\"Url\":\"" + url + "\"}");
				//图标
				if (Objects.equals(formTree.getNodeType(), "form"))
				{
					ico = "icon-sheet";
				}
				appendMenuSb.append(",\"state\":\"" + treeState + "\"");
				appendMenuSb.append(",iconCls:\"");
				appendMenuSb.append(ico);
				appendMenuSb.append("\"");
			}
			// 增加它的子级.
			appendMenuSb.append(",\"children\":");
			AddChildren(item, ens, checkIds);
			appendMenuSb.append("},");
		}
		if (appendMenuSb.length() > 1)
		{
			appendMenuSb = appendMenuSb.deleteCharAt(appendMenuSb.length() - 1);
		}
		appendMenuSb.append("]");
		appendMenus.append(appendMenuSb);
		appendMenuSb.setLength(0);
	}

		///#endregion


		///#region 表单方案.
	/** 
	 表单方案
	 
	 @return 
	*/
	public final String BindFrms_Init() throws Exception {
		//注册这个枚举，防止第一次运行出错.
		SysEnums ses = new SysEnums("FrmEnableRole");

		String text = "";
		Node nd = new Node(this.getNodeID());

		//FrmNodeExt fns = new FrmNodeExt(this.FlowNo, this.getNodeID());

		FrmNodes fns = new FrmNodes(this.getFlowNo(), this.getNodeID());


			///#region 如果没有ndFrm 就增加上.
		boolean isHaveNDFrm = false;
		for (FrmNode fn : fns.ToJavaList())
		{
			if (Objects.equals(fn.getFrmID(), "ND" + this.getNodeID()))
			{
				isHaveNDFrm = true;
				break;
			}
		}

		if (isHaveNDFrm == false)
		{
			FrmNode fn = new FrmNode();
			fn.setFlowNo(this.getFlowNo());
			fn.setFrmID("ND" + this.getNodeID());
			fn.setNodeID(this.getNodeID());

			fn.setFrmEnableRole(FrmEnableRole.Disable); //就是默认不启用.
			fn.setFrmSln(FrmSln.forValue(0));
			//  fn.IsEdit = true;
			fn.setItIsEnableLoadData(true);
			fn.Insert();
			fns.AddEntity(fn);
		}

			///#endregion 如果没有ndFrm 就增加上.

		//组合这个实体才有外键信息.
		for (FrmNode fn : fns.ToJavaList())
		{
			MapData md = new MapData();
			md.setNo(fn.getFrmID());
			if (md.getIsExits()==false)
			{
				fn.Delete(); //说明该表单不存在了，就需要把这个删除掉.
				continue;
			}
		   // FrmNodeExt myen = new FrmNodeExt(fn.MyPK);
			//fnes.AddEntity(myen);
		}

		FrmNodeExts fnes = new FrmNodeExts();
		fnes.Retrieve(FrmNodeAttr.FK_Flow, this.getFlowNo(), FrmNodeAttr.FK_Node, this.getNodeID(), FrmNodeAttr.Idx);

		//把json数据返回过去.
		return fnes.ToJson("dt");
	}

		///#endregion 表单方案.


		///#region 字段权限.

	public static class FieldsAttrs
	{
		public int idx;
		public String KeyOfEn;
		public String Name;
		public String LGTypeT;
		public boolean UIVisible;
		public boolean UIIsEnable;
		public boolean ItIsSigan;
		public String DefVal;
		public boolean ItIsNotNull;
		public String RegularExp;
		public boolean ItIsWriteToFlowTable;
		/** 
		  add new attr 是否写入流程注册表
		*/
		public boolean ItIsWriteToGenerWorkFlow;
	}

		///#endregion 字段权限.

}
