package bp.en;

import java.io.Serializable;

/**
 SearchKey 的摘要说明。
 用来处理一条记录的存放，问题。
*/
public class AttrOfOneVSM  implements Serializable
{
	private static final long serialVersionUID = 1L;
		///基本属性
	/**
	 工作模式
	*/
	public Dot2DotModel dot2DotModel = Dot2DotModel.Default;
	/**
	 树
	*/
	public EntitiesTree EnsTree = null;
	/**
	 默认的分组key.
	*/
	public String DefaultGroupAttrKey = null;
	/**
	 树的根节点
	*/
	public String RootNo = null;
	/**
	 显示的扩展列
	*/
	public String ExtShowCols = null;
	/**
	 关联的树字段
	*/
	public String RefTreeAttr = null;
	/**
	 多对多的实体.
	*/
	private Entities _ensOfMM = null;
	public String GroupName = "";
	/**
	 多对多的实体集合
	*/
	public final Entities getEnsOfMM()
	{
		return _ensOfMM;
	}
	public final void setEnsOfMM(Entities value)
	{_ensOfMM = value;
	}
	/**
	 多对多的实体
	*/
	private Entities _ensOfM = null;
	/**
	 多对多的实体集合
	*/
	public final Entities getEnsOfM()
	{
		return _ensOfM;
	}
	public final void setEnsOfM(Entities value)
	{_ensOfM = value;
	}
	/**
	 M的实体属性在多对多的实体中
	*/
	private String _Desc = null;
	/**
	 的实体属性在多对多的实体中
	*/
	public final String getDesc()
	{
		return _Desc; //edited by liuxc,2014-10-18 "<font color=blue ><u>" + _Desc + "</u></font>";
	}
	public final void setDesc(String value)
	{
		_Desc = value;
	}
	/**
	 一的实体属性在多对多的实体中.
	*/
	private String _AttrOfOneInMM = null;
	/**
	 一的实体属性在多对多的实体中
	*/
	public final String getAttrOfOneInMM()
	{
		return _AttrOfOneInMM;
	}
	public final void setAttrOfOneInMM(String value)
	{
		_AttrOfOneInMM = value;
	}

	/**
	 M的实体属性在多对多的实体中
	*/
	private String _AttrOfMInMM = null;
	/**
	 的实体属性在多对多的实体中
	*/
	public final String getAttrOfMInMM()
	{
		return _AttrOfMInMM;
	}
	public final void setAttrOfMInMM(String value)
	{
		_AttrOfMInMM = value;
	}
	/**
	 标签
	*/
	private String _AttrOfMText = null;
	/**
	 标签
	*/
	public final String getAttrOfMText()
	{
		return _AttrOfMText;
	}
	public final void setAttrOfMText(String value)
	{
		_AttrOfMText = value;
	}
	/**
	 Value
	*/
	private String _AttrOfMValue = null;
	/**
	 Value
	*/
	public final String getAttrOfMValue()
	{
		return _AttrOfMValue;
	}
	public final void setAttrOfMValue(String value)
	{
		_AttrOfMValue = value;
	}

		///


		///构造方法
	/**
	 AttrOfOneVSM
	*/
	public AttrOfOneVSM()
	{
	}
	/**
	 AttrOfOneVSM

	 param _ensOfMM
	 param _ensOfM
	 param AttrOfOneInMM
	 param AttrOfMInMM
	 param AttrOfMText
	 param AttrOfMValue
	 * @
	*/
	public AttrOfOneVSM(Entities _ensOfMM, Entities _ensOfM, String AttrOfOneInMM, String AttrOfMInMM, String AttrOfMText, String AttrOfMValue, String desc)
	{
		this.setEnsOfM(_ensOfM);
		this.setEnsOfMM(_ensOfMM);
		this.setAttrOfOneInMM(AttrOfOneInMM);
		this.setAttrOfMInMM(AttrOfMInMM);
		this.setAttrOfMText(AttrOfMText);
		this.setAttrOfMValue(AttrOfMValue);
		this.setDesc(desc);
	}

		///

}
