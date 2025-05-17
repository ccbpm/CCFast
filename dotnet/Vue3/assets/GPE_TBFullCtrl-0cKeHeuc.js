var T=Object.defineProperty;var c=(i,t,e)=>t in i?T(i,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[t]=e;var o=(i,t,e)=>c(i,typeof t!="symbol"?t+"":t,e);var F=(i,t,e)=>new Promise((s,r)=>{var p=l=>{try{m(e.next(l))}catch(n){r(n)}},a=l=>{try{m(e.throw(l))}catch(n){r(n)}},m=l=>l.done?s(l.value):Promise.resolve(l.value).then(p,a);m((e=e.apply(i,t)).next())});import{TBFullCtrl1 as g}from"./TBFullCtrl1-DPMPtLaC.js";import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-IicyYiex.js";import{TBFullCtrl2 as L}from"./TBFullCtrl2-BRaRjlD1.js";import{b as K,a as N}from"./MapExt-DtQWKcAY.js";import{GloComm as u}from"./GloComm-CmAl8MpM.js";import{G as E,l as S}from"./entry/index-C6uBgOW5-1730430676707.js";import{MENoNameP1 as y}from"./MENoNameP1-BCq_2W4B.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";class V extends h{constructor(){super("GPE_TBFullCtrl");o(this,"Desc0",`
  #### 帮助
  - 禁用：不启用.
  - 文本框的自动完成，就是在文本框输入的时候，输入特定的关键字就可以搜索到要填写的内容.
  - 比如：人员输入、药品输入.
  - 分为：简洁模式与表格模式两种.
  #### 效果图
   - 简洁模式 
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/TBFullCtrl/Simple.png "屏幕截图.png")

  #### 效果图
   -  表格模式
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/TBFullCtrl/Simple1.png "屏幕截图.png")

  `);o(this,"Desc1",`
  #### 帮助
   - 填充SQL帮助
   1. 设置一个查询的SQL语句，该SQL必须包含 No, Name 列, 用与展示快速补全的部分。
   1. 该SQL必须包含 @Key 关键字，@Key 输入文本框的值.
   1. SQL返回的列与其他字段名称保持一致，就可以完成控件数据的自动填充。
   1. 比如: SELECT No,Name FROM WF_Emp WHERE No LIKE '%@Key%'
   1. 为防止URL编码规定like的第一个%写成[%],如果like '%@Key%' 写成'[%]@Key%'
   - 填充Url帮助
   1. 设置URL，返回的必须是json格式。
   1. 比如: /App/Handler.ashx?DoType=Emps&Key=@Key
   1. @Key 是输入的关键字

  #### 配置图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/TBFullCtrl/SimplePeizhi.png "配置说明")

  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/TBFullCtrl/Simple.png "配置说明")
  `);o(this,"Desc2",`
  #### 帮助
   - 填充SQL帮助
  1. 设置一个查询的SQL语句，该SQL必须包含 No, Name 列, 用与展示快速补全的部分。
  1. 该SQL必须包含 @Key 关键字，@Key 输入文本框的值.
  1. SQL返回的列与其他字段名称保持一致，就可以完成控件数据的自动填充。
  1. 比如:SELECT No,Name,Name as CaoZuoYuanMingCheng,Tel as DianHua,Email,FK_Dept FROM WF_Emp WHERE No LIKE '%@Key%'
  1. 为防止URL编码规定like的第一个%写成[%],如果like '%@Key%' 写成'[%]@Key%'

  #### 配置图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/TBFullCtrl/SimplePeizhi1.png "配置说明")
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/TBFullCtrl/Simple1.png "配置说明")
  `);this.PageTitle="文本框自动完成"}AfterSave(e,s){}BtnClick(e,s,r){var p;if(r=="字典维护"){const a=u.UrlSearch("TS.FrmUI.SFTable");return new E(S.OpenUrlByDrawer75,a)}if(r==="落值填充"||r==="填充"){const a=u.UrlEn("TS.MapExt.FullData",(p=this.entity)==null?void 0:p.MyPK);return new E(S.OpenUrlByDrawer75,a)}}Init(){return F(this,null,function*(){this.entity=new K,this.KeyOfEn=N.DoWay,yield this.entity.InitDataForMapAttr("TBFullCtrl",this.GetRequestVal("PKVal"),"None"),this.Btns=[{pageNo:"Simple",list:["填充"]},{pageNo:"Table",list:["填充"]},{pageNo:"SimpleSFTable",list:["填充","字典维护"]},{pageNo:"TableSFTable",list:["填充","字典维护"]}],yield this.entity.InitDataForMapAttr("TBFullCtrl",this.GetRequestVal("PKVal"),"None"),this.AddGroup("A","文本框自动完成"),this.Blank("None","禁用",this.Desc0),this.AddEntity("Simple","简洁模式",new g,this.Desc1),this.AddEntity("Table","表格模式",new L,this.Desc2),this.AddEntity("SimpleSFTable","简洁模式(字典表)",new y,this.Desc1),this.AddEntity("TableSFTable","表格模式(字典表)",new y,this.Desc2)})}}export{V as GPE_TBFullCtrl};
