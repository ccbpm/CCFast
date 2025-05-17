var c=Object.defineProperty;var l=(r,i,t)=>i in r?c(r,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[i]=t;var h=(r,i,t)=>l(r,typeof i!="symbol"?i+"":i,t);var u=(r,i,t)=>new Promise((s,n)=>{var o=e=>{try{a(t.next(e))}catch(p){n(p)}},m=e=>{try{a(t.throw(e))}catch(p){n(p)}},a=e=>e.done?s(e.value):Promise.resolve(e.value).then(o,m);a((t=t.apply(r,i)).next())});import{MapAttr as E}from"./MapAttr-B1mxD3vP.js";import{b as f,a as y}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class g extends A{constructor(){super("GPE_NumEnterLimit");h(this,"Desc1",` 
  #### 帮助
   - 禁用：不对格式有限制。
   - 启用限制：请按照格式输入值, 比如:您的输入在18岁到40岁,请输入18-40。
  `);h(this,"Desc2",` 
  1. 请按照格式输入值, 比如:您的输入在18岁到40岁,请输入18-40。
  2. 中间不要有空格区分大小写,不能用全角的减号。
  3. 如果您会写正则表达式，您也可以使用绑定函数正则来实现。
....`);this.PageTitle="输入值限制"}Init(){this.entity=new f,this.KeyOfEn=y.DoWay,this.AddGroup("A","数值输入值限制"),this.Blank("0","禁用",this.Desc1),this.SingleTB("1","启用限制","",this.Desc2,"格式:0-18")}AfterSave(t,s){return u(this,null,function*(){var m;if(this.entity==null)return;const n=(m=this.entity)==null?void 0:m.AttrOfOper,o=new E(n);yield o.Retrieve(),t==="0"?o.SetPara("NumEnterLimit",""):o.SetPara("NumEnterLimit",this.entity.Tag),s!=null})}BtnClick(t,s,n){if(t==s||t===n)throw new Error("Method not implemented.")}}export{g as GPE_NumEnterLimit};
