var E=Object.defineProperty;var R=(o,r,e)=>r in o?E(o,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[r]=e;var l=(o,r,e)=>R(o,typeof r!="symbol"?r+"":r,e);var n=(o,r,e)=>new Promise((y,p)=>{var D=a=>{try{i(e.next(a))}catch(m){p(m)}},S=a=>{try{i(e.throw(a))}catch(m){p(m)}},i=a=>a.done?y(a.value):Promise.resolve(a.value).then(D,S);i((e=e.apply(o,r)).next())});import{b5 as f,aM as N,W as s,D as G,G as d,l as x}from"./entry/index-C6uBgOW5-1730430676707.js";import{Template as u}from"./Template-CRuw6ZOu.js";import{GloComm as w}from"./GloComm-CmAl8MpM.js";import{Project as b}from"./Project-DpaPcyZg.js";import{TaskAPI as g}from"./TaskAPI-Dz19boyX.js";import{DailyItems as I}from"./DailyItem-C0yDUYZH.js";import{PrjDailyDtl as v}from"./PrjDailyDtl-DNhyFVUt.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./TemplateNode-D7I_95eR.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./Track-Dx-Niwh6.js";import"./ProjectTask-BycSSR5W.js";import"./EntityOID-DvdPWQGp.js";class tt extends f{constructor(){super("GPN_Daily");l(this,"Daily",`
  #### 帮助
   - 有科目作为明细，记账人员都可以记账，有项目组人员范围。
   - 没有时限要求，每个人的工作按照时间段记账，可以按照科目汇总项目成本，比如研发类项目，日常持续的投入。
 
  `);l(this,"Section",`
  #### 帮助
   - 有先后顺序，不同工种协作完成，有一定的时限要求 
   - 比如开发类项目 技术支持类项目，有一定的明确的目标，类似于，project项目管理。 
   
  `);l(this,"Nondeterminacy",`
  #### 帮助 
  - 有目标，没有时限，多人协作，有项目里程碑，完成度. 
  - 比如，客户跟踪类型项目，项目组有明确的人员分工，但是没有顺序，没有时限。不同的角色协助完成一个客户的跟踪，有完成度，概率，有阶段。
  `);l(this,"Task",`
  #### 帮助
   - 也称为简单项目或者不确定性项目，没有模式的临时性任务，
    - 比如 申报知识产权，找几个人协作完成，可以有固定时限，每个子任务分给不同的人，处理完毕后需要汇报，认可，确认，可以树形结构的分发收回，子任务数不确定。
  `);this.PageTitle="新建项目"}Init(){return n(this,null,function*(){const e=this.RequestVal("TemplateNo");yield new u(e).Retrieve(),this.AddGroup("A","模板类型"),this.TextBox1_Name("Daily","新建项目",this.Daily,"项目名称","产品研发"),this.SelectItemsByTreeEns("Daily.Starter","项目负责人",this.Daily,!1,N.srcDeptLazily,"0",N.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByTreeEns("Daily.Starter.Emps","参与人",this.Daily,!0,N.srcDeptLazily,"0",N.srcEmpLazily,"@No=账号@Name=名称@Tel=电话")})}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,y,p,D,S){return n(this,null,function*(){if(e==="Daily.Starter.Emps"){const i=this.RequestVal("TemplateNo"),a=new u(i);yield a.Retrieve();const m=yield g.Prj_CreateNo(i),t=new b(m);t.No=m,yield t.Retrieve(),t.Name=this.RequestVal("tb1","Daily"),t.Manager=this.RequestVal("tb1","Daily.Starter"),t.ManagerT=this.RequestVal("tb2","Daily.Starter"),t.StarterNo=s.No,t.StarterName=s.Name,t.DeptNo=s.DeptNo,t.DeptName=s.DeptName,t.OrgNo=s.OrgNo,t.Emps=p,t.EmpsT=D,t.TemplateNo=a.No,t.TemplateName=a.Name,t.RDT=G.CurrentDate,t.PrjSta=2,t.SetPara("EnName","TS.TA.PrjDaily"),yield t.Update();const c=new I;yield c.Retrieve("TemplateNo",i,"Idx");for(let T=0;T<c.length;T++){const j=c[T],P=new v;P.Name=j.Name,P.PrjNo=t.No,yield P.Insert()}yield w.WriteFrmTrack("TS.TA.PrjDaily",t.No,"StartPrj","启动项目",s.Name+"启动项目.");const h=w.UrlEn("TS.TA.PrjDaily",t.No);return new d(x.OpenUrlByDrawer75,h)}})}}export{tt as GPN_Daily};
