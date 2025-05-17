var D=Object.defineProperty;var h=(o,a,t)=>a in o?D(o,a,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[a]=t;var p=(o,a,t)=>h(o,typeof a!="symbol"?a+"":a,t);var N=(o,a,t)=>new Promise((l,n)=>{var c=r=>{try{s(t.next(r))}catch(i){n(i)}},S=r=>{try{s(t.throw(r))}catch(i){n(i)}},s=r=>r.done?l(r.value):Promise.resolve(r.value).then(c,S);s((t=t.apply(o,a)).next())});import{b9 as w,aM as T,W as m,aD as E,aB as j,aC as R}from"./entry/index-M8VErHPE-1727507756861.js";import{Template as y}from"./Template-Dw0AHgRY.js";import{GloComm as u}from"./GloComm-DZ1gELjv.js";import{Project as G}from"./Project-BtdGRfUQ.js";import{TaskAPI as k}from"./TaskAPI-Bna81sC2.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./TemplateNode-BtwFXyzM.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Track-BuOWI5GS.js";import"./ProjectTask-6IIhWWQ7.js";import"./EntityOID-BVVq-i_P.js";class U extends w{constructor(){super("GPN_TaskTree");p(this,"Daily",`
  #### 帮助
   - 有科目作为明细，记账人员都可以记账，有项目组人员范围。
   - 没有时限要求，每个人的工作按照时间段记账，可以按照科目汇总项目成本，比如研发类项目，日常持续的投入。
 
  `);p(this,"Section",`
  #### 帮助
   - 有先后顺序，不同工种协作完成，有一定的时限要求 
   - 比如开发类项目 技术支持类项目，有一定的明确的目标，类似于，project项目管理。 
   
  `);p(this,"Nondeterminacy",`
  #### 帮助 
  - 有目标，没有时限，多人协作，有项目里程碑，完成度. 
  - 比如，客户跟踪类型项目，项目组有明确的人员分工，但是没有顺序，没有时限。不同的角色协助完成一个客户的跟踪，有完成度，概率，有阶段。
  `);p(this,"Task",`
  #### 帮助
   - 也称为简单项目或者不确定性项目，没有模式的临时性任务，
    - 比如 申报知识产权，找几个人协作完成，可以有固定时限，每个子任务分给不同的人，处理完毕后需要汇报，认可，确认，可以树形结构的分发收回，子任务数不确定。
  `);this.PageTitle="新建任务树"}Init(){return N(this,null,function*(){const t=this.RequestVal("TemplateNo");yield new y(t).Retrieve(),this.AddGroup("A","模板类型"),this.TextBox1_Name("Section","新建任务树",this.Daily,"项目名称","收文任务"),this.SelectItemsByTreeEns("Section.Starter","负责人",this.Daily,!1,T.srcDeptLazily,"0",T.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByTreeEns("Section.Starter.Emps","参与人",this.Daily,!0,T.srcDeptLazily,"0",T.srcEmpLazily,"@No=账号@Name=名称@Tel=电话")})}GenerSorts(){return N(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,l,n,c,S){return N(this,null,function*(){if(t==="Section.Starter.Emps"){const s=this.RequestVal("TemplateNo"),r=new y(s);yield r.Retrieve();const i=yield k.Prj_CreateNo(s),e=new G(i);e.No=i,yield e.Retrieve(),e.Name=this.RequestVal("tb1","Section"),e.Manager=this.RequestVal("tb1","Section.Starter"),e.ManagerT=this.RequestVal("tb2","Section.Starter"),e.StarterNo=m.No,e.StarterName=m.Name,e.DeptNo=m.DeptNo,e.DeptName=m.DeptName,e.OrgNo=m.OrgNo,e.Emps=n,e.EmpsT=c,e.TemplateNo=r.No,e.TemplateName=r.Name,e.RDT=E.CurrentDate,e.PrjSta=2,e.SetPara("EnName","TS.TA.PrjTaskTree"),yield e.Update(),yield u.WriteFrmTrack("TS.TA.PrjTaskTree",e.No,"StartPrj","启动项目",m.Name+"启动项目.");const P=u.UrlEn("TS.TA.PrjTaskTree",e.No);return new j(R.OpenUrlByDrawer75,P)}})}}export{U as GPN_TaskTree};
