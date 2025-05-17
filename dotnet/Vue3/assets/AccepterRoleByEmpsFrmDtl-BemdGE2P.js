var ce=Object.defineProperty;var me=(d,e,r)=>e in d?ce(d,e,{enumerable:!0,configurable:!0,writable:!0,value:r}):d[e]=r;var t=(d,e,r)=>me(d,typeof e!="symbol"?e+"":e,r);var F=(d,e,r)=>new Promise((n,i)=>{var a=S=>{try{p(r.next(S))}catch(B){i(B)}},h=S=>{try{p(r.throw(S))}catch(B){i(B)}},p=S=>S.done?n(S.value):Promise.resolve(S.value).then(a,h);p((r=r.apply(d,e)).next())});import{aM as P,aP as C,U as A,L as D,Y as G,H as $,aB as N,aC as _,aD as ue,bk as Se,aQ as he}from"./entry/index-M8VErHPE-1727507756861.js";import{EntityNodeID as y,EntitiesNodeID as Fe}from"./EntityNodeID-De9k9loD.js";import{Node as g,NodeAttr as o}from"./Node-B6HRFhwD.js";import{BtnAttr as b,BtnLab as Ae}from"./BtnLab-DDtmliJP.js";import{FormSlnType as E}from"./EnumLab-CLN2gIih.js";import{FrmNode as q,FrmNodeAttr as k}from"./FrmNode-ICauEUJT.js";import{MapData as Y}from"./MapData-lfC2UY9r.js";import{PageBaseGroupEdit as R}from"./PageBaseGroupEdit-JIgqoTiq.js";import{GloComm as I}from"./GloComm-DZ1gELjv.js";import Q from"./HttpHandler-Ebi1068_.js";import{DeliveryWay as s}from"./DeliveryWay-CVCcKNk8.js";import{AccepterRoleBindStation as W}from"./AccepterRoleBindStation-DdRTXWKY.js";import{AccepterRoleBindEmp as K}from"./AccepterRoleBindEmp-CRh2yorG.js";import{AccepterRoleBindDeptStation as O}from"./AccepterRoleBindDeptStation-CyQkDE1t.js";import{AccepterRoleBindDept as M}from"./AccepterRoleBindDept-U-Revlbh.js";import{AccepterRoleBindSFTable as De}from"./AccepterRoleBindSFTable-FI4LbJ26.js";import{ARBindWebApi as ye}from"./ARBindWebApi-ubOfOuM4.js";import{GPE_ShenFenModel as Be}from"./GPE_ShenFenModel-ByWjF4SH.js";import{A as Ee,G as fe}from"./ARBindStationSpecDept-Bk9VZi_1.js";import{A as Ne,G as _e}from"./ARBindStationSpecSta-Q-Yj_XTA.js";import{ARStation as j}from"./ARStation-B0hXohoy.js";import{AR501 as J}from"./AR501-Brrbs0MN.js";import{AccepterRoleMLeader as Ie}from"./AccepterRoleMLeader-qHjJR2qD.js";import{ARWebAPI as we}from"./ARWebAPI-D8evxd8e.js";import{RouteAttr2Emp as ge}from"./RouteAttr2Emp-D2pGOYjX.js";import{CCRole as Te}from"./CCRole-BYc1Lkgq.js";import{e as Pe}from"./antd-DkiF_jXA.js";import{GPE_TurnTo as Re}from"./GPE_TurnTo-CYU8FR2B.js";import{GPE_TodolistModel as Me}from"./GPE_TodolistModel-DEAZhHvC.js";import{NodeToolbars as Le,NodeToolbarAttr as z}from"./NodeToolbar-CfeKGY2_.js";import{SubFlows as be,SubFlowAttr as We}from"./SubFlow-DqNaKCzf.js";import{GPE_OvertimeRole as Ke}from"./GPE_OvertimeRole-mTyCgJu8.js";import{FrmNodeExts as Oe}from"./FrmNodeExt-D_BvSa8V.js";import{FrmNodeBatchs as ve}from"./FrmNodeBatch-BEv_VoAb.js";import{GPE_FrmTransfer as Ue}from"./GPE_FrmTransfer-BHqi9DsM.js";import{SysEvents as Ce}from"./SysEvent-BDHAFjMW.js";import{PushMsgs as Ge,PushMsgAttr as V}from"./PushMsg-Bf2jY7mr.js";import{GPE_BlockModel as xe}from"./GPE_BlockModel-Cw3lBpWx.js";import{GPE_CCWriteRole as He}from"./GPE_CCWriteRole-BpSRGZtU.js";import{Conds as ke}from"./Cond-U5EvNC-p.js";class Z extends y{constructor(e){super("TS.AttrNode.Sln11"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","单表单(绑定独立表单)");e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.Name,null,"节点名称",!0,!0,0,50,200),e.AddTBString(m.FK_Flow,null,"FK_Flow",!1,!1,0,50,200),e.AddTBString(m.NodeFrmID,null,"表单ID",!0,!1,0,50,200),e.SetPopGroupList(m.NodeFrmID,P.srcFrmTree,P.srcBindFrmList,!1),e.AddGroupMethod("绑定单表单"),e.AddRM_UrlTabOpen("节点表单属性","/#/WF/Comm/En?EnName=TS.AttrNode.FrmNodeExt&PKVal=@NodeFrmID_@FrmID_@FK_Flow","icon-drop");const n=new G;return n.Title="设置所有节点都采用此方案",n.ClassMethod="DoSetIt",n.RefMethodType=C.Func,e.AddRefMethod(n),this._enMap=e,this._enMap}beforeUpdateInsertAction(){return F(this,null,function*(){try{const e=new q;e.MyPK=this.NodeFrmID+"_"+this.NodeID+"_"+this.FK_Flow;const r=yield e.RetrieveFromDBSources();if(e.FK_Node=this.NodeID,e.FK_Frm=this.NodeFrmID,e.FK_Flow=this.FK_Flow,e.SetPara("EnName","TS.AttrNode.FrmNodeExt"),r==0){const n=new Y(this.NodeFrmID);return yield n.RetrieveFromDBSources(),e.FrmNameShow=n.Name,yield e.Insert(),!0}return yield e.Update(),!0}catch(e){return alert(e),!1}})}DoFrmAttr(){return this.NodeFrmID==""||this.NodeFrmID==null?"err@错误,请先设置表单ID.":"url@/src/WF/Comm/En.vue?EnName=TS.AttrNode.FrmNodeExt&PKVal="+this.NodeFrmID+"_"+this.NodeFrmID+"_"+this.FK_Flow}DoSetIt(){return F(this,null,function*(){if(this.NodeFrmID==""||this.NodeFrmID==null)throw new Error("err@错误,请先设置表单ID.");const e=new $("BP.WF.HttpHandler.WF_Admin_AttrNode_FrmSln");return e.AddPara("FK_Node",this.NodeID),yield e.DoMethodReturnString("RefOneFrmTree_SetAllNodeFrmUseThisSln")})}}const bt=Object.freeze(Object.defineProperty({__proto__:null,Sln11:Z},Symbol.toStringTag,{value:"Module"}));class v extends y{constructor(e){super("TS.WF.SelfFormEn","BP.WF.Template.NodeExt"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","URL参数");return e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.Name,null,"名称",!1,!1,0,50,200),e.AddTBString(m.FormUrl,null,"文件URL(PC端)",!0,!1,0,50,200,!0),e.AddTBString(m.FormUrlMobile,null,"文件URL(移动端)",!0,!1,0,50,200,!0),e.AddDDLSysEnum("SelfFormEnRoot",0,"文件根路径",!0,!0,"SelfFormEnRoot","@0=本机文件@1=全路径@2=子系统设置@3=全局配置文件(.env)@4=系统参数FrmUrl"),e.ParaFields=",SelfFormEnRoot,",e.AddTBAtParas(500),this._enMap=e,this._enMap}}const Wt=Object.freeze(Object.defineProperty({__proto__:null,SelfFormEn:v},Symbol.toStringTag,{value:"Module"}));class X extends y{constructor(e){super("TS.WF.EntityTSForm","BP.WF.Template.NodeExt"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","EntityTS高代码");return e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.FK_Flow,null,"FK_Flow",!1,!1,0,50,200),e.AddTBString(m.Name,null,"名称",!0,!0,0,50,200),e.AddTBString(m.FormUrl,null,"高代码",!0,!1,0,50,200,!0),e.AddTBAtParas(500),this._enMap=e,this._enMap}beforeUpdateInsertAction(){return F(this,null,function*(){try{const e=new q;e.MyPK=this.FormUrl+"_"+this.NodeID+"_"+this.FK_Flow;const r=yield e.RetrieveFromDBSources();if(e.FK_Node=this.NodeID,e.FK_Frm=this.NodeFrmID,e.FK_Flow=this.FK_Flow,e.SetPara("EnName","TS.AttrNode.FrmNodeExt"),r==0){const n=new Y(this.NodeFrmID);return yield n.RetrieveFromDBSources(),e.FrmNameShow=n.Name,yield e.Insert(),!0}return yield e.Update(),!0}catch(e){return alert(e),!1}})}}const Kt=Object.freeze(Object.defineProperty({__proto__:null,EntityTSForm:X},Symbol.toStringTag,{value:"Module"}));class ee extends R{constructor(){super("GPE_FrmSln");t(this,"FoolForm",`

  #### 帮助
   - 视频教程: https://drive.weixin.qq.com/s?k=AOsAZQczAAY9NtLjEw
   - 设计方便，界面简洁清晰。
   - 字段的顺序可以通过拖拽实现移动,通过栅栏格来布局界面元素。
   - 可以通过定义文本属性来体现不同控件的展示要求（文本，单选，多选，定位，评分，多附件，地图，身份证识别等）满足表单要求。
 
  #### 图例1
  - ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/FoolFrmD.png "屏幕截图.png")
   
  #### 图例2
  - ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/FoolFrmD2.png "屏幕截图.png")
 
  #### 表单模式
  - ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/shagua1.png "屏幕截图.png")
   
  `);t(this,"Developer",`
  #### 帮助
   - 支持编写js,Html模式，使用富文本编辑器开发.
  #### 样式
  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/kaifaze.png "屏幕截图.png")
  `);t(this,"RefNodeFrm",`
  #### 帮助
   - 支持编写js,Html模式，使用富文本编辑器开发.
  #### 样式
  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/kaifaze.png "屏幕截图.png")
  `);t(this,"FoolTruck",`
  #### 帮助
   - 该表单是以经典表单展示，也叫累加表单，也叫轨迹表单。
   - 每个节点上都有自己的一个字段集合, 当前节点的表单是以前表单按照走过的顺序累加（默认字段只读）加上当前节点表单拼接而成的。
   - 该表单设计简单，思路清晰，适用于在审核组件模式下不能解决的审核节点特殊字段数据的采集。
   - 该表单是审核组件模式表单的有益补充。
  `);t(this,"SelfForm",`
  #### 定义
 - 嵌入模式的表单，就是开发者自定义个一个表单，然后把表单的地址绑定到节点属性的一种做法。
 - 该模式下，系统在运行的过程中，头部使用工具栏，尾部使用审核组件。
 - 开发者自己定义的页面有一个Save() 的 function ，当用户点击框架外面的工具栏上的【保存】按钮或者【发送】按钮，就会触发这个函数。
 - 您需要在Save()的function里完成数据完整性效验与数据保存，或者调用ccbpm的接口把参数传入到流程引擎里面去.
 - 该方法: 如果保存成功就return true, 保存失败就return false. 用于校验数据填写的完整性. 比如:当用户执行发送的时候，首先执行保存，保存成功后在执行发送，保存失败后，就阻止发送。
 - 您输入的Url可以有参数，但是系统会把所有的参数附件到该url后面。
 - 例如:/SDKFlowDemo/QingJia/SDKQianRuFangShiForm.htm(也可能是vue的路由页面.例如：http://localhost:3000/#/QingJia)
 - 比如:您配置的url为 http://xxxx:222:/abc.htm 系统实际的Url为 http://xxxx:222:/abc.htm?FK_Flow=xxx&FK_Node=xxx&WorkID=xxx&UserNo=xxx&Token=xxx
 - 系统会把当前流程环境中的变量与参数都传递到您的自定义页面上来，您可以根据这些参数来展示，保存数据，控制数据只读，可编辑。
 #### 工作示意图
 - 头部：工具栏, 尾部:审核组件, 中间部门自定义url.
 - 图：
 ![嵌入模式表单](/resource/WF/Admin/AttrNode/FrmSln/SelfFrm/SelfFrm.png "嵌入模式表单.")
 - 该模式充分利用ccbpm提供的toolbar功能, 启用禁用流程功能按钮，在节点属性里设置. 
 - 审核组件的禁用启用，在表单属性=》表单=》设置，或者在流程属性里批量设置。

 #### Url跟目录定义
 - 本机文件: 在ccbpm前端目录里开发的表单文件.
 - 全路径: 比如：http://xxx.ccbpm.cn/xx.htm
 - 子系统设置: 在流程模板目录上设置的路径,在流程目录上点属性设置路径.
 - 图：
 ![嵌入模式表单](/resource/WF/Admin/AttrNode/FrmSln/SelfFrm/systemSetting.png "路径设置.")
 - ccbpm 会读取第一个设置作为表单的根目录.
 - 全局配置文件(.env):  配置在.env 的http路径变量.  VITE_GLOB_API_URL
 - 系统参数(PC端FrmUrl,移动端:MobileFrmUrl) 启动或者运行流程的过程中通过调用api Node_SaveParas() 方法写入的参数的Url, ccbpm就获取这个参数作为url的路径.

 ##### 1)、操作步骤:
      (1). /WF/API/Port_Login (如果已经执行登录就忽略）
      (2). /WF/API/Node_CreateBlankWorkID (创建流程运行实例)
      (3). /WF/API/Node_SaveParas (设置FrmUrl)
 ![嵌入模式表单](/resource/WF/Admin/AttrNode/FrmSln/SelfFrm/SelfFrmUrl.png "嵌入模式表单.")

 #### 如何向流程引擎写入参数?
 - 在流程运行的过程中,需要向流程引擎写入参数,用于控制:流程方向转向、以及接受人等.
 - ccbpm提供两种方法:
 1. 在保存方法里调用API接口.
 2. 在保存方法里，返回指定的格式的json: 
  
  `);t(this,"SDKFormSmart",`
  #### 帮助
  1. 如果要在您的业务表单上跑流程，仅仅要做的是把 SmartSDKFrm.js 放入到您的页面里面。
  1. 系统就会自动生成流程引擎的控制toolbar， toolbar 的按钮权限在节点属性里直接控制。
  1. 您可以充分利用ccbpm的很多组件功能，比如：定位、地图、拍照、附件、评论、写字板、超链接组件等等。
  `);t(this,"SDKForm",`

  #### 帮助
  1. SDK表单就是ccbpm把界面的展现完全交给了开发人员处理,开发人员只要设计一个表单,增加一个发送按钮,调用ccbpm的发送API就可以完成。
  1. 如果使用绝对路径可以使用ccbpm的全局变量@SDKFromServHost ，比如: @SDKFromServHost/MyFile.htm
  1. 例如:/SDKFlowDemo/QingJia/S1_TianxieShenqingDan.jsp , /SDKFlowDemo/QingJia/S1_TianxieShenqingDan.htm
  1. ccbpm团队为您提供了一个demo流程 \\流程树\\SDK流程\\ 该目录下有很多SDK模式的流程供您参考。
  #### 样式
  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/SDKFrm.png "屏幕截图.png")
  `);t(this,"RefOneFrmTree",`
  #### 帮助
   - 表单库里选择一个表单绑定到当前节点上,该模式我们也称为绑定独立表单.
   - 一个表单可以被多个流程模板绑定，这个表单模板就可以重用. 
   - 比如: 集团IT部门发布一个请假表单，一个公文格式表单，各个子公司都可以使用。
   - 我们可以在每个节点上通过节点与表单的关系设置，字段的可见，可用，只读等操作，满足不同的场景使用。
   - 该表单的工作方式与内置表单工作模式一致, 上面是工具栏下面是表单.

  #### 设置所有的节点都采用此表单
   - 驰骋工作流从理论上来说，没个节点都可以绑定不同的单个表单，但是实践的场景是一个流程模板，通常绑定一个表单。
   - 系统提供这个功能，方便设计人员，一次性就可以绑定表单.
   - 我们通过节点与表单的设计实现该表单工作场景，如下说明.
  #### 节点与表单的关系
   - 

  #### 工作样式
  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/ziyou.png")
 
  `);t(this,"SheetTree",`

  #### 帮助
   - 当一个节点需要绑定多个表单的时候，我们把这样的节点称为多表单节点，或者表单树节点。
   - 比如: 项目申报流程，提报项目的时候（开始节点）需要提交，项目基本信息、项目环评信息、项目风险评估、项目实施计划书.
   #### 页面展示
   ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/duobiaodanzhanshi.png "屏幕截图.png")

   #### 功能说明
   - 如果在每个节点上，绑定不同的表单，就要在每个节点上单独绑定. 比如: 开始节点绑定3个表单，第2个节点绑定10个表单.
   #### 表单节点关系
   - 一个表单可以绑定一个流程的不同节点上，每个节点与这个表单，都有一个设置关系,我们称为表单节点关系.
   - 如下图，节点绑定表单
   ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/duobiaodanliebiao.png "屏幕截图.png")

   #### 表单展现方式
   - 我们把一个节点需要绑定多个表单的节点称为多表单节点，它有两种展现方式，标签页与表单树。
   - 对应的流程demo:\\流程树\\表单解决方案\\树形表单与多表单。
  `);t(this,"EntityTS",`
  #### 帮助
   - 在我们的写的TS的Entity中选择其中一个绑定到当前节点上,该模式我们也称为绑定高表单.
   - 一个实体Entity可以被多个流程模板绑定

  `);this.PageTitle="表单方案",this.Btns=[{pageNo:"2",list:["设置所有节点都采用此方案"]},{pageNo:"5",list:["设置"]},{pageNo:"11",list:["表单权限","设置所有节点都采用此方案"]}]}Init(){this.entity=new g,this.KeyOfEn=o.FormType,this.AddGroup("A","内置表单"),this.Blank(E.FoolForm,"节点表单(默认)",this.FoolForm),this.SelectItemsByList(E.RefNodeFrm,"引用其它节点表单",this.RefNodeFrm,!1,P.sqlNodeFrmList,o.NodeFrmID),this.AddGroup("B","自定义表单"),this.AddEntity(E.SelfForm,"嵌入式表单",new v,this.SelfForm),this.AddEntity(E.SDKForm,"SDK表单",new v,this.SDKForm),this.AddGroup("C","绑定表单库里的表单"),this.AddEntity(E.RefOneFrmTree,"单表单(绑定独立表单)",new Z,this.RefOneFrmTree),this.Blank(E.SheetTree,"多表单",this.SheetTree),this.AddEntity(E.EntityTS,"绑定高代码TS",new X,this.EntityTS)}AfterSave(r,n){return F(this,null,function*(){if(r==E.FoolForm.toString()||n==E.Developer.toString()){const i=new g(this.RefPKVal);yield i.Retrieve(),i.NodeFrmID="",yield i.Update()}})}BtnClick(r,n,i){return F(this,null,function*(){var a,h,p,S,B,w;if(r=="2"){if(window.confirm("您确定要执行吗？请仔细阅读执行说明。")==!1)return"";const u=new Q("BP.WF.HttpHandler.WF_Admin_AttrNode_FrmSln");return u.AddPara("FK_Node",(a=this.entity)==null?void 0:a.NodeID),yield u.DoMethodReturnJson("SelfForm_SetAllNodeFrmUseThisSln")}if(r=="5"){const u=I.UrlEn("TS.AttrNode.Sln5",(h=this.entity)==null?void 0:h.PKVal);return new N(_.OpenUrlByDrawer75,u,"设置")}if(r=="11"&&i=="设置所有节点都采用此方案"){if(window.confirm("您确定要执行吗？请仔细阅读执行说明。")==!1)return"";const u=new Q("BP.WF.HttpHandler.WF_Admin_AttrNode_FrmSln");return u.AddPara("FK_Node",(p=this.entity)==null?void 0:p.NodeID),yield u.DoMethodReturnJson("RefOneFrmTree_SetAllNodeFrmUseThisSln")}if(r=="11"&&i=="表单权限"){const u=((S=this.entity)==null?void 0:S.NodeFrmID)+"_"+((B=this.entity)==null?void 0:B.NodeID)+"_"+((w=this.entity)==null?void 0:w.FK_Flow),T=I.UrlEn("TS.AttrNode.FrmNode11",u);return new N(_.OpenUrlByDrawer75,T,"设置")}throw new Error("方法暂未实现")})}}const Ot=Object.freeze(Object.defineProperty({__proto__:null,GPE_FrmSln:ee},Symbol.toStringTag,{value:"Module"}));class c extends o{}t(c,"NodeID","NodeID"),t(c,"SelectorModel","SelectorModel"),t(c,"SelectorP1","SelectorP1"),t(c,"SelectorP2","SelectorP2"),t(c,"SelectorP3","SelectorP3"),t(c,"SelectorP4","SelectorP4"),t(c,"FK_SQLTemplate","FK_SQLTemplate"),t(c,"IsAutoLoadEmps","IsAutoLoadEmps"),t(c,"IsSimpleSelector","IsSimpleSelector"),t(c,"IsEnableDeptRange","IsEnableDeptRange"),t(c,"IsEnableStaRange","IsEnableStaRange"),t(c,"Name","Name");class U extends y{constructor(e){super("TS.WF.SelecterFree"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","自由选择(海选)");return e.AddTBIntPK(m.NodeID,0,"节点ID",!1),e.AddBoolean(c.IsAutoLoadEmps,!0,"是否自动加载上一次选择的人员？",!0,!0,!0),e.AddBoolean(c.IsSimpleSelector,!1,"是否单项选择(只能选择一个人)？",!0,!0,!0),e.AddBoolean(c.IsEnableDeptRange,!1,"是否启用部门搜索范围限定(对使用通用人员选择器有效)？",!0,!0,!0),e.AddBoolean(c.IsEnableStaRange,!1,"是否启用角色搜索范围限定(对使用通用人员选择器有效)？",!0,!0,!0),this._enMap=e,this._enMap}}const vt=Object.freeze(Object.defineProperty({__proto__:null,SelecterAttr:c,SelecterFree:U},Symbol.toStringTag,{value:"Module"}));class l{}t(l,"Station",0),t(l,"Dept",1),t(l,"Emp",2),t(l,"SQL",3),t(l,"SQLTemplate",4),t(l,"GenerUserSelecter",5),t(l,"DeptAndStation",6),t(l,"Url",7),t(l,"AccepterOfDeptStationEmp",8),t(l,"AccepterOfDeptStationOfCurrentOper",9),t(l,"TeamOrgOnly",10),t(l,"TeamOnly",11),t(l,"TeamDeptOnly",12),t(l,"ByStationAI",13),t(l,"ByWebAPI",14),t(l,"ByMyDeptEmps",15);class x extends R{constructor(){super("GPE_SelectorModel");t(this,"SQLTemplate",`
  #### 帮助
  - 选择已经预制好的SQL语句,查询出来可以选择的人员范围.
  - 这些模板文件都存储在 WF_SQLTemplate ,您可以使用 SELECT No,Name FROM WF_SQLTemplate WHERE SQLType=5 查询出来这些模板.
  - 维护这些模板在表单设计器中维护，也可以手工维护.
  `);t(this,"DeptAndStation",`
  #### 帮助
   - 按选择的部门与角色的交集，显示人员集合。
   - 角色集合下有一些人员，部门下有一些人员，他们的交集，就是要选择的范围.
 
   `);t(this,"Emp",`
  #### 帮助
   -  绑定多少人，就显示多少人。
    `);t(this,"ByMyDeptEmps",`
  #### 帮助
   - 弹出的人员选择器中，仅仅列出来本部门的人员，包括兼职部门。
   - 关于组织结构表，请参考doc.ccbpm.cn手册.
    `);t(this,"ByStation",`
  #### 帮助
   - 根据选择的角色集合，求出来该集合下的人员集合。
   - 把人员集合列出来，让用户选择。
  `);t(this,"ByStationAI",`
  #### 帮助

   - 根据当前人员所在部门的集合（一人可能所在多个部门）得到的人员集合,与选择的角色下的人员集合的交集,就是可选择的人员集合.

   `);t(this,"ByBindEmp",`
  #### 帮助
   - 按绑定的人员集合，作为选择的对象。     
`);t(this,"ByAPIUrl",`
#### 帮助
 - 接口返回值类型为String类型, 多个人员用逗号分开.
 - 格式: zhangsan,lisi,wangwu
`);t(this,"BySQL",`
 
  #### 帮助
   -  该SQL是需要返回No,Name两个列，分别是人员编号,人员名称，返回的数据必须按照顺序来。
   -  SQL语句支持ccbpm表达式, 比如：SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
   -  比如：SELECT No,Name FROM Port_Emp WHERE FK_Dept='@MyFieldName' MyFieldName 可以是节点表单字段.
   -  如果通过api接口发起流程，可以传入系统参数 SELECT No,Name FROM Port_Emp WHERE FK_Dept='@MySysPara' .
   -  如何传入系统参数：请参考流程属性，流程二开，调用： Flow_SaveParas(workid, paras) 的方法.
   #### 总结表达式分为三类.
   - 登录信息:  @WebUser.No, @WebUser.DeptNo ，@WebUser.OrgNo
   - 节点表单字段: @MyFieldName.
   - 系统传来的参数:  通过调用API. 调用： Flow_SaveParas(workid, paras) 的方法.
  #### 其他.
   -  什么是ccbpm表达式，请百度：ccbpm 表达式。
   -  注意：1. 区分大小写。2. 顺序不能变化, No,Name 
`);this.PageTitle="可选人员范围"}Init(){this.entity=new L,this.KeyOfEn=c.SelectorModel,this.AddGroup("B","按组织结构限定范围"),this.AddEntity(l.Station,"按照角色",new W,this.ByStation),this.AddEntity(l.ByStationAI,"按角色智能计算",new W,this.ByStationAI),this.AddEntity(l.DeptAndStation,"按部门与角色的交集",new O,this.DeptAndStation),this.AddEntity(l.Dept,"按绑定的部门计算",new M,this.ByBindEmp),this.Blank(l.ByMyDeptEmps,"只能选择本部门人员",this.ByMyDeptEmps),this.AddEntity(l.Emp,"只能选择指定的人员",new K,this.Emp),this.AddGroup("C","按自定义SQL限定范围"),this.SingleTBSQL(l.SQL,"按SQL计算",o.DeliveryParas,this.BySQL),this.SingleDDLSQL(l.SQLTemplate,"按SQL模板计算",o.DeliveryParas,this.SQLTemplate,"SELECT No,Name FROM WF_SQLTemplate WHERE SQLType=5",!1)}AfterSave(r,n){if(r==n)throw new Error("Method not implemented.")}BtnClick(r,n,i){if(r==n||r===i)throw new Error("Method not implemented.")}}const Ut=Object.freeze(Object.defineProperty({__proto__:null,GPE_SelectorModel:x,SelectorModelEnum:l},Symbol.toStringTag,{value:"Module"}));class L extends y{constructor(e){super("TS.WF.SelecterFix"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","人员选择器");return e.AddTBIntPK(m.NodeID,0,"节点ID",!0),e.AddTBInt(c.SelectorModel,0,"显示方式",!1,!1),e.AddBoolean(c.IsAutoLoadEmps,!0,"是否自动加载上一次选择的人员？",!0,!0,!0),e.AddBoolean(c.IsSimpleSelector,!1,"是否单项选择(只能选择一个人)？",!0,!0,!0),e.AddTBString(c.DeliveryParas,null,"参数",!1,!1,0,100,100,!1,null),e.AddRM_GPE(new x,"icon-drop"),this._enMap=e,this._enMap}}const Ct=Object.freeze(Object.defineProperty({__proto__:null,SelecterFix:L},Symbol.toStringTag,{value:"Module"}));class te extends R{constructor(){super("GPE_AccepterRole");t(this,"Starter_Station",`
  #### 帮助
  - 角色下的人员集合可以发起.
  `);t(this,"Starter_Dept",`
  #### 帮助
  - 部门下的人员集合的交集可以发起.
  `);t(this,"Starter_DeptAndStation",`
  #### 帮助
  - 角色下的人员集合与部门下的人员集合的交集可以发起.
  `);t(this,"Starter_Orgs",`
  #### 帮助
  - 对于集团版一个组织设计一个流程如果要共享给其他组织使用，就可以启用该功能.
  - 这就叫流程模板共享, 共享的流程都必须是任何人都可以发起的流程.
  ##### 其他
  - 关于ccbpm的工作模式，请参考doc.ccbpm.cn 手册. 
  `);t(this,"Starter_Guests",`
  #### 帮助
  - 我们把用户分为外部用户与内部用户.
  - 内部用户就是组织内的用户，存储在Port_Emp表里, 就是组织内的工作人员.
  - 外部用户有多种, 比如:供应商、销售商、合作伙伴这些人员可以存储自己指定的表里.
  - 更多信息请参考操作手册 doc.ccbpm.cn
  #### 学生请假系统
  - 流程图: 
  - 教职工就是内部用户，家长为孩子请假，家长登录就是外部用户.
  - ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/StartGuest.png "屏幕截图")
  - 外部用户也有待办、在途、发起、流程操作，接口与内部用户不同.
  #### 外部用户发起流程的API接口.
  - BP.WF.Dev2InterfaceGuest.*.* 
  - ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/StarterDev2.png "屏幕截图.png")

  `);t(this,"Starter_AnyOne",`
  #### 帮助
  - 流程发起人也称为流程的启动权限.
  - 默认为：所有的人都可发起,启动改流程的权限不限制.
  #### 其他
  - 有一些流程不想被出现在发起流程的列表里，比如：子流程不能被单独调用，就在流程属性里是否可以独立启动.
  - 不可以独立启动，就算有发起权限也不能显示在发起流程的列表里.
  #### 发起流程列表
  - 一个人进入系统后，可以发起的流程列表是可以通过接口调用的.
  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/StarterAnyOne.png "屏幕截图.png")

  `);t(this,"Starter_Emps",`
  #### 帮助
  - 按绑定的人员计算, 绑定的人才能发起流程.
  `);t(this,"ByStation",`
  #### 帮助
  - 定义: 接受人规则,就是A节点发到B节点, B节点上那些人可以处理B节点上的工作,根据不同的情况下，我们做不同的处理，这就叫接受人规则.
  - 接收人规则,分为两大类:主观选择与自动计算.
  - 功能拓扑图

  #### 找人算法
  
   -  第0步：首先从本级部门内的人员寻找，该节点绑定的角色，如果找不到人就进入下一步寻找。
   -  第1步：直线上级寻找，一直查询到根节点，如果找不到人就进入下一步寻找。
   -  第2步：直线上级的平级部门寻找，一直查询到根节点，如果找不到人就进入下一步寻找。
   -  第3步：当前部门的下一级所有的部门寻找。
   -  第4步：查找全局角色人员。
   
  #### 什么是人员身份? 
   -  就是按照那个人员来计算当前节点的接受人规则，默认是谁操作的按照谁的身份计算。
   -  比如：我要请假，找部门负责人，就按照当前操作员的身份计算。
   -  有的环境下并不是按照当前人员计算。 
   -  比如：我要为别人请假，找部门负责人，要按照指定的字段（该字段就是被请假人员的账号）作为人员身份。
   -  详见Entity内说明。
   #### 其他说明
   - 该选项与仅按照角色计算有区别.
   - 仅按照角色计算，就是发送给选择的角色下的所有的人。
  `);t(this,"ByStationSpecDepts",`
  #### 帮助
   - 当前节点设置角色集合，设置部门集合规则.
   - 系统在计算的时候，就可以根据部门规则求出部门集合，加上角色集合求出他们的交集。
  #### 其他
  - 为天宇集团开发,
  - 开发日期: 2023.07.18
  #### 累加场景
  - 一个单子，需要子公司的主管审批,也需要总公司的主管审批,根据指定的部门一步步的寻找,然后按照主线直线寻找.
  - 在每个部门寻找出来的人员就累加起来.
    `);t(this,"ByStationOnly",`
  #### 帮助

   - 当前节点绑定角色的集合下面的人员集合作为接受人。
   - 比如：一个省级的公安系统应用里，当前节点绑定派出所所长角色, 如果仅按照角色计算，就会投递到全省的所有派出所所长。
   - 如果按照角色智能计算，就会投递到该警察所在部门的派出所所长。
   #### 其他
   - 全局的按照角色寻找接收人，不考虑部门的维度。

    `);t(this,"ByBindEmp",`
  #### 帮助
   - 绑定的所有的人员，都可以处理该节点的工作。
   - 绑定多少个人，当前节点就有多少个人处理，这一种是最简洁最直接的方式。
   - 适用于当前节点人员比较稳定，一般不会变化的情况。
   - 如果人员变化比较频繁，就需要设置角色，让角色设置人员。     
`);t(this,"ByDeptAndStation",`
  #### 帮助
   - 取既具备此角色集合的又具备此部门集合的人员，做为本节点的接受人员。
   - 部门人员是一个集合，角色人员是一个集合，两个集合相交的人员集合就是当前节点要投递的对象。
`);t(this,"ByStationAndEmpDept",`

  #### 帮助

   - 该操作需要设置部门与设置角色，两个设置。
   - 当前节点的处理人员需要求两个集合的交集。
   - 比如:在角色里设置部门经理角色，在部门里设置财务与人力资源两个部门。
   - 系统就会得到两个人员集合，第一个集合是所有具有部门经理角色的人员，第2个集合就是财务部，人力资源部所有的人员。
   - 两个的交集就是一个财务部经理于一个人力资源部经理。

`);t(this,"BySpecNodeEmpStation",`
  #### 帮助
   - 指定节点处理人员的身份的角色做为计算规则。
   - 与当前操作员角色身份不同的是，以以前的节点处理人的身份信息，部门信息，角色信息来计算。
`);t(this,"BySetDeptAsSubthread",`

  #### 帮助

  - 仅适用于子线程节点，按照部门分组子线程上的处理人员。
  - 每个部门一个任务，如果该部门的其中有一个人处理了，就标识该部门的工作完成，可以流转到下一步。
`);t(this,"FindSpecDeptEmps",`

  #### 帮助
   

    
  - 绑定的所有的人员，都可以处理该节点的工作。
  - 绑定多少个人，当前节点就有多少个人处理，这一种是最简洁最直接的方式。
  - 适用于当前节点人员比较稳定，一般不会变化的情况。
  - 如果人员变化比较频繁，就需要设置角色，让角色设置人员。

   
  

`);t(this,"ByDeptLeader",`
        
  #### 帮助
          
   - 就是按照那个人员来计算当前节点的接受人规则，默认是谁操作的按照谁的身份计算。 
   - 比如：我要请假，找部门负责人，就按照当前操作员的身份计算。 
   - 有的环境下并不是按照当前人员计算。 
   - 比如：我要为别人请假，找部门负责人，要按照指定的字段（该字段就是被请假人员的账号）作为人员身份。 

        
  #### 说明
  
   - 上一个节点发送人的直属领导，处理该工作。 
   - 部门负责人信息，存储到表 Port_Dept 字段：Leader 中.  
   - 注意:Leader 字段是登录人员的帐号，不是人员名称，如果没有此列系统就报错。   
   - 说明：如果当前部门没有负责人，就向上一级部门去找负责人，如果在没有，就提示错误。  
        
    `);t(this,"ByDeptSpecer",`
        
    #### 帮助
            
     - 就是按照那个人员来计算当前节点的接受人规则，默认是谁操作的按照谁的身份计算。 
     - 比如：我要请假，找部门部门分管领导，就按照当前操作员的身份计算。 
     - 有的环境下并不是按照当前人员计算。 
     - 比如：我要为别人请假，找部门分管领导，要按照指定的字段（该字段就是被请假人员的账号）作为人员身份。 
          
    #### 说明
    
     - 上一个节点发送人的分管领导，处理该工作。 
     - 部门负责人信息，存储到表 Port_Dept 字段：Specer 中.  
     - 注意:Leader 字段是登录人员的帐号，不是人员名称，如果没有此列系统就报错。   
     - 说明：如果当前部门没有分管领导，就向上一级部门去找负责人，如果在没有，就提示错误。  
          
      `);t(this,"ByDept",`
  #### 说明  
   - 节点绑定部门就是该节点下绑定部门里面的所有人员都可以接受该工作.
          
  `);t(this,"ByEmpLeader",`
 
  #### 说明
   - 指定节点发送人的直属领导，处理该工作。 
   - 信息，存储到表 Port_Emp 字段：Leader 中.  
   - 说明：使用本规则前，请配置相应人员的直属部门的Leader！ 
   #### 技术说明
  - Port_Emp 是人员表, Leader 字段就是本部门的负责人.
  - Port_Emp.Leader 是存储的部门领导登录账号.
  - 如果找不到(没有设置)，部门的领导，系统默认会向部门领导去找.
  `);t(this,"BySenderParentDeptLeader",`
  #### 什么是人员身份
   - 就是按照那个人员来计算当前节点的接受人规则，默认是谁操作的按照谁的身份计算。
   - 比如：我要请假，找部门负责人，就按照当前操作员的身份计算。
   - 有的环境下并不是按照当前人员计算。
   - 比如：我要为别人请假，找部门负责人，要按照指定的字段（该字段就是被请假人员的账号）作为人员身份。 
  #### 说明
   - 上一个节点发送人的直属领导，处理该工作。
   - 部门负责人信息，存储到表 Port_Dept 字段：Leader 中.
   - 注意:Leader 字段是登录人员的帐号，不是人员名称，如果没有此列系统就报错。
   - 如果当前部门没有负责人，就向上一级部门去找负责人，如果还没有，就提示错误。 
`);t(this,"BySenderParentDeptStations",`
  #### 帮助   
   - 指定节点发送人的上级部门所在角色下的所有人员。 
   - 注意：上级部门必须绑定角色 
`);t(this,"ByStarter",`
  #### 帮助       
   - 当前节点的处理人与开始节点一致，发起人是 zhangsan,现在节点的处理人也是zhangsan。   
   - 多用于反馈给申请人节点，通知申请人审批审核结果，此工作已经审核审批完毕。
   #### 图例
   
   - ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingFlow3.png "屏幕截图")

`);t(this,"ByPreviousNodeEmp",`
  #### 帮助      
   - 节点A是zhangsan处理，发送到节点B,也是需要zhangsan处理。 
   - 就是自己发送给自己的模式。 
`);t(this,"BySpecNodeEmp",`
  #### 帮助  
   - 当前节点的处理人与指定的节点处理人相同。
   - 所指定的节点一般都是当前节点以前的节点，由于分支原因会导致历史的节点有多个。
   - 如果出现多个，系统就会按照节点的发生时间排序的第一个节点计算。  
      
`);t(this,"BySFTable",`
#### 帮助
 -  绑定的字典计算.
 - 请参考字典的概念，并定义字典表.
 -  字典维护: 系统设置=>字典维护.
`);t(this,"BySQL",`
  #### 帮助
   -  该SQL是需要返回No,Name两个列，分别是人员编号,人员名称，返回的数据必须按照顺序来。
   -  SQL语句支持ccbpm表达式, 比如：SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
   -  比如：SELECT No,Name FROM Port_Emp WHERE FK_Dept='@MyFieldName' MyFieldName 必须是节点表单字段.
   -  什么是ccbpm表达式，请百度：ccbpm 表达式。
   -  注意：1. 区分大小写。2. 顺序不能变化, No,Name 
   #### 其他
   - 复杂的，常用的sql,可以使用 '按设置的SQLTempate获取接受人计算' .
      
`);t(this,"BySQLTemplate",`
  #### 帮助
   - 对于经常使用的、复杂的用sql表达的接收人场景，保存起来，在其他节点上可以调用.
   - SQL模板变化后，其他节点跟着变化. 是引用关系，而不是复制关系.
   - 场景: 开发人员把规则配置好，业务人员可以调用.
   #### 技术信息
   - 数据存储在 wf_sqltemplate 里面. 
   - 可以手工的维护上.
`);t(this,"BySQLAsSubThreadEmpsAndData",`
 
  #### 帮助
   - 此方法与分合流相关，只有当前节点是子线程才有意义。
              
`);t(this,"BySelected",`
  #### 帮助

   - A节点发送到B节点，如果B节点的接受人规则是由A来选择的,这样的行为类似于发送邮件模式，就是我选择谁就发送给谁.
   - 这种模式是有上一个节点的发送人主观判断的，而非自动计算。
   - 海选:就是可以选择任何人,如下图，可以在文本框里搜索。
  #### 通用的人员选择器效果图
 - ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/BySelected.png "屏幕截图.png")
      
`);t(this,"BySelectedFix",`
  #### 帮助
   - A节点发送到B节点，如果B节点的接受人规则是由A来选择的,这样的行为类似于发送邮件模式，就是我选择谁就发送给谁.
   - 这种模式是有上一个节点的发送人主观判断的，而非自动计算。
   - 固定范围的选择:就是需要设置选择人的范围，比如：按照SQL,部门，人员设定仅仅选择副局长，副部长.
  #### 固定范围人员选择器效果图
  - ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/BySelectedFix.png "屏幕截图.png")
      
`);t(this,"ByRouteAttr2Emp",`
  #### 帮助
   - 是由填写表单里的枚举字段值控制的，根据对应枚举字段值不同，所处理人不一样这样的行为使用这个字段路由.
   - 字段名称的配置：字段ID(示例：请假类型枚举字段，对应的字段ID是QJLX)
   - 配置格式是：@ + 枚举key + , + 人员账号No（示例：@0,admin）
      
`);t(this,"BySelfUrl",`

  #### 帮助
   -  该URL是点击发送直接弹出自定义的人员选择的页面，需要开发人员将接收人的信息保存到 WF_SelectAccepter表里.
   -  系统将会把当前节点的信息传入到您的url里面去，比如：FK_Node,WorkID,FK_Flow
   -  在选择完毕后,您需要将选择的用户ID,存储到到接口里面去。

  #### DEMO 
   - /DataUser/PopSelf.htm
      
`);t(this,"ByAPIUrl",`
 
  #### 帮助
   - 请设置webpai， 点击webApi设置按钮.
   - 返回的数据格式为: zhangsan,lisi,wangwu
   - 多个人员用逗号分开.
      
`);t(this,"ByPreviousNodeFormEmpsField",`
  #### 帮助
   - 在设计节点表单的时，增加一个人员信息字段。该字段的内容做为本节点的接收人.
  #### 运行图
  -  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/EmpsField.png "屏幕截图.png")
      
`);t(this,"ByDtlAsSubThreadEmps",`
 
  #### 帮助
   - 适用于分合流场景，当前节点的上一个节点是分流节点, 当前节点是子线程有效.
   - 上一个节点在从表里采集到接收人信息，做为当前节点的接收人.
   - 此方法与分合流相关，只有当前节点是子线程才有意义。
   - 当前参数为明细表的字段列，如果不填写，就默认为 UserNo 。
      
`);t(this,"ByFEE",`
 
  #### 帮助

  - 用流程事件,通过调用设置接受的接口,来设置当前节点的接收人,实现的把接受人信息写入接收人列表里。
  - 这里需要启动流程事件,在事件里动态的,用程序来计算接受人.
`);t(this,"ByFromEmpToEmp",`
 
  #### 帮助
  - 定义: 按照格式配置人员到人员的接收人的路径,每个人员的下一个节点的处理人是固定的.
  - 格式为 @zhangsan,lisi@wangwu,zhaoliu 说明：如果是张三发送的就发送到李四身上. 多个人员对用@分开。
  - 默认接受人列表，就按照默认值寻找: @Defualt,zhangsan 着一样配置表示，没有找到人就按照默认值投递。
  - 应用场景: 1. 人员规则比较固定. 2.数据量较小.  3. 临时性的项目组工作.
  #### 配置效果图
  -  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ByFromEmpToEmp.png "屏幕截图.png")

      
`);this.PageTitle="接收人规则"}Init(){return F(this,null,function*(){this.entity=new g,this.KeyOfEn=o.DeliveryWay,this.Icon="icon-user-following",this.entity.setPKVal(this.PKVal),yield this.entity.Retrieve();const r=this.PKVal;if(!r||typeof r!="string")throw new Error("初始化失败，未获取到主键");if(r.endsWith("01")){this.PageTitle="发起人规则",this.AddGroup("A","流程发起人设置"),this.Blank(s.BySelected,"所有人都可以发起(默认)",this.Starter_AnyOne),this.AddEntity(s.ByStation,"绑定角色计算",new j,this.Starter_Station),this.AddEntity(s.ByBindEmp,"按绑定人员计算",new K,this.Starter_Emps),this.AddEntity(s.ByDept,"按绑定部门计算",new M,this.Starter_Dept),this.AddEntity(s.ByDeptAndStation,"按绑定的角色与部门交集计算",new O,this.Starter_DeptAndStation),this.Blank(s.ByGuest,"仅外部用户可发起",this.Starter_Guests);return}this.Btns=[{pageNo:s.ByStation.toString(),list:["身份规则"]},{pageNo:s.ByMLeader0.toString(),list:["身份规则"]},{pageNo:s.ByStationSpecDepts.toString(),list:["部门范围规则"]},{pageNo:s.ByStationSpecDepts.toString(),list:["部门范围规则"]},{pageNo:s.ByDeptLeader.toString(),list:["部门范围规则"]},{pageNo:s.ByDeptSpecer.toString(),list:["部门范围规则"]},{pageNo:s.BySelected_2.toString(),list:["设置范围"]}],this.AddGroup("A","按组织结构绑定"),this.AddEntity(s.ByStation,"按角色智能计算",new W,this.ByStation),this.AddEntity(s.ByStationOnly,"仅按绑定的角色计算",new j,this.ByStationOnly),this.AddEntity(s.ByBindEmp,"按绑定的人员计算",new K,this.ByBindEmp),this.AddEntity(s.ByDeptAndStation,"按绑定的角色与部门交集计算",new O,this.ByDeptAndStation),this.SingleTB(s.BySpecNodeEmpStation,"按指定节点的人员角色计算",o.DeliveryParas,this.BySpecNodeEmp,"请输入节点ID"),this.AddEntity(s.BySetDeptAsSubthread,"按绑定部门计算，该部门一人处理标识该工作结束(子线程)",new M,this.BySetDeptAsSubthread),this.AddEntity(s.FindSpecDeptEmps,"找本部门范围内的角色集合里面的人员",new M,this.FindSpecDeptEmps),this.Blank(s.ByDeptLeader,"找部门的领导(主管,负责人,Port_Dept.Leader)",this.ByDeptLeader),this.Blank(s.ByDeptSpecer,"找部门的分管领导(Port_Dept.Specer)",this.ByDeptSpecer);const n="SELECT NodeID as No, Name FROM WF_Node WHERE FK_Flow='@FK_Flow'";this.SingleDDLSQL(s.ByEmpLeader,"找指定节点的人员直属领导(主管,负责人,Port_Emp.Leader)","DeliveryParas",this.ByEmpLeader,n,!0),this.AddEntity(s.ByDept,"按绑定的部门计算",new M,this.ByDept),this.AddEntity(s.ByStationSpecDepts,"按指定的部门集合规则与绑定的角色交集计算",new Ee,this.ByStationSpecDepts),this.AddEntity(s.ByStationSpecStas,"按指定的角色集合规则与绑定的部门交集计算",new Ne,this.HelpUn),this.AddGroup("Y","连续多级主管"),this.AddEntity(s.ByMLeader0,"指定角色模式",new Ie,""),this.SingleTB(s.ByMLeader1,"指定通讯录模式","DeliveryParas",this.HelpUn,"请输入级别",ue.AppInt),this.AddGroup("B","按上一个节点的处理人身份"),this.Blank(s.BySenderParentDeptLeader,"发送人上级部门的负责人",this.ByDeptLeader),this.Blank(s.BySenderParentDeptStations,"发送人上级部门角色下的人员(需绑定角色)",this.ByDeptLeader),this.AddGroup("C","按指定节点处理人"),this.Blank(s.ByStarter,"与开始节点处理人相同",this.ByStarter),this.Blank(s.ByPreviousNodeEmp,"与上一节点处理人相同",this.ByPreviousNodeEmp),this.SingleDDLSQL(s.BySpecNodeEmp,"与指定节点处理人相同","DeliveryParas",this.BySpecNodeEmp,n,!0),this.AddGroup("D","按自定义SQL查询"),this.SingleTBSQL(s.BySQL,"按设置的SQL获取接受人计算",o.DeliveryParas,this.BySQL),this.SingleTBSQL(s.BySQLAsSubThreadEmpsAndData,"按SQL确定子线程接受人与数据源",o.DeliveryParas,this.BySQLAsSubThreadEmpsAndData),this.AddEntity(s.BySFTable,"绑定字典(多参数)",new De,this.BySQL),this.AddEntity(s.ByAPIUrl,"按照WebAPI计算",new we,this.ByAPIUrl),Se.CustomNo=="CCFlow"&&(this.AddEntity(s.ZhieJieShangJi601,"直接上级",new J,this.ByStationOnly),this.AddEntity(s.ZhieJieShangJi602,"直接上2级",new J,this.ByStationOnly)),this.AddGroup("E","使用人员选择器-主观选择"),this.AddEntity(s.BySelected,"自由选择(海选)",new U,this.BySelected),this.AddEntity(s.BySelected_2,"固定范围选择",new L,this.BySelectedFix),this.SingleTB(s.BySelfUrl,"自定义人员选择器",o.DeliveryParas,this.BySelfUrl,"请输入自定义的url"),this.AddGroup("F","使用人员选择器-预先选择"),this.AddEntity(s.PreplaceWokerFree,"自由选择(海选)",new U,this.BySelected),this.AddEntity(s.PreplaceWokerFix,"固定范围选择",new L,this.BySelectedFix),this.AddGroup("G","节点表单字段");const a=`SELECT KeyOfEn as No, Name FROM Sys_MapAttr WHERE FK_MapData='${"ND"+Number.parseInt(this.GetRequestVal("FlowNo"))+"Rpt"}'
     AND KeyOfEn Not IN ('AtPara','OID','RDT','GUID','Title','Rec','CDT')`;this.SingleDDLSQL(s.ByPreviousNodeFormEmpsField,"主表人员编号字段","DeliveryParas",this.ByPreviousNodeFormEmpsField,a,!0),this.AddEntity(s.ByPreviousNodeFormEmpsFrmDtl,"从表里的人员编号字段",new pe,this.ByPreviousNodeFormEmpsField),this.SingleDDLSQL(s.ByPreviousNodeFormDepts,"字段是部门编号(按部门的领导计算)","DeliveryParas",this.ByPreviousNodeFormEmpsField,a,!0),this.SingleDDLSQL(s.ByPreviousNodeFormStationsAI,"字段是角色编号(按角色智能计算)","DeliveryParas",this.ByPreviousNodeFormEmpsField,a,!0),this.SingleDDLSQL(s.ByPreviousNodeFormStationsOnly,"字段是角色编号(仅按角色计算)","DeliveryParas",this.ByPreviousNodeFormEmpsField,a,!0),this.SingleDDLSQL(s.ByPreviousNodeFormEmpsTeam,"字段是权限组","DeliveryParas",this.ByPreviousNodeFormEmpsField,a,!0),this.SingleDDLSQL(s.ByDtlAsSubThreadEmps,"由上一节点的明细表来决定子线程的接受人","DeliveryParas",this.ByDtlAsSubThreadEmps,a,!0),this.AddGroup("W","路由配置"),this.SingleTextArea(s.ByFromEmpToEmp,"人员路由列表",o.DeliveryParas,"请阅读帮助规则输入参数",this.ByFromEmpToEmp),this.SingleTextArea(s.ByFromDeptToEmp,"部门路由列表",o.DeliveryParas,"请阅读帮助规则输入参数",this.ByFromEmpToEmp),this.AddEntity(s.ByFromAttrToEmp,"字段路由列表",new ge,this.ByRouteAttr2Emp),this.AddGroup("Z","其它方式"),this.Blank(s.ByFEE,"由FEE来决定",this.ByFEE)})}AfterSave(r,n){return F(this,null,function*(){if(this.PKVal.endsWith("01")){const p=new H;p.NodeID=this.PKVal,yield p.RetrieveFromDBSources();const S=new he("BP.WF.Flow",p.FK_Flow);S.No=p.FK_Flow,yield S.RetrieveFromDBSources(),yield S.DoMethodReturnString("ClearStartFlows"),Pe.info("保存成功，缓存清除了。")}const a=new Te;if(a.setPKVal(this.PKVal),(yield a.RetrieveFromDBSources())==1&&(a.EnIDs=r,a.EnIDsT=n,yield a.Update()),r==n)throw new Error("Method not implemented.")})}BtnClick(r,n,i){return F(this,null,function*(){if(i==="身份规则"){const a=I.UrlGPE(new Be,this.PKVal);return new N(_.OpenUrlByDrawer75,a)}if(i==="部门范围规则"){const a=I.UrlGPE(new fe,this.PKVal);return new N(_.OpenUrlByDrawer75,a)}if(i==="角色范围规则"){const a=I.UrlGPE(new _e,this.PKVal);return new N(_.OpenUrlByDrawer75,a)}if(i==="设置范围"){const a=I.UrlGPE(new x,this.PKVal);return new N(_.OpenUrlByDrawer75,a)}if(r==s.ByAPIUrl.toString()&&i=="设置WebApi"){const a=this.PKVal,h="AR"+a,p=new ye;if(yield p.Init(),p.setPKVal(h),p.MyPK=h,(yield p.RetrieveFromDBSources())==0){const w=new g;w.NodeID=a,yield w.RetrieveFromDBSources(),p.NodeID=a,p.FlowNo=w.FK_Flow,p.MyPK=h,yield p.Insert()}const B=I.UrlEn(p.classID,h);return new N(_.OpenUrlByDrawer75,B)}if(r==n||r===i)throw new Error("Method not implemented.")})}}const Gt=Object.freeze(Object.defineProperty({__proto__:null,GPE_AccepterRole:te},Symbol.toStringTag,{value:"Module"}));class f{}t(f,"BatchCheckNoteModel","BatchCheckNoteModel"),t(f,"BatchCheckListCount","BatchCheckListCount"),t(f,"BatchFields","BatchFields"),t(f,"EditFields","EditFields");class re extends y{constructor(r){super("TS.WF.GPENodeBatchRole1");t(this,"ShowRows",`
  #### 帮助
   - 显示的行数,就是每页显示多少条记录.
   - 设置太多批量审核就会导致系统太慢.
   `);t(this,"Note1",`
  #### 帮助
   - 设置显示的列表字段，多个字段用逗号分开.
   - 比如： Tel,Addr,Email
   - 这里的字段是可以编辑的字段.
   `);t(this,"Note2",`
   #### 帮助
    - 设置显示的列表字段，多个字段用逗号分开.
    - 比如： Tel,Addr,Email
    `);r&&(this.NodeID=r)}get HisUAC(){const r=new A;return r.IsDelete=!1,r.IsUpdate=!0,r.IsInsert=!1,r}get EnMap(){const r=new D("WF_Node","审核组件模式");return r.AddTBIntPK(m.NodeID,0,"节点ID",!0),r.AddTBString(m.Name,null,"名称",!0,!1,0,100,10),r.AddTBString(m.FK_Flow,null,"FK_Flow",!1,!1,0,100,10),r.AddDDLSysEnum(f.BatchCheckNoteModel,0,"填写意见格式",!0,!0,"BatchCheckNoteModel","@0=选择的多条记录一个意见框@1=每个记录后面都有一个意见框@2=无意见",null,!0),r.AddTBInt(f.BatchCheckListCount,12,"显示行数",!0,!1,!1,this.ShowRows),r.AddTBString(f.BatchFields,null,"显示的字段",!0,!1,0,300,10,!0,this.Note1),r.AddTBString(f.EditFields,null,"可编辑的字段",!0,!1,0,300,10,!0,this.Note2),r.AddTBAtParas(),r.ParaFields=",BatchCheckNoteModel,BatchCheckListCount,BatchFields,EditFields,",this._enMap=r,this._enMap}}const xt=Object.freeze(Object.defineProperty({__proto__:null,GPENodeBatchRole1:re,GPENodeBatchRoleAttr:f},Symbol.toStringTag,{value:"Module"}));class oe extends y{constructor(e){super("TS.WF.GPENodeBatchRole2"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","审核字段分组模式");return e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.Name,null,"节点名",!0,!0,0,300,10,!0),e.AddDDLSysEnum("BatchCheckNoteModel",0,"填写意见格式",!0,!0,"BatchCheckNoteModel","@0=选择的多条记录一个意见框@1=每个记录后面都有一个意见框@2=无意见"),e.AddTBInt("BatchCheckListCount",0,"显示行数",!0,!1),e.AddTBString("BatchCheckNoteField",null,"设置分组字段",!0,!1,0,300,10,!0),e.AddTBString("BatchFields",null,"显示的字段",!0,!1,0,300,10,!0),e.AddTBString("EditFields",null,"可编辑的字段",!0,!1,0,300,10,!0),e.AddTBAtParas(),e.ParaFields=",BatchCheckNoteModel,BatchCheckListCount,BatchFields,EditFields,BatchCheckNoteField,",this._enMap=e,this._enMap}}const Ht=Object.freeze(Object.defineProperty({__proto__:null,GPENodeBatchRole2:oe},Symbol.toStringTag,{value:"Module"}));class se extends R{constructor(){super("GPE_BatchRole");t(this,"Desc0",`
  #### 帮助
  
   - 默认为不处理。
   - 批处理有两种模式：1 审核组件的批处理，2.审核分组的批处理。
   - 审核组件的批处理：是当前节点启用了审核组件，审核意见的时候。
   - 审核分组的批处理：是采用经典表单设计模式，设计的审核分组，用户填写意见是填写的审核分组。

 `);t(this,"Desc1",`

  #### 说明

   - 仅仅对当前节点启用了审批组件(或者签批组件)有效.
   - 审核组件的信息会记录到审核信息表里面.
   - 通过设置批量审批属性可以灵活的满足不同的客户需求.
   
  #### 运行效果图
  
  ![输入图片说明](/resource/WF/Admin/AttrNode/BatchRole/Img/NodeBatchRole.png "屏幕截图.png")

  #### 流程案例图
  - 减刑假释流程
  ![输入图片说明](/resource/WF/Admin/AttrNode/BatchRole/Img/NodeBatchRoleFlow.png "屏幕截图.png")
  - 批次减刑流程
  ![输入图片说明](/resource/WF/Admin/AttrNode/BatchRole/Img/NodeBatchRoleFlow1.png "屏幕截图.png")

  
  #### 配置说明

  ![输入图片说明](/resource/WF/Admin/AttrNode/BatchRole/Img/NodeBatchRoleFlow2.png "屏幕截图.png")

 `);t(this,"Desc2",`
  #### 帮助
  - 对于节点表单有效.
  - 建议使用审核组件.
  #### 其它 
  - 该功能在2022.10以后的版本取消了.
  `);this.PageTitle="批量审核"}Init(){this.entity=new g,this.KeyOfEn=o.BatchRole,this.AddGroup("A","节点表单批处理"),this.Blank("0","不使用批处理",this.Desc0),this.AddEntity("1","审核组件模式批处理",new re,this.Desc1),this.AddEntity("2","字段分组模式批处理",new oe,this.Desc2)}AfterSave(r,n){if(r==n)throw new Error("Method not implemented.")}BtnClick(r,n,i){if(r==n||r===i)throw new Error("Method not implemented.")}}const kt=Object.freeze(Object.defineProperty({__proto__:null,GPE_BatchRole:se},Symbol.toStringTag,{value:"Module"}));class ne extends y{get NodeFrmID(){return this.GetValStringByKey("NodeFrmID")}constructor(e){super("TS.WF.EvaluationRole1"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","固定时效考核");return e.GroupBarShowModel=1,e.AddGroupAttr("基本设置"),e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.Name,null,"名称",!0,!1,0,50,200),e.AddDDLSysEnum("CHWayOfTimeRole",0,"时间计算方式",!0,!1,"CHWayOfTimeRole","@0=按设置计算@1=按表单的字段计算"),e.AddGroupAttr("按设置计算"),e.AddTBInt("TimeLimit",1,"天数",!0,!1),e.AddTBInt("TimeLimitHH",0,"小时",!0,!1),e.AddTBInt("TimeLimitMM",0,"分钟",!0,!1),e.AddTBAtParas(),e.ParaFields=",CHWayOfTimeRole,TimeLimit,TimeLimitHH,TimeLimitMM,",this._enMap=e,this._enMap}beforeUpdateInsertAction(){return F(this,null,function*(){return this.CHWayOfTimeRole=0,!0})}}const Qt=Object.freeze(Object.defineProperty({__proto__:null,EvaluationRole1:ne},Symbol.toStringTag,{value:"Module"}));class ie extends y{constructor(e){super("TS.WF.EvaluationRole4"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","指定字段的时间考核");return e.AddGroupAttr("基本设置"),e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.Name,null,"名称",!0,!1,0,50,200),e.AddDDLSysEnum("CHWayOfTimeRole",1,"时间计算方式",!0,!1,"CHWayOfTimeRole","@0=按设置计算@1=按表单的字段计算"),e.AddGroupAttr("按表单的字段计算"),e.AddDDLSQL("CHWayOfTimeRoleField",null,"选字段(对表单字段有效)","SELECT KeyOfEn as No, Name FROM Sys_MapAttr WHERE FK_MapData ='ND@NodeID' AND MyDataType IN (7,8) ",!0),e.AddTBAtParas(),e.ParaFields=",CHWayOfTimeRole,CHWayOfTimeRoleField,",e.AddRM_UrlRightFrameOpen("设置工作日","/src/","高级设置"),this._enMap=e,this._enMap}beforeUpdateInsertAction(){return F(this,null,function*(){return this.CHWayOfTimeRole=1,!0})}}const jt=Object.freeze(Object.defineProperty({__proto__:null,EvaluationRole4:ie},Symbol.toStringTag,{value:"Module"}));class ae extends R{constructor(){super("GPE_EvaluationRole");t(this,"Desc0",`
    
  #### 帮助
   - 默认为不考核，当前节点不设置任何形式的考核。
   #### 其它
   - ccbpm把考核分为:时效考核、工作量考核、质量考核三种类型.
   - 时效考核,就是按照指定时间范围内考核,比如:一件工作需要3天完成.
   - 工作量考核:就类似于计件工资.
   - 质量考核:就是工作处理的内容完成的结果由下一步的工作人员(领导)进行打分考核.
   ##### 质量考核存储表
   - 质量考核的数据存储在 WF_CHEval 表里.  
   ![输入图片说明](/resource/WF/Admin/AttrNode/EvaluationRole/Img/WF_CH.png "屏幕截图")
   #### 时效考核存储表.
   ![输入图片说明](/resource/WF/Admin/AttrNode/EvaluationRole/Img/WF_CH.png "屏幕截图")

    `);t(this,"Desc1",`
  #### 帮助
   - 按时间点计算，或者说按照设置的时间区间计算。
   - 这个方式有：
   - 1. 设置天数，比如设置应该在几天几小时完成。
   - 2. 按表单的表单字段，选择时间字段，按其设置的时间计算。
   - 3. 流转自定义。 
  
 
      `);t(this,"Desc2",`
  #### 帮助
   - 按照处理工作的多少进行考核。 
   - 这样的节点，一般都是多人处理的节点。
   `);t(this,"Desc3",`
  #### 帮助
   - 质量考核，是当前节点对上一步的工作进行一个工作好坏的一个考核。
   - 考核的方式是对上一个节点进行打分，该分值记录到WF_CHEval的表里，开发人员对WF_CHEval的数据根据用户的需求进行二次处理。
   `);this.PageTitle="考核规则"}Init(){this.entity=new g,this.KeyOfEn="CHWay",this.AddGroup("A","考核规则"),this.Blank("0","不考核",this.Desc0),this.AddEntity("1","按照固定时效考核",new ne,this.Desc1),this.AddEntity("4","按照指定字段的时效考核",new ie,this.Desc1),this.Blank("2","按工作量考核",this.Desc2),this.Blank("3","按工作质量考核",this.Desc3)}AfterSave(r,n){if(r==n)throw new Error("Method not implemented.")}BtnClick(r,n,i){if(r==n||r===i)throw new Error("Method not implemented.")}}const Jt=Object.freeze(Object.defineProperty({__proto__:null,GPE_EvaluationRole:ae},Symbol.toStringTag,{value:"Module"}));class de extends y{constructor(e){super("TS.AttrNode.Sln5"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","多表单");e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.Name,null,"名称",!0,!0,0,50,200),e.AddTBInt("FrmSummaryFieldRole",0,"摘要字段规则",!1,!1),e.AddTBString("FrmSummaryFields",null,"摘要字段s",!1,!1,0,50,200),e.AddTBString("FrmSummaryNames",null,"字段名称",!1,!1,0,50,200),e.AddDDLSysEnum("SheetTreeModel",0,"展示方式设置",!0,!0,"SheetTreeModel","@0=表单树@1=为1个表单的时候,按绑定表单库的表单计算@2=Tab标签页","工作处理器的展现方式.",!1),e.ParaFields=",FrmSummaryFieldRole,FrmSummaryFields,SheetTreeModel,",e.AddTBAtParas(),e.AddGroupMethod("绑定多表单"),e.AddRM_DtlSearch("绑定表单",new Oe,k.FK_Node,"","","FK_Frm,FrmSln,WhoIsPK,FrmNameShow,FrmEnableRole","icon-drop",!0,""),e.AddRM_DtlBatch("批量修改",new ve,k.FK_Node,"","","icon-drop","");const i=new G;return i.Title="设置所有节点都采用此方案",i.Warning="您确定要执行,设置该流程所有节点都采用此方案吗?",i.ClassMethod="DoSetIt",i.RefMethodType=C.FuncToolbar,e.AddRefMethod(i),this._enMap=e,this._enMap}DoFrmAttr(){return this.NodeFrmID==""||this.NodeFrmID==null?"err@错误,请先设置表单ID.":new N(_.GoToUrl,I.UrlEn("TS.AttrNode.FrmNodeExt",this.NodeID+"_"+this.NodeFrmID))}DoSetIt(){return F(this,null,function*(){const e=new $("BP.WF.HttpHandler.WF_Admin_AttrNode_FrmSln");return e.AddPara("FK_Node",this.NodeID),yield e.DoMethodReturnString("RefOneFrmTree_SetAllNodeFrmUseThisSln")})}}const zt=Object.freeze(Object.defineProperty({__proto__:null,Sln5:de},Symbol.toStringTag,{value:"Module"}));class le extends R{constructor(){super("GPE_FrmSummaryField");t(this,"Desc0",`
  #### 帮助
   - 摘要字段定义: 把挑选的字段存储到流程引擎注册表的AtPara里.
   - 说明:  在流程功能页面(待办、在途、草稿)中可以查看的都是流程字段,业务表单信息无法看到,该功能解决了此问题.
  #### 应用场景
   -  在审批请假信息中,没有打开之前，就想看到请假天数，请假原因.
   -  审批合同的时间，在待办里可以看到，合同金额。
 `);t(this,"Desc1",`
 #### 帮助
  - 请选择摘要字段,显示顺序是按照表单的字段顺序进行排序的.
  #### 设置效果图
  - 暂无
  #### 展示效果图
   - 暂无
`);this.PageTitle="摘要字段"}Init(){return F(this,null,function*(){this.entity=new de,this.KeyOfEn="FrmSummaryFieldRole",this.AddGroup("A","+摘要字段"),this.Blank("0","不启用",this.Desc0);const r=this.GetRequestVal("PKVal"),n=new g;n.NodeID=r,yield n.RetrieveFromDBSources();const i=n.NodeFrmID?n.NodeFrmID:parseInt(n.FK_Flow)+r,a=` SELECT KeyOfEn as No, Name, GroupID as GroupNo
                      FROM Sys_MapAttr
                      WHERE FK_MapData='`+i+"' and UIVisible=1 Order BY  GroupID, Idx",h=`SELECT OID AS No, Lab as  Name
                          FROM Sys_GroupField
                          WHERE FrmID='`+i+"'";this.SelectItemsByGroupList("1","选择摘要字段",this.Desc1,!0,h,a,"FrmSummaryFields","FrmSummaryNames")})}AfterSave(r,n){if(r==n)throw new Error("Method not implemented.")}BtnClick(r,n,i){if(r==n||r===i)throw new Error("Method not implemented.")}}const Vt=Object.freeze(Object.defineProperty({__proto__:null,GPE_FrmSummaryField:le},Symbol.toStringTag,{value:"Module"}));class m extends o{}class H extends y{constructor(e){super("TS.WF.Template.NodeExt","BP.WF.Template.NodeExt"),e&&this.setPKVal(e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","节点属性");e.EnClassID=this.classID,e.GroupBarShowModel=0,e.AddGroupAttr("基本配置"),e.AddTBIntPK(m.NodeID,0,"节点ID",!0),e.SetHelperUrl(o.NodeID,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3576080&doc_id=31094"),e.AddTBString(o.FK_Flow,null,"流程编号",!1,!1,0,5,10),e.AddTBString(o.FlowName,null,"流程名",!1,!0,0,200,10),e.AddTBString(o.Name,null,"名称",!0,!0,0,100,10,!1),e.SetHelperAlert(o.Name,"修改节点名称时如果节点表单名称为空着节点表单名称和节点名称相同，否则节点名称和节点表单名称可以不相同"),e.AddDDLSysEnum(o.WhoExeIt,0,"谁执行它",!0,!0,o.WhoExeIt,"@0=操作员执行@1=机器执行@2=混合执行"),e.SetHelperUrl(o.WhoExeIt,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3576195&doc_id=31094"),e.AddDDLSysEnum(o.ReadReceipts,0,"已读回执",!0,!0,o.ReadReceipts,"@0=不回执@1=自动回执@2=由上一节点表单字段决定@3=由SDK开发者参数决定"),e.SetHelperUrl(o.ReadReceipts,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3882411&doc_id=31094"),e.AddDDLSysEnum(o.CancelRole,0,"撤销规则",!0,!0,o.CancelRole,"@0=上一步可以撤销@1=不能撤销@2=上一步与开始节点可以撤销@3=指定的节点可以撤销"),e.SetHelperUrl(o.CancelRole,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3576276&doc_id=31094"),e.AddTBString(o.CancelNodes,null,"可撤销的节点",!0,!1,0,200,50,!0),e.AddBoolean(o.CancelDisWhenRead,!1,"对方已经打开就不能撤销",!0,!0),e.AddBoolean(o.IsOpenOver,!1,"已阅即完成?",!0,!0,!1),e.SetHelperUrl(o.IsOpenOver,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3653663&doc_id=31094"),e.AddBoolean(o.IsResetAccepter,!1,"可逆节点时重新计算接收人?",!0,!0,!0,`
    #### 定义
    - 所谓的可逆节点,Reversible Node, 就是双向箭头节点,可以重复执行的节点. 
    - 当一个节点，被运动了1次+，它就是可逆节点, 因为它被重复发送了.
    - 第一次按照接收人规则接收人有a,b,c三个人. 如果在发送回来, 需要重新计算接收人就true,
    不需要重新计算接受人,把当事人做为接收人就是 false.
    #### 应用场景
    - 流程图
    - 节点A,节点B是双线箭头.
    `),e.AddBoolean(o.IsSendDraftSubFlow,!1,"是否发送草稿子流程?",!0,!0,!0,"如果有启动的草稿子流程，是否发送它们？"),e.AddBoolean(o.IsGuestNode,!1,"是否是外部用户执行的节点(非组织结构人员参与处理工作的节点)?",!0,!0,!0,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661834&doc_id=31094"),e.AddBoolean(o.IsYouLiTai,!1,"该节点是否是游离态",!0,!0),e.SetHelperUrl(o.IsYouLiTai,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3653664&doc_id=31094"),e.AddBoolean(o.WaitIframeMsg,!1,"是否等待嵌入式表单消息",!0,!0,!0),e.SetHelperUrl(o.WaitIframeMsg,"[适用于嵌入式表单]如果集成的页面需要预处理部分逻辑，那么需要开启此项，等待集成的页面处理完成后，才继续执行工具栏按钮"),e.AddTBString(o.FocusField,null,"焦点字段",!0,!1,0,50,10,!0),e.SetHelperUrl(o.FocusField,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3653665&doc_id=31094"),e.AddTBInt("FWCSta",0,"节点状态",!1,!1),e.AddTBInt("FWCAth",0,"审核附件是否启用",!1,!1),e.AddTBString(o.SelfParas,null,"自定义属性",!0,!1,0,500,10,!0),e.SetHelperUrl(o.SelfParas,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3653666&doc_id=31094"),e.AddTBInt(o.Step,0,"步骤(无计算意义)",!0,!1),e.SetHelperUrl(o.Step,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3576085&doc_id=31094"),e.AddTBString(o.Tip,null,"操作提示",!0,!1,0,100,10,!1,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3653667&doc_id=31094"),e.AddBoolean(o.IsTask,!1,"是否允许分配人员?",!0,!0,!0,`
    #### 帮助
    - 该属性是对于该节点上有多个人处理有效。
    - 比如:A,发送到B,B节点上有张三，李四，王五可以处理，您可以指定1个或者多个人处理B节点上的工作。
    `),e.AddBoolean(o.IsRM,!0,"是否启用投递路径自定记忆?",!0,!0,!0,`
    #### 帮助
    - 该属性是对于该节点上有多个人处理有效。
    - 比如:A,发送到B,B节点上有张三，李四，王五可以处理，这次你把工作分配给李四，
    - 如果设置了记忆，那么ccbpm就在下次发送的时候，自动投递给李四，当然您也可以重新分配。
    `),e.AddBoolean(o.IsExpSender,!0,"接收人范围是否排除发送人?",!0,!0,!0,`
    #### 帮助
    - 该属性是对于该节点上有多个人处理有效。
    - 比如:A发送到B,B节点上有张三，李四，王五可以处理，如果是李四发送的，该设置是否需要把李四排除掉。
    `),e.AddBoolean("IsOpenSelecter",!1,"找不到接收人是否手工选择?",!0,!0,!0,`
    #### 帮助
    - 在按照绑定人员计算时，如果找不到人，就弹出接收人选择器选择.
    `),e.AddGroupAttr("运行模式"),e.AddDDLSysEnum(o.RunModel,0,"节点类型",!0,!1,o.RunModel,"@0=线形@1=合流@2=分流@3=分合流@4=同表单@5=异表单"),e.SetHelperUrl(o.RunModel,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661853&doc_id=31094"),e.AddTBFloat(o.PassRate,100,"完成通过率",!0,!1),e.SetHelperUrl(o.PassRate,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661856&doc_id=31094"),e.AddBoolean(b.ThreadIsCanDel,!0,"是否可以删除子线程(当前节点已经发送出去的线程，并且当前节点是分流，或者分合流有效，在子线程退回后的操作)？",!0,!0,!0),e.AddBoolean(b.ThreadIsCanAdd,!0,"是否可以增加子线程(当前节点已经发送出去的线程，并且当前节点是分流，或者分合流有效)？",!0,!0,!0),e.AddBoolean(b.ThreadIsCanShift,!1,"是否可以移交子线程(当前节点已经发送出去的线程，并且当前节点是分流，或者分合流有效，在子线程退回后的操作)？",!0,!0,!0),e.AddDDLSysEnum(o.USSWorkIDRole,0,"异表单子线程WorkID生成规则",!0,!0,o.USSWorkIDRole,"@0=仅生成一个WorkID@1=按接受人生成WorkID"),e.SetHelperAlert(o.USSWorkIDRole,"对上一个节点是合流节点，当前节点是异表单子线程有效."),e.AddBoolean(o.IsSendBackNode,!1,"是否是发送返回节点(发送当前节点,自动发送给该节点的发送人,发送节点.)?",!0,!0,!0),e.SetHelperUrl(o.IsSendBackNode,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=6396936&doc_id=31094"),e.AddGroupAttr("跳转"),e.AddBoolean(o.AutoJumpRole0,!1,"处理人就是发起人",!0,!0,!0),e.SetHelperUrl(o.AutoJumpRole0,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3980077&doc_id=31094"),e.AddBoolean(o.AutoJumpRole1,!1,"处理人已经出现过",!0,!0,!0),e.AddBoolean(o.AutoJumpRole3,!1,"未来节点处理人已经出现过(只针对计算未来处理人的节点使用)",!0,!0,!0),e.SetHelperUrl(o.AutoJumpRole3,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=8393139&doc_id=31094"),e.AddBoolean(o.AutoJumpRole2,!1,"处理人与上一步相同",!0,!0,!0),e.AddBoolean(o.WhenNoWorker,!1,"(是)找不到人就跳转,(否)提示错误.",!0,!0,!0),e.AddTBString(o.AutoJumpExp,null,"表达式",!0,!1,0,200,10,!0),e.SetHelperAlert(o.AutoJumpExp,"可以输入Url或SQL语句,请参考帮助文档."),e.AddDDLSysEnum(o.SkipTime,0,"执行跳转事件",!0,!0,o.SkipTime,"@0=上一个节点发送时@1=当前节点工作打开时"),e.SetHelperUrl(o.SkipTime,"https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3980077&doc_id=31094"),e.AddGroupAttr("按钮权限");const B=new Ae;e.AddAttrs(B._enMap.attrs),e.SetPopList(o.ReturnNodes,P.srcNodes,!0,"300px","500px","选择退回到的节点","icon-people"),e.SetPopList(o.CancelNodes,P.srcNodes,!0,"300px","500px","选择撤销的节点","icon-people"),e.SetPopList(o.JumpToNodes,P.srcNodes,!0,"300px","500px","可跳转到的节点","icon-people"),e.ParaFields=",IsTask,IsRM,IsExpSender,IsYouLiTai,CancelNodes,ReturnNodes,IsOpenSelecter,",e.AddTBAtParas(4e3),e.AddGroupMethod("基本配置","icon-drop"),e.AddRM_GPE(new te,"icon-user-following"),e.AddRM_GPE(new Me,"icon-people"),e.AddRM_GPE(new He,"icon-book-open"),e.AddRM_DtlSearch("节点事件",new Ce,"RefPKVal",null,"","","icon-energy",!0),e.AddRM_DtlSearch("节点消息",new Ge,V.RefPKVal,"","",V.showAttrsMsg,"icon-speech",!1,"&MsgModel=NodeMsg"),e.AddRM_GPE(new Re,"icon-directions"),e.AddRM_GPE(new xe,"icon-close"),e.AddRM_DtlSearch("流程完成条件",new ke,"RefPKVal","","","DataFromText,Note,","icon-drop",!0,"");const u=new G,T=new Le;return u.Title="自定义工具栏",u.Icon="icon-settings",u.RefMethodType=C.Dtl,u.RefDtlClsID=T.GetNewEntity.classID,u.RefDtlRefPK=z.FK_Node,u.ClassMethod="/src/WF/Comm/Dtl/DtlSearch.vue?key=custom_toolbar",u.params={EnName:T.GetNewEntity.classID,RefPK:z.FK_Node,RefMainEnName:this.EnClassID,ButsTableTop:"",ButsItem:"",ShowAttrs:"",isMove:"0",Title:"节点事件",Icon:"icon-settings"},e.AddRefMethod(u),e.AddGroupMethod("表单方案","icon-grid"),e.AddRM_GPE(new ee,"icon-grid"),e.AddRM_GPE(new se,"iconfont icon-ptkj-lianxuqianpimoshi"),e.AddRM_EnOnly("审核组件","TS.WF.Template.NodeWorkCheck","@NodeID","icon-note"),e.AddRM_EnOnly("公文组件","TS.WF.Template.NodeGovDoc","@NodeID","icon-note"),e.AddRM_GPE(new Ue,"icon-puzzle"),e.AddRM_GPE(new le,"iconfont icon-fuwenbenkuang"),e.AddGroupMethod("子流程","icon-paper-plane"),e.AddRM_EnOnly("子流程组件","TS.WF.Template.FrmSubFlow","@NodeID","icon-organization"),e.AddRM_DtlSearch("子流程",new be,We.FK_Node,"","","SubFlowNo,SubFlowName,SubFlowSta,SubFlowType,SubFlowModel,","icon-organization",!0,""),e.AddGroupMethod("考核规则"),e.AddRM_GPE(new ae,"icon-badge"),e.AddRM_GPE(new Ke,"icon-bell"),this._enMap=e,this._enMap}DoSetCheckModel(){return""}DoNodeToolbars(){return"/WF/Comm/Dtl.vue?EnsName=TS.WF.Template.NodeToolbars&RefPK=FK_Node&RefPKVal="+this.NodeID}}class Qe extends Fe{get GetNewEntity(){return new H}constructor(){super()}}const $t=Object.freeze(Object.defineProperty({__proto__:null,NodeExt:H,NodeExtAttr:m,NodeExts:Qe},Symbol.toStringTag,{value:"Module"}));class pe extends y{constructor(e){super("TS.WF.AccepterRoleByEmpsFrmDtl"),e&&(this.NodeID=e)}get HisUAC(){const e=new A;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new D("WF_Node","从表里的人员编号字段");e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString(m.NodeFrmID,null,"表单ID",!1,!0,100),this.NodeFrmID;let r="SELECT No,Name From Sys_MapDtl Where FK_MapData!='' AND (FK_MapData='@NodeFrmID' OR FK_MapData='ND@NodeID')";return e.AddDDLSQL("DeliveryParas",null,"从表",r,!0),e.AddTBString("DtlField",null,"从表列",!0,!1,0,50,200,!0),r="SELECT KeyOfEn as No, Name FROM Sys_MapAttr WHERE FK_MapData='@DeliveryParas' AND MyDataType=1 AND KeyOfEn Not IN ('AtPara','OID','RDT','GUID','Title','Rec','CDT')",e.SetPopList("DtlField",r,!1,"200px","300px","选择字段"),e.AddTBAtParas(4e3),e.ParaFields=",DtlField,",this._enMap=e,this._enMap}}const qt=Object.freeze(Object.defineProperty({__proto__:null,AccepterRoleByEmpsFrmDtl:pe},Symbol.toStringTag,{value:"Module"}));export{qt as A,Kt as E,te as G,m as N,bt as S,H as a,Wt as b,Ot as c,vt as d,Ut as e,Ct as f,Gt as g,xt as h,Ht as i,kt as j,Qt as k,jt as l,Jt as m,zt as n,Vt as o,$t as p};
