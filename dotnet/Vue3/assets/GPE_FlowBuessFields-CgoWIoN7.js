var E=Object.defineProperty;var F=(s,e,t)=>e in s?E(s,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):s[e]=t;var p=(s,e,t)=>F(s,typeof e!="symbol"?e+"":e,t);var n=(s,e,t)=>new Promise((o,i)=>{var m=r=>{try{l(t.next(r))}catch(a){i(a)}},D=r=>{try{l(t.throw(r))}catch(a){i(a)}},l=r=>r.done?o(r.value):Promise.resolve(r.value).then(m,D);l((t=t.apply(s,e)).next())});import{Flow as d}from"./Flow-D1QXg1UO.js";import{PageBaseGroupEdit as u}from"./PageBaseGroupEdit-JIgqoTiq.js";import"./entry/index-M8VErHPE-1727507756861.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./Help-D0bDMZWg.js";class f extends u{constructor(){super("GPE_FlowBuessFields");p(this,"Desc0",`

  #### 概念
   - 业务字段是流程引擎系统字段以外的，用户自定义的表单字段.
   - 比如：请假日期从、到、请假人、请假原因等.
   - 流程引擎系统字段包括： 标题、发起人、发起日期、发起人部门、停留节点、当前处理人、流程状态等.
  #### 设置作用
   - 通用的待办列表显示的是系统字段, 比如：标题、发起人、发起日期、状态、停留节点.
   - 如果显示指定流程的待办、在途、抄送、就可以使用业务字段显示.
   - 点发起菜单，转到流程一户式操作,就可以查看该流程的信息.
`);this.PageTitle="业务字段"}Init(){return n(this,null,function*(){this.entity=new d,this.KeyOfEn="BuessFieldRole",this.AddGroup("A","显示列"),this.Blank("0","不启用",this.Desc0);let t="";t==""&&(t="ND"+parseInt(this.PKVal)+"01");const o=`SELECT OID as No, Lab as Name FROM Sys_GroupField WHERE FrmID='${t}' AND CtrlID='' `,i=` SELECT KeyOfEn AS No, Name, GroupID FROM Sys_MapAttr WHERE FK_MapData='${t}' 
      AND UIContralType <=4 AND KeyOfEn NOT IN ('OID','Rec','RDT','FID','Title','BillNo','BillState','FlowStarter',
      'FlowEmps','FlowStartRDT','WFState','Emps')
      AND UIVisible=1 ORDER BY GroupID,Idx
      `;this.SelectItemsByGroupList("1","选择字段",this.Desc0,!0,o,i,"BuessFields","BuessFieldNames")})}AfterSave(t,o){if(t==o)throw new Error("Method not implemented.")}BtnClick(t,o,i){if(t==o||t===i)throw new Error("Method not implemented.")}}export{f as GPE_FlowBuessFields};
