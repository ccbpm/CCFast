var D=Object.defineProperty;var h=(s,i,t)=>i in s?D(s,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[i]=t;var B=(s,i,t)=>h(s,typeof i!="symbol"?i+"":i,t);var n=(s,i,t)=>new Promise((d,a)=>{var p=o=>{try{r(t.next(o))}catch(e){a(e)}},m=o=>{try{r(t.throw(o))}catch(e){a(e)}},r=o=>o.done?d(o.value):Promise.resolve(o.value).then(p,m);r((t=t.apply(s,i)).next())});import{b5 as c,G as y,l as R}from"./entry/index-C6uBgOW5-1730430676707.js";import{DBRole as u}from"./DBRole-BA4YAefA.js";import{GloComm as S}from"./GloComm-CmAl8MpM.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class F extends c{constructor(){super("GPN_DBFlowRole");B(this,"DeptLeader",`
  #### 帮助
  - 部门负责人的数据存储在:  Port_Dept.Leader字段
`);B(this,"None",`
  #### 帮助
  - 控制的内容为,列表查看权限控制.
  - 数据权限的控制方式有菜单栏里几种.
  #### 控制解析规则.
  1. 首先判断当前登录人员是否有查看全局的权限?
  1. 其次判断按部门权限.
  1. 最后判断人员权限.
  #### 可以查看全部数据的权限规则
  1. 控制权限数据为 0 条记录.
  1. 按照岗位，部门判断有任何一个条件成立.
`);this.ForEntityClassID="TS.CCBill.DBRole",this.PageTitle="数据权限规则"}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Init(){return n(this,null,function*(){this.AddGroup("A","数据权限规则"),this.AddBlank("SelfOnly","只能查看自己创建的流程",this.None),this.AddIcon("SelfOnly","icon-user"),this.AddBlank("DeptOnly","本部门的人员可以查看本部门的流程",this.None),this.AddIcon("ByEmps","icon-user-follow"),this.AddBlank("DeptLeader","部门负责人可以查看本部门的里流程",this.DeptLeader),this.AddIcon("DeptLeader","icon-user-following"),this.AddBlank("ByStations","指定岗位下的人员可以查看全部流程",this.None),this.AddIcon("ByStations","icon-people"),this.AddBlank("ByDepts","指定部门下的人员可以查看编辑数据全部流程",this.None),this.AddIcon("ByDepts","icon-people"),this.AddBlank("ByEmps","指定人员可以查看编辑数据全部流程",this.None)})}Save_TextBox_X(t,d,a,p,m){return n(this,null,function*(){const r=this.RequestVal("RefPKVal");let o=this.RequestVal("DBRole");o&&(o="DBList");const e=new u;if(e.MyPK=r+"_"+t+"_"+o,alert(e.MyPK),(yield e.RetrieveFromDBSources())==1){alert("该选项已经存在,请点击修改.");return}e.FrmID=r,e.MarkID=t,e.DBRole="DBList",e.MarkName=this.GetPageName(t),e.Docs="无";let l="TS.CCBill.DBRole";return t=="ByStations"&&(l="TS.CCBill.DBRoleStation"),t=="ByDepts"&&(l="TS.CCBill.DBRoleDept"),t=="ByEmps"&&(l="TS.CCBill.DBRoleEmp"),e.SetPara("EnName",l),yield e.Insert(),new y(R.GoToUrl,S.UrlEn(l,e.MyPK))})}}export{F as GPN_DBFlowRole};
