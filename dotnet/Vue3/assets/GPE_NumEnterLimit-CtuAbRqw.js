var c=Object.defineProperty;var l=(i,e,t)=>e in i?c(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var h=(i,e,t)=>l(i,typeof e!="symbol"?e+"":e,t);var u=(i,e,t)=>new Promise((n,s)=>{var a=r=>{try{m(t.next(r))}catch(p){s(p)}},o=r=>{try{m(t.throw(r))}catch(p){s(p)}},m=r=>r.done?n(r.value):Promise.resolve(r.value).then(a,o);m((t=t.apply(i,e)).next())});import{bb as E,ba as f,aL as y}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class D extends A{constructor(){super("GPE_NumEnterLimit");h(this,"Desc1",` 
  #### 帮助
   - 禁用：不对格式有限制。
   - 启用限制：请按照格式输入值, 比如:您的输入在18岁到40岁,请输入18-40。
  `);h(this,"Desc2",` 
  1. 请按照格式输入值, 比如:您的输入在18岁到40岁,请输入18-40。
  2. 中间不要有空格区分大小写,不能用全角的减号。
  3. 如果您会写正则表达式，您也可以使用绑定函数正则来实现。
....`);this.PageTitle="输入值限制"}Init(){this.entity=new E,this.KeyOfEn=f.DoWay,this.AddGroup("A","数值输入值限制"),this.Blank("0","禁用",this.Desc1),this.SingleTB("1","启用限制","",this.Desc2,"格式:0-18")}AfterSave(t,n){return u(this,null,function*(){var o;if(this.entity==null)return;const s=(o=this.entity)==null?void 0:o.AttrOfOper,a=new y(s);yield a.Retrieve(),t==="0"?a.SetPara("NumEnterLimit",""):a.SetPara("NumEnterLimit",this.entity.Tag),n!=null})}BtnClick(t,n,s){if(t==n||t===s)throw new Error("Method not implemented.")}}export{D as GPE_NumEnterLimit};
