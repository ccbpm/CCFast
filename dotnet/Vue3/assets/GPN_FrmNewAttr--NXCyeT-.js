var E=Object.defineProperty;var y=(a,s,t)=>s in a?E(a,s,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[s]=t;var K=(a,s,t)=>y(a,typeof s!="symbol"?s+"":s,t);var c=(a,s,t)=>new Promise((l,i)=>{var F=o=>{try{r(t.next(o))}catch(n){i(n)}},f=o=>{try{r(t.throw(o))}catch(n){i(n)}},r=o=>o.done?l(o.value):Promise.resolve(o.value).then(F,f);r((t=t.apply(a,s)).next())});import{FrmNodeAttr as w}from"./FrmNode-ICauEUJT.js";import{FrmNodeFields as A,FrmNodeField as R}from"./FrmNodeField-DYiwhQcm.js";import{b9 as S,bc as P}from"./entry/index-M8VErHPE-1727507756861.js";import{FrmNodeCtrlSln as u}from"./FrmNodeCtrlSln-kdg-RRBQ.js";import{e as b}from"./antd-DkiF_jXA.js";import"./vue-DGeTOT5N.js";import"./MapDtlSln-fCuyEFFI.js";import"./FrmAttachmentSln-DvKZQAA2.js";import"./FrmAttachment-DyM-VnR7.js";class C extends S{constructor(){super("GPN_FrmNewAttr");K(this,"DescAttrs",`
  #### 帮助
  - 什么是正则表达式，请baidu，这个名词。
  - 系统已经帮您准备一个常用正则表达式.
  - 请选择正确的事件，然后根据事件选择表达式.
  - 比如: 校验文本框是否是电话号码，应该在失去焦点，而不能在双击事件.
  #### 图例
  - 电话号码校验
  - 
`);K(this,"DescDtl",`
  #### 帮助
  - 自定义正则表达式, 请在文本框输入正则表达式，然后执行创建。
  - 请正确的选择事件然.
`);K(this,"DescAth",`
  #### 帮助
  - 自定义函数，就是在服务器上创建一个js文件，写入一个函数.
`);this.ForEntityClassID="TS.AttrNode.FrmNodeField",this.PageTitle="字段权限"}Init(){return c(this,null,function*(){this.AddGroup("A","字段权限");const t=new u;t.setPKVal(this.RefPKVal),yield t.Retrieve();const l=t.FK_Frm,i=`SELECT OID AS No,Lab as Name FROM Sys_GroupField WHERE CtrlType='' AND FrmID='${l}' ORDER BY Idx`,F=`SELECT KeyOfEn AS No, Name, GroupID as GroupNo FROM Sys_MapAttr
     WHERE FK_MapData='${l}' AND UIVisible=1 
      ORDER BY Idx`;this.SelectItemsByGroupList("Attr","字段权限","",!0,i,F)})}GenerSorts(){return c(this,null,function*(){return null})}Save_TextBox_X(t,l,i,F,f){return c(this,null,function*(){const r=new u;r.setPKVal(this.RefPKVal),yield r.Retrieve();const o=r.FK_Frm,n=new A;yield n.Retrieve(w.FK_Node,r.FK_Node,"FK_Frm",o);const _=new P;yield _.Retrieve("FK_MapData",o);const I=i.split(","),D=F.split(",");for(let m=0;m<I.length;m++){const p=I[m],e=new R;if(e.MyPK=r.FK_Frm+"_"+r.FK_Node+"_"+p,e.setPKVal(e.MyPK),n.filter(d=>d.KeyOfEn===p).length>0==!0)continue;const N=_.filter(d=>d.KeyOfEn==p)[0];e.FK_Flow=r.FK_Flow,e.FK_Node=r.FK_Node,e.FK_MapData=r.FK_Frm,e.KeyOfEn=p,e.Name=D[m],e.EleType="Field",e.UIIsEnable=N.UIIsEnable,e.UIVisible=N.UIVisible,e.IsNotNull=N.UIIsInput,e.DefVal=N.DefVal,yield e.Insert()}b.info("设置成功,请关闭或者设置其他元素,进行编辑.")})}}export{C as GPN_FrmNewAttr};
