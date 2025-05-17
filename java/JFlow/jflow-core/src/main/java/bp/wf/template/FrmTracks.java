package bp.wf.template;

import bp.en.*;
import java.util.*;

/** 
 轨迹图标组件s
*/
public class FrmTracks extends Entities
{

		///#region 构造
	/** 
	 轨迹图标组件s
	*/
	public FrmTracks()
	{
	}
	/** 
	 轨迹图标组件s
	 
	 @param fk_mapdata s
	*/
	public FrmTracks(String fk_mapdata) throws Exception {

			this.RetrieveFromCache("No", (Object)fk_mapdata);

	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new FrmTrack();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List
	/** 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<FrmTrack> ToJavaList()
	{
		return (java.util.List<FrmTrack>)(Object)this;
	}

	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<FrmTrack> Tolist()
	{
		ArrayList<FrmTrack> list = new ArrayList<FrmTrack>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((FrmTrack)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
