var m=Object.defineProperty;var E=(r,e,t)=>e in r?m(r,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[e]=t;var n=(r,e,t)=>E(r,typeof e!="symbol"?e+"":e,t);var i=(r,e,t)=>new Promise((l,p)=>{var u=s=>{try{a(t.next(s))}catch(o){p(o)}},d=s=>{try{a(t.throw(s))}catch(o){p(o)}},a=s=>s.done?l(s.value):Promise.resolve(s.value).then(u,d);a((t=t.apply(r,e)).next())});import{j as c,k as y,U as M,h as X}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as h}from"./MapExt-DtQWKcAY.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class g extends c{constructor(t){super("TS.MapExt.FullBodySFTable");n(this,"DescSrc",`
  #### 定义
  - 用户对控件进行操作的时候，比如: pop弹窗选择一个值, 是一个单据编号，人员编号，根据这个值获取数据填充其它数据控件的行为，我们称为落值填充.
  - 数据来源: Pop弹窗返回值、文本框自动完成、下拉框联动.
  #### 应用场景
  - 我们做一个档案系统, 在文本框输入一个人员编号,这里可以使用文本框自动完成.
  - 输入完成后，根据文本框的人员编号，获取该人员的地址，电话，邮件(主表信息), 他拥有的角色(主表下拉框信息) 以及教育经历(从表数据)
  - 填充到表单中去，完成用户的操作.
  #### 可填充的数据
  - 主表数据,比如:电话，邮件，地址.
  - 从表数据,比如:教育经历.
  - 下拉框内容,比如:人员拥有的角色集合(用下拉框展现)
  #### 配置说明
   - 填一个数据源返回的数据是一行多列，列的名字与主表字段对应，就会实现数据的自动填充.
   - 数据源必须有 @Key 参数，该值是传递来的数据.
   - 比如：SELECT Tel as DianHua, Email, Addr FROM Port_Emp WHERE No='@Key'
   - 如果配置的是url, 配置内容为: http://118.11.1.1/XXX/XX.do    
   - 系统解析为:http://118.11.1.1/XXX/XX.do?Key=zhangsan
   - 如果是函数: 请输入函数名称, 比如: GetEmpInfo()
  `);t&&(this.MyPK=t)}get HisUAC(){const t=new M;return t.IsDelete=!1,t.IsUpdate=!0,t.IsInsert=!1,t}get EnMap(){const t=new X("Sys_MapExt","填充主表");return t.AddGroupAttr("填充主表"),t.AddMyPK(),h.AddAttrSFSearch(t,"Tag6","查询",1),this._enMap=t,this._enMap}beforeInsert(){return i(this,null,function*(){return Promise.resolve(!0)})}}class D extends y{get GetNewEntity(){return new FullData}constructor(){super()}}export{g as FullBodySFTable,D as FullDatas};
