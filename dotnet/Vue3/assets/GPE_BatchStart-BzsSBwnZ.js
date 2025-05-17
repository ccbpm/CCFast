var n=Object.defineProperty;var h=(i,e,t)=>e in i?n(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var o=(i,e,t)=>h(i,typeof e!="symbol"?e+"":e,t);import{aD as a}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as p}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Flow as m}from"./Flow-D1QXg1UO.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class E extends p{constructor(){super("GPE_BatchStart");o(this,"Desc0",` 
  #### 帮助
   - 不启动：不启动流程的批量发起。
   - 启用批量发起:一次批量发起多个流程。
 
     
    `);o(this,"Desc1",` 
  #### 帮助
   
    
    `);this.PageTitle="批量发起"}Init(){this.entity=new m,this.KeyOfEn="BatchListCount",this.AddGroup("A","批量发起"),this.Blank("0","不启用",this.Desc0),this.SingleTB("1","启用批量发起","BatchListCount",this.Desc1,"一次发起多少条记录",a.AppString)}AfterSave(t,r){if(t==r)throw new Error("Method not implemented.")}BtnClick(t,r,s){if(t==r||t===s)throw new Error("Method not implemented.")}}export{E as GPE_BatchStart};
