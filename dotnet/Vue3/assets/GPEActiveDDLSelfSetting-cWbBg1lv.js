var l=Object.defineProperty;var n=(r,t,e)=>t in r?l(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var s=(r,t,e)=>n(r,typeof t!="symbol"?t+"":t,e);import{N as i,ba as a,a0 as o,U as E,L as d}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class S extends i{constructor(e){super("TS.MapExt.GPEActiveDDLSelfSetting");s(this,"DescDoc",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new E;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new d("Sys_MapExt","级联下拉框");return e.GroupBarShowModel=1,e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddTBString(a.FK_MapData,null,"表单ID",!0,!0,0,50,200),e.AddTBString(a.ExtModel,null,"ActiveDDL",!1,!1,0,50,200),e.AddTBString(a.ExtType,null,"ActiveDDL",!1,!1,0,50,200),e.AddTBString(a.AttrOfOper,null,"当前字段",!0,!0,0,50,200),e.AddDDLSQL(a.AttrsOfActive,null,"联动的下拉框","SELECT KeyOfEn AS No, Name FROM Sys_MapAttr WHERE FK_MapData='@FK_MapData' AND UIContralType=1  AND KeyOfEn !='@AttrOfOper'  ",!0),e.AddDDLEntities(a.FK_DBSrc,"local","数据源",new o,!0,null,!1),e.AddBoolean("IsSelectVal",!1,"级联是否默认选择值",!0,!0,!1,!1),e.AddTBStringDoc(a.Doc,null,"数据源表达式",!0,!1,!0,this.DescDoc),e.ParaFields=",IsSelectVal,",e.AddTBAtParas(),this._enMap=e,this._enMap}beforeUpdateInsertAction(){return this.DoWay=1,Promise.resolve(!0)}}export{S as GPEActiveDDLSelfSetting};
