var s=Object.defineProperty;var l=(o,a,e)=>a in o?s(o,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[a]=e;var r=(o,a,e)=>l(o,typeof a!="symbol"?a+"":a,e);import{N as n,ba as t,a0 as d,U as T,L as i}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class S extends n{constructor(e){super("TS.MapExt.DtlTreeEns");r(this,"NoteSearchTip",`
  #### 帮助
   - 显示在搜索文本框的背景文字，比如:
   - 请输入付款人名称,进行搜索.
   - 输入人员编号,名称，名称全拼,简拼关键字搜索
  `);r(this,"NoteTag1",`
  #### 帮助
   - 点击关键字执行搜索返回的数据源，@Key是关键字,是搜索的关键字.
   - For URL:/DataUser/Handler.ashx?DoType=SearchEmps&Keyword=@Key
   - For SQL: SELECT No,Name FROM Port_Emp WHERE No like '%@Key%' OR Name like '%@Key%'
  `);r(this,"NoteTag2",`
  #### 帮助
   - 设置一个可以返回json的数据源该数据源有No,Name,ParentNo三个约定的列.
   - For URL:/DataUser/Handler.ashx?DoType=ReqDepts
   - For SQL:SELECT No,Name, ParentNo FROM Port_Dept
  `);r(this,"NoteDoc",`
  #### 帮助
   - 支持ccbpm的表达式,比如:@WebUser.DeptNo , @FieldName ,@WebUser.OrgNo 
  `);r(this,"NoteTag3",`
  #### 帮助
   - 选择右边的树返回的详细信息列表数据源 ， @Key是关键字,是选择的树节点编号.
   - For URL:/DataUser/Handler.ashx?DoType=ReqEmpsByDeptNo&DeptNo=@Key
   - For SQL:SELECT No,Name FROM Port_Emp WHERE FK_Dept='@Key'
  `);r(this,"NoteTag",`
  #### 帮助
   - 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头.
   - 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件
  `);r(this,"NoteTag5",`
  #### 帮助
   - 该选项可以为空,弹出框确定后执行的JS，可以直接写方法名或者方法. 
  `);e&&(this.MyPK=e)}get HisUAC(){const e=new T;return e.IsUpdate=!0,e}get EnMap(){const e=new i("Sys_MapExt","从表导入");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddTBString(t.FK_MapData,null,"表单ID",!1,!1,0,50,200),e.AddDDLEntities(t.FK_DBSrc,"local","数据源",new d,!0,null,!1),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),e.AddTBStringDoc(t.Tag1,null,"搜索数据源",!0,!1,!0,this.NoteTag1),e.AddTBStringDoc(t.Tag2,null,"左侧树列表数据源",!0,!1,!0,this.NoteTag2),e.AddTBString(t.Doc,null,"根节点树编号",!0,!1,0,50,200,!0,this.NotDoc),e.AddTBStringDoc(t.Tag3,null,"实体数据源",!0,!1,!0,this.NoteTag3),e.AddTBString(t.Tag,null,"数据列名与中文意思对照",!0,!1,0,50,200,!0,this.NoteTag),e.AddTBString(t.Tag5,null,"确定后执行的JS",!0,!1,0,50,200,!0,this.NoteTag5),e.AddGroupAttr("外观"),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("BtnLab","查找","查找按钮标签",!0,!1,0,50,200),e.AddTBInt(t.H,400,"弹窗高度",!0,!1),e.AddTBInt(t.W,900,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",Title,BtnLab,SearchTip,",this._enMap=e,this._enMap}}export{S as DtlTreeEns};
