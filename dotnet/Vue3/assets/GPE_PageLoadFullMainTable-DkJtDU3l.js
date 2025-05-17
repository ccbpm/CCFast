var m=Object.defineProperty;var h=(a,t,e)=>t in a?m(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var r=(a,t,e)=>h(a,typeof t!="symbol"?t+"":t,e);var p=(a,t,e)=>new Promise((n,i)=>{var E=s=>{try{o(e.next(s))}catch(l){i(l)}},c=s=>{try{o(e.throw(s))}catch(l){i(l)}},o=s=>s.done?n(s.value):Promise.resolve(s.value).then(E,c);o((e=e.apply(a,t)).next())});import{bb as S,ba as d}from"./entry/index-M8VErHPE-1727507756861.js";import{PageLoadFull as F}from"./PageLoadFull-BP7wUlpC.js";import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class P extends u{constructor(){super("GPE_PageLoadFullMainTable");r(this,"Desc0",`
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
  `);r(this,"Desc1",`
  #### 帮助
   - 请点击【编辑】按钮执行编辑填充主表数据信息.
   - 填写格式，点击字段icon帮助.
  `);this.PageTitle="装载填充"}Init(){return p(this,null,function*(){this.entity=new S,this.KeyOfEn=d.DoWay,yield this.entity.InitDataForMapAttr("PageLoadFullMainTable",this.GetRequestVal("PKVal")),this.AddGroup("A","装载填充"),this.Blank("None","不设置",this.Desc0),this.AddEntity("Self","按照SQL填充",new F,this.Desc1),this.SelectItemsByGroupList("SFTable","按照查询填充",this.HelpUn,!1,"SELECT No,Name FROM Sys_SFDBSrc ","SELECT No,Name,FK_SFDBSrc FROM sys_sfsearch ","Doc")})}BtnClick(e,n,i){}AfterSave(e){}}export{P as GPE_PageLoadFullMainTable};
