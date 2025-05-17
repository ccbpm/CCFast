var D=Object.defineProperty;var g=(o,r,s)=>r in o?D(o,r,{enumerable:!0,configurable:!0,writable:!0,value:s}):o[r]=s;var n=(o,r,s)=>g(o,typeof r!="symbol"?r+"":r,s);var P=(o,r,s)=>new Promise((S,a)=>{var m=e=>{try{p(s.next(e))}catch(c){a(c)}},C=e=>{try{p(s.throw(e))}catch(c){a(c)}},p=e=>e.done?S(e.value):Promise.resolve(e.value).then(m,C);p((s=s.apply(o,r)).next())});import{PCenter as h}from"./PCenter-g1qHGjKQ.js";import{b5 as u,aM as i,W as d,G,l as A}from"./entry/index-C6uBgOW5-1730430676707.js";import{D as T}from"./DBAccess-sLO0RM-h.js";import{AuthType as t}from"./AuthType-CrkD6Sy7.js";import{b as l}from"./antd-Dd9L3uAF.js";import"./PowerCenter-xD33lI2C.js";import"./vue-BXIlYw1E.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class Q extends u{constructor(){super("GPN_PCenter");n(this,"Docs0",`
  #### 帮助
  - 所有人都可以有权限。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Anyone.png "屏幕截图.png") 

`);n(this,"Docs1",`
  #### 帮助
  - 只有管理员有权限。
  #### 配置图
  ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Admin.png "屏幕截图.png") 

  `);n(this,"Docs2",`
  #### 帮助
  - 管理员和二级管理员有权限。
  #### 配置图
  ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/AdminerAndAmin2.png "屏幕截图.png") 

  
  `);n(this,"Docs3",`
  #### 帮助
  - 按选择的人员赋权。
  #### 配置图
  - ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Emp.png "屏幕截图.png") 
  `);n(this,"Docs4",`
  #### 帮助
  - 按选择的角色人员赋权。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Stations.png "屏幕截图.png") 
  `);n(this,"Docs5",`
  #### 帮助
  - 按选择的部门人员赋权。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Dept.png "屏幕截图.png") 
  `);n(this,"Docs6",`
  #### 帮助
  - 自动抄送给要绑定的人员.1. 输入的SQL是一个查询语句，返回的一行的第一列数据。
  - 该数据大于0 ，就是真(可以拥有此权限)，否则就是假（不能操作此权限）。
  - SQL语句支持ccbpm的表达式，比如：SELECT count(*) FROM Port_Dept WHERE No='@WebUser.DeptNo'。
  #### 说明
  - @WebUser.No 当前登录的人员编号
  - @WebUser.DeptNo 当前登录的部门编号
  - @RDT 是当前日期， 比如：2020-01-01
  - @DateTime 是当前时间， 比如：2020-01-01 10:09
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Sql.png "屏幕截图.png") 
  `);this.ForEntityClassID="TS.GPM.PCenter",this.PageTitle="新建权限"}Init(){this.AddGroup("A","本组织权限"),this.AddBlank(t.Anyone,"所有人(本组织)",this.Docs0),this.AddBlank(t.Adminer,"管理员",this.Docs1),this.AddBlank(t.AdminerAndAdmin2,"管理员、二级管理员",this.Docs2),this.SelectItemsByTreeEns(t.Emps,"按人员计算",this.Docs3,!0,i.srcDeptLazily,"0",i.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByGroupList(t.Stations,"按角色计算",this.Docs4,!0,i.srcStationTypes,i.srcStations),this.SelectItemsByTree(t.Depts,"按部门计算",this.Docs5,!0,i.srcDepts,i.srcDeptRoot),this.TextBox1_Name(t.SQL,"按SQL计算",this.Docs6,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo' ","输入的SQL返回"),this.AddGroup("B","高级权限"),this.AddBlank(t.AnyOrgs,"所有组织",this.HelpUn),this.SelectItemsByList(t.SpecOrgs,"指定组织所有人员",this.Docs4,!0,"SELECT No,Name FROM Port_Org WHERE No!='@WebUser.OrgNo'"),this.SelectItemsByGroupList(t.SpecOrgStations,"指定组织的指定岗位",this.HelpTodo,!0,i.srcStationTypes,i.srcStations),this.SelectItemsByTree(t.SpecOrgDepts,"指定组织的指定指定部门",this.Docs5,!0,"SELECT No,Name,ParentNo FROM Port_Dept ","0")}GenerSorts(){return P(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(s,S,a,m,C){return P(this,null,function*(){const p=this.RefPKVal,e=new h;if(e.CtrlObj=this.RequestVal("CtrlObj"),e.CtrlPKVal=p,e.CtrlModel=s,e.CtrlModelT=this.GetPageName(s),e.IDs=a,e.IDsT=m,e.OrgNo=d.OrgNo,e.MyPK=T.GenerGUID(),(s===t.Anyone||s===t.Adminer||s===t.AdminerAndAdmin2||s===t.AnyOrgs||s===t.SpecOrgs)&&(e.IDs="无",e.IDNames="无",e.MyPK=e.CtrlPKVal+"_"+s,(yield e.IsExits())==!0)){l.info("已经存在这个模式");return}const c=new Map([[t.Emps,"TS.GPM.PCenterEmp"],[t.Depts,"TS.GPM.PCenterDept"],[t.Stations,"TS.GPM.PCenterStation"],[t.SQL,"TS.GPM.PCenterSQL"],[t.SpecOrgStations,"TS.GPM.SpecOrgStation"],[t.SpecOrgs,"TS.GPM.SpecOrg"]]);return e.SetPara("EnName",c.get(s)||"None"),yield e.Insert(),l.info("创建成功"),new G(A.CloseAndReload)})}}export{Q as GPN_PCenter};
