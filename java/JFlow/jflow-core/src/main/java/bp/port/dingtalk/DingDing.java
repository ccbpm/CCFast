package bp.port.dingtalk;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.time.*;

import bp.en.QueryObject;
import bp.port.*;
import bp.port.dingtalk.DingModel.*;
import bp.tools.*;
import bp.port.dingtalk.ddsdk.*;
import bp.da.*;

/**
 * 钉钉主类
 */
public class DingDing {

    public static String getAccessToken() {
        //if (DataType.IsNullOrEmpty(AccessToken_Ding.Value) || AccessToken_Ding.Begin.AddSeconds(ConstVars.CACHE_TIME) < DateTime.Now)
        //    UpdateAccessToken(true);
        //return AccessToken_Ding.Value;
        String url = "https://oapi.dingtalk.com/gettoken?appkey=" + bp.difference.SystemConfig.getDing_AppKey() + "&appsecret=" + bp.difference.SystemConfig.getDing_AppSecret();
        String str = RequestHelper.Get(url);
        bp.da.Log.DebugWriteError(str);
        DingAccessToken token = null;
        try {
            token = (DingAccessToken) FormatToJson.ParseFromJson(str);
        } catch (Exception e) {
            Log.DebugWriteError(e.getMessage());
        }
        if (Objects.equals(token.getErrcode(), "0")) {
            return token.getAccessToken();
        } else {
            return "";
        }

    }

    /**
     * 获取用户ID
     *
     * @param code
     * @return
     */
    public final String GetUserID(String code) {
        String access_token = getAccessToken();
        bp.da.Log.DebugWriteInfo(access_token);
        bp.da.Log.DebugWriteInfo(code);
        String url = "https://oapi.dingtalk.com/user/getuserinfo?access_token=" + access_token + "&code=" + code;
        try {
            String str = RequestHelper.Get(url);
            bp.da.Log.DebugWriteInfo(str);
            CreateUserPostVal user = new CreateUserPostVal();
            user = (CreateUserPostVal) FormatToJson.ParseFromJson(str);
            bp.da.Log.DebugWriteInfo(user.getUserid());
            //bp.da.Log.DebugWriteInfo(access_token + "code:" + code + "1." + user.userid + "2." + user.errcode + "3." + user.errmsg);
            if (!DataType.IsNullOrEmpty(user.getUserid())) {
                UserInfoResult result = getUserInfo(access_token, user.getUserid());
                return result.getMobile();
            }
        } catch (RuntimeException ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
            return ex.getMessage();
        } catch (Exception e) {
            bp.da.Log.DebugWriteError(e.getMessage());
            return e.getMessage();
        }
        return "";
    }

    public final UserInfoResult getUserInfo(String access_token, String userid) throws Exception {
        String userUrl = "https://oapi.dingtalk.com/topapi/v2/user/get?access_token=" + access_token + "&userid=" + userid;

        String json = RequestHelper.Get(userUrl);
        bp.da.Log.DebugWriteInfo(json);
        DingUserInfo userinfo = null;
        UserInfoResult result = new UserInfoResult();
        try {
            userinfo = (DingUserInfo) FormatToJson.ParseFromJson(json);
            result = userinfo.getResult();
            return result;
        } catch (Exception e) {
            throw e;
        }

    }
    ///#region 客户端开发

    /**
     * 获取JS票据
     *
     * @return
     */
    public static JSTicket FetchJSTicket() {
        SimpleCacheProvider cache = SimpleCacheProvider.GetInstance();
        JSTicket jsTicket = cache.GetCache(ConstVars.CACHE_JS_TICKET_KEY, JSTicket.class);
        if (jsTicket == null || DataType.IsNullOrEmpty(jsTicket.getTicket())) {
            String apiurl = FormatApiUrlWithToken(Urls.GET_JSAPI_TICKET);
            jsTicket = Analyze.Get(apiurl, JSTicket.class);
            cache.SetCache(ConstVars.CACHE_JS_TICKET_KEY, jsTicket, ConstVars.CACHE_TIME);
        }
        return jsTicket;
    }

    /**
     * 获取签名包
     *
     * @param url
     * @return
     */
    public static SignPackage FetchSignPackage(String url) {
        JSTicket jsticket = FetchJSTicket();
        SignPackage signPackage = FetchSignPackage(url, jsticket);
        return signPackage;
    }

    /**
     * 获取签名包
     *
     * @param url
     * @return
     */
    public static SignPackage FetchSignPackage(String url, JSTicket jsticket) {
        String timestamp = SignPackageHelper.ConvertToUnixTimeStamp(LocalDateTime.now());
        String nonceStr = SignPackageHelper.CreateNonceStr();
        if (jsticket == null) {
            return null;
        }

        // 这里参数的顺序要按照 key 值 ASCII 码升序排序
        String rawString = Keys.jsapi_ticket + "=" + jsticket.getTicket();
        rawString += "&" + Keys.noncestr + "=" + nonceStr;
        rawString += "&" + Keys.timestamp + "=" + timestamp;
        rawString += "&" + Keys.url + "=" + url;
        String signature = SignPackageHelper.Sha1Hex(rawString).toLowerCase();

        SignPackage signPackage = new SignPackage();
        signPackage.setAgentId(bp.difference.SystemConfig.getDing_AgentID());
        signPackage.setCorpId(bp.difference.SystemConfig.getDing_CorpID());
        signPackage.setTimeStamp(timestamp);
        signPackage.setNonceStr(nonceStr);
        signPackage.setSignature(signature);
        signPackage.setUrl(url);
        signPackage.setRawstring(rawString);
        signPackage.setJsticket(jsticket.getTicket());
        return signPackage;
    }

    /**
     * 更新票据
     */

    public static void UpdateAccessToken() {
        UpdateAccessToken(false);
    }

    /**
     * 更新票据
     *
     * @param forced true:强制更新.false:按缓存是否到期来更新
     */
    public static void UpdateAccessToken(boolean forced) {
        if (!forced && AccessToken_Ding.getBegin().plusSeconds(ConstVars.CACHE_TIME).compareTo(LocalDateTime.now()) >= 0) {
            //没有强制更新，并且没有超过缓存时间
            return;
        }
        String CorpID = bp.difference.SystemConfig.getDing_CorpID();
        String CorpSecret = bp.difference.SystemConfig.getDing_CorpSecret();
        String TokenUrl = Urls.GETTOKEN;
        String apiurl = TokenUrl + "?" + Keys.corpid + "=" + CorpID + "&" + Keys.corpsecret + "=" + CorpSecret;
        TokenResult tokenResult = Analyze.Get(apiurl, TokenResult.class);
        if (tokenResult.getErrCode() == ErrCodeEnum.OK) {
            AccessToken_Ding.setValue(tokenResult.getAccessToken());
            AccessToken_Ding.setBegin(LocalDateTime.now());
        }
    }

    /**
     * 获取拼接Token的url
     *
     * @param apiUrl
     * @return
     */
    public static String FormatApiUrlWithToken(String apiUrl) {
        //为空或超时
        return apiUrl + "?access_token=" + getAccessToken();
    }
    ///#endregion

    /**
     * 下载多媒体文件
     *
     * @param mediaId  多媒体编号
     * @param workID   业务编号
     * @param savePath 保存路径
     * @return
     */
    public static boolean DownLoadMediaById(String mediaId, String workID, String savePath) {
        String apiurl = FormatApiUrlWithToken(Urls.GET_MEDIA) + "&media_id=" + mediaId;
        return Analyze.HttpDownLoadFile(apiurl, savePath);
    }

    /**
     * 下载钉钉所有头像
     *
     * @param savePath
     * @return
     */
    public final boolean DownLoadUserIcon(String savePath) {
        if ((new File(savePath)).isDirectory() == false) {
            (new File(savePath)).mkdirs();
        }

        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/department/list?access_token=" + access_token;
        try {
            String str = RequestHelper.Get(url);
            DepartMentList departMentList = (DepartMentList) FormatToJson.ParseFromJson(str);
            //部门集合
            if (departMentList != null && departMentList.getDepartment() != null && departMentList.getDepartment().size() > 0) {
                //部门信息
                for (DepartMentDetailInfo deptMentInfo : departMentList.getDepartment()) {
                    //部门人员
                    DepartMentUserList userList = GenerDeptUserList(access_token, deptMentInfo.getId());
                    if (userList != null) {
                        for (DepartMentUserInfo userInfo : userList.getUserlist()) {
                            if (DataType.IsNullOrEmpty(userInfo.getAvatar())) {
                                //大图标
                                String UserIcon = savePath + "/" + userInfo.getUserid() + "Biger.png";
                                Files.copy(Paths.get(savePath + "/DefaultBiger.png"), Paths.get(UserIcon), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

                                //小图标
                                UserIcon = savePath + "/" + userInfo.getUserid() + "Smaller.png";
                                Files.copy(Paths.get(savePath + "/DefaultSmaller.png"), Paths.get(UserIcon), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

                                //正常图标
                                UserIcon = savePath + "/" + userInfo.getUserid() + ".png";
                                Files.copy(Paths.get(savePath + "/Default.png"), Paths.get(UserIcon), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                //大图标
                                String headimgurl = userInfo.getAvatar();
                                String UserIcon = savePath + "/" + userInfo.getUserid() + "Biger.png";
                                DataType.HttpDownloadFile(headimgurl, UserIcon);

                                //小图标
                                UserIcon = savePath + "/" + userInfo.getUserid() + "Smaller.png";
                                DataType.HttpDownloadFile(headimgurl, UserIcon);

                                //正常图标
                                UserIcon = savePath + "/" + userInfo.getUserid() + ".png";
                                DataType.HttpDownloadFile(headimgurl, UserIcon);
                            }
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            Log.DebugWriteError(e.getMessage());
            return false;
        }
    }

    private DepartMentUserList GenerDeptUserList(String accessToken, String id) {
        String url = "https://oapi.dingtalk.com/user/list?access_token=" + accessToken + "&department_id=" + id;
        try {
            String str = RequestHelper.Get(url);
            DepartMentUserList departMentUserList = (DepartMentUserList) FormatToJson.ParseFromJson(str);

            //部门人员集合
            if (departMentUserList != null && departMentUserList.getUserlist() != null && departMentUserList.getUserlist().size() > 0)
                return departMentUserList;
        } catch (Exception ex) {
            Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    /**
     * 同步钉钉通讯录到CCGPM
     *
     * @return
     */
    public final boolean AnsyOrgToCCGPM() {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/department/list?access_token=" + access_token;
        try {
            String str = RequestHelper.Get(url);
            DepartMentList departMentList = (DepartMentList) FormatToJson.ParseFromJson(str);
            //部门集合
            if (departMentList != null && departMentList.getDepartment() != null && departMentList.getDepartment().size() > 0) {
                //删除旧数据
                ClearOrgOld();
                //获取根部门
                DepartMentDetailInfo rootDepartMent = new DepartMentDetailInfo();
                for (DepartMentDetailInfo deptMenInfo : departMentList.getDepartment()) {
                    if (deptMenInfo.getId().equals("1")) {
                        rootDepartMent = deptMenInfo;
                        break;
                    }
                }
                //增加跟部门
                int deptIdx = 0;
                Dept rootDept = new Dept();
                rootDept.SetValByKey("No", rootDepartMent.getId());
                rootDept.SetValByKey("Name", rootDepartMent.getName());
                rootDept.SetValByKey("ParentNo", "0");
                rootDept.DirectInsert();


                //部门信息
                for (DepartMentDetailInfo deptMentInfo : departMentList.getDepartment()) {
                    //增加部门,排除根目录
                    if (!deptMentInfo.getId().equals("1")) {
                        Dept dept = new Dept();
                        dept.SetValByKey("No", deptMentInfo.getId());
                        dept.SetValByKey("Name", deptMentInfo.getName());
                        dept.SetValByKey("ParentNo", deptMentInfo.getParentid());
                        dept.DirectInsert();
                    }

                    //部门人员
                    DepartMentUserList userList = GenerDeptUserList(access_token, deptMentInfo.getId());
                    if (userList != null) {
                        for (DepartMentUserInfo userInfo : userList.getUserlist()) {
                            Emp emp = new Emp();
                            DeptEmp deptEmp = new DeptEmp();
                            //如果账户存在则人员信息不添加，添加关联表
                            if (emp.IsExit(EmpAttr.No, userInfo.getUserid()) == true) {
                                deptEmp.setMyPK(deptMentInfo.getId() + "_" + emp.getNo());
                                deptEmp.SetValByKey("DeptNo", deptMentInfo.getId());
                                deptEmp.SetValByKey("EmpNo", emp.getNo());
                                deptEmp.DirectInsert();
                                continue;
                            }

                            //增加人员
                            emp.SetValByKey("No", userInfo.getUserid());
                            emp.SetValByKey("Name", userInfo.getName());
                            emp.SetValByKey("DeptNo", deptMentInfo.getId());
                            emp.SetValByKey("Tel", userInfo.getMobile());
                            emp.SetValByKey("Email", userInfo.getEmail());
                            //emp.Idx = DataType.IsNullOrEmpty(userInfo.getOrder()) == true ? 0 : Integer.parseInt(userInfo.getOrder());
                            emp.DirectInsert();

                            //增加人员与部门对应表
                            deptEmp.setMyPK(deptMentInfo.getId() + "_" + emp.getNo());
                            deptEmp.SetValByKey("DeptNo", deptMentInfo.getId());
                            deptEmp.SetValByKey("DeptNo", emp.getNo());
                            deptEmp.DirectInsert();
                        }
                    }
                }

                ///#region 处理部门名称全程
                //OrgInit_NameOfPath nameOfPath = new OrgInit_NameOfPath();
                //if (nameOfPath.IsCanDo)
                //nameOfPath.Do();
                ///#endregion

            }
            //开始同步角色
            url = "https://oapi.dingtalk.com/topapi/role/list?access_token=" + access_token;
            str = RequestHelper.Get(url);
            //获取返回参数
            DingRoleRequest ding_Role_Request = (DingRoleRequest) FormatToJson.ParseFromJson(str);
            Log.DebugWriteInfo("Ding_Role_request:" + str);
            if (!ding_Role_Request.getErrcode().equals("0")) {
                return false;
            }
            //获取返回值
            DingRolePageVo ding_Role_PageVo = ding_Role_Request.getResult();
            //获取角色分组
            List<DingRoleGroupList> ding_Role_Group_Lists = ding_Role_PageVo.getList();
            for (DingRoleGroupList group_List : ding_Role_Group_Lists) {
                //增加角色分组
                StationType stationType = new StationType();
                stationType.SetValByKey("No", group_List.getGroupId());
                stationType.SetValByKey("Name", group_List.getName());
                stationType.DirectInsert();

                //同步角色
                List<DingRoleList> ding_Role_Lists = group_List.getRoles();
                for (DingRoleList role : ding_Role_Lists) {
                    Station station = new Station();
                    station.SetValByKey("No", role.getId());
                    station.SetValByKey("Name", role.getName());
                    station.SetValByKey("FK_StationType", group_List.getGroupId());
                    station.DirectInsert();

                    //开始同步人员-部门-岗位表
                    String role_user_url = "https://oapi.dingtalk.com/topapi/role/simplelist?access_token=" + access_token;

                    Map<String, Object> bodyStr = new HashMap<String, Object>();
                    bodyStr.put("role_id", role.getId());
                    str = RequestHelper.Post(role_user_url, bp.tools.Json.ToJson(bodyStr));
                    Log.DebugWriteError("Ding_Role_User_Request:" + str);
                    //str = RequestHelper.Get(role_user_url);
                    DingRoleUserRequest request = (DingRoleUserRequest) FormatToJson.ParseFromJson(str);
                    DingRoleUserPageVo pageVo = request.getResult();
                    List<DingRoleUserList> users = pageVo.getList();
                    for (DingRoleUserList user : users) {
                        //获取用户详细信息
                        Emp emp = new Emp(user.getUserid());
                        //插入部门人员岗位表
                        DeptEmpStation deptEmpStation = new DeptEmpStation();
                        deptEmpStation.SetValByKey("EmpNo", user.getUserid());
                        deptEmpStation.SetValByKey("StationNo", role.getId());
                        deptEmpStation.SetValByKey("DeptNo", emp.getDeptNo());
                        deptEmpStation.SetValByKey("MyPK", emp.getDeptNo() + "_" + role.getId() + "_" + user.getUserid());
                        deptEmpStation.DirectInsert();
                    }
                }
            }

            return true;

        } catch (Exception e) {
            Log.DebugWriteError(e.getMessage());
        }
        return false;
    }

    /**
     * 增量同步组织结构
     *
     * @return
     */
    public String AnsyIncrementOrgToGPM() {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/department/list?access_token=" + access_token;
        try {
            StringBuilder sb = new StringBuilder();
            String str = RequestHelper.Get(url);
            DepartMentList departMentList = (DepartMentList) FormatToJson.ParseFromJson(str);
            if (departMentList == null || departMentList.getDepartment() == null || departMentList.getDepartment().size() == 0)
                return "钉钉获取部门出错。";

            ///#region 获取钉钉组织结构，进行更新与新增
            //增加跟部门
            int deptIdx = 0;
            Boolean doSomeThing = false;
            //部门信息
            for (DepartMentDetailInfo deptMentInfo : departMentList.getDepartment()) {
                deptIdx++;
                doSomeThing = false;
                //增加部门,排除根目录
                if (deptMentInfo.getId() != "1") {
                    Dept dept = new Dept();
                    if (dept.IsExit(DeptAttr.No, deptMentInfo.getId()) == true) {
                        if (dept.getNo() == deptMentInfo.getId() && !dept.getName().equals(deptMentInfo.getName())) {
                            doSomeThing = true;
                            sb.append("\r\n部门名称发生变化：" + dept.getName() + " --> " + deptMentInfo.getName());
                        }
                        if (!dept.getParentNo().equals(deptMentInfo.getParentid())) {
                            doSomeThing = true;
                            sb.append("\r\n部门父级发生变化：" + dept.getParentNo() + " --> " + deptMentInfo.getParentid());
                        }
                        //有变化，更新
                        if (doSomeThing == true) {
                            dept.SetValByKey("No", deptMentInfo.getId());
                            dept.SetValByKey("Name", deptMentInfo.getName());
                            dept.SetValByKey("ParentNo", deptMentInfo.getParentid());
                            dept.DirectUpdate();
                        }
                    } else {
                        //不存在则新增
                        dept.SetValByKey("No", deptMentInfo.getId());
                        dept.SetValByKey("Name", deptMentInfo.getName());
                        dept.SetValByKey("ParentNo", deptMentInfo.getParentid());
                        dept.DirectInsert();
                        sb.append("\r\n新增部门：" + deptMentInfo.getId() + " - " + deptMentInfo.getName());
                    }
                }
                //部门人员
                DepartMentUserList userList = GenerDeptUserList(access_token, deptMentInfo.getId());
                if (userList != null) {
                    for (DepartMentUserInfo userInfo : userList.getUserlist()) {
                        Emp emp = new Emp();
                        emp.SetValByKey("No", userInfo.getUserid());

                        DeptEmp deptEmp = new DeptEmp();
                        //如果账户存在则人员信息不添加，添加关联表
                        if (emp.RetrieveFromDBSources() > 0) {
                            if (!emp.getName().equals(userInfo.getName())) {
                                emp.SetValByKey("Name", userInfo.getName());
                                emp.DirectUpdate();
                                sb.append("\r\n人员名称发生变化：" + emp.getName() + " --> " + userInfo.getName());
                            }

                            deptEmp.setMyPK(deptMentInfo.getId() + "_" + emp.getNo());
                            if (deptEmp.RetrieveFromDBSources() > 0)
                                continue;

                            //增加人员归属部门

                            deptEmp.SetValByKey("DeptNo", deptMentInfo.getId());
                            deptEmp.SetValByKey("EmpNo", emp.getNo());
                            deptEmp.DirectInsert();
                            sb.append("\r\n增加人员归属部门：" + emp.getName() + " - " + deptMentInfo.getName());
                            continue;
                        }

                        //增加人员
                        emp.SetValByKey("No", userInfo.getUserid());
                        emp.SetValByKey("Name", userInfo.getName());
                        emp.SetValByKey("DeptNo", deptMentInfo.getId());
                        emp.SetValByKey("Tel", userInfo.getMobile());
                        emp.SetValByKey("Email", userInfo.getEmail());
                        //emp.Idx = DataType.IsNullOrEmpty(userInfo.order) == true ? 0 : Int32.Parse(userInfo.order);
                        emp.DirectInsert();
                        sb.append("\r\n增加人员：" + emp.getName() + "  所属部门:" + deptMentInfo.getName());

                        //增加人员与部门对应表
                        deptEmp.setMyPK(deptMentInfo.getId() + "_" + emp.getNo());
                        deptEmp.SetValByKey("DeptNo", deptMentInfo.getId());
                        deptEmp.SetValByKey("EmpNo", emp.getNo());
                        deptEmp.DirectInsert();
                    }
                }
            }
            ///#endregion

            ///#region GPM组织结构，在钉钉不存在进行删除部门与人员关系表，人员表不进行删除,删除业务人员表WF_Emp
            Depts gpm_Depts = new Depts();
            gpm_Depts.RetrieveAllFromDBSource();
            for (Dept gpm_Dept : gpm_Depts.ToJavaList()) {
                Boolean isHave = false;
                for (DepartMentDetailInfo ding_Dept : departMentList.getDepartment()) {
                    if (gpm_Dept.getNo().equals(ding_Dept.getId())) {
                        isHave = true;
                        break;
                    }
                }

                //部门在钉钉不存在则进行删除：部门表、部门人员、部门人员角色、部门职位、部门角色
                if (isHave == false) {
                    ////部门角色
                    //DeptStation deptStation = new DeptStation();
                    //int iDeptStation = deptStation.Delete(DeptStationAttr.FK_Dept, gpm_Dept.No);

                    ////部门人员角色
                    //DeptEmpStation deptEmpStation = new DeptEmpStation();
                    //int iDeptEmpStation = deptEmpStation.Delete(DeptEmpStationAttr.FK_Dept, gpm_Dept.No);
                    ////部门人员
                    //DeptEmp deptEmp = new DeptEmp();
                    //int iDeptEmp = deptEmp.Delete(DeptEmpAttr.FK_Dept, gpm_Dept.No);
                    ////部门表
                    //Dept dt = new Dept(gpm_Dept.No);
                    //dt.Delete();
                    //sb.append("\r\n删除部门：" + gpm_Dept.Name + " 部门全路径：" + gpm_Dept.NameOfPath);
                    //sb.append("\r\n        部门角色 " + iDeptStation + " 条记录");
                    //sb.append("\r\n        部门人员角色 " + iDeptEmpStation + " 条记录");
                    //sb.append("\r\n        部门人员 " + iDeptEmp + " 条记录");
                } else {
                    //组织结构人员
                    DeptEmps deptEmps = new DeptEmps();
                    QueryObject obj = new QueryObject(deptEmps);
                    obj.AddWhere(DeptEmpAttr.FK_Dept, gpm_Dept.getNo());
                    obj.addAnd();
                    obj.AddWhereNotIn(DeptEmpAttr.FK_Emp, "'admin'");
                    obj.DoQuery();

                    //部门下没有人员不需要处理
                    if (deptEmps == null || deptEmps.size() == 0)
                        continue;

                    //钉钉部门人员
                    DepartMentUserList userList = GenerDeptUserList(access_token, gpm_Dept.getNo());
                    //部门下没有人员，清除部门下的所有人员
                    if (userList == null || userList.getUserlist().size() == 0) {
                        sb.append("\r\n删除部门下的人员，部门：" + gpm_Dept.getName() + " 部门全路径：" + gpm_Dept.getNameOfPath());
                        for (DeptEmp dt : deptEmps.ToJavaList()) {
                            dt.Delete();
                            Emp ep = new Emp();
                            ep.SetValByKey("No", dt.getEmpNo());
                            ep.RetrieveFromDBSources();
                            sb.append("\r\n        人员编号：" + dt.getEmpNo() + " 姓名:" + ep.getName());
                        }
                        continue;
                    }

                    //判断部门下的人员是否存在
                    for (DeptEmp deptEmp : deptEmps.ToJavaList()) {
                        isHave = false;
                        for (DepartMentUserInfo userInfo : userList.getUserlist()) {
                            if (deptEmp.getEmpNo().equals(userInfo.getUserid())) {
                                isHave = true;
                                break;
                            }
                        }

                        //不存在，删除
                        if (isHave == false) {
                            deptEmp.Delete();
                            Emp ep = new Emp();
                            ep.SetValByKey("No", deptEmp.getEmpNo());
                            ep.RetrieveFromDBSources();
                            sb.append("\r\n删除部门下的人员，部门：" + gpm_Dept.getName() + " 部门全路径：" + gpm_Dept.getNameOfPath());
                            sb.append("\r\n        人员编号：" + deptEmp.getEmpNo() + " 姓名：" + ep.getName());
                        }
                    }
                }
            }
            //删除没包含在部门的人员

            ///#endregion

            ///#region 处理部门名称全程
            //OrgInit_NameOfPath nameOfPath = new OrgInit_NameOfPath();
            //if (nameOfPath.IsCanDo)
            //    nameOfPath.Do();
            ///#endregion
            return sb.toString();
        } catch (Exception ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    ///#region 组织结构同步

    /**
     * 钉钉，新增部门同步钉钉
     *
     * @param dept
     * @return
     */
    public CreateDepartMent_PostVal GPM_Ding_CreateDept(Dept dept) {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/department/create?access_token=" + access_token;
        try {
            Hashtable<String, Object> ht = new Hashtable<>();
            ht.put("name", dept.getName());
            ht.put("parentid", dept.getParentNo());
            //ht.put("order", "1");
            ht.put("createDeptGroup", "true");

            String str = bp.tools.Json.ToJson(ht);
            str = RequestHelper.Post(url, str);
            CreateDepartMent_PostVal postVal = (CreateDepartMent_PostVal) FormatToJson.ParseFromJson(str);

            //请求返回信息
            if (postVal != null) {
                if (postVal.getErrcode() != "0")
                    bp.da.Log.DebugWriteError("钉钉新增部门失败：" + postVal.getErrcode() + "-" + postVal.getErrmsg());

                return postVal;
            }
        } catch (Exception ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    /**
     * 钉钉，编辑部门同步钉钉
     *
     * @param dept
     * @return
     */
    public DingPostReturnVal GPM_Ding_EditDept(Dept dept) {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/department/update?access_token=" + access_token;
        try {
            Hashtable<String, Object> ht = new Hashtable<>();
            ht.put("id", dept.getNo());
            ht.put("name", dept.getName());
            //根目录不允许修改
            if (dept.getNo() != "1") {
                ht.put("parentid", dept.getParentNo());
            }

            String str = bp.tools.FormatToJson.ToJson(ht);
            str = RequestHelper.Post(url, str);
            DingPostReturnVal postVal = (DingPostReturnVal) FormatToJson.ParseFromJson(str);

            //请求返回信息
            if (postVal != null) {
                if (postVal.getErrcode() != "0")
                    bp.da.Log.DebugWriteError("钉钉修改部门失败：" + postVal.getErrcode() + "-" + postVal.getErrmsg());

                return postVal;
            }
        } catch (Exception ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    /**
     * 钉钉，删除部门同步钉钉
     *
     * @param deptId
     * @return
     */
    public DingPostReturnVal GPM_Ding_DeleteDept(String deptId) {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/department/delete?access_token=" + access_token + "&id=" + deptId;
        try {
            String str = RequestHelper.Get(url);
            DingPostReturnVal postVal = (DingPostReturnVal) FormatToJson.ParseFromJson(str);

            //请求返回信息
            if (postVal != null) {
                if (postVal.getErrcode() != "0")
                    bp.da.Log.DebugWriteError("钉钉删除部门失败：" + postVal.getErrcode() + "-" + postVal.getErrmsg());

                return postVal;
            }
        } catch (Exception ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    /**
     * 钉钉，新增人员同步钉钉
     *
     * @param emp
     * @return
     */
    public CreateUserPostVal GPM_Ding_CreateEmp(Emp emp) {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/user/create?access_token=" + access_token;
        try {
            Hashtable<String, Object> ht = new Hashtable<>();
            //如果用户编号存在则按照此账号进行新建
            if (!(DataType.IsNullOrEmpty(emp.getNo()) || DataType.IsNullOrEmpty(emp.getNo()))) {
                ht.put("userid", emp.getNo());
            }
            ht.put("name", emp.getName());
            //部门数组
            List<String> listArrary = new ArrayList<String>();
            listArrary.add(emp.getDeptNo());

            ht.put("department", listArrary);
            ht.put("mobile", emp.getTel());
            ht.put("email", emp.getEmail());

            String str = bp.tools.Json.ToJson(ht);
            str = RequestHelper.Post(url, str);
            CreateUserPostVal postVal = (CreateUserPostVal) FormatToJson.ParseFromJson(str);

            //请求返回信息
            if (postVal != null) {
                if (postVal.getErrcode() != "0") {
                    //在钉钉通讯录已经存在
                    if (postVal.getErrcode() == "60102") postVal.setUserid(emp.getNo());
                    bp.da.Log.DebugWriteError("钉钉新增人员失败：" + postVal.getErrcode() + "-" + postVal.getErrmsg());
                }
                return postVal;
            }
        } catch (Exception ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    public DingPostReturnVal GPM_Ding_EditEmp(Emp emp) {
        return GPM_Ding_EditEmp(emp, null);
    }

    /**
     * 钉钉，编辑人员同步钉钉
     *
     * @param emp
     * @param deptIds
     * @return
     */
    public DingPostReturnVal GPM_Ding_EditEmp(Emp emp, List<String> deptIds) {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/user/update?access_token=" + access_token;
        try {
            Hashtable<String, Object> ht = new Hashtable<>();
            ht.put("userid", emp.getNo());
            ht.put("name", emp.getName());
            ht.put("email", emp.getEmail());
            ht.put("mobile", emp.getTel());
            ht.put("position", "");
            //钉钉根据此从其他部门删除或增加到其他部门
            if (deptIds != null && deptIds.size() > 0) {
                ht.put("department", deptIds);
            }
            String str = bp.tools.Json.ToJson(ht);
            str = RequestHelper.Post(url, str);
            DingPostReturnVal postVal = (DingPostReturnVal) FormatToJson.ParseFromJson(str);

            //请求返回信息
            if (postVal != null) {
                Boolean create_Ding_user = false;
                //40022企业中的手机号码和登录钉钉的手机号码不一致,暂时不支持修改用户信息,可以删除后重新添加
                if (postVal.getErrcode() == "40022" || postVal.getErrcode() == "40021") {
                    create_Ding_user = true;
                    postVal = GPM_Ding_DeleteEmp(emp.getNo());
                    //删除失败
                    if (postVal.getErrcode() != "0")
                        create_Ding_user = false;
                } else if (postVal.getErrcode() == "60121")//60121找不到该用户
                {
                    create_Ding_user = true;
                }

                //需要新增人员
                if (create_Ding_user == true) {
                    CreateUserPostVal postUserVal = GPM_Ding_CreateEmp(emp);
                    //消息传递
                    postVal.setErrcode(postUserVal.getErrcode());
                    postVal.setErrmsg(postUserVal.getErrmsg());
                }

                if (postVal.getErrcode() != "0") {
                    bp.da.Log.DebugWriteError("钉钉修改人员失败：" + postVal.getErrcode() + "-" + postVal.getErrmsg());
                }
                return postVal;
            }
        } catch (Exception ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }

    /**
     * 钉钉，删除人员同步钉钉
     *
     * @param userid
     * @return
     */
    public DingPostReturnVal GPM_Ding_DeleteEmp(String userid) {
        String access_token = getAccessToken();
        String url = "https://oapi.dingtalk.com/user/delete?access_token=" + access_token + "&userid=" + userid;
        try {
            String str = RequestHelper.Get(url);
            DingPostReturnVal postVal = (DingPostReturnVal) FormatToJson.ParseFromJson(str);

            //请求返回信息
            if (postVal != null) {
                if (postVal.getErrcode() != "0")
                    bp.da.Log.DebugWriteError("钉钉删除人员失败：" + postVal.getErrcode() + "-" + postVal.getErrmsg());

                return postVal;
            }
        } catch (Exception ex) {
            bp.da.Log.DebugWriteError(ex.getMessage());
        }
        return null;
    }
    //#endregion 组织结构同步

    /**
     * 清空组织结构
     */
    private void ClearOrgOld() throws Exception {
        //人员
        DBAccess.RunSQL("DELETE FROM Port_Emp");
        //部门
        DBAccess.RunSQL("DELETE FROM Port_Dept");
        //部门人员
        DBAccess.RunSQL("DELETE FROM Port_DeptEmp");
        //岗位
        DBAccess.RunSQL("DELETE FROM Port_Station");
        //岗位类型
        DBAccess.RunSQL("DELETE FROM Port_StationType");
        //部门人员角色
        DBAccess.RunSQL("DELETE FROM Port_DeptEmpStation");
        //admin 是必须存在的
        Emp emp = new Emp();
        emp.SetValByKey("No", "admin");
        emp.SetValByKey("Pass", "pub");
        emp.SetValByKey("Name", "管理员");
        emp.SetValByKey("DeptNo", "1");
        emp.DirectInsert();
        //部门人员关联表
        DeptEmp deptEmp = new DeptEmp();
        deptEmp.SetValByKey("DeptNo", "1");
        deptEmp.SetValByKey("EmpNo", "admin");
        deptEmp.DirectInsert();
    }
}