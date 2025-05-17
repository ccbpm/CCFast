var l=Object.defineProperty;var n=(o,t,e)=>t in o?l(o,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[t]=e;var r=(o,t,e)=>n(o,typeof t!="symbol"?t+"":t,e);import{j as s,U as i,h as d}from"./entry/index-C6uBgOW5-1730430676707.js";import{a}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as D}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class E extends s{constructor(e){super("TS.MapExt.PopTableSearch");r(this,"NoteSearchTip",` 
  #### 帮助
  - 显示在搜索文本框的背景文字，比如:
  - 请输入付款人名称,进行搜索。
  - 输入人员编号,名称，名称全拼,简拼关键字搜索。
  `);r(this,"NoteTag1",` 
  #### 帮助

  该设置对table查询有效,(可以为空)：日期的默认值是JS函数表达式.
  SQL格式为:Para必须是对应查询表中的字段
  $Para=BanJiNo#Label=所在班级#ListSQL=Select No,Name FROM Demo_BanJi
  $Para=XB#Label=性别#EnumKey=XB
  $Para=RegDate#Label=注册日期从#DefVal=(new Date( (new Date().setDate(-30 + new Date().getDate()))))
  $Para=RegDate#Label=到#DefVal=(new Date())
  URL格式为:
  $Para=BanJiNo#Label=所在班级#ListURL=/DataUser/Handler.ashx?xxx=sss
  执行CCFromRef.js返回JSON格式为:
  $Para=BanJiNo#Label=所在班级#ListFuncName=MyFunc
  `);r(this,"NoteTag2",` 
  #### 帮助
   - 设置一个查询的SQL语句，必须返回No,Name两个列。
   - 该参数支持ccbpm表达式,比如:SELECT No, Name FROM WF_Emp WHERE FK_Dept='@WebUser.DeptNo'
   - 必须有：设置的查询条件必须在数据源中包含,时间的查询条件的设置增加RegDate>='@DTFrom_RegDate' AND RegDate<='@DTTo_RegDate'
   - 分页:MySQL不包含PageIdx,PageSize;SQLServer必须包含PageIdx,PageSize;Oracle,KingBaseR3,KingBaseR6可以包含也可以不包含PageIdx,PageSize,
   - 比如For SQLServer:SELECT TOP @PageSize * FROM ( SELECT row_number() over(order by t.No) as rownumber,No,Name,Tel,Email FROM Demo_Student WHERE Name LIKE '%@Key%' AND BanJiNo='@BanJiNo' AND XB='@XB' AND RegDate>='@DTFrom_RegDate' AND RegDate<='@DTTo_RegDate' ) A WHERE rownumber > @PageIdx
   - 比如For MySQL:SELECT No,Name,Tel,Email FROM Demo_Student WHERE Name LIKE '%@Key%' AND BanJiNo='@BanJiNo' AND XB='@XB' AND RegDate>='@DTFrom_RegDate' AND RegDate<='@DTTo_RegDate' 
  `);r(this,"NoteTag3",` 
   #### 帮助
   - 分页总条数
   - 总条数: SELECT COUNT(no) FROM Demo_Student WHERE Name LIKE '%@Key%' AND BanJiNo='@BanJiNo' AND XB='@XB' AND RegDate>='@DTFrom_RegDate' AND RegDate<='@DTTo_RegDate'
  `);r(this,"NoteTag",` 
   #### 帮助
   - 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头.
   - 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件
  `);r(this,"NoteTag5",` 
 
  #### 帮助
 
   - 该选项可以为空,弹出框确定后执行的JS，可以直接写方法名或者方法. 

  `);e&&(this.MyPK=e)}get HisUAC(){const e=new i;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new d("Sys_MapExt","表格弹窗");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddTBString(a.FK_MapData,null,"表单ID",!1,!1,0,50,200),e.AddTBString(a.ExtModel,"Pop","模式(大类)",!1,!1,0,50,200),e.AddTBString(a.ExtType,null,"类型(小类)",!1,!1,0,50,200),e.AddDDLEntities(a.FK_DBSrc,"local","数据源",new D,!0,null,!1),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),e.AddTBStringDoc(a.Tag1,null,"查询条件设置",!0,!1,!0,this.NoteTag1),e.AddTBStringDoc(a.Tag2,null,"数据源",!0,!1,!0,this.NoteTag2),e.AddTBStringDoc(a.Tag3,null,"分页总数数据源",!0,!1,!0,this.NoteTag3),e.AddTBString(a.Tag,null,"数据列名与中文意思对照",!0,!1,0,50,200,!0,this.NoteTag),e.AddTBString(a.Tag5,null,"确定后执行的JS",!0,!1,0,50,200,!0,this.NoteTag5),e.AddGroupAttr("外观"),e.AddRadioBtn("ShowModel",0,"展示方式",!1,!1,"ShowModel","@0=POP弹出窗@1=下拉搜索选择",null,!0),e.AddRadioBtn("PopSelectType",1,"选择类型",!0,!0,"PopSelectType","@0=单选@1=多选",null,!0),e.AddBoolean("IsFirstLoad",!0,"打开是否直接加载数据",!0,!0,!1),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("BtnLab","查找","查找按钮标签",!0,!1,0,50,200),e.AddTBInt(a.H,500,"弹窗高度",!0,!1),e.AddTBInt(a.W,900,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",Title,BtnLab,SearchTip,ShowModel,PopSelectType,IsFirstLoad,",this._enMap=e,this._enMap}}export{E as PopTableSearch};
