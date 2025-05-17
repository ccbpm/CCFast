var N=Object.defineProperty;var P=(o,s,t)=>s in o?N(o,s,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[s]=t;var i=(o,s,t)=>P(o,typeof s!="symbol"?s+"":s,t);var I=(o,s,t)=>new Promise((C,c)=>{var n=r=>{try{e(t.next(r))}catch(m){c(m)}},l=r=>{try{e(t.throw(r))}catch(m){c(m)}},e=r=>r.done?C(r.value):Promise.resolve(r.value).then(n,l);e((t=t.apply(o,s)).next())});import{b5 as G,aM as a,G as F,l as u}from"./entry/index-C6uBgOW5-1730430676707.js";import"./Events-D9tOL1Ad.js";import{MapData as p}from"./MapData-Ccsy8tbB.js";import{b as d}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./EnumLab-CzismWql.js";class R extends G{constructor(){super("GPN_DBList");i(this,"Docs0",`
  #### 帮助
  - 所有人都可以有权限。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Anyone.png "屏幕截图.png") 
`);i(this,"Docs1",`
  #### 帮助
  - 只有管理员有权限。
  #### 配置图
  ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Admin.png "屏幕截图.png") 
  `);i(this,"Docs2",`
  #### 帮助
  - 管理员和二级管理员有权限。
  #### 配置图
  ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/AdminerAndAmin2.png "屏幕截图.png") 
  `);i(this,"Docs3",`
  #### 帮助
  - 按选择的人员赋权。
  #### 配置图
  - ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Emp.png "屏幕截图.png") 
  `);i(this,"Docs4",`
  #### 帮助
  - 按选择的角色人员赋权。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Stations.png "屏幕截图.png") 
  `);i(this,"Docs5",`
  #### 帮助
  - 按选择的部门人员赋权。
  #### 配置图
   ![输入图片说明](/resource/CCFast/GPM/PCenter/Img/Dept.png "屏幕截图.png") 
  `);i(this,"Docs6",`
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
  `);this.PageTitle="数据源实体"}Init(){this.AddGroup("A","数据源实体"),this.TextBox3_NameNoNote("FrmID","按表单ID创建",this.HelpTodo,"DB_","请输入数据源ID","输入数据源名称","请输入表单ID","我的数据源"),this.SelectItemsByGroupList("FrmID.Table","主表字段",this.HelpTodo,!0,a.srcFrmGroups,a.srcFrmFields),this.SelectItemsByGroupList("FrmID.Table.SelectDtl","选择从表",this.HelpTodo,!0,a.srcFrmGroups,a.srcFrmFields),this.SelectItemsByGroupList("FrmID.Table.SelectDtl.Dtl","从表字段",this.HelpTodo,!0,a.srcFrmGroups,a.srcFrmFields),this.TextBox2_NameNo("View","按关系数据源创建",this.HelpTodo,"DBList","输入ID","输入名称","输入表单ID"),this.TextSQL("View.SQL","输入查询SQL",this.HelpTodo,"DBList","SELECT No as OID, No as BillNo,  Name as Title, Tel,Email  FROM Port_Emp","请输入SQL")}GenerSorts(){return I(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,C,c,n,l){return I(this,null,function*(){if(t=="View"){const e=new p;if(e.No=n,(yield e.IsExits())==!1)return d.info("ID:"+n+"已经存在,请重命名."),new F(u.CloseAndReload)}if(t=="FrmID"){const e=new p;if(e.No=n,(yield e.IsExits())==!1)return d.info("ID:"+n+"已经存在,请重命名."),new F(u.CloseAndReload);if(e.No=l,(yield e.IsExits())==!1)return d.info("输入表单ID"+l+"不存在."),new F(u.CloseAndReload)}if(t=="FrmID.TableField.Dtl"){const e=this.RequestVal("tb1","FrmID"),r=this.RequestVal("tb2","FrmID"),m=this.RequestVal("tb3","FrmID");this.RequestVal("tb1","FrmID.TableField"),this.RequestVal("tb1","FrmID.TableField.Dtl");const T=new p;T.No=m,yield T.RetrieveFromDBSources();const D=new p;D.No=e,D.Name=r,D.PTable=e,yield D.Insert()}})}}export{R as GPN_DBList};
