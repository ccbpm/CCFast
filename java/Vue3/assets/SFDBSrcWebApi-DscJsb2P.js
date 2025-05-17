var s=Object.defineProperty;var p=(t,r,e)=>r in t?s(t,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[r]=e;var n=(t,r,e)=>p(t,typeof r!="symbol"?r+"":r,e);import{E as d,U as S,h as a,i as c}from"./entry/index-C6uBgOW5-1730430676707.js";import{SFDBSrcAttr as i}from"./SFDBSrc-DKIMsnoa.js";import{b as m}from"./DBAccess-sLO0RM-h.js";import{SFProcedures as u}from"./SFProcedure-CE1gQz0T.js";import{GPN_SFTableWebAPI as A}from"./GPN_SFTableWebAPI-BxuD2fqC.js";import{SFTableWebApiNoNames as D}from"./SFTableWebApiNoName-BiXZ3im8.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFParaSln-BnGoh0gJ.js";import"./GloComm-CmAl8MpM.js";import"./FrmTrack-BAfWiAdt.js";class x extends d{constructor(e){super("TS.Sys.SFDBSrcWebApi");n(this,"WebAPI",`
  #### 字典
  - 应用的场景-外部数据源,表单下拉字段级联关系,接收人规则
  #### 查询
  - 应用的场景-pop填充,装载填充,从表填充,
  #### 过程
  - 应用的场景-流程事件,节点事件
  #### 通用配置
  - 1.请求方式分为Get、Post方法
  - 2.请求方式为Get方式时，路径的配置方式为/xxxx?UserID=@WebUser.No&Ticket=@Token&workID=@WorkID&JE=@JinE
       请求方式为Post方式时，路径的配置方式为/xxx,post内容为JSON格式,例如：
       {
          "UserID":"@WebUser.No",
          "Ticket":"@Token",
          "WorkID":"@WorkID",
          "JE":@JinE
        }
  - 3.参数别名:当使用的位置的替换的参数值与设置的参数不一致时，需要设置参数别名,例如:@JinE=JE,ZongJinE,HuaFeiJinE@WorkID=OID,FID等
  `);e&&this.setPKVal(e)}get HisUAC(){const e=new S;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new a("Sys_SFDBSrc","webApi数据源");e.AddTBStringPK(i.No,null,"编号",!0,!0,1,20,20),e.AddTBString(i.Name,null,"名称",!0,!1,0,30,20),e.AddTBString(i.DBSrcType,"WebApi","类型",!0,!0,0,30,20),e.AddTBString(i.ConnString,null,"连接",!0,!1,0,300,20,!0),e.AddGroupMethod("基本设置"),e.AddRM_GPN(new A,"icon-drop");const o=new c;return o.Title="连接测试",o.ClassMethod="TestConn",o.Warning="",e.AddRefMethod(o),e.AddGroupMethod("列表"),e.AddRM_DtlSearch("字典",new D,"FK_SFDBSrc","","","No,Name,CodeStruct,RequestMethod,ParaMethod,","icon-drop"),e.AddRM_DtlSearch("查询",new m,"FK_SFDBSrc","","","","icon-drop"),e.AddRM_DtlSearch("过程",new u,"FK_SFDBSrc","","","","icon-drop"),e.AddRM_HelpDocs("帮助","/src/WF/Comm/HelpDocs.vue?key=WebAPI",this.WebAPI,"icon-support"),this._enMap=e,this._enMap}TestConn(){return"测试成功."}}export{x as SFDBSrcWebApi};
