var h=Object.defineProperty;var E=(a,e,t)=>e in a?h(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var m=(a,e,t)=>E(a,typeof e!="symbol"?e+"":e,t);var l=(a,e,t)=>new Promise((n,i)=>{var r=o=>{try{s(t.next(o))}catch(p){i(p)}},N=o=>{try{s(t.throw(o))}catch(p){i(p)}},s=o=>o.done?n(o.value):Promise.resolve(o.value).then(r,N);s((t=t.apply(a,e)).next())});import{bb as c,ba as D,aB as P,aC as S}from"./entry/index-M8VErHPE-1727507756861.js";import{MENoNameP0 as u}from"./MENoNameP0-CO43FZIR.js";import{GloComm as A}from"./GloComm-DZ1gELjv.js";import{PageBaseGroupEdit as B}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Help-D0bDMZWg.js";class I extends B{constructor(){super("GPE_PageLoadFullDDL");m(this,"Desc0",`
 
  #### 帮助
   - 不设置(默认)：不对主表数据进行填充。
   - 设置下拉框填充：返回一个数据源用来填充下拉框，该行里必须有No,Name两个字段 No就是下拉框的值，Name就是下拉框的标签。
  
  `);m(this,"Desc1",`
  #### 帮助
   - 填充下拉框的SQL。
   - 返回一个数据源用来填充下拉框，该行里必须有No,Name两个字段 No就是下拉框的值，Name就是下拉框的标签。
   - 实例(选择的人员的角色下拉框)： SELECT B.FK_Station AS No, A.Name FROM Port_Station A, Port_DeptEmpStation B WHERE B.FK_Emp='@Key' AND B.Station=A.No
   - @Key 系统约定的选择的编号。
  `);this.PageTitle="装载填充"}Init(){return l(this,null,function*(){this.entity=new c,this.KeyOfEn=D.DoWay,this.Btns=[{pageNo:"2",list:["字典维护"]}],yield this.entity.InitDataForMapAttr("PageLoadFullDDL",this.GetRequestVal("PKVal"),"0"),this.AddGroup("A","装载填充"),this.Blank("0","不设置(默认)",this.Desc0),this.SingleTBSQL("1","自定义填充",D.Doc,this.Desc1),this.AddEntity("2","绑定字典填充",new u,this.Desc1)})}BtnClick(t,n,i){if(i=="字典维护"){const r=A.UrlSearch("TS.FrmUI.SFTable");return new P(S.OpenUrlByDrawer75,r)}}AfterSave(t){}}export{I as GPE_PageLoadFullDDL};
