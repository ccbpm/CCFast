var i=Object.defineProperty;var l=(r,t,e)=>t in r?i(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var a=(r,t,e)=>l(r,typeof t!="symbol"?t+"":t,e);import{j as n,U as o,h as u}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as s}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class c extends n{constructor(e){super("TS.MapExt.TBFullCtrl2");a(this,"Desc2",`
  #### 说明
   
  1. 设置一个查询的SQL语句，该SQL必须包含 No, Name 列, 用与展示快速补全的部分。
  1. 该SQL必须包含 @Key 关键字，@Key 输入文本框的值.
  1. SQL返回的列与其他字段名称保持一致，就可以完成控件数据的自动填充。
  1. 比如:SELECT No,Name,Name as CaoZuoYuanMingCheng,Tel as DianHua,Email,FK_Dept FROM WF_Emp WHERE No LIKE '%@Key%'
  1. 为防止URL编码规定like的第一个%写成[%],如果like '%@Key%' 写成'[%]@Key%'
  `);a(this,"Desc1",`
  #### 说明
   
  1. 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头.
  1. 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件
  
  `);this.RefEnName="TS.Sys.MapExt",e&&(this.MyPK=e)}get HisUAC(){const e=new o;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new u("Sys_MapExt","文本框自动完成2");return e.AddMyPK(),e.AddMyPK(),e.AddDDLSysEnum(s.DBType,0,"数据源类型",!0,!0,"DBType","@0=执行SQL@1=执行url返回JSON@2=执行CCFromRef.js返回JSON",null,!1),e.AddTBStringDoc(s.Tag4,null,"数据源:",!0,!1,!0,this.Desc2),e.AddTBStringDoc(s.Tag3,null,"数据列名与中文意思对照 ",!0,!1,!0,this.Desc1),this._enMap=e,this._enMap}}export{c as TBFullCtrl2};
