var m=Object.defineProperty;var T=(o,r,e)=>r in o?m(o,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[r]=e;var i=(o,r,e)=>T(o,typeof r!="symbol"?r+"":r,e);var s=(o,r,e)=>new Promise((l,a)=>{var p=t=>{try{n(e.next(t))}catch(c){a(c)}},u=t=>{try{n(e.throw(t))}catch(c){a(c)}},n=t=>t.done?l(t.value):Promise.resolve(t.value).then(p,u);n((e=e.apply(o,r)).next())});import{b5 as w,G as x,l as B}from"./entry/index-C6uBgOW5-1730430676707.js";import N from"./Dev2Interface-BiOSL5Ij.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class _ extends w{constructor(){super("GPN_StartFlowZHT");i(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);i(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 .  完善测试该方法.

  `);this.PageTitle="新建收入合同",this.ForEntityClassID="TS.PM.ZHTIncome"}Init(){return s(this,null,function*(){this.AddGroup("A","选择方式"),this.TextBox1_Name("Track1","收入合同",this.HelpUn,"合同名称","我的收入合同","请输入您的合同名称")})}GenerSorts(){return s(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,l,a,p,u){return s(this,null,function*(){if(e=="Track1"){const n=yield N.Node_CreateBlank("002"),t="/#/WF/MyFlow?FlowNo=002&StrZhuBanBuMen=68ffdf05-96f9-4063-8132-706263a885cc&StrZhuBanBuMenT=经营服务部&HeTongMingChen="+a+"&WorkID="+n;return new x(B.OpenUrlByDrawer90,t,"流程")}})}}export{_ as GPN_StartFlowZHT};
