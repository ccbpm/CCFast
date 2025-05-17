var h=Object.defineProperty;var n=(i,t,e)=>t in i?h(i,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[t]=e;var s=(i,t,e)=>n(i,typeof t!="symbol"?t+"":t,e);import{PageBaseGroupEdit as l}from"./PageBaseGroupEdit-IicyYiex.js";import{Node as m}from"./Node-DKGUVcK1.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";import"./EntityNodeID-BCBbJNH2.js";class N extends l{constructor(){super("GPE_OvertimeRole");s(this,"Desc0",`
  #### 帮助
   - 超时的时候一直处理超时的状态。

 `);s(this,"Desc1",`
  #### 帮助
   
   - 超时了当前节点自动运动到下一个环节，如果要控制特定的条件下不向下运动，就需要在当前节点的发送前事件里编写相关的业务逻辑。
   - 自动向下运动需要明确下一个节点的接受人，与到达的节点，所以一下两种行为不能自动向下运动。
   - 当前节点的方向条件控制规则是选择的模式.
   - 到达的节点的接受人规则是上一个节点选择的.
   
 `);s(this,"Desc2",`
  #### 帮助
   - 超时自动跳转到指定的节点。

 
 `);s(this,"Desc3",`
  #### 帮助
   - 接受输入的必须是人员的工作帐号。
   - 如果有多个人元用半角的逗号分开，比如: zhangsan,lisi。
   - 超时后就自动的把工作移交给指定的人员。
  
  `);s(this,"Desc4",`
  #### 帮助
   - 接受输入的必须是人员的工作帐号。
   - 如果有多个人元用半角的逗号分开，比如: zhangsan,lisi。
   - 超时后，系统就会向这些人员发送消息提醒。
  
  `);s(this,"Desc5",`
  #### 帮助
   - 超时后就自动删除当前的流程。
     
  `);s(this,"ExecSQL",`
  #### 帮助
   - 当前的的sql支持ccbpm的表达式.比如:@WebUser.No,@WebUser.Name,@WebUser.DeptNo,@WebUser.OrgNo
   - 执行SQL,处理业务逻辑.
  `);this.PageTitle="超时处理规则"}Init(){this.entity=new m,this.KeyOfEn="OutTimeDeal",this.AddGroup("A","超时处理规则"),this.Blank("0","不处理",this.Desc0),this.Blank("1","自动向下运动",this.Desc1),this.SingleDDLSQL("2","跳转到指定的节点","DoOutTime",this.Desc2,"SELECT NodeID as No,CONCAT(NodeID,'-',Name) AS Name FROM WF_Node WHERE FK_Flow='@FK_Flow'",!1),this.SingleTB("3","移交给指定的人员","DoOutTime",this.Desc3,"输入人员编号"),this.Blank("5","删除流程",this.Desc5),this.SingleTBSQL("6","按SQL","DoOutTime",this.ExecSQL),this.AddGroup("B","消息提醒"),this.SingleTB("4","给指定的人员发送消息.","DoOutTime",this.Desc4,"输入人员编号"),this.Blank("7","向开始节点发消息.",this.HelpUn),this.Blank("8","向当前节点处理人发消息.",this.HelpUn),this.Blank("9","向参与流程的所有人发消息.",this.HelpUn)}AfterSave(e,o){if(e==o)throw new Error("Method not implemented.")}BtnClick(e,o,r){if(e==o||e===r)throw new Error("Method not implemented.")}}export{N as GPE_OvertimeRole};
