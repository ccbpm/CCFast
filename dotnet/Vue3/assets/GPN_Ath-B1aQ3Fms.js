var P=Object.defineProperty;var M=(l,I,e)=>I in l?P(l,I,{enumerable:!0,configurable:!0,writable:!0,value:e}):l[I]=e;var p=(l,I,e)=>M(l,typeof I!="symbol"?I+"":I,e);var f=(l,I,e)=>new Promise((u,s)=>{var o=n=>{try{r(e.next(n))}catch(i){s(i)}},y=n=>{try{r(e.throw(n))}catch(i){s(i)}},r=n=>n.done?u(n.value):Promise.resolve(n.value).then(o,y);r((e=e.apply(l,I)).next())});import{FrmAttachment as w}from"./FrmAttachment-D6absTvH.js";import{MapAttr as T}from"./MapAttr-B1mxD3vP.js";import{GroupField as A,GroupCtrlType as N}from"./GroupField-DV5A1BJs.js";import{MapDtl as E}from"./MapDtl-C1-EwBHu.js";import{b5 as _,G as D,l as F,aF as h}from"./entry/index-C6uBgOW5-1730430676707.js";import{FrmImgAth as G}from"./FrmImgAth-BE7f1paG.js";import{b as x}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./Events-D9tOL1Ad.js";import"./EntityOID-DvdPWQGp.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";class q extends _{constructor(){super("GPN_Ath");p(this,"FieldAth",`
  #### 帮助
  - 字段附件，附件以字段名的形式在页面中显示; 
  #### 图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/SFTable/Img/Ath1.png "屏幕截图.png") 
  #### 数据存储
  - 附件的默认保存在web服务器上。
  - 可以保存到ftp服务器上, ftp的服务器的连接配置在全局的配置文件中。
  - 如果需要保存到数据库，就需要考虑数据库的存储与备份的问题，文件将会存储在 Sys_FrmAttachmentDB 表中。
 
    `);p(this,"NewIntEnum",`
  #### 帮助
  - 填写格式1: 团员,党员,群众
  - 系统解析为: 0是团员，1是党员，2是群众.
  - 填写格式2: @0=团员@1=党员@2=群众
  - 系统解析为: 0是团员，1是党员，2是群众，这样就可以自己定义枚举值.
  #### 数据存储
  - int类型的枚举值是常用的数据类型，ccfrom是格式化的存储到数据表里.
  - 创建一个int类型的字段，用于存储枚举的数据.
    `);p(this,"Docs1",`
  #### 帮助 
   - 该表单是固定格式的表单,可以展现4列6列展现.
   - 优点:开发效率高,展现简洁,学习成本低,业务人员可以入手.
   - 缺点:展示样式固定.
  `);p(this,"Docs2",`

  #### 帮助
   - 依托富文本编辑器,实现对表单的编辑.
   - 优点:格式灵活,展现效果随心所欲.
   - 缺点:业务人员入手需要一定的学习成本.
   - 适用于:效果
    
  `);this.PageTitle="附件从表"}Init(){this.AddGroup("A","附件"),this.TextBox2_NameNo("field","字段附件",this.FieldAth,"Ath","字段ID","附件名称","我的附件"),this.TextBox2_NameNo("table","表格附件",this.FieldAth,"Ath","字段ID","附件名称","表格附件"),this.TextBox2_NameNo("image","图片附件",this.FieldAth,"Ath","字段ID","附件名称","我的图片"),this.TextBox2_NameNo("write","写字板",this.FieldAth,"Ath","字段ID","写字板名称","写字板")}GenerSorts(){return f(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,u,s,o,y){return f(this,null,function*(){const r=this.RequestVal("FrmID"),n=this.RequestVal("GroupField");let i="";const t=new T;if(e==="field"||e=="FrmAttachmentSingle"){if(t.GroupID=n,t.FK_MapData=r,t.MyPK=r+"_"+o,t.KeyOfEn=o,t.Name=s,(yield t.IsExits())==!0)return new D(F.Error,"附件ID["+t.MyPK+"]已存在");const a=new w;a.MyPK=t.MyPK,(yield t.IsExits())==!1&&(a.FK_MapData=r,a.NoOfObj=o,a.Name=s,a.GroupID=n,a.IsDtlAth=0,e==="field"?t.UploadType=1:t.UploadType=0,yield a.Insert()),e==="field"&&(t.UIContralType=h.AthShow,i="TS.FrmUI.FrmAttachmentExt",t.SetPara("EnName",i)),e==="FrmAttachmentSingle"&&(t.UIContralType=h.AthShow,t.UploadType=0,i="TS.FrmUI.FrmAttachmentExt",t.SetPara("EnName",i)),yield t.Insert()}if(e==="table"){t.GroupID=n,t.FK_MapData=r,t.MyPK=r+"_"+o,i="TS.FrmUI.FrmAttachmentExt";const a=new A;a.Lab=s,a.FrmID=r,a.CtrlID=t.MyPK,a.CtrlType=N.Ath,yield a.Insert();const m=new w;if(m.MyPK=t.MyPK,(yield m.IsExits())==!0)return new D(F.Error,"附件ID["+t.MyPK+"]已存在");m.FK_MapData=r,m.NoOfObj=o,m.Name=s,m.GroupID=a.OID,m.IsDtlAth=0,yield m.Insert()}if(e==="write"&&(t.GroupID=n,t.FK_MapData=r,t.MyPK=r+"_"+o,t.KeyOfEn=o,t.Name=s,t.UIContralType=h.HandWriting,i="TS.FrmUI.FrmHandWriting",t.SetPara("EnName",i),yield t.Insert()),e==="image"){if(t.GroupID=n,t.FK_MapData=r,t.MyPK=r+"_"+o,t.KeyOfEn=o,t.Name=s,t.UIContralType=h.FrmImgAth,i="TS.FrmUI.FrmImgAth",t.SetPara("EnName",i),(yield t.IsExits())==!0)return new D(F.Error,"附件ID["+t.MyPK+"]已存在");const a=new G;a.MyPK=t.MyPK,(yield t.IsExits())==!1&&(a.FK_MapData=r,a.CtrlID=o,a.Name=s,a.GroupID=n,a.IsDtlAth=0,yield a.Insert()),yield t.Insert()}if(e==="office"&&(t.GroupID=n,t.FK_MapData=r,t.MyPK=r+"_"+o,t.KeyOfEn=o,t.Name=s,t.UIContralType=h.GovDocFile,i="TS.FrmUI.MapAttrGovDocFile",t.SetPara("EnName",i),yield t.Insert()),e==="Dtl"){const a=new E;if(a.No=r+o,yield a.IsExits()){x.info(o+"已经存在");return}a.Name=s,a.FK_MapData=r,a.PTable=a.No;const m=new A;m.Lab=a.Name,m.FrmID=a.FK_MapData,m.CtrlType="Dtl",m.CtrlID=a.No,yield m.Insert(),a.GroupField=m.OID,yield a.Insert(),i="TS.Frm.MapDtlExt";const K="/@/WF/Comm/En.vue?EnName="+i+"&PKVal="+a.No;return new D(F.GoToUrl,K)}const c="/@/WF/Comm/En.vue?EnName="+i+"&PKVal="+t.MyPK;return new D(F.GoToUrl,c)})}}export{q as GPN_Ath};
