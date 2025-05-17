var m=Object.defineProperty;var d=(a,e,t)=>e in a?m(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var l=(a,e,t)=>d(a,typeof e!="symbol"?e+"":e,t);var h=(a,e,t)=>new Promise((s,r)=>{var p=i=>{try{n(t.next(i))}catch(o){r(o)}},c=i=>{try{n(t.throw(i))}catch(o){r(o)}},n=i=>i.done?s(i.value):Promise.resolve(i.value).then(p,c);n((t=t.apply(a,e)).next())});import{bb as D}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as B}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class u extends B{constructor(){super("GPE_FieldFormat");l(this,"Desc0",` 
  #### 定义
  - 字段格式是指输入的格式与呈现的格式.
  - 输入格式: 在文本输入的时候,用特定的风格固定的格式. 比如:身份证格式、关键字格式。
  - 呈现格式: 在只读的模式下,呈现出来的样式,比如:电话、邮件、地址、事件点击以后能够呈现出来的效果.
  - 电话格式:移动端点击的时候直接启动电话.
  - 邮件: 点击的时候启动邮件程序.
  - 地址:移动端点击的时候弹出地址导航。
  - 事件:弹出日历.
  - 关键字: 输入的时候呈现 气泡文字.
   `);this.PageTitle="字段格式"}Init(){return h(this,null,function*(){this.entity=new D,this.KeyOfEn="DoWay",yield this.entity.InitDataForMapAttr("FieldFormat",this.GetRequestVal("PKVal"),"None"),this.AddGroup("A","字段格式"),this.Blank("None","不启用",this.Desc0),this.Blank("Tel","电话-启动电话链接(移动端)",this.Desc0),this.Blank("Email","邮件-启动邮件程序",this.Desc0),this.Blank("Addr","地址-打开导航",this.Desc0),this.Blank("Calendar","事件-加入到日历",this.Desc0),this.Blank("KeyWords","关键字输入",this.Desc0)})}AfterSave(t,s){}BtnClick(t,s,r){}}export{u as GPE_FieldFormat};
