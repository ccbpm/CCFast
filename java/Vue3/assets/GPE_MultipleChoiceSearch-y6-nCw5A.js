var m=Object.defineProperty;var u=(o,i,e)=>i in o?m(o,i,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[i]=e;var p=(o,i,e)=>u(o,typeof i!="symbol"?i+"":i,e);var s=(o,i,e)=>new Promise((a,r)=>{var t=c=>{try{h(e.next(c))}catch(n){r(n)}},l=c=>{try{h(e.throw(c))}catch(n){r(n)}},h=c=>c.done?a(c.value):Promise.resolve(c.value).then(t,l);h((e=e.apply(o,i)).next())});import{b as M,a as S}from"./MapExt-DtQWKcAY.js";import{MultipleChoiceSearchEn1 as g}from"./MultipleChoiceSearchEn1-By7IOpY8.js";import{MultipleChoiceSearchEn2 as E}from"./MultipleChoiceSearchEn2-B9sTqxIk.js";import{PageBaseGroupEdit as d}from"./PageBaseGroupEdit-IicyYiex.js";import{MapAttr as C}from"./MapAttr-B1mxD3vP.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";import"./Events-D9tOL1Ad.js";class L extends d{constructor(){super("GPE_MultipleChoiceSearch");p(this,"Desc0",`
  #### 帮助
   - 在配置页面中配置好数据源，那么录入的时候可以根据录入的关键词，搜素出相关的内容。
  #### 应用场景
   - 比如，申请人，申请原因等；
  #### 搜索选择-效果图
  - 搜索选择
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearch.png "屏幕截图.png")
  #### 搜索+输入多选-效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleInputSearch.png "屏幕截图.png")
  `);p(this,"Desc1",`
  #### 帮助
   - 搜索选择:选择的范围必须是在数据源中.
   - 可以通过关键字，进行搜索数据源的内容.
  #### 配置图
  
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearchPeizhi.png "数据源配置")
  #### 效果图
  
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearch.png "运行效果")
  `);p(this,"Desc2",`
  #### 帮助
  - 搜索选择+输入多选:选择的范围是数据源中的，也可以是录入的.
  - 可以通过关键字，进行搜索数据源的内容进行选择也可以输入数据.
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearchPeizhi2.png "数据源配置")
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleInputSearch.png "屏幕截图.png")
  `);this.PageTitle="搜索选择"}BtnClick(e,a,r){if(e==a||e===r)throw new Error("Method not implemented.")}AfterSave(e,a){return s(this,null,function*(){if(e!="None"){let r=this.GetRequestVal("PKVal");r.endsWith("_MultipleChoiceSearch")&&(r=r.replace("_MultipleChoiceSearch",""));const t=new C,l=r;t.setPKVal(r+"T"),(yield t.RetrieveFromDBSources())==0&&(t.setPKVal(l),yield t.RetrieveFromDBSources(),t.MyPK=t.MyPK+"T",t.KeyOfEn=t.KeyOfEn+"T",t.Name=t.Name+"T",t.UIVisible=!1,t.UIIsEnable=!1,yield t.Insert())}if(e==a)throw new Error("Method not implemented.")})}Init(){return s(this,null,function*(){this.entity=new M,this.KeyOfEn=S.DoWay,yield this.entity.InitDataForMapAttr("MultipleChoiceSearch",this.GetRequestVal("PKVal")),this.AddGroup("A","搜索选择"),this.Blank("0","不设置",this.Desc0),this.AddEntity("1","搜索选择",new g,this.Desc1),this.AddEntity("2","搜索选择+输入多选",new E,this.Desc2)})}}export{L as GPE_MultipleChoiceSearch};
