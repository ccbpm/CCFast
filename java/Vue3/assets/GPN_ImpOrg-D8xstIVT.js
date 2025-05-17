var c=Object.defineProperty;var h=(s,r,e)=>r in s?c(s,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[r]=e;var n=(s,r,e)=>h(s,typeof r!="symbol"?r+"":r,e);var m=(s,r,e)=>new Promise((p,a)=>{var l=t=>{try{o(e.next(t))}catch(i){a(i)}},d=t=>{try{o(e.throw(t))}catch(i){a(i)}},o=t=>t.done?p(t.value):Promise.resolve(t.value).then(l,d);o((e=e.apply(s,r)).next())});import{b5 as N,H as g,G as x,l as P}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as y}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";class G extends N{constructor(){super("GPN_ImpOrg");n(this,"Imp",`
  #### 帮助
   - 上传模板、选择模式进行导入流程操作.
  ##### 选择模式说明
   - 作为新流程导入1：由ccbpm自动生成新的流程编号
   - 作为新流程导入2：使用流程模版里面的流程编号，如果该编号已经存在系统则会提示错误
   - 作为新流程导入3：使用流程模版里面的流程编号，如果该编号已经存在系统则会覆盖此流程
  `);n(this,"BPMN2",`
  #### 帮助
  - 导入符合bpmn2.0格式的文件.
  `);this.PageTitle="导入组织数据"}Init(){this.AddGroup("A","导入"),this.FileUpload("ImpOrgExcel","导入本机Excel模板","请上传符合ccbpm组织结构格式的模板数据",this.Imp),this.TextBox3_NameNoNote("ImpDingDing","钉钉组织",this.HelpTodo,"","规划中","Key2","Key3",""),this.TextBox3_NameNoNote("ImpWeiXin","企业组织",this.HelpTodo,"","规划中","Key2","Key3",""),this.TextBox3_NameNoNote("ImpHongShu","小红书",this.HelpTodo,"","规划中","Key2","Key3","")}GenerSorts(){return m(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,p,a,l,d){return m(this,null,function*(){if(e=="ImpOrgExcel")try{const o=new g("BP.WF.HttpHandler.GPMPage");o.AddFile(this.UploadFile),o.AddPara("FK_Sort",p),o.AddPara("ImpWay",a);const t=yield o.DoMethodReturnString("Template_Save");return new x(P.Message,(t==null?void 0:t.Msg)||"导入成功")}catch(o){y.error(o)}})}}export{G as GPN_ImpOrg};
