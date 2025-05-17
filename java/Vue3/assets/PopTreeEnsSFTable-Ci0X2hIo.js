var d=Object.defineProperty;var l=(o,t,e)=>t in o?d(o,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[t]=e;var a=(o,t,e)=>l(o,typeof t!="symbol"?t+"":t,e);import{j as s,U as T,h as n}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as r,b as p}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class P extends s{constructor(e){super("TS.MapExt.PopTreeEnsSFTable");a(this,"NoteSearchTip",`
  #### 帮助
   - 显示在搜索文本框的背景文字，比如:
   - 请输入付款人名称,进行搜索.
   - 输入人员编号,名称，名称全拼,简拼关键字搜索
  `);a(this,"NoteTag1",`
  #### 帮助
   - 点击关键字执行搜索返回的数据源，@Key是关键字,是搜索的关键字.
   - For URL:/DataUser/Handler.ashx?DoType=SearchEmps&Keyword=@Key
   - For SQL: SELECT No,Name FROM Port_Emp WHERE No like '%@Key%' OR Name like '%@Key%'
  `);a(this,"NoteTag2",`
  #### 帮助
   - 设置一个可以返回json的数据源该数据源有No,Name,ParentNo三个约定的列.
   - For URL:/DataUser/Handler.ashx?DoType=ReqDepts
   - For SQL:SELECT No,Name, ParentNo FROM Port_Dept
  `);a(this,"NoteDoc",`
  #### 帮助
   - 支持ccbpm的表达式,比如:@WebUser.DeptNo , @FieldName ,@WebUser.OrgNo 
  `);a(this,"NoteTag3",`
  #### 帮助
   - 选择右边的树返回的详细信息列表数据源 ， @Key是关键字,是选择的树节点编号.
   - For URL:/DataUser/Handler.ashx?DoType=ReqEmpsByDeptNo&DeptNo=@Key
   - For SQL:SELECT No,Name FROM Port_Emp WHERE FK_Dept='@Key'
  `);a(this,"NoteTag",`
  #### 帮助
   - 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头.
   - 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件
  `);a(this,"NoteTag5",`
  #### 帮助
   - 该选项可以为空,弹出框确定后执行的JS，可以直接写方法名或者方法. 
  `);e&&(this.MyPK=e)}get HisUAC(){const e=new T;return e.IsUpdate=!0,e}get EnMap(){const e=new n("Sys_MapExt","树干叶子弹窗");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddTBString(r.FK_MapData,null,"表单ID",!1,!1,0,50,200),e.AddTBString(r.ExtModel,"Pop","模式(大类)",!1,!1,0,50,200),e.AddTBString(r.ExtType,null,"类型(小类)",!1,!1,0,50,200),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),p.AddAttrSFTable(e,"Tag1","搜索数据源",1),p.AddAttrSFTable(e,"Tag2","左侧树列表数据源",0,1),e.AddTBString(r.Doc,null,"根节点树编号",!0,!1,0,50,200,!0,this.NotDoc),p.AddAttrSFTable(e,"Tag3","实体数据源",1),e.AddTBString(r.Tag,null,"数据列名与中文意思对照",!0,!1,0,50,200,!0,this.NoteTag),e.AddTBString(r.Tag5,null,"确定后执行的JS",!0,!1,0,50,200,!0,this.NoteTag5),e.AddGroupAttr("外观"),e.AddRadioBtn("ShowModel",1,"展示方式",!1,!0,"ShowModel","@0=POP弹出窗@1=下拉搜索选择",null,!0),e.AddRadioBtn("PopSelectType",1,"选择类型",!0,!0,"PopSelectType","@0=单选@1=多选",null,!0),e.AddRadioBtn("OpenPopType",1,"打开Pop弹出窗的方式",!0,!0,"OpenPopType","@0=双击打开@1=点击按钮打开",null,!0),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("BtnLab","查找","查找按钮标签",!0,!1,0,50,200),e.AddTBInt(r.H,400,"弹窗高度",!0,!1),e.AddTBInt(r.W,500,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",Title,BtnLab,SearchTip,ShowModel,PopSelectType,OpenPopType,",this._enMap=e,this._enMap}}export{P as PopTreeEnsSFTable};
