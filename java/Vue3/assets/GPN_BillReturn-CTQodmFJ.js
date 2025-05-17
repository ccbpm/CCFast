var u=Object.defineProperty;var I=(s,t,r)=>t in s?u(s,t,{enumerable:!0,configurable:!0,writable:!0,value:r}):s[t]=r;var m=(s,t,r)=>I(s,typeof t!="symbol"?t+"":t,r);var p=(s,t,r)=>new Promise((a,e)=>{var l=o=>{try{i(r.next(o))}catch(n){e(n)}},c=o=>{try{i(r.throw(o))}catch(n){e(n)}},i=o=>o.done?a(o.value):Promise.resolve(o.value).then(l,c);i((r=r.apply(s,t)).next())});import{b5 as N,G as b,l as f}from"./entry/index-C6uBgOW5-1730430676707.js";import{GenerWorkers as h}from"./GenerWorker-BontIOCy.js";import G from"./Dev2InterfaceCCBill-BgcYUWBE.js";import{b as P}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class j extends N{constructor(){super("GPN_BillReturn");m(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);m(this,"BPMN2",`
  #### 帮助
  - 导入符合bpmn2.0格式的文件.
  `);this.PageTitle="退回",this.SortNameLabel="退回到"}Init(){this.TextArea("Info","退回信息",this.HelpTodo,"退回原因","不同意，请重新修改","请输入退回原因，不能为空")}GenerSorts(){return p(this,null,function*(){const r=new h;yield r.Retrieve("WorkID",this.RequestVal("WorkID"),"PassSta",2,"Idx");for(let a=0;a<r.length;a++){const e=r[a];e.No=e.Idx,e.Name=e.EmpNo+","+e.EmpName}return r})}Save_TextBox_X(r,a,e,l,c){return p(this,null,function*(){if(r=="Info")try{const i=this.RequestVal("WorkID"),o=yield G.ReturnWork(Number.parseInt(i),Number.parseInt(a),e);return alert(o),new b(f.Message,o)}catch(i){P.error(i)}})}}export{j as GPN_BillReturn};
