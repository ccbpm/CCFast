var p=Object.defineProperty;var o=(r,t,e)=>t in r?p(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var s=(r,t,e)=>o(r,typeof t!="symbol"?t+"":t,e);import{j as i,U as n,h as l}from"./entry/index-C6uBgOW5-1730430676707.js";import{a}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as m}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class D extends i{constructor(e){super("TS.MapExt.TBFullCtrl1");s(this,"Desc1",`
  #### 说明
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

 
  `);this.RefEnName="TS.Sys.MapExt",e&&(this.MyPK=e)}get HisUAC(){const e=new n;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new l("Sys_MapExt","文本框自动完成");return e.AddMyPK(),e.AddDDLEntities(a.FK_DBSrc,"local","数据源",new m,!0,null,!1),e.AddTBStringDoc(a.Tag4,null,"搜索列表数据源配置:",!0,!1,!0,this.Desc1),this._enMap=e,this._enMap}}export{D as TBFullCtrl1};
