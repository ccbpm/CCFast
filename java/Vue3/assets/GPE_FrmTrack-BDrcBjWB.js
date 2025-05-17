var p=Object.defineProperty;var n=(i,t,r)=>t in i?p(i,t,{enumerable:!0,configurable:!0,writable:!0,value:r}):i[t]=r;var e=(i,t,r)=>n(i,typeof t!="symbol"?t+"":t,r);import{FrmTrack as o}from"./FrmTrack-BAfWiAdt.js";import{PageBaseGroupEdit as c}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./Help-D0bDMZWg.js";class G extends c{constructor(){super("GPE_FrmTrack");e(this,"Desc0",` 
  #### 帮助
  - 

  #### 流程图
  - 

  
  `);e(this,"Desc1",` 
  #### 帮助
  - 只读状态下，仅仅可以查看工序，不能对工序进行编排.
  `);e(this,"Desc2",` 
  #### 帮助
  - 
....`);this.PageTitle="轨迹组件"}Init(){this.entity=new o,this.KeyOfEn="FrmTrackSta",this.AddGroup("A","组件状态"),this.Blank("0","禁用",this.Desc0),this.AddEntity("1","显示轨迹图",new o,this.Desc2),this.AddEntity("2","显示轨迹表",new o,this.Desc2)}BtnClick(r,s,m){if(r==s||r===m)throw new Error("Method not implemented.")}AfterSave(r,s){}}export{G as GPE_FrmTrack};
