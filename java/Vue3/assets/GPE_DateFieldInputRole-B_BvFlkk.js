var c=Object.defineProperty;var D=(r,i,t)=>i in r?c(r,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[i]=t;var n=(r,i,t)=>D(r,typeof i!="symbol"?i+"":i,t);var l=(r,i,t)=>new Promise((o,e)=>{var a=p=>{try{s(t.next(p))}catch(m){e(m)}},d=p=>{try{s(t.throw(p))}catch(m){e(m)}},s=p=>p.done?o(p.value):Promise.resolve(p.value).then(a,d);s((t=t.apply(r,i)).next())});import{a as F,b as u}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as E}from"./PageBaseGroupEdit-IicyYiex.js";import{GPEDateFieldInputRole as g}from"./GPEDateFieldInputRole-ClKUNFo-.js";import{MapAttr as h}from"./MapAttr-B1mxD3vP.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";import"./Events-D9tOL1Ad.js";class W extends E{constructor(){super("GPE_DateFieldInputRole");n(this,"Desc0",` 
  #### 帮助
   - 不限制，对于日期型数据，不做任何限制。
   - 不能输入历史日期：不是输入之前的日期，只能输入当前日期以及未来日期。
   - 只能输入指定运算符(如大于等于)指定字段的日期，比如请假结束日期，就不能小于请假开始日期。
  #### 应用场景
   - 多数用于对日期有要求的字段，比如请假单，销假单，疫情信息上报日期等。
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DateFieldInputRole/Img/DateFiledInptRole.png "日期限制.png") 
  `);n(this,"Desc1",`
  #### 帮助 
  1. 历史日期禁止输入。
  2. 比如请假日期从,对于当前人员来说,不能输入历史日期。
....`);n(this,"Desc2",` 
  #### 帮助
  1. 比如: 请假日期到，不能大于请假日期从。
  2. 用于限制一个时间点要大于指定字段的时间点。
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DateFieldInputRole/Img/DateFiledInptRoleSetting.png "日期限制.png") 
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DateFieldInputRole/Img/DateFiledInptRole.png "日期限制.png") 
`);this.PageTitle="输入值限制"}Init(){return l(this,null,function*(){this.KeyOfEn=F.DoWay;const t=this.GetRequestVal("PKVal"),o=new h(t);yield o.Retrieve();const e=new u,a="DateFieldInputRole";e.MyPK=o.MyPK+"_"+a,(yield e.RetrieveFromDBSources())==0&&(e.FK_MapData=o.FK_MapData,e.DoWay=0,e.ExtType=a,e.AttrOfOper=o.KeyOfEn,e.Tag1=1,yield e.Insert()),this.entity=e,this.AddGroup("A","日期输入值限制"),this.Blank("0","不限制",this.Desc0),this.Blank("1","不能输入历史日期,只能输入当前日期以及未来日期.",this.Desc1),this.AddEntity("2","只能输入指定运算符(如大于等于)指定字段的日期",new g,this.Desc2)})}AfterSave(t,o){if(t==o)throw new Error("Method not implemented.")}BtnClick(t,o,e){if(t==o||t===e)throw new Error("Method not implemented.")}}export{W as GPE_DateFieldInputRole};
