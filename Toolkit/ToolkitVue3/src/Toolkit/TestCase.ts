import { message } from "ant-design-vue";
import WebUser from "./WebUser";
import Dev2UrlInterface from "./Dev2UrlInterface";
import Dev2ApiInterface from "./Dev2ApiInterface";
import  Events  from '@/utils/Events';

//学生科目s
export class TestCase {
  public static async CurrentWebUser() {

    const no = WebUser.No; //登陆账号
    const name = WebUser.Name; //登陆名称.
    const deptNo = WebUser.DeptNo; //登陆部门编号.
    const deptName = WebUser.DeptName; //登陆部门名称.
    const orgNo = WebUser.OrgNo; //登陆组织编号.
    const orgName = WebUser.OrgName; //登陆组织名称.
    const val = ` 登录帐号:${WebUser.No} ,名称${WebUser.Name} 
     部门编号
     `;
    alert('执行:TestCase的方法:CurrentWebUser' + val);
    // //获取登陆人员的角色.
    // const deptEmpStations = new DeptEmpStations();
    // deptEmpStations.Retrieve('FK_Emp', WebUser.No);
    // let staNos = ''; //角色编号.
    // let staNames = ''; //角色名称.
    // for (let index = 0; index < deptEmpStations.length; index++) {
    //   const element = deptEmpStations[index];
    //   staNos += ',' + element.FK_Station;

    //   const station = new Station(element.FK_Station);
    //   station.RetrieveFromDBSources();
    //   staNames += ',' + station.Name;
    // }
  }

  //执行一个流程的简单发送.
  public static async Bill_Tester() {

    // const workID = ;

    const flowNo = prompt('请输入流程编号', '001');
    const msg = '您确定要发起流程[' + flowNo + ']吗？';
    if (window.confirm(msg) == false)
      return;
    //0. 生成流程实例ID.
    //1.执行保存草稿.
    alert('已经保存草稿');

    //2.向流程引擎写入操作

    //3. 流程发起成功.
  }

  /**
   * 演示目的：
   * 1. 用参数来控制节点的运转.
   */
  public static async FlowAPI_TurnTo_Demo(){
    // const flowNo = '103';
    const flowNo = '082';   //ke数据库
    //数据滞空
    let turnToMsg = ''
    localStorage.setItem('turnToMsg', turnToMsg);
    Events.emit('turn-to-demo');
    const workID = await Dev2ApiInterface.Node_CreateBlank(flowNo);
    //1、
    turnToMsg += `<p>1、WorkID:${workID},创建成功</p>`
    localStorage.setItem('turnToMsg', turnToMsg);
    Events.emit('turn-to-demo');
    // 发送到下一个节点》
    await Dev2ApiInterface.Node_SendWork(flowNo, workID, 0, 'admin').then((res: any) => {
      console.log('Node_SendWork', res);
      message.success(res.data);
      //2、
      turnToMsg += `<p>2、${res.data}</p>`
      localStorage.setItem('turnToMsg', turnToMsg);
      Events.emit('turn-to-demo');
    })

    //获得参数.
    const JE = prompt('请输入金额')
    //写入参数.
    //设置流程变量:这些变量用于控制方向条件，接受人.
    const flowParas = `@JE=${JE}`;
    await Dev2ApiInterface.Flow_SaveParas(workID, flowParas).then((res: any) => {
      console.log('res', res);
      message.success(res.message);
      //3、
      turnToMsg += `<p>3、${res.message}</p>`
      localStorage.setItem('turnToMsg', turnToMsg);
      Events.emit('turn-to-demo');
    });

    // 执行发送》
    await Dev2ApiInterface.Node_SendWork(flowNo, workID, 0, 'admin', '金额为:' + JE).then((res: any) => {
      console.log('Node_SendWork', res);
      message.success(res.data);
      //4、
      turnToMsg += `<p>4、${res.data}</p>`
      localStorage.setItem('turnToMsg', turnToMsg);
      Events.emit('turn-to-demo');
    })

    message.info('已经实现根据参数自动转向，到达下一个节点.');
    //执行删除.
    await Dev2ApiInterface.Flow_DeleteFlow(workID.toString()).then((res: any) => {
      console.log('Flow_DeleteFlow', res);
      message.success(res.message);
      //5、
      turnToMsg += `<p>5、${res.message}</p>`
      localStorage.setItem('turnToMsg', turnToMsg);
      Events.emit('turn-to-demo');
    });
  }

  //流程API的demo操作.
  public static async FlowAPI_Demo() {
    //创建workID.
    const flowNo = '085';
    //数据滞空
    let apiDemoMsg = ''
    localStorage.setItem('apiDemoMsg', apiDemoMsg);
    Events.emit('api-demo');

    const workID = await Dev2ApiInterface.Node_CreateBlank(flowNo);
    console.log('workID', workID);
    //1、
    apiDemoMsg += `<p>1、WorkID:${workID},创建成功</p>`
    localStorage.setItem('apiDemoMsg', apiDemoMsg);
    Events.emit('api-demo');


    message.success('WorkID:' + workID + ',创建成功');

    //设置草稿,标识该workID已经被占用.
    await Dev2ApiInterface.Node_SetDraft(workID);
    message.success('草稿保存成功');
    //2、
    apiDemoMsg += `<p>2、草稿保存成功</p>`
    localStorage.setItem('apiDemoMsg', apiDemoMsg);
    Events.emit('api-demo');
    //设置主表数据:
    // const frmData = `@Tel=18660153393@Addr=济南高新区@Email=ccbpm#@ccbmp.cn`;
    const frmData = {
      keyVals: `@Tel=18660153393@Addr=济南高新区@Email=ccbpm#@ccbmp.cn`,
    }

    const jsonData = JSON.parse(JSON.stringify(frmData))
    await Dev2ApiInterface.Node_SaveWork(workID, jsonData).then((res: any) => {
      message.success(res.message);
      //3、
      apiDemoMsg += `<p>3、${res.message}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    });

    //设置流程变量:这些变量用于控制方向条件，接受人.
    const flowParas = `@JE=1000.00@QingJiaTianShu=9@PrjType=1`;
    await Dev2ApiInterface.Flow_SaveParas(workID, flowParas).then((res: any) => {
      message.success(res.message);
      //4、
      apiDemoMsg += `<p>4、${res.message}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    });

    // 发送到下一个节点1: 直接发送
    // const msg1 = await Dev2ApiInterface.Node_SendWork(flowNo, workID);
    // message.info(msg1);

    // //发送到下一个节点2: 发送到指定的节点
    // const msg2 = await Dev2ApiInterface.Node_SendWork(flowNo, workID, 105);
    // message.info(msg2);

    //发送到下一个节点3: 发送到指定的节点指定的人.
    await Dev2ApiInterface.Node_SendWork(flowNo, workID, 8502, 'admin').then((res: any) => {
      console.log('Node_SendWork', res);
      message.success(res.data);
      //5、
      apiDemoMsg += `<p>5、${res.data}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    })


    //获得流程当前信息,查看当前的节点.
    await Dev2ApiInterface.Flow_GenerWorkFlow(workID).then((res: any) => {
      console.log('Flow_GenerWorkFlow', res);
      message.success('获得流程当前信息：' + JSON.parse(JSON.stringify(res)));
      //6、
      apiDemoMsg += `<p>
            6、获得流程当前信息：
              <pre>
                ${JSON.stringify(res)}
              </pre>
            </p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    });

    //执行撤销.
    await Dev2ApiInterface.Flow_DoUnSend(workID.toString()).then((res: any) => {
      console.log('Flow_DoUnSend', res);
      message.success(res.message);
      //7、
      apiDemoMsg += `<p>7、${res.message}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    });

    //再次执行发送.
    await Dev2ApiInterface.Node_SendWork(flowNo, workID, 8502, 'admin').then((res: any) => {
      console.log('Node_SendWork', res);
      message.success(res.data);
      //8、
      apiDemoMsg += `<p>8、${res.data}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    })

    //退回操作,退回开始节点
    await Dev2ApiInterface.Node_ReturnWork(workID, 8501, '报销内容有误...').then((res: any) => {
      console.log('Node_ReturnWork', res);
      message.success(res.message);
      //9、
      apiDemoMsg += `<p>9、${res.message}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    });

    //执行移交.
    await Dev2ApiInterface.Node_ShiftWork(workID, 'yuwen', '同事已经离职，把工作已交给您.').then((res: any) => {
      console.log('Node_ShiftWork', res);
      message.success(res.data);
      //10、
      apiDemoMsg += `<p>10、${res.data}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    });

    //查看流程轨迹 -调用功能页面API.
    const url = await Dev2UrlInterface.Flow_MyView(workID);
    message.info(url);
    //11、
    apiDemoMsg += `<p>11、${url}</p>`
    localStorage.setItem('apiDemoMsg', apiDemoMsg);
    Events.emit('api-demo');

    //执行删除.
    await Dev2ApiInterface.Flow_DeleteFlow(workID.toString()).then((res: any) => {
      console.log('Flow_DeleteFlow', res);
      message.success(res.message);
      //12、
      apiDemoMsg += `<p>12、${res.message}</p>`
      localStorage.setItem('apiDemoMsg', apiDemoMsg);
      Events.emit('api-demo');
    });

  }
}
