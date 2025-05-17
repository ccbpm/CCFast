var h=Object.defineProperty;var W=(a,m,e)=>m in a?h(a,m,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[m]=e;var n=(a,m,e)=>W(a,typeof m!="symbol"?m+"":m,e);var d=(a,m,e)=>new Promise((l,s)=>{var w=r=>{try{o(e.next(r))}catch(t){s(t)}},F=r=>{try{o(e.throw(r))}catch(t){s(t)}},o=r=>r.done?l(r.value):Promise.resolve(r.value).then(w,F);o((e=e.apply(a,m)).next())});import{b9 as A,aM as p,ct as D,aB as x,aC as u,H as T}from"./entry/index-M8VErHPE-1727507756861.js";import{FlowDevModel as c}from"./EnumLab-CzismWql.js";import{useClassFactoryLoader as I}from"./useClassFactoryLoader-DnOCYN87.js";import{GloComm as S}from"./GloComm-DZ1gELjv.js";import{e as N}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class R extends A{constructor(){super("GPN_NewFlowOfSaaS");n(this,"TaskTree",` 
  #### 帮助
  - 场景：张三把任务分配给N个人, N 个人都需要向张三汇报(也可以不汇报), N个人中间的任何人，都可以在继续分配给M个人，同样M也可以继续分配给Y个人。我们把这样的模式的流程称为任务树流程。
  - 根据分配任务的特征，被分配的人需要向上级汇报，可以查看下级的任务内容。
  
  `);n(this,"Desc100","暂未开放");n(this,"FrmExcel",`
  #### 帮助
  - 借助Excel的功能实现的流程表单.
  - 使用vsto技术来实现, 对于客户安装的office版本于环境有一定的要求.
  - 需要借助插件.

  #### 应用场景
  1. 复杂的科学计算.
  1. 客户使用的表单已经是excel的模式成为了习惯.
  `);n(this,"SDKFrmWorkID",`
  ####  帮助
  - 每次新建一个单据之前, 要通过ccbpm的接口,根据模板编号生成一个WorkID作为该单据的主键.
  - 设计表单的主表的时候建议采用OID做为主键,可以是varchar(20), 用于存储表单的主键。
  - 从发起、待办、在途、已完成页面打开表单的时候.，系统传入 xxxx.htm?WorkID=xxx&FK_Node=xxxx 打开表单.
  - 我们把这样的模式称为 WorkID做主键的模式表单，因为该表单的主键生成是通过流程模板编号与当前用户信息生成的.

  ### 表单开发要求
  1. 接受WorkID=xxx传来的参数，用于提取表单数据,并渲染.
  2. 接受NodeID=xxx的参数，用于判断表单的元素只读、隐藏.
  2. 表单里必须有一个Save()的方法.
  1. 对于vue在表单的头部，尾部增加toolbar工具栏组件，WorkCheck审核组件.
  1. 对于html表单需要增加 toolbar,workcheck的标记，详细请参考demo.

  #### 如果您的业务系统与ccbpm是数据库合并的.
  1. 表单的主键设置 OID ，设置varchar(30) ,做主键.
  1. 打开流程属性让流程的业务表与该表单的表设置保持一致.
  1. 流程运转的时候，系统就会自动扩展该表的自动增加上，流程的系统自动字段.
  1. 比如: OID,WFState(状态),Title(流程标题),FlowEndNode(停留节点),Emps(参与人),FlowStarter(发起人),FlowStartRDT(发起日期) ....
  
  #### 如果两个数据库不是合并的.
  1. 可以通过OID,与流程引擎注册表 WF_GenerWorkFlow 关联查询获得流程运转信息.
  1. 可以通过事件把流程运转的信息写入到单据表里,比如:节点发送前事件,流程结束事件.

  #### 应用场景
  1. 应用于ccbpm的内置表单不能满足要求，需要自定义表单.
  1. 新建的单据.
  `);n(this,"SDKFrmSelfPK",`
  ####  帮助
  - 单据的增删改查工作已经通过自己的系统完成了, 每个单据都有自己的主键(一般称为单据编号)
  - 开发者期望使用ccbpm把单据列表的一行记录(一个单据)使用流程流转起来.
  - 比如: 有一个新闻列表，每条新闻右侧有个审批按钮,启动对该新闻的发布审批流程.
  - 我们把这样的模式称为自定义主键模式表单，因为该表单的主键生成是通过自己的架构生成的.
  #### 主键参数
  - 打开一个单据页面的主键参数, 比如: /XXX/BuyBill.vue?DJBH=xxxxx
  - DJBH就是主键参数.
  - 定义：打开单据表单的主键ID的参数名，就称为主键参数.
  #### 发起流程调用
  - 当发起流程的时候，开发者需要把主键参数传入到工作流处理器. /WF/MyFlow.vue?FK_Flow=001&DJBH=xxxx 
  - ccbpm就会转到您的自定义的url页面，比如: /XXX/BuyBill.vue?DJBH=xxxxx&WorkID=xxx&FK_Node=111&UserNo=xxx&Token=xxx
  - 打开您的单据.
  #### 待办调用
  - 不需要开发者处理，系统就会自动的把主键参数传递给您.

  ### 表单开发要求
  1. 接受NodeID=xxx的参数，用于判断表单的元素只读、隐藏.
  1. 表单里必须有一个Save()的方法.
  1. 对于vue在表单的头部，尾部增加toolbar工具栏组件，WorkCheck审核组件.
  1. 对于html表单需要增加 toolbar,workcheck的标记，详细请参考demo.

  #### 应用场景
  - 历史表单改造起来成本太高,可以保持数据结构的不变化,而使用上ccbpm.
  - 

  `);n(this,"TSEntity",`
       
  #### 帮助
   - 该模式是使用TSEntity编码的模式实现表单的控制.
   - 首先写一个子类从基类上集成下来.
   #### 流程
    

   `);n(this,"Profession",`       
  #### 帮助
   - 专业模式就是任何节点的表单方案都可单独定制和设计，它适应与更复杂的环境配置。
   - 该模式的表单定义是自由的，每个节点上都可以定义不同的表单方案。
   - 每个节点上都可以灵活定义个性化的表单，而不需要统一管理。
   #### 表单图例
   ![输入图片说明](/resource/WF/Admin/Img/ZhuanYeBiaoDan_1.png "表单设计")
   ![输入图片说明](/resource/WF/Admin/Img/ZhuanYeBiaoDan_2.png "表单设计")

   `);n(this,"JiJian",`
  #### 帮助
   - 视频教程: https://drive.weixin.qq.com/s?k=AOsAZQczAAYH6pM5dn
   - 极简模式是采用经典表单+审核组件实现流程审核的一种模式。
   - 开始节点填写申请，以后的节点都是审批，我们把这样的模式称为极简模式。
   - 在极简模式下，每个节点右键上有一个审核组件状态（启用，禁用，只读）。
  #### 图例
  ![输入图片说明](/resource/WF/Admin/Img/JiJian.jpg "极简模式.png")
        
  `);n(this,"Docs2",`
  #### 帮助
   - 累加表单模式是经典表单模式的一种，它是在每个节点上都设置自己的表单。
   - 流程在运动的过程中，把所有经过的表单都串联起来组成一个完整的表单。
   - 累加表单容易理解与设计，一般在开始节点上设置申请单的内容，在其他节点上设置审核分组表单就可以完成审核的需求场景。
  `);n(this,"Docs10",`
  #### 帮助
   - 支持编写js,Html模式，使用富文本编辑器开发。
  #### 图例
  ![输入图片说明](/resource/WF/Admin/Img/kaifaze.png "屏幕截图.png") 
`);n(this,"RefFrm",`
  #### 帮助
   - 视频教程: https://drive.weixin.qq.com/s?k=AOsAZQczAAYyuEwsm1
   - 表单库的表单称为独立表单，每个表单都可以与任何流程的任何节点绑定。
   - 一个表单类似于一个车厢，停留在仓库里，只有他在被绑定到节点上才可以使用。
   - 每个节点就类似于火车头，一个火车头可以挂多个车厢，也可以挂一个车厢。
   - 一个节点挂一个表单，我们称为单表单流程，多个表单称为表单树流程。
   - 表单的权限控制: 一个节点挂接一个表单有权限控制，控制整体表单只读、可见、可编辑，可控制每个表单元素的状态特征。
  #### 图例
  ![输入图片说明](/resource/WF/Admin/Img/Frms.jpg "屏幕截图.png")    
  `);n(this,"RefFrm_SelectOneFrm",`
  #### 帮助  
   - 表单库的表单称为独立表单，每个表单都可以与任何流程的任何节点绑定。
   - 一个表单类似于一个车厢，停留在仓库里，只有他在被绑定到节点上才可以使用。
   - 每个节点就类似于火车头，一个火车头可以挂多个车厢，也可以挂一个车厢。
   - 一个节点挂一个表单，我们称为单表单流程，多个表单称为表单树流程。
   - 表单的权限控制: 一个节点挂接一个表单有权限控制，控制整体表单只读、可见、可编辑，可控制每个表单元素的状态特征。
  #### 图例
  ![输入图片说明](/resource/WF/Admin/Img/Frms.jpg "屏幕截图.png")    
  `);n(this,"FrmTree_SelectFrms",`
  #### 帮助
   - 表单库的表单称为独立表单，每个表单都可以与任何流程的任何节点绑定。
   - 一个表单类似于一个车厢，停留在仓库里，只有他在被绑定到节点上才可以使用。
   - 每个节点就类似于火车头，一个火车头可以挂多个车厢，也可以挂一个车厢。
   - 一个节点挂一个表单，我们称为单表单流程，多个表单称为表单树流程。
   - 表单的权限控制: 一个节点挂接一个表单有权限控制，控制整体表单只读、可见、可编辑，可控制每个表单元素的状态特征。
  #### 图例 -1
  ![输入图片说明](/resource/WF/Admin/Img/Tree1.png "屏幕截图.png") 
  #### 图例 -2
  ![输入图片说明](/resource/WF/Admin/Img/Tree.png "屏幕截图.png") 

  `);n(this,"Docs5",`
  #### 帮助
   - SDK表单就是ccbpm把界面的展现完全交给了开发人员处理,开发人员只要设计一个表单,增加一个发送按钮,调用ccbpm的发送API就可以完成
   - 表单的渲染，都是由开发人员完成，对流程的操作调用不同的接口即可。
   - 这种模式适应于比较复杂的表单但是ccform又满足不了用户的要求的情况下使用sdk表单。
   - 关于该表单的调用接口可以参考：流程属性\\开发接口。
  ![输入图片说明](/resource/WF/Admin/Img/SDKFrm.png "屏幕截图.png")     
   `);n(this,"QianRuFrm",`
  #### 帮助
   - 您可以定义一个页面，绑定到该节点上. 
   - 该页面里面有一个Save() 的 function ，当用户点击框架外面的工具栏上的【保存】按钮或者【发送】按钮，就会触发这个函数。
   - 您需要在Save()的function里完成数据完整性效验与数据保存。
   - 如果保存成功就return true, 保存失败就return false. 比如:当用户执行发送的时候，首先执行保存，保存成功后在执行发送，保存失败后，就阻止发送。
   - 您输入的Url可以有参数，但是系统会把所有的参数附件到该url后面。
   - 例如:/SDKFlowDemo/QingJia/SDKQianRuFangShiForm.htm 。
   - 比如:您配置的url为 http://xxxx:222:/abc.htm 系统实际的Url为 http://xxxx:222:/abc.htm?FK_Flow=xxx&FK_Node=xxx&WorkID=xxx&UserNo=xxx&Token=xxx
   - 系统会把当前流程环境中的变量与参数都传递到您的自定义页面上来，您可以根据这些参数来展示，保存数据，控制数据只读，可编辑。
   - 如果使用绝对路径可以使用ccbpm的全局变量@SDKFromServHost ，比如: @SDKFromServHost/MyFile.htm

  #### 样例1
  ![输入图片说明](/resource/WF/Admin/Img/SelfFrm.png "屏幕截图.png")    

  #### 样例2
  ![输入图片说明](/resource/WF/Admin/Img/SDKFrm.png "屏幕截图.png")  
  `);this.PageTitle="新建流程"}Init(){return d(this,null,function*(){this.AddGroup("A","内置表单模式"),this.TextBox1_Name("JiJian","极简模式",this.JiJian,"流程名称","我的极简流程"),this.TextBox1_Name("Profession","专业模式",this.Profession,"流程名称","我的专业流程"),this.AddGroup("B","绑定表单模式"),this.TextBox1_Name("RefOneFrmTree","绑定单表单",this.RefFrm,"流程名称","我的单表单流程"),this.SelectItemsByGroupList("RefOneFrmTree.SelectOneFrm","选择表单",this.RefFrm_SelectOneFrm,!1,p.srcFrmTree,p.srcFrmList),this.TextBox1_Name("FrmTree","绑定多表单",this.FrmTree_SelectFrms,"流程名称","我的多表单流程"),this.SelectItemsByGroupList("FrmTree.SelectFrms","选择表单",this.FrmTree_SelectFrms,!0,p.srcFrmTree,p.srcFrmList),this.AddGroup("C","使用AI创建"),this.AddHelp("AI","使用AI创建","使用AI创建,调用AI大模型创建流程.");const e=S.UrlGPN("GPN_AIFlowNew","","&SortNo=123");this.AddGoToUrl("AI.AIFlow","使用AI创建",e)})}GenerEnsList(){return d(this,null,function*(){return yield(yield I("ClassFactory")).toJSON([])})}GenerSorts(){return d(this,null,function*(){const e=new D;return yield e.RetrieveAll(),e})}Save_TextBox_X(e,l,s,w,F){return d(this,null,function*(){if(e=="AI.AIFlow"){const o=S.UrlGPN("GPN_AIFlowNew","","&SortNo="+l);return new x(u.OpenUrlByNewWindow,o)}if(e==="Profession"||e==="JiJian"){const o=s,r=w;let t=c.Prefessional;e=="JiJian"&&(t=c.JiJian);const i=yield this.creteFlow(l,t,o,r,F);if(i==null)return;const f="/#/WF/Designer/EditFlow?FlowNo="+i;return new x(u.OpenUrlByNewWindow,f)}if(e=="SDKFrmWorkID"||e=="SDKFrmWorkID"||e=="QianRuFrm"){const o=w,r=s,t=yield this.creteFlow(l,c.SDKFrm,o,r,F);if(t==null)return;const i="/#/WF/Designer/EditFlow?FlowNo="+t;return new x(u.OpenUrlByNewWindow,i)}if(e==="RefOneFrmTree.SelectOneFrm"){const o=this.RequestVal("tb1","RefOneFrmTree"),r=s,t=yield this.creteFlow(l,c.RefOneFrmTree,o,r,F);if(t==null)return;const i="/#/WF/Designer/EditFlow?FlowNo="+t;return new x(u.OpenUrlByNewWindow,i)}if(e==="FrmTree.SelectFrms"){const o=this.RequestVal("tb1","FrmTree"),r=s,t=yield this.creteFlow(l,c.FrmTree,o,r,F);if(t==null)return;const i="/#/WF/Designer/EditFlow?FlowNo="+t;return new x(u.OpenUrlByNewWindow,i)}if(e==="FrmExcel"||e==="FrmWord"){const o=s,r=w;let t=c.FrmWord;e=="FrmExcel"&&(t=c.FrmExcel),e=="FrmWord"&&(t=c.FrmWord);const i=yield this.creteFlow(l,t,o,r,F);if(i==null)return;const f="/#/WF/Designer/EditFlow?FlowNo="+i;return new x(u.OpenUrlByNewWindow,f)}if(e==="TaskTreeSimple"){const o=s,r=w,t=c.TaskTree,i=yield this.creteFlow(l,t,o,r,F);if(i==null)return;N.info("流程创建成功,模板编号:"+i);const f="/#/WF/Designer/EditFlow?FlowNo="+i;return new x(u.OpenUrlByNewWindow,f)}})}creteFlow(e,l,s,w,F){return d(this,null,function*(){const o=new T("BP.WF.HttpHandler.WF_Admin_CCBPMDesigner_FlowDevModel");o.AddPara("SortNo",e),o.AddPara("FlowName",s),o.AddPara("FlowDevModel",l),o.AddPara("FrmUrl",w),o.AddPara("FrmPK",F);const r=yield o.DoMethodReturnString("FlowDevModel_Save_SaaS");return r==null||r==null?(N.info("创建失败:"+r),null):(N.info("流程创建成功,模板编号:"+r),r)})}}export{R as GPN_NewFlowOfSaaS};
