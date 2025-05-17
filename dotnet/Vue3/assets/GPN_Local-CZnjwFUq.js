var u=Object.defineProperty;var p=(i,S,r)=>S in i?u(i,S,{enumerable:!0,configurable:!0,writable:!0,value:r}):i[S]=r;var a=(i,S,r)=>p(i,typeof S!="symbol"?S+"":S,r);var T=(i,S,r)=>new Promise((D,c)=>{var s=o=>{try{e(r.next(o))}catch(t){c(t)}},N=o=>{try{e(r.throw(o))}catch(t){c(t)}},e=o=>o.done?D(o.value):Promise.resolve(o.value).then(s,N);e((r=r.apply(i,S)).next())});import{SFProcedure as h}from"./SFProcedure-CE1gQz0T.js";import{S as d}from"./DBAccess-sLO0RM-h.js";import{SFTable as E}from"./SFTable-BlM1UBse.js";import{b5 as b,G as l,l as m,D as x,B as _}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as F}from"./GloComm-CmAl8MpM.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFPara-T412M7pO.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFParaSln-BnGoh0gJ.js";import"./SFColumn-CXmWKoZg.js";import"./FrmTrack-BAfWiAdt.js";class V extends b{constructor(){super("GPN_Local");a(this,"HelpSearch",`
  #### 帮助
  - 查询与字典表不同，他需要参数据才能执行.
  #### 用到场景
  - 文本框自动完成, 级联下拉框、自动填充.
  `);a(this,"WebApi_Url",`
  #### 帮助
   - 请输入路径参数.
   - 仅仅输入主机端口号后面的部分.
   - 比如: /xxxx.do
  `);a(this,"SrcHelp",`
  #### 帮助
   - 请选择数据源，如果没有，请新建数据源.
   - 
  `);a(this,"SFTable",`
  #### 帮助
   - 内置字典表,比如: 省份，片区、城市、税种，税目
   - 内置字典表，是自己可以维护的表.
   - 存储在 Sys_SFTableDtl 表里. 
   - 用户可以通过ccfrom自己定义，自己维护的基础数据.
  `);a(this,"Handler",`
  #### 帮助
   - 优点:格式灵活,展现效果随心所欲.
   - 适用于:效果
   #### lisdxcx
  `);a(this,"SQL",`
  #### 帮助
   - 设置一个SQL语句从数据源中查询出来.
   - 支持ccbpm的表达式. @WebUser.No 当前用户编号， @WebUser.Name 登录名称， @WebUser.DeptNo 登录人所在部门.
   #### DEMO
   - 本部门的人员.
   - SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
   - 我的下级部门
   - SELECT No,Name FROM Port_Dept WHERE PartentNo='@WebUser.DeptNo'
xxx      
  `);a(this,"SQL_Doc",`
  #### 帮助
   - 设置一个SQL语句从数据源中查询出来.
   - 支持ccbpm的表达式. @WebUser.No 当前用户编号， @WebUser.Name 登录名称， @WebUser.DeptNo 登录人所在部门.
   #### DEMO
   - 本部门的人员.
   - SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
   - 我的下级部门
   - SELECT No,Name FROM Port_Dept WHERE PartentNo='@WebUser.DeptNo'
xxx      
  `);a(this,"JavaScript",`
    #### 帮助
     - 暂无
     #### lisdxcx
     function Xxx()
     {
        
     }
xxx      
    `);a(this,"WebApi",`
  #### 帮助
   - 调用服务获得数据.
    
  `);a(this,"WebApi_Doc",`
  #### 帮助
  - 调用服务获得数据.
    
  `);a(this,"Docs1",`
  #### 帮助 
  - 暂无
  `);a(this,"Docs2",`
  #### 帮助
  - 暂无
    
  `);a(this,"Docs4",`
  #### 帮助
  - 填写格式: 枚举值,枚举标签; 
  - 例如: ty,团员;dy=党员;qz,群众; 
  - 系统解析为: ty是团员, dy是党员, qz是群众.

  #### 数据存储.
  - string类型的枚举也称为标记枚举,字母存储一个列,标签存储一个列.
  - 在表单里字段是abc,那系统就会自动创建一个影子字段 abcT.
  - abc字段存储的是标记, abcT存储的是标签.
  `);this.PageTitle="新建"}Init(){this.AddGroup("A","新建字典","icon-list"),this.TextBox2_NameNo("SFTableDict","内置字典表",this.SFTable,"SF_","字典ID","字典名称",""),this.SelectItemsByList("SFTableDict.CodeStruct","数据结构",this.SFTable,!1,this.GetCodeStruct()),this.TextBox2_NameNo("SQL","SQL查询字典表",this.SQL,"SQL_","字典ID","字典名称",""),this.TextArea("SQL.Doc","填写SQL",this.SQL_Doc,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'","查询语句"),this.SelectItemsByList("SQL.Doc.CodeStruct","数据结构",this.SFTable,!1,this.GetCodeStruct()),this.TextBox2_NameNo("Handler","微服务Handler字典表",this.Handler,"Handler_","字典ID","字典名称",""),this.TextSQL("Handler.Doc","填写内容",this.SQL_Doc,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'","查询语句"),this.TextBox2_NameNo("JavaScript","JavaScript字典表",this.JavaScript,"JS_","字典ID","字典名称",""),this.TextArea("JavaScript.Doc","填写方法名",this.SQL_Doc,"方法名"," MyDict() ","Javascript的方法名"),this.AddGroup("B","新建查询","Search"),this.TextBox3_NameNoNote("Search","创建查询",this.HelpSearch,"S","编号","名称","备注","我的查询"),this.AddIcon("icon-layers","Search"),this.AddGroup("C","新建过程","Procedure"),this.TextBox3_NameNoNote("Procedure","创建过程",this.HelpSearch,"S","编号","名称","备注","我的过程"),this.AddIcon("icon-options","Procedure")}GetCodeStruct(){return JSON.stringify([{No:"0",Name:"编号名称"},{No:"1",Name:"树结构"}])}GenerSorts(){return T(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(r,D,c,s,N){return T(this,null,function*(){if(r=="Search"){const t=new d;if(t.No=s,(yield t.IsExits())==!0)return new l(m.Error,"编号["+s+"]已经存在.");t.Name=c,t.FK_SFDBSrc="local",t.Remark=N,t.SetPara("EnName","TS.FrmUI.SFSearchSQL"),yield t.Insert();const n=F.UrlEn("TS.FrmUI.SFSearchSQL",t.No);return new l(m.GoToUrl,n)}if(r=="Procedure"){const t=new h;if(t.No=s,(yield t.IsExits())==!0)return new l(m.Error,"编号["+s+"]已经存在.");t.Name=c,t.FK_SFDBSrc="local",t.Remark=N,t.SetPara("EnName","TS.FrmUI.SFProcedureSQL"),yield t.Insert();const n=F.UrlEn("TS.FrmUI.SFProcedureSQL",t.No);return new l(m.GoToUrl,n)}const e=new E;if(e.Name=c,e.No=s,e.TableDesc=N,e.RDT=x.CurrentDateTime,r.includes(".")==!1&&(yield e.IsExits()))throw new Error("编号:"+e.No+"已存在.");if(r==="SFTableDict.CodeStruct"){e.Name=this.RequestVal("tb1","SFTableDict"),e.No=this.RequestVal("tb2","SFTableDict"),e.FK_SFDBSrc="local",e.DBSrcType="SysDict",e.DBType=0,e.FK_Val=e.No,e.CodeStruct=this.RequestVal("tb1","SFTableDict.CodeStruct"),e.CodeStruct==0?e.SetPara("EnName","TS.FrmUI.SFTableDictNoName"):e.SetPara("EnName","TS.FrmUI.SFTableDictTree"),yield e.Insert();const t=new _("BP.Sys.SFTable",e.No);yield t.Retrieve(),yield t.DoMethodReturnString("GenerDataOfJson");let n="";return n="/@/WF/Comm/En.vue?EnName="+e.GetParaString("EnName","")+"&PKVal="+e.No,new l(m.GoToUrl,n)}if(r=="SQL.Doc.CodeStruct"){e.Name=this.RequestVal("tb1","SQL"),e.No=this.RequestVal("tb2","SQL"),e.DBSrcType="SQL",e.CodeStruct=this.RequestVal("tb1","SQL.Doc.CodeStruct");let t="TS.FrmUI.SFTableSQLNoName";e.CodeStruct==1&&(t="TS.FrmUI.SFTableSQLTree"),e.SetPara("EnName",t),e.SelectStatement=this.RequestVal("tb1","SQL.Doc"),e.FK_SFDBSrc="local",e.FK_Val=e.No,e.SrcType=3,yield e.Insert();const n=F.UrlEn(t,e.No);return new l(m.GoToUrl,n)}r=="Handler.Doc"&&(e.Name=this.RequestVal("tb1","Handler"),e.No=this.RequestVal("tb2","Handler"),e.DBSrcType="Handler"),r=="JavaScript.Doc"&&(e.Name=this.RequestVal("tb1","JavaScript"),e.No=this.RequestVal("tb2","JavaScript"),e.DBSrcType="JavaScript");let o="";if(r==="Handler.Doc"&&(o="TS.FrmUI.SFTableHandler"),r==="JavaScript.Doc"&&(o="TS.FrmUI.SFTableJS"),e.SelectStatement=s,e.FK_SFDBSrc=c,e.FK_Val=e.No,o!==""){e.SetPara("EnName",o),e.SelectStatement=s,e.FK_SFDBSrc=c,e.FK_Val=e.No,yield e.Insert();const t="/@/WF/Comm/En.vue?EnName="+o+"&PKVal="+e.No;return new l(m.GoToUrl,t)}})}}export{V as GPN_Local};
