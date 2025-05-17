package bp.en;

import bp.da.DataType;
import bp.da.Log;
import bp.difference.SystemConfig;
import bp.sys.GEDtl;
import bp.sys.GEEntity;
import bp.sys.base.EventBase;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * ClassFactory 的摘要说明。
 */
public class ClassFactory {
	
    
	public static Hashtable Htable_Evbase;


	/**
	 * 得到一个事件实体
	 * 
	 * param className
	 *            类名称
	 * @return bp.sys.EventBase
	 */
	public static bp.sys.base.EventBase GetEventBase(String className) {
		//className = bp.sys.base.Glo.DealClassEntityName(className);
		if (Htable_Evbase == null || Htable_Evbase.isEmpty()) {
			Htable_Evbase = new Hashtable();
			String cl = "bp.sys.base.EventBase";
			ArrayList al = ClassFactory.GetObjects(cl);
			Htable_Evbase.clear();
			for (Object en : al) {
				try {
					Htable_Evbase.put(((EventBase) en).getClass().getName(), en);
				} catch (java.lang.Exception e) {
				}
			}
		}
		bp.sys.base.EventBase ens = (EventBase) ((Htable_Evbase.get(className) instanceof EventBase)
				? Htable_Evbase.get(className) : null);
		return ens;
	}
 
	private static final ConcurrentHashMap<String,Object> objects = new ConcurrentHashMap<>();
	/// <summary>
	/// 尽量不用此方法来获取事例
	/// </summary>
	/// <param name="className"></param>
	/// <returns></returns>
//	public static Object GetObject_OK(String className) throws Exception {
//		if (DataType.IsNullOrEmpty(className) == true)
//			return "err@要转化类名称为空...";
//		className = bp.sys.base.Glo.DealClassEntityName(className);
//		Class clazz = Class.forName(className);
//		return clazz.newInstance();
//	}

	/**
	 * 根据一个抽象的基类，取出此系统中从他上面继承的子类集合。 非抽象的类。
	 * 
	 * param baseEnsName
	 *            抽象的类名称
	 * @return ArrayList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static synchronized ArrayList GetObjects(String baseEnsName) {
		baseEnsName = bp.sys.base.Glo.DealClassEntityName(baseEnsName);
		ArrayList list = (ArrayList) objects.get(baseEnsName);

		if (list != null && list.size()!=0) {
			return list;
		}

		try {
			list = new ArrayList();
			Class parent = Class.forName(baseEnsName);
			Set<Class<?>> set = bp.tools.ClassScaner.scan("bp",parent);
			for (Class<?> clazz : set) {
				try {
					list.add(clazz.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			list.sort(Comparator.comparing(o -> o.getClass().getName()));
			Log.DebugWriteInfo("扫描 " + baseEnsName + " 父类，共 " + set.size() + " 子类：" + set);
			objects.putIfAbsent(baseEnsName, list);
			return list;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		return list;
	}

	private static HashMap<String, Object> Htable_En;

	/**
	 * 得到一个实体
	 * last-modified  22/7/2
	 * @author wanglu
	 * 修改ClassFactory中 集合类为线程安全集合类
	 * param className
	 *            类名称
	 * @return En
	 */
	public static Entity GetEn(String className) throws Exception {
		// 判断标记初始化实体.
		if (!className.contains(".")) {
			if (className.contains("Dtl")) {
				return new GEDtl(className); // 明细表.
			} else {
				return new GEEntity(className); // 表单实体.
			}
		}
		if (className.startsWith("TS."))
		{
			Map map = bp.ents.Glo.GenerMap(className);
			if (map.getAttrs().contains("No"))
				return new TSEntityNoName(className);
			if (map.getAttrs().contains("OID"))
				return new TSEntityOID(className);
			if (map.getAttrs().contains("MyPK"))
				return new TSEntityMyPK(className);
			if (map.getAttrs().contains("WorkID"))
				return new TSEntityWorkID(className);
			if (map.getAttrs().contains("NodeID"))
				return new TSEntityNodeID(className);
			throw new Exception("err@没有判断的类型.");
		}
		className = bp.sys.base.Glo.DealClassEntityName(className);

		if (Htable_En == null || Htable_En.size()==0) {
			Htable_En = new HashMap<>();
			String cl = "bp.en.Entity";
			ArrayList al = ClassFactory.GetObjects(cl);
			for (Object en : al) {
				if (null == en || DataType.IsNullOrEmpty(en.getClass().getName()))
					continue;
				if (!Htable_En.containsKey(en.getClass().getName())) {
					Htable_En.put(en.getClass().getName(), en);
				}
			}
		}
		Object tmp = Htable_En.get(className);
		
		if (tmp==null)
			return null;
		/*
		  22/7/2
		  最后修改： wanglu
		  这里应该返回新对象实例而不是原始对象，
		  如果返回原始对象，多线程下会操作同一个内存地址，抢占资源，导致异常。
		 */
		return  (Entity)tmp.getClass().newInstance();
	}

	private static ConcurrentHashMap<String, Object> Htable_Method = new ConcurrentHashMap<>();

	/*
	 * 得到一个实体
	 * 
	 * param className 类名称
	 * 
	 * @return En
	 */
	public static Method GetMethod(String className) {
		className = bp.sys.base.Glo.DealClassEntityName(className);
		if (Htable_Method == null) {
//			Htable_Method = new Hashtable();
			String cl = "bp.en.Method";
			ArrayList<Method> al = ClassFactory.GetObjects(cl);
			for (Method en : al) {
				Htable_Method.putIfAbsent(en.getClass().getName(), en);
			}
		}
		Object tmp = Htable_Method.get(className);
		return ((bp.en.Method) ((tmp instanceof bp.en.Method) ? tmp : null));
	}

	// 获取 ens
	public static ConcurrentHashMap<String, Object> Htable_Ens = new ConcurrentHashMap<>();

	public static Entities GetEns(String className)  {

		if (!className.contains(".")) {
			return new bp.sys.GEEntitys(className);
		}
		if (className.startsWith("TS."))
		{
			Map map = bp.ents.Glo.GenerMap(className);
			if (map.getAttrs().contains("No"))
				return new TSEntitiesNoName(className);
			if (map.getAttrs().contains("MyPK"))
				return new TSEntitiesMyPK(className);
			if (map.getAttrs().contains("WorkID"))
				return new TSEntitiesWorkID(className);
			if (map.getAttrs().contains("NodeID"))
				return new TSEntitiesNodeID(className);
			if (map.getAttrs().contains("OID"))
				return new TSEntitiesOID(className);
			return null;
		}
		className = bp.sys.base.Glo.DealClassEntityName(className);
		if (Htable_Ens == null || Htable_Ens.isEmpty()) {
//			Htable_Ens = new Hashtable<String, Object>();
			String cl = "bp.en.Entities";
			ArrayList al = ClassFactory.GetObjects(cl);

			Htable_Ens.clear();
			for (Object en : al) {
				try {
					Htable_Ens.putIfAbsent(en.getClass().getName(), en);
				} catch (java.lang.Exception e) {
				}
			}
		}
		Entities ens = (Entities) ((Htable_Ens.get(className) instanceof Entities) ? Htable_Ens.get(className) : null);

		return ens;
	}

	/**
	 * 得到一个实体
	 * 
	 * param className
	 *            类名称
	 * @return En
	 */
	public static Entities GetEnsNew(String className) throws Exception {

		if (className.contains(".") == false) {
			bp.sys.GEEntitys myens = new bp.sys.GEEntitys(className);
			return myens;
		}
		className = bp.sys.base.Glo.DealClassEntityName(className);
		// 实例化这个类
		try {

			Class catClass = Class.forName(className);
			Entities obj = (Entities) catClass.newInstance();
			return obj;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

		// if (Htable_Ens == null || Htable_Ens.isEmpty()) {
//		Htable_Ens = new Hashtable<String, Object>();
//		String cl = "bp.en.Entities";
//		ArrayList al = ClassFactory.GetObjects(cl);
//
//		Htable_Ens.clear();
//		for (Object en : al) {
//			try {
//				Htable_Ens.putIfAbsent(en.getClass().getName(), en);
//			} catch (java.lang.Exception e) {
//			}
//		}
//		// }
//		Entities ens = (Entities) ((Htable_Ens.get(className) instanceof Entities) ? Htable_Ens.get(className) : null);
//
//		// /#warning 会清除 Cache 中的数据。
//		return ens;
	}

	// 获取 ens
	public static Hashtable<String, Object> Htable_XmlEns;

	/**
	 * 得到一个实体
	 * 
	 * param className
	 *            类名称
	 * @return En
	 */
	public static bp.sys.xml.XmlEns GetXmlEns(String className) {
		if (Htable_XmlEns == null) {
			Htable_XmlEns = new Hashtable<String, Object>();
			String cl = "bp.xml.XmlEns";
			ArrayList al = ClassFactory.GetObjects(cl);
			for (Object en : al) {
				Htable_XmlEns.put(en.getClass().getName(), en);
			}
		}
		className = bp.sys.base.Glo.DealClassEntityName(className);
		Object tmp = Htable_XmlEns.get(className);
		return ((bp.sys.xml.XmlEns) ((tmp instanceof bp.sys.xml.XmlEns) ? tmp : null));
	}

	// 获取 en
	public static Hashtable<String, Object> Htable_XmlEn;

	/**
	 * 得到一个实体
	 * 
	 * param className
	 *            类名称
	 * @return En
	 */
	public static bp.sys.xml.XmlEn GetXmlEn(String className) {
		if (Htable_XmlEn == null) {
			Htable_XmlEn = new Hashtable<String, Object>();
			String cl = "bp.xml.XmlEn";
			ArrayList al = ClassFactory.GetObjects(cl);
			for (Object en : al) {
				Htable_XmlEn.put(en.getClass().getName(), en);
			}
		}
		className = bp.sys.base.Glo.DealClassEntityName(className);
		Object tmp = Htable_XmlEn.get(className);
		return ((bp.sys.xml.XmlEn) ((tmp instanceof bp.sys.xml.XmlEn) ? tmp : null));
	}

	public static String getClassPath() {
		String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
		path = path.substring(path.indexOf("/") + 1);
		int i = 0;
		while (path.indexOf("/") != -1 && path.length() > 0) {
			path = path.substring(0, path.lastIndexOf("/"));
			if (1 == i) {

				String service = SystemConfig.getAppSettings().get("Service").toString().toLowerCase();
				if (service.equals("tomcat")) {
					path += System.getProperty("file.separator") + "lib" + System.getProperty("file.separator");
					return path;
				} else if (service.equals("jetty")) {
					path += System.getProperty("file.separator");// +"lib"+
																	// System.getProperty("file.separator");
					return path;
				}
			}
			i++;
		}
		return path.substring(path.indexOf("/") + 1, path.lastIndexOf("/") - 7);
	}

	// 获取 HandlerBase
	private static Hashtable Htable_HandlerPage;

	/// <summary>
	/// 得到一个实体
	/// </summary>
	/// <param name="className">类名称</param>
	/// <returns>En</returns>
	public static Object GetHandlerPage(String className) {
		className = bp.sys.base.Glo.DealClassEntityName(className);
		if (Htable_HandlerPage == null) {
			Htable_HandlerPage = new Hashtable();
			String cl = "bp.difference.handler.DirectoryPageBase";
			ArrayList al = ClassFactory.GetObjects(cl);
			for (Object en : al) {
				String key = "";
				if (null == en || DataType.IsNullOrEmpty(key = en.getClass().getName()))
					continue;

				if (Htable_HandlerPage.containsKey(key) == false)
					Htable_HandlerPage.put(key, en);

			}
		}
		return Htable_HandlerPage.get(className);
	}
}