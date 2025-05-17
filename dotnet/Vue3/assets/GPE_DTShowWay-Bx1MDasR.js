var m=Object.defineProperty;var e=(r,t,i)=>t in r?m(r,t,{enumerable:!0,configurable:!0,writable:!0,value:i}):r[t]=i;var o=(r,t,i)=>e(r,typeof t!="symbol"?t+"":t,i);import{F as s}from"./FrmBill-BBwTGzvc.js";import{PageBaseGroupEdit as a}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmAdm-DEdgJPii.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";import"./SysEvent-BDHAFjMW.js";import"./Collection-C1EQ_otd.js";import"./PG_Group2Method-C4uAx4td.js";import"./PageBasePanelGroup-BlJ5oMyj.js";import"./GroupMethod-Cc_Vx1Kw.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./PCenter-CGZJ3ajQ.js";import"./PowerCenter-B1QbxnUu.js";import"./Method-D3vubAhY.js";import"./GloComm-DZ1gELjv.js";import"./FrmTrack-0uAZQ3B_.js";import"./SearchFKEnum-BLDDK4zg.js";import"./GPE_ActiveDDL-BSvw0lCs.js";import"./GPEActiveDDLSFTable-CDs1bctA.js";import"./GPEActiveDDLSelfSetting-cWbBg1lv.js";import"./Help-D0bDMZWg.js";import"./GPE_AutoFullDLL-kKOYGZbv.js";import"./GPEAutoFullDLL-BTgpBioK.js";import"./GPEAutoFullDDLSFTable-MXOvWgx8.js";import"./DBRole-BthbZKHy.js";import"./GPE_FrmType-DMY15Fbs.js";import"./Flow-D1QXg1UO.js";import"./SelfCheck-QnMztK45.js";import"./ByEmpNo-B1iso2da.js";class L extends a{constructor(){super("GPE_DTShowWay");o(this,"Desc0",`
  #### 帮助
  - 按照选择的日期型字段进行查询。
  - 在报表中根据 like 日期字段值 进行查询。
  
  #### 效果图
  ![输入图片说明](/resource/CCBill/SearchCond/SearchKeyData.png "屏幕截图.png")  
 `);o(this,"Desc1",`
  #### 帮助
  - 按照选择的日期型字段进行查询。
  - 在报表中根据 like 日期时间值 进行查询。
  `);this.PageTitle="日期展示方式"}AfterSave(i,p){if(i==p)return null}BtnClick(i,p,n){}Init(){this.entity=new s,this.KeyOfEn="DTShowWay",this.AddGroup("A","日期查询格式"),this.Blank("0","字段方式（日期从，到）",this.Desc0),this.Blank("1","日期Tab页（月份、季度、年度）",this.Desc0),this.Blank("2","日期Tab页（月份、季度、年度、自定义）(未解析)",this.Desc0)}}export{L as GPE_DTShowWay};
