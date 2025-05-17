var R=Object.defineProperty;var l=(s,t,e)=>t in s?R(s,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[t]=e;var i=(s,t,e)=>l(s,typeof t!="symbol"?t+"":t,e);var m=(s,t,e)=>new Promise((N,r)=>{var p=o=>{try{n(e.next(o))}catch(a){r(a)}},E=o=>{try{n(e.throw(o))}catch(a){r(a)}},n=o=>o.done?N(o.value):Promise.resolve(o.value).then(p,E);n((e=e.apply(s,t)).next())});import{b5 as d,B as u,G as S,l as D}from"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class h extends d{constructor(){super("GPN_Adminer");i(this,"NewDept",`
  #### 帮助
   - ccbpm支持一人多部门,每个部门支持多岗位.
  #### 详细说明
   - 组织结构可以与现在的系统集成, 集成信息: https://doc.ccbpm.cn
`);this.PageTitle="设置二级管理员"}Init(){const e=this.RequestVal("RefPKVal"),N=`SELECT No,Name,ParentNo FROM Port_Dept WHERE ParentNo='${e}' OR No='${e}' 
    UNION
    SELECT No,Name,ParentNo FROM Port_Dept WHERE ParentNo
       IN ( SELECT No FROM Port_Dept WHERE ParentNo='${e}' OR No='${e}' ) 
    `;this.SelectItemsByTreeEns("Emp","选择管理员",this.HelpUn,!1,N,e,"SELECT No,Name FROM Port_Emp B WHERE FK_Dept='@Key'","")}GenerSorts(){return m(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,N,r,p,E){return m(this,null,function*(){const n=this.RequestVal("RefPKVal"),o=r,a=p,c=new u("BP.WF.Port.Dept",n);yield c.RetrieveFromDBSources();const P=yield c.DoMethodReturnString("DoSetAdminer",o,a);return new S(D.CloseAndReload,P)})}}export{h as GPN_Adminer};
