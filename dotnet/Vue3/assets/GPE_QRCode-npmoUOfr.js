var m=Object.defineProperty;var l=(i,e,t)=>e in i?m(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var a=(i,e,t)=>l(i,typeof e!="symbol"?e+"":e,t);var p=(i,e,t)=>new Promise((o,r)=>{var c=s=>{try{n(t.next(s))}catch(h){r(h)}},d=s=>{try{n(t.throw(s))}catch(h){r(h)}},n=s=>s.done?o(s.value):Promise.resolve(s.value).then(c,d);n((t=t.apply(i,e)).next())});import{bb as E,ba as D}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as f}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class M extends f{constructor(){super("GPE_QRCode");a(this,"Desc0",`
  #### 帮助
   - 禁用：不使用扫码录入。
   - 二维码：适用于移动端，扫二维码获得一些信息，进行一些操作。
   - 条码：适用于移动端，扫条码获得一些信息，进行一些操作。
  #### 场景
   - 利用扫描枪或手机扫描功能，把带有识别码的物品信息录入到系统中。
   - 比如：维修工具归还流程中，当工具归还时，可以利用外置扫描枪，扫描工具上的识别码（二维码或条码）。
   - 把工具的编号，名称等信息自动录入到流程表单中。
   
  

  `);a(this,"Desc1",`
  #### 帮助
   - 用户向通过扫描二维码之后获得一些信息，进行一些操作。
   - 我们把这个场景叫做二维码，扫描。
  `);a(this,"Desc2",`
  #### 帮助
   - 适用于移动端，扫条码获得一些信息，进行一些操作
  `);this.PageTitle="扫码录入"}Init(){return p(this,null,function*(){this.entity=new E,this.KeyOfEn=D.DoWay,yield this.entity.InitDataForMapAttr("QRCode",this.GetRequestVal("PKVal")),this.AddGroup("A","扫码录入"),this.Blank("0","禁用",this.Desc0),this.Blank("1","二维码",this.Desc1),this.Blank("2","条码",this.Desc2)})}AfterSave(t,o){if(t==o)throw new Error("Method not implemented.")}BtnClick(t,o,r){if(t==o||t===r)throw new Error("Method not implemented.")}}export{M as GPE_QRCode};
