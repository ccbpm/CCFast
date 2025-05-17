var r=Object.defineProperty;var a=(t,e,i)=>e in t?r(t,e,{enumerable:!0,configurable:!0,writable:!0,value:i}):t[e]=i;var o=(t,e,i)=>a(t,typeof e!="symbol"?e+"":e,i);import{DeadLineRole1 as n}from"./DeadLineRole1-DHH0Di2_.js";import{PageBaseGroupEdit as p}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Flow as d}from"./Flow-D1QXg1UO.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class R extends p{constructor(){super("GPE_DeadLineRole");o(this,"Desc0",`
  #### 帮助
   - 不设置，整个流程没有时间限制。
  `);o(this,"Desc1",`
  #### 帮助
   - 该设置，可以为空。
   - 用来限制该流程可以在什么时间段内完成。
   - WF_CH 这个表用于存储时效考核数据,您可以仔细研究该表的结构并做想用的考核数据的使用。
  #### 配置图
   ![输入图片说明](/resource/WF/Admin/AttrFlow/DeadLineRole/Img/DeadLineRole.png "屏幕截图.png")  
  `);this.PageTitle="流程完成时限规则"}Init(){this.entity=new d,this.KeyOfEn="DeadLineRole",this.AddGroup("A","流程完成时限规则"),this.Blank("0","不设置",this.Desc0),this.AddEntity("1","不计算节假日",new n,this.Desc1),this.AddEntity("2","计算节假日",new n,this.Desc1)}BtnClick(i,s,l){}AfterSave(i,s){}}export{R as GPE_DeadLineRole};
