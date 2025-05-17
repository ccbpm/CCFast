var l=Object.defineProperty;var n=(s,a,e)=>a in s?l(s,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[a]=e;var r=(s,a,e)=>n(s,typeof a!="symbol"?a+"":a,e);import{N as o,ba as t,a0 as d,U as u,L as T}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class D extends o{constructor(e){super("TS.MapExt.PopGroupList");r(this,"DescTag1",` 
  ### 说明
  1. 设置一个可以返回json的数据源该数据源有No,Name,ParentNo三个约定的列. 
  1. SQL事例1: SELECT No,Name FROM Demo_BanJi
  1. SQL事例2: SELECT No,Name FROM Port_Dept
  1. Url事例: /DataUser/Handler.ashx?DoType=ReqDepts
   `);r(this,"DescTag2",` 
   ### 说明
   1. 设置一个可以返回json的数据源该数据源有No,Name, 关联外键列 三个约定的列.  
   1. 比如:Url事例: /DataUser/Handler.ashx?DoType=Demo_Students
   1. SQL事例: SELECT No,Name,FK_BanJi FROM Demo_Student
   1. SQL事例: SELECT No,Name,FK_Dept FROM Port_Emp
    `);r(this,"DescTag5",` 
 
  #### 帮助
   - 该选项可以为空,弹出框确定后执行的JS，可以直接写方法名或者方法. 
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new u;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new T("Sys_MapExt","分组列表弹窗");return e.AddMyPK(),e.AddTBString(t.FK_MapData,null,"表单ID",!1,!1,0,50,200),e.AddTBString(t.ExtModel,"Pop","模式(大类)",!1,!1,0,50,200),e.AddTBString(t.ExtType,null,"类型(小类)",!1,!1,0,50,200),e.AddDDLEntities(t.FK_DBSrc,"local","数据源",new d,!0,null,!1),e.AddTBInt("ShowCol",3,"设置显示列数",!0,!1),e.AddTBString(t.Tag1,null,"分组数据源",!0,!1,0,50,200,!0,this.DescTag1),e.AddTBString(t.Tag2,null,"实体数据源 ",!0,!1,0,50,200,!0,this.DescTag2),e.AddTBString(t.Tag5,null,"确定后执行的JS",!0,!1,0,50,200,!0,this.DescTag5),e.AddGroupAttr("外观"),e.AddRadioBtn("ShowModel",0,"展示方式",!1,!1,"ShowModel","@0=POP弹出窗@1=下拉搜索选择",null,!0),e.AddRadioBtn("PopSelectType",1,"选择类型",!0,!0,"PopSelectType","@0=单选@1=多选",null,!0),e.AddTBString("Title",null,"标题",!0,!1,0,50,200,!0),e.AddTBString("BtnLab","查找","查找按钮标签",!0,!1,0,50,200),e.AddTBInt(t.H,400,"弹窗高度",!0,!1),e.AddTBInt(t.W,500,"弹窗宽度",!0,!1),e.AddBoolean("IsEnter",!1,"是否允许手工输入",!0,!0),e.AddTBAtParas(4e3),e.ParaFields=",ShowCol,IsEnter,Title,BtnLab,ShowModel,PopSelectType,",this._enMap=e,this._enMap}}export{D as PopGroupList};
