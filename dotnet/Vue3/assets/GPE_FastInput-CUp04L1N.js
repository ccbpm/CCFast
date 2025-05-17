var F=Object.defineProperty;var h=(e,i,t)=>i in e?F(e,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):e[i]=t;var a=(e,i,t)=>h(e,typeof i!="symbol"?i+"":i,t);var m=(e,i,t)=>new Promise((n,p)=>{var u=r=>{try{s(t.next(r))}catch(o){p(o)}},c=r=>{try{s(t.throw(r))}catch(o){p(o)}},s=r=>r.done?n(r.value):Promise.resolve(r.value).then(u,c);s((t=t.apply(e,i)).next())});import{b as g,a as I}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as d}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class W extends d{constructor(){super("GPE_FastInput");a(this,"Desc0",` 

  #### 帮助
  - 快速录入是为了解决重复数据相同内容的填写。
  - 能够减轻输入人员的劳动，并大幅度提高使用体验。
  - 快速录入是对用户可能要输入的内容，预先存储到数据库中，在用户录入的时候进行选择。
  - 比如：审核意见、 法律法规、请假原因
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/FastInput/Img/FastInput.png "屏幕截图.png")

   `);a(this,"Desc1",` 
  #### 帮助
   - 快速输入内容，多行用@分开。
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/FastInput/Img/FastInputPeizhi.png "屏幕截图.png")
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/FastInput/Img/FastInput.png "屏幕截图.png")

  
   `);this.PageTitle="快速录入"}Init(){return m(this,null,function*(){this.entity=new g,this.KeyOfEn="DoWay",yield this.entity.InitDataForMapAttr("FastInput",this.GetRequestVal("PKVal"),"0"),this.AddGroup("A","快速录入"),this.Blank("0","不启用",this.Desc0),this.SingleTextArea("1","启用快速录入",I.Doc,"@同意",this.Desc1)})}AfterSave(t,n){if(t==n)throw new Error("Method not implemented.")}BtnClick(t,n,p){if(t==n||t===p)throw new Error("Method not implemented.")}}export{W as GPE_FastInput};
