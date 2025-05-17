var T=Object.defineProperty;var _=(m,s,i)=>s in m?T(m,s,{enumerable:!0,configurable:!0,writable:!0,value:i}):m[s]=i;var a=(m,s,i)=>_(m,typeof s!="symbol"?s+"":s,i);var p=(m,s,i)=>new Promise((c,r)=>{var o=t=>{try{n(i.next(t))}catch(e){r(e)}},A=t=>{try{n(i.throw(t))}catch(e){r(e)}},n=t=>t.done?c(t.value):Promise.resolve(t.value).then(o,A);n((i=i.apply(m,s)).next())});import{GloComm as F}from"./GloComm-DZ1gELjv.js";import{b9 as w,aL as B,aB as I,aC as h,aG as d,bb as y}from"./entry/index-M8VErHPE-1727507756861.js";import{GroupFields as M}from"./GroupField-JFOnJiHV.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFColumn-Q_PoS_2g.js";import"./EntityOID-BVVq-i_P.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";class W extends w{constructor(){super("GPN_ComponentField");a(this,"ScoreDesc",`
  #### 帮助
  - 评分控件: 对当前记录的数据进行打分的控件.
  - 使用属性可以控制打分的长度，比如：5分，10分.
  - 设置图片:
  - 运行图片：
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
  - 数据存储在,控件ID对应的字段里.
    `);a(this,"MapDesc",`
  #### 帮助
  - 地图控件: 对当前记录的数据进行打分的控件.
  - 使用属性可以控制打分的长度，比如：5分，10分.
  - 设置图片:
  - 运行图片：
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
  - 数据存储在,控件ID对应的字段里.
    `);a(this,"FrmBtnDesc",`
  #### 帮助
  - 按钮控件: 可以点击后执行Js脚本的控件, 利用onclick来承载编写的js.
  - 应用场景: 使用属性可以控制打分的长度，比如：5分，10分.
  - 如下图:
  - 
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
    `);a(this,"FrmLinkDesc",`
  #### 帮助
  - 按钮控件: 可以点击后执行Js脚本的控件, 利用onclick来承载编写的js.
  - 如下图:
  - 
  #### 数据存储.
  - 设置信息存储在：Sys_MapAttr表里.
  - 数据信息存储：无
    `);a(this,"LocationDesc",`
    #### 帮助
    - 按钮控件: 定位信息
    - 如下图:
    - 
    #### 数据存储.
    - 设置信息存储在：Sys_MapAttr表里.
    - 数据信息存储：控件对应的字段.
      `);a(this,"FieldAth",`
  #### 帮助
  - 字段附件; 
  #### 数据存储.
    `);a(this,"NewIntEnum",`
  #### 帮助
  - 填写格式1: 团员,党员,群众
  - 系统解析为: 0是团员， 1是党员，2是群众.
  - 填写格式2: @0=团员@1=党员@2=群众
  - 系统解析为: 10是团员， 20是党员，30是群众，这样就可以自己定义枚举值.
  #### 数据存储
  - int类型的枚举值是常用的数据类型，ccfrom是格式化的存储到数据表里.
  - 创建一个int类型的字段，用于存储枚举的数据.
    `);a(this,"Docs1",`
  #### 帮助 
   - 该表单是固定格式的表单,可以展现4列6列展现.
   - 优点:开发效率高,展现简洁,学习成本低,业务人员可以入手.
   - 缺点:展示样式固定.
  `);a(this,"Docs2",`

  #### 帮助
   - 依托富文本编辑器,实现对表单的编辑.
   - 优点:格式灵活,展现效果随心所欲.
   - 缺点:业务人员入手需要一定的学习成本.
   - 适用于:效果
  `);this.PageTitle="新建字段自定义组件"}Init(){this.AddGroup("A","通用组件"),this.TextBox2_NameNo("ExtScore","评分",this.ScoreDesc,"Score","字段ID","名称","评分"),this.AddIcon("icon-like","ExtScore"),this.TextBox2_NameNo("FrmBtn","按钮",this.FrmBtnDesc,"FrmBtn","组件ID","组件名称","按钮1"),this.AddIcon("icon-drop","FrmBtn"),this.TextBox2_NameNo("FrmLink","超链接",this.FrmLinkDesc,"FrmLink","字段ID","连接标签","我的连接"),this.AddIcon("icon-link","FrmLink"),this.TextBox2_NameNo("Location","定位",this.LocationDesc,"Location","字段ID","名称","定位组件"),this.AddIcon("icon-location-pin","Location"),this.TextBox1_Name("FrmHtml","大块说明",this.HelpUn,"FrmHtml","大块说明"),this.AddIcon("icon-doc","FrmHtml"),this.TextBox2_NameNo("ExtMap","地图",this.MapDesc,"Map","字段ID","名称","地图"),this.AddIcon("icon-doc","ExtMap"),this.AddGroup("F","OCR组件"),this.TextBox2_NameNo("id_card_upload","身份证",this.HelpUn,"Card","组件ID","组件名称","身份证1"),this.AddIcon("icon-user","id_card_upload"),this.TextBox2_NameNo("Invoice","发票",this.HelpUn,"Card","组件ID","组件名称","发票"),this.AddIcon("icon-layers","Invoice"),this.AddGroup("B","流程组件"),this.TextBox2_NameNo("FlowRefLink","关联流程",this.HelpUn,"FlowRefLink","字段ID","名称","关联流程"),this.AddIcon("icon-share","FlowRefLink"),this.TextBox2_NameNo("BillRefLink","关联单据",this.HelpUn,"BillRefLink","字段ID","名称","关联单据"),this.AddIcon("icon-share","BillRefLink"),this.AddBlank("GovAth","公文正文",this.HelpUn),this.AddIcon("icon-doc","GovAth"),this.AddBlank("WordNum","公文字号",this.HelpUn),this.AddIcon("icon-star","WordNum"),this.AddBlank("FlowBBS","流程评论",this.HelpUn),this.AddIcon("icon-bubble","FlowBBS"),this.TextBox2_NameNo("SignCheck","签批组件",this.HelpUn,"SC","字段ID","名称","签批组件"),this.AddIcon("icon-check","SignCheck"),this.AddGroup("C","实验中"),this.TextBox3_NameNoNote("img","图片",this.HelpUn,"Img","组件ID","图片名称","图片URL","通用图片"),this.AddIcon("icon-picture","img"),this.TextBox2_NameNo("Progress","流程进度图",this.HelpUn,"Progress","字段ID","名称","流程进度图"),this.AddIcon("icon-check","Progress"),this.TextBox2_NameNo("iframe","框架",this.FieldAth,"Iframe","字段ID","名称","我的框架"),this.AddIcon("icon-loop","iframe")}GenerSorts(){return p(this,null,function*(){const i=this.RequestVal("FrmID"),c=new M;return yield c.Retrieve("FrmID",i,"Idx"),c.filter(r=>r.CtrlType===""||r.CtrlType==="Attr").map(r=>({No:r.PKVal,Name:r.Lab}))})}Save_TextBox_X(i,c,r,o,A){return p(this,null,function*(){if(i==="ExtScore"||i==="ExtMap"||i=="FlowRefLink"||i=="BillRefLink"||i=="FrmBtn"||i=="FrmLink"||i=="SignCheck"){const n=this.RequestVal("FrmID"),t=new B;if(t.GroupID=c,t.FK_MapData=n,t.MyPK=n+"_"+o,yield t.IsExits())return new I(h.Message,"字段ID=["+o+"]已经存在");t.KeyOfEn=o,t.Name=r;let e=0;i=="ExtScore"&&(e=101),i=="ExtMap"&&(e=4),i=="FlowRefLink"&&(e=200),i=="BillRefLink"&&(e=201),i=="SignCheck"&&(e=d.SignCheck),i=="FrmBtn"&&(e=d.Btn),i=="FrmLink"&&(e=d.HyperLink);const x="TS.FrmUI.SelfCommonent."+i;t.UIContralType=e,t.SetPara("CtrlType",i),t.SetPara("EnName",x),yield t.Insert();const f=F.UrlEn(x,t.MyPK);return new I(h.GoToUrl,f)}if(i==="FlowBBS"||i=="FrmHtml"||i=="Location"||i=="WordNum"||i=="GovAth"){const n=this.RequestVal("FrmID"),t=new B;if(t.GroupID=c,t.Name=this.GetPageName(i),t.FK_MapData=n,t.MyPK=n+"_"+i,yield t.IsExits())return new I(h.Message,"字段ID=["+i+"]已经存在");t.KeyOfEn=i,t.Name=r;let e=0;if(i=="FlowBBS"&&(e=d.FlowBBS),i=="FrmHtml"&&(e=d.FrmHtml),i=="Location"&&(e=d.Location),i=="WordNum"&&(e=d.WordNum),i=="GovAth"&&(e=d.GovDocFile,t.Name="公文正文"),i=="FrmHtml"){const D="HtmlText_"+t.MyPK,l=new y("BP.Sys.MapExt");l.setPKVal(D),(yield l.RetrieveFromDBSources())==0&&(l.MyPK=D,l.FK_MapData=n,l.ExtType="HtmlText",l.ExtModel="HtmlText",l.AttrOfOper=i,yield l.Insert());const u="TS.FrmUI.SelfCommonent."+i;t.UIContralType=e,t.SetPara("CtrlType",i),t.SetPara("EnName",u),yield t.Insert();const S=F.UrlEn(u,D);return new I(h.GoToUrl,S)}const x="TS.FrmUI.SelfCommonent."+i;t.UIContralType=e,t.SetPara("CtrlType",i),t.SetPara("EnName",x),yield t.Insert();const f=F.UrlEn(x,t.MyPK);return new I(h.GoToUrl,f)}if(i==="id_card_upload"){const n=this.RequestVal("FrmID"),t=new B;if(t.GroupID=c,t.FK_MapData=n,t.MyPK=n+"_"+o,yield t.IsExits())return new I(h.Message,"字段ID=["+i+"]已经存在");t.KeyOfEn=o,t.Name=r,t.UIContralType=13,t.UIIsEnable=0,yield t.Insert(),t.MyPK=n+"_"+o+"Name",t.KeyOfEn=o+"Name",t.Name="姓名",yield t.Insert(),t.MyPK=n+"_"+o+"Address",t.KeyOfEn=o+"Address",t.ColSpan=3,t.Name="地址",yield t.Insert();const e=F.UrlEn("TS.FrmUI.MapAttrString",n+"_"+o);return new I(h.GoToUrl,e)}})}}export{W as GPN_ComponentField};
