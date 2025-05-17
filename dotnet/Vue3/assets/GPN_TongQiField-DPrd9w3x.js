var A=Object.defineProperty;var U=(T,I,t)=>I in T?A(T,I,{enumerable:!0,configurable:!0,writable:!0,value:t}):T[I]=t;var l=(T,I,t)=>U(T,typeof I!="symbol"?I+"":I,t);var w=(T,I,t)=>new Promise((S,i)=>{var m=n=>{try{o(t.next(n))}catch(y){i(y)}},d=n=>{try{o(t.throw(n))}catch(y){i(y)}},o=n=>n.done?S(n.value):Promise.resolve(n.value).then(m,d);o((t=t.apply(T,I)).next())});import{b9 as P,aM as x,aB as p,aC as u,cq as B,aL as D,aD as E,aE as N,aG as f}from"./entry/index-M8VErHPE-1727507756861.js";import{GPN_NewDDL as F}from"./GPN_NewDDL-D4iwPIsH.js";import{SysEnumMain as K}from"./SysEnumMain-Bn9Li5_w.js";import{FrmAttachment as _}from"./FrmAttachment-DyM-VnR7.js";import{GroupFields as G}from"./GroupField-JFOnJiHV.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./GloComm-DZ1gELjv.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./SysEnum-B89JeOjj.js";import"./EntityOID-BVVq-i_P.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";class Y extends P{constructor(){super("GPN_TongQiField");l(this,"HelpString",`
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
 
    `)}Init(){this.PageTitle="同期字段",this.ForEntityClassID="",this.AddGroup("Nurel","基本字段"),this.TextBox2_NameNo("String","文本字段",this.HelpString,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuwenbenkuang","String"),this.TextBox2_NameNo("Int","整数",this.HelpInt,"","字段名","中文名",""),this.AddIcon("iconfont icon-zhengshu","Int"),this.TextBox2_NameNo("Number","数值",this.HelpNumber,"","字段名","中文名",""),this.AddIcon("iconfont icon-ziduanleixing-zhengshu","Number"),this.TextBox2_NameNo("JE","金额",this.HelpJE,"","字段名","中文名",""),this.AddIcon("iconfont icon-yifabupiaoju-renminbi-xi","JE"),this.TextBox2_NameNo("Time","日期时间",this.HelpTime,"","字段名","中文名",""),this.AddIcon("iconfont icon-shijian1","Time"),this.TextBox2_NameNo("DT","日期",this.HelpDT,"","字段名","中文名",""),this.AddIcon("iconfont icon-riqiqishu","DT"),this.TextBox2_NameNo("Boolean","开关",this.HelpBoolean,"","字段名","中文名",""),this.AddIcon("iconfont icon-fuxuankuang","Boolean"),this.AddGroup("Enum","枚举字段");const t=x.SQLEnumMain;this.SelectItemsByList("SelectedEnum","新建枚举字段",F.SelectedEnum,!1,t),this.TextBox1_Name("SelectedEnum.FieldName","输入字段ID",F.SelectedEnum_FieldName,"字段ID",()=>this.RequestVal("tb1","SelectedEnum"),"英文字母或者下划线开头.")}AdminEnum(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SysEnumMain";return new p(u.GoToUrl,t)}AdminDict(){const t="/@/WF/Comm/Search.vue?EnName=TS.FrmUI.SFTable";return new p(u.GoToUrl,t)}GenerSorts(){return w(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,S,i,m,d){return w(this,null,function*(){const o=this.RequestVal("FrmID"),n=new G;yield n.Retrieve("FrmID",o,"Idx");const y=n.filter(e=>e.CtrlType===null||e.CtrlType===""||e.CtrlType==="Attr"),h=y.length===0?0:y[0].OID;if(t=="Blank")return yield B.Init_Blank(),this.InitDDL(m,i,"Blank");if(t==="String"||t==="Int"||t==="Number"||t==="DT"||t==="Boolean"||t==="JE"||t==="Time"){const e=new D;if(e.MyPK=o+"_"+m,e.FK_MapData=o,e.GroupID=h,e.KeyOfEn=m,(yield e.IsExits())==!0)return new p(u.Error,"字段已经存在");e.KeyOfEn=m,e.Name=i;let a="";return t==="String"&&(e.MyDataType=E.AppString,e.MaxLen=50,a="TS.FrmUI.MapAttrString",e.SetPara("EnName",a),yield e.Insert()),t==="Int"&&(e.MyDataType=E.AppInt,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="Number"&&(e.MyDataType=E.AppFloat,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="JE"&&(e.MyDataType=E.AppMoney,a="TS.FrmUI.MapAttrNum",e.SetPara("EnName",a),yield e.Insert()),t==="DT"&&(e.MyDataType=E.AppDate,e.IsSupperText="0",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Time"&&(e.MyDataType=E.AppDateTime,e.IsSupperText="1",a="TS.FrmUI.MapAttrDT",e.SetPara("EnName",a),yield e.Insert()),t==="Boolean"&&(e.MyDataType=E.AppBoolean,a="TS.FrmUI.MapAttrBoolean",e.SetPara("EnName",a),yield e.Insert()),new p(u.Message,"创建成功")}if(t==="SelectedEnum.FieldName"){if(!i)return;const a=this.RequestVal("tb1","SelectedEnum"),c=this.RequestVal("tb2","SelectedEnum"),r=new K(a);r.No=a,yield r.Retrieve(),r.EnumKey===""&&(r.EnumKey=i);const s=new D;if(s.MyPK=o+"_"+i,(yield s.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+i);s.GroupID=h,s.Name=c,s.KeyOfEn=i,s.FK_MapData=o,s.UIVisible=1,s.UIIsEnable=1,s.LGType=1,s.MyDataType=r.EnumType===0?2:1,s.SetPara("RBShowModel",3),s.UIContralType=1,s.UIBindKey=r.EnumKey,yield s.Insert();const M="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrEnum&PKVal="+s.MyPK;return new p(u.GoToUrl,M)}if(t==="SelectedDict.Name"){if(!i)return;const a=this.RequestVal("tb1","SelectedDict"),c=new N(a);c.No=a,yield c.Retrieve();const r=new D;if(r.MyPK=o+"_"+i,(yield r.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+i);r.Name=c.Name,r.KeyOfEn=i,r.FK_MapData=o,r.GroupID=h,r.UIVisible=1,r.UIIsEnable=1,r.LGType=0,r.MyDataType=1,r.UIContralType=f.DDL,r.UIBindKey=c.No,r.SetPara("SrcType",c.DBSrcType),yield r.Insert(),r.UIVisible=0,r.UIContralType=f.TB,r.MyPK=o+"_"+i+"T",r.KeyOfEn=i+"T",yield r.Insert();const s="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+r.MyPK;return new p(u.GoToUrl,s)}if(t==="AthField"){const e=new D;if(e.FK_MapData=o,e.MyPK=o+"_"+m,e.KeyOfEn=m,e.Name=i,e.GroupID=h,(yield e.IsExits())==!0)return new p(u.Error,"附件ID["+e.MyPK+"]已存在");const a=new _;a.MyPK=e.MyPK,(yield e.IsExits())==!1&&(a.FK_MapData=o,a.NoOfObj=m,a.Name=i,a.IsDtlAth=1,e.UploadType=1,yield a.Insert()),e.UIContralType=f.AthShow,e.SetPara("EnName","TS.FrmUI.FrmAttachmentExt"),yield e.Insert()}})}InitDDL(t,S,i){return w(this,null,function*(){const m=this.RequestVal("FrmID");if(!t)return;const o=new N(i);o.No=i,yield o.Retrieve();const n=new D;if(n.MyPK=m+"_"+t,(yield n.IsExits())==!0)return new p(u.Error,"字段在表单已经存在"+t);S?n.Name=S:n.Name=o.Name,n.KeyOfEn=t,n.FK_MapData=m,n.UIVisible=1,n.UIIsEnable=1,n.LGType=0,n.MyDataType=1,n.UIContralType=f.DDL,n.UIBindKey=o.No,n.SetPara("SrcType",o.DBSrcType),yield n.Insert(),n.UIVisible=0,n.MyPK=m+"_"+t+"T",n.KeyOfEn=t+"T",n.UIContralType=f.TB,yield n.Insert();const y="/@/WF/Comm/En.vue?EnName=TS.FrmUI.MapAttrSFSQL&PKVal="+n.MyPK;return new p(u.GoToUrl,y)})}}export{Y as GPN_TongQiField};
