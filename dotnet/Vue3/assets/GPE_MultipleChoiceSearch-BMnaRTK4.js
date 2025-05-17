var m=Object.defineProperty;var u=(c,i,e)=>i in c?m(c,i,{enumerable:!0,configurable:!0,writable:!0,value:e}):c[i]=e;var l=(c,i,e)=>u(c,typeof i!="symbol"?i+"":i,e);var s=(c,i,e)=>new Promise((o,r)=>{var t=a=>{try{p(e.next(a))}catch(n){r(n)}},h=a=>{try{p(e.throw(a))}catch(n){r(n)}},p=a=>a.done?o(a.value):Promise.resolve(a.value).then(t,h);p((e=e.apply(c,i)).next())});import{aL as M,bb as S,ba as g}from"./entry/index-M8VErHPE-1727507756861.js";import{MultipleChoiceSearchEn1 as E}from"./MultipleChoiceSearchEn1-EFJF-NdD.js";import{MultipleChoiceSearchEn2 as d}from"./MultipleChoiceSearchEn2-CJvtfK98.js";import{PageBaseGroupEdit as C}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class K extends C{constructor(){super("GPE_MultipleChoiceSearch");l(this,"Desc0",`
  #### 帮助
   - 在配置页面中配置好数据源，那么录入的时候可以根据录入的关键词，搜素出相关的内容。
  #### 应用场景
   - 比如，申请人，申请原因等；
  #### 搜索选择-效果图
  - 搜索选择
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearch.png "屏幕截图.png")
  #### 搜索+输入多选-效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleInputSearch.png "屏幕截图.png")
  `);l(this,"Desc1",`
  #### 帮助
   - 搜索选择:选择的范围必须是在数据源中.
   - 可以通过关键字，进行搜索数据源的内容.
  #### 配置图
  
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearchPeizhi.png "数据源配置")
  #### 效果图
  
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearch.png "运行效果")
  `);l(this,"Desc2",`
  #### 帮助
  - 搜索选择+输入多选:选择的范围是数据源中的，也可以是录入的.
  - 可以通过关键字，进行搜索数据源的内容进行选择也可以输入数据.
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleChoiceSearchPeizhi2.png "数据源配置")
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/MultipleChoiceSearch/Img/MultipleInputSearch.png "屏幕截图.png")
  `);this.PageTitle="搜索选择"}BtnClick(e,o,r){if(e==o||e===r)throw new Error("Method not implemented.")}AfterSave(e,o){return s(this,null,function*(){if(e!="None"){let r=this.GetRequestVal("PKVal");r.endsWith("_MultipleChoiceSearch")&&(r=r.replace("_MultipleChoiceSearch",""));const t=new M,h=r;t.setPKVal(r+"T"),(yield t.RetrieveFromDBSources())==0&&(t.setPKVal(h),yield t.RetrieveFromDBSources(),t.MyPK=t.MyPK+"T",t.KeyOfEn=t.KeyOfEn+"T",t.Name=t.Name+"T",t.UIVisible=!1,t.UIIsEnable=!1,yield t.Insert())}if(e==o)throw new Error("Method not implemented.")})}Init(){return s(this,null,function*(){this.entity=new S,this.KeyOfEn=g.DoWay,yield this.entity.InitDataForMapAttr("MultipleChoiceSearch",this.GetRequestVal("PKVal")),this.AddGroup("A","搜索选择"),this.Blank("0","不设置",this.Desc0),this.AddEntity("1","搜索选择",new E,this.Desc1),this.AddEntity("2","搜索选择+输入多选",new d,this.Desc2)})}}export{K as GPE_MultipleChoiceSearch};
