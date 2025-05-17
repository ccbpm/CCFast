var r=Object.defineProperty;var o=(x,t,e)=>t in x?r(x,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):x[t]=e;var a=(x,t,e)=>o(x,typeof t!="symbol"?t+"":t,e);import{I as n,aP as l,U as p,L as u,Y as d}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class M extends n{constructor(e){super("TS.Frm.VSTOExcel");a(this,"HelpVSTOIsEnableSave",`
  #### 帮助
  - 是否允许保存？
  - 把vsto的表单作为打印模板的时候，用户打开文件，该文件是数据+模板文件.
  - 如果允许保存：用户修改后，就可以保存.
    
  `);e&&this.setPKVal(e)}get HisUAC(){const e=new p;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new u("Sys_MapData","VSTOExcel表单模板");e.GroupBarShowModel=1,e.AddGroupAttr("基本属性"),e.AddTBStringPK("No",null,"表单编号",!0,!0,1,190,20),e.AddTBString("Name",null,"名称",!0,!1,0,500,20,!0),e.AddBoolean("VSTOExcelIsEnableSave",!1,"是否允许保存生成的单据?",!0,!0,!0,this.HelpVSTOIsEnableSave);const s=new d;return s.Title="VSTOExcel设计器",s.RefMethodType=l.FuncToolbar,s.ClassMethod="DoOpenIt",e.AddRefMethod(s),this._enMap=e,this._enMap}DoOpenIt(){return"检查结果如下..xxxxxxxxxxxxxx@xxxxxxx  @xxxxxxxxxx"}}export{M as VSTOExcel};
