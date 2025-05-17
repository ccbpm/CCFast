var K=Object.defineProperty;var y=(a,r,t)=>r in a?K(a,r,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[r]=t;var c=(a,r,t)=>y(a,typeof r!="symbol"?r+"":r,t);var p=(a,r,t)=>new Promise((n,i)=>{var m=e=>{try{s(t.next(e))}catch(o){i(o)}},l=e=>{try{s(t.throw(e))}catch(o){i(o)}},s=e=>e.done?n(e.value):Promise.resolve(e.value).then(m,l);s((t=t.apply(a,r)).next())});import{SearchFKEnum as E}from"./SearchFKEnum-BLDDK4zg.js";import{b9 as C,aL as I,aB as S,aC as h}from"./entry/index-M8VErHPE-1727507756861.js";import"./GPE_ActiveDDL-BSvw0lCs.js";import"./PageBaseGroupEdit-JIgqoTiq.js";import"./Help-D0bDMZWg.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./GPEActiveDDLSFTable-CDs1bctA.js";import"./GPEActiveDDLSelfSetting-cWbBg1lv.js";import"./GloComm-DZ1gELjv.js";import"./FrmTrack-0uAZQ3B_.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";import"./GPE_AutoFullDLL-kKOYGZbv.js";import"./GPEAutoFullDLL-BTgpBioK.js";import"./GPEAutoFullDDLSFTable-MXOvWgx8.js";class V extends C{constructor(){super("GPN_SearchFKEnum");c(this,"Docs2",`
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

`);this.ForEntityClassID="TS.CCBill.SearchFKEnum",this.PageTitle="新建查询条件"}Init(){this.AddGroup("A","新建查询条件");const n=`SELECT KeyOfEn as No, Name FROM Sys_MapAttr
     WHERE FK_MapData='${this.RefPKVal}'
     AND UIVisible=1 
     AND  (UIContralType=1 OR UIContralType=2 OR UIContralType=3)
     `;this.SelectItemsByList("1","选择外键枚举字段",this.Docs2,!1,n)}GenerSorts(){return p(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,n,i,m,l){return p(this,null,function*(){const s=this.RequestVal("RefPKVal"),e=new E;e.FrmID=s,e.KeyOfEn=i,e.Name=m,e.MyPK=e.FrmID+"_"+e.KeyOfEn,(yield e.IsExits())==!0?alert("该查询条件已经存在."):yield e.Insert();const o=new I;o.MyPK=e.MyPK,yield o.Retrieve(),e.UIBindKey=o.UIBindKey,o.LGType==1&&(e.IsEnum=1),e.Update();const u="/@/WF/Comm/En.vue?EnName=TS.CCBill.SearchFKEnum&PKVal="+e.MyPK;return new S(h.CloseAndReload,u)})}}export{V as GPN_SearchFKEnum};
