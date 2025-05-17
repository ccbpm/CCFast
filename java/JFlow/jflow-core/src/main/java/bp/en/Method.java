package bp.en;
import bp.sys.base.Glo;

import java.io.Serializable;
import java.math.*;

/**
 Method 的摘要说明
*/
public abstract class Method implements Serializable
{
	private static final long serialVersionUID = 1L;
	/**
	 信息显示类型
	*/
	public MsgShowType HisMsgShowType = MsgShowType.Blank;


		///Http
	public final String Request(String key)
	{
		return Glo.getRequest().getParameter(key);
	}
	/**
	 获取MyPK
	*/
	public final String getRequestRefMyPK()
	{
		String s = Request("RefMyPK");
		if (s == null)
		{
			s = Request("RefPK");
		}

		return s;
	}
	public final String getRequestRefNo()
	{
		return Request("RefNo");
	}
	public final int getRequestRefOID()
	{
		return Integer.parseInt(Request("RefOID"));
	}

		/// Http


		///ROW
	/**
	 获取Key值

	 param key 键
	 @return 结果
	*/
	public final Object GetValByKey(String key)
	{
		return this.getRow().GetValByKey(key);
	}
	/**
	 获取str值

	 param key 键
	 @return 结果
	*/
	public final String GetValStrByKey(String key)  {
		return this.GetValByKey(key).toString();
	}
	/**
	 获取int值

	 param key 键
	 @return 结果
	*/
	public final int GetValIntByKey(String key)
	{
		return (Integer)this.GetValByKey(key);
	}

	/**
	 获取decimal值

	 param key 键
	 @return 结果
	*/
	public final BigDecimal GetValDecimalByKey(String key)
	{
		return (BigDecimal)this.GetValByKey(key);
	}
	/**
	 获取bool值

	 param key 键
	 @return 结果
	*/
	public final boolean GetValBoolByKey(String key)
	{
		if (this.GetValIntByKey(key) == 1)
		{
			return true;
		}
		return false;
	}
	public final void SetValByKey(String attrKey, int val)
	{
		this.getRow().SetValByKey(attrKey, val);
	}
	public final void SetValByKey(String attrKey, long val)
	{
		this.getRow().SetValByKey(attrKey, val);
	}
	public final void SetValByKey(String attrKey, float val)
	{
		this.getRow().SetValByKey(attrKey, val);
	}
	public final void SetValByKey(String attrKey, BigDecimal val)
	{
		this.getRow().SetValByKey(attrKey, val);
	}
	public final void SetValByKey(String attrKey, Object val)
	{
		this.getRow().SetValByKey(attrKey, val);
	}
	/**
	 实体的 map 信息。
	*/
	//public abstract void EnMap();
	private Row _row = null;
	public final Row getRow()
	{
		if (this.getHisAttrs() == null)
		{
			return null;
		}

		if (this._row == null)
		{
			this._row = new Row();
			this._row.LoadAttrs(this.getHisAttrs());
		}

		return this._row;
	}
	public final void setRow(Row value)
	{this._row = value;
	}

		///

	/**
	 方法基类
	*/
	public Method()
	{

	}


		///属性
	/**
	 参数
	*/
	private Attrs _HisAttrs = null;
	public final Attrs getHisAttrs()
	{
		if (_HisAttrs == null)
		{
			_HisAttrs = new Attrs();
		}
		return _HisAttrs;
	}
	/**
	 标题
	*/
	public String Title = null;
	public String Help = null;
	public String GroupName = "基本方法";
	/**
	 操作前提示信息
	*/
	public String Warning = null;
	/**
	 图标
	*/
	public String Icon = null;
	public final String GetIcon(String path)
	{
		if (this.Icon == null)
		{
			return "<img src='/WF/Img/Btn/Do.gif'  border=0 />";
		}
		else
		{
			return Icon;
			//return "<img src='" + path + Icon + "'  border=0 />";
		}
	}
	/**
	 提示信息
	*/
	public String ToolTip = null;
	/**
	 目标
	*/
	public String Target = "OpenWin";
	/**
	 高度
	*/
	public int Height = 600;
	/**
	 宽度
	*/
	public int Width = 800;
	/**
	 执行
	 @return
	 * @throws Exception
	*/
	public abstract Object Do() throws Exception;
	public abstract void Init() ;
	/**
	 权限管理
	 * @throws Exception
	*/
	public abstract boolean getIsCanDo() ;
	/**
	 是否显示在功能列表里
	*/
	public boolean ItIsVisable = true;

		///
}
