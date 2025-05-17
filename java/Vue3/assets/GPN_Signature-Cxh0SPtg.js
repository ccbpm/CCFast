var h=Object.defineProperty;var P=(a,t,e)=>t in a?h(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var l=(a,t,e)=>P(a,typeof t!="symbol"?t+"":t,e);var i=(a,t,e)=>new Promise((o,s)=>{var p=r=>{try{n(e.next(r))}catch(d){s(d)}},m=r=>{try{n(e.throw(r))}catch(d){s(d)}},n=r=>r.done?o(r.value):Promise.resolve(r.value).then(p,m);n((e=e.apply(a,t)).next())});import{b5 as _,G as c,l as u,H as A}from"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class B extends _{constructor(){super("GPN_Signature");l(this,"ImpLocal",`
  #### 帮助 
   ##### 上传模板
   - 请上传您的表单模板.
   ##### 选择导入模式
   - 请选择导入模式.
   - 按照模版的表单编号导入1：如果该编号已经存在就提示错误.
   - 按照模版的表单编号导入2：如果该编号已经存在就直接覆盖.
   - 按照模版的表单编号导入3：如果该编号已经存在就增加@WebUser.OrgNo(组织编号)导入.
        `);l(this,"ImpFrmID",`
  #### 帮助
   - 从表单库导入
  `);this.PageTitle="字段签名"}Init(){return i(this,null,function*(){this.AddGroup("A","字段签名","icon-xxx"),this.AddBlank("0","图片签名",this.HelpUn),this.AddBlank("1","手写签名",this.HelpUn),this.AddBlank("2","电子签章",this.HelpUn)})}GenerSorts(){return i(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,o,s,p,m){return i(this,null,function*(){if(e==="ImpLocal.Way"){const n=new A("BP.WF.HttpHandler.WF_Admin_Template");n.AddFile(this.UploadFile),n.AddPara("RB_ImpType",s),n.AddPara("FrmSort",o);const r=yield n.DoMethodReturnString("ImpFrmLocal_Done");return r.includes("err@")?new c(u.Error,r):new c(u.Message,r)}})}}export{B as GPN_Signature};
