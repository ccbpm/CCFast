var g=Object.defineProperty;var D=(s,r,e)=>r in s?g(s,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):s[r]=e;var a=(s,r,e)=>D(s,typeof r!="symbol"?r+"":r,e);var l=(s,r,e)=>new Promise((t,o)=>{var p=n=>{try{i(e.next(n))}catch(d){o(d)}},u=n=>{try{i(e.throw(n))}catch(d){o(d)}},i=n=>n.done?t(n.value):Promise.resolve(n.value).then(p,u);i((e=e.apply(s,r)).next())});import{j as T,f as A,B as N,U,h as W,i as h}from"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class S extends T{constructor(e){super("TS.WF.ARBindWebApi");a(this,"Help",`
  ####  帮助
  - 请输入完整的url 比如: http:/ccbpm.cn:9090/xxx.do?xx=xx&WorkID=@WorkID&Token=@WwebUser.Token
  - 内部参数格式: @WebUser.* 是当前登录人员的信息.
  - 表单参数: @+字段名, 比如审批金额: @SPJE   
  - 流程参数： @WorkID , @NodeID  两个固定的参数.
  ####  对于post模式.
  - 请参考 post填写帮助.
  ####  返回值格式.
  - 必须返回: zhangsan,lisi,wangwu 这样的格式.
  `);a(this,"HelpParaModel",`
  #### 自定义模式
  - 在【自定义数据内容】输入json字符串，字符串内可以使用@+字段名.
  - 比如：
  {
    QingJiaTianShu:@QingJiaTianShu
    WorkID:@WorkID
    NodeID:@FK_Node
    User:@WebUser.No
  }
  - 参数里包括三类:
  - 1. 内置参数@WebUser.* ,   解释: 当前登录人员的信息, @WebUer.No,  @WebUer.Nme,  @WebUer.FK_Dept,  @WebUer.DeptName,  @WebUer.OrgNo
  - 2. 表单参数:@表单字段     解释: 表单的英文字段名.
  - 3. 流程参数:@WorkID   @NodeID    解释:当前的工作ID，节点ID.
  #### 全量模式
  - 把表单字段, 作为一个大的json传入接口里面去.
  - 接口开发者，获得表单数据进行有效的利用，生成下一步工作人员，返回过来。
  `);a(this,"helpJsonNode",`
  #### 帮助
  - 返回的JSON从那个节点下开始获取数据，如果没有节点就不需要设置.
  #### 比如1：返回数据
   {
        "contents": [
          {
             "UserNo": "string",
             "UserName": "string",
          },
          {
              "UserNo": "string",
               "UserName": "string",
          }
        ]
   }

   - 这个模式需要配置节点ID: contents
   #### 比如2：返回数据
    [
          {
             "UserNo": "string",
             "UserName": "string",
          },
          {
              "UserNo": "string",
               "UserName": "string",
          }
        ]

   - 该模式不需要配置.
  `);e&&this.setPKVal(e)}get HisUAC(){const e=new U;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new W("WF_Part","绑定WebApi");e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddTBString("FlowNo",null,"FlowNo",!1,!1,0,800,100,!1),e.AddTBInt("NodeID",0,"NodeID",!1,!1),e.AddTBString("Tag0",null,"Url",!0,!1,0,800,100,!0,this.Help),e.AddDDLStringEnum("Tag1","Get","请求模式","@Get=Get模式@POST=Post模式",!0,""),e.AddTBStringDoc("Tag9",null,"备注",!0,!1,!0,this.Help),e.AddGroupAttr("POST设置"),e.AddDDLStringEnum("Tag2","0","参数模式","@0=自定义模式@1=全量模式",!0,this.HelpParaModel),e.AddTBStringDoc("Tag3",null,"自定义数据内容",!0,!1,!0,this.Help),e.AddDDLStringEnum("Tag4","0","数据格式","@0=From格式@1=JSON格式",!0,this.HelpParaModel);const t=new h;return t.Title="执行测试",t.RefMethodType=A.Func,t.HisMap.AddTBString("P",null,"输入参数",!0,!1,0,2e3,300,!0,"格式@BU=1001@PDT=1002"),t.Warning="您确定要执行吗？",t.ClassMethod="DoTest",e.AddRefMethod(t),this._enMap=e,this._enMap}DoTest(e){return l(this,null,function*(){const t=new N("BP.WF.Template.Part",this.MyPK);return yield t.Init(),yield t.Retrieve(),"tabOpen@接受人:"+(yield t.DoMethodReturnString("DoTestARWebApi",e))})}}export{S as ARBindWebApi};
