package bp.en;

import bp.da.*;
import bp.*;
import bp.difference.SystemConfig;
import bp.port.Emp;

import java.util.*;

/** 
 通用编号名称实体
*/
public class TSEntityNoName extends EntityNoName
{

		///#region 构造函数
	/** 
	 主键
	*/
	@Override
	public String getPK()
	{
		return "No";
	}
	@Override
	public String getPKField()
	{
		return "No";
	}
	/** 
	 转化为类.
	 
	 @return 
	*/
	@Override
	public String toString()
	{
		return this._TSclassID;
	}
	/** 
	 主键ID
	*/
	@Override
	public String getClassID()
	{
		return this._TSclassID;
	}
	private String _TSclassID = "";
	public TSEntityNoName()
	{
		//构造.
		bp.port.StationType en = new bp.port.StationType();
		this.set_enMap(en.get_enMap());
	}

	/** 
	 通用编号名称实体
	 
	 @param _TSclassID 类ID
	*/
	public TSEntityNoName(String _TSclassID)
	{
		this._TSclassID = _TSclassID;
		this.set_enMap(bp.ents.Glo.GenerMap(_TSclassID));
		// this._enMap = map as Map;
		// this._enMap = BP.EnTS.Glo.GenerMap(_TSclassID);
	}
	/** 
	 通用编号名称实体
	 
	 @param classID 表单ID
	 @param pk
	*/
	public TSEntityNoName(String classID, String pk) throws Exception {
		this._TSclassID = classID;
		this.set_enMap(bp.ents.Glo.GenerMap(_TSclassID));
		this.setNo(pk);
		this.Retrieve();
	}

		///#endregion


		///#region 构造映射.
	/** 
	 重写基类方法
	*/
	@Override
	public Map getEnMap()
	{
		if (this.get_enMap() != null)
		{
			return this.get_enMap();
		}

		if (this._TSclassID == null)
		{
			throw new RuntimeException("没有给 _TSclassID 值，您不能获取它的Map。");
		}

		this.set_enMap(bp.ents.Glo.GenerMap(this._TSclassID));
		return this.get_enMap();
	}
	/** 
	 TSEntityNoNames
	*/
	@Override
	public Entities GetNewEntities()
	{
		if (this._TSclassID == null)
		{
			throw new RuntimeException("没有给 _TSclassID 值，您不能获取它的 Entities。");
		}
		return new TSEntitiesNoName(this._TSclassID);
	}
	@Override
	protected void afterInsert() throws Exception {
		String tsid=this._TSclassID;
		if(tsid.equals("TS.Port.Emp")){
			//密码修改
			String no=this.getNo();
			Emp em= new Emp(no);
			String pass = SystemConfig.getUserDefaultPass();
			String type = SystemConfig.getIsPasswordEncryptionType();
			//BCrypt加密类型
			if (type != null && type.equals("2")) {
				pass = bp.tools.BCryptUtils.hashpw(pass);
			}
			//MD5加密类型
			if (type != null && type.equals("1")) {
				pass = bp.tools.MD5Utill.MD5Encode(pass, "UTF8");
			}

			em.setPass(pass); //设置密码.

		}

		super.afterInsert();
	}

		///#endregion
}
