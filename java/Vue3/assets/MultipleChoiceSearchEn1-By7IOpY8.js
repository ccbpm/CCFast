var s=Object.defineProperty;var l=(r,t,e)=>t in r?s(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var a=(r,t,e)=>l(r,typeof t!="symbol"?t+"":t,e);import{j as p,U as n,h as u}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as i}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as o}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class D extends p{constructor(e){super("TS.MapExt.MultipleChoiceSearchEn1");a(this,"DescSearchTip",`
  #### 说明
  - 显示在搜索文本框的背景文字.
  - 输入城市名称,比如:beijing,bj,进行搜索.
  - 人员的编号,名称,拼音,进行模糊搜索.
  
  `);a(this,"DescTag2",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
  `);e&&(this.MyPK=e)}get HisUAC(){const e=new n;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new u("Sys_MapExt","搜索多选");return e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddDDLEntities(i.FK_DBSrc,"local","数据源",new o,!0,null,!1),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.DescSearchTip),e.AddDDLSysEnum(i.DBType,0,"数据源类型",!0,!0,"DBType","@0=执行SQL@1=执行url返回JSON@2=执行CCFromRef.js返回JSON",null,!1),e.AddRadioBtn("MultipleSelectType",1,"选择类型",!0,!0,"MultipleSelectType","@0=单选@1=多选",null,!0),e.AddTBString(i.Doc,null,"数据源表达式",!0,!1,0,50,200,!0,this.DescTag2),e.ParaFields=",SearchTip,MultipleSelectType,",e.AddTBAtParas(4e3),this._enMap=e,this._enMap}}export{D as MultipleChoiceSearchEn1};
