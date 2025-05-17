var S=Object.defineProperty;var g=(n,t,e)=>t in n?S(n,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):n[t]=e;var a=(n,t,e)=>g(n,typeof t!="symbol"?t+"":t,e);var m=(n,t,e)=>new Promise((r,s)=>{var p=o=>{try{l(e.next(o))}catch(c){s(c)}},d=o=>{try{l(e.throw(o))}catch(c){s(c)}},l=o=>o.done?r(o.value):Promise.resolve(o.value).then(p,d);l((e=e.apply(n,t)).next())});import{aM as F}from"./entry/index-C6uBgOW5-1730430676707.js";import{Cond as h,CondAttr as i}from"./Cond-Ca-U73Si.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-IicyYiex.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./Node-DKGUVcK1.js";import"./EntityNodeID-BCBbJNH2.js";import"./Help-D0bDMZWg.js";class L extends A{constructor(){super("GPE_CondShenFenDept");a(this,"Desc0",`
  #### 说明
   - 默认为该模式。
   - 提交人员登录部门就是，条件的判断参数.
    `);a(this,"Desc1",`
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
      `);a(this,"Desc2",`
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
      `);a(this,"Desc3",`
  #### 说明
   - 获取当前人员信息的是按照指定的部门与指定的角色的交集计算.
   - 指定的部门是从ccbpm的系统参数获取的.
   - 在流程运行的过程中系统参数，是通过 Flow_SavePara() 的方法保存到ccbpm中的.
   - 流程的系统参数存储在 表:WF_GenerWorkFlow 字段:AtPara 中.
   #### 其他
   - 请阅读ccbpm的接口方法，保存参数.

    `);this.PageTitle="人员身份"}Init(){return m(this,null,function*(){this.entity=new h,this.KeyOfEn=i.SpecOperWay;const e=new h(this.PKVal);yield e.Retrieve(),this.AddGroup("A","按发送节点提交人计算"),this.Blank("0"," 发送人登录的部门",this.HelpUn),this.Blank("1"," 发送人的所有部门",this.HelpUn),this.Blank("2"," 发送人使用的部门",this.HelpUn),this.Blank("3"," 发送人使用部门的父级",this.HelpUn),this.AddGroup("B","按指定节点提交人计算");const r=`SELECT NodeID No,Name FROM WF_Node WHERE FK_Flow='${e.FK_Flow}'`;this.SelectItemsByList("10","指定节点提交人的使用部门",this.HelpUn,!1,r,i.SpecOperPara,"Tag1"),this.SelectItemsByList("11","指定节点提交人的所有部门",this.HelpUn,!1,r,i.SpecOperPara,"Tag1"),this.SelectItemsByList("12","指定节点提交人的主部门",this.HelpUn,!1,r,i.SpecOperPara,"Tag1"),this.AddGroup("C","按表单字段人员计算");const s="ND"+parseInt(e.FK_Flow)+"Rpt",p=F.SQLOfMapAttrsGener(s);this.SelectItemsByList("20","字段(参数)值是人员编号-主部门",this.HelpUn,!1,p,i.SpecOperPara,"Tag1"),this.SelectItemsByList("21","字段(参数)值是人员编号-所有部门",this.HelpUn,!1,p,i.SpecOperPara,"Tag1"),this.SelectItemsByList("22","字段(参数)值是部门编号",this.HelpUn,!1,p,i.SpecOperPara,"Tag1"),this.SingleTB("23","系统参数值是部门编号",this.Desc3,i.SpecOperPara,"请输入系统参数")})}AfterSave(e,r){if(e==r)throw new Error("Method not implemented.")}BtnClick(e,r,s){if(e==r||e===s)throw new Error("Method not implemented.")}}export{L as GPE_CondShenFenDept};
