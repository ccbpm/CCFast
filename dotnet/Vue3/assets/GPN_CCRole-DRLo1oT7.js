var T=Object.defineProperty;var N=(i,r,e)=>r in i?T(i,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[r]=e;var n=(i,r,e)=>N(i,typeof r!="symbol"?r+"":r,e);var S=(i,r,e)=>new Promise((c,l)=>{var a=t=>{try{m(e.next(t))}catch(o){l(o)}},p=t=>{try{m(e.throw(t))}catch(o){l(o)}},m=t=>t.done?c(t.value):Promise.resolve(t.value).then(a,p);m((e=e.apply(i,r)).next())});import{b5 as R,aM as s,G as C,l as D}from"./entry/index-C6uBgOW5-1730430676707.js";import{CCRole as E}from"./CCRole-G1BkUVVW.js";import{GloComm as h}from"./GloComm-CmAl8MpM.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./FrmTrack-BAfWiAdt.js";class M extends R{constructor(){super("GPN_CCRole");n(this,"ByField",`
  #### 帮助
  - 按照节点表单的字段作为抄送人.
  - 通常是在节点表单上加一个字段,这个字段存储的是人员账号，多个人员使用逗号分开.
  #### 实例
  - 填写表单的字段ID ：ChaoSong
`);n(this,"Docs1",`
  #### 帮助
  - 自动抄送给要绑定的人员.
`);n(this,"Desc5",`
  #### 帮助
  - 绑定节点的接收人规则.
  - 请点击设置接受人规则.
`);n(this,"BySQL",`
  #### 帮助
  - 按SQL计算抄送人员.
  - 有一个规则
  #### DEMO
  - 抄送本部门的人员.
  - SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo';
  
`);this.ForEntityClassID="TS.AttrNode.CCRole",this.PageTitle="新建抄送规则"}Init(){this.AddGroup("A","请选择规则"),this.SelectItemsByTreeEns("1","按人员计算",this.Docs1,!0,s.srcDeptLazily,s.srcDeptRoot,s.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByGroupList("2","按角色计算",this.Docs1,!0,s.srcStationTypes,s.srcStations),this.SelectItemsByTree("3","按部门计算",this.Docs1,!0,s.srcDepts,s.srcDeptRoot),this.TextSQL("4","按SQL计算",this.BySQL,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo' ","输入的SQL返回人员集合具有No,Name两个列."),this.TextBox1_Name("0","按表单字段计算",this.ByField,"表单字段","","请输入节点表单的字段名."),this.AddBlank("5","按接受人规则计算",this.Desc5),this.SelectItemsByTree("6","抄送给指定部门负责人",this.Docs1,!0,s.srcDepts,s.srcDeptRoot)}GenerSorts(){return S(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,c,l,a,p){return S(this,null,function*(){const m=this.RefPKVal,t=new E;t.NodeID=m,t.CCRoleExcType=e,t.EnIDs=l,t.Tag2=a,t.EnIDsT=a,t.FlowNo=this.RequestVal("FlowNo");let o="";if(e==="0"&&(o="TS.AttrNode.CCRoleByField"),e==="1"&&(o="TS.AttrNode.CCRoleByEmp"),e==="2"&&(o="TS.AttrNode.CCRoleByStation"),e==="3"&&(o="TS.AttrNode.CCRoleByDept"),e==="6"&&(o="TS.AttrNode.CCRoleByDept"),e==="4"&&(o="TS.AttrNode.CCRoleBySQL",t.DBSrc=l,t.Tag1=a),e==="5"){if(o="TS.AttrNode.CCRoleByDeliveryWay",t.MyPK=this.RefPKVal,t.Tag1="设置接受人规则.",(yield t.IsExits())==!0)return new C(D.Message,"err@该规则已经存在,只允许有一个规则.");t.SetPara("EnName",o),yield t.Insert();return}t.SetPara("EnName",o),yield t.Insert();const y=h.UrlEn(o,t.MyPK);return new C(D.GoToUrl,y)})}}export{M as GPN_CCRole};
