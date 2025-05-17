package bp.sys;

import bp.da.*;
import bp.en.*;
import java.util.*;

/** 
 表单元素扩展DBs
*/
public class FrmEleDBs extends EntitiesMyPK
{

		///#region 构造
	/** 
	 表单元素扩展DBs
	*/
	public FrmEleDBs()
	{
	}
	/** 
	 表单元素扩展DBs
	 
	 @param fk_mapdata
	 @param pkval
	*/
	public FrmEleDBs(String fk_mapdata, String pkval) throws Exception {
		this.Retrieve(FrmEleDBAttr.FK_MapData, fk_mapdata, FrmEleDBAttr.EleID, pkval);
	}
	/** 
	 表单元素扩展DBs
	 
	 @param fk_mapdata s
	*/
	public FrmEleDBs(String fk_mapdata) throws Exception {
		if (bp.difference.SystemConfig.isDebug())
		{
			this.Retrieve(MapAttrAttr.FK_MapData, fk_mapdata);
		}
		else
		{
			this.RetrieveFromCache(MapAttrAttr.FK_MapData, (Object)fk_mapdata);
		}
	}

	public final void SaveFrmEleDBs(String fk_mapdata, String eleID, String refPKVal, String paras) throws Exception {
		if (DataType.IsNullOrEmpty(paras) == true)
		{
			return;
		}
		String[] strs = paras.split("[;]", -1);
		FrmEleDB frmEleDB = null;
		for (String str : strs)
		{
			String[] vals = str.split("[,]", -1);
			frmEleDB = new FrmEleDB();
			frmEleDB.setMyPK(eleID + "_" + refPKVal + "_" + vals[0]);
			frmEleDB.setFrmID(fk_mapdata);
			frmEleDB.setEleID(eleID);
			frmEleDB.setRefPKVal(refPKVal);
			frmEleDB.setTag1(vals[0]);
			if (vals.length >= 2 && DataType.IsNullOrEmpty(vals[1]) == false)
			{
				frmEleDB.setTag2(vals[1]);
			}
			else
			{
				frmEleDB.setTag2("");
			}
			if (vals.length == 3)
			{
			frmEleDB.setTag3(vals[2]);
			}
			frmEleDB.Save();
		}
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmEleDB();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmEleDB> ToJavaList()
	{
		return (java.util.List<FrmEleDB>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmEleDB> Tolist()
	{
		ArrayList<FrmEleDB> list = new ArrayList<FrmEleDB>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmEleDB)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
