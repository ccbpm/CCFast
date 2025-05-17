var w=Object.defineProperty;var P=(i,e,r)=>e in i?w(i,e,{enumerable:!0,configurable:!0,writable:!0,value:r}):i[e]=r;var n=(i,e,r)=>P(i,typeof e!="symbol"?e+"":e,r);var s=(i,e,r)=>new Promise((m,a)=>{var l=t=>{try{o(r.next(t))}catch(p){a(p)}},c=t=>{try{o(r.throw(t))}catch(p){a(p)}},o=t=>t.done?m(t.value):Promise.resolve(t.value).then(l,c);o((r=r.apply(i,e)).next())});import{b5 as G,H as d,G as x,l as h}from"./entry/index-C6uBgOW5-1730430676707.js";import{XS as A}from"./XS-DKvkOOiY.js";import{b as u}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./Events-D9tOL1Ad.js";import"./SPLX-DBryo2Vv.js";import"./XiangZhen-C460LNeL.js";import"./QuXian-cNeFaw7o.js";import"./DiQu-DMrzyfmy.js";import"./MeiTi-Bqh1QpeV.js";import"./Dev2InterfaceCCBill-BgcYUWBE.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapAttr-B1mxD3vP.js";class X extends G{constructor(){super("GPN_ADImp");n(this,"Imp",`
  #### 帮助
   - 从其他部门的人员里导入人员，放入本部门中.
   - 一个人拥有多个部门.
  `);n(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 .  完善测试该方法.

  `);this.PageTitle="导入广告"}Init(){return s(this,null,function*(){this.AddGroup("A","导入广告"),this.FileUpload("Excel","导入Excel文件","请上传约定格式的文件","上传文件然后执行导入"),this.FileUpload("Zip","导入zip文件","请上传约定格式的文件","上传文件然后执行导入"),this.AddBlank("xx","从接口导入",this.HelpTodo)})}GenerSorts(){return s(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(r,m,a,l,c){return s(this,null,function*(){if(r=="Excel")try{yield new A().Init();const t=new d("BP.App.Handler_AD");t.AddFile(this.UploadFile);const p=yield t.DoMethodReturnString("ImpAD");return new x(h.Message,p)}catch(o){u.error(o)}if(r=="Zip")try{yield new A().Init();const t=new d("BP.App.Handler_AD");t.AddFile(this.UploadFile);const p=yield t.DoMethodReturnString("ImpZipAD");return new x(h.Message,p)}catch(o){u.error(o)}})}}export{X as GPN_ADImp};
