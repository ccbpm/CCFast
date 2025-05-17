var r=Object.defineProperty;var i=(a,e,t)=>e in a?r(a,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[e]=t;var s=(a,e,t)=>i(a,typeof e!="symbol"?e+"":e,t);import{N as o,ba as n,aM as M,U as p,L as E}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class K extends o{constructor(t){super("TS.MapExt.GPEDtlDateFiledMaxMin");s(this,"JSDesc",` 
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
   `);t&&(this.MyPK=t)}get HisUAC(){const t=new p;return t.IsDelete=!0,t.IsUpdate=!0,t.IsInsert=!0,t}get EnMap(){const t=new E("Sys_MapExt","对从表列求值");return t.AddGroupAttr("基本设置"),t.AddMyPK(),t.AddDDLSQL(n.Doc,null,"从表","SELECT No,Name From Sys_MapDtl WHERE FK_MapData='@FK_MapData'",!0),t.AddTBString(n.FK_MapData,null,"对应的表单",!1,!1,0,50,200,!0,this.DescDoc),t.AddTBString(n.Tag1,null,"输入列",!0,!1,0,50,200,!0,this.DescTag1),t.SetPopList("Tag1",M.sqlMapAttrDTFields,!1,"200px","300px","选择字段","icon-people"),t.AddDDLStringEnum(n.Tag,"Max","计算方式","@Max=求最大@Min=求最小",!0),this._enMap=t,this._enMap}}export{K as GPEDtlDateFiledMaxMin};
