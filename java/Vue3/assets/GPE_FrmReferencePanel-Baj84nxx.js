var D=Object.defineProperty;var g=(i,e,t)=>e in i?D(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var n=(i,e,t)=>g(i,typeof e!="symbol"?e+"":e,t);var c=(i,e,t)=>new Promise((m,a)=>{var p=r=>{try{l(t.next(r))}catch(o){a(o)}},h=r=>{try{l(t.throw(r))}catch(o){a(o)}},l=r=>r.done?m(r.value):Promise.resolve(r.value).then(p,h);l((t=t.apply(i,e)).next())});import{b as u,a as s}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class F extends A{constructor(){super("GPE_FrmReferencePanel");n(this,"Desc0",`
  #### 帮助
   - 用户在录入表单的时候，需要参考一些数据，这些数据在其他系统里，为了方便客户能查看到这些数据，就可以需要使用这个自定义面板。
   - 参考面板就是为了获取其他系统与当前表单相关联的数据而设计的。
  `);n(this,"Desc1",`
  #### 帮助
   - 输入HTML内容
  `);this.PageTitle="参考面板"}Init(){return c(this,null,function*(){this.entity=new u,this.KeyOfEn=s.DoWay,yield this.entity.InitDataForMapData("FrmReferencePanel",this.GetRequestVal("PKVal"),"None"),this.AddGroup("A","静态展示"),this.Blank("None","禁用",this.Desc0),this.SingleTextArea("Html","静态Html脚本",s.Doc,"请输入html内容",this.Desc1),this.SingleTB("Url","静态框架Url",s.Doc,this.Desc1,"请输入url."),this.AddGroup("B","动态展示"),this.SingleTB("ActiveUrl","动态Url",s.Doc,this.Desc1,"请输入url."),this.SingleTB("ActiveHtml","动态Html脚本",s.Doc,this.Desc1,"请输入html内容.")})}BtnClick(t,m,a){}AfterSave(t){}}export{F as GPE_FrmReferencePanel};
