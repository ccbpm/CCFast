var f=Object.defineProperty;var S=(o,a,r)=>a in o?f(o,a,{enumerable:!0,configurable:!0,writable:!0,value:r}):o[a]=r;var m=(o,a,r)=>S(o,typeof a!="symbol"?a+"":a,r);var F=(o,a,r)=>new Promise((l,i)=>{var D=s=>{try{t(r.next(s))}catch(n){i(n)}},N=s=>{try{t(r.throw(s))}catch(n){i(n)}},t=s=>s.done?l(s.value):Promise.resolve(s.value).then(D,N);t((r=r.apply(o,a)).next())});import{FrmNodeAttr as w}from"./FrmNode-C2BSQEvr.js";import{FrmNodeFields as P}from"./FrmNodeField-CBfPmN_v.js";import{b5 as u,B as M}from"./entry/index-C6uBgOW5-1730430676707.js";import{FrmNodeCtrlSln as d}from"./FrmNodeCtrlSln-BduaLP9U.js";import{b as E}from"./antd-Dd9L3uAF.js";import"./vue-BXIlYw1E.js";import"./MapDtlSln-BjGvUudN.js";import"./FrmAttachmentSln-ScI3pTi5.js";import"./FrmAttachment-D6absTvH.js";class G extends u{constructor(){super("GPN_FrmNewDtl");m(this,"DescAttrs",`
  #### 帮助
  - 什么是正则表达式，请baidu，这个名词。
  - 系统已经帮您准备一个常用正则表达式.
  - 请选择正确的事件，然后根据事件选择表达式.
  - 比如: 校验文本框是否是电话号码，应该在失去焦点，而不能在双击事件.
  #### 图例
  - 电话号码校验
  - 
`);m(this,"DescDtl",`
  #### 帮助
  - 自定义正则表达式, 请在文本框输入正则表达式，然后执行创建。
  - 请正确的选择事件然.
`);m(this,"DescAth",`
  #### 帮助
  - 自定义函数，就是在服务器上创建一个js文件，写入一个函数.
`);this.ForEntityClassID="TS.AttrNode.MapDtlSln",this.PageTitle="从表权限"}Init(){return F(this,null,function*(){this.AddGroup("A","从表权限");const r=new d;r.setPKVal(this.RefPKVal),yield r.Retrieve();const i=`SELECT No,Name FROM Sys_MapDtl WHERE FK_MapData='${r.FK_Frm}' AND FK_Node=0 `;this.SelectItemsByList("Dtl","影响的从表",this.DescDtl,!0,i)})}GenerSorts(){return F(this,null,function*(){return null})}Save_TextBox_X(r,l,i,D,N){return F(this,null,function*(){const t=new d;t.setPKVal(this.RefPKVal),yield t.Retrieve();const s=t.FK_Frm;yield new P().Retrieve(w.FK_Node,t.FK_Node,"FK_MapData",s);const K=i.split(",");for(let p=0;p<K.length;p++){const c=K[p],_=c+"_"+t.FK_Node,e=new M("BP.WF.Template.Frm.MapDtlExt");e.setPK(_),(yield e.RetrieveFromDBSources())==0?(e.setPK(c),yield e.RetrieveFromDBSources(),e.setPK(_),e.FK_Node=t.FK_Node,e.FK_MapData=t.FK_Frm,e.No=_,e.setPara("EnName","TS.Frm.MapDtlExt"),yield e.Insert(),yield e.DoMethodReturnString("InitAttrsOfSelf")):e.FK_MapData!=t.FK_Frm+"_"+t.FK_Node&&(e.FK_MapData=t.FK_Frm+"_"+t.FK_Node,e.setPara("EnName","TS.Frm.MapDtlExt"),yield e.Update())}E.info("设置成功,请关闭或者设置其他元素,进行编辑.")})}}export{G as GPN_FrmNewDtl};
