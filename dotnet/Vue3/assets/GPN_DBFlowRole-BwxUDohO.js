var m=Object.defineProperty;var h=(i,s,e)=>s in i?m(i,s,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[s]=e;var B=(i,s,e)=>h(i,typeof s!="symbol"?s+"":s,e);var n=(i,s,e)=>new Promise((d,a)=>{var D=o=>{try{l(e.next(o))}catch(t){a(t)}},p=o=>{try{l(e.throw(o))}catch(t){a(t)}},l=o=>o.done?d(o.value):Promise.resolve(o.value).then(D,p);l((e=e.apply(i,s)).next())});import{b9 as c,aB as y,aC as R}from"./entry/index-M8VErHPE-1727507756861.js";import{DBRole as u}from"./DBRole-BthbZKHy.js";import{GloComm as S}from"./GloComm-DZ1gELjv.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class E extends c{constructor(){super("GPN_DBFlowRole");B(this,"DeptLeader",`
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
`);this.ForEntityClassID="TS.CCBill.DBRole",this.PageTitle="数据权限规则"}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Init(){return n(this,null,function*(){this.AddGroup("A","数据权限规则"),this.AddBlank("SelfOnly","只能查看自己创建的流程",this.None),this.AddIcon("SelfOnly","icon-user"),this.AddBlank("DeptOnly","本部门的人员可以查看本部门的流程",this.None),this.AddIcon("ByEmps","icon-user-follow"),this.AddBlank("DeptLeader","部门负责人可以查看本部门的里流程",this.DeptLeader),this.AddIcon("DeptLeader","icon-user-following"),this.AddBlank("ByStations","指定岗位下的人员可以查看全部流程",this.None),this.AddIcon("ByStations","icon-people"),this.AddBlank("ByDepts","指定部门下的人员可以查看编辑数据全部流程",this.None),this.AddIcon("ByDepts","icon-people"),this.AddBlank("ByEmps","指定人员可以查看编辑数据全部流程",this.None)})}Save_TextBox_X(e,d,a,D,p){return n(this,null,function*(){const l=this.RequestVal("RefPKVal");let o=this.RequestVal("DBRole");o&&(o="DBList");const t=new u;if(t.MyPK=l+"_"+e+"_"+o,alert(t.MyPK),(yield t.RetrieveFromDBSources())==1){alert("该选项已经存在,请点击修改.");return}t.FrmID=l,t.MarkID=e,t.DBRole="DBList",t.MarkName=this.GetPageName(e),t.Docs="无";let r="TS.CCBill.DBRole";return e=="ByStations"&&(r="TS.CCBill.DBRoleStation"),e=="ByDepts"&&(r="TS.CCBill.DBRoleDept"),e=="ByEmps"&&(r="TS.CCBill.DBRoleEmp"),t.SetPara("EnName",r),yield t.Insert(),new y(R.GoToUrl,S.UrlEn(r,t.MyPK))})}}export{E as GPN_DBFlowRole};
