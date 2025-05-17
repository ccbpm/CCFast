var p=Object.defineProperty;var w=(s,e,t)=>e in s?p(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var h=(s,e,t)=>w(s,typeof e!="symbol"?e+"":e,t);var l=(s,e,t)=>new Promise((o,r)=>{var m=i=>{try{n(t.next(i))}catch(a){r(a)}},c=i=>{try{n(t.throw(i))}catch(a){r(a)}},n=i=>i.done?o(i.value):Promise.resolve(i.value).then(m,c);n((t=t.apply(s,e)).next())});import{aH as f}from"./entry/index-M8VErHPE-1727507756861.js";import{E as d}from"./EnCfg-4nSmBEOT.js";import{PageBaseGroupEdit as E}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class B extends E{constructor(){super("GPE_ShowCol");h(this,"Desc0",`
  #### 帮助
   - 显示所有的列.
   - 这些列是按照实体的Attr是否可见与顺序决定的.
`);h(this,"Desc1",`
  #### 帮助
  - 选择要显示的列.
  - 系统列出的是所有可以显示的列.
  `);this.PageTitle="显示列"}Init(){return l(this,null,function*(){this.entity=new d,this.KeyOfEn="ShowColModel",this.AddGroup("A","显示列"),this.Blank("0","所有的列",this.Desc0),this.SelectItemsByList("1","指定的选择列",this.Desc1,!0,yield this.GenerAttrs(),"ShowCols")})}GenerAttrs(){return l(this,null,function*(){const o=(yield f.GetEn(this.PKVal))._enMap.attrs.filter(r=>!!r.UIVisible).map(r=>({Name:r.Desc,No:r.Key}));return JSON.stringify(o)})}AfterSave(t,o){if(t==o)throw new Error("Method not implemented.")}BtnClick(t,o,r){if(t==o||t===r)throw new Error("Method not implemented.")}}export{B as GPE_ShowCol};
