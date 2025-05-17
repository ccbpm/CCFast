var m=Object.defineProperty;var y=(n,i,t)=>i in n?m(n,i,{enumerable:!0,configurable:!0,writable:!0,value:t}):n[i]=t;var o=(n,i,t)=>y(n,typeof i!="symbol"?i+"":i,t);var s=(n,i,t)=>new Promise((r,l)=>{var x=e=>{try{p(t.next(e))}catch(a){l(a)}},E=e=>{try{p(t.throw(e))}catch(a){l(a)}},p=e=>e.done?r(e.value):Promise.resolve(e.value).then(x,E);p((t=t.apply(n,i)).next())});import{bb as O,ba as h}from"./entry/index-M8VErHPE-1727507756861.js";import{PageBaseGroupEdit as B}from"./PageBaseGroupEdit-JIgqoTiq.js";import{LinkAttr as d}from"./LinkAttr-BpSijlR-.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class u extends B{constructor(){super("GPE_ReadOnlyLink");o(this,"Desc0",`
  #### 帮助
  - 定义: 对输入的字段进行详尽的描述，文字特别多，就需要此功能.
  - 比如: 项目申报流程中，对项目的预期效益进行描述.
  #### 效果图
  - 暂无
  `);o(this,"HelpInfo",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 支持markdown 语法, 支持html.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);o(this,"UrlRightOpen",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 支持markdown 语法, 支持html.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);o(this,"UrlOpen",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 格式: http://11.112.11.2/xx.do?DoType=xx&JinE=@JinE&BianHao=@BillNo
  - 解析后的格式: http://11.112.11.2/xx.do?DoType=xx&JinE=123.99&BianHao=100-02
  - 解析说明, @BillNo,@JinE 就是字段名. 解析的时候，会把字段名替换掉.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);o(this,"UrlWinOpen",`
  #### 帮助 
  - 请输入该字段的详细描述信息.
  - 格式: http://11.112.11.2/xx.do?DoType=xx&JinE=@JinE&BianHao=@BillNo
  - 解析后的格式: http://11.112.11.2/xx.do?DoType=xx&JinE=123.99&BianHao=100-02
  - 解析说明, @BillNo,@JinE 就是字段名. 解析的时候，会把字段名替换掉.
  #### 图例
  - 暂无
  #### 运行图
  - 暂无
  `);this.PageTitle="字段值链接"}Init(){return s(this,null,function*(){this.entity=new O,this.KeyOfEn=h.DoWay,yield this.entity.InitDataForMapAttr("ReadOnlyLink",this.GetRequestVal("PKVal")),this.AddGroup("A","字段值链接"),this.Blank("0","禁用",this.Desc0),this.SingleTextArea("HelpInfo","弹出帮助信息",h.Doc,"请按照格式输入内容",this.HelpInfo),this.SingleTB("UrlRightOpen","侧滑弹出url",h.Doc,this.UrlRightOpen,"请输入url"),this.AddEntity("UrlOpen","模态弹窗",new d,this.UrlOpen,""),this.AddEntity("UrlWinOpen","新窗口弹出url",new d,this.UrlWinOpen,"")})}AfterSave(t,r){if(t==r)throw new Error("Method not implemented.")}BtnClick(t,r,l){if(t==r||t===l)throw new Error("Method not implemented.")}}export{u as GPE_ReadOnlyLink};
