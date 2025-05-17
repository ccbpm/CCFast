var E=Object.defineProperty;var f=(e,i,t)=>i in e?E(e,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):e[i]=t;var l=(e,i,t)=>f(e,typeof i!="symbol"?i+"":i,t);var a=(e,i,t)=>new Promise((r,o)=>{var m=n=>{try{p(t.next(n))}catch(s){o(s)}},x=n=>{try{p(t.throw(n))}catch(s){o(s)}},p=n=>n.done?r(n.value):Promise.resolve(n.value).then(m,x);p((t=t.apply(e,i)).next())});import{bb as c,ba as h}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-JIgqoTiq.js";import{LinkAttr as d}from"./LinkAttr-BpSijlR-.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class k extends A{constructor(){super("GPE_FieldNameLink");l(this,"Desc0",`
  #### 帮助
  - 定义: 对输入的字段进行详尽的描述，文字特别多，就需要此功能.
  - 比如: 项目申报流程中，对项目的预期效益进行描述.
  #### 效果图
  - 暂无
  `);l(this,"HelpInfo",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 支持markdown 语法, 支持html.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);l(this,"UrlRightOpen",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 格式: http://11.112.11.2/xx.do?DoType=xx&JinE=@JinE&BianHao=@BillNo
  - 解析后的格式: http://11.112.11.2/xx.do?DoType=xx&JinE=123.99&BianHao=100-02
  - 解析说明, @BillNo,@JinE 就是字段名. 解析的时候，会把字段名替换掉.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);this.PageTitle="字段名链接"}Init(){return a(this,null,function*(){this.entity=new c,this.KeyOfEn=h.DoWay,yield this.entity.InitDataForMapAttr("FieldNameLink",this.GetRequestVal("PKVal")),this.AddGroup("A","字段名链接"),this.Blank("0","禁用",this.Desc0),this.SingleTextArea("HelpInfo","弹出帮助信息",h.Doc,"请按照格式输入内容",this.HelpInfo),this.SingleTextArea("HelpSQL","弹出SQL查询",h.Doc,"请按照格式输入内容",this.HelpInfo),this.SingleTB("UrlRightOpen","侧滑弹出url",h.Doc,this.UrlRightOpen,"请输入url"),this.AddEntity("UrlOpen","模态弹窗",new d,this.UrlRightOpen,""),this.AddEntity("UrlWinOpen","新窗口弹出url",new d,this.UrlRightOpen,"")})}AfterSave(t,r){if(t==r)throw new Error("Method not implemented.")}BtnClick(t,r,o){if(t==r||t===o)throw new Error("Method not implemented.")}}export{k as GPE_FieldNameLink};
