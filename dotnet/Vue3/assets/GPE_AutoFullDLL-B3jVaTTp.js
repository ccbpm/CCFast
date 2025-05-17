var F=Object.defineProperty;var E=(o,r,t)=>r in o?F(o,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[r]=t;var a=(o,r,t)=>E(o,typeof r!="symbol"?r+"":r,t);var u=(o,r,t)=>new Promise((s,n)=>{var p=e=>{try{i(t.next(e))}catch(l){n(l)}},m=e=>{try{i(t.throw(e))}catch(l){n(l)}},i=e=>e.done?s(e.value):Promise.resolve(e.value).then(p,m);i((t=t.apply(o,r)).next())});import{b as A,a as h}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as d}from"./PageBaseGroupEdit-IicyYiex.js";import{GPEAutoFullDLL as g}from"./GPEAutoFullDLL-DSguwNJm.js";import{SFTable as f}from"./SFTable-BlM1UBse.js";import{GloComm as c}from"./GloComm-CmAl8MpM.js";import{G as L,l as D}from"./entry/index-C6uBgOW5-1730430676707.js";import{GPEAutoFullDDLSFTable as w}from"./GPEAutoFullDDLSFTable-VBdXIJnx.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";import"./SFDBSrc-DKIMsnoa.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";class k extends d{constructor(){super("GPE_AutoFullDLL");a(this,"Desc1",`
  #### 帮助
  - 该SQL必须返回No,Name 两个列。
  - 支持ccbpm表达式。
  - SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
  #### 应用场景
   - 选择一个会议主持人，从本部门中选择。
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDLL/Img/AutoFullDLLSetting.png "屏幕截图.png")
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDLL/Img/AutoFullDLL.png "屏幕截图.png")
  `);a(this,"Desc0",`
  #### 帮助
   - 不启用，不启用过滤功能。
   - 启用过滤功能，应用SQL语句等，得到系统返回值。对数据进行过滤处理。
   - 比如，选取主讲人，选取班主任。
   - 加载的时候填充的数据.
  
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDLL/Img/AutoFullDLL.png "屏幕截图.png") 
  
  `);a(this,"Desc2",`
  #### 帮助
   - 绑定字典表的显示过滤.
   - 您可以维护字典表.
  
  `);this.PageTitle="设置显示过滤"}Init(){return u(this,null,function*(){this.entity=new A,this.KeyOfEn=h.DoWay,this.Btns=[{pageNo:"2",list:["字典维护"]}],yield this.entity.InitDataForMapAttr("AutoFullDLL",this.GetRequestVal("PKVal")),this.AddGroup("A","设置显示过滤"),this.Blank("0","不启用",this.Desc0),this.AddEntity("1","自定义设置",new g,this.Desc1),this.AddEntity("2","绑定字典表",new w,this.Desc2)})}AfterSave(t,s){if(t==s)throw new Error("Method not implemented.")}BtnClick(t,s,n){return u(this,null,function*(){var p;if(n=="字典属性"){const m=(p=this.entity)==null?void 0:p.Doc;if(!m){alert("请选择绑定的字典，然后执行保存按钮.");return}const i=new f(m);yield i.Retrieve();const e=i.GetParaString("EnName",""),l=c.UrlEn(e,i.No);return new L(D.OpenUrlByDrawer75,l)}if(n=="字典维护"){const m=c.UrlSearch("TS.FrmUI.SFTable");return new L(D.OpenUrlByDrawer75,m)}if(t==s||t===n)throw new Error("Method not implemented.")})}}export{k as GPE_AutoFullDLL};
