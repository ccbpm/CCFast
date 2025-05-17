var E=Object.defineProperty;var r=(a,t,e)=>t in a?E(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var s=(a,t,e)=>r(a,typeof t!="symbol"?t+"":t,e);import{N as u,ba as n,U as p,L as o}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class K extends u{constructor(e){super("TS.MapExt.GPEDateFieldInputRole");s(this,"JSDesc",` 
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
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new p;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new o("Sys_MapExt","日期输入规则");return e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddTBString(n.FK_MapData,null,"表单ID",!0,!0,0,100,100),e.AddDDLStringEnum(n.Tag,"GTE","运算符","@GT=大于@GTE=大于等于@IT=小于@ITE=小于等于@EQ=等于@NEQ=不等于",!0,null,!1),e.AddDDLSQL(n.Tag1,null,"日期字段",`
     SELECT KeyOfEn as No, Name FROM Sys_MapAttr 
     WHERE MyDataType IN(6,7) AND UIVisible=1 AND UIIsEnable=1 AND FK_MapData='@FK_MapData'`,!0,null,!0),this._enMap=e,this._enMap}}export{K as GPEDateFieldInputRole};
