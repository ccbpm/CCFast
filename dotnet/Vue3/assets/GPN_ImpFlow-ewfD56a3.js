var h=Object.defineProperty;var y=(o,r,t)=>r in o?h(o,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[r]=t;var p=(o,r,t)=>y(o,typeof r!="symbol"?r+"":r,t);var d=(o,r,t)=>new Promise((a,n)=>{var i=e=>{try{s(t.next(e))}catch(c){n(c)}},l=e=>{try{s(t.throw(e))}catch(c){n(c)}},s=e=>e.done?a(e.value):Promise.resolve(e.value).then(i,l);s((t=t.apply(o,r)).next())});import{b9 as F,aM as N,H as u,aB as m,aC as P}from"./entry/index-M8VErHPE-1727507756861.js";import{e as B}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";class w extends F{constructor(){super("GPN_ImpFlow");p(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);p(this,"BPMN2",`
  #### 帮助
  - 导入符合bpmn2.0格式的文件.
  `);this.PageTitle="流程导入"}Init(){this.AddGroup("A","流程导入"),this.FileUpload("Local","导入本机模板","请上传符合ccform表单格式的模式",this.Imp),this.FileUpload("BPMN2","导入BPM2格式模板","请上传文件",this.BPMN2),this.FileUpload("DingDing","导入钉钉格式模板","请上传文件",this.BPMN2);const a=N.AtParaStringToJson("@0=作为新流程导入1@1=作为新流程导入2@2=作为新流程导入3"),n=Object.keys(a),i=[];for(const l of n)i.push({No:l,Name:a[l]});this.SelectItemsByList("Local.Way","选择模式","说明",!1,JSON.stringify(i))}GenerSorts(){return d(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,a,n,i,l){return d(this,null,function*(){if(t=="Local.Way")try{const s=new u("BP.WF.HttpHandler.WF_Admin_AttrFlow");s.AddFile(this.UploadFile),s.AddPara("FK_Sort",a),s.AddPara("ImpWay",n);const e=yield s.DoMethodReturnString("Imp_Done");return new m(P.Message,(e==null?void 0:e.Msg)||"创建成功")}catch(s){B.error(s)}if(t=="BPMN2"){const s="已经取消了支持.";return new m(P.Error,s)}})}}export{w as GPN_ImpFlow};
