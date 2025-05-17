var A=Object.defineProperty;var F=(u,c,t)=>c in u?A(u,c,{enumerable:!0,configurable:!0,writable:!0,value:t}):u[c]=t;var p=(u,c,t)=>F(u,typeof c!="symbol"?c+"":c,t);var h=(u,c,t)=>new Promise((d,o)=>{var I=i=>{try{n(t.next(i))}catch(E){o(E)}},w=i=>{try{n(t.throw(i))}catch(E){o(E)}},n=i=>i.done?d(i.value):Promise.resolve(i.value).then(I,w);n((t=t.apply(u,c)).next())});import{MapAttr as f}from"./MapAttr-B1mxD3vP.js";import{b5 as x,aM as K,G as y,l,D as m,aF as D}from"./entry/index-C6uBgOW5-1730430676707.js";import{GPN_NewDDL as P}from"./GPN_NewDDL-HobTM8q_.js";import{SFTables as U,SFTable as N}from"./SFTable-BlM1UBse.js";import{SysEnumMain as _}from"./SysEnumMain-CBhfewK0.js";import{FrmAttachment as O}from"./FrmAttachment-D6absTvH.js";import{GroupFields as B}from"./GroupField-DV5A1BJs.js";import{MapDtl as v}from"./MapDtl-C1-EwBHu.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./GloComm-CmAl8MpM.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./SysEnum-DlgPT0C2.js";import"./EntityOID-DvdPWQGp.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";class ae extends x{constructor(){super("GPN_DtlField");p(this,"HelpString",`
  #### 帮助
   - 文本类型的字段.
   - 比如:姓名、编号、地址、电话、邮件.
  `);p(this,"HelpInt",`
  #### 帮助
   - 整数类型数据.
  `);p(this,"HelpNumber",`
  #### 帮助
  - 数值类型数据.
  `);p(this,"HelpEnumDDL",`
    #### 帮助
    - 枚举类型数据: 枚举值,枚举标签; 
    `);p(this,"HelpEnumNew",`
        #### 帮助
        - 填写格式: 枚举值,枚举标签; 
        - 例如: ty,团员;dy=党员;qz,群众;
        - 系统解析为: ty是团员, dy是党员, qz是群众.
      
        #### 数据存储.
        - string类型的枚举也称为标记枚举,字母存储一个列,标签存储一个列.
        - 在表单里字段是abc,那系统就会自动创建一个影子字段 abcT.
        - abc字段存储的是标记, abcT存储的是标签.
        `);p(this,"Blank",`
  #### 帮助
  - 空白的字段: 用于加载表单的时候，他的数据源是通过，由参数的字典获得的.
  - 比如：表单里由，片区、省份、地市、区县四个下拉框字段. 当表单加载的时候，在没有确定片区其他的三个字段都无法确定值。
  - 省份、地区、区县就需要绑定空白数据源外键.
  - 使用级联关系，把其他的字段数据实现数据级联查询.
  `);p(this,"HelpBoolean",`
    #### 帮助
    - 填写格式:开关类型数据.
    `);p(this,"HelpJE",`
  #### 帮助
  - 金额类型数据; 
  `);p(this,"HelpTime",`
  #### 帮助
  - 时间类型数据.
  `);p(this,"HelpDT",`
  #### 帮助
  - 日期类型数据; 
  `);p(this,"FieldAth",`
  #### 帮助
  - 字段附件，附件以字段名的形式在页面中显示; 
  #### 图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/SFTable/Img/Ath1.png "屏幕截图.png") 
  #### 数据存储
  - 附件的默认保存在web服务器上。
  - 可以保存到ftp服务器上, ftp的服务器的连接配置在全局的配置文件中。
  - 如果需要保存到数据库，就需要考虑数据库的存储与备份的问题，文件将会存储在 Sys_FrmAttachmentDB 表中。
 
    `)}Init(){this.PageTitle="新建从表字段",this.ForEntityClassID="",this.AddGroup("Nurel","基本字段"),this.TextBox2_NameNo("String","文本字段",this.HelpString,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuwenbenkuang","String"),this.TextBox2_NameNo("Int","整数",this.HelpInt,"","字段名","中文名",""),this.AddIcon("iconfont icon-zhengshu","Int"),this.TextBox2_NameNo("Number","数值",this.HelpNumber,"","字段名","中文名",""),this.AddIcon("iconfont icon-ziduanleixing-zhengshu","Number"),this.TextBox2_NameNo("JE","金额",this.HelpJE,"","字段名","中文名",""),this.AddIcon("iconfont icon-yifabupiaoju-renminbi-xi","JE"),this.TextBox2_NameNo("Time","日期时间",this.HelpTime,"","字段名","中文名",""),this.AddIcon("iconfont icon-shijian1","Time"),this.TextBox2_NameNo("DT","日期",this.HelpDT,"","字段名","中文名",""),this.AddIcon("iconfont icon-riqiqishu","DT"),this.TextBox2_NameNo("Boolean","开关",this.HelpBoolean,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuxuankuang","Boolean"),this.TextBox2_NameNo("write","写字板",this.FieldAth,"Ath","字段ID","写字板名称","写字板"),this.AddIcon("iconfont icon-xiezi","write"),this.AddGroup("Enum","枚举字段");const t=K.SQLEnumMain;this.SelectItemsByList("SelectedEnum","新建枚举字段",P.SelectedEnum,!1,t),this.TextBox1_Name("SelectedEnum.FieldName","输入字段ID",this.HelpEnumDDL,"字段ID",()=>this.RequestVal("tb1","SelectedEnum"),"英文字母或者下划线开头."),this.AddFunction("AdminEnum","枚举库维护",this.AdminEnum),this.AddGroup("FK","外键字段"),this.SelectItemsByGroupList("SelectedDict","新建外键字段",this.HelpEnumDDL,!1,K.srcDBSrc,K.SQLSFTable),this.TextBox1_Name("SelectedDict.Name","输入字段ID",this.HelpEnumDDL,"字段ID",()=>this.RequestVal("tb1","SelectedDict"),"英文字母或者下划线开头."),this.AddFunction("AdminDict","外键库维护",this.AdminDict),this.AddGroup("Component","组件"),this.TextBox2_NameNo("AthField","字段附件",this.FieldAth,"Ath","字段ID","附件名称","我的附件"),this.AddIcon("iconfont icon-attach","AthField")}AdminEnum(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SysEnumMain";return new y(l.GoToUrl,t)}AdminDict(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SFTable";return new y(l.GoToUrl,t)}GenerSorts(){return h(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,d,o,I,w){return h(this,null,function*(){const n=this.RequestVal("FrmID"),i=new B;yield i.Retrieve("FrmID",n,"Idx");const E=i.filter(e=>e.CtrlType===null||e.CtrlType===""),S=E.length===0?0:E[0].OID;if(t=="Blank")return yield U.Init_Blank(),this.InitDDL(I,o,"Blank");if(t==="String"||t==="Int"||t==="Number"||t==="DT"||t==="Boolean"||t==="JE"||t==="Time"||t==="write"){const e=new f;if(e.MyPK=n+"_"+I,e.FK_MapData=n,e.GroupID=S,e.KeyOfEn=I,(yield e.IsExits())==!0)return new y(l.Error,"字段已经存在");e.KeyOfEn=I,e.Name=o;let a="";return t==="String"&&(e.MyDataType=m.AppString,e.MaxLen=50,a="TS.FrmUI.MapAttrString",e.SetPara("EnName",a),yield e.Insert()),t==="Int"&&(e.MyDataType=m.AppInt,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="Number"&&(e.MyDataType=m.AppFloat,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="JE"&&(e.MyDataType=m.AppMoney,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="DT"&&(e.MyDataType=m.AppDate,e.IsSupperText="0",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Time"&&(e.MyDataType=m.AppDateTime,e.IsSupperText="1",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Boolean"&&(e.MyDataType=m.AppBoolean,a="TS.FrmUI.MapAttrBoolean",e.SetPara("EnName",a),yield e.Insert()),t==="write"&&(e.MyDataType=m.AppString,e.UIContralType=D.HandWriting,a="TS.FrmUI.FrmHandWriting",e.SetPara("EnName",a),yield e.Insert()),new y(l.Message,"创建成功")}if(t==="Invoice"){const e=new f;if(e.GroupID=S,e.FK_MapData=n,e.SetPara("GroupName","Invoice"),e.DataType=m.AppString,e.UIIsEnable=0,e.Name="发票代码",e.KeyOfEn="InvoiceID",e.MyPK=n+"_"+e.KeyOfEn,yield e.IsExits())return new y(l.Message,"发票已经存在.");yield e.Insert(),e.Name="发票号码",e.KeyOfEn="InvoiceCode",e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.Name="开票日期",e.KeyOfEn="InvoiceRelDT",e.DataType=m.AppDate,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.Name="发票类型",e.KeyOfEn="InvoiceTypeStr",e.DataType=m.AppString,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.Name="发票金额",e.KeyOfEn="InvoiceJE",e.DataType=m.AppMoney,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.Name="税额",e.KeyOfEn="InvoiceTaxJE",e.DataType=m.AppMoney,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.Name="税率",e.KeyOfEn="InvoiceTaxRate",e.DataType=m.AppMoney,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.Name="购买方名称",e.KeyOfEn="InvoiceBuyName",e.MaxLen=200,e.MyPK=n+"_"+e.KeyOfEn,e.UIWidth=200,yield e.Insert(),e.KeyOfEn="InvoiceBuyCode",e.Name="购买方ID",e.MaxLen=200,e.MyPK=n+"_"+e.KeyOfEn,e.UIWidth=150,yield e.Insert(),e.KeyOfEn="InvoiceBuyAddr",e.Name="购买方地址电话",e.DataType=m.AppString,e.MaxLen=200,e.UIWidth=200,yield e.Insert(),e.KeyOfEn="InvoiceBuyBankInfo",e.Name="购买方开户行及账号",e.DataType=m.AppString,e.MaxLen=200,e.UIWidth=200,yield e.Insert(),e.Name="销售方名称",e.KeyOfEn="InvoiceSaleName",e.MaxLen=200,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.KeyOfEn="InvoiceSaleCode",e.Name="销售方ID",e.MaxLen=150,e.UIWidth=150,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.KeyOfEn="InvoiceSaleAddr",e.Name="销售方地址电话",e.UIWidth=200,e.MaxLen=200,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert(),e.KeyOfEn="InvoiceSaleBankInfo",e.Name="销售方开户行及账号",e.MaxLen=200,e.UIWidth=200,e.MyPK=n+"_"+e.KeyOfEn,yield e.Insert();const a=new v(e.FK_MapData);yield a.Retrieve(),a.SetPara("IsInvoice",1),a.IsInsert=0,yield a.Update()}if(t==="SelectedEnum.FieldName"){if(!o)return;const a=this.RequestVal("tb1","SelectedEnum"),T=this.RequestVal("tb2","SelectedEnum"),r=new _(a);r.No=a,yield r.Retrieve(),r.EnumKey===""&&(r.EnumKey=o);const s=new f;if(s.MyPK=n+"_"+o,(yield s.IsExits())==!0)return new y(l.Error,"字段在表单已经存在"+o);s.GroupID=S,s.Name=T,s.KeyOfEn=o,s.FK_MapData=n,s.UIVisible=1,s.UIIsEnable=1,s.LGType=1,s.MyDataType=r.EnumType===0?2:1,s.SetPara("RBShowModel",3),s.UIContralType=1,s.UIBindKey=r.EnumKey,yield s.Insert();const M="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrEnum&PKVal="+s.MyPK;return new y(l.GoToUrl,M)}if(t==="SelectedDict.Name"){if(!o)return;const a=this.RequestVal("tb1","SelectedDict"),T=new N(a);T.No=a,yield T.Retrieve();const r=new f;if(r.MyPK=n+"_"+o,(yield r.IsExits())==!0)return new y(l.Error,"字段在表单已经存在"+o);r.Name=T.Name,r.KeyOfEn=o,r.FK_MapData=n,r.GroupID=S,r.UIVisible=1,r.UIIsEnable=1,r.LGType=0,r.MyDataType=1,r.UIContralType=D.DDL,r.UIBindKey=T.No,r.SetPara("SrcType",T.DBSrcType),yield r.Insert();const s=r.MyPK;r.UIVisible=0,r.UIContralType=D.TB,r.MyPK=n+"_"+o+"T",r.KeyOfEn=o+"T",r.Name=r.Name+"T",yield r.Insert();const M="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+s;return new y(l.GoToUrl,M)}if(t==="AthField"){const e=new f;if(e.FK_MapData=n,e.MyPK=n+"_"+I,e.KeyOfEn=I,e.Name=o,e.GroupID=S,(yield e.IsExits())==!0)return new y(l.Error,"附件ID["+e.MyPK+"]已存在");const a=new O;a.MyPK=e.MyPK,(yield e.IsExits())==!1&&(a.FK_MapData=n,a.NoOfObj=I,a.Name=o,a.IsDtlAth=1,a.SetPara("IsDtlAth","1"),e.UploadType=1,yield a.Insert()),e.UIContralType=D.AthShow,e.SetPara("EnName","TS.FrmUI.FrmAttachmentExt"),yield e.Insert()}})}InitDDL(t,d,o){return h(this,null,function*(){const I=this.RequestVal("FrmID");if(!t)return;const n=new N(o);n.No=o,yield n.Retrieve();const i=new f;if(i.MyPK=I+"_"+t,(yield i.IsExits())==!0)return new y(l.Error,"字段在表单已经存在"+t);d?i.Name=d:i.Name=n.Name,i.KeyOfEn=t,i.FK_MapData=I,i.UIVisible=1,i.UIIsEnable=1,i.LGType=0,i.MyDataType=1,i.UIContralType=D.DDL,i.UIBindKey=n.No,i.SetPara("SrcType",n.DBSrcType),yield i.Insert(),i.UIVisible=0,i.MyPK=I+"_"+t+"T",i.KeyOfEn=t+"T",i.UIContralType=D.TB,yield i.Insert();const E="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+i.MyPK;return new y(l.GoToUrl,E)})}}export{ae as GPN_DtlField};
