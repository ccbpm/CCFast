var I=Object.defineProperty;var w=(n,a,e)=>a in n?I(n,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):n[a]=e;var m=(n,a,e)=>w(n,typeof a!="symbol"?a+"":a,e);var o=(n,a,e)=>new Promise((l,r)=>{var c=t=>{try{S(e.next(t))}catch(i){r(i)}},A=t=>{try{S(e.throw(t))}catch(i){r(i)}},S=t=>t.done?l(t.value):Promise.resolve(t.value).then(c,A);S((e=e.apply(n,a)).next())});import{SyncData as F}from"./SyncData-CXNuvf7A.js";import{SyncDataField as K}from"./SyncDataField-MvjQnnae.js";import{b5 as x,B as f,G as B,l as D,D as u}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as h}from"./GloComm-CmAl8MpM.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./FrmTrack-BAfWiAdt.js";class H extends x{constructor(){super("GPN_SyncData");m(this,"APIOfSystem",`
  #### 帮助
  - 选择同步模式: ccbpm提供了三种同步模式.
  - 请仔细阅读每种模式.
  #### 同步到数据源
  - 选择一个数据源,如果列表里没有就配置一个数据源.
  - 选择要同步的表
  - 之后流程字段与表字段的同步关系.
  #### 同步到自定义的API
  - 输入一个API 地址.
  - 系统就会把流程运行的数据同步到这个地址里面去.
  #### 同步到内置的API
  - 系统提供一个内置的API接口，按照设置的同步时间点,系统就会数据写入到这个接口里面去.
  - 您可以重写这个接口，把后把数据写入到指定的位置.
  `);m(this,"APIOfSelf",`
  #### 帮助
  - 请输入API地址.
  - 格式为: http://xxx.xxx.xxxx
  #### 写入数据说明.
  - 系统会把流程的主表数据形成一个JSON传入到您的接口里.
  - 您需要接收这个JSON数据实体来处理它.
`);m(this,"DBSrc",`
  #### 帮助
  - 数据源：就是链接数据库的工具.
  - 如果没有您的数据源，需要在系统管理里创建一个数据源.
`);m(this,"DBSrcTable",`
  #### 帮助
  - 选择要同步数据的表.
  - 注意不能选择视图
  - 完成之后就需要设置字段的同步对应关系.

`);this.PageTitle="新建数据同步",this.ForEntityClassID="TS.AttrFlow.SyncData"}Init(){return o(this,null,function*(){this.AddGroup("A","新建数据同步"),this.AddBlank("APIOfSystem","同步到内置的API接口",this.APIOfSystem),this.TextBox1_Name("APIOfSelf","同步到API接口",this.APIOfSelf,"输入API","http://xxx.xxx.xxx.xxx","输入的SQL返回"),this.SelectItemsByList("DBSrc","数据源",this.DBSrc,!1,"SELECT No,Name FROM Sys_SFDBSrc"),this.SelectItemsByList("DBSrc.Table","表",this.DBSrcTable,!1,this.DBSrc_GenerTables),this.SelectItemsByList("DBSrc.Table.PKField","表的主键",this.DBSrcTable,!1,this.DBSrc_GenerTable_PKField,!0,!0),this.SelectItemsByList("DBSrc.Table.PKField.Fields","要同步的字段",this.DBSrcTable,!0,this.DBSrc_GenerTable_PKField,!0,!0);const l=this.RequestVal("RefPKVal"),r="ND"+Number(l)+"Rpt",c=`
    SELECT DISTINCT A.FK_Frm as No, B.Name as Name FROM WF_FrmNode A,Sys_MapData B WHERE A.FK_Flow=${l} AND A.FK_Frm=B.No
     UNION
    SELECT '${r}' as No, '${r}流程业务表' as Name FROM Port_Emp where No='admin' 
    `;this.SelectItemsByList("DBSrc.Table.PKField.Fields.Frm","数据源表单",this.HelpUn,!1,c),this.AddGroup("B","数据源管理"),this.AddBlank("AdminDBSrc","数据源维护",""),this.AddBlank("AdminDBSrc.ToUrl","修改","")})}GenerSorts(){return o(this,null,function*(){return Promise.resolve([])})}DBSrc_GenerTables(){return o(this,null,function*(){const e=this.RequestVal("tb1","DBSrc"),l=new f("BP.Sys.SFDBSrc",e);yield l.Retrieve();const r=yield l.DoMethodReturnJSON("GetTablesJson");return JSON.stringify(r)})}DBSrc_GenerTable_PKField(){return o(this,null,function*(){const e=this.RequestVal("tb1","DBSrc"),l=this.RequestVal("tb1","DBSrc.Table"),r=new f("BP.Sys.SFDBSrc",e);yield r.Retrieve();const c=yield r.DoMethodReturnJSON("GetTableFieldsJson",l);return JSON.stringify(c)})}Save_TextBox_X(e,l,r,c,A){return o(this,null,function*(){if(e=="AdminDBSrc"||e=="AdminDBSrc.ToUrl"){const t="/@/WF/Comm/Search.vue?EnName=TS.Sys.SFDBSrc";return new B(D.GoToUrl,t)}const S=this.RequestVal("RefPKVal");if(r==="APIOfSystem"){const t=new F;t.FlowNo=S,t.SyncType="APIOfSystem",t.SyncTypeT="系统内置的API",t.Note=r+c,t.DBSrc=r,t.SetPara("EnName","TS.AttrFlow.SyncDataByAPI"),yield t.Insert();const i="/@/WF/Comm/En.vue?EnName="+t.GetParaString("EnName","")+"&PKVal="+t.MyPK;return new B(D.GoToUrl,i)}if(e==="APIOfSelf"){const t=new F;t.FlowNo=S,t.SyncType="APIOfSelf",t.SyncTypeT="同步到API接口",t.Note="同步到API接口："+r,t.APIUrl=r,t.SetPara("EnName","TS.AttrFlow.SyncDataByAPI"),yield t.Insert();const i=h.UrlEnOnly(t.GetParaString("EnName",""),t.MyPK);return new B(D.GoToUrl,i)}if(e!=="DBSrc"&&!(e=="DBSrc.Table"||e==="DBSrc.Table.PKField"||e=="DBSrc.Table.PKField.Fields")){if(e==="DBSrc.Table.PKField.Fields.Frm"){const t=new F;t.FlowNo=S,t.SyncType="DBSrc",t.SyncTypeT="数据源",t.Note="把流程数据同步到数据源上:"+this.RequestVal("tb1","DBSrc"),t.DBSrc=this.RequestVal("tb1","DBSrc"),t.DBSrcT=this.RequestVal("tb2","DBSrc"),t.Src=this.RequestVal("tb1","DBSrc"),t.SetPara("EnName","TS.AttrFlow.SyncDataByDBSrc"),t.PTable=this.RequestVal("tb1","DBSrc.Table"),t.PTableName=this.RequestVal("tb2","DBSrc.Table"),t.FrmID=this.RequestVal("tb1","DBSrc.Table.PKField.Fields.Frm"),t.FrmName=this.RequestVal("tb2","DBSrc.Table.PKField.Fields.Frm");const i=this.RequestVal("tb1","DBSrc.Table.PKField").split("=");t.TablePKName=i[0],t.TablePKType=i[1],yield t.Insert();const b=this.RequestVal("tb1","DBSrc.Table.PKField.Fields").split(","),p=this.RequestVal("tb2","DBSrc.Table.PKField.Fields").split(","),s=new K;for(let T=0;T<b.length;T++){const d=b[T],P=d.split("=")[0],y=d.split("=")[1].toLowerCase();P==t.TablePKName||(s.MyPK=t.PKVal+"_"+P,(yield s.IsExits())==!0)||(s.RefPKVal=t.PKVal,s.AttrKey=P,s.AttrName=p[T],y.includes("int")==!0&&(s.AttrType=u.AppFloat,s.AttrTypeT="数值"),y.includes("char")==!0&&(s.AttrType=u.AppString,s.AttrTypeT="文本"),y.includes("int")==!0&&(s.AttrType=u.AppInt,s.AttrTypeT="整形"),s.FrmID=t.FrmID,s.FlowNo=this.RefPKVal,yield s.Insert())}const N=h.UrlEnOnly(t.GetParaString("EnName",""),t.MyPK);return new B(D.GoToUrl,N)}alert("没有判断的页面类型:["+e+"]")}})}}export{H as GPN_SyncData};
