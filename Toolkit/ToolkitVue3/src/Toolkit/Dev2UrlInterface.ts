import { useUserStore, type LoginParams, type User } from '@/stores/user';
import { AppConfig } from './AppConfig';
import WebUser from './WebUser';

/**
 * 说明:
 * 1. 该类是一个接口文件需要引入到自己的前端vue项目中去.
 * 10. 更多的帮助: 请参考: https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=8095471&doc_id=31094
 */
export default class Dev2UrlInterface {
  /*************************************  管理员接口  ******************************/
  /**
   * 流程设计
   * @returns url
   */
  public static async Menu_Admin_Flows() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=TreeEns_FlowSort2Flow&token=${WebUser.Token}`;
    return url;
  }
   /**
   *流程设计器:设计单个流程
   * @param flowNo 流程编号
   * @returns
   */
   public static  Admin_Flow_One(flowNo: any) {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#WF/Port?DoWhat=FlowDesign&FlowNo=${flowNo}&token=${WebUser.Token}`;
    // http://localhost:3009/#/WF/Port?DoWhat=FlowDesign&FlowNo=220&Token=477c784459444e349a1239248fd6ddd0
    return url;
  }
 
  /**
   * 表单设计
   * @returns url
   */
  public static async Menu_Admin_Frms() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=TreeEns_FrmSort2Frm&token=${WebUser.Token}`;
    return url;
  }
  /**
   *表单设计器:设计单个表单
   * @param frmID 表单ID.
   *@returns
   */
   public static Admin_Frm_One(frmID: any) {
    const host = AppConfig.AppCenterHost
    // http://localhost:3009/#/WF/Port?DoWhat=FrmDesign&FrmID=En_F002&Token=477c784459444e349a1239248fd6ddd0
    const url = `${host}/#WF/Port?DoWhat=FrmDesign&FrmID=${frmID}&token=${WebUser.Token}`;
    
    return url;
  }
  /**
   * 白色大屏设计
   * @param rptID 表单ID. @llj
   * @returns 
   */
  public static async Menu_Admin_RptWhite(rptID:string) {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=${rptID}&token=${WebUser.Token}`;
    return url;
  }
  /**
   * 蓝色大屏设计
   * @param rptID 报表ID
   * @returns 设计的url.
   */
  public static async Menu_Admin_RptBlue(rptID:string) {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=${rptID}&token=${WebUser.Token}`;
    return url;
  }
  /**
   * 组织结构设计
   * @returns url
   */
  public static async Menu_Admin_Orgs() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=TreeEns_Dept2Emp&token=${WebUser.Token}`;
    return url;
  }
  /**
   * 数据源
   * @returns url
   */
  public static async Menu_Admin_DBSrc() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=TreeEns_DBSrc&token=${WebUser.Token}`;
    return url;
  }
  /*********************** 菜单接口************************************* */
  //发起
  public static async Menu_Flow_Start() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_Start&token=${WebUser.Token}`;
    return url;
  }
  //待办
  public static async Menu_Flow_Todolist() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_Todolist&token=${WebUser.Token}`;
    return url;
  }
  //在途
  public static async Menu_Flow_Runing() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_Runing&token=${WebUser.Token}`;
    return url;
  }
  //近期
  public static async Menu_Flow_Nearly() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_RecentWork&token=${WebUser.Token}`;
    return url;
  }
  //已完成
  public static async Menu_Flow_Complate() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_Complete&token=${WebUser.Token}`;
    return url;
  }
  //抄送
  public static async Menu_Flow_CC() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_CC&token=${WebUser.Token}`;
    return url;
  }
  //草稿
  public static async Menu_Flow_Draft() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_Draft&token=${WebUser.Token}`;
    return url;
  }
  //消息
  public static async Menu_Flow_Msg() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GL_Msg&token=${WebUser.Token}`;
    return url;
  }
  //综合查询
  public static async Menu_Flow_Search() {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=EntitySearch&EnName=TS.FlowData.GenerWorkFlowView&token=${WebUser.Token}`;
    return url;
  }

  //我的设置菜单
  public static async MySettingMenu(){
    const host = AppConfig.AppCenterHost
    const url =  `${host}/#/WF/Port?DoWhat=En&EnName=TS.Port.MySetting&PKVal=${WebUser.No}&token=${WebUser.Token}`
    return url;
  }

  // ======================================================== 流程页面接口. =======================================================
  /**
   * 工作处理器
   * @param workID 工作ID,如果WorkID是0, 则会自动创建一个新的WorkID.
   * @param paras 可选参数：可以向表单传入的参数,格式：&Tel=18660153393&Addr=山东济南.
   * @returns 直接打开MyFlow工作处理器的url.
   */
  public static Flow_MyFlowByFlowNo(flowNo: string, paras = '') {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=StartFlow&FlowNo=${flowNo}${paras}&token=${WebUser.Token}`;
    return url;
  }
  public static Flow_MyFlow(workID: string, paras = '') {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=StartFlow&WorkID=${workID}${paras}&token=${WebUser.Token}`;
    return url;
  }
  /**
   * 工作查看器
   * @param workID 工作ID,如果WorkID是0, 则会自动创建一个新的WorkID.
   * @returns 直接打开MyView工作查看器的url.
   */
  public static Flow_MyView(workID: number) {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=MyView&WorkID=${workID}&token=${WebUser.Token}`;
    return url;
  }
  /**
   * 抄送查看器
   * @param workID 工作ID,如果WorkID是0, 则会自动创建一个新的WorkID.
   * @returns 直接打开MyCC工作查看器的url.
   */
  public static Flow_MyCC(flowNo: number, workID: number) {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=MyCC&FlowNo=${flowNo}&WorkID=${workID}&token=${WebUser.Token}`;
    return url;
  }
  public static Flow_Track(flowNo: number, workID: number) {
    // http://localhost:3009/#/WF/Port?DoWhat=Vue3Track&FK_Flow=114&WorkID=1888671138&Token=eff9ed6b38934e08a5ab1b595e26f909
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=Vue3Track&FK_Flow=${flowNo}&WorkID=${workID}&token=${WebUser.Token}`;
    return url;
  }

  public static Flow_Search(flowNo: string) {
    // http://localhost:3009/#/WF/Port?DoWhat=Vue3Track&FK_Flow=114&WorkID=1888671138&Token=eff9ed6b38934e08a5ab1b595e26f909
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=SearchFlow&FlowNo=${flowNo}&token=${WebUser.Token}`;
    return url;
  }

  /*************************************  实体单据接口  ******************************/
  /**
   * 单据列表
   * @param frmID 单据ID
   * @param paras  查询参数, 比如: &Key=123
   * @returns 单据查询的url.
   */
  public static async Bill_Search(frmID: string, paras: string) {
    const url = `${AppConfig.AppCenterHost}/#/WF/Port?DoWhat=Vue3Track&FK_Flow=${frmID}${paras}&token=${WebUser.Token}`;
    return url;
  }
  /**
   * 单据分析
   * @param frmID 单据ID
   * @param paras  查询参数, 比如: &Key=123
   * @returns 单据查询的url.
   */
  public static async Bill_Group(frmID: string, paras: string) {
    const url = `${AppConfig.AppCenterHost}/#/WF/Port?DoWhat=Vue3Track&FK_Flow=${frmID}${paras}&token=${WebUser.Token}`;
    return url;
  }
  /**
  * 新建单据实例
  * @param frmID 单据ID
  * @param paras 查询参数, 比如: &Addr=山东济南&Tel=18660153393
  * @returns 新建单据的url.
  */
  public static async Bill_NewBill(frmID: string, paras: string) {
    const url = `${AppConfig.AppCenterHost}/#/WF/Port?DoWhat=Vue3Track&FK_Flow=${frmID}${paras}&token=${WebUser.Token}`;
    return url;
  }
  /**
  * 单据卡片信息
  * @param workID 流程实例ID
  * @param frmID 单据ID
  * @returns 卡片信息url.
  */
  public static async Bill_MyBill(workID: number, frmID: string, param: string) {
    const url = `${AppConfig.AppCenterHost}/#/WF/Port?DoWhat=Vue3Track&FK_Flow=${frmID}${param}&token=${WebUser.Token}`;
    return url;
  }

}

