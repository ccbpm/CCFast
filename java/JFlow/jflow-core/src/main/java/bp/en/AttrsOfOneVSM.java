package bp.en;

import java.io.Serializable;
import java.util.ArrayList;

/**
 AttrsOfOneVSM 集合
*/
public class AttrsOfOneVSM extends ArrayList<AttrOfOneVSM> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String GroupName = "";

	public AttrsOfOneVSM()
	{
	}

	/**
	 增加一个SearchKey .

	 param attr SearchKey
	*/
	public final void Add(AttrOfOneVSM attr)  {
		if (this.getIsExits(attr))
		{
			return;
		}
		attr.GroupName = this.GroupName;
		this.add(attr);
	}

	/**
	 是不是存在集合里面

	 param en 要检查的EnDtl
	 @return true/false
	*/
	public final boolean getIsExits(AttrOfOneVSM en)  {
		for (AttrOfOneVSM attr : this)
		{
			if (attr.getEnsOfMM() == en.getEnsOfMM())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 增加一个属性

	 param _ensOfMM 多对多的实体
	 param _ensOfM 多实体
	 param AttrOfOneInMM 点实体,在MM中的属性
	 param AttrOfMInMM 多实体主键在MM中的属性
	 param AttrOfMText
	 param AttrOfMValue
	 param desc 描述
	 * @throws Exception
	*/

	public final void Add(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String AttrOfMText, String AttrOfMValue, String desc, Dot2DotModel model, EntitiesTree ensTree)
	{
		Add(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc, model, ensTree, null);
	}

	public final void Add(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String AttrOfMText, String AttrOfMValue, String desc, Dot2DotModel model)
	{
		Add(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc, model, null, null);
	}

	public final void Add(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String AttrOfMText, String AttrOfMValue, String desc)
	{
		Add(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc, Dot2DotModel.Default, null, null);
	}


	public final void Add(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String AttrOfMText, String AttrOfMValue, String desc, Dot2DotModel model, EntitiesTree ensTree, String refTreeAttr)
	{

		//属性.
		AttrOfOneVSM en = new AttrOfOneVSM(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc);

		//工作模式.
		en.dot2DotModel = model;
		en.EnsTree = ensTree;
		en.RefTreeAttr = refTreeAttr;

		this.Add(en);
	}
	/**
	 绑定树模式

	 param _ensOfMM 比如 bp.wf.NodeDepts
	 param _ensOfM 比如: bp.port.Depts
	 param rootNo 跟节点
	 param AttrOfOneInMM 比如:FK_Node
	 param AttrOfMInMM 比如:FK_Dept
	 param desc 比如:节点绑定部门
	 param AttrOfMText 一般是Name
	 param AttrOfMValue 一般是No
	 param rootNo 根目录节点
	 * @throws Exception
	*/

	public final void AddBranches(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String AttrOfMText, String AttrOfMValue, String rootNo)
	{
		AddBranches(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, AttrOfMText, AttrOfMValue, rootNo, null);
	}

	public final void AddBranches(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String AttrOfMText, String AttrOfMValue)
	{
		AddBranches(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, AttrOfMText, AttrOfMValue, "0", null);
	}

	public final void AddBranches(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String AttrOfMText)
	{
		AddBranches(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, AttrOfMText, "No", "0", null);
	}

	public final void AddBranches(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc)
	{
		AddBranches(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, "Name", "No", "0", null);
	}


	public final void AddBranches(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String AttrOfMText, String AttrOfMValue, String rootNo, String expShowCols)
	{
		//属性.
		AttrOfOneVSM en = new AttrOfOneVSM(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc);

		//工作模式.
		en.dot2DotModel = Dot2DotModel.TreeDept; //分组模式.

		en.RootNo = rootNo; //默认的根目录.
		en.ExtShowCols = expShowCols; //显示的列.
		this.Add(en);
	}
	/**
	 增加树杆叶子类型

	 param _ensOfMM
	 param _ensOfM
	 param AttrOfOneInMM
	 param AttrOfMInMM
	 param desc
	 param defaultGroupKey
	 param AttrOfMText
	 param AttrOfMValue
	 param rootNo 根目录编号
	 * @throws Exception
	*/

	public final void AddBranchesAndLeaf(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText, String AttrOfMValue, String rootNo)
	{
		AddBranchesAndLeaf(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, AttrOfMText, AttrOfMValue, rootNo, null);
	}

	public final void AddBranchesAndLeaf(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText, String AttrOfMValue)
	{
		AddBranchesAndLeaf(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, AttrOfMText, AttrOfMValue, "0", null);
	}

	public final void AddBranchesAndLeaf(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText)
	{
		AddBranchesAndLeaf(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, AttrOfMText, "No", "0", null);
	}

	public final void AddBranchesAndLeaf(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey)
	{
		AddBranchesAndLeaf(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, "Name", "No", "0", null);
	}

	public final void AddBranchesAndLeaf(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc) throws Exception
	{
		AddBranchesAndLeaf(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, null, "Name", "No", "0", null);
	}


	public final void AddBranchesAndLeaf(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText, String AttrOfMValue, String rootNo, String extShowCols)
	{
		//属性.
		AttrOfOneVSM en = new AttrOfOneVSM(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc);

		//工作模式.
		en.dot2DotModel = Dot2DotModel.TreeDeptEmp; //分组模式.

		//默认的分组字段，可以是一个类名或者枚举.
		en.DefaultGroupAttrKey = defaultGroupKey;
		en.RootNo = rootNo; //默认的根目录.
		en.ExtShowCols = extShowCols; //显示的扩展列 , @FK_DeptName=部门名称@OrgName=组织名称.

		this.Add(en);
	}
	/**
	 增加分组列表模式

	 param _ensOfMM
	 param _ensOfM
	 param AttrOfOneInMM
	 param AttrOfMInMM
	 param desc
	 param defaultGroupKey
	 param AttrOfMText
	 * @throws Exception
	*/

	public final void AddGroupListModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText) throws Exception
	{
		AddGroupListModel(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, AttrOfMText, "No");
	}

	public final void AddGroupListModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey)
	{
		AddGroupListModel(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, "Name", "No");
	}

	public final void AddGroupListModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc) throws Exception
	{
		AddGroupListModel(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, null, "Name", "No");
	}


	public final void AddGroupListModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText, String AttrOfMValue)
	{
		//属性.
		AttrOfOneVSM en = new AttrOfOneVSM(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc);

		//工作模式.
		en.dot2DotModel = Dot2DotModel.TreeDeptEmp; //分组模式.

		//默认的分组字段，可以是一个类名或者枚举.
		en.DefaultGroupAttrKey = defaultGroupKey;
		en.RootNo = "0";

		this.Add(en);
	}
	/**
	 绑定分组列表平铺模式

	 param _ensOfMM
	 param _ensOfM
	 param AttrOfOneInMM
	 param AttrOfMInMM
	 param desc 标签或者描述
	 param AttrOfMText 显示的标签,一般为 Name
	 param defaultGroupKey 默认的分组外键或者枚举,如果为空就不分组.
	 * @throws Exception
	*/

	public final void AddGroupPanelModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText) throws Exception
	{
		AddGroupPanelModel(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, AttrOfMText, "No");
	}

	public final void AddGroupPanelModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey)
	{
		AddGroupPanelModel(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, defaultGroupKey, "Name", "No");
	}

	public final void AddGroupPanelModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc)
	{
		AddGroupPanelModel(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, desc, null, "Name", "No");
	}

	public final void AddGroupPanelModel(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String desc, String defaultGroupKey, String AttrOfMText, String AttrOfMValue)
	{
		//属性.
		AttrOfOneVSM en = new AttrOfOneVSM(_ensOfMM, _ensOfM, AttrOfOneInMM, AttrOfMInMM, AttrOfMText, AttrOfMValue, desc);

		//工作模式.
		en.dot2DotModel = Dot2DotModel.Default; //分组模式.

		//默认的分组字段，可以是一个类名或者枚举.
		en.DefaultGroupAttrKey = defaultGroupKey;

		this.Add(en);
	}

}
