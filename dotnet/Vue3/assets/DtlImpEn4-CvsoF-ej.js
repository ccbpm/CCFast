var E=Object.defineProperty;var s=(a,t,e)=>t in a?E(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var r=(a,t,e)=>s(a,typeof t!="symbol"?t+"":t,e);import{N,ba as o,U as S,L as n}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class m extends N{constructor(e){super("TS.MapExt.DtlImpEn4");r(this,"NoteTag",` 
 
  #### 帮助
   - 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头..
   - 不为空时，设置几个字段则列表里面显示几个字段
   - 格式为:
   - 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件
   `);r(this,"NoteTag1",` 
  #### 帮助
   - 用户打开导入页面的初始化数据内容.
   - 比如: SELECT No as EmpNo, Name as EmpName, Tel, Email FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
  #### 注意
   - 返回的列名于从表字段的ID相同,就可以匹配填充.
    `);r(this,"NoteTag2",` 
  #### 帮助
   - 用户输入关键字点击查询按钮所执行结果返回的数据源.
   - 比如: SELECT No as EmpNo, Name as EmpName, Tel, Email FROM Port_Emp WHERE  Name LIKE '%@Key%' OR No LIKE '%@Key%'
  ##### 说明
  1. @Key 是文本框输入的参数.
  2. 返回的列名于从表的字段ID保持一致.

    `);r(this,"NoteTag3",` 
 
  #### 帮助
 
 
   - 比如For SQLServer: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR
     No LIKE '%@Key%') AND FK_BanJi=@FK_BanJi ANND XB=@XB 
   - 比如For Oracle: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR No
     LIKE '%@Key%') AND FK_BanJi=@FK_BanJi ANND XB=@XB 
   - 比如For MySQL: SELECT count(No) FROM Demo_Student WHERE (Name LIKE '%@Key%' OR No
     LIKE '%@Key%') AND FK_BanJi=@FK_BanJi ANND XB=@XB 
    `);e&&this.setPKVal(e)}get HisUAC(){const e=new S;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new n("Sys_MapExt","从表导入");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddTBStringDoc(o.Tag1,null,"初始化列表数据源",!0,!1,!0,this.NoteTag1),e.SetPopGroupList("Tag1","SELECT No,Name FROM Sys_DBSrc","SELECT No,Name,FK_SFDBSrc FROM Sys_SFSearch ",!1),e.AddTBStringDoc(o.Tag2,null,"关键字查询数据源",!0,!1,!0,this.NoteTag2),e.SetPopGroupList("Tag2","SELECT No,Name FROM Sys_DBSrc","SELECT No,Name,FK_SFDBSrc FROM Sys_SFSearch ",!1),e.AddTBString(o.Tag,null,"数据列名与中文意思对照",!0,!1,0,50,200,!0,this.NoteTag),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),e.AddTBInt(o.H,500,"弹窗高度",!0,!1),e.AddTBInt(o.W,800,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",Title,SearchTip,",this._enMap=e,this._enMap}}export{m as DtlImpEn4};
