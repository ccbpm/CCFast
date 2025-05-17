var a=Object.defineProperty;var s=(e,t,i)=>t in e?a(e,t,{enumerable:!0,configurable:!0,writable:!0,value:i}):e[t]=i;var p=(e,t,i)=>s(e,typeof t!="symbol"?t+"":t,i);import{MapData as n,MapDataAttr as o}from"./MapData-lfC2UY9r.js";import{PageBaseGroupEdit as l}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EnumLab-CzismWql.js";import"./Help-D0bDMZWg.js";class B extends l{constructor(){super("GPE_FrmType");p(this,"Desc0",`  
  #### 帮助
  - 使用表格的方式编辑数据，如下图.
  - 适用于列较少，数据量小，编辑简单直观. 
  #### 图例
  - 表格模式
  -
  `);p(this,"Desc1",`
  #### 帮助
  - 使用表单的方式编辑数据，如下图.
  - 适用于列较多，有孙表，编辑新建需要弹窗.  
  #### 列表图例
  - 点击红色的区域，新建与编辑.
  - 列表的数据都是只读的.
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/EditModel/Card1.png "表格模式")  
  #### 编辑图例
  - 从表就是一个新的表单.
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/EditModel/Card2.png "表格模式")  
  - 可以使用【保存并新建】，【删除】等操作.
  `);p(this,"Desc2",`
  #### 帮助
  - 同经典表单，只是表单的展示不同.
  `);this.PageTitle="表单工作模式"}Init(){this.entity=new n,this.KeyOfEn=o.FrmType,this.AddGroup("A","表单工作模式"),this.Blank("0","经典表单",this.HelpUn),this.Blank("9","开发者表单",this.HelpUn),this.Blank("10","章节表单",this.HelpUn),this.Blank("6","VSTO表单",this.HelpUn)}BtnClick(i,r,m){}AfterSave(i,r){}}export{B as GPE_FrmType};
