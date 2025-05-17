var D=Object.defineProperty;var S=(n,e,t)=>e in n?D(n,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):n[e]=t;var a=(n,e,t)=>S(n,typeof e!="symbol"?e+"":e,t);var l=(n,e,t)=>new Promise((o,s)=>{var p=r=>{try{d(t.next(r))}catch(c){s(c)}},i=r=>{try{d(t.throw(r))}catch(c){s(c)}},d=r=>r.done?o(r.value):Promise.resolve(r.value).then(p,i);d((t=t.apply(n,e)).next())});import{aM as A,U as m,L as f}from"./entry/index-M8VErHPE-1727507756861.js";import{EntityNodeID as g}from"./EntityNodeID-De9k9loD.js";import{NodeStations as I,NodeStation as N}from"./NodeStation-Dp7o-G35.js";import{PageBaseGroupEdit as R}from"./PageBaseGroupEdit-JIgqoTiq.js";import{Node as P}from"./Node-B6HRFhwD.js";class h extends R{constructor(){super("GPE_ARDeptModel");a(this,"Help0",`
  #### 说明
   - 提交人所有的岗位集合,与当前的岗位集合匹配.
    `);a(this,"Help1",`
    #### 说明
     - 提交人登录部门下的岗位集合.
  `);a(this,"Desc1",`
  #### 说明
   - 指定节点的处理人作为本步骤的身份.
   - 需要选择一个节点ID.
  #### 流程图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingFlow1.png "屏幕截图")
  #### 配置图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingPeizhi1.png "屏幕截图")
      `);a(this,"Desc2",`
  #### 说明
  - 选择的字段存储的是作为人员身份(该字段里存储的是账号)
  - 指定节点表单的字段作为本步骤的本步骤的身份.
  - 需要选择一个节点ID.
  #### 流程图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingFlow2.png "屏幕截图")
  #### 表单图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingBiaodan2.png "屏幕截图")
  #### 配置图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingPeizhi2.png "屏幕截图")
      `);this.PageTitle="部门集合范围"}Init(){return l(this,null,function*(){this.entity=new u,this.KeyOfEn="ARDeptModel";const t=new P(this.PKVal);yield t.Retrieve();const o=t.FK_Flow;this.AddGroup("A","按发送节点发送人计算"),this.Blank("0","(上一节点)发送人所有的部门",this.Help0),this.Blank("1","(上一节点)发送人登录部门",this.Help1),this.Blank("2","(上一节点)发送人使用部门",this.HelpUn),this.Blank("3","(开始节点)发起人使用部门",this.HelpUn),this.AddGroup("B","按指定节点提交人计算");const s=`SELECT NodeID No,CONCAT(NodeID,'_',Name) as Name FROM WF_Node WHERE FK_Flow='${o}' `;this.SelectItemsByList("10","指定节点提交人的使用部门",this.HelpUn,!1,s,"ARDeptPara",""),this.SelectItemsByList("11","指定节点提交人的所有部门",this.HelpUn,!1,s,"ARDeptPara",""),this.SelectItemsByList("12","指定节点提交人的主部门",this.HelpUn,!1,s,"ARDeptPara",""),this.AddGroup("C","按表单字段计算");const p="ND"+parseInt(o)+"Rpt",i=A.SQLOfMapAttrsGener(p);this.SelectItemsByList("20","字段(参数)值是人员编号-主部门",this.HelpUn,!1,i,"ARDeptPara",""),this.SelectItemsByList("21","字段(参数)值是人员编号-所有部门",this.HelpUn,!1,i,"ARDeptPara",""),this.SelectItemsByList("22","字段(参数)值是部门编号",this.HelpUn,!1,i,"ARDeptPara",""),this.SingleTB("23","系统参数值是部门编号","ARDeptPara",this.HelpUn,"请输入系统参数")})}AfterSave(t,o){if(t==o)throw new Error("Method not implemented.")}BtnClick(t,o,s){if(t==o||t===s)throw new Error("Method not implemented.")}}const G=Object.freeze(Object.defineProperty({__proto__:null,GPE_ARDeptModel:h},Symbol.toStringTag,{value:"Module"}));class u extends g{constructor(e){super("TS.WF.ARBindStationSpecDept"),e&&(this.NodeID=e)}get HisUAC(){const e=new m;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new f("WF_Node","按指定的部门集合与设置的角色交集计算");return e.AddTBIntPK("NodeID",0,"节点ID",!0),e.AddTBString("ARDeptModel",null,"部门规则",!1,!1,0,100,100,!0),e.AddTBString("ARDeptPara",null,"参数",!1,!1,0,100,100,!0),e.AddTBString("NodeStations",null,"角色",!0,!1,0,100,100,!0),e.SetPopGroupList("NodeStations",A.srcStationTypes,A.srcStations,!0,"300px","500px","选择角色","icon-people","1"),e.AddDDLSysEnum("DGModel56",0,"递归模式",!0,!0,"DGModel56","@0=递归并累加@1=递归不累加@2=不递归",`
    #### 帮助
    - 指定的部门集合递归模式.
    - 0=递归并累加,递归到根节点,并把找到的人累加起来.
    - 1=递归不累加,向根节点递归,如果找到人,就不在递归了.
    - 2=不递归, 仅仅按照指定的部门寻找.
    `,!0),e.AddTBString("ARDeptPara",null,"参数",!1,!1,0,100,100,!0),e.AddTBAtParas(4e3),e.ParaFields=",ARDeptModel,ARDeptPara,DGModel56,",e.AddRM_GPE(new h,"icon-drop"),this._enMap=e,this._enMap}afterUpdate(){return l(this,null,function*(){return yield new I().Delete("FK_Node",this.NodeID),typeof this.NodeStations!="string"||this.NodeStations.split(",").forEach(o=>l(this,null,function*(){const s=new N;s.FK_Node=this.NodeID,s.FK_Station=o,s.MyPK=this.NodeID+"_"+o,yield s.Insert()})),Promise.resolve(!0)})}}const E=Object.freeze(Object.defineProperty({__proto__:null,ARBindStationSpecDept:u},Symbol.toStringTag,{value:"Module"}));export{u as A,h as G,G as a,E as b};
