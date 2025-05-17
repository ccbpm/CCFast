var u=Object.defineProperty;var c=(i,r,t)=>r in i?u(i,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[r]=t;var m=(i,r,t)=>c(i,typeof r!="symbol"?r+"":r,t);var D=(i,r,t)=>new Promise((o,e)=>{var s=l=>{try{n(t.next(l))}catch(a){e(a)}},p=l=>{try{n(t.throw(l))}catch(a){e(a)}},n=l=>l.done?o(l.value):Promise.resolve(l.value).then(s,p);n((t=t.apply(i,r)).next())});import{b as g,a as F}from"./MapExt-DtQWKcAY.js";import{GloComm as h}from"./GloComm-CmAl8MpM.js";import{PageBaseGroupEdit as E}from"./PageBaseGroupEdit-IicyYiex.js";import{G as L,l as y}from"./entry/index-C6uBgOW5-1730430676707.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./Help-D0bDMZWg.js";class S extends E{constructor(){super("GPE_DDLFullCtrlts");m(this,"Desc0",` 
  #### 帮助
  - 不填充：对控件没有填充要求。
  - 启用填充控件: 当选项发生变化后，同表单的其它控件的数据需要变化，我们把这样的行为称为其他控件填充。
  - 比如：下拉框的人员变化后，其它的字段就跟着变化。
  - 如下图，当人员选择变化时，Email自动变化。
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DDLFullCtrl/Img/DDLFullCtrl.png "屏幕截图.png") 
   `);m(this,"Desc1",` 
  #### 帮助
  - 定义: 当选项发生变化后，同表单的其它控件的数据需要变化，我们把这样的行为称为其他控件填充。
  - 应用场景
  - 在做一个选择操作员的时候，需要把操作员的电话，邮件填充到主表其他字段里面，需要把操作员的角色显示到下拉框里面。
  - 人员是一个下拉框，人员变动的时候，其他的控件也在跟着变动。
  - 填写数据源。
  #### 配置图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DDLFullCtrl/Img/DDLFullCtrlSetting.png "屏幕截图.png") 
  #### 运行图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DDLFullCtrl/Img/DDLFullCtrl.png "屏幕截图.png") 
  设置下拉框在值变化后，填充其他控件与从表 `);this.PageTitle="下拉框填充"}Init(){return D(this,null,function*(){this.entity=new g,this.KeyOfEn=F.DoWay,this.Btns=[{pageNo:"1",list:["填充"]}],this.entity=yield this.entity.InitDataForMapAttr("DDLFullCtrl",this.GetRequestVal("PKVal")),this.AddGroup("A","下拉框填充"),this.Blank("0","不填充",this.Desc0),this.Blank("1","启用填充",this.Desc0)})}BtnClick(t,o,e){return D(this,null,function*(){var s;if(e==="落值填充"||e==="填充"){const p=h.UrlEn("TS.MapExt.FullData",(s=this.entity)==null?void 0:s.MyPK);return new L(y.OpenUrlByDrawer75,p)}if(t==o||t===e)throw new Error("Method not implemented.")})}AfterSave(t,o){}}export{S as GPE_DDLFullCtrlts};
