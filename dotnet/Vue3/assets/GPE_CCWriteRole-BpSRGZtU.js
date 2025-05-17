var n=Object.defineProperty;var m=(o,e,t)=>e in o?n(o,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[e]=t;var r=(o,e,t)=>m(o,typeof e!="symbol"?e+"":e,t);import{PageBaseGroupEdit as d}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Node as c,NodeAttr as h}from"./Node-B6HRFhwD.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";import"./EntityNodeID-De9k9loD.js";class g extends d{constructor(){super("GPE_CCWriteRole");r(this,"Desc0",`
  #### 说明
  - 显示在抄送列表.
  - 抄送是一个单独功能页面.
  #### 关于待办的分类
  - 待办有如下类别: 发送的、退回的、抄送的、移交的、加签的
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/AttrNode/CCRole/Img/cc.png "屏幕截图")
 `);r(this,"Desc1",`
  #### 说明
  - 出现了抄送的工作让其与待办放在一起,待办理表里也可以查看抄送信息.
  #### 关于待办的分类
  - 待办有如下类别: 发送的、退回的、抄送的、移交的、加签的
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/AttrNode/CCRole/Img/todolist.png "屏幕截图")
 `);r(this,"Desc2",`
  #### 说明
  - 抄送的信息显示在待办与抄送列表都存在.
  #### 关于待办的分类
  - 待办有如下类别: 发送的、退回的、抄送的、移交的、加签的
  `);this.PageTitle="抄送写入规则"}Init(){this.entity=new c,this.KeyOfEn=h.CCWriteTo,this.AddGroup("A","+抄送写入规则"),this.Blank("0","写入抄送列表",this.Desc0),this.Blank("1","写入待办",this.Desc1),this.Blank("2","写入待办+抄送列表",this.Desc2)}AfterSave(t,i){if(t==i)throw new Error("Method not implemented.")}BtnClick(t,i,s){if(t==i||t===s)throw new Error("Method not implemented.")}}export{g as GPE_CCWriteRole};
