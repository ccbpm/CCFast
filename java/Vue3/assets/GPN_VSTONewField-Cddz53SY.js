var B=Object.defineProperty;var x=(D,c,e)=>c in D?B(D,c,{enumerable:!0,configurable:!0,writable:!0,value:e}):D[c]=e;var l=(D,c,e)=>x(D,typeof c!="symbol"?c+"":c,e);var d=(D,c,e)=>new Promise((I,r)=>{var m=n=>{try{o(e.next(n))}catch(y){r(y)}},N=n=>{try{o(e.throw(n))}catch(y){r(y)}},o=n=>n.done?I(n.value):Promise.resolve(n.value).then(m,N);o((e=e.apply(D,c)).next())});import{GloComm as E}from"./GloComm-CmAl8MpM.js";import{GPN_NewDDL as M}from"./GPN_NewDDL-HobTM8q_.js";import{MapAttr as f}from"./MapAttr-B1mxD3vP.js";import{MapDtls as _,MapDtl as P}from"./MapDtl-C1-EwBHu.js";import{SFTables as G,SFTable as A}from"./SFTable-BlM1UBse.js";import{SysEnumMain as K}from"./SysEnumMain-CBhfewK0.js";import{b5 as b,aM as F,G as p,l as u,D as S,aF as h,B as L}from"./entry/index-C6uBgOW5-1730430676707.js";import{GroupFields as H}from"./GroupField-DV5A1BJs.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./Events-D9tOL1Ad.js";import"./SysEnum-DlgPT0C2.js";import"./EntityOID-DvdPWQGp.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";class ie extends b{constructor(){super("GPN_VSTONewField");l(this,"HelpString",`
  #### 帮助
   - 文本类型的字段.
   - 比如:姓名、编号、地址、电话、邮件.
  `);l(this,"HelpInt",`
  #### 帮助
   - 整数类型数据.
  `);l(this,"HelpNumber",`
  #### 帮助
  - 数值类型数据.
  `);l(this,"HelpEnumDDL",`
    #### 帮助
    - 枚举类型数据: 枚举值,枚举标签; 
    `);l(this,"HelpEnumNew",`
        #### 帮助
        - 填写格式: 枚举值,枚举标签; 
        - 例如: ty,团员;dy=党员;qz,群众;
        - 系统解析为: ty是团员, dy是党员, qz是群众.
      
        #### 数据存储.
        - string类型的枚举也称为标记枚举,字母存储一个列,标签存储一个列.
        - 在表单里字段是abc,那系统就会自动创建一个影子字段 abcT.
        - abc字段存储的是标记, abcT存储的是标签.
        `);l(this,"Blank",`
  #### 帮助
  - 空白的字段: 用于加载表单的时候，他的数据源是通过，由参数的字典获得的.
  - 比如：表单里由，片区、省份、地市、区县四个下拉框字段. 当表单加载的时候，在没有确定片区其他的三个字段都无法确定值。
  - 省份、地区、区县就需要绑定空白数据源外键.
  - 使用级联关系，把其他的字段数据实现数据级联查询.
  `);l(this,"HelpBoolean",`
    #### 帮助
    - 填写格式:开关类型数据.
    `);l(this,"HelpJE",`
  #### 帮助
  - 金额类型数据; 
  `);l(this,"HelpTime",`
  #### 帮助
  - 时间类型数据.
  `);l(this,"HelpDT",`
  #### 帮助
  - 日期类型数据; 
  `);l(this,"FieldAth",`
  #### 帮助
  - 字段附件，附件以字段名的形式在页面中显示; 
  #### 图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/SFTable/Img/Ath1.png "屏幕截图.png") 
  #### 数据存储
  - 附件的默认保存在web服务器上。
  - 可以保存到ftp服务器上, ftp的服务器的连接配置在全局的配置文件中。
  - 如果需要保存到数据库，就需要考虑数据库的存储与备份的问题，文件将会存储在 Sys_FrmAttachmentDB 表中。
 
    `)}Init(){this.PageTitle="新建从表字段",this.ForEntityClassID="",this.AddGroup("Nurel","基本字段"),this.TextBox2_NameNo("String","文本字段",this.HelpString,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuwenbenkuang","String"),this.TextBox2_NameNo("Int","整数",this.HelpInt,"","字段名","中文名",""),this.AddIcon("iconfont icon-zhengshu","Int"),this.TextBox2_NameNo("Number","数值",this.HelpNumber,"","字段名","中文名",""),this.AddIcon("iconfont icon-ziduanleixing-zhengshu","Number"),this.TextBox2_NameNo("JE","金额",this.HelpJE,"","字段名","中文名",""),this.AddIcon("iconfont icon-yifabupiaoju-renminbi-xi","JE"),this.TextBox2_NameNo("Time","日期时间",this.HelpTime,"","字段名","中文名",""),this.AddIcon("iconfont icon-shijian1","Time"),this.TextBox2_NameNo("DT","日期",this.HelpDT,"","字段名","中文名",""),this.AddIcon("iconfont icon-riqiqishu","DT"),this.TextBox2_NameNo("Boolean","开关",this.HelpBoolean,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuxuankuang","Boolean"),this.AddGroup("Enum","枚举字段");const e=F.SQLEnumMain;this.SelectItemsByList("SelectedEnum","新建枚举字段",M.SelectedEnum,!1,e),this.TextBox1_Name("SelectedEnum.FieldName","输入字段ID",M.SelectedEnum_FieldName,"字段ID",()=>this.RequestVal("tb1","SelectedEnum"),"英文字母或者下划线开头."),this.AddFunction("AdminEnum","枚举库维护",this.AdminEnum),this.AddGroup("FK","外键字段"),this.SelectItemsByList("SelectedDict","新建外键字段",this.HelpEnumDDL,!1,F.SQLSFTable),this.TextBox1_Name("SelectedDict.Name","输入字段ID",this.HelpEnumDDL,"字段ID",()=>this.RequestVal("tb1","SelectedDict"),"英文字母或者下划线开头."),this.AddFunction("AdminDict","外键库维护",this.AdminDict),this.AddGroup("Component","组件"),this.TextBox2_NameNo("Dtl","从表",this.FieldAth,"Dtl","从表ID","从表名称","我的从表"),this.AddIcon("iconfont icon-attach","Dtl")}AdminEnum(){const e=E.UrlSearch("TS.FrmUI.SysEnumMain");return new p(u.GoToUrl,e)}AdminDict(){const e=E.UrlSearch("TS.FrmUI.SFTable");return new p(u.GoToUrl,e)}GenerSorts(){return d(this,null,function*(){const e=this.RequestVal("FrmID"),I=new _;yield I.Retrieve("FK_MapData",e);const r=[{Name:"主表:"+e,No:e}];for(const m of I)r.push({Name:m.Name+":"+m.No,No:m.No});return Promise.resolve(r)})}Save_TextBox_X(e,I,r,m,N){return d(this,null,function*(){const o=I,n=new H;yield n.Retrieve("FrmID",o,"Idx");const y=n.filter(t=>t.CtrlType===null||t.CtrlType===""),w=y.length===0?0:y[0].OID;if(e=="Blank")return yield G.Init_Blank(),this.InitDDL(m,r,"Blank");if(e==="String"||e==="Int"||e==="Number"||e==="DT"||e==="Boolean"||e==="JE"||e==="Time"){const t=new f;if(t.MyPK=o+"_"+m,t.FK_MapData=o,t.GroupID=w,t.KeyOfEn=m,(yield t.IsExits())==!0)return new p(u.Error,"字段已经存在");t.KeyOfEn=m,t.Name=r;let i="";return e==="String"&&(t.MyDataType=S.AppString,t.MaxLen=50,i="TS.FrmUI.MapAttrString",t.SetPara("EnName",i),yield t.Insert()),e==="Int"&&(t.MyDataType=S.AppInt,i="TS.FrmUI.MapAttrNum",t.SetPara("EnName",i),yield t.Insert()),e==="Number"&&(t.MyDataType=S.AppFloat,i="TS.FrmUI.MapAttrNum",t.SetPara("EnName",i),yield t.Insert()),e==="JE"&&(t.MyDataType=S.AppMoney,i="TS.FrmUI.MapAttrNum",t.SetPara("EnName",i),yield t.Insert()),e==="DT"&&(t.MyDataType=S.AppDate,t.IsSupperText="0",i="TS.FrmUI.MapAttrDT",t.SetPara("EnName",i),yield t.Insert()),e==="Time"&&(t.MyDataType=S.AppDateTime,t.IsSupperText="1",i="TS.FrmUI.MapAttrDT",t.SetPara("EnName",i),yield t.Insert()),e==="Boolean"&&(t.MyDataType=S.AppBoolean,i="TS.FrmUI.MapAttrBoolean",t.SetPara("EnName",i),yield t.Insert()),new p(u.Message,"创建成功")}if(e==="SelectedEnum.FieldName"){if(!r)return;const i=this.RequestVal("tb1","SelectedEnum"),T=this.RequestVal("tb2","SelectedEnum"),a=new K(i);a.No=i,yield a.Retrieve(),a.EnumKey===""&&(a.EnumKey=r);const s=new f;if(s.MyPK=o+"_"+r,(yield s.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+r);s.GroupID=w,s.Name=T,s.KeyOfEn=r,s.FK_MapData=o,s.UIVisible=1,s.UIIsEnable=1,s.LGType=1,s.MyDataType=a.EnumType===0?2:1,s.SetPara("RBShowModel",3),s.UIContralType=1,s.UIBindKey=a.EnumKey,yield s.Insert();const U=E.UrlEn("TS.FrmUI.MapAttrEnum",s.MyPK);return new p(u.GoToUrl,U)}if(e==="SelectedDict.Name"){if(!r)return;const i=this.RequestVal("tb1","SelectedDict"),T=new A(i);T.No=i,yield T.Retrieve();const a=new f;if(a.MyPK=o+"_"+r,(yield a.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+r);a.Name=T.Name,a.KeyOfEn=r,a.FK_MapData=o,a.GroupID=w,a.UIVisible=1,a.UIIsEnable=1,a.LGType=0,a.MyDataType=1,a.UIContralType=h.DDL,a.UIBindKey=T.No,a.SetPara("SrcType",T.DBSrcType),yield a.Insert(),a.UIVisible=0,a.UIContralType=h.TB,a.MyPK=o+"_"+r+"T",a.KeyOfEn=r+"T",a.Name=a.Name+"T",yield a.Insert();const s=E.UrlEn("TS.FrmUI.MapAttrSFSQL",a.MyPK);return new p(u.GoToUrl,s)}if(e==="Dtl"){const t=new P;if(t.No=o+m,(yield t.IsExits())==!0)return new p(u.Error,"从表ID["+t.No+"]已存在");const i=new L("BP.Sys.MapDtl");i.No=t.No,i.Name=r,i.FK_MapData=o,yield i.Insert(),i.DoMethodReturnString("IntMapAttrs");const T=E.UrlEn("TS.Frm.MapDtlExt",t.No);return new p(u.GoToUrl,T)}})}InitDDL(e,I,r){return d(this,null,function*(){const m=this.RequestVal("FrmID");if(!e)return;const o=new A(r);o.No=r,yield o.Retrieve();const n=new f;if(n.MyPK=m+"_"+e,(yield n.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+e);I?n.Name=I:n.Name=o.Name,n.KeyOfEn=e,n.FK_MapData=m,n.UIVisible=1,n.UIIsEnable=1,n.LGType=0,n.MyDataType=1,n.UIContralType=h.DDL,n.UIBindKey=o.No,n.SetPara("SrcType",o.DBSrcType),yield n.Insert(),n.UIVisible=0,n.MyPK=m+"_"+e+"T",n.KeyOfEn=e+"T",n.UIContralType=h.TB,yield n.Insert();const y="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+n.MyPK;return new p(u.GoToUrl,y)})}}export{ie as GPN_VSTONewField};
