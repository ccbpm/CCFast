var f=Object.defineProperty;var h=(s,e,t)=>e in s?f(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var d=(s,e,t)=>h(s,typeof e!="symbol"?e+"":e,t);var S=(s,e,t)=>new Promise((r,n)=>{var i=l=>{try{o(t.next(l))}catch(A){n(A)}},u=l=>{try{o(t.throw(l))}catch(A){n(A)}},o=l=>l.done?r(l.value):Promise.resolve(l.value).then(i,u);o((t=t.apply(s,e)).next())});import{E as P,W as a,C as g,cf as B,f as m,B as y,m as T,bo as _,U as E,h as F,i as c,H as w}from"./entry/index-C6uBgOW5-1730430676707.js";import{Auths as N,AuthAttr as D}from"./Auth-Fw03_9g2.js";import{GPN_WorkShift as I}from"./GPN_WorkShift-BMuLYP5P.js";import{PageBaseGroupEdit as R}from"./PageBaseGroupEdit-IicyYiex.js";class p extends R{constructor(){super("GPE_MyFrmStyle");d(this,"Desc0",`
  #### 帮助
   - 表单风格有两种风格：经典风格,简洁风格;
   - 经典风格有三种模式：1.默认模式，2.时尚模式，3.黑色模式。
   - 简洁风格有两种模式：1.简洁清晰模式，2.简洁紧凑模式。
   - 现在采用的是经典风格的默认模式。
   #### 运行效果图
   - ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/FrmStyle.png "屏幕截图.png")
 `);d(this,"Desc1",`
 #### 帮助
  - 表单风格有两种风格;
  - 经典风格有三种模式：1.默认模式，2.时尚模式，3.黑色模式。
  - 简洁风格有两种模式：1.简洁清晰模式，2.简洁紧凑模式。
  - 现在采用的是经典风格的时尚模式。
  #### 运行效果图
  - ![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/FrmStyle1.png "屏幕截图.png")
`);d(this,"Desc2",`
#### 帮助
 - 表单风格有两种风格;
 - 经典风格有三种模式：1.默认模式，2.时尚模式，3.黑色模式。
 - 简洁风格有两种模式：1.简洁清晰模式，2.简洁紧凑模式。
 - 现在采用的是经典风格的黑色模式。
 #### 运行效果图
![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/FrmStyle2.png "屏幕截图.png")
`);d(this,"Desc3",`
#### 帮助
 - 表单风格有两种风格;
 - 经典风格有三种模式：1.默认模式，2.时尚模式，3.黑色模式。
 - 简洁风格有两种模式：1.简洁清晰模式，2.简洁紧凑模式。
 - 现在采用的是时尚风格的简洁清晰模式。
 #### 运行效果图
![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/FrmStyle3.png "屏幕截图.png")
`);d(this,"Desc4",`
#### 帮助
 - 表单风格有两种风格;
 - 经典风格有三种模式：1.默认模式，2.时尚模式，3.黑色模式。
 - 简洁风格有两种模式：1.简洁清晰模式，2.简洁紧凑模式。
 - 现在采用的是经典风格的简洁紧凑模式。
 #### 运行效果图
![输入图片说明](/resource/WF/Admin/AttrNode/FrmSln/Img/FrmStyle4.png "屏幕截图.png")
`);d(this,"Desc11",`

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

 `);d(this,"Desc21",`
  #### 帮助
  - 对于节点表单有效.
  - 建议使用审核组件.
  #### 其它 
  - 该功能在2022.10以后的版本取消了.
  `);this.PageTitle="表单风格"}Init(){this.entity=new M,this.KeyOfEn="FrmStyle",this.AddGroup("A","经典风格"),this.Blank("0","默认模式",this.Desc0),this.Blank("1","时尚模式",this.Desc1),this.Blank("2","黑色模式",this.Desc2),this.AddGroup("B","简洁风格"),this.Blank("3","清晰模式",this.Desc3),this.Blank("4","紧凑模式",this.Desc4)}AfterSave(t,r){if(t==r)throw new Error("Method not implemented.")}BtnClick(t,r,n){if(t==r||t===n)throw new Error("Method not implemented.")}}const W=Object.freeze(Object.defineProperty({__proto__:null,GPE_MyFrmStyle:p},Symbol.toStringTag,{value:"Module"}));class M extends P{constructor(e){super("TS.Port.MySetting"),a.CCBPMRunModel==g.SAAS&&(e==null?void 0:e.includes("_"))==!1&&(e=a.OrgNo+"_"+e),e&&this.setPKVal(e)}get HisUAC(){const e=new E;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new F("WF_Emp","我的设置");e.AddGroupAttr("基本信息"),e.AddTBStringPK("No",null,"账号",!0,!0,1,3,50),e.AddTBString("UserID",null,"UserID",!0,!1,0,50,200),e.AddTBString("Name",null,"名称",!0,!1,0,50,200),e.AddDDLEntities("FK_Dept",null,"部门",new B,!1),e.AddTBString("Tel",null,"电话",!0,!1,0,50,200),e.AddTBString("Email",null,"邮件",!0,!1,0,50,200),e.AddTBInt("FrmStyle",0,"表单风格",!1,!1),e.AddDDLStringEnum("SysLang","CH","系统语言","@CH=中文@En=英文@FT=繁体@JP=日文",!0),e.AddGroupAttr("邮件设置"),e.AddDDLStringEnum("EM_SeverType","IMAP","服务器类型","@IMAP=IMAP@POP3=POP3",!0),e.AddTBString("EM_Email",null,"邮件账号",!0,!1,0,50,200),e.AddTBString("EM_Pass",null,"密码",!0,!1,0,50,200),e.AddTBString("EM_IMAP_SeverIP",null,"IMAP服务器",!0,!1,0,50,200,!0),e.AddTBString("EM_IMAP_Port",null,"端口",!0,!1,0,50,200),e.AddBoolean("EM_IMAP_SSL",!1,"SSL",!0,!0),e.AddTBString("EM_SMTP_SeverIP",null,"SMTP服务器",!0,!1,0,50,200,!0),e.AddTBString("EM_SMTP_Port",null,"端口",!0,!1,0,50,200),e.AddBoolean("EM_SMTP_SSL",!1,"SSL",!0,!0),e.AddTBString("EM_SMTP_SenderName",null,"发件人名称",!0,!1,0,50,200,!0),e.AddTBStringDoc("EM_SMTP_SenderSigner",null,"签名",!0,!1,!0),e.AddRM_DtlSearch("授权",new N,D.Auther,"","","AutherToEmpNo,AutherToEmpName,AuthTypeText","icon-drop",!1,"");const t=new c;t.Title="修改密码",t.ClassMethod="CPass",t.HisMap.AddTBString("p1",null,"原密码",!0,!1,0,100,1e3,!0),t.HisMap.AddTBString("p2",null,"新密码",!0,!1,0,100,1e3,!0),t.HisMap.AddTBString("p3",null,"确认密码",!0,!1,0,100,1e3,!0),e.AddRefMethod(t);const r=new c;r.Title="设置头像",r.RefMethodType=m.TabOpen,r.ClassMethod="/src/views/sys/user/UploadAvatar.vue",e.AddRefMethod(r);const n=new c;return n.Title="设置签名（电子签名图片）",n.RefMethodType=m.TabOpen,n.ClassMethod="/src/views/sys/user/CreateSignature.vue",e.AddRefMethod(n),e.AddRM_GPE(new p,"icon-drop"),e.AddRM_GPN(new I,"icon-login"),this._enMap=e,this._enMap}SetIcon(){return"未实现."}SetSigner(){return"未实现."}SetSigineBody(){return"未实现."}CPass(e,t,r){return S(this,null,function*(){if(!e||!t||!r)return"新密码或旧密码不能为空,请填写完整.";{let n=a.No;a.CCBPMRunModel==g.SAAS&&(n=a.OrgNo+"_"+a.No);const i=new y("BP.Port.Emp",n);yield i.Retrieve();const{VITE_GLOB_ENCRYPTION_KEY:u}=T(),o=new _({key:u});return e=o.encryptByAES(e),t=o.encryptByAES(t),r=o.encryptByAES(r),yield i.DoMethodReturnString("ChangePass",e,t,r)}})}DingDingSetting(e,t){return S(this,null,function*(){if(!e||!t)return"人员或流程不能为空,请选择.";{const r=new w("bp.cdtb.Handler");return yield r.AddPara("EmpNo",e),yield r.AddPara("FlowNo",t),yield r.DoMethodReturnString("taskCycle")}})}}const k=Object.freeze(Object.defineProperty({__proto__:null,MySetting:M},Symbol.toStringTag,{value:"Module"}));export{p as G,M,W as a,k as b};
