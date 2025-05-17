var h=Object.defineProperty;var S=(s,r,e)=>r in s?h(s,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[r]=e;var a=(s,r,e)=>S(s,typeof r!="symbol"?r+"":r,e);var p=(s,r,e)=>new Promise((l,m)=>{var N=i=>{try{o(e.next(i))}catch(t){m(t)}},u=i=>{try{o(e.throw(i))}catch(t){m(t)}},o=i=>i.done?l(i.value):Promise.resolve(i.value).then(N,u);o((e=e.apply(s,r)).next())});import{b5 as f,G as T,l as d,D as R,W as n,a2 as B}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as E}from"./GloComm-CmAl8MpM.js";import{Student as D}from"./Student-slk6_qTQ.js";import G from"./Dev2InterfaceCCBill-BgcYUWBE.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./Resume-R4UtACBN.js";import"./ShengFen-DoUitV-T.js";import"./City-B1mtzh8K.js";import"./BanJi-CaxC2D97.js";import"./Honor-DbGVh5Xd.js";import"./StudentKeMu-CZgXUjQz.js";import"./KeMu-P6hOPLTr.js";import"./GL_Todolist-DUB0aUFN.js";import"./PageBaseGenerList-CFbIdYv0.js";import"./WebConfig-BG6o2XHQ.js";import"./Flow-D4nUES4A.js";import"./index-Ixz86ukz.js";import"./Dev2Interface-BiOSL5Ij.js";import"./Member-D0Tpi7D7.js";class tt extends f{constructor(){super("GPN_Student");a(this,"Daily",`
  #### 帮助
   - 有科目作为明细，记账人员都可以记账，有项目组人员范围。
   - 没有时限要求，每个人的工作按照时间段记账，可以按照科目汇总项目成本，比如研发类项目，日常持续的投入。
 
  `);a(this,"Section",`
  #### 帮助
   - 有先后顺序，不同工种协作完成，有一定的时限要求 
   - 比如开发类项目 技术支持类项目，有一定的明确的目标，类似于，project项目管理。 
   
  `);a(this,"Nondeterminacy",`
  #### 帮助 
  - 有目标，没有时限，多人协作，有项目里程碑，完成度. 
  - 比如，客户跟踪类型项目，项目组有明确的人员分工，但是没有顺序，没有时限。不同的角色协助完成一个客户的跟踪，有完成度，概率，有阶段。
  `);a(this,"Task",`
  #### 帮助
   - 也称为简单项目或者不确定性项目，没有模式的临时性任务，
    - 比如 申报知识产权，找几个人协作完成，可以有固定时限，每个子任务分给不同的人，处理完毕后需要汇报，认可，确认，可以树形结构的分发收回，子任务数不确定。
  `);this.PageTitle="新建学生",this.ForEntityClassID="TS.Demo.Student"}Init(){return p(this,null,function*(){this.AddGroup("A","选择方式"),this.TextBox2_NameNo("NoName","输入账号",this.Daily,"","学生编号","学生名称",""),this.SelectItemsByList("NoName.BanJi","选择班级",this.HelpTodo,!1,"SELECT * FROM Demo_BanJi "),this.SelectItemsByList("EmpList","单选-账户列表",this.HelpTodo,!1,"SELECT * FROM Port_Emp "),this.Table("EmpListTable","Table-账户列表",this.HelpTodo,!1,"SELECT * FROM Port_Emp "),this.AddGoToUrl("GoTo","转到新建页面",E.UrlEn("TS.Demo.Student","")),this.AddGroup("B","批量导入"),this.FileUpload("ImpExcel","导入Excel","请上传符合格式的Excel文件.",this.HelpTodo)})}GenerSorts(){return p(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,l,m,N,u){return p(this,null,function*(){if(e==="NoName"){const o=N;if((yield new D(o).IsExits())==!0)return new T(d.Error,"学生人员编号["+o+"],已经存在.")}if(e==="NoName.BanJi"){const o=this.RequestVal("tb2","NoName"),i=this.RequestVal("tb1","NoName"),t=new D(o);return t.BanJiNo=m,t.BanJiNoT=N,t.Name=i,t.No=o,t.RDT=R.CurrentDateTime,t.RecNo=n.No,t.RecName=n.Name,t.RecDeptNo=n.DeptNo,t.RecDeptName=n.DeptName,t.OrgNo=n.OrgNo,yield t.DirectInsert(),yield G.WriteTrack("Demo_Student",t.No,"创建记录."),new T(d.GoToUrl,E.UrlEn("TS.Demo.Student",o))}if(e==="Imp"){const o=this.RequestVal("DeptNo");m.split(",").forEach(t=>p(this,null,function*(){const c=new B(o+"_"+t);(yield c.RetrieveFromDBSources())==0&&(c.FK_Dept=o,c.FK_Emp=t,yield c.Insert())}))}})}}export{tt as GPN_Student};
