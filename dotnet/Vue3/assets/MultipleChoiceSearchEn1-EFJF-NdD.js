var i=Object.defineProperty;var l=(r,t,e)=>t in r?i(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var a=(r,t,e)=>l(r,typeof t!="symbol"?t+"":t,e);import{N as n,ba as s,a0 as u,U as p,L as c}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class M extends n{constructor(e){super("TS.MapExt.MultipleChoiceSearchEn1");a(this,"DescSearchTip",`
  #### 说明
  - 显示在搜索文本框的背景文字.
  - 输入城市名称,比如:beijing,bj,进行搜索.
  - 人员的编号,名称,拼音,进行模糊搜索.
  
  `);a(this,"DescTag2",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
  `);e&&(this.MyPK=e)}get HisUAC(){const e=new p;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new c("Sys_MapExt","搜索多选");return e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddDDLEntities(s.FK_DBSrc,"local","数据源",new u,!0,null,!1),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.DescSearchTip),e.AddDDLSysEnum(s.DBType,0,"数据源类型",!0,!0,"DBType","@0=执行SQL@1=执行url返回JSON@2=执行CCFromRef.js返回JSON",null,!1),e.AddRadioBtn("MultipleSelectType",1,"选择类型",!0,!0,"MultipleSelectType","@0=单选@1=多选",null,!0),e.AddTBString(s.Doc,null,"数据源表达式",!0,!1,0,50,200,!0,this.DescTag2),e.ParaFields=",SearchTip,MultipleSelectType,",e.AddTBAtParas(4e3),this._enMap=e,this._enMap}}export{M as MultipleChoiceSearchEn1};
