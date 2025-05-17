var T=Object.defineProperty;var u=(o,r,e)=>r in o?T(o,r,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[r]=e;var i=(o,r,e)=>u(o,typeof r!="symbol"?r+"":r,e);var s=(o,r,e)=>new Promise((l,n)=>{var p=t=>{try{a(e.next(t))}catch(c){n(c)}},m=t=>{try{a(e.throw(t))}catch(c){n(c)}},a=t=>t.done?l(t.value):Promise.resolve(t.value).then(p,m);a((e=e.apply(o,r)).next())});import{b9 as w,aB as x,aC as N}from"./entry/index-M8VErHPE-1727507756861.js";import y from"./Dev2Interface-BNvJe3G3.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class B extends w{constructor(){super("GPN_StartFlowHT");i(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);i(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 . @hongyan 完善测试该方法.

  `);this.PageTitle="新建收入合同",this.ForEntityClassID="TS.PM.HTIncome"}Init(){return s(this,null,function*(){this.AddGroup("A","选择方式"),this.TextBox1_Name("Track1","收入合同",this.HelpUn,"合同名称","我的收入合同","请输入您的合同名称")})}GenerSorts(){return s(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,l,n,p,m){return s(this,null,function*(){if(e=="Track1"){const a=yield y.Node_CreateBlank("002"),t="/#/WF/MyFlow?FlowNo=002&HeTongMingChen="+n+"&WorkID="+a;return new x(N.OpenUrlByDrawer90,t,"流程")}})}}export{B as GPN_StartFlowHT};
