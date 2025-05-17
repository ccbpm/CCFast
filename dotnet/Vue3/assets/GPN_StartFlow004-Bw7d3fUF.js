var T=Object.defineProperty;var p=(o,n,e)=>n in o?T(o,n,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[n]=e;var c=(o,n,e)=>p(o,typeof n!="symbol"?n+"":n,e);var i=(o,n,e)=>new Promise((l,t)=>{var h=a=>{try{r(e.next(a))}catch(s){t(s)}},u=a=>{try{r(e.throw(a))}catch(s){t(s)}},r=a=>a.done?l(a.value):Promise.resolve(a.value).then(h,u);r((e=e.apply(o,n)).next())});import{b5 as m,G as S,l as g}from"./entry/index-C6uBgOW5-1730430676707.js";import w from"./Dev2Interface-BiOSL5Ij.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";class y extends m{constructor(){super("GPN_StartFlow004");c(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);c(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 .  完善测试该方法.

  `);this.PageTitle="新建主营分包流程",this.ForEntityClassID="TS.PM.FenBaoSQ"}Init(){return i(this,null,function*(){this.AddGroup("A","选择方式"),this.Table("Track0","选择合同",this.HelpTodo,!1,"SELECT No,HeTongMingChen Name,XiangMuBianHao 项目编号,XiangMuMingChen 项目名称,QianDingRiQi 签订日期,case HeTongLaiYuan when 0 then '投标' when 1 then '委托' else '其他' end  合同来源,JianSheShanWeiT 建设单位,JianSheGuanLiShanWeiT 建设管理单位,JiaFangDanWeiLianXi 甲方单位联系人 FROM PM_HTIncome order by No")})}GenerSorts(){return i(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,l,t,h,u){return i(this,null,function*(){if(e=="Track0"){const r=yield w.Node_CreateBlank("004"),a="/#/WF/MyFlow?FlowNo=004&ZhuShouRuHeTongBianH="+t+"&WorkID="+r;return new S(g.OpenIframeByDrawer75,a,"流程")}})}}export{y as GPN_StartFlow004};
