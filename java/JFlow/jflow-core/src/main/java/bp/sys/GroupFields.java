package bp.sys;

import bp.en.*;
import java.util.*;

/** 
 分组-集合
*/
public class GroupFields extends EntitiesOID
{



		///#region 构造
	/** 
	 GroupFields
	*/
	public GroupFields()
	{
	}
	/** 
	 GroupFields
	 
	 @param enName 名称
	*/
	public GroupFields(String enName) throws Exception {
		int i = this.Retrieve(GroupFieldAttr.FrmID, enName, GroupFieldAttr.Idx);
		if (i == 0)
		{
			GroupField gf = new GroupField();
			gf.setFrmID(enName);
			MapData md = new MapData();
			md.setNo(enName);
			if (md.RetrieveFromDBSources() == 0)
			{
				gf.setLab("基础信息");
			}
			else
			{
				gf.setLab(md.getName());
			}
			gf.setIdx(0);
			gf.Insert();
			this.AddEntity(gf);
		}
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new GroupField();
	}
	/** 
	 查询
	 
	 @param enName
	 @return 
	*/
	public final int RetrieveFieldGroup(String enName) throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(GroupFieldAttr.FrmID, enName);
		qo.addAnd();
		qo.AddWhereIsNull(GroupFieldAttr.CtrlID);
		//qo.AddWhereLen(GroupFieldAttr.CtrlID, " = ", 0, bp.difference.SystemConfig.getAppCenterDBType());
		int num = qo.DoQuery();

		if (num == 0)
		{
			GroupField gf = new GroupField();
			gf.setFrmID(enName);
			MapData md = new MapData();
			md.setNo(enName);
			if (md.RetrieveFromDBSources() == 0)
			{
				gf.setLab("基础信息");
			}
			else
			{
				gf.setLab(md.getName());
			}
			gf.setIdx(0);
			gf.Insert();
			this.AddEntity(gf);
			return 1;
		}
		return num;
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<GroupField> ToJavaList()
	{
		return (java.util.List<GroupField>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<GroupField> Tolist()
	{
		ArrayList<GroupField> list = new ArrayList<GroupField>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((GroupField)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
