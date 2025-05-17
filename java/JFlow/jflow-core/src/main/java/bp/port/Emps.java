package bp.port;

import bp.da.*;
import bp.difference.*;
import bp.en.*; import bp.en.Map;
import bp.sys.*;
import bp.web.*;
import bp.*;
import java.util.*;
import java.io.*;

/** 
 操作员
*/
// </summary>
public class Emps extends EntitiesNoName
{

		///#region 构造方法
	/** 
	 得到它的 Entity 
	*/
	@Override
	public Entity getNewEntity()
	{
		return new Emp();
	}
	/** 
	 操作员s
	*/
	public Emps()
	{
	}
	/** 
	 操作员s
	*/
	public Emps(String deptNo) throws Exception {

		this.Retrieve(EmpAttr.FK_Dept, deptNo);

	}

		///#endregion 构造方法

	public final String reseet()
	{
		return "ceshi";
	}


		///#region 重写查询,add by zhoupeng 2015.09.30 为了适应能够从 webservice 数据源查询数据.
	/** 
	 重写查询全部适应从WS取数据需要
	 
	 @return 
	*/
	@Override
	public int RetrieveAll() throws Exception {
		//if (bp.web.WebUser.getNo() != "admin")
		//    throw new Exception("@您没有查询的权限.");
		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAll();
		}

		return this.Retrieve("OrgNo", WebUser.getOrgNo());
	}
	/** 
	 重写重数据源查询全部适应从WS取数据需要
	 
	 @return 
	*/
	@Override
	public int RetrieveAllFromDBSource() throws Exception {

		if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
		{
			return super.RetrieveAllFromDBSource();
		}

		return this.Retrieve("OrgNo", WebUser.getOrgNo());
	}

		///#endregion 重写查询.


		///#region 为了适应自动翻译成java的需要,把实体转换成List.
	/** 
	 转化成 java list,C#不能调用.
	 
	 @return List
	*/
	public final java.util.List<Emp> ToJavaList()
	{
		return (java.util.List<Emp>)(Object)this;
	}
	/** 
	 转化成list
	 
	 @return List
	*/
	public final ArrayList<Emp> Tolist()
	{
		ArrayList<Emp> list = new ArrayList<Emp>();
		for (int i = 0; i < this.size(); i++)
		{
			list.add((Emp)this.get(i));
		}
		return list;
	}

		///#endregion 为了适应自动翻译成java的需要,把实体转换成List.

}
