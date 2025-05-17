package bp.en;

import bp.da.*;


/** 
 EntityMyPKMyFile
*/
public abstract class EntityMyPKMyFile extends EntityMyPK
{

		///属性
	@Override
	public String getPK()
	{
		return "MyPK";
	}
	public final String getMyFilePath()  {
		return this.GetValStringByKey("MyFilePath");
	}
	public final void setMyFilePath(String value){
		this.SetValByKey("MyFilePath", value);
	}
	public final String getMyFileExt()  {
		return this.GetValStringByKey("MyFileExt");
	}
	public final void setMyFileExt(String value){
		this.SetValByKey("MyFileExt", value.replace(".",""));
	}
	public final String getMyFileName()  {
		return this.GetValStringByKey("MyFileName");
	}
	public final void setMyFileName(String value){
		this.SetValByKey("MyFileName", value);
	}
	public final long getMyFileSize()  {
		return this.GetValInt64ByKey("MyFileSize");
	}
	public final void setMyFileSize(long value){
		this.SetValByKey("MyFileSize", value);
	}
	public final int getMyFileH()  {
		return this.GetValIntByKey("MyFileH");
	}
	public final void setMyFileH(int value){
		this.SetValByKey("MyFileH", value);
	}
	public final int getMyFileW()  {
		return this.GetValIntByKey("MyFileW");
	}
	public final void setMyFileW(int value){
		this.SetValByKey("MyFileW", value);
	}
	public final boolean getIsImg() {
		return DataType.IsImgExt(this.getMyFileExt());
	}

		///


		///构造
	public EntityMyPKMyFile()
	{
	}
	/** 
	 class Name 
	 
	 param _MyPK _MyPK
	 * @throws Exception 
	*/
	protected EntityMyPKMyFile(String _MyPK) throws Exception
	{
		this.setMyPK(_MyPK);
		this.Retrieve();
	}

		///
}