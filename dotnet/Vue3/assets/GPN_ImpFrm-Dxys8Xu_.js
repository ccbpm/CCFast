var u=Object.defineProperty;var F=(s,r,t)=>r in s?u(s,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[r]=t;var l=(s,r,t)=>F(s,typeof r!="symbol"?r+"":r,t);var c=(s,r,t)=>new Promise((a,n)=>{var i=e=>{try{o(t.next(e))}catch(p){n(p)}},m=e=>{try{o(t.throw(e))}catch(p){n(p)}},o=e=>e.done?a(e.value):Promise.resolve(e.value).then(i,m);o((t=t.apply(s,r)).next())});import{FrmSorts as h}from"./FrmSort-BiT7edPu.js";import{b9 as y,aM as x,aB as d,aC as I,H as A}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class G extends y{constructor(){super("GPN_ImpFrm");l(this,"ImpLocal",`
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
  `);this.PageTitle="导入表单"}Init(){return c(this,null,function*(){this.AddGroup("A","导入表单","icon-xxx"),this.FileUpload("ImpLocal","从本机导入","",this.ImpLocal);const a=x.AtParaStringToJson("@0=按照模版的表单编号导入1@1=按照模版的表单编号导入2@2=按照模版的表单编号导入3"),n=Object.keys(a),i=[];for(const m of n)i.push({No:m,Name:a[m]});this.SelectItemsByList("ImpLocal.Way","选择模式",this.ImpLocal,!1,JSON.stringify(i)),this.AddIcon("ImpFrmID","icon-user"),this.AddIcon("ImpFrmIDxx","icon-user"),this.AddIcon("ImpFrmIDxx","icon-user")})}GenerSorts(){return c(this,null,function*(){const t=new h;return yield t.Init(),yield t.RetrieveAll(),t})}Save_TextBox_X(t,a,n,i,m){return c(this,null,function*(){if(t==="ImpLocal.Way"){const o=new A("BP.WF.HttpHandler.WF_Admin_Template");o.AddFile(this.UploadFile),o.AddPara("RB_ImpType",n),o.AddPara("FrmSort",a);const e=yield o.DoMethodReturnString("ImpFrmLocal_Done");return e.includes("err@")?new d(I.Error,e):new d(I.Message,e)}})}}export{G as GPN_ImpFrm};
