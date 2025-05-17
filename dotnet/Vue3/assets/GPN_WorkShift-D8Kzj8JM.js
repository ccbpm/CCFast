var d=Object.defineProperty;var y=(r,o,t)=>o in r?d(r,o,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[o]=t;var p=(r,o,t)=>y(r,typeof o!="symbol"?o+"":o,t);var l=(r,o,t)=>new Promise((n,i)=>{var s=e=>{try{a(t.next(e))}catch(c){i(c)}},u=e=>{try{a(t.throw(e))}catch(c){i(c)}},a=e=>e.done?n(e.value):Promise.resolve(e.value).then(s,u);a((t=t.apply(r,o)).next())});import{TransferWork as D}from"./restApi-DSjrn32D.js";import{b9 as F,aM as m,W as N,H as T}from"./entry/index-M8VErHPE-1727507756861.js";import{e as f}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";class E extends F{constructor(){super("GPN_WorkShift");p(this,"Docs0",`
  #### 帮助
  - 按照节点表单的字段作为抄送人.
  - 通常是在节点表单上加一个字段,这个字段存储的是人员账号，多个人员使用逗号分开.
  #### 运行图例
  - @liang.

`);p(this,"Docs1",`
  #### 帮助
  - 自动抄送给要绑定的人员.
`);p(this,"Docs2",`
  #### 帮助
  - 按照绑定的部角色下的人员集合作为抄送人.
  - 有一个规则
  
`);this.PageTitle="工作移交"}Init(){return l(this,null,function*(){this.AddGroup("A","请选择规则"),this.SelectItemsByList("Flows","选择待办流程",this.Docs0,!0,this.getTodoList),this.SelectItemsByTreeEns("Flows.Emps","选择被移交人",this.Docs1,!1,m.srcDeptLazily,m.srcDeptRoot,m.srcEmpLazily,"@No=账号@Name=名称@Tel=电话")})}getTodoList(){return l(this,null,function*(){const n=yield new T("BP.WF.HttpHandler.WF").DoMethodReturnJson("Todolist_Init");if(!Array.isArray(n))throw new Error("获取待办列表失败，请检查数据源");const i=[];for(const s of n)(s.AtPara||"").includes("@IsCC=1")||parseInt(s.WFState)!==1&&(s.No=s.WorkID,s.Name=s.FlowName+" (标题："+s.Title+")",i.push(s));return JSON.stringify(i)})}GenerSorts(){return l(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,n,i,s,u){return l(this,null,function*(){if(t==="Flows.Emps"){const a=window.prompt("请输入移交原因");if(!(a!=null&&a.trim())){f.warn("需要输入移交原因");return}const e=this.RequestVal("tb1","Flows"),c=this.RequestVal("tb1","Flows.Emps"),h=N.Token,w=yield D(e,c,a,h);f.info(w.data);return}})}}export{E as GPN_WorkShift};
