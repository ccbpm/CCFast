var d=Object.defineProperty;var f=(t,a,e)=>a in t?d(t,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[a]=e;var n=(t,a,e)=>f(t,typeof a!="symbol"?a+"":a,e);var o=(t,a,e)=>new Promise((r,s)=>{var g=i=>{try{m(e.next(i))}catch(p){s(p)}},h=i=>{try{m(e.throw(i))}catch(p){s(p)}},m=i=>i.done?r(i.value):Promise.resolve(i.value).then(g,h);m((e=e.apply(t,a)).next())});import{PageBaseGroupEdit as c}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Node as T}from"./Node-B6HRFhwD.js";import{aD as l}from"./entry/index-M8VErHPE-1727507756861.js";import"./Help-D0bDMZWg.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EntityNodeID-De9k9loD.js";class E extends c{constructor(){super("GPE_TeamleaderConfirm");n(this,"Desc0",`
  #### 定义
  - 组长确认规则,是指如何在当前节点的接收人集合里确认那些是组长.
  #### 组长的权限
  - 可以邀请其他人处理.
  - 可以执行退回，移交，删除等按钮操作。
  `);this.PageTitle="组长确认规则"}Init(){this.entity=new T,this.KeyOfEn="TeamleaderConfirm",this.AddGroup("A","组长确认规则"),this.Blank("0","都是组长",this.Desc0),this.SingleTB("1","包含如下人员账号的是组长","TeamleaderCfmVal",this.Desc0,"输入人员账号:比如 zhangsan,lisi,",l.AppString),this.SingleTB("2","包含如下人员账号的是组长","TeamleaderCfmVal",this.Desc0,"输入岗位编号:比如 001,002",l.AppString)}AfterSave(e,r){return o(this,null,function*(){})}BtnClick(e,r,s){}}export{E as GPE_TeamleaderConfirm};
