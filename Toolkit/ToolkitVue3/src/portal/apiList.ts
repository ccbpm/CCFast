import { reactive } from 'vue';
import Dev2ApiInterface from '@/Toolkit/Dev2ApiInterface';
import Dev2UrlInterface from '@/Toolkit/Dev2UrlInterface';

interface ApiList{
    key: number,
    Name: string,
    children: children[],
}
interface children{
    key: number,
    Name: String,
    Docs: String,
    Interface: any,
    Port:String,
    Enable: number,
}
const apiList = reactive<ApiList[]>([
  {
    key: 1,
    Name: '登录信息',
    children: [
      {
        key: 101,
        Name: '登录信息',
        Docs: '在登录的时候调用Dev2ApiInterface.Port_Login',
        Interface: Dev2ApiInterface.Port_Login,
        Port:'Dev2ApiInterface.Port_Login',
        Enable: 1,
      },
    ],
  },
  {
    key:2,
    Name: '菜单接口',
    children: [
      {
        key: 201,
        Name: '发起',
        Docs: '当前登录人员可以发起的流程,Dev2ApiInterface.DB_Start',
        Interface:Dev2ApiInterface.DB_Start,
        Port:'Dev2ApiInterface.DB_Start',
        Enable: 1,
      },
      {
        key: 202,
        Name: '待办',
        Docs: '当前登录人员的待办.Dev2ApiInterface.DB_Todolist',
        Interface:Dev2ApiInterface.DB_Todolist,
        Port:'Dev2ApiInterface.DB_Todolist',
        Enable: 1,
      },
      {
        key: 203,
        Name: '在途',
        Docs: '当前登录人员参与的但是没有结束的流程.Dev2ApiInterface.DB_Runing',
        Interface:Dev2ApiInterface.DB_Runing,
        Port:'Dev2ApiInterface.DB_Runing',
        Enable: 1,
      },
      {
        key: 204,
        Name: '抄送',
        Docs: '抄送给当前登录人员的工作,Dev2ApiInterface.DB_CCList',
        Interface:Dev2ApiInterface.DB_CCList,
        Port:'Dev2ApiInterface.DB_CCList',
        Enable: 1,
      },
      {
        key: 205,
        Name: '近期',
        Docs: '近期处理的工作.Flow_RecentWorkInit',
        Interface:Dev2ApiInterface.Flow_RecentWorkInit,
        Port:'Dev2ApiInterface.Flow_RecentWorkInit',
        Enable: 1,
      },
      {
        key: 206,
        Name: '已完成',
        Docs: '最近已经完成的工作.Dev2ApiInterface.Complete_Init',
        Interface:Dev2ApiInterface.Complete_Init,
        Port:'Dev2ApiInterface.Complete_Init',
        Enable: 1,
      },
    ],
  },
  // {
  //   key: 3,
  //   Name: '流程接口(依次执行)',
  //   children: [
  //     {
  //       key: 301,
  //       Name: '创建WorkID(流程实例ID)',
  //       Docs: '创建一个实例,宿主类型的流程,请参考合同审批事例. Dev2ApiInterface.Node_CreateBlank',
  //       Interface:Dev2ApiInterface.Node_CreateBlank,
  //       Enable: 1,
  //     },
  //     {
  //       key: 302,
  //       Name: '节点保存',
  //       Docs: '根据workid把当前实体的数据保存到开始节点表单里,Dev2ApiInterface.Node_SaveWork',
  //       Interface:Dev2ApiInterface.Node_SaveWork,
  //       Enable: 1,
  //     },
  //     {
  //       key: 303,
  //       Name: '节点发送',
  //       Docs: '执行发送动作,Dev2ApiInterface.Node_SendWork',
  //       Interface:Dev2ApiInterface.Node_SendWork,
  //       Enable: 1,
  //     },
  //     {
  //       key: 304,
  //       Name: '节点退回',
  //       Docs: '执行退回接口Dev2ApiInterface.Node_ReturnWork',
  //       Interface:Dev2ApiInterface.Node_ReturnWork,
  //       Enable: 1,
  //     },
  //     {
  //       key: 305,
  //       Name: '节点移交',
  //       Docs: '根据workid执行移交 Dev2ApiInterface Node_ShiftWork',
  //       Interface:Dev2ApiInterface.Node_ShiftWork,
  //       Enable: 1,
  //     },
  //     {
  //       key: 307,
  //       Name: '流程删除',
  //       Docs: '根据workid删除流程,Dev2ApiInterface.Flow_DeleteFlow',
  //       Interface:Dev2ApiInterface.Flow_DeleteFlow,
  //       Enable: 1,
  //     },
  //   ],
  // },
  {
    key: 4,
    Name: '功能页面接口',
    children: [
      {
        key: 401,
        Name: '单流程发起',
        Docs: '单流程发起(MyFlow)链接 Dev2UrlInterface.Flow_MyFlowByFlowNo',
        Interface:Dev2UrlInterface.Flow_MyFlowByFlowNo,
        Port:'Dev2UrlInterface.Flow_MyFlowByFlowNo',
        Enable: 1,
      },
      {
        key: 402,
        Name: '单流程查看',
        Docs: '单流程查看链接(MyView) Dev2UrlInterface.Flow_MyView',
        Interface:Dev2UrlInterface.Flow_MyView,
        Port:'Dev2UrlInterface.Flow_MyView',
        Enable: 1,
      },
      {
        key: 403,
        Name: '单流程抄送',
        Docs: '单流程抄送链接(MyCC) Dev2UrlInterface.Flow_MyCC',
        Interface:Dev2UrlInterface.Flow_MyCC,
        Port:'Dev2UrlInterface.Flow_MyCC',
        Enable: 1,
      },
      {
        key: 404,
        Name: '设计单个流程',
        Docs: '设计单个流程链接(Admin_Flow_One) Dev2UrlInterface.Admin_Flow_One',
        Interface:Dev2UrlInterface.Admin_Flow_One,
        Port:'Dev2UrlInterface.Admin_Flow_One',
        Enable: 1,
      },
      {
        key: 405,
        Name: '设计单个表单',
        Docs: '设计单个表单链接(Admin_Frm_One) Dev2UrlInterface.Admin_Frm_One',
        Interface:Dev2UrlInterface.Admin_Frm_One,
        Port:'Dev2UrlInterface.Admin_Frm_One',
        Enable: 1,
      },
      {
        key: 406,
        Name: '轨迹',
        Docs: '流程轨迹链接(Track) Dev2UrlInterface.Flow_Track',
        Interface:Dev2UrlInterface.Flow_Track,
        Port:'Dev2UrlInterface.Flow_Track',
        Enable: 1,
      },
    ],
  },
]);

export default apiList;
