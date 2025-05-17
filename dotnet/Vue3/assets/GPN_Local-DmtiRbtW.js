var u=Object.defineProperty;var p=(c,S,r)=>S in c?u(c,S,{enumerable:!0,configurable:!0,writable:!0,value:r}):c[S]=r;var a=(c,S,r)=>p(c,typeof S!="symbol"?S+"":S,r);var T=(c,S,r)=>new Promise((D,i)=>{var s=o=>{try{e(r.next(o))}catch(t){i(t)}},m=o=>{try{e(r.throw(o))}catch(t){i(t)}},e=o=>o.done?D(o.value):Promise.resolve(o.value).then(s,m);e((r=r.apply(c,S)).next())});import{SFProcedure as h}from"./SFProcedure-CLBeHp-Y.js";import{S as E}from"./DBAccess-CzjFzLoq.js";import{b9 as d,aB as l,aC as N,aE as b,aD as x,aQ as _}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as F}from"./GloComm-DZ1gELjv.js";import"./SFPara-DL_8hzxu.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFParaSln-B7KC4nzL.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmTrack-0uAZQ3B_.js";class y extends d{constructor(){super("GPN_Local");a(this,"HelpSearch",`
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
  `);this.PageTitle="新建"}Init(){this.AddGroup("A","新建字典","icon-list"),this.TextBox2_NameNo("SFTableDict","内置字典表",this.SFTable,"SF_","字典ID","字典名称",""),this.SelectItemsByList("SFTableDict.CodeStruct","数据结构",this.SFTable,!1,this.GetCodeStruct()),this.TextBox2_NameNo("SQL","SQL查询字典表",this.SQL,"SQL_","字典ID","字典名称",""),this.TextArea("SQL.Doc","填写SQL",this.SQL_Doc,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'","查询语句"),this.SelectItemsByList("SQL.Doc.CodeStruct","数据结构",this.SFTable,!1,this.GetCodeStruct()),this.TextBox2_NameNo("Handler","微服务Handler字典表",this.Handler,"Handler_","字典ID","字典名称",""),this.TextSQL("Handler.Doc","填写内容",this.SQL_Doc,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'","查询语句"),this.TextBox2_NameNo("JavaScript","JavaScript字典表",this.JavaScript,"JS_","字典ID","字典名称",""),this.TextArea("JavaScript.Doc","填写方法名",this.SQL_Doc,"方法名"," MyDict() ","Javascript的方法名"),this.AddGroup("B","新建查询","Search"),this.TextBox3_NameNoNote("Search","创建查询",this.HelpSearch,"S","编号","名称","备注","我的查询"),this.AddIcon("icon-layers","Search"),this.AddGroup("C","新建过程","Procedure"),this.TextBox3_NameNoNote("Procedure","创建过程",this.HelpSearch,"S","编号","名称","备注","我的过程"),this.AddIcon("icon-options","Procedure")}GetCodeStruct(){return JSON.stringify([{No:"0",Name:"编号名称"},{No:"1",Name:"树结构"}])}GenerSorts(){return T(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(r,D,i,s,m){return T(this,null,function*(){if(r=="Search"){const t=new E;if(t.No=s,(yield t.IsExits())==!0)return new l(N.Error,"编号["+s+"]已经存在.");t.Name=i,t.FK_SFDBSrc="local",t.Remark=m,t.SetPara("EnName","TS.FrmUI.SFSearchSQL"),yield t.Insert();const n=F.UrlEn("TS.FrmUI.SFSearchSQL",t.No);return new l(N.GoToUrl,n)}if(r=="Procedure"){const t=new h;if(t.No=s,(yield t.IsExits())==!0)return new l(N.Error,"编号["+s+"]已经存在.");t.Name=i,t.FK_SFDBSrc="local",t.Remark=m,t.SetPara("EnName","TS.FrmUI.SFProcedureSQL"),yield t.Insert();const n=F.UrlEn("TS.FrmUI.SFProcedureSQL",t.No);return new l(N.GoToUrl,n)}const e=new b;if(e.Name=i,e.No=s,e.TableDesc=m,e.RDT=x.CurrentDateTime,r.includes(".")==!1&&(yield e.IsExits()))throw new Error("编号:"+e.No+"已存在.");if(r==="SFTableDict.CodeStruct"){e.Name=this.RequestVal("tb1","SFTableDict"),e.No=this.RequestVal("tb2","SFTableDict"),e.FK_SFDBSrc="local",e.DBSrcType="SysDict",e.DBType=0,e.FK_Val=e.No,e.CodeStruct=this.RequestVal("tb1","SFTableDict.CodeStruct"),e.CodeStruct==0?e.SetPara("EnName","TS.FrmUI.SFTableDictNoName"):e.SetPara("EnName","TS.FrmUI.SFTableDictTree"),yield e.Insert();const t=new _("BP.Sys.SFTable",e.No);yield t.Retrieve(),yield t.DoMethodReturnString("GenerDataOfJson");let n="";return n="/@/WF/Comm/En.vue?EnName="+e.GetParaString("EnName","")+"&PKVal="+e.No,new l(N.GoToUrl,n)}if(r=="SQL.Doc.CodeStruct"){e.Name=this.RequestVal("tb1","SQL"),e.No=this.RequestVal("tb2","SQL"),e.DBSrcType="SQL",e.CodeStruct=this.RequestVal("tb1","SQL.Doc.CodeStruct");let t="TS.FrmUI.SFTableSQLNoName";e.CodeStruct==1&&(t="TS.FrmUI.SFTableSQLTree"),e.SetPara("EnName",t),e.SelectStatement=this.RequestVal("tb1","SQL.Doc"),e.FK_SFDBSrc="local",e.FK_Val=e.No,yield e.Insert();const n=F.UrlEn(t,e.No);return new l(N.GoToUrl,n)}r=="Handler.Doc"&&(e.Name=this.RequestVal("tb1","Handler"),e.No=this.RequestVal("tb2","Handler"),e.DBSrcType="Handler"),r=="JavaScript.Doc"&&(e.Name=this.RequestVal("tb1","JavaScript"),e.No=this.RequestVal("tb2","JavaScript"),e.DBSrcType="JavaScript");let o="";if(r==="Handler.Doc"&&(o="TS.FrmUI.SFTableHandler"),r==="JavaScript.Doc"&&(o="TS.FrmUI.SFTableJS"),e.SelectStatement=s,e.FK_SFDBSrc=i,e.FK_Val=e.No,o!==""){e.SetPara("EnName",o),e.SelectStatement=s,e.FK_SFDBSrc=i,e.FK_Val=e.No,yield e.Insert();const t="/@/WF/Comm/En.vue?EnName="+o+"&PKVal="+e.No;return new l(N.GoToUrl,t)}})}}export{y as GPN_Local};
