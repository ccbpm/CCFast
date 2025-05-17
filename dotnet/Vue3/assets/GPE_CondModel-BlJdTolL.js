var p=Object.defineProperty;var d=(t,e,o)=>e in t?p(t,e,{enumerable:!0,configurable:!0,writable:!0,value:o}):t[e]=o;var r=(t,e,o)=>d(t,typeof e!="symbol"?e+"":e,o);import{GloComm as c}from"./GloComm-CmAl8MpM.js";import{Node as h,NodeAttr as s}from"./Node-DKGUVcK1.js";import{PageBaseGroupEdit as l}from"./PageBaseGroupEdit-IicyYiex.js";import{G as g,l as u}from"./entry/index-C6uBgOW5-1730430676707.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./EntityNodeID-BCBbJNH2.js";import"./Help-D0bDMZWg.js";class x extends l{constructor(){super("GPE_CondModel");r(this,"Desc0",` 
  #### 帮助
   - 该模式需要为每一跟连接线设置方向条件。
   - ccbpm在发送的时候会检查这些条件,如果条件成立就转向这个节点。
   - 该模式是让ccbpm自动为您计算要发送到的节点。
  #### 效果图
   - ![输入图片说明](/resource/WF/Admin/Img/CondModel0.png "屏幕截图.png")
   - 请在右边要到达的节点设置方向条件。
   `);r(this,"Desc1",`
  #### 帮助
   - 视频教程: https://drive.weixin.qq.com/s?k=AOsAZQczAAYgmfqbFS
   - 用户发送的时候，在发送按钮旁边有一个下拉框，该下拉框是ccbpm为您计算出来的可以发送到的节点。
   - 由操作者来决定要发送到那个节点上去。
   - 如果您选择的节点的接收人规则是由上一步发送人员来选择的，系统就会弹出接受人按钮。
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/Img/CondModel1.png "屏幕截图.png") 
  
  `);r(this,"Desc2",`
  #### 帮助
   - 该模式多用于分合流节点。
   - 在异表单的合流节点上配置该模式，合流节点的操作员发送后，就转到该页面上，选择到达的节点。

   #### 图例
   - ![输入图片说明](/resource/WF/Admin/Img/CondModel2.png "屏幕截图.png") 
   - 也可以用于协作模式下的节点,到达的节点是需要最后一个人审核,由最后一个人选择到达的节点与接受人。
  `);r(this,"Desc3",`
  #### 帮助
   - 用户发送的时候，在发送按钮有选择按钮，是ccbpm为您计算出来的可以发送到的节点。
   - 是由操作者来决定要发送到那个节点上去。
   - 如果您选择的节点的接收人规则是由上一步发送人员来选择的，系统就会弹出接受人按钮。 
  
  `);this.PageTitle="转向规则"}Init(){this.entity=new h,this.KeyOfEn="CondModel",this.Icon="icon-directions",this.Btns=[{pageNo:"0",list:["优先级"]}],this.AddGroup("A","自动计算"),this.Blank("0","由连接线条件控制",this.Desc0),this.AddGroup("B","主观选择"),this.SingleCheckBox("2","下拉框模式","退回节点是否现在工具栏?",s.IsShowReturnNodeInToolbar,this.Desc1),this.SingleCheckBox("3","按钮模式","退回节点是否现在工具栏?",s.IsShowReturnNodeInToolbar,this.Desc3),this.Blank("1"," 发送后手工选择到达节点与接受人",this.Desc2)}AfterSave(o,i){if(o==i)throw new Error("Method not implemented.")}BtnClick(o,i,n){if(n=="优先级"){this.RefPKVal;const m=c.UrlEn("TS.WF.Template.NodeDir",this.entity.NodeID);return new g(u.OpenUrlByDrawer75,m)}if(o==i||o===n)throw new Error("Method not implemented.")}}export{x as GPE_CondModel};
