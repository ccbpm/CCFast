var x=Object.defineProperty;var m=(r,t,e)=>t in r?x(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var l=(r,t,e)=>m(r,typeof t!="symbol"?t+"":t,e);var o=(r,t,e)=>new Promise((a,u)=>{var p=n=>{try{d(e.next(n))}catch(i){u(i)}},c=n=>{try{d(e.throw(n))}catch(i){u(i)}},d=n=>n.done?a(n.value):Promise.resolve(n.value).then(p,c);d((e=e.apply(r,t)).next())});import{N as D,U as S,L as g}from"./entry/index-M8VErHPE-1727507756861.js";import{SysEventAttr as s}from"./SysEvent-BDHAFjMW.js";import{SFProcedure as A}from"./SFProcedure-CLBeHp-Y.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFPara-DL_8hzxu.js";import"./SFParaSln-B7KC4nzL.js";class f extends D{constructor(e){super("TS.Sys.SysEventSFWebApi");l(this,"Help",`
  ####  帮助
  ####  POST模式
  - 输入完整的url 比如: http:/ccbpm.cn:9090?xx=@FK_Flow&xx=@WebUser.No&xx=@WebUser.Name&xx=@WebUser.FK_Dept&xx=@FK_Node&xx=@FrmID&xx=@
  WorkID&xx=@WebUser.OrgNo&xx=@表单字段英文名
  - 全量模式会对表单的全部字段数据进行格式封装例如json格式{"AtPara":"","SQR":"admin","Rec":"admin","RDT":"2024-05-14".......}
  - 对于全量模式java接口定义规则如下
    @PostMapping("/ceshi")
    @ResponseBody
    public  Object ceshi(@RequestBody Map<String,Object> map, String token, String nodeId) 
  - 对于全量模式net接口定义规则如下
    [HttpPost]
    public object ceshi([FromBody] dynamic data, string token, string workiD)
  ####  对于GET模式.
  - 输入完整的url 比如: http:/ccbpm.cn:9090?xx=@FK_Flow&xx=@WebUser.No&xx=@WebUser.Name&xx=@WebUser.FK_Dept&xx=@FK_Node&xx=@FrmID&xx=@
  WorkID&xx=@WebUser.OrgNo&xx=@表单字段英文名
  ####  返回值格式.
  - {"message":"执行成功","code":200,"data":"","msg":"执行成功"}
  - {"message":"执行失败","code":500,"data":"","msg":"执行成功"}
  `);e&&(this.MyPK=e)}get HisUAC(){const e=new S;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new g("Sys_FrmEvent","服务事件");return e.AddMyPK(),e.AddTBString(s.RefPKVal,null,"关键值",!1,!0,0,100,10),e.AddTBString(s.EventID,null,"事件标记",!0,!0,0,100,150),e.AddTBString(s.EventName,null,"事件名称",!0,!0,0,100,150),e.AddTBString(s.EventDoType,null,"执行标记",!0,!0,0,100,100),e.AddTBString(s.EventDoTypeT,null,"执行标记T",!1,!0,0,100,100),e.AddTBString("FrmID",null,"表单ID",!0,!0,0,100,10),e.AddTBString("FK_Flow",null,"流程编号",!0,!0,0,100,10),e.AddTBInt("FK_Node",0,"节点ID",!0,!0),e.AddTBString("DoDoc",null,"Url",!0,!1,0,800,100,!0,this.Help),e.AddDDLStringEnum("PostModel","Get","请求模式","@Get=Get模式@POST=Post模式",!0,""),e.AddTBStringDoc("DoDocT",null,"备注",!0,!1,!0,this.Help),e.AddGroupAttr("POST设置"),e.AddDDLStringEnum("ParaMoel","0","参数模式","@0=自定义模式@1=全量模式",!0,this.HelpParaModel),e.AddTBStringDoc("ParaDocs",null,"自定义数据内容",!0,!1,!0,this.Help),e.AddDDLStringEnum("ParaDTModel","1","数据格式","@0=From格式@1=JSON格式",!0,this.HelpParaModel),this._enMap=e,this._enMap}GenerParas(){return o(this,null,function*(){const e=new A(this.DoDoc);yield e.Retrieve();const a=this.MyPK;return yield e.AddSln(a,this.FrmID)})}beforeInsert(){return o(this,null,function*(){return Promise.resolve(!0)})}beforeUpdateInsertAction(){return o(this,null,function*(){return Promise.resolve(!0)})}}export{f as SysEventSFWebApi};
