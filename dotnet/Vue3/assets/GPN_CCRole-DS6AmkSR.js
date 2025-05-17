var T=Object.defineProperty;var N=(i,o,t)=>o in i?T(i,o,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[o]=t;var m=(i,o,t)=>N(i,typeof o!="symbol"?o+"":o,t);var S=(i,o,t)=>new Promise((c,a)=>{var l=e=>{try{n(t.next(e))}catch(s){a(s)}},C=e=>{try{n(t.throw(e))}catch(s){a(s)}},n=e=>e.done?c(e.value):Promise.resolve(e.value).then(l,C);n((t=t.apply(i,o)).next())});import{b9 as R,aM as r,aB as D,aC as y}from"./entry/index-M8VErHPE-1727507756861.js";import{CCRole as E}from"./CCRole-BYc1Lkgq.js";import{GloComm as h}from"./GloComm-DZ1gELjv.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmTrack-0uAZQ3B_.js";class _ extends R{constructor(){super("GPN_CCRole");m(this,"ByField",`
  #### 帮助
  - 按照节点表单的字段作为抄送人.
  - 通常是在节点表单上加一个字段,这个字段存储的是人员账号，多个人员使用逗号分开.
  #### 实例
  - 填写表单的字段ID ：ChaoSong
`);m(this,"Docs1",`
  #### 帮助
  - 自动抄送给要绑定的人员.
`);m(this,"Desc5",`
  #### 帮助
  - 绑定节点的接收人规则.
  - 请点击设置接受人规则.
`);m(this,"BySQL",`
  #### 帮助
  - 按SQL计算抄送人员.
  - 有一个规则
  #### DEMO
  - 抄送本部门的人员.
  - SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo';
  
`);this.ForEntityClassID="TS.AttrNode.CCRole",this.PageTitle="新建抄送规则"}Init(){this.AddGroup("A","请选择规则"),this.SelectItemsByTreeEns("1","按人员计算",this.Docs1,!0,r.srcDeptLazily,r.srcDeptRoot,r.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByGroupList("2","按角色计算",this.Docs1,!0,r.srcStationTypes,r.srcStations),this.SelectItemsByTree("3","按部门计算",this.Docs1,!0,r.srcDepts,r.srcDeptRoot),this.TextSQL("4","按SQL计算",this.BySQL,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo' ","输入的SQL返回人员集合具有No,Name两个列."),this.TextBox1_Name("0","按表单字段计算",this.ByField,"表单字段","","请输入节点表单的字段名."),this.AddBlank("5","按接受人规则计算",this.Desc5),this.SelectItemsByTree("6","抄送给指定部门负责人",this.Docs1,!0,r.srcDepts,r.srcDeptRoot)}GenerSorts(){return S(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,c,a,l,C){return S(this,null,function*(){const n=this.RefPKVal,e=new E;e.NodeID=n,e.CCRoleExcType=t,e.EnIDs=a,e.Tag2=l,e.EnIDsT=l,e.FlowNo=this.RequestVal("FlowNo");let s="";if(t==="0"&&(s="TS.AttrNode.CCRoleByField"),t==="1"&&(s="TS.AttrNode.CCRoleByEmp"),t==="2"&&(s="TS.AttrNode.CCRoleByStation"),t==="3"&&(s="TS.AttrNode.CCRoleByDept"),t==="6"&&(s="TS.AttrNode.CCRoleByDept"),t==="4"&&(s="TS.AttrNode.CCRoleBySQL",e.DBSrc=a,e.Tag1=l),t==="5"){if(s="TS.AttrNode.CCRoleByDeliveryWay",e.MyPK=this.RefPKVal,e.Tag1="设置接受人规则.",(yield e.IsExits())==!0)return new D(y.Message,"err@该规则已经存在,只允许有一个规则.");e.SetPara("EnName",s),yield e.Insert();return}e.SetPara("EnName",s),yield e.Insert();const p=h.UrlEn(s,e.MyPK);return new D(y.GoToUrl,p)})}}export{_ as GPN_CCRole};
