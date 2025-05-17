var m=Object.defineProperty;var u=(s,t,e)=>t in s?m(s,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[t]=e;var c=(s,t,e)=>u(s,typeof t!="symbol"?t+"":t,e);var o=(s,t,e)=>new Promise((p,n)=>{var i=a=>{try{r(e.next(a))}catch(l){n(l)}},d=a=>{try{r(e.throw(a))}catch(l){n(l)}},r=a=>a.done?p(a.value):Promise.resolve(a.value).then(i,d);r((e=e.apply(s,t)).next())});import{b5 as x,H as I,G as P,l as h}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as G}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";class y extends x{constructor(){super("GPN_DtlImpContract");c(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);c(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 .  完善测试该方法.

  `);this.PageTitle="导入合同"}Init(){return o(this,null,function*(){this.AddGroup("A","导入合同"),this.FileUpload("Excel","导入Excel文件","请上传约定格式的文件","上传文件然后执行导入")})}GenerSorts(){return o(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,p,n,i,d){return o(this,null,function*(){if(e=="Excel")try{const r=new I("BP.App.YNJT.Handler_YNJT");r.AddFile(this.UploadFile),r.AddPara("WorkID",this.RequestVal("WorkID")),r.AddPara("FrmID",this.RequestVal("FrmID"));const a=yield r.DoMethodReturnString("ImpContract");return new P(h.Message,a)}catch(r){G.error(r)}})}}export{y as GPN_DtlImpContract};
