var h=Object.defineProperty;var u=(i,r,t)=>r in i?h(i,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):i[r]=t;var n=(i,r,t)=>u(i,typeof r!="symbol"?r+"":r,t);var a=(i,r,t)=>new Promise((c,m)=>{var p=e=>{try{o(t.next(e))}catch(s){m(s)}},l=e=>{try{o(t.throw(e))}catch(s){m(s)}},o=e=>e.done?c(e.value):Promise.resolve(e.value).then(p,l);o((t=t.apply(i,r)).next())});import{b5 as B,W as N,G as x,l as G}from"./entry/index-C6uBgOW5-1730430676707.js";import{Y as I}from"./YSBatch-DH4fRkyA.js";import{GloComm as S}from"./GloComm-CmAl8MpM.js";import f from"./Dev2InterfaceCCBill-BgcYUWBE.js";import{b as T}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./Task-Bv14ZuGG.js";import"./GenerBill-CRsPBFfB.js";import"./EntityWorkID-8an0JVx8.js";import"./YSOrg-CjZuQfEF.js";import"./DeptFrm-DSsPtZyX.js";import"./PageBaseTreeEns-CdhfbuDU.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";import"./Events-D9tOL1Ad.js";import"./FrmSort-DM5yTjQh.js";import"./FrmAdm-DqTHICqI.js";import"./MapData-Ccsy8tbB.js";import"./EnumLab-CzismWql.js";import"./FrmTrack-BAfWiAdt.js";class K extends B{constructor(){super("GPN_NewBatch");n(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);n(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 . 完善测试该方法.

  `);this.PageTitle="新建批次号",this.ForEntityClassID="TS.YS.YSBatch"}Init(){return a(this,null,function*(){if(N.IsAdmin==!1){T.error("err@您好:"+N.Name+",非管理员用户不能查看.");return}this.AddGroup("A","创建申报任务"),this.TextBox2_NameNo("BatchNoName","输入批次ID与名称",this.HelpUn,"","任务编号","任务名称","")})}GenerSorts(){return a(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,c,m,p,l){return a(this,null,function*(){const o=new I;if(o.No=p,o.Name=m,(yield o.IsExits())==!0){alert("批次号已经存在.");return}yield o.Insert(),f.WriteTrack("YS_Batch",o.No,"创建批次任务.");const e=S.UrlEn("TS.YS.YSBatch",o.No);return new x(G.GoToUrl,e)})}}export{K as GPN_NewBatch};
