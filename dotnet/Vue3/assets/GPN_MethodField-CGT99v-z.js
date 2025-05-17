var w=Object.defineProperty;var F=(u,T,t)=>T in u?w(u,T,{enumerable:!0,configurable:!0,writable:!0,value:t}):u[T]=t;var o=(u,T,t)=>F(u,typeof T!="symbol"?T+"":T,t);var c=(u,T,t)=>new Promise((E,n)=>{var s=e=>{try{i(t.next(e))}catch(a){n(a)}},f=e=>{try{i(t.throw(e))}catch(a){n(a)}},i=e=>e.done?E(e.value):Promise.resolve(e.value).then(s,f);i((t=t.apply(u,T)).next())});import{b5 as N,aM as M,G as p,l,D as y,aF as D}from"./entry/index-C6uBgOW5-1730430676707.js";import{FrmAttachment as A}from"./FrmAttachment-D6absTvH.js";import{GPN_NewDDL as d}from"./GPN_NewDDL-HobTM8q_.js";import{MapAttr as S}from"./MapAttr-B1mxD3vP.js";import{SFTable as h}from"./SFTable-BlM1UBse.js";import{SysEnumMain as P}from"./SysEnumMain-CBhfewK0.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./GloComm-CmAl8MpM.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./Events-D9tOL1Ad.js";import"./SysEnum-DlgPT0C2.js";class W extends N{constructor(){super("GPN_MethodField");o(this,"HelpString",`
  #### 帮助
   - 文本类型的字段.
   - 比如:姓名、编号、地址、电话、邮件.
  `);o(this,"HelpInt",`
  #### 帮助
   - 整数类型数据.
  `);o(this,"HelpNumber",`
  #### 帮助
  - 数值类型数据.
  `);o(this,"HelpEnumDDL",`
    #### 帮助
    - 枚举类型数据: 枚举值,枚举标签; 
    `);o(this,"HelpEnumNew",`
        #### 帮助
        - 填写格式: 枚举值,枚举标签; 
        - 例如: ty,团员;dy=党员;qz,群众;
        - 系统解析为: ty是团员, dy是党员, qz是群众.
      
        #### 数据存储.
        - string类型的枚举也称为标记枚举,字母存储一个列,标签存储一个列.
        - 在表单里字段是abc,那系统就会自动创建一个影子字段 abcT.
        - abc字段存储的是标记, abcT存储的是标签.
        `);o(this,"Blank",`
  #### 帮助
  - 空白的字段: 用于加载表单的时候，他的数据源是通过，由参数的字典获得的.
  - 比如：表单里由，片区、省份、地市、区县四个下拉框字段. 当表单加载的时候，在没有确定片区其他的三个字段都无法确定值。
  - 省份、地区、区县就需要绑定空白数据源外键.
  - 使用级联关系，把其他的字段数据实现数据级联查询.
  `);o(this,"HelpBoolean",`
    #### 帮助
    - 填写格式:开关类型数据.
    `);o(this,"HelpJE",`
  #### 帮助
  - 金额类型数据; 
  `);o(this,"HelpTime",`
  #### 帮助
  - 时间类型数据.
  `);o(this,"HelpDT",`
  #### 帮助
  - 日期类型数据; 
  `);o(this,"FieldAth",`
  #### 帮助
  - 字段附件，附件以字段名的形式在页面中显示; 
  #### 图例
  ![输入图片说明](/resource/WF/Admin/FrmLogic/SFTable/Img/Ath1.png "屏幕截图.png") 
  #### 数据存储
  - 附件的默认保存在web服务器上。
  - 可以保存到ftp服务器上, ftp的服务器的连接配置在全局的配置文件中。
  - 如果需要保存到数据库，就需要考虑数据库的存储与备份的问题，文件将会存储在 Sys_FrmAttachmentDB 表中。
 
    `);this.ForEntityClassID="TS.CCBill.MethodFuncPara"}Init(){this.PageTitle="新建参数",this.ForEntityClassID="TS.CCBill.MethodFuncPara",this.AddGroup("Nurel","参数"),this.TextBox2_NameNo("String","文本",this.HelpString,"Str","字段名","中文名",""),this.TextBox2_NameNo("Int","整数",this.HelpInt,"Int","字段名","中文名",""),this.TextBox2_NameNo("Number","数值",this.HelpNumber,"Num","字段名","中文名",""),this.TextBox2_NameNo("JE","金额",this.HelpJE,"JE","字段名","中文名",""),this.TextBox2_NameNo("Time","日期时间",this.HelpTime,"DT","字段名","中文名",""),this.TextBox2_NameNo("DT","日期",this.HelpDT,"DT","字段名","中文名",""),this.TextBox2_NameNo("Boolean","开关",this.HelpBoolean,"Is","字段名","中文名",""),this.AddGroup("Enum","枚举字段");const t=M.SQLEnumMain;this.SelectItemsByList("SelectedEnum","新建枚举字段",d.SelectedEnum,!1,t),this.AddFunction("AdminEnum","枚举库维护",this.AdminEnum),this.AddGroup("FK","外键字段"),this.SelectItemsByList("SelectedDict","新建外键字段",this.HelpEnumDDL,!1,M.SQLSFTable),this.TextBox1_Name("SelectedDict.Name","输入字段ID",this.HelpEnumDDL,"字段ID",()=>this.RequestVal("tb1","SelectedDict"),"英文字母或者下划线开头."),this.AddFunction("AdminDict","外键库维护",this.AdminDict)}AdminEnum(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SysEnumMain";return new p(l.GoToUrl,t)}AdminDict(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SFTable";return new p(l.GoToUrl,t)}GenerSorts(){return c(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,E,n,s,f){return c(this,null,function*(){const i=this.RefPKVal;if(t==="String"||t==="Int"||t==="Number"||t==="DT"||t==="Boolean"||t==="JE"||t==="Time"){const e=new S;if(e.MyPK=i+"_"+s,e.FK_MapData=i,e.GroupID=0,e.KeyOfEn=s,(yield e.IsExits())==!0)return new p(l.Error,"字段已经存在");e.KeyOfEn=s,e.Name=n;let a="";return t==="String"&&(e.MyDataType=y.AppString,e.MaxLen=50,a="TS.FrmUI.MapAttrString",e.SetPara("EnName",a),yield e.Insert()),t==="Int"&&(e.MyDataType=y.AppInt,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="Number"&&(e.MyDataType=y.AppFloat,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="JE"&&(e.MyDataType=y.AppMoney,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="DT"&&(e.MyDataType=y.AppDate,e.IsSupperText="0",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Time"&&(e.MyDataType=y.AppDateTime,e.IsSupperText="1",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Boolean"&&(e.MyDataType=y.AppBoolean,a="TS.FrmUI.MapAttrBoolean",e.SetPara("EnName",a),yield e.Insert()),new p(l.Message,"创建成功")}if(t==="SelectedEnum"){if(!n)return;const a=n,I=s,r=new P(a);r.No=a,yield r.Retrieve(),r.EnumKey===""&&(r.EnumKey=n);const m=new S;return m.MyPK=i+"_"+n,(yield m.IsExits())==!0?new p(l.Error,"字段在表单已经存在"+n):(m.GroupID=0,m.Name=I,m.KeyOfEn=n,m.FK_MapData=i,m.UIVisible=1,m.UIIsEnable=1,m.LGType=1,m.MyDataType=r.EnumType===0?2:1,m.SetPara("RBShowModel",3),m.UIContralType=1,m.UIBindKey=r.EnumKey,m.DefVal=0,m.SetPara("EnName","TS.FrmUI.MapAttrEnum"),yield m.Insert(),new p(l.Message,"创建成功"))}if(t==="SelectedDict.Name"){if(!n)return;const a=this.RequestVal("tb1","SelectedDict"),I=new h(a);I.No=a,yield I.Retrieve();const r=new S;if(r.MyPK=i+"_"+n,(yield r.IsExits())==!0)return new p(l.Error,"字段在表单已经存在"+n);r.Name=I.Name,r.KeyOfEn=n,r.FK_MapData=i,r.GroupID=groupID,r.UIVisible=1,r.UIIsEnable=1,r.LGType=0,r.MyDataType=1,r.UIContralType=D.DDL,r.UIBindKey=I.No,r.SetPara("SrcType",I.DBSrcType),yield r.Insert(),r.UIVisible=0,r.UIContralType=D.TB,r.MyPK=i+"_"+n+"T",r.KeyOfEn=n+"T",yield r.Insert();const m="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+r.MyPK;return new p(l.GoToUrl,m)}if(t==="AthField"){const e=new S;if(e.FK_MapData=i,e.MyPK=i+"_"+s,e.KeyOfEn=s,e.Name=n,e.GroupID=groupID,(yield e.IsExits())==!0)return new p(l.Error,"附件ID["+e.MyPK+"]已存在");const a=new A;a.MyPK=e.MyPK,(yield e.IsExits())==!1&&(a.FK_MapData=i,a.NoOfObj=s,a.Name=n,a.IsDtlAth=1,e.UploadType=1,yield a.Insert()),e.UIContralType=D.AthShow,e.SetPara("EnName","TS.FrmUI.FrmAttachmentExt"),yield e.Insert()}})}InitDDL(t,E,n){return c(this,null,function*(){const s=this.RequestVal("FrmID");if(!t)return;const i=new h(n);i.No=n,yield i.Retrieve();const e=new S;if(e.MyPK=s+"_"+t,(yield e.IsExits())==!0)return new p(l.Error,"字段在表单已经存在"+t);E?e.Name=E:e.Name=i.Name,e.KeyOfEn=t,e.FK_MapData=s,e.UIVisible=1,e.UIIsEnable=1,e.LGType=0,e.MyDataType=1,e.UIContralType=D.DDL,e.UIBindKey=i.No,e.SetPara("SrcType",i.DBSrcType),yield e.Insert(),e.UIVisible=0,e.MyPK=s+"_"+t+"T",e.KeyOfEn=t+"T",e.UIContralType=D.TB,yield e.Insert();const a="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+e.MyPK;return new p(l.GoToUrl,a)})}}export{W as GPN_MethodField};
