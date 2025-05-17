var w=Object.defineProperty;var N=(u,T,t)=>T in u?w(u,T,{enumerable:!0,configurable:!0,writable:!0,value:t}):u[T]=t;var o=(u,T,t)=>N(u,typeof T!="symbol"?T+"":T,t);var c=(u,T,t)=>new Promise((E,r)=>{var m=e=>{try{i(t.next(e))}catch(a){r(a)}},M=e=>{try{i(t.throw(e))}catch(a){r(a)}},i=e=>e.done?E(e.value):Promise.resolve(e.value).then(m,M);i((t=t.apply(u,T)).next())});import{b9 as F,aM as f,aB as p,aC as l,aL as D,aD as y,aE as h,aG as S}from"./entry/index-M8VErHPE-1727507756861.js";import{FrmAttachment as A}from"./FrmAttachment-DyM-VnR7.js";import{GPN_NewDDL as d}from"./GPN_NewDDL-D4iwPIsH.js";import{SysEnumMain as P}from"./SysEnumMain-Bn9Li5_w.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./GloComm-DZ1gELjv.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./SysEnum-B89JeOjj.js";class v extends F{constructor(){super("GPN_MethodField");o(this,"HelpString",`
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
 
    `);this.ForEntityClassID="TS.CCBill.MethodFuncPara"}Init(){this.PageTitle="新建参数",this.ForEntityClassID="TS.CCBill.MethodFuncPara",this.AddGroup("Nurel","参数"),this.TextBox2_NameNo("String","文本",this.HelpString,"Str","字段名","中文名",""),this.TextBox2_NameNo("Int","整数",this.HelpInt,"Int","字段名","中文名",""),this.TextBox2_NameNo("Number","数值",this.HelpNumber,"Num","字段名","中文名",""),this.TextBox2_NameNo("JE","金额",this.HelpJE,"JE","字段名","中文名",""),this.TextBox2_NameNo("Time","日期时间",this.HelpTime,"DT","字段名","中文名",""),this.TextBox2_NameNo("DT","日期",this.HelpDT,"DT","字段名","中文名",""),this.TextBox2_NameNo("Boolean","开关",this.HelpBoolean,"Is","字段名","中文名",""),this.AddGroup("Enum","枚举字段");const t=f.SQLEnumMain;this.SelectItemsByList("SelectedEnum","新建枚举字段",d.SelectedEnum,!1,t),this.AddFunction("AdminEnum","枚举库维护",this.AdminEnum),this.AddGroup("FK","外键字段"),this.SelectItemsByList("SelectedDict","新建外键字段",this.HelpEnumDDL,!1,f.SQLSFTable),this.TextBox1_Name("SelectedDict.Name","输入字段ID",this.HelpEnumDDL,"字段ID",()=>this.RequestVal("tb1","SelectedDict"),"英文字母或者下划线开头."),this.AddFunction("AdminDict","外键库维护",this.AdminDict)}AdminEnum(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SysEnumMain";return new p(l.GoToUrl,t)}AdminDict(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SFTable";return new p(l.GoToUrl,t)}GenerSorts(){return c(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,E,r,m,M){return c(this,null,function*(){const i=this.RefPKVal;if(t==="String"||t==="Int"||t==="Number"||t==="DT"||t==="Boolean"||t==="JE"||t==="Time"){const e=new D;if(e.MyPK=i+"_"+m,e.FK_MapData=i,e.GroupID=0,e.KeyOfEn=m,(yield e.IsExits())==!0)return new p(l.Error,"字段已经存在");e.KeyOfEn=m,e.Name=r;let a="";return t==="String"&&(e.MyDataType=y.AppString,e.MaxLen=50,a="TS.FrmUI.MapAttrString",e.SetPara("EnName",a),yield e.Insert()),t==="Int"&&(e.MyDataType=y.AppInt,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="Number"&&(e.MyDataType=y.AppFloat,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="JE"&&(e.MyDataType=y.AppMoney,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="DT"&&(e.MyDataType=y.AppDate,e.IsSupperText="0",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Time"&&(e.MyDataType=y.AppDateTime,e.IsSupperText="1",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Boolean"&&(e.MyDataType=y.AppBoolean,a="TS.FrmUI.MapAttrBoolean",e.SetPara("EnName",a),yield e.Insert()),new p(l.Message,"创建成功")}if(t==="SelectedEnum"){if(!r)return;const a=r,I=m,n=new P(a);n.No=a,yield n.Retrieve(),n.EnumKey===""&&(n.EnumKey=r);const s=new D;return s.MyPK=i+"_"+r,(yield s.IsExits())==!0?new p(l.Error,"字段在表单已经存在"+r):(s.GroupID=0,s.Name=I,s.KeyOfEn=r,s.FK_MapData=i,s.UIVisible=1,s.UIIsEnable=1,s.LGType=1,s.MyDataType=n.EnumType===0?2:1,s.SetPara("RBShowModel",3),s.UIContralType=1,s.UIBindKey=n.EnumKey,s.DefVal=0,s.SetPara("EnName","TS.FrmUI.MapAttrEnum"),yield s.Insert(),new p(l.Message,"创建成功"))}if(t==="SelectedDict.Name"){if(!r)return;const a=this.RequestVal("tb1","SelectedDict"),I=new h(a);I.No=a,yield I.Retrieve();const n=new D;if(n.MyPK=i+"_"+r,(yield n.IsExits())==!0)return new p(l.Error,"字段在表单已经存在"+r);n.Name=I.Name,n.KeyOfEn=r,n.FK_MapData=i,n.GroupID=groupID,n.UIVisible=1,n.UIIsEnable=1,n.LGType=0,n.MyDataType=1,n.UIContralType=S.DDL,n.UIBindKey=I.No,n.SetPara("SrcType",I.DBSrcType),yield n.Insert(),n.UIVisible=0,n.UIContralType=S.TB,n.MyPK=i+"_"+r+"T",n.KeyOfEn=r+"T",yield n.Insert();const s="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+n.MyPK;return new p(l.GoToUrl,s)}if(t==="AthField"){const e=new D;if(e.FK_MapData=i,e.MyPK=i+"_"+m,e.KeyOfEn=m,e.Name=r,e.GroupID=groupID,(yield e.IsExits())==!0)return new p(l.Error,"附件ID["+e.MyPK+"]已存在");const a=new A;a.MyPK=e.MyPK,(yield e.IsExits())==!1&&(a.FK_MapData=i,a.NoOfObj=m,a.Name=r,a.IsDtlAth=1,e.UploadType=1,yield a.Insert()),e.UIContralType=S.AthShow,e.SetPara("EnName","TS.FrmUI.FrmAttachmentExt"),yield e.Insert()}})}InitDDL(t,E,r){return c(this,null,function*(){const m=this.RequestVal("FrmID");if(!t)return;const i=new h(r);i.No=r,yield i.Retrieve();const e=new D;if(e.MyPK=m+"_"+t,(yield e.IsExits())==!0)return new p(l.Error,"字段在表单已经存在"+t);E?e.Name=E:e.Name=i.Name,e.KeyOfEn=t,e.FK_MapData=m,e.UIVisible=1,e.UIIsEnable=1,e.LGType=0,e.MyDataType=1,e.UIContralType=S.DDL,e.UIBindKey=i.No,e.SetPara("SrcType",i.DBSrcType),yield e.Insert(),e.UIVisible=0,e.MyPK=m+"_"+t+"T",e.KeyOfEn=t+"T",e.UIContralType=S.TB,yield e.Insert();const a="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+e.MyPK;return new p(l.GoToUrl,a)})}}export{v as GPN_MethodField};
