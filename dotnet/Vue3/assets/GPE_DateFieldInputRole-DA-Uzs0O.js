var c=Object.defineProperty;var D=(o,i,t)=>i in o?c(o,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[i]=t;var p=(o,i,t)=>D(o,typeof i!="symbol"?i+"":i,t);var m=(o,i,t)=>new Promise((a,e)=>{var r=n=>{try{s(t.next(n))}catch(l){e(l)}},d=n=>{try{s(t.throw(n))}catch(l){e(l)}},s=n=>n.done?a(n.value):Promise.resolve(n.value).then(r,d);s((t=t.apply(o,i)).next())});import{ba as F,aL as u,bb as E}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as g}from"./PageBaseGroupEdit-JIgqoTiq.js";import{GPEDateFieldInputRole as h}from"./GPEDateFieldInputRole-CbsKI4pE.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class x extends g{constructor(){super("GPE_DateFieldInputRole");p(this,"Desc0",` 
  #### 帮助
   - 不限制，对于日期型数据，不做任何限制。
   - 不能输入历史日期：不是输入之前的日期，只能输入当前日期以及未来日期。
   - 只能输入指定运算符(如大于等于)指定字段的日期，比如请假结束日期，就不能小于请假开始日期。
  #### 应用场景
   - 多数用于对日期有要求的字段，比如请假单，销假单，疫情信息上报日期等。
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DateFieldInputRole/Img/DateFiledInptRole.png "日期限制.png") 
  `);p(this,"Desc1",`
  #### 帮助 
  1. 历史日期禁止输入。
  2. 比如请假日期从,对于当前人员来说,不能输入历史日期。
....`);p(this,"Desc2",` 
  #### 帮助
  1. 比如: 请假日期到，不能大于请假日期从。
  2. 用于限制一个时间点要大于指定字段的时间点。
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DateFieldInputRole/Img/DateFiledInptRoleSetting.png "日期限制.png") 
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DateFieldInputRole/Img/DateFiledInptRole.png "日期限制.png") 
`);this.PageTitle="输入值限制"}Init(){return m(this,null,function*(){this.KeyOfEn=F.DoWay;const t=this.GetRequestVal("PKVal"),a=new u(t);yield a.Retrieve();const e=new E,r="DateFieldInputRole";e.MyPK=a.MyPK+"_"+r,(yield e.RetrieveFromDBSources())==0&&(e.FK_MapData=a.FK_MapData,e.DoWay=0,e.ExtType=r,e.AttrOfOper=a.KeyOfEn,e.Tag1=1,yield e.Insert()),this.entity=e,this.AddGroup("A","日期输入值限制"),this.Blank("0","不限制",this.Desc0),this.Blank("1","不能输入历史日期,只能输入当前日期以及未来日期.",this.Desc1),this.AddEntity("2","只能输入指定运算符(如大于等于)指定字段的日期",new h,this.Desc2)})}AfterSave(t,a){if(t==a)throw new Error("Method not implemented.")}BtnClick(t,a,e){if(t==a||t===e)throw new Error("Method not implemented.")}}export{x as GPE_DateFieldInputRole};
