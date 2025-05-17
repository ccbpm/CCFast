var u=Object.defineProperty;var I=(a,r,e)=>r in a?u(a,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[r]=e;var m=(a,r,e)=>I(a,typeof r!="symbol"?r+"":r,e);var p=(a,r,e)=>new Promise((s,t)=>{var l=o=>{try{n(e.next(o))}catch(i){t(i)}},c=o=>{try{n(e.throw(o))}catch(i){t(i)}},n=o=>o.done?s(o.value):Promise.resolve(o.value).then(l,c);n((e=e.apply(a,r)).next())});import{b9 as N,aB as b,aC as f}from"./entry/index-M8VErHPE-1727507756861.js";import{GenerWorkers as h}from"./GenerWorker-D2cCQzTX.js";import P from"./Dev2InterfaceCCBill-C1eteExO.js";import{e as R}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";class D extends N{constructor(){super("GPN_BillReturn");m(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);m(this,"BPMN2",`
  #### 帮助
  - 导入符合bpmn2.0格式的文件.
  `);this.PageTitle="退回",this.SortNameLabel="退回到"}Init(){this.TextArea("Info","退回信息",this.HelpTodo,"退回原因","不同意，请重新修改","请输入退回原因，不能为空")}GenerSorts(){return p(this,null,function*(){const e=new h;yield e.Retrieve("WorkID",this.RequestVal("WorkID"),"PassSta",2,"Idx");for(let s=0;s<e.length;s++){const t=e[s];t.No=t.Idx,t.Name=t.EmpNo+","+t.EmpName}return e})}Save_TextBox_X(e,s,t,l,c){return p(this,null,function*(){if(e=="Info")try{const n=this.RequestVal("WorkID"),o=yield P.ReturnWork(Number.parseInt(n),Number.parseInt(s),t);return alert(o),new b(f.Message,o)}catch(n){R.error(n)}})}}export{D as GPN_BillReturn};
