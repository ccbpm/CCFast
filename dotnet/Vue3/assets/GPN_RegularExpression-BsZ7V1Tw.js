var D=Object.defineProperty;var d=(s,a,e)=>a in s?D(s,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[a]=e;var o=(s,a,e)=>d(s,typeof a!="symbol"?a+"":a,e);var g=(s,a,e)=>new Promise((t,i)=>{var m=r=>{try{n(e.next(r))}catch(p){i(p)}},c=r=>{try{n(e.throw(r))}catch(p){i(p)}},n=r=>r.done?t(r.value):Promise.resolve(r.value).then(m,c);n((e=e.apply(s,a)).next())});import{bb as y,ba as E}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class F extends h{constructor(){super("GPN_RegularExpression");o(this,"Desc1",`
  #### 帮助
  - 应用场景：在请假单中，根据请假日期从，请假日期到的差值，自动计算请假天数。

  #### 配置图例1
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysBiaodan.png "屏幕截图.png") 
  - 配置图例2
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysBiaodan2.png "屏幕截图.png") 

  #### 运行图例
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysYanshi.png "屏幕截图.png") 
  `);o(this,"Desc2",`
  #### 帮助
   - 对两个日期求差，日期1-日期2= 天数.
   - 得出的值是一个整形的数值。
  #### 应用场景
   - 根据请假日期从，请假日期到自动计算请假天数.
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ReqDays/Img/ReqDaysYanshi.png "屏幕截图.png") 
  
  `);this.PageTitle="正则表达式"}BtnClick(e,t,i){throw new Error("Method not implemented.{pageID}")}Init(){this.entity=new y,this.KeyOfEn=E.DoWay,this.AddGroup("A","正则表达式"),this.Blank("0","不启用",this.Desc2)}AfterSave(e,t){return g(this,null,function*(){})}}export{F as GPN_RegularExpression};
