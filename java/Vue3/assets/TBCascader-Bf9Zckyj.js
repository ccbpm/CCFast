var o=Object.defineProperty;var n=(r,t,e)=>t in r?o(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var s=(r,t,e)=>n(r,typeof t!="symbol"?t+"":t,e);import{j as i,U as c,h as p}from"./entry/index-C6uBgOW5-1730430676707.js";import{a}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as N}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class y extends i{constructor(e){super("TS.MapExt.TBCascader");s(this,"Desc1",`
  #### 说明
   - 设置一个数据源：必须包含No,Name两个列. 
   - 数据源支持ccbpm表达式,比如: SELECT * FROM Port_Dept WHERE ParentNo='@WebUser.DeptNo'
   - 比如: SELECT No,Name FROM CN_ShengFen 
   - 关于ccbpm表达式: @WebUser.No,@WebUser.Name,@WebUser.DeptNo 是当前用户登陆信息.
   - 暂时不支持字典.
  `);s(this,"Desc2",`
  #### 说明
   1. 设置一个查询的SQL语句，该SQL必须包No,Name 两个列。
   1. 该SQL必须包含 @Key 关键字，@Key 就是上次选择的第1级数据项的编号.
   1. 比如: SELECT No,Name FROM CN_City WHERE ShengFenNo='@Key'
   1. 暂时不支持字典.
  `);s(this,"Desc3",`
  #### 说明
   1. 设置一个查询的SQL语句，该SQL必须包No,Name 两个列。  
   1. 该SQL必须包含 @Key 关键字，@Key 就是上次选择的第2级数据项的编号.
   1. 比如: SELECT No,Name FROM CN_QuXian WHERE CityNo='@Key'
   1. 暂时不支持字典.
  `);this.RefEnName="TS.Sys.MapExt",e&&(this.MyPK=e)}get HisUAC(){const e=new c;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new p("Sys_MapExt","级联数据源");return e.AddMyPK(),e.AddDDLEntities(a.FK_DBSrc,"local","数据源",new N,!0,null,!1),e.AddTBStringDoc(a.Tag1,null,"1级数据源:",!0,!1,!0,this.Desc1),e.AddTBStringDoc(a.Tag2,null,"2级数据源:",!0,!1,!0,this.Desc2),e.AddTBStringDoc(a.Tag3,null,"3级数据源:",!0,!1,!0,this.Desc3),this._enMap=e,this._enMap}}export{y as TBCascader};
