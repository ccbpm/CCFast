var a=(u,e,n)=>new Promise((i,l)=>{var d=t=>{try{s(n.next(t))}catch(o){l(o)}},p=t=>{try{s(n.throw(t))}catch(o){l(o)}},s=t=>t.done?i(t.value):Promise.resolve(t.value).then(d,p);s((n=n.apply(u,e)).next())});import{N as c,U as D,L as S}from"./entry/index-M8VErHPE-1727507756861.js";import{SysEventAttr as r}from"./SysEvent-BDHAFjMW.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class U extends c{constructor(e){super("TS.Sys.SysEventURL"),e&&(this.MyPK=e)}get HisUAC(){const e=new D;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new S("Sys_FrmEvent","服务事件");return e.AddMyPK(),e.AddTBString(r.RefPKVal,null,"关键值",!1,!1,0,100,10),e.AddTBString(r.EventID,null,"事件标记",!0,!0,0,100,150),e.AddTBString(r.EventName,null,"事件名称",!0,!0,0,100,150),e.AddTBString(r.EventDoType,null,"执行标记",!1,!1,0,100,100),e.AddTBString(r.EventDoTypeT,null,"执行标记",!0,!0,0,100,100),e.AddTBStringDoc(r.DoDoc,null,"URL/WebServices",!0,!1,!0,`
    #### 帮助
    - 请填写SQL或者存储过程
    - 支持ccbpm表达式.
    #### SQL的demo.
    - 支持ccbpm表达式.
    - UPDATE MyTable SET XXX='@QingJiaTianshu', DoWorkerID='@WebUser.No', 
        DoWorkerName='@WebUser.Name', DoWorkerDept='@WebUser.DeptNo',
       WHERE xxxx=@WorkID
    #### 存储过程DEMO
    - @yln 完善.
    - 执行的结果返回string类型的数据，如果
    `),e.AddTBAtParas(4e3),this._enMap=e,this._enMap}beforeInsert(){return a(this,null,function*(){return Promise.resolve(!0)})}beforeUpdateInsertAction(){return a(this,null,function*(){return Promise.resolve(!0)})}}export{U as SysEventURL};
