var I=Object.defineProperty;var D=(l,e,t)=>e in l?I(l,e,{enumerable:!0,configurable:!0,writable:!0,value:t}):l[e]=t;var o=(l,e,t)=>D(l,typeof e!="symbol"?e+"":e,t);var m=(l,e,t)=>new Promise((c,r)=>{var S=s=>{try{n(t.next(s))}catch(i){r(i)}},f=s=>{try{n(t.throw(s))}catch(i){r(i)}},n=s=>s.done?c(s.value):Promise.resolve(s.value).then(S,f);n((t=t.apply(l,e)).next())});import{b9 as u,aL as N,aB as _,aC as y}from"./entry/index-M8VErHPE-1727507756861.js";import{D as h}from"./DBAccess-CzjFzLoq.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class A extends u{constructor(){super("GPN_ImpDtlAttrs");o(this,"Imp",`
  #### 帮助
  - 选择一个从表，然后导入字段.
  - 已经有的就自动创建.
  - 
  #### 配置图
  - sdfsdfsd
`);o(this,"Imp_SelectDtl",`
  #### 帮助
  - csdfsadfsad
  #### 配置图
  - sdfsdfsd
`);o(this,"Imp_SelectDtl_SelectFields",`
  #### 帮助
  - csdfsadfsad
  #### 配置图
  - sdfsdfsd
`);this.PageTitle="导入实体字段"}Init(){this.AddGroup("A","导入实体字段"),this.AddBlank("Imp","准备",this.Imp),this.SelectItemsByList("Imp.SelectDtl","选择从表",this.Imp_SelectDtl,!1,"SELECT No,Name FROM Sys_MapDtl "),this.SelectItemsByList("Imp.SelectDtl.SelectFields","选择字段",this.Imp_SelectDtl_SelectFields,!0,this.GenerTableFields)}GenerTableFields(){return m(this,null,function*(){const c=`SELECT MyPK as No, Name FROM Sys_MapAttr WHERE FK_MapData='${this.RequestVal("tb1","Imp.SelectDtl")}' `,r=yield h.RunSQLReturnTable(c);return JSON.stringify(r)})}GenerSorts(){return m(this,null,function*(){return Promise.resolve([])})}Save_TextBox_X(t,c,r,S,f){return m(this,null,function*(){if(t==="Imp.SelectDtl.SelectFields"){const n=this.PKVal,s=r.split(",");let i="";const a=new N;for(let p=0;p<s.length;p++){const d=s[p];if(a.MyPK=d,yield a.Retrieve(),a.MyPK=n+"_"+a.Key,(yield a.IsExits())==!0){i+=`@字段: ${d} - ${a.Name} 已经存在.`;continue}a.FK_MapData=n,yield a.Insert(),i+=`@字段: ${d} - ${a.Name} 成功导入...`}return new _(y.Message,i)}})}}export{A as GPN_ImpDtlAttrs};
