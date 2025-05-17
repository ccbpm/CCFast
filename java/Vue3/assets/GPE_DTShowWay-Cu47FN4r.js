var m=Object.defineProperty;var e=(r,t,i)=>t in r?m(r,t,{enumerable:!0,configurable:!0,writable:!0,value:i}):r[t]=i;var o=(r,t,i)=>e(r,typeof t!="symbol"?t+"":t,i);import{F as s}from"./FrmBill-B9qIgF69.js";import{PageBaseGroupEdit as a}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmAdm-DqTHICqI.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";import"./SysEvent-D4PfKXg3.js";import"./Collection-C0bGHKA7.js";import"./PG_Group2Method-C5y-BSQj.js";import"./PageBasePanelGroup-UYeak5w4.js";import"./GroupMethod-CUWbZDLV.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./PCenter-g1qHGjKQ.js";import"./PowerCenter-xD33lI2C.js";import"./Method-CoRs5kcO.js";import"./GloComm-CmAl8MpM.js";import"./FrmTrack-BAfWiAdt.js";import"./SearchFKEnum-CidaowCH.js";import"./GPE_ActiveDDL-CfyLOIGk.js";import"./MapExt-DtQWKcAY.js";import"./GPEActiveDDLSFTable-DeY3RH6z.js";import"./GPEActiveDDLSelfSetting-C96E1r0O.js";import"./Help-D0bDMZWg.js";import"./GPE_AutoFullDLL-B3jVaTTp.js";import"./GPEAutoFullDLL-DSguwNJm.js";import"./GPEAutoFullDDLSFTable-VBdXIJnx.js";import"./DBRole-BA4YAefA.js";import"./GPE_FrmType-BPAZNZeD.js";import"./Flow-D4nUES4A.js";import"./SelfCheck-Dde0SsBc.js";import"./ByEmpNo-Bq18vxbq.js";class X extends a{constructor(){super("GPE_DTShowWay");o(this,"Desc0",`
  #### 帮助
  - 按照选择的日期型字段进行查询。
  - 在报表中根据 like 日期字段值 进行查询。
  
  #### 效果图
  ![输入图片说明](/resource/CCBill/SearchCond/SearchKeyData.png "屏幕截图.png")  
 `);o(this,"Desc1",`
  #### 帮助
  - 按照选择的日期型字段进行查询。
  - 在报表中根据 like 日期时间值 进行查询。
  `);this.PageTitle="日期展示方式"}AfterSave(i,p){if(i==p)return null}BtnClick(i,p,n){}Init(){this.entity=new s,this.KeyOfEn="DTShowWay",this.AddGroup("A","日期查询格式"),this.Blank("0","字段方式（日期从，到）",this.Desc0),this.Blank("1","日期Tab页（月份、季度、年度）",this.Desc0),this.Blank("2","日期Tab页（月份、季度、年度、自定义）(未解析)",this.Desc0)}}export{X as GPE_DTShowWay};
