var p=Object.defineProperty;var B=(t,o,e)=>o in t?p(t,o,{enumerable:!0,configurable:!0,writable:!0,value:e}):t[o]=e;var l=(t,o,e)=>B(t,typeof o!="symbol"?o+"":o,e);var n=(t,o,e)=>new Promise((F,s)=>{var i=r=>{try{a(e.next(r))}catch(c){s(c)}},T=r=>{try{a(e.throw(r))}catch(c){s(c)}},a=r=>r.done?F(r.value):Promise.resolve(r.value).then(i,T);a((e=e.apply(t,o)).next())});import{b5 as m,G as w,l as N}from"./entry/index-C6uBgOW5-1730430676707.js";import u from"./Dev2Interface-BiOSL5Ij.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class G extends m{constructor(){super("GPN_StartFlow006");l(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);l(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 .  完善测试该方法.

  `);this.PageTitle="新建主营分包流程",this.ForEntityClassID="TS.PM.BoFuSQ"}Init(){return n(this,null,function*(){this.AddGroup("A","选择方式"),this.Table("Track0","分包合同名称",this.HelpTodo,!1,"SELECT No,FBHTMC Name,FBHTJEY 分包合同金额,FBHTJF 分包合同甲方,FBHTYF 分包合同乙方,case SFBCHT when 0 then '否' when 1 then '是' end  是否补充合同 FROM PM_FenBaoSQ order by No desc ")})}GenerSorts(){return n(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,F,s,i,T){return n(this,null,function*(){if(e=="Track0"){const a=yield u.Node_CreateBlank("006"),r="/#/WF/MyFlow?FlowNo=006&FenBaoHeTongBianHao="+s+"&WorkID="+a;return new w(N.OpenIframeByDrawer75,r,"流程")}})}}export{G as GPN_StartFlow006};
