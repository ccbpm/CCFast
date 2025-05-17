var o=Object.defineProperty;var a=(e,t,i)=>t in e?o(e,t,{enumerable:!0,configurable:!0,writable:!0,value:i}):e[t]=i;var r=(e,t,i)=>a(e,typeof t!="symbol"?t+"":t,i);import{MapDtl as p,MapDtlAttr as d}from"./MapDtl-C1-EwBHu.js";import{PageBaseGroupEdit as l}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";class u extends l{constructor(){super("GPE_EditModel");r(this,"Desc0",`  
  #### 帮助
  - 使用表格的方式编辑数据，如下图.
  - 适用于列较少，数据量小，编辑简单直观. 
  #### 图例
  - 表格模式
  -
  `);r(this,"Desc1",`
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
  `);r(this,"Desc2",`
  #### 帮助
  - 同经典表单，只是表单的展示不同.

  
  `);this.PageTitle="编辑模式"}Init(){this.entity=new p,this.KeyOfEn=d.EditModel,this.AddGroup("A","展示模式"),this.Blank("0","表格模式(默认)",this.Desc0),this.Blank("1","经典表单",this.Desc1),this.Blank("2","开发者表单",this.Desc2)}BtnClick(i,s,n){}AfterSave(i,s){}}export{u as GPE_EditModel};
