var c=Object.defineProperty;var m=(t,o,e)=>o in t?c(t,o,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[o]=e;var r=(t,o,e)=>m(t,typeof o!="symbol"?o+"":o,e);import{PageBaseGroupEdit as T}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Node as h,NodeAttr as n}from"./Node-B6HRFhwD.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";import"./EntityNodeID-De9k9loD.js";class A extends T{constructor(){super("GPE_TurnTo");r(this,"Desc0",`
  #### 帮助
   - 默认为不设置，按照机器自动生成的语言提示，这是标准的信息提示。
   - 比如：您的当前的工作已经处理完成。下一步工作自动启动，已经提交给xxx处理。 
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/AttrNode/TurnTo/Img/Turn.png "屏幕截图.png")
 `);r(this,"Desc1",`
  #### 帮助
   - 按照您定义的信息格式，提示给已经操作完成的用户。
   - 比如：您的申请已经发送至XXX 进行审批。 
   - 该自定义信息支持ccbpm的表达式，具体可参考右侧帮助文档。
   - 发送后系统变量如下:
   - 您可以设置为: 当前工作提交给:【 @VarAcceptersName 】处理。
   - 例如：您的请假申请单，已经提交给 @VarAcceptersName ，提交到： @VarToNodeName , 请假了@QingJiaTianTianShu天。
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/AttrNode/TurnTo/Img/TurnTo.png "屏幕截图.png")
`);r(this,"Desc2",`
  #### 帮助
  
   - 按照您定义的url转向，可处理较为复杂的业务逻辑处理。
   - 比如：URL为MyFlow.htm页面或www.baidu.com。
   - 该URL支持ccbpm参数形式，具体传值参考右侧帮助。
   - 启动子流程实例: /WF/MyFlow.htm?FK_Flow=003&PFlowNo=002
  `);r(this,"Desc3",`
  #### 帮助
  - 发送成功后直接关闭.
  `);r(this,"Desc4",`
  #### 帮助
   - 按照设置的条件转向。
   - 该功能将要取消.
  `);this.PageTitle="发送后转向"}Init(){this.entity=new h,this.Icon="icon-directions",this.KeyOfEn=n.TurnToDeal,this.AddGroup("A","+转向规则"),this.Blank("0","提示ccflow默认信息",this.Desc0),this.SingleTB("1","提示指定信息",n.TurnToDealDoc,this.Desc1,"请输入提示信息"),this.SingleTB("2","转向指定的URL",n.TurnToDealDoc,this.Desc2,"请输入转向URL"),this.Blank("3","发送完毕就关闭，不提示任何信息.",this.Desc3)}AfterSave(e,i){if(e==i)throw new Error("Method not implemented.")}BtnClick(e,i,s){if(e==i||e===s)throw new Error("Method not implemented.")}}export{A as GPE_TurnTo};
