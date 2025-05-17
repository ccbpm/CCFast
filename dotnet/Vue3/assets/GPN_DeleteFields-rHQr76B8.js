var P=Object.defineProperty;var K=(u,I,t)=>I in u?P(u,I,{enumerable:!0,configurable:!0,writable:!0,value:t}):u[I]=t;var p=(u,I,t)=>K(u,typeof I!="symbol"?I+"":I,t);var F=(u,I,t)=>new Promise((S,i)=>{var o=a=>{try{m(t.next(a))}catch(T){i(T)}},M=a=>{try{m(t.throw(a))}catch(T){i(T)}},m=a=>a.done?S(a.value):Promise.resolve(a.value).then(o,M);m((t=t.apply(u,I)).next())});import{MapAttr as c}from"./MapAttr-B1mxD3vP.js";import{SFTables as N,SFTable as A}from"./SFTable-BlM1UBse.js";import{SysEnumMain as d}from"./SysEnumMain-CBhfewK0.js";import{b5 as h,G as l,l as y,D,aF as f}from"./entry/index-C6uBgOW5-1730430676707.js";import{FrmAttachment as G}from"./FrmAttachment-D6absTvH.js";import{GroupFields as B}from"./GroupField-DV5A1BJs.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFDBSrc-DKIMsnoa.js";import"./SysEnum-DlgPT0C2.js";import"./EntityOID-DvdPWQGp.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";class z extends h{constructor(){super("GPN_DeleteFields");p(this,"HelpString",`
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
 
    `)}Init(){this.PageTitle="删除字段",this.ForEntityClassID="",this.RequestVal("FrmID"),this.AddGroup("Nurel","基本字段"),this.SelectItemsByGroupList("Delete","要删除的字段",this.HelpString,"","字段名","中文名","")}AdminEnum(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SysEnumMain";return new l(y.GoToUrl,t)}AdminDict(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SFTable";return new l(y.GoToUrl,t)}GenerSorts(){return F(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,S,i,o,M){return F(this,null,function*(){const m=this.RequestVal("FrmID"),a=new B;yield a.Retrieve("FrmID",m,"Idx");const T=a.filter(e=>e.CtrlType===null||e.CtrlType===""),w=T.length===0?0:T[0].OID;if(t=="Blank")return yield N.Init_Blank(),this.InitDDL(o,i,"Blank");if(t==="String"||t==="Int"||t==="Number"||t==="DT"||t==="Boolean"||t==="JE"||t==="Time"){const e=new c;if(e.MyPK=m+"_"+o,e.FK_MapData=m,e.GroupID=w,e.KeyOfEn=o,(yield e.IsExits())==!0)return new l(y.Error,"字段已经存在");e.KeyOfEn=o,e.Name=i;let n="";return t==="String"&&(e.MyDataType=D.AppString,e.MaxLen=50,n="TS.FrmUI.MapAttrString",e.SetPara("EnName",n),yield e.Insert()),t==="Int"&&(e.MyDataType=D.AppInt,n="TS.FrmUI.MapAttrNum",e.SetPara("EnName",n),yield e.Insert()),t==="Number"&&(e.MyDataType=D.AppFloat,n="TS.FrmUI.MapAttrNum",e.SetPara("EnName",n),yield e.Insert()),t==="JE"&&(e.MyDataType=D.AppMoney,n="TS.FrmUI.MapAttrNum",e.SetPara("EnName",n),yield e.Insert()),t==="DT"&&(e.MyDataType=D.AppDate,e.IsSupperText="0",n="TS.FrmUI.MapAttrDT",e.SetPara("EnName",n),yield e.Insert()),t==="Time"&&(e.MyDataType=D.AppDateTime,e.IsSupperText="1",n="TS.FrmUI.MapAttrDT",e.SetPara("EnName",n),yield e.Insert()),t==="Boolean"&&(e.MyDataType=D.AppBoolean,n="TS.FrmUI.MapAttrBoolean",e.SetPara("EnName",n),yield e.Insert()),new l(y.Message,"创建成功")}if(t==="SelectedEnum.FieldName"){if(!i)return;const n=this.RequestVal("tb1","SelectedEnum"),E=this.RequestVal("tb2","SelectedEnum"),r=new d(n);r.No=n,yield r.Retrieve(),r.EnumKey===""&&(r.EnumKey=i);const s=new c;if(s.MyPK=m+"_"+i,(yield s.IsExits())==!0)return new l(y.Error,"字段在表单已经存在"+i);s.GroupID=w,s.Name=E,s.KeyOfEn=i,s.FK_MapData=m,s.UIVisible=1,s.UIIsEnable=1,s.LGType=1,s.MyDataType=r.EnumType===0?2:1,s.SetPara("RBShowModel",3),s.UIContralType=1,s.UIBindKey=r.EnumKey,yield s.Insert();const U="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrEnum&PKVal="+s.MyPK;return new l(y.GoToUrl,U)}if(t==="SelectedDict.Name"){if(!i)return;const n=this.RequestVal("tb1","SelectedDict"),E=new A(n);E.No=n,yield E.Retrieve();const r=new c;if(r.MyPK=m+"_"+i,(yield r.IsExits())==!0)return new l(y.Error,"字段在表单已经存在"+i);r.Name=E.Name,r.KeyOfEn=i,r.FK_MapData=m,r.GroupID=w,r.UIVisible=1,r.UIIsEnable=1,r.LGType=0,r.MyDataType=1,r.UIContralType=f.DDL,r.UIBindKey=E.No,r.SetPara("SrcType",E.DBSrcType),yield r.Insert();const s=r.MyPK;r.UIVisible=0,r.UIContralType=f.TB,r.MyPK=m+"_"+i+"T",r.KeyOfEn=i+"T",r.Name=r.Name+"T",yield r.Insert();const U="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+s;return new l(y.GoToUrl,U)}if(t==="AthField"){const e=new c;if(e.FK_MapData=m,e.MyPK=m+"_"+o,e.KeyOfEn=o,e.Name=i,e.GroupID=w,(yield e.IsExits())==!0)return new l(y.Error,"附件ID["+e.MyPK+"]已存在");const n=new G;n.MyPK=e.MyPK,(yield e.IsExits())==!1&&(n.FK_MapData=m,n.NoOfObj=o,n.Name=i,n.IsDtlAth=1,e.UploadType=1,yield n.Insert()),e.UIContralType=f.AthShow,e.SetPara("EnName","TS.FrmUI.FrmAttachmentExt"),yield e.Insert()}})}InitDDL(t,S,i){return F(this,null,function*(){const o=this.RequestVal("FrmID");if(!t)return;const m=new A(i);m.No=i,yield m.Retrieve();const a=new c;if(a.MyPK=o+"_"+t,(yield a.IsExits())==!0)return new l(y.Error,"字段在表单已经存在"+t);S?a.Name=S:a.Name=m.Name,a.KeyOfEn=t,a.FK_MapData=o,a.UIVisible=1,a.UIIsEnable=1,a.LGType=0,a.MyDataType=1,a.UIContralType=f.DDL,a.UIBindKey=m.No,a.SetPara("SrcType",m.DBSrcType),yield a.Insert(),a.UIVisible=0,a.MyPK=o+"_"+t+"T",a.KeyOfEn=t+"T",a.UIContralType=f.TB,yield a.Insert();const T="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+a.MyPK;return new l(y.GoToUrl,T)})}}export{z as GPN_DeleteFields};
