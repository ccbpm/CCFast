var A=Object.defineProperty;var _=(c,o,e)=>o in c?A(c,o,{enumerable:!0,configurable:!0,writable:!0,value:e}):c[o]=e;var m=(c,o,e)=>_(c,typeof o!="symbol"?o+"":o,e);var K=(c,o,e)=>new Promise((I,n)=>{var s=t=>{try{a(e.next(t))}catch(i){n(i)}},D=t=>{try{a(e.throw(t))}catch(i){n(i)}},a=t=>t.done?I(t.value):Promise.resolve(t.value).then(s,D);a((e=e.apply(c,o)).next())});import{GloComm as h}from"./GloComm-CmAl8MpM.js";import{MapAttr as E}from"./MapAttr-B1mxD3vP.js";import{GroupFields as S}from"./GroupField-DV5A1BJs.js";import{b5 as T,G as f,l as y,aF as d,D as p}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as u}from"./MapExt-DtQWKcAY.js";import{MapDtl as B}from"./MapDtl-C1-EwBHu.js";import{FrmAttachment as P}from"./FrmAttachment-D6absTvH.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./Events-D9tOL1Ad.js";import"./EntityOID-DvdPWQGp.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";class b extends T{constructor(){super("GPN_ComponentField");m(this,"ScoreDesc",`
  #### 帮助
  - 评分控件: 对当前记录的数据进行打分的控件.
  - 使用属性可以控制打分的长度，比如：5分，10分.
  - 设置图片:
  - 运行图片：
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
  - 数据存储在,控件ID对应的字段里.
    `);m(this,"MapDesc",`
  #### 帮助
  - 地图控件: 对当前记录的数据进行打分的控件.
  - 使用属性可以控制打分的长度，比如：5分，10分.
  - 设置图片:
  - 运行图片：
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
  - 数据存储在,控件ID对应的字段里.
    `);m(this,"FrmBtnDesc",`
  #### 帮助
  - 按钮控件: 可以点击后执行Js脚本的控件, 利用onclick来承载编写的js.
  - 应用场景: 使用属性可以控制打分的长度，比如：5分，10分.
  - 如下图:
  - 
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
    `);m(this,"FrmLinkDesc",`
  #### 帮助
  - 按钮控件: 可以点击后执行Js脚本的控件, 利用onclick来承载编写的js.
  - 如下图:
  - 
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
  - 数据信息存储：无
    `);m(this,"LocationDesc",`
    #### 帮助
    - 按钮控件: 定位信息
    - 如下图:
    - 
    #### 数据存储.
    - 设置信息存储在：Sys_MapAttr表里.
    - 数据信息存储：控件对应的字段.
      `);m(this,"FieldAth",`
  #### 帮助
  - 字段附件; 
  #### 数据存储.
    `);m(this,"NewIntEnum",`
  #### 帮助
  - 填写格式1: 团员,党员,群众
  - 系统解析为: 0是团员， 1是党员，2是群众.
  - 填写格式2: @0=团员@1=党员@2=群众
  - 系统解析为: 10是团员， 20是党员，30是群众，这样就可以自己定义枚举值.
  #### 数据存储
  - int类型的枚举值是常用的数据类型，ccfrom是格式化的存储到数据表里.
  - 创建一个int类型的字段，用于存储枚举的数据.
    `);m(this,"Docs1",`
  #### 帮助 
   - 该表单是固定格式的表单,可以展现4列6列展现.
   - 优点:开发效率高,展现简洁,学习成本低,业务人员可以入手.
   - 缺点:展示样式固定.
  `);m(this,"Docs2",`

  #### 帮助
   - 依托富文本编辑器,实现对表单的编辑.
   - 优点:格式灵活,展现效果随心所欲.
   - 缺点:业务人员入手需要一定的学习成本.
   - 适用于:效果
  `);this.PageTitle="新建字段自定义组件"}Init(){this.AddGroup("A","通用组件"),this.TextBox2_NameNo("ExtScore","评分",this.ScoreDesc,"Score","字段ID","名称","评分"),this.AddIcon("icon-like","ExtScore"),this.TextBox2_NameNo("FrmBtn","按钮",this.FrmBtnDesc,"FrmBtn","组件ID","组件名称","按钮1"),this.AddIcon("icon-drop","FrmBtn"),this.TextBox2_NameNo("FrmLink","超链接",this.FrmLinkDesc,"FrmLink","字段ID","连接标签","我的连接"),this.AddIcon("icon-link","FrmLink"),this.TextBox2_NameNo("Location","定位",this.LocationDesc,"Location","字段ID","名称","定位组件"),this.AddIcon("icon-location-pin","Location"),this.TextBox1_Name("FrmHtml","大块说明",this.HelpUn,"FrmHtml","大块说明"),this.AddIcon("icon-doc","FrmHtml"),this.TextBox2_NameNo("ExtMap","地图",this.MapDesc,"Map","字段ID","名称","地图"),this.AddIcon("icon-doc","ExtMap"),this.AddGroup("F","OCR组件"),this.TextBox2_NameNo("id_card_upload","身份证",this.HelpUn,"Card","组件ID","组件名称","身份证1"),this.AddIcon("icon-user","id_card_upload"),(this.RequestVal("PageFrom")||"")==="Dtl"&&this.AddBlank("Invoice","发票",this.HelpUn,"icon-layers"),this.AddGroup("B","流程组件"),this.TextBox2_NameNo("FlowRefLink","关联流程",this.HelpUn,"FlowRefLink","字段ID","名称","关联流程"),this.AddIcon("icon-share","FlowRefLink"),this.TextBox2_NameNo("BillRefLink","关联单据",this.HelpUn,"BillRefLink","字段ID","名称","关联单据"),this.AddIcon("icon-share","BillRefLink"),this.AddBlank("GovAth","公文正文",this.HelpUn),this.AddIcon("icon-doc","GovAth"),this.AddBlank("WordNum","公文字号",this.HelpUn),this.AddIcon("icon-star","WordNum"),this.AddBlank("FlowBBS","流程评论",this.HelpUn),this.AddIcon("icon-bubble","FlowBBS"),this.TextBox2_NameNo("SignCheck","签批组件",this.HelpUn,"SC","字段ID","名称","签批组件"),this.AddIcon("icon-check","SignCheck"),this.AddGroup("C","实验中"),this.TextBox3_NameNoNote("img","图片",this.HelpUn,"Img","组件ID","图片名称","图片URL","通用图片"),this.AddIcon("icon-picture","img"),this.TextBox2_NameNo("Progress","流程进度图",this.HelpUn,"Progress","字段ID","名称","流程进度图"),this.AddIcon("icon-check","Progress"),this.TextBox2_NameNo("iframe","框架",this.FieldAth,"Iframe","字段ID","名称","我的框架"),this.AddIcon("icon-loop","iframe")}GenerSorts(){return K(this,null,function*(){const e=this.RequestVal("FrmID"),I=new S;return yield I.Retrieve("FrmID",e,"Idx"),I.filter(n=>n.CtrlType===""||n.CtrlType==="Attr").map(n=>({No:n.PKVal,Name:n.Lab}))})}Save_TextBox_X(e,I,n,s,D){return K(this,null,function*(){const a=this.RequestVal("FrmID");if(e==="ExtScore"||e==="ExtMap"||e=="FlowRefLink"||e=="BillRefLink"||e=="FrmBtn"||e=="FrmLink"||e=="SignCheck"){const t=new E;if(t.GroupID=I,t.FK_MapData=a,t.MyPK=a+"_"+s,yield t.IsExits())return new f(y.Message,"字段ID=["+s+"]已经存在");t.KeyOfEn=s,t.Name=n;let i=0;e=="ExtScore"&&(i=101),e=="ExtMap"&&(i=4),e=="FlowRefLink"&&(i=200),e=="BillRefLink"&&(i=201),e=="SignCheck"&&(i=d.SignCheck),e=="FrmBtn"&&(i=d.Btn),e=="FrmLink"&&(i=d.HyperLink);const r="TS.FrmUI.SelfCommonent."+e;t.UIContralType=i,t.SetPara("CtrlType",e),t.SetPara("EnName",r),yield t.Insert();const x=h.UrlEn(r,t.MyPK);return new f(y.GoToUrl,x)}if(e=="Invoice"){const t=new E;if(t.GroupID=I,t.FK_MapData=a,t.SetPara("GroupName","Invoice"),t.DataType=p.AppString,t.UIIsEnable=0,t.Name="发票代码",t.KeyOfEn="InvoiceID",t.MyPK=a+"_"+t.KeyOfEn,yield t.IsExits())return new f(y.Message,"发票已经存在.");yield t.Insert(),t.Name="发票号码",t.KeyOfEn="InvoiceCode",t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.Name="开票日期",t.KeyOfEn="InvoiceRelDT",t.DataType=p.AppDate,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.Name="发票类型",t.KeyOfEn="InvoiceTypeStr",t.DataType=p.AppString,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.Name="发票金额",t.KeyOfEn="InvoiceJE",t.DataType=p.AppMoney,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.Name="税额",t.KeyOfEn="InvoiceTaxJE",t.DataType=p.AppMoney,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.Name="税率",t.KeyOfEn="InvoiceTaxRate",t.DataType=p.AppMoney,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.Name="购买方名称",t.KeyOfEn="InvoiceBuyName",t.MaxLen=200,t.MyPK=a+"_"+t.KeyOfEn,t.UIWidth=200,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="InvoiceBuyCode",t.Name="购买方ID",t.MaxLen=200,t.MyPK=a+"_"+t.KeyOfEn,t.UIWidth=150,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="InvoiceBuyAddr",t.Name="购买方地址电话",t.DataType=p.AppString,t.MaxLen=200,t.UIWidth=200,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="InvoiceBuyBankInfo",t.Name="购买方开户行及账号",t.DataType=p.AppString,t.MaxLen=200,t.UIWidth=200,(yield t.IsExits())==!1&&(yield t.Insert()),t.Name="销售方名称",t.KeyOfEn="InvoiceSaleName",t.MaxLen=200,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="InvoiceSaleCode",t.Name="销售方ID",t.MaxLen=150,t.UIWidth=150,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="InvoiceSaleAddr",t.Name="销售方地址电话",t.UIWidth=200,t.MaxLen=200,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="InvoiceSaleBankInfo",t.Name="销售方开户行及账号",t.MaxLen=200,t.UIWidth=200,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="Note",t.Name="备注",t.MaxLen=500,t.UIWidth=500,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert()),t.KeyOfEn="InvoiceFJ",t.Name="附件",t.MaxLen=500,t.UIWidth=500,t.UIContralType=6,t.MyPK=a+"_"+t.KeyOfEn,(yield t.IsExits())==!1&&(yield t.Insert());const i=new P;i.MyPK=t.MyPK,(yield i.IsExits())==!1&&(i.FK_MapData=a,i.NoOfObj=s,i.Name=n,i.IsDtlAth=1,i.IsVisable=0,yield i.Insert());const r=new B(t.FK_MapData);yield r.Retrieve(),r.SetPara("IsInvoice",1),r.IsInsert=0,yield r.Update()}if(e==="FlowBBS"||e=="FrmHtml"||e=="Location"||e=="WordNum"||e=="GovAth"){const t=new E;if(t.GroupID=I,t.Name=this.GetPageName(e),t.FK_MapData=a,t.MyPK=a+"_"+e,yield t.IsExits())return new f(y.Message,"字段ID=["+e+"]已经存在");t.KeyOfEn=e,t.Name=n;let i=0;if(e=="FlowBBS"&&(i=d.FlowBBS),e=="FrmHtml"&&(i=d.FrmHtml),e=="Location"&&(i=d.Location),e=="WordNum"&&(i=d.WordNum),e=="GovAth"&&(i=d.GovDocFile,t.Name="公文正文"),e=="FrmHtml"){const w="HtmlText_"+t.MyPK,l=new u("BP.Sys.MapExt");l.setPKVal(w),(yield l.RetrieveFromDBSources())==0&&(l.MyPK=w,l.FK_MapData=a,l.ExtType="HtmlText",l.ExtModel="HtmlText",l.AttrOfOper=e,yield l.Insert());const M="TS.FrmUI.SelfCommonent."+e;t.UIContralType=i,t.SetPara("CtrlType",e),t.SetPara("EnName",M),yield t.Insert();const F=h.UrlEn(M,w);return new f(y.GoToUrl,F)}const r="TS.FrmUI.SelfCommonent."+e;t.UIContralType=i,t.SetPara("CtrlType",e),t.SetPara("EnName",r),yield t.Insert();const x=h.UrlEn(r,t.MyPK);return new f(y.GoToUrl,x)}if(e==="id_card_upload"){const t=this.RequestVal("FrmID"),i=new E;if(i.GroupID=I,i.FK_MapData=t,i.MyPK=t+"_"+s,yield i.IsExits())return new f(y.Message,"字段ID=["+e+"]已经存在");i.KeyOfEn=s,i.Name=n,i.UIContralType=13,i.UIIsEnable=0,yield i.Insert(),i.MyPK=t+"_"+s+"Name",i.KeyOfEn=s+"Name",i.Name="姓名",yield i.Insert(),i.MyPK=t+"_"+s+"Address",i.KeyOfEn=s+"Address",i.ColSpan=3,i.Name="地址",yield i.Insert();const r=h.UrlEn("TS.FrmUI.MapAttrString",t+"_"+s);return new f(y.GoToUrl,r)}})}}export{b as GPN_ComponentField};
