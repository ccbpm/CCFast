var T=Object.defineProperty;var d=(r,t,e)=>t in r?T(r,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[t]=e;var s=(r,t,e)=>d(r,typeof t!="symbol"?t+"":t,e);var u=(r,t,e)=>new Promise((E,l)=>{var i=a=>{try{n(e.next(a))}catch(o){l(o)}},p=a=>{try{n(e.throw(a))}catch(o){l(o)}},n=a=>a.done?E(a.value):Promise.resolve(a.value).then(i,p);n((e=e.apply(r,t)).next())});import{N as c,bb as m,aD as g,aE as S,U as M,L as N}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class A extends c{constructor(e){super("TS.MapExt.MENoNameP1");s(this,"HelpTag",` 
  #### resufull数据源说明
  - 点击保存按钮系统自动出现【数据源表达式】.
  - 比如数据源表达式:/get_BU_PDT/{accesstoken}/{BU}
  - 标识他需要二个参数,{accesstoken} 与  {BU} ， 这个{BU}就是我们的联动的 @Key, 就是当前下拉框的选中的值.
  - 在【参数格式】里我们输入:{BU}=@Key;{accesstoken}=@WebUser.Token
  - 说明: @WebUser.Token 是系统参数, @Key 是ccform约定的参数.
  #### SQL数据源说明
  - 点击保存按钮系统自动出现【数据源表达式】.
  - 比如数据源表达式:SELECT No,Name FROM Port_Emp WHERE Name='@Key'
  - 参数格式:就可以为空,必须要输入.
  - 比如数据源表达式:SELECT No,Name FROM Port_Emp WHERE Name='@BU'
  - 参数格式: @BU=@Key;
   `);s(this,"HelpTag1",` 
   #### 说明
   - 该字段只读
   - 当您选择一个字典的时候，点击保存该字典要获得数据的表达就会自动填充上来.
    `);s(this,"DescSearchtip",` 
  #### 说明
  - 显示在搜索文本框的背景文字.
  - 输入城市名称,比如:beijing,bj,进行搜索.
  - 人员的编号,名称,拼音,进行模糊搜索.
   `);s(this,"DescTag1",` 
   #### 说明
   - zhoupeng 补充
    `);s(this,"DescDoc",` 
  #### 说明
  - SQL格式为:
  - SELECT No,Name FROM Port_Emp WHERE PinYin LIKE '%@Key%' OR No LIKE '%@Key%' OR Name LIKE '%@Key%' 
  - SELECT No,Name FROM CN_City WHERE PinYin LIKE '%@Key%' OR Name LIKE '%@Key%'
  - URL格式为:
  - /DataUser/Handler.ashx?xxx=sss 
  - 方法的格式为:
  - MyFunName
   `);e&&(this.MyPK=e)}get HisUAC(){const e=new M;return e.IsDelete=!1,e.IsUpdate=!0,e.IsInsert=!1,e}get EnMap(){const e=new N("Sys_MapExt","编号名称字典(有参)");return e.GroupBarShowModel=1,e.AddGroupAttr("基本设置"),e.AddMyPK(),e.AddTBString("FK_MapData",null,"表单ID",!0,!0,0,50,200),e.AddTBString("ExtModel",null,"ExtModel",!1,!1,0,50,200),e.AddTBString("ExtType",null,"ExtModel",!1,!1,0,50,200),e.AddTBString("AttrOfOper",null,"当前字段",!0,!0,0,50,200),m.AddAttrSFTable(e,"Tag4","字典表",1),e.AddTBString("Tag1",null,"数据源表达式",!0,!0,0,50,200,!0,this.HelpTag1),e.AddTBString("Tag2",null,"主机",!0,!0,0,50,200,!0),e.AddTBStringDoc("Tag3",null,"设置信息",!0,!0,!0),this._enMap=e,this._enMap}beforeUpdate(){return u(this,null,function*(){if(g.IsNullOrEmpty(this.Doc)==!0)return Promise.resolve(!0);const e=new S;return e.No=this.Doc,(yield e.RetrieveFromDBSources())==0||(this.Tag1=e.SelectStatement,this.Tag2=e.ConnString),Promise.resolve(!0)})}}export{A as MENoNameP1};
