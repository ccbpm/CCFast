var n=Object.defineProperty;var i=(r,e,t)=>e in r?n(r,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[e]=t;var s=(r,e,t)=>i(r,typeof e!="symbol"?e+"":e,t);import{j as o,U as p,h as E}from"./entry/index-C6uBgOW5-1730430676707.js";import{a}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class I extends o{constructor(t){super("TS.MapExt.GPERadioBtns");s(this,"JSDesc",` 
  #### 说明
  计算后要触发的脚本函数(比如:求和以后要激活的function)，该脚本要求写入到:DataUserJSLabMyFromID_Self.js
   `);s(this,"DescTag1",` 
   #### 说明
   - zhoupeng 补充
    `);s(this,"DescDoc",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
   `);t&&(this.MyPK=t)}get HisUAC(){const t=new p;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new E("Sys_MapExt","");return t.AddGroupAttr("未完成"),t.AddMyPK(),t.AddTBString(a.Tag1,null,"执行JS脚本(可以为空)",!0,!0,0,50,200,!0,this.JSDesc),t.AddTBString(a.Tag2,null,"Tip 提示信息(可以为空)",!0,!0,0,50,200,!0,this.DescDoc),this._enMap=t,this._enMap}}export{I as GPERadioBtns};
