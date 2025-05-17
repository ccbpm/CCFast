var T=Object.defineProperty;var f=(a,t,e)=>t in a?T(a,t,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[t]=e;var n=(a,t,e)=>f(a,typeof t!="symbol"?t+"":t,e);var l=(a,t,e)=>new Promise((c,s)=>{var N=r=>{try{o(e.next(r))}catch(i){s(i)}},m=r=>{try{o(e.throw(r))}catch(i){s(i)}},o=r=>r.done?c(r.value):Promise.resolve(r.value).then(N,m);o((e=e.apply(a,t)).next())});import{b9 as h,aB as p,aC as w}from"./entry/index-M8VErHPE-1727507756861.js";import k from"./Dev2Interface-BNvJe3G3.js";import{ND101Dtl1s as y}from"./ND101Dtl1-FrzVN0NH.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./EntityOID-BVVq-i_P.js";class D extends h{constructor(){super("GPN_StartFlow001");n(this,"fNam");n(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);n(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 . @hongyan 完善测试该方法.

  `);this.PageTitle="新建项目",this.ForEntityClassID="TS.PM.Prj"}Init(){return l(this,null,function*(){const e=[{No:"0",Name:"房建"},{No:"1",Name:"市政"},{No:"2",Name:"燃气"},{No:"4",Name:"敞口"}];this.AddGroup("A","选择方式"),this.TextBox1_Name("Track0","立项流程-当轨道",this.HelpUn,"项目名称","我的项目-当轨道","请输入您的项目名称"),this.TextBox1_Name("Track1","立项流程-当非轨道",this.HelpUn,"项目名称","我的项目-当非轨道","请输入您的项目名称"),this.SelectItemsByList("Track1.Track2","项目类型",this.Imp,!1,JSON.stringify(e))})}GenerSorts(){return l(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,c,s,N,m){return l(this,null,function*(){if(e=="Track0"){const o=yield k.Node_CreateBlank("001"),r="/#/WF/MyFlow?FlowNo=001&PrjName="+s+"&WorkID="+o+"&XMLX=3";return new p(w.OpenUrlByDrawer90,r,"流程")}else if(e=="Track1")this.fNam=s;else if(e=="Track1.Track2"){const o=yield k.Node_CreateBlank("001");yield new y().Retrieve("RefPK",o);const i="/#/WF/MyFlow?FlowNo=001&PrjName="+this.fNam+"&WorkID="+o+"&XMLX="+s;return new p(w.OpenUrlByDrawer90,i,"流程")}})}}export{D as GPN_StartFlow001};
