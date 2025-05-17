var h=Object.defineProperty;var E=(o,e,t)=>e in o?h(o,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[e]=t;var p=(o,e,t)=>E(o,typeof e!="symbol"?e+"":e,t);var l=(o,e,t)=>new Promise((n,i)=>{var r=a=>{try{s(t.next(a))}catch(m){i(m)}},N=a=>{try{s(t.throw(a))}catch(m){i(m)}},s=a=>a.done?n(a.value):Promise.resolve(a.value).then(r,N);s((t=t.apply(o,e)).next())});import{b as c,a as D}from"./MapExt-DtQWKcAY.js";import{MENoNameP0 as P}from"./MENoNameP0-BuuWG5wk.js";import{GloComm as S}from"./GloComm-CmAl8MpM.js";import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-IicyYiex.js";import{G as A,l as g}from"./entry/index-C6uBgOW5-1730430676707.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./Help-D0bDMZWg.js";class C extends u{constructor(){super("GPE_PageLoadFullDDL");p(this,"Desc0",`
 
  #### 帮助
   - 不设置(默认)：不对主表数据进行填充。
   - 设置下拉框填充：返回一个数据源用来填充下拉框，该行里必须有No,Name两个字段 No就是下拉框的值，Name就是下拉框的标签。
  
  `);p(this,"Desc1",`
  #### 帮助
   - 填充下拉框的SQL。
   - 返回一个数据源用来填充下拉框，该行里必须有No,Name两个字段 No就是下拉框的值，Name就是下拉框的标签。
   - 实例(选择的人员的角色下拉框)： SELECT B.FK_Station AS No, A.Name FROM Port_Station A, Port_DeptEmpStation B WHERE B.FK_Emp='@Key' AND B.Station=A.No
   - @Key 系统约定的选择的编号。
  `);this.PageTitle="装载填充"}Init(){return l(this,null,function*(){this.entity=new c,this.KeyOfEn=D.DoWay,this.Btns=[{pageNo:"2",list:["字典维护"]}],yield this.entity.InitDataForMapAttr("PageLoadFullDDL",this.GetRequestVal("PKVal"),"0"),this.AddGroup("A","装载填充"),this.Blank("0","不设置(默认)",this.Desc0),this.SingleTBSQL("1","自定义填充",D.Doc,this.Desc1),this.AddEntity("2","绑定字典填充",new P,this.Desc1)})}BtnClick(t,n,i){if(i=="字典维护"){const r=S.UrlSearch("TS.FrmUI.SFTable");return new A(g.OpenUrlByDrawer75,r)}}AfterSave(t){}}export{C as GPE_PageLoadFullDDL};
