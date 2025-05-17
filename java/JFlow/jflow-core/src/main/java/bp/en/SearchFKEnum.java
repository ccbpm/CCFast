package bp.en;

import bp.*;

import java.io.Serializable;

/**
 查询属性
*/
public class SearchFKEnum implements Serializable
{
	private static final long serialVersionUID = 1L;
	/**
	 查询属性
	*/
	public Attr HisAttr = null;
	/**
	 是否显示全部
	*/
	public boolean IsShowAll = true;
	/**
	 及联子菜单
	*/
	public String RelationalDtlKey = null;
	/**
	 查询的字段
	*/
	public String Key = null;
	/**
	 下拉框显示的宽度
	*/
	public int Width = 120;
	public SearchFKEnum()
	{
	}
}
