package bp.da;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import bp.difference.SystemConfig;


/**
 * 参数集合
 */
public class Paras extends ArrayList<Para>
{
	private static final long serialVersionUID = 1L;
	
	public Paras()  {
	}
	
	public Paras(Object o)
	{
		this.Add("p", o);
	}
	
	public final String getDBStr()throws Exception
	{
		return SystemConfig.getAppCenterDBVarStr();
	}
	
	public Paras(String p, Object v)
	{
		this.Add(p, v);
	}
	
	public Paras(String p1, Object o1, String p2, Object o2)
	{
		this.Add(p1, o1);
		this.Add(p2, o2);
	}
	
	public final void Add(Para para)
	{
		for (Para p : this)
		{
			if (p.ParaName.equals(para.ParaName))
			{
				p.val = para.val;
				return;
			}
		}
		this.add(para);
	}
	
	public final void Add(Object obj)
	{
		this.Add("p", obj);
	}

	public  void Add(String _name, Object obj) {
		
		if (_name.equals("abc")) {
			return;
		}
		if (obj == null) {
			throw new RuntimeException("@参数:" + _name + " 值无效.");
		}
		for (Para p : this) {
			if (p.ParaName.equals(_name)) {
				p.val = obj;
				return;
			}
		}
		Add(_name, obj, obj.getClass());
	}

	public final void Add(String _name, Object obj, Class<?> clazzType)
	{
		if (_name.equals("abc"))
		{
			return;
		}

		for (Para p : this)
		{
			if (p.ParaName.equals(_name))
			{
				p.val = obj;
				return;
			}
		}
		
		if (clazzType == String.class)
		{
			this.Add(_name, (String) obj);
			return;
		}

		if (clazzType == Integer.class || clazzType == Byte.class
				|| clazzType == Short.class)
		{
			this.Add(_name, (Integer) obj);
			return;
		}

		if (clazzType == Long.class)
		{
			this.Add(_name, (Long) obj);
			return;
		}

		if (clazzType == Float.class)
		{
			this.Add(_name, (Float) obj);
			return;
		}

		if (clazzType == java.math.BigDecimal.class)
		{
			this.Add(_name, (java.math.BigDecimal) obj);
			return;
		}

		if (clazzType == Double.class)
		{
			this.Add(_name, (Double) obj);
			return;
		}

		if (clazzType == Date.class)
		{
			this.Add(_name, (java.util.Date) obj);
			return;
		}

		//null值暂时定义为数组类型
		this.AddDBNull(_name);
	
	}

	public void AddDBNull(String _name)
	{
		Para en = new Para();
		en.DAType = ArrayList.class;
		en.val =new ArrayList<String>();
		en.ParaName = _name;
		this.Add(en);
	}

	public void Add(String _name, String _val)
	{
		Add(_name, _val,false);
	}
	/**
	 * 是否是大块文本?
	 * param _name 名称
	 * param _val 值
	 * param isBigTxt 是否是大文本?
	 */
	public void Add(String _name, String _val,boolean isBigTxt)
	{
		Para en = new Para();

		en.DAType = String.class;
        en.val = _val;
        en.ParaName = _name;
		en.Size = _val.length();
        en.IsBigText = isBigTxt; //是否是大块文本.
        this.Add(en);
	}
	
	public void Add(String _name, Integer _val)
	{
		Para en = new Para();
		en.DAType = Integer.class;
		en.val = _val;
		en.ParaName = _name;
		this.Add(en);
	}
	
	public void Add(String _name, Long _val)
	{
		Para en = new Para();
		en.DAType = Long.class;
		en.val = _val;
		en.ParaName = _name;
		this.Add(en);
	}
	
	public void Add(String _name, Float _val)
	{
		Para en = new Para();
		en.DAType = Float.class;
		// en.DAType = System.Data.DbType.Int64;
		en.val = _val;
		en.ParaName = _name;
		this.Add(en);
	}
	
	/*
	 * warning public final void AddDBNull(String _name) { Para en = new Para();
	 * en.DAType = Object.class; en.val = DBNull.getValue(); en.ParaName =
	 * _name; this.Add(en); }
	 */
	
	public void Add(String _name, java.math.BigDecimal _val)
	{
		Para en = new Para();
		en.DAType = BigDecimal.class;
		en.val = _val;
		en.ParaName = _name;
		this.Add(en);
	}
	
	public void Add(String _name, Double _val)
	{
		Para en = new Para();
		en.DAType = Double.class;
		en.val = _val;
		en.ParaName = _name;
		this.Add(en);
	}
	
	private void Add(String _name, Date _val)
	{
		Para en = new Para();
		en.DAType = Date.class;
		en.val = _val;
		en.ParaName = _name;
		this.Add(en);
	}
	
	public final void remove(String paraName)
	{
		int i = 0;
		for (Para p : this)
		{
			if (p.ParaName.equals(paraName))
			{
				break;
			}
			i++;
		}
		this.remove(i);
	}

	// 特殊处理
	public final void AddFK_Emp(String userNo)
	{
		this.Add("FK_Emp", userNo);
	}
	
	public void AddFK_Emp() throws Exception
	{
		this.Add("FK_Emp", bp.web.WebUser.getNo());
	}
	
	public final void AddFK_NY(String fk_ny)
	{
		this.Add("FK_NY", fk_ny);
	}
	
	public final void AddFK_NY()throws Exception
	{
		this.Add("FK_NY", DataType.getCurrentYearMonth());
	}
	
	public final void AddFK_Dept(String val)
	{
		this.Add("FK_Dept", val);
	}
	
	public final String ToDesc()throws Exception
	{
		String msg = "";
		for (Para p : this)
		{
			msg += "@" + p.ParaName + " = " + p.val;
		}
		return msg;
	}
	
	public String SQL = null;
	
	public final String getSQLNoPara()throws Exception
	{
		Object tempVar = this.SQL;
		/*
		 * warning Object tempVar = this.SQL;
		 */
		String mysql = (String) ((tempVar instanceof String) ? tempVar : null);
		if (mysql == null){
			return null;
		}
		for (Para p : this)
		{
			if (p.DAType == String.class)
			{
				if (mysql.contains(SystemConfig.getAppCenterDBVarStr()
						+ p.ParaName + ","))
				{
					Object val = p.val;
					if (val == null)
						val = "NULL";
					mysql = mysql.replace(SystemConfig.getAppCenterDBVarStr()
							+ p.ParaName + ",", "'" +  val + "',");
				} else
				{
					if(p.val == null)
						p.val = "";
					mysql = mysql.replace(SystemConfig.getAppCenterDBVarStr()
							+ p.ParaName, "'" + p.val.toString() + "',");
				}
			} else
			{
				if (mysql.contains(SystemConfig.getAppCenterDBVarStr()
						+ p.ParaName + ","))
				{
					mysql = mysql.replace(SystemConfig.getAppCenterDBVarStr()
							+ p.ParaName + ",", p.val.toString() + ",");
				} else
				{
					mysql = mysql.replace(SystemConfig.getAppCenterDBVarStr()
							+ p.ParaName, p.val.toString());
				}
			}
		}
		return mysql;
	}
	
	private String debugInfo = null;
	
	public String getDebugInfo(){
		if (debugInfo == null){
			StringBuilder sb = new StringBuilder();
			sb.append("Num=" + this.size());
			for (Para pa : this) {
				sb.append(", " + pa.ParaName + "=" + pa.val + "(");
				if (pa.val != null){
					sb.append(pa.val.getClass().getSimpleName());
				}else{
					sb.append("null");
				}
				sb.append(")");
			}
			debugInfo = sb.toString();
		}
		return debugInfo;
	}
}
