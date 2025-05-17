var S=Object.defineProperty;var u=(a,s,e)=>s in a?S(a,s,{enumerable:!0,configurable:!0,writable:!0,value:e}):a[s]=e;var p=(a,s,e)=>u(a,typeof s!="symbol"?s+"":s,e);var i=(a,s,e)=>new Promise((n,o)=>{var l=r=>{try{t(e.next(r))}catch(m){o(m)}},c=r=>{try{t(e.throw(r))}catch(m){o(m)}},t=r=>r.done?n(r.value):Promise.resolve(r.value).then(l,c);t((e=e.apply(a,s)).next())});import{b5 as D,W as N,G as O,l as F}from"./entry/index-C6uBgOW5-1730430676707.js";import{DeptFrm as f}from"./DeptFrm-DSsPtZyX.js";import{b as R}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";class y extends D{constructor(){super("GPN_DeptFrms");p(this,"SelectOneDept",`
  #### 帮助
   - 由admin控制一个为部门设置申报表单.
   - 一个人拥有多个部门.
  `);p(this,"ImpExcel",`
  #### 帮助
   - 从excel导入数据.
   - 按照ccbpm的excel格式要求.
   - 格式文件位于 .  完善测试该方法.

  `);this.PageTitle="新建部门表单",this.ForEntityClassID="TS.YS.DeptFrm"}Init(){return i(this,null,function*(){if(N.IsAdmin==!1){R.error("err@您好:"+N.Name+",非管理员用户不能查看.");return}this.AddGroup("A","创建申报任务");let e=this.RequestVal("OrgNo");e||(e=this.RefPKVal);const n=`SELECT No,Name,ParentNo FROM Port_Dept WHERE OrgNo='${e}'`,o="SELECT No, Name,ParentNo FROM Sys_FormTree",l="SELECT No,Name,FK_FormTree GroupNo FROM Sys_MapData";this.SelectItemsByTree("SelectOneDept","选择部门",this.SelectOneDept,!1,n,e,!1),this.SelectItemsByGroupList("SelectOneDept.SelectOneFrm","选择表单","请选择要申报的表单",!1,o,l)})}GenerSorts(){return i(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(e,n,o,l,c){return i(this,null,function*(){if(e==="SelectOneDept.SelectOneFrm"){const t=new f;if(t.MyPK=this.RequestVal("tb1","SelectOneDept")+"_"+o,(yield t.IsExits())==!0){alert("该数据已经存在.");return}t.FrmID=o,t.FrmName=l,t.DeptNo=this.RequestVal("tb1","SelectOneDept"),t.DeptName=this.RequestVal("tb2","SelectOneDept");let r=this.RequestVal("OrgNo");return r||(r=this.RefPKVal),t.OrgNo=r,yield t.Insert(),new O(F.CloseAndReload,"增加成功")}})}}export{y as GPN_DeptFrms};
