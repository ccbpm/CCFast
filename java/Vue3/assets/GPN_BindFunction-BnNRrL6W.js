var g=Object.defineProperty;var y=(r,o,t)=>o in r?g(r,o,{enumerable:!0,configurable:!0,writable:!0,value:t}):r[o]=t;var p=(r,o,t)=>y(r,typeof o!="symbol"?o+"":o,t);var x=(r,o,t)=>new Promise((u,a)=>{var c=n=>{try{i(t.next(n))}catch(l){a(l)}},S=n=>{try{i(t.throw(n))}catch(l){a(l)}},i=n=>n.done?u(n.value):Promise.resolve(n.value).then(c,S);i((t=t.apply(r,o)).next())});import{MapAttr as h}from"./MapAttr-B1mxD3vP.js";import{BindFunction as d}from"./BindFunction-DFH_1SFW.js";import{RegularExpressionFactory as f}from"./RegularExpressionFactory-D7e_GgST.js";import{b5 as T}from"./entry/index-C6uBgOW5-1730430676707.js";import"./Events-D9tOL1Ad.js";import"./vue-BXIlYw1E.js";import"./antd-Dd9L3uAF.js";import"./DBAccess-sLO0RM-h.js";import"./SFTable-BlM1UBse.js";import"./SFDBSrc-DKIMsnoa.js";import"./SFPara-T412M7pO.js";import"./SFColumn-CXmWKoZg.js";import"./MapExt-DtQWKcAY.js";class w extends T{constructor(){super("GPN_BindFunction");p(this,"RegularExpressionLab",`
  #### 帮助
  - 什么是正则表达式，请baidu，这个名词。
  - 系统已经帮您准备一个常用正则表达式.
  - 请选择正确的事件，然后根据事件选择表达式.
  - 比如: 校验文本框是否是电话号码，应该在失去焦点，而不能在双击事件.
  #### 图例
  - 电话号码校验
  - 
`);p(this,"RegularExpressionSelf",`
  #### 帮助
  - 自定义正则表达式, 请在文本框输入正则表达式，然后执行创建。
  - 请正确的选择事件然.
`);p(this,"SelfFunc",`
  #### 帮助
  - 自定义函数，就是在服务器上创建一个js文件，写入一个函数.
  -  
`);p(this,"JSBody",`
#### 帮助
-  输入函数的脚本.
-  系统就会执行这些脚本，在您指定的事件里.
`);this.ForEntityClassID="TS.MapExt.BindFunction",this.PageTitle="新建绑定正则/函数"}Init(){this.AddGroup("A","绑定正则"),this.SelectItemsByGroupList("RegularExpressionLab","绑定正则表达式库","",!1,this.Group(),this.List()),this.TextBox2_NameNo("RegularExpressionSelf","自定义正则表达式",this.RegularExpressionSelf,"","正则表达式","提示信息",""),this.AddGroup("B","绑定函数"),this.TextBox2_NameNo("SelfFunc","绑定自定义函数",this.SelfFunc,"","函数名","备注",""),this.TextSQL("JSBody","执行函数体",this.JSBody,"输入JS脚本","","请参考下面的帮助文档.")}List(){return JSON.stringify(f.getLabs())}Group(){return JSON.stringify([{No:"onblur,onchange",Name:"onblur失去焦点,与onchange内容变化"},{No:"onblur",Name:"onblur失去焦点"},{No:"onchange",Name:"onchange内容变化"},{No:"onclick",Name:"onclick点击"},{No:"ondblclick",Name:"ondblclick双击"},{No:"onkeypress",Name:"onkeypress当键盘按键被按下并释放一个键时发生"},{No:"onkeyup",Name:"onkeyup释放键盘按键"}])}GenerSorts(){return x(this,null,function*(){return Promise.resolve([{No:"blur",Name:"blur失去焦点"},{No:"change",Name:"change内容变化"}])})}Save_TextBox_X(t,u,a,c,S){return x(this,null,function*(){const i=this.RequestVal("RefPKVal"),n=u,l=yield this.GetSortName(u),m=new h(i);yield m.Retrieve();const e=new d;e.ExtModel="BindFunction",e.Tag=n,e.Tag1=l,e.FK_MapData=m.FK_MapData,e.AttrOfOper=m.KeyOfEn,e.RefPKVal=i,e.Tag6=this.GetPageName(t);let s="TS.MapExt.BindFunction";if(t==="RegularExpressionLab"){e.ExtType="RegularExpression";const N=f.GetEn(a);e.Doc=N.Exp,e.Tag2=N.Message,s="TS.MapExt.RegularExpression"}t==="RegularExpressionSelf"&&(e.ExtType="RegularExpression",e.Doc=c,e.Tag2=a,s="TS.MapExt.RegularExpression"),t==="JSBody"&&(e.ExtType="JSBody",e.Doc=a,s="TS.MapExt.JSBody"),t==="SelfFunc"&&(e.ExtType="SelfFunc",e.Doc=c,s="TS.MapExt.SelfFunc"),e.SetPara("EnName",s),yield e.Insert();let E="";return E="/@/WF/Comm/En.vue?EnName="+s+"&PKVal="+e.MyPK,"url@"+E})}}export{w as GPN_BindFunction};
