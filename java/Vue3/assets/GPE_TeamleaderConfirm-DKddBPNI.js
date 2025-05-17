var d=Object.defineProperty;var f=(a,t,e)=>t in a?d(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var n=(a,t,e)=>f(a,typeof t!="symbol"?t+"":t,e);var o=(a,t,e)=>new Promise((r,s)=>{var g=i=>{try{m(e.next(i))}catch(p){s(p)}},h=i=>{try{m(e.throw(i))}catch(p){s(p)}},m=i=>i.done?r(i.value):Promise.resolve(i.value).then(g,h);m((e=e.apply(a,t)).next())});import{PageBaseGroupEdit as c}from"./PageBaseGroupEdit-IicyYiex.js";import{Node as T}from"./Node-DKGUVcK1.js";import{D as l}from"./entry/index-C6uBgOW5-1730430676707.js";import"./Help-D0bDMZWg.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./EntityNodeID-BCBbJNH2.js";class E extends c{constructor(){super("GPE_TeamleaderConfirm");n(this,"Desc0",`
  #### 定义
  - 组长确认规则,是指如何在当前节点的接收人集合里确认那些是组长.
  #### 组长的权限
  - 可以邀请其他人处理.
  - 可以执行退回，移交，删除等按钮操作。
  `);this.PageTitle="组长确认规则"}Init(){this.entity=new T,this.KeyOfEn="TeamleaderConfirm",this.AddGroup("A","组长确认规则"),this.Blank("0","都是组长",this.Desc0),this.SingleTB("1","包含如下人员账号的是组长","TeamleaderCfmVal",this.Desc0,"输入人员账号:比如 zhangsan,lisi,",l.AppString),this.SingleTB("2","包含如下岗位的是组长","TeamleaderCfmVal",this.Desc0,"输入岗位编号:比如 001,002",l.AppString)}AfterSave(e,r){return o(this,null,function*(){})}BtnClick(e,r,s){}}export{E as GPE_TeamleaderConfirm};
