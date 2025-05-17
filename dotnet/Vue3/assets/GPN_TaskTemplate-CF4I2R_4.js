var k=Object.defineProperty;var f=(m,r,t)=>r in m?k(m,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):m[r]=t;var l=(m,r,t)=>f(m,typeof r!="symbol"?r+"":r,t);var p=(m,r,t)=>new Promise((T,s)=>{var P=o=>{try{e(t.next(o))}catch(a){s(a)}},x=o=>{try{e(t.throw(o))}catch(a){s(a)}},e=o=>o.done?T(o.value):Promise.resolve(o.value).then(P,x);e((t=t.apply(m,r)).next())});import{b9 as h,aB as y,aC as S}from"./entry/index-M8VErHPE-1727507756861.js";import{Sorts as A}from"./Sort-DL2UOyFV.js";import{Template as u}from"./Template-Dw0AHgRY.js";import{GloComm as B}from"./GloComm-DZ1gELjv.js";import{DailyItem as i}from"./DailyItem-B8UrYShK.js";import{D as G}from"./DBAccess-CzjFzLoq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./TemplateNode-BtwFXyzM.js";import"./FrmTrack-0uAZQ3B_.js";class R extends h{constructor(){super("GPN_TaskTemplate");l(this,"Daily",`
  #### 帮助
   - 有科目作为明细，记账人员都可以记账，有项目组人员范围。
   - 没有时限要求，每个人的工作按照时间段记账，可以按照科目汇总项目成本，比如研发类项目，日常持续的投入。
 
  `);l(this,"Section",`
  #### 帮助
   - 有目标，有时限，有先后顺序，不同工种协作完成，有一定的时限要求.
   - 比如开发类项目 技术支持类项目，有一定的明确的目标，类似于，project项目管理。
   - 也称为流水性质的项目.
   - 比如:ccflow技术支持类项目,立项后分: 培训、技术支持、商务验收 三个模块. 
   
  `);l(this,"Nondeterminacy",`
  #### 帮助
  - 有目标，有时限，多人协作，有项目里程碑，完成度. 
  - 内容不确定性,有里程碑, 每个任务有先后关系.
  - 比如，客户跟踪类型项目，项目组有明确的人员分工，但是没有顺序，没有时限。不同的角色协助完成一个客户的跟踪，有完成度，概率，有阶段。
  - 类似于 project 项目管理一样.
  `);l(this,"Task",`
  #### 帮助
   - 也称为简单项目或者不确定性项目，没有模式的临时性任务。
   - 比如 申报知识产权：找几个人协作完成，可以有固定时限，每个子任务分给不同的人，处理完毕后需要汇报，认可，确认，可以树形结构的分发收回，子任务数不确定。
  `);this.PageTitle="新建项目模板"}Init(){return p(this,null,function*(){this.AddGroup("A","项目模板类型"),this.TextBox1_Name("Daily","日常项目",this.Daily,"模板名称","产品研发"),this.TextBox1_Name("Section","阶段性固定模式",this.Section,"模板名称","技术支持"),this.TextBox1_Name("Nondeterminacy","不确定性(project)项目",this.Nondeterminacy,"模板名称","外包项目实施"),this.TextBox1_Name("TaskTree","任务树类型",this.Task,"模板名称","收文任务")})}GenerSorts(){return p(this,null,function*(){const t=new A;return yield t.RetrieveAll(),t})}Save_TextBox_X(t,T,s,P,x){return p(this,null,function*(){if(t==="Daily"){const e=new u;e.Name=s,e.SortNo=T,e.TaskModel=t,e.BillNoFormat="{yyyy}-{MM}-{LSH4}",e.SetPara("EnName","TS.TA.Template.Daily"),yield e.Insert();const o=B.UrlEn("TS.TA.Template.Daily",e.No),a=new i;a.TemplateNo=e.No,a.Name="日常工作模块1",a.ParentNo=e.No,yield a.Insert();const n=new i;n.TemplateNo=e.No,n.Name="字模块1",n.ParentNo=a.No,yield n.Insert();const N=new i;N.TemplateNo=e.No,N.Name="字模块2",N.ParentNo=a.No,yield N.Insert();const w=new i;w.Name="日常工作模块2",w.TemplateNo=e.No,a.ParentNo=e.No,yield w.Insert();const c=new i;return c.TemplateNo=e.No,c.No=G.GenerGUID(),c.Name="日常工作模块3",a.ParentNo=e.No,yield c.Insert(),new y(S.OpenUrlByDrawer75,o)}if(t==="Section"){const e=new u;e.Name=s,e.SortNo=T,e.TaskModel=t,e.BillNoFormat="{yyyy}-{MM}-{LSH4}",e.SetPara("EnName","TS.TA.Template.Section"),yield e.Insert();const o=new i;o.TemplateNo=e.No,o.Name="培训",yield o.Insert();const a=new i;a.Name="结束支持",a.TemplateNo=e.No,yield a.Insert();const n=new i;n.TemplateNo=e.No,n.No=G.GenerGUID(),n.Name="商务验收",yield n.Insert();const N=B.UrlEn("TS.TA.Template.Section",e.No);return new y(S.OpenUrlByDrawer75,N)}if(t==="TaskTree"){const e=new u;e.Name=s,e.SortNo=T,e.TaskModel=t,e.BillNoFormat="{yyyy}-{MM}-{LSH4}",e.SetPara("EnName","TS.TA.Template.TaskTree"),yield e.Insert();const o=B.UrlEn("TS.TA.Template.TaskTree",e.No);return new y(S.OpenUrlByDrawer75,o)}alert("没有判断的类型,pageID:"+t)})}}export{R as GPN_TaskTemplate};
