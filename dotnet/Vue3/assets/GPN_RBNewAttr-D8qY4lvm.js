var h=Object.defineProperty;var _=(o,a,t)=>a in o?h(o,a,{enumerable:!0,configurable:!0,writable:!0,value:t}):o[a]=t;var y=(o,a,t)=>_(o,typeof a!="symbol"?a+"":a,t);var u=(o,a,t)=>new Promise((n,p)=>{var c=s=>{try{r(t.next(s))}catch(i){p(i)}},l=s=>{try{r(t.throw(s))}catch(i){p(i)}},r=s=>s.done?n(s.value):Promise.resolve(s.value).then(c,l);r((t=t.apply(o,a)).next())});import{MapAttr as K}from"./MapAttr-B1mxD3vP.js";import{M as I,b as N}from"./MapExt-DtQWKcAY.js";import{SysEnums as x,SysEnumAttr as B}from"./SysEnum-DlgPT0C2.js";import{b5 as F}from"./entry/index-C6uBgOW5-1730430676707.js";import{b as O}from"./antd-Dd9L3uAF.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";class H extends F{constructor(){super("GPN_RBNewAttr");y(this,"DescAttrs",`
  #### 帮助
  - 什么是正则表达式，请baidu，这个名词。
  - 系统已经帮您准备一个常用正则表达式.
  - 请选择正确的事件，然后根据事件选择表达式.
  - 比如: 校验文本框是否是电话号码，应该在失去焦点，而不能在双击事件.
  #### 图例
  - 电话号码校验
  
`);y(this,"DescDtl",`
  #### 帮助
  - 自定义正则表达式, 请在文本框输入正则表达式，然后执行创建。
  - 请正确的选择事件然.
`);y(this,"DescAth",`
  #### 帮助
  - 自定义函数，就是在服务器上创建一个js文件，写入一个函数.
`);this.ForEntityClassID="TS.MapExt.RBAttr",this.PageTitle="新增影响的元素"}Init(){return u(this,null,function*(){this.AddGroup("A","影响的字段");const t=new K;t.setPKVal(this.RefPKVal),yield t.Retrieve();const n=t.FK_MapData,p=`SELECT OID AS No,Lab as Name FROM Sys_GroupField WHERE CtrlType='' AND FrmID='${n}' ORDER BY Idx`,c=`SELECT KeyOfEn AS No, Name, GroupID as GroupNo FROM Sys_MapAttr
     WHERE FK_MapData='${n}' AND UIVisible=1 
      ORDER BY Idx`;this.SelectItemsByGroupList("Attr","选择影响的字段","",!0,p,c);const l=`SELECT No,Name FROM Sys_MapDtl WHERE FK_MapData='${n}'`;this.SelectItemsByList("Dtl","影响的从表",this.DescDtl,!0,l);const r=`SELECT NoOfObj AS No,Name FROM Sys_Frmattachment WHERE FK_MapData='${n}'`;this.SelectItemsByList("Ath","影响的附件",this.DescAth,!0,r)})}GenerSorts(){return u(this,null,function*(){return null})}Save_TextBox_X(t,n,p,c,l){return u(this,null,function*(){const r=this.RequestVal("RefPKVal"),s=new K(r);yield s.Retrieve();const i=new x;yield i.Retrieve(B.EnumKey,s.UIBindKey);const D=new I;yield D.Retrieve("RefPKVal",r,"ExtType",t);const f=p.split(","),S=c.split(",");for(let A=0;A<i.length;A++){const m=i[A];for(let E=0;E<f.length;E++){const R=f[E],e=new N;e.MyPK=R+"_RBAction_"+m.IntKey,e.setPKVal(R+"_RBAction_"+m.IntKey),D.filter(M=>M.AttrOfOper===R).length>0!=!0&&(e.FK_MapData=s.FK_MapData,e.ExtModel="RBAction",e.ExtType=t,e.RefPKVal=r,e.AttrOfOper=R,e.Tag=S[E],e.Tag1=m.IntKey,e.Tag2=m.Lab,e.Tag3="0",yield e.Insert())}}O.info("设置成功,请关闭或者设置其他元素,进行编辑.")})}}export{H as GPN_RBNewAttr};
