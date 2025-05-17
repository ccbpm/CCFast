var F=Object.defineProperty;var E=(o,e,t)=>e in o?F(o,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[e]=t;var p=(o,e,t)=>E(o,typeof e!="symbol"?e+"":e,t);var u=(o,e,t)=>new Promise((a,n)=>{var m=r=>{try{i(t.next(r))}catch(l){n(l)}},s=r=>{try{i(t.throw(r))}catch(l){n(l)}},i=r=>r.done?a(r.value):Promise.resolve(r.value).then(m,s);i((t=t.apply(o,e)).next())});import{bb as A,ba as h,aE as d,aB as c,aC as L}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as g}from"./PageBaseGroupEdit-JIgqoTiq.js";import{GPEAutoFullDLL as w}from"./GPEAutoFullDLL-BTgpBioK.js";import{GloComm as D}from"./GloComm-DZ1gELjv.js";import{GPEAutoFullDDLSFTable as f}from"./GPEAutoFullDDLSFTable-MXOvWgx8.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class I extends g{constructor(){super("GPE_AutoFullDLL");p(this,"Desc1",`
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
  `);p(this,"Desc0",`
  #### 帮助
   - 不启用，不启用过滤功能。
   - 启用过滤功能，应用SQL语句等，得到系统返回值。对数据进行过滤处理。
   - 比如，选取主讲人，选取班主任。
   - 加载的时候填充的数据.
  
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/AutoFullDLL/Img/AutoFullDLL.png "屏幕截图.png") 
  
  `);p(this,"Desc2",`
  #### 帮助
   - 绑定字典表的显示过滤.
   - 您可以维护字典表.
  
  `);this.PageTitle="设置显示过滤"}Init(){return u(this,null,function*(){this.entity=new A,this.KeyOfEn=h.DoWay,this.Btns=[{pageNo:"2",list:["字典维护"]}],yield this.entity.InitDataForMapAttr("AutoFullDLL",this.GetRequestVal("PKVal")),this.AddGroup("A","设置显示过滤"),this.Blank("0","不启用",this.Desc0),this.AddEntity("1","自定义设置",new w,this.Desc1),this.AddEntity("2","绑定字典表",new f,this.Desc2)})}AfterSave(t,a){if(t==a)throw new Error("Method not implemented.")}BtnClick(t,a,n){return u(this,null,function*(){var m;if(n=="字典属性"){const s=(m=this.entity)==null?void 0:m.Doc;if(!s){alert("请选择绑定的字典，然后执行保存按钮.");return}const i=new d(s);yield i.Retrieve();const r=i.GetParaString("EnName",""),l=D.UrlEn(r,i.No);return new c(L.OpenUrlByDrawer75,l)}if(n=="字典维护"){const s=D.UrlSearch("TS.FrmUI.SFTable");return new c(L.OpenUrlByDrawer75,s)}if(t==a||t===n)throw new Error("Method not implemented.")})}}export{I as GPE_AutoFullDLL};
