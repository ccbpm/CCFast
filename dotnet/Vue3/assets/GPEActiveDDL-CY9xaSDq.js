var n=Object.defineProperty;var l=(s,t,e)=>t in s?n(s,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[t]=e;var r=(s,t,e)=>l(s,typeof t!="symbol"?t+"":t,e);import{N as D,ba as a,a0 as i,U as E,L as u}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class S extends D{constructor(e){super("TS.MapExt.GPEActiveDDL");r(this,"DescSearchtip",` 
  #### 说明
  - 显示在搜索文本框的背景文字.
  - 输入城市名称,比如:beijing,bj,进行搜索.
  - 人员的编号,名称,拼音,进行模糊搜索.
   `);r(this,"DescTag1",` 
   #### 说明
   - zhoupeng 补充
    `);r(this,"DescDoc",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new E;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new u("Sys_MapExt","级联下拉框");return e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddTBString(a.FK_MapData,null,"表单ID",!0,!0,0,50,200),e.AddTBString(a.ExtModel,null,"ActiveDDL",!1,!1,0,50,200),e.AddTBString(a.ExtType,null,"ActiveDDL",!1,!1,0,50,200),e.AddTBString(a.AttrOfOper,null,"当前字段",!0,!0,0,50,200),e.AddDDLSQL(a.AttrsOfActive,null,"联动的字段",`
    SELECT KeyOfEn AS No, Name FROM Sys_MapAttr WHERE FK_MapData='@FK_MapData' 
    AND UIContralType=2  
    `,!0),e.AddDDLSysEnum(a.DBType,0,"数据源类型",!0,!0,"DBType","@0=执行SQL@1=执行url返回JSON@2=执行CCFromRef.js返回JSON",null,!1),e.AddDDLEntities(a.FK_DBSrc,"local","数据源",new i,!0,null,!1),e.AddTBString(a.Doc,null,"数据源表达式",!0,!1,0,50,200,!0,this.DescDoc),this._enMap=e,this._enMap}}export{S as GPEActiveDDL};
