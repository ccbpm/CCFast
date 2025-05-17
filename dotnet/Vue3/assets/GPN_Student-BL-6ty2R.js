var D=Object.defineProperty;var S=(r,i,o)=>i in r?D(r,i,{enumerable:!0,configurable:!0,writable:!0,value:o}):r[i]=o;var m=(r,i,o)=>S(r,typeof i!="symbol"?i+"":i,o);var n=(r,i,o)=>new Promise((T,a)=>{var p=s=>{try{e(o.next(s))}catch(t){a(t)}},E=s=>{try{e(o.throw(s))}catch(t){a(t)}},e=s=>s.done?T(s.value):Promise.resolve(s.value).then(p,E);e((o=o.apply(r,i)).next())});import{b9 as R,aB as l,aC as u,aD as f,W as c,X as B}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as d}from"./GloComm-DZ1gELjv.js";import{Student as h}from"./Student-DyHrVzcd.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Resume-DRLpkBhu.js";import"./ShengFen-CSxO0wNv.js";import"./City-C-Lw18Sk.js";import"./BanJi-Bv9AyOh4.js";import"./Honor-BkjkQ7-q.js";import"./StudentKeMu-De5Hy_ae.js";import"./KeMu-B0GfuhP6.js";import"./GL_Todolist-CT_RkJ5g.js";import"./PageBaseGenerList-Be8VFIt5.js";import"./WebConfig-BG6o2XHQ.js";import"./Flow-D1QXg1UO.js";class W extends R{constructor(){super("GPN_Student");m(this,"Daily",`
  #### 帮助
   - 有科目作为明细，记账人员都可以记账，有项目组人员范围。
   - 没有时限要求，每个人的工作按照时间段记账，可以按照科目汇总项目成本，比如研发类项目，日常持续的投入。
 
  `);m(this,"Section",`
  #### 帮助
   - 有先后顺序，不同工种协作完成，有一定的时限要求 
   - 比如开发类项目 技术支持类项目，有一定的明确的目标，类似于，project项目管理。 
   
  `);m(this,"Nondeterminacy",`
  #### 帮助 
  - 有目标，没有时限，多人协作，有项目里程碑，完成度. 
  - 比如，客户跟踪类型项目，项目组有明确的人员分工，但是没有顺序，没有时限。不同的角色协助完成一个客户的跟踪，有完成度，概率，有阶段。
  `);m(this,"Task",`
  #### 帮助
   - 也称为简单项目或者不确定性项目，没有模式的临时性任务，
    - 比如 申报知识产权，找几个人协作完成，可以有固定时限，每个子任务分给不同的人，处理完毕后需要汇报，认可，确认，可以树形结构的分发收回，子任务数不确定。
  `);this.PageTitle="新建学生",this.ForEntityClassID="TS.Demo.Student"}Init(){return n(this,null,function*(){this.AddGroup("A","选择方式"),this.TextBox2_NameNo("NoName","输入账号",this.Daily,"","学生编号","学生名称",""),this.SelectItemsByList("NoName.BanJi","选择班级",this.HelpTodo,!1,"SELECT * FROM Demo_BanJi "),this.SelectItemsByList("EmpList","单选-账户列表",this.HelpTodo,!1,"SELECT * FROM Port_Emp "),this.Table("EmpListTable","Table-账户列表",this.HelpTodo,!1,"SELECT * FROM Port_Emp "),this.AddGoToUrl("GoTo","转到新建页面",d.UrlEn("TS.Demo.Student","")),this.AddGroup("B","批量导入"),this.FileUpload("ImpExcel","导入Excel","请上传符合格式的Excel文件.",this.HelpTodo)})}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(o,T,a,p,E){return n(this,null,function*(){if(o==="NoName"){const e=p;if((yield new h(e).IsExits())==!0)return new l(u.Error,"学生人员编号["+e+"],已经存在.")}if(o==="NoName.BanJi"){const e=this.RequestVal("tb2","NoName"),s=this.RequestVal("tb1","NoName"),t=new h(e);return(yield t.IsExits())==!0?new l(u.Error,"学生人员编号["+e+"],已经存在."):(t.BanJiNo=a,t.BanJiNoT=p,t.Name=s,t.No=e,t.RDT=f.CurrentDateTime,t.RecNo=c.No,t.RecName=c.Name,t.RecDeptNo=c.DeptNo,t.RecDeptName=c.DeptNoText,yield t.DirectInsert(),new l(u.GoToUrl,d.UrlEn("TS.Demo.Student",e)))}if(o==="Imp"){const e=this.RequestVal("DeptNo");a.split(",").forEach(t=>n(this,null,function*(){const N=new B(e+"_"+t);(yield N.RetrieveFromDBSources())==0&&(N.FK_Dept=e,N.FK_Emp=t,yield N.Insert())}))}})}}export{W as GPN_Student};
