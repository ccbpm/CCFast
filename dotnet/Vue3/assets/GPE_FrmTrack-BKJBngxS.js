var n=Object.defineProperty;var p=(i,t,r)=>t in i?n(i,t,{enumerable:!0,configurable:!0,writable:!0,value:r}):i[t]=r;var e=(i,t,r)=>p(i,typeof t!="symbol"?t+"":t,r);import{FrmTrack as s}from"./FrmTrack-0uAZQ3B_.js";import{PageBaseGroupEdit as c}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./Help-D0bDMZWg.js";class T extends c{constructor(){super("GPE_FrmTrack");e(this,"Desc0",` 
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
....`);this.PageTitle="轨迹组件"}Init(){this.entity=new s,this.KeyOfEn="FrmTrackSta",this.AddGroup("A","组件状态"),this.Blank("0","禁用",this.Desc0),this.AddEntity("1","显示轨迹图",new s,this.Desc2),this.AddEntity("2","显示轨迹表",new s,this.Desc2)}BtnClick(r,o,m){if(r==o||r===m)throw new Error("Method not implemented.")}AfterSave(r,o){}}export{T as GPE_FrmTrack};
