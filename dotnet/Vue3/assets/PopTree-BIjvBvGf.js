var l=Object.defineProperty;var s=(o,r,e)=>r in o?l(o,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[r]=e;var a=(o,r,e)=>s(o,typeof r!="symbol"?r+"":r,e);import{j as d,U as n,h as u}from"./entry/index-C6uBgOW5-1730430676707.js";import{a as t}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as i}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class B extends d{constructor(e){super("TS.MapExt.PopTree");a(this,"NoteSearchTip",` 
 
  #### 帮助
   - 显示在搜索文本框的背景文字，比如:
   - 请输入付款人名称,进行搜索。
   - 输入人员编号,名称，名称全拼,简拼关键字搜索。
   
   `);a(this,"NotTag1",` 
 
  #### 帮助
 
 
   - 点击关键字执行搜索返回的数据源，@Key是关键字,是搜索的关键字.
   - For URL:/DataUser/Handler.ashx?DoType=SearchEmps&Keyword=@Key
   - For SQL: SELECT No,Name FROM Port_Emp WHERE No like '%@Key%' OR Name like '%@Key%'
    
    `);a(this,"NotTag2",` 
 
  #### 帮助
   - 设置一个可以返回json的数据源该数据源有No,Name,ParentNo三个约定的列.
   - For URL:/DataUser/Handler.ashx?DoType=ReqDepts
   - For SQL:SELECT No,Name, ParentNo FROM Port_Dept ORDER BY Idx
  #### 懒加载说明:
   - 如果是懒加载，数据源里必须有 @Key 字段.
   - 初始化的时候，使用 @Key 作为父节点的编号。
   - 比如： SELECT No,Name,ParentNo FROM Port_Dept WHERE No='@Key' OR ParentNo='@Key' ORDER BY Idx
    `);a(this,"NotDoc",` 
 
  #### 帮助
 
 
   - 支持ccbpm的表达式,比如:@WebUser.DeptNo , @FieldName @WebUser.OrgNo
    
    `);a(this,"NotTag",` 
 
  #### 帮助
 
 
   - 该选项可以为空,在右上角的列表里查询或点树树干的数据源出现的列表,需要用中文显示列头.
   - 例如: No=编号,Name=名称,Addr=地址,Tel=电话,Email=邮件

    `);a(this,"NotTag5",` 
 
  #### 帮助
 
   - 该选项可以为空,弹出框确定后执行的JS，可以直接写方法名或者方法. 

  `);e&&(this.MyPK=e)}get HisUAC(){const e=new n;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new u("Sys_MapExt","树干弹窗");return e.AddGroupAttr("数据来源"),e.AddMyPK(),e.AddTBString(t.FK_MapData,null,"表单ID",!1,!1,0,50,200),e.AddTBString(t.ExtModel,"Pop","模式(大类)",!1,!1,0,50,200),e.AddTBString(t.ExtType,null,"类型(小类)",!1,!1,0,50,200),e.AddDDLEntities(t.FK_DBSrc,"local","数据源",new i,!0,null,!1),e.AddTBString("SearchTip",null,"搜索提示",!0,!1,0,50,200,!0,this.NoteSearchTip),e.AddTBStringDoc(t.Tag2,null,"树干列表数据源 ",!0,!1,!0,this.NotTag2),e.AddTBString(t.Doc,null,"根目录树编号",!0,!1,0,50,200,!0,this.NotDoc),e.AddTBString(t.Tag,null,"列名中文对照",!0,!1,0,50,200,!0,this.NotTag),e.AddTBString(t.Tag5,null,"确定后执行的JS",!0,!1,0,50,200,!0,this.NotTag5),e.AddBoolean("IsLazy",!1,"是否懒加载?",!0,!0,!0),e.AddBoolean("NodeCascade",!0,"父子节点是否级联",!0,!0,!0),e.AddBoolean("IsShowFullPath",!1,"是否全路径显示(不显示顶级父节点)",!0,!0,!0),e.AddGroupAttr("外观"),e.AddRadioBtn("ShowModel",0,"展示方式",!0,!0,"ShowModel","@0=POP弹出窗@1=下拉搜索选择",null,!0),e.AddRadioBtn("PopSelectType",1,"选择类型",!0,!0,"PopSelectType","@0=单选@1=多选",null,!0),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("BtnLab","查找","查找按钮标签",!0,!1,0,50,200),e.AddTBInt(t.H,400,"弹窗高度",!0,!1),e.AddTBInt(t.W,500,"弹窗宽度",!0,!1),e.AddTBAtParas(4e3),e.ParaFields=",PopSelectType,Title,BtnLab,SearchTip,ShowModel,PopSelectType,IsLazy,NodeCascade,IsShowFullPath,",this._enMap=e,this._enMap}beforeUpdateInsertAction(){return this.GetParaBoolean("IsLazy")==!0&&this.Tag2.includes("@Key")==!1&&alert("配置错误:树干列表数据源必须包含, @Key 表达式, 请参考说明."),Promise.resolve(!0)}}export{B as PopTree};
