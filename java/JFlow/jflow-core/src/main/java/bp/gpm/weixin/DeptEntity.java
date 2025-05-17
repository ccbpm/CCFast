package bp.gpm.weixin;

import bp.da.*;
import bp.*;
import java.util.*;

/** 
 部门信息
*/
public class DeptEntity
{

		///#region 属性.
	/** 
	  部门id 
	*/
	private String id;
	public final String getId()
	{
		return id;
	}
	public final void setId(String value)
	{
		id = value;
	}
	/** 
	 部门名称 
	*/
	private String name;
	public final String getName()
	{
		return name;
	}
	public final void setName(String value)
	{
		name = value;
	}
	/** 
	  父亲部门id。根部门为1 
	*/
	private String parentid;
	public final String getParentid()
	{
		return parentid;
	}
	public final void setParentid(String value)
	{
		parentid = value;
	}
	/** 
	  在父部门中的次序值。order值小的排序靠前
	*/
	private String order;
	public final String getOrder()
	{
		return order;
	}
	public final void setOrder(String value)
	{
		order = value;
	}

		///#endregion 属性.

	/** 
	 构造函数
	 
	 @param id
	*/
	public DeptEntity(String id)
	{
	}
}
