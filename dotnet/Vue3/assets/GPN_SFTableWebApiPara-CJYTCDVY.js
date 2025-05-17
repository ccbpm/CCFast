var x=Object.defineProperty;var h=(i,r,e)=>r in i?x(i,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):i[r]=e;var a=(i,r,e)=>h(i,typeof r!="symbol"?r+"":r,e);var p=(i,r,e)=>new Promise((P,o)=>{var l=s=>{try{t(e.next(s))}catch(n){o(n)}},c=s=>{try{t(e.throw(s))}catch(n){o(n)}},t=s=>s.done?P(s.value):Promise.resolve(s.value).then(l,c);t((e=e.apply(i,r)).next())});import{SFPara as U}from"./SFPara-DL_8hzxu.js";import{b9 as E,aB as m,aC as D}from"./entry/index-M8VErHPE-1727507756861.js";import{GloComm as d}from"./GloComm-DZ1gELjv.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFColumn-Q_PoS_2g.js";class K extends E{constructor(){super("GPN_SFTableWebApiPara");a(this,"WebApi_Url",`
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
   xxxxx
xxx      
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
  `);this.PageTitle="参数",this.ForEntityClassID="TS.FrmUI.SFParaSelf"}Init(){this.AddGroup("A","内部参数"),this.AddBlank("@WebUserNo","当前登陆人员账号",this.HelpUn),this.AddBlank("@WebUserName","登陆人名称",this.HelpUn),this.AddBlank("@WebUserDeptNo","登陆人部门编号",this.HelpUn),this.AddBlank("@FrmDataMain","调用的表单主表数据",this.HelpUn),this.AddBlank("@FrmDataDtlMain","调用的表单主表+从表数据",this.HelpUn),this.AddBlank("@FrmDataDtlAthMain","调用的表单主表+从表+附件数据",this.HelpUn),this.AddBlank("@FrmID","表单ID",this.HelpUn),this.AddBlank("@OID","OID/WorkID",this.HelpUn),this.AddBlank("@FlowNo","流程编号",this.HelpUn),this.AddBlank("@NodeID","节点ID",this.HelpUn),this.AddGroup("B","外部参数"),this.TextBox2_NameNo("Self","自定义参数",this.HelpUn,"","参数Key","参数名称","")}GetCodeStruct(){return JSON.stringify([{No:"0",Name:"编号名称"},{No:"1",Name:"树结构"}])}GenerSorts(){return p(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,P,o,l,c){return p(this,null,function*(){const t=new U;if(e.includes("@")==!0){e=e.replace("@","");const S=this.RefPKVal+"_"+e;if(t.setPKVal(S),yield t.IsExits())return new m(D.Error,"该参数已经存在.");t.Name=e,t.RefPKVal=this.RefPKVal,t.ParaKey=e,t.ParaName=this.GetPageName(e),t.SetPara("EnName","TS.FrmUI.SFParaSys"),t.setPKVal(S),t.DataType="String",(e=="OID"||e=="NodeID")&&(t.DataType="Int"),(e=="FrmDataMain"||e=="FrmDataDtlMain"||e=="FrmDataDtlAthMain")&&(t.DataType="Json"),t.IsSys=0,yield t.DirectInsert();const N=d.UrlEn(t.GetParaString("EnName",""),t.MyPK);return new m(D.GoToUrl,N)}const s=this.RefPKVal+"_"+l;if(t.setPKVal(s),yield t.Init(),yield t.IsExits())return new m(D.Error,"该参数已经存在.");t.Name=e,t.RefPKVal=this.RefPKVal,t.ParaKey=l,t.ParaName=o,t.SetPara("EnName","TS.FrmUI.SFParaSelf"),t.setPKVal(s),t.DataType="String",t.IsSys=1,yield t.DirectInsert();const n=d.UrlEn(t.GetParaString("EnName",""),t.MyPK);return new m(D.GoToUrl,n)})}}export{K as GPN_SFTableWebApiPara};
