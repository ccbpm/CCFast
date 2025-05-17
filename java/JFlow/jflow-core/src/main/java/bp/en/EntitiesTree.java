package bp.en;
import java.util.List;


/** 
 树实体s
*/
public abstract class EntitiesTree extends Entities
{

	private static final long serialVersionUID = 1L;
	///转化为树结构的tree.
	private StringBuilder appendMenus = null;
	private StringBuilder appendMenuSb = null;
	/** 
	 转化为json树
	 
	 @return 
	*/

	public final String ToJsonOfTree()throws Exception
	{
		return ToJsonOfTree("0");
	}


	public final String ToJsonOfTree(String rootNo)throws Exception
	{
		appendMenus = new StringBuilder();
		appendMenuSb = new StringBuilder();
        EntityTree root = (EntityTree) this.GetEntityByKey(EntityTreeAttr.ParentNo, rootNo) ;
		if (root == null)
		{
		   return "err@没有找到rootNo=" + rootNo + "的entity.";
		}

		appendMenus.append("[{");
		appendMenus.append("'id':'" + root.getNo() + "',");
		appendMenus.append("'pid':'" + root.getParentNo() + "',");
		appendMenus.append("'text':'" + root.getName() + "'");
	   // appendMenus.Append(IsPermissionsNodes(ens, dms, root.getNo()));

		// 增加它的子级.
		appendMenus.append(",'children':");
		AddChildren(root, this);
		appendMenus.append(appendMenuSb);
		appendMenus.append("}]");

		return ReplaceIllgalChart(appendMenus.toString());
	}
	public final void AddChildren(EntityTree parentEn, EntitiesTree ens) throws Exception
	{
		appendMenus.append(appendMenuSb);
		appendMenuSb.setLength(0);

		appendMenuSb.append("[");
		for (EntityTree item : ens.ToJavaListEnTree())
		{
			if (!item.getParentNo().equals(parentEn.getNo()))
			{
				continue;
			}

			appendMenuSb.append("{'id':'" + item.getNo() + "','pid':'" + item.getParentNo() + "','text':'" + item.getName() + "'");
			//appendMenuSb.Append(IsPermissionsNodes(ens, dms, item.getNo()));
			EntityTree treeNode = item instanceof EntityTree ? (EntityTree)item : null;
			// 增加它的子级.
			appendMenuSb.append(",'children':");
			AddChildren(item, ens);
			appendMenuSb.append("},");
		}
		if (appendMenuSb.length() > 1)
		{
			appendMenuSb = appendMenuSb.deleteCharAt(appendMenuSb.length() - 1);
		}
		appendMenuSb.append("]");
		appendMenus.append(appendMenuSb);
		appendMenuSb.setLength(0);
	}
	public final String ReplaceIllgalChart(String s)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0, j = s.length(); i < j; i++)
		{

			char c = s.charAt(i);
			switch (c)
			{
				case '\"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '/':
					sb.append("\\/");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				default:
					sb.append(c);
					break;
			}
		}
		return sb.toString();
	}

		/// 转化为树结构的tree.


		///构造.
	/** 
	 查询他的子节点
	 
	 param en
	 @return 
	 * @throws Exception 
	*/
	public final int RetrieveHisChinren(EntityTree en) throws Exception
	{
		int i = this.Retrieve(EntityTreeAttr.ParentNo, en.getNo());
		this.AddEntity(en);
		return i + 1;
	}

	/** 
	 获取它的子节点
	 
	 param en
	 @return 
	*/
	public final EntitiesTree GenerHisChinren(EntityTree en)throws Exception
	{
		Entities tempVar = this.CreateInstance();
		EntitiesTree ens = tempVar instanceof EntitiesTree ? (EntitiesTree)tempVar : null;
		for (EntityTree item : ens.ToJavaListEnTree())
		{
			if (en.getParentNo().equals(en.getNo()))
			{
				ens.AddEntity(item);
			}
		}
		return ens;
	}

	/** 
	 构造
	*/
	public EntitiesTree()
	{
	}

	public List<EntityTree> ToJavaListEnTree()
	{
		return (List<EntityTree>)(Object)(Object)this;
	}	
		/// 构造.
}