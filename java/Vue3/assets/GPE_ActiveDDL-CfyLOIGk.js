var A=Object.defineProperty;var F=(i,e,t)=>e in i?A(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var m=(i,e,t)=>F(i,typeof e!="symbol"?e+"":e,t);var D=(i,e,t)=>new Promise((s,a)=>{var p=r=>{try{o(t.next(r))}catch(c){a(c)}},n=r=>{try{o(t.throw(r))}catch(c){a(c)}},o=r=>r.done?s(r.value):Promise.resolve(r.value).then(p,n);o((t=t.apply(i,e)).next())});import{PageBaseGroupEdit as L}from"./PageBaseGroupEdit-IicyYiex.js";import{b as S,a as u}from"./MapExt-DtQWKcAY.js";import{GPEActiveDDLSFTable as d}from"./GPEActiveDDLSFTable-DeY3RH6z.js";import{GPEActiveDDLSelfSetting as y}from"./GPEActiveDDLSelfSetting-C96E1r0O.js";import{G as l,l as g}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as E}from"./GloComm-CmAl8MpM.js";import{SFTable as P}from"./SFTable-BlM1UBse.js";import"./Help-D0bDMZWg.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFDBSrc-DKIMsnoa.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";class k extends L{constructor(){super("GPE_ActiveDDL");m(this,"Desc0",`
  #### 帮助
  - 定义: 当一个下拉框的数据源变化后，另外一个下拉框的数据同时发生变化，我们把这样的行为称为级联下拉框.
  - 比如： 大类、小类，实现级联。
  - 比如: 片区、省份、城市、市县。
  - 如果实现两两级联，就可以实现无限制的级联。
  - 级联关系体现主表上，也可体现从表上。
  #### 主表无限级联效果图
  - 省份城市
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ActiveDDL/Img/MainTable.png "省份城市.png")  

  #### 从表的级联效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ActiveDDL/Img/Dtl.png "省份城市从表.png")  

  `);m(this,"GPEActiveDDLSelfSetting",`
  #### 帮助
- 为了兼容旧版本，保留此模式,新模式下，建议使用,查询.

 - 在下面文本框中输入一个SQL,具有编号，标签列，用来绑定下从动下拉框。
 - 我们建议使用查询的模式设置.

 #### 关系数据库配置
 - 比如: SELECT No, Name FROM CN_SF WHERE FK_PQ = '@Key'
 - SELECT No, Name FROM CN_City WHERE FK_SF = '@Key'
 - 说明:@Key是ccflow约定的关键字，是主下拉框传递过来的值。
 - 主菜单是编号的是从动菜单编号的前几位，不必联动内容。
 - 比如: 主下拉框是省份，联动菜单是城市。
 #### WebApi模式配置.
 - 设置url: /xxxx/@WebUser.No/@MyFormName/@WorkID
 - 格式说明: 
 - 1. 设置选择的web服务地址的后部分.
 - 2. 设置的内容如果需要变量就使用@+字段名.
 - 3. 支持ccbpm的表达式.
 #### 配置图
 -  ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ActiveDDL/Img/MainTableSetting.png "省份城市.png")  

 #### 主表无限级联效果图
  - 省份城市
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ActiveDDL/Img/MainTable.png "省份城市.png")  

 #### 从表的级联效果图.
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/ActiveDDL/Img/Dtl.png "省份城市从表.png")  
  ...
  `);m(this,"GPEActiveDDLSFTable",`
  #### 查询定义
  - 把表单中用到对外部数据的获取行为，我们称为查询.
  - 查询有一些参数输入，通过查询类的计算获得数据源.
  #### 如何设置查询?
  - 在表单设计器工具栏中，找到系统管理，查询管理.
  - 进入查询列表，然后新建查询.
  #### 帮助
 - 查询定义: 输入要求的参数，经过系统处理，返回要指定的数据.
 - 一个查询通常有关系数据库与web服务两个类型.
 - 查询：就是从已经设置的查询库中选择一个查询.
 - 系统设置的查询库，都已经约定好了要输入的参数名字.
 #### 参数格式
 - 格式: {Url参数名1}=@字段名或者ccbpm表达式1;{Url参数名2}=@字段名或者ccbpm表达式2;
 - 多个参数使用分号分隔.
 - 实例: Url格式: /BU_PDT/{appcode}/{accesstoken}/{BU}
 - 实例配置: {BU_PDT}=@BU;{appcode}=@WebUser.No;{accesstoken}=@Token
 #### 配置图
 - 待提供
  ...
  `);this.PageTitle="级联下拉框"}Init(){return D(this,null,function*(){this.entity=new S,this.KeyOfEn=u.DoWay,this.Btns=[{pageNo:"2",list:["字典维护"]}],yield this.entity.InitDataForMapAttr("ActiveDDL",this.GetRequestVal("PKVal")),this.AddGroup("A","级联下拉框"),this.Blank("0","不启用",this.Desc0),this.AddEntity("1","配置数据源",new y,this.GPEActiveDDLSelfSetting),this.AddEntity("2","绑定字典表",new d,this.GPEActiveDDLSFTable)})}BtnClick(t,s,a){return D(this,null,function*(){var p;if(a=="字典属性"){const n=(p=this.entity)==null?void 0:p.Doc;if(!n){alert("请选择绑定的字典，然后执行保存按钮.");return}const o=new P(n);yield o.Retrieve();const r=o.GetParaString("EnName",""),c=E.UrlEn(r,o.No);return new l(g.OpenUrlByDrawer75,c)}if(a=="字典维护"){const n=E.UrlSearch("TS.FrmUI.SFTable");return new l(g.OpenUrlByDrawer75,n)}throw new Error(`没有定义此按钮【${a}】的事件，请检查！`)})}AfterSave(t,s){}}export{k as GPE_ActiveDDL};
