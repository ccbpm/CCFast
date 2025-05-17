var h=Object.defineProperty;var y=(o,e,t)=>e in o?h(o,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[e]=t;var p=(o,e,t)=>y(o,typeof e!="symbol"?e+"":e,t);var d=(o,e,t)=>new Promise((a,n)=>{var i=r=>{try{s(t.next(r))}catch(c){n(c)}},l=r=>{try{s(t.throw(r))}catch(c){n(c)}},s=r=>r.done?a(r.value):Promise.resolve(r.value).then(i,l);s((t=t.apply(o,e)).next())});import{b5 as F,aM as N,H as u,G as m,l as P}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as B}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";class b extends F{constructor(){super("GPN_ImpFlow");p(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);p(this,"BPMN2",`
  #### 帮助
  - 导入符合bpmn2.0格式的文件.
  `);this.PageTitle="流程导入"}Init(){this.AddGroup("A","流程导入"),this.FileUpload("Local","导入本机模板","请上传符合ccform表单格式的模式",this.Imp),this.FileUpload("BPMN2","导入BPM2格式模板","请上传文件",this.BPMN2),this.FileUpload("DingDing","导入钉钉格式模板","请上传文件",this.BPMN2);const a=N.AtParaStringToJson("@0=作为新流程导入1@1=作为新流程导入2@2=作为新流程导入3"),n=Object.keys(a),i=[];for(const l of n)i.push({No:l,Name:a[l]});this.SelectItemsByList("Local.Way","选择模式","说明",!1,JSON.stringify(i))}GenerSorts(){return d(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,a,n,i,l){return d(this,null,function*(){if(t=="Local.Way")try{const s=new u("BP.WF.HttpHandler.WF_Admin_AttrFlow");s.AddFile(this.UploadFile),s.AddPara("FK_Sort",a),s.AddPara("ImpWay",n);const r=yield s.DoMethodReturnString("Imp_Done");return new m(P.Message,(r==null?void 0:r.Msg)||"创建成功")}catch(s){B.error(s)}if(t=="BPMN2"){const s="已经取消了支持.";return new m(P.Error,s)}})}}export{b as GPN_ImpFlow};
