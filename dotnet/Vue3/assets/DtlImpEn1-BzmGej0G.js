var N=Object.defineProperty;var E=(a,t,e)=>t in a?N(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var r=(a,t,e)=>E(a,typeof t!="symbol"?t+"":t,e);import{j as s,U as n,h as i}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as o}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as l}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class K extends s{constructor(e){super("TS.MapExt.DtlImpEn1");r(this,"NoteTag",` 
 
  #### 帮助
   - 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头..
   - 不为空时，设置几个字段则列表里面显示几个字段
   - 格式为:
   - 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件
   `);r(this,"NoteTag1",` 
  #### 帮助
   - 用户打开导入页面的初始化数据内容.
   - 比如: SELECT No,Name, No as EmpNo, Name as EmpName, Tel, Email FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
  #### 注意
   - 返回的列名于从表字段的ID相同,就可以匹配填充.
    `);r(this,"NoteTag2",` 
  #### 帮助
   - 用户输入关键字点击查询按钮所执行结果返回的数据源.
   - 比如: SELECT No,Name, No as EmpNo, Name as EmpName, Tel, Email FROM Port_Emp WHERE  Name LIKE '%@Key%' OR No LIKE '%@Key%'
  ##### 说明
  1. @Key 是文本框输入的参数.
  2. 返回的列名于从表的字段ID保持一致.

    `);r(this,"NoteTag3",` 
 
  #### 帮助
 
 
   - 比如For SQLServer: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR
     No LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
   - 比如For Oracle: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR No
     LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
   - 比如For MySQL: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR No
     LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
    `);e&&this.setPKVal(e)}get HisUAC(){const e=new n;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new i("Sys_MapExt","从表导入");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddDDLEntities(o.FK_DBSrc,"local","数据源",new l,!0,null,!1),e.AddTBStringDoc(o.Tag1,null,"初始化列表数据源",!0,!1,!0,this.NoteTag1),e.AddTBStringDoc(o.Tag2,null,"关键字查询数据源",!0,!1,!0,this.NoteTag2),e.AddTBString(o.Tag,null,"数据列名与中文意思对照",!0,!1,0,50,200,!0,this.NoteTag),e.AddGroupAttr("基本信息"),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),e.AddTBInt(o.H,500,"弹窗高度",!0,!1),e.AddTBInt(o.W,800,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",Title,SearchTip,",this._enMap=e,this._enMap}}export{K as DtlImpEn1};
