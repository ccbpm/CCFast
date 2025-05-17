var p=Object.defineProperty;var B=(t,o,e)=>o in t?p(t,o,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[o]=e;var F=(t,o,e)=>B(t,typeof o!="symbol"?o+"":o,e);var s=(t,o,e)=>new Promise((i,n)=>{var l=r=>{try{a(e.next(r))}catch(c){n(c)}},T=r=>{try{a(e.throw(r))}catch(c){n(c)}},a=r=>r.done?i(r.value):Promise.resolve(r.value).then(l,T);a((e=e.apply(t,o)).next())});import{b9 as m,aB as w,aC as N}from"./entry/index-M8VErHPE-1727507756861.js";import h from"./Dev2Interface-BNvJe3G3.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class H extends m{constructor(){super("GPN_StartFlow006");F(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);F(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 . @hongyan 完善测试该方法.

  `);this.PageTitle="新建主营分包流程",this.ForEntityClassID="TS.PM.BoFuSQ"}Init(){return s(this,null,function*(){this.AddGroup("A","选择方式"),this.Table("Track0","分包合同名称",this.HelpTodo,!1,"SELECT No,FBHTMC Name,FBHTJEY 分包合同金额,FBHTJF 分包合同甲方,FBHTYF 分包合同乙方,case SFBCHT when 0 then '否' when 1 then '是' end  是否补充合同 FROM PM_FenBaoSQ order by No desc ")})}GenerSorts(){return s(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,i,n,l,T){return s(this,null,function*(){if(e=="Track0"){const a=yield h.Node_CreateBlank("006"),r="/#/WF/MyFlow?FlowNo=006&FenBaoHeTongBianHao="+n+"&WorkID="+a;return new w(N.OpenIframeByDrawer75,r,"流程")}})}}export{H as GPN_StartFlow006};
