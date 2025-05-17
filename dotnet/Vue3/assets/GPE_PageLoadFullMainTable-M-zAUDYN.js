var c=Object.defineProperty;var h=(a,e,t)=>e in a?c(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var l=(a,e,t)=>h(a,typeof e!="symbol"?e+"":e,t);var m=(a,e,t)=>new Promise((p,s)=>{var n=i=>{try{o(t.next(i))}catch(r){s(r)}},E=i=>{try{o(t.throw(i))}catch(r){s(r)}},o=i=>i.done?p(i.value):Promise.resolve(i.value).then(n,E);o((t=t.apply(a,e)).next())});import{b as S,a as d}from"./MapExt-DtQWKcAY.js";import{PageLoadFull as F}from"./PageLoadFull-DEGaDCzx.js";import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class G extends u{constructor(){super("GPE_PageLoadFullMainTable");l(this,"Desc0",`
  #### 帮助
   - 定义:当表单加载的时，给当前表单一些数据填充到控件上，我们称为装载填充.
   - 不设置(默认)：不对主表数据进行填充。
   - 一个表单的控件分为主表，从表、主表的下拉框框数据。
   #### 应用场景
   - 比如填写申请单的时候，需要把自己的地址、邮件、电话信息设置的表单上。
   - 格式: SELECT Email, Addr, Tel FROM MyTable WHERE EmpNo='@WebUer.No' 
   - 返回一行一列的数据，列名与字段名对应的上.
   #### 其他说明
   - 在2022年以前的版本，在一个表单属性上设置装载填充，填充主表数据，从表数据，下拉框数据。
   - 在vue3以后的版本，这些装载填充功能，分配到各个控件属性中了。
  `);l(this,"Desc1",`
  #### 帮助
   - 请点击【编辑】按钮执行编辑填充主表数据信息.
   - 填写格式，点击字段icon帮助.
  `);this.PageTitle="装载填充"}Init(){return m(this,null,function*(){this.entity=new S,this.KeyOfEn=d.DoWay,yield this.entity.InitDataForMapAttr("PageLoadFullMainTable",this.GetRequestVal("PKVal")),this.AddGroup("A","装载填充"),this.Blank("None","不设置",this.Desc0),this.AddEntity("Self","按照SQL填充",new F,this.Desc1),this.SelectItemsByGroupList("SFTable","按照查询填充",this.HelpUn,!1,"SELECT No,Name FROM Sys_SFDBSrc ","SELECT No,Name,FK_SFDBSrc FROM sys_sfsearch ","Doc")})}BtnClick(t,p,s){}AfterSave(t){}}export{G as GPE_PageLoadFullMainTable};
