var A=Object.defineProperty;var P=(s,l,a)=>l in s?A(s,l,{enumerable:!0,configurable:!0,writable:!0,value:a}):s[l]=a;var p=(s,l,a)=>P(s,typeof l!="symbol"?l+"":l,a);var h=(s,l,a)=>new Promise((c,m)=>{var n=r=>{try{i(a.next(r))}catch(o){m(o)}},y=r=>{try{i(a.throw(r))}catch(o){m(o)}},i=r=>r.done?c(r.value):Promise.resolve(r.value).then(n,y);i((a=a.apply(s,l)).next())});import{FrmAttachment as w}from"./FrmAttachment-DyM-VnR7.js";import{b9 as N,aL as G,aB as D,aC as F,aG as u}from"./entry/index-M8VErHPE-1727507756861.js";import{GroupField as E,GroupCtrlType as S}from"./GroupField-JFOnJiHV.js";import{MapDtl as K}from"./MapDtl-Bo8WkjPG.js";import{FrmImgAth as M}from"./FrmImgAth-BobBm2n2.js";import{GloComm as U}from"./GloComm-DZ1gELjv.js";import{MapData as d}from"./MapData-lfC2UY9r.js";import{e as C}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";import"./EntityOID-BVVq-i_P.js";import"./EnumLab-CzismWql.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class J extends N{constructor(){super("GPN_DirNew");p(this,"FieldAth",`
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
    
  `);p(this,"HelpDirGroupField",`
 #### 帮助
 - Dir章节内的子级只能是附件，从表，Attr章节，Dir章节，大块文本字段.
`);p(this,"HelpDefaultGroupField",`
#### 帮助
- CtrlType是空的字符串或者null.
- 子级可以是 或者 附件，从表，Attr章节，Dir章节.
`);p(this,"HelpAttrGroupField",`
 #### 帮助
 - Attr章节内的字段会显示为小表单的形式.
`);this.PageTitle="新建目录"}Init(){this.AddGroup("A","新建目录"),this.TextBox1_Name("Dir","新建纯目录",this.HelpDirGroupField,"新建纯目录章节","纯目录章节","输入目录名称"),this.TextBox1_Name("Attr","新建Attr章节",this.HelpAttrGroupField,"新建Attr章节","Attr章节","输入目录名称"),this.TextBox2_NameNo("Dtl","新建从表",this.FieldAth,"Dtl","从表ID","名称","从表"),this.TextBox2_NameNo("Ath","新建表格附件",this.FieldAth,"Ath","附件ID","名称","附件"),this.TextBox2_NameNo("ChapterFrmLinkFrm","自定义表单",this.HelpUn,"","表单ID","名称","自定义表单"),this.TextBox2_NameNo("Self","自定义URL",this.HelpUn,"","url","名称","自定义Url")}GenerSorts(){return h(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(a,c,m,n,y){return h(this,null,function*(){const i=this.RequestVal("FrmID"),r=new E;r.FrmID=i,r.ParentOID=this.RequestVal("ParentOID");let o="";const I=this.RequestVal("ParentOID"),t=new G;if(a==="field"||a=="FrmAttachmentSingle"){if(t.GroupID=I,t.FK_MapData=i,t.MyPK=i+"_"+n,t.KeyOfEn=n,t.Name=m,(yield t.IsExits())==!0)return new D(F.Error,"附件ID["+t.MyPK+"]已存在");const e=new w;e.MyPK=t.MyPK,(yield t.IsExits())==!1&&(e.FK_MapData=i,e.NoOfObj=n,e.Name=m,e.GroupID=I,e.IsDtlAth=0,a==="field"?t.UploadType=1:t.UploadType=0,yield e.Insert()),a==="field"&&(t.UIContralType=u.AthShow,o="TS.FrmUI.FrmAttachmentExt",t.SetPara("EnName",o)),a==="FrmAttachmentSingle"&&(t.UIContralType=u.AthShow,t.UploadType=0,o="TS.FrmUI.FrmAttachmentExt",t.SetPara("EnName",o)),yield t.Insert()}if(a==="Ath"){t.MyPK=i+"_"+n,o="TS.FrmUI.FrmAttachmentExt",r.Lab=m,r.FrmID=i,r.CtrlID=t.MyPK,r.CtrlType=S.Ath,r.Icon="icon-paper-clip",yield r.Insert();const e=new w;if(e.MyPK=t.MyPK,(yield e.IsExits())==!0)return new D(F.Error,"附件ID["+t.MyPK+"]已存在");e.FK_MapData=i,e.NoOfObj=n,e.Name=m,e.GroupID=r.OID,e.IsDtlAth=0,yield e.Insert();const f="/@/WF/Comm/EnOnly.vue?EnName="+o+"&PKVal="+e.MyPK;return new D(F.GoToUrl,f)}if(a==="write"&&(t.GroupID=I,t.FK_MapData=i,t.MyPK=i+"_"+n,t.KeyOfEn=n,t.Name=m,t.UIContralType=u.HandWriting,o="TS.FrmUI.FrmHandWriting",t.SetPara("EnName",o),yield t.Insert()),a==="image"){if(t.GroupID=I,t.FK_MapData=i,t.MyPK=i+"_"+n,t.KeyOfEn=n,t.Name=m,t.UIContralType=u.FrmImgAth,o="TS.FrmUI.FrmImgAth",t.SetPara("EnName",o),(yield t.IsExits())==!0)return new D(F.Error,"附件ID["+t.MyPK+"]已存在");const e=new M;e.MyPK=t.MyPK,(yield t.IsExits())==!1&&(e.FK_MapData=i,e.CtrlID=n,e.Name=m,e.GroupID=I,e.IsDtlAth=0,yield e.Insert()),yield t.Insert()}if(a==="office"&&(t.GroupID=I,t.FK_MapData=i,t.MyPK=i+"_"+n,t.KeyOfEn=n,t.Name=m,t.UIContralType=u.GovDocFile,o="TS.FrmUI.MapAttrGovDocFile",t.SetPara("EnName",o),yield t.Insert()),a==="Dtl"){const e=new K;if(e.No=i+n,yield e.IsExits()){C.info(n+"已经存在");return}e.Name=m,e.FK_MapData=i,e.PTable=e.No,r.Lab=e.Name,r.CtrlType="Dtl",r.CtrlID=e.No,r.Icon="icon-list",yield r.Insert(),e.GroupField=r.OID,yield e.Insert(),o="TS.Frm.MapDtlExt";const f="/@/WF/Comm/EnOnly.vue?EnName="+o+"&PKVal="+e.No;return new D(F.GoToUrl,f)}if(a==="Dir"&&(r.Lab=m,r.CtrlType="Dir",r.SetPara("EnName","TS.FrmUI.GroupField"),yield r.Insert()),a==="Attr"&&(r.Lab=m,r.CtrlType="Attr",r.SetPara("EnName","TS.FrmUI.GroupField"),yield r.Insert()),a==="Self"&&(r.Lab=m,r.CtrlType="ChapterFrmSelfUrl",r.CtrlID=n,r.SetPara("EnName","TS.FrmUI.GroupField"),r.Icon="icon-link",yield r.Insert()),a==="ChapterFrmLinkFrm"){const e=new d(i);if(e.No=n,(yield e.RetrieveFromDBSources())==0)return"err@表单ID输入错误.";r.Lab=m,r.CtrlType="ChapterFrmLinkFrm",r.CtrlID=n,r.SetPara("EnName","TS.FrmUI.GroupField"),r.Icon="icon-cup",yield r.Insert()}const T=U.UrlEn(r.GetParaString("EnName",""),r.OID);return new D(F.GoToUrl,T)})}}export{J as GPN_DirNew};
