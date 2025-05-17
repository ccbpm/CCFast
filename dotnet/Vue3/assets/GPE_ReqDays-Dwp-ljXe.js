var g=Object.defineProperty;var D=(s,e,t)=>e in s?g(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var p=(s,e,t)=>D(s,typeof e!="symbol"?e+"":e,t);var m=(s,e,t)=>new Promise((i,r)=>{var y=a=>{try{n(t.next(a))}catch(o){r(o)}},c=a=>{try{n(t.throw(a))}catch(o){r(o)}},n=a=>a.done?i(a.value):Promise.resolve(a.value).then(y,c);n((t=t.apply(s,e)).next())});import{bb as d,ba as h}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as E}from"./PageBaseGroupEdit-JIgqoTiq.js";import{GPEReqDays as q}from"./GPEReqDays-D-8gKSrr.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class f extends E{constructor(){super("GPE_ReqDays");p(this,"Desc1",`
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
  
  `);this.PageTitle="求两个日期之差"}Init(){return m(this,null,function*(){this.entity=new d,this.KeyOfEn=h.DoWay,yield this.entity.InitDataForMapAttr("ReqDays",this.GetRequestVal("PKVal")),this.AddGroup("A","求两个日期之差"),this.Blank("0","不启用",this.Desc2),this.AddEntity("1","选择日期",new q,this.Desc1)})}AfterSave(t,i){if(t==i)throw new Error("Method not implemented.")}BtnClick(t,i,r){if(t==i||t===r)throw new Error("Method not implemented.")}}export{f as GPE_ReqDays};
