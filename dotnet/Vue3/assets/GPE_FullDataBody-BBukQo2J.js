var E=Object.defineProperty;var L=(i,r,t)=>r in i?E(i,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[r]=t;var F=(i,r,t)=>L(i,typeof r!="symbol"?r+"":r,t);var c=(i,r,t)=>new Promise((s,l)=>{var o=e=>{try{n(t.next(e))}catch(a){l(a)}},p=e=>{try{n(t.throw(e))}catch(a){l(a)}},n=e=>e.done?s(e.value):Promise.resolve(e.value).then(o,p);n((t=t.apply(i,r)).next())});import{bb as f,ba as w,aB as u,aC as h}from"./entry/index-M8VErHPE-1727507756861.js";import{S as d}from"./DBAccess-CzjFzLoq.js";import{FullBodySFTable as C}from"./FullBodySFTable-COOniFLO.js";import{FullBodySelf as A}from"./FullBodySelf-rVVzfCzN.js";import{GloComm as y}from"./GloComm-DZ1gELjv.js";import{PageBaseGroupEdit as B}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmTrack-0uAZQ3B_.js";import"./Help-D0bDMZWg.js";class v extends B{constructor(){super("GPE_FullDataBody");F(this,"Desc0",` 
  #### 帮助
  - 不填充：对控件没有填充要求。
  - 启用填充控件: 当选项发生变化后，同表单的其它控件的数据需要变化，我们把这样的行为称为其他控件填充。
  - 比如：下拉框的人员变化后，其它的字段就跟着变化。
  - 如下图，当人员选择变化时，Email自动变化。
  #### 效果图
  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/DDLFullCtrl/Img/DDLFullCtrl.png "屏幕截图.png") 
   `);F(this,"Desc1",` 
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
  设置下拉框在值变化后，填充其他控件与从表 `);F(this,"Desc2",` 
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
  设置下拉框在值变化后，填充其他控件与从表 `);this.PageTitle="填充主表"}Init(){return c(this,null,function*(){this.entity=new f,this.KeyOfEn=w.Tag5,this.Btns=[{pageNo:"SFTable",list:["字段对应","查询维护"]}],this.AddGroup("A","填充主表"),this.Blank("None","不填充",this.Desc0),this.AddEntity("Self","自定义设置",new A,this.Desc1),this.AddEntity("SFTable","绑定查询",new C,this.Desc1)})}BtnClick(t,s,l){return c(this,null,function*(){var o,p,n,e,a,g;if(l==="设置参数"||l=="字段对应"){const D=new d((o=this.entity)==null?void 0:o.Tag6);yield D.Retrieve();const m=(p=this.entity)==null?void 0:p.MyPK;yield D.AddSln(m,(n=this.entity)==null?void 0:n.FK_MapData);const S=y.UrlDtlBatch("TS.FrmUI.SFColumnSln","&RefPKVal="+m+"&FrmID="+((e=this.entity)==null?void 0:e.FK_MapData));return new u(h.OpenUrlByDrawer75,S)}if(l==="查询属性"||l==="查询维护"){yield new d((a=this.entity)==null?void 0:a.Tag6).Retrieve();const m=y.UrlEn("TS.FrmUI.SFSearch",(g=this.entity)==null?void 0:g.Tag6);return new u(h.OpenUrlByDrawer75,m)}if(t==s||t===l)throw new Error("Method not implemented.")})}AfterSave(t,s){}}export{v as GPE_FullDataBody};
