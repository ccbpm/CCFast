var n=Object.defineProperty;var i=(s,e,t)=>e in s?n(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var a=(s,e,t)=>i(s,typeof e!="symbol"?e+"":e,t);import{N as o,ba as r,U as E,L as u}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class K extends o{constructor(t){super("TS.MapExt.GPERadioBtns");a(this,"JSDesc",` 
  #### 说明
  计算后要触发的脚本函数(比如:求和以后要激活的function)，该脚本要求写入到:DataUserJSLabMyFromID_Self.js
   `);a(this,"DescTag1",` 
   #### 说明
   - zhoupeng 补充
    `);a(this,"DescDoc",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
   `);t&&(this.MyPK=t)}get HisUAC(){const t=new E;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new u("Sys_MapExt","");return t.AddGroupAttr("未完成"),t.AddMyPK(),t.AddTBString(r.Tag1,null,"执行JS脚本(可以为空)",!0,!0,0,50,200,!0,this.JSDesc),t.AddTBString(r.Tag2,null,"Tip 提示信息(可以为空)",!0,!0,0,50,200,!0,this.DescDoc),this._enMap=t,this._enMap}}export{K as GPERadioBtns};
