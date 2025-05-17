var l=Object.defineProperty;var n=(a,t,e)=>t in a?l(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var s=(a,t,e)=>n(a,typeof t!="symbol"?t+"":t,e);import{j as o,U as i,h as p}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as r}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as E}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class y extends o{constructor(e){super("TS.MapExt.GPEActiveDDLSelfSetting");s(this,"DescDoc",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new i;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new p("Sys_MapExt","级联下拉框");return e.GroupBarShowModel=1,e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddTBString(r.FK_MapData,null,"表单ID",!0,!0,0,50,200),e.AddTBString(r.ExtModel,null,"ActiveDDL",!1,!1,0,50,200),e.AddTBString(r.ExtType,null,"ActiveDDL",!1,!1,0,50,200),e.AddTBString(r.AttrOfOper,null,"当前字段",!0,!0,0,50,200),e.AddDDLSQL(r.AttrsOfActive,null,"联动的下拉框","SELECT KeyOfEn AS No, Name FROM Sys_MapAttr WHERE FK_MapData='@FK_MapData' AND UIContralType=1  AND KeyOfEn !='@AttrOfOper'  ",!0),e.AddDDLEntities(r.FK_DBSrc,"local","数据源",new E,!0,null,!1),e.AddBoolean("IsSelectVal",!1,"级联是否默认选择值",!0,!0,!1,!1),e.AddTBStringDoc(r.Doc,null,"数据源表达式",!0,!1,!0,this.DescDoc),e.ParaFields=",IsSelectVal,",e.AddTBAtParas(),this._enMap=e,this._enMap}beforeUpdateInsertAction(){return this.DoWay=1,Promise.resolve(!0)}}export{y as GPEActiveDDLSelfSetting};
