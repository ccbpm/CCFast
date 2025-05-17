var K=Object.defineProperty;var y=(o,e,r)=>e in o?K(o,e,{enumerable:!0,configurable:!0,writable:!0,value:r}):o[e]=r;var c=(o,e,r)=>y(o,typeof e!="symbol"?e+"":e,r);var p=(o,e,r)=>new Promise((a,n)=>{var m=t=>{try{s(r.next(t))}catch(i){n(i)}},l=t=>{try{s(r.throw(t))}catch(i){n(i)}},s=t=>t.done?a(t.value):Promise.resolve(t.value).then(m,l);s((r=r.apply(o,e)).next())});import{SearchFKEnum as E}from"./SearchFKEnum-CidaowCH.js";import{MapAttr as C}from"./MapAttr-B1mxD3vP.js";import{b5 as I,G as S,l as h}from"./entry/index-C6uBgOW5-1730430676707.js";import"./GPE_ActiveDDL-CfyLOIGk.js";import"./PageBaseGroupEdit-IicyYiex.js";import"./Help-D0bDMZWg.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./MapExt-DtQWKcAY.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./GPEActiveDDLSFTable-DeY3RH6z.js";import"./Events-D9tOL1Ad.js";import"./GPEActiveDDLSelfSetting-C96E1r0O.js";import"./GloComm-CmAl8MpM.js";import"./FrmTrack-BAfWiAdt.js";import"./DBAccess-sLO0RM-h.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./GPE_AutoFullDLL-B3jVaTTp.js";import"./GPEAutoFullDLL-DSguwNJm.js";import"./GPEAutoFullDDLSFTable-VBdXIJnx.js";class W extends I{constructor(){super("GPN_SearchFKEnum");c(this,"Docs2",`
  #### 帮助
  - 按照外键或枚举字段做为查询条件，一个表单里有枚举或者外键字段(能放入到下拉框的字段) 都可以作为查询条件.
  - 比如:按性别、政治面貌、班级来查询.
  - 在上面选择您要查询的字段，点击确定按钮. 每次只能创建一个，如果需要创建多个.
  #### 实现级联查询
  - 请编辑属性: 参考如何设置级联查询.
  #### 实现对查询范围进行控制.
  - 请编辑属性: 参考如何设置查询范围的权限控制.

  #### 配置图
  ![输入图片说明](/src/resource/CCBill/SearchCond/SearchFKEnumSetting.png "屏幕截图.png")  
  #### 效果图
  - 按照配置的字段显示的查询条件.
  ![输入图片说明](/src/resource/CCBill/SearchCond/SearchFKEnum.png "屏幕截图.png")  

`);this.ForEntityClassID="TS.CCBill.SearchFKEnum",this.PageTitle="新建查询条件"}Init(){this.AddGroup("A","新建查询条件");const a=`SELECT KeyOfEn as No, Name FROM Sys_MapAttr
     WHERE FK_MapData='${this.RefPKVal}'
     AND UIVisible=1 
     AND  (UIContralType=1 OR UIContralType=2 OR UIContralType=3)
     `;this.SelectItemsByList("1","选择外键枚举字段",this.Docs2,!1,a)}GenerSorts(){return p(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(r,a,n,m,l){return p(this,null,function*(){const s=this.RequestVal("RefPKVal"),t=new E;t.FrmID=s,t.KeyOfEn=n,t.Name=m,t.MyPK=t.FrmID+"_"+t.KeyOfEn,(yield t.IsExits())==!0?alert("该查询条件已经存在."):yield t.Insert();const i=new C;i.MyPK=t.MyPK,yield i.Retrieve(),t.UIBindKey=i.UIBindKey,i.LGType==1&&(t.IsEnum=1),t.Update();const u="/@/WF/Comm/En.vue?EnName=TS.CCBill.SearchFKEnum&PKVal="+t.MyPK;return new S(h.CloseAndReload,u)})}}export{W as GPN_SearchFKEnum};
