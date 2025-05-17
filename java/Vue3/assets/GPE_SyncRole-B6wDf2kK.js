var c=Object.defineProperty;var n=(s,e,t)=>e in s?c(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var o=(s,e,t)=>n(s,typeof e!="symbol"?e+"":e,t);import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-IicyYiex.js";import{Flow as m}from"./Flow-D4nUES4A.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";class w extends h{constructor(){super("GPE_SyncRole");o(this,"Desc0",`
  #### 帮助
  - 数据同步，就是在流程运动过程过程中向他系统读写数据.
  - 默认为不同步.
  - 根据应用系统的需求，设置同步时间与同步方式.
  - 使用ccbpm的数据同步功能可能定期的向指定的系统推送数据.
  - 推送什么数据，在同步内容里定义。
  ##### 应用场景
  - 请假流程中，请假信息需要写入到HR系统中去.
  - 订单流程需要写入到ERP，进销存系统中去.
  ##### 其他
  - 流程都有一个业务数据表,默认名字NDxxxRpt,此表的名字可以自定义.
  - ccbpm在运行过程中，都把业务数据写入到这个表里，该表的数据是流程数据+业务数据组成.
  - 二次开发人员可以通过访问该表定期的获取数据,也可以使用触发器来完成数据同步.
`);o(this,"Desc1",`
  #### 帮助
  - 当用户执行发送的时候执行数据同步
  - 任何节点执行发送,都执行同步.
  #### 其它
  - 如果数据量太大,就会导致发送变慢.
`);o(this,"Desc2",`
  #### 帮助
  - 在流程的结束事件里执行同步.
`);o(this,"Desc3",`
  #### 帮助
  - 在指定的节点发送的事件里执行同步.
  - 请选择要执行的节点.
`);this.PageTitle="同步数据规则"}Init(){this.entity=new m,this.KeyOfEn="DTSWay",this.AddGroup("A","数据同步规则"),this.Blank("0","不执行同步",this.Desc0),this.Blank("1","任何节点发送后都执行同步",this.Desc1);const t=`SELECT NodeID as No,Name FROM WF_Node WHERE FK_Flow='${this.PKVal}' `;this.SelectItemsByList("3","指定的节点发送后",this.Desc3,!0,t,"DTSSpecNodes",""),this.Blank("2","流程结束时",this.Desc2)}AfterSave(t,i){if(t==i)throw new Error("Method not implemented.")}BtnClick(t,i,r){if(t==i||t===r)throw new Error("Method not implemented.")}}export{w as GPE_SyncRole};
