package WebService;

import javax.jws.WebMethod;
import javax.jws.WebService;
import bp.da.DataSet;
@WebService
public interface CCFormAPII {

	/** 
	 获得Word文件 - 未开发完成.
	 @param userNo 用户编号
	 @param sid SID
	 @param frmID 表单ID
	 @param oid 表单主键
	 @return 
	 * @throws Exception 
	*/
	@WebMethod
	public  void WordFileGener(String userNo, String sid, long workID, byte[] bytes) throws Exception;

	void GenerBillTemplate(String userNo, String sid, long workID, String billTemplateNo, bp.da.DataSet ds,
			byte[] bytes) throws Exception;
	

}
