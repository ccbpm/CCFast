var c=Object.defineProperty;var h=(a,r,e)=>r in a?c(a,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[r]=e;var n=(a,r,e)=>h(a,typeof r!="symbol"?r+"":r,e);var m=(a,r,e)=>new Promise((p,s)=>{var l=t=>{try{o(e.next(t))}catch(i){s(i)}},d=t=>{try{o(e.throw(t))}catch(i){s(i)}},o=t=>t.done?p(t.value):Promise.resolve(t.value).then(l,d);o((e=e.apply(a,r)).next())});import{b9 as N,H as g,aB as x,aC as P}from"./entry/index-M8VErHPE-1727507756861.js";import{e as y}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";class H extends N{constructor(){super("GPN_ImpOrg");n(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);n(this,"BPMN2",`
  #### 帮助
  - 导入符合bpmn2.0格式的文件.
  `);this.PageTitle="导入组织数据"}Init(){this.AddGroup("A","导入"),this.FileUpload("ImpOrgExcel","导入本机Excel模板","请上传符合ccbpm组织结构格式的模板数据",this.Imp),this.TextBox3_NameNoNote("ImpDingDing","钉钉组织",this.HelpTodo,"","规划中","Key2","Key3",""),this.TextBox3_NameNoNote("ImpWeiXin","企业组织",this.HelpTodo,"","规划中","Key2","Key3",""),this.TextBox3_NameNoNote("ImpHongShu","小红书",this.HelpTodo,"","规划中","Key2","Key3","")}GenerSorts(){return m(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,p,s,l,d){return m(this,null,function*(){if(e=="ImpOrgExcel")try{const o=new g("BP.WF.HttpHandler.GPMPage");o.AddFile(this.UploadFile),o.AddPara("FK_Sort",p),o.AddPara("ImpWay",s);const t=yield o.DoMethodReturnString("Template_Save");return new x(P.Message,(t==null?void 0:t.Msg)||"导入成功")}catch(o){y.error(o)}})}}export{H as GPN_ImpOrg};
