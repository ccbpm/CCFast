var h=Object.defineProperty;var d=(r,e,t)=>e in r?h(r,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[e]=t;var p=(r,e,t)=>d(r,typeof e!="symbol"?e+"":e,t);var D=(r,e,t)=>new Promise((i,s)=>{var m=a=>{try{l(t.next(a))}catch(n){s(n)}},o=a=>{try{l(t.throw(a))}catch(n){s(n)}},l=a=>a.done?i(a.value):Promise.resolve(a.value).then(m,o);l((t=t.apply(r,e)).next())});import{b as c,a as g}from"./MapExt-DtQWKcAY.js";import{DtlImpEn1 as E}from"./DtlImpEn1-BzmGej0G.js";import{DtlImpEn3 as I}from"./DtlImpEn3-_zNyYGGT.js";import{DtlTreeEns as f}from"./DtlTreeEns-D9x1rY8-.js";import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class G extends u{constructor(){super("GPE_DtlImp");p(this,"help5",`
  #### 帮助
  - 对于比较复杂的导入，系统满足不了，需要个性化实现,就使用该模式.
  - 系统提供一个Demo,请参考/DataUser/DtlImpDemo.vue
  - 如何使用参考Demo.
  `);p(this,"Desc1",`
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
  #### 效果图
  - ![从表导入:效果图.](/resource/WF/CCForm/DtlImpByTable.png "从表导入")
  `);p(this,"Desc2",`
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

  `);p(this,"Desc3",`
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

    
  #### 设置
  - ![从表导入:效果图.](/resource/WF/CCForm/DtlImpByTableSetting.png "从表导入")
  #### 效果图1
  - ![从表导入:效果图.](/resource/WF/CCForm/DtlImpByTable.png "从表导入")
  `);this.PageTitle="从表导入"}Init(){this.entity=new c,this.KeyOfEn=g.DoWay,this.AddGroup("A","从表导入模式"),this.Blank("0","无,不设置(默认)","不设置导入."),this.AddEntity("1","表格查询(简单模式-SQL)",new E,this.Desc1),this.AddEntity("3","表格查询模式（高级）",new I,this.Desc3),this.AddEntity("4","左树右表(TreeEns)",new f,this.Desc3),this.AddGroup("B","自定义模式"),this.SingleTB("5","自定义url模式","Tag1",this.help5,"请输入URL:比如/src/DataUser/DtlImpDemo.vue")}AfterSave(t,i){return D(this,null,function*(){const s=new c,m=this.params.RefPKVal,o=this.params.RefPKVal+this.params.suffix;s.setPKVal(o),yield s.Retrieve(),s.SetValByKey("FK_MapData",m),s.DirectUpdate()})}BtnClick(t,i,s){}}export{G as GPE_DtlImp};
