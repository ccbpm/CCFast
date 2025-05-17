import request from '@/utils/request'
import type { BatchWork, CCFlowResponse, CCWork, Draft, GenerWorkFlow, RecentWork, RunningFlow, SearchWork, flow, } from '@/types/interfaceType'
import { useUserStore, type LoginParams, type User } from '@/stores/user';
import CryptoJS from 'crypto-js';
import { AppConfig } from './AppConfig';
import WebUser from './WebUser';

/**
 * 说明:
 * 1. 该类是一个接口文件需要引入到自己的前端vue项目中去.
 * 2. 它提供了与bpm服务器交互的接口, bpm服务器的接口分为两部分, 功能页面接口，API接口.
 * 3. 功能页面接口是指: 比如: 待办,在途,抄送,草稿,我的工作等.通过一个功能页面就可以查看相关的工作.
 * 4. API接口是指: 比如: 发起流程,查询流程,抄送流程,撤销流程,撤回流程,删除流程等. 使用API接口需要在功能页面中点击按钮来调用.
 * 5. 以 Open_开头的方法是打开一个功能页面,比如: Open_MyFlow(workID,paras),它会打开一个功能页面,并传入workID和paras参数.
 * 6. 以 Number_开头的方法是获取一个数字,比如: NumberTodolist(flowNo,paras),它会返回一个数字,比如: 10,表示有10条待办.
 * 7. 以 Port_开头的方法是与bpm服务器交互的接口,比如: Port_Login(userNo,password),它会返回一个数字,比如: 0,表示登录成功.
 * 8. 以 Node_或则Flow_开头的方法是对流程操作的方法，比如：发起、设置草稿、保存流程环境变量.    
 * 9. 以 DB_ 开头的方法是获得一个Json集合的，菜单接口，比如: DB_Start(),它会返回一个Json集合,比如: [{No:'001',Name:'请假申请'},{No:'002',Name:'出差申请'}]
 * 10. 更多的帮助: 请参考: https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=8095471&doc_id=31094
 */
export default class Dev2ApiInterface {


  // (用于数量气泡显示)
  public static async Flow_TodoNums(domain = ''): Promise<any> {
    return request.get<any, any>('/WF/API/Flow_TodoNums', {
      params: {
        domain: domain || ''
      }
    })
  }

  //待办数量(用于数量气泡显示)
  public static async Number_Todolist(): Promise<number> {
    let num = 0
    await Dev2ApiInterface.Flow_TodoNums().then((res) => {
      num = res.Todolist_EmpWorks;
    });
    return num
  }

  // 消息数量(用于数量气泡显示)
  public static async Number_Message(): Promise<number> {
    let num = 0
    await Dev2ApiInterface.Flow_TodoNums().then((res) => {
      num = res.Todolist_Msg;
    });
    return num
  }

  // ======================================================== 门户接口. =======================================================

  /**
   * 用户登录
   * @param userNo 用户账号
   * @param password 密码
   * @returns 返回:用户信息并记录到WebUser.*的实体类.{No:'zhangsan',Name:'张三',DeptNo:'001','DeptName','开发部',OrgNo:'ccflow',OrgName:'驰骋公司',Token:'xxxxx'} 
   * 
   */
  public static async Port_Login(userNo: string, password: string) {
    // 对用户名和密码进行 MD5 哈希处理
    // const md5Username = CryptoJS.MD5(params.username).toString(CryptoJS.enc.Hex);
    // const md5Password = CryptoJS.MD5(params.password).toString(CryptoJS.enc.Hex);
    // 将哈希值拼接，并转换为大写
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0'); // Months are zero-based, so add 1
    const day = String(now.getDate()).padStart(2, '0');
    const dataTime = `${year}_${month}_${day}`;

    const str = userNo + dataTime + password;
    const val = CryptoJS.MD5(str).toString(CryptoJS.enc.Hex);

    const combinedHash = val.toUpperCase();
    // 创建最终的 privateKey 与后台的解析方法要对应.
    const privateKey = userNo + combinedHash;
    return request.get<any, User>('/WF/API/Port_Login', {
      params: {
        privateKey: privateKey,
        userNo,
      }
    })
  }

  /**
   * 用户退出
   * @param userNo 
   * @returns 
   */
  public static async Port_Logout(userNo: string) {
    return request.get<any, null>('/WF/API/Port_LoginOut', {
      params: {
        userNo
      }
    })
  }
  /**
   * 切换登录部门
   * @param deptNo 要切换的部门编号
   * @returns  是否切换成功?如果部门不匹配就抛出错误.
   */
  public static async Port_ChangeDept(deptNo: string): Promise<number> {
    return 0;
  }
  /**
   * 切换登录部门岗位
   * @param deptNo 部门编号
   * @param stationNo 岗位编号
   * @returns 是否切换成功?如果岗位与部门不匹配就抛出错误.
   */
  public static async Port_ChangeDeptAndStation(deptNo: string, stationNo: string): Promise<number> {
    return 0;
  }
  /**
   * 设置主题样式，为当前的用户系统保持一致.
   * @param style 主题样式
   * @returns 无
   */
  public static async Port_SetStyle(style: string) {
    return 0;
  }
  /**
   * 用户信息获取
   */
  public static async Port_GetWebUser() {
    const userStore = useUserStore();
    const WebUser = userStore.webUser;
    return WebUser;
  }
  // ======================================================== 流程操作. =======================================================

  /**
   * 创建一个WorkID,并写入流程参数或者表单数据
   * @param flowNo 流程编号
   * @param paras 创建流程写入的环境变量或者流程系统参数.
   * @returns 创建的流程实例,我们称为WorkID, 这个时间WorkID是空白状态,未被使用,如果他发送下去或者设置草稿,在创建workid就是一个新的WorkID.
   */
  public static async Node_CreateBlank(flowNo: string, paras = ''): Promise<number> {
    return request.get<any, any>('/WF/API/Node_CreateBlankWorkID', {
      params: {
        flowNo: flowNo,
        Paras: paras,
      }
    })
  }

  // /**
  //  * 保存
  //  * @param workID 工作ID
  //  * @param mainTable 主表数据: 格式1: @filed1=val1@filed2=val2  格式2: { filed1:'xxx', filed2:'yyyy'}
  //  * @returns 
  //  */
  // public static async Node_SaveWork(workID:number,mainTable:string): Promise<string> {
  //     return request.post<any, any>('/WF/API/Node_SaveWork', null, {
  //       params: {
  //         workID:workID,
  //         mainTable:mainTable,
  //       }
  //     })
  //   }

  /**
   * 保存
   * 例如
   *  {
   *      keyVals: "{\"CheLiangBianHao\": \"100001\",\"CheLiangMingChen\": \"别克\",\"WXYY\": \"保养\",}",
   *      dtlJSON:"{\"ND7201CheLiangPeiJian\":[{\"MingChen\":\"别克\",\"ShuLiang\":\"2\",\"DanJia\":\"150000\",\"BeiZhu\":\"车况正常\",}]}",
   *      athJSON: "{\"AthCLSMS\":[{\"FileName\":\"006.jpg\",\"FileUrl\":\"http://222.190.125.90:8083/zcgl/sys/common/view/2025-01-20/jpg/22b9d1d9-57cc-4f82-8285-d557f442a209/006.jpg\",}]}",
   *      workID: 743, //需要WorkID
   *   },
   * @returns
   */
  public static async Node_SaveWork(workID: number, jsonData: any) {
    return request.post<any, CCFlowResponse<String>>('/WF/API/Node_SaveWork', null, {
      params: {
        workID,
        token: WebUser.Token,
        ...jsonData,
      }
    })
  }

  /**
    * 发送
    * @returns
    */
  public static async Node_SendWork(flowNo: string, workID: number, toNodeID = 0, toEmps = '', checkNote = ''): Promise<any> {
    return request.get<any, CCFlowResponse<String>>('/WF/API/Node_SendWork', {
      params: {
        FlowNo: flowNo,
        WorkID: workID,
        toNodeIDStr: toNodeID,
        ToEmps: toEmps,
        checkNote: checkNote,
      }
    })
  }

  /**
    * 退回
    * @returns
    */
  public static async Node_ReturnWork(workID: number, toNodeID: number, msg: string): Promise<string> {
    return request.get<any, any>('/WF/API/Node_ReturnWork', {
      params: {
        WorkID: workID,
        ToNodeID: toNodeID,
        Msg: msg,
      }
    })
  }

  /**
    * 移交
    * @returnsx
    */
  public static async Node_ShiftWork(workID: number, toEmps: string, msg: string): Promise<string> {
    return request.post<any, any>('/WF/API/Node_Shift', null, {
      params: {
        WorkID: workID,
        toEmpNo: toEmps,
        Msg: msg,
      }
    })
  }

  /**
   * 获得流程信息
   * @param workid 流程实例
   * @returns 
   */
  public static async Flow_GenerWorkFlow(workid: number): Promise<string> {
    return request.get<any, any>('/WF/API/Flow_GenerWorkFlow', {
      params: {
        workID: workid,
      }
    })
  }
  /**
    * 批量删除
    * @returns
    */
  public static async Flow_DeleteFlow(workIDs: string): Promise<string> {
    return request.get<any, any>('/WF/API/Flow_BatchDeleteByFlag', {
      params: {
        workIDs: workIDs,
      }
    })
  }

  /**
   * 设置流程参数
   * @param workID 
   * @param paras 格式: @JinE=100@key1=val1
   * @returns 执行结果.
   */
  public static async Flow_SaveParas(workID: number, paras: string): Promise<string> {
    return request.get<any, any>('/WF/API/Flow_SaveParas', {
      params: {
        workID,
        paras,
      }
    })
  }
  /**
   * 创建草稿
   * @param workID 
   */
  public static Node_SetDraft(workID: number): Promise<string> {
    return request.post<any, any>('/WF/API/Node_SetDraft', null, {
      params: {
        WorkID: workID,
      }
    })
  }

  /**
   * 删除草稿
   * @param workIDs  草稿实例编号,多个用逗号隔开
   * @returns 成功或者失败信息
   */
  public static async Flow_DeleteDraft(workIDs: string): Promise<string> {
    return request.get<any, any>('/WF/API/Flow_DeleteDraft', {
      params: {
        workIDs,
      }
    })
  }

  /**
   * 撤销发送
   * @param workIDs 要执行的实例,多个实例用逗号分开比如：1001,1002,1003
   * @returns 失败返回失败信息
   */
  public static async Flow_DoUnSend(workIDs: string): Promise<string> {
    return request.get<any, any>('/WF/API/Flow_DoUnSend', {
      params: {
        workIDs
      }
    })
  }

  /**
   * 催办
   * @param workIDs 催办的实例
   * @param msg 催办信息
   * @returns
   */
  public static async Flow_DoPress(workIDs: string, msg: string): Promise<string> {
    return request.get<any, any>('/WF/API/Flow_DoPress', {
      params: {
        workIDs,
        msg
      }
    })
  }

  /**
   * 获取批处理
   * @returns 批处理节点
   */
  public static async Batch_Init() {
    return request.get<any, BatchWork[]>('/WF/API/Batch_Init', {
      params: {}
    });
  }

  /**
   * 获取抄送
   * @returns 抄送节点
   * @param  domain 域 非必需
   * @param flowNo 流程编号 非必需
   */
  public static async DB_CCList(domain = '', flowNo = '') {
    return request.get<any, CCWork[]>('/WF/API/DB_CCList', {
      params: {
        domain,
        flowNo,
      }
    })
  }

  /**
   * 查询数据
   * @param key
   * @param dtFrom
   * @param dtTo
   * @param scop
   * @param pageIdx
   * @returns
   */
  public static async Search_Init(key: string, dtFrom: string, dtTo: string, scop: string, pageIdx: number) {
    return request.get<any, SearchWork>('/WF/API/Search_Init', {
      params: {
        key,
        dtFrom,
        dtTo,
        scop,
        pageIdx,
      }
    })
  }
  /**
   * 近期工作
   * @returns 
   */
  public static async Flow_RecentWorkInit() {
    return request.get<any, RecentWork[]>('/WF/API/Flow_RecentWorkInit', { params: {} })
  }

  /**
   * 已完成
   * @returns 
   */
  public static async Complete_Init() {
    return request.get<any, RecentWork[]>('/WF/API/DB_Complete', { params: {} })
  }

  public static async DtlAth_Fill(params: any) {
    return request.post<any, CCFlowResponse<String>>('/WF/API/Node_SaveWork', null, {
      params: {
        ...params,
      }
    })
  }

  // ======================================================== 菜单数据接口. =======================================================
  //发起流程
  public static async DB_Start(domain?: any) {
    return request.get<any, flow[]>('/WF/API/DB_Start', {
      params: {
        domain: domain || ''
      }
    })
  }

  //待办
  public static async DB_Todolist(domain?: any) {
    return request.get<any, GenerWorkFlow[]>('/WF/API/DB_Todolist', {
      params: {
        domain: domain || ''
      }
    })
  }

  //在途
  public static async DB_Runing(domain?: any) {
    return request.get<any, RunningFlow[]>('/WF/API/DB_Runing', {
      params: {
        domain: domain || ''
      }
    })
  }

  /**
   * 获取草稿列表
   * @param domain 流程的域/系统编号
   * @returns 草稿数组
   */
  public static async DB_Draft(domain?: any) {
    return request.get<any, Draft[]>('/WF/API/DB_Draft', {
      params: {
        domain: domain || ''
      }
    })
  }

  // ======================================================== Bill 单据接口. =======================================================
  /**
   * 创建单据实例.
   * @param frmID 单据ID
   * @returns 返回执行结果Int类型的OID.
   */
  public static async Bill_CreateBlankBillID(frmID: string): Promise<number> {
    return request.get<any, any>('/WF/API/Bill_CreateBlankBillID', {
      params: {
        frmID: frmID || ''
      }
    })
  }
  /**
   * 将单据保存草稿.
   * @param frmID 单据ID
   * @returns null
   */
  public static async Bill_SaveAsDraft(workID: number): Promise<number> {
    return request.get<any, any>('/WF/API/Bill_SaveAsDraft', {
      params: {
        workID: workID
      }
    })
  }
  /**
   * 保存单据
   * @param workID 表单实例
   * @param bodyData 主表数据
   * @param dtlJosn 从表数据
   * @param athsJSON 附件数据
   * @returns 执行结果.
   */
  public static async Bill_Save(workID: number, bodyData: string, dtlJosn: string, athsJSON: string): Promise<number> {
    return request.get<any, any>('/WF/API/Bill_SaveAsDraft', {
      params: {
        workID: workID,
        bodyData: bodyData,
        dtlJosn: dtlJosn,
        athsJSON: athsJSON,
      }
    })
  }

  /**
   * 预置审批人
   * 1. 等开始节点的填写人提交审核的时候，系统就会按照这些预置的审批人进行启动审核。
   * 2. 设置的审核人是按照顺序审批。
   * 3. 当前单据的审批模式必须是按照外部的程序调用模式。
   * @param workID 表单实例
   * @param checkEmpNos  要预置的审核人员，比如:zhangsan,lisi,wangwu 多个人员用逗号分开.
   * @returns 执行结果.
   */
  public static async Bill_PreplaceChecker(workID: number, checkEmpNos: string) {
    return request.get<any, any>('/WF/API/Bill_PreplaceChecker', {
      params: {
        workID: workID,
        checkEmpNos: checkEmpNos,
      }
    })
  }

  /**
   * 提交单据：由编辑状态转为归档状态,归档后表单只读.
   * @param workID 流程实例
   * @returns 执行结果
   */
  public static async Bill_SubmitWork(workID: number) {
    return request.get<any, any>('/WF/API/Bill_SubmitWork', {
      params: {
        workID: workID,
      }
    })
  }
  /**
 * 撤销提交: 由单据的归档状态转变为可编辑.
 * @param workID 流程实例
 * @returns 执行结果
 */
  public static async Bill_UnSubmitWork(workID: number) {
    return request.get<any, any>('/WF/API/Bill_UnSubmitWork', {
      params: {
        workID: workID,
      }
    })
  }

  /**
  * 初始化单据的审核人
  * 1. 如果单据需要审批，审批之前需要初始化审批人.
  * 2. 执行后，该单据的状态转化为审批中.
  * @param workID 流程实例
  * @param empNosOfChecker 可以审批的人员,格式:zhangsan,lisi,wangwu
  * @returns 执行结果
  */
  public static async Bill_CheckerInit(workID: number, empNosOfChecker: string) {
    return request.get<any, any>('/WF/API/Bill_CheckerInit', {
      params: {
        workID: workID,
        empNosOfChecker: empNosOfChecker,
      }
    })
  }


}

