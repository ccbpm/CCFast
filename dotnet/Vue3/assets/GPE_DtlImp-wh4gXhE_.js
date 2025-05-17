var h=Object.defineProperty;var d=(s,e,t)=>e in s?h(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var r=(s,e,t)=>d(s,typeof e!="symbol"?e+"":e,t);var D=(s,e,t)=>new Promise((p,a)=>{var m=i=>{try{n(t.next(i))}catch(l){a(l)}},o=i=>{try{n(t.throw(i))}catch(l){a(l)}},n=i=>i.done?p(i.value):Promise.resolve(i.value).then(m,o);n((t=t.apply(s,e)).next())});import{bb as c,ba as E}from"./entry/index-M8VErHPE-1727507756861.js";import{DtlImpEn1 as f}from"./DtlImpEn1-BOZDs7jO.js";import{DtlImpEn3 as I}from"./DtlImpEn3-BorfoLIh.js";import{DtlTreeEns as g}from"./DtlTreeEns-D3VPqjLr.js";import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class V extends u{constructor(){super("GPE_DtlImp");r(this,"help5",`
  #### 帮助
  - 对于比较复杂的导入，系统满足不了，需要个性化实现,就使用该模式.
  - 系统提供一个Demo,请参考/DataUser/DtlImpDemo.vue
  - 如何使用参考Demo.
  `);r(this,"Desc1",`
  #### 帮助
  - 按照要求配置数据源.
  - 在从表上显示导入功能,如下图:
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapDtl/EditModel/Card1.png "表格模式")

  #### 说明
   - 系统首先检查数据是否正确，如果有非法的数据，系统就会 0 导入.
   #### 检查内容：
    1. 数值类型的字段是否为空合法.
    1. 日期字段格式是否合理. 
    1. 枚举字段是否完整,比如：性别字段，枚举是男,女. 
    1. 外键字段(外部数据源)是否符合要求. 
    
  `);r(this,"Desc2",`
  #### 帮助
   - 制作 excel模板, 放入到: \\DataUser\\TempleteOfImp\\从表ID.xls
   - 在excel模板中填写数据.
   - 执行导入
   #### 说明
   - 系统首先检查数据是否正确，如果有非法的数据，系统就会 0 导入.
   #### 检查内容：
    1. 数值类型的字段是否为空合法.
    1. 日期字段格式是否合理. 
    1. 枚举字段是否完整,比如：性别字段，枚举是男,女. 
    1. 外键字段(外部数据源)是否符合要求. 

  `);r(this,"Desc3",`
  #### 表格查询(简单模式-SQL)
   - 按照要求配置数据源.
   - 在从表上显示导入功能,如下图:
   #### 说明
   - 系统首先检查数据是否正确，如果有非法的数据，系统就会 0 导入.
   #### 检查内容：
    1. 数值类型的字段是否为空合法.
    1. 日期字段格式是否合理. 
    1. 枚举字段是否完整,比如：性别字段，枚举是男,女. 
    1. 外键字段(外部数据源)是否符合要求. 
    
  `);this.PageTitle="从表导入"}Init(){this.entity=new c,this.KeyOfEn=E.DoWay,this.AddGroup("A","从表导入模式"),this.Blank("0","无,不设置(默认)","不设置导入."),this.AddEntity("1","表格查询(简单模式-SQL)",new f,this.Desc1),this.AddEntity("3","表格查询模式（高级）",new I,this.Desc3),this.AddEntity("4","左树右表(TreeEns)",new g,this.Desc3),this.AddGroup("B","自定义模式"),this.SingleTB("5","自定义url模式","Tag1",this.help5,"请输入URL:比如/src/DataUser/DtlImpDemo.vue")}AfterSave(t,p){return D(this,null,function*(){const a=new c,m=this.params.RefPKVal,o=this.params.RefPKVal+this.params.suffix;a.setPKVal(o),yield a.Retrieve(),a.SetValByKey("FK_MapData",m),a.DirectUpdate()})}BtnClick(t,p,a){}}export{V as GPE_DtlImp};
