package bp.en;
import java.util.ArrayList;
import java.util.List;



/** 
 具有编号名称的基类实体s
*/
public abstract class EntitiesNoName extends EntitiesNo
{

	private static final long serialVersionUID = 1L;
	/** 
	 将对象添加到集合尾处，如果对象已经存在，则不添加.
	 
	 param entity 要添加的对象
	 @return 返回添加到的地方
	*/
	public int AddEntity(Entity entity)  {
		for (Entity en : this)
		{
			if (en.GetValStrByKey("No").equals(entity.GetValStrByKey("No")))
			{
				return 0;
			}
		}
		this.add(entity);
		
		return this.size();
	}
	/** 
	 将对象添加到集合尾处，如果对象已经存在，则不添加
	 
	 param entity 要添加的对象
	 @return 返回添加到的地方
	 * @throws Exception 
	*/
	public void Insert(int index, EntityNoName entity)
	{
		for (EntityNoName en : convertEntityNoName(this))
		{
			if (en.getNo().equals(entity.getNo()))
			{
				return;
			}
		}

		this.add(index, entity);
	}

	/** 
	 构造
	*/
	public EntitiesNoName()
	{
	}
	/**  
	 按照名称模糊查询
	 
	 param likeName likeName
	 @return 返回查询的Num
	 * @throws Exception 
	*/
	public final int RetrieveByLikeName(String likeName) throws Exception
	{
		QueryObject qo = new QueryObject(this);
		qo.AddWhere("Name", "like", " %" + likeName + "% ");
		return qo.DoQuery();
	}
	@Override
	public int RetrieveAll() throws Exception
	{
		return super.RetrieveAll("No");
	}
	
	public static ArrayList<EntityNoName> convertEntityNoName(Object obj)
	{
		return (ArrayList<EntityNoName>) obj;
	}
	public List<EntityNoName> ToJavaListEnNo()throws Exception
	{
		return (List<EntityNoName>)(Object)(Object)this;
	}
}