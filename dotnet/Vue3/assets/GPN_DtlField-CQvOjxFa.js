var M=Object.defineProperty;var x=(c,I,t)=>I in c?M(c,I,{enumerable:!0,configurable:!0,writable:!0,value:t}):c[I]=t;var l=(c,I,t)=>x(c,typeof I!="symbol"?I+"":I,t);var f=(c,I,t)=>new Promise((E,r)=>{var m=n=>{try{s(t.next(n))}catch(T){r(T)}},A=n=>{try{s(t.throw(n))}catch(T){r(T)}},s=n=>n.done?E(n.value):Promise.resolve(n.value).then(m,A);s((t=t.apply(c,I)).next())});import{b9 as U,aM as F,aB as p,aC as u,cq as B,aL as S,aD as y,aG as h,aE as N}from"./entry/index-M8VErHPE-1727507756861.js";import{GPN_NewDDL as P}from"./GPN_NewDDL-D4iwPIsH.js";import{SysEnumMain as K}from"./SysEnumMain-Bn9Li5_w.js";import{FrmAttachment as _}from"./FrmAttachment-DyM-VnR7.js";import{GroupFields as G}from"./GroupField-JFOnJiHV.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./GloComm-DZ1gELjv.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./SysEnum-B89JeOjj.js";import"./EntityOID-BVVq-i_P.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";class Y extends U{constructor(){super("GPN_DtlField");l(this,"HelpString",`
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
 
    `)}Init(){this.PageTitle="新建从表字段",this.ForEntityClassID="",this.AddGroup("Nurel","基本字段"),this.TextBox2_NameNo("String","文本字段",this.HelpString,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuwenbenkuang","String"),this.TextBox2_NameNo("Int","整数",this.HelpInt,"","字段名","中文名",""),this.AddIcon("iconfont icon-zhengshu","Int"),this.TextBox2_NameNo("Number","数值",this.HelpNumber,"","字段名","中文名",""),this.AddIcon("iconfont icon-ziduanleixing-zhengshu","Number"),this.TextBox2_NameNo("JE","金额",this.HelpJE,"","字段名","中文名",""),this.AddIcon("iconfont icon-yifabupiaoju-renminbi-xi","JE"),this.TextBox2_NameNo("Time","日期时间",this.HelpTime,"","字段名","中文名",""),this.AddIcon("iconfont icon-shijian1","Time"),this.TextBox2_NameNo("DT","日期",this.HelpDT,"","字段名","中文名",""),this.AddIcon("iconfont icon-riqiqishu","DT"),this.TextBox2_NameNo("Boolean","开关",this.HelpBoolean,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuxuankuang","Boolean"),this.TextBox2_NameNo("write","写字板",this.FieldAth,"Ath","字段ID","写字板名称","写字板"),this.AddIcon("iconfont icon-xiezi","write"),this.AddGroup("Enum","枚举字段");const t=F.SQLEnumMain;this.SelectItemsByList("SelectedEnum","新建枚举字段",P.SelectedEnum,!1,t),this.TextBox1_Name("SelectedEnum.FieldName","输入字段ID",this.HelpEnumDDL,"字段ID",()=>this.RequestVal("tb1","SelectedEnum"),"英文字母或者下划线开头."),this.AddFunction("AdminEnum","枚举库维护",this.AdminEnum),this.AddGroup("FK","外键字段"),this.SelectItemsByGroupList("SelectedDict","新建外键字段",this.HelpEnumDDL,!1,F.srcDBSrc,F.SQLSFTable),this.TextBox1_Name("SelectedDict.Name","输入字段ID",this.HelpEnumDDL,"字段ID",()=>this.RequestVal("tb1","SelectedDict"),"英文字母或者下划线开头."),this.AddFunction("AdminDict","外键库维护",this.AdminDict),this.AddGroup("Component","组件"),this.TextBox2_NameNo("AthField","字段附件",this.FieldAth,"Ath","字段ID","附件名称","我的附件"),this.AddIcon("iconfont icon-attach","AthField")}AdminEnum(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SysEnumMain";return new p(u.GoToUrl,t)}AdminDict(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SFTable";return new p(u.GoToUrl,t)}GenerSorts(){return f(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,E,r,m,A){return f(this,null,function*(){const s=this.RequestVal("FrmID"),n=new G;yield n.Retrieve("FrmID",s,"Idx");const T=n.filter(e=>e.CtrlType===null||e.CtrlType===""),d=T.length===0?0:T[0].OID;if(t=="Blank")return yield B.Init_Blank(),this.InitDDL(m,r,"Blank");if(t==="String"||t==="Int"||t==="Number"||t==="DT"||t==="Boolean"||t==="JE"||t==="Time"||t==="write"){const e=new S;if(e.MyPK=s+"_"+m,e.FK_MapData=s,e.GroupID=d,e.KeyOfEn=m,(yield e.IsExits())==!0)return new p(u.Error,"字段已经存在");e.KeyOfEn=m,e.Name=r;let a="";return t==="String"&&(e.MyDataType=y.AppString,e.MaxLen=50,a="TS.FrmUI.MapAttrString",e.SetPara("EnName",a),yield e.Insert()),t==="Int"&&(e.MyDataType=y.AppInt,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="Number"&&(e.MyDataType=y.AppFloat,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="JE"&&(e.MyDataType=y.AppMoney,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="DT"&&(e.MyDataType=y.AppDate,e.IsSupperText="0",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Time"&&(e.MyDataType=y.AppDateTime,e.IsSupperText="1",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Boolean"&&(e.MyDataType=y.AppBoolean,a="TS.FrmUI.MapAttrBoolean",e.SetPara("EnName",a),yield e.Insert()),t==="write"&&(e.MyDataType=y.AppString,e.UIContralType=h.HandWriting,a="TS.FrmUI.FrmHandWriting",e.SetPara("EnName",a),yield e.Insert()),new p(u.Message,"创建成功")}if(t==="SelectedEnum.FieldName"){if(!r)return;const a=this.RequestVal("tb1","SelectedEnum"),D=this.RequestVal("tb2","SelectedEnum"),i=new K(a);i.No=a,yield i.Retrieve(),i.EnumKey===""&&(i.EnumKey=r);const o=new S;if(o.MyPK=s+"_"+r,(yield o.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+r);o.GroupID=d,o.Name=D,o.KeyOfEn=r,o.FK_MapData=s,o.UIVisible=1,o.UIIsEnable=1,o.LGType=1,o.MyDataType=i.EnumType===0?2:1,o.SetPara("RBShowModel",3),o.UIContralType=1,o.UIBindKey=i.EnumKey,yield o.Insert();const w="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrEnum&PKVal="+o.MyPK;return new p(u.GoToUrl,w)}if(t==="SelectedDict.Name"){if(!r)return;const a=this.RequestVal("tb1","SelectedDict"),D=new N(a);D.No=a,yield D.Retrieve();const i=new S;if(i.MyPK=s+"_"+r,(yield i.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+r);i.Name=D.Name,i.KeyOfEn=r,i.FK_MapData=s,i.GroupID=d,i.UIVisible=1,i.UIIsEnable=1,i.LGType=0,i.MyDataType=1,i.UIContralType=h.DDL,i.UIBindKey=D.No,i.SetPara("SrcType",D.DBSrcType),yield i.Insert();const o=i.MyPK;i.UIVisible=0,i.UIContralType=h.TB,i.MyPK=s+"_"+r+"T",i.KeyOfEn=r+"T",i.Name=i.Name+"T",yield i.Insert();const w="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+o;return new p(u.GoToUrl,w)}if(t==="AthField"){const e=new S;if(e.FK_MapData=s,e.MyPK=s+"_"+m,e.KeyOfEn=m,e.Name=r,e.GroupID=d,(yield e.IsExits())==!0)return new p(u.Error,"附件ID["+e.MyPK+"]已存在");const a=new _;a.MyPK=e.MyPK,(yield e.IsExits())==!1&&(a.FK_MapData=s,a.NoOfObj=m,a.Name=r,a.IsDtlAth=1,a.SetPara("IsDtlAth","1"),e.UploadType=1,yield a.Insert()),e.UIContralType=h.AthShow,e.SetPara("EnName","TS.FrmUI.FrmAttachmentExt"),yield e.Insert()}})}InitDDL(t,E,r){return f(this,null,function*(){const m=this.RequestVal("FrmID");if(!t)return;const s=new N(r);s.No=r,yield s.Retrieve();const n=new S;if(n.MyPK=m+"_"+t,(yield n.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+t);E?n.Name=E:n.Name=s.Name,n.KeyOfEn=t,n.FK_MapData=m,n.UIVisible=1,n.UIIsEnable=1,n.LGType=0,n.MyDataType=1,n.UIContralType=h.DDL,n.UIBindKey=s.No,n.SetPara("SrcType",s.DBSrcType),yield n.Insert(),n.UIVisible=0,n.MyPK=m+"_"+t+"T",n.KeyOfEn=t+"T",n.UIContralType=h.TB,yield n.Insert();const T="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+n.MyPK;return new p(u.GoToUrl,T)})}}export{Y as GPN_DtlField};
