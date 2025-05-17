var S=Object.defineProperty;var u=(n,r,t)=>r in n?S(n,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):n[r]=t;var o=(n,r,t)=>u(n,typeof r!="symbol"?r+"":r,t);var P=(n,r,t)=>new Promise((C,p)=>{var c=e=>{try{a(t.next(e))}catch(m){p(m)}},D=e=>{try{a(t.throw(e))}catch(m){p(m)}},a=e=>e.done?C(e.value):Promise.resolve(e.value).then(c,D);a((t=t.apply(n,r)).next())});import{PCenter as A}from"./PCenter-CGZJ3ajQ.js";import{b9 as G,aM as i,aB as d,aC as h}from"./entry/index-M8VErHPE-1727507756861.js";import{D as M}from"./DBAccess-CzjFzLoq.js";import{AuthType as s}from"./AuthType-BH7489sx.js";import{e as l}from"./antd-DkiF_jXA.js";import"./PowerCenter-B1QbxnUu.js";import"./vue-DGeTOT5N.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class _ extends G{constructor(){super("GPN_PCenter");o(this,"Docs0",`
  #### 帮助
  - 所有人都可以有权限。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Anyone.png "屏幕截图.png") 

`);o(this,"Docs1",`
  #### 帮助
  - 只有管理员有权限。
  #### 配置图
  ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Admin.png "屏幕截图.png") 

  `);o(this,"Docs2",`
  #### 帮助
  - 管理员和二级管理员有权限。
  #### 配置图
  ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/AdminerAndAmin2.png "屏幕截图.png") 

  
  `);o(this,"Docs3",`
  #### 帮助
  - 按选择的人员赋权。
  #### 配置图
  - ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Emp.png "屏幕截图.png") 
  `);o(this,"Docs4",`
  #### 帮助
  - 按选择的角色人员赋权。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Stations.png "屏幕截图.png") 
  `);o(this,"Docs5",`
  #### 帮助
  - 按选择的部门人员赋权。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Dept.png "屏幕截图.png") 
  `);o(this,"Docs6",`
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
  `);this.ForEntityClassID="TS.GPM.PCenter",this.PageTitle="新建权限"}Init(){this.AddGroup("A","新建权限"),this.AddBlank(s.Anyone,"所有人",this.Docs0),this.AddBlank(s.Adminer,"管理员",this.Docs1),this.AddBlank(s.AdminerAndAmin2,"管理员、二级管理员",this.Docs2),this.SelectItemsByTreeEns(s.Emps,"按人员计算",this.Docs3,!0,i.srcDeptLazily,"0",i.srcEmpLazily,"@No=账号@Name=名称@Tel=电话"),this.SelectItemsByGroupList(s.Stations,"按角色计算",this.Docs4,!0,i.srcStationTypes,i.srcStations),this.SelectItemsByTree(s.Depts,"按部门计算",this.Docs5,!0,i.srcDepts,i.srcDeptRoot),this.TextBox1_Name(s.SQL,"按SQL计算",this.Docs6,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo' ","输入的SQL返回")}GenerSorts(){return P(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,C,p,c,D){return P(this,null,function*(){const a=this.RefPKVal,e=new A;if(e.CtrlObj=this.RequestVal("CtrlObj"),e.CtrlPKVal=a,e.CtrlModel=t,e.CtrlModelT=this.GetPageName(t),e.IDs=p,e.IDsT=c,e.MyPK=M.GenerGUID(),(t===s.Anyone||t===s.Adminer||t===s.AdminerAndAmin2)&&(e.IDs="无",e.IDNames="无",e.MyPK=e.CtrlPKVal+"_"+t,(yield e.IsExits())==!0)){l.info("已经存在这个模式");return}const m=new Map([[s.Emps,"TS.GPM.PCenterEmp"],[s.Depts,"TS.GPM.PCenterDept"],[s.Stations,"TS.GPM.PCenterStation"],[s.SQL,"TS.GPM.PCenterSQL"]]);return e.SetPara("EnName",m.get(t)||"None"),yield e.Insert(),l.info("创建成功"),new d(h.CloseAndReload)})}}export{_ as GPN_PCenter};
