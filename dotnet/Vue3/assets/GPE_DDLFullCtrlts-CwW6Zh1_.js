var u=Object.defineProperty;var c=(i,r,t)=>r in i?u(i,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[r]=t;var D=(i,r,t)=>c(i,typeof r!="symbol"?r+"":r,t);var m=(i,r,t)=>new Promise((s,e)=>{var a=l=>{try{o(t.next(l))}catch(p){e(p)}},n=l=>{try{o(t.throw(l))}catch(p){e(p)}},o=l=>l.done?s(l.value):Promise.resolve(l.value).then(a,n);o((t=t.apply(i,r)).next())});import{bb as g,ba as F,aB as h,aC as E}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as C}from"./GloComm-DZ1gELjv.js";import{PageBaseGroupEdit as L}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Help-D0bDMZWg.js";class W extends L{constructor(){super("GPE_DDLFullCtrlts");D(this,"Desc0",` 
  #### 帮助
  - 不填充：对控件没有填充要求。
  - 启用填充控件: 当选项发生变化后，同表单的其它控件的数据需要变化，我们把这样的行为称为其他控件填充。
  - 比如：下拉框的人员变化后，其它的字段就跟着变化。
  - 如下图，当人员选择变化时，Email自动变化。
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DDLFullCtrl/Img/DDLFullCtrl.png "屏幕截图.png") 
   `);D(this,"Desc1",` 
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
  设置下拉框在值变化后，填充其他控件与从表 `);this.PageTitle="下拉框填充"}Init(){return m(this,null,function*(){this.entity=new g,this.KeyOfEn=F.DoWay,this.Btns=[{pageNo:"1",list:["填充"]}],this.entity=yield this.entity.InitDataForMapAttr("DDLFullCtrl",this.GetRequestVal("PKVal")),this.AddGroup("A","下拉框填充"),this.Blank("0","不填充",this.Desc0),this.Blank("1","启用填充",this.Desc0)})}BtnClick(t,s,e){return m(this,null,function*(){var a;if(e==="落值填充"||e==="填充"){const n=C.UrlEn("TS.MapExt.FullData",(a=this.entity)==null?void 0:a.MyPK);return new h(E.OpenUrlByDrawer75,n)}if(t==s||t===e)throw new Error("Method not implemented.")})}AfterSave(t,s){}}export{W as GPE_DDLFullCtrlts};
