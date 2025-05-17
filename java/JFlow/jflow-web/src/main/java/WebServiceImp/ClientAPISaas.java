package WebServiceImp;

import bp.da.DataType;
import bp.tools.HttpClientUtil;
import bp.tools.HttpConnectionManager;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 此文件用于放到自己的开发后台代码里， 可以直接调用这些静态的方法，ccbpm帮您封装好了.
public class ClientAPISaas {
	public static String DomainExt = ""; //同一个系统下的流程表单目录,可以为空.
	public static String CCBPMHost = "http://localhost:8085"; //链接的ccbpm服务器.
	public static String OrgNo = "300"; //组织(系统编号). 为集团或者-SAAS模式有效.
	public static String PrivateKey = "DiGuaDiGua,IamCCBPM"; //私钥.
	public static void main(String[] args) throws Exception {
		//1.执行登录:
		String UserNo="300";
		String postData = Port_Login( UserNo);
		if (postData.toString().startsWith("err@"))
			System.out.println("登录失败");


		//String Token = postData.get("Token").toString(); //通过token执行相关的操作.
		//Node_CreateBlankWorkID();
    }
	/// <summary>
	/// 数据返回格式:
	/// 不同的客户要求格式不同，在这里统一做转换即可.
	/// </summary>
	/// <param name="code">代码：200=成功，500=失败.</param>
	/// <param name="msg">消息</param>
	/// <param name="data">执行的数据,可以为空，也可以为json.</param>
	/// <returns>返回的是json格式的数据</returns>
	public static String Return_Info(int code, String msg, String data)
	{
		String json = "{ code:'" + code + "',msg:'" + msg + "',data:\"" + data + "\"}";
		return json;
	}

	///#region 组织结构- 登录登出.
	/**
	 登录获得token
	 @param userNo 用户编号
	 @return 与用户的信息
	 */
	public static String Port_Login(String userNo)
	{
		try
		{
			String str = HttpPostConnect("Port_Login?userNo=" + userNo + "&privateKey=" + PrivateKey + "&orgNo=" + OrgNo);
			if (str != null && str.equals("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException e)
		{
			return Return_Info(500, e.getMessage(), e.getMessage());
		}
	}
	public static String Port_LoginOut(String userNo)
	{
		String str = HttpPostConnect("Port_LoginOut?userNo=" + userNo + "&PrivateKey=" + PrivateKey);
		return Return_Info(200, "退出成功", str);
	}
	///#endregion 组织结构-登录登出

	///#region 组织结构同步.
	public static String Port_Emp_Save(String token, String userNo, String userName, String deptNo, String kv, String stats)
	{
		try
		{
			String str = HttpPostConnect("Port_Emp_Save?userNo=" + userNo + "&userName=" + userName + "&deptNo=" + deptNo + "&kv="
					+ kv + "&stats=" + stats );
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public static String Port_Emp_Delete(String token, String userNo)
	{
		try
		{
			String str = HttpPostConnect("Port_Emp_Delete?token=" + token + "&userNo=" + userNo);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public static String Port_Org_Save(String token, String name, String adminer, String adminerName, String keyVals)
	{
		try
		{
			String str = HttpPostConnect("Port_Org_Save?token=" + token + "&name=" + name + "&adminer=" + adminer + "&keyVals="
					+ keyVals);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public static String Port_Dept_Save(String token, String no, String name, String parentNo, String keyVals)
	{
		try
		{
			String str = HttpPostConnect("Port_Dept_Save?token=" + token + "&no=" + no + "&name=" + name + "&parentNo="
					+ parentNo+ "&keyVals="+ keyVals);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public static String Port_Dept_Delete(String token, String no)
	{
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_Station_Save(String token,String orgNo, String no, String name, String keyVals) {
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_Station_Delete(String token,String no) {
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_Team_Save(String token, String orgNo, String no, String name, String keyVals)   {
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_Team_Delete(String token, String no){
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_TeamType_Save(String token, String orgNo, String no, String name, String keyVals) {
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_TeamType_Delete(String token, String no)  {
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_StationType_Save(String token, String orgNo, String no, String name, String keyVals){
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Port_StationType_Delete(String token, String no) {
		try
		{
			String str = HttpPostConnect("Port_Dept_Delete?token=" + token + "&no=" + no);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	///#endregion 组织结构同步.
	public String Node_CreateBlankWorkID(String token,String flowNo){
		try
		{
			String str = HttpPostConnect("Node_CreateBlankWorkID?token=" + token + "&flowNo=" + flowNo);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Node_SetDraft(String token,String flowNo,Long workID) {
		try
		{
			String str = HttpPostConnect("Node_SetDraft?token=" + token + "&flowNo=" + flowNo + "&workID=" + workID);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}

	public String Node_Shift(String token,Long workID,String toEmpNo,String msg) {
		try
		{
			String str = HttpPostConnect("Node_Shift?token=" + token + "&workID=" + workID + "&toEmpNo=" + toEmpNo + "&msg=" + msg);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Node_AddTodolist(String token,Long workID,String empNo)  {
		try
		{
			String str = HttpPostConnect("Node_AddTodolist?token=" + token + "&workID=" + workID + "&empNo=" + empNo);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_GenerWorkFlow(String token,Long workID) {
		try
		{
			String str = HttpPostConnect("Flow_GenerWorkFlow?token=" + token + "&workID=" + workID);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Node_SaveParas(String token,Long workID,String paras) {
		try
		{
			String str = HttpPostConnect("Node_SaveParas?token=" + token + "&workID=" + workID + "&paras=" + paras);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_SetTitle(String token,Long workID,String title,String flowNo) {
		try
		{
			String str = HttpPostConnect("Flow_SetTitle?token=" + token + "&workID=" + workID+ "&title=" + title+ "&flowNo=" + flowNo);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Node_SendWork(String token,Long workID,String ht,String flowNo,Integer toNodeID,String toEmps) {
		try
		{
			String str = HttpPostConnect("Node_SendWork?token=" + token + "&ht=" + ht+ "&flowNo=" + flowNo+ "&toNodeID=" + toNodeID
					+ "&toEmps=" + toEmps+ "&workID=" + workID);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String DB_GenerWillReturnNodes(String token,Long workID,Long fid,Integer nodeID) {
		try
		{
			String str = HttpPostConnect("DB_GenerWillReturnNodes?token=" + token + "&workID=" + workID+ "&fid=" + fid+ "&nodeID=" + nodeID);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}

	public String Node_ReturnWork(String token,long workID, Integer nodeID,Integer returnToNodeID,String returnToEmp,String msg,boolean isBackToThisNode) {
		try
		{
			String str = HttpPostConnect("Node_ReturnWork?token=" + token + "&workID=" + workID+ "&nodeID=" + nodeID
					+ "&returnToNodeID=" + returnToNodeID+ "&returnToEmp=" + returnToEmp+ "&msg=" + msg+ "&isBackToThisNode=" + isBackToThisNode);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Search_Init(String token,String scop,String key,String dtFrom,String dtTo,Integer pageIdx) {
		try
		{
			String str = HttpPostConnect("Search_Init?token=" + token + "&scop=" + scop+ "&key=" + key + "&dtFrom=" + dtFrom
					+ "&dtTo=" + dtTo + "&pageIdx=" + pageIdx);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String DB_Start(String token,String domainExt) {
		try
		{
			String str = HttpPostConnect("DB_Start?token=" + token + "&domainExt=" + domainExt);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String DB_Draft(String token,String flowNo,String domainExt) {
		try
		{
			String str = HttpPostConnect("DB_Draft?token=" + token + "&flowNo=" + flowNo+ "&domainExt=" + domainExt);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String DB_Todolist(String token,String flowNo,Integer nodeID,String domainExt) {
		try
		{
			String str = HttpPostConnect("DB_Todolist?token=" + token + "&flowNo=" + flowNo+ "&nodeID=" + nodeID+ "&domainExt=" + domainExt);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String DB_Runing(String token,String domainExt) {
		try
		{
			String str = HttpPostConnect("DB_Runing?token=" + token + "&domainExt=" + domainExt);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String DB_CCList(String token,String domainExt) {
		try
		{
			String str = HttpPostConnect("DB_CCList?token=" + token + "&domainExt=" + domainExt);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String DB_CCListBySta(String token,Integer sta, String domainExt){
		try
		{
			String str = HttpPostConnect("DB_CCListBySta?token=" + token + "&sta=" + sta+ "&domainExt=" + domainExt);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Node_CC_WriteTo_CClist(String token,Long workID,Integer nodeID,String title, String doc,String emps,String depts,String stations) {
		try
		{
			String str = HttpPostConnect("Node_CC_WriteTo_CClist?token=" + token + "&workID=" + workID+ "&nodeID=" + nodeID
					+ "&title=" + title+ "&doc=" + doc + "&emps=" + emps + "&depts=" + depts+ "&stations=" + stations);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_DoPress(String token,String workIDs,String msg) {
		try
		{
			String str = HttpPostConnect("Flow_DoPress?token=" + token + "&workIDs=" + workIDs + "&msg=" + msg);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String CC_BatchCheckOver(String token,String workIDs)  {
		try
		{
			String str = HttpPostConnect("CC_BatchCheckOver?token=" + token + "&workIDs=" + workIDs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_BatchDeleteByFlag(String token,String workIDs) {
		try
		{
			String str = HttpPostConnect("Flow_BatchDeleteByFlag?token=" + token + "&workIDs=" + workIDs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_BatchDeleteByReal(String token,String workIDs) {
		try
		{
			String str = HttpPostConnect("Flow_BatchDeleteByReal?token=" + token + "&workIDs=" + workIDs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_BatchDeleteByFlagAndUnDone(String token, String  workIDs) {
		try
		{
			String str = HttpPostConnect("Flow_BatchDeleteByFlagAndUnDone?token=" + token + "&workIDs=" + workIDs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_DoUnSend(String token, String workIDs) {
		try
		{
			String str = HttpPostConnect("Flow_DoUnSend?token=" + token + "&workIDs=" + workIDs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_DeleteDraft(String token, String  workIDs) {
		try
		{
			String str = HttpPostConnect("Flow_DeleteDraft?token=" + token + "&workIDs=" + workIDs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String Flow_DoFlowOver(String token, String workIDs){
		try
		{
			String str = HttpPostConnect("Flow_DoFlowOver?token=" + token + "&workIDs=" + workIDs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String HuiQian_Init(Long workID){
		try
		{
			String str = HttpPostConnect("HuiQian_Init?workID=" + workID);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String HuiQian_AddEmps(Long workID, String huiQianType, String empStrs){
		try
		{
			String str = HttpPostConnect("HuiQian_AddEmps?workID=" + workID + "&huiQianType=" + huiQianType + "&empStrs=" + empStrs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}
	public String HuiQian_DeleteEmps(Long workID, String empStrs){
		try
		{
			String str = HttpPostConnect("HuiQian_DeleteEmps?workID=" + workID + "&empStrs=" + empStrs);
			if (str.contains("err@") == true)
			{
				return Return_Info(500, str, str);
			}
			return Return_Info(200, str, str);
		}
		catch (RuntimeException ex)
		{
			return Return_Info(500, ex.getMessage(), String.valueOf(ex.getStackTrace()));
		}
	}

	private static String HttpPostConnect(String apiName)
	{
		String url = CCBPMHost + "/WF/API/" + apiName;

		return doPost(url, null,null,null);
		//string data =BP.Tools.Encode(data);
	}
	public static String doPost(String url, Map<String, String> param,String header,String context) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpConnectionManager.getHttpClient();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);

			if(DataType.IsNullOrEmpty(context)== false)
				httpPost.setHeader(header,context);
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (Object key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key.toString(), param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "utf-8");

				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==302){
				return "";
			}

			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}
}
