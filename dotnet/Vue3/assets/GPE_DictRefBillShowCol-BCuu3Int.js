var h=Object.defineProperty;var f=(r,e,t)=>e in r?h(r,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[e]=t;var a=(r,e,t)=>f(r,typeof e!="symbol"?e+"":e,t);var p=(r,e,t)=>new Promise((o,i)=>{var c=s=>{try{m(t.next(s))}catch(n){i(n)}},l=s=>{try{m(t.throw(s))}catch(n){i(n)}},m=s=>s.done?o(s.value):Promise.resolve(s.value).then(c,l);m((t=t.apply(r,e)).next())});import{MethodDictRefBill as w}from"./MethodDictRefBill-Du9wvo-g.js";import{PageBaseGroupEdit as d}from"./PageBaseGroupEdit-JIgqoTiq.js";import{bc as y}from"./entry/index-M8VErHPE-1727507756861.js";import"./Method-D3vubAhY.js";import"./GroupMethod-Cc_Vx1Kw.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFColumn-Q_PoS_2g.js";import"./PCenter-CGZJ3ajQ.js";import"./PowerCenter-B1QbxnUu.js";import"./Help-D0bDMZWg.js";class O extends d{constructor(){super("GPE_DictRefBillShowCol");a(this,"Desc0",`
  #### 帮助
   - 显示所有的列.
   - 这些列是按照实体的Attr是否可见与顺序决定的.
`);a(this,"Desc1",`
  #### 帮助
  - 选择要显示的列.
  - 系统列出的是所有可以显示的列.
  `);this.PageTitle="显示列"}Init(){return p(this,null,function*(){this.entity=new w,this.KeyOfEn="Tag3",this.AddGroup("A","显示列"),this.Blank("0","所有的列",this.Desc0),this.SelectItemsByList("1","指定的选择列",this.Desc1,!0,yield this.GenerAttrs(),"Docs")})}GenerAttrs(){return p(this,null,function*(){const t=new y;yield t.Retrieve("FK_MapData",this.PKVal);const o=t.filter(i=>!!i.UIVisible).map(i=>({Name:i.Name,No:i.KeyOfEn}));return JSON.stringify(o)})}AfterSave(t,o){if(t==o)throw new Error("Method not implemented.")}BtnClick(t,o,i){if(t==o||t===i)throw new Error("Method not implemented.")}}export{O as GPE_DictRefBillShowCol};
