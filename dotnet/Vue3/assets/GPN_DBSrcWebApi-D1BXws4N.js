var W=Object.defineProperty;var x=(s,i,r)=>i in s?W(s,i,{enumerable:!0,configurable:!0,writable:!0,value:r}):s[i]=r;var o=(s,i,r)=>x(s,typeof i!="symbol"?i+"":i,r);var l=(s,i,r)=>new Promise((m,c)=>{var a=t=>{try{e(r.next(t))}catch(S){c(S)}},n=t=>{try{e(r.throw(t))}catch(S){c(S)}},e=t=>t.done?m(t.value):Promise.resolve(t.value).then(a,n);e((r=r.apply(s,i)).next())});import{SFProcedure as T}from"./SFProcedure-CLBeHp-Y.js";import{S as d}from"./DBAccess-CzjFzLoq.js";import{b9 as D,aB as p,aC as N,aE as u,aD as h}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as b}from"./GloComm-DZ1gELjv.js";import"./SFPara-DL_8hzxu.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFParaSln-B7KC4nzL.js";import"./SFColumn-Q_PoS_2g.js";import"./FrmTrack-0uAZQ3B_.js";class G extends D{constructor(){super("GPN_DBSrcWebApi");o(this,"HelpSearch",`
  #### 帮助
  - 查询与字典表不同，他需要参数据才能执行.
  #### 用到场景
  - 文本框自动完成, 级联下拉框、自动填充.
  `);o(this,"WebApi_Url",`
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
  `);this.PageTitle="新建"}Init(){this.AddGroup("A","新建字典","icon-list"),this.TextBox2_NameNo("WebApi","WebApi接口字典表",this.WebApi,"WebApi_","字典ID","字典名称",""),this.TextArea("WebApi.Doc","服务链接",this.WebApi_Url,"路径与参数","/xxx.do","输入主机的后部分"),this.SelectItemsByList("WebApi.Doc.CodeStruct","数据结构",this.HelpTodo,!1,this.GetCodeStruct()),this.AddGroup("B","新建查询","icon-layers"),this.TextBox3_NameNoNote("Search","创建查询",this.HelpSearch,"S","编号","名称","备注","我的查询"),this.AddIcon("icon-layers","Search"),this.AddGroup("C","新建过程","icon-options"),this.TextBox3_NameNoNote("Procedure","创建过程",this.HelpSearch,"S","编号","名称","备注","我的过程"),this.AddIcon("icon-options","Procedure")}GetCodeStruct(){return JSON.stringify([{No:"0",Name:"编号名称"},{No:"1",Name:"树结构"}])}GenerSorts(){return l(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(r,m,c,a,n){return l(this,null,function*(){if(r=="Search"){const e=new d;if(e.No=a,(yield e.IsExits())==!0)return new p(N.Error,"编号["+a+"]已经存在.");e.Name=c,e.FK_SFDBSrc=this.RefPKVal,e.Remark=n,e.SetPara("EnName","TS.FrmUI.SFSearchWebApi"),yield e.Insert();const t=b.UrlEn("TS.FrmUI.SFSearchWebApi",e.No);return new p(N.GoToUrl,t)}if(r=="Procedure"){const e=new T;if(e.No=a,(yield e.IsExits())==!0)return new p(N.Error,"编号["+a+"]已经存在.");e.Name=c,e.FK_SFDBSrc=this.RefPKVal,e.Remark=n,e.SetPara("EnName","TS.FrmUI.SFProcedureWebApi"),yield e.Insert();const t=b.UrlEn("TS.FrmUI.SFProcedureWebApi",e.No);return new p(N.GoToUrl,t)}if(r=="WebApi"){const e=new u;if(e.Name=c,e.No=a,e.TableDesc=n,e.RDT=h.CurrentDateTime,r.includes(".")==!1&&(yield e.IsExits()))throw new Error("编号:"+e.No+"已存在.")}if(r=="WebApi.Doc.CodeStruct"){const e=new u;e.Name=this.RequestVal("tb1","WebApi"),e.No=this.RequestVal("tb2","WebApi"),e.DBSrcType="WebApi",e.CodeStruct=this.RequestVal("tb1","WebApi.Doc.CodeStruct");let t="TS.FrmUI.SFTableWebApiNoName";e.CodeStruct==1&&(t="TS.FrmUI.SFTableWebApiTree"),e.SetPara("EnName",t),e.SelectStatement=this.RequestVal("tb1","WebApi.Doc"),e.FK_SFDBSrc=this.RefPKVal,e.FK_Val=e.No,yield e.Insert();const S=b.UrlEn(t,e.No);return new p(N.GoToUrl,S)}})}}export{G as GPN_DBSrcWebApi};
