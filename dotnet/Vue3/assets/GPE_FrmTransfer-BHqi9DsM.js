var o=Object.defineProperty;var p=(t,r,e)=>r in t?o(t,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[r]=e;var s=(t,r,e)=>p(t,typeof r!="symbol"?r+"":r,e);import{FrmTransferCustom as i}from"./FrmTransferCustom-CLYjrv9F.js";import{PageBaseGroupEdit as a}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EntityNodeID-De9k9loD.js";import"./Node-B6HRFhwD.js";import"./BtnLab-DDtmliJP.js";import"./Help-D0bDMZWg.js";class w extends a{constructor(){super("GPE_FrmTransfer");s(this,"Desc0",` 
  #### 帮助
  - 流转自定义 定义: 在流程运行过程中，可以对节点进行动态编排, 我们把这样的行为，称为流程自定义.
  - 比如：一个项目申报有n个工序, 整体流程中,头部与尾部节点固定，根据项目类型不同，需要选择不同的工序. 
  - 游离态节点定义: 一个节点是一道工序，这道工序在一个流程中.

  #### 流程图
  - 这是一个项目申报流程，中间的工序节点是可以被选择的, 可以动态组态.
  -![输入图片说明](/resource/WF/Admin/AttrNode/FrmTransfer/Img/FrmTransferFlow.png "屏幕截图.png")  

  #### 流转自定义-(工序控制图)

  - 可以为每个工序设置操作人员. 
  - 可以调整工序顺序.
  - 可以增加或者减少工序(节点). 
  
 #### 其他说明
  - 工序的节点，都是游离态节点.
  - 流程测试案例 027. 如下图.
  -![输入图片说明](/resource/WF/Admin/AttrNode/FrmTransfer/Img/FrmTransferFlow2.png "屏幕截图.png")  
  `);s(this,"Desc1",` 
  #### 帮助
  - 只读状态下，仅仅可以查看工序，不能对工序进行编排.
  `);s(this,"Desc2",` 
  #### 帮助
  - 可以动态编排工序.
  - 可以调整顺序.
  - 可以设置工序上的操作员.
  #### 组件工作图
  - ![输入图片说明](/resource/WF/Admin/AttrNode/FrmTransfer/Img/FrmTransfer.png "屏幕截图.png")  
 
....`);this.PageTitle="流转自定义"}Init(){this.entity=new i,this.KeyOfEn="FTCSta",this.AddGroup("A","组件状态"),this.Blank("0","禁用",this.Desc0),this.Blank("1","只读",this.Desc1),this.AddEntity("2","可定义",new i,this.Desc2)}BtnClick(e,n,m){if(e==n||e===m)throw new Error("Method not implemented.")}AfterSave(e,n){}}export{w as GPE_FrmTransfer};
