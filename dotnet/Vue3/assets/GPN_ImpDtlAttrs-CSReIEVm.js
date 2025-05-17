var I=Object.defineProperty;var D=(l,e,t)=>e in l?I(l,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):l[e]=t;var n=(l,e,t)=>D(l,typeof e!="symbol"?e+"":e,t);var p=(l,e,t)=>new Promise((m,r)=>{var S=s=>{try{o(t.next(s))}catch(a){r(a)}},f=s=>{try{o(t.throw(s))}catch(a){r(a)}},o=s=>s.done?m(s.value):Promise.resolve(s.value).then(S,f);o((t=t.apply(l,e)).next())});import{MapAttr as u}from"./MapAttr-B1mxD3vP.js";import{b5 as N,G as _,l as y}from"./entry/index-C6uBgOW5-1730430676707.js";import{D as h}from"./DBAccess-sLO0RM-h.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";class g extends N{constructor(){super("GPN_ImpDtlAttrs");n(this,"Imp",`
  #### 帮助
  - 选择一个从表，然后导入字段.
  - 已经有的就自动创建.
  - 
  #### 配置图
  - sdfsdfsd
`);n(this,"Imp_SelectDtl",`
  #### 帮助
  - csdfsadfsad
  #### 配置图
  - sdfsdfsd
`);n(this,"Imp_SelectDtl_SelectFields",`
  #### 帮助
  - csdfsadfsad
  #### 配置图
  - sdfsdfsd
`);this.PageTitle="导入实体字段"}Init(){this.AddGroup("A","导入实体字段"),this.AddBlank("Imp","准备",this.Imp),this.SelectItemsByList("Imp.SelectDtl","选择从表",this.Imp_SelectDtl,!1,"SELECT No,Name FROM Sys_MapDtl "),this.SelectItemsByList("Imp.SelectDtl.SelectFields","选择字段",this.Imp_SelectDtl_SelectFields,!0,this.GenerTableFields)}GenerTableFields(){return p(this,null,function*(){const m=`SELECT MyPK as No, Name FROM Sys_MapAttr WHERE FK_MapData='${this.RequestVal("tb1","Imp.SelectDtl")}' `,r=yield h.RunSQLReturnTable(m);return JSON.stringify(r)})}GenerSorts(){return p(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,m,r,S,f){return p(this,null,function*(){if(t==="Imp.SelectDtl.SelectFields"){const o=this.PKVal,s=r.split(",");let a="";const i=new u;for(let c=0;c<s.length;c++){const d=s[c];if(i.MyPK=d,yield i.Retrieve(),i.MyPK=o+"_"+i.Key,(yield i.IsExits())==!0){a+=`@字段: ${d} - ${i.Name} 已经存在.`;continue}i.FK_MapData=o,yield i.Insert(),a+=`@字段: ${d} - ${i.Name} 成功导入...`}return new _(y.Message,a)}})}}export{g as GPN_ImpDtlAttrs};
