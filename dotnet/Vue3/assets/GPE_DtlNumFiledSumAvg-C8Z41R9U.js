var p=Object.defineProperty;var F=(e,i,t)=>i in e?p(e,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):e[i]=t;var r=(e,i,t)=>F(e,typeof i!="symbol"?i+"":i,t);var u=(e,i,t)=>new Promise((m,s)=>{var o=l=>{try{n(t.next(l))}catch(a){s(a)}},g=l=>{try{n(t.throw(l))}catch(a){s(a)}},n=l=>l.done?m(l.value):Promise.resolve(l.value).then(o,g);n((t=t.apply(e,i)).next())});import{bb as c,ba as A}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as D}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class N extends D{constructor(){super("GPE_DtlNumFiledSumAvg");r(this,"Desc1",`
  #### 帮助
   - 对从表的列，进行求和，求平均、求最大、求最小计算显示。
   - 求出来的数据呈现在从表的底部。
   - 对当前字段是从表有效。
   - 配置图例1
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DtlNumFiledSumAvg/Img/AutoFullDtl1.png "屏幕截图.png") 

   - 配置图例2
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DtlNumFiledSumAvg/Img/AutoFullDtl.png "屏幕截图.png") 
 
   - 配置图例3
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DtlNumFiledSumAvg/Img/AutoFullDtl2.png "屏幕截图.png") 
  
   - 运行图例
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DtlNumFiledSumAvg/Img/AutoFullDtlXianshi.png "屏幕截图.png") 
   
  .`);r(this,"Desc0",`
  #### 帮助
  - 禁用：不对从表列进行计算。
  - 对当前字段是从表有效。
  - 对从表的列，进行求和，求平均、求最大、求最小计算显示。
  - 求出来的数据呈现在从表的底部。
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DtlNumFiledSumAvg/Img/AutoFullDtlXianshi.png "屏幕截图.png") 


  `);this.PageTitle="求合,平均显示"}Init(){return u(this,null,function*(){this.entity=new c,this.KeyOfEn=A.DoWay,yield this.entity.InitDataForMapAttr("NumFiledSumAvg",this.GetRequestVal("PKVal")),this.AddGroup("A","扫码录入"),this.Blank("0","禁用",this.Desc0),this.Blank("1","显示合计",this.Desc1),this.Blank("2","显示平均数",this.Desc1),this.Blank("3","显示最大",this.Desc1),this.Blank("4","显示最小",this.Desc1)})}BtnClick(t,m,s){}AfterSave(t,m){return u(this,null,function*(){if(t==m)throw new Error("Method not implemented.")})}}export{N as GPE_DtlNumFiledSumAvg};
