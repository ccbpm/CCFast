var F=Object.defineProperty;var h=(s,e,t)=>e in s?F(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var m=(s,e,t)=>h(s,typeof e!="symbol"?e+"":e,t);var d=(s,e,t)=>new Promise((o,r)=>{var c=i=>{try{n(t.next(i))}catch(a){r(a)}},l=i=>{try{n(t.throw(i))}catch(a){r(a)}},n=i=>i.done?o(i.value):Promise.resolve(i.value).then(c,l);n((t=t.apply(s,e)).next())});import{aM as f}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as N}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Node as p,NodeAttr as g}from"./Node-B6HRFhwD.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";import"./EntityNodeID-De9k9loD.js";class G extends N{constructor(){super("GPE_FrmPos");m(this,"DescCurrentFrm",`
  #### 说明
   - 是使用当前节点的表单.
  `);m(this,"DescEtc",`
  #### 帮助
   - 支持编写js,Html模式，使用富文本编辑器开发.
  #### 样式
  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/kaifaze.png "屏幕截图.png")
  
  `);m(this,"DescPri",`
  #### 帮助
   - 支持编写js,Html模式，使用富文本编辑器开发.
  #### 样式
  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/kaifaze.png "屏幕截图.png")
  
  `);this.PageTitle="表单引用",this.Btns="帮助"}Init(){this.entity=new p,this.KeyOfEn="NodeFrmRef",this.AddGroup("A","引用位置"),this.Blank("0","当前节点表单",this.DescCurrentFrm),this.SelectItemsByList("1","引用其他节点表单",this.DescEtc,!1,f.sqlNodeFrmList,g.NodeFrmID)}AfterSave(t,o){return d(this,null,function*(){if(t==="0"){const r=new p;r.NodeID=this.PKVal,yield r.Retrieve(),r.NodeFrmID="",yield r.Update()}})}BtnClick(t,o,r){if(t==o||t===r)throw new Error("Method not implemented.")}}export{G as GPE_FrmPos};
