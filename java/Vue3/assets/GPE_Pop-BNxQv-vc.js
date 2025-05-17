var P=Object.defineProperty;var l=(t,r,o)=>r in t?P(t,r,{enumerable:!0,configurable:!0,writable:!0,value:o}):t[r]=o;var i=(t,r,o)=>l(t,typeof r!="symbol"?r+"":r,o);var m=(t,r,o)=>new Promise((s,p)=>{var e=n=>{try{g(o.next(n))}catch(c){p(c)}},a=n=>{try{g(o.throw(n))}catch(c){p(c)}},g=n=>n.done?s(n.value):Promise.resolve(n.value).then(e,a);g((o=o.apply(t,r)).next())});import{b as h,P as d}from"./MapExt-DtQWKcAY.js";import{PopGroupList as L}from"./PopGroupList-GB-eV1Se.js";import{PopSelfUrl as u}from"./PopSelfUrl-EnRZlsK0.js";import{PopTree as A}from"./PopTree-BIjvBvGf.js";import{PopTreeEns as f}from"./PopTreeEns-DVS5IZGD.js";import{PopTreeEnsSFTable as F}from"./PopTreeEnsSFTable-Ci0X2hIo.js";import{GloComm as E}from"./GloComm-CmAl8MpM.js";import{PageBaseGroupEdit as T}from"./PageBaseGroupEdit-IicyYiex.js";import{G as B,l as M}from"./entry/index-C6uBgOW5-1730430676707.js";import{MapAttr as b}from"./MapAttr-B1mxD3vP.js";import{PopTableSearch as y}from"./PopTableSearch-BhdZQymx.js";import{PopTableSimple as I}from"./PopTableSimple-CzVOdxQE.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./Events-D9tOL1Ad.js";import"./Help-D0bDMZWg.js";class Y extends T{constructor(){super("GPE_Pop");i(this,"PopTableSearch",` 

  #### 帮助
   - 数据是以表格的模式展现，可以设置查询条件, 比如选择单据、产品、所在班级。
   - 适应数据量较大，需要搜索完成。
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/TableSearch2.png "屏幕截图.png")
  #### 运行效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/TableSearch.png "屏幕截图.png")
  
  `);i(this,"PopSelfUrl",` 
  #### 帮助
   - 当ccflow提供的模式不能满足您的要求的时候，这个方案就是终极解决办法。
   - 您自己定义一个页面，配置到系统中去. 
   - 返回的数据，需要满足ccflow的规范,请参考示例. /DataUser/

  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/Url2.png "屏幕截图.png")


  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/Url.png "屏幕截图.png")

  
  `);i(this,"Desc1",` 

  #### 帮助
   - 弹窗（Pop）返回值，就是双击一个文本框弹出一个窗体，选择窗体的内容点击"确定"按钮把选择的值放入到该文本框里。
   - 当放入文本框的值以后，激活一个方法，把其他的值填充到其他控件里，我们把这种行为称为填充设置。
   - 为了满足不同模式下的填充窗体内容的显示，我们分为很多种弹窗返回值，您可以根据不同的场景设置不同的模式。
 #### 应用场景
  - 对一个文本框输入的数据需要获取外部数据的时候.
  - 比如：选择参与人、选择产品、选择客户、选择...
  - 输入的数据需要选择的时候.
  - 数据源获取方式：执行SQL、执行URL、执行自定义的function.
  - 弹窗类型：树干叶子模式 、树干模式、 分组模式、 列表模式、表格模式
  #### 效果图
   - 树干叶子模式
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/BranchesAndLeaf.png "屏幕截图.png") 
   - 树干模式
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/Branches.png "树结构效果图.png")
   - 分组模式
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/GroupList.png "屏幕截图.png") 
   - 列表模式
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/TableList.png "屏幕截图.png")
   - 表格模式
   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/TableSearch.png "屏幕截图.png")

   `);i(this,"PopBranchesAndLeaf",` 
  #### 说明
   - 该模式下最经典的就是部门树与人员的结构, 部门就是树干，人员就是叶子，我们把这样的模式成为树干叶子模式。
   - 与此相类似的有： 流程树与流程的关系， 表单库与表单关系.
   - 通过定义的数据源，就可以轻松实现这样的Pop结构。
  
   #### 配置图

   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/BranchesAndLeaf2.png "屏幕截图.png")    
  
   #### 效果图

   - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/BranchesAndLeaf.png "屏幕截图.png")
 
 
   `);i(this,"PopBranches",` 

  #### 说明
   - 弹窗的数据展现为树结构，比如：部门、类别等等。
   - 数据结构为常见通用的编号、名称、父节点编号规则。
   - 点击上方按钮可以设置属性。
   
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/Branches2.png "屏幕截图.png") 
   #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/Branches.png "树结构效果图.png")


   `);i(this,"PopTableList",` 

  #### 帮助
   - 单实体平铺，就是对数据源进行简单的宫格列表展示，方便用户选择。
   - 是最简单的一种弹窗数据展现模式，适用于数据量较小，没有数据展示分组的需要。
  
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/TableList2.png "屏幕截图.png")  

  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/TableList.png "屏幕截图.png")


   `);i(this,"PopGroupList",` 

  #### 帮助
   - 分组列表平铺,就是对实体进行分组展示. 例如: 产品类别与产品。 角色类型与角色。
   - 产品类别角色类型就是分组数据源，产品与角色就是实体数据源。
   - 实体数据源要求返回三个列，最后一列就是与分组数据源对应的外键列。
   - 请参考配置图。
  
  #### 配置图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/GroupList2.png "屏幕截图.png") 

  #### 效果图
  - ![输入图片说明](/resource/WF/Admin/FrmLogic/MapExt/Pop/Img/GroupList.png "屏幕截图.png")
  
    `);this.PageTitle="弹窗返回值"}AfterSave(o,s){return m(this,null,function*(){if(o!="None"){let p=this.GetRequestVal("PKVal");p.endsWith("_Pop")&&(p=p.replace("_Pop",""));const e=new b,a=p;e.setPKVal(p+"T"),(yield e.RetrieveFromDBSources())==0&&(e.setPKVal(a),yield e.RetrieveFromDBSources(),e.MyPK=e.MyPK+"T",e.KeyOfEn=e.KeyOfEn+"T",e.Name=e.Name+"T",e.UIVisible=!1,e.UIIsEnable=!1,yield e.Insert())}if(o==s)throw new Error("Method not implemented.")})}BtnClick(o,s,p){return m(this,null,function*(){var e;if(p==="落值填充"||p==="填充"){const a=E.UrlEn("TS.MapExt.FullData",(e=this.entity)==null?void 0:e.MyPK);return new B(M.OpenUrlByDrawer75,a)}})}Init(){return m(this,null,function*(){this.entity=new h,this.KeyOfEn="DoWay",this.Btns=[{pageNo:"PopBranchesAndLeaf",list:["填充"]},{pageNo:"PopBranches",list:["填充"]},{pageNo:"PopGroupList",list:["填充"]},{pageNo:"PopTableList",list:["填充"]},{pageNo:"PopTable",list:["填充"]},{pageNo:"PopTableSimple",list:["填充"]},{pageNo:"PopSelfUrl",list:["填充"]},{pageNo:"PopBranches",list:["填充"]}],yield this.entity.InitDataForMapAttr("Pop",this.GetRequestVal("PKVal"),"None"),this.AddGroup("A","树形结构"),this.Blank("None","无,不设置(默认).",this.Desc1),this.AddEntity("PopBranchesAndLeaf","树干叶子模式",new f,this.PopBranchesAndLeaf),this.AddEntity("PopBranchesAndLeafSFTable","树干叶子模式(绑定字典表)",new F,this.PopBranchesAndLeaf),this.AddEntity("PopBranches","树干模式",new A,this.PopBranches),this.AddGroup("B","分组模式"),this.AddEntity("PopGroupList","分组列表平铺",new L,this.PopGroupList),this.AddEntity("PopTableList","单实体平铺",new d,this.PopTableList),this.AddGroup("C","其他模式"),this.AddEntity("PopTable","表格-分页模式",new y,this.PopTableSearch),this.AddEntity("PopTableSimple","表格-简洁模式",new I,this.PopTableSearch),this.AddEntity("PopSelfUrl","自定义URL",new u,this.PopSelfUrl)})}}export{Y as GPE_Pop};
