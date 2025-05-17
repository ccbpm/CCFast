var F=Object.defineProperty;var d=(i,e,t)=>e in i?F(i,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[e]=t;var w=(i,e,t)=>d(i,typeof e!="symbol"?e+"":e,t);var c=(i,e,t)=>new Promise((a,o)=>{var s=r=>{try{l(t.next(r))}catch(p){o(p)}},n=r=>{try{l(t.throw(r))}catch(p){o(p)}},l=r=>r.done?a(r.value):Promise.resolve(r.value).then(s,n);l((t=t.apply(i,e)).next())});import{B as u,G as h,l as m,D as f}from"./entry/index-C6uBgOW5-1730430676707.js";import{PageBaseGroupEdit as A}from"./PageBaseGroupEdit-IicyYiex.js";import{Flow as g}from"./Flow-D4nUES4A.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./Help-D0bDMZWg.js";class k extends A{constructor(){super("GPE_FullSA");w(this,"Desc0",`
  #### 帮助
  - 自动计算未来处理人，是指在流程发起的时候，自动计算出来流程要走的方向以及每个节点的抄送人员、处理人员。
  - 不计算：是一个默认的模式，是指每次发送的时候计算下一步的接受人.
  - 自动计算：在开始节点填写表单之后，就执行计算每个节点的接受人，抄送人。
  #### 应用场景
   - 适合不同的客户需求，计算未来接收人，能够让发起人，或者中间节点的处理人清楚的知道未来处理人。
   - 更多帮助: https://gitee.com/opencc/JFlow/wikis/pages/preview?sort_id=3661875&doc_id=31094
  `);w(this,"Desc1",`
  
  #### 帮助 
  - 自动计算：在开始节点填写表单之后，就执行计算每个节点的接受人,抄送人.
  - 该模式应用场景较少.
  - 如下图:
  - ![输入图片说明](/resource/WF/Admin/AttrFlow/FullSA/FullSA.png);

  #### 生成数据版本号
  - 发起人启动起来流程以后，系统就会计算运动路径以及处理人，如果流程模板在中途变更了(主要是方向条件与接受人规则发生变化了),也不影响流程运行.
  - 如果需要对运行的流程实例，接受变更后的影响，就需要生成数据版本编号.
  - 每个流程实例启动后，就记录了当前流程模板的数据版本好, 在流程运行过程中，如果版本号变化了，就会重新计算接受人.
  #### 其他
  - 如果流程发生减少节点、减少表单字段信息变更，就需要创建流程模板。
  - 如果接受人规则、抄送规则、方向条件等变更，则不需要创建流程模板版本。

  `);this.PageTitle="计算未来处理人"}AfterSave(t,a){if(t===a)throw new Error("Method not implemented.")}Init(){this.entity=new g,this.KeyOfEn="IsFullSA",this.Btns=[{pageNo:"1",list:["检查正确性","生成数据版本号"]}],this.AddGroup("A","工作模式"),this.Blank("0","不计算",this.Desc0),this.Blank("1","自动计算未来处理人",this.Desc1)}BtnClick(t,a,o){return c(this,null,function*(){if(o==="检查正确性"){const s=new u("BP.WF.Flow",this.params.FlowNo);yield s.Retrieve();const n=yield s.DoMethodReturnString("DoCheckFullSA");return new h(m.Message,n)}if(o==="生成数据版本号"){if(window.confirm("您确认要执行吗?")==!1)return;const s=new u("BP.WF.Flow",this.params.FlowNo);return yield s.Retrieve(),s.setPara("SADataVer",f.CurrentDateTime),yield s.Update(),new h(m.Message,"生成成功,在途的流程就会重新计算接受人.")}})}}export{k as GPE_FullSA};
