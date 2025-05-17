package bp.ccfast.rpt;
import bp.en.*;
import java.util.*;

/** 
 三维报表s
*/
public class Rpt3Ds extends EntitiesNoName
{

		///#region 构造
	/** 
	 三维报表s
	*/
	public Rpt3Ds()
	{
	}
	/** 
	 得到它的 Entity
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Rpt3D();
	}

		///#endregion


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Rpt3D> ToJavaList()
	{
		return (java.util.List<Rpt3D>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Rpt3D> Tolist()
	{
		ArrayList<Rpt3D> list = new ArrayList<Rpt3D>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Rpt3D)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.
}
