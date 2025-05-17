package bp.en;

import java.io.Serializable;
import java.lang.reflect.Method;

import bp.da.DataType;
import bp.tools.StringHelper;

/**
 * RefMethod 的摘要说明。
 */
public class RefMethod implements Serializable
{

	private static final long serialVersionUID = 1L;

	// 与窗口有关的方法.
	/**
	 * 高度
	 */
	public int Height = 600;
	/**
	 * 宽度
	 */
	public int Width = 800;
	public String Target = "_B123";
	/**
	 * 功能
	 */
	public RefMethodType refMethodType=bp.en.RefMethodType.Func;

	/**
	 * 相关字段
	 */
	public String RefAttrKey = null;
	/**
	 * 连接标签
	 */
	public String RefAttrLinkLabel = null;
	/**
	 * 分组名称
	 */
	public String GroupName = null;


    public String IsShowForEnsCondtion = null;



	/**
	 * 是否显示在Ens中?
	 */
	public boolean ItIsForEns = false;

	/**
	 * 相关功能
	 */
	public RefMethod()
	{
	}

	/**
	 * 参数
	 */
	private Attrs _HisAttrs = null;

	/**
	 * 参数
	 */
	public final Attrs getHisAttrs()
	{
		if (_HisAttrs == null)
		{
			_HisAttrs = new Attrs();
		}
		return _HisAttrs;
	}

	public final void setHisAttrs(Attrs value)
	{_HisAttrs = value;
	}

	/**
	 * 索引位置，用它区分实体.
	 */
	public int Index = 0;
	/**
	 * 是否显示
	 */
	public boolean Visable = true;
	/**
	 * 是否可以批处理
	 */
	public boolean ItIsCanBatch = false;
	/**
	 * 标题
	 */
	public String Title = null;
	/**
	 * 操作前提示信息
	 */
	public String Warning = null;
	/**
	 * 连接
	 */
	public String ClassMethodName = null;

	/**
	 * 图标
	 */
	public String Icon = null;

	public final String GetIcon(String path)
	{
		if (this.Icon == null)
		{
			/*
			 * warning return null;
			 */
			return "<img src='" + path + "WF/Img/Btn/Do.gif'  border=0 />";
		} else
		{
			String url = Icon;
			if (!Icon.contains("http://"))
			{
				Icon = path + Icon;
				// url = url.replace("//", "/");
			}

			return "<img src='" + url + "'  border=0 />";
		}
	}

	/**
	 * 提示信息
	 */
	public String ToolTip = null;

	/**
	 * PKVal
	 */
	public Object PKVal = "PKVal";
	/**


	*/
	public Entity HisEn = null;
	/**
	 * 实体PK
	 */
	public String[] PKs = (new String("")).split("[.]", -1);

	/**
	 * 执行
	 *
	 * param paras
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public final Object Do(Object[] paras) throws Exception
	{
		// throw new RuntimeException("编译不通过");

		String str = StringHelper.trim(this.ClassMethodName, ' ', ';', '.');
		int pos = str.lastIndexOf(".");
		String clas = this.HisEn.toString();
		String meth = str;
		if (pos > 0)
		{
			clas = str.substring(0, pos);
			meth = str.substring(pos + 1, str.length()).trim();
		}

		if (meth.contains("()"))
		{
			meth = meth.replace("()", "");
		}
		if (null == this.HisEn)
		{
			this.HisEn = ClassFactory.GetEn(clas);
			Attrs attrs = this.HisEn.getEnMap().getAttrs();
		}
		Method mp = null;
		Method[] mps = this.HisEn.getClass().getMethods();
		int length = mps.length;
		for (int i = 0; i < length; i++)
		{
//			System.out.println(mps[i].getName());
			if (mps[i].getName().equals(meth))
			{

				mp = mps[i];
				break;
			}
		}
		if (null == mp)
			throw new Exception("@对象实例中没有找到方法[" + meth + "]！");
		try
		{
			if (mp.getParameterTypes().length > 0)
			{
				return mp.invoke(this.HisEn, paras);
			} else
			{
				return mp.invoke(this.HisEn);
			}
			// 调用由此 MethodInfo 实例反射的方法或构造函数。
		} catch (Exception e)
		{
			String cause = e.getCause().getMessage();
			String msg = e.getMessage();
			if (!DataType.IsNullOrEmpty(msg))
			{
				cause = msg + "\n" + cause;
			}
			String strs = "";
			if (paras == null)
			{
				throw new Exception(cause);
			} else
			{
				for (Object obj : paras)
				{
					strs += "para= " + obj.toString() + "\n<br>";
				}
				throw new Exception(cause + "  more info:" + strs);
			}
		}


	}





}
