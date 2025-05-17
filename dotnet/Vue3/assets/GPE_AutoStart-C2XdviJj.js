var o=Object.defineProperty;var r=(a,t,e)=>t in a?o(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var s=(a,t,e)=>r(a,typeof t!="symbol"?t+"":t,e);import{AutoStartEn5 as h}from"./AutoStartEn5-DLFKweCe.js";import{PageBaseGroupEdit as m}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Flow as p,FlowAttr as n}from"./Flow-D1QXg1UO.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class y extends m{constructor(){super("GPE_AutoStart");s(this,"Desc0",`
  
  #### 帮助
   - 说明：就是说工作人员从流程发起列表里，点流程，就发起流程。
   -  定义：自动启动工作流程，一个流程的开始节点的填写与发起是在特定规则的设置下自动发起的流程。
   -  解释：通常模式下的流程启动是手工的启动，就是用户从一个发起列表，点击流程名字，就启动了该流程。但是有的时候，是系统自动发起该流程。
  #### 应用场景
   - 周例会流程，用户希望每个周都要启动例会通知流程这个启动是让系统自动发起而非人工发起。
  `);s(this,"Desc1",`
  
  #### 帮助 
   - 说明：指定的人员指定的发起时间点自动启动流程。
   - 请打开操作手册：<a href="https://gitee.com/opencc/JFlow/wikis/%E5%85%B3%E4%BA%8E%E9%A9%B0%E9%AA%8BBPM"  >操作手册</a>
   - 一个人启动的参数格式：人员编号@时间点1@时间点2@时间点n
   - n个人启动的参数格式：人员编号@时间点1@时间点2@时间点n,人员编号@时间点1@时间点2@时间点n,人员编号@时间点1@时间点2@时间点n
   - 比如：zhangshan@01:01@12:01 让张三在每天的 1点零1分，与12点零1分发起此流程。
   - 比如：zhangshan@-01 01:01 让张三在每月1号的1点零1分发起此流程。
   - 比如：zhangshan@06-01 01:01 让张三在每年的6月1号的1点零1分发起此流程。
   - cc的日期格式为：yyyy-MM-dd HH:mm 如果时间匹配到您设置的时间点，那么系统就会触发流程，多个时间点用@符号隔开。

  #### 定义
   - 自动启动工作流程，一个流程的开始节点的填写与发起是在特定规则的设置下自动发起的流程。
   - 解释：通常模式下的流程启动是手工的启动，就是用户从一个发起列表，点击流程名字，就启动了该流程。但是有的时候，是系统自动发起该流程。
  #### 应用场景
   - 1周例会流程，用户希望每个周都要启动例会通知流程这个启动是让系统自动发起而非人工发起。
  `);s(this,"Desc2",`
  #### 帮助
   - 按照数据集启动，参数需要填写一个SQL，执行该SQL返回是一个数据集合.
   - 该集合内有多少条数据，就发起多少个流程。
   - 该数据源必有MainPK列，用与标记流程实例的主键，避免流程发起重复.
   - 关于MainPK 字段:它可以是一个订单号、事故号、单据号、或者字符组合的不重复的编号,如果MainPK已经存在,就不在发起了.
   - 主表的每行记录都可以是一个, 可以有ToNode,ToEmps 字段. ToNode字段标识流程发送到哪个节点,ToEmps字段是接受人。
   - 需要启动ccbpm的流程服务才能确保自动工作,扫描数据源的时间频率由服务来设定. 比如：设定10分钟扫描一次数据源。
   #### DEMO
   - SELECT MainPK, MyField1,MyField2,MyField3, 103 AS ToNode, 'zhangsan' as ToEmps  FROM MyTable WHERE 1=1 
   - 解释: 发送到第三个节点上, 让zhangsan 去处理. 这两个字段是可选.
   #### 应用场景
   - 定期的扫描客户付款表，如果发现有新的付款记录，就启动发货流程。
  `);s(this,"WFTask",`
  #### 帮助
   - 第三方软件向特定的表 WF_Task 中写入数据，每写入一条数据系统就会自动发起一条流程。
   - ccBPM就会读取这张表来完成流程的发起,发起成功后就把这条记录设置成已经发起的状态。
  #### 表结构
   - MyPK:是一个不能重复的ID, 任何数据类型.
   - FK_Flow: 是要启动的流程编号, 例如:001
   - Starter 要启动的人员名称, 例如：zhangsan
   - ToNode 启动后要到达的节点，如果为0，则发送到第2个节点.
   - ToEmps 要发送的人员(接收人),多个人员用逗号分开,如果为空. 例如: zhangsan,lisi
   - Paras 是表单数据，就是要装载的表单数据，开始节点表单的字段名与参数名保持一致系统就会自动赋值。
   - 例如：@XingMing=张三@Addr=山东济南@Tel=186xxxxxxxx
   - 解释：开始节点的字段有这些字段，就可以用启动起来。
   - TaskSta 是状态，默认为0，表示没有启动， 启动成功后=1， 启动失败=2 ，启动的消息系统自动写入到 Msg字段。
   - Msg 系统执行自动启动后，写入的消息.
   - StartRDT 要启动的日期，如果为空就是立即启动.
   - RDT 发送日期.
  #### 图例说明
  - 第1笔记录已经启动成功.
  - 第2笔记录等待启动.
  ![输入图片说明](/resource/WF/Admin/AttrFlow/AutoStart/Img/WF_Task.png "屏幕截图.png")  
  `);s(this,"Desc4",`
  #### 帮助
   - 集合中的指定人，按指定的时间发起流程。
  `);s(this,"Desc5",`  

  ### 参数1
    比如：SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.FK_Dept' 支持表达式, 如果指定的人员，比如：zhangsan,lisi,wangwu
支持表达式：@WebUser.FK_Dept, @WebUser.No
  ### 参数2
    比如：@01:01@12:01 在每天的 1点零1分，与12点零1分发起此流程。
    比如：@-01 01:01 在每月1号的1点零1分发起此流程。
    比如：@06-01 01:01 在每年的6月1号的1点零1分发起此流程。
    cc的日期格式为：yyyy-MM-dd HH:mm 如果时间匹配到您设置的时间点，那么系统就会触发流程，多个时间点用@符号隔开。
    按照周设置的格式为: Week.3 HH:mm , 比如每周1早晨8点00分启动. @Week.1 08:00 , 可以支持多个时间段比如：@Week.1 08:00@Week.3 08:00@Week.4 08:00@Week.7 08:00
    设置每月的指定的日期发起: @-01 08:00 , 每月1号8点00分启动.
    设置每月的最后一天发起: @LastDayOfMonth 20:00 , 每月最后一天 20点00分启动.
`);this.PageTitle="自动发起"}BtnClick(e,i,d){}AfterSave(e,i){if(e==i)throw new Error("Method not implemented.")}Init(){this.entity=new p,this.KeyOfEn="FlowRunWay",this.AddGroup("A","配置模式"),this.Blank("0","手工发起",this.Desc0),this.SingleTB("1","按人员定时启动",n.RunObj,this.Desc1,"请按照格式输入参数"),this.SingleTBSQL("2","按数据集启动",n.RunObj,this.Desc2),this.TextBox2("4","指定人员集合按时启动",n.StartGuidePara1,"请输入人员集合",n.StartGuidePara2,"请输入时间点",this.Desc4),this.AddEntity("5","以admin启动发送给指定人员集合",new h,this.Desc5),this.AddGroup("B","开发者模式"),this.Blank("3","触发试启动",this.WFTask)}}export{y as GPE_AutoStart};
