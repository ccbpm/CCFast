var n=Object.defineProperty;var F=(s,t,e)=>t in s?n(s,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[t]=e;var r=(s,t,e)=>F(s,typeof t!="symbol"?t+"":t,e);import{cs as i,aM as c}from"./entry/index-M8VErHPE-1727507756861.js";import{FrmNode as p,FrmNodeAttr as m}from"./FrmNode-ICauEUJT.js";import{PageBaseGroupEdit as h}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class _ extends h{constructor(){super("GPE_FrmNodeEnableRole");r(this,"Desc0",`
  #### 帮助
   - 始终启用该表单, 默认为始终启用.
   #### 定义
   - 一个节点上绑定多个表单,这些表单在不同的场景下的展示隐藏规则，就是表单启用规则.
   - ccbpm提供了这些规则满足不同的场景需要, 每种类型的规则.
    `);r(this,"Desc1",`
      
  #### 帮助
   - 在节点绑定表单的有数据时显示该表单.
   - 一个流程的所有节点都绑定了10个表单,开始节点采集的时候填写了1个表单, 其他的9个表单没有数据.
   #### 应用场景
   - 一个税务局有10项业务,这10项业务的表单各不同,但每项业务流程完全相同（发起-审核-反馈）.
   #### 设计过程
   - 设计流程时, 只需设计一个流程,创建10个业务表单，每个节点上都绑定10个表单. 
   - 在该流程的流程属性里设置发起前置导航，设置为【按照开始节点绑定的独立表单计算】,请参考流程属性发起前置导航.
   - 在该流程的每个节点表单启用规则里, 开始节点全部设置为有参数时启用，其他节点设置有数据时启用.
   - 请参考 026 流程.
   #### 运行效果-前置导航发起流程
   -  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Sln5/Img/RoleDate.png "屏幕截图.png")
   #### 运行效果-发起流程
   -  ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Sln5/Img/RoleDate2.png "屏幕截图.png")
   
   #### 其他说明
   - 开始节点调用流程的时候也可以不使用前置导航,也可以使用参数模式调用该表单.
   - 比如: 启动行政处罚流程 /WF/MyFlow.htm?FK_Flow=001&Frms=Frm_XZCF
   - 比如: 启动项目申请流程 /WF/MyFlow.htm?FK_Flow=001&Frms=Frm_A,Frm_B,Frm_C
      `);r(this,"Desc2",`
  #### 帮助
   - 在节点绑定表单，在流程属性里，要设置发起前置导航，按照表单列表的方式设置。
   - 格式:MyFlow.htm?FK_Flow=001&Frms=Frm_A,Frm_B,Frm_C
   - 解析:系统就会显示Frm_A,Frm_B,Frm_C 三个表单. 根据传入的参数来显示表单.
   #### 应用场景
   - 业务类型不同,每个业务类型对应一个表单, 流程节点与节点的处理人相同.
   - 在启动流程的时，要选择一个业务类型(就是选择一个表单).
   
      `);r(this,"Desc3",`
     
   #### 帮助
   - 一个流程开始节点表单输入一些信息，根据这些信息决定以后每个节点上应该绑定什么表单.
   - 格式: Attr1=1
   - 解析: 当节点表单字段Attr1 的值等于1的时候，就启用这个表单.
       
   `);r(this,"Desc4",`
  #### 帮助
   - 按SQL表达式启用表单。
   - 设置一个查询语句返回一列一行，如果数值 > 0 则表示该表单被启用.
   - SQL支持表达式,比如: select count(*) from xxxxx where KK=@WebUser.DeptNo
   - 支持ccbpm表达式,什么是ccbpm表达式，请baidu : ccbpm表达式.
   `);r(this,"Desc6",`
  #### 帮助
   - 按选择的角色启用表单.
   - 根据当前打开流程处理器的人员身份计算,是否可以启用这个表单.
   - 如果当前人员的角色集合里,与绑定的角色有交集就可以启用该表单.
   #### 应用场景
   - 在一个点上有多个表单，表单的分配权限按照打开该工作的人员身份来确定的.
   - 拥有xx岗,yy岗的人员，才能显示该表单.
    `);r(this,"Desc7",`
  #### 帮助
   - 按选择的部门启用表单。
   - 根据当前打开流程处理器的人员身份计算,是否可以启用这个表单.
   - 如果当前人员的部门集合里,与绑定的部门有交集就可以启用该表单.
   #### 应用场景
   - 在一个点上有多个表单，表单的分配权限按照打开该工作的人员身份来确定的.
   - 拥有xx部门,yy部门的人员，才能显示该表单.
  `);this.PageTitle="启用规则"}Init(){this.entity=new p,this.KeyOfEn=m.FrmEnableRole,this.AddGroup("A","表单启用规则"),this.Blank("0","始终启用(默认)",this.Desc0),this.Blank("1","有数据时启用",this.Desc1),this.Blank("2","有参数时启用",this.Desc2),this.SingleTBSQL("4","按SQL表达式计算",m.FrmEnableExp,this.Desc4,"输入查询SQL,支持ccbpm表达式"),this.AddGroup("B","按操作员的身份"),this.SelectItemsByGroupList("6","包含角色启用",this.Desc6,!0,i.srcGroupStationTypes,i.srcListStations,m.FrmEnableExp),this.SelectItemsByTree("7","包含部门启用",this.Desc7,!0,i.srcTreeDept,"0",m.FrmEnableExp),this.SelectItemsByTreeEns("8","包含人员启动",this.Desc7,!0,c.srcDeptLazily,c.srcDeptRoot,c.srcEmpLazily,"@No=账号@Name=名称@Tel=电话",m.FrmEnableExp),this.Blank("10","禁用","不启用该表单.")}AfterSave(e,o){if(e==o)throw new Error("Method not implemented.")}BtnClick(e,o,l){if(e==o||e===l)throw new Error("Method not implemented.")}}export{_ as GPE_FrmNodeEnableRole};
