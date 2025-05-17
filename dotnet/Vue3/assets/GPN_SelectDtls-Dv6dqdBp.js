var P=Object.defineProperty;var h=(o,t,e)=>t in o?P(o,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[t]=e;var c=(o,t,e)=>h(o,typeof t!="symbol"?t+"":t,e);var l=(o,t,e)=>new Promise((D,s)=>{var m=a=>{try{i(e.next(a))}catch(r){s(r)}},p=a=>{try{i(e.throw(a))}catch(r){s(r)}},i=a=>a.done?D(a.value):Promise.resolve(a.value).then(m,p);i((e=e.apply(o,t)).next())});import{b9 as w}from"./entry/index-M8VErHPE-1727507756861.js";import{PackageEn as u}from"./PackageEn-p_XPAtWx.js";import{CGDtl as S}from"./CGDtl-CYCqQKRo.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EntityOID-BVVq-i_P.js";class N extends w{constructor(){super("GPN_SelectDtls");c(this,"Daily",`
  #### 帮助
   - 有科目作为明细，记账人员都可以记账，有项目组人员范围。
   - 没有时限要求，每个人的工作按照时间段记账，可以按照科目汇总项目成本，比如研发类项目，日常持续的投入。
 
  `);c(this,"Section",`
  #### 帮助
   - 有先后顺序，不同工种协作完成，有一定的时限要求 
   - 比如开发类项目 技术支持类项目，有一定的明确的目标，类似于，project项目管理。 
   
  `);c(this,"Nondeterminacy",`
  #### 帮助 
  - 有目标，没有时限，多人协作，有项目里程碑，完成度. 
  - 比如，客户跟踪类型项目，项目组有明确的人员分工，但是没有顺序，没有时限。不同的角色协助完成一个客户的跟踪，有完成度，概率，有阶段。
  `);c(this,"Task",`
  #### 帮助
   - 也称为简单项目或者不确定性项目，没有模式的临时性任务，
    - 比如 申报知识产权，找几个人协作完成，可以有固定时限，每个子任务分给不同的人，处理完毕后需要汇报，认可，确认，可以树形结构的分发收回，子任务数不确定。
  `);this.PageTitle="选择子项",this.ForEntityClassID="TS.CG.CGDtl"}Init(){return l(this,null,function*(){this.AddGroup("A","选择子项");const e=new u(this.RefPKVal);yield e.RetrieveFromDBSources();const s=`SELECT OID as No, ShangPinMingCheng as Name, ZhengFuCaiGouMuLuT ,DanWei,   DanJia FROM ND701CaiGouQingDan WHERE RefPK='${e.RefPK}' `;this.Table("Dtls","选择子项",this.HelpTodo,!0,s)})}GenerSorts(){return l(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,D,s,m,p){return l(this,null,function*(){if(e==="Dtls"){const i=new u(this.RefPKVal);yield i.RetrieveFromDBSources(),s.split(",").forEach(r=>l(this,null,function*(){const n=new S(r);yield n.Init(),yield n.RetrieveFromDBSources(),n.PackgeOID=this.RefPKVal,n.PackgeOIDT=i.BaoHao,yield n.Update()}))}})}}export{N as GPN_SelectDtls};
