package bp.ccoa;

import bp.web.*;
import bp.en.*;
import java.util.*;

/** 
 记事本 s
*/
public class Notepads extends EntitiesMyPK
{
	/** 
	 查询前30个数据.
	 
	 @return 
	*/
	public final String RetrieveTop30() throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NotepadAttr.Rec, WebUser.getNo());
		//qo.addAnd();
		//qo.AddWhere(NotepadAttr.IsStar, 0);
		qo.Top = 30;
		qo.addOrderBy("RDT");
		qo.DoQuery();

		return this.ToJson("dt");
	}
	public final String RetrieveTop30Stars() throws Exception {
		QueryObject qo = new QueryObject(this);
		qo.AddWhere(NotepadAttr.Rec, WebUser.getNo());
		qo.addAnd();
		qo.AddWhere(NotepadAttr.IsStar, 1);
		qo.Top = 30;
		qo.addOrderBy("RDT");
		qo.DoQuery();

		return this.ToJson("dt");
	}
	/** 
	 记事本
	*/
	public Notepads()
	{
	}
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Notepad();
	}


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Notepad> ToJavaList()
	{
		return (java.util.List<Notepad>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Notepad> Tolist()
	{
		ArrayList<Notepad> list = new ArrayList<Notepad>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Notepad)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
