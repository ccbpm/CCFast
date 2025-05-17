var n=Object.defineProperty;var l=(s,t,e)=>t in s?n(s,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[t]=e;var a=(s,t,e)=>l(s,typeof t!="symbol"?t+"":t,e);import{N as p,ba as r,a0 as i,U as o,L as u}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class c extends p{constructor(e){super("TS.MapExt.TBFullCtrl1");a(this,"Desc1",`
  #### 说明
   - 填充SQL帮助
   1. 设置一个查询的SQL语句，该SQL必须包含 No, Name 列, 用与展示快速补全的部分。
   1. 该SQL必须包含 @Key 关键字，@Key 输入文本框的值.
   1. SQL返回的列与其他字段名称保持一致，就可以完成控件数据的自动填充。
   1. 比如: SELECT No,Name FROM WF_Emp WHERE No LIKE '%@Key%'
   1. 为防止URL编码规定like的第一个%写成[%],如果like '%@Key%' 写成'[%]@Key%'
   - 填充Url帮助
   1. 设置URL，返回的必须是json格式。
   1. 比如: /App/Handler.ashx?DoType=Emps&Key=@Key
   1. @Key 是输入的关键字

 
  `);this.RefEnName="TS.Sys.MapExt",e&&(this.MyPK=e)}get HisUAC(){const e=new o;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new u("Sys_MapExt","文本框自动完成");return e.AddMyPK(),e.AddDDLEntities(r.FK_DBSrc,"local","数据源",new i,!0,null,!1),e.AddTBStringDoc(r.Tag4,null,"搜索列表数据源配置:",!0,!1,!0,this.Desc1),this._enMap=e,this._enMap}}export{c as TBFullCtrl1};
