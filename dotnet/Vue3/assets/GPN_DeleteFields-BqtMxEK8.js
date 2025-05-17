var P=Object.defineProperty;var K=(u,I,t)=>I in u?P(u,I,{enumerable:!0,configurable:!0,writable:!0,value:t}):u[I]=t;var l=(u,I,t)=>K(u,typeof I!="symbol"?I+"":I,t);var F=(u,I,t)=>new Promise((S,i)=>{var o=a=>{try{s(t.next(a))}catch(T){i(T)}},M=a=>{try{s(t.throw(a))}catch(T){i(T)}},s=a=>a.done?S(a.value):Promise.resolve(a.value).then(o,M);s((t=t.apply(u,I)).next())});import{b9 as N,aB as p,aC as y,cq as d,aL as c,aD as D,aE as A,aG as w}from"./entry/index-M8VErHPE-1727507756861.js";import{SysEnumMain as h}from"./SysEnumMain-Bn9Li5_w.js";import{FrmAttachment as B}from"./FrmAttachment-DyM-VnR7.js";import{GroupFields as G}from"./GroupField-JFOnJiHV.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SysEnum-B89JeOjj.js";import"./EntityOID-BVVq-i_P.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";class q extends N{constructor(){super("GPN_DeleteFields");l(this,"HelpString",`
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
 
    `)}Init(){this.PageTitle="删除字段",this.ForEntityClassID="",this.RequestVal("FrmID"),this.AddGroup("Nurel","基本字段"),this.SelectItemsByGroupList("Delete","要删除的字段",this.HelpString,"","字段名","中文名","")}AdminEnum(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SysEnumMain";return new p(y.GoToUrl,t)}AdminDict(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SFTable";return new p(y.GoToUrl,t)}GenerSorts(){return F(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,S,i,o,M){return F(this,null,function*(){const s=this.RequestVal("FrmID"),a=new G;yield a.Retrieve("FrmID",s,"Idx");const T=a.filter(e=>e.CtrlType===null||e.CtrlType===""),f=T.length===0?0:T[0].OID;if(t=="Blank")return yield d.Init_Blank(),this.InitDDL(o,i,"Blank");if(t==="String"||t==="Int"||t==="Number"||t==="DT"||t==="Boolean"||t==="JE"||t==="Time"){const e=new c;if(e.MyPK=s+"_"+o,e.FK_MapData=s,e.GroupID=f,e.KeyOfEn=o,(yield e.IsExits())==!0)return new p(y.Error,"字段已经存在");e.KeyOfEn=o,e.Name=i;let n="";return t==="String"&&(e.MyDataType=D.AppString,e.MaxLen=50,n="TS.FrmUI.MapAttrString",e.SetPara("EnName",n),yield e.Insert()),t==="Int"&&(e.MyDataType=D.AppInt,n="TS.FrmUI.MapAttrNum",e.SetPara("EnName",n),yield e.Insert()),t==="Number"&&(e.MyDataType=D.AppFloat,n="TS.FrmUI.MapAttrNum",e.SetPara("EnName",n),yield e.Insert()),t==="JE"&&(e.MyDataType=D.AppMoney,n="TS.FrmUI.MapAttrNum",e.SetPara("EnName",n),yield e.Insert()),t==="DT"&&(e.MyDataType=D.AppDate,e.IsSupperText="0",n="TS.FrmUI.MapAttrDT",e.SetPara("EnName",n),yield e.Insert()),t==="Time"&&(e.MyDataType=D.AppDateTime,e.IsSupperText="1",n="TS.FrmUI.MapAttrDT",e.SetPara("EnName",n),yield e.Insert()),t==="Boolean"&&(e.MyDataType=D.AppBoolean,n="TS.FrmUI.MapAttrBoolean",e.SetPara("EnName",n),yield e.Insert()),new p(y.Message,"创建成功")}if(t==="SelectedEnum.FieldName"){if(!i)return;const n=this.RequestVal("tb1","SelectedEnum"),E=this.RequestVal("tb2","SelectedEnum"),r=new h(n);r.No=n,yield r.Retrieve(),r.EnumKey===""&&(r.EnumKey=i);const m=new c;if(m.MyPK=s+"_"+i,(yield m.IsExits())==!0)return new p(y.Error,"字段在表单已经存在"+i);m.GroupID=f,m.Name=E,m.KeyOfEn=i,m.FK_MapData=s,m.UIVisible=1,m.UIIsEnable=1,m.LGType=1,m.MyDataType=r.EnumType===0?2:1,m.SetPara("RBShowModel",3),m.UIContralType=1,m.UIBindKey=r.EnumKey,yield m.Insert();const U="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrEnum&PKVal="+m.MyPK;return new p(y.GoToUrl,U)}if(t==="SelectedDict.Name"){if(!i)return;const n=this.RequestVal("tb1","SelectedDict"),E=new A(n);E.No=n,yield E.Retrieve();const r=new c;if(r.MyPK=s+"_"+i,(yield r.IsExits())==!0)return new p(y.Error,"字段在表单已经存在"+i);r.Name=E.Name,r.KeyOfEn=i,r.FK_MapData=s,r.GroupID=f,r.UIVisible=1,r.UIIsEnable=1,r.LGType=0,r.MyDataType=1,r.UIContralType=w.DDL,r.UIBindKey=E.No,r.SetPara("SrcType",E.DBSrcType),yield r.Insert();const m=r.MyPK;r.UIVisible=0,r.UIContralType=w.TB,r.MyPK=s+"_"+i+"T",r.KeyOfEn=i+"T",r.Name=r.Name+"T",yield r.Insert();const U="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+m;return new p(y.GoToUrl,U)}if(t==="AthField"){const e=new c;if(e.FK_MapData=s,e.MyPK=s+"_"+o,e.KeyOfEn=o,e.Name=i,e.GroupID=f,(yield e.IsExits())==!0)return new p(y.Error,"附件ID["+e.MyPK+"]已存在");const n=new B;n.MyPK=e.MyPK,(yield e.IsExits())==!1&&(n.FK_MapData=s,n.NoOfObj=o,n.Name=i,n.IsDtlAth=1,e.UploadType=1,yield n.Insert()),e.UIContralType=w.AthShow,e.SetPara("EnName","TS.FrmUI.FrmAttachmentExt"),yield e.Insert()}})}InitDDL(t,S,i){return F(this,null,function*(){const o=this.RequestVal("FrmID");if(!t)return;const s=new A(i);s.No=i,yield s.Retrieve();const a=new c;if(a.MyPK=o+"_"+t,(yield a.IsExits())==!0)return new p(y.Error,"字段在表单已经存在"+t);S?a.Name=S:a.Name=s.Name,a.KeyOfEn=t,a.FK_MapData=o,a.UIVisible=1,a.UIIsEnable=1,a.LGType=0,a.MyDataType=1,a.UIContralType=w.DDL,a.UIBindKey=s.No,a.SetPara("SrcType",s.DBSrcType),yield a.Insert(),a.UIVisible=0,a.MyPK=o+"_"+t+"T",a.KeyOfEn=t+"T",a.UIContralType=w.TB,yield a.Insert();const T="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+a.MyPK;return new p(y.GoToUrl,T)})}}export{q as GPN_DeleteFields};
