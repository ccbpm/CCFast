var x=Object.defineProperty;var S=(n,i,r)=>i in n?x(n,i,{enumerable:!0,configurable:!0,writable:!0,value:r}):n[i]=r;var d=(n,i,r)=>S(n,typeof i!="symbol"?i+"":i,r);var h=(n,i,r)=>new Promise((N,_)=>{var B=t=>{try{m(r.next(t))}catch(l){_(l)}},p=t=>{try{m(r.throw(t))}catch(l){_(l)}},m=t=>t.done?N(t.value):Promise.resolve(t.value).then(B,p);m((r=r.apply(n,i)).next())});import{FrmSorts as b}from"./FrmSort-BiT7edPu.js";import w from"./GloFrm-DsIDCWiO.js";import{GroupFields as g,GroupField as T}from"./GroupField-JFOnJiHV.js";import{b9 as W,aB as c,aC as D,H as P}from"./entry/index-M8VErHPE-1727507756861.js";import{windowOpen as f}from"./windowOpen-BB7Oz6Ju.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EntityOID-BVVq-i_P.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";class M extends W{constructor(){super("GPN_NewFrm");d(this,"FrmEntityNoName",`
 #### 帮助 
   - 实体是管理对象. 比如固定资产管理、合同管理、人力学生、项目等.
   - 实体的基础管理就是对它的增、删、改、查. 
   - 对实体的管理包括实体的流程管理、相关功能管理、方法管理三部分.
   - 实体不能绑定到流程节点上,单据与独立表单可以.
   - 通过对外提供url模式的api接口,绑定到菜单里, 实体与流程的关系请参考: http://doc.ccbpm.cn
   #### 数据库字段
   - 实体数据存储在数据表里,数据表的字段分为系统字段+业务字段.
   - 比如： 编号、名称、创建人、创建日期、创建人部门、创建人组织就是系统字段.  实体电话、邮件、地址就是业务字段
   - No,varchar,主键,实体编号(编号的生成规则可以自定义默认为001,0002)
   - Name,varchar,实体名称
   - EntityState,int,枚举类型 -1=删除,0初始化,1=草稿,2=编辑,3=归档.
   - RecNo,varchar记录人编号,
   - RecName,varchar,记录人名称
   - DeptNo,varchar,记录人部门编号,
   - OrgNo,varchar,记录人组织
   #### 示例-车辆管理.
   - 实体列表:
   ![实体](/resource/WF/Admin/FrmLogic/EntityType5.png "屏幕截图.png")  
   - 单个记录:
   ![实体](/resource/WF/Admin/FrmLogic/EntityType5_1.png "屏幕截图.png")  
  `);d(this,"FrmAskDesc",`
  #### 帮助
   - 定义: 单据具有流水性质的数据增删改查,比如:报销单、请假单、出差申请单.
   - 单据与流程: 单据可以被流程节点绑定,也可也从实体上发起.
   - 基本字段: 制单人Starter、制单日期RDT、单号BillNo、标题Title、状态BillSta.
   - 单据编号:可以自动定义,存储在BillNo字段中.
   - 单据标题: 可以自定义规则，类似于流程标题.
   - 单据状态: BillState 0=草稿,1=编辑中,2=退回,3=归档.
   - 单据主键: OID 是个自动生的字段,类似于WorkID.
   - 发起人: Starter, StarterName.
   - 冗余字段: PWorkID, PFrmID父表单.
   #### 操作界面.
   1. 创建一个单据数据存储到 Frm_GenerBill 一份.
   2. 待办:单据管理
   3. 我的单据: 我发起的单据,等待我审批的单据,已经创建的单据.
   4. 发起单据: 我能创建的单据列表.
   5. 单据草稿: 启动的草稿.
   6. 近期单据: 近期发起的单据.
  `);d(this,"FrmBillDesc",`
  #### 帮助
   - 定义: 单据具有流水性质的数据增删改查,比如:报销单、请假单、出差申请单.
   - 单据与流程: 单据可以被流程节点绑定,也可也从实体上发起.
   - 基本字段: 制单人Starter、制单日期RDT、单号BillNo、标题Title、状态BillSta.
   - 单据编号: 可以自动定义,存储在BillNo字段中.
   - 单据标题: 可以自定义规则，类似于流程标题.
   - 单据状态: BillState 0=草稿,1=编辑中,2=退回,3=归档.
   - 单据主键: OID 是个自动生的字段,类似于WorkID.
   - 发起人: Starter, StarterName.
   - 冗余字段: PWorkID, PFrmID父表单.
   #### 操作界面.
   1. 创建一个单据数据存储到 Frm_GenerBill 一份.
   2. 待办:单据管理
   3. 我的单据: 我发起的单据,等待我审批的单据,已经创建的单据.
   4. 发起单据: 我能创建的单据列表.
   5. 单据草稿: 启动的草稿.
   6. 近期单据: 近期发起的单据.
  `);d(this,"FoolFrm",`
  #### 帮助 
   - 该表单是固定格式的表单,可以展现4列6列展现.
   - 优点:开发效率高,展现简洁,学习成本低,业务人员可以入手.
   - 缺点:展示样式固定.
  `);d(this,"DevFrm",`
  #### 帮助
   - 依托富文本编辑器,实现对表单的编辑.
   - 优点:格式灵活,展现效果随心所欲.
   - 缺点:业务人员入手需要一定的学习成本.
   - 适用于:效果
    
  `);d(this,"ChartFrm",`
  
  #### 帮助
   - 依托于经典表单设计器进行设计.
   - 一个分组就是章.
   - 每个字段都是大块文本,就是节.
    
  `);this.PageTitle="新建表单"}Init(){return h(this,null,function*(){this.AddGroup("D","实体单据"),this.TextBox2_NameNo("FrmEntityNoName","新建实体",this.FrmEntityNoName,"Entity_","实体ID","实体名称","资产台账"),this.AddIcon("icon-layers","FrmEntityNoName"),this.TextBox2_NameNo("FrmBill","新建单据",this.FrmBillDesc,"Bill_","单据ID","单据名称","维修单"),this.AddIcon("icon-notebook","FrmBill"),this.TextBox2_NameNo("FrmAsk","新建调查问卷",this.FrmBillDesc,"Ask_","问卷ID","问卷名称","饮食调查"),this.AddIcon("icon-emotsmile","FrmAsk"),this.AddGroup("A","流程表单"),this.TextBox3_NameNoNote("0","经典表单",this.FoolFrm,"Frm_","表单ID","表单名称","存储表",""),this.AddIcon("icon-notebook","0"),this.TextBox3_NameNoNote("8","开发者表单",this.DevFrm,"Frm_","表单ID","表单名称","存储表",""),this.AddIcon("icon-calendar","8"),this.TextBox3_NameNoNote("10","章节表单",this.ChartFrm,"Frm_","表单ID","表单名称","存储表",""),this.TextBox3_NameNoNote("6","Vsto模模式Excel表单",this.HelpUn,"Frm_","表单ID","表单名称","存储表",""),this.TextBox3_NameNoNote("11","AI大文本表单",this.HelpUn,"Frm_","表单ID","表单名称","存储表",""),this.AddIcon("icon-grid","6"),this.TextBox3_NameNoNote("9","Wps表单",this.HelpUn,"Frm_","表单ID","表单名称","存储表",""),this.TextBox2_NameNo("3","嵌入式表单",this.HelpUn,"Frm_","表单ID","表单名称","")})}GenerSorts(){return h(this,null,function*(){const r=new b;return yield r.Init(),yield r.RetrieveAll(),r})}Save_TextBox_X(r,N,_,B,p){return h(this,null,function*(){var y;const m=_,t=B,l=p;if(r=="FrmDict"||r=="FrmAsk"||r=="FrmEntityNoName"){const e=new P("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");e.AddPara("TB_No",t),e.AddPara("TB_Name",m),e.AddPara("TB_PTable",l),e.AddPara("DDL_DBSrc","local"),e.AddPara("FK_FrmSort",N),e.AddPara("EntityType",2),r=="FrmAsk"&&e.AddPara("EntityType",4),r=="FrmEntityNoName"&&e.AddPara("EntityType",5);const a=yield e.DoMethodReturnString("NewFrmGuide_Create");if((y=a==null?void 0:a.includes)!=null&&y.call(a,"err@"))return new c(D.Error,a);yield w.CheckForm(t);const o="/#/WF/Designer/Form?FrmID="+t;return new c(D.OpenUrlByNewWindow,o)}if(r=="FrmBill"){const e=new P("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");e.AddPara("TB_No",t),e.AddPara("TB_Name",m),e.AddPara("TB_PTable",l),e.AddPara("DDL_DBSrc","local"),e.AddPara("FK_FrmSort",N),e.AddPara("EntityType",1);const o=yield e.DoMethodReturnString("NewFrmGuide_Create");if(typeof o=="string"&&o.includes("err@")==!0)return new c(D.Error,o);yield w.CheckForm(t);const s="/#/WF/Designer/Form?FrmID="+t;return new c(D.OpenUrlByNewWindow,s)}const F=new P("BP.WF.HttpHandler.WF_Admin_CCFormDesigner");F.AddPara("TB_No",t),F.AddPara("TB_Name",m),F.AddPara("TB_PTable",l),F.AddPara("DDL_FrmType",r),F.AddPara("FK_FrmSort",N);const I=yield F.DoMethodReturnString("NewFrmGuide_Create");if(typeof I=="string"){f(I,m);return}if(r=="10"){const e=new g;if(yield e.Retrieve("FrmID",t),e.length==0){const a=new T;a.Lab="节点1",a.FrmID=t,a.CtrlType="Dir",a.Idx=0,yield a.Insert();const o=new T;o.Lab="节点2",o.FrmID=t,o.CtrlType="Dir",o.ParentOID=a.OID,o.Idx=0,yield o.Insert();const s=new T;s.Lab="节点3",s.FrmID=t,s.CtrlType="Dir",s.ParentOID=a.OID,s.Idx=1,yield s.Insert()}}const A=I;if(typeof A=="string"&&A.includes("err@")==!0)return new c(D.Error,A);yield w.CheckForm(t);const u="/#/WF/Designer/Form?FrmID="+t;return new c(D.OpenUrlByNewWindow,u)})}}export{M as GPN_NewFrm};
