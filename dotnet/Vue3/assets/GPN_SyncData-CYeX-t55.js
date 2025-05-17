var I=Object.defineProperty;var w=(n,a,e)=>a in n?I(n,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):n[a]=e;var T=(n,a,e)=>w(n,typeof a!="symbol"?a+"":a,e);var o=(n,a,e)=>new Promise((l,r)=>{var S=t=>{try{c(e.next(t))}catch(i){r(i)}},A=t=>{try{c(e.throw(t))}catch(i){r(i)}},c=t=>t.done?l(t.value):Promise.resolve(t.value).then(S,A);c((e=e.apply(n,a)).next())});import{SyncData as F}from"./SyncData-BCXvRA7o.js";import{SyncDataField as K}from"./SyncDataField-CEOgQs9w.js";import{b9 as x,aQ as b,aB as D,aC as m,aD as u}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as h}from"./GloComm-DZ1gELjv.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmTrack-0uAZQ3B_.js";class v extends x{constructor(){super("GPN_SyncData");T(this,"APIOfSystem",`
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
  `);T(this,"APIOfSelf",`
  #### 帮助
  - 请输入API地址.
  - 格式为: http://xxx.xxx.xxxx
  #### 写入数据说明.
  - 系统会把流程的主表数据形成一个JSON传入到您的接口里.
  - 您需要接收这个JSON数据实体来处理它.
`);T(this,"DBSrc",`
  #### 帮助
  - 数据源：就是链接数据库的工具.
  - 如果没有您的数据源，需要在系统管理里创建一个数据源.
`);T(this,"DBSrcTable",`
  #### 帮助
  - 选择要同步数据的表.
  - 注意不能选择视图
  - 完成之后就需要设置字段的同步对应关系.

`);this.PageTitle="新建数据同步",this.ForEntityClassID="TS.AttrFlow.SyncData"}Init(){return o(this,null,function*(){this.AddGroup("A","新建数据同步"),this.AddBlank("APIOfSystem","同步到内置的API接口",this.APIOfSystem),this.TextBox1_Name("APIOfSelf","同步到API接口",this.APIOfSelf,"输入API","http://xxx.xxx.xxx.xxx","输入的SQL返回"),this.SelectItemsByList("DBSrc","数据源",this.DBSrc,!1,"SELECT No,Name FROM Sys_SFDBSrc"),this.SelectItemsByList("DBSrc.Table","表",this.DBSrcTable,!1,this.DBSrc_GenerTables),this.SelectItemsByList("DBSrc.Table.PKField","表的主键",this.DBSrcTable,!1,this.DBSrc_GenerTable_PKField,!0,!0),this.SelectItemsByList("DBSrc.Table.PKField.Fields","要同步的字段",this.DBSrcTable,!0,this.DBSrc_GenerTable_PKField,!0,!0);const l=this.RequestVal("RefPKVal"),r="ND"+Number(l)+"Rpt",S=`
    SELECT DISTINCT A.FK_Frm as No, B.Name as Name FROM WF_FrmNode A,Sys_MapData B WHERE A.FK_Flow=${l} AND A.FK_Frm=B.No
     UNION
    SELECT '${r}' as No, '${r}流程业务表' as Name FROM Port_Emp where No='admin' 
    `;this.SelectItemsByList("DBSrc.Table.PKField.Fields.Frm","数据源表单",this.HelpUn,!1,S),this.AddGroup("B","数据源管理"),this.AddBlank("AdminDBSrc","数据源维护",""),this.AddBlank("AdminDBSrc.ToUrl","修改","")})}GenerSorts(){return o(this,null,function*(){return Promise.resolve([])})}DBSrc_GenerTables(){return o(this,null,function*(){const e=this.RequestVal("tb1","DBSrc"),l=new b("BP.Sys.SFDBSrc",e);yield l.Retrieve();const r=yield l.DoMethodReturnJSON("GetTablesJson");return JSON.stringify(r)})}DBSrc_GenerTable_PKField(){return o(this,null,function*(){const e=this.RequestVal("tb1","DBSrc"),l=this.RequestVal("tb1","DBSrc.Table"),r=new b("BP.Sys.SFDBSrc",e);yield r.Retrieve();const S=yield r.DoMethodReturnJSON("GetTableFieldsJson",l);return JSON.stringify(S)})}Save_TextBox_X(e,l,r,S,A){return o(this,null,function*(){if(e=="AdminDBSrc"||e=="AdminDBSrc.ToUrl"){const t="/@/WF/Comm/Search.vue?EnName=TS.Sys.SFDBSrc";return new D(m.GoToUrl,t)}const c=this.RequestVal("RefPKVal");if(r==="APIOfSystem"){const t=new F;t.FlowNo=c,t.SyncType="APIOfSystem",t.SyncTypeT="系统内置的API",t.Note=r+S,t.DBSrc=r,t.SetPara("EnName","TS.AttrFlow.SyncDataByAPI"),yield t.Insert();const i="/@/WF/Comm/En.vue?EnName="+t.GetParaString("EnName","")+"&PKVal="+t.MyPK;return new D(m.GoToUrl,i)}if(e==="APIOfSelf"){const t=new F;t.FlowNo=c,t.SyncType="APIOfSelf",t.SyncTypeT="APIOfSelf",t.Note=r+S,t.DBSrc=r,t.SetPara("EnName","TS.AttrFlow.SyncDataByAPI"),yield t.Insert();const i=h.UrlEnOnly(t.GetParaString("EnName",""),t.MyPK);return new D(m.GoToUrl,i)}if(e!=="DBSrc"&&!(e=="DBSrc.Table"||e==="DBSrc.Table.PKField"||e=="DBSrc.Table.PKField.Fields")){if(e==="DBSrc.Table.PKField.Fields.Frm"){const t=new F;t.FlowNo=c,t.SyncType="DBSrc",t.SyncTypeT="数据源",t.Note="把流程数据同步到数据源上:"+this.RequestVal("tb1","DBSrc"),t.DBSrc=this.RequestVal("tb1","DBSrc"),t.DBSrcT=this.RequestVal("tb2","DBSrc"),t.Src=this.RequestVal("tb1","DBSrc"),t.SetPara("EnName","TS.AttrFlow.SyncDataByDBSrc"),t.PTable=this.RequestVal("tb1","DBSrc.Table"),t.PTableName=this.RequestVal("tb2","DBSrc.Table"),t.FrmID=this.RequestVal("tb1","DBSrc.Table.PKField.Fields.Frm"),t.FrmName=this.RequestVal("tb2","DBSrc.Table.PKField.Fields.Frm");const i=this.RequestVal("tb1","DBSrc.Table.PKField").split("=");t.TablePKName=i[0],t.TablePKType=i[1],yield t.Insert();const f=this.RequestVal("tb1","DBSrc.Table.PKField.Fields").split(","),N=this.RequestVal("tb2","DBSrc.Table.PKField.Fields").split(","),s=new K;for(let B=0;B<f.length;B++){const d=f[B],P=d.split("=")[0],y=d.split("=")[1].toLowerCase();P==t.TablePKName||(s.MyPK=t.PKVal+"_"+P,(yield s.IsExits())==!0)||(s.RefPKVal=t.PKVal,s.AttrKey=P,s.AttrName=N[B],y.includes("int")==!0&&(s.AttrType=u.AppFloat,s.AttrTypeT="数值"),y.includes("char")==!0&&(s.AttrType=u.AppString,s.AttrTypeT="文本"),y.includes("int")==!0&&(s.AttrType=u.AppInt,s.AttrTypeT="整形"),s.FrmID=t.FrmID,s.FlowNo=this.RefPKVal,yield s.Insert())}const p=h.UrlEnOnly(t.GetParaString("EnName",""),t.MyPK);return new D(m.GoToUrl,p)}alert("没有判断的页面类型:["+e+"]")}})}}export{v as GPN_SyncData};
