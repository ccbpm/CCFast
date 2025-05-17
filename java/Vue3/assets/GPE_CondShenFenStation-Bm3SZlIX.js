var c=Object.defineProperty;var g=(i,t,e)=>t in i?c(i,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[t]=e;var l=(i,t,e)=>g(i,typeof t!="symbol"?t+"":t,e);var h=(i,t,e)=>new Promise((n,p)=>{var s=r=>{try{a(e.next(r))}catch(d){p(d)}},S=r=>{try{a(e.throw(r))}catch(d){p(d)}},a=r=>r.done?n(r.value):Promise.resolve(r.value).then(s,S);a((e=e.apply(i,t)).next())});import{aM as F}from"./entry/index-C6uBgOW5-1730430676707.js";import{Cond as m,CondAttr as o}from"./Cond-Ca-U73Si.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-IicyYiex.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./Node-DKGUVcK1.js";import"./EntityNodeID-BCBbJNH2.js";import"./Help-D0bDMZWg.js";class B extends A{constructor(){super("GPE_CondShenFenStation");l(this,"Desc0",`
  #### 说明
   - 默认为该模式。
   - 提交人员登录部门就是，条件的判断参数.
    `);l(this,"Desc1",`
  #### 说明
   - 指定节点的处理人作为本步骤的身份。
   - 如下图，设备维修申请人，为公司不同部门，当设备部人员检查后，认定该设备可以维护并给出报价，这时审批权就交还给申请部门的领导。
   - 那么在转向条件就设置为按角色选择，也就是把各部门领导角色的人选择出来，再确认申请人的部门，这样，具有审批权的接收人就是申请人的部门领导。
  #### 流程图
  ![输入图片说明](/resource/WF/Admin/Cond2020/Img/CondShenFenModel.png "屏幕截图")
  #### 配置图
  - 选择新增方向条件
  ![输入图片说明](/resource/WF/Admin/Cond2020/Img/CondShenFenModelSetting.png "屏幕截图")
  - 选择人员身份
  ![输入图片说明](/resource/WF/Admin/Cond2020/Img/CondShenFenModelSetting2.png "屏幕截图")
  
  `);l(this,"Desc2",`
  #### 说明
  - 选择的字段存储的是作为人员身份(该字段里存储的是账号)
  - 指定节点表单的字段作为本步骤的本步骤的身份。
  - 需要选择一个节点ID.
  #### 流程图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingFlow2.png "屏幕截图")
  #### 表单图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingBiaodan2.png "屏幕截图")
  #### 配置图
  ![输入图片说明](/resource/WF/Admin/AttrNode/AccepterRole/Img/ShengqingPeizhi2.png "屏幕截图")
      `);this.PageTitle="岗位条件人员身份"}Init(){return h(this,null,function*(){this.entity=new m,this.KeyOfEn=o.SpecOperWay;const e=new m(this.PKVal);yield e.Retrieve(),this.AddGroup("A","按发送节点提交人计算"),this.Blank("0","发送人登录部门下所有岗位",this.HelpUn),this.Blank("1","发送人的所有部门的岗位",this.HelpUn),this.Blank("2","发送人选择部门+岗位",this.HelpUn),this.AddGroup("B","按指定节点提交人计算");const n=`SELECT NodeID No,Name FROM WF_Node WHERE FK_Flow='${e.FK_Flow}'`;this.SingleDDLSQL("10","指定节点提交人的使用部门下所有岗位",o.SpecOperPara,this.HelpUn,n,!1),this.SingleDDLSQL("11","指定节点提交人的所有部门的岗位",o.SpecOperPara,this.HelpUn,n,!1),this.SingleDDLSQL("24","指定节点提交人的使用部门所使用的岗位",o.SpecOperPara,this.HelpUn,n,!1);const p="ND"+parseInt(e.FK_Flow)+"Rpt",s=F.SQLOfMapAttrsGener(p);this.AddGroup("C","按表单字段人员计算"),this.SingleDDLSQL("20","字段(参数)人员的主部门下所有的岗位",o.SpecOperPara,this.HelpUn,s,!1),this.SingleDDLSQL("21","字段(参数)人员的所有部门的岗位",o.SpecOperPara,this.HelpUn,s,!1),this.SingleDDLSQL("22","字段(参数)就是岗位编号",o.SpecOperPara,this.HelpUn,s,!1)})}AfterSave(e,n){if(e==n)throw new Error("Method not implemented.")}BtnClick(e,n,p){if(e==n||e===p)throw new Error("Method not implemented.")}}export{B as GPE_CondShenFenStation};
