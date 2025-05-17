var w=Object.defineProperty;var u=(o,n,i)=>n in o?w(o,n,{enumerable:!0,configurable:!0,writable:!0,value:i}):o[n]=i;var p=(o,n,i)=>u(o,typeof n!="symbol"?n+"":n,i);var s=(o,n,i)=>new Promise((D,d)=>{var F=t=>{try{r(i.next(t))}catch(e){d(e)}},c=t=>{try{r(i.throw(t))}catch(e){d(e)}},r=t=>t.done?D(t.value):Promise.resolve(t.value).then(F,c);r((i=i.apply(o,n)).next())});import{b9 as P,aB as m,aC as A,H as h}from"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";class W extends P{constructor(){super("GPN_ImpFlowData");p(this,"DingDing",`
  #### 帮助
  - 选择从钉钉系统中导出的流程实例Excel格式的流程数据.
  - 上传执行导入.
  #### 说明
  - 系统读取excel以后，根据当前的流程节点设置，导入数据。
  - 能够解析出来审批意见，审核路径，流程轨迹图。
  - 能够设置当初的执行时间。
  - 适用于钉钉系统转ccbpm系统，流程实例数据的溯源。
  - ![导入钉钉格式模板](/resource/WF/Admin/AttrFlow/ImpFlowData_DingDing.png "导入钉钉格式模板")  

  `);p(this,"WeiXin",`
  #### 帮助
  - 选择从微信系统中导出的流程实例Excel格式的流程数据.
  - 上传执行导入.
  #### 说明
  - 系统读取excel以后，根据当前的流程节点设置，导入数据。
  - 能够解析出来审批意见，审核路径，流程轨迹图。
  - 能够设置当初的执行时间。
  - 适用于微信系统转ccbpm系统，流程实例数据的溯源。
  - ![导入微信格式模板](/resource/WF/Admin/AttrFlow/ImpFlowData_WeiXin.png "导入微信格式模板")  

  `);this.PageTitle="流程数据导入"}Init(){return s(this,null,function*(){this.AddGroup("A","系统支持"),this.FileUpload("DingDing","钉钉数据导入","请上传符合格式的Excel文件.",this.DingDing),this.FileUpload("WeiXin","微信数据导入","请上传符合格式的Excel文件.",this.WeiXin),this.FolderUpload("DingDingDtl","钉钉附件数据导入","请上传符合格式的Excel文件.",this.DingDing)})}GenerSorts(){return s(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(i,D,d,F,c){return s(this,null,function*(){const r=this.PKVal;if(i=="DingDing"){const t=new h("BP.WF.HttpHandler.WF_Admin_AttrFlow");t.AddFile(this.UploadFile),t.AddPara("FlowNo",r);let e=yield t.DoMethodReturnString("DingDing_ImpDataFile");return typeof e=="string"&&e.includes("@")&&(e=e.split("@").join(`
`)),new m(A.Message,e)}if(i=="DingDingDtl"){const t=this.UploadFilArr,e=this.FolderName,l=new h("BP.WF.HttpHandler.WF_Admin_AttrFlow");for(const g of t)l.AddFile(g==null?void 0:g.originFileObj);l.AddPara("FlowNo",r),l.AddPara("AthName",e||"");let a=yield l.DoMethodReturnString("DingDing_ImpAths");return typeof a=="string"&&a.includes("@")&&(a=a.split("@").join(`
`)),new m(A.Message,a)}})}}export{W as GPN_ImpFlowData};
