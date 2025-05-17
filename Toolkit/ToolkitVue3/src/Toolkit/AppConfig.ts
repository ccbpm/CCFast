import request from '@/utils/request'
import type { BatchWork, CCFlowResponse, CCWork, Draft, GenerWorkFlow, RecentWork, RunningFlow, SearchWork, flow, } from './types'

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
export class AppConfig {
  // 应用中心服务器地址  前台地址
  public static  AppCenterHost='http://vue3.ccbpm.cn';
  //BPM服务器地址 后台地址
  public static  AppHost='http://vue3.ccflow.org';
  //系统编号
  public static  Domain='';

  //系统编号.
  public static  AppNo='RuoYi';
  //系统名称.
  public static  AppName='若依';

  
  // ############## 以下不要变更.
  // # .net处理器
  public static GLOB_CCFLOW_HANDLER = 'WF/Comm/Handler.ashx';
  // # Java处理器
  public static GLOB_JFLOW_HANDLER = 'WF/Comm/ProcessRequest.do';
}

