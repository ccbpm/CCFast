var E=Object.defineProperty;var R=(o,a,t)=>a in o?E(o,a,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[a]=t;var n=(o,a,t)=>R(o,typeof a!="symbol"?a+"":a,t);var N=(o,a,t)=>new Promise((l,p)=>{var S=r=>{try{i(t.next(r))}catch(s){p(s)}},u=r=>{try{i(t.throw(r))}catch(s){p(s)}},i=r=>r.done?l(r.value):Promise.resolve(r.value).then(S,u);i((t=t.apply(o,a)).next())});import{b9 as f,aM as c,W as m,aD as G,aB as d,aC as x}from"./entry/index-M8VErHPE-1727507756861.js";import{Template as w}from"./Template-Dw0AHgRY.js";import{GloComm as D}from"./GloComm-DZ1gELjv.js";import{Project as b}from"./Project-BtdGRfUQ.js";import{TaskAPI as g}from"./TaskAPI-Bna81sC2.js";import{SectionItems as B}from"./SectionItem-t1R9bUtU.js";import{PrjSectionDtl as I}from"./PrjSectionDtl-CDfkTdkG.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./TemplateNode-BtwFXyzM.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Track-BuOWI5GS.js";import"./ProjectTask-6IIhWWQ7.js";import"./EntityOID-BVVq-i_P.js";class Q extends f{constructor(){super("GPN_Section");n(this,"Daily",`
  #### 帮助
   - 有科目作为明细，记账人员都可以记账，有项目组人员范围。
   - 没有时限要求，每个人的工作按照时间段记账，可以按照科目汇总项目成本，比如研发类项目，日常持续的投入。
 
  `);n(this,"Section",`
  #### 帮助
   - 有先后顺序，不同工种协作完成，有一定的时限要求 
   - 比如开发类项目 技术支持类项目，有一定的明确的目标，类似于，project项目管理。 
   
  `);n(this,"Nondeterminacy",`
  #### 帮助 
  - 有目标，没有时限，多人协作，有项目里程碑，完成度. 
  - 比如，客户跟踪类型项目，项目组有明确的人员分工，但是没有顺序，没有时限。不同的角色协助完成一个客户的跟踪，有完成度，概率，有阶段。
  `);n(this,"Task",`
  #### 帮助
   - 也称为简单项目或者不确定性项目，没有模式的临时性任务，
    - 比如 申报知识产权，找几个人协作完成，可以有固定时限，每个子任务分给不同的人，处理完毕后需要汇报，认可，确认，可以树形结构的分发收回，子任务数不确定。
  `);this.PageTitle="新建固定项目"}Init(){return N(this,null,function*(){const t=this.RequestVal("TemplateNo");yield new w(t).Retrieve(),this.AddGroup("A","模板类型"),this.TextBox1_Name("Section","新建固定项目",this.Daily,"项目名称","技术支持项目"),this.SelectItemsByTreeEns("Section.Starter","项目负责人",this.Daily,!1,c.srcDeptLazily,"0",c.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByTreeEns("Section.Starter.Emps","参与人",this.Daily,!0,c.srcDeptLazily,"0",c.srcEmpLazily,"@No=账号@Name=名称@Tel=电话")})}GenerSorts(){return N(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,l,p,S,u){return N(this,null,function*(){if(t==="Section.Starter.Emps"){const i=this.RequestVal("TemplateNo"),r=new w(i);yield r.Retrieve();const s=yield g.Prj_CreateNo(i),e=new b(s);e.No=s,yield e.Retrieve(),e.Name=this.RequestVal("tb1","Section"),e.Manager=this.RequestVal("tb1","Section.Starter"),e.ManagerT=this.RequestVal("tb2","Section.Starter"),e.StarterNo=m.No,e.StarterName=m.Name,e.DeptNo=m.DeptNo,e.DeptName=m.DeptName,e.OrgNo=m.OrgNo,e.Emps=p,e.EmpsT=S,e.TemplateNo=r.No,e.TemplateName=r.Name,e.RDT=G.CurrentDate,e.PrjSta=2,e.SetPara("EnName","TS.TA.PrjSection"),yield e.Update();const T=new B;yield T.Retrieve("TemplateNo",i,"Idx");for(let P=0;P<T.length;P++){const j=T[P],y=new I;y.Name=j.Name,y.PrjNo=e.No,yield y.Insert()}yield D.WriteFrmTrack("TS.TA.PrjSection",e.No,"StartPrj","启动项目",m.Name+"启动项目.");const h=D.UrlEn("TS.TA.PrjSection",e.No);return new d(x.OpenUrlByDrawer75,h)}})}}export{Q as GPN_Section};
