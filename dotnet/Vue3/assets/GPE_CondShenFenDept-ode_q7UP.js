var S=Object.defineProperty;var g=(r,t,e)=>t in r?S(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var a=(r,t,e)=>g(r,typeof t!="symbol"?t+"":t,e);var h=(r,t,e)=>new Promise((n,o)=>{var p=i=>{try{l(e.next(i))}catch(c){o(c)}},d=i=>{try{l(e.throw(i))}catch(c){o(c)}},l=i=>i.done?n(i.value):Promise.resolve(i.value).then(p,d);l((e=e.apply(r,t)).next())});import{aM as F}from"./entry/index-M8VErHPE-1727507756861.js";import{Cond as m,CondAttr as s}from"./Cond-U5EvNC-p.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Node-B6HRFhwD.js";import"./EntityNodeID-De9k9loD.js";import"./Help-D0bDMZWg.js";class E extends A{constructor(){super("GPE_CondShenFenDept");a(this,"Desc0",`
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

    `);this.PageTitle="人员身份"}Init(){return h(this,null,function*(){this.entity=new m,this.KeyOfEn=s.SpecOperWay;const e=new m(this.PKVal);yield e.Retrieve(),this.AddGroup("A","按发送节点提交人计算"),this.Blank("0"," 发送人登录的部门",this.HelpUn),this.Blank("1"," 发送人的所有部门",this.HelpUn),this.Blank("2"," 发送人使用的部门",this.HelpUn),this.Blank("3"," 发送人使用部门的父级",this.HelpUn),this.AddGroup("B","按指定节点提交人计算");const n=`SELECT NodeID No,Name FROM WF_Node WHERE FK_Flow='${e.FK_Flow}'`;this.SelectItemsByList("10","指定节点提交人的使用部门",this.HelpUn,!1,n,s.SpecOperPara,"Tag1"),this.SelectItemsByList("11","指定节点提交人的所有部门",this.HelpUn,!1,n,s.SpecOperPara,"Tag1"),this.SelectItemsByList("12","指定节点提交人的主部门",this.HelpUn,!1,n,s.SpecOperPara,"Tag1"),this.AddGroup("C","按表单字段人员计算");const o="ND"+parseInt(e.FK_Flow)+"Rpt",p=F.SQLOfMapAttrsGener(o);this.SelectItemsByList("20","字段(参数)值是人员编号-主部门",this.HelpUn,!1,p,s.SpecOperPara,"Tag1"),this.SelectItemsByList("21","字段(参数)值是人员编号-所有部门",this.HelpUn,!1,p,s.SpecOperPara,"Tag1"),this.SelectItemsByList("22","字段(参数)值是部门编号",this.HelpUn,!1,p,s.SpecOperPara,"Tag1"),this.SingleTB("23","系统参数值是部门编号",this.Desc3,s.SpecOperPara,"请输入系统参数")})}AfterSave(e,n){if(e==n)throw new Error("Method not implemented.")}BtnClick(e,n,o){if(e==n||e===o)throw new Error("Method not implemented.")}}export{E as GPE_CondShenFenDept};
