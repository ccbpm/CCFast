package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 文件管理者 
*/
public class SysFileManagers extends EntitiesOID
{
	/** 
	 文件管理者
	*/
	public SysFileManagers()
	{
	}
	/** 
	 文件管理者
	 
	 @param EnName
	 @param refval
	*/
	public SysFileManagers(String EnName, String refval) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(SysFileManagerAttr.EnName, EnName);
		qo.addAnd();
		qo.AddWhere(SysFileManagerAttr.RefVal, refval);
		qo.DoQuery();
	}
	/** 
	 文件管理者
	 
	 @param EnName
	*/
	public SysFileManagers(String EnName) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(SysFileManagerAttr.EnName, EnName);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SysFileManager();
	}
	public final SysFileManager GetSysFileByAttrFileNo(String key)
	{
		for (SysFileManager en : this.ToJavaList())
		{
			if (Objects.equals(en.getAttrFileNo(), key))
			{
				return en;
			}
		}
		return null;
	}
	public final List<SysFileManager> ToJavaList()
	{
		return (List<SysFileManager>)(Object)this;
	}
}
