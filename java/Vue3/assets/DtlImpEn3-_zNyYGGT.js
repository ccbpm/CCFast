var N=Object.defineProperty;var n=(o,a,e)=>a in o?N(o,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[a]=e;var r=(o,a,e)=>n(o,typeof a!="symbol"?a+"":a,e);import{j as E,U as i,h as l}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as t}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as s}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class A extends E{constructor(e){super("TS.MapExt.DtlImpEn3");r(this,"NoteTag",` 
 
  #### 帮助

   - 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头..
   - 不为空时，设置几个字段则列表里面显示几个字段
   - 格式为:
   - 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件
   
   `);r(this,"NoteTag1",` 
 
  #### 帮助
   - 该设置对table查询有效,(可以为空)：日期的默认值是JS函数表达式.
   - SQL格式为:
   - $Para=BanJiNo#Label=所在班级#ListSQL=Select No,Name FROM Demo_BanJi 
   - $Para=XB#Label=性别#EnumKey=XB
   - $Para=DTFrom#Label=注册日期从#DefVal=(new Date( (new Date().setDate(-30 + new Date().getDate()))))
   - $Para=DTTo#Label=到#DefVal=(new Date())
   - URL格式为:
   - $Para=BanJiNo#Label=所在班级#ListURL=/DataUser/Handler.ashx?xxx=sss 
   - 执行CCFromRef.js返回JSON格式为:
   - $Para=BanJiNo#Label=所在班级#ListFuncName=MyFunc 
    
    `);r(this,"NoteTag2",` 
 
  #### 帮助
 
 
   -设置一个查询的SQL语句，必须返回No,Name两个列。
   - 该参数支持ccbpm表达式,比如:SELECT No, Name FROM WF_Emp WHERE FK_Dept='@WebUser.DeptNo'
   - 必须有：@PageCount @PageSize @Key 三个参数,分别标识:@PageCount =第几页, @PageSize=每页大小. @Key=关键字
   - 比如For SQLServer: SELECT TOP @PageSize * FROM ( SELECT row_number() over(order by
      t.No) as rownumber,No,Name,Tel,Email FROM Demo_Student WHERE Name LIKE '%@Key%'
      ) A WHERE rownumber > @PageCount 
   - 总条数: SELECT COUNT(no) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR No LIKE '%@Key%')
      AND BanJiNo=@BanJiNo AND XB=@XB 
   - 比如For Oracle: SELECT No,Name,Email,Tel FROM Demo_Student WHERE (Name LIKE '%@Key%'
       OR No LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
   - 比如For MySQL: SELECT No,Name,Email,Tel FROM Demo_Student WHERE (Name LIKE '%@Key%'
       OR No LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
   - 支持ccbpm的表达式,比如:@WebUser.DeptNo , @FieldName @WebUser.OrgNo
    
    `);r(this,"NoteTag3",` 
 
  #### 帮助
 
 
   - 比如For SQLServer: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR
     No LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
   - 比如For Oracle: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR No
     LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
   - 比如For MySQL: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR No
     LIKE '%@Key%') AND BanJiNo=@BanJiNo AND XB=@XB 
    `);r(this,"NoteTag4",` 
 
  #### 帮助
 
   - 主键不为空，SQL查询的列表设置主键时，不会重复导入该数据
   - 主键为空时，则为选择的全部导入，不过滤已经导入的数据

  `);e&&this.setPKVal(e)}get HisUAC(){const e=new i;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new l("Sys_MapExt","从表导入");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddDDLEntities(t.FK_DBSrc,"local","数据源",new s,!0,null,!1),e.AddTBStringDoc(t.Tag1,null,"查询条件设置",!0,!1,!0,this.NoteTag1),e.AddTBStringDoc(t.Tag2,null,"查询数据源 ",!0,!1,!0,this.NoteTag2),e.AddTBStringDoc(t.Tag3,null,"总条数",!0,!1,!0,this.NoteTag3),e.AddTBStringDoc(t.Tag,null,"数据列名与中文意思对照",!0,!1,!0,this.NoteTag),e.AddGroupAttr("基本信息"),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),e.AddTBInt(t.H,500,"弹窗高度",!0,!1),e.AddTBInt(t.W,800,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",Title,SearchTip,",this._enMap=e,this._enMap}}export{A as DtlImpEn3};
