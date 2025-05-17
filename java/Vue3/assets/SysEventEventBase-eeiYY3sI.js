var a=(u,e,r)=>new Promise((p,i)=>{var l=t=>{try{n(r.next(t))}catch(o){i(o)}},c=t=>{try{n(r.throw(t))}catch(o){i(o)}},n=t=>t.done?p(t.value):Promise.resolve(t.value).then(l,c);n((r=r.apply(u,e)).next())});import{j as d,U as D,h as E}from"./entry/index-C6uBgOW5-1730430676707.js";import{SysEventAttr as s}from"./SysEvent-D4PfKXg3.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class v extends d{constructor(e){super("TS.Sys.SysEventEventBase"),e&&(this.MyPK=e)}get HisUAC(){const e=new D;return e.IsDelete=!0,e.IsUpdate=!0,e.IsInsert=!0,e}get EnMap(){const e=new E("Sys_FrmEvent","事件基类");return e.AddMyPK(),e.AddTBString(s.RefPKVal,null,"关键值",!1,!1,0,100,10),e.AddTBString(s.EventDoType,null,"执行标记",!0,!0,0,100,100),e.AddTBString(s.EventDoTypeT,null,"执行标记",!0,!0,0,100,100),e.AddTBStringDoc(s.DoDoc,null,"事件基类",!0,!0,!0,`
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
    `),e.AddTBAtParas(4e3),this._enMap=e,this._enMap}beforeInsert(){return a(this,null,function*(){return Promise.resolve(!0)})}beforeUpdateInsertAction(){return a(this,null,function*(){return Promise.resolve(!0)})}}export{v as SysEventEventBase};
