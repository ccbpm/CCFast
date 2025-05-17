var F=Object.defineProperty;var d=(o,i,t)=>i in o?F(o,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[i]=t;var u=(o,i,t)=>d(o,typeof i!="symbol"?i+"":i,t);var p=(o,i,t)=>new Promise((r,l)=>{var s=e=>{try{n(t.next(e))}catch(m){l(m)}},a=e=>{try{n(t.throw(e))}catch(m){l(m)}},n=e=>e.done?r(e.value):Promise.resolve(e.value).then(s,a);n((t=t.apply(o,i)).next())});import{b as A,a as c}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as g}from"./PageBaseGroupEdit-IicyYiex.js";import{GPEAutoFullDtlField as E}from"./GPEAutoFullDtlField-CC9cZu3-.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class W extends g{constructor(){super("GPE_AutoFullDtlField");u(this,"Desc0",`
  #### 帮助
   - 不启用：不对主表字段进行从表列的数学计算。
   - 对从表列求值：当前是主表字段，对从表的列进行求和、平均、最大、最小计算. 
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFull.png "屏幕截图.png") 
  `);u(this,"Desc1",`

  #### 帮助
  - 当前是主表字段，对从表的列进行求和、平均、最大、最小计算.  
  - 点击设置进入详细设置.
  - 配置图例1
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFullBiaodan.png "屏幕截图.png") 
  - 配置图例2
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFullBiaodan2.png "屏幕截图.png") 

  - 运行图例
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDtlField/Img/AutoFull.png "屏幕截图.png") 

  `);this.PageTitle="对从表列求值"}Init(){return p(this,null,function*(){this.entity=new A,this.KeyOfEn=c.DoWay,yield this.entity.InitDataForMapAttr("NumEnterLimit",this.GetRequestVal("PKVal")),this.AddGroup("A","对从表列求值"),this.Blank("0","不启用",this.Desc0),this.AddEntity("1","启用设置",new E,this.Desc1)})}AfterSave(t,r){if(t==r)throw new Error("Method not implemented.")}BtnClick(t,r,l){if(t==r||t===l)throw new Error("Method not implemented.")}}export{W as GPE_AutoFullDtlField};
