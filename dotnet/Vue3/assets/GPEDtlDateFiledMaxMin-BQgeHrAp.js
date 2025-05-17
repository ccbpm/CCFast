var n=Object.defineProperty;var i=(a,e,t)=>e in a?n(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var s=(a,e,t)=>i(a,typeof e!="symbol"?e+"":e,t);import{j as o,aM as p,U as M,h as E}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as r}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class y extends o{constructor(t){super("TS.MapExt.GPEDtlDateFiledMaxMin");s(this,"JSDesc",` 
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
   `);t&&(this.MyPK=t)}get HisUAC(){const t=new M;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new E("Sys_MapExt","对从表列求值");return t.AddGroupAttr("基本设置"),t.AddMyPK(),t.AddDDLSQL(r.Doc,null,"从表","SELECT No,Name From Sys_MapDtl WHERE FK_MapData='@FK_MapData'",!0),t.AddTBString(r.FK_MapData,null,"对应的表单",!1,!1,0,50,200,!0,this.DescDoc),t.AddTBString(r.Tag1,null,"输入列",!0,!1,0,50,200,!0,this.DescTag1),t.SetPopList("Tag1",p.sqlMapAttrDTFields,!1,"200px","300px","选择字段","icon-people"),t.AddDDLStringEnum(r.Tag,"Max","计算方式","@Max=求最大@Min=求最小",!0),this._enMap=t,this._enMap}}export{y as GPEDtlDateFiledMaxMin};
