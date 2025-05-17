var f=Object.defineProperty;var y=(a,n,t)=>n in a?f(a,n,{enumerable:!0,configurable:!0,writable:!0,value:t}):a[n]=t;var u=(a,n,t)=>y(a,typeof n!="symbol"?n+"":n,t);var x=(a,n,t)=>new Promise((p,r)=>{var c=o=>{try{s(t.next(o))}catch(l){r(l)}},S=o=>{try{s(t.throw(o))}catch(l){r(l)}},s=o=>o.done?p(o.value):Promise.resolve(o.value).then(c,S);s((t=t.apply(a,n)).next())});import{b9 as h,aL as d}from"./entry/index-M8VErHPE-1727507756861.js";import{BindFunction as T}from"./BindFunction-DqLhO-gN.js";import{RegularExpressionFactory as g}from"./RegularExpressionFactory-DfGcs7QO.js";import"./vue-DGeTOT5N.js";import"./antd-DkiF_jXA.js";import"./DBAccess-CzjFzLoq.js";import"./SFPara-DL_8hzxu.js";import"./SFColumn-Q_PoS_2g.js";class k extends h{constructor(){super("GPN_BindFunction");u(this,"RegularExpressionLab",`
  #### 帮助
  - 什么是正则表达式，请baidu，这个名词。
  - 系统已经帮您准备一个常用正则表达式.
  - 请选择正确的事件，然后根据事件选择表达式.
  - 比如: 校验文本框是否是电话号码，应该在失去焦点，而不能在双击事件.
  #### 图例
  - 电话号码校验
  - 
`);u(this,"RegularExpressionSelf",`
  #### 帮助
  - 自定义正则表达式, 请在文本框输入正则表达式，然后执行创建。
  - 请正确的选择事件然.
`);u(this,"SelfFunc",`
  #### 帮助
  - 自定义函数，就是在服务器上创建一个js文件，写入一个函数.
  -  
`);u(this,"JSBody",`
#### 帮助
-  输入函数的脚本.
-  系统就会执行这些脚本，在您指定的事件里.
`);this.ForEntityClassID="TS.MapExt.BindFunction",this.PageTitle="新建绑定正则/函数"}Init(){this.AddGroup("A","绑定正则"),this.SelectItemsByGroupList("RegularExpressionLab","绑定正则表达式库","",!1,this.Group(),this.List()),this.TextBox2_NameNo("RegularExpressionSelf","自定义正则表达式",this.RegularExpressionSelf,"","正则表达式","提示信息",""),this.AddGroup("B","绑定函数"),this.TextBox2_NameNo("SelfFunc","绑定自定义函数",this.SelfFunc,"","函数名","备注",""),this.TextSQL("JSBody","执行函数体",this.JSBody,"输入JS脚本","","请参考下面的帮助文档.")}List(){return JSON.stringify(g.getLabs())}Group(){return JSON.stringify([{No:"onblur,onchange",Name:"onblur失去焦点,与onchange内容变化"},{No:"onblur",Name:"onblur失去焦点"},{No:"onchange",Name:"onchange内容变化"},{No:"onclick",Name:"onclick点击"},{No:"ondblclick",Name:"ondblclick双击"},{No:"onkeypress",Name:"onkeypress当键盘按键被按下并释放一个键时发生"},{No:"onkeyup",Name:"onkeyup释放键盘按键"}])}GenerSorts(){return x(this,null,function*(){return Promise.resolve([{No:"blur",Name:"blur失去焦点"},{No:"change",Name:"change内容变化"}])})}Save_TextBox_X(t,p,r,c,S){return x(this,null,function*(){const s=this.RequestVal("RefPKVal"),o=p,l=yield this.GetSortName(p),m=new d(s);yield m.Retrieve();const e=new T;e.ExtModel="BindFunction",e.Tag=o,e.Tag1=l,e.FK_MapData=m.FK_MapData,e.AttrOfOper=m.KeyOfEn,e.RefPKVal=s,e.Tag6=this.GetPageName(t);let i="TS.MapExt.BindFunction";if(t==="RegularExpressionLab"){e.ExtType="RegularExpression";const N=g.GetEn(r);e.Doc=N.Exp,e.Tag2=N.Message,i="TS.MapExt.RegularExpression"}t==="RegularExpressionSelf"&&(e.ExtType="RegularExpression",e.Doc=c,e.Tag2=r,i="TS.MapExt.RegularExpression"),t==="JSBody"&&(e.ExtType="JSBody",e.Doc=r,i="TS.MapExt.JSBody"),t==="SelfFunc"&&(e.ExtType="SelfFunc",e.Doc=c,i="TS.MapExt.SelfFunc"),e.SetPara("EnName",i),yield e.Insert();let E="";return E="/@/WF/Comm/En.vue?EnName="+i+"&PKVal="+e.MyPK,"url@"+E})}}export{k as GPN_BindFunction};
