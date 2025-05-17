var E=Object.defineProperty;var c=(a,e,t)=>e in a?E(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var r=(a,e,t)=>c(a,typeof e!="symbol"?e+"":e,t);var n=(a,e,t)=>new Promise((p,i)=>{var D=s=>{try{o(t.next(s))}catch(l){i(l)}},m=s=>{try{o(t.throw(s))}catch(l){i(l)}},o=s=>s.done?p(s.value):Promise.resolve(s.value).then(D,m);o((t=t.apply(a,e)).next())});import{bb as S,ba as h}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as F}from"./PageBaseGroupEdit-JIgqoTiq.js";import{PageLoadFull as u}from"./PageLoadFull-BP7wUlpC.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class A extends F{constructor(){super("GPE_PageLoadFullDtl");r(this,"Desc0",`

  #### 帮助
   - 不设置(默认)：不对从表数据进行填充。
   - 设置从表填充：返回一个数据源，该数据源的列，需要与从表的列匹配，对于匹配上的数据自动填充。
  
  `);r(this,"Desc1",`
  #### 帮助
   - 填充从表的SQL。
   - 返回一个数据源，该数据源的列，需要与从表的列匹配，对于匹配上的数据自动填充。
   - 实例:SELECT * FROM Demo_Resume WHERE RefPK='@Key'
   - @Key 是系统约定的标记，就是选择的编号或者ID。
   - 这个数据源就会清空的方式复制到从表里面去。
  `);this.PageTitle="装载填充从表"}Init(){return n(this,null,function*(){this.entity=new S,this.KeyOfEn=h.DoWay,yield this.entity.InitDataForMapAttr("PageLoadFullDtl",this.GetRequestVal("PKVal")),this.AddGroup("A","装载填充"),this.Blank("None","不设置(默认)",this.Desc0),this.AddEntity("Self","按SQL填充",new u,this.Desc1),this.SelectItemsByGroupList("SFTable","按照查询填充",this.HelpUn,!1,"SELECT No,Name FROM Sys_SFDBSrc ","SELECT No,Name,FK_SFDBSrc FROM sys_sfsearch ","Doc")})}BtnClick(t,p,i){}AfterSave(t){}}export{A as GPE_PageLoadFullDtl};
