var p=Object.defineProperty;var F=(i,S,t)=>S in i?p(i,S,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[S]=t;var o=(i,S,t)=>F(i,typeof S!="symbol"?S+"":S,t);var D=(i,S,t)=>new Promise((N,s)=>{var l=r=>{try{e(t.next(r))}catch(a){s(a)}},b=r=>{try{e(t.throw(r))}catch(a){s(a)}},e=r=>r.done?N(r.value):Promise.resolve(r.value).then(l,b);e((t=t.apply(i,S)).next())});import{SFTable as u}from"./SFTable-BlM1UBse.js";import{b5 as h,G as n,l as m,D as d,B as x}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as T}from"./GloComm-CmAl8MpM.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class I extends h{constructor(){super("GPN_SFTable");o(this,"WebApi_Url",`
  #### 帮助
   - 请输入路径参数.
   - 仅仅输入主机端口号后面的部分.
   - 比如: /xxxx.do
  `);o(this,"SrcHelp",`
  #### 帮助
   - 请选择数据源，如果没有，请新建数据源.
   - 
  `);o(this,"SFTable",`
  #### 帮助
   - 内置字典表,比如: 省份，片区、城市、税种，税目
   - 内置字典表，是自己可以维护的表.
   - 存储在 Sys_SFTableDtl 表里. 
   - 用户可以通过ccfrom自己定义，自己维护的基础数据.
   
  `);o(this,"Handler",`
  #### 帮助
   - 优点:格式灵活,展现效果随心所欲.
   - 适用于:效果
   #### lisdxcx
   xxxxx
xxx      
  `);o(this,"SQL",`
  #### 帮助
   - 设置一个SQL语句从数据源中查询出来.
   - 支持ccbpm的表达式. @WebUser.No 当前用户编号， @WebUser.Name 登录名称， @WebUser.DeptNo 登录人所在部门.
   #### DEMO
   - 本部门的人员.
   - SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
   - 我的下级部门
   - SELECT No,Name FROM Port_Dept WHERE PartentNo='@WebUser.DeptNo'
xxx      
  `);o(this,"SQL_Doc",`
  #### 帮助
   - 设置一个SQL语句从数据源中查询出来.
   - 支持ccbpm的表达式. @WebUser.No 当前用户编号， @WebUser.Name 登录名称， @WebUser.DeptNo 登录人所在部门.
   #### DEMO
   - 本部门的人员.
   - SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'
   - 我的下级部门
   - SELECT No,Name FROM Port_Dept WHERE PartentNo='@WebUser.DeptNo'
xxx      
  `);o(this,"JavaScript",`
    #### 帮助
     - 暂无
     #### lisdxcx
     function Xxx()
     {
        
     }
xxx      
    `);o(this,"WebApi",`
  #### 帮助
   - 调用服务获得数据.
    
  `);o(this,"WebApi_Doc",`
  #### 帮助
  - 调用服务获得数据.
    
  `);o(this,"Docs1",`
  #### 帮助 
  - 暂无
  `);o(this,"Docs2",`
  #### 帮助
  - 暂无
    
  `);o(this,"Docs4",`
  #### 帮助
  - 填写格式: 枚举值,枚举标签; 
  - 例如: ty,团员;dy=党员;qz,群众; 
  - 系统解析为: ty是团员, dy是党员, qz是群众.

  #### 数据存储.
  - string类型的枚举也称为标记枚举,字母存储一个列,标签存储一个列.
  - 在表单里字段是abc,那系统就会自动创建一个影子字段 abcT.
  - abc字段存储的是标记, abcT存储的是标签.
  `);this.ForEntityClassID="TS.FrmUI.SFTable",this.PageTitle="新建字典"}Init(){this.AddGroup("A","新建字典"),this.TextBox2_NameNo("SFTableDict","内置字典表",this.SFTable,"SF_","字典ID","字典名称",""),this.SelectItemsByList("SFTableDict.CodeStruct","数据结构",this.SFTable,!1,this.GetCodeStruct()),this.TextBox2_NameNo("SQL","SQL查询字典表",this.SQL,"SQL_","字典ID","字典名称",""),this.TextSQL("SQL.Doc","填写SQL",this.SQL_Doc,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'","查询语句"),this.SelectItemsByList("SQL.Doc.CodeStruct","数据结构",this.SFTable,!1,this.GetCodeStruct()),this.TextBox2_NameNo("WebApi","WebApi接口字典表",this.WebApi,"WebApi_","字典ID","字典名称",""),this.TextSQL("WebApi.Doc","服务链接",this.WebApi_Url,"路径与参数","/xxx.do","输入主机的后部分"),this.SelectItemsByList("WebApi.Doc.CodeStruct","数据结构",this.SFTable,!1,this.GetCodeStruct()),this.TextBox2_NameNo("Handler","微服务Handler字典表",this.Handler,"Handler_","字典ID","字典名称",""),this.TextSQL("Handler.Doc","填写内容",this.SQL_Doc,"查询SQL","SELECT No,Name FROM Port_Emp WHERE FK_Dept='@WebUser.DeptNo'","查询语句"),this.TextBox2_NameNo("JavaScript","JavaScript字典表",this.JavaScript,"JS_","字典ID","字典名称",""),this.TextArea("JavaScript.Doc","填写方法名",this.SQL_Doc,"方法名"," MyDict() ","Javascript的方法名"),this.AddGroup("B","相关功能"),this.AddFunction("AdminDBSrc_ToUrl","数据源维护",this.GotoUrl)}GetCodeStruct(){return JSON.stringify([{No:"0",Name:"编号名称"},{No:"1",Name:"树结构"}])}GotoUrl(){const t="/@/WF/Comm/Search.vue?EnName=TS.Sys.SFDBSrc";return new n(m.GoToUrl,t)}GenerSorts(){return D(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,N,s,l,b){return D(this,null,function*(){const e=new u;if(e.Name=s,e.No=l,e.TableDesc=b,e.RDT=d.CurrentDateTime,t.includes(".")==!1&&(yield e.IsExits()))throw new Error("编号:"+e.No+"已存在.");if(t==="SFTableDict.CodeStruct"){e.Name=this.RequestVal("tb1","SFTableDict"),e.No=this.RequestVal("tb2","SFTableDict"),e.FK_SFDBSrc="local",e.DBSrcType="SysDict",e.DBType=0,e.FK_Val=e.No,e.NoGenerModel=1,e.CodeStruct=this.RequestVal("tb1","SFTableDict.CodeStruct"),e.CodeStruct==0?e.SetPara("EnName","TS.FrmUI.SFTableDictNoName"):e.SetPara("EnName","TS.FrmUI.SFTableDictTree"),yield e.Insert();const a=new x("BP.Sys.SFTable",e.No);yield a.Retrieve(),yield a.DoMethodReturnString("GenerDataOfJson");let c="";return c="/@/WF/Comm/En.vue?EnName="+e.GetParaString("EnName","")+"&PKVal="+e.No,new n(m.GoToUrl,c)}if(t=="SQL.Doc.CodeStruct"){e.Name=this.RequestVal("tb1","SQL"),e.No=this.RequestVal("tb2","SQL"),e.DBSrcType="SQL",e.CodeStruct=this.RequestVal("tb1","SQL.Doc.CodeStruct");let a="TS.FrmUI.SFTableSQLNoName";e.CodeStruct==1&&(a="TS.FrmUI.SFTableSQLTree"),e.SetPara("EnName",a),e.SelectStatement=this.RequestVal("tb2","SQL.Doc"),e.FK_SFDBSrc=this.RequestVal("tb1","SQL.Doc"),e.FK_Val=e.No,yield e.Insert();const c=T.UrlEn(a,e.No);return new n(m.GoToUrl,c)}if(t=="WebApi.Doc.CodeStruct"){e.Name=this.RequestVal("tb1","WebApi"),e.No=this.RequestVal("tb2","WebApi"),e.DBSrcType="WebApi",e.CodeStruct=this.RequestVal("tb1","WebApi.Doc.CodeStruct");let a="TS.FrmUI.SFTableWebApiNoName";e.CodeStruct==1&&(a="TS.FrmUI.SFTableWebApiTree"),e.SetPara("EnName",a),e.SelectStatement=this.RequestVal("tb2","WebApi.Doc"),e.FK_SFDBSrc=this.RequestVal("tb1","WebApi.Doc"),e.FK_Val=e.No,yield e.Insert();const c=T.UrlEn(a,e.No);return new n(m.GoToUrl,c)}t=="Handler.Doc"&&(e.Name=this.RequestVal("tb1","Handler"),e.No=this.RequestVal("tb2","Handler"),e.DBSrcType="Handler"),t=="JavaScript.Doc"&&(e.Name=this.RequestVal("tb1","JavaScript"),e.No=this.RequestVal("tb2","JavaScript"),e.DBSrcType="JavaScript");let r="";if(t==="Handler.Doc"&&(r="TS.FrmUI.SFTableHandler"),t==="JavaScript.Doc"&&(r="TS.FrmUI.SFTableJS"),e.SelectStatement=l,e.FK_SFDBSrc=s,e.FK_Val=e.No,r!==""){e.SetPara("EnName",r),e.SelectStatement=l,e.FK_SFDBSrc=s,e.FK_Val=e.No,yield e.Insert();const a="/@/WF/Comm/En.vue?EnName="+r+"&PKVal="+e.No;return new n(m.GoToUrl,a)}})}}export{I as GPN_SFTable};
