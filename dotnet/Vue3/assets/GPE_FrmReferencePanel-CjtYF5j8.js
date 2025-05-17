var D=Object.defineProperty;var g=(i,e,t)=>e in i?D(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var c=(i,e,t)=>g(i,typeof e!="symbol"?e+"":e,t);var h=(i,e,t)=>new Promise((o,r)=>{var m=s=>{try{l(t.next(s))}catch(n){r(n)}},p=s=>{try{l(t.throw(s))}catch(n){r(n)}},l=s=>s.done?o(s.value):Promise.resolve(s.value).then(m,p);l((t=t.apply(i,e)).next())});import{bb as u,ba as a}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class T extends A{constructor(){super("GPE_FrmReferencePanel");c(this,"Desc0",`
  #### 帮助
   - 用户在录入表单的时候，需要参考一些数据，这些数据在其他系统里，为了方便客户能查看到这些数据，就可以需要使用这个自定义面板。
   - 参考面板就是为了获取其他系统与当前表单相关联的数据而设计的。
  `);c(this,"Desc1",`
  #### 帮助
   - 输入HTML内容
  `);this.PageTitle="参考面板"}Init(){return h(this,null,function*(){this.entity=new u,this.KeyOfEn=a.DoWay,yield this.entity.InitDataForMapData("FrmReferencePanel",this.GetRequestVal("PKVal"),"None"),this.AddGroup("A","静态展示"),this.Blank("None","禁用",this.Desc0),this.SingleTextArea("Html","静态Html脚本",a.Doc,"请输入html内容",this.Desc1),this.SingleTB("Url","静态框架Url",a.Doc,this.Desc1,"请输入url."),this.AddGroup("B","动态展示"),this.SingleTB("ActiveUrl","动态Url",a.Doc,this.Desc1,"请输入url."),this.SingleTB("ActiveHtml","动态Html脚本",a.Doc,this.Desc1,"请输入html内容.")})}BtnClick(t,o,r){}AfterSave(t){}}export{T as GPE_FrmReferencePanel};
