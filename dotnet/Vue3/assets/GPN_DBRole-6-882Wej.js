var C=Object.defineProperty;var p=(s,o,e)=>o in s?C(s,o,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[o]=e;var B=(s,o,e)=>p(s,typeof o!="symbol"?o+"":o,e);var n=(s,o,e)=>new Promise((m,a)=>{var D=r=>{try{l(e.next(r))}catch(t){a(t)}},R=r=>{try{l(e.throw(r))}catch(t){a(t)}},l=r=>r.done?m(r.value):Promise.resolve(r.value).then(D,R);l((e=e.apply(s,o)).next())});import{DBRole as P}from"./DBRole-BthbZKHy.js";import{b9 as u,aB as S,aC as c}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as y}from"./GloComm-DZ1gELjv.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class b extends u{constructor(){super("GPN_DBRole");B(this,"DeptLeader",`
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
`);this.ForEntityClassID="TS.CCBill.DBRole",this.PageTitle="列权限权限规则"}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Init(){return n(this,null,function*(){this.RequestVal("DBRole"),this.AddGroup("A","数据权限规则"),this.AddBlank("Cols","选择列",this.None),this.AddBlank("Cols.Stations","选择角色",this.None)})}Save_TextBox_X(e,m,a,D,R){return n(this,null,function*(){const l=this.RequestVal("RefPKVal"),r=this.RequestVal("DBRole"),t=new P;if(t.MyPK=l+"_"+e+"_"+r,(yield t.RetrieveFromDBSources())==1){alert("该选项【"+t.MyPK+"】已经存在,请点击修改.");return}t.FrmID=l,t.MarkID=e,t.DBRole=r,t.MarkName=this.GetPageName(e),t.Docs="无";let i="TS.CCBill.DBRole";return e=="ByStations"&&(i="TS.CCBill.DBRoleStation"),e=="ByDepts"&&(i="TS.CCBill.DBRoleDept"),e=="ByEmps"&&(i="TS.CCBill.DBRoleEmp"),e=="ByExp"&&(i="TS.CCBill.DBRoleExp",t.Docs=a),t.SetPara("EnName",i),yield t.Insert(),new S(c.GoToUrl,y.UrlEn(i,t.MyPK))})}}export{b as GPN_DBRole};
