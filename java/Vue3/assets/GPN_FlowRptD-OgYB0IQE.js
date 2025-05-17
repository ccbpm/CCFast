var p=Object.defineProperty;var m=(n,r,t)=>r in n?p(n,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):n[r]=t;var e=(n,r,t)=>m(n,typeof r!="symbol"?r+"":r,t);var i=(n,r,t)=>new Promise((u,s)=>{var l=o=>{try{a(t.next(o))}catch(d){s(d)}},c=o=>{try{a(t.throw(o))}catch(d){s(d)}},a=o=>o.done?u(o.value):Promise.resolve(o.value).then(l,c);a((t=t.apply(n,r)).next())});import{b5 as g}from"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class I extends g{constructor(){super("GPN_FlowRptD");e(this,"HelpFlow",`
  #### 帮助
  - 如何把流程绑定到您的业务系统中？
  - 如何在一行记录上启动流程、传入数据？
  - 如何把自己的表单绑定到ccbpm的流程引擎中去然后让流程启动起来？
  - 如何把业务数据作为参数传入ccbpm流程引擎中然后控制流程运转作为流程的接受人，控制流程的运转方向条件？
  - 如何根据流程实例的ID, 查看流程运行的情况？
  - 请参考 https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=8488019&doc_id=31094
  `);e(this,"HelpFEE",`
  #### 帮助
  - FEE 是流程事件代码类的简称.
  - 它是从一个指定的基类上继承下来实现，重写流程事件的方法，
  - 流程在运动过程中有许多事件，在事件中插入代码逻辑实现，与其他系统交互实现用户的需求。
  - 比如：在流程的发送前、发送成功后、发送失败后、流程结束后、等事件执行的相关脚本.这些脚本实现业务逻辑于外部系统交互.
  - 实现与ccbpm交互载体之一.
  #### 注意事项
  - 对于java: 代码类必须在bp的开头的包名里.
  - 对于.net: 代码必须放在BP.*.dll的文件里, 明明必须以BP.开头，比如: BP.XXXX.XXX
  #### 关于事件
  - 可以重写节点事件、流程事件.
  - 节点事件比如:发送前、发送成功时、发送失败时、退回前、退回后、撤销前、撤销后、移交前、移交后.
  - 流程事件比如:流程结束前、流程结束后、流程删除前、流程删除后.
  `);e(this,"RptHelp",`
  #### 查询帮助
  - 对流程的查询分为:流程综合查询与单流程查询两部分,两者查询的数据源不同，展示的内容也不同这里做分别介绍.
  #### 单流程查询
  - 单流程的查询:是对单个流程的业务数据表的查询(对NDxxxRpt表的查询),
  - 比如:请假日期从,到,请假类型,标题等业务字段查询,业务字段是可以定义的.
  - 我们使用字段定义工具定义可以查询的列.
  #### 流程综合查询
  - 是对流程引擎的注册表的查询(对WF_GenerWorkFlow表的查询),
  - 查询的数据列是固定的,发起人、发起日期、标题、状态、停留节点、当前处理人等.
  `);e(this,"GroupDesc",`
  #### 帮助
  - 对数据生成饼图、柱状图、折线图的分析.
  - 开发中,敬请等待.
  `);e(this,"APIDesc",`
  #### 帮助
  - 页面功能API:是指把页面功能做成一个独立的api可以被应用系统调用.
  - 如果您想把流程的数据页面绑定到您的菜单上去,就把url地址绑定.
  #### API接口
   - 查询API
   - http://ccbpm.cn/Port?Token=xxx&DoWhat=FlowSearch&FlowNo=流程模板编号
   - 分组API
  - http://ccbpm.cn/Port?Token=xxx&DoWhat=FlowGroup&FlowNo=流程模板编号
  `);e(this,"Dev_Url",`
  #### API

#### URL调用接口
1. ccbpm提供页面级的功能组件，这些功能组件在/WF/下面。
2. 比如：发起、待办、抄送、查询。
3. 这些功能可以以明文的方式传输调用。
4. 他的安全性是需要系统调用登录方法才可以，调用以上的URL。
5. 如何调用登录方法，请参考下面的API。
#### 菜单列表
1. 流程发起: **/WF/Start.htm** 获得当前操作员的流程发起的列表，每个操作员的权限不同能发起的流程列表也不同。
2. 工作待办:**/WF/Todolist.htm** 获得当前操作员的所有的待办列表，**/WF/Todolist.htm?FK_Flow=002** 当前操作员的指定流程的待办。
3. 在途:**/WF/Runing.htm** 当前工作人员的所有在途,**/WF/Runing.htm?FK_Flow =002**当前操作员的指定流程的在途。在途定义：一个操作员的参与的流程，但是流程还没有完成，就叫在途。
4. 抄送: **/WF/CC.htm** 抄送来的工作，当前人员没有处理权限，但是可以查看。
5. 查询: **/WF/Search.htm** 对完成或者未完成的流程进行查询。

#### 流程请假流程-极简模式发起
1. 发起URL: **/WF/MyFlow.htm?FK_Flow=002**，您可以把该URL 放入到自己的系统菜单里，或者列表里。
2. 该页面组件名叫：“工作处理器”，该处理器可以接受很多参数，可以向工作处理器里传入很多参数，格式与约定请参考说明书。

#### 工作待办
1. 当前流程工作待办:**/WF/Todolist.htm?FK_Flow=002**
2. 所有流程工作待办: **/WF/Todolist.htm**

#### 在途工作(也称为未完成)
1. 当前流程工作在途: **/WF/Runing.htm?FK_Flow= 002**
2. 所有流程工作在途: **/WF/Runing.htm**

`);e(this,"Dev_SDK",`
  ####  API

  #### 登录与门户API
  1. 首先要进行代码集成与组织机构的集成。
  2. 其次在自己的系统登录界面，登录成功后要执行ccbpm的框架登录。
  3. 所谓的登录就是调用ccbpm的登录接口，如左边的代码所示。

<pre>
// 如下代码需要写入您的系统校验密码与用户名之后。
string userNo = "zhangsan";
BP.WF.Dev2Interface.Port_Login(userNo);
</pre>

#### 菜单

1. 发起：一个操作员可以发起的工作

2. 待办：等待处理的工作。

3. 在途：我参与的，但是这条流程还没有结束的流程。

4. 抄送：不需要我处理，但是需要我知晓的工作。

  ##### 发起:

<pre>
//获得指定人员的可以发起的流程列表,调用这个接口返回一个datatable, 可以参考一个demo实现发起列表的输出。
System.Data.DataTable dtStart = BP.WF.Dev2Interface.DB_GenerCanStartFlowsOfDataTable("zhangsan");
</pre>

  ##### 待办：

<pre>
//获得指定人员的待办,调用这个接口返回一个datatable, 可以参考一个demo实现发起列表的输出。
DataTable dtTodolist = BP.WF.Dev2Interface.DB_GenerEmpWorksOfDataTable();
</pre>
  ##### 在途：

<pre>
//获得指定人员的在途,调用这个接口返回一个datatable ，代码参考：。
DataTable dtRuning = BP.WF.Dev2Interface.DB_GenerRuning();
</pre>

  ##### 查询：

<pre>
//ccbpm给你提供了一个link ，您可以调用这个link ,也可以自己去根据代码实现。
实现列表输出代码，请参考:
运行Demo: <a href=''>查询</a>
</pre>

#### 创建WorkID

1. 创建工作ID是启动流程的开始。

2. ccbpm的工作ID是一个Int64位的整数，始终是按照序号+1产生的。

3. 该workid全局唯一，并且没有重复性，该信息记录到Sys_Serial，WorkID的生成从100开始。

4. 该workid全局唯一，并且没有重复性，该信息记录到Sys_Serial，WorkID的生成从100开始。
  
<pre>
//传入流程编号，调用创建一个工作ID。
Int64 workid = BP.WF. Dev2Interface.Node_CreateBlankWork("001");
</pre>

#### 发送 - 简单发送

1. 工作发送就是让节点向下运动。

2. 调用接口执行发送后，返回一个执行结果的对象，该对象是流程引擎执行过程中的变量。

3. 解析该变量，可以检查出流程是否完成，运行到那一个节点上去了，下一个节点谁可以处理工作？

4. 它的流向，是根据流程设计的规则执行的。

5. 它的接收人，是根据接受人的规则确定的。

<pre>
//传入流程编号, WorkID执行发送.
BP.WF.SendReturnObjs objs= BP.WF.Dev2Interface.Node_SendWork("001",workid);

// 检查流程是否结束？
bool isFlowOver = objs.IsStopFlow;

// 获得发送到那个节点上去了？
int toNodeID = objs.VarToNodeID;
string toNodeName = objs.VarToNodeName;

// 获得发送给谁了？ 注意：这里如果是多个接受人员就会使用逗号分开。
string toEmpID = objs.VarAcceptersID;
string toEmpName = objs.VarAcceptersName;

// 输出提示信息, 这个信息可以提示给操作员.
string infoMsg = objs.ToMsgOfHtml();
</pre>

#### 发送 - 要指定发送给谁？发送到那个节点？(万能发送接口)

1. 如果程序员知道下一步要发送给谁，发送到那一个节点的情况下，就可以调用这个接口。
2. 该接口就会摆脱流程引擎设计的方向条件规则与接受人规则。
<pre>
//如果确定了（或者自己计算好了）下一步要达到的节点，下一步的接受人，就可以按照如下格式调用。
BP.WF.SendReturnObjs objs = null; objs = BP.WF.Dev2Interface.Node_SendWork("001", workid, 103, "zhangsan" );
//发送给一个人,如果发送给多个人用逗号分开比如: zhangsan,lisi,wangwu

//下面调用方式，是知道要发送到那一个节点，但是不知道要发送给谁，让当前的节点定义的接受人规则来确定。
objs = BP.WF.Dev2Interface.Node_SendWork("001", workid, 103, null);

//下面调用方式，是知道要发送到那些人，但是不知道要发送到那个节点，让当前的节点定义的方向条件来确定。
objs = BP.WF.Dev2Interface.Node_SendWork("001", workid, 103,"zhangsan");

// 输出提示信息, 这个信息可以提示给操作员.
string infoMsg = objs.ToMsgOfHtml();
</pre>
#### 撤销

1. 撤销是发送的逆向操作。
2. 撤销可以调用ccbpm提供的撤销窗口完成，这是最简单的方式。
3. 地址为：/WF/WorkOpt/UnSend.htm 参数为: FK_Flow,FK_Node,WorkID,FID，当前流程的4大参数。
4. 如果需要在其他设备上工作，或者要自己写一个移交界面，请参考。
5. 能否被撤销，是有当前活动节点的撤销规则所决定的。
6. 撤销的功能显示在，在途的流程列表里，只有在途的工作才能被撤销。
7. 在途工作：顾名思义，就是我参与的工作，并且工作尚未完成。
8. 回滚流程，是在流程结束后需要重新在指定的节点，让指定的人员从新向下走。
<pre>
/*
*执行撤销，返回撤销是否成功信息，如果抛出异常就说明撤销失败。
*撤销失败的原因多种，最有可能的是因为当前活动节点不允许撤销规则决定的。
*/

string msg= BP.WF.Dev2Interface.Flow_DoUnSend( workID);
</pre>

#### 回滚

1. 回滚与撤销不同的是回滚是在流程完成以后的操作，并且回滚是由管理员操作的。
2. 回滚流程，是在流程结束后需要重新在指定的节点，让指定的人员从新向下走。
<pre>
//执行回滚，返回的是回滚执行信息，如果回滚失败，则会抛出异常。
string msg= BP.WF.Dev2Interface.Flow_DoRebackWorkFlow("001", workID, 103, "因为审批错误，需要回滚，从节点103重新开始审批。");
</pre>
#### 退回
1. 退回可以调用ccbpm提供的退回窗口完成，这是最简单的方式。
2. 地址为：/WF/WorkOpt/ReturnWork.htm 参数为: FK_Flow,FK_Node,WorkID,FID，当前流程的4大参数。
3. 如果需要在其他设备上工作，或者要自己写一个退回界面，请参考。
<pre>
/*
* 1, 获得当前节点可以退回的节点，该接口返回一个datatable。
* 2, 一个节点能够退回到那写节点是由当前节点的退回规则确定的。
* 3, 调用退回需要三个参数：节点编号，工作ID, 流程ID, 对于线性流程FID始终等于0.
*/
System.Data.DataTable dtCanReturnNodes = BP.WF.Dev2Interface.DB_GenerWillReturnNodes(103, workid, 0);

// 返回的是可以退回的节点。

//执行退回，当前的节点是103，要退回的节点是105，
string msg = BP.WF.Dev2Interface.Node_ReturnWork("001", workid, 0, 103, 105, "您的申请信息不完整，请修改后重新发送。", false);
</pre>
#### 移交

1. 移交也可以调用ccbpm提供的移交窗口完成，这是最简单的方式。
2. 地址为：/WF/WorkOpt/Shift.htm 参数为: FK_Flow,FK_Node,WorkID,FID，当前流程的4大参数。
3. 移交就是把自己所要做的工作交给其他人处理。
4. 如果需要在其他设备上工作，或者要自己写一个移交界面，请参考。
<pre>
/*
* 调用移交接口，传入必要的参数执行移交.
* FID 在线性流程上始终等于0.
*/

BP.WF.Dev2Interface.Node_Shift("001", 103, workid, 0, "zhangsan", "因我需要出差，所以特把工作移交给您。");

/*
* 撤销移交
* 如果在移交之后，发现不需要移交，就需要撤销回来，调用撤销移交接口。
*/
BP.WF.Dev2Interface.Node_ShiftUn("001", workid);
</pre>
#### 加签

1. 加签也可以调用ccbpm提供的加签窗口完成，这是最简单的方式。
2. 地址为：/WF/WorkOpt/Shift.htm 参数为: FK_Flow,FK_Node,WorkID,FID，当前流程的4大参数。
3. 加签就是把自己所要做的工作参考其他人意见，或者让其他人处理。
4. 加签有两种模式：1，加签后由加签人发送到下一个节点。2，加签后由让加签人发送给当前人，由当前人发送给下一个节点。
5. 如果需要在其他设备上工作，或者要自己写一个加签界面，请参考。
<pre>
/*
* 调用加签接口，传入必要的参数执行.
* FID 在线性流程上始终等于0.
*/

//技术人员zhangsan接受工作后，点击发送还会发送给当前人员，由当前人员发送给下一节点。
string info1= BP.WF.Dev2Interface.Node_Askfor(workid, BP.WF.AskforHelpSta.AfterDealSendByWorker, "zhangsan", "这里需要您出具技术鉴定意见.");

//技术人员填写后，直接就发送了下一节点.
string info2 = BP.WF.Dev2Interface.Node_Askfor(workid, BP.WF.AskforHelpSta.AfterDealSend, "zhangsan", "这里需要您出具技术鉴定意见.");

//技术人员回复加签，在由当前人发送到下一个节点。
string infoReply = BP.WF.Dev2Interface.Node_AskforReply("001", 103, workid,0, "我已经出具了技术鉴定意见，请参考.");
</pre>
#### 结束流程

1. 流程结束有三种方式
2. 第一种走到最后一个节点正常结束。
3. 第二种在特定的节点上，用户需要终止流程向下运动(与删除流程不同)。
4. 第三种在特定的节点上，用户需要删除流程。
<pre>
/* *  
* 手工的结束流程,这种方式会记录日志.
*/
string overInfo = BP.WF.Dev2Interface .Flow_DoFlowOver( workID, "该供应商找不到了，要结束掉该流程。");
/* * 删除流程,
* 删除流程有多种方式，用户可以根据自己的需求，调用不同的方式.
* 最后一个参数是是否删除子流程.
*/

//按照标记删除流程
string delInfo0 = BP.WF.Dev2Interface .Flow_DoDeleteFlowByFlag( workID, "我不需要请假了", true);

//彻底的删除流程，无日志记录.
string delInfo1 = BP.WF.Dev2Interface .Flow_DoDeleteFlowByReal( workID, "我不需要请假了", true);

//彻底的删除流程,有日志记录.
string delInfo2 = BP.WF.Dev2Interface .Flow_DoDeleteFlowByWriteLog("001", workID, "我不需要请假了", true);
</pre>
  ##### 封装的WebServices的接口
1. ccbpm的接口API都是一组静态的方法，这些方法可以被封装为多种形式比如：微服务、webservice、接口、controller等等。

2. 现在以经典常用的webservice封装为api为例来说明一下。

3. ![img](http://localhost:2296/WF/Admin/AttrFlow/APICode1.png)


  ##### 接口封装

  ![img](http://localhost:2296/WF/Admin/AttrFlow/APICode3.png)

  ##### 调用方法
  ![img](http://localhost:2296/WF/Admin/AttrFlow/APICode2.png)
`);e(this,"Dev_FEE_Java",`
  ## API

  #### FEE代码接口-Java
<pre>
package BP.FlowEvent;

import BP.DTS.*;
import BP.En.*;
import BP.Web.*;
import BP.Sys.*;
import BP.WF.*;

/**
 * @FlowName 事件子类.
 * ccbpm提供了可以让程序员编写代码与流程引擎，表单引擎进行交互，以处理复杂的业务逻辑。
 * ccbpm预留一个基类 BP.WF.FlowEventBase ，只要从这个基类上集成下来的子类，按照约定的格式重写相关的方法属性，流程引擎就会把这些代码注册到流程引擎中，并在运动中执行。
 * 该功能提供了一个自动生成的代码模版，如果您有编程基础，就很容易明白如何通过该子类实现复杂的业务逻辑。
 * 下载下来该类后，您必须把他放入一个以BP.开头的类库里，ccflow才能被注册到引擎中去。
 */
public class F001Templepte extends BP.WF.FlowEventBase {
    ///#region 重写属性.

    /**
     * 重写流程标记
     */
    @Override
    public String getFlowMark() {
        return "@FlowNo";
    }
    ///#endregion 重写属性.

    ///#region 构造 & 变量.

    /**
     * 报销流程事件
     */
    public F001Templepte() {
    }
    ///#endregion 构造 & 变量.

    ///#region 与发送相关事件.

    /**
     * 重写发送前事件
     *
     * @return
     */
    @Override
    public String SendWhen() {
//            
//             * 说明：
//             * 0. 此事件在发送前触发，如果抛出异常，系统就会把异常信息提示出来，从而阻止向下运动。
//             * 1, 一般来说，在改事件里填写安全性检查代码与其他的业务逻辑。比如表单校验，校验失败就提示错误。
//             * 2, 也可以写一些其他的业务逻辑，来组织特定的节点在不符合条件的时候 ，不让其向下发送。
//             * 3, 也可以更新一些其他系统的数据、状态.
//             

        // 当前的节点, 其他的变量请从 this.HisNode .
        int nodeID = this.HisNode.NodeID; // int类型的ID.
        String nodeName = this.HisNode.getName(); // 当前节点名称.
        return super.SendWhen();
    }

    /**
     * 发送成功事件，发送成功时，把流程的待办写入其他系统里.
     *
     * @return 返回执行结果，如果返回null就不提示。
     */
    @Override
    public String SendSuccess() {

//           
//           * 说明：
//           * 0. 此事件在发送成功的时候触发，系统会把抛出异常的信息提示出来，如果返回一个执行结果的字符串系统就会把它提示出来。
//           * 1, 一般来说，在改事件里填写与外部数据交互代码，比如：把执行的信息，写入到其他的系统。
//           * 2, 可以通过访问当前的发送结果对象，来判断到流程实例发送到那个节点，那些接收人。
//           

        try {
            // 组织必要的变量.
            long workid = this.WorkID; // 工作id.
            String flowNo = this.HisNode.FK_Flow; // 流程编号.
            int currNodeID = this.SendReturnObjs.VarCurrNodeID; //当前节点id
            int toNodeID = this.SendReturnObjs.VarToNodeID; // 到达节点id.
            String toNodeName = this.SendReturnObjs.VarToNodeName; // 到达节点名称。
            String acceptersID = this.SendReturnObjs.VarAcceptersID; // 接受人员id, 多个人员会用 逗号分看 ,比如 zhangsan,lisi。
            String acceptersName = this.SendReturnObjs.VarAcceptersName; // 接受人员名称，多个人员会用逗号分开比如:张三,李四.

            //执行向其他系统写入待办.
//                
//                 * 在这里需要编写你的业务逻辑，根据上面组织的变量.
//                 * 
//                 

            return super.SendSuccess();
        } catch (RuntimeException ex) {
            throw new RuntimeException("执行发送失败,执行节点[" + this.HisNode.NodeID + "," + this.HisNode.getName() + "]，详细信息：" + ex.getMessage());
        }
    }

    /**
     * 发送失败事件
     *
     * @return 返回空
     */
    @Override
    public String SendError() {
//            
//          * 说明：
//          * 0. 此事件在发送失败的时候触发，系统会把抛出异常的信息提示出来，如果返回一个执行结果的字符串系统就会把它提示出来。
//          * 1, 一般来说，在改事件里填写与外部数据交互回滚代码，比如在发送前，已经写了收款逻辑，在发送失败后就执行退款逻辑。
//          

        try {
            // 组织必要的变量.
            long workid = this.WorkID; // 工作id.
            String flowNo = this.HisNode.FK_Flow; // 流程编号.
            int currNodeID = this.SendReturnObjs.VarCurrNodeID; //当前节点id
            int toNodeID = this.SendReturnObjs.VarToNodeID; // 到达节点id.
            String toNodeName = this.SendReturnObjs.VarToNodeName; // 到达节点名称。
            String acceptersID = this.SendReturnObjs.VarAcceptersID; // 接受人员id, 多个人员会用 逗号分看 ,比如 zhangsan,lisi。
            String acceptersName = this.SendReturnObjs.VarAcceptersName; // 接受人员名称，多个人员会用逗号分开比如:张三,李四.

            //执行向其他系统写入待办.
//                
//                 * 在这里需要编写你的业务逻辑，根据上面组织的变量.
//                 * 
//                 

            return super.SendError();
        } catch (RuntimeException ex) {
            throw new RuntimeException("执行发送失败,执行节点[" + this.HisNode.NodeID + "," + this.HisNode.getName() + "]，详细信息：" + ex.getMessage());
        }
    }

    ///#endregion 与发送相关事件.

    ///#region 与流程相关的操作.

    /**
     * 当创建WorkID的时候.
     * 经常根据当前用户的身份初始化数据.
     *
     * @return 返回执行信息.
     */
    @Override
    public String FlowOnCreateWorkID() {
        return super.FlowOnCreateWorkID();
    }

    /**
     * 流程结束之前
     *
     * @return 返回null, 不提示信息，返回string提示结束信息,抛出异常就阻止流程删除.
     */
    @Override
    public String FlowOverBefore() {
//            
//             * 说明
//             * 0, 流程结束之前的操作. 
//             

        try {
            return null;
        } catch (RuntimeException ex) {
            throw new RuntimeException("@流程不能结束,异常信息:" + ex.getMessage());
        }
    }

    /**
     * 流程结束之后
     *
     * @return 返回null，不提示信息，返回string提示结束信息,抛出异常不处理。
     */
    @Override
    public String FlowOverAfter() {
        try {
            return null;
            //return "流程正常结束.";
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    /**
     * 流程删除前
     *
     * @return 返回null, 不提示信息, 返回信息，提示删除警告/提示信息, 抛出异常阻止删除操作.
     */
    @Override
    public String BeforeFlowDel() {
//            
//             * 说明：
//             * 0，该方法在流程删除之前触发。
//             * 1，删除之前，首先要检查必要的业务操作，如果抛出异常就不能阻止删除动作，
//             * 2, 返回string 就提示删除操作的风险。
//             * 3，返回null, 就不提示。
//             


//            
//             * 执行退款操作.
//             
        if (1 == 2) {
            throw new RuntimeException("@该流程不能被删除，因为退款不成功。");
        }

        return "@退款已经成功了，该订单取消了。";
    }

    /**
     * 流程删除后
     *
     * @return 返回null, 不提示信息, 返回信息，提示删除警告/提示信息, 抛出异常不处理.
     */
    @Override
    public String AfterFlowDel() {
//            
//           * 说明：
//           * 0，该方法在流程成功删除之后触发。
//           * 1，删除之后，可以处理相关的业务，如果抛出异常系统不处理。
//           * 2, 返回string 提示删除执行结果。
//           * 3，返回null, 就不提示。
//           

        return super.AfterFlowDel();
    }

    ///#endregion 与流程相关的操作


    ///#region 与节点表单相关事件 .

    /**
     * 保存后执行的事件
     *
     * @return
     */
    @Override
    public String SaveAfter() {
//            
//             * 说明：
//             * 0, 该事件仅仅与节点表单有效.
//             * 1, 
//             * 1, 保存后，可以处理复杂的表单字段之间的关系,比如：把从表的一个数值列求和到主表上去。
//             
        try {
            // 当前的节点, 其他的变量请从 this.HisNode .
            int nodeID = this.HisNode.NodeID; // int类型的ID.
            String nodeName = this.HisNode.getName(); // 当前节点名称.
            return super.SaveAfter();
        } catch (RuntimeException ex) {
            throw new RuntimeException("执行[保存后执行的事件],执行节点[" + this.HisNode.NodeID + "," + this.HisNode.getName() + "]，详细信息：" + ex.getMessage());
        }
    }

    /**
     * 保存之前
     *
     * @return
     */
    @Override
    public String SaveBefore() {
        return super.SaveBefore();
    }

    ///#endregion 与节点表单相关事件 .

    ///#region 与撤销相关的操作.

    /**
     * 撤销之前的操作
     *
     * @return 返回撤销前提示的信息, 返回null，则不提示,抛出异常则阻止撤销操作.
     */
    @Override
    public String UndoneBefore() {
//            
//             * 说明:
//             * 0, 撤销是一个在发送之后，由当前节点之前的发送人执行的收回发送操作.
//             * 1, 该事件在执行撤销之前触发。
//             * 2，如果返回null, 怎不提示，返回一个字符串则提示撤销前的警告，如果抛出异常则阻止撤销操作。
//             * 3, 使用该事件，可以灵活的处理撤销前后的一些业务逻辑。
//             
        return super.UndoneBefore();
    }

    /**
     * 撤销之后的操作
     *
     * @return 返回执行撤销成功的信息, 返回null，则不提示，如果抛出异常不处理。
     */
    @Override
    public String UndoneAfter() {
//            
//            * 说明:
//            * 0, 撤销后事件是在执行撤销后触发。
//            * 1，如果返回null, 怎不提示，返回一个字符串则提示撤销前的警告，如果抛出异常则阻止撤销操作。
//            * 2, 使用该事件，可以灵活的处理撤销前后的一些业务逻辑。
//            
        return super.UndoneAfter();
    }
    ///#endregion 与撤销相关的操作.

    ///#region 与退回相关事件.

    /**
     * 退回前事件
     *
     * @return 退回时的提示信息
     */
    @Override
    public String ReturnBefore() {
//            
//            * 说明：
//            * 0, 退回前要触发的事件.
//            * 1, 如果抛出异常，系统就阻止了退回，比如：有一些商品是不允许退货的，有一些商品是可以退货。
//             *   执行这个事件就抛出异常系统就会把该信息提示出来。
//            * 2, 返回string信息，系统就提示该信息给操作员。
//            
        try {
            // 当前的节点, 其他的变量请从 this.HisNode .
            int nodeID = this.HisNode.NodeID; // int类型的ID.
            String nodeName = this.HisNode.getName(); // 当前节点名称.

            if (1 == 0) {
                throw new RuntimeException("该商品不允许退货，您不能退回。");
            }

            return null; //返回空就不提示。
        } catch (RuntimeException ex) {
            throw new RuntimeException("执行[退回前事件],执行节点[" + this.HisNode.NodeID + "," + this.HisNode.getName() + "]，详细信息：" + ex.getMessage());
        }
    }

    /**
     * 退回后执行的事件
     *
     * @return 退回后的提示信息
     */
    @Override
    public String ReturnAfter() {
//            
//           * 说明：
//           * 0, 退回前要触发的事件.
//           * 1, 如果抛出异常，系统不处理，比如：比如在商城退货后就提示信息，告诉操作员。
//           * 2, 返回string信息，系统就提示该信息给操作员。
//           
        try {
            // 当前的节点, 其他的变量请从 this.HisNode .
            int nodeID = this.HisNode.NodeID; // int类型的ID.
            String nodeName = this.HisNode.getName(); // 当前节点名称.

            return "退回成功."; //提示退回信息. return null, 不提示信息。
        } catch (RuntimeException ex) {
            return "执行[退回前事件],执行节点[" + this.HisNode.NodeID + "," + this.HisNode.getName() + "]，详细信息：" + ex.getMessage();
        }
    }

    ///#endregion 与退回相关事件.

    ///#region 与加签相关的操作.

    /**
     * 加签之后的操作
     *
     * @return 返回执行的信息
     */
    @Override
    public String AskerAfter() {
        return super.AskerAfter();
    }

    /**
     * 加签答复相关的操作
     *
     * @return 返回执行的信息
     */
    @Override
    public String AskerReAfter() {
        return super.AskerReAfter();
    }

    ///#endregion 与加签相关的操作.

}
</pre>
`);e(this,"Dev_FEE_Net",`
  ## API

  #### FEE代码接口-Net
<pre>
  using System;
  using System.Threading;
  using System.Collections;
  
  using System.Data;
  using
  using BP.DTS;
  using BP.En;
  using BP.Web;
  using BP.Sys;
  using BP.WF;
  
  namespace BP.FlowEvent
  {
      /// <summary>
      /// @FlowName 事件子类.
      /// ccbpm提供了可以让程序员编写代码与流程引擎，表单引擎进行交互，以处理复杂的业务逻辑。
      /// ccbpm预留一个基类 BP.WF.FlowEventBase ，只要从这个基类上集成下来的子类，按照约定的格式重写相关的方法属性，流程引擎就会把这些代码注册到流程引擎中，并在运动中执行。
      /// 该功能提供了一个自动生成的代码模版，如果您有编程基础，就很容易明白如何通过该子类实现复杂的业务逻辑。
      /// 下载下来该类后，您必须把他放入一个以BP.开头的类库里，ccflow才能被注册到引擎中去。
      /// </summary>
      public class F001Templepte : BP.WF.FlowEventBase
      {
          #region 重写属性.
          /// <summary>
          /// 重写流程标记
          /// </summary>
          public override string FlowMark
          {
              get { return "@FlowNo"; }
          }
          #endregion 重写属性.
  
          #region 构造 & 变量.
          /// <summary>
          /// 报销流程事件
          /// </summary>
          public F001Templepte()
          {
          }
          #endregion 构造 & 变量.
  
          #region 与发送相关事件.
          /// <summary>
          /// 重写发送前事件
          /// </summary>
          /// <returns></returns>
          public override string SendWhen()
          {
              /*
               * 说明：
               * 0. 此事件在发送前触发，如果抛出异常，系统就会把异常信息提示出来，从而阻止向下运动。
               * 1, 一般来说，在改事件里填写安全性检查代码与其他的业务逻辑。比如表单校验，校验失败就提示错误。
               * 2, 也可以写一些其他的业务逻辑，来组织特定的节点在不符合条件的时候 ，不让其向下发送。
               * 3, 也可以更新一些其他系统的数据、状态.
               */
  
              // 当前的节点, 其他的变量请从 this.HisNode .
              int nodeID = this.HisNode.NodeID;    // int类型的ID.
              string nodeName = this.HisNode.Name; // 当前节点名称.
              return base.SendWhen();
          }
          /// <summary>
          /// 发送成功事件，发送成功时，把流程的待办写入其他系统里.
          /// </summary>
          /// <returns>返回执行结果，如果返回null就不提示。</returns>
          public override string SendSuccess()
          {
  
             /*
             * 说明：
             * 0. 此事件在发送成功的时候触发，系统会把抛出异常的信息提示出来，如果返回一个执行结果的字符串系统就会把它提示出来。
             * 1, 一般来说，在改事件里填写与外部数据交互代码，比如：把执行的信息，写入到其他的系统。
             * 2, 可以通过访问当前的发送结果对象，来判断到流程实例发送到那个节点，那些接收人。
             */
  
              try
              {
                  // 组织必要的变量.
                  Int64 workid = this.WorkID; // 工作id.
                  string flowNo = this.HisNode.FK_Flow; // 流程编号.
                  int currNodeID = this.SendReturnObjs.VarCurrNodeID; //当前节点id
                  int toNodeID = this.SendReturnObjs.VarToNodeID; // 到达节点id.
                  string toNodeName = this.SendReturnObjs.VarToNodeName; // 到达节点名称。
                  string acceptersID = this.SendReturnObjs.VarAcceptersID; // 接受人员id, 多个人员会用 逗号分看 ,比如 zhangsan,lisi。
                  string acceptersName = this.SendReturnObjs.VarAcceptersName; // 接受人员名称，多个人员会用逗号分开比如:张三,李四.
  
                  //执行向其他系统写入待办.
                  /*
                   * 在这里需要编写你的业务逻辑，根据上面组织的变量.
                   * 
                   */
  
                  return base.SendSuccess();
              }
              catch (Exception ex)
              {
                  throw new Exception("执行发送失败,执行节点[" + this.HisNode.NodeID + "," + this.HisNode.Name + "]，详细信息：" + ex.Message);
              }
          }
          /// <summary>
          /// 发送失败事件
          /// </summary>
          /// <returns>返回空</returns>
          public override string SendError()
          {
              /*
            * 说明：
            * 0. 此事件在发送失败的时候触发，系统会把抛出异常的信息提示出来，如果返回一个执行结果的字符串系统就会把它提示出来。
            * 1, 一般来说，在改事件里填写与外部数据交互回滚代码，比如在发送前，已经写了收款逻辑，在发送失败后就执行退款逻辑。
            */
  
              try
              {
                  // 组织必要的变量.
                  Int64 workid = this.WorkID; // 工作id.
                  string flowNo = this.HisNode.FK_Flow; // 流程编号.
                  int currNodeID = this.SendReturnObjs.VarCurrNodeID; //当前节点id
                  int toNodeID = this.SendReturnObjs.VarToNodeID; // 到达节点id.
                  string toNodeName = this.SendReturnObjs.VarToNodeName; // 到达节点名称。
                  string acceptersID = this.SendReturnObjs.VarAcceptersID; // 接受人员id, 多个人员会用 逗号分看 ,比如 zhangsan,lisi。
                  string acceptersName = this.SendReturnObjs.VarAcceptersName; // 接受人员名称，多个人员会用逗号分开比如:张三,李四.
  
                  //执行向其他系统写入待办.
                  /*
                   * 在这里需要编写你的业务逻辑，根据上面组织的变量.
                   * 
                   */
  
                  return base.SendError();
              }
              catch (Exception ex)
              {
                  throw new Exception("执行发送失败,执行节点[" + this.HisNode.NodeID + "," + this.HisNode.Name + "]，详细信息：" + ex.Message);
              }
          }
          #endregion 与发送相关事件.
  
          #region 与流程相关的操作.
          /// <summary>
          /// 当创建WorkID的时候.
          /// 经常根据当前用户的身份初始化数据.
          /// </summary>
          /// <returns>返回执行信息.</returns>
          public override string FlowOnCreateWorkID()
          {
              return base.FlowOnCreateWorkID();
          }
          /// <summary>
          /// 流程结束之前
          /// </summary>
          /// <returns>返回null,不提示信息，返回string提示结束信息,抛出异常就阻止流程删除.</returns>
          public override string FlowOverBefore()
          {
              /*
               * 说明
               * 0, 流程结束之前的操作. 
               */
  
              try
              {
                  return null;
              }
              catch(Exception ex)
              {
                  throw new Exception("@流程不能结束,异常信息:"+ex.Message);
              }
          }
          /// <summary>
          /// 流程结束之后
          /// </summary>
          /// <returns>返回null，不提示信息，返回string提示结束信息,抛出异常不处理。</returns>
          public override string FlowOverAfter()
          {
              try
              {
                  return null;
                  //return "流程正常结束.";
              }
              catch(Exception ex) 
              {
                  return ex.Message;
              }
          }
          /// <summary>
          /// 流程删除前
          /// </summary>
          /// <returns>返回null,不提示信息,返回信息，提示删除警告/提示信息, 抛出异常阻止删除操作.</returns>
          public override string BeforeFlowDel()
          {
              /*
               * 说明：
               * 0，该方法在流程删除之前触发。
               * 1，删除之前，首先要检查必要的业务操作，如果抛出异常就不能阻止删除动作，
               * 2, 返回string 就提示删除操作的风险。
               * 3，返回null, 就不提示。
               */
  
  
              /*
               * 执行退款操作.
               */
              if (1 == 2)
                  throw new Exception("@该流程不能被删除，因为退款不成功。");
  
              return "@退款已经成功了，该订单取消了。";
          }
          /// <summary>
          /// 流程删除后
          /// </summary>
          /// <returns>返回null,不提示信息,返回信息，提示删除警告/提示信息, 抛出异常不处理.</returns>
          public override string AfterFlowDel()
          {
              /*
             * 说明：
             * 0，该方法在流程成功删除之后触发。
             * 1，删除之后，可以处理相关的业务，如果抛出异常系统不处理。
             * 2, 返回string 提示删除执行结果。
             * 3，返回null, 就不提示。
             */
  
              return base.AfterFlowDel();
          }
          #endregion 与流程相关的操作
  
          #region 与节点表单相关事件 .
          /// <summary>
          /// 保存后执行的事件
          /// </summary>
          /// <returns></returns>
          public override string SaveAfter()
          {
              /*
               * 说明：
               * 0, 该事件仅仅与节点表单有效.
               * 1, 
               * 1, 保存后，可以处理复杂的表单字段之间的关系,比如：把从表的一个数值列求和到主表上去。
               */
              try
              {
                  // 当前的节点, 其他的变量请从 this.HisNode .
                  int nodeID = this.HisNode.NodeID;    // int类型的ID.
                  string nodeName = this.HisNode.Name; // 当前节点名称.
                  return base.SaveAfter();
              }
              catch (Exception ex)
              {
                  throw new Exception("执行[保存后执行的事件],执行节点[" + this.HisNode.NodeID + "," + this.HisNode.Name + "]，详细信息：" + ex.Message);
              }
          }
          /// <summary>
          /// 保存之前
          /// </summary>
          /// <returns></returns>
          public override string SaveBefore()
          {
              return base.SaveBefore();
          }
          #endregion 与节点表单相关事件 .
  
          #region 与撤销相关的操作.
          /// <summary>
          /// 撤销之前的操作
          /// </summary>
          /// <returns>返回撤销前提示的信息,返回null，则不提示,抛出异常则阻止撤销操作.</returns>
          public override string UndoneBefore()
          {
              /*
               * 说明:
               * 0, 撤销是一个在发送之后，由当前节点之前的发送人执行的收回发送操作.
               * 1, 该事件在执行撤销之前触发。
               * 2，如果返回null, 怎不提示，返回一个字符串则提示撤销前的警告，如果抛出异常则阻止撤销操作。
               * 3, 使用该事件，可以灵活的处理撤销前后的一些业务逻辑。
               */
              return base.UndoneBefore();
          }
          /// <summary>
          /// 撤销之后的操作 
          /// </summary>
          /// <returns>返回执行撤销成功的信息,返回null，则不提示，如果抛出异常不处理。</returns>
          public override string UndoneAfter()
          {
              /*
              * 说明:
              * 0, 撤销后事件是在执行撤销后触发。
              * 1，如果返回null, 怎不提示，返回一个字符串则提示撤销前的警告，如果抛出异常则阻止撤销操作。
              * 2, 使用该事件，可以灵活的处理撤销前后的一些业务逻辑。
              */
              return base.UndoneAfter();
          }
          #endregion 与撤销相关的操作.
  
          #region 与退回相关事件.
          /// <summary>
          /// 退回前事件
          /// </summary>
          /// <returns>退回时的提示信息</returns>
          public override string ReturnBefore()
          {
              /*
              * 说明：
              * 0, 退回前要触发的事件.
              * 1, 如果抛出异常，系统就阻止了退回，比如：有一些商品是不允许退货的，有一些商品是可以退货。
               *   执行这个事件就抛出异常系统就会把该信息提示出来。
              * 2, 返回string信息，系统就提示该信息给操作员。
              */
              try
              {
                  // 当前的节点, 其他的变量请从 this.HisNode .
                  int nodeID = this.HisNode.NodeID;    // int类型的ID.
                  string nodeName = this.HisNode.Name; // 当前节点名称.
  
                  if (1 == 0)
                      throw new Exception("该商品不允许退货，您不能退回。");
  
                  return null; //返回空就不提示。
              }
              catch (Exception ex)
              {
                  throw new Exception("执行[退回前事件],执行节点[" + this.HisNode.NodeID + "," + this.HisNode.Name + "]，详细信息：" + ex.Message);
              }
          }
          /// <summary>
          /// 退回后执行的事件
          /// </summary>
          /// <returns>退回后的提示信息</returns>
          public override string ReturnAfter()
          {
              /*
             * 说明：
             * 0, 退回前要触发的事件.
             * 1, 如果抛出异常，系统不处理，比如：比如在商城退货后就提示信息，告诉操作员。
             * 2, 返回string信息，系统就提示该信息给操作员。
             */
              try
              {
                  // 当前的节点, 其他的变量请从 this.HisNode .
                  int nodeID = this.HisNode.NodeID;    // int类型的ID.
                  string nodeName = this.HisNode.Name; // 当前节点名称.
  
                  return "退回成功."; //提示退回信息. return null, 不提示信息。
              }
              catch (Exception ex)
              {
                  return "执行[退回前事件],执行节点[" + this.HisNode.NodeID + "," + this.HisNode.Name + "]，详细信息：" + ex.Message;
              }
          }
          #endregion 与退回相关事件.
  
          #region 与加签相关的操作.
          /// <summary>
          /// 加签之后的操作
          /// </summary>
          /// <returns>返回执行的信息</returns>
          public override string AskerAfter()
          {
              return base.AskerAfter();
          }
          /// <summary>
          /// 加签答复相关的操作
          /// </summary>
          /// <returns>返回执行的信息</returns>
          public override string AskerReAfter()
          {
              return base.AskerReAfter();
          }
         
          #endregion 与加签相关的操作.
  
      }
  }  
</pre>
`);e(this,"Dev_SelfAttr",`
  ## API

  #### 自定义流程属性

- 该文档是用于处理，流程属性自定义的文件。

- 如果遇到流程的个性化属性设置，可以在该文件里实现。

- 在流程属性里可以连接到该页面，并传入流程模版字段.，用户需要终止流程向下运动(与删除流程不同)。

`);this.PageTitle="流程2开"}Init(){return i(this,null,function*(){this.AddGroup("A","查询报表设计"),this.AddHelp("Help","帮助",this.RptHelp),this.AddGoToUrl("Search","查询","/src/WF/Rpt/SearchFlow.vue?FlowNo="+this.PKVal),this.AddIcon("icon-notebook","Search"),this.AddGoToUrl("Group","分析","/src/WF/Rpt/GroupFlow.vue?FlowNo="+this.PKVal),this.AddIcon("icon-chart","Group"),this.AddHelp("API","API接口",this.APIDesc),this.AddIcon("icon-puzzle","API"),this.AddGroup("B","FEE代码开发"),this.AddHelp("HelpFEE","帮助",this.HelpFEE),this.AddHelp("Dev_FEE_Java","FEE-Java",this.Dev_FEE_Java),this.AddIcon("icon-puzzle","Dev_FEE_Java"),this.AddHelp("Dev_FEE_Net","FEE-Net",this.Dev_FEE_Net),this.AddIcon("icon-key","Dev_FEE_Net"),this.AddGroup("C","流程调用"),this.AddHelp("HelpFlow","帮助",this.HelpFlow)})}GenerSorts(){return i(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,u,s,l,c){return i(this,null,function*(){})}}export{I as GPN_FlowRptD};
