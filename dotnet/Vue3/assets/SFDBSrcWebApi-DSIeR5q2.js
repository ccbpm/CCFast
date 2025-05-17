var i=Object.defineProperty;var d=(t,r,e)=>r in t?i(t,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[r]=e;var s=(t,r,e)=>d(t,typeof r!="symbol"?r+"":r,e);import{I as p,cw as n,U as S,L as a,Y as c}from"./entry/index-M8VErHPE-1727507756861.js";import{b as u}from"./DBAccess-CzjFzLoq.js";import{SFProcedures as m}from"./SFProcedure-CLBeHp-Y.js";import{GPN_SFTableWebAPI as A}from"./GPN_SFTableWebAPI-W6A-3XV3.js";import{SFTableWebApiNoNames as D}from"./SFTableWebApiNoName-DIt2WvlH.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./SFParaSln-B7KC4nzL.js";import"./GloComm-DZ1gELjv.js";import"./FrmTrack-0uAZQ3B_.js";class k extends p{constructor(e){super("TS.Sys.SFDBSrcWebApi");s(this,"WebAPI",`
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
  `);e&&this.setPKVal(e)}get HisUAC(){const e=new S;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new a("Sys_SFDBSrc","webApi数据源");e.AddTBStringPK(n.No,null,"编号",!0,!0,1,20,20),e.AddTBString(n.Name,null,"名称",!0,!1,0,30,20),e.AddTBString(n.DBSrcType,"WebApi","类型",!0,!0,0,30,20),e.AddTBString(n.ConnString,null,"连接",!0,!1,0,300,20,!0),e.AddGroupMethod("基本设置"),e.AddRM_GPN(new A,"icon-drop");const o=new c;return o.Title="连接测试",o.ClassMethod="TestConn",o.Warning="",e.AddRefMethod(o),e.AddGroupMethod("列表"),e.AddRM_DtlSearch("字典",new D,"FK_SFDBSrc","","","No,Name,CodeStruct,RequestMethod,ParaMethod,","icon-drop"),e.AddRM_DtlSearch("查询",new u,"FK_SFDBSrc","","","","icon-drop"),e.AddRM_DtlSearch("过程",new m,"FK_SFDBSrc","","","","icon-drop"),e.AddRM_HelpDocs("帮助","/src/WF/Comm/HelpDocs.vue?key=WebAPI",this.WebAPI,"icon-support"),this._enMap=e,this._enMap}TestConn(){return"测试成功."}}export{k as SFDBSrcWebApi};
