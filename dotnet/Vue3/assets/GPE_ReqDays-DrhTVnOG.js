var g=Object.defineProperty;var D=(i,e,t)=>e in i?g(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var p=(i,e,t)=>D(i,typeof e!="symbol"?e+"":e,t);var m=(i,e,t)=>new Promise((s,a)=>{var y=r=>{try{o(t.next(r))}catch(n){a(n)}},c=r=>{try{o(t.throw(r))}catch(n){a(n)}},o=r=>r.done?s(r.value):Promise.resolve(r.value).then(y,c);o((t=t.apply(i,e)).next())});import{b as d,a as h}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as E}from"./PageBaseGroupEdit-IicyYiex.js";import{GPEReqDays as q}from"./GPEReqDays-C-fTrwNK.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class I extends E{constructor(){super("GPE_ReqDays");p(this,"Desc1",`
  #### 帮助
  - 应用场景：在请假单中，根据请假日期从，请假日期到的差值，自动计算请假天数。
  #### 配置图例1
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysBiaodan.png "屏幕截图.png") 
  - 配置图例2
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysBiaodan2.png "屏幕截图.png") 
  #### 运行图例
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysYanshi.png "屏幕截图.png") 
  `);p(this,"Desc2",`
  #### 帮助
   - 对两个日期求差，日期1-日期2= 天数.
   - 得出的值是一个整形的数值。
  #### 应用场景
   - 根据请假日期从，请假日期到自动计算请假天数.
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysYanshi.png "屏幕截图.png") 
  
  `);this.PageTitle="求两个日期之差"}Init(){return m(this,null,function*(){this.entity=new d,this.KeyOfEn=h.DoWay,yield this.entity.InitDataForMapAttr("ReqDays",this.GetRequestVal("PKVal")),this.AddGroup("A","求两个日期之差"),this.Blank("0","不启用",this.Desc2),this.AddEntity("1","选择日期",new q,this.Desc1)})}AfterSave(t,s){if(t==s)throw new Error("Method not implemented.")}BtnClick(t,s,a){if(t==s||t===a)throw new Error("Method not implemented.")}}export{I as GPE_ReqDays};
