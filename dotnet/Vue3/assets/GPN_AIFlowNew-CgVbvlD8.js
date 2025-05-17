var W=Object.defineProperty;var p=(l,s,t)=>s in l?W(l,s,{enumerable:!0,configurable:!0,writable:!0,value:t}):l[s]=t;var a=(l,s,t)=>p(l,typeof s!="symbol"?s+"":s,t);var d=(l,s,t)=>new Promise((e,n)=>{var u=r=>{try{i(t.next(r))}catch(o){n(o)}},c=r=>{try{i(t.throw(r))}catch(o){n(o)}},i=r=>r.done?e(r.value):Promise.resolve(r.value).then(u,c);i((t=t.apply(l,s)).next())});import{b5 as N,G as w,l as h}from"./entry/index-C6uBgOW5-1730430676707.js";import A from"./HttpHandler-DNmkgeyq.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Events-D9tOL1Ad.js";class b extends N{constructor(){super("GPN_AIFlowNew");a(this,"AlertCar",`
    我要做一个车辆申请流程，如
    员工审批路线： 填写申请、部门审批、办公室审批、反馈给申请人.
    `);a(this,"AlertEmp",`
    我要做一个员工入职流程。
    `);a(this,"AlertQingJia0",`
    请假流程，流程节点包括，填写申请单、部门审批、人力资源审批、反馈给申请人节点等，请补齐。
    `);a(this,"AlertQingJia1",`
    请假表单内容包括，请假日期从，到，请假天数，请假原因，请假类型等内容。
   `);a(this,"WordAlert",`
  #### 帮助
  - 字段附件，附件以字段名的形式在页面中显示; 
  #### 图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/SFTable/Img/Ath1.png "屏幕截图.png") 
  #### 数据存储
  - 附件的默认保存在web服务器上。
  - 可以保存到ftp服务器上, ftp的服务器的连接配置在全局的配置文件中。
  - 如果需要保存到数据库，就需要考虑数据库的存储与备份的问题，文件将会存储在 Sys_FrmAttachmentDB 表中。
    `);a(this,"HelpFile",`
    #### 帮助
    - 请上传excel，word,表单图片文件，AI将会根据内容，自动生成内容.
  `);a(this,"SelectAttrs",`
  #### 帮助
  - 系统根据提示词，生成如下字段.
  #### 数据存储
  - int类型的枚举值是常用的数据类型，ccfrom是格式化的存储到数据表里.
  - 创建一个int类型的字段，用于存储枚举的数据.
    `);this.PageTitle="AI流程"}Init(){return d(this,null,function*(){this.AddGroup("A","AI流程"),this.TextArea("AIFlow","输入提示词",this.WordAlert,"提示词","我要创建一个请假流程 包括但不限于：填写申请单，部门审批，人力资源审批等节点。","请点击帮助参考如何输入提示词?"),this.TextArea("AIFlow.Frm","输入表单要求",this.WordAlert,"提示词","创建请假表单，包括但不限于如下字段：请假日期从、到、请假天数请假原因。","请点击帮助参考如何输入提示词?"),this.FileUpload("File","根据文件生成流程","请上传Excel,word,图片文件.",this.HelpFile),this.AddGroup("B","提示词参考"),this.TextArea("Demo1","用车审批流程",this.WordAlert,"提示词",this.AlertCar,"请点击帮助参考如何输入提示词?"),this.Table("Demo1.SelectedAttrs","选择字段",this.SelectAttrs,!0,this.GenerNodes),this.TextArea("QingJia","请假流程",this.WordAlert,"请输入流程要求",this.AlertQingJia0,"请点击帮助参考如何输入提示词?"),this.TextArea("QingJia.Frm","表单要求",this.WordAlert,"表单要求",this.AlertQingJia1,"请点击帮助参考如何输入提示词?"),this.TextArea("Demo3","收文流程",this.WordAlert,"提示词",this.AlertQingJia1,"请点击帮助参考如何输入提示词?"),this.Table("Demo3.SelectedAttrs","选择字段",this.SelectAttrs,!0,this.GenerNodes)})}GenerNodes(){return d(this,null,function*(){const t=new A("BP.WF.HttpHandler.WF_Admin_AI");t.AddPara("FlowNo",this.RequestVal("FlowNo"));let e=this.RequestVal("tb1","AIFlow");(e==null||e==null||e=="")&&(e=this.RequestVal("tb1","Demo1")),(e==null||e==null||e=="")&&(e=this.RequestVal("tb1","Demo2")),(e==null||e==null||e=="")&&(e=this.RequestVal("tb1","Demo3")),t.AddPara("Words",e);const n=yield t.DoMethodReturnJson("AiFlow_GenerNodes");return JSON.stringify(n)})}GenerSorts(){return d(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,e,n,u,c){return d(this,null,function*(){if(t=="AIFlow.Frm"){const i=this.RequestVal("tb1","AIFlow"),r=this.RequestVal("tb1","AIFlow.Frm"),o=new A("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("SortNo",this.RequestVal("SortNo")),o.AddPara("FlowWords",i),o.AddPara("FrmWords",r);const F="/#/WF/Designer/EditFlow?FlowNo="+(yield o.DoMethodReturnString("AiFlow_SaveFlow"));return new w(h.OpenUrlByNewWindow,F)}if(t.includes("QingJia.Frm")){const i=this.RequestVal("tb1","QingJia"),r=this.RequestVal("tb1","QingJia.Frm"),o=new A("BP.WF.HttpHandler.WF_Admin_AI");o.AddPara("SortNo",this.RequestVal("SortNo")),o.AddPara("FlowWords",i),o.AddPara("FrmWords",r);const F="/#/WF/Designer/EditFlow?FlowNo="+(yield o.DoMethodReturnString("AiFlow_SaveFlow"));return new w(h.OpenUrlByNewWindow,F)}if(t=="File"){alert("开发中.");const i=new A("BP.WF.HttpHandler.Admin_AI");i.AddFile(this.UploadFile),i.AddPara("FlowNo",this.RequestVal("FlowNo"));let r=yield i.DoMethodReturnString("AiFlow_File");return typeof r=="string"&&r.includes("@")&&(r=r.split("@").join(`
`)),new w(h.Message,r)}})}}export{b as GPN_AIFlowNew};
