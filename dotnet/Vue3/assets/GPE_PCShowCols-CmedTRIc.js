var r=Object.defineProperty;var a=(e,t,i)=>t in e?r(e,t,{enumerable:!0,configurable:!0,writable:!0,value:i}):e[t]=i;var s=(e,t,i)=>a(e,typeof t!="symbol"?t+"":t,i);import{MapDtl as p,MapDtlAttr as o}from"./MapDtl-Bo8WkjPG.js";import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class A extends h{constructor(){super("GPE_PCShowCols");s(this,"Desc0",`
  #### 帮助
  - 多个字段用逗号分开
  - 比如: File1,FIle2

  `);s(this,"Desc1",`
  #### 帮助
  - 多个字段用逗号分开
  - 比如: File1,FIle2

  ...`);this.PageTitle="字段展现模式"}Init(){this.entity=new p,this.KeyOfEn=o.EditModel,this.AddGroup("A","展示模式"),this.Blank("0","显示全部",this.Desc0),this.SingleTextArea("1","显示部分字段",o.ShowCols,"多个字段用逗号分开",this.Desc1)}BtnClick(i,l,n){}AfterSave(i,l){}}export{A as GPE_PCShowCols};
