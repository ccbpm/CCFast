var E=Object.defineProperty;var c=(a,e,t)=>e in a?E(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var l=(a,e,t)=>c(a,typeof e!="symbol"?e+"":e,t);var m=(a,e,t)=>new Promise((p,i)=>{var n=s=>{try{o(t.next(s))}catch(r){i(r)}},D=s=>{try{o(t.throw(s))}catch(r){i(r)}},o=s=>s.done?p(s.value):Promise.resolve(s.value).then(n,D);o((t=t.apply(a,e)).next())});import{b as S,a as h}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as F}from"./PageBaseGroupEdit-IicyYiex.js";import{PageLoadFull as u}from"./PageLoadFull-DEGaDCzx.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class M extends F{constructor(){super("GPE_PageLoadFullDtl");l(this,"Desc0",`

  #### 帮助
   - 不设置(默认)：不对从表数据进行填充。
   - 设置从表填充：返回一个数据源，该数据源的列，需要与从表的列匹配，对于匹配上的数据自动填充。
  
  `);l(this,"Desc1",`
  #### 帮助
   - 填充从表的SQL。
   - 返回一个数据源，该数据源的列，需要与从表的列匹配，对于匹配上的数据自动填充。
   - 实例:SELECT * FROM Demo_Resume WHERE RefPK='@Key'
   - @Key 是系统约定的标记，就是选择的编号或者ID。
   - 这个数据源就会清空的方式复制到从表里面去。
  `);this.PageTitle="装载填充从表"}Init(){return m(this,null,function*(){this.entity=new S,this.KeyOfEn=h.DoWay,yield this.entity.InitDataForMapAttr("PageLoadFullDtl",this.GetRequestVal("PKVal")),this.AddGroup("A","装载填充"),this.Blank("None","不设置(默认)",this.Desc0),this.AddEntity("Self","按SQL填充",new u,this.Desc1),this.SelectItemsByGroupList("SFTable","按照查询填充",this.HelpUn,!1,"SELECT No,Name FROM Sys_SFDBSrc ","SELECT No,Name,FK_SFDBSrc FROM sys_sfsearch ","Doc")})}BtnClick(t,p,i){}AfterSave(t){}}export{M as GPE_PageLoadFullDtl};
