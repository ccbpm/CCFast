var E=Object.defineProperty;var f=(e,i,t)=>i in e?E(e,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):e[i]=t;var p=(e,i,t)=>f(e,typeof i!="symbol"?i+"":i,t);var a=(e,i,t)=>new Promise((r,o)=>{var d=n=>{try{h(t.next(n))}catch(s){o(s)}},x=n=>{try{h(t.throw(n))}catch(s){o(s)}},h=n=>n.done?r(n.value):Promise.resolve(n.value).then(d,x);h((t=t.apply(e,i)).next())});import{b as c,a as l}from"./MapExt-DtQWKcAY.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-IicyYiex.js";import{LinkAttr as m}from"./LinkAttr-CYMqtT8-.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./Help-D0bDMZWg.js";class S extends A{constructor(){super("GPE_FieldNameLink");p(this,"Desc0",`
  #### 帮助
  - 定义: 对输入的字段进行详尽的描述，文字特别多，就需要此功能.
  - 比如: 项目申报流程中，对项目的预期效益进行描述.
  #### 效果图
  - 暂无
  `);p(this,"HelpInfo",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 支持markdown 语法, 支持html.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);p(this,"UrlRightOpen",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 格式: http://11.112.11.2/xx.do?DoType=xx&JinE=@JinE&BianHao=@BillNo
  - 解析后的格式: http://11.112.11.2/xx.do?DoType=xx&JinE=123.99&BianHao=100-02
  - 解析说明, @BillNo,@JinE 就是字段名. 解析的时候，会把字段名替换掉.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);this.PageTitle="字段名链接"}Init(){return a(this,null,function*(){this.entity=new c,this.KeyOfEn=l.DoWay,yield this.entity.InitDataForMapAttr("FieldNameLink",this.GetRequestVal("PKVal")),this.AddGroup("A","字段名链接"),this.Blank("0","禁用",this.Desc0),this.SingleTextArea("HelpInfo","弹出帮助信息",l.Doc,"请按照格式输入内容",this.HelpInfo),this.SingleTextArea("HelpSQL","弹出SQL查询",l.Doc,"请按照格式输入内容",this.HelpInfo),this.SingleTB("UrlRightOpen","侧滑弹出url",l.Doc,this.UrlRightOpen,"请输入url"),this.AddEntity("UrlOpen","模态弹窗",new m,this.UrlRightOpen,""),this.AddEntity("UrlWinOpen","新窗口弹出url",new m,this.UrlRightOpen,"")})}AfterSave(t,r){if(t==r)throw new Error("Method not implemented.")}BtnClick(t,r,o){if(t==r||t===o)throw new Error("Method not implemented.")}}export{S as GPE_FieldNameLink};
