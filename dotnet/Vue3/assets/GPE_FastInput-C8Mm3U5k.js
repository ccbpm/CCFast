var F=Object.defineProperty;var h=(i,e,t)=>e in i?F(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var o=(i,e,t)=>h(i,typeof e!="symbol"?e+"":e,t);var m=(i,e,t)=>new Promise((s,r)=>{var u=n=>{try{a(t.next(n))}catch(p){r(p)}},c=n=>{try{a(t.throw(n))}catch(p){r(p)}},a=n=>n.done?s(n.value):Promise.resolve(n.value).then(u,c);a((t=t.apply(i,e)).next())});import{bb as g,ba as I}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as d}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class w extends d{constructor(){super("GPE_FastInput");o(this,"Desc0",` 

  #### 帮助
  - 快速录入是为了解决重复数据相同内容的填写。
  - 能够减轻输入人员的劳动，并大幅度提高使用体验。
  - 快速录入是对用户可能要输入的内容，预先存储到数据库中，在用户录入的时候进行选择。
  - 比如：审核意见、 法律法规、请假原因
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/FastInput/Img/FastInput.png "屏幕截图.png")

   `);o(this,"Desc1",` 
  #### 帮助
   - 快速输入内容，多行用@分开。
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/FastInput/Img/FastInputPeizhi.png "屏幕截图.png")
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/FastInput/Img/FastInput.png "屏幕截图.png")

  
   `);this.PageTitle="快速录入"}Init(){return m(this,null,function*(){this.entity=new g,this.KeyOfEn="DoWay",yield this.entity.InitDataForMapAttr("FastInput",this.GetRequestVal("PKVal"),"0"),this.AddGroup("A","快速录入"),this.Blank("0","不启用",this.Desc0),this.SingleTextArea("1","启用快速录入",I.Doc,"@同意",this.Desc1)})}AfterSave(t,s){if(t==s)throw new Error("Method not implemented.")}BtnClick(t,s,r){if(t==s||t===r)throw new Error("Method not implemented.")}}export{w as GPE_FastInput};
