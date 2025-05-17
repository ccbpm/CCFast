var l=Object.defineProperty;var s=(o,r,e)=>r in o?l(o,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[r]=e;var a=(o,r,e)=>s(o,typeof r!="symbol"?r+"":r,e);import{j as d,U as n,h as T}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as t}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as p}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class m extends d{constructor(e){super("TS.MapExt.PopTreeEns");a(this,"NoteSearchTip",`
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
  `);e&&(this.MyPK=e)}get HisUAC(){const e=new n;return e.IsUpdate=!0,e}get EnMap(){const e=new T("Sys_MapExt","树干叶子弹窗");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddTBString(t.FK_MapData,null,"表单ID",!1,!1,0,50,200),e.AddTBString(t.ExtModel,"Pop","模式(大类)",!1,!1,0,50,200),e.AddTBString(t.ExtType,null,"类型(小类)",!1,!1,0,50,200),e.AddDDLEntities(t.FK_DBSrc,"local","数据源",new p,!0,null,!1),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),e.AddTBStringDoc(t.Tag1,null,"搜索数据源",!0,!1,!0,this.NoteTag1),e.AddTBStringDoc(t.Tag2,null,"左侧树列表数据源",!0,!1,!0,this.NoteTag2),e.AddTBString(t.Doc,null,"根节点树编号",!0,!1,0,50,200,!0,this.NotDoc),e.AddTBStringDoc(t.Tag3,null,"实体数据源",!0,!1,!0,this.NoteTag3),e.AddTBString(t.Tag,null,"数据列名与中文意思对照",!0,!1,0,50,200,!0,this.NoteTag),e.AddTBString(t.Tag5,null,"确定后执行的JS",!0,!1,0,50,200,!0,this.NoteTag5),e.AddGroupAttr("外观"),e.AddRadioBtn("ShowModel",0,"展示方式",!1,!1,"ShowModel","@0=POP弹出窗@1=下拉搜索选择",null,!0),e.AddRadioBtn("PopSelectType",1,"选择类型",!0,!0,"PopSelectType","@0=单选@1=多选",null,!0),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("BtnLab","查找","查找按钮标签",!0,!1,0,50,200),e.AddTBInt(t.H,400,"弹窗高度",!0,!1),e.AddTBInt(t.W,500,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",Title,BtnLab,SearchTip,ShowModel,PopSelectType,",this._enMap=e,this._enMap}}export{m as PopTreeEns};
