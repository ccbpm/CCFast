package bp.web;

import bp.en.*;
import bp.da.*;
import bp.port.*;
import bp.sys.*;
import bp.pub.*;
import bp.difference.*;
import bp.tools.StringUtils;
import com.rabbitmq.client.AMQP;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;

/**
 * User 的摘要说明。
 */
public class WebUser {

    private static String currentUser = "";

    public static void setCurrentUser(String no){
        currentUser = no;
    }
    private static RedissonClient redisson = ContextHolderUtils.getRedisson();

    public static String redisKey = SystemConfig.getRedisCacheKey("WebUserCache");
    private static RMapCache<Object,Object> _WebUser_Cache;
    public static RMapCache getWebUser_Redis() {
        if (_WebUser_Cache == null) {
            _WebUser_Cache = redisson.getMapCache(redisKey);
        }
        return _WebUser_Cache;
    }

    /**
     * 更改一个人当前登录的主要部门
     * 再一个人有多个部门的情况下有效.
     * <p>
     * param empNo 人员编号
     * param fk_dept 当前所在的部门.
     */
    public static void ChangeMainDept(String empNo, String fk_dept) {
        //这里要考虑集成的模式下，更新会出现是.

        String sql = SystemConfig.GetValByKey("UpdataMainDeptSQL", "");
        if (sql.equals("")) {
            /*如果没有配置, 就取默认的配置.*/
            sql = "UPDATE Port_Emp SET FK_Dept=@FK_Dept WHERE No=@No";
        }

        sql = sql.replace("@FK_Dept", "'" + fk_dept + "'");
        sql = sql.replace("@No", "'" + empNo + "'");

        try {
            if (sql.contains("UPDATE Port_Emp SET FK_Dept=") == true) {
                if (DBAccess.IsView("Port_Emp", SystemConfig.getAppCenterDBType()) == true) {
                    return;
                }
            }
            DBAccess.RunSQL(sql);

            sql = "UPDATE WF_Emp SET OrgNo=@FK_Dept,FK_Dept=@FK_Dept WHERE No=@No";
            sql = sql.replace("@FK_Dept", "'" + fk_dept + "'");
            sql = sql.replace("@No", "'" + empNo + "'");
            DBAccess.RunSQL(sql);
        } catch (RuntimeException ex) {
            throw new RuntimeException("@执行更改当前操作员的主部门的时候错误,请检查SQL配置:" + ex.getMessage());
        }
    }

    /**
     * 通用的登录
     * <p>
     * param em 人员
     * param lang 语言
     * //param auth 授权人
     * param isRememberMe 是否记录cookies
     * param IsRecSID 是否记录SID
     */

    public static void SignInOfGener(Emp em, String lang, boolean isRememberMe, boolean IsRecSID, String authNo) throws Exception {
        SignInOfGener(em, lang, isRememberMe, IsRecSID, authNo, null);
    }

    public static void SignInOfGener(Emp em, String lang, boolean isRememberMe, boolean IsRecSID) throws Exception {
        SignInOfGener(em, lang, isRememberMe, IsRecSID, null, null);
    }

    public static void SignInOfGener(Emp em, String lang, boolean isRememberMe) throws Exception {
        SignInOfGener(em, lang, isRememberMe, false, null, null);
    }

    public static void SignInOfGener(Emp em, String lang) throws Exception {
        SignInOfGener(em, lang, false, false, null, null);
    }

    public static void SignInOfGener(Emp em) throws Exception {
        SignInOfGener(em, "CH", false, false, null, null);
    }

    public static void SignInOfGener(Emp em, String lang, boolean isRememberMe, boolean IsRecSID, String authNo, String authName) throws Exception {
        if (ContextHolderUtils.getInstance() == null) {
            SystemConfig.setIsBSsystem(false);
        } else {
            SystemConfig.setIsBSsystem(true);
        }
        // 这里的登录记录太多
        // if (SystemConfig.isBSsystem()) {
        //     bp.sys.base.Glo.WriteUserLog(em.getNo(), "登录");
        // }
        HashMap<String, String> ht = new HashMap<>();
        //存储WebUser信息
        ht.put("No", em.getUserID());
        ht.put("Name", em.getName());

        // 增加他的orgNo
//        if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
//            ht.put("OrgNo",em.getOrgNo());
//        }

        if (DataType.IsNullOrEmpty(authNo) == false) {
            ht.put("Auth", authNo); // 被授权人，实际工作的执行者.
            ht.put("AuthName", authName);
        } else {
            ht.put("Auth", ""); // 被授权人，实际工作的执行者.
            ht.put("AuthName", "");
        }

        //解决没有部门编号的问题.
        if (DataType.IsNullOrEmpty(em.getOrgNo()) == false && DataType.IsNullOrEmpty(em.getDeptNo()) == true)
        {
            bp.port.DeptEmp de = new bp.port.DeptEmp();
            de.SetValByKey("FK_Dept", em.getOrgNo());
            de.SetValByKey("FK_Emp", em.getNo());
            de.SetValByKey("OrgNo", em.getOrgNo());
            de.Insert();
            // em.FK_Dept = em.OrgNo;
        }

        /// 解决部门的问题.
        if (DataType.IsNullOrEmpty(em.getDeptNo()) == true) {
           // String sql = "";
               // sql = "SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp='" + em.getNo() + "'";
            Paras paras = new Paras();
            paras.SQL="SELECT FK_Dept FROM Port_DeptEmp WHERE FK_Emp="+SystemConfig.getAppCenterDBVarStr()+"FK_Emp";
            paras.Add("FK_Emp",em.getNo());
            String deptNo = DBAccess.RunSQLReturnString(paras);
            if (DataType.IsNullOrEmpty(deptNo) == true) {
                if ("Guest".equals(em.getNo()) == true)
                {
                    if (bp.difference.SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
                    {
                        bp.port.DeptEmp de = new bp.port.DeptEmp();
                        de.SetValByKey("FK_Dept", "ccs");
                        de.SetValByKey("FK_Emp", "Guest");
                        de.Insert();
                    }
                }
                else
                {
                    if (DataType.IsNullOrEmpty(deptNo) == true)
                        throw new Exception("@登录人员(" + em.getUserID() + "," + em.getName() + ")没有维护部门." + paras.SQL);
                }
            } else {
                // 调用接口更改所在的部门.
                WebUser.ChangeMainDept(em.getNo(), deptNo);
                em.SetValByKey("FK_Dept", deptNo);
            }
        }

        bp.port.Dept dept = new Dept();
        dept.SetValByKey("No", em.getDeptNo());
        if (dept.RetrieveFromDBSources() == 0) {
            throw new RuntimeException(
                    "@登录人员(" + em.getUserID() + "," + em.getName() + ")没有维护部门,或者部门编号{" + em.getDeptNo() + "}不存在.");
        }
        /// 解决部门的问题.

        ht.put("FK_Dept", em.getDeptNo());
        ht.put("OrgNo", dept.getOrgNo());
        ht.put("FK_DeptName", em.getDeptText());
        ht.put("SysLang", lang);

        //判断是否启用Redis
        if(SystemConfig.getRedisIsEnable()==true){
            String ip = getIp();
            if (!StringUtils.isBlank(ip)) {
                getWebUser_Redis().put("WebUser_" + ip,ht);
            }
        }else{
            for (java.util.Map.Entry<String, String> next: ht.entrySet()) {

                WebUser.setItemValue(next.getKey(),next.getValue());
            }
        }

        if (SystemConfig.isBSsystem()) {

            // cookie操作，为适应不同平台，统一使用HttpContextHelper
            ContextHolderUtils.addCookie("No", em.getUserID());
            if (isRememberMe) {
                ContextHolderUtils.addCookie("IsRememberMe", "1");
            } else {
                ContextHolderUtils.addCookie("IsRememberMe", "0");
            }

            ContextHolderUtils.addCookie("FK_Dept", em.getDeptNo());

            //设置组织编号.
            if (bp.difference.SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single)
                ContextHolderUtils.addCookie("OrgNo", em.getOrgNo());

            ContextHolderUtils.addCookie("Tel", em.getTel());
            ContextHolderUtils.addCookie("Lang", lang);
            if (authNo == null) {
                authNo = "";
            }
            ContextHolderUtils.addCookie("Auth", authNo); // 授权人.
            ContextHolderUtils.addCookie("CCBPMRunModel", String.valueOf(SystemConfig.getCCBPMRunModel().getValue()));
            ContextHolderUtils.addCookie("AppCenterDBType", SystemConfig.getAppCenterDBType().toString());
            ContextHolderUtils.addCookie("CustomName", SystemConfig.getCustomerName());
            ContextHolderUtils.addCookie("CustomNo", SystemConfig.getCustomerNo());
            if (authName == null) {
                authName = "";
            }
        }
    }

    /**
     * 通过key,取出session.
     * <p>
     * param key key
     * param isNullAsVal 如果是Null, 返回的值.
     *
     * @return
     */
    public static String GetSessionByKey(String key, String isNullAsVal) {

        if (getIsBSMode() && ContextHolderUtils.getInstance() != null
                && ContextHolderUtils.getInstance().getSession() != null) {
            String str = (String) ContextHolderUtils.getSession().getAttribute(key);
            if (DataType.IsNullOrEmpty(str)) {
                str = isNullAsVal;
            }
            return str;
        } else {
            if (bp.pub.Current.Session.get(key) == null || bp.pub.Current.Session.get(key).toString().equals("")) {
                bp.pub.Current.Session.put(key, isNullAsVal);
                return isNullAsVal;
            } else {
                return (String) bp.pub.Current.Session.get(key);
            }
        }
    }

    ///#endregion

    /**
     * 是不是b/s 工作模式。
     */
    protected static boolean isBSMode() {
        if (ContextHolderUtils.getInstance() == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置session
     * <p>
     * param key 键
     * param val 值
     */
    public static void SetSessionByKey(String key, String val) {
        if (val == null) {
            return;
        }
        if (getIsBSMode() && null != ContextHolderUtils.getRequest() && null != ContextHolderUtils.getSession()) {
            ContextHolderUtils.getSession().setAttribute(key, val);
        } else {
            Current.SetSession(key, val);
        }
    }

    /**
     * 退回
     */
    public static void Exit() throws Exception {
        String guid = DBAccess.GenerGUID();
        /*Paras paras = new Paras();
        String dbStr = SystemConfig.getAppCenterDBVarStr();
        paras.SQL="UPDATE WF_Emp SET AtPara=REPLACE(AtPara,'@Token_PC="+dbStr+"Token1', '@Token_PC="+dbStr+"Token2') WHERE No="+dbStr+"UserNo";
        paras.Add("Token1",WebUser.getToken());
        paras.Add("Token2",guid);
        paras.Add("UserNo",WebUser.getNo());*/
        String sql = "UPDATE WF_Emp SET AtPara = REPLACE(AtPara, '@Token_PC=" + WebUser.getToken() + "', '@Token_PC=" + guid + "') WHERE No = '" + WebUser.getNo() + "'";
        DBAccess.RunSQL( sql);

        if(SystemConfig.getRedisIsEnable()){
            String token = getIp();
            if(DataType.IsNullOrEmpty(token)==false && WebUser.getIsAdmin()==false) {
                getWebUser_Redis().remove("WebUser_" + token);
            }
        }
        try {
            // 清理Session
            if(SystemConfig.getRedisIsEnable()==false){
                WebUser.setNo("");
                WebUser.setName("");
                WebUser.setDeptNo("");
                WebUser.setDeptName("");
                WebUser.setSID("");
                WebUser.setAuth("");
                WebUser.setSysLang("");
            }
            if (SystemConfig.isBSsystem()) {

                ContextHolderUtils.addCookie("No", "");
                ContextHolderUtils.addCookie("Name", "");
                ContextHolderUtils.addCookie("IsRememberMe", "");
                ContextHolderUtils.addCookie("FK_Dept", "");
                ContextHolderUtils.addCookie("FK_DeptName", "");
                ContextHolderUtils.addCookie("Token", "");
                ContextHolderUtils.addCookie("SID", "");
                ContextHolderUtils.addCookie("Lang", "");
                ContextHolderUtils.addCookie("Auth", "");
            }
        } catch (java.lang.Exception e2) {
        }
    }

    /**
     * 授权人
     */
    public static String getAuth() throws Exception {
        return getItemValue("Auth");
    }

    public static String getItemValue(String item) {
        if(SystemConfig.getRedisIsEnable()){
            if(DataType.IsNullOrEmpty(getIp())==true)
                return "";
            Object obj = getWebUser_Redis().get("WebUser_" + getIp());
            if (obj == null)
                return "";
            HashMap ht = obj instanceof HashMap ? (HashMap) obj : null;
            if(ht==null)
                return "";
            if( ht.get(item) != null && !ht.get(item).equals("null")){
                return ht.get(item).toString();
            }else {
                return  "";
            }
        }
        return GetValFromCookie(item, null, true);
    }

    public static void setItemValue(String item, String value)  {
        if (value==null || value=="" )
            value="null";
        value=value.replace(" ","");

        if(SystemConfig.getRedisIsEnable()){
            String ip = getIp();
            if (StringUtils.isBlank(ip)) {
                return;
            }
            Object obj = getWebUser_Redis().get("WebUser_" + ip);
            HashMap<String, Object> ht = null;
            if (obj == null)
                ht = new HashMap<>();
            else
                ht = obj instanceof HashMap ? (HashMap) obj : null;
            if (value == null) value = "";
            if (ht != null) {
                ht.put(item, value);
                getWebUser_Redis().get("WebUser_" + ip);
            }
            return;
        }

        ContextHolderUtils.addCookie(item, value.trim());
        SetSessionByKey(item, value.trim());

    }

    public static void setAuth(String value) throws Exception {
        setItemValue("Auth", value);
    }

    /**
     * 部门名称
     */
    public static String getDeptName() {
        return getItemValue("FK_DeptName");
    }

    public static void setDeptName(String value) throws Exception {
        setItemValue("FK_DeptName", value);
    }

    /**
     * 部门全称
     */
    public static String getDeptNameOfFull() {
        String val = getItemValue("FK_DeptNameOfFull");
		if (DataType.IsNullOrEmpty(val))
		{
			try
			{

				Paras ps = new Paras();
				ps.SQL = "SELECT NameOfPath FROM Port_Dept WHERE No =" + ps.getDBStr() + "No";
				ps.Add("No", WebUser.getDeptNo());
				val = DBAccess.RunSQLReturnStringIsNull(ps, null);
				if (DataType.IsNullOrEmpty(val))
					val = WebUser.getDeptName();

				WebUser.setDeptNameOfFull(val);
				return val;
			}
			catch (java.lang.Exception e)
			{
				val = WebUser.getDeptName();
			}
		}
		return val;
    }

    public static void setDeptNameOfFull(String value) throws Exception {
        setItemValue("FK_DeptNameOfFull", value);
    }

    /**
     * 令牌
     */
    public static String getToken() {
        if(SystemConfig.getRedisIsEnable())
            return getIp();
        return getItemValue("Token");
    }

    public static void setToken(String value) throws Exception {
        ContextHolderUtils.addCookie("Token", value);
        setItemValue("Token", value);
    }

    /**
     * 语言
     */
    public static String getSysLang() {
        return "CH";

    }

    public static void setSysLang(String value) throws Exception {
        SetSessionByKey("Lang", value);
    }

    /**
     * 当前登录人员的部门
     */
    public static String getDeptNo() {
        String val =  getItemValue("FK_Dept");
		if (val == null)
		{
			if (WebUser.getNo() == null)
				throw new RuntimeException("@登录信息丢失，请你确认是否启用了cookie? ");

			String sql = "SELECT FK_Dept FROM Port_Emp WHERE No='" + WebUser.getNo() + "'";
			String dept = DBAccess.RunSQLReturnStringIsNull(sql, null);
			if (dept == null)
			{
				sql = "SELECT FK_Dept FROM Port_Emp WHERE No='" + WebUser.getNo() + "'";
				dept = DBAccess.RunSQLReturnStringIsNull(sql, null);
			}

			if (dept == null)
				throw new RuntimeException("@err-003 FK_Dept，当前登录人员(" + WebUser.getNo() + ")，没有设置部门。");
            setItemValue("FK_Dept", dept);
			return dept;
		}
		return val;
    }

    public static void setDeptNo(String value){
        setItemValue("FK_Dept", value);
    }

    /**
     * 当前登录人员的父节点编号
     */
    public static String getDeptParentNo() throws Exception {
        String val = getItemValue("DeptParentNo");
        if (DataType.IsNullOrEmpty(val)) {
            if (bp.web.WebUser.getDeptNo() == null) {
                throw new RuntimeException("@err-001 DeptParentNo, FK_Dept 登录信息丢失。");
            }

            Dept dept = new Dept(bp.web.WebUser.getDeptNo());
            bp.web.WebUser.setDeptParentNo(dept.getParentNo());
            return dept.getParentNo();
        }
        return val;
    }

    public static void setDeptParentNo(String value) throws Exception {
        setItemValue("DeptParentNo", value);
    }

    public static String getNoOfRel() throws Exception {
        return getItemValue("No");
    }

    public static String GetValFromCookie(String valKey, String isNullAsVal, boolean isChinese) {
        if (!getIsBSMode()) {
            return Current.GetSessionStr(valKey, isNullAsVal);
        }

        try {
            // 先从session里面取.
            Object value = ContextHolderUtils.getSession().getAttribute(valKey);
            String v = value == null ? "" : String.valueOf(value);
            if (DataType.IsNullOrEmpty(v) == false) {
                if (isChinese) {
                    v = URLDecoder.decode(v, "UTF-8");
                }
                return v;
            }
            if (valKey.equals("No") && DataType.IsNullOrEmpty(v) == true)
                return "";
        } catch (java.lang.Exception e) {

        }

        try {
            String val = null;
            Cookie cookie = ContextHolderUtils.getCookie(valKey);
            if (cookie != null) {
                if (isChinese) {
                    val = URLDecoder.decode(cookie.getValue(), "UTF-8");
                } else {
                    val = cookie.getValue();
                }
            }

            if (DataType.IsNullOrEmpty(val) == true) {
                return "";
            }
            return val;
        } catch (java.lang.Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    /**
     * 是不是b/s 工作模式。
     *
     * @throws Exception
     */
    protected static boolean getIsBSMode() {
        if (ContextHolderUtils.getInstance() == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置信息.
     * <p>
     * param keyVals
     */
    public static void SetValToCookie(String keyVals) {
        if (SystemConfig.isBSsystem() == false) {
            return;
        }
        AtPara ap = new AtPara(keyVals);
        for (String key : ap.getHisHT().keySet()) {
            ContextHolderUtils.addCookie(key, ap.GetValStrByKey(key));
        }
    }

    public static boolean getIsAdmin() {
        if (WebUser.getNo() != null &&( WebUser.getNo().equals("admin") || WebUser.getNo().equals("system")))
            return true;

        try{
            if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single) {
                GloVar gloVar = new GloVar();
                gloVar.setNo(WebUser.getNo() + "_Adminer");
                if(gloVar.RetrieveFromDBSources()==0)
                    return false; //单机版.
                return true;
            }
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }


        // SAAS版本. 集团版
        if (SystemConfig.getCCBPMRunModel() != CCBPMRunModel.Single) {
            String sql = "SELECT FK_Emp FROM Port_OrgAdminer WHERE FK_Emp='" + WebUser.getUserID() + "' AND OrgNo='"
                    + WebUser.getOrgNo() + "'";
            if (DBAccess.RunSQLReturnTable(sql).Rows.size() == 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static String getRootNo() throws Exception {
        if (WebUser.getNo().equals("Admin"))
            return "0";
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single)
        {
            if (WebUser.getIsAdmin())
            {
                GloVar gloVar = new GloVar();
                gloVar.setNo(WebUser.getNo() + "_Adminer");
                if (gloVar.RetrieveFromDBSources() == 1)
                    return gloVar.getNote(); //单机版.
            }
            return "0";
        }
        return WebUser.getOrgNo();
    }
    /**
     * 编号
     */
    public static String getNo() {
        return getItemValue("No");
    }
    public static String getUserID() {
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
            return WebUser.getOrgNo() + "_" + WebUser.getNo();
        return WebUser.getNo();
    }

    public static void setNo(String value) throws Exception {
        setItemValue("No", value.trim());
    }

    /**
     * 名称
     */
    public static String getName() {
		String no = bp.web.WebUser.getNo();

		String val = getItemValue("Name");
        if(DataType.IsNullOrEmpty(val))
            val = no;
		if (val == null)
			throw new RuntimeException("@err-002 Name 登录信息丢失。");
		return val;
    }

    public static void setName(String value) throws Exception {
        setItemValue("Name", value);
    }

    /**
     * 运行设备
     */
    public static String getSheBei() throws Exception {
        return "PC";
		/*String no = bp.web.WebUser.getNo();
		String val = GetValFromCookie("SheBei", no, true);
		if (val == null)
		{
			return "PC";
		}
		return val;*/
    }

    public static void setSheBei(String value) throws Exception {
        SetSessionByKey("SheBei", value);
    }

    /**
     * 更新当前管理员的组织SID信息.
     */
    public static void UpdateSIDAndOrgNoSQL() throws Exception {
        String sql = "";
        if (DBAccess.IsView("Port_Emp") == false) {
            sql = "UPDATE Port_Emp SET OrgNo='" + WebUser.getOrgNo() + "', FK_Dept='" + WebUser.getDeptNo() + "' WHERE No='" + WebUser.getNo() + "'";
            DBAccess.RunSQL(sql);

            sql = "UPDATE WF_Emp SET OrgNo='" + WebUser.getOrgNo() + "', FK_Dept='" + WebUser.getDeptNo() + "' WHERE No='" + WebUser.getNo() + "'";
            DBAccess.RunSQL(sql);
            return;
        }

        //比如: UPDATE XXX SET bumenbianao='@FK_Dept', zhizhibianhao='@OrgNo',  SID='@SID'  WHERE bianhao='@No'
        sql = bp.sys.base.Glo.getUpdateSIDAndOrgNoSQL();
        if (DataType.IsNullOrEmpty(sql) == true) {
            return;
        }
        //      throw new Exception("err@系统管理员缺少全局配置变量 AppSetting UpdateSIDAndOrgNoSQL ");

        sql = sql.replace("@FK_Dept", WebUser.getDeptNo());
        sql = sql.replace("@OrgNo", WebUser.getOrgNo());
        sql = sql.replace("@Token", WebUser.getToken());
        sql = sql.replace("@No", WebUser.getNo());
        DBAccess.RunSQL(sql);
    }

    /**
     * SID
     *
     * @throws Exception
     */
    public static String getSID() throws Exception {
        String val = GetValFromCookie("SID", null, true);
        if (val == null) {
            return "";
        }
        return val;
    }

    public static void setSID(String value) throws Exception {
        SetSessionByKey("SID", value);
    }

    /**
     * 设置SID
     * <p>
     * param sid
     *
     * @throws Exception
     */
    public static void SetSID(String sid) throws Exception {
        // 判断是否视图，如果为视图则不进行修改
        if (DBAccess.IsView("Port_Emp", SystemConfig.getAppCenterDBType()) == false) {
            Paras ps = new Paras();
            ps.SQL = "UPDATE Port_Emp SET SID=" + SystemConfig.getAppCenterDBVarStr() + "SID WHERE No="
                    + SystemConfig.getAppCenterDBVarStr() + "No";
            ps.Add("SID", sid);
            ps.Add("No", WebUser.getNo());
            DBAccess.RunSQL(ps);
        }
        WebUser.setSID(sid);
    }

    /**
     * 所在的组织
     */
    public static String getOrgNo() {
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single) {
            return "";
        }
        String val =  getItemValue("OrgNo");
        if (DataType.IsNullOrEmpty(val)==true) {
            if (WebUser.getNo() == null) {
                throw new RuntimeException("err@登录信息丢失，请重新登录.");
            }

            if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS) {
                String no = DBAccess.RunSQLReturnString("SELECT OrgNo FROM Port_Emp WHERE UserID='" + WebUser.getNo() + "'");
                if(DataType.IsNullOrEmpty(no)==true)
                    throw new RuntimeException("err@SAAS模式人员编号为["+WebUser.getNo()+"]的Port_Emp表中组织编号不能为空");
                setItemValue("OrgNo", no);
                return no;
            }

            if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.GroupInc) {
                String no = DBAccess.RunSQLReturnString("SELECT OrgNo FROM Port_Emp WHERE No='" + WebUser.getNo() + "'");
                if(DataType.IsNullOrEmpty(no)==true)
                    throw new RuntimeException("err@集团模式人员编号为["+WebUser.getNo()+"]的Port_Emp表中组织编号不能为空");
                setItemValue("OrgNo", no);
                return no;
            }
        }
        return val;
    }

    public static void setOrgNo(String value) {
        setItemValue("OrgNo", value);
    }

    public static String getOrgName() {
        if (SystemConfig.getCCBPMRunModel() == CCBPMRunModel.Single) {
            return "";
        }

        String val = getItemValue("OrgName");
        if (DataType.IsNullOrEmpty(val) == true) {
            if (WebUser.getNo() == null) {
                throw new RuntimeException("@err-006 OrgName 登录信息丢失，或者在 CCBPMRunModel=0 的模式下不能读取该节点.");
            }

            val = DBAccess.RunSQLReturnString("SELECT Name FROM Port_Org WHERE No='" + WebUser.getOrgNo() + "'");
            setItemValue("OrgName", val);

        }
        if (val == null) {
            val = "";
        }
        return val;
    }

    public static void setOrgName(String value) throws Exception {
        setItemValue("OrgName", value);
    }

    /**
     * 手机号
     */
    public static String getTel() throws Exception {
        String val = getItemValue("Tel");
        if (val == null) {
            if (WebUser.getNo() == null) {
                throw new RuntimeException("@登录信息丢失，请你确认是否启用了cookie? ");
            }

            String sql = "SELECT Tel FROM Port_Emp WHERE No='" + WebUser.getNo() + "'";
            String tel = DBAccess.RunSQLReturnStringIsNull(sql, null);

            setItemValue("Tel", tel);
            return tel;
        }
        return val;
    }

    public static void setTel(String value) throws Exception {
        setItemValue("Tel", value);
    }

    /**
     * 域
     */
    public static String getDomainExt() throws Exception {
        String val = getItemValue("DomainExt");
        if (val == null) {
            throw new RuntimeException("@err-003 DomainExt 登录信息丢失。");
        }
        return val;
    }

    public static void setDomainExt(String value) throws Exception {
        setItemValue("DomainExt", value);
    }

    public static Stations getHisStations() throws Exception {
        Stations sts = new Stations();
        QueryObject qo = new QueryObject(sts);
        if(SystemConfig.getCCBPMRunModel() == CCBPMRunModel.SAAS)
            qo.AddWhereInSQL("No", "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" +WebUser.getUserID()+ "'");
        else
        qo.AddWhereInSQL("No", "SELECT FK_Station FROM Port_DeptEmpStation WHERE FK_Emp='" + WebUser.getNo() + "'");
        qo.DoQuery();

        return sts;
    }

    /**
     * 是否是授权状态
     */
    public static boolean getIsAuthorize() throws Exception {
        if (getAuth() == null || getAuth().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * 使用授权人ID
     */
    public static String getAuthName() throws Exception {
        return getItemValue("AuthName");
		/*String val = GetValFromCookie("AuthName", null, false);
		if (val == null)
		{
			val = GetSessionByKey("AuthName", null);
		}
		return val;*/
    }

    public static void setAuthName(String value) throws Exception {
            setItemValue("AuthName", value);

    }

    /**
     * 获取唯一标识
     *
     * @return
     */
    private static String getIp() {
        HttpServletRequest request = ContextHolderUtils.getRequest();
        Cookie[] cookies = request.getCookies();
        String cookieValue = "";
        if (DataType.IsNullOrEmpty(cookies) == false) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals("Token")) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
        }
        if (DataType.IsNullOrEmpty(cookieValue) == true) {
            cookieValue = ContextHolderUtils.getRequest().getHeader("Token");
        }
        if (DataType.IsNullOrEmpty(cookieValue) == true) {
            Object obj = ContextHolderUtils.getRequest().getAttribute("Token");
            if(obj!=null)
                cookieValue = obj.toString();
        }
        if(DataType.IsNullOrEmpty(cookieValue) == true){
            try {
                bp.wf.port.WFEmp emp = new bp.wf.port.WFEmp(currentUser);
                cookieValue = emp.GetParaString("Token_PC"); //获得token.
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return cookieValue;
    }
    //第一次登录 0:否  1：是
    public static String getIsFirstLogin() {
        return getItemValue("IsFirstLogin");
    }
    public static String getRoles() {
        List<String>list=new ArrayList<>();
        String sql="select FK_Station from Port_DeptEmpStation where FK_Emp='"+getNo()+"'";
        DataTable rlist= DBAccess.RunSQLReturnTable(sql);
        for(DataRow row: rlist.Rows){
          String role=  row.getValue(0).toString();
            list.add(role);
        }
        return String.join(",", list);
    }

    //第一次登录
    public static void setIsFirstLogin(String is)  {
        setItemValue("IsFirstLogin", is);
    }

    /**
     设置临时变量.

     @param key 不重复的标记
     @param vals 值
     */
    public static void TempValSaveByKey(String key, String vals) throws Exception {
        UserRegedit en = new UserRegedit();
        String mypk = WebUser.getUserID() + "_" + key;
        en.setMyPK(mypk);
        if (en.RetrieveFromDBSources() == 0)
        {
            en.setCfgKey("TempVar");
            en.SetValByKey("Vals", vals);
            en.Insert();
        }
        else
        {
            en.SetValByKey("Vals", vals);
            en.Update();
        }
    }
    /**
     获得临时变量
     @param key 不重复的值
     @return
     */

    public static String TempValGetByKey(String key) throws Exception {
        return TempValGetByKey(key, "");
    }

    //C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static string TempValGetByKey(string key, string isNullValsAs = "")
    public static String TempValGetByKey(String key, String isNullValsAs) throws Exception {
        UserRegedit en = new UserRegedit();
        String mypk = WebUser.getUserID() + "_" + key;
        en.setMyPK(mypk);
        if (en.RetrieveFromDBSources() == 0)
        {
            return isNullValsAs;
        }
        return en.getVals();
    }

}
