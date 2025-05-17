import request from '@/utils/request';
import { AppConfig } from './AppConfig';
import WebUser from './WebUser';
import type { CCFlowResponse } from '@/types/interfaceType';
import Dev2UrlInterface from './Dev2UrlInterface';

/**
 * 说明:
 * 1. 该接口是一个为其他系统引入使用ccfast的功能菜单提供的菜单接口
 * 10. 更多的帮助: 请参考:https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=13933053&doc_id=31094
 */
export default class Dev2CCFastInterface {
  
  /**
   * 新建菜单
   * @param sortNo 模块编号
   * @param sortName 模块名称
   * @returns 菜单工具箱.
   */
  public static Open_GPN_Menus(sortNo: string,sortName:string) {

    const appNo= AppConfig.AppNo;
    const appName= AppConfig.AppName;
    //处理模块类别编号，是否存在，不存在就创建一个.
     request.get<any, CCFlowResponse<String>>('/WF/API/CCFast_Mouldle_Init', {
      params: {
        appNo: appNo,
        appName: appName,
        sortNo: sortNo,
        sortName: sortName,
      }
    })

    //返回菜单连接.
    const host = AppConfig.AppCenterHost;
    const url = `${host}/#/WF/Port?DoWhat=Component&EnName=GPN_Menu&SortNo=${sortNo}&SortName=${sortName}&token=${WebUser.Token}`;
    return url;
  }
  /**
   * 获得菜单信息
   * @param mypk 菜单主键.
   * @returns 菜单实体信息json.
   */
  public static GenerMenuInfo(mypk: string):Promise<any> {
    return request.get<any, CCFlowResponse<String>>('/WF/API/CCFast_MenuInfo', {
      params: {
        menuMyPK: mypk,
      }
    })
  }
  
  /**
   * 获得模块的最后一个加入菜单
   * @param sortNo 模块编号.
   * @returns 返回: @MenuNo=xxx@MenuName=yyyy 或者为空.
   */
    public static GenerLastMenuInfo(sortNo: string):Promise<any> {
      return request.get<any, CCFlowResponse<String>>('/WF/API/CCFast_LastOneMenu', {
        params: {
          menuMyPK: sortNo,
        }
      })
    }
  /**
   * 删除菜单
   * @param myPK 主键
   * @returns 删除菜单.
   */
  public static Menu_Delete(myPK: string):Promise<any> {
    return request.get<any, CCFlowResponse<String>>('/WF/API/CCFast_MenuDelete', {
      params: {
        menuMyPK: myPK,
      }
    })
  }

  /**
   * 设计菜单
   * @param myPK 
   * @returns 返回设计的MyPK,Name字段.
   */
  public static Menu_Designer_Url(myPK: string) {
    const menuInfo= Dev2CCFastInterface.GenerMenuInfo(myPK);
    const menuType=menuInfo.MenuModel ; //'RefFlow'; //菜单类型.
    //如果是流程.
    if (menuType=='RefFlow')
    {
      const flowNo=menuInfo.FlowNo; //获得流程编号.
      return Dev2UrlInterface.Menu_Admin_Flow(flowNo);
      return;
    }

     //如果是流程.
     if (menuType=='Bill' ||  menuType=='EntityNoName' )
      {
        const frmID=menuInfo.FrmID; //获得流程编号.
        return Dev2UrlInterface.Menu_Admin_Frm(frmID);
      }
 
      // 大屏.
      if (menuType=='RptWhite')
        return Dev2UrlInterface.Menu_Admin_RptWhite(myPK);
      if (menuType=='RptBlue')
          return Dev2UrlInterface.Menu_Admin_RptBlue(myPK);

      alert('没有解析的类型:'+menuType);
  }

  /**
   * 运行菜单
   * @param myPK 菜单mypk
   * @returns 
   */
  public static Menu_Runing_Url(myPK: string) {
    const host = AppConfig.AppCenterHost
    const url = `${host}/#/WF/Port?DoWhat=CCFastMenu&MyPK=${myPK}&token=${WebUser.Token}`;
    return url;
  }
}

