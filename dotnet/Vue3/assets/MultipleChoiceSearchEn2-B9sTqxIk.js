var n=Object.defineProperty;var l=(r,t,e)=>t in r?n(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var s=(r,t,e)=>l(r,typeof t!="symbol"?t+"":t,e);import{j as u,U as o,h as c}from"./entry/index-C6uBgOW5-1730430676707.js";import{a}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as i}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class K extends u{constructor(e){super("TS.MapExt.MultipleChoiceSearchEn2");s(this,"DescSearchtip",` 
  #### 说明
  - 显示在搜索文本框的背景文字.
  - 输入城市名称,比如:beijing,bj,进行搜索.
  - 人员的编号,名称,拼音,进行模糊搜索.
   `);s(this,"DescTag1",` 
   #### 说明
   - 列表数据源，可以限定显示列表的范围，比如只显示前10行。使用于大量数据时。
    `);s(this,"DescDoc",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new o;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new c("Sys_MapExt","搜索+输入多选");return e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddDDLEntities(a.FK_DBSrc,"local","数据源",new i,!0,null,!1),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.DescSearchtip),e.AddDDLSysEnum(a.DBType,0,"数据源类型",!0,!0,"DBType","@0=执行SQL@1=执行url返回JSON@2=执行CCFromRef.js返回JSON",null,!1),e.AddBoolean(a.Tag,!1,"是否显示签名",!0,!0),e.AddDDLEntities(a.FK_DBSrc,"local","数据库",new i,!0,null,!1),e.AddTBString(a.Doc,null,"搜索数据源",!0,!1,0,50,200,!0,this.DescDoc),e.AddTBString(a.Tag1,null,"列表数据源",!0,!1,0,50,200,!0,this.DescTag1),e.ParaFields=",Title,SearchTip,",e.AddTBAtParas(),this._enMap=e,this._enMap}}export{K as MultipleChoiceSearchEn2};
