var T=Object.defineProperty;var c=(i,t,e)=>t in i?T(i,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[t]=e;var o=(i,t,e)=>c(i,typeof t!="symbol"?t+"":t,e);var F=(i,t,e)=>new Promise((s,r)=>{var p=l=>{try{n(e.next(l))}catch(m){r(m)}},a=l=>{try{n(e.throw(l))}catch(m){r(m)}},n=l=>l.done?s(l.value):Promise.resolve(l.value).then(p,a);n((e=e.apply(i,t)).next())});import{TBFullCtrl1 as g}from"./TBFullCtrl1-CRYSxR-v.js";import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-JIgqoTiq.js";import{TBFullCtrl2 as L}from"./TBFullCtrl2-JbkyD3WP.js";import{aB as u,aC as E,bb as K,ba as N}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as S}from"./GloComm-DZ1gELjv.js";import{MENoNameP1 as y}from"./MENoNameP1-zw_DAO3F.js";import"./Help-D0bDMZWg.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class G extends h{constructor(){super("GPE_TBFullCtrl");o(this,"Desc0",`
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
  `);this.PageTitle="文本框自动完成"}AfterSave(e,s){}BtnClick(e,s,r){var p;if(r=="字典维护"){const a=S.UrlSearch("TS.FrmUI.SFTable");return new u(E.OpenUrlByDrawer75,a)}if(r==="落值填充"||r==="填充"){const a=S.UrlEn("TS.MapExt.FullData",(p=this.entity)==null?void 0:p.MyPK);return new u(E.OpenUrlByDrawer75,a)}}Init(){return F(this,null,function*(){this.entity=new K,this.KeyOfEn=N.DoWay,yield this.entity.InitDataForMapAttr("TBFullCtrl",this.GetRequestVal("PKVal"),"None"),this.Btns=[{pageNo:"Simple",list:["填充"]},{pageNo:"Table",list:["填充"]},{pageNo:"SimpleSFTable",list:["填充","字典维护"]},{pageNo:"TableSFTable",list:["填充","字典维护"]}],yield this.entity.InitDataForMapAttr("TBFullCtrl",this.GetRequestVal("PKVal"),"None"),this.AddGroup("A","文本框自动完成"),this.Blank("None","禁用",this.Desc0),this.AddEntity("Simple","简洁模式",new g,this.Desc1),this.AddEntity("Table","表格模式",new L,this.Desc2),this.AddEntity("SimpleSFTable","简洁模式(字典表)",new y,this.Desc1),this.AddEntity("TableSFTable","表格模式(字典表)",new y,this.Desc2)})}}export{G as GPE_TBFullCtrl};
