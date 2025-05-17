var s=Object.defineProperty;var l=(r,t,e)=>t in r?s(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var a=(r,t,e)=>l(r,typeof t!="symbol"?t+"":t,e);import{E as n,U as u,h as i}from"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class m extends n{constructor(e){super("TS.WF.Template.FlowTestParaSetting");a(this,"DescTestSysPara",`
  #### 帮助
  - 流程系统参数是流程实例在运行过程中，由外部接口传递写入的参数。
  - 参数存储在WF_GenerWorkFlow表的 AtPara字段里面，格式为 @Key1=Val1@Key2=Val2
  - 调用接口 Flow_SaveParas(workID, paras) 可以保存或者更改系统参数. 
  - 该参数可以用到，方向条件、接受人规则、表单的url参数.
  #### 填写示例
  @QingJiaTianShu=14
  #### 常见的固定参数约定.
  - 发起流程传入的嵌入模式的表单url参数. 参数键值:  
  - 示例: 

  #### 嵌入嵌入式表单url地址参数
  - 参数键值: FrmUrl
  - 示例:

  `);a(this,"TestFrmPara",`
  #### 帮助
  - 表单参数是测试的时候，为了避免重复录入在发起之前设置的默认字段值.
  - 启动流程的时候，系统自动按照设置的数据填充到节点表单，然后启动流程.
  #### 参数示例
  @XMMC=驰骋BPM工作流引擎项目
  @XMJE=100
  @XMDZ=山东济南高新区.碧桂园凤凰中心.A座1903
  
  
  `);e&&this.setPKVal(e)}get HisUAC(){const e=new u;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new i("WF_Flow","测试参数");return e.AddTBStringPK("No",null,"编号",!0,!0,1,3,50),e.AddTBString("Name",null,"名称",!0,!0,0,50,500),e.AddTBStringDoc("TestSysPara",null,"流程系统参数",!0,!1,!0,this.DescTestSysPara),e.AddTBStringDoc("TestFrmPara",null,"表单填充数据",!0,!1,!0,this.TestFrmPara),this._enMap=e,this._enMap}}export{m as FlowTestParaSetting};
