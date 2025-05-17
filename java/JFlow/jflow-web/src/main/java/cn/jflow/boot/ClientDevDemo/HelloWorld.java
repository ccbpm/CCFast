package cn.jflow.boot.ClientDevDemo;

import bp.tools.HttpClientUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// 此文件用于放到自己的开发后台代码里， 可以直接调用这些静态的方法，ccbpm帮您封装好了.
public class HelloWorld {
    //后台地址
    static String host="http://localhost:8009/WF/API";
    static String webPath="";
    //秘钥：用于双方系统登录的通讯, 只有在用户登录的时候用到.
    static String privateKey= "DiGuaDiGua,IamCCBPM";
    //流程id
    static String flowNo ="001";
    //管理员id
    static String userNo="admin";
    //token
    static String token="";

    //发起的流程工作id
    static long workID=0l;

    /// <summary>
    /// 单组织:组织结构数据数据同步API.
    /// </summary>
    public static void Organization_Single_API(){
        /*
         * 说明：
         * 1. 阅读本代码之前需要了解ccbpm的组织结构表结构设计. https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3557782&doc_id=31094
         * 2. 该组织结构系列api就是通过接口的模式对这些表进行增加、删除、修改.
         * 3. 您也可以使用自己的方法，直接对表进行操作.
         */

        //登陆:获得token. 只有管理员这个账号才能执行组织结构同步.
        String adminToken = Dev2InterfaceRestfulAPI.Port_Login("admin", "");

        //#region 1. 对角色类型的操作.
        /*
         * 说明：
         * 1. 角色类型是对表： Port_StationType 的操作.
         * 2. 角色类型用于对角色的分组，没有计算意义,对于角色表来说它是外键，如果您的系统没有这个表，就插入一笔编号：001, 名称：通用角色 的记录。
         */
        // 保存或者新增角色, 有编号=001的数据就更新，没有就增加。
        Dev2InterfaceRestfulAPI.Port_StationType_Save(adminToken, "", "001", "通用角色", "");

        //删除角色.
        Dev2InterfaceRestfulAPI.Port_StationType_Delete(adminToken, "", "001");
        //#endregion 1. 对角色类型的操作.

        //#region 2. 对角色的操作.
        /*
         * 说明：
         * 1. 角色是对表 Port_Station 的操作.
         */

        // 保存或者新增角色, 有编号=001的数据就更新，没有就增加。
        Dev2InterfaceRestfulAPI.Port_StationType_Save(adminToken, "", "001", "财务经理岗", "");

        //删除角色.
        Dev2InterfaceRestfulAPI.Port_StationType_Delete(adminToken, "", "001");
        //#endregion 2. 对角色的操作.

        //#region 3. 对部门的操作.
        /*
         * 说明：
         * 1. 部门是对 Port_Dept 的操作.
         */
        // 保存或者新增, 有编号=001的数据就更新，没有就增加。
        Dev2InterfaceRestfulAPI.Port_Dept_Save(adminToken, "", "001", "总经理办公室", "100", "");

        //删除.
        Dev2InterfaceRestfulAPI.Port_Dept_Delete(adminToken, "001");
        //#endregion 3. 对部门的操作.

        //#region 4. 对人员的操作.
        /* 增加或者保存人员,对应的表:Port_Emp.
         * 1. 如果有此zhangsan的账号就更新，没有就插入，详细的参数请参考结构说明.
         * 2. OrgNo 是组织编号,对单组织为空.
         * 3. 如果一人多部门就可以调用多次,stationNos 是当前人员在该部门下的角色.
         */
        String userNo = "zhangsan", userName = "张三";
        String deptNo = "1001"; // 部门编号.
        String keyValues = "@Tel=xxxx@Leader=lisi@Email=zhangsan##ccflow.org"; // 字段值..
        String stationNos = "001,003,003"; // 更新的岗位编号，这些岗位的信息更新到 deptNo 部门下.
        Dev2InterfaceRestfulAPI.Port_Emp_Save(adminToken, "", userNo, userName, deptNo, keyValues, stationNos);

        //删除兼职部门,以及他在该部门下的岗位集合.
        Dev2InterfaceRestfulAPI.Port_Emp_DeleteOneDept(adminToken, "", userNo, deptNo);

        //删除账号
        Dev2InterfaceRestfulAPI.Port_Emp_Delete(adminToken, userNo);
       //#endregion 4. 对人员的操作.

    }
    /// <summary>
    /// 集团组织:组织结构数据数据同步API.
    /// </summary>
    public static void Organization_Group_API(){
        /*
         * 说明：
         * 1. 阅读本代码之前需要了解ccbpm的组织结构表结构设计. https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3557782&doc_id=31094
         * 2. 该组织结构系列api就是通过接口的模式对这些表进行增加、删除、修改.
         * 3. 您也可以使用自己的方法，直接对表进行操作.
         * 4. 参考单组织版.
         */
        //登陆:获得token. 只有管理员这个账号才能执行组织结构同步.
        String adminToken = Dev2InterfaceRestfulAPI.Port_Login("admin", "100");

        //#region 1. 对角色类型的操作.
        /*
         * 说明：
         * 1. 角色类型是对表： Port_StationType 的操作.
         * 2. 角色类型用于对角色的分组，没有计算意义,对于角色表来说它是外键，如果您的系统没有这个表，就插入一笔编号：001, 名称：通用角色 的记录。
         */

        // 保存或者新增角色, 有编号=001的数据就更新，没有就增加。
        Dev2InterfaceRestfulAPI.Port_StationType_Save(adminToken, "100", "001", "通用角色", "");

        //删除角色.
        Dev2InterfaceRestfulAPI.Port_StationType_Delete(adminToken, "100", "001");
        //#endregion 1. 对角色类型的操作.

        //#region 2. 对角色的操作.
        /*
         * 说明：
         * 1. 角色是对表 Port_Station 的操作.
         */

        // 保存或者新增角色, 有编号=001的数据就更新，没有就增加。
        Dev2InterfaceRestfulAPI.Port_StationType_Save(adminToken, "100", "001", "财务经理岗", "");

        //删除角色.
        Dev2InterfaceRestfulAPI.Port_StationType_Delete(adminToken, "100", "001");
        //#endregion 2. 对角色的操作.

      //#region 3. 对部门的操作.
        /*
         * 说明：
         * 1. 部门是对 Port_Dept 的操作.
         */
        // 保存或者新增, 有编号=001的数据就更新，没有就增加。
        Dev2InterfaceRestfulAPI.Port_Dept_Save(adminToken, "100", "001", "总经理办公室", "100", "");

        //删除.
        Dev2InterfaceRestfulAPI.Port_Dept_Delete(adminToken, "001");
        //#endregion 3. 对部门的操作.

        //#region 4. 对人员的操作.
        /* 增加或者保存人员,对应的表:Port_Emp.
         * 1. 如果有此zhangsan的账号就更新，没有就插入，详细的参数请参考结构说明.
         * 2. OrgNo 是组织编号,对单组织为空.
         * 3. 如果一人多部门就可以调用多次,stationNos 是当前人员在该部门下的角色.
         */
        String userNo = "zhangsan", userName = "张三";
        String deptNo = "1001"; // 部门编号.
        String keyValues = "@Tel=xxxx@Leader=lisi@Email=zhangsan##ccflow.org"; // 字段值..
        String stationNos = "001,003,003"; // 更新的岗位编号，这些岗位的信息更新到 deptNo 部门下.
        Dev2InterfaceRestfulAPI.Port_Emp_Save(adminToken, "100", userNo, userName, deptNo, keyValues, stationNos);

        //删除兼职部门,以及他在该部门下的岗位集合.
        Dev2InterfaceRestfulAPI.Port_Emp_DeleteOneDept(adminToken, "100", userNo, deptNo);

        //删除账号
        Dev2InterfaceRestfulAPI.Port_Emp_Delete(adminToken, userNo);
        //#endregion 4. 对人员的操作.

    }

    /// <summary>
    /// 菜单api: 获得发起、待办、在途、抄送等菜单
    /// </summary>
    public static void Demo0_MenuDBApi() throws Exception {
        /*
         * 说明：
         * 1. 通过api获得json数据集合，并构建自己的发起、待办、...页面。
         */
        //登陆:获得用户token.
        String token = Dev2InterfaceRestfulAPI.Port_LoginReToken("liping", "100");

        //获得发起列表.
        String jsonStart = Dev2InterfaceRestfulAPI.DB_Start(token, "");

        //获得待办.
        String jsonTodolist = Dev2InterfaceRestfulAPI.DB_Start(token, "");

        //获得待办(待阅/已阅).
        String jsonTodolistByIsRead = Dev2InterfaceRestfulAPI.DB_TodolistByIsRead(token, "0", "");

        //获得在途
        String jsonRuning = Dev2InterfaceRestfulAPI.DB_Runing(token, "");

        //草稿:暂存的表单,请参考流程属性草稿规则
        String draftCCList = Dev2InterfaceRestfulAPI.DB_Draft(token, "");

        //根据已读未读参数获取抄送列表数据
        String CCListByIsRead = Dev2InterfaceRestfulAPI.DB_CCListByIsRead(token, "0");

        String jsonCCList = Dev2InterfaceRestfulAPI.DB_CCList(token, "", "001");
    }

    /// <summary>
    /// 创建一个简单的流程实例: 发起、发送、撤销、催办、抄送、移交、退回等基础操作.
    /// </summary>
    public static void Demo1_StartSimpleWorkFlow() throws Exception {
        /*
         * 说明：
         * 1. 首先在流程设计器里定义一个流程模板.
         * 2. 记录下来流程编号，每个节点的ID.
         */

        //#region 1.开始节点发起.
        //登陆:获得用户token.
        String token = Dev2InterfaceRestfulAPI.Port_LoginReToken("liping", "100");

        //生成一个流程实例：WorkID，它是一个长整型的数据。 如果liping不能执行发起此流程系统就抛出错误。018
        int workID = Dev2InterfaceRestfulAPI.Node_CreateBlankWorkID(token, "001");

        //如果需要用到了草稿功能：就执行设置草稿。
        Dev2InterfaceRestfulAPI.Node_SetDraft(token, workID);

        //如果需要向流程引擎写入参数,就执行写入参数,比如：按照参数控制方向条件、或者接受人。
        String paras = "@JinE=1000@JSR=zhangsna";
        Dev2InterfaceRestfulAPI.Flow_SaveParas(token, workID, paras);

        //**提交表单时同时做以下两步
        //如果当前流程使用的是节点表单,需要向开始节点表单写入数据. etc: 向从表、附件写入数据参考下一个demo.
        String keyVals = "@Tel=18660153393@Addr=山东济南";
        Dev2InterfaceRestfulAPI.Node_SaveWork(token, workID, keyVals,null,null);

        //如果想打开工作处理器:
        String url = "https://wwww.workflow.com/Port.vue?DoWhat=MyWork&WorkID="+ workID + "&Token="+ token;

        //执行发送1: 系统就按照设计器定义的接受人规则与方向流转,返回执行结果,提示给操作员.
        String msgSend = Dev2InterfaceRestfulAPI.Node_SendWork(token, workID,"","","","");
        //**提交表单时同时做以上两步

        //如需需要撤销.
        String msgUnSend = Dev2InterfaceRestfulAPI.Flow_DoUnSend(token, String.valueOf(workID));

        //执行抄送，需要的情景下执行完发送执行 ,抄送标题可以为空，空时默认流程标题
        String msgCCSend = Dev2InterfaceRestfulAPI.Node_CC_WriteTo_CClist(token, 602, workID, "zhanghaicheng", "张海成", "关于XXX的请示", "请悉知");

        //执行发送2: 发送或者跳转到执行的节点，返回执行结果,提示给操作员.
        //msgSend = Dev2InterfaceRestfulAPI.Node_SendWork(token, workID, "603", "lisi,wangwu");

        //如果需要催办.
        String msgPress = Dev2InterfaceRestfulAPI.Flow_DoPress(token, String.valueOf(workID), "请于7日前办理");

        //如果需要设置标题.
        String msgSetTitle = Dev2InterfaceRestfulAPI.Flow_SetTitle(token, workID, "关于2024年3月报销流程通知");
        //#endregion 2.开始节点发起.

        //#region 2. 第2个节点执行.
        //切换账号.
        token = Dev2InterfaceRestfulAPI.Port_LoginReToken("yuwen", "");

        keyVals = "@Tel=18660153393";
        Dev2InterfaceRestfulAPI.Node_SaveWork(token, workID, keyVals,null,null);

        //可以执行继续发送,执行发送.
        msgSend = Dev2InterfaceRestfulAPI.Node_SendWork(token, workID,"","","","");

        keyVals = "@Addr=山东济南";
        Dev2InterfaceRestfulAPI.Node_SaveWork(token, workID, keyVals,null,null);

        //再次发送
        msgSend = Dev2InterfaceRestfulAPI.Node_SendWork(token, workID,"","","","");

        //#endregion 2. 第2个节点执行.

        //#region 3. 第3个节点执行.
        //切换账号.
        token = Dev2InterfaceRestfulAPI.Port_LoginReToken("fuhui", "");

        //如果需要退回：执行退回, 返回执行结果.，第三个参数节点ID可以为空，为空时自动计算可以退回的节点ID执行退回操作
        String msgReturn = Dev2InterfaceRestfulAPI.Node_ReturnWork(token, workID, 602, "", "工作有误需要退回", true);
        //#endregion 3. 第3个节点执行.

        //如果需要移交.
        String msgShift = Dev2InterfaceRestfulAPI.Node_Shift(token, workID, "zhangyifan", "需要您处理一下");

        //如果需要增加处理人.
        String msgAddTodo = Dev2InterfaceRestfulAPI.Node_AddTodolist(token, workID, "zhanghaicheng");

    //如果需要结束流程.101,102,103,stopFlowType:结束类型,写入到WF_GenerWorkFlow,AtPara字段 1=正常结束,0=非正常结束
        String msgFlowOver = Dev2InterfaceRestfulAPI.Flow_DoFlowOver(token, String.valueOf(workID), 0);

    //如果需要删除流程.
        String msgFlowDel = Dev2InterfaceRestfulAPI.Batch_Delete(token, String.valueOf(workID));
    }

    /// <summary>
    /// 创建一个简单的流程实例: 为开始节点表单保存数据.
    /// 主表数据、从表数据、附件数据
    /// </summary>
    public static void Demo2_StartSimpleAddFormData() throws Exception {
        /*
         * 说明：
         * 1. 首先在流程设计器里定义一个流程模板.
         * 2. 记录下来流程编号，每个节点的ID.
         * 3. 在开始节点上，设置一个表单、包含主表、从表、附件元素.记录下来从表的标识、附件的标识。
         * 4. 设置附件属性为url模式。
         */

        //登陆:获得用户token.
        String token = Dev2InterfaceRestfulAPI.Port_LoginReToken("fuhui", "");

        //生成一个流程实例：WorkID，它是一个长整型的数据。 如果liping不能执行发起此流程系统就抛出错误。
        int workID = Dev2InterfaceRestfulAPI.Node_CreateBlankWorkID(token, "006");

        //保存主表数据.
        String keyVals = "@Tel=18660153393@Addr=山东济南";
        //从表数据:支持多个从表.通过json的标记与从表的ID对应,标识数据存储到那个从表组件里.
        String dtlJSON = "{'ND601CheLiangXinXi':[{'ChePaiHao':'黑A888C','CheLiangLeiXing':'轿车'}],'ND601RenYuanXinXi':[{'XingMing':'张三','JiGuan':'山东','XueLi':'本科'},{'XingMing':'李四','JiGuan':'北京','XueLi':'硕士'}]}";
        // 附件数据:支持多个附件,通过json的标记与附件的ID对应,标识数据存储到那个附件组件里.
        String athJSON = "{'FuJian1':[{'FileName':'wx01.png','FileUrl':'http://ccflow.org/image/wx01.png'},{'FileName':'wx02.png','FileUrl':'http://ccflow.org/image/wx02.png'}],'FuJian2':[{'FileName':'ccbpm.css','FileUrl':'http://localhost:2296/DataUser/InstancePacketOfData/Template/ccbpm.css'}]}";
        //执行数据保存.
        Dev2InterfaceRestfulAPI.Node_SaveWork(token, workID, keyVals, dtlJSON, athJSON);
    }
    /// <summary>
    /// 启动一个流程，设置父子流程关系.
    /// </summary>
    public void Demo3_StartSimpleSetParentFlow()
    {
        /*
         * 说明：
         * 1. 首先在流程设计器里定义一个流程模板.
         */

        //登陆:获得用户token.
        String token = Dev2InterfaceRestfulAPI.Port_LoginReToken("liping", "");

        //生成一个流程实例：WorkID，它是一个长整型的数据。 如果liping不能执行发起此流程系统就抛出错误。
        // Int64 workID = Dev2InterfaceRestfulAPI.Node_CreateBlankWorkID(token, "001");

        String parentNo = "002";
        int parentWorkID = 1000;

        //设置流程的父子关系.
        // Dev2InterfaceRestfulAPI.Flow_SetParentInfo(token, "001", workID, parentWorkID, false);
    }

}