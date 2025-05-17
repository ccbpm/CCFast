package bp.wf.template.frm;

import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.*;
import bp.wf.*;
import bp.wf.template.*;
import java.util.*;

/** 
 Word表单属性s
*/
public class MapFrmReferencePanels extends EntitiesNoName
{

		///#region 构造
	/** 
	 Word表单属性s
	*/
	public MapFrmReferencePanels()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new MapFrmReferencePanel();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<MapFrmReferencePanel> ToJavaList()
	{
		return (java.util.List<MapFrmReferencePanel>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<MapFrmReferencePanel> Tolist()
	{
		ArrayList<MapFrmReferencePanel> list = new ArrayList<MapFrmReferencePanel>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((MapFrmReferencePanel)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
