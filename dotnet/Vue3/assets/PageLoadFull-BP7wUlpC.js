var n=Object.defineProperty;var l=(s,t,e)=>t in s?n(s,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[t]=e;var a=(s,t,e)=>l(s,typeof t!="symbol"?t+"":t,e);import{N as o,ba as r,a0 as u,U as i,L as p}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class M extends o{constructor(e){super("TS.MapExt.PageLoadFull");a(this,"Desc0",`
  #### 执行内容
   - 首先要选择数据源,根据数据源填写执行内容.
   - 执行内容后返回的是数据源，对数据源要要求是一行一列数据.
  #### SQL格式
   - SQL填写帮助.
   - 必须返回一行数据的 SQL或者数据源.
   - 返回的列名要与字段名进行对应，如果匹配的不管控件类型系统就会自动赋值。
   - 实例： SELECT Name as MingCheng, Tel as DianHua, Email FROM WF_EMP WHERE No='@WebUser.No'
   - @WebUser.No 系统约定的标记。
   - @WorkID 替换流程中的数据
  #### URL格式
   - 必须返回一行数据的的json格式的数据源。
   - 返回的列名要与字段名进行对应，如果匹配的不管控件类型系统就会自动赋值。
   - 实例： /App/Handler.ashx?DoType=EmpFull&Key=@Key
   - @Key 就是指选择的主键. 是系统约定的标记。
  #### 特别注意
  - 如果单据或者独立表单绑定了节点，他的启用受到节点与表单关系的限制，在有的节点不需要填充。
  - 系统默认节点绑定了表单，是不填充的。

  `);a(this,"Desc1",`
  #### 帮助
   - 该选项可以为空,弹出框确定后执行的JS，可以直接写方法名或者方法()。
   - 用户数据返回填充后，对数据的处理。
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new i;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new p("Sys_MapExt","装载填充主表");return e.AddGroupAttr("填充主表"),e.AddMyPK(),e.AddDDLEntities(r.FK_DBSrc,"local","数据源",new u,!0,null,!1),e.AddTBStringDoc(r.Doc,null,"执行内容",!0,!1,!0,this.Desc0),e.AddTBStringDoc(r.Tag2,null,"确定后执行的JS",!0,!1,!0,this.Desc1),this._enMap=e,this._enMap}}export{M as PageLoadFull};
