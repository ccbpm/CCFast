var g=Object.defineProperty;var F=(e,i,t)=>i in e?g(e,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):e[i]=t;var n=(e,i,t)=>F(e,typeof i!="symbol"?i+"":i,t);var a=(e,i,t)=>new Promise((l,r)=>{var u=m=>{try{s(t.next(m))}catch(o){r(o)}},p=m=>{try{s(t.throw(m))}catch(o){r(o)}},s=m=>m.done?l(m.value):Promise.resolve(m.value).then(u,p);s((t=t.apply(e,i)).next())});import{b as c,a as A}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as D}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class y extends D{constructor(){super("GPE_DtlNumFiledSumAvg");n(this,"Desc1",`
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
   
  .`);n(this,"Desc0",`
  #### 帮助
  - 禁用：不对从表列进行计算。
  - 对当前字段是从表有效。
  - 对从表的列，进行求和，求平均、求最大、求最小计算显示。
  - 求出来的数据呈现在从表的底部。
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DtlNumFiledSumAvg/Img/AutoFullDtlXianshi.png "屏幕截图.png") 


  `);this.PageTitle="求合,平均显示"}Init(){return a(this,null,function*(){this.entity=new c,this.KeyOfEn=A.DoWay,yield this.entity.InitDataForMapAttr("NumFiledSumAvg",this.GetRequestVal("PKVal")),this.AddGroup("A","扫码录入"),this.Blank("0","禁用",this.Desc0),this.Blank("1","显示合计",this.Desc1),this.Blank("2","显示平均数",this.Desc1),this.Blank("3","显示最大",this.Desc1),this.Blank("4","显示最小",this.Desc1)})}BtnClick(t,l,r){}AfterSave(t,l){return a(this,null,function*(){if(t==l)throw new Error("Method not implemented.")})}}export{y as GPE_DtlNumFiledSumAvg};
