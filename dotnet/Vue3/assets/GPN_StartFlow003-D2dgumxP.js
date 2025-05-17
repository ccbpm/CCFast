var p=Object.defineProperty;var u=(o,a,e)=>a in o?p(o,a,{enumerable:!0,configurable:!0,writable:!0,value:e}):o[a]=e;var c=(o,a,e)=>u(o,typeof a!="symbol"?a+"":a,e);var r=(o,a,e)=>new Promise((l,t)=>{var h=n=>{try{i(e.next(n))}catch(s){t(s)}},T=n=>{try{i(e.throw(n))}catch(s){t(s)}},i=n=>n.done?l(n.value):Promise.resolve(n.value).then(h,T);i((e=e.apply(o,a)).next())});import{b9 as g,aB as m,aC as w}from"./entry/index-M8VErHPE-1727507756861.js";import S from"./Dev2Interface-BNvJe3G3.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class I extends g{constructor(){super("GPN_StartFlow003");c(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);c(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 . @hongyan 完善测试该方法.

  `);this.PageTitle="新建开票流程",this.ForEntityClassID="TS.PM.KaiPiaoSQ"}Init(){return r(this,null,function*(){this.AddGroup("A","选择方式"),this.Table("Track0","选择合同",this.HelpTodo,!1,"SELECT No,HeTongMingChen Name,XiangMuBianHao 项目编号,XiangMuMingChen 项目名称,QianDingRiQi 签订日期,case HeTongLaiYuan when 0 then '投标' when 1 then '委托' else '其他' end  合同来源,JianSheShanWeiT 建设单位,JianSheGuanLiShanWeiT 建设管理单位,JiaFangDanWeiLianXi 甲方单位联系人 FROM PM_HTIncome ")})}GenerSorts(){return r(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,l,t,h,T){return r(this,null,function*(){if(e=="Track0"){const i=yield S.Node_CreateBlank("003"),n="/#/WF/MyFlow?FlowNo=003&HeTongBianHao="+t+"&WorkID="+i;return new m(w.OpenUrlByDrawer90,n,"流程")}})}}export{I as GPN_StartFlow003};
