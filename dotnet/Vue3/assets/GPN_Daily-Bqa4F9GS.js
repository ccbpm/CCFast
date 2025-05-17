var E=Object.defineProperty;var R=(o,r,t)=>r in o?E(o,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[r]=t;var l=(o,r,t)=>R(o,typeof r!="symbol"?r+"":r,t);var n=(o,r,t)=>new Promise((y,p)=>{var D=a=>{try{i(t.next(a))}catch(s){p(s)}},S=a=>{try{i(t.throw(a))}catch(s){p(s)}},i=a=>a.done?y(a.value):Promise.resolve(a.value).then(D,S);i((t=t.apply(o,r)).next())});import{b9 as f,aM as N,W as m,aD as G,aB as d,aC as x}from"./entry/index-M8VErHPE-1727507756861.js";import{Template as u}from"./Template-Dw0AHgRY.js";import{GloComm as w}from"./GloComm-DZ1gELjv.js";import{Project as b}from"./Project-BtdGRfUQ.js";import{TaskAPI as g}from"./TaskAPI-Bna81sC2.js";import{DailyItems as B}from"./DailyItem-B8UrYShK.js";import{PrjDailyDtl as I}from"./PrjDailyDtl-DIH-RDxD.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./TemplateNode-BtwFXyzM.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Track-BuOWI5GS.js";import"./ProjectTask-6IIhWWQ7.js";import"./EntityOID-BVVq-i_P.js";class Q extends f{constructor(){super("GPN_Daily");l(this,"Daily",`
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
  `);this.PageTitle="新建项目"}Init(){return n(this,null,function*(){const t=this.RequestVal("TemplateNo");yield new u(t).Retrieve(),this.AddGroup("A","模板类型"),this.TextBox1_Name("Daily","新建项目",this.Daily,"项目名称","产品研发"),this.SelectItemsByTreeEns("Daily.Starter","项目负责人",this.Daily,!1,N.srcDeptLazily,"0",N.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByTreeEns("Daily.Starter.Emps","参与人",this.Daily,!0,N.srcDeptLazily,"0",N.srcEmpLazily,"@No=账号@Name=名称@Tel=电话")})}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,y,p,D,S){return n(this,null,function*(){if(t==="Daily.Starter.Emps"){const i=this.RequestVal("TemplateNo"),a=new u(i);yield a.Retrieve();const s=yield g.Prj_CreateNo(i),e=new b(s);e.No=s,yield e.Retrieve(),e.Name=this.RequestVal("tb1","Daily"),e.Manager=this.RequestVal("tb1","Daily.Starter"),e.ManagerT=this.RequestVal("tb2","Daily.Starter"),e.StarterNo=m.No,e.StarterName=m.Name,e.DeptNo=m.DeptNo,e.DeptName=m.DeptName,e.OrgNo=m.OrgNo,e.Emps=p,e.EmpsT=D,e.TemplateNo=a.No,e.TemplateName=a.Name,e.RDT=G.CurrentDate,e.PrjSta=2,e.SetPara("EnName","TS.TA.PrjDaily"),yield e.Update();const c=new B;yield c.Retrieve("TemplateNo",i,"Idx");for(let T=0;T<c.length;T++){const j=c[T],P=new I;P.Name=j.Name,P.PrjNo=e.No,yield P.Insert()}yield w.WriteFrmTrack("TS.TA.PrjDaily",e.No,"StartPrj","启动项目",m.Name+"启动项目.");const h=w.UrlEn("TS.TA.PrjDaily",e.No);return new d(x.OpenUrlByDrawer75,h)}})}}export{Q as GPN_Daily};
