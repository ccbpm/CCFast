var n=Object.defineProperty;var E=(a,e,t)=>e in a?n(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var r=(a,e,t)=>E(a,typeof e!="symbol"?e+"":e,t);import{j as o,U as p,h as i}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as s}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class L extends o{constructor(t){super("TS.MapExt.GPEDateFieldInputRole");r(this,"JSDesc",` 
  #### 说明
  计算后要触发的脚本函数(比如:求和以后要激活的function)，该脚本要求写入到:DataUserJSLabMyFromID_Self.js
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
   `);t&&(this.MyPK=t)}get HisUAC(){const t=new p;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new i("Sys_MapExt","日期输入规则");return t.AddGroupAttr("基本设置"),t.AddMyPK(),t.AddTBString(s.FK_MapData,null,"表单ID",!0,!0,0,100,100),t.AddDDLStringEnum(s.Tag,"GTE","运算符","@GT=大于@GTE=大于等于@IT=小于@ITE=小于等于@EQ=等于@NEQ=不等于",!0,null,!1),t.AddDDLSQL(s.Tag1,null,"日期字段",`
     SELECT KeyOfEn as No, Name FROM Sys_MapAttr 
     WHERE MyDataType IN(6,7) AND UIVisible=1 AND UIIsEnable=1 AND FK_MapData='@FK_MapData'`,!0,null,!0),this._enMap=t,this._enMap}}export{L as GPEDateFieldInputRole};
