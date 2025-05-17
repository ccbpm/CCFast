var e=Object.defineProperty;var m=(i,r,t)=>r in i?e(i,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[r]=t;var o=(i,r,t)=>m(i,typeof r!="symbol"?r+"":r,t);import{F as s}from"./FrmBill-B9qIgF69.js";import{PageBaseGroupEdit as n}from"./PageBaseGroupEdit-IicyYiex.js";import"./entry/index-C6uBgOW5-1730430676707.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./FrmAdm-DqTHICqI.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";import"./SysEvent-D4PfKXg3.js";import"./Collection-C0bGHKA7.js";import"./PG_Group2Method-C5y-BSQj.js";import"./PageBasePanelGroup-UYeak5w4.js";import"./GroupMethod-CUWbZDLV.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./PCenter-g1qHGjKQ.js";import"./PowerCenter-xD33lI2C.js";import"./Method-CoRs5kcO.js";import"./GloComm-CmAl8MpM.js";import"./FrmTrack-BAfWiAdt.js";import"./SearchFKEnum-CidaowCH.js";import"./GPE_ActiveDDL-CfyLOIGk.js";import"./MapExt-DtQWKcAY.js";import"./GPEActiveDDLSFTable-DeY3RH6z.js";import"./GPEActiveDDLSelfSetting-C96E1r0O.js";import"./Help-D0bDMZWg.js";import"./GPE_AutoFullDLL-B3jVaTTp.js";import"./GPEAutoFullDLL-DSguwNJm.js";import"./GPEAutoFullDDLSFTable-VBdXIJnx.js";import"./DBRole-BA4YAefA.js";import"./GPE_FrmType-BPAZNZeD.js";import"./Flow-D4nUES4A.js";import"./SelfCheck-Dde0SsBc.js";import"./ByEmpNo-Bq18vxbq.js";class X extends n{constructor(){super("GPE_SearchKey");o(this,"Desc0",`
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
  `);this.PageTitle="关键字查询"}AfterSave(t,p){if(t==p)return null}BtnClick(t,p,c){}Init(){this.entity=new s,this.KeyOfEn="IsSearchKey",this.AddGroup("A","关键字查询"),this.Blank("0","不设置","不设置"),this.Blank("1","全部关键字查询",this.Desc0)}}export{X as GPE_SearchKey};
