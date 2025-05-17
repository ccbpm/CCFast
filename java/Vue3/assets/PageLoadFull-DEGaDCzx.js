var o=Object.defineProperty;var n=(r,t,e)=>t in r?o(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var s=(r,t,e)=>n(r,typeof t!="symbol"?t+"":t,e);import{j as i,U as p,h as l}from"./entry/index-C6uBgOW5-1730430676707.js";import{a}from"./MapExt-DtQWKcAY.js";import{SFDBSrc as u}from"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";class h extends i{constructor(e){super("TS.MapExt.PageLoadFull");s(this,"Desc0",`
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

  `);s(this,"Desc1",`
  #### 帮助
   - 该选项可以为空,弹出框确定后执行的JS，可以直接写方法名或者方法()。
   - 用户数据返回填充后，对数据的处理。
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new p;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new l("Sys_MapExt","装载填充主表");return e.AddGroupAttr("填充主表"),e.AddMyPK(),e.AddDDLEntities(a.FK_DBSrc,"local","数据源",new u,!0,null,!1),e.AddTBStringDoc(a.Doc,null,"执行内容",!0,!1,!0,this.Desc0),e.AddTBStringDoc(a.Tag2,null,"确定后执行的JS",!0,!1,!0,this.Desc1),this._enMap=e,this._enMap}}export{h as PageLoadFull};
