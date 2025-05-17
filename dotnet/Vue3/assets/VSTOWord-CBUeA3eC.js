var x=Object.defineProperty;var a=(r,t,e)=>t in r?x(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var o=(r,t,e)=>a(r,typeof t!="symbol"?t+"":t,e);import{E as n,f as d,U as l,h as p,i as u}from"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class h extends n{constructor(e){super("TS.Frm.VSTOWord");o(this,"HelpVSTOIsEnableSave",`
  #### 帮助
  - 是否允许保存？
  - 把vsto的表单作为打印模板的时候，用户打开文件，该文件是数据+模板文件.
  - 如果允许保存：用户修改后，就可以保存.
  `);e&&this.setPKVal(e)}get HisUAC(){const e=new l;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new p("Sys_MapData","VSTOWord表单模板");e.GroupBarShowModel=1,e.AddGroupAttr("基本属性"),e.AddTBStringPK("No",null,"表单编号",!0,!0,1,190,20),e.AddTBString("Name",null,"名称",!0,!1,0,500,20,!0),e.AddBoolean("VSTOWordIsEnableSave",!1,"是否允许保存生成的单据?",!0,!0,!0,this.HelpVSTOIsEnableSave);const s=new u;return s.Title="VSTOWord设计器",s.RefMethodType=d.FuncToolbar,s.ClassMethod="DoOpenIt",e.AddRefMethod(s),this._enMap=e,this._enMap}DoOpenIt(){return"检查结果如下..xxxxxxxxxxxxxx@xxxxxxx  @xxxxxxxxxx"}}export{h as VSTOWord};
