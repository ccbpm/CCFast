package bp.wf.port;

import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.wf.template.*;
import bp.*;
import bp.wf.*;
import java.util.*;

/**
 部门(即将弃用)
*/
public class Dept extends EntityNoName
{

		///#region 属性
	public final int getIdx()  {
		return this.GetValIntByKey(DeptAttr.Idx);
	}
	public final void setIdx(int value){
		this.SetValByKey(DeptAttr.Idx, value);
	}
	/**
	 父节点编号
	*/
	public final String getParentNo()  {
		return this.GetValStrByKey(DeptAttr.ParentNo);
	}
	public final void setParentNo(String value){
		this.SetValByKey(DeptAttr.ParentNo, value);
	}
	public final String getOrgNo()  {
		return this.GetValStrByKey(DeptAttr.OrgNo);
	}
	public final void setOrgNo(String value){
		this.SetValByKey(DeptAttr.OrgNo, value);
	}
	public final String getLeader()  {
		return this.GetValStrByKey(DeptAttr.Leader);
	}
	public final void setLeader(String value){
		this.SetValByKey(DeptAttr.Leader, value);
	}

		///#endregion


		///#region 构造函数
	/**
	 部门
	*/
	public Dept()
	{
	}
	/**
	 部门

	 @param no 编号
	*/
	public Dept(String no) throws Exception
	{
		super(no);
	}

		///#endregion


		///#region 重写方法
	/**
	 UI界面上的访问控制
	*/
	@Override
	public UAC getHisUAC()
	{
		UAC uac = new UAC();
		uac.OpenForSysAdmin();
		uac.IsInsert = false;
		return uac;
	}
	/**
	 Map
	*/
	@Override
	public Map getEnMap() {
		if (this.get_enMap() != null)
			return this.get_enMap();

		Map map = new Map("Port_Dept", "部门");

		map.AddTBStringPK(DeptAttr.No, null, "编号", true, false, 1, 30, 40);
		map.AddTBString(DeptAttr.Name, null,"名称", true, false, 0, 60, 200);
		map.AddTBString(DeptAttr.ParentNo, null, "父节点编号", true, true, 0, 30, 40);
		map.AddTBString(DeptAttr.OrgNo, null, "隶属组织", true, true, 0, 50, 250);

		map.AddDDLEntities(DeptAttr.Leader, null, "部门领导", new bp.port.Emps(), true);
		map.AddTBInt(DeptAttr.Idx, 0, "序号", true, false);

		//map.AddTBString(DeptAttr.FK_Unit, "1", "隶属单位", false, false, 0, 50, 10);
		//设置二级管理员
		RefMethod rm = new RefMethod();
		rm.Title = "设置二级管理员";
		rm.ClassMethodName = this.toString() + ".ToSetAdminer";
		rm.refMethodType = RefMethodType.RightFrameOpen;
		rm.ItIsCanBatch = false; //是否可以批处理？
		map.AddRefMethod(rm);
		this.set_enMap(map);
		return this.get_enMap();
	}

		///#endregion


	@Override
	protected boolean beforeDelete() throws Exception
	{
		//检查是否可以删除.
		bp.port.Dept dept = new bp.port.Dept(this.getNo());
		dept.CheckIsCanDelete();

		return super.beforeDelete();
	}
	/**
	 跳转到单机版二级管理员设置

	 @return
	*/
	public final String ToSetAdminer() {
		return "../../../GPM/SetAdminer.htm?FK_Dept=" + this.getNo();
	}
	/**
	 设置单机版二级管理员

	 @param adminer
	 @return
	*/
	public final String DoSetAdminer(String adminer, String userName) throws Exception {
		GloVar gloVar = new GloVar();
		gloVar.setNo(adminer + "_Adminer");
		if (gloVar.RetrieveFromDBSources() == 1)
		{
			return userName + "已经设置成部门[" + this.getName() + "]的二级管理员";
		}
		gloVar.setName(userName);
		gloVar.setVal(adminer);
		gloVar.setNote(this.getNo());
		gloVar.setGroupKey("Adminer");
		gloVar.Insert();

		//设置流程目录、表单目录

			///#region 检查流程树.
		FlowSort fs = new bp.wf.template.FlowSort();
		fs.setNo(this.getNo());
		if (fs.RetrieveFromDBSources() == 0)
		{
			//获得根目录节点.
			FlowSort root = new FlowSort();
			int i = root.Retrieve(FlowSortAttr.ParentNo, "0");

			//设置流程树权限.
			fs.setNo(this.getNo());
			fs.setName(this.getName());
			fs.setParentNo(root.getNo());
			fs.setIdx(999);
			fs.DirectInsert();

			//创建下一级目录.
			bp.en.EntityTree tempVar = fs.DoCreateSubNode(null);
			FlowSort en = tempVar instanceof FlowSort ? (FlowSort)tempVar : null;
			en.setName("日常办公类");
			en.setDomainExt("");
			en.DirectUpdate();
		}

			///#endregion 检查流程树.


			///#region 检查表单树.
		//表单根目录.
		SysFormTree ftRoot = new SysFormTree();
		int val = ftRoot.Retrieve(FlowSortAttr.ParentNo, "0");
		if (val == 0)
		{
			val = ftRoot.Retrieve(FlowSortAttr.No, "100");
			if (val == 0)
			{
				ftRoot.setNo("100");
				ftRoot.setName("表单库");
				ftRoot.setParentNo("0");
				ftRoot.Insert();
			}
			else
			{
				ftRoot.setParentNo("0");
				ftRoot.setName("表单库");
				ftRoot.Update();
			}
		}

		//设置表单树权限.
		SysFormTree ft = new SysFormTree();
		ft.setNo(this.getNo());
		if (ft.RetrieveFromDBSources() == 0)
		{
			ft.setName(this.getName());
			ft.setParentNo(ftRoot.getNo());
			ft.setIdx(999);
			ft.DirectInsert();

			//创建两个目录.
			bp.en.EntityTree tempVar2 = ft.DoCreateSubNode(null);
			SysFormTree mySubFT = tempVar2 instanceof SysFormTree ? (SysFormTree)tempVar2 : null;
			mySubFT.setName("日常办公类");
			mySubFT.DirectUpdate();
		}


			///#endregion 检查表单树.
		return "设置成功";
	}
}
