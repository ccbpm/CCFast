var l=Object.defineProperty;var S=(i,s,t)=>s in i?l(i,s,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[s]=t;var o=(i,s,t)=>S(i,typeof s!="symbol"?s+"":s,t);var m=(i,s,t)=>new Promise((b,p)=>{var c=r=>{try{e(t.next(r))}catch(a){p(a)}},N=r=>{try{e(t.throw(r))}catch(a){p(a)}},e=r=>r.done?b(r.value):Promise.resolve(r.value).then(c,N);e((t=t.apply(i,s)).next())});import{SFTable as x}from"./SFTable-BlM1UBse.js";import{b5 as n,D as W,G as D,l as T}from"./entry/index-C6uBgOW5-1730430676707.js";import{GloComm as E}from"./GloComm-CmAl8MpM.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";class I extends n{constructor(){super("GPN_SFTableWebAPI");o(this,"WebApi_Url",`
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
  `);this.PageTitle="新建字典",this.ForEntityClassID="TS.FrmUI.SFTableWebApiNoName"}Init(){this.AddGroup("A","数据源类型"),this.TextBox2_NameNo("WebApi","WebApi接口字典表",this.WebApi,"WebApi_","字典ID","字典名称",""),this.TextArea("WebApi.Doc","服务链接",this.WebApi_Url,"路径与参数","/xxx.do","输入主机的后部分"),this.SelectItemsByList("WebApi.Doc.CodeStruct","数据结构",this.HelpTodo,!1,this.GetCodeStruct())}GetCodeStruct(){return JSON.stringify([{No:"0",Name:"编号名称"},{No:"1",Name:"树结构"}])}GenerSorts(){return m(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,b,p,c,N){return m(this,null,function*(){const e=new x;if(e.Name=p,e.No=c,e.TableDesc=N,e.RDT=W.CurrentDateTime,t.includes(".")==!1&&(yield e.IsExits()))throw new Error("编号:"+e.No+"已存在.");if(t=="WebApi.Doc.CodeStruct"){e.Name=this.RequestVal("tb1","WebApi"),e.No=this.RequestVal("tb2","WebApi"),e.DBSrcType="WebApi",e.CodeStruct=this.RequestVal("tb1","WebApi.Doc.CodeStruct");let r="TS.FrmUI.SFTableWebApiNoName";e.CodeStruct==1&&(r="TS.FrmUI.SFTableWebApiTree"),e.SetPara("EnName",r),e.SelectStatement=this.RequestVal("tb1","WebApi.Doc"),e.FK_SFDBSrc=this.RefPKVal,e.FK_Val=e.No,yield e.Insert();const a=E.UrlEn(r,e.No);return new D(T.GoToUrl,a)}})}}export{I as GPN_SFTableWebAPI};
