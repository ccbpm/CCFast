var E=Object.defineProperty;var c=(s,e,t)=>e in s?E(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var p=(s,e,t)=>c(s,typeof e!="symbol"?e+"":e,t);var l=(s,e,t)=>new Promise((u,o)=>{var d=r=>{try{a(t.next(r))}catch(n){o(n)}},i=r=>{try{a(t.throw(r))}catch(n){o(n)}},a=r=>r.done?u(r.value):Promise.resolve(r.value).then(d,i);a((t=t.apply(s,e)).next())});import{N as y,bb as M,O as X,U as m,L as A}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class P extends y{constructor(t){super("TS.MapExt.FullBodySFTable");p(this,"DescSrc",`
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
  `);t&&(this.MyPK=t)}get HisUAC(){const t=new m;return t.IsDelete=!1,t.IsUpdate=!0,t.IsInsert=!1,t}get EnMap(){const t=new A("Sys_MapExt","填充主表");return t.AddGroupAttr("填充主表"),t.AddMyPK(),M.AddAttrSFSearch(t,"Tag6","查询",1),this._enMap=t,this._enMap}beforeInsert(){return l(this,null,function*(){return Promise.resolve(!0)})}}class f extends X{get GetNewEntity(){return new FullData}constructor(){super()}}export{P as FullBodySFTable,f as FullDatas};
