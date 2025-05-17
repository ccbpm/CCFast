package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 文件管理者 
*/
public class SysDocFiles extends Entities
{
	public SysDocFiles()
	{
	}
	public SysDocFiles(String _tableName, String _key) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(SysDocFileAttr.EnName, _tableName);
		qo.addAnd();
		qo.AddWhere(SysDocFileAttr.RefKey, _key);
		qo.DoQuery();
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new SysDocFile();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<SysDocFile> ToJavaList()
	{
		return (java.util.List<SysDocFile>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<SysDocFile> Tolist()
	{
		ArrayList<SysDocFile> list = new ArrayList<SysDocFile>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((SysDocFile)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
