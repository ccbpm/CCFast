var A=Object.defineProperty;var h=(a,n,e)=>n in a?A(a,n,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[n]=e;var c=(a,n,e)=>h(a,typeof n!="symbol"?n+"":n,e);var i=(a,n,e)=>new Promise((t,r)=>{var s=o=>{try{l(e.next(o))}catch(S){r(S)}},p=o=>{try{l(e.throw(o))}catch(S){r(S)}},l=o=>o.done?t(o.value):Promise.resolve(o.value).then(s,p);l((e=e.apply(a,n)).next())});import{I as b,cx as d,a0 as T,aP as D,aQ as u,K as f,U as g,L as w,Y as N}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class m extends b{constructor(e){super("TS.FrmUI.SFTableWebApiNoName");c(this,"descPost",`
  #### 帮助
   -  格式:  /xxx.do?userID=@WebUser.No&tike=@Token&workID=@WorkID&ndID=@NodeID&je=@JinE
   -  参数分为系统参与与自定义参数.
   -  系统参数: 登陆人员的信息,比如:@WebUser.No 登陆人员账号,@WebUser.Name 名称,@WebUser.DeptNo 部门编号,@WebUser.OrgNo 组织编号, @Token token.
   -  自定义参数: &je=@JE   金额是自定义参数. 

   #### 什么是调用主体？
  - 使用字典的对象就是调用主体, 调用主体大概是: 接受人规则、绑定下拉框字典、级联字典.
  - 调用主体在调用的环境里有一些数据，这些数据对当前字典来说都可以是参数.
  `);c(this,"descApi",`
  #### 帮助
   -  格式:  /xxx.do?userID=@WebUser.No&tike=@Token&workID=@WorkID&ndID=@NodeID&je=@JinE
   -  参数分为系统参与与自定义参数.
   -  系统参数: 登陆人员的信息,比如:@WebUser.No 登陆人员账号,@WebUser.Name 名称,@WebUser.DeptNo 部门编号,@WebUser.OrgNo 组织编号, @Token token.
   -  自定义参数: &je=@JE   金额是自定义参数. 

   #### 什么是调用主体？
  - 使用字典的对象就是调用主体, 调用主体大概是: 接受人规则、绑定下拉框字典、级联字典.
  - 调用主体在调用的环境里有一些数据，这些数据对当前字典来说都可以是参数.
  `);e&&this.setPKVal(e)}get HisUAC(){const e=new g;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new w("Sys_SFTable","WebAPI字典表");e.AddTBStringPK(d.No,null,"编号",!0,!0,1,200,200),e.AddTBString(d.Name,null,"名称",!0,!1,0,200,200),e.AddDDLSysEnum(d.CodeStruct,0,"字典表类型",!0,!1,d.CodeStruct,"@0=编号名称类型@1=树结构类型"),e.AddDDLEntities(d.FK_SFDBSrc,"local","数据源",new T,!1),e.AddDDLStringEnum("RequestMethod","Get","请求方式","@Get=Get@POST=POST",!0),e.AddTBString(d.SelectStatement,null,"接口地址",!0,!1,0,1e3,600,!0,this.descApi),e.AddTBStringDoc("PostDoc",null,"POST内容",!0,!1,!0,this.descPost),e.AddTBStringDoc("Remark",null,"参数别名",!0,!1,!0),e.AddGroupAttr("返回数据&列对应"),e.AddTBString("JsonNode","","Json节点名称",!0,!1,0,1e3,600,!1,"返回数据的跟节点,比如:contents"),e.AddTBString("FieldNo","","编号属性No",!0,!1,0,200,600,!0),e.AddTBString("FieldName","","名称属性Name",!0,!1,0,200,600,!0),e.AddGroupAttr("测试设置"),e.AddTBString("Test1",null,"接口地址",!0,!1,0,1e3,600,!0,this.descApi),e.AddTBStringDoc("Test2",null,"POST内容",!0,!1,!0),e.ParaFields=",Test1,Test2,",e.AddTBAtParas(4e3),e.AddGroupMethod("接口测试");const r=new N;r.Title="原始数据",r.RefMethodType=D.Func,r.Warning="",r.ClassMethod="DoUrl",e.AddRefMethod(r);const s=new N;return s.Title="结构化数据",s.RefMethodType=D.Func,s.Warning="",s.ClassMethod="DoCodeStruct",e.AddRefMethod(s),this._enMap=e,this._enMap}DoUrl(){return i(this,null,function*(){const e=new u("BP.Sys.SFTable",this.No);yield e.Init(),yield e.Retrieve();const t=yield e.DoMethodReturnString("TS_YuanShi_Data_WebApi_Test");return`tabOpen@原始数据:
`+JSON.stringify(t,null,2)})}DoCodeStruct(){return i(this,null,function*(){if(this.FieldNo&&this.FileName){const e=new u("BP.Sys.SFTable",this.No);yield e.Init(),yield e.Retrieve();const t=yield e.DoMethodReturnString("TS_YuanShi_Data_WebApi_CodeStruct_Test");return`tabOpen@结构数据:
`+JSON.stringify(t,null,2)}return this.DoUrl()})}DoParaUrl(e){return i(this,null,function*(){if(this.IsPara==0)return"tabOpen@该字典是无参字典，请使用无参方法测试.";const t=new u("BP.Sys.SFTable",this.No);yield t.Init(),yield t.Retrieve();const r=yield t.DoMethodReturnString("TS_YuanShi_Data_WebApi_Para",e);return"tabOpen@原始数据:"+JSON.stringify(r,null,2)})}DoParaCodeStract(e){return i(this,null,function*(){if(this.IsPara==0)return"tabOpen@该字典是无参字典，请使用无参方法测试.";const t=new u("BP.Sys.SFTable",this.No);yield t.Init(),yield t.Retrieve();const r=yield t.DoMethodReturnString("GenerJsonByPara",e);return`tabOpen@结构数据:
`+JSON.stringify(r,null,2)})}beforeUpdateInsertAction(){return i(this,null,function*(){const e=new T(this.FK_SFDBSrc);return yield e.Retrieve(),this.ConnString=e.ConnString,Promise.resolve(!0)})}}class W extends f{get GetNewEntity(){return new m}constructor(){super()}}export{m as SFTableWebApiNoName,W as SFTableWebApiNoNames};
