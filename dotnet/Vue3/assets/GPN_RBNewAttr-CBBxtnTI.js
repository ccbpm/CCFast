var M=Object.defineProperty;var _=(n,a,t)=>a in n?M(n,a,{enumerable:!0,configurable:!0,writable:!0,value:t}):n[a]=t;var y=(n,a,t)=>_(n,typeof a!="symbol"?a+"":a,t);var u=(n,a,t)=>new Promise((o,p)=>{var c=s=>{try{r(t.next(s))}catch(i){p(i)}},l=s=>{try{r(t.throw(s))}catch(i){p(i)}},r=s=>s.done?o(s.value):Promise.resolve(s.value).then(c,l);r((t=t.apply(n,a)).next())});import{b9 as I,aL as S,aY as N,bb as x}from"./entry/index-M8VErHPE-1727507756861.js";import{SysEnums as B,SysEnumAttr as F}from"./SysEnum-B89JeOjj.js";import{e as O}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";class g extends I{constructor(){super("GPN_RBNewAttr");y(this,"DescAttrs",`
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
`);this.ForEntityClassID="TS.MapExt.RBAttr",this.PageTitle="新增影响的元素"}Init(){return u(this,null,function*(){this.AddGroup("A","影响的字段");const t=new S;t.setPKVal(this.RefPKVal),yield t.Retrieve();const o=t.FK_MapData,p=`SELECT OID AS No,Lab as Name FROM Sys_GroupField WHERE CtrlType='' AND FrmID='${o}' ORDER BY Idx`,c=`SELECT KeyOfEn AS No, Name, GroupID as GroupNo FROM Sys_MapAttr
     WHERE FK_MapData='${o}' AND UIVisible=1 
      ORDER BY Idx`;this.SelectItemsByGroupList("Attr","选择影响的字段","",!0,p,c);const l=`SELECT No,Name FROM Sys_MapDtl WHERE FK_MapData='${o}'`;this.SelectItemsByList("Dtl","影响的从表",this.DescDtl,!0,l);const r=`SELECT NoOfObj AS No,Name FROM Sys_Frmattachment WHERE FK_MapData='${o}'`;this.SelectItemsByList("Ath","影响的附件",this.DescAth,!0,r)})}GenerSorts(){return u(this,null,function*(){return null})}Save_TextBox_X(t,o,p,c,l){return u(this,null,function*(){const r=this.RequestVal("RefPKVal"),s=new S(r);yield s.Retrieve();const i=new B;yield i.Retrieve(F.EnumKey,s.UIBindKey);const D=new N;yield D.Retrieve("RefPKVal",r,"ExtType",t);const K=p.split(","),f=c.split(",");for(let A=0;A<i.length;A++){const E=i[A];for(let m=0;m<K.length;m++){const R=K[m],e=new x;e.MyPK=R+"_RBAction_"+E.IntKey,e.setPKVal(R+"_RBAction_"+E.IntKey),D.filter(h=>h.AttrOfOper===R).length>0!=!0&&(e.FK_MapData=s.FK_MapData,e.ExtModel="RBAction",e.ExtType=t,e.RefPKVal=r,e.AttrOfOper=R,e.Tag=f[m],e.Tag1=E.IntKey,e.Tag2=E.Lab,e.Tag3="0",yield e.Insert())}}O.info("设置成功,请关闭或者设置其他元素,进行编辑.")})}}export{g as GPN_RBNewAttr};
