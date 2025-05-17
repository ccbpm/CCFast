var e=Object.defineProperty;var m=(i,r,t)=>r in i?e(i,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[r]=t;var o=(i,r,t)=>m(i,typeof r!="symbol"?r+"":r,t);import{F as s}from"./FrmBill-BBwTGzvc.js";import{PageBaseGroupEdit as n}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./FrmAdm-DEdgJPii.js";import"./MapData-lfC2UY9r.js";import"./EnumLab-CzismWql.js";import"./SysEvent-BDHAFjMW.js";import"./Collection-C1EQ_otd.js";import"./PG_Group2Method-C4uAx4td.js";import"./PageBasePanelGroup-BlJ5oMyj.js";import"./GroupMethod-Cc_Vx1Kw.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./PCenter-CGZJ3ajQ.js";import"./PowerCenter-B1QbxnUu.js";import"./Method-D3vubAhY.js";import"./GloComm-DZ1gELjv.js";import"./FrmTrack-0uAZQ3B_.js";import"./SearchFKEnum-BLDDK4zg.js";import"./GPE_ActiveDDL-BSvw0lCs.js";import"./GPEActiveDDLSFTable-CDs1bctA.js";import"./GPEActiveDDLSelfSetting-cWbBg1lv.js";import"./Help-D0bDMZWg.js";import"./GPE_AutoFullDLL-kKOYGZbv.js";import"./GPEAutoFullDLL-BTgpBioK.js";import"./GPEAutoFullDDLSFTable-MXOvWgx8.js";import"./DBRole-BthbZKHy.js";import"./GPE_FrmType-DMY15Fbs.js";import"./Flow-D1QXg1UO.js";import"./SelfCheck-QnMztK45.js";import"./ByEmpNo-B1iso2da.js";class M extends n{constructor(){super("GPE_SearchKey");o(this,"Desc0",`
  #### 帮助
  - 关键字查询是接受用户输入一个关键字，在整个报表的显示列中使用like查询(外键、枚举、数值类型的除外)
  - 关键字搜索提示, 默认为:请输入关键字...
  #### 效果图
  -  ![输入图片说明](/src/resource/CCBill/SearchCond/SearchKey.png "屏幕截图.png")  
 `);o(this,"Desc1",`
  #### 帮助
  - 选择特定字段，在报表中根据 like 模糊查询
  #### 配置图
  ![输入图片说明](/src/resource/CCBill/SearchCond/StringSearchKeysetting.png "屏幕截图.png")  
  #### 效果图
  ![输入图片说明](/src/resource/CCBill/SearchCond/StringSearchKey.png "屏幕截图.png")  
  `);this.PageTitle="关键字查询"}AfterSave(t,p){if(t==p)return null}BtnClick(t,p,c){}Init(){this.entity=new s,this.KeyOfEn="IsSearchKey",this.AddGroup("A","关键字查询"),this.Blank("0","不设置","不设置"),this.Blank("1","全部关键字查询",this.Desc0)}}export{M as GPE_SearchKey};
