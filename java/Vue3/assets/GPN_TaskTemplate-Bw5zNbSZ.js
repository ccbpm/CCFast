var k=Object.defineProperty;var f=(m,r,t)=>r in m?k(m,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):m[r]=t;var l=(m,r,t)=>f(m,typeof r!="symbol"?r+"":r,t);var c=(m,r,t)=>new Promise((T,s)=>{var B=a=>{try{e(t.next(a))}catch(o){s(o)}},G=a=>{try{e(t.throw(a))}catch(o){s(o)}},e=a=>a.done?T(a.value):Promise.resolve(a.value).then(B,G);e((t=t.apply(m,r)).next())});import{b5 as h,G as y,l as S}from"./entry/index-C6uBgOW5-1730430676707.js";import{Sorts as A}from"./Sort-CGZ6d0xm.js";import{Template as u}from"./Template-CRuw6ZOu.js";import{GloComm as P}from"./GloComm-CmAl8MpM.js";import{DailyItem as i}from"./DailyItem-C0yDUYZH.js";import{D as x}from"./DBAccess-sLO0RM-h.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./TemplateNode-D7I_95eR.js";import"./FrmTrack-BAfWiAdt.js";class q extends h{constructor(){super("GPN_TaskTemplate");l(this,"Daily",`
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
  `);this.PageTitle="新建项目模板"}Init(){return c(this,null,function*(){this.AddGroup("A","项目模板类型"),this.TextBox1_Name("Daily","日常项目",this.Daily,"模板名称","产品研发"),this.TextBox1_Name("Section","阶段性固定模式",this.Section,"模板名称","技术支持"),this.TextBox1_Name("Nondeterminacy","不确定性(project)项目",this.Nondeterminacy,"模板名称","外包项目实施"),this.TextBox1_Name("TaskTree","任务树类型",this.Task,"模板名称","收文任务")})}GenerSorts(){return c(this,null,function*(){const t=new A;return yield t.RetrieveAll(),t})}Save_TextBox_X(t,T,s,B,G){return c(this,null,function*(){if(t==="Daily"){const e=new u;e.Name=s,e.SortNo=T,e.TaskModel=t,e.BillNoFormat="{yyyy}-{MM}-{LSH4}",e.SetPara("EnName","TS.TA.Template.Daily"),yield e.Insert();const a=P.UrlEn("TS.TA.Template.Daily",e.No),o=new i;o.TemplateNo=e.No,o.Name="日常工作模块1",o.ParentNo=e.No,yield o.Insert();const n=new i;n.TemplateNo=e.No,n.Name="字模块1",n.ParentNo=o.No,yield n.Insert();const N=new i;N.TemplateNo=e.No,N.Name="字模块2",N.ParentNo=o.No,yield N.Insert();const w=new i;w.Name="日常工作模块2",w.TemplateNo=e.No,o.ParentNo=e.No,yield w.Insert();const p=new i;return p.TemplateNo=e.No,p.No=x.GenerGUID(),p.Name="日常工作模块3",o.ParentNo=e.No,yield p.Insert(),new y(S.OpenUrlByDrawer75,a)}if(t==="Section"){const e=new u;e.Name=s,e.SortNo=T,e.TaskModel=t,e.BillNoFormat="{yyyy}-{MM}-{LSH4}",e.SetPara("EnName","TS.TA.Template.Section"),yield e.Insert();const a=new i;a.TemplateNo=e.No,a.Name="培训",yield a.Insert();const o=new i;o.Name="结束支持",o.TemplateNo=e.No,yield o.Insert();const n=new i;n.TemplateNo=e.No,n.No=x.GenerGUID(),n.Name="商务验收",yield n.Insert();const N=P.UrlEn("TS.TA.Template.Section",e.No);return new y(S.OpenUrlByDrawer75,N)}if(t==="TaskTree"){const e=new u;e.Name=s,e.SortNo=T,e.TaskModel=t,e.BillNoFormat="{yyyy}-{MM}-{LSH4}",e.SetPara("EnName","TS.TA.Template.TaskTree"),yield e.Insert();const a=P.UrlEn("TS.TA.Template.TaskTree",e.No);return new y(S.OpenUrlByDrawer75,a)}alert("没有判断的类型,pageID:"+t)})}}export{q as GPN_TaskTemplate};
